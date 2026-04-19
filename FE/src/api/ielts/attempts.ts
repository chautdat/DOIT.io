/**
 * Attempts API
 *
 * API functions for managing test attempts and user answers
 *
 * @module api/ielts/attempts
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/attempts'

/**
 * Get user's test attempts
 * @param params Query parameters
 * @returns Paginated list of attempts
 */
export function getAttempts(params?: {
  page?: number
  size?: number
  skillType?: Api.IELTS.SkillType
  status?: string
}) {
  return request.get<Api.Common.PaginatedResponse<Api.IELTS.TestAttempt>>({
    url: BASE_URL,
    params
  })
}

/**
 * Get attempt by ID
 * @param id Attempt ID
 * @returns Attempt details
 */
export function getAttempt(id: number) {
  return request.get<Api.IELTS.TestAttempt>({
    url: `${BASE_URL}/${id}`
  })
}

/**
 * Start a new test attempt
 * @param testId Test ID
 * @param skillType Skill type
 * @returns New attempt info
 */
export function startAttempt(testId: number, skillType: Api.IELTS.SkillType) {
  return request.post<Api.IELTS.TestAttempt>({
    url: BASE_URL,
    params: { testId, skillType }
  })
}

/**
 * Submit attempt answers
 * @param attemptId Attempt ID
 * @param answers User answers
 * @returns Submission result
 */
export function submitAttemptAnswers(attemptId: number, answers: Api.IELTS.UserAnswer[]) {
  return request.post({
    url: `${BASE_URL}/${attemptId}/submit`,
    params: { answers }
  })
}

/**
 * Save progress (auto-save)
 * @param attemptId Attempt ID
 * @param answers Current answers
 * @returns Save result
 */
export function saveProgress(attemptId: number, answers: Api.IELTS.UserAnswer[]) {
  return request.put({
    url: `${BASE_URL}/${attemptId}/progress`,
    params: { answers }
  })
}

/**
 * Abandon attempt
 * @param attemptId Attempt ID
 * @returns Abandonment result
 */
export function abandonAttempt(attemptId: number) {
  return request.put({
    url: `${BASE_URL}/${attemptId}/abandon`
  })
}

/**
 * Get attempt result with detailed analysis
 * @param attemptId Attempt ID
 * @returns Detailed result and analysis
 */
export function getAttemptResult(attemptId: number) {
  return request.get({
    url: `${BASE_URL}/${attemptId}/result`
  })
}

/**
 * Get user's answer history for a question
 * @param questionId Question ID
 * @returns Answer history
 */
export function getAnswerHistory(questionId: number) {
  return request.get({
    url: `${BASE_URL}/history`,
    params: { questionId }
  })
}

/**
 * Get user's statistics
 * @returns User statistics
 */
export function getUserStatistics() {
  return request.get({
    url: `${BASE_URL}/statistics`
  })
}
