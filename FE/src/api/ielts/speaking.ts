/**
 * Speaking API
 *
 * API functions for IELTS Speaking tests and practice
 *
 * @module api/ielts/speaking
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/speaking'

/**
 * Get all speaking topics
 * @param params Query parameters
 * @returns List of speaking topics
 */
export function getSpeakingTopics(params?: { page?: number; size?: number; part?: number }) {
  return request.get({
    url: BASE_URL,
    params
  })
}

/**
 * Get speaking topic by ID
 * @param id Topic ID
 * @returns Speaking topic details
 */
export function getSpeakingTopic(id: number) {
  return request.get<Api.IELTS.SpeakingPart>({
    url: `${BASE_URL}/${id}`
  })
}

/**
 * Get speaking questions for a part
 * @param partId Part ID
 * @returns List of questions
 */
export function getSpeakingQuestions(partId: number) {
  return request.get({
    url: `${BASE_URL}/${partId}/questions`
  })
}

/**
 * Upload speaking recording
 * @param topicId Topic ID
 * @param audioFile Audio file blob
 * @returns Upload result
 */
export function uploadSpeakingRecording(topicId: number, audioFile: Blob) {
  const formData = new FormData()
  formData.append('audio', audioFile, 'recording.webm')
  formData.append('topicId', topicId.toString())

  return request.post({
    url: `${BASE_URL}/${topicId}/upload`,
    params: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * Submit speaking test
 * @param testId Test ID
 * @param recordings Recording IDs for each part
 * @returns Submission result
 */
export function submitSpeakingTest(testId: number, recordings: number[]) {
  return request.post({
    url: `${BASE_URL}/${testId}/submit`,
    params: { recordings }
  })
}

/**
 * Get speaking evaluation
 * @param attemptId Attempt ID
 * @returns Evaluation with band scores and feedback
 */
export function getSpeakingEvaluation(attemptId: number) {
  return request.get({
    url: `${BASE_URL}/attempts/${attemptId}/evaluation`
  })
}

/**
 * Get AI speaking feedback
 * @param recordingId Recording ID
 * @returns AI-generated feedback
 */
export function getAISpeakingFeedback(recordingId: number) {
  return request.get({
    url: `${BASE_URL}/recordings/${recordingId}/ai-feedback`
  })
}

/**
 * Start speaking practice session
 * @param partNumber Part number (1, 2, or 3)
 * @returns Practice session info with random topic
 */
export function startSpeakingPractice(partNumber: number) {
  return request.post({
    url: `${BASE_URL}/practice/start`,
    params: { partNumber }
  })
}
