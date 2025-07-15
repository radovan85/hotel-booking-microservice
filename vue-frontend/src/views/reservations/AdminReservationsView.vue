<template>
  <div
    class="container py-5 mt-5 mb-5"
    style="
      font-family: 'Rajdhani', sans-serif;
      color: #12044f;
      font-weight: 700;
    "
  >
    <div class="text-center mb-5">
      <h2>📋 Reservations List</h2>
    </div>

    <!-- Filter Buttons -->
    <div class="d-flex justify-content-center gap-3 mb-4">
      <button class="btn btn-info px-4" @click="listAllReservations">
        All
      </button>
      <button class="btn btn-primary px-4" @click="listAllActiveReservations">
        Active
      </button>
      <button class="btn btn-danger px-4" @click="listExpiredReservations">
        Expired
      </button>
    </div>

    <!-- 🟦 All Reservations -->
    <section v-if="listAll">
      <div class="table-responsive">
        <table
          class="table table-bordered table-hover table-striped align-middle text-center"
        >
          <thead class="table-info">
            <tr>
              <th>Reservation Id</th>
              <th>Room Id</th>
              <th>User</th>
              <th>Check-In</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <template
              v-for="temp in paginatedReservationsAll"
              :key="temp.reservationId"
            >
              <tr>
                <td>{{ temp.reservationId }}</td>
                <td>{{ temp.roomId }}</td>
                <td>
                  {{
                    getUserByGuestId(temp.guestId).firstName +
                    " " +
                    getUserByGuestId(temp.guestId).lastName
                  }}
                </td>
                <td>{{ temp.checkInDateStr }}</td>
                <td>
                  <button class="btn btn-info btn-sm me-2" @click="$router.push(`reservations/details/${temp.reservationId}`)">Details</button>
                  <button
                    class="btn btn-danger btn-sm"
                    @click="deleteReservation(temp.reservationId)"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>

      <div class="d-flex justify-content-between align-items-center mt-4">
        <button
          class="btn btn-primary"
          @click="prevPageByType('all')"
          :disabled="currentPage === 1"
        >
          Prev
        </button>
        <span
          >Page: <strong>{{ currentPage }}</strong></span
        >
        <button
          class="btn btn-primary"
          @click="nextPageByType('all')"
          :disabled="currentPage === totalPages"
        >
          Next
        </button>
      </div>
    </section>

    <!-- 🟥 Expired Reservations -->
    <section v-if="listExpired">
      <div class="table-responsive">
        <table
          class="table table-bordered table-hover table-striped align-middle text-center"
        >
          <thead class="table-info">
            <tr>
              <th>Reservation Id</th>
              <th>Room Id</th>
              <th>User</th>
              <th>Check-In</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <template
              v-for="temp in paginatedReservationsExpired"
              :key="temp.reservationId"
            >
              <tr>
                <td>{{ temp.reservationId }}</td>
                <td>{{ temp.roomId }}</td>
                <td>
                  {{
                    getUserByGuestId(temp.guestId).firstName +
                    " " +
                    getUserByGuestId(temp.guestId).lastName
                  }}
                </td>
                <td>{{ temp.checkInDateStr }}</td>
                <td>
                  <button class="btn btn-info btn-sm me-2" @click="$router.push(`reservations/details/${temp.reservationId}`)">Details</button>
                  <button
                    class="btn btn-danger btn-sm"
                    @click="deleteReservation(temp.reservationId)"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>

      <div class="d-flex justify-content-between align-items-center mt-4">
        <button
          class="btn btn-primary"
          @click="prevPageByType('expired')"
          :disabled="currentPage === 1"
        >
          Prev
        </button>
        <span
          >Page: <strong>{{ currentPage }}</strong></span
        >
        <button
          class="btn btn-primary"
          @click="nextPageByType('expired')"
          :disabled="currentPage === totalPages"
        >
          Next
        </button>
      </div>
    </section>

    <!-- 🟩 Active Reservations -->
    <section v-if="listActive">
      <div class="table-responsive">
        <table
          class="table table-bordered table-hover table-striped align-middle text-center"
        >
          <thead class="table-info">
            <tr>
              <th>Reservation Id</th>
              <th>Room Id</th>
              <th>User</th>
              <th>Check-In</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <template
              v-for="temp in paginatedReservationsActive"
              :key="temp.reservationId"
            >
              <tr>
                <td>{{ temp.reservationId }}</td>
                <td>{{ temp.roomId }}</td>
                <td>
                  {{
                    getUserByGuestId(temp.guestId).firstName +
                    " " +
                    getUserByGuestId(temp.guestId).lastName
                  }}
                </td>
                <td>{{ temp.checkInDateStr }}</td>
                <td>
                  <button class="btn btn-info btn-sm me-2" @click="$router.push(`reservations/details/${temp.reservationId}`)">Details</button>
                  <button class="btn btn-warning btn-sm me-2" @click="$router.push(`reservations/switchRoom/${temp.reservationId}`)">
                    Change Room
                  </button>
                  <button
                    class="btn btn-danger btn-sm"
                    @click="deleteReservation(temp.reservationId)"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>

      <div class="d-flex justify-content-between align-items-center mt-4">
        <button
          class="btn btn-primary"
          @click="prevPageByType('active')"
          :disabled="currentPage === 1"
        >
          Prev
        </button>
        <span
          >Page: <strong>{{ currentPage }}</strong></span
        >
        <button
          class="btn btn-primary"
          @click="nextPageByType('active')"
          :disabled="currentPage === totalPages"
        >
          Next
        </button>
      </div>
    </section>

    <!-- 🔙 Return Home -->
    <div class="text-center mt-5">
      <router-link class="btn btn-secondary px-4" to="/home"
        >🏠 Home Page</router-link
      >
    </div>
  </div>
</template>

<script lang="ts">
import User from "@/classes/User";
import GuestService from "@/services/GuestService";
import ReservationService from "@/services/ReservationService";
import UserService from "@/services/UserService";
import { defineComponent } from "vue";
import { useRouter } from "vue-router";

export default defineComponent({
  data() {
    return {
      router: useRouter(),
      listAll: false,
      listActive: false,
      listExpired: false,
      reservationService: new ReservationService(),
      guestService: new GuestService(),
      userService: new UserService(),
      reservationListAll: [] as any[],
      reservationListActive: [] as any[],
      reservationListExpired: [] as any[],
      guestList: [] as any[],
      userList: [] as any[],
      paginatedReservationsAll: [] as any[],
      paginatedReservationsActive: [] as any[],
      paginatedReservationsExpired: [] as any[],
      pageSize: 8,
      currentPage: 1,
      totalPages: 1,
    };
  },

  methods: {
    setPageByType(type: "all" | "active" | "expired", page: number) {
      if (page < 1) return;

      this.currentPage = page;

      if (type === "all") {
        this.totalPages = Math.ceil(
          this.reservationListAll.length / this.pageSize
        );
        this.paginatedReservationsAll = this.reservationListAll.slice(
          (page - 1) * this.pageSize,
          page * this.pageSize
        );
      }

      if (type === "active") {
        this.totalPages = Math.ceil(
          this.reservationListActive.length / this.pageSize
        );
        this.paginatedReservationsActive = this.reservationListActive.slice(
          (page - 1) * this.pageSize,
          page * this.pageSize
        );
      }

      if (type === "expired") {
        this.totalPages = Math.ceil(
          this.reservationListExpired.length / this.pageSize
        );
        this.paginatedReservationsExpired = this.reservationListExpired.slice(
          (page - 1) * this.pageSize,
          page * this.pageSize
        );
      }
    },

    nextPageByType(type: "all" | "active" | "expired") {
      this.setPageByType(type, this.currentPage + 1);
    },

    prevPageByType(type: "all" | "active" | "expired") {
      this.setPageByType(type, this.currentPage - 1);
    },

    listAllReservations(): Promise<any> {
      return new Promise(() => {
        this.reservationService.collectAllReservations().then((response) => {
          this.reservationListAll = response.data;
          this.listAll = true;
          this.listActive = false;
          this.listExpired = false;
          this.setPageByType("all", 1);
        });
      });
    },

    listExpiredReservations(): Promise<any> {
      return new Promise(() => {
        this.reservationService
          .collectAllExpiredReservations()
          .then((response) => {
            this.reservationListExpired = response.data;
            this.listAll = false;
            this.listActive = false;
            this.listExpired = true;
            this.setPageByType("expired", 1);
          });
      });
    },

    listAllActiveReservations(): Promise<any> {
      return new Promise(() => {
        this.reservationService
          .collectAllActiveReservations()
          .then((response) => {
            this.reservationListActive = response.data;
            this.listAll = false;
            this.listActive = true;
            this.listExpired = false;
            this.setPageByType("active", 1);
          });
      });
    },

    listAllGuests(): Promise<any> {
      return new Promise(() => {
        this.guestService.collectAllGuests().then((response) => {
          this.guestList = response.data;
        });
      });
    },

    listAllUsers(): Promise<any> {
      return new Promise(() => {
        this.userService.collectAllUsers().then((response) => {
          this.userList = response.data;
        });
      });
    },

    getUserByGuestId(guestId: any): User {
      const guest = this.guestList.find((g) => g.guestId === guestId);
      if (!guest) throw new Error(`Guest with ID ${guestId} not found.`);

      const user = this.userList.find((u) => u.id === guest.userId);
      if (!user) throw new Error(`User with ID ${guest.userId} not found.`);

      return user;
    },

    deleteReservation(reservationId: any) {
      if (confirm(`Remove this reservation?`)) {
        this.reservationService
          .deleteReservation(reservationId)
          .then(() => {
            if (this.listActive) this.listAllActiveReservations();
            if (this.listExpired) this.listExpiredReservations();
            if (this.listAll) this.listAllReservations();
          })

          .catch((error) => {
            console.error(`Deletion failed: ${error}`);
            alert("Could not delete reservation. Try again later.");
          });
      }
    },
  },

  created() {
    Promise.all([this.listAllGuests(), this.listAllUsers()]).catch((error) => {
      console.log(`Error loading functions: ${error}`);
    });
  },
});
</script>
