/**
 * System-level Enum Definitions
 *
 * ## Main Features
 *
 * - Menu type enum (left, top, mixed, dual-column)
 * - Theme type enum (light, dark, auto)
 * - Menu theme enum (design, light, dark)
 * - Language type enum (English)
 * - Container width enum (full screen, fixed)
 * - Menu width enum (collapsed width)
 *
 * @module enums/appEnum
 * @author DOIT IELTS Team
 */

/**
 * Menu Type
 */
export enum MenuTypeEnum {
  /** Left menu */
  LEFT = 'left',
  /** Top menu */
  TOP = 'top',
  /** Top + Left menu */
  TOP_LEFT = 'top-left',
  /** Dual-column menu */
  DUAL_MENU = 'dual-menu'
}

/**
 * System Theme
 */
export enum SystemThemeEnum {
  /** Dark theme */
  DARK = 'dark',
  /** Light theme */
  LIGHT = 'light',
  /** Auto theme (follow system) */
  AUTO = 'auto'
}

/**
 * Menu Theme
 */
export enum MenuThemeEnum {
  /** Dark theme */
  DARK = 'dark',
  /** Light theme */
  LIGHT = 'light',
  /** Design theme */
  DESIGN = 'design'
}

/**
 * Menu Width
 */
export enum MenuWidth {
  /** Collapsed width */
  CLOSE = '64px'
}

/**
 * Language Type
 */
export enum LanguageEnum {
  /** English */
  EN = 'en'
}

/**
 * Container Width
 */
export enum ContainerWidthEnum {
  /** Full width */
  FULL = '100%',
  /** Fixed width */
  BOXED = '1200px'
}
