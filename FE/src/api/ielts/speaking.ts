/**
 * Speaking API
 *
 * API functions for IELTS Speaking tests and practice
 * Matches BE: SpeakingController
 *
 * @module api/ielts/speaking
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/speaking'

/**
 * Get all speaking exams
 * @param params Query parameters
 * @returns List of speaking exams
 */
export function getSpeakingExams(params?: {
  bandLevel?: string
  examType?: string
}) {
  return request.get({
    url: `${BASE_URL}/exams`,
    params
  })
}

/**
 * Get speaking exam by ID
 * @param examId Exam ID
 * @returns Speaking exam details with parts
 */
export function getSpeakingExam(examId: string) {
  return request.get({
    url: `${BASE_URL}/exams/${examId}`
  })
}

/**
 * Start a speaking test attempt
 * @param examId Exam ID
 * @returns New attempt info
 */
export function startSpeakingAttempt(examId: string) {
  return request.post({
    url: `${BASE_URL}/exams/${examId}/start`
  })
}

/**
 * Upload speaking recording
 * @param attemptId Attempt ID
 * @param part Part number (1, 2, or 3)
 * @param audioFile Audio file blob
 * @returns Upload result
 */
export function uploadSpeakingRecording(attemptId: string, part: number, audioFile: Blob) {
  const formData = new FormData()
  formData.append('audio', audioFile, 'recording.webm')
  formData.append('part', part.toString())

  return request.post({
    url: `${BASE_URL}/attempts/${attemptId}/upload`,
    params: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * Submit speaking test
 * @param attemptId Attempt ID
 * @param transcript Optional transcript text
 * @returns Submission result
 */
export function submitSpeakingTest(attemptId: string, transcript?: string) {
  return request.post({
    url: `${BASE_URL}/attempts/${attemptId}/submit`,
    params: { transcript }
  })
}

/**
 * Get speaking evaluation with AI grading
 * @param attemptId Attempt ID
 * @returns Evaluation with band scores and feedback
 */
export function getSpeakingEvaluation(attemptId: string) {
  return request.get({
    url: `${BASE_URL}/attempts/${attemptId}/evaluation`
  })
}

/**
 * Get user's speaking attempt history
 * @param params Pagination parameters
 * @returns List of past attempts
 */
export function getSpeakingHistory(params?: {
  page?: number
  size?: number
}) {
  return request.get({
    url: `${BASE_URL}/history`,
    params
  })
}