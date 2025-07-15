<template>
  <div
    class="container py-5"
    style="
      font-family: Rajdhani, sans-serif;
      margin-top: 50px;
      color: #12044f;
      font-weight: 700;
    "
  >
    <div class="text-center mb-4">
      <h2 class="text-uppercase fw-bold">Categories List</h2>
      <hr class="w-25 mx-auto border-dark border-2" />
    </div>

    <!-- Dugme za dodavanje -->
    <div class="d-flex justify-content-end mb-3">
      <router-link
        class="btn btn-info shadow-sm px-4"
        to="/categories/addCategory"
        >Add Category</router-link
      >
    </div>

    <!-- Tabela kategorija -->
    <div class="shadow-sm">
      <table class="table table-hover table-bordered align-middle text-center">
        <thead class="table-dark">
          <tr>
            <th>Category Id</th>
            <th>Category</th>
            <th>Price</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody class="table-light">
          <template
            v-for="tempCategory in paginatedCategories"
            :key="tempCategory.roomCategoryId"
          >
            <tr>
              <td v-html="tempCategory.roomCategoryId"></td>
              <td v-html="tempCategory.name"></td>
              <td v-html="formattedPrice(tempCategory.price)"></td>
              <td>
                <div class="dropdown">
                  <button
                    class="btn btn-info dropdown-toggle btn-sm"
                    type="button"
                    data-bs-toggle="dropdown"
                  >
                    Actions
                  </button>
                  <ul class="dropdown-menu">
                    <li>
                      <button
                        class="dropdown-item"
                        @click="
                          $router.push(
                            `categories/categoryDetails/${tempCategory.roomCategoryId}`
                          )
                        "
                      >
                        Details
                      </button>
                    </li>
                    <li>
                      <button
                        class="dropdown-item"
                        @click="
                          $router.push(
                            `categories/updateCategory/${tempCategory.roomCategoryId}`
                          )
                        "
                      >
                        Update
                      </button>
                    </li>
                    <li>
                      <button
                        class="dropdown-item text-danger"
                        @click="deleteCategory(tempCategory.roomCategoryId)"
                      >
                        Delete
                      </button>
                    </li>
                  </ul>
                </div>
              </td>
            </tr>
          </template>
          <!-- Ostale kategorije ovde v-for ili petlja -->
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="categoryList.length > 0">
      <button @click="prevPage" :disabled="currentPage === 1">Previous</button>
      <span>Page {{ currentPage }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages">
        Next
      </button>
    </div>
  </div>
</template>

<script lang="ts">
import RoomCategoryService from "@/services/RoomCategoryService";
import { defineComponent } from "vue";
import { useRouter } from "vue-router";

export default defineComponent({
  
  data() {
    return {
      router: useRouter(),
      categoryService: new RoomCategoryService(),
      categoryList: [] as any[],
      paginatedCategories: [] as any[],
      pageSize: 6,
      currentPage: 1,
      totalPages: 1,
    };
  },

  methods: {
    listAllCategories(): Promise<any> {
      return new Promise(() => {
        this.categoryService.collectAllCategories().then((response) => {
          this.categoryList = response.data;
          this.totalPages = Math.ceil(this.categoryList.length / this.pageSize);
          this.setPage(1);
        });
      });
    },

    setPage(page: number) {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page;
      this.paginatedCategories = this.categoryList.slice(
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

    deleteCategory(categoryId: any) {
      if (confirm(`Remove this category?\nIt will affect all related data!`)) {
        this.categoryService.deleteCategory(categoryId).then(() => {
          this.categoryList = this.categoryList.filter(
            (tempCategory) => tempCategory.roomCategoryId !== categoryId
          );

          this.totalPages = Math.max(
            1,
            Math.ceil(this.categoryList.length / this.pageSize)
          );

          if (
            (this.currentPage - 1) * this.pageSize >=
              this.categoryList.length &&
            this.currentPage > 1
          ) {
            this.currentPage--;
          }

          this.setPage(this.currentPage);
          this.paginatedCategories = [
            ...this.categoryList.slice(
              (this.currentPage - 1) * this.pageSize,
              this.currentPage * this.pageSize
            ),
          ];
        });
      }
    },

    formattedPrice(price: number) {
      return `&euro; ${Number(price).toFixed(2)}`;
    },
  },

  created() {
    Promise.all([this.listAllCategories()]).catch((error) => {
      console.log(`Error loading functions  ${error}`);
    });
  },
});
</script>
