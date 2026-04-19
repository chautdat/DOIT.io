/**
 * Mock Test API
 *
 * API functions for full IELTS mock tests
 *
 * @module api/ielts/mock-test
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/mock-tests'

/**
 * Get available mock tests
 * @param params Query parameters
 * @returns List of mock tests
 */
export function getMockTests(params?: { page?: number; size?: number; type?: string }) {
  return request.get({
    url: BASE_URL,
    params
  })
}

/**
 * Get mock test by ID
 * @param id Test ID
 * @returns Mock test details
 */
export function getMockTest(id: number) {
  return request.get({
    url: `${BASE_URL}/${id}`
  })
}

/**
 * Start mock test
 * @param testId Test ID
 * @returns Test session info
 */
export function startMockTest(testId: number) {
  return request.post({
    url: `${BASE_URL}/${testId}/start`
  })
}

/**
 * Get mock test sections
 * @param testId Test ID
 * @returns Test sections (Listening, Reading, Writing, Speaking)
 */
export function getMockTestSections(testId: number) {
  return request.get({
    url: `${BASE_URL}/${testId}/sections`
  })
}

/**
 * Submit mock test section
 * @param testId Test ID
 * @param sectionType Section type (LISTENING, READING, WRITING, SPEAKING)
 * @param answers User answers
 * @returns Submission result
 */
export function submitMockTestSection(
  testId: number,
  sectionType: Api.IELTS.SkillType,
  answers: Api.IELTS.UserAnswer[] | string
) {
  return request.post({
    url: `${BASE_URL}/${testId}/sections/${sectionType}/submit`,
    params: { answers }
  })
}

/**
 * Complete mock test
 * @param testId Test ID
 * @returns Final results
 */
export function completeMockTest(testId: number) {
  return request.post({
    url: `${BASE_URL}/${testId}/complete`
  })
}

/**
 * Get mock test result
 * @param attemptId Attempt ID
 * @returns Detailed test result with band scores
 */
export function getMockTestResult(attemptId: number) {
  return request.get({
    url: `${BASE_URL}/attempts/${attemptId}/result`
  })
}

/**
 * Get mock test history
 * @param params Query parameters
 * @returns List of completed mock tests
 */
export function getMockTestHistory(params?: { page?: number; size?: number }) {
  return request.get({
    url: `${BASE_URL}/history`,
    params
  })
}

/**
 * Get time remaining in mock test
 * @param sessionId Session ID
 * @returns Time remaining in seconds
 */
export function getTimeRemaining(sessionId: number) {
  return request.get({
    url: `${BASE_URL}/sessions/${sessionId}/time`
  })
}

/**
 * Pause mock test
 * @param sessionId Session ID
 * @returns Pause result
 */
export function pauseMockTest(sessionId: number) {
  return request.post({
    url: `${BASE_URL}/sessions/${sessionId}/pause`
  })
}

/**
 * Resume mock test
 * @param sessionId Session ID
 * @returns Resume result
 */
export function resumeMockTest(sessionId: number) {
  return request.post({
    url: `${BASE_URL}/sessions/${sessionId}/resume`
  })
}
