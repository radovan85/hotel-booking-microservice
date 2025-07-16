import { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { AuthService } from '@/services/AuthService'

export async function UserGuard(to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) {
  const authService = new AuthService()

  await new Promise(resolve => setTimeout(resolve, 200)) // delay da se role sigurno učita

  if (authService.isUser()) {
    next()
  } else {
    next('/home')
  }
}
