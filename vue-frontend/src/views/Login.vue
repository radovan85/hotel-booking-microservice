<template>
    <div class="container" style="margin-top: 120px; margin-bottom: 100px; font-family: Rajdhani, sans-serif; color: #12044F; font-weight: 700;">
  <div class="text-center text-uppercase pt-3">
    <span class="text-danger" id="alertMessage" style="visibility: hidden;">Invalid username or password!</span>
    <h3 class="mb-3">Login Form</h3>
    <hr class="mx-auto w-25">
  </div>

  <div class="row mt-4">
    <div class="col-md-6 offset-md-3">
      <form class="p-4 border rounded shadow-sm" id="loginForm">
        <div class="mb-3">
          <label class="form-label">Email</label>
          <input 
            type="text" 
            class="form-control" 
            id="email"
            name="email"  
            placeholder="Email Address" 
          />
        </div>

        <div class="mb-4">
          <label class="form-label">Password</label>
          <input 
            type="password" 
            class="form-control" 
            id="password"
            name="password"  
            placeholder="Password" 
          />
        </div>

        <div class="d-grid">
          <button class="btn btn-primary btn-block" type="submit">Login</button>
        </div>
      </form>
    </div>
  </div>
</div>

</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import axios from 'axios'
import { AuthService } from '@/services/AuthService'
import type AuthenticationRequest from '@/classes/AuthenticationRequest'
import type User from '@/classes/User'

const authService = new AuthService()

onMounted(() => {
  const form = document.getElementById('loginForm') as HTMLFormElement
  const alertSpan = document.getElementById('alertMessage')

  form?.addEventListener('submit', async (e) => {
    e.preventDefault()
    const formData = new FormData(form)
    const email = formData.get('email')?.toString().trim() || ''
    const password = formData.get('password')?.toString().trim() || ''

    const payload: AuthenticationRequest = {
      username: email,
      password: password
    }

    try {
      const response = await axios.post(`${authService.getTargetUrl()}api/auth/login`, payload)
      const user = response.data as User

      if (user.authToken) {
        const token = `Bearer ${user.authToken}`
        localStorage.setItem('authToken', token)
        localStorage.setItem('currentUser', JSON.stringify(user))

        const roleId = Object.values(user.rolesIds || {})[0]
        if (roleId === 1) localStorage.setItem('role', 'ADMIN')
        if (roleId === 2) localStorage.setItem('role', 'ROLE_USER')

        if (alertSpan) alertSpan.style.visibility = 'hidden'
        window.location.reload()
      } else {
        if (alertSpan) alertSpan.style.visibility = 'visible'
      }
    } catch {
      if (alertSpan) alertSpan.style.visibility = 'visible'
    }
  })
})
</script>
