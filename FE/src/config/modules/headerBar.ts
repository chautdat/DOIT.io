
import { HeaderBarFeatureConfig } from '@/types'

export const headerBarConfig: HeaderBarFeatureConfig = {
  menuButton: {
    enabled: true,
    description: 'Toggle the left menu open and closed'
  },
  refreshButton: {
    enabled: true,
    description: 'Refresh the page'
  },
  fastEnter: {
    enabled: true,
    description: 'Quick access to common apps and links'
  },
  breadcrumb: {
    enabled: true,
    description: 'Breadcrumb navigation showing the current page path'
  },
  globalSearch: {
    enabled: true,
    description: 'Global search with Ctrl+K or Cmd+K'
  },
  fullscreen: {
    enabled: true,
    description: 'Toggle fullscreen mode'
  },
  notification: {
    enabled: true,
    description: 'Notification center for system alerts and messages'
  },
  chat: {
    enabled: true,
    description: 'Chat for real-time communication'
  },
  language: {
    enabled: true,
    description: 'Language switcher'
  },
  settings: {
    enabled: true,
    description: 'System settings panel'
  },
  themeToggle: {
    enabled: true,
    description: 'Theme toggle (light and dark)'
  }
}

export default headerBarConfig
