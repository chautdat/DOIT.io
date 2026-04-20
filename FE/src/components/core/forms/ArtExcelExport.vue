<!-- Export Excel File -->
<template>
  <ElButton
    :type="type"
    :size="size"
    :loading="isExporting"
    :disabled="disabled || !hasData"
    v-ripple
    @click="handleExport"
  >
    <template #loading>
      <ElIcon class="is-loading">
        <Loading />
      </ElIcon>
      {{ loadingText }}
    </template>
    <slot>{{ buttonText }}</slot>
  </ElButton>
</template>

<script setup lang="ts">
  import * as XLSX from 'xlsx'
  import FileSaver from 'file-saver'
  import { ref, computed, nextTick } from 'vue'
  import { Loading } from '@element-plus/icons-vue'
  import type { ButtonType } from 'element-plus'
  import { useThrottleFn } from '@vueuse/core'

  defineOptions({ name: 'ArtExcelExport' })

  /** Export data type */
  type ExportValue = string | number | boolean | null | undefined | Date

  interface ExportData {
    [key: string]: ExportValue
  }

  /** Column configuration */
  interface ColumnConfig {
    /** Column title */
    title: string
    /** Column width */
    width?: number
    /** Data formatter function */
    formatter?: (value: ExportValue, row: ExportData, index: number) => string
  }

  /** Export configuration options */
  interface ExportOptions {
    /** Data source */
    data: ExportData[]
    /** Filename (without extension) */
    filename?: string
    /** Worksheet name */
    sheetName?: string
    /** Button type */
    type?: ButtonType
    /** Button size */
    size?: 'large' | 'default' | 'small'
    /** Whether disabled */
    disabled?: boolean
    /** Button text */
    buttonText?: string
    /** Loading text */
    loadingText?: string
    /** Whether to auto add index column */
    autoIndex?: boolean
    /** Index column title */
    indexColumnTitle?: string
    /** Column configuration mapping */
    columns?: Record<string, ColumnConfig>
    /** Header mapping (simplified version, backward compatible) */
    headers?: Record<string, string>
    /** Maximum export rows */
    maxRows?: number
    /** Whether to show success message */
    showSuccessMessage?: boolean
    /** Whether to show error message */
    showErrorMessage?: boolean
    /** Workbook configuration */
    workbookOptions?: {
      /** Creator */
      creator?: string
      /** Last modified by */
      lastModifiedBy?: string
      /** Created time */
      created?: Date
      /** Modified time */
      modified?: Date
    }
  }

  const props = withDefaults(defineProps<ExportOptions>(), {
    filename: () => `export_${new Date().toISOString().slice(0, 10)}`,
    sheetName: 'Sheet1',
    type: 'primary',
    size: 'default',
    disabled: false,
    buttonText: 'Export Excel',
    loadingText: 'Exporting...',
    autoIndex: false,
    indexColumnTitle: 'No.',
    columns: () => ({}),
    headers: () => ({}),
    maxRows: 100000,
    showSuccessMessage: true,
    showErrorMessage: true,
    workbookOptions: () => ({})
  })

  const emit = defineEmits<{
    'before-export': [data: ExportData[]]
    'export-success': [filename: string, rowCount: number]
    'export-error': [error: ExportError]
    'export-progress': [progress: number]
  }>()

  /** Export error type */
  class ExportError extends Error {
    constructor(
      message: string,
      public code: string,
      public details?: any
    ) {
      super(message)
      this.name = 'ExportError'
    }
  }

  const isExporting = ref(false)

  /** Whether there is data to export */
  const hasData = computed(() => Array.isArray(props.data) && props.data.length > 0)

  /** Validate export data */
  const validateData = (data: ExportData[]): void => {
    if (!Array.isArray(data)) {
      throw new ExportError('Data must be in array format', 'INVALID_DATA_TYPE')
    }

    if (data.length === 0) {
      throw new ExportError('No data to export', 'NO_DATA')
    }

    if (data.length > props.maxRows) {
      throw new ExportError(`Data rows exceed limit (${props.maxRows} rows)`, 'EXCEED_MAX_ROWS', {
        currentRows: data.length,
        maxRows: props.maxRows
      })
    }
  }

  /** Format cell value */
  const formatCellValue = (
    value: ExportValue,
    key: string,
    row: ExportData,
    index: number
  ): string => {
    // Use column config formatter function
    const column = props.columns[key]
    if (column?.formatter) {
      return column.formatter(value, row, index)
    }

    // Handle special values
    if (value === null || value === undefined) {
      return ''
    }

    if (value instanceof Date) {
      return value.toLocaleDateString('en-US')
    }

    if (typeof value === 'boolean') {
      return value ? 'Yes' : 'No'
    }

    return String(value)
  }

  /** Process data */
  const processData = (data: ExportData[]): Record<string, string>[] => {
    const processedData = data.map((item, index) => {
      const processedItem: Record<string, string> = {}

      // Add index column
      if (props.autoIndex) {
        processedItem[props.indexColumnTitle] = String(index + 1)
      }

      // Process data columns
      Object.entries(item).forEach(([key, value]) => {
        // Get column title
        let columnTitle = key
        if (props.columns[key]?.title) {
          columnTitle = props.columns[key].title
        } else if (props.headers[key]) {
          columnTitle = props.headers[key]
        }

        // Format value
        processedItem[columnTitle] = formatCellValue(value, key, item, index)
      })

      return processedItem
    })

    return processedData
  }

  /** Calculate column widths */
  const calculateColumnWidths = (data: Record<string, string>[]): XLSX.ColInfo[] => {
    if (data.length === 0) return []

    const sampleSize = Math.min(data.length, 100) // Only take first 100 rows to calculate column width
    const columns = Object.keys(data[0])

    return columns.map((column) => {
      // Use configured column width
      const configWidth = Object.values(props.columns).find((col) => col.title === column)?.width

      if (configWidth) {
        return { wch: configWidth }
      }

      // Auto calculate column width
      const maxLength = Math.max(
        column.length, // Title length
        ...data.slice(0, sampleSize).map((row) => String(row[column] || '').length)
      )

      // Limit min and max width
      const width = Math.min(Math.max(maxLength + 2, 8), 50)
      return { wch: width }
    })
  }

  /** Export to Excel */
  const exportToExcel = async (
    data: ExportData[],
    filename: string,
    sheetName: string
  ): Promise<void> => {
    try {
      emit('export-progress', 10)

      // Process data
      const processedData = processData(data)
      emit('export-progress', 30)

      // Create workbook
      const workbook = XLSX.utils.book_new()

      // Set workbook properties
      if (props.workbookOptions) {
        workbook.Props = {
          Title: filename,
          Subject: 'Data Export',
          Author: props.workbookOptions.creator || 'Art Design Pro',
          Manager: props.workbookOptions.lastModifiedBy || '',
          Company: 'System Export',
          Category: 'Data',
          Keywords: 'excel,export,data',
          Comments: 'Auto generated by system',
          CreatedDate: props.workbookOptions.created || new Date(),
          ModifiedDate: props.workbookOptions.modified || new Date()
        }
      }

      emit('export-progress', 50)

      // Create worksheet
      const worksheet = XLSX.utils.json_to_sheet(processedData)

      // Set column widths
      worksheet['!cols'] = calculateColumnWidths(processedData)

      emit('export-progress', 70)

      // Add worksheet to workbook
      XLSX.utils.book_append_sheet(workbook, worksheet, sheetName)

      emit('export-progress', 85)

      // Generate Excel file
      const excelBuffer = XLSX.write(workbook, {
        bookType: 'xlsx',
        type: 'array',
        compression: true
      })

      // Create Blob and download
      const blob = new Blob([excelBuffer], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      })

      emit('export-progress', 95)

      // Use timestamp to ensure unique filename
      const timestamp = new Date().toISOString().replace(/[:.]/g, '-')
      const finalFilename = `${filename}_${timestamp}.xlsx`

      FileSaver.saveAs(blob, finalFilename)

      emit('export-progress', 100)

      // Wait for download to start
      await nextTick()

      return Promise.resolve()
    } catch (error) {
      throw new ExportError(`Excel export failed: ${(error as Error).message}`, 'EXPORT_FAILED', error)
    }
  }

  /** Handle export */
  const handleExport = useThrottleFn(async () => {
    if (isExporting.value) return

    isExporting.value = true

    try {
      // Validate data
      validateData(props.data)

      // Trigger before export event
      emit('before-export', props.data)

      // Execute export
      await exportToExcel(props.data, props.filename, props.sheetName)

      // Trigger success event
      emit('export-success', props.filename, props.data.length)

      // Show success message
      if (props.showSuccessMessage) {
        ElMessage.success({
          message: `Successfully exported ${props.data.length} records`,
          duration: 3000
        })
      }
    } catch (error) {
      const exportError =
        error instanceof ExportError
          ? error
          : new ExportError(`Export failed: ${(error as Error).message}`, 'UNKNOWN_ERROR', error)

      // Trigger error event
      emit('export-error', exportError)

      // Show error message
      if (props.showErrorMessage) {
        ElMessage.error({
          message: exportError.message,
          duration: 5000
        })
      }

      console.error('Excel export error:', exportError)
    } finally {
      isExporting.value = false
      emit('export-progress', 0)
    }
  }, 1000)

  // Expose methods for parent component
  defineExpose({
    exportData: handleExport,
    isExporting: readonly(isExporting),
    hasData
  })
</script>

<style scoped>
  .is-loading {
    animation: rotating 2s linear infinite;
  }

  @keyframes rotating {
    0% {
      transform: rotate(0deg);
    }

    100% {
      transform: rotate(360deg);
    }
  }
</style>
