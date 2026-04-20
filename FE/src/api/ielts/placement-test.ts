/**
 * Placement Test API
 *
 * API functions for IELTS placement/level assessment tests
 * Matches BE: PlacementTestController
 *
 * @module api/ielts/placement-test
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/placement'

/**
 * Get placement test info
 * @returns Placement test information and status
 */
export function getPlacementTestInfo() {
  return request.get({
    url: `${BASE_URL}/info`
  })
}

/**
 * Start placement test
 * @returns Attempt ID
 */
export function startPlacementTest() {
  return request.post({
    url: `${BASE_URL}/start`
  })
}

/**
 * Submit placement test
 * @param submission Placement test answers
 * @returns Placement result with estimated level
 */
export function submitPlacementTest(submission: {
  answers: Record<string, string>
  timeSpent?: number
}) {
  return request.post({
    url: `${BASE_URL}/submit`,
    params: submission
  })
}

/**
 * Get placement test result
 * @returns Detailed result with skill breakdown
 */
export function getPlacementResult() {
  return request.get({
    url: `${BASE_URL}/result`
  })
}