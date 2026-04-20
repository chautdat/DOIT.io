/**
 * Vocabulary API
 *
 * API functions for vocabulary learning and dictionary lookup
 * Matches BE: VocabularyController
 *
 * @module api/ielts/vocabulary
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/vocabulary'

/**
 * Get word definition from dictionary API
 * @param word Word to lookup
 * @returns Word definition, pronunciation, examples
 */
export function getWordDefinition(word: string) {
  return request.get<Api.IELTS.WordDefinition>({
    url: `${BASE_URL}/definition/${word}`
  })
}

/**
 * Get synonyms for a word
 * @param word Word to find synonyms for
 * @returns List of synonyms
 */
export function getWordSynonyms(word: string) {
  return request.get<string[]>({
    url: `${BASE_URL}/synonyms/${word}`
  })
}

/**
 * Get antonyms for a word
 * @param word Word to find antonyms for
 * @returns List of antonyms
 */
export function getWordAntonyms(word: string) {
  return request.get<string[]>({
    url: `${BASE_URL}/antonyms/${word}`
  })
}

/**
 * Get pronunciation audio URL for a word
 * @param word Word to get pronunciation for
 * @returns Audio URL
 */
export function getPronunciationUrl(word: string) {
  return request.get<{ audioUrl: string }>({
    url: `${BASE_URL}/pronunciation/${word}`
  })
}
