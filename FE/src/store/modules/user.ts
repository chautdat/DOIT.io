/**
 * User State Management Module
 *
 * Provides user-related state management
 *
 * ## Main Features
 *
 * - User login status management
 * - User information storage
 * - Access token and refresh token management
 * - Language settings
 * - Search history records
 * - Lock screen status and password management
 * - Logout cleanup logic
 *
 * ## Use Cases
 *
 * - User login and authentication
 * - Permission verification
 * - Personal information display
 * - Language switching
 * - Lock screen functionality
 * - Search history management
 *
 * ## Persistence
 *
 * - Uses localStorage for storage
 * - Storage key: sys-v{version}-user
 * - Auto cleanup on logout
 *
 * @module store/modules/user
 * @author Art Design Pro Team
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { LanguageEnum } from '@/enums/appEnum'
import { router } from '@/router'
import { useSettingStore } from './setting'
import { useWorktabStore } from './worktab'
import { AppRouteRecord } from '@/types/router'
import { setPageTitle } from '@/utils/router'
import { resetRouterState } from '@/router/guards/beforeEach'
import { useMenuStore } from './menu'
import { StorageConfig } from '@/utils/storage/storage-config'

/**
 * User State Management
 * Manages user login status, personal info, language settings, search history, lock screen status, etc.
 */
export const useUserStore = defineStore(
  'userStore',
  () => {
    // Language setting
    const language = ref(LanguageEnum.EN)
    // Login status
    const isLogin = ref(false)
    // Lock screen status
    const isLock = ref(false)
    // Lock screen password
    const lockPassword = ref('')
    // User information
    const info = ref<Partial<Api.Auth.UserInfo>>({})
    // Search history records
    const searchHistory = ref<AppRouteRecord[]>([])
    // Access token
    const accessToken = ref('')
    // Refresh token
    const refreshToken = ref('')

    // Computed property: Get user info
    const getUserInfo = computed(() => info.value)
    // Computed property: Get setting state
    const getSettingState = computed(() => useSettingStore().$state)
    // Computed property: Get worktab state
    const getWorktabState = computed(() => useWorktabStore().$state)

    /**
     * Set user information
     * @param newInfo New user information
     */
    const setUserInfo = (newInfo: Api.Auth.UserInfo) => {
      info.value = newInfo
    }

    /**
     * Set login status
     * @param status Login status
     */
    const setLoginStatus = (status: boolean) => {
      isLogin.value = status
    }

    /**
     * Set language
     * @param lang Language enum value
     */
    const setLanguage = (lang: LanguageEnum) => {
      setPageTitle(router.currentRoute.value)
      language.value = lang
    }

    /**
     * Set search history
     * @param list Search history list
     */
    const setSearchHistory = (list: AppRouteRecord[]) => {
      searchHistory.value = list
    }

    /**
     * Set lock screen status
     * @param status Lock screen status
     */
    const setLockStatus = (status: boolean) => {
      isLock.value = status
    }

    /**
     * Set lock screen password
     * @param password Lock screen password
     */
    const setLockPassword = (password: string) => {
      lockPassword.value = password
    }

    /**
     * Set tokens
     * @param newAccessToken Access token
     * @param newRefreshToken Refresh token (optional)
     */
    const setToken = (newAccessToken: string, newRefreshToken?: string) => {
      accessToken.value = newAccessToken
      if (newRefreshToken) {
        refreshToken.value = newRefreshToken
      }
    }

    /**
     * Log out
     * Clear all user-related state and redirect to login page
     * If the same account logs in again, preserve worktab tabs
     */
    const logOut = () => {
      // Save current user ID for checking if same user logs in next time
      const currentUserId = info.value.userId
      if (currentUserId) {
        localStorage.setItem(StorageConfig.LAST_USER_ID_KEY, String(currentUserId))
      }

      // Clear user info
      info.value = {}
      // Reset login status
      isLogin.value = false
      // Reset lock screen status
      isLock.value = false
      // Clear lock screen password
      lockPassword.value = ''
      // Clear access token
      accessToken.value = ''
      // Clear refresh token
      refreshToken.value = ''
      // Note: Don't clear worktab tabs, check on next login based on user
      // Remove iframe route cache
      sessionStorage.removeItem('iframeRoutes')
      // Clear home path
      useMenuStore().setHomePath('')
      // Reset router state
      resetRouterState(500)
      // Redirect to login page with current route as redirect parameter
      const currentRoute = router.currentRoute.value
      const redirect = currentRoute.path !== '/login' ? currentRoute.fullPath : undefined
      router.push({
        name: 'Login',
        query: redirect ? { redirect } : undefined
      })
    }

    /**
     * Check and clear worktab tabs
     * If different user logs in, clear worktab tabs
     * Should be called after successful login
     */
    const checkAndClearWorktabs = () => {
      const lastUserId = localStorage.getItem(StorageConfig.LAST_USER_ID_KEY)
      const currentUserId = info.value.userId

      // Cannot get current user ID, skip check
      if (!currentUserId) return

      // First login or cache cleared, keep existing tabs
      if (!lastUserId) {
        return
      }

      // Different user logged in, clear worktab tabs
      if (String(currentUserId) !== lastUserId) {
        const worktabStore = useWorktabStore()
        worktabStore.opened = []
        worktabStore.keepAliveExclude = []
      }

      // Clear temporary storage
      localStorage.removeItem(StorageConfig.LAST_USER_ID_KEY)
    }

    return {
      language,
      isLogin,
      isLock,
      lockPassword,
      info,
      searchHistory,
      accessToken,
      refreshToken,
      getUserInfo,
      getSettingState,
      getWorktabState,
      setUserInfo,
      setLoginStatus,
      setLanguage,
      setSearchHistory,
      setLockStatus,
      setLockPassword,
      setToken,
      logOut,
      checkAndClearWorktabs
    }
  },
  {
    persist: {
      key: 'user',
      storage: localStorage
    }
  }
)
