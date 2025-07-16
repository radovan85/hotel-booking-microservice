<template>
  <div v-if="isReady" class="container" style="margin-top: 100px; font-family: Rajdhani, sans-serif">
    <h2 class="text-center text-uppercase mb-4 fw-bold">Guests List</h2>

    <div class="table-responsive shadow-sm">
      <table class="table table-hover table-bordered align-middle text-center">
        <thead class="table-dark">
          <tr>
            <th scope="col">Guest Id</th>
            <th scope="col">Full Name</th>
            <th scope="col">Email</th>
            <th scope="col">Enabled</th>
            <th scope="col">Actions</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="tempGuest in paginatedGuests" :key="tempGuest.guestId">
            <tr>
              <td v-html="tempGuest.guestId"></td>
              <td v-html="getUserById(tempGuest.userId).firstName + ' ' + getUserById(tempGuest.userId).lastName"></td>
              <td v-html="getUserById(tempGuest.userId).email"></td>
              <td>
                <span :class="getUserById(tempGuest.userId).enabled === 1 ? 'badge bg-success' : 'badge bg-danger'">
                  {{ getUserById(tempGuest.userId).enabled === 1 ? 'Yes' : 'No' }}
                </span>
              </td>
              <td>
                <button class="btn btn-sm btn-primary me-1" @click="$router.push(`/guests/guestDetails/${tempGuest.guestId}`)">
                  Details
                </button>
              </td>
            </tr>
          </template>
        </tbody>
      </table>

      <div class="pagination" v-if="guestList.length > 0">
        <button @click="prevPage" :disabled="currentPage === 1">Previous</button>
        <span>Page {{ currentPage }} of {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages">Next</button>
      </div>
    </div>
  </div>

  <div v-else class="text-center mt-5">
    <div class="spinner-border text-primary mt-5" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script lang="ts">
import User from "@/classes/User";
import GuestService from "@/services/GuestService";
import UserService from "@/services/UserService";
import { defineComponent } from "vue";
import { useRouter } from "vue-router";

export default defineComponent({
  name: "GuestListView",

  data() {
    return {
      isReady: false,
      guestService: new GuestService(),
      userService: new UserService(),
      guestList: [] as any[],
      paginatedGuests: [] as any[],
      userList: [] as User[],
      pageSize: 6,
      currentPage: 1,
      totalPages: 1,
      router: useRouter()
    };
  },

  methods: {
    listAllGuests(): Promise<void> {
      return this.guestService.collectAllGuests()
        .then((response) => {
          this.guestList = response.data;
          this.totalPages = Math.ceil(this.guestList.length / this.pageSize);
          this.setPage(1);
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

    setPage(page: number) {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page;
      this.paginatedGuests = this.guestList.slice(
        (page - 1) * this.pageSize,
        page * this.pageSize
      );
    },

    nextPage() {
      this.setPage(this.currentPage + 1);
    },

    prevPage() {
      this.setPage(this.currentPage - 1);
    }
  },

  created() {
    Promise.all([
      this.listAllGuests(),
      this.listAllUsers()
    ])
    .then(() => {
      this.isReady = true;
    })
    .catch((error) => {
      console.error("Error loading guest list:", error);
    });
  }
});
</script>

<style>
.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 20px;
  padding-right: 20px;
}

.pagination button {
  background-color: #5d3b99;
  color: #fff;
  border: none;
  padding: 8px 16px;
  margin-left: 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.pagination button:hover {
  background-color: #7d5ba6;
}
</style>
