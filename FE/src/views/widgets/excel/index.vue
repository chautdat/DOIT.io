<template>
  <div class="page-content">
    <ArtExcelImport @import-success="handleImportSuccess" @import-error="handleImportError">
      <template #import-text> Upload Excel </template>
    </ArtExcelImport>

    <ArtExcelExport
      style="margin-left: 10px"
      :data="tableData"
      filename="user-data-1"
      sheetName="User List"
      type="success"
      :headers="headers"
      auto-index
      :columns="columnConfig"
      @export-success="handleExportSuccess"
      @export-error="handleExportError"
      @export-progress="handleProgress"
    >
      Export Excel
    </ArtExcelExport>

    <ElButton type="danger" @click="handleClear" v-ripple>Clear Data</ElButton>

    <ArtTable :data="tableData" style="margin-top: 10px">
      <ElTableColumn
        v-for="key in Object.keys(headers)"
        :key="key"
        :prop="key"
        :label="headers[key as keyof typeof headers]"
      />
    </ArtTable>
  </div>
</template>

<script setup lang="ts">
  defineOptions({ name: 'WidgetsExcel' })

  /**
   * Table data type definition
   */
  interface TableData {
    name: string
    age: number
    city: string
  }

  /**
   * Table data
   */
  const tableData = ref<TableData[]>([
    { name: 'John', age: 20, city: 'Shanghai' },
    { name: 'Jane', age: 25, city: 'Beijing' },
    { name: 'Mike', age: 30, city: 'Guangzhou' },
    { name: 'Sarah', age: 35, city: 'Shenzhen' },
    { name: 'Tom', age: 28, city: 'Hangzhou' },
    { name: 'Lisa', age: 32, city: 'Chengdu' },
    { name: 'David', age: 27, city: 'Wuhan' },
    { name: 'Emma', age: 40, city: 'Nanjing' },
    { name: 'James', age: 22, city: 'Chongqing' },
    { name: 'Anna', age: 33, city: "Xi'an" }
  ])

  /**
   * Header mapping configuration
   * For Excel import/export field mapping
   */
  const headers = {
    name: 'Name',
    age: 'Age',
    city: 'City'
  }

  /**
   * Column configuration
   * For Excel export column width and formatting
   */
  const columnConfig = {
    name: {
      title: 'Name',
      width: 20,
      formatter: (value: unknown) => (value ? String(value) : 'Unknown')
    },
    age: {
      title: 'Age',
      width: 10,
      formatter: (value: unknown) => (value ? `${value} years` : '0 years')
    },
    city: {
      title: 'City',
      width: 12,
      formatter: (value: unknown) => (value ? String(value) : 'Unknown')
    }
  }

  /**
   * Handle Excel import success
   * Convert imported data to table data format
   * @param data Imported raw data
   */
  const handleImportSuccess = (data: Array<Record<string, unknown>>) => {
    const formattedData: TableData[] = data.map((item) => ({
      name: String(item['Name'] || item['name'] || ''),
      age: Number(item['Age'] || item['age']) || 0,
      city: String(item['City'] || item['city'] || '')
    }))
    tableData.value = formattedData
    ElMessage.success(`Successfully imported ${formattedData.length} records`)
  }

  /**
   * Handle Excel import error
   * @param error Error object
   */
  const handleImportError = (error: Error) => {
    console.error('Import failed:', error)
    ElMessage.error(`Import failed: ${error.message}`)
  }

  /**
   * Handle Excel export success
   */
  const handleExportSuccess = () => {
    console.log('Export success')
    ElMessage.success('Excel exported successfully')
  }

  /**
   * Handle Excel export error
   * @param error Error object
   */
  const handleExportError = (error: Error) => {
    ElMessage.error(`Export failed: ${error.message}`)
  }

  /**
   * Handle export progress
   * @param progress Export progress percentage
   */
  const handleProgress = (progress: number) => {
    console.log('Export progress:', progress)
  }

  /**
   * Clear table data
   */
  const handleClear = () => {
    tableData.value = []
    ElMessage.info('Data cleared')
  }
</script>
