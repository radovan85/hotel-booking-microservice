<template>
  <div v-if="isReady" class="container" style="font-family: Rajdhani, sans-serif; color: #12044f; font-weight: 700; margin-bottom: 100px; margin-top: 120px;">
    <div class="text-center">
      <h2>Account Information</h2>
    </div>

    <table class="table table-bordered table-dark mt-5">
      <tbody>
        <tr>
          <td class="text-center">Guest Id</td>
          <td class="text-center" v-html="guest.guestId"></td>
        </tr>
        <tr>
          <td class="text-center">First Name</td>
          <td class="text-center" v-html="user.firstName"></td>
        </tr>
        <tr>
          <td class="text-center">Last Name</td>
          <td class="text-center" v-html="user.lastName"></td>
        </tr>
        <tr>
          <td class="text-center">Email</td>
          <td class="text-center" v-html="user.email"></td>
        </tr>
        <tr>
          <td class="text-center">Id Number</td>
          <td class="text-center" v-html="guest.idNumber"></td>
        </tr>
        <tr>
          <td class="text-center">Phone</td>
          <td class="text-center" v-html="guest.phoneNumber"></td>
        </tr>
      </tbody>
    </table>

    <div class="d-flex justify-content-center mt-5">
      <router-link class="btn btn-secondary border-dark" to="/home">Home Page</router-link>
    </div>
  </div>

  <div v-else class="text-center mt-5">
    <div class="spinner-border text-primary mt-5" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script lang="ts">
import Guest from "@/classes/Guest";
import User from "@/classes/User";
import GuestService from "@/services/GuestService";
import UserService from "@/services/UserService";
import { defineComponent } from "vue";

export default defineComponent({
  name: "AccountView",

  data() {
    return {
      isReady: false,
      guest: new Guest(),
      user: new User(),
      guestService: new GuestService(),
      userService: new UserService(),
    };
  },

  methods: {
    retrieveUser(): Promise<void> {
      return this.userService.getAuthUser()
        .then((response) => {
          this.user = response.data;
        });
    },

    retrieveGuest(): Promise<void> {
      return this.guestService.getCurrentGuest()
        .then((response) => {
          this.guest = response.data;
        });
    },
  },

  created() {
    Promise.all([
      this.retrieveGuest(),
      this.retrieveUser()
    ])
    .then(() => {
      this.isReady = true;
    })
    .catch((error) => {
      console.error("Failed to load account data:", error);
    });
  },
});
</script>
