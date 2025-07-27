<template>
  <div v-if="isReady" class="container py-5" style="font-family: Rajdhani, sans-serif; color: #12044f; font-weight: 700; margin-top: 50px;">
    <div class="text-center mb-4">
      <h2 class="text-uppercase fw-bold">Room Details</h2>
      <hr class="mx-auto" style="width: 60px; border-top: 3px solid #12044f" />
    </div>

    <div class="table-responsive shadow-sm">
      <table class="table table-bordered table-striped align-middle text-center bg-light">
        <tbody>
          <tr><td>Room Id</td><td>{{ room.roomId }}</td></tr>
          <tr><td>Room Number</td><td>{{ room.roomNumber }}</td></tr>
          <tr><td>Cost Per Night</td><td>&euro; {{ room.price?.toFixed(2) }}</td></tr>
          <tr><td>Category</td><td v-html="getCategoryById(room.roomCategoryId).name"></td></tr>
          <tr><td>WC</td><td><i v-if="getCategoryById(room.roomCategoryId).wc === 1" class="fas fa-check text-success"></i><i v-else class="fas fa-times text-danger"></i></td></tr>
          <tr><td>Wi-fi</td><td><i v-if="getCategoryById(room.roomCategoryId).wifi === 1" class="fas fa-check text-success"></i><i v-else class="fas fa-times text-danger"></i></td></tr>
          <tr><td>TV</td><td><i v-if="getCategoryById(room.roomCategoryId).tv === 1" class="fas fa-check text-success"></i><i v-else class="fas fa-times text-danger"></i></td></tr>
          <tr><td>Bar</td><td><i v-if="getCategoryById(room.roomCategoryId).bar === 1" class="fas fa-check text-success"></i><i v-else class="fas fa-times text-danger"></i></td></tr>
        </tbody>
      </table>
    </div>

    <div class="text-center mt-5">
      <button class="btn btn-info px-4" @click="goBack">Back</button>
    </div>
  </div>

  <div v-else class="text-center mt-5">
    <div class="spinner-border text-primary mt-5" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script lang="ts">
import Room from "@/classes/Room";
import RoomCategory from "@/classes/RoomCategory";
import RoomCategoryService from "@/services/RoomCategoryService";
import RoomService from "@/services/RoomService";
import { defineComponent } from "vue";
import { useRoute, useRouter } from "vue-router";

export default defineComponent({
  name: "RoomDetailsView",

  data() {
    return {
      isReady: false,
      roomService: new RoomService(),
      categoryService: new RoomCategoryService(),
      room: new Room(),
      categoryList: [] as RoomCategory[],
      router: useRouter(),
      route: useRoute()
    };
  },

  methods: {
    getRoomDetails(roomId: any): Promise<void> {
      return this.roomService.getRoomDetails(roomId)
        .then((response) => {
          this.room = response.data;
        });
    },

    listAllCategories(): Promise<void> {
      return this.categoryService.collectAllCategories()
        .then((response) => {
          this.categoryList = response.data;
        });
    },

    getCategoryById(categoryId: any): RoomCategory {
      return this.categoryList.find((c) => c.roomCategoryId === categoryId) || new RoomCategory();
    },

    goBack(): void {
      this.router.back();
    }
  },

  created() {
    Promise.all([
      this.listAllCategories(),
      this.getRoomDetails(this.route.params.roomId)
    ])
    .then(() => {
      this.isReady = true;
    })
    .catch((error) => {
      console.error("Error loading room details:", error);
    });
  }
});
</script>
