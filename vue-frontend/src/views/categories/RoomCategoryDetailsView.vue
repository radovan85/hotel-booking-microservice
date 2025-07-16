<template>
  <div v-if="isReady" class="container py-5" style="font-family: Rajdhani, sans-serif; margin-top: 50px; color: #12044F; font-weight: 700;">
    <div class="text-center mb-5">
      <h2 class="text-uppercase">Category Details</h2>
      <hr class="mx-auto" style="width: 50px; border-top: 3px solid #12044F;">
    </div>

    <table class="table table-bordered align-middle text-center shadow-sm">
      <tbody>
        <tr>
          <td>Category Id</td>
          <td>{{ category.roomCategoryId }}</td>
        </tr>
        <tr>
          <td>Category</td>
          <td>{{ category.name }}</td>
        </tr>
        <tr>
          <td>WC</td>
          <td>
            <i v-if="category.wc === 1" class="fas fa-check text-success"></i>
            <i v-else class="fas fa-times text-danger"></i>
          </td>
        </tr>
        <tr>
          <td>Wi-fi</td>
          <td>
            <i v-if="category.wifi === 1" class="fas fa-check text-success"></i>
            <i v-else class="fas fa-times text-danger"></i>
          </td>
        </tr>
        <tr>
          <td>Tv</td>
          <td>
            <i v-if="category.tv === 1" class="fas fa-check text-success"></i>
            <i v-else class="fas fa-times text-danger"></i>
          </td>
        </tr>
        <tr>
          <td>Bar</td>
          <td>
            <i v-if="category.bar === 1" class="fas fa-check text-success"></i>
            <i v-else class="fas fa-times text-danger"></i>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="text-center mt-5">
      <button class="btn btn-info px-4" @click="goBack">All Categories</button>
    </div>
  </div>

  <div v-else class="text-center mt-5">
    <div class="spinner-border text-primary mt-5" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script lang="ts">
import RoomCategory from '@/classes/RoomCategory';
import RoomCategoryService from '@/services/RoomCategoryService';
import { defineComponent } from 'vue';
import { useRoute, useRouter } from 'vue-router';

export default defineComponent({
  name: "CategoryDetailsView",

  data() {
    return {
      isReady: false,
      router: useRouter(),
      route: useRoute(),
      categoryService: new RoomCategoryService,
      category: new RoomCategory
    };
  },

  methods: {
    goBack() {
      this.router.back();
    },

    getCategoryDetails(categoryId: any): Promise<void> {
      return this.categoryService.getCategoryDetails(categoryId)
        .then((response) => {
          this.category = response.data;
        });
    }
  },

  created() {
    Promise.all([
      this.getCategoryDetails(this.route.params.categoryId)
    ])
    .then(() => {
      this.isReady = true;
    })
    .catch((err) => {
      console.error("Error fetching category :", err);
    });
  }
});
</script>
