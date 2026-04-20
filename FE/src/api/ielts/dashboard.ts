/**
 * Dashboard API
 *
 * API functions for user dashboard and analytics
 * Matches BE: DashboardController
 *
 * @module api/ielts/dashboard
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/dashboard'

/**
 * Get dashboard overview
 * @returns Dashboard statistics and summary
 */
export function getDashboard() {
  return request.get({
    url: BASE_URL
  })
}

/**
 * Get skill progress breakdown
 * @returns Progress for each IELTS skill
 */
export function getSkillProgress() {
  return request.get({
    url: `${BASE_URL}/skill-progress`
  })
}

/**
 * Get recent activities
 * @param limit Number of items to return
 * @returns Recent activity list
 */
export function getRecentActivities(limit?: number) {
  return request.get({
    url: `${BASE_URL}/recent-activities`,
    params: { limit }
  })
}

/**
 * Get progress chart data
 * @param period Time period (week, month, year)
 * @returns Progress data for charts
 */
export function getProgressChart(period?: 'week' | 'month' | 'year') {
  return request.get({
    url: `${BASE_URL}/progress-chart`,
    params: { period }
  })
}

/**
 * Get user statistics
 * @returns Detailed user statistics
 */
export function getStatistics() {
  return request.get({
    url: `${BASE_URL}/statistics`
  })
}