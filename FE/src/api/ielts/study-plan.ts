/**
 * Study Plan API
 *
 * API functions for managing personalized study plans
 *
 * @module api/ielts/study-plan
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/study-plans'

/**
 * Get current user's study plan
 * @returns User's study plan
 */
export function getStudyPlan() {
  return request.get<Api.IELTS.StudyPlan>({
    url: `${BASE_URL}/current`
  })
}

/**
 * Create or update study plan
 * @param plan Study plan data
 * @returns Created/updated study plan
 */
export function saveStudyPlan(plan: Partial<Api.IELTS.StudyPlan>) {
  return request.post<Api.IELTS.StudyPlan>({
    url: BASE_URL,
    params: plan
  })
}

/**
 * Update study plan target
 * @param targetBand Target band score
 * @param targetDate Target date
 * @returns Updated study plan
 */
export function updateTarget(targetBand: number, targetDate: string) {
  return request.put<Api.IELTS.StudyPlan>({
    url: `${BASE_URL}/target`,
    params: { targetBand, targetDate }
  })
}

/**
 * Get daily tasks
 * @param date Date (YYYY-MM-DD format)
 * @returns List of daily tasks
 */
export function getDailyTasks(date?: string) {
  return request.get<Api.IELTS.DailyTask[]>({
    url: `${BASE_URL}/tasks`,
    params: { date }
  })
}

/**
 * Complete a daily task
 * @param taskId Task ID
 * @returns Updated task
 */
export function completeTask(taskId: number) {
  return request.put<Api.IELTS.DailyTask>({
    url: `${BASE_URL}/tasks/${taskId}/complete`
  })
}

/**
 * Get weekly schedule
 * @param weekStart Start date of the week
 * @returns Weekly schedule
 */
export function getWeeklySchedule(weekStart?: string) {
  return request.get({
    url: `${BASE_URL}/schedule`,
    params: { weekStart }
  })
}

/**
 * Generate AI study plan recommendation
 * @param params User preferences
 * @returns Recommended study plan
 */
export function generateRecommendation(params: {
  targetBand: number
  targetDate: string
  weeklyHours: number
  focusAreas: Api.IELTS.SkillType[]
}) {
  return request.post<Api.IELTS.StudyPlan>({
    url: `${BASE_URL}/recommend`,
    params
  })
}

/**
 * Get study plan progress
 * @returns Progress statistics
 */
export function getProgress() {
  return request.get({
    url: `${BASE_URL}/progress`
  })
}

/**
 * Reset study plan
 * @returns Reset result
 */
export function resetPlan() {
  return request.del({
    url: `${BASE_URL}/current`
  })
}
