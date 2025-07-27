<template>
  <div v-if="isReady" class="container py-5" style="font-family: Rajdhani, sans-serif; margin-top: 50px; color: #12044f; font-weight: 700;">
    <div class="text-center mb-4">
      <h2 class="text-uppercase fw-bold">Rooms List</h2>
      <hr class="w-25 mx-auto border-dark border-2" />
    </div>

    <router-link class="mb-4 btn btn-info" to="/rooms/addRoom">Add Room</router-link>

    <div class="shadow-sm">
      <table class="table table-bordered table-hover table-striped align-middle text-center bg-light">
        <thead class="table-info text-uppercase">
          <tr>
            <th>Room Id</th>
            <th>Category</th>
            <th>Price</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="tempRoom in paginatedRooms" :key="tempRoom.roomId">
            <tr>
              <td v-html="tempRoom.roomId"></td>
              <td v-html="getCategoryById(tempRoom.roomCategoryId).name"></td>
              <td v-html="formattedPrice(tempRoom.price)"></td>
              <td>
                <div class="dropdown">
                  <button class="btn btn-info btn-sm dropdown-toggle px-3" type="button" data-bs-toggle="dropdown">
                    Actions
                  </button>
                  <ul class="dropdown-menu dropdown-menu-end">
                    <li><a class="dropdown-item" @click="$router.push(`/rooms/roomDetails/${tempRoom.roomId}`)">Details</a></li>
                    <li><a class="dropdown-item" @click="$router.push(`/rooms/updateRoom/${tempRoom.roomId}`)">Update</a></li>
                    <li><a class="dropdown-item text-danger" @click="deleteRoom(`${tempRoom.roomId}`)">Delete</a></li>
                  </ul>
                </div>
              </td>
            </tr>
          </template>
        </tbody>
      </table>

      <div class="pagination" v-if="roomList.length > 0">
        <button @click="prevPage" :disabled="currentPage === 1">Previous</button>
        <span>Page {{ currentPage }} of {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages">Next</button>
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
import RoomCategory from "@/classes/RoomCategory";
import RoomCategoryService from "@/services/RoomCategoryService";
import RoomService from "@/services/RoomService";
import { defineComponent } from "vue";

export default defineComponent({
  name: "RoomListView",

  data() {
    return {
      isReady: false,
      roomService: new RoomService(),
      categoryService: new RoomCategoryService(),
      roomList: [] as any[],
      paginatedRooms: [] as any[],
      categoryList: [] as RoomCategory[],
      pageSize: 6,
      currentPage: 1,
      totalPages: 1,
    };
  },

  methods: {
    setPage(page: number) {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page;
      this.paginatedRooms = this.roomList.slice(
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

    listAllRooms(): Promise<void> {
      return this.roomService.collectAllRooms()
        .then((response) => {
          this.roomList = response.data;
          this.totalPages = Math.ceil(this.roomList.length / this.pageSize);
          this.setPage(1);
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

    async deleteRoom(roomId: any) {
      if (!confirm("Remove this room?\nIt will affect all related data!")) return;

      try {
        await this.roomService.deleteRoom(roomId);
        const index = this.roomList.findIndex((room) => room.roomId === roomId);
        if (index !== -1) {
          this.roomList.splice(index, 1);
        }

        await this.listAllRooms();
        this.totalPages = Math.max(1, Math.ceil(this.roomList.length / this.pageSize));
        this.currentPage = Math.min(this.currentPage, this.totalPages);
        this.$forceUpdate();
      } catch (error) {
        console.error("Error deleting room:", error);
        alert("Failed!");
      }
    },

    formattedPrice(price: number): string {
      return `&euro; ${Number(price).toFixed(2)}`;
    },
  },

  created() {
    Promise.all([this.listAllRooms(), this.listAllCategories()])
      .then(() => {
        this.isReady = true;
      })
      .catch((error) => {
        console.error("Error loading room list:", error);
      });
  },
});
</script>
