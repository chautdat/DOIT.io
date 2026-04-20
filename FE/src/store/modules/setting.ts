/**
 * System Settings State Management Module
 *
 * Provides complete system settings state management
 *
 * ## Main Features
 *
 * - Menu layout configuration (left, top, mixed, dual-column)
 * - Theme management (light, dark, auto)
 * - Menu theme style configuration
 * - Interface display switches (breadcrumbs, tabs, language switch, etc.)
 * - Feature switches (accordion mode, color-weak mode, watermark, etc.)
 * - Style configuration (border, radius, container width, page transition)
 * - Festival feature configuration
 * - Element Plus theme color dynamic setting
 *
 * ## Use Cases
 *
 * - Settings panel configuration management
 * - Theme switching and style customization
 * - Interface feature toggle control
 * - User preference persistence
 *
 * ## Persistence
 *
 * - Uses localStorage for storage
 * - Storage key: sys-v{version}-setting
 * - Supports cross-version data migration
 *
 * @module store/modules/setting
 * @author Art Design Pro Team
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { MenuThemeType } from '@/types/store'
import AppConfig from '@/config'
import { SystemThemeEnum, MenuThemeEnum, MenuTypeEnum, ContainerWidthEnum } from '@/enums/appEnum'
import { setElementThemeColor } from '@/utils/ui'
import { useCeremony } from '@/hooks/core/useCeremony'
import { StorageConfig } from '@/utils'
import { SETTING_DEFAULT_CONFIG } from '@/config/setting'

/**
 * System Settings State Management
 * Manages app's menu, theme, interface display and other settings
 */
export const useSettingStore = defineStore(
  'settingStore',
  () => {
    // Menu related settings
    /** Menu type */
    const menuType = ref(SETTING_DEFAULT_CONFIG.menuType)
    /** Menu expand width */
    const menuOpenWidth = ref(SETTING_DEFAULT_CONFIG.menuOpenWidth)
    /** Whether menu is expanded */
    const menuOpen = ref(SETTING_DEFAULT_CONFIG.menuOpen)
    /** Whether dual menu shows text */
    const dualMenuShowText = ref(SETTING_DEFAULT_CONFIG.dualMenuShowText)

    // Theme related settings
    /** System theme type */
    const systemThemeType = ref(SETTING_DEFAULT_CONFIG.systemThemeType)
    /** System theme mode */
    const systemThemeMode = ref(SETTING_DEFAULT_CONFIG.systemThemeMode)
    /** Menu theme type */
    const menuThemeType = ref(SETTING_DEFAULT_CONFIG.menuThemeType)
    /** System theme color */
    const systemThemeColor = ref(SETTING_DEFAULT_CONFIG.systemThemeColor)

    // Interface display settings
    /** Whether to show menu button */
    const showMenuButton = ref(SETTING_DEFAULT_CONFIG.showMenuButton)
    /** Whether to show fast enter */
    const showFastEnter = ref(SETTING_DEFAULT_CONFIG.showFastEnter)
    /** Whether to show refresh button */
    const showRefreshButton = ref(SETTING_DEFAULT_CONFIG.showRefreshButton)
    /** Whether to show breadcrumbs */
    const showCrumbs = ref(SETTING_DEFAULT_CONFIG.showCrumbs)
    /** Whether to show work tab */
    const showWorkTab = ref(SETTING_DEFAULT_CONFIG.showWorkTab)
    /** Whether to show language switch */
    const showLanguage = ref(SETTING_DEFAULT_CONFIG.showLanguage)
    /** Whether to show progress bar */
    const showNprogress = ref(SETTING_DEFAULT_CONFIG.showNprogress)
    /** Whether to show setting guide */
    const showSettingGuide = ref(SETTING_DEFAULT_CONFIG.showSettingGuide)
    /** Whether to show festival text */
    const showFestivalText = ref(SETTING_DEFAULT_CONFIG.showFestivalText)
    /** Whether to show watermark */
    const watermarkVisible = ref(SETTING_DEFAULT_CONFIG.watermarkVisible)

    // Feature settings
    /** Whether to auto close */
    const autoClose = ref(SETTING_DEFAULT_CONFIG.autoClose)
    /** Whether unique opened */
    const uniqueOpened = ref(SETTING_DEFAULT_CONFIG.uniqueOpened)
    /** Whether color weak mode */
    const colorWeak = ref(SETTING_DEFAULT_CONFIG.colorWeak)
    /** Whether to refresh */
    const refresh = ref(SETTING_DEFAULT_CONFIG.refresh)
    /** Whether holiday fireworks loaded */
    const holidayFireworksLoaded = ref(SETTING_DEFAULT_CONFIG.holidayFireworksLoaded)

    // Style settings
    /** Box border mode */
    const boxBorderMode = ref(SETTING_DEFAULT_CONFIG.boxBorderMode)
    /** Page transition effect */
    const pageTransition = ref(SETTING_DEFAULT_CONFIG.pageTransition)
    /** Tab style */
    const tabStyle = ref(SETTING_DEFAULT_CONFIG.tabStyle)
    /** Custom radius */
    const customRadius = ref(SETTING_DEFAULT_CONFIG.customRadius)
    /** Container width */
    const containerWidth = ref(SETTING_DEFAULT_CONFIG.containerWidth)

    // Festival related
    /** Festival date */
    const festivalDate = ref('')

    /**
     * Get menu theme
     * Returns corresponding theme config based on current theme type and dark mode
     */
    const getMenuTheme = computed((): MenuThemeType => {
      const list = AppConfig.themeList.filter((item) => item.theme === menuThemeType.value)
      if (isDark.value) {
        return AppConfig.darkMenuStyles[0]
      } else {
        return list[0]
      }
    })

    /**
     * Check if dark mode
     */
    const isDark = computed((): boolean => {
      return systemThemeType.value === SystemThemeEnum.DARK
    })

    /**
     * Get menu expand width
     */
    const getMenuOpenWidth = computed((): string => {
      return menuOpenWidth.value + 'px' || SETTING_DEFAULT_CONFIG.menuOpenWidth + 'px'
    })

    /**
     * Get custom radius
     */
    const getCustomRadius = computed((): string => {
      return customRadius.value + 'rem' || SETTING_DEFAULT_CONFIG.customRadius + 'rem'
    })

    /**
     * Whether to show fireworks
     * Determines based on current date and festival date
     */
    const isShowFireworks = computed((): boolean => {
      return festivalDate.value === useCeremony().currentFestivalData.value?.date ? false : true
    })

    /**
     * Switch menu layout
     * @param type Menu type
     */
    const switchMenuLayouts = (type: MenuTypeEnum) => {
      menuType.value = type
    }

    /**
     * Set menu expand width
     * @param width Width value
     */
    const setMenuOpenWidth = (width: number) => {
      menuOpenWidth.value = width
    }

    /**
     * Set global theme
     * @param theme Theme type
     * @param themeMode Theme mode
     */
    const setGlopTheme = (theme: SystemThemeEnum, themeMode: SystemThemeEnum) => {
      systemThemeType.value = theme
      systemThemeMode.value = themeMode
      localStorage.setItem(StorageConfig.THEME_KEY, theme)
    }

    /**
     * Switch menu styles
     * @param theme Menu theme
     */
    const switchMenuStyles = (theme: MenuThemeEnum) => {
      menuThemeType.value = theme
    }

    /**
     * Set Element Plus theme color
     * @param theme Theme color
     */
    const setElementTheme = (theme: string) => {
      systemThemeColor.value = theme
      setElementThemeColor(theme)
    }

    /**
     * Toggle border mode
     */
    const setBorderMode = () => {
      boxBorderMode.value = !boxBorderMode.value
    }

    /**
     * Set container width
     * @param width Container width enum value
     */
    const setContainerWidth = (width: ContainerWidthEnum) => {
      containerWidth.value = width
    }

    /**
     * Toggle unique opened mode
     */
    const setUniqueOpened = () => {
      uniqueOpened.value = !uniqueOpened.value
    }

    /**
     * Toggle menu button display
     */
    const setButton = () => {
      showMenuButton.value = !showMenuButton.value
    }

    /**
     * Toggle fast enter display
     */
    const setFastEnter = () => {
      showFastEnter.value = !showFastEnter.value
    }

    /**
     * Toggle auto close
     */
    const setAutoClose = () => {
      autoClose.value = !autoClose.value
    }

    /**
     * Toggle refresh button display
     */
    const setShowRefreshButton = () => {
      showRefreshButton.value = !showRefreshButton.value
    }

    /**
     * Toggle breadcrumbs display
     */
    const setCrumbs = () => {
      showCrumbs.value = !showCrumbs.value
    }

    /**
     * Set work tab display
     * @param show Whether to show
     */
    const setWorkTab = (show: boolean) => {
      showWorkTab.value = show
    }

    /**
     * Toggle language switch display
     */
    const setLanguage = () => {
      showLanguage.value = !showLanguage.value
    }

    /**
     * Toggle progress bar display
     */
    const setNprogress = () => {
      showNprogress.value = !showNprogress.value
    }

    /**
     * Toggle color weak mode
     */
    const setColorWeak = () => {
      colorWeak.value = !colorWeak.value
    }

    /**
     * Hide setting guide
     */
    const hideSettingGuide = () => {
      showSettingGuide.value = false
    }

    /**
     * Show setting guide
     */
    const openSettingGuide = () => {
      showSettingGuide.value = true
    }

    /**
     * Set page transition effect
     * @param transition Transition effect name
     */
    const setPageTransition = (transition: string) => {
      pageTransition.value = transition
    }

    /**
     * Set tab style
     * @param style Style name
     */
    const setTabStyle = (style: string) => {
      tabStyle.value = style
    }

    /**
     * Set menu open state
     * @param open Whether to open
     */
    const setMenuOpen = (open: boolean) => {
      menuOpen.value = open
    }

    /**
     * Reload page
     */
    const reload = () => {
      refresh.value = !refresh.value
    }

    /**
     * Set watermark visibility
     * @param visible Whether visible
     */
    const setWatermarkVisible = (visible: boolean) => {
      watermarkVisible.value = visible
    }

    /**
     * Set custom radius
     * @param radius Radius value
     */
    const setCustomRadius = (radius: string) => {
      customRadius.value = radius
      document.documentElement.style.setProperty('--custom-radius', `${radius}rem`)
    }

    /**
     * Set holiday fireworks loaded state
     * @param isLoad Whether loaded
     */
    const setholidayFireworksLoaded = (isLoad: boolean) => {
      holidayFireworksLoaded.value = isLoad
    }

    /**
     * Set festival text visibility
     * @param show Whether to show
     */
    const setShowFestivalText = (show: boolean) => {
      showFestivalText.value = show
    }

    const setFestivalDate = (date: string) => {
      festivalDate.value = date
    }

    const setDualMenuShowText = (show: boolean) => {
      dualMenuShowText.value = show
    }

    return {
      menuType,
      menuOpenWidth,
      systemThemeType,
      systemThemeMode,
      menuThemeType,
      systemThemeColor,
      boxBorderMode,
      uniqueOpened,
      showMenuButton,
      showFastEnter,
      showRefreshButton,
      showCrumbs,
      autoClose,
      showWorkTab,
      showLanguage,
      showNprogress,
      colorWeak,
      showSettingGuide,
      pageTransition,
      tabStyle,
      menuOpen,
      refresh,
      watermarkVisible,
      customRadius,
      holidayFireworksLoaded,
      showFestivalText,
      festivalDate,
      dualMenuShowText,
      containerWidth,
      getMenuTheme,
      isDark,
      getMenuOpenWidth,
      getCustomRadius,
      isShowFireworks,
      switchMenuLayouts,
      setMenuOpenWidth,
      setGlopTheme,
      switchMenuStyles,
      setElementTheme,
      setBorderMode,
      setContainerWidth,
      setUniqueOpened,
      setButton,
      setFastEnter,
      setAutoClose,
      setShowRefreshButton,
      setCrumbs,
      setWorkTab,
      setLanguage,
      setNprogress,
      setColorWeak,
      hideSettingGuide,
      openSettingGuide,
      setPageTransition,
      setTabStyle,
      setMenuOpen,
      reload,
      setWatermarkVisible,
      setCustomRadius,
      setholidayFireworksLoaded,
      setShowFestivalText,
      setFestivalDate,
      setDualMenuShowText
    }
  },
  {
    persist: {
      key: 'setting',
      storage: localStorage
    }
  }
)
