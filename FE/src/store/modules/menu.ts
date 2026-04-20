/**
 * Menu State Management Module
 *
 * Provides state management for menu data and dynamic routes
 *
 * ## Main Features
 *
 * - Menu list storage and management
 * - Home page path configuration
 * - Dynamic route registration and removal
 * - Route removal function management
 * - Menu width configuration
 *
 * ## Use Cases
 *
 * - Dynamic menu loading and rendering
 * - Route permission control
 * - Dynamic home page path setting
 * - Clean up dynamic routes on logout
 *
 * ## Workflow
 *
 * 1. Get menu data (frontend/backend mode)
 * 2. Set menu list and home page path
 * 3. Register dynamic routes and save removal functions
 * 4. Call removal functions to clean up routes on logout
 *
 * @module store/modules/menu
 * @author Art Design Pro Team
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { AppRouteRecord } from '@/types/router'
import { getFirstMenuPath } from '@/utils'
import { HOME_PAGE_PATH } from '@/router'

/**
 * Menu State Management
 * Manages app's menu list, home path, menu width and dynamic route removal functions
 */
export const useMenuStore = defineStore('menuStore', () => {
  /** Home page path */
  const homePath = ref(HOME_PAGE_PATH)
  /** Menu list */
  const menuList = ref<AppRouteRecord[]>([])
  /** Menu width */
  const menuWidth = ref('')
  /** Array storing route removal functions */
  const removeRouteFns = ref<(() => void)[]>([])

  /**
   * Set menu list
   * @param list Menu route record array
   */
  const setMenuList = (list: AppRouteRecord[]) => {
    menuList.value = list
    setHomePath(HOME_PAGE_PATH || getFirstMenuPath(list))
  }

  /**
   * Get home page path
   * @returns Home page path string
   */
  const getHomePath = () => homePath.value

  /**
   * Set home page path
   * @param path Home page path
   */
  const setHomePath = (path: string) => {
    homePath.value = path
  }

  /**
   * Add route removal functions
   * @param fns Route removal functions array to add
   */
  const addRemoveRouteFns = (fns: (() => void)[]) => {
    removeRouteFns.value.push(...fns)
  }

  /**
   * Remove all dynamic routes
   * Execute all stored route removal functions and clear the array
   */
  const removeAllDynamicRoutes = () => {
    removeRouteFns.value.forEach((fn) => fn())
    removeRouteFns.value = []
  }

  /**
   * Clear route removal functions array
   */
  const clearRemoveRouteFns = () => {
    removeRouteFns.value = []
  }

  return {
    menuList,
    menuWidth,
    removeRouteFns,
    setMenuList,
    getHomePath,
    setHomePath,
    addRemoveRouteFns,
    removeAllDynamicRoutes,
    clearRemoveRouteFns
  }
})
