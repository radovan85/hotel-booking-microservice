<template>
  <div
    class="container shadow-sm bg-white p-4 rounded"
    style="margin-bottom: 100px; margin-top: 100px"
  >
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4 class="mb-0">Reservations</h4>
      <router-link class="btn btn-primary" to="/reservations/book"
        >Add Reservation</router-link
      >
    </div>

    <table class="table table-bordered table-hover">
      <thead class="table-light">
        <tr>
          <th scope="col">Reservation ID</th>
          <th scope="col">Room ID</th>
          <th scope="col">Check-in Date</th>
          <th scope="col">Actions</th>
        </tr>
      </thead>
      <tbody>
        <template
          v-for="tempReservation in paginatedReservations"
          :key="tempReservation.reservationId"
        >
          <tr>
            <td v-html="tempReservation.reservationId"></td>
            <td v-html="tempReservation.roomId"></td>
            <td v-html="tempReservation.checkInDateStr"></td>
            <td>
              <button
                v-if="tempReservation.canBeCanceled"
                class="btn btn-sm btn-outline-danger"
                @click="cancelReservation(tempReservation.reservationId)"
              >Cancel
              </button>
</td>
          </tr>
        </template>
      </tbody>
    </table>
    <div class="pagination" v-if="reservationList.length > 0">
        <button @click="prevPage" :disabled="currentPage === 1">
          Previous
        </button>
        <span>Page {{ currentPage }} of {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages">
          Next
        </button>
      </div>
  </div>
</template>

<script lang="ts">
import Reservation from "@/classes/Reservation";
import ReservationService from "@/services/ReservationService";
import { defineComponent } from "vue";

export default defineComponent({
  data() {
    return {
      paginatedReservations: [] as any[],
      reservationList: [] as any[],
      reservationService: new ReservationService(),
      pageSize: 6,
      currentPage: 1,
      totalPages: 1,
    };
  },

  methods: {
    setPage(page: number) {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page;
      this.paginatedReservations = this.reservationList.slice(
        (page - 1) * this.pageSize,
        page * this.pageSize
      );
    },

    nextPage() {
      this.setPage(this.currentPage + 1);
    },

    prevPage() {
      this.setPage(this.currentPage - 1);
    },

    listMyReservations(): Promise<any> {
      return new Promise(() => {
        this.reservationService.collectMyReservations().then((response) => {
          this.reservationList = response.data.map((r: any) => Object.assign(new Reservation(), r));
          this.totalPages = Math.ceil(
            this.reservationList.length / this.pageSize
          );
          this.setPage(1);
        });
      });
    },

    cancelReservation(reservationId: any) {
      if (confirm(`Are you sure you want to cancel this reservation?`)) {
        this.reservationService.cancelReservation(reservationId)
        .then(() => {
          this.reservationList = this.reservationList.filter(
            (tempReservation) => tempReservation.reservationId !== reservationId
          );

          this.totalPages = Math.max(
            1,
            Math.ceil(this.reservationList.length / this.pageSize)
          );

          if (
            (this.currentPage - 1) * this.pageSize >=
              this.reservationList.length &&
            this.currentPage > 1
          ) {
            this.currentPage--;
          }

          this.setPage(this.currentPage);
          this.paginatedReservations = [
            ...this.reservationList.slice(
              (this.currentPage - 1) * this.pageSize,
              this.currentPage * this.pageSize
            ),
          ];
        });
      }
    },
  },

  created() {
    Promise.all([this.listMyReservations()])
    .catch((error) => {
      console.log(`Error loading functions  ${error}`);
    });
  },
});
</script>

<style scoped>
th,
td {
  text-align: center;
  vertical-align: middle;
}

th {
  font-weight: 700;
  color: #12044f;
  background-color: #deeaf7;
}

td {
  font-weight: 500;
  color: #333;
}

.table-hover tbody tr:hover {
  background-color: #f5faff;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.pagination button {
  margin: 0 5px;
  padding: 6px 12px;
  font-weight: 600;
  border-radius: 5px;
}
</style>

