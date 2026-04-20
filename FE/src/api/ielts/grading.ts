/**
 * Grading API
 *
 * API functions for IELTS grading and band score calculation
 * Matches BE: GradingController, IeltsGradingService
 *
 * @module api/ielts/grading
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/grading'

/**
 * Calculate Listening band score from correct answers
 * @param correct Number of correct answers (0-40)
 * @returns Band score and description
 */
export function calculateListeningBand(correct: number) {
  return request.get<Api.IELTS.BandResult>({
    url: `${BASE_URL}/listening/band`,
    params: { correct }
  })
}

/**
 * Calculate Reading Academic band score
 * @param correct Number of correct answers (0-40)
 * @returns Band score and description
 */
export function calculateReadingAcademicBand(correct: number) {
  return request.get<Api.IELTS.BandResult>({
    url: `${BASE_URL}/reading/academic/band`,
    params: { correct }
  })
}

/**
 * Calculate Reading General Training band score
 * @param correct Number of correct answers (0-40)
 * @returns Band score and description
 */
export function calculateReadingGeneralBand(correct: number) {
  return request.get<Api.IELTS.BandResult>({
    url: `${BASE_URL}/reading/general/band`,
    params: { correct }
  })
}

/**
 * Grade Writing Task 2 essay with AI
 * @param essay Essay content
 * @returns AI grading with criteria scores and feedback
 */
export function gradeWritingTask2(essay: string) {
  return request.post<Api.IELTS.WritingGradingResult>({
    url: `${BASE_URL}/writing/task2`,
    params: { essay }
  })
}

/**
 * Grade Speaking with AI
 * @param transcript Speaking transcript
 * @param durationSeconds Duration of speaking in seconds
 * @returns AI grading with criteria scores and feedback
 */
export function gradeSpeaking(transcript: string, durationSeconds?: number) {
  return request.post<Api.IELTS.SpeakingGradingResult>({
    url: `${BASE_URL}/speaking`,
    params: { transcript, durationSeconds }
  })
}

/**
 * Calculate overall band score from 4 skills
 * @param listening Listening band score
 * @param reading Reading band score
 * @param writing Writing band score
 * @param speaking Speaking band score
 * @returns Overall band score
 */
export function calculateOverallBand(
  listening: number,
  reading: number,
  writing: number,
  speaking: number
) {
  return request.get<Api.IELTS.OverallBandResult>({
    url: `${BASE_URL}/overall`,
    params: { listening, reading, writing, speaking }
  })
}

/**
 * Get band score conversion table
 * @returns Conversion tables for listening and reading
 */
export function getConversionTable() {
  return request.get({
    url: `${BASE_URL}/conversion-table`
  })
}
