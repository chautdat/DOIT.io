/**
 * Pinia Store Configuration Module
 *
 * Provides global state management initialization and configuration
 *
 * ## Main Features
 *
 * - Pinia Store instance creation
 * - Persistence plugin configuration (pinia-plugin-persistedstate)
 * - Versioned storage key management
 * - Automatic data migration (cross-version)
 * - LocalStorage serialization configuration
 * - Store initialization function
 *
 * ## Persistence Strategy
 *
 * - Uses StorageKeyManager to generate versioned storage keys
 * - Format: sys-v{version}-{storeId}
 * - Automatically migrates old version data to current version
 * - Uses localStorage as storage medium
 *
 * @module store/index
 * @author Art Design Pro Team
 */
import type { App } from 'vue'
import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-plugin-persistedstate'
import { StorageKeyManager } from '@/utils/storage/storage-key-manager'

export const store = createPinia()

// Create storage key manager instance
const storageKeyManager = new StorageKeyManager()

// Configure persistence plugin
store.use(
  createPersistedState({
    key: (storeId: string) => storageKeyManager.getStorageKey(storeId),
    storage: localStorage,
    serializer: {
      serialize: JSON.stringify,
      deserialize: JSON.parse
    }
  })
)

/**
 * Initialize Store
 */
export function initStore(app: App<Element>): void {
  app.use(store)
}
