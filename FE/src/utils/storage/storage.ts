import { router } from '@/router'
import { useUserStore } from '@/store/modules/user'
import { StorageConfig } from '@/utils/storage/storage-config'

class StorageCompatibilityManager {
  getSystemVersion(): string | null {
    return localStorage.getItem(StorageConfig.VERSION_KEY)
  }

  getSystemStorage(): any {
    const version = this.getSystemVersion() || StorageConfig.CURRENT_VERSION
    const legacyKey = StorageConfig.generateLegacyKey(version)
    const data = localStorage.getItem(legacyKey)
    return data ? JSON.parse(data) : null
  }

  private hasCurrentVersionStorage(): boolean {
    const storageKeys = Object.keys(localStorage)
    const currentVersionPattern = StorageConfig.createCurrentVersionPattern()

    return storageKeys.some(
      (key) => currentVersionPattern.test(key) && localStorage.getItem(key) !== null
    )
  }

  private hasAnyVersionStorage(): boolean {
    const storageKeys = Object.keys(localStorage)
    const versionPattern = StorageConfig.createVersionPattern()

    return storageKeys.some((key) => versionPattern.test(key) && localStorage.getItem(key) !== null)
  }

  private getLegacyStorageData(): Record<string, any> {
    try {
      const systemStorage = this.getSystemStorage()
      return systemStorage || {}
    } catch (error) {
      console.warn('[Storage] Failed to parse legacy storage data:', error)
      return {}
    }
  }

  private showStorageError(): void {
    ElMessage({
      type: 'error',
      offset: 40,
      duration: 5000,
      message: 'The system detected corrupted local data. Please sign in again to continue.'
    })
  }

  private performSystemLogout(): void {
    setTimeout(() => {
      try {
        localStorage.clear()
        useUserStore().logOut()
        router.push({ name: 'Login' })
        console.info('[Storage] System logout completed')
      } catch (error) {
        console.error('[Storage] System logout failed:', error)
      }
    }, StorageConfig.LOGOUT_DELAY)
  }

  private handleStorageError(): void {
    this.showStorageError()
    this.performSystemLogout()
  }

  validateStorageData(requireAuth: boolean = false): boolean {
    try {
      if (this.hasCurrentVersionStorage()) {
        return true
      }

      if (this.hasAnyVersionStorage()) {
        return true
      }

      const legacyData = this.getLegacyStorageData()
      if (Object.keys(legacyData).length === 0) {
        if (requireAuth) {
          console.warn('[Storage] No storage data found. Sign in again.')
          this.performSystemLogout()
          return false
        }
        return true
      }

      console.debug('[Storage] Legacy storage data detected')
      return true
    } catch (error) {
      console.error('[Storage] Storage data validation failed:', error)
      if (requireAuth) {
        this.handleStorageError()
        return false
      }
      return true
    }
  }

  isStorageEmpty(): boolean {
    if (this.hasCurrentVersionStorage()) {
      return false
    }

    if (this.hasAnyVersionStorage()) {
      return false
    }

    const legacyData = this.getLegacyStorageData()
    return Object.keys(legacyData).length === 0
  }

  checkCompatibility(requireAuth: boolean = false): boolean {
    try {
      const isValid = this.validateStorageData(requireAuth)
      const isEmpty = this.isStorageEmpty()

      if (isValid || isEmpty) {
        return true
      }

      console.warn('[Storage] Storage compatibility check failed')
      return false
    } catch (error) {
      console.error('[Storage] Compatibility check exception:', error)
      return false
    }
  }
}

const storageManager = new StorageCompatibilityManager()

export function getSystemStorage(): any {
  return storageManager.getSystemStorage()
}

export function getSysVersion(): string | null {
  return storageManager.getSystemVersion()
}

export function validateStorageData(requireAuth: boolean = false): boolean {
  return storageManager.validateStorageData(requireAuth)
}

export function checkStorageCompatibility(requireAuth: boolean = false): boolean {
  return storageManager.checkCompatibility(requireAuth)
}
