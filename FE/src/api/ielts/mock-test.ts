/**
 * Mock Test API
 *
 * API functions for full IELTS mock tests
 * Matches BE: MockTestController
 *
 * @module api/ielts/mock-test
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/mock-test'

/**
 * Start a new mock test
 * @returns Mock test session info
 */
export function startMockTest() {
  return request.post({
    url: `${BASE_URL}/start`
  })
}

/**
 * Start a mock test with pre-selected exams
 * @param exams Selected exam IDs for each skill
 * @returns Mock test session info
 */
export function startMockTestWithExams(exams: {
  listeningExamId?: string
  readingExamId?: string
  writingExamId?: string
  speakingExamId?: string
}) {
  return request.post({
    url: `${BASE_URL}/start-with-exams`,
    params: exams
  })
}

/**
 * Get mock test by ID
 * @param mockTestId Mock test ID
 * @returns Mock test details
 */
export function getMockTest(mockTestId: string) {
  return request.get({
    url: `${BASE_URL}/${mockTestId}`
  })
}

/**
 * Get user's mock tests
 * @returns List of mock tests
 */
export function getUserMockTests() {
  return request.get({
    url: BASE_URL
  })
}

/**
 * Get paginated mock tests
 * @param params Pagination parameters
 * @returns Paginated list of mock tests
 */
export function getUserMockTestsPaginated(params?: {
  page?: number
  size?: number
}) {
  return request.get({
    url: `${BASE_URL}/paginated`,
    params
  })
}

/**
 * Get in-progress mock tests
 * @returns List of in-progress mock tests
 */
export function getInProgressMockTests() {
  return request.get({
    url: `${BASE_URL}/in-progress`
  })
}

/**
 * Submit skill attempt for mock test
 * @param mockTestId Mock test ID
 * @param skillSubmission Skill submission data
 * @returns Updated mock test
 */
export function submitSkillAttempt(mockTestId: string, skillSubmission: {
  skill: 'LISTENING' | 'READING' | 'WRITING' | 'SPEAKING'
  attemptId: string
}) {
  return request.post({
    url: `${BASE_URL}/${mockTestId}/submit-skill`,
    params: skillSubmission
  })
}

/**
 * Complete mock test
 * @param mockTestId Mock test ID
 * @returns Final results
 */
export function completeMockTest(mockTestId: string) {
  return request.post({
    url: `${BASE_URL}/${mockTestId}/complete`
  })
}

/**
 * Get mock test result
 * @param mockTestId Mock test ID
 * @returns Detailed test result with band scores
 */
export function getMockTestResult(mockTestId: string) {
  return request.get({
    url: `${BASE_URL}/${mockTestId}/result`
  })
}