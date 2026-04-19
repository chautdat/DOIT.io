/**
 * Internationalization Configuration
 *
 * Multi-language internationalization solution based on vue-i18n.
 * Supports English and Chinese switching, automatically restores user's language preference from local storage.
 *
 * ## Main Features
 *
 * - Multi-language support - Supports English and Chinese (Simplified)
 * - Language switching - Dynamic language switching at runtime without page refresh
 * - Persistent storage - Automatically saves and restores user's language preference
 * - Global injection - $t function can be used in any component for translation
 * - Type safety - Provides TypeScript type support
 *
 * ## Supported Languages
 *
 * - en: English
 * - zh: 简体中文 (Chinese Simplified)
 *
 * @module locales
 * @author Art Design Pro Team
 */

import { createI18n } from 'vue-i18n'
import type { I18n, I18nOptions } from 'vue-i18n'
import { LanguageEnum } from '@/enums/appEnum'
import { getSystemStorage } from '@/utils/storage'
import { StorageKeyManager } from '@/utils/storage/storage-key-manager'

// Import language files synchronously
import enMessages from './langs/en.json'
import zhMessages from './langs/zh.json'

/**
 * Storage key manager instance
 */
const storageKeyManager = new StorageKeyManager()

/**
 * Language messages object
 */
const messages = {
  [LanguageEnum.EN]: enMessages,
  [LanguageEnum.ZH]: zhMessages
}

/**
 * Language options list
 * Used for language switch dropdown
 */
export const languageOptions = [
  { value: LanguageEnum.EN, label: 'English' },
  { value: LanguageEnum.ZH, label: '简体中文' }
]

/**
 * Get language setting from storage
 * @returns Language setting, returns default language if retrieval fails
 */
const getDefaultLanguage = (): LanguageEnum => {
  // Try to get language setting from versioned storage
  try {
    const storageKey = storageKeyManager.getStorageKey('user')
    const userStore = localStorage.getItem(storageKey)

    if (userStore) {
      const { language } = JSON.parse(userStore)
      if (language && Object.values(LanguageEnum).includes(language)) {
        return language
      }
    }
  } catch (error) {
    console.warn('[i18n] Failed to get language setting from versioned storage:', error)
  }

  // Try to get language setting from system storage
  try {
    const sys = getSystemStorage()
    if (sys) {
      const { user } = JSON.parse(sys)
      if (user?.language && Object.values(LanguageEnum).includes(user.language)) {
        return user.language
      }
    }
  } catch (error) {
    console.warn('[i18n] Failed to get language setting from system storage:', error)
  }

  // Return default language - English
  console.debug('[i18n] Using default language:', LanguageEnum.EN)
  return LanguageEnum.EN
}

/**
 * i18n configuration options
 */
const i18nOptions: I18nOptions = {
  locale: getDefaultLanguage(),
  legacy: false,
  globalInjection: true,
  fallbackLocale: LanguageEnum.EN,
  messages
}


const i18n: I18n = createI18n(i18nOptions)


interface Translation {
  (key: string): string
}
export const $t = i18n.global.t as Translation

export default i18n
