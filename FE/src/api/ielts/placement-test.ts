/**
 * Placement Test API
 *
 * API functions for IELTS placement/level assessment tests
 *
 * @module api/ielts/placement-test
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/placement-tests'

/**
 * Check if user needs placement test
 * @returns Whether placement test is needed
 */
export function checkPlacementStatus() {
  return request.get<{ needed: boolean; lastCompleted?: string }>({
    url: `${BASE_URL}/status`
  })
}

/**
 * Start placement test
 * @returns Placement test session
 */
export function startPlacementTest() {
  return request.post({
    url: `${BASE_URL}/start`
  })
}

/**
 * Get placement test questions
 * @param sessionId Session ID
 * @returns Adaptive questions based on current level
 */
export function getPlacementQuestions(sessionId: number) {
  return request.get({
    url: `${BASE_URL}/sessions/${sessionId}/questions`
  })
}

/**
 * Submit placement test answer
 * @param sessionId Session ID
 * @param answer User answer
 * @returns Next question or completion status
 */
export function submitPlacementAnswer(sessionId: number, answer: Api.IELTS.UserAnswer) {
  return request.post({
    url: `${BASE_URL}/sessions/${sessionId}/answer`,
    params: answer
  })
}

/**
 * Complete placement test
 * @param sessionId Session ID
 * @returns Placement test result with estimated level
 */
export function completePlacementTest(sessionId: number) {
  return request.post({
    url: `${BASE_URL}/sessions/${sessionId}/complete`
  })
}

/**
 * Get placement test result
 * @param sessionId Session ID
 * @returns Detailed result with skill breakdown
 */
export function getPlacementResult(sessionId: number) {
  return request.get({
    url: `${BASE_URL}/sessions/${sessionId}/result`
  })
}

/**
 * Get placement test history
 * @returns List of previous placement tests
 */
export function getPlacementHistory() {
  return request.get({
    url: `${BASE_URL}/history`
  })
}

/**
 * Get recommended starting point
 * @param sessionId Session ID
 * @returns Recommended study plan based on placement
 */
export function getRecommendedPlan(sessionId: number) {
  return request.get({
    url: `${BASE_URL}/sessions/${sessionId}/recommendation`
  })
}

/**
 * Skip placement test (use default level)
 * @param level Default level to use
 * @returns Result
 */
export function skipPlacementTest(level: Api.IELTS.DifficultyLevel) {
  return request.post({
    url: `${BASE_URL}/skip`,
    params: { level }
  })
}
