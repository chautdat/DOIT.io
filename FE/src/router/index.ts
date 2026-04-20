import type { App } from 'vue'
import { createRouter, createWebHashHistory } from 'vue-router'
import { staticRoutes } from './routes/staticRoutes'
import { configureNProgress } from '@/utils/router'
import { setupBeforeEachGuard } from './guards/beforeEach'
import { setupAfterEachGuard } from './guards/afterEach'

// Create router instance
export const router = createRouter({
  history: createWebHashHistory(),
  routes: staticRoutes // Static routes
})

// Initialize router
export function initRouter(app: App<Element>): void {
  configureNProgress() // Top progress bar
  setupBeforeEachGuard(router) // Router before guard
  setupAfterEachGuard(router) // Router after guard
  app.use(router)
}

// Home page path, uses first valid menu path by default, uses this path when configured
export const HOME_PAGE_PATH = ''
