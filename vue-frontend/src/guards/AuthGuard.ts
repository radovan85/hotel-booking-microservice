import { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { AuthService } from '@/services/AuthService'

export function AuthGuard(to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) {
  const authService = new AuthService()

  if (authService.isAuthenticated()) {
    //console.log('Auth guard: Identification confirmed!')
    next()
  } else {
    //console.log('Auth guard: Identification failed! Redirecting to login.')
    next('/login')
  }
}
