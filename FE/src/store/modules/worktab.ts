/**
 * Work Tab State Management Module
 *
 * Provides complete state management for multi-tab functionality
 *
 * ## Main Features
 *
 * - Tab opening and closing
 * - Tab pinning and unpinning
 * - Batch close (left, right, others, all)
 * - Tab cache management (KeepAlive)
 * - Tab title customization
 * - Tab route validation
 * - Dynamic route parameter handling
 *
 * ## Use Cases
 *
 * - Multi-tab navigation
 * - Page cache control
 * - Tab right-click menu
 * - Pin frequently used pages
 * - Batch close tabs
 *
 * ## Core Features
 *
 * - Smart tab reuse (reuse by same route name)
 * - Fixed tab protection (cannot close)
 * - KeepAlive cache exclusion management
 * - Route validity verification
 * - Home page auto retention
 *
 * ## Persistence
 * - Uses localStorage for storage
 * - Storage key: sys-v{version}-worktab
 * - Maintain tab state after page refresh
 *
 * @module store/modules/worktab
 * @author Art Design Pro Team
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { router } from '@/router'
import { LocationQueryRaw, Router } from 'vue-router'
import { WorkTab } from '@/types'
import { useCommon } from '@/hooks/core/useCommon'

interface WorktabState {
  current: Partial<WorkTab>
  opened: WorkTab[]
  keepAliveExclude: string[]
}

/**
 * Work Tab Management Store
 */
export const useWorktabStore = defineStore(
  'worktabStore',
  () => {
    // State definitions
    const current = ref<Partial<WorkTab>>({})
    const opened = ref<WorkTab[]>([])
    const keepAliveExclude = ref<string[]>([])

    // Computed properties
    const hasOpenedTabs = computed(() => opened.value.length > 0)
    const hasMultipleTabs = computed(() => opened.value.length > 1)
    const currentTabIndex = computed(() =>
      current.value.path ? opened.value.findIndex((tab) => tab.path === current.value.path) : -1
    )

    /**
     * Find tab index
     */
    const findTabIndex = (path: string): number => {
      return opened.value.findIndex((tab) => tab.path === path)
    }

    /**
     * Get tab
     */
    const getTab = (path: string): WorkTab | undefined => {
      return opened.value.find((tab) => tab.path === path)
    }

    /**
     * Check if tab is closable
     */
    const isTabClosable = (tab: WorkTab): boolean => {
      return !tab.fixedTab
    }

    /**
     * Safe router push
     */
    const safeRouterPush = (tab: Partial<WorkTab>): void => {
      if (!tab.path) {
        console.warn('Attempting to navigate to tab with invalid path')
        return
      }

      try {
        router.push({
          path: tab.path,
          query: tab.query as LocationQueryRaw
        })
      } catch (error) {
        console.error('Router navigation failed:', error)
      }
    }

    /**
     * Open or activate a tab
     */
    const openTab = (tab: WorkTab): void => {
      if (!tab.path) {
        console.warn('Attempting to open invalid tab')
        return
      }

      // Remove from keepAlive exclude list
      if (tab.name) {
        removeKeepAliveExclude(tab.name)
      }

      // First search by route name (for dynamic route params causing multiple tabs), if not found then search by path
      let existingIndex = -1
      if (tab.name) {
        existingIndex = opened.value.findIndex((t) => t.name === tab.name)
      }
      if (existingIndex === -1) {
        existingIndex = findTabIndex(tab.path)
      }

      if (existingIndex === -1) {
        // Add new tab
        const insertIndex = tab.fixedTab ? findFixedTabInsertIndex() : opened.value.length
        const newTab = { ...tab }

        if (tab.fixedTab) {
          opened.value.splice(insertIndex, 0, newTab)
        } else {
          opened.value.push(newTab)
        }

        current.value = newTab
      } else {
        // Update existing tab (when dynamic route params or query changes, reuse same tab)
        const existingTab = opened.value[existingIndex]

        opened.value[existingIndex] = {
          ...existingTab,
          path: tab.path,
          params: tab.params,
          query: tab.query,
          title: tab.title || existingTab.title,
          fixedTab: tab.fixedTab ?? existingTab.fixedTab,
          keepAlive: tab.keepAlive ?? existingTab.keepAlive,
          name: tab.name || existingTab.name,
          icon: tab.icon || existingTab.icon
        }

        current.value = opened.value[existingIndex]
      }
    }

    /**
     * Find fixed tab insert position
     */
    const findFixedTabInsertIndex = (): number => {
      let insertIndex = 0
      for (let i = 0; i < opened.value.length; i++) {
        if (opened.value[i].fixedTab) {
          insertIndex = i + 1
        } else {
          break
        }
      }
      return insertIndex
    }

    /**
     * Close specified tab
     */
    const removeTab = (path: string): void => {
      const targetTab = getTab(path)
      const targetIndex = findTabIndex(path)

      if (targetIndex === -1) {
        console.warn(`Attempting to close non-existent tab: ${path}`)
        return
      }

      if (targetTab && !isTabClosable(targetTab)) {
        console.warn(`Attempting to close fixed tab: ${path}`)
        return
      }

      // Remove from tab list
      opened.value.splice(targetIndex, 1)

      // Handle cache exclusion
      if (targetTab?.name) {
        addKeepAliveExclude(targetTab)
      }

      const { homePath } = useCommon()

      // If no tabs after closing, navigate to home
      if (!hasOpenedTabs.value) {
        if (path !== homePath.value) {
          current.value = {}
          safeRouterPush({ path: homePath.value })
        }
        return
      }

      // If closing current active tab, need to activate another tab
      if (current.value.path === path) {
        const newIndex = targetIndex >= opened.value.length ? opened.value.length - 1 : targetIndex
        current.value = opened.value[newIndex]
        safeRouterPush(current.value)
      }
    }

    /**
     * Close left tabs
     */
    const removeLeft = (path: string): void => {
      const targetIndex = findTabIndex(path)

      if (targetIndex === -1) {
        console.warn(`Attempting to close left tabs, but target tab does not exist: ${path}`)
        return
      }

      // Get closable left tabs
      const leftTabs = opened.value.slice(0, targetIndex)
      const closableLeftTabs = leftTabs.filter(isTabClosable)

      if (closableLeftTabs.length === 0) {
        console.warn('No closable tabs on the left')
        return
      }

      // Mark for cache exclusion
      markTabsToRemove(closableLeftTabs)

      // Remove closable left tabs
      opened.value = opened.value.filter(
        (tab, index) => index >= targetIndex || !isTabClosable(tab)
      )

      // Ensure current tab is active
      const targetTab = getTab(path)
      if (targetTab) {
        current.value = targetTab
      }
    }

    /**
     * Close right tabs
     */
    const removeRight = (path: string): void => {
      const targetIndex = findTabIndex(path)

      if (targetIndex === -1) {
        console.warn(`Attempting to close right tabs, but target tab does not exist: ${path}`)
        return
      }

      // Get closable right tabs
      const rightTabs = opened.value.slice(targetIndex + 1)
      const closableRightTabs = rightTabs.filter(isTabClosable)

      if (closableRightTabs.length === 0) {
        console.warn('No closable tabs on the right')
        return
      }

      // Mark for cache exclusion
      markTabsToRemove(closableRightTabs)

      // Remove closable right tabs
      opened.value = opened.value.filter(
        (tab, index) => index <= targetIndex || !isTabClosable(tab)
      )

      // Ensure current tab is active
      const targetTab = getTab(path)
      if (targetTab) {
        current.value = targetTab
      }
    }

    /**
     * Close other tabs
     */
    const removeOthers = (path: string): void => {
      const targetTab = getTab(path)

      if (!targetTab) {
        console.warn(`Attempting to close other tabs, but target tab does not exist: ${path}`)
        return
      }

      // Get other closable tabs
      const otherTabs = opened.value.filter((tab) => tab.path !== path)
      const closableTabs = otherTabs.filter(isTabClosable)

      if (closableTabs.length === 0) {
        console.warn('No other closable tabs')
        return
      }

      // Mark for cache exclusion
      markTabsToRemove(closableTabs)

      // Keep only current tab and fixed tabs
      opened.value = opened.value.filter((tab) => tab.path === path || !isTabClosable(tab))

      // Ensure current tab is active
      current.value = targetTab
    }

    /**
     * Close all closable tabs
     */
    const removeAll = (): void => {
      const { homePath } = useCommon()
      const hasFixedTabs = opened.value.some((tab) => tab.fixedTab)

      // Get closable tabs
      const closableTabs = opened.value.filter((tab) => {
        if (!isTabClosable(tab)) return false
        // If there are fixed tabs, all closable tabs can be closed; otherwise keep home
        return hasFixedTabs || tab.path !== homePath.value
      })

      if (closableTabs.length === 0) {
        console.warn('No closable tabs')
        return
      }

      // Mark for cache exclusion
      markTabsToRemove(closableTabs)

      // Keep non-closable tabs and home (when no fixed tabs)
      opened.value = opened.value.filter((tab) => {
        return !isTabClosable(tab) || (!hasFixedTabs && tab.path === homePath.value)
      })

      // Handle active state
      if (!hasOpenedTabs.value) {
        current.value = {}
        safeRouterPush({ path: homePath.value })
        return
      }

      // Select active tab: prioritize home, then first available tab
      const homeTab = opened.value.find((tab) => tab.path === homePath.value)
      const targetTab = homeTab || opened.value[0]

      current.value = targetTab
      safeRouterPush(targetTab)
    }

    /**
     * Add specified tab to keepAlive exclude list
     */
    const addKeepAliveExclude = (tab: WorkTab): void => {
      if (!tab.keepAlive || !tab.name) return

      if (!keepAliveExclude.value.includes(tab.name)) {
        keepAliveExclude.value.push(tab.name)
      }
    }

    /**
     * Remove specified component name from keepAlive exclude list
     */
    const removeKeepAliveExclude = (name: string): void => {
      if (!name) return

      keepAliveExclude.value = keepAliveExclude.value.filter((item) => item !== name)
    }

    /**
     * Mark component names of passed tabs for cache exclusion
     */
    const markTabsToRemove = (tabs: WorkTab[]): void => {
      tabs.forEach((tab) => {
        if (tab.name) {
          addKeepAliveExclude(tab)
        }
      })
    }

    /**
     * Toggle fixed state of specified tab
     */
    const toggleFixedTab = (path: string): void => {
      const targetIndex = findTabIndex(path)

      if (targetIndex === -1) {
        console.warn(`Attempting to toggle fixed state of non-existent tab: ${path}`)
        return
      }

      const tab = { ...opened.value[targetIndex] }
      tab.fixedTab = !tab.fixedTab

      // Remove from original position
      opened.value.splice(targetIndex, 1)

      if (tab.fixedTab) {
        // Insert fixed tab at the end of all fixed tabs
        const firstNonFixedIndex = opened.value.findIndex((t) => !t.fixedTab)
        const insertIndex = firstNonFixedIndex === -1 ? opened.value.length : firstNonFixedIndex
        opened.value.splice(insertIndex, 0, tab)
      } else {
        // Insert non-fixed tab after all fixed tabs
        const fixedCount = opened.value.filter((t) => t.fixedTab).length
        opened.value.splice(fixedCount, 0, tab)
      }

      // Update current tab reference
      if (current.value.path === path) {
        current.value = tab
      }
    }

    /**
     * Validate worktab route validity
     */
    const validateWorktabs = (routerInstance: Router): void => {
      try {
        // Dynamic route validation: prioritize route name for validity check; otherwise use resolve for parameterized paths
        const isTabRouteValid = (tab: Partial<WorkTab>): boolean => {
          try {
            if (tab.name) {
              const routes = routerInstance.getRoutes()
              if (routes.some((r) => r.name === tab.name)) return true
            }
            if (tab.path) {
              const resolved = routerInstance.resolve({
                path: tab.path,
                query: (tab.query as LocationQueryRaw) || undefined
              })
              return resolved.matched.length > 0
            }
            return false
          } catch {
            return false
          }
        }

        // Filter out valid tabs
        const validTabs = opened.value.filter((tab) => isTabRouteValid(tab))

        if (validTabs.length !== opened.value.length) {
          console.warn('Found invalid tab routes, auto cleaned')
          opened.value = validTabs
        }

        // Validate current active tab validity
        const isCurrentValid = current.value && isTabRouteValid(current.value)

        if (!isCurrentValid && validTabs.length > 0) {
          console.warn('Current active tab is invalid, auto switched')
          current.value = validTabs[0]
        } else if (!isCurrentValid) {
          current.value = {}
        }
      } catch (error) {
        console.error('Failed to validate worktabs:', error)
      }
    }

    /**
     * Clear all state (for logout scenarios)
     */
    const clearAll = (): void => {
      current.value = {}
      opened.value = []
      keepAliveExclude.value = []
    }

    /**
     * Get state snapshot (for persistent storage)
     */
    const getStateSnapshot = (): WorktabState => {
      return {
        current: { ...current.value },
        opened: [...opened.value],
        keepAliveExclude: [...keepAliveExclude.value]
      }
    }

    /**
     * Get tab title
     */
    const getTabTitle = (path: string): WorkTab | undefined => {
      const tab = getTab(path)
      return tab
    }

    /**
     * Update tab title
     */
    const updateTabTitle = (path: string, title: string): void => {
      const tab = getTab(path)
      if (tab) {
        tab.customTitle = title
      }
    }

    /**
     * Reset tab title
     */
    const resetTabTitle = (path: string): void => {
      const tab = getTab(path)
      if (tab) {
        tab.customTitle = ''
      }
    }

    return {
      // State
      current,
      opened,
      keepAliveExclude,

      // Computed properties
      hasOpenedTabs,
      hasMultipleTabs,
      currentTabIndex,

      // Methods
      openTab,
      removeTab,
      removeLeft,
      removeRight,
      removeOthers,
      removeAll,
      toggleFixedTab,
      validateWorktabs,
      clearAll,
      getStateSnapshot,

      // Utility methods
      findTabIndex,
      getTab,
      isTabClosable,
      addKeepAliveExclude,
      removeKeepAliveExclude,
      markTabsToRemove,
      getTabTitle,
      updateTabTitle,
      resetTabTitle
    }
  },
  {
    persist: {
      key: 'worktab',
      storage: localStorage
    }
  }
)
