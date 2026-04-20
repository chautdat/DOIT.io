<!-- Menu Management Page -->
<template>
  <div class="menu-page art-full-height">
    <!-- Search Bar -->
    <ArtSearchBar
      v-model="formFilters"
      :items="formItems"
      :showExpand="false"
      @reset="handleReset"
      @search="handleSearch"
    />

    <ElCard class="art-table-card">
      <!-- Table Header -->
      <ArtTableHeader
        :showZebra="false"
        :loading="loading"
        v-model:columns="columnChecks"
        @refresh="handleRefresh"
      >
        <template #left>
          <ElButton v-auth="'add'" @click="handleAddMenu" v-ripple> Add Menu </ElButton>
          <ElButton @click="toggleExpand" v-ripple>
            {{ isExpanded ? 'Collapse' : 'Expand' }}
          </ElButton>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        rowKey="path"
        :loading="loading"
        :columns="columns"
        :data="filteredTableData"
        :stripe="false"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        :default-expand-all="false"
      />

      <MenuDialog
        v-model:visible="dialogVisible"
        :type="dialogType"
        :editData="editData"
        :lockType="lockMenuType"
        @submit="handleSubmit"
      />
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { formatMenuTitle } from '@/utils/router'
  import ArtButtonTable from '@/components/core/forms/ArtButtonTable.vue'
  import { useTableColumns } from '@/hooks/core/useTableColumns'
  import type { AppRouteRecord } from '@/types/router'
  import MenuDialog from './modules/menu-dialog.vue'
  import { fetchGetMenuList } from '@/api/system-manage'
  import { ElTag, ElMessageBox } from 'element-plus'

  defineOptions({ name: 'Menus' })

  // State management
  const loading = ref(false)
  const isExpanded = ref(false)
  const tableRef = ref()

  // Dialog related
  const dialogVisible = ref(false)
  const dialogType = ref<'menu' | 'button'>('menu')
  const editData = ref<AppRouteRecord | any>(null)
  const lockMenuType = ref(false)

  // Search related
  const initialSearchState = {
    name: '',
    route: ''
  }

  const formFilters = reactive({ ...initialSearchState })
  const appliedFilters = reactive({ ...initialSearchState })

  const formItems = computed(() => [
    {
      label: 'Menu Name',
      key: 'name',
      type: 'input',
      props: { clearable: true }
    },
    {
      label: 'Route Path',
      key: 'route',
      type: 'input',
      props: { clearable: true }
    }
  ])

  onMounted(() => {
    getMenuList()
  })

  /**
   * Get menu list data
   */
  const getMenuList = async (): Promise<void> => {
    loading.value = true

    try {
      const list = await fetchGetMenuList()
      tableData.value = list
    } catch (error) {
      throw error instanceof Error ? error : new Error('Failed to get menu list')
    } finally {
      loading.value = false
    }
  }

  /**
   * Get menu type tag color
   * @param row Menu row data
   * @returns Tag color type
   */
  const getMenuTypeTag = (
    row: AppRouteRecord
  ): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
    if (row.meta?.isAuthButton) return 'danger'
    if (row.children?.length) return 'info'
    if (row.meta?.link && row.meta?.isIframe) return 'success'
    if (row.path) return 'primary'
    if (row.meta?.link) return 'warning'
    return 'info'
  }

  /**
   * Get menu type text
   * @param row Menu row data
   * @returns Menu type text
   */
  const getMenuTypeText = (row: AppRouteRecord): string => {
    if (row.meta?.isAuthButton) return 'Button'
    if (row.children?.length) return 'Directory'
    if (row.meta?.link && row.meta?.isIframe) return 'Iframe'
    if (row.path) return 'Menu'
    if (row.meta?.link) return 'External'
    return 'Unknown'
  }

  // Table column configuration
  const { columnChecks, columns } = useTableColumns(() => [
    {
      prop: 'meta.title',
      label: 'Menu Name',
      minWidth: 120,
      formatter: (row: AppRouteRecord) => formatMenuTitle(row.meta?.title)
    },
    {
      prop: 'type',
      label: 'Menu Type',
      formatter: (row: AppRouteRecord) => {
        return h(ElTag, { type: getMenuTypeTag(row) }, () => getMenuTypeText(row))
      }
    },
    {
      prop: 'path',
      label: 'Route',
      formatter: (row: AppRouteRecord) => {
        if (row.meta?.isAuthButton) return ''
        return row.meta?.link || row.path || ''
      }
    },
    {
      prop: 'meta.authList',
      label: 'Permission',
      formatter: (row: AppRouteRecord) => {
        if (row.meta?.isAuthButton) {
          return row.meta?.authMark || ''
        }
        if (!row.meta?.authList?.length) return ''
        return `${row.meta.authList.length} permissions`
      }
    },
    {
      prop: 'date',
      label: 'Edit Time',
      formatter: () => '2022-3-12 12:00:00'
    },
    {
      prop: 'status',
      label: 'Status',
      formatter: () => h(ElTag, { type: 'success' }, () => 'Enabled')
    },
    {
      prop: 'operation',
      label: 'Actions',
      width: 180,
      align: 'right',
      formatter: (row: AppRouteRecord) => {
        const buttonStyle = { style: 'text-align: right' }

        if (row.meta?.isAuthButton) {
          return h('div', buttonStyle, [
            h(ArtButtonTable, {
              type: 'edit',
              onClick: () => handleEditAuth(row)
            }),
            h(ArtButtonTable, {
              type: 'delete',
              onClick: () => handleDeleteAuth()
            })
          ])
        }

        return h('div', buttonStyle, [
          h(ArtButtonTable, {
            type: 'add',
            onClick: () => handleAddAuth(),
            title: 'Add Permission'
          }),
          h(ArtButtonTable, {
            type: 'edit',
            onClick: () => handleEditMenu(row)
          }),
          h(ArtButtonTable, {
            type: 'delete',
            onClick: () => handleDeleteMenu()
          })
        ])
      }
    }
  ])

  // Data related
  const tableData = ref<AppRouteRecord[]>([])

  /**
   * Reset search conditions
   */
  const handleReset = (): void => {
    Object.assign(formFilters, { ...initialSearchState })
    Object.assign(appliedFilters, { ...initialSearchState })
    getMenuList()
  }

  /**
   * Execute search
   */
  const handleSearch = (): void => {
    Object.assign(appliedFilters, { ...formFilters })
    getMenuList()
  }

  /**
   * Refresh menu list
   */
  const handleRefresh = (): void => {
    getMenuList()
  }

  /**
   * Deep clone object
   * @param obj Object to clone
   * @returns Cloned object
   */
  const deepClone = <T,>(obj: T): T => {
    if (obj === null || typeof obj !== 'object') return obj
    if (obj instanceof Date) return new Date(obj) as T
    if (Array.isArray(obj)) return obj.map((item) => deepClone(item)) as T

    const cloned = {} as T
    for (const key in obj) {
      if (Object.prototype.hasOwnProperty.call(obj, key)) {
        cloned[key] = deepClone(obj[key])
      }
    }
    return cloned
  }

  /**
   * Convert permission list to children
   * @param items Menu item array
   * @returns Converted menu item array
   */
  const convertAuthListToChildren = (items: AppRouteRecord[]): AppRouteRecord[] => {
    return items.map((item) => {
      const clonedItem = deepClone(item)

      if (clonedItem.children?.length) {
        clonedItem.children = convertAuthListToChildren(clonedItem.children)
      }

      if (item.meta?.authList?.length) {
        const authChildren: AppRouteRecord[] = item.meta.authList.map(
          (auth: { title: string; authMark: string }) => ({
            path: `${item.path}_auth_${auth.authMark}`,
            name: `${String(item.name)}_auth_${auth.authMark}`,
            meta: {
              title: auth.title,
              authMark: auth.authMark,
              isAuthButton: true,
              parentPath: item.path
            }
          })
        )

        clonedItem.children = clonedItem.children?.length
          ? [...clonedItem.children, ...authChildren]
          : authChildren
      }

      return clonedItem
    })
  }

  /**
   * Search menu
   * @param items Menu item array
   * @returns Search results array
   */
  const searchMenu = (items: AppRouteRecord[]): AppRouteRecord[] => {
    const results: AppRouteRecord[] = []

    for (const item of items) {
      const searchName = appliedFilters.name?.toLowerCase().trim() || ''
      const searchRoute = appliedFilters.route?.toLowerCase().trim() || ''
      const menuTitle = formatMenuTitle(item.meta?.title || '').toLowerCase()
      const menuPath = (item.path || '').toLowerCase()
      const nameMatch = !searchName || menuTitle.includes(searchName)
      const routeMatch = !searchRoute || menuPath.includes(searchRoute)

      if (item.children?.length) {
        const matchedChildren = searchMenu(item.children)
        if (matchedChildren.length > 0) {
          const clonedItem = deepClone(item)
          clonedItem.children = matchedChildren
          results.push(clonedItem)
          continue
        }
      }

      if (nameMatch && routeMatch) {
        results.push(deepClone(item))
      }
    }

    return results
  }

  // Filtered table data
  const filteredTableData = computed(() => {
    const searchedData = searchMenu(tableData.value)
    return convertAuthListToChildren(searchedData)
  })

  /**
   * Add menu
   */
  const handleAddMenu = (): void => {
    dialogType.value = 'menu'
    editData.value = null
    lockMenuType.value = true
    dialogVisible.value = true
  }

  /**
   * Add permission button
   */
  const handleAddAuth = (): void => {
    dialogType.value = 'menu'
    editData.value = null
    lockMenuType.value = false
    dialogVisible.value = true
  }

  /**
   * Edit menu
   * @param row Menu row data
   */
  const handleEditMenu = (row: AppRouteRecord): void => {
    dialogType.value = 'menu'
    editData.value = row
    lockMenuType.value = true
    dialogVisible.value = true
  }

  /**
   * Edit permission button
   * @param row Permission row data
   */
  const handleEditAuth = (row: AppRouteRecord): void => {
    dialogType.value = 'button'
    editData.value = {
      title: row.meta?.title,
      authMark: row.meta?.authMark
    }
    lockMenuType.value = false
    dialogVisible.value = true
  }

  /**
   * Menu form data type
   */
  interface MenuFormData {
    name: string
    path: string
    component?: string
    icon?: string
    roles?: string[]
    sort?: number
    [key: string]: any
  }

  /**
   * Submit form data
   * @param formData Form data
   */
  const handleSubmit = (formData: MenuFormData): void => {
    console.log('Submit data:', formData)
    // TODO: Call API to save data
    getMenuList()
  }

  /**
   * Delete menu
   */
  const handleDeleteMenu = async (): Promise<void> => {
    try {
      await ElMessageBox.confirm(
        'Are you sure to delete this menu? This cannot be undone.',
        'Warning',
        {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }
      )
      ElMessage.success('Deleted successfully')
      getMenuList()
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('Delete failed')
      }
    }
  }

  /**
   * Delete permission button
   */
  const handleDeleteAuth = async (): Promise<void> => {
    try {
      await ElMessageBox.confirm(
        'Are you sure to delete this permission? This cannot be undone.',
        'Warning',
        {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }
      )
      ElMessage.success('Deleted successfully')
      getMenuList()
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('Delete failed')
      }
    }
  }

  /**
   * Toggle expand/collapse all menus
   */
  const toggleExpand = (): void => {
    isExpanded.value = !isExpanded.value
    nextTick(() => {
      if (tableRef.value?.elTableRef && filteredTableData.value) {
        const processRows = (rows: AppRouteRecord[]) => {
          rows.forEach((row) => {
            if (row.children?.length) {
              tableRef.value.elTableRef.toggleRowExpansion(row, isExpanded.value)
              processRows(row.children)
            }
          })
        }
        processRows(filteredTableData.value)
      }
    })
  }
</script>
