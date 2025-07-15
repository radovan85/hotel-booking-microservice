<template>
  <div
    v-if="isReady"
    class="container my-5 py-5"
    style="
      font-family: 'Rajdhani', sans-serif;
      color: #12044f;
      font-weight: 700;
    "
  >
    <!-- Title -->
    <div class="text-center mb-5">
      <h2>📌 Reservation Details</h2>
    </div>

    <!-- Details Table -->
    <div class="table-responsive">
      <table class="table table-bordered table-striped text-center">
        <tbody>
          <tr>
            <td class="fw-bold bg-light">Reservation Id</td>
            <td>{{ reservation.reservationId }}</td>
          </tr>

          <tr>
            <td class="fw-bold bg-light">Room Number</td>
            <td>
              {{
                getRoomById(reservation.roomId)?.roomNumber || "Loading Room..."
              }}
            </td>
          </tr>

          <tr>
            <td class="fw-bold bg-light">User</td>
            <td>
              {{
                getUserByGuestId(reservation.guestId)?.firstName +
                  " " +
                  getUserByGuestId(reservation.guestId)?.lastName ||
                "Loading User..."
              }}
            </td>
          </tr>

          <tr>
            <td class="fw-bold bg-light">Email</td>
            <td>
              {{
                getUserByGuestId(reservation.guestId)?.email ||
                "Loading Email..."
              }}
            </td>
          </tr>

          <tr>
            <td class="fw-bold bg-light">Category</td>
            <td>{{ getCategoryByRoomId(reservation.roomId)?.name }}</td>
          </tr>

          <tr>
            <td class="fw-bold bg-light">Nights Per Stay</td>
            <td>{{ reservation.numberOfNights }}</td>
          </tr>

          <tr>
            <td class="fw-bold bg-light">Price</td>
            <td>&euro; {{ reservation.price }}</td>
          </tr>

          <tr>
            <td class="fw-bold bg-light">Check-In</td>
            <td>{{ reservation.checkInDateStr }}</td>
          </tr>

          <tr>
            <td class="fw-bold bg-light">Check-Out</td>
            <td>{{ reservation.checkOutDateStr }}</td>
          </tr>

          <tr>
            <td class="fw-bold bg-light">Created</td>
            <td>{{ reservation.createTimeStr }}</td>
          </tr>

          <tr>
            <td class="fw-bold bg-light">Updated</td>
            <td>{{ reservation.updateTimeStr }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Action Buttons -->
    <div class="text-center mt-5">
      <button class="btn btn-secondary px-4 me-3" @click="goBack">Back</button>
      <button class="btn btn-info px-4 me-3">Switch Room</button>
      <button class="btn btn-danger px-4" @click="deleteReservation(reservation.reservationId)">Remove</button>
    </div>
  </div>

  <!-- Loader dok čekamo podatke -->
  <div v-else class="text-center my-5 py-5">
    <div class="spinner-border text-primary" role="status"></div>
    <p class="mt-3">Loading reservation details...</p>
  </div>
</template>

<script lang="ts">
import Reservation from "@/classes/Reservation";
import Room from "@/classes/Room";
import RoomCategory from "@/classes/RoomCategory";
import User from "@/classes/User";
import GuestService from "@/services/GuestService";
import ReservationService from "@/services/ReservationService";
import RoomCategoryService from "@/services/RoomCategoryService";
import RoomService from "@/services/RoomService";
import UserService from "@/services/UserService";
import { defineComponent } from "vue";
import { useRoute, useRouter } from "vue-router";

export default defineComponent({
  data() {
    return {
      router: useRouter(),
      route: useRoute(),
      reservation: new Reservation(),
      reservationService: new ReservationService(),
      guestService: new GuestService(),
      userService: new UserService(),
      roomService: new RoomService(),
      categoryService: new RoomCategoryService(),
      guestList: [] as any[],
      userList: [] as any[],
      roomList: [] as any[],
      categoryList: [] as any[],
      isReady: false,
    };
  },

  methods: {
    listAllRooms(): Promise<any> {
      return this.roomService.collectAllRooms().then((response) => {
        this.roomList = response.data;
      });
    },

    listAllGuests(): Promise<any> {
      return this.guestService.collectAllGuests().then((response) => {
        this.guestList = response.data;
      });
    },

    listAllUsers(): Promise<any> {
      return this.userService.collectAllUsers().then((response) => {
        this.userList = response.data;
      });
    },

    listAllCategories(): Promise<any> {
      return this.categoryService.collectAllCategories().then((response) => {
        this.categoryList = response.data;
      });
    },

    getCategoryByRoomId(roomId: any): RoomCategory | null {
      const room = this.roomList.find((r) => r.roomId == roomId);
      if (!room || !room.roomCategoryId) return null;

      const category = this.categoryList.find(
        (c) => c.roomCategoryId == room.roomCategoryId
      );
      return category || null;
    },

    getUserByGuestId(guestId: any): User | null {
      const guest = this.guestList.find((g) => g.guestId === guestId);
      const user = guest
        ? this.userList.find((u) => u.id === guest.userId)
        : null;
      return user || null;
    },

    getRoomById(roomId: any): Room | null {
      if (!roomId) return null;
      return this.roomList.find((room) => room.roomId === roomId) || null;
    },

    getReservationById(reservationId: any): Promise<any> {
      return this.reservationService
        .getReservationDetails(reservationId)
        .then((response) => {
          this.reservation = response.data;
        });
    },

    goBack() {
      this.router.back();
    },

    deleteReservation(reservationId: any) {
      if (confirm(`Remove this reservation?`)) {
        this.reservationService
          .deleteReservation(reservationId)
          .then(() => {
            this.router.push(`/reservations`);
          })

          .catch((error) => {
            alert(`Error removing reservation!`);
            console.log(`Error:   ${error}`);
          });
      }
    },
  },

  created() {
    Promise.all([
      this.listAllRooms(),
      this.listAllUsers(),
      this.listAllGuests(),
      this.listAllCategories(),
      this.getReservationById(this.route.params.id),
    ])
      .then(() => {
        this.isReady = true;
      })
      .catch((error) => {
        console.log(`Error loading functions: ${error}`);
      });
  },
});
</script>
