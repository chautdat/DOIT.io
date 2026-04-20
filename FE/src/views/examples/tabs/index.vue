<!-- Tabs Example Page -->
<template>
  <div class="page-content">
    <h3 class="mb-5 text-xl font-normal">Tab Operations</h3>

    <!-- Modify Tab Title Module -->
    <ElCard class="mb-7.5" header="Modify Tab Title">
      <div class="flex gap-2">
        <ElInput
          v-model="newTabTitle"
          placeholder="Please enter new tab title"
          clearable
          class="!max-w-75"
        />

        <ElButton type="primary" @click="handleUpdateTabTitle" :disabled="!newTabTitle.trim()">
          Modify
        </ElButton>
        <ElButton @click="handleResetTabTitle"> Reset </ElButton>
      </div>
    </ElCard>

    <!-- Get Tab Info Module -->
    <ElCard class="mb-7.5" header="Get Tab Info">
      <div class="mb-4">
        <p class="m-0 mb-2 text-sm text-g-600"> Current tab info: {{ currentTab }} </p>
      </div>
      <ElRow :gutter="20">
        <ElCol :span="24">
          <ElButton type="success" plain @click="handleGetCurrentTabTitle(routePath)">
            Get Current Tab Info
          </ElButton>
        </ElCol>
      </ElRow>
    </ElCard>

    <!-- Close Tab Module -->
    <ElCard class="mb-7.5" header="Close Tab">
      <ElRow :gutter="20">
        <ElCol :span="24">
          <ElButton type="danger" plain @click="handleCloseTab(routePath)">
            Close Current Tab
          </ElButton>
          <ElButton type="warning" plain @click="handleCloseOthersTab(routePath)">
            Close Other Tabs
          </ElButton>
          <ElButton type="danger" plain @click="handleCloseAllTab"> Close All Tabs </ElButton>
        </ElCol>
      </ElRow>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
  import { useWorktabStore } from '@/store/modules/worktab'
  import { WorkTab } from '@/types'

  defineOptions({ name: 'TabsExample' })

  const worktabStore = useWorktabStore()
  const currentTab = ref<WorkTab | null>(null)
  const newTabTitle = ref('')
  const routePath = '/examples/tabs'

  /**
   * Update current tab title
   * Validate input content then call store method to update title
   */
  const handleUpdateTabTitle = (): void => {
    const trimmedTitle = newTabTitle.value.trim()
    if (trimmedTitle) {
      worktabStore.updateTabTitle(routePath, trimmedTitle)
      ElMessage.success('Tab title updated')
    }
  }

  /**
   * Reset tab title
   * Reset title to default value and clear input
   */
  const handleResetTabTitle = (): void => {
    worktabStore.resetTabTitle(routePath)
    newTabTitle.value = ''
    ElMessage.success('Tab title reset')
  }

  /**
   * Get tab info by path
   * @param path Tab path
   */
  const handleGetCurrentTabTitle = (path: string): void => {
    const tab = worktabStore.getTabTitle(path)
    if (tab) {
      currentTab.value = tab
      ElMessage.success('Tab info retrieved')
    } else {
      ElMessage.warning('Tab info not found')
    }
  }

  /**
   * Close tab by path
   * @param path Tab path to close
   */
  const handleCloseTab = (path: string): void => {
    worktabStore.removeTab(path)
  }

  /**
   * Close all other tabs except the specified path
   * @param path Tab path to keep
   */
  const handleCloseOthersTab = (path: string): void => {
    worktabStore.removeOthers(path)
  }

  /**
   * Close all tabs
   */
  const handleCloseAllTab = (): void => {
    worktabStore.removeAll()
  }
</script>
