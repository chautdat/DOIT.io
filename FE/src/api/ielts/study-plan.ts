/**
 * Study Plan API
 *
 * API functions for managing personalized study plans
 * Matches BE: StudyPlanController
 *
 * @module api/ielts/study-plan
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/study-plans'

/**
 * Get user's active study plan
 * @returns Active study plan
 */
export function getActiveStudyPlan() {
  return request.get({
    url: `${BASE_URL}/active`
  })
}

/**
 * Get all user's study plans
 * @returns List of study plans
 */
export function getStudyPlans() {
  return request.get({
    url: BASE_URL
  })
}

/**
 * Create a new study plan
 * @param plan Study plan data
 * @returns Created study plan
 */
export function createStudyPlan(plan: {
  targetBand: number
  targetDate: string
  focusSkills?: string[]
  studyHoursPerDay?: number
}) {
  return request.post({
    url: BASE_URL,
    params: plan
  })
}

/**
 * Update study plan
 * @param planId Plan ID
 * @param updates Updated fields
 * @returns Updated study plan
 */
export function updateStudyPlan(planId: string, updates: {
  targetBand?: number
  targetDate?: string
  focusSkills?: string[]
  isActive?: boolean
}) {
  return request.put({
    url: `${BASE_URL}/${planId}`,
    params: updates
  })
}

/**
 * Get today's study plan items
 * @returns Today's tasks
 */
export function getTodayItems() {
  return request.get({
    url: `${BASE_URL}/today`
  })
}

/**
 * Get upcoming study plan items
 * @param days Number of days to look ahead
 * @returns Upcoming tasks
 */
export function getUpcomingItems(days?: number) {
  return request.get({
    url: `${BASE_URL}/upcoming`,
    params: { days }
  })
}

/**
 * Get pending (overdue) items
 * @returns Overdue tasks
 */
export function getPendingItems() {
  return request.get({
    url: `${BASE_URL}/pending`
  })
}

/**
 * Complete a study plan item
 * @param itemId Item ID
 * @returns Updated item
 */
export function completeItem(itemId: string) {
  return request.put({
    url: `${BASE_URL}/items/${itemId}/complete`
  })
}

/**
 * Skip a study plan item
 * @param itemId Item ID
 * @returns Updated item
 */
export function skipItem(itemId: string) {
  return request.put({
    url: `${BASE_URL}/items/${itemId}/skip`
  })
}