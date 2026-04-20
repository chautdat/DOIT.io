<template>
  <div class="page-content">
    <ElButton @contextmenu.prevent="showMenu"> Right-click to trigger menu </ElButton>

    <!-- Context menu component -->
    <ArtMenuRight
      ref="menuRef"
      :menu-items="menuItems"
      :menu-width="180"
      :submenu-width="140"
      :border-radius="10"
      @select="handleSelect"
      @show="onMenuShow"
      @hide="onMenuHide"
    />
  </div>
</template>

<script setup lang="ts">
  import { computed, nextTick } from 'vue'
  import ArtMenuRight from '@/components/core/others/art-menu-right/ArtMenuRight.vue'
  import type { MenuItemType } from '@/components/core/others/art-menu-right/ArtMenuRight.vue'

  defineOptions({ name: 'TemplateContextMenu' })

  const menuRef = ref<InstanceType<typeof ArtMenuRight>>()
  const lastAction = ref('')

  /**
   * Context menu options configuration
   */
  const menuItems = computed((): MenuItemType[] => [
    {
      key: 'copy',
      label: 'Copy',
      icon: 'ri:file-copy-line'
    },
    {
      key: 'paste',
      label: 'Paste',
      icon: 'ri:capsule-line'
    },
    {
      key: 'cut',
      label: 'Cut',
      icon: 'ri:clipboard-line',
      showLine: true
    },
    {
      key: 'export',
      label: 'Export Options',
      icon: 'ri:export-line',
      children: [
        {
          key: 'exportExcel',
          label: 'Export Excel',
          icon: 'ri:file-excel-2-line'
        },
        {
          key: 'exportPdf',
          label: 'Export PDF',
          icon: 'ri:file-pdf-2-line'
        }
      ]
    },
    {
      key: 'edit',
      label: 'Edit Options',
      icon: 'ri:edit-2-line',
      children: [
        {
          key: 'rename',
          label: 'Rename'
        },
        {
          key: 'duplicate',
          label: 'Duplicate'
        }
      ]
    },
    {
      key: 'share',
      label: 'Share',
      icon: 'ri:share-forward-line',
      showLine: true
    },
    {
      key: 'delete',
      label: 'Delete',
      icon: 'ri:delete-bin-line'
    },
    {
      key: 'disabled',
      label: 'Disabled Option',
      icon: 'ri:close-circle-line',
      disabled: true
    }
  ])

  /**
   * Handle menu item selection
   * @param item Selected menu item
   */
  const handleSelect = (item: MenuItemType) => {
    lastAction.value = `${item.label} (${item.key})`
    ElMessage.success(`Action: ${item.label}`)
    console.log('Selected menu item:', item)
  }

  /**
   * Show context menu
   * @param e Mouse event
   */
  const showMenu = (e: MouseEvent) => {
    e.preventDefault()
    e.stopPropagation()

    nextTick(() => {
      menuRef.value?.show(e)
    })
  }

  /**
   * Menu show callback
   */
  const onMenuShow = () => {
    console.log('Menu shown')
  }

  /**
   * Menu hide callback
   */
  const onMenuHide = () => {
    console.log('Menu hidden')
  }
</script>
