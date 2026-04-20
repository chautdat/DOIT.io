<template>
  <div class="art-full-height">
    <div class="box-border flex gap-4 h-full max-md:block max-md:gap-0 max-md:h-auto">
      <div class="flex-shrink-0 w-58 h-full max-md:w-full max-md:h-auto max-md:mb-5">
        <ElCard class="tree-card art-card-xs flex flex-col h-full mt-0">
          <template #header>
            <b>Category tree</b>
          </template>
          <ElScrollbar>
            <ElTree
              :data="treeData"
              :props="treeProps"
              node-key="id"
              default-expand-all
              highlight-current
              @node-click="handleNodeClick"
            />
          </ElScrollbar>
        </ElCard>
      </div>

      <div class="flex flex-col flex-grow min-w-0">
        <UserSearch v-model="defaultFilter" />

        <ElCard class="flex flex-col flex-1 min-h-0 art-table-card">
          <ArtTableHeader v-model:columns="columnChecks" :loading="loading" @refresh="refreshData">
            <template #left>
              <ElSpace wrap>
                <ElButton @click="showButtons = !showButtons" v-ripple type="primary" plain
                  >{{ showButtons ? 'Collapse' : 'Expand' }} button group</ElButton
                >
                <ElButton v-show="showButtons" v-ripple v-for="value in 12" :key="value"
                  >Responsive table</ElButton
                >
              </ElSpace>
            </template>
          </ArtTableHeader>

          <ArtTable
            rowKey="id"
            :loading="loading"
            :data="data"
            :columns="columns"
            :pagination="pagination"
            @pagination:size-change="handleSizeChange"
            @pagination:current-change="handleCurrentChange"
          >
          </ArtTable>
        </ElCard>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useTable } from '@/hooks/core/useTable'
  import { fetchGetUserList } from '@/api/system-manage'
  import UserSearch from '@/views/system/user/modules/user-search.vue'

  defineOptions({ name: 'TreeTable' })

  const showButtons = ref(false)

  const treeData = ref([
    {
      id: 1,
      label: 'Engineering',
      children: [
        {
          id: 11,
          label: 'Frontend team',
          children: [
            { id: 111, label: 'React team' },
            { id: 112, label: 'Vue team' },
            { id: 113, label: 'Mobile team' }
          ]
        },
        {
          id: 12,
          label: 'Backend team',
          children: [
            { id: 121, label: 'Java team' },
            { id: 122, label: 'Node.js team' },
            { id: 123, label: 'Python team' }
          ]
        },
        {
          id: 13,
          label: 'QA team',
          children: [
            { id: 131, label: 'Functional testing' },
            { id: 132, label: 'Automation testing' },
            { id: 133, label: 'Performance testing' }
          ]
        },
        {
          id: 14,
          label: 'Operations team',
          children: [
            { id: 141, label: 'System operations' },
            { id: 142, label: 'DevOps' }
          ]
        }
      ]
    },
    {
      id: 2,
      label: 'Product',
      children: [
        {
          id: 21,
          label: 'Product design team',
          children: [
            { id: 211, label: 'UI design' },
            { id: 212, label: 'UX design' },
            { id: 213, label: 'Interaction design' }
          ]
        },
        {
          id: 22,
          label: 'Product ops team',
          children: [
            { id: 221, label: 'User operations' },
            { id: 222, label: 'Content operations' },
            { id: 223, label: 'Campaign operations' }
          ]
        },
        { id: 23, label: 'Data analysis team' }
      ]
    },
    {
      id: 3,
      label: 'Marketing',
      children: [
        { id: 31, label: 'Brand promotion team' },
        { id: 32, label: 'Channel growth team' },
        {
          id: 33,
          label: 'Sales team',
          children: [
            { id: 331, label: 'Enterprise customers' },
            { id: 332, label: 'Individual customers' }
          ]
        }
      ]
    },
    {
      id: 4,
      label: 'Administration',
      children: [
        { id: 41, label: 'HR team' },
        { id: 42, label: 'Finance team' },
        { id: 43, label: 'Admin support team' }
      ]
    },
    {
      id: 5,
      label: 'Support',
      children: [
        { id: 51, label: 'Pre-sales consulting' },
        { id: 52, label: 'After-sales support' },
        { id: 53, label: 'Customer success' }
      ]
    }
  ])

  const treeProps = {
    children: 'children',
    label: 'label'
  }

  const handleNodeClick = (data: any) => {
    console.log('Selected node:', data)
  }

  const defaultFilter = ref<Api.SystemManage.UserSearchParams>({
    userName: undefined,
    userGender: undefined,
    userPhone: undefined,
    userEmail: undefined,
    status: undefined
  })

  const {
    data,
    columns,
    columnChecks,
    loading,
    pagination,
    refreshData,
    handleSizeChange,
    handleCurrentChange
  } = useTable({
    core: {
      apiFn: fetchGetUserList,
      apiParams: {
        current: 1,
        size: 20,
        userName: '',
        userPhone: '',
        userEmail: ''
      },
      columnsFactory: () => [
        {
          prop: 'id',
          label: 'ID'
        },
        {
          prop: 'nickName',
          label: 'Nickname'
        },
        {
          prop: 'userGender',
          label: 'Gender',
          sortable: true,
          formatter: (row) => row.userGender || 'Unknown'
        },
        {
          prop: 'userPhone',
          label: 'Phone'
        },
        {
          prop: 'userEmail',
          label: 'Email'
        }
      ]
    }
  })
</script>

<style scoped>
  .tree-card :deep(.el-card__body) {
    flex: 1;
    min-height: 0;
    padding: 10px 2px 10px 10px;
  }
</style>
