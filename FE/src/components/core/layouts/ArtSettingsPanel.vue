<template>
  <div class="layout-settings">
    <SettingDrawer v-model="showDrawer" @open="handleOpen" @close="handleClose">
      <SettingHeader @close="closeDrawer" />
      <ThemeSettings />
      <MenuLayoutSettings />
      <MenuStyleSettings />
      <ColorSettings />
      <BoxStyleSettings />
      <ContainerSettings />
      <BasicSettings />
      <SettingActions />
    </SettingDrawer>
  </div>
</template>

<script setup lang="ts">
  import { useSettingsPanel } from './art-settings-panel/composables/useSettingsPanel'

  import SettingDrawer from './art-settings-panel/widget/SettingDrawer.vue'
  import SettingHeader from './art-settings-panel/widget/SettingHeader.vue'
  import ThemeSettings from './art-settings-panel/widget/ThemeSettings.vue'
  import MenuLayoutSettings from './art-settings-panel/widget/MenuLayoutSettings.vue'
  import MenuStyleSettings from './art-settings-panel/widget/MenuStyleSettings.vue'
  import ColorSettings from './art-settings-panel/widget/ColorSettings.vue'
  import BoxStyleSettings from './art-settings-panel/widget/BoxStyleSettings.vue'
  import ContainerSettings from './art-settings-panel/widget/ContainerSettings.vue'
  import BasicSettings from './art-settings-panel/widget/BasicSettings.vue'
  import SettingActions from './art-settings-panel/widget/SettingActions.vue'

  defineOptions({ name: 'ArtSettingsPanel' })

  interface Props {
    open?: boolean
  }

  const props = defineProps<Props>()

  const settingsPanel = useSettingsPanel()
  const { showDrawer } = settingsPanel

  const { handleOpen, handleClose, closeDrawer } = settingsPanel.useDrawerControl()
  const { initializeSettings, cleanupSettings } = settingsPanel.useSettingsInitializer()

  settingsPanel.usePropsWatcher(props)

  onMounted(() => {
    initializeSettings()
  })

  onUnmounted(() => {
    cleanupSettings()
  })
</script>

<style lang="scss">
  @use './art-settings-panel/style';
</style>
