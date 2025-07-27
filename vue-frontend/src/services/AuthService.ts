import axios from 'axios'
import router from '@/router'
import type AuthenticationRequest  from '@/classes/AuthenticationRequest'
import type User from '@/classes/User'

// 🔐 Axios interceptori
axios.interceptors.request.use(config => {
  const authToken = localStorage.getItem('authToken')
  if (authToken) {
    config.headers.Authorization = `${authToken}`
  }
  return config
})

axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.clear()
      window.location.reload()
    }
    return Promise.reject(error)
  }
)

axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 451) {
      alert('Account suspended')
      localStorage.clear()
      window.location.reload()
    }
    return Promise.reject(error)
  }
)

// 🎯 AuthService definicija
export class AuthService {
  private authRequest: AuthenticationRequest = {} as AuthenticationRequest
  private authUser: User = {} as User
  private targetUrl: string = 'http://localhost:8090/'

  isAdmin(): boolean {
    const decoded = this.decodeToken()
    return decoded?.roles?.includes('ROLE_ADMIN') ?? false
  }

  isUser(): boolean {
    const decoded = this.decodeToken()
    return decoded?.roles?.includes('ROLE_USER') ?? false
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('authToken')
  }

  setAuthRequest(req: AuthenticationRequest) {
    this.authRequest = req
  }

  logout() {
    localStorage.clear()
    window.location.reload();
  }

  redirectRegister() {
    router.push({ path: '/registration' })
  }

  getTargetUrl(): string {
    return this.targetUrl
  }

  decodeToken(): any | null {
    const token = localStorage.getItem('authToken')
    if (token) {
      try {
        const base64Url = token.split('.')[1]
        const base64 = decodeURIComponent(escape(atob(base64Url)))
        return JSON.parse(base64)
      } catch {
        return null
      }
    }
    return null
  }
}
