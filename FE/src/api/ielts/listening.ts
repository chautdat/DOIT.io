/**
 * Listening API
 *
 * API functions for IELTS Listening tests and practice
 * Matches BE: ListeningController
 *
 * @module api/ielts/listening
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/listening'

/**
 * Get all listening exams
 * @param params Query parameters (bandLevel, examType)
 * @returns List of listening exams
 */
export function getListeningExams(params?: { 
  bandLevel?: string
  examType?: string 
}) {
  return request.get({
    url: `${BASE_URL}/exams`,
    params
  })
}

/**
 * Get listening exam by ID
 * @param examId Exam ID
 * @returns Listening exam details
 */
export function getListeningExam(examId: string) {
  return request.get({
    url: `${BASE_URL}/exams/${examId}`
  })
}

/**
 * Start a listening test attempt
 * @param examId Exam ID
 * @returns New attempt info
 */
export function startListeningAttempt(examId: string) {
  return request.post({
    url: `${BASE_URL}/exams/${examId}/start`
  })
}

/**
 * Get listening sections for an exam
 * @param examId Exam ID
 * @returns List of sections with questions
 */
export function getListeningSections(examId: string) {
  return request.get({
    url: `${BASE_URL}/exams/${examId}/sections`
  })
}

/**
 * Submit listening test answers
 * @param attemptId Attempt ID
 * @param answers User answers
 * @returns Submission result with score
 */
export function submitListeningAnswers(attemptId: string, answers: Record<string, string>) {
  return request.post({
    url: `${BASE_URL}/attempts/${attemptId}/submit`,
    params: { answers }
  })
}

/**
 * Get listening attempt result
 * @param attemptId Attempt ID
 * @returns Detailed result with band score
 */
export function getListeningResult(attemptId: string) {
  return request.get({
    url: `${BASE_URL}/attempts/${attemptId}/result`
  })
}

/**
 * Get user's listening attempt history
 * @param params Pagination parameters
 * @returns List of past attempts
 */
export function getListeningHistory(params?: { 
  page?: number
  size?: number 
}) {
  return request.get({
    url: `${BASE_URL}/history`,
    params
  })
}