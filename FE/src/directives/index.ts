import type { App } from 'vue'
import { setupAuthDirective, type AuthDirective } from './core/auth'
import { setupHighlightDirective, type HighlightDirective } from './business/highlight'
import { setupRippleDirective, type RippleDirective } from './business/ripple'
import { setupRolesDirective, type RolesDirective } from './core/roles'

export function setupGlobDirectives(app: App) {
  setupAuthDirective(app)
  setupRolesDirective(app)
  setupHighlightDirective(app)
  setupRippleDirective(app)
}

export type { AuthDirective, HighlightDirective, RippleDirective, RolesDirective }
