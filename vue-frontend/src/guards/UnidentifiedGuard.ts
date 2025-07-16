import { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { AuthService } from '@/services/AuthService'

export function UnidentifiedGuard(to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) {
  const authService = new AuthService()

  if (authService.isAuthenticated()) {
    // console.log('UnidentifiedGuard: Already identified, redirecting to home.')
    next('/')
  } else {
    // console.log('UnidentifiedGuard: Not identified, access granted.')
    next()
  }
}
