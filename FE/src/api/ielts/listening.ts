/**
 * Listening API
 *
 * API functions for IELTS Listening tests and practice
 *
 * @module api/ielts/listening
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/listening'

/**
 * Get all listening tests
 * @param params Query parameters
 * @returns List of listening tests
 */
export function getListeningTests(params?: { page?: number; size?: number; difficulty?: string }) {
  return request.get({
    url: BASE_URL,
    params
  })
}

/**
 * Get listening test by ID
 * @param id Test ID
 * @returns Listening test details
 */
export function getListeningTest(id: number) {
  return request.get({
    url: `${BASE_URL}/${id}`
  })
}

/**
 * Get listening sections for a test
 * @param testId Test ID
 * @returns List of sections
 */
export function getListeningSections(testId: number) {
  return request.get<Api.IELTS.ListeningSection[]>({
    url: `${BASE_URL}/${testId}/sections`
  })
}

/**
 * Get questions for a listening section
 * @param sectionId Section ID
 * @returns List of questions
 */
export function getListeningQuestions(sectionId: number) {
  return request.get<Api.IELTS.Question[]>({
    url: `${BASE_URL}/sections/${sectionId}/questions`
  })
}

/**
 * Submit listening test answers
 * @param testId Test ID
 * @param answers User answers
 * @returns Submission result
 */
export function submitListeningAnswers(testId: number, answers: Api.IELTS.UserAnswer[]) {
  return request.post({
    url: `${BASE_URL}/${testId}/submit`,
    params: { answers }
  })
}

/**
 * Get listening test result
 * @param attemptId Attempt ID
 * @returns Test result with score
 */
export function getListeningResult(attemptId: number) {
  return request.get({
    url: `${BASE_URL}/attempts/${attemptId}/result`
  })
}

/**
 * Start a new listening practice session
 * @param sectionId Section ID for practice
 * @returns Practice session info
 */
export function startListeningPractice(sectionId: number) {
  return request.post({
    url: `${BASE_URL}/practice/start`,
    params: { sectionId }
  })
}
