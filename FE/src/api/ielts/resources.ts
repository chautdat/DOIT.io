/**
 * IELTS Resources API
 *
 * API functions for IELTS learning resources (vocabulary, topics, band descriptors)
 * Matches BE: IeltsResourceController
 *
 * @module api/ielts/resources
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/ielts'

/**
 * Get IELTS overview information
 * @returns IELTS exam overview
 */
export function getIeltsOverview() {
  return request.get({
    url: `${BASE_URL}/overview`
  })
}

/**
 * Get IELTS vocabulary by band level
 * @param band Band level (6-7 or 7-8)
 * @returns Cambridge Academic Word List vocabulary
 */
export function getIeltsVocabulary(band?: string) {
  return request.get<Api.IELTS.VocabularyResource>({
    url: `${BASE_URL}/vocabulary`,
    params: { band }
  })
}

/**
 * Get IELTS Speaking topics
 * @param part Speaking part (1, 2, or 3)
 * @returns List of speaking topics
 */
export function getSpeakingTopicsResource(part?: number) {
  return request.get({
    url: `${BASE_URL}/speaking/topics`,
    params: { part }
  })
}

/**
 * Get random Speaking Part 2 cue card
 * @returns Random cue card topic
 */
export function getRandomCueCard() {
  return request.get({
    url: `${BASE_URL}/speaking/cue-card/random`
  })
}

/**
 * Get IELTS Writing topics
 * @param taskType Task type (task1 or task2)
 * @returns List of writing topics
 */
export function getWritingTopicsResource(taskType?: string) {
  return request.get({
    url: `${BASE_URL}/writing/topics`,
    params: { taskType }
  })
}

/**
 * Get random Writing topic
 * @param taskType Task type (task1 or task2)
 * @returns Random writing topic
 */
export function getRandomWritingTopic(taskType?: string) {
  return request.get({
    url: `${BASE_URL}/writing/topic/random`,
    params: { taskType }
  })
}

/**
 * Get IELTS Reading question types
 * @returns List of reading question types with descriptions
 */
export function getReadingQuestionTypes() {
  return request.get({
    url: `${BASE_URL}/reading/question-types`
  })
}

/**
 * Get IELTS Listening section information (resource)
 * @returns List of listening sections with descriptions
 */
export function getListeningSectionsInfo() {
  return request.get({
    url: `${BASE_URL}/listening/sections`
  })
}

/**
 * Get IELTS Band Descriptors
 * @param skill Skill type (writing or speaking)
 * @returns Band descriptors for the skill
 */
export function getBandDescriptors(skill?: string) {
  return request.get({
    url: `${BASE_URL}/band-descriptors`,
    params: { skill }
  })
}
