<!-- Table Component -->
<!-- Supports: All el-table attributes, events, slots, same as official documentation -->
<!-- Extended features: Pagination, custom columns, loading, table border, stripe, table size, header background config -->
<!-- Get ref: Exposes elTableRef, external access via ref.value.elTableRef to call el-table methods -->
<template>
  <div class="art-table" :class="{ 'is-empty': isEmpty }" :style="containerHeight">
    <ElTable ref="elTableRef" v-loading="!!loading" v-bind="mergedTableProps">
      <template v-for="col in columns" :key="col.prop || col.type">
        <ElTableColumn v-if="col.type === 'globalIndex'" v-bind="{ ...col }">
          <template #default="{ $index }">
            <span>{{ getGlobalIndex($index) }}</span>
          </template>
        </ElTableColumn>

        <ElTableColumn v-else-if="col.type === 'expand'" v-bind="cleanColumnProps(col)">
          <template #default="{ row }">
            <component :is="col.formatter ? col.formatter(row) : null" />
          </template>
        </ElTableColumn>

        <ElTableColumn v-else v-bind="cleanColumnProps(col)">
          <template v-if="col.useHeaderSlot && col.prop" #header="headerScope">
            <slot
              :name="col.headerSlotName || `${col.prop}-header`"
              v-bind="{ ...headerScope, prop: col.prop, label: col.label }"
            >
              {{ col.label }}
            </slot>
          </template>
          <template v-if="col.useSlot && col.prop" #default="slotScope">
            <slot
              v-if="shouldRenderSlotScope(slotScope)"
              :name="col.slotName || col.prop"
              v-bind="{
                ...slotScope,
                prop: col.prop,
                value: col.prop ? slotScope.row[col.prop] : undefined
              }"
            />
          </template>
        </ElTableColumn>
      </template>

      <template v-if="$slots.default" #default><slot /></template>

      <template #empty>
        <div v-if="loading"></div>
        <ElEmpty v-else :description="emptyText" :image-size="120" />
      </template>
    </ElTable>

    <div
      class="pagination custom-pagination"
      v-if="showPagination"
      :class="mergedPaginationOptions?.align"
      ref="paginationRef"
    >
      <ElPagination
        v-bind="mergedPaginationOptions"
        :total="pagination?.total"
        :disabled="loading"
        :page-size="pagination?.size"
        :current-page="pagination?.current"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, nextTick, watchEffect, getCurrentInstance, useAttrs } from 'vue'
  import type { ElTable, TableProps } from 'element-plus'
  import { storeToRefs } from 'pinia'
  import { ColumnOption } from '@/types'
  import { useTableStore } from '@/store/modules/table'
  import { useCommon } from '@/hooks/core/useCommon'
  import { useTableHeight } from '@/hooks/core/useTableHeight'
  import { useResizeObserver, useWindowSize } from '@vueuse/core'

  defineOptions({ name: 'ArtTable' })

  const { width } = useWindowSize()
  const elTableRef = ref<InstanceType<typeof ElTable> | null>(null)
  const paginationRef = ref<HTMLElement>()
  const tableHeaderRef = ref<HTMLElement>()
  const tableStore = useTableStore()
  const { isBorder, isZebra, tableSize, isFullScreen, isHeaderBackground } = storeToRefs(tableStore)

  /** Pagination configuration interface */
  interface PaginationConfig {
    /** Current page number */
    current: number
    /** Number of items per page */
    size: number
    /** Total number of items */
    total: number
  }

  /** Pagination options interface */
  interface PaginationOptions {
    /** Page size selector options */
    pageSizes?: number[]
    /** Pagination alignment */
    align?: 'left' | 'center' | 'right'
    /** Pagination layout */
    layout?: string
    /** Whether to show pagination background */
    background?: boolean
    /** Whether to hide pagination when only one page */
    hideOnSinglePage?: boolean
    /** Pagination size */
    size?: 'small' | 'default' | 'large'
    /** Number of page buttons */
    pagerCount?: number
  }

  /** ArtTable component Props interface */
  interface ArtTableProps extends TableProps<Record<string, any>> {
    /** Loading state */
    loading?: boolean
    /** Column configuration */
    columns?: ColumnOption[]
    /** Pagination state */
    pagination?: PaginationConfig
    /** Pagination options */
    paginationOptions?: PaginationOptions
    /** Empty table height */
    emptyHeight?: string
    /** Text displayed when data is empty */
    emptyText?: string
    /** Whether to enable ArtTableHeader for table height auto-adaptation */
    showTableHeader?: boolean
  }

  const props = withDefaults(defineProps<ArtTableProps>(), {
    columns: () => [],
    fit: true,
    showHeader: true,
    stripe: undefined,
    border: undefined,
    size: undefined,
    emptyHeight: '100%',
    emptyText: 'No Data',
    showTableHeader: true
  })
  const instance = getCurrentInstance()
  const attrs = useAttrs()

  const LAYOUT = {
    MOBILE: 'prev, pager, next, sizes, jumper, total',
    IPAD: 'prev, pager, next, jumper, total',
    DESKTOP: 'total, prev, pager, next, sizes, jumper'
  }

  const layout = computed(() => {
    if (width.value < 768) {
      return LAYOUT.MOBILE
    } else if (width.value < 1024) {
      return LAYOUT.IPAD
    } else {
      return LAYOUT.DESKTOP
    }
  })

  // Default pagination constants
  const DEFAULT_PAGINATION_OPTIONS: PaginationOptions = {
    pageSizes: [10, 20, 30, 50, 100],
    align: 'center',
    background: true,
    layout: layout.value,
    hideOnSinglePage: false,
    size: 'default',
    pagerCount: width.value > 1200 ? 7 : 5
  }

  // Merge pagination config
  const mergedPaginationOptions = computed(() => ({
    ...DEFAULT_PAGINATION_OPTIONS,
    ...props.paginationOptions
  }))

  // Border (priority: props > store)
  const border = computed(() => props.border ?? isBorder.value)
  // Stripe
  const stripe = computed(() => props.stripe ?? isZebra.value)
  // Table size
  const size = computed(() => props.size ?? tableSize.value)
  // Whether data is empty
  const isEmpty = computed(() => props.data?.length === 0)

  const paginationHeight = ref(0)
  const tableHeaderHeight = ref(0)

  // Use useResizeObserver to monitor pagination height changes
  useResizeObserver(paginationRef, (entries) => {
    const entry = entries[0]
    if (entry) {
      // Use requestAnimationFrame to avoid ResizeObserver loop warning
      requestAnimationFrame(() => {
        paginationHeight.value = entry.contentRect.height
      })
    }
  })

  // Use useResizeObserver to monitor table header height changes
  useResizeObserver(tableHeaderRef, (entries) => {
    const entry = entries[0]
    if (entry) {
      // Use requestAnimationFrame to avoid ResizeObserver loop warning
      requestAnimationFrame(() => {
        tableHeaderHeight.value = entry.contentRect.height
      })
    }
  })

  // Spacing constant between pagination and table (computed property, responsive to showTableHeader changes)
  const PAGINATION_SPACING = computed(() => (props.showTableHeader ? 6 : 15))

  // Use table height calculation Hook
  const { containerHeight } = useTableHeight({
    showTableHeader: computed(() => props.showTableHeader),
    paginationHeight,
    tableHeaderHeight,
    paginationSpacing: PAGINATION_SPACING
  })

  // Table height logic
  const height = computed(() => {
    // Full screen mode fills entire screen
    if (isFullScreen.value) return '100%'
    // Fixed height when empty and not loading
    if (isEmpty.value && !props.loading) return props.emptyHeight
    // Use passed in height
    if (props.height) return props.height
    // Default fills container height
    return '100%'
  })

  // Header background color style
  const headerCellStyle = computed(() => ({
    background: isHeaderBackground.value
      ? 'var(--el-fill-color-lighter)'
      : 'var(--default-box-color)',
    ...(props.headerCellStyle || {}) // Merge user passed styles
  }))

  // Only override when explicitly passed, to avoid inherited Boolean props overwriting official defaults
  const hasExplicitTableProp = (propName: string): boolean => {
    const rawProps = (instance?.vnode.props || {}) as Record<string, unknown>
    const kebabName = propName.replace(/[A-Z]/g, (match) => `-${match.toLowerCase()}`)
    return propName in rawProps || kebabName in rawProps
  }

  const mergedTableProps = computed(() => ({
    ...attrs,
    ...props,
    height: height.value,
    stripe: stripe.value,
    border: border.value,
    size: size.value,
    headerCellStyle: headerCellStyle.value,
    // Element Plus default is true, should not be overridden to false when not explicitly passed
    selectOnIndeterminate: hasExplicitTableProp('selectOnIndeterminate')
      ? props.selectOnIndeterminate
      : undefined
  }))

  // Whether to show pagination
  const showPagination = computed(() => props.pagination && !isEmpty.value)

  // Element Plus pre-renders with $index = -1 in some scenarios.
  // This doesn't affect normal display, but causes ElForm to incorrectly register fields like lineList.-1.xxx
  const shouldRenderSlotScope = (slotScope: { $index?: number }) => {
    return slotScope.$index === undefined || slotScope.$index >= 0
  }

  // Clean column properties, remove custom slot-related properties to ensure they're not misinterpreted by ElTableColumn
  const cleanColumnProps = (col: ColumnOption) => {
    const columnProps = { ...col }
    // Delete custom slot control properties
    delete columnProps.useHeaderSlot
    delete columnProps.headerSlotName
    delete columnProps.useSlot
    delete columnProps.slotName
    return columnProps
  }

  // Page size change
  const handleSizeChange = (val: number) => {
    emit('pagination:size-change', val)
  }

  // Current page change
  const handleCurrentChange = (val: number) => {
    emit('pagination:current-change', val)
    scrollToTop() // Scroll to table top after page change
  }

  const { scrollToTop: scrollPageToTop } = useCommon()

  // Scroll table content to top, and optionally scroll page to top
  const scrollToTop = () => {
    nextTick(() => {
      elTableRef.value?.setScrollTop(0) // Scroll ElTable internal scrollbar to top
      scrollPageToTop() // Call common composable to scroll page to top
    })
  }

  // Global index
  const getGlobalIndex = (index: number) => {
    if (!props.pagination) return index + 1
    const { current, size } = props.pagination
    return (current - 1) * size + index + 1
  }

  const emit = defineEmits<{
    (e: 'pagination:size-change', val: number): void
    (e: 'pagination:current-change', val: number): void
  }>()

  // Find and bind table header element - optimized with VueUse
  const findTableHeader = () => {
    if (!props.showTableHeader) {
      tableHeaderRef.value = undefined
      return
    }

    const tableHeader = document.getElementById('art-table-header')
    if (tableHeader) {
      tableHeaderRef.value = tableHeader
    } else {
      // If table header not found, set to undefined, useElementSize will return 0
      tableHeaderRef.value = undefined
    }
  }

  watchEffect(
    () => {
      // Access reactive data to establish dependency tracking
      void props.data?.length // Track data changes
      const shouldShow = props.showTableHeader

      // Only search for table header when needed
      if (shouldShow) {
        nextTick(() => {
          findTableHeader()
        })
      } else {
        // Clear reference when not showing
        tableHeaderRef.value = undefined
      }
    },
    { flush: 'post' }
  )

  defineExpose({
    scrollToTop,
    elTableRef
  })
</script>

<style lang="scss" scoped>
  @use './style';
</style>
