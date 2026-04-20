/**
 * Attempts API
 *
 * API functions for managing test attempts
 * Note: Most attempt operations are in skill-specific controllers
 *
 * @module api/ielts/attempts
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/attempts'

/**
 * Get user's all test attempts
 * @param params Query parameters
 * @returns Paginated list of attempts
 */
export function getAllAttempts(params?: {
  page?: number
  size?: number
  skill?: 'LISTENING' | 'READING' | 'WRITING' | 'SPEAKING'
  status?: 'IN_PROGRESS' | 'COMPLETED' | 'ABANDONED'
}) {
  return request.get({
    url: BASE_URL,
    params
  })
}

/**
 * Get attempt by ID
 * @param attemptId Attempt ID
 * @returns Attempt details
 */
export function getAttempt(attemptId: string) {
  return request.get({
    url: `${BASE_URL}/${attemptId}`
  })
}

/**
 * Abandon an in-progress attempt
 * @param attemptId Attempt ID
 * @returns Updated attempt
 */
export function abandonAttempt(attemptId: string) {
  return request.put({
    url: `${BASE_URL}/${attemptId}/abandon`
  })
}