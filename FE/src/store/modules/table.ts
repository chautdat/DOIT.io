/**
 * Table State Management Module
 *
 * Provides state management for table display configuration
 *
 * ## Main Features
 *
 * - Table size configuration (compact, default, loose)
 * - Zebra stripe toggle
 * - Border toggle
 * - Header background toggle
 * - Fullscreen mode toggle
 *
 * ## Use Cases
 * - Table component style configuration
 * - User table preference settings
 * - Table toolbar feature control
 *
 * ## Persistence
 *
 * - Uses localStorage for storage
 * - Storage key: sys-v{version}-table
 * - User configuration persists across pages
 *
 * @module store/modules/table
 * @author Art Design Pro Team
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { TableSizeEnum } from '@/enums/formEnum'

// Table
export const useTableStore = defineStore(
  'tableStore',
  () => {
    // Table size
    const tableSize = ref(TableSizeEnum.DEFAULT)
    // Zebra stripe
    const isZebra = ref(false)
    // Border
    const isBorder = ref(false)
    // Header background
    const isHeaderBackground = ref(false)

    // Is fullscreen
    const isFullScreen = ref(false)

    /**
     * Set table size
     * @param size Table size enum value
     */
    const setTableSize = (size: TableSizeEnum) => (tableSize.value = size)

    /**
     * Set zebra stripe display status
     * @param value Whether to show zebra stripe
     */
    const setIsZebra = (value: boolean) => (isZebra.value = value)

    /**
     * Set table border display status
     * @param value Whether to show border
     */
    const setIsBorder = (value: boolean) => (isBorder.value = value)

    /**
     * Set header background display status
     * @param value Whether to show header background
     */
    const setIsHeaderBackground = (value: boolean) => (isHeaderBackground.value = value)

    /**
     * Set fullscreen status
     * @param value Whether fullscreen
     */
    const setIsFullScreen = (value: boolean) => (isFullScreen.value = value)

    return {
      tableSize,
      isZebra,
      isBorder,
      isHeaderBackground,
      setTableSize,
      setIsZebra,
      setIsBorder,
      setIsHeaderBackground,
      isFullScreen,
      setIsFullScreen
    }
  },
  {
    persist: {
      key: 'table',
      storage: localStorage
    }
  }
)
