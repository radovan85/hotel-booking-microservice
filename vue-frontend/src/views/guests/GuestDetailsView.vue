<template>
  <div v-if="isReady" class="container" style="font-family: Rajdhani, sans-serif; color: #12044F; font-weight: 700; margin-top: 120px; margin-bottom: 100px;">
    <div class="text-center mb-4">
      <h2 class="text-uppercase fw-bold">Guest Details</h2>
      <hr class="w-25 mx-auto border-3 border-dark" />
    </div>

    <div class="table-responsive shadow-sm">
      <table class="table table-bordered align-middle table-hover">
        <tbody class="table-light text-center">
          <tr>
            <th scope="row" class="text-uppercase">Guest Id</th>
            <td v-html="guest.guestId"></td>
          </tr>
          <tr>
            <th scope="row" class="text-uppercase">First Name</th>
            <td v-html="getUserById(guest.userId).firstName"></td>
          </tr>
          <tr>
            <th scope="row" class="text-uppercase">Last Name</th>
            <td v-html="getUserById(guest.userId).lastName"></td>
          </tr>
          <tr>
            <th scope="row" class="text-uppercase">Email</th>
            <td v-html="getUserById(guest.userId).email"></td>
          </tr>
          <tr>
            <th scope="row" class="text-uppercase">ID Number</th>
            <td v-html="guest.idNumber"></td>
          </tr>
          <tr>
            <th scope="row" class="text-uppercase">Phone</th>
            <td v-html="guest.phoneNumber"></td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="d-flex justify-content-center gap-3 mt-5">
      <button class="btn btn-info border-dark px-4 shadow-sm" @click="goBack()">← Back</button>
      <button class="btn btn-danger border-dark px-4 shadow-sm" @click="deleteGuest(guest.guestId)">Remove Guest</button>
    </div>
  </div>

  <div v-else class="text-center mt-5">
    <div class="spinner-border text-primary mt-5" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script lang="ts">
import Guest from '@/classes/Guest';
import User from '@/classes/User';
import GuestService from '@/services/GuestService';
import UserService from '@/services/UserService';
import { defineComponent } from 'vue';
import { useRoute, useRouter } from 'vue-router';

export default defineComponent({
  name: "GuestDetailsView",

  data() {
    return {
      isReady: false,
      guest: new Guest(),
      userList: [] as User[],
      guestService: new GuestService(),
      userService: new UserService(),
      route: useRoute(),
      router: useRouter()
    };
  },

  methods: {
    getGuestDetails(guestId: any): Promise<void> {
      return this.guestService.getGuestDetails(guestId)
        .then((response) => {
          this.guest = response.data;
        });
    },

    listAllUsers(): Promise<void> {
      return this.userService.collectAllUsers()
        .then((response) => {
          this.userList = response.data;
        });
    },

    getUserById(userId: any): User {
      return this.userList.find((user) => user.id === userId) || new User();
    },

    goBack() {
      this.router.back();
    },

    deleteGuest(guestId: any): void {
      if (confirm("Remove this guest?\nIt will affect all related data!")) {
        this.guestService.deleteGuest(guestId)
          .then(() => {
            this.guestService.redirectAllGuests();
          })
          .catch((error) => {
            alert("Failed!");
            console.error("Error deleting guest:", error);
          });
      }
    }
  },

  created() {
    Promise.all([
      this.listAllUsers(),
      this.getGuestDetails(this.route.params.guestId)
    ])
    .then(() => {
      this.isReady = true;
    })
    .catch((error) => {
      console.error("Error loading guest details:", error);
    });
  }
});
</script>
