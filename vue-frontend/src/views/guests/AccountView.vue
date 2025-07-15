<template>
  <div
    class="container"
    style="
      font-family: Rajdhani, sans-serif;
      color: #12044f;
      font-weight: 700;
      margin-bottom: 100px;
      margin-top: 120px;
    "
  >
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
      <router-link class="btn btn-secondary border-dark" to="/home"
        >Home Page</router-link
      >
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
  data() {
    return {
      guest: new Guest(),
      user: new User(),
      guestService: new GuestService(),
      userService: new UserService(),
    };
  },

  methods: {
    retrieveUser(): Promise<any> {
      return new Promise(() => {
        this.userService.getAuthUser().then((response) => {
          this.user = response.data;
        });
      });
    },

    retrieveGuest(): Promise<any> {
      return new Promise(() => {
        this.guestService.getCurrentGuest().then((response) => {
          this.guest = response.data;
        });
      });
    },
  },

  created() {
    Promise.all([this.retrieveGuest(), this.retrieveUser()]).catch((error) => {
      console.log(`Error loading functions  ${error}`);
    });
  },
});
</script>
