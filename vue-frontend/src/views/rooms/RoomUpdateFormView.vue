<template>
  <div v-if="isReady" class="container" style="font-family: Rajdhani, sans-serif; color: #12044F; font-weight: 700; margin-bottom: 100px; margin-top: 125px;">
    <div class="text-center text-uppercase mb-4">
      <h3>Room update Form</h3>
      <hr class="w-25 mx-auto">
    </div>

    <div class="row">
      <div class="col-md-6 offset-md-3">
        <form class="mt-3" id="roomForm">
          <div class="mb-3">
            <label for="roomNumber" class="form-label">Room Number</label>
            <input
              type="text"
              class="form-control"
              id="roomNumber"
              name="roomNumber"
              placeholder="Enter Room Number"
              :value="currentRoom.roomNumber"
              @keydown="validationService.validateNumber($event)"
            />
            <span class="text-danger" id="roomNumberError" style="visibility: hidden">
              Please provide room number!
            </span>
          </div>

          <div class="mb-5">
            <label for="roomCategory" class="form-label">Room Category</label>
            <select class="form-select" id="roomCategory" name="roomCategoryId">
              <option :value="''">Please Select</option>
              <option
                v-for="tempCategory in categoryList"
                :key="tempCategory.roomCategoryId"
                :value="tempCategory.roomCategoryId"
                :selected="tempCategory.roomCategoryId === currentRoom.roomCategoryId"
              >
                {{ tempCategory.name }}
              </option>
            </select>
            <span class="text-danger" id="roomCategoryError" style="visibility: hidden">
              Please provide room category!
            </span>
          </div>

          <div class="text-center">
            <button type="submit" class="btn btn-info">Update</button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div v-else class="text-center mt-5">
    <div class="spinner-border text-info mt-5" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script lang="ts">
import Room from "@/classes/Room";
import RoomCategory from "@/classes/RoomCategory";
import RoomCategoryService from "@/services/RoomCategoryService";
import RoomService from "@/services/RoomService";
import ValidationService from "@/services/ValidationService";
import axios from "axios";
import { defineComponent } from "vue";
import { useRoute } from "vue-router";

export default defineComponent({
  name: "RoomUpdateView",

  data() {
    return {
      isReady: false,
      roomService: new RoomService(),
      categoryService: new RoomCategoryService(),
      validationService: new ValidationService(),
      categoryList: [] as RoomCategory[],
      currentRoom: new Room(),
      route: useRoute(),
    };
  },

  methods: {
    listAllCategories(): Promise<void> {
      return this.categoryService.collectAllCategories()
        .then((response) => {
          this.categoryList = response.data;
        });
    },

    getRoomDetails(roomId: any): Promise<void> {
      return this.roomService.getRoomDetails(roomId)
        .then((response) => {
          this.currentRoom = response.data;
        });
    },

    handleFormSubmission(): void {
      const form = document.getElementById("roomForm") as HTMLFormElement;
      form?.addEventListener("submit", async (event) => {
        event.preventDefault();

        const formData = new FormData(form);
        const serializedData: { [key: string]: string } = {};
        formData.forEach((value, key) => {
          serializedData[key] = value.toString().trim();
        });

        if (this.validationService.validateRoom()) {
          try {
            await axios.put(`${this.roomService.getTargetUrl()}/${this.currentRoom.roomId}`, {
              roomNumber: Number(serializedData.roomNumber),
              roomCategoryId: Number(serializedData.roomCategoryId),
            });
            this.roomService.redirectAllRooms();
          } catch (error: any) {
            if (error.response?.status === 409) {
              alert(error.response.data);
            } else {
              console.error("Update failed:", error);
            }
          }
        }
      });
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
      console.error("Error loading room update form:", error);
    });
  },

  mounted() {
    const interval = setInterval(() => {
      const form = document.getElementById("roomForm");
      if (form && this.isReady) {
        clearInterval(interval);
        this.handleFormSubmission();
      }
    }, 100);
  }
});
</script>
