/**
 * HTTP Error Handling Module
 *
 * Provides unified HTTP request error handling mechanism
 *
 * ## Main Features
 *
 * - Custom HttpError class, encapsulating error info, status code, timestamp, etc.
 * - Error interception and transformation, converting Axios errors to standard HttpError
 * - Error message internationalization, returning multilingual error prompts based on status code
 * - Error logging for issue tracking and debugging
 * - Unified display of error and success messages
 * - Type guard functions for determining error types
 *
 * ## Use Cases
 *
 * - Unified error handling in HTTP request interceptors
 * - Catching and handling specific errors in business code
 * - Error log collection and reporting
 *
 * @module utils/http/error
 * @author Art Design Pro Team
 */
import { AxiosError } from 'axios'
import { ApiStatus } from './status'
import { $t } from '@/locales'

// Error response interface
export interface ErrorResponse {
  /** Error status code */
  code: number
  /** Error message */
  msg: string
  /** Error additional data */
  data?: unknown
}

// Error log data interface
export interface ErrorLogData {
  /** Error status code */
  code: number
  /** Error message */
  message: string
  /** Error additional data */
  data?: unknown
  /** Error timestamp */
  timestamp: string
  /** Request URL */
  url?: string
  /** Request method */
  method?: string
  /** Error stack trace */
  stack?: string
}

// Custom HttpError class
export class HttpError extends Error {
  public readonly code: number
  public readonly data?: unknown
  public readonly timestamp: string
  public readonly url?: string
  public readonly method?: string

  constructor(
    message: string,
    code: number,
    options?: {
      data?: unknown
      url?: string
      method?: string
    }
  ) {
    super(message)
    this.name = 'HttpError'
    this.code = code
    this.data = options?.data
    this.timestamp = new Date().toISOString()
    this.url = options?.url
    this.method = options?.method
  }

  public toLogData(): ErrorLogData {
    return {
      code: this.code,
      message: this.message,
      data: this.data,
      timestamp: this.timestamp,
      url: this.url,
      method: this.method,
      stack: this.stack
    }
  }
}

/**
 * Get error message
 * @param status Error status code
 * @returns Error message
 */
const getErrorMessage = (status: number): string => {
  const errorMap: Record<number, string> = {
    [ApiStatus.unauthorized]: 'httpMsg.unauthorized',
    [ApiStatus.forbidden]: 'httpMsg.forbidden',
    [ApiStatus.notFound]: 'httpMsg.notFound',
    [ApiStatus.methodNotAllowed]: 'httpMsg.methodNotAllowed',
    [ApiStatus.requestTimeout]: 'httpMsg.requestTimeout',
    [ApiStatus.internalServerError]: 'httpMsg.internalServerError',
    [ApiStatus.badGateway]: 'httpMsg.badGateway',
    [ApiStatus.serviceUnavailable]: 'httpMsg.serviceUnavailable',
    [ApiStatus.gatewayTimeout]: 'httpMsg.gatewayTimeout'
  }

  return $t(errorMap[status] || 'httpMsg.internalServerError')
}

/**
 * Handle error
 * @param error Error object
 * @returns Error object
 */
export function handleError(error: AxiosError<ErrorResponse>): never {
  // Handle cancelled requests
  if (error.code === 'ERR_CANCELED') {
    console.warn('Request cancelled:', error.message)
    throw new HttpError($t('httpMsg.requestCancelled'), ApiStatus.error)
  }

  const statusCode = error.response?.status
  const errorMessage = error.response?.data?.msg || error.message
  const requestConfig = error.config

  // Handle network errors
  if (!error.response) {
    throw new HttpError($t('httpMsg.networkError'), ApiStatus.error, {
      url: requestConfig?.url,
      method: requestConfig?.method?.toUpperCase()
    })
  }

  // Handle HTTP status code errors
  const message = statusCode
    ? getErrorMessage(statusCode)
    : errorMessage || $t('httpMsg.requestFailed')
  throw new HttpError(message, statusCode || ApiStatus.error, {
    data: error.response.data,
    url: requestConfig?.url,
    method: requestConfig?.method?.toUpperCase()
  })
}

/**
 * Show error message
 * @param error Error object
 * @param showMessage Whether to show error message
 */
export function showError(error: HttpError, showMessage: boolean = true): void {
  if (showMessage) {
    ElMessage.error(error.message)
  }
  // Log error
  console.error('[HTTP Error]', error.toLogData())
}

/**
 * Show success message
 * @param message Success message
 * @param showMessage Whether to show message
 */
export function showSuccess(message: string, showMessage: boolean = true): void {
  if (showMessage) {
    ElMessage.success(message)
  }
}

/**
 * Check if error is HttpError type
 * @param error Error object
 * @returns Whether it's HttpError type
 */
export const isHttpError = (error: unknown): error is HttpError => {
  return error instanceof HttpError
}
