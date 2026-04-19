/**
 * Reading API
 *
 * API functions for IELTS Reading tests and practice
 *
 * @module api/ielts/reading
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/reading'

/**
 * Get all reading tests
 * @param params Query parameters
 * @returns List of reading tests
 */
export function getReadingTests(params?: { page?: number; size?: number; difficulty?: string }) {
  return request.get({
    url: BASE_URL,
    params
  })
}

/**
 * Get reading test by ID
 * @param id Test ID
 * @returns Reading test details
 */
export function getReadingTest(id: number) {
  return request.get({
    url: `${BASE_URL}/${id}`
  })
}

/**
 * Get reading passages for a test
 * @param testId Test ID
 * @returns List of passages
 */
export function getReadingPassages(testId: number) {
  return request.get<Api.IELTS.ReadingPassage[]>({
    url: `${BASE_URL}/${testId}/passages`
  })
}

/**
 * Get questions for a reading passage
 * @param passageId Passage ID
 * @returns List of questions
 */
export function getReadingQuestions(passageId: number) {
  return request.get<Api.IELTS.Question[]>({
    url: `${BASE_URL}/passages/${passageId}/questions`
  })
}

/**
 * Submit reading test answers
 * @param testId Test ID
 * @param answers User answers
 * @returns Submission result
 */
export function submitReadingAnswers(testId: number, answers: Api.IELTS.UserAnswer[]) {
  return request.post({
    url: `${BASE_URL}/${testId}/submit`,
    params: { answers }
  })
}

/**
 * Get reading test result
 * @param attemptId Attempt ID
 * @returns Test result with score
 */
export function getReadingResult(attemptId: number) {
  return request.get({
    url: `${BASE_URL}/attempts/${attemptId}/result`
  })
}

/**
 * Start a new reading practice session
 * @param passageId Passage ID for practice
 * @returns Practice session info
 */
export function startReadingPractice(passageId: number) {
  return request.post({
    url: `${BASE_URL}/practice/start`,
    params: { passageId }
  })
}
