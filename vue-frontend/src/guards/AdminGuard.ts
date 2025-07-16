import { NavigationGuardNext, RouteLocationNormalized } from 'vue-router'
import { AuthService } from '@/services/AuthService'

export async function AdminGuard(to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext) {
  const authService = new AuthService()

  // Kratko kašnjenje kao u Angular varijanti (ako čekaš neko asinhrono učitavanje)
  await new Promise(resolve => setTimeout(resolve, 200))

  if (authService.isAdmin()) {
    next()
  } else {
    next('/home')
  }
}
