/**
 * API Status Codes
 */
export enum ApiStatus {
  success = 200, // Success
  error = 400, // Error
  unauthorized = 401, // Unauthorized
  forbidden = 403, // Forbidden
  notFound = 404, // Not found
  methodNotAllowed = 405, // Method not allowed
  requestTimeout = 408, // Request timeout
  internalServerError = 500, // Server error
  notImplemented = 501, // Not implemented
  badGateway = 502, // Bad gateway
  serviceUnavailable = 503, // Service unavailable
  gatewayTimeout = 504, // Gateway timeout
  httpVersionNotSupported = 505 // HTTP version not supported
}
