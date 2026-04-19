/**
 * Dashboard API
 *
 * API functions for user dashboard and analytics
 *
 * @module api/ielts/dashboard
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/dashboard'

/**
 * Get dashboard overview statistics
 * @returns Dashboard statistics
 */
export function getDashboardStats() {
  return request.get<Api.IELTS.DashboardStats>({
    url: `${BASE_URL}/stats`
  })
}

/**
 * Get recent activity
 * @param limit Number of items to return
 * @returns Recent activity list
 */
export function getRecentActivity(limit?: number) {
  return request.get<Api.IELTS.RecentActivity[]>({
    url: `${BASE_URL}/activity`,
    params: { limit }
  })
}

/**
 * Get skill scores breakdown
 * @returns Skill scores for each IELTS section
 */
export function getSkillScores() {
  return request.get({
    url: `${BASE_URL}/skills`
  })
}

/**
 * Get progress chart data
 * @param period Time period (week, month, year)
 * @returns Progress data for charts
 */
export function getProgressChart(period: 'week' | 'month' | 'year') {
  return request.get<Api.IELTS.WeeklyProgress[]>({
    url: `${BASE_URL}/progress`,
    params: { period }
  })
}

/**
 * Get score history
 * @param skillType Optional skill type filter
 * @returns Score history
 */
export function getScoreHistory(skillType?: Api.IELTS.SkillType) {
  return request.get({
    url: `${BASE_URL}/scores`,
    params: { skillType }
  })
}

/**
 * Get weak areas analysis
 * @returns Weak areas and recommendations
 */
export function getWeakAreas() {
  return request.get({
    url: `${BASE_URL}/weak-areas`
  })
}

/**
 * Get practice time statistics
 * @param period Time period
 * @returns Practice time breakdown
 */
export function getPracticeTime(period: 'week' | 'month') {
  return request.get({
    url: `${BASE_URL}/practice-time`,
    params: { period }
  })
}

/**
 * Get streak information
 * @returns Current streak and best streak
 */
export function getStreak() {
  return request.get({
    url: `${BASE_URL}/streak`
  })
}

/**
 * Get achievements
 * @returns User achievements list
 */
export function getAchievements() {
  return request.get({
    url: `${BASE_URL}/achievements`
  })
}

/**
 * Get upcoming tasks
 * @returns Upcoming study tasks
 */
export function getUpcomingTasks() {
  return request.get<Api.IELTS.DailyTask[]>({
    url: `${BASE_URL}/upcoming-tasks`
  })
}
