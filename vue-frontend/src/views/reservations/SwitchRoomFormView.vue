<template>
  <div
    class="container my-5 py-5"
    style="font-family: 'Rajdhani', sans-serif; color: #12044f; font-weight: 700;"
  >
    <!-- Title and Separator -->
    <div class="text-center text-uppercase pt-3 mb-4">
      <h3>Switch Room Form</h3>
      <hr />
    </div>

    <!-- Conditional View -->
    <div v-if="isReady">
      <div v-if="availableRooms.length === 0" class="text-center mt-4">
        <p class="text-muted">🚫 No alternative rooms available in this category.</p>
      </div>

      <div v-else class="row mt-4">
        <div class="col-md-8 col-lg-6 offset-md-2 offset-lg-3">
          <div class="card shadow-sm">
            <div class="card-body">
              <form class="mt-3" id="switchRoomForm" @submit.prevent="handleFormSubmit">
                <!-- Room Category -->
                <div class="mb-3">
                  <span class="form-text">
                    Room Category: {{ getCategoryByRoomId(reservation.roomId)?.name }}
                  </span>
                </div>

                <!-- Current Room -->
                <div class="mb-3">
                  <span class="form-text">
                    Current Room: {{ getRoomById(reservation.roomId)?.roomNumber }}
                  </span>
                </div>

                <!-- Room Number Selection -->
                <div class="mb-3">
                  <label class="form-label">Room Number</label>
                  <select class="form-select" id="roomId" name="roomId">
                    <option value="">Please Select</option>
                    <option
                      v-for="room in availableRooms"
                      :key="room.roomId"
                      :value="room.roomId"
                    >
                      {{ room.roomNumber }}
                    </option>
                  </select>
                  <div class="form-text text-danger" id="roomIdError" style="visibility: hidden">
                    Please select room!
                  </div>
                </div>

                <!-- Hidden Fields -->
                <input type="hidden" name="price" :value="reservation.price" />
                <input type="hidden" name="guestId" :value="reservation.guestId" />
                <input type="hidden" name="numberOfNights" :value="reservation.numberOfNights" />

                <!-- Submit Button -->
                <div class="text-center">
                  <button type="submit" class="btn btn-info">Submit</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading View -->
    <div v-else class="text-center mt-5">
      <p class="text-muted">🔄 Loading reservation info and available rooms...</p>
    </div>
  </div>
</template>

<script lang="ts">
import Reservation from "@/classes/Reservation";
import Room from "@/classes/Room";
import RoomCategory from "@/classes/RoomCategory";
import ReservationService from "@/services/ReservationService";
import RoomCategoryService from "@/services/RoomCategoryService";
import RoomService from "@/services/RoomService";
import ValidationService from "@/services/ValidationService";
import axios from "axios";
import { defineComponent } from "vue";
import { useRoute, useRouter } from "vue-router";

export default defineComponent({
  data() {
    return {
      router: useRouter(),
      route: useRoute(),
      validationService: new ValidationService(),
      roomService: new RoomService(),
      categoryService: new RoomCategoryService(),
      reservationService: new ReservationService(),
      roomList: [] as any[],
      categoryList: [] as any[],
      availableRooms: [] as any[],
      reservation: new Reservation(),
      isReady: false,
    };
  },

  methods: {
    async initView(): Promise<void> {
      try {
        await Promise.all([
          this.listAllRooms(),
          this.listAllCategories(),
          this.getReservationDetails(this.route.params.reservationId),
          this.resolveAvailableRooms(this.route.params.reservationId),
        ]);
      } finally {
        this.isReady = true;
      }
    },

    listAllRooms(): Promise<any> {
      return this.roomService.collectAllRooms().then((response) => {
        this.roomList = response.data;
      });
    },

    listAllCategories(): Promise<any> {
      return this.categoryService.collectAllCategories().then((response) => {
        this.categoryList = response.data;
      });
    },

    getReservationDetails(reservationId: any): Promise<any> {
      return this.reservationService.getReservationDetails(reservationId).then((response) => {
        this.reservation = response.data;
      });
    },

    resolveAvailableRooms(reservationId: any): Promise<any> {
      return this.reservationService.collectAlternativeRooms(reservationId).then((response) => {
        this.availableRooms = response.data;
      });
    },

    getCategoryByRoomId(roomId: any): RoomCategory | null {
      const room = this.getRoomById(roomId);
      if (!room || !room.roomCategoryId) return null;
      return (
        this.categoryList.find((c) => c.roomCategoryId == room.roomCategoryId) || null
      );
    },

    getRoomById(roomId: any): Room {
      return this.roomList.find((room) => room.roomId == roomId) || null;
    },

    handleFormSubmit(event: Event): void {
      const form = document.getElementById("switchRoomForm") as HTMLFormElement;
      const formData = new FormData(form);
      const serializedData: { [key: string]: string } = {};
      formData.forEach((value, key) => {
        serializedData[key] = value.toString().trim();
      });

      if (this.validationService.validateRoomNumber()) {
        axios
          .put(
            `${this.reservationService.getTargetUrl()}/${this.reservation.reservationId}`,
            {
              roomId: Number(serializedData["roomId"]),
              price: Number(serializedData["price"]),
              numberOfNights: Number(serializedData["numberOfNights"]),
              guestId: Number(serializedData["guestId"]),
            }
          )
          .then(() => {
            alert("✅ Reservation updated successfully!");
            this.router.push("/reservations");
          })
          .catch((error) => {
            alert("❌ Error updating reservation!");
            console.error(error);
          });
      }
    },
  },

  mounted() {
    this.initView();
  },
});
</script>
