<template>
<nav id="header" class="navbar navbar-expand-lg navbar-dark bg-dark" style="position: fixed; top: 0; left: 0; right: 0; z-index: 99;">
  <div class="container-fluid">
    <a class="navbar-brand" href="#">Woods</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent"
      aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse justify-content-end" id="navbarContent">
      <ul class="navbar-nav">
        <li class="nav-item"><router-link class="nav-link" to="/">Home</router-link></li>
        <li v-if="!isAuthenticated" class="nav-item"><router-link class="nav-link" to="/register">Register</router-link></li>
        <li class="nav-item"><router-link class="nav-link" to="/contact">Contact</router-link></li>
        <li v-if="hasAuthorityAdmin" class="nav-item"><router-link class="nav-link" to="/reservations">Reservations</router-link></li>
        <li v-if="hasAuthorityAdmin" class="nav-item"><router-link class="nav-link" to="/rooms">Rooms</router-link></li>
        <li v-if="hasAuthorityAdmin" class="nav-item"><router-link class="nav-link" to="/categories">Categories</router-link></li>
        <li v-if="hasAuthorityAdmin" class="nav-item"><router-link class="nav-link" to="/guests">Guests</router-link></li>
        <li v-if="hasAuthorityAdmin" class="nav-item"><router-link class="nav-link" to="/notes">Notes</router-link></li>
        <li v-if="hasAuthorityUser" class="nav-item"><router-link class="nav-link" to="/reservations/me">Your Reservations</router-link></li>
      </ul>
      <ul class="navbar-nav ms-auto">
        <li v-if="!isAuthenticated" class="nav-item"><router-link class="nav-link" to="/login">Login</router-link></li>
        <li v-if="isAuthenticated" class="nav-item"><a class="nav-link" @click="redirectLogout">Logout</a></li>
      </ul>
    </div>
  </div>
</nav>

</template>


<script lang="ts">
import { AuthService } from '@/services/AuthService';
import { defineComponent } from 'vue';
import { useRouter } from 'vue-router';


export default defineComponent({

  data(){
    return {
      hasAuthorityAdmin: false,
      hasAuthorityUser: false,
      isAuthenticated: false,
      authService: new AuthService,
      router: useRouter()
    };
  },

  methods: {

    loadRoles() {
      this.hasAuthorityAdmin = this.authService.isAdmin();
      this.hasAuthorityUser = this.authService.isUser();
      this.isAuthenticated = this.authService.isAuthenticated();
    },

    redirectLogout(){
      this.authService.logout();
    }

  },

  created(){
    Promise.all([
      this.loadRoles()
    ])

      .catch((error) => {
        console.log(`Error loading the functions ${error}`);
      })
  }

});

</script>

<style>
.nav-link:hover {
  cursor: pointer;
}
</style>
