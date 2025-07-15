<template>
  <div class="container bg-white p-4 rounded shadow-sm mt-5">
    <!-- Forma za rezervaciju -->
    <div v-if="showForm">
      <h5 class="mb-3">Book Reservation</h5>

      <form id="reservationForm" class="row g-3">
        <div class="col-md-6">
          <label for="checkInDate" class="form-label">Check-in Date</label>
          <input
            type="date"
            id="checkInDate"
            name="checkInDate"
            class="form-control"
            :min="minCheckIn"
            :max="maxDate"
            @change="adjustCheckOutMin"
            required
          />
        </div>

        <div class="col-md-6">
          <label for="checkOutDate" class="form-label">Check-out Date</label>
          <input
            type="date"
            id="checkOutDate"
            name="checkOutDate"
            class="form-control"
            :min="minCheckOut"
            :max="maxDate"
            required
          />
        </div>

        <div class="col-12">
          <button type="submit" class="btn btn-primary mt-3">Find Available Rooms</button>
        </div>
      </form>
    </div>

    <!-- Prikaz rezervacija -->
    <div v-else>
      <div class="d-flex justify-content-between align-items-center mb-3 mt-5">
        <h5 class="mb-0">Available Reservations</h5>
        <button class="btn btn-outline-primary" @click="resetForm">New Search</button>
      </div>

      <!-- 🛑 Prikaz kada nema rezervacija -->
      <div v-if="noReservations" class="text-center p-5 bg-light border rounded shadow-sm">
        <h6 class="text-muted mb-3">No rooms available for the selected dates.</h6>
        <button class="btn btn-outline-secondary" @click="resetForm">Try different dates</button>
      </div>

      <!-- ✅ Prikaz kada ima rezervacija -->
      <div v-else class="row">
        <div
          v-for="res in reservations"
          :key="res.roomId"
          class="col-md-4 mb-4"
        >
          <div class="card h-100 shadow-sm border-0">
            <div class="card-body">
              <h6 class="card-title text-primary">{{ res.categoryName }}</h6>
              <p class="mb-1"><strong>Check-in:</strong> {{ res.checkInDateStr }}</p>
              <p class="mb-1"><strong>Check-out:</strong> {{ res.checkOutDateStr }}</p>
              <p class="mb-3"><strong>Price:</strong> €{{ res.price }}</p>
              <button class="btn btn-success w-100" @click="bookReservation(res)">Book</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import ReservationService from '@/services/ReservationService';
import axios from 'axios';
import RoomCategoryService from '@/services/RoomCategoryService';
import RoomService from '@/services/RoomService';

export default defineComponent({
  name: 'BookReservationForm',
  data() {
    const today = new Date();
    const max = new Date();
    max.setFullYear(today.getFullYear() + 1);

    return {
      minCheckIn: today.toISOString().split('T')[0],
      maxDate: max.toISOString().split('T')[0],
      minCheckOut: today.toISOString().split('T')[0],
      showForm: true,
      noReservations: false,
      reservations: [] as any[],
      reservationService: new ReservationService(),
      categoryService: new RoomCategoryService(),
      RoomService: new RoomService()
    };
  },
  methods: {
    adjustCheckOutMin() {
      const checkInInput = document.getElementById('checkInDate') as HTMLInputElement;
      const checkOutInput = document.getElementById('checkOutDate') as HTMLInputElement;

      if (checkInInput?.value) {
        const checkInDate = new Date(checkInInput.value);
        const nextDay = new Date(checkInDate);
        nextDay.setDate(checkInDate.getDate() + 1);
        const nextDayStr = nextDay.toISOString().split('T')[0];

        this.minCheckOut = nextDayStr;
        checkOutInput.min = nextDayStr;

        const currentValue = checkOutInput.value;
        if (!currentValue || new Date(currentValue) < nextDay) {
          checkOutInput.value = nextDayStr;
        }
      }
    },
    resetForm() {
      this.showForm = true;
      this.noReservations = false;
      this.reservations = [];
      const form = document.getElementById('reservationForm') as HTMLFormElement;
      if (form) form.reset();
    },
    async bookReservation(res: any) {
      const confirmed = confirm(
        `Are you sure you want to book this room?\nCategory: ${res.categoryName}\nCheck-in: ${res.checkInDateStr}\nCheck-out: ${res.checkOutDateStr}\nTotal Price: €${res.price}`
      );

      if (!confirmed) {
        return;
      }

      try {
        const payload = {
          roomId: res.roomId,
          guestId: res.guestId,
          checkInDateStr: res.checkInDateStr,
          checkOutDateStr: res.checkOutDateStr,
          price: res.price,
          numberOfNights: res.numberOfNights
        };

        await axios.post(
          `${this.reservationService.getTargetUrl()}/book`,
          payload,
          {
            headers: {
              Authorization: localStorage.getItem("token") || ""
            }
          }
        );

        this.reservationService.redirectReservationBooked();
      } catch (error) {
        console.error("❌ Reservation failed:", error);
        alert("Something went wrong while booking. Please try again.");
      }
    }
  },
  mounted() {
    const form = document.getElementById('reservationForm') as HTMLFormElement;

    form.addEventListener('submit', async (event) => {
      event.preventDefault();

      const formData = new FormData(form);
      const payload = {
        checkInDate: formData.get('checkInDate')?.toString().trim(),
        checkOutDate: formData.get('checkOutDate')?.toString().trim()
      };

      try {
        const [reservationRes, roomRes, categoryRes] = await Promise.all([
          axios.post(`${this.reservationService.getTargetUrl()}/provideReservations`, payload),
          this.RoomService.collectAllRooms(),
          this.categoryService.collectAllCategories()
        ]);

        const allRooms = roomRes.data;
        const allCategories = categoryRes.data;

        if (reservationRes.data.length === 0) {
          this.noReservations = true;
        }

        this.reservations = reservationRes.data.map((res: any) => {
          const room = allRooms.find((r: any) => r.roomId === res.roomId);
          const category = allCategories.find((c: any) => c.roomCategoryId === room?.roomCategoryId);
          return {
            ...res,
            categoryName: category?.name || "Unknown"
          };
        });

        this.showForm = false;
      } catch (error) {
        console.error("❌ Error fetching reservations:", error);
      }
    });
  }
});
</script>
