/**
 * System Settings Default Value Configuration
 *
 * Unified management of all default values for system settings
 *
 * ## Main Features
 *
 * - Menu related default configuration
 * - Theme related default configuration
 * - Interface display default configuration
 * - Feature switch default configuration
 * - Style related default configuration
 *
 * ## Notes
 *
 * 1. When modifying the configuration items in this file, you need to synchronously update the following files:
 *    - src/components/core/layouts/art-settings-panel/widget/SettingActions.vue (copy config and reset config logic)
 *    - src/store/modules/setting.ts (Store state definition)
 * 2. You can quickly generate configuration code through the "Copy Config" button in the settings panel
 * 3. Enum type values need to be consistent with the definitions in src/enums/appEnum.ts
 */

import AppConfig from '@/config'
import { SystemThemeEnum, MenuThemeEnum, MenuTypeEnum, ContainerWidthEnum } from '@/enums/appEnum'

/**
 * System settings default value configuration
 */
export const SETTING_DEFAULT_CONFIG = {
  /** Menu type */
  menuType: MenuTypeEnum.LEFT,
  /** Menu expand width */
  menuOpenWidth: 230,
  /** Menu is expanded */
  menuOpen: true,
  /** Dual menu show text */
  dualMenuShowText: false,
  /** System theme type */
  systemThemeType: SystemThemeEnum.AUTO,
  /** System theme mode */
  systemThemeMode: SystemThemeEnum.AUTO,
  /** Menu style */
  menuThemeType: MenuThemeEnum.DESIGN,
  /** System theme color */
  systemThemeColor: AppConfig.systemMainColor[0],
  /** Show menu button */
  showMenuButton: true,
  /** Show fast enter */
  showFastEnter: true,
  /** Show refresh button */
  showRefreshButton: true,
  /** Show breadcrumbs */
  showCrumbs: true,
  /** Show work tab */
  showWorkTab: true,
  /** Show language switch */
  showLanguage: true,
  /** Show progress bar */
  showNprogress: false,
  /** Show setting guide */
  showSettingGuide: true,
  /** Show festival text */
  showFestivalText: false,
  /** Show watermark */
  watermarkVisible: false,
  /** Auto close */
  autoClose: false,
  /** Unique opened */
  uniqueOpened: true,
  /** Color weak mode */
  colorWeak: false,
  /** Refresh */
  refresh: false,
  /** Holiday fireworks loaded */
  holidayFireworksLoaded: false,
  /** Box border mode */
  boxBorderMode: true,
  /** Page transition effect */
  pageTransition: 'slide-left',
  /** Tab style */
  tabStyle: 'tab-default',
  /** Custom radius */
  customRadius: '0.75',
  /** Container width */
  containerWidth: ContainerWidthEnum.FULL,
  /** Festival date */
  festivalDate: ''
}

/**
 * Get setting defaults
 * @returns Setting defaults object
 */
export function getSettingDefaults() {
  return { ...SETTING_DEFAULT_CONFIG }
}

/**
 * Reset to default settings
 * @param currentSettings Current settings object
 */
export function resetToDefaults(currentSettings: Record<string, any>) {
  const defaults = getSettingDefaults()
  Object.keys(defaults).forEach((key) => {
    if (key in currentSettings) {
      currentSettings[key] = defaults[key as keyof typeof defaults]
    }
  })
}
