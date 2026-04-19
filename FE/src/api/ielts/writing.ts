/**
 * Writing API
 *
 * API functions for IELTS Writing tests and practice
 *
 * @module api/ielts/writing
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/writing'

/**
 * Get all writing tasks
 * @param params Query parameters
 * @returns List of writing tasks
 */
export function getWritingTasks(params?: { page?: number; size?: number; taskType?: string }) {
  return request.get({
    url: BASE_URL,
    params
  })
}

/**
 * Get writing task by ID
 * @param id Task ID
 * @returns Writing task details
 */
export function getWritingTask(id: number) {
  return request.get<Api.IELTS.WritingTask>({
    url: `${BASE_URL}/${id}`
  })
}

/**
 * Submit writing task response
 * @param taskId Task ID
 * @param response User's written response
 * @returns Submission result
 */
export function submitWritingResponse(taskId: number, response: string) {
  return request.post({
    url: `${BASE_URL}/${taskId}/submit`,
    params: { response }
  })
}

/**
 * Get writing task evaluation
 * @param attemptId Attempt ID
 * @returns Evaluation with band scores and feedback
 */
export function getWritingEvaluation(attemptId: number) {
  return request.get({
    url: `${BASE_URL}/attempts/${attemptId}/evaluation`
  })
}

/**
 * Save writing draft
 * @param taskId Task ID
 * @param content Draft content
 * @returns Save result
 */
export function saveWritingDraft(taskId: number, content: string) {
  return request.post({
    url: `${BASE_URL}/${taskId}/draft`,
    params: { content }
  })
}

/**
 * Get writing draft
 * @param taskId Task ID
 * @returns Draft content if exists
 */
export function getWritingDraft(taskId: number) {
  return request.get({
    url: `${BASE_URL}/${taskId}/draft`
  })
}

/**
 * Get AI feedback for writing
 * @param attemptId Attempt ID
 * @returns AI-generated feedback
 */
export function getAIWritingFeedback(attemptId: number) {
  return request.get({
    url: `${BASE_URL}/attempts/${attemptId}/ai-feedback`
  })
}
