/**
 * Questions API
 *
 * API functions for managing IELTS questions
 *
 * @module api/ielts/questions
 * @author DOIT IELTS Team
 */

import request from '@/utils/http'

const BASE_URL = '/api/v1/questions'

/**
 * Get questions with filters
 * @param params Query parameters
 * @returns Paginated list of questions
 */
export function getQuestions(params?: {
  page?: number
  size?: number
  skillType?: Api.IELTS.SkillType
  questionType?: Api.IELTS.QuestionType
  difficulty?: Api.IELTS.DifficultyLevel
}) {
  return request.get<Api.Common.PaginatedResponse<Api.IELTS.Question>>({
    url: BASE_URL,
    params
  })
}

/**
 * Get question by ID
 * @param id Question ID
 * @returns Question details
 */
export function getQuestion(id: number) {
  return request.get<Api.IELTS.Question>({
    url: `${BASE_URL}/${id}`
  })
}

/**
 * Create new question (Admin)
 * @param question Question data
 * @returns Created question
 */
export function createQuestion(question: Partial<Api.IELTS.Question>) {
  return request.post<Api.IELTS.Question>({
    url: BASE_URL,
    params: question
  })
}

/**
 * Update question (Admin)
 * @param id Question ID
 * @param question Updated question data
 * @returns Updated question
 */
export function updateQuestion(id: number, question: Partial<Api.IELTS.Question>) {
  return request.put<Api.IELTS.Question>({
    url: `${BASE_URL}/${id}`,
    params: question
  })
}

/**
 * Delete question (Admin)
 * @param id Question ID
 * @returns Deletion result
 */
export function deleteQuestion(id: number) {
  return request.del({
    url: `${BASE_URL}/${id}`
  })
}

/**
 * Get random questions for practice
 * @param params Filter parameters
 * @returns Random questions
 */
export function getRandomQuestions(params: {
  skillType: Api.IELTS.SkillType
  count: number
  difficulty?: Api.IELTS.DifficultyLevel
}) {
  return request.get<Api.IELTS.Question[]>({
    url: `${BASE_URL}/random`,
    params
  })
}

/**
 * Import questions from file (Admin)
 * @param file Excel or JSON file
 * @returns Import result
 */
export function importQuestions(file: File) {
  const formData = new FormData()
  formData.append('file', file)

  return request.post({
    url: `${BASE_URL}/import`,
    params: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * Export questions (Admin)
 * @param params Filter parameters
 * @returns Download URL
 */
export function exportQuestions(params?: {
  skillType?: Api.IELTS.SkillType
  questionType?: Api.IELTS.QuestionType
}) {
  return request.get({
    url: `${BASE_URL}/export`,
    params,
    responseType: 'blob'
  })
}
