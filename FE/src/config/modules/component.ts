
import { defineAsyncComponent } from 'vue'

export const globalComponentsConfig: GlobalComponentConfig[] = [
  {
    name: 'Settings Panel',
    key: 'settings-panel',
    component: defineAsyncComponent(
      () => import('@/components/core/layouts/ArtSettingsPanel.vue')
    ),
    enabled: true
  },
  {
    name: 'Global Search',
    key: 'global-search',
    component: defineAsyncComponent(
      () => import('@/components/core/layouts/ArtGlobalSearch.vue')
    ),
    enabled: true
  },
  {
    name: 'Screen Lock',
    key: 'screen-lock',
    component: defineAsyncComponent(
      () => import('@/components/core/layouts/ArtScreenLock.vue')
    ),
    enabled: true
  },
  {
    name: 'Chat Window',
    key: 'chat-window',
    component: defineAsyncComponent(
      () => import('@/components/core/layouts/ArtChatWindow.vue')
    ),
    enabled: true
  },
  {
    name: 'Fireworks Effect',
    key: 'fireworks-effect',
    component: defineAsyncComponent(
      () => import('@/components/core/layouts/ArtFireworksEffect.vue')
    ),
    enabled: true
  },
  {
    name: 'Watermark Effect',
    key: 'watermark',
    component: defineAsyncComponent(
      () => import('@/components/core/others/art-watermark/ArtWatermark.vue')
    ),
    enabled: true
  }
]

export interface GlobalComponentConfig {
  name: string
  key: string
  component: any
  enabled?: boolean
  description?: string
}

export const getEnabledGlobalComponents = () => {
  return globalComponentsConfig.filter((config) => config.enabled !== false)
}

export const getGlobalComponentByKey = (key: string) => {
  return globalComponentsConfig.find((config) => config.key === key)
}
