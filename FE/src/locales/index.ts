/**
 * Internationalization Configuration
 *
 * Multi-language internationalization solution based on vue-i18n.
 * Currently supports English only.
 *
 * ## Main Features
 *
 * - Language support - English
 * - Persistent storage - Automatically saves and restores user's language preference
 * - Global injection - $t function can be used in any component for translation
 * - Type safety - Provides TypeScript type support
 *
 * ## Supported Languages
 *
 * - en: English
 *
 * @module locales
 * @author DOIT IELTS Team
 */

import { createI18n } from 'vue-i18n'
import type { I18n, I18nOptions } from 'vue-i18n'
import { LanguageEnum } from '@/enums/appEnum'

// Import language files synchronously
import enMessages from './langs/en.json'

/**
 * Language messages object
 */
const messages = {
  [LanguageEnum.EN]: enMessages
}

/**
 * Language options list
 * Used for language switch dropdown
 */
export const languageOptions = [
  { value: LanguageEnum.EN, label: 'English' }
]

/**
 * Get default language
 * @returns Default language (English)
 */
const getDefaultLanguage = (): LanguageEnum => {
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
