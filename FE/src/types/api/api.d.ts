/**
 * API Interface Type Definition Module
 *
 * Provides type definitions for all backend interfaces
 *
 * ## Main Features
 *
 * - Common types (pagination parameters, response structures, etc.)
 * - Authentication types (login, user info, etc.)
 * - System management types (users, roles, etc.)
 * - IELTS types (questions, attempts, tests, etc.)
 * - Global namespace declaration
 *
 * ## Usage Scenarios
 *
 * - API request parameter type constraints
 * - API response data type definitions
 * - Interface documentation type synchronization
 *
 * ## Notes
 *
 * - When using in .vue files, configure globals: { Api: 'readonly' } in eslint.config.mjs
 * - Uses global namespace, no import required
 *
 * ## Usage
 *
 * ```typescript
 * const params: Api.Auth.LoginParams = { userName: 'admin', password: '123456' }
 * const response: Api.Auth.UserInfo = await fetchUserInfo()
 * ```
 *
 * @module types/api/api
 * @author DOIT IELTS Team
 */

declare namespace Api {
  /** Common Types */
  namespace Common {
    /** Pagination Parameters */
    interface PaginationParams {
      /** Current page number */
      current: number
      /** Items per page */
      size: number
      /** Total items */
      total: number
    }

    /** Common Search Parameters */
    type CommonSearchParams = Pick<PaginationParams, 'current' | 'size'>

    /** Paginated Response Base Structure */
    interface PaginatedResponse<T = any> {
      records: T[]
      current: number
      size: number
      total: number
    }

    /** Enable Status */
    type EnableStatus = '1' | '2'
  }

  /** Authentication Types */
  namespace Auth {
    /** Login Parameters */
    interface LoginParams {
      email: string
      password: string
      userName?: string // for backward compatibility
    }

    /** Register Parameters */
    interface RegisterParams {
      username: string
      email: string
      password: string
      fullName?: string
    }

    /** Login Response */
    interface LoginResponse {
      accessToken: string
      refreshToken: string
      tokenType: string
      expiresIn: number
      token?: string // alias for accessToken (backward compatibility)
    }

    /** User Info */
    interface UserInfo {
      id: number
      username: string
      email: string
      fullName: string
      avatar?: string
      targetBand?: number
      currentLevel?: string
      roles: string[]
      buttons: string[]
      userId: number
      userName: string
    }
  }

  /** IELTS Types */
  namespace IELTS {
    /** Question Types */
    type QuestionType =
      | 'MULTIPLE_CHOICE'
      | 'TRUE_FALSE_NOT_GIVEN'
      | 'YES_NO_NOT_GIVEN'
      | 'MATCHING_HEADINGS'
      | 'MATCHING_INFORMATION'
      | 'MATCHING_FEATURES'
      | 'MATCHING_SENTENCE_ENDINGS'
      | 'SENTENCE_COMPLETION'
      | 'SUMMARY_COMPLETION'
      | 'NOTE_COMPLETION'
      | 'TABLE_COMPLETION'
      | 'FLOW_CHART_COMPLETION'
      | 'DIAGRAM_LABELLING'
      | 'SHORT_ANSWER'
      | 'FORM_COMPLETION'

    /** Skill Type */
    type SkillType = 'LISTENING' | 'READING' | 'WRITING' | 'SPEAKING'

    /** Difficulty Level */
    type DifficultyLevel = 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED'

    /** Question */
    interface Question {
      id: number
      questionText: string
      questionType: QuestionType
      skillType: SkillType
      difficulty: DifficultyLevel
      correctAnswer: string
      options?: string[]
      explanation?: string
      points: number
    }

    /** Listening Section */
    interface ListeningSection {
      id: number
      sectionNumber: number
      title: string
      audioUrl: string
      transcript?: string
      questions: Question[]
    }

    /** Reading Passage */
    interface ReadingPassage {
      id: number
      passageNumber: number
      title: string
      content: string
      questions: Question[]
    }

    /** Writing Task */
    interface WritingTask {
      id: number
      taskNumber: 1 | 2
      taskType: string
      prompt: string
      imageUrl?: string
      minWords: number
      maxWords: number
      criteria: string[]
    }

    /** Speaking Part */
    interface SpeakingPart {
      id: number
      partNumber: 1 | 2 | 3
      topic: string
      questions: string[]
      preparationTime?: number
      speakingTime: number
    }

    /** Test Attempt */
    interface TestAttempt {
      id: number
      userId: number
      testId: number
      skillType: SkillType
      startTime: string
      endTime?: string
      score?: number
      bandScore?: number
      status: 'IN_PROGRESS' | 'COMPLETED' | 'ABANDONED'
    }

    /** User Answer */
    interface UserAnswer {
      questionId: number
      answer: string
      isCorrect?: boolean
      score?: number
    }

    /** Study Plan */
    interface StudyPlan {
      id: number
      userId: number
      targetBand: number
      targetDate: string
      weeklyHours: number
      focusAreas: SkillType[]
      dailyTasks: DailyTask[]
    }

    /** Daily Task */
    interface DailyTask {
      id: number
      date: string
      skillType: SkillType
      taskType: string
      duration: number
      completed: boolean
    }

    /** Dashboard Stats */
    interface DashboardStats {
      totalPracticeTime: number
      testsCompleted: number
      averageBandScore: number
      skillScores: {
        listening: number
        reading: number
        writing: number
        speaking: number
      }
      recentActivity: RecentActivity[]
      weeklyProgress: WeeklyProgress[]
    }

    /** Recent Activity */
    interface RecentActivity {
      id: number
      type: string
      skillType: SkillType
      score: number
      date: string
    }

    /** Weekly Progress */
    interface WeeklyProgress {
      week: string
      listening: number
      reading: number
      writing: number
      speaking: number
    }

    /** Word Definition from Dictionary API */
    interface WordDefinition {
      word: string
      phonetic?: string
      phonetics?: Array<{
        text?: string
        audio?: string
      }>
      meanings: Array<{
        partOfSpeech: string
        definitions: Array<{
          definition: string
          example?: string
          synonyms?: string[]
          antonyms?: string[]
        }>
        synonyms?: string[]
        antonyms?: string[]
      }>
    }

    /** Vocabulary Resource */
    interface VocabularyResource {
      source: string
      band: string
      words: Array<{
        word: string
        partOfSpeech: string
        definition: string
        example: string
      }>
    }

    /** Band Score Result */
    interface BandResult {
      totalQuestions: number
      correctAnswers: number
      bandScore: number
      description: string
    }

    /** Writing Grading Result */
    interface WritingGradingResult {
      wordCount: number
      overallBand: number
      criteriaScores: {
        taskAchievement: number
        coherenceCohesion: number
        lexicalResource: number
        grammaticalRange: number
      }
      feedback: string
      strengths: string[]
      improvements: string[]
    }

    /** Speaking Grading Result */
    interface SpeakingGradingResult {
      wordCount: number
      wordsPerMinute: number
      overallBand: number
      criteriaScores: {
        fluencyCoherence: number
        lexicalResource: number
        grammaticalRange: number
        pronunciation: number
      }
      feedback: string
      fillerWords: {
        count: number
        fillers: string[]
      }
    }

    /** Overall Band Result */
    interface OverallBandResult {
      listening: number
      reading: number
      writing: number
      speaking: number
      overall: number
      description: string
    }
  }

  /** System Management Types */
  namespace SystemManage {
    /** User List */
    type UserList = Api.Common.PaginatedResponse<UserListItem>

    /** User List Item */
    interface UserListItem {
      id: number
      avatar: string
      status: string
      userName: string
      userGender: string
      nickName: string
      userPhone: string
      userEmail: string
      userRoles: string[]
      createBy: string
      createTime: string
      updateBy: string
      updateTime: string
    }

    /** 用户搜索参数 */
    type UserSearchParams = Partial<
      Pick<UserListItem, 'id' | 'userName' | 'userGender' | 'userPhone' | 'userEmail' | 'status'> &
        Api.Common.CommonSearchParams
    >

    /** 角色列表 */
    type RoleList = Api.Common.PaginatedResponse<RoleListItem>

    /** 角色列表项 */
    interface RoleListItem {
      roleId: number
      roleName: string
      roleCode: string
      description: string
      enabled: boolean
      createTime: string
    }

    /** 角色搜索参数 */
    type RoleSearchParams = Partial<
      Pick<RoleListItem, 'roleId' | 'roleName' | 'roleCode' | 'description' | 'enabled'> &
        Api.Common.CommonSearchParams & {
          startTime: string | null
          endTime: string | null
        }
    >
  }
}
