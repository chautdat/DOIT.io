/**
 * System Global Configuration
 *
 * This is the core configuration file of the system, centrally managing all global configuration items.
 * Contains all configurable items including system information, theme styles, menu layouts, color schemes, etc.
 *
 * ## Main Features
 *
 * - System Information - Basic information like system name
 * - Theme Configuration - Style configuration for light/dark/auto themes
 * - Menu Configuration - Menu layout, theme, width configuration
 * - Color Schemes - System main colors and preset color lists
 * - Quick Entry - Quick entry apps and link configuration
 * - Header Bar Configuration - Header bar function module configuration
 *
 * ## Configuration Items Description
 *
 * - systemInfo: Basic system information (name, etc.)
 * - systemThemeStyles: System theme style mapping
 * - settingThemeList: Available system theme list
 * - menuLayoutList: Available menu layout list
 * - themeList: Menu theme style list
 * - darkMenuStyles: Menu styles in dark mode
 * - systemMainColor: Preset system main color list
 * - fastEnter: Quick entry configuration
 * - headerBar: Header bar function configuration
 *
 * @module config
 * @author Art Design Pro Team
 */

import { MenuThemeEnum, MenuTypeEnum, SystemThemeEnum } from '@/enums/appEnum'
import { SystemConfig } from '@/types/config'
import { configImages } from './assets/images'
import fastEnterConfig from './modules/fastEnter'
import { headerBarConfig } from './modules/headerBar'

const appConfig: SystemConfig = {
  // System information
  systemInfo: {
    name: 'DOIT IELTS' // System name
  },
  // System theme
  systemThemeStyles: {
    [SystemThemeEnum.LIGHT]: { className: '' },
    [SystemThemeEnum.DARK]: { className: SystemThemeEnum.DARK }
  },
  // System theme list
  settingThemeList: [
    {
      name: 'Light',
      theme: SystemThemeEnum.LIGHT,
      color: ['#fff', '#fff'],
      leftLineColor: '#EDEEF0',
      rightLineColor: '#EDEEF0',
      img: configImages.themeStyles.light
    },
    {
      name: 'Dark',
      theme: SystemThemeEnum.DARK,
      color: ['#22252A'],
      leftLineColor: '#3F4257',
      rightLineColor: '#3F4257',
      img: configImages.themeStyles.dark
    },
    {
      name: 'System',
      theme: SystemThemeEnum.AUTO,
      color: ['#fff', '#22252A'],
      leftLineColor: '#EDEEF0',
      rightLineColor: '#3F4257',
      img: configImages.themeStyles.system
    }
  ],
  // Menu layout list
  menuLayoutList: [
    { name: 'Left', value: MenuTypeEnum.LEFT, img: configImages.menuLayouts.vertical },
    { name: 'Top', value: MenuTypeEnum.TOP, img: configImages.menuLayouts.horizontal },
    { name: 'Mixed', value: MenuTypeEnum.TOP_LEFT, img: configImages.menuLayouts.mixed },
    { name: 'Dual Column', value: MenuTypeEnum.DUAL_MENU, img: configImages.menuLayouts.dualColumn }
  ],
  // Menu theme list
  themeList: [
    {
      theme: MenuThemeEnum.DESIGN,
      background: '#FFFFFF',
      systemNameColor: 'var(--art-gray-800)',
      iconColor: '#6B6B6B',
      textColor: '#29343D',
      img: configImages.menuStyles.design
    },
    {
      theme: MenuThemeEnum.DARK,
      background: '#191A23',
      systemNameColor: '#D9DADB',
      iconColor: '#BABBBD',
      textColor: '#BABBBD',
      img: configImages.menuStyles.dark
    },
    {
      theme: MenuThemeEnum.LIGHT,
      background: '#ffffff',
      systemNameColor: 'var(--art-gray-800)',
      iconColor: '#6B6B6B',
      textColor: '#29343D',
      img: configImages.menuStyles.light
    }
  ],
  // Dark mode menu styles
  darkMenuStyles: [
    {
      theme: MenuThemeEnum.DARK,
      background: 'var(--default-box-color)',
      systemNameColor: '#DDDDDD',
      iconColor: '#BABBBD',
      textColor: 'rgba(#FFFFFF, 0.7)'
    }
  ],
  // System main colors
  systemMainColor: [
    '#5D87FF',
    '#B48DF3',
    '#1D84FF',
    '#60C041',
    '#38C0FC',
    '#F9901F',
    '#FF80C8'
  ] as const,
  // Quick entry configuration
  fastEnter: fastEnterConfig,
  // Header bar function configuration
  headerBar: headerBarConfig
}

export default Object.freeze(appConfig)
