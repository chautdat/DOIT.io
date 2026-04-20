<template>
  <div class="flex flex-col gap-4 pb-5">
    <ElCard class="art-card-xs">
      <template #header>
        <div class="flex-wrap gap-3 flex-cb">
          <h3 class="m-0">Advanced table capabilities demo</h3>
          <div class="flex flex-wrap gap-2">
            <ElTag type="success" effect="light">Smart cache</ElTag>
            <ElTag type="primary" effect="light">Debounced search</ElTag>
            <ElTag type="warning" effect="light">Multiple refresh modes</ElTag>
            <ElTag type="info" effect="light">Error handling</ElTag>
          </div>
        </div>
      </template>
      <div>
        <p class="m-0 mb-4 leading-[1.6] text-g-700">
          Integrated search, refresh, fullscreen, size control, column hide/show, drag sorting, table styling controls, and a built-in useTable
          composable. It provides a powerful composable API with data fetching, smart cache (LRU), 
          multiple refresh strategies, and other core features to improve table development efficiency.
        </p>

        <div class="my-4" v-if="showDebugPanel">
          <ElCollapse v-model="debugActiveNames">
            <ElCollapseItem name="cache" title="Cache stats demo">
              <div class="flex flex-col gap-2">
                <div class="flex-cb">
                  <span class="font-medium text-g-700">Cache status：</span>
                  <ElTag type="success">Enabled</ElTag>
                </div>
                <div class="flex-cb">
                  <span class="font-medium text-g-700">Cache entries：</span>
                  <span class="font-semibold text-theme">{{ cacheInfo.total }}</span>
                </div>
                <div class="flex-cb">
                  <span class="font-medium text-g-700">Cache size：</span>
                  <span class="font-semibold text-theme">{{ cacheInfo.size }}</span>
                </div>
                <div class="flex-cb">
                  <span class="font-medium text-g-700">Hit rate：</span>
                  <span class="font-semibold text-theme">{{ cacheInfo.hitRate }}</span>
                </div>

                <div class="flex gap-2 mt-2">
                  <ElButton size="small" @click="handleClearCache">Clear cache</ElButton>
                  <ElButton size="small" @click="handleCleanExpiredCache">Clean expired cache</ElButton>
                  <ElButton size="small" @click="handleTestCache">Test cache</ElButton>
                  <ElButton size="small" @click="forceRefreshCacheInfo">Refresh cache info</ElButton>
                </div>
              </div>
            </ElCollapseItem>
            <ElCollapseItem name="logs" title="Cache log">
              <div class="flex flex-col gap-2">
                <div class="max-h-50 overflow-y-auto">
                  <div v-if="cacheDebugLogs.length === 0" class="p-5 text-center">
                    <ElEmpty description="No cache logs yet" :image-size="60" />
                  </div>
                  <div v-else class="flex flex-col gap-1">
                    <div
                      v-for="(log, index) in cacheDebugLogs"
                      :key="index"
                      class="p-1.5 px-2 text-xs leading-[1.4] bg-g-200 border-l-1 border-g-400 rounded"
                      :class="{
                        'bg-[rgba(103,194,58,0.1)] !border-l-success': log.includes('✅'),
                        'bg-[rgba(64,158,255,0.1)] !border-l-theme': log.includes('🎯'),
                        'bg-[rgba(245,108,108,0.1)] !border-l-danger': log.includes('❌')
                      }"
                    >
                      {{ log }}
                    </div>
                  </div>
                </div>
                <div class="flex gap-2 mt-2">
                  <ElButton size="small" @click="cacheDebugLogs = []">Clear log</ElButton>
                </div>
              </div>
            </ElCollapseItem>
            <ElCollapseItem name="request" title="Request status">
              <div class="flex flex-col gap-2">
                <div class="flex-cb">
                  <span class="font-medium text-g-700">Loading status：</span>
                  <ElTag :type="loading ? 'warning' : 'success'">
                    {{ loading ? 'Loading' : 'Idle' }}
                  </ElTag>
                </div>
                <div class="flex-cb">
                  <span class="font-medium text-g-700">Data status：</span>
                  <ElTag :type="hasData ? 'success' : 'info'">
                    {{ hasData ? `${data.length} rows` : 'No data' }}
                  </ElTag>
                </div>
                <div class="flex-cb">
                  <span class="font-medium text-g-700">Error status：</span>
                  <ElTag :type="error ? 'danger' : 'success'">
                    {{ error ? 'Error' : 'Normal' }}
                  </ElTag>
                </div>
                <div class="flex flex-col gap-2">
                  <span class="font-medium text-g-700">Current request params：</span>
                  <ElText
                    tag="pre"
                    class="max-h-50 p-2 overflow-y-auto text-xs bg-g-200 border border-g-400 rounded-md"
                    >{{ JSON.stringify(requestParams, null, 2) }}</ElText
                  >
                </div>
                <div class="flex gap-2 mt-2">
                  <ElButton size="small" @click="handleCancelRequest">Cancel request</ElButton>
                  <ElButton size="small" @click="handleClearData">ClearData</ElButton>
                </div>
              </div>
            </ElCollapseItem>
          </ElCollapse>
        </div>

        <div class="flex flex-wrap gap-4 mt-4">
          <ElSwitch v-model="showDebugPanel" active-text="Debug panel" />
          <ElText type="info" size="small"> 💡 Cache is enabled; use the debug panel for details </ElText>
        </div>
      </div>
    </ElCard>

    <ArtSearchBar
      ref="searchBarRef"
      v-model="searchFormState"
      :items="searchItems"
      :rules="rules"
      :is-expand="false"
      :show-expand="true"
      :show-reset-button="true"
      :show-search-button="true"
      :disabled-search-button="false"
      @search="handleSearch"
      @reset="handleReset"
    />

    <ElCard class="flex-1 art-table-card" style="margin-top: 0">
      <template #header>
        <div class="flex-cb">
          <h4 class="m-0">User data table</h4>
          <div class="flex gap-2">
            <ElTag v-if="error" type="danger">{{ error.message }}</ElTag>
            <ElTag v-else-if="loading" type="warning">Loading...</ElTag>
            <ElTag v-else type="success">{{ data.length }} rows</ElTag>
          </div>
        </div>
      </template>

      <ArtTableHeader
        v-model:columns="columnChecks"
        :loading="loading"
        @refresh="handleRefresh"
        layout="refresh,size,fullscreen,columns,settings"
        fullClass="art-table-card"
      >
        <template #left>
          <ElSpace wrap>
            <ElButton type="primary" @click="handleAdd" v-ripple>
              <ElIcon>
                <Plus />
              </ElIcon>
              Add user
            </ElButton>

            <ArtExcelExport
              :data="data as any"
              :columns="exportColumns as any"
              filename="userData"
              :auto-index="true"
              button-text="Export"
              @export-success="handleExportSuccess"
            />
            <ArtExcelImport
              @import-success="handleImportSuccess"
              @import-error="handleImportError"
              style="margin: 0 12px"
            />

            <ElButton @click="handleClearData" plain v-ripple> ClearData </ElButton>

            <ElButton @click="handleBatchDelete" :disabled="selectedRows.length === 0" v-ripple>
              <ElIcon>
                <Delete />
              </ElIcon>
              Batch delete ({{ selectedRows.length }})
            </ElButton>
            <ElDropdown @command="handleColumnCommand" style="margin-left: 10px">
              <ElButton type="primary" plain>
                Dynamic table column updates
                <ElIcon class="el-icon--right">
                  <ArrowDown />
                </ElIcon>
              </ElButton>
              <template #dropdown>
                <ElDropdownMenu>
                  <ElDropdownItem command="addColumn">Add column (remark column)</ElDropdownItem>
                  <ElDropdownItem command="batchAddColumns">Batch add (remark, tag)</ElDropdownItem>
                  <ElDropdownItem command="toggleColumn">Toggle column (phone)</ElDropdownItem>
                  <ElDropdownItem command="batchToggleColumns"
                    >Batch toggle (gender, phone)</ElDropdownItem
                  >
                  <ElDropdownItem command="removeColumn">Remove column (status column)</ElDropdownItem>
                  <ElDropdownItem command="batchRemoveColumns"
                    >Batch remove (status, rating)</ElDropdownItem
                  >
                  <ElDropdownItem command="updateColumn">Update column (phone)</ElDropdownItem>
                  <ElDropdownItem command="batchUpdateColumns"
                    >Batch update (gender, phone)</ElDropdownItem
                  >
                  <ElDropdownItem command="reorderColumns"
                    >Swap column order (gender, phone)</ElDropdownItem
                  >
                  <ElDropdownItem command="resetColumns" divided>Reset all column settings</ElDropdownItem>
                </ElDropdownMenu>
              </template>
            </ElDropdown>
          </ElSpace>
        </template>
      </ArtTableHeader>

      <ArtTable
        ref="tableRef"
        :loading="loading"
        :pagination="pagination"
        :data="data"
        :columns="columns"
        :height="computedTableHeight"
        empty-height="360px"
        @selection-change="handleSelectionChange"
        @row-click="handleRowClick"
        @header-click="handleHeaderClick"
        @sort-change="handleSortChange"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #avatar="{ row }">
          <div class="flex gap-3 user-info">
            <ElAvatar :src="row.avatar" :size="40" />
            <div class="flex-1 min-w-0">
              <p class="m-0 overflow-hidden font-medium text-ellipsis whitespace-nowrap">{{
                row.userName
              }}</p>
              <p
                class="m-0 mt-1 overflow-hidden text-xs text-g-700 text-ellipsis whitespace-nowrap"
                >{{ row.userEmail }}</p
              >
            </div>
          </div>
        </template>

        <template #avatar-header="{ column }">
          <div class="flex-c gap-1">
            <span>{{ column.label }}</span>
            <ElTooltip content="Includes avatar, name, and email" placement="top">
              <ElIcon>
                <QuestionFilled />
              </ElIcon>
            </ElTooltip>
          </div>
        </template>

        <template #status="{ row }">
          <ElTag :type="getUserStatusConfig(row.status).type" effect="light">
            {{ getUserStatusConfig(row.status).text }}
          </ElTag>
        </template>

        <template #score="{ row }">
          <ElRate v-model="row.score" disabled size="small" />
        </template>

        <template #operation="{ row }">
          <div class="flex">
            <ArtButtonTable type="view" :row="row" @click="handleView(row)" />
            <ArtButtonTable type="add" :row="row" @click="handleAdd()" />
            <ArtButtonTable type="edit" :row="row" @click="handleEdit(row)" />
            <ArtButtonTable type="delete" :row="row" @click="handleDelete(row)" />
          </div>
        </template>

        <template #userPhone-header="{ column }">
          <ElPopover placement="bottom" :width="200" trigger="hover">
            <template #reference>
              <div class="inline-block gap-1 text-theme c-p custom-header">
                <span>{{ column.label }}</span>
                <ElIcon>
                  <Search />
                </ElIcon>
              </div>
            </template>
            <ElInput
              v-model="phoneSearch"
              placeholder="Search phone number"
              size="small"
              @input="handlePhoneSearch"
            >
              <template #prefix>
                <ElIcon>
                  <Search />
                </ElIcon>
              </template>
            </ElInput>
          </ElPopover>
        </template>
      </ArtTable>
    </ElCard>

    <ElCard class="art-card-xs">
      <template #header>
        <h4 class="m-0">Advanced features demo</h4>
      </template>
      <div class="flex flex-col gap-6">
        <div class="p-4 bg-g-200 border-full-d rounded-lg">
          <h5 class="m-0 mb-4 text-sm font-semibold">Event listener demo</h5>
          <div class="flex flex-wrap gap-2 mb-3 last:mb-0">
            <ElButton @click="toggleEventDemo" :type="eventDemoEnabled ? 'success' : 'primary'">
              {{ eventDemoEnabled ? 'Disable' : 'Enable' }} event listener
            </ElButton>
            <ElButton @click="clearEventLogs" v-if="eventDemoEnabled">Clear log</ElButton>
          </div>
          <div
            v-if="eventDemoEnabled && eventLogs.length > 0"
            class="p-3 mt-3 bg-g-200 border border-g-400 rounded-md"
          >
            <div class="flex-cb mb-2 font-medium text-g-700">
              <span>Recent event log：</span>
              <ElTag size="small">{{ eventLogs.length }} items</ElTag>
            </div>
            <div class="flex flex-col gap-1 max-h-50 overflow-y-auto">
              <div
                v-for="(log, index) in eventLogs.slice(0, 20)"
                :key="index"
                class="flex-c gap-2 p-1.5 px-2 text-xs bg-g-300 border-l-1 border-g-400 rounded"
              >
                <ElTag :type="getEventType(log.type)" size="small">{{ log.type }}</ElTag>
                <span class="flex-1 text-g-700">{{ log.message }}</span>
                <span class="text-xs text-g-600">{{ log.time }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="p-4 bg-g-200 border-full-d rounded-lg">
          <h5 class="m-0 mb-4 text-sm font-semibold">Table settings demo</h5>
          <div class="flex flex-wrap gap-2 mb-3 last:mb-0">
            <ElSwitch
              v-model="tableConfig.fixedHeight"
              active-text="Fixed height (500px)"
              inactive-text="Auto height"
              class="ml-2"
            />
          </div>
        </div>

        <div class="p-4 bg-g-200 border-full-d rounded-lg">
          <h5 class="m-0 mb-4 text-sm font-semibold">Custom actions</h5>
          <div class="flex flex-wrap gap-2 mb-3 last:mb-0">
            <ElButton @click="handleScrollToTop">Scroll to top</ElButton>
            <ElButton @click="handleScrollToPosition">Scroll to position</ElButton>
            <ElButton @click="handleToggleSelection">Toggle select all</ElButton>
            <ElButton @click="handleGetTableInfo">Get table info</ElButton>
          </div>
        </div>
      </div>
    </ElCard>

    <ElCard class="art-card-xs">
      <template #header>
        <h4 class="m-0">Cache refresh strategy demo</h4>
      </template>
      <div class="flex flex-wrap gap-2 max-md:flex-col">
        <ElButton @click="refreshData" v-ripple>
          <ElIcon>
            <Refresh />
          </ElIcon>
          General refresh
        </ElButton>
        <ElButton @click="refreshSoft" v-ripple>
          <ElIcon>
            <Refresh />
          </ElIcon>
          Soft refresh
        </ElButton>
        <ElButton @click="refreshCreate" v-ripple>
          <ElIcon>
            <Plus />
          </ElIcon>
          Refresh after create
        </ElButton>
        <ElButton @click="refreshUpdate" v-ripple>
          <ElIcon>
            <Edit />
          </ElIcon>
          Refresh after update
        </ElButton>
        <ElButton @click="refreshRemove" v-ripple>
          <ElIcon>
            <Delete />
          </ElIcon>
          Refresh after delete
        </ElButton>
      </div>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, watch, nextTick } from 'vue'
  import {
    Plus,
    Delete,
    Edit,
    Search,
    Refresh,
    QuestionFilled,
    ArrowDown
  } from '@element-plus/icons-vue'
  import { ElMessageBox } from 'element-plus'
  import { useTable, CacheInvalidationStrategy } from '@/hooks/core/useTable'
  import { fetchGetUserList } from '@/api/system-manage'
  import { ACCOUNT_TABLE_DATA } from '@/mock/temp/formData'
  import { getColumnKey } from '@/hooks/core/useTableColumns'

  defineOptions({ name: 'AdvancedTableDemo' })

  type UserListItem = Api.SystemManage.UserListItem

  const selectedRows = ref<UserListItem[]>([])

  const tableRef = ref()

  const showDebugPanel = ref(false)
  const debugActiveNames = ref(['cache', 'request', 'logs'])

  const cacheDebugLogs = ref<string[]>([])
  const requestParams = ref<any>({
    current: 1,
    size: 20,
    name: '',
    phone: '',
    status: '',
    department: '',
    daterange: undefined
  })

  const cacheKeys = ref<string[]>([])

  const phoneSearch = ref('')

  const eventDemoEnabled = ref(false)
  const eventLogs = ref<Array<{ type: string; message: string; time: string }>>([])

  const tableConfig = ref({
    height: '100%',
    fixedHeight: false
  })

  const computedTableHeight = computed(() => {
    return tableConfig.value.fixedHeight ? '500px' : ''
  })

  const searchBarRef = ref()

  const rules = {
    name: [{ required: true, message: 'Please enter a username', trigger: 'blur' }],
    phone: [
      { required: true, message: 'Please enter a phone number', trigger: 'blur' },
      { pattern: /^1[3456789]\d{9}$/, message: 'Please enter a valid phone number', trigger: 'blur' }
    ]
  }

  const searchFormState = ref({
    name: '',
    phone: '',
    status: '1',
    department: '',
    daterange: ['2025-01-01', '2025-02-10']
  })

  // const searchFormState = ref({ ...defaultFilter.value })

  const USER_STATUS_CONFIG = {
    '1': { type: 'success' as const, text: 'Online' },
    '2': { type: 'info' as const, text: 'Offline' },
    '3': { type: 'warning' as const, text: 'Error' },
    '4': { type: 'danger' as const, text: 'Deactivated' }
  } as const

  const searchItems = computed(() => [
    {
      key: 'name',
      label: 'Username',
      type: 'input',
      props: {
        placeholder: 'Please enter a username'
      }
    },
    {
      key: 'phone',
      label: 'Phone',
      type: 'input',
      props: {
        placeholder: 'Please enter a phone number',
        maxlength: '11'
      }
    },
    {
      key: 'status',
      label: 'Status',
      type: 'select',
      options: [
        { label: 'All', value: '' },
        { label: 'Online', value: '1' },
        { label: 'Offline', value: '2' },
        { label: 'Error', value: '3' },
        { label: 'Deactivated', value: '4' }
      ]
    },
    {
      key: 'department',
      label: 'Department',
      type: 'select',
      options: [
        { label: 'All', value: '' },
        { label: 'Engineering', value: 'Engineering' },
        { label: 'Product', value: 'Product' },
        { label: 'Operations', value: 'Operations' },
        { label: 'Marketing', value: 'Marketing' },
        { label: 'Design', value: 'Design' }
      ]
    },
    {
      key: 'daterange',
      label: 'Date range',
      type: 'daterange',
      props: {
        type: 'daterange',
        startPlaceholder: 'Start date',
        endPlaceholder: 'End date',
        valueFormat: 'YYYY-MM-DD'
      }
    }
  ])

  const exportColumns = computed(() => ({
    userName: { title: 'Username', width: 15 },
    userEmail: { title: 'Email', width: 20 },
    userPhone: { title: 'Phone', width: 15 },
    userGender: { title: 'Gender', width: 10 },
    department: { title: 'Department', width: 15 },
    status: {
      title: 'Status',
      width: 10,
      formatter: (value: string) => getUserStatusConfig(value).text
    }
  }))

  const getUserStatusConfig = (status: string) => {
    return (
      USER_STATUS_CONFIG[status as keyof typeof USER_STATUS_CONFIG] || {
        type: 'info' as const,
        text: 'Unknown'
      }
    )
  }

  const buildSearchParams = (params: typeof searchFormState.value) => {
    const { daterange, ...filtersParams } = params
    const [startTime, endTime] = Array.isArray(daterange) ? daterange : [null, null]

    return {
      ...filtersParams,
      startTime,
      endTime
    }
  }

  // const simulateNetworkRequest = (): Promise<void> => {
  //   return new Promise((resolve) => {
  //     setTimeout(() => {
  //       resolve()
  //     }, 500)
  //   })
  // }

  // onMounted(async () => {
  //   await simulateNetworkRequest()
  //   await fetchData({ name: 'ricky', phone: 19388828388 })
  // })

  const {
    data,
    loading,
    error,
    hasData,

    pagination,
    handleSizeChange,
    handleCurrentChange,

    searchParams,
    replaceSearchParams,
    resetSearchParams,

    getData,
    getDataDebounced,
    clearData,

    refreshData,
    refreshSoft,
    refreshCreate,
    refreshUpdate,
    refreshRemove,

    cacheInfo,
    clearCache,
    clearExpiredCache,

    cancelRequest,

    columns,
    columnChecks,
    addColumn,
    removeColumn,
    updateColumn,
    toggleColumn,
    resetColumns,
    reorderColumns,
    getColumnConfig,
    getAllColumns
  } = useTable({
    core: {
      apiFn: (params) => {
        const requestKey = JSON.stringify(params)
        console.log('🚀 API Request params:', params)
        addCacheLog(`🚀 API request: current=${params.current}, size=${params.size}`)
        addCacheLog(`🔑 Request key: ${requestKey.substring(0, 100)}...`)

        updateCacheKeys(requestKey)

        return fetchGetUserList(params)
      },
      apiParams: {
        current: 1,
        size: 20,
        ...searchFormState.value
      },
      excludeParams: ['daterange'],
      // paginationKey: {
      //   current: 'pageNum',
      //   size: 'pageSize'
      // },
      immediate: true,
      columnsFactory: () => [
        // {
        //   type: 'expand',
        //   width: 80,
        //   formatter: (row) =>
        //     h('div', { style: 'padding: 10px 30px' }, [
        //     ])
        // },
        { type: 'selection', width: 50 },
        { type: 'globalIndex', width: 60, label: 'No.' },
        {
          prop: 'avatar',
          label: 'User info',
          minWidth: 200,
          useSlot: true,
          useHeaderSlot: true,
          sortable: false
        },
        {
          prop: 'userGender',
          label: 'Gender',
          sortable: true,
          formatter: (row) => row.userGender || 'Unknown'
        },
        {
          prop: 'userPhone',
          label: 'Phone',
          useHeaderSlot: true,
          sortable: true
        },
        {
          prop: 'department',
          label: 'Department',
          sortable: true
        },
        {
          prop: 'score',
          label: 'Rating',
          useSlot: true,
          sortable: true
        },
        {
          prop: 'status',
          label: 'Status',
          useSlot: true,
          sortable: true
        },
        {
          prop: 'operation',
          label: 'Actions',
          width: 190,
          useSlot: true,
          fixed: 'right'
        }
      ]
    },

    transform: {
      dataTransformer: (records) => {
        if (!Array.isArray(records)) return []

        return records.map((item, index: number) => ({
          ...item,
          avatar: ACCOUNT_TABLE_DATA[index % ACCOUNT_TABLE_DATA.length].avatar,
          department: ['Engineering', 'Product', 'Operations', 'Marketing', 'Design'][
            Math.floor(Math.random() * 5)
          ],
          score: Math.floor(Math.random() * 5) + 1,
          status: ['1', '2', '3', '4'][Math.floor(Math.random() * 4)]
        }))
      }
      // responseAdapter: (data) => {
      //   const { list, total, pageNum, pageSize } = data
      //   return {
      //     records: list,
      //     total: total,
      //     current: pageNum,
      //     size: pageSize
      //   }
      // }
    },

    performance: {
      enableCache: true,
      cacheTime: 5 * 60 * 1000,
      debounceTime: 300,
      maxCacheSize: 100
    },

    hooks: {
      onSuccess: (data, response) => {
        console.log('📊 Response details:', response)
        addCacheLog(`✅ Network request succeeded: ${data.length} rows`)
        addCacheLog(
          `📝 Response info: total=${response.total}, current=${response.current}, size=${response.size}`
        )
      },
      onError: (error) => {
        console.error('❌ DataLoad failed:', error)
        addCacheLog(`❌ Request failed: ${error.message}`)
        ElMessage.error(error.message)
      },
      onCacheHit: (data, response) => {
        console.log('🎯 Cache hit:', data.length, 'items')
        console.log('🔑 Cache source:', response)
        addCacheLog(
          `🎯 Cache hit: ${data.length} rows (current=${response.current}, size=${response.size})`
        )
        ElMessage.info('Data loaded from cache')
      },
      resetFormCallback: () => {
        console.log('🔄 Form reset')
        addCacheLog('🔄 Form reset')
      }
    },

    debug: {
      enableLog: true,
      logLevel: 'info'
    }
  })

  const handleSelectionChange = (selection: UserListItem[]) => {
    selectedRows.value = selection
    console.log('Selection change:', selection)
  }

  const handleRowClick = (row: UserListItem) => {
    console.log('Row click:', row)
    logEvent('Row click', `Clicked user: ${row.userName}`)
  }

  const handleHeaderClick = (column: { label: string; property: string }) => {
    console.log('Header click:', column)
    logEvent('Header click', `Clicked the ${column.label} column header`)
  }

  interface SortInfo {
    prop: string
    order: 'ascending' | 'descending' | null
  }

  const handleSortChange = (sortInfo: SortInfo) => {
    console.log('Sort event:', sortInfo)
    console.log('Sort field:', sortInfo.prop)
    console.log('Sort direction:', sortInfo.order)
    logEvent('Sort change', `Field: ${sortInfo.prop}, direction: ${sortInfo.order}`)
  }

  const logEvent = (type: string, message: string) => {
    if (!eventDemoEnabled.value) return

    const time = new Date().toLocaleTimeString()
    eventLogs.value.unshift({ type, message, time })

    if (eventLogs.value.length > 20) {
      eventLogs.value = eventLogs.value.slice(0, 20)
    }
  }

  const getEventType = (type: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
    const typeMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
      'Row click': 'primary',
      'Row double click': 'success',
      'Row right click': 'warning',
      'Cell click': 'info',
      'Cell double click': 'success',
      'Header click': 'primary',
      'Selection change': 'warning',
      'Sort change': 'success'
    }
    return typeMap[type] || 'info'
  }

  const toggleEventDemo = () => {
    eventDemoEnabled.value = !eventDemoEnabled.value
    if (eventDemoEnabled.value) {
      ElMessage.success('Event listener enabled. Interact with the table to see the effect')
    } else {
      ElMessage.info('Event listener disabled')
    }
  }

  const clearEventLogs = () => {
    eventLogs.value = []
    ElMessage.info('Event log cleared')
  }

  const handleScrollToTop = () => {
    tableRef.value?.scrollToTop()
  }

  const handleScrollToPosition = () => {
    tableRef.value?.elTableRef.setScrollTop(200)
  }

  const handleToggleSelection = () => {
    if (selectedRows.value.length === 0) {
      tableRef.value?.elTableRef.toggleAllSelection()
      ElMessage.info('All selected')
    } else {
      tableRef.value?.elTableRef.clearSelection()
      ElMessage.info('Selection cleared')
    }
  }

  const handleGetTableInfo = () => {
    const info = {
      dataRows: data.value.length,
      selectedRowsCount: selectedRows.value.length,
      columnCount: columns?.value?.length ?? 0,
      currentPage: pagination.current,
      pageSize: pagination.size,
      totalRows: pagination.total
    }

    console.log('Table info:', info)
    ElMessage.info(`Table info has been logged to the console, currently ${info.dataRows} rows`)
  }

  const handleSearch = async () => {
    await searchBarRef.value.validate()

    console.log('Search params:', searchFormState.value)
    replaceSearchParams(buildSearchParams(searchFormState.value))
    getData()
  }

  const handleReset = () => {
    addCacheLog('🔄 Reset search')
    // searchFormState.value = { ...defaultFilter.value }
    resetSearchParams()
  }

  const handlePhoneSearch = (value: string) => {
    searchFormState.value.phone = value
    replaceSearchParams(buildSearchParams(searchFormState.value))
    requestParams.value = { ...searchParams }
    addCacheLog(`📱 Phone search: ${value}`)
    getDataDebounced()
  }

  const handleRefresh = () => {
    addCacheLog('🔄 Manual refresh')
    refreshData()
  }

  const handleAdd = () => {
    ElMessage.success('User added successfully')
    refreshCreate()
  }

  const handleEdit = (row: UserListItem) => {
    ElMessage.success(`Edit user ${row.userName} successfully`)
    setTimeout(() => {
      refreshUpdate()
    }, 1000)
  }

  const handleDelete = async (row: UserListItem) => {
    try {
      await ElMessageBox.confirm(`Are you sure you want to delete user ${row.userName}?`, 'Warning', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      })

      ElMessage.success('Deleted successfully')
      setTimeout(() => {
        refreshRemove()
      }, 1000)
    } catch {
      ElMessage.info('Delete canceled')
    }
  }

  const handleView = (row: UserListItem) => {
    ElMessage.info(`View user ${row.userName}`)
  }

  const handleBatchDelete = async () => {
    try {
      await ElMessageBox.confirm(
        `Are you sure you want to delete the selected ${selectedRows.value.length} users?`,
        'Warning',
        {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }
      )

      ElMessage.success(`Batch deleted ${selectedRows.value.length} users successfully`)
      selectedRows.value = []
      setTimeout(() => {
        refreshRemove()
      }, 1000)
    } catch {
      ElMessage.info('Delete canceled')
    }
  }

  const handleExportSuccess = (filename: string, count: number) => {
    ElMessage.success(`Exported ${count} rows successfully`)
  }

  const handleImportSuccess = (data: Record<string, any>[]) => {
    ElMessage.success(`Imported ${data.length} rows successfully`)
    refreshCreate()
  }

  const handleImportError = (error: Error) => {
    ElMessage.error(`Import failed: ${error.message}`)
  }

  const handleClearCache = () => {
    clearCache(CacheInvalidationStrategy.CLEAR_ALL, 'Manual clear')
    cacheKeys.value = []
    addCacheLog('🗑️ Manually cleared all cache')
    ElMessage.success('Cache cleared')
  }

  const handleCleanExpiredCache = () => {
    const count = clearExpiredCache()
    addCacheLog(`🧹 Cleaned ${count} expired cache entries`)
    ElMessage.info(`Cleaned ${count} expired cache entries`)
  }

  const handleCancelRequest = () => {
    cancelRequest()
    addCacheLog('❌ Canceled current request')
    ElMessage.info('Request canceled')
  }

  const handleClearData = () => {
    clearData()
    addCacheLog('🗑️ Clear all data')
    ElMessage.info('Data cleared')
  }

  const handleTestCache = () => {
    const testPages = [1, 2, 3, 2, 1]

    ElMessage.info('Starting cache test...')
    addCacheLog('🧪 Start cache test')

    let index = 0
    const testInterval = setInterval(() => {
      if (index >= testPages.length) {
        clearInterval(testInterval)
        addCacheLog('✅ Cache test completed')
        ElMessage.success('Cache test complete! Watch the cache stats change')
        return
      }

      const page = testPages[index]
      addCacheLog(`📄 Switched to page ${page} during test`)
      requestParams.value = { ...requestParams.value, current: page }

      handleCurrentChange(page)
      index++
    }, 1000)
  }

  const addCacheLog = (message: string): void => {
    const timestamp = new Date().toLocaleTimeString()
    cacheDebugLogs.value.unshift(`[${timestamp}] ${message}`)
    if (cacheDebugLogs.value.length > 20) {
      cacheDebugLogs.value = cacheDebugLogs.value.slice(0, 20)
    }
  }

  const updateCacheKeys = (key: string, operation: 'add' | 'remove' = 'add'): void => {
    if (operation === 'add' && !cacheKeys.value.includes(key)) {
      cacheKeys.value.push(key)
      addCacheLog(`Add cache key: ${getCacheKeySummary(key)}`)
    } else if (operation === 'remove') {
      const index = cacheKeys.value.indexOf(key)
      if (index > -1) {
        cacheKeys.value.splice(index, 1)
        addCacheLog(`Remove cache key: ${getCacheKeySummary(key)}`)
      }
    }
  }

  const getCacheKeySummary = (key: string): string => {
    try {
      const params = JSON.parse(key)
      return `Page: ${params.current || 1}, Size: ${params.size || 20}${params.name ? ', name: ' + params.name : ''}${params.status ? ', Status: ' + params.status : ''}`
    } catch {
      return 'Invalid cache key'
    }
  }

  const forceRefreshCacheInfo = (): void => {
    const currentStats = cacheInfo.value
    addCacheLog(`Cache info refreshed: ${currentStats.total} cached items`)

    if (currentStats.total === 0) {
      cacheKeys.value = []
    }

    nextTick(() => {
      console.log('Current cache stats:', cacheInfo.value)
    })
  }

  watch(
    () => [pagination.current, pagination.size, searchFormState.value],
    ([current, size, search]) => {
      requestParams.value = {
        ...(search as any),
        current,
        size
      }
    },
    { deep: true, immediate: true }
  )

  const handleColumnCommand = (command: string): void => {
    switch (command) {
      case 'addColumn': {
        addColumn?.({
          prop: 'remark',
          label: 'Notes',
          width: 150,
          formatter: () => h('span', { style: 'color: #999' }, 'No notes yet')
        })
        ElMessage.success('Added "Notes" column')
        break
      }

      case 'batchAddColumns': {
        addColumn?.(
          [
            {
              prop: 'remark',
              label: 'Notes',
              width: 150,
              formatter: () => h('span', { style: 'color: #999' }, 'No notes yet')
            },
            {
              prop: 'tags',
              label: 'Tag',
              width: 120,
              formatter: () => h('span', { style: 'color: #67c23a' }, 'New users')
            }
          ],
          5
        )
        ElMessage.success('Added "Notes" and "Tag" columns in batch')
        break
      }

      case 'toggleColumn': {
        if (getColumnConfig?.('userPhone')) {
          toggleColumn?.('userPhone')
          ElMessage.success('Toggled Phone column visibility')
        }
        break
      }

      case 'batchToggleColumns': {
        toggleColumn?.(['userGender', 'userPhone'])
        ElMessage.success('Toggled Gender and Phone column visibility in batch')
        break
      }

      case 'removeColumn': {
        removeColumn?.('status')
        ElMessage.success('Deleted Status column')
        break
      }

      case 'batchRemoveColumns': {
        removeColumn?.(['status', 'score'])
        ElMessage.success('Deleted Status and Rating columns in batch')
        break
      }

      case 'updateColumn': {
        updateColumn?.('userPhone', {
          label: 'Phone number',
          width: 140
        })
        ElMessage.success('Phone column updated to "Phone number"')
        break
      }

      case 'batchUpdateColumns': {
        updateColumn?.([
          { prop: 'userGender', updates: { width: 200, label: 'Gender - updated', sortable: false } },
          { prop: 'userPhone', updates: { width: 200, label: 'Phone - updated', sortable: false } }
        ])
        ElMessage.success('Updated Gender and Phone columns in batch')
        break
      }

      case 'reorderColumns': {
        const allCols = getAllColumns?.()
        if (allCols) {
          const genderIndex = allCols.findIndex((col) => getColumnKey(col) === 'userGender')
          const phoneIndex = allCols.findIndex((col) => getColumnKey(col) === 'userPhone')

          if (genderIndex !== -1 && phoneIndex !== -1) {
            reorderColumns?.(genderIndex, phoneIndex)
            ElMessage.success('Swapped Gender and Phone column positions')
          }
        }
        break
      }

      case 'resetColumns': {
        resetColumns?.()
        ElMessage.success('Reset all column settings')
        break
      }

      default:
        console.warn('Unknown column configuration command:', command)
    }
  }
</script>

<style scoped>
  .user-info .el-avatar {
    flex-shrink: 0;
    width: 40px !important;
    height: 40px !important;
  }

  .user-info .el-avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center;
  }

  .custom-header:hover {
    color: var(--el-color-primary-light-3);
  }

  .demo-group .config-toggles .el-switch {
    --el-switch-on-color: var(--el-color-primary);
  }

  .demo-group .performance-info .el-alert {
    --el-alert-padding: 12px;
  }
</style>
