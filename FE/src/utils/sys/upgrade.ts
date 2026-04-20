import { upgradeLogList } from '@/mock/upgrade/changeLog'
import { ElNotification } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import { StorageConfig } from '@/utils/storage/storage-config'

class VersionManager {
  private normalizeVersion(version: string): string {
    return version.replace(/^v/, '')
  }

  private getStoredVersion(): string | null {
    return localStorage.getItem(StorageConfig.VERSION_KEY)
  }

  private setStoredVersion(version: string): void {
    localStorage.setItem(StorageConfig.VERSION_KEY, version)
  }

  private shouldSkipUpgrade(): boolean {
    return StorageConfig.CURRENT_VERSION === StorageConfig.SKIP_UPGRADE_VERSION
  }

  private isFirstVisit(storedVersion: string | null): boolean {
    return !storedVersion
  }

  private isSameVersion(storedVersion: string): boolean {
    return storedVersion === StorageConfig.CURRENT_VERSION
  }

  private findLegacyStorage(): { oldSysKey: string | null; oldVersionKeys: string[] } {
    const storageKeys = Object.keys(localStorage)
    const currentVersionPrefix = StorageConfig.generateStorageKey('').slice(0, -1)

    const oldSysKey =
      storageKeys.find(
        (key) =>
          StorageConfig.isVersionedKey(key) && key !== currentVersionPrefix && !key.includes('-')
      ) || null

    const oldVersionKeys = storageKeys.filter(
      (key) =>
        StorageConfig.isVersionedKey(key) &&
        !StorageConfig.isCurrentVersionKey(key) &&
        key.includes('-')
    )

    return { oldSysKey, oldVersionKeys }
  }

  private shouldRequireReLogin(storedVersion: string): boolean {
    const normalizedCurrent = this.normalizeVersion(StorageConfig.CURRENT_VERSION)
    const normalizedStored = this.normalizeVersion(storedVersion)

    return upgradeLogList.value.some((item) => {
      const itemVersion = this.normalizeVersion(item.version)
      return (
        item.requireReLogin && itemVersion > normalizedStored && itemVersion <= normalizedCurrent
      )
    })
  }

  private buildUpgradeMessage(requireReLogin: boolean): string {
    const { title: content } = upgradeLogList.value[0]

    const messageParts = [
      `<p style="color: var(--art-gray-800) !important; padding-bottom: 5px;">`,
      `The system has been upgraded to ${StorageConfig.CURRENT_VERSION}. This release includes the following improvements:`,
      `</p>`,
      content
    ]

    if (requireReLogin) {
      messageParts.push(
        `<p style="color: var(--theme-color); padding-top: 5px;">Upgrade complete. Please sign in again to continue.</p>`
      )
    }

    return messageParts.join('')
  }

  private showUpgradeNotification(message: string): void {
    ElNotification({
      title: 'System Upgrade Notice',
      message,
      duration: 0,
      type: 'success',
      dangerouslyUseHTMLString: true
    })
  }

  private cleanupLegacyData(oldSysKey: string | null, oldVersionKeys: string[]): void {
    if (oldSysKey) {
      localStorage.removeItem(oldSysKey)
      console.info(`[Upgrade] Cleared legacy storage: ${oldSysKey}`)
    }

    oldVersionKeys.forEach((key) => {
      localStorage.removeItem(key)
      console.info(`[Upgrade] Cleared legacy storage: ${key}`)
    })
  }

  private performLogout(): void {
    try {
      useUserStore().logOut()
      console.info('[Upgrade] Performed post-upgrade logout')
    } catch (error) {
      console.error('[Upgrade] Post-upgrade logout failed:', error)
    }
  }

  private async executeUpgrade(
    storedVersion: string,
    legacyStorage: ReturnType<typeof this.findLegacyStorage>
  ): Promise<void> {
    try {
      if (!upgradeLogList.value.length) {
        console.warn('[Upgrade] Upgrade log list is empty')
        return
      }

      const requireReLogin = this.shouldRequireReLogin(storedVersion)
      const message = this.buildUpgradeMessage(requireReLogin)

      this.showUpgradeNotification(message)

      this.setStoredVersion(StorageConfig.CURRENT_VERSION)

      this.cleanupLegacyData(legacyStorage.oldSysKey, legacyStorage.oldVersionKeys)

      if (requireReLogin) {
        this.performLogout()
      }

      console.info(`[Upgrade] Upgrade complete: ${storedVersion} → ${StorageConfig.CURRENT_VERSION}`)
    } catch (error) {
      console.error('[Upgrade] System upgrade handling failed:', error)
    }
  }

  async processUpgrade(): Promise<void> {
    if (this.shouldSkipUpgrade()) {
      console.debug('[Upgrade] Skipping version upgrade check')
      return
    }

    const storedVersion = this.getStoredVersion()

    if (this.isFirstVisit(storedVersion)) {
      this.setStoredVersion(StorageConfig.CURRENT_VERSION)
      return
    }

    if (this.isSameVersion(storedVersion!)) {
      return
    }

    const legacyStorage = this.findLegacyStorage()
    if (!legacyStorage.oldSysKey && legacyStorage.oldVersionKeys.length === 0) {
      this.setStoredVersion(StorageConfig.CURRENT_VERSION)
      console.info('[Upgrade] No legacy data found. Version number updated.')
      return
    }

    setTimeout(() => {
      this.executeUpgrade(storedVersion!, legacyStorage)
    }, StorageConfig.UPGRADE_DELAY)
  }
}

const versionManager = new VersionManager()

export async function systemUpgrade(): Promise<void> {
  await versionManager.processUpgrade()
}
