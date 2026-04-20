/**
 * Reading API
 *
 * API functions for IELTS Reading tests and practice
 * Matches BE: ReadingController
 *
 * @module api/ielts/reading
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/reading'

/**
 * Get all reading exams
 * @param params Query parameters
 * @returns List of reading exams
 */
export function getReadingExams(params?: {
  bandLevel?: string
  examType?: string
}) {
  return request.get({
    url: `${BASE_URL}/exams`,
    params
  })
}

/**
 * Get reading exam by ID
 * @param examId Exam ID
 * @returns Reading exam details
 */
export function getReadingExam(examId: string) {
  return request.get({
    url: `${BASE_URL}/exams/${examId}`
  })
}

/**
 * Start a reading test attempt
 * @param examId Exam ID
 * @returns New attempt info
 */
export function startReadingAttempt(examId: string) {
  return request.post({
    url: `${BASE_URL}/exams/${examId}/start`
  })
}

/**
 * Get reading passages for an exam
 * @param examId Exam ID
 * @returns List of passages with questions
 */
export function getReadingPassages(examId: string) {
  return request.get({
    url: `${BASE_URL}/exams/${examId}/passages`
  })
}

/**
 * Submit reading test answers
 * @param attemptId Attempt ID
 * @param answers User answers
 * @returns Submission result with score
 */
export function submitReadingAnswers(attemptId: string, answers: Record<string, string>) {
  return request.post({
    url: `${BASE_URL}/attempts/${attemptId}/submit`,
    params: { answers }
  })
}

/**
 * Get reading attempt result
 * @param attemptId Attempt ID
 * @returns Detailed result with band score
 */
export function getReadingResult(attemptId: string) {
  return request.get({
    url: `${BASE_URL}/attempts/${attemptId}/result`
  })
}

/**
 * Get user's reading attempt history
 * @param params Pagination parameters
 * @returns List of past attempts
 */
export function getReadingHistory(params?: {
  page?: number
  size?: number
}) {
  return request.get({
    url: `${BASE_URL}/history`,
    params
  })
}