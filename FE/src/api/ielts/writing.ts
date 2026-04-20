/**
 * Writing API
 *
 * API functions for IELTS Writing tests and practice
 * Matches BE: WritingController
 *
 * @module api/ielts/writing
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/writing'

/**
 * Get all writing exams
 * @param params Query parameters
 * @returns List of writing exams
 */
export function getWritingExams(params?: {
  bandLevel?: string
  examType?: string
  taskType?: string
}) {
  return request.get({
    url: `${BASE_URL}/exams`,
    params
  })
}

/**
 * Get writing exam by ID
 * @param examId Exam ID
 * @returns Writing exam details with tasks
 */
export function getWritingExam(examId: string) {
  return request.get({
    url: `${BASE_URL}/exams/${examId}`
  })
}

/**
 * Start a writing test attempt
 * @param examId Exam ID
 * @returns New attempt info
 */
export function startWritingAttempt(examId: string) {
  return request.post({
    url: `${BASE_URL}/exams/${examId}/start`
  })
}

/**
 * Submit writing test response
 * @param attemptId Attempt ID
 * @param submission Writing submission (task1Response, task2Response)
 * @returns Submission result
 */
export function submitWritingResponse(attemptId: string, submission: {
  task1Response?: string
  task2Response?: string
}) {
  return request.post({
    url: `${BASE_URL}/attempts/${attemptId}/submit`,
    params: submission
  })
}

/**
 * Get writing evaluation with AI grading
 * @param attemptId Attempt ID
 * @returns Evaluation with band scores and feedback
 */
export function getWritingEvaluation(attemptId: string) {
  return request.get({
    url: `${BASE_URL}/attempts/${attemptId}/evaluation`
  })
}

/**
 * Save writing draft (auto-save)
 * @param attemptId Attempt ID
 * @param content Draft content
 * @returns Save result
 */
export function saveWritingDraft(attemptId: string, content: {
  task1Response?: string
  task2Response?: string
}) {
  return request.post({
    url: `${BASE_URL}/attempts/${attemptId}/draft`,
    params: content
  })
}

/**
 * Get user's writing attempt history
 * @param params Pagination parameters
 * @returns List of past attempts
 */
export function getWritingHistory(params?: {
  page?: number
  size?: number
}) {
  return request.get({
    url: `${BASE_URL}/history`,
    params
  })
}