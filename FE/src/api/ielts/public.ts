/**
 * Public API
 *
 * API functions for public endpoints (no authentication required)
 * Matches BE: PublicController
 *
 * @module api/ielts/public
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/public'

/**
 * Health check endpoint
 * @returns Service health status
 */
export function healthCheck() {
  return request.get<{
    status: string
    timestamp: Date
    version: string
    environment: string
  }>({
    url: `${BASE_URL}/health`
  })
}

/**
 * Get platform statistics
 * @returns Platform stats (users, exams, features)
 */
export function getPlatformStats() {
  return request.get<{
    totalExams: number
    totalUsers: number
    totalAttempts: number
    examsBySkill: Record<string, number>
    features: string[]
  }>({
    url: `${BASE_URL}/stats`
  })
}

/**
 * Get exam types and skills
 * @returns Available exam types and skills
 */
export function getExamTypes() {
  return request.get<{
    skills: Array<{ value: string; label: string }>
    bandLevels: Array<{ value: string; label: string }>
    examTypes: Array<{ value: string; label: string }>
  }>({
    url: `${BASE_URL}/exam-types`
  })
}

/**
 * Get sample exams (no auth required)
 * @returns List of sample exams
 */
export function getSampleExams() {
  return request.get({
    url: `${BASE_URL}/sample-exams`
  })
}

/**
 * Get IELTS Band Score Guide
 * @returns Band score descriptions (0-9)
 */
export function getBandGuide() {
  return request.get<
    Array<{
      band: number
      level: string
      description: string
    }>
  >({
    url: `${BASE_URL}/band-guide`
  })
}

/**
 * Get study tips for a specific skill
 * @param skill Skill type (listening, reading, writing, speaking)
 * @returns Study tips and resources
 */
export function getStudyTips(skill: string) {
  return request.get<{
    skill: string
    tips: string[]
    resources: string[]
  }>({
    url: `${BASE_URL}/study-tips/${skill}`
  })
}
