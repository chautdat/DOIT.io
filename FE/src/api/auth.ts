import request from '@/utils/http'

/**
 * Login
 * @param params Login parameters
 * @returns Login response
 */
export function fetchLogin(params: Api.Auth.LoginParams) {
  return request.post<Api.Auth.LoginResponse>({
    url: '/api/v1/auth/login',
    params
    // showSuccessMessage: true // Show success message
    // showErrorMessage: false // Don't show error message
  })
}

/**
 * Register new user
 * @param params Registration parameters
 * @returns Registration response
 */
export function fetchRegister(params: Api.Auth.RegisterParams) {
  return request.post<Api.Auth.LoginResponse>({
    url: '/api/v1/auth/register',
    params
  })
}

/**
 * Get user info
 * @returns User info
 */
export function fetchGetUserInfo() {
  return request.get<Api.Auth.UserInfo>({
    url: '/api/v1/users/me'
    // Custom request headers
    // headers: {
    //   'X-Custom-Header': 'your-custom-value'
    // }
  })
}

/**
 * Refresh token
 * @param refreshToken Refresh token
 * @returns New token pair
 */
export function fetchRefreshToken(refreshToken: string) {
  return request.post<Api.Auth.LoginResponse>({
    url: '/api/v1/auth/refresh',
    params: { refreshToken }
  })
}

/**
 * Logout
 * @returns Logout response
 */
export function fetchLogout() {
  return request.post({
    url: '/api/v1/auth/logout'
  })
}
