<template>
  <section class="mt-5 mb-5">
    <div
      class="container py-5"
      style="
        font-family: 'Rajdhani', sans-serif;
        color: #12044f;
        font-weight: 600;
      "
    >
      <div class="text-center mb-5">
        <h2 class="fw-bold">📋 Notes Overview</h2>
        <p class="text-muted">System-generated notes and audit messages</p>
      </div>

      <!-- Filter buttons -->
      <div class="d-flex justify-content-center flex-wrap mb-4 gap-3">
        <router-link class="btn btn-outline-primary px-4" to="/notes/today"
          >Today</router-link
        >
        <button class="btn btn-outline-danger px-4" @click="clearAll">Clear All</button>
      </div>

      <!-- Notes table -->
      <div class="table-responsive shadow rounded">
        <table class="table table-hover table-bordered align-middle mb-0">
          <thead class="table-light text-center">
            <tr>
              <th>🆔 Note ID</th>
              <th>📌 Subject</th>
              <th>🗓️ Created</th>
              <th>🔍 Actions</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="tempNote in paginatedNotes" :key="tempNote.noteId">
              <tr class="text-center">
                <td v-html="tempNote.noteId"></td>
                <td v-html="tempNote.subject"></td>
                <td v-html="tempNote.createTimeStr"></td>
                <td>
                  <button
                    class="btn btn-sm btn-primary"
                    @click="
                      $router.push(`notes/noteDetails/${tempNote.noteId}`)
                    "
                  >
                    Details
                  </button>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>

      <div class="pagination" v-if="noteList.length > 0">
        <button @click="prevPage" :disabled="currentPage === 1">
          Previous
        </button>
        <span>Page {{ currentPage }} of {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages">
          Next
        </button>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import NoteService from "@/services/NoteService";
import { defineComponent } from "vue";
import { useRouter } from "vue-router";

export default defineComponent({
  data() {
    return {
      noteService: new NoteService(),
      noteList: [] as any[],
      paginatedNotes: [] as any[],
      pageSize: 6,
      currentPage: 1,
      totalPages: 1,
      router: useRouter(),
    };
  },

  methods: {
    listAllNotes(): Promise<any> {
      return new Promise(() => {
        this.noteService.getAllNotes().then((response) => {
          this.noteList = response.data;
          this.totalPages = Math.ceil(this.noteList.length / this.pageSize);
          this.setPage(1);
        });
      });
    },

    setPage(page: number) {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page;
      this.paginatedNotes = this.noteList.slice(
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

    async clearAll(): Promise<void> {
      if (
        !confirm(
          "Are you sure you want to delete all notes? This action cannot be undone."
        )
      ) {
        return; // korisnik odustao
      }

      try {
        await this.noteService.deleteAll();
        this.noteList = []; // očisti listu direktno
        this.paginatedNotes = [];
        this.totalPages = 1;
        this.currentPage = 1;
      } catch (error) {
        alert("Failed to clear notes!");
        console.log(error);
      }
    },
  },

  created() {
    Promise.all([this.listAllNotes()]).catch((error) => {
      console.log(`Error loading functions  ${error}`);
    });
  },
});
</script>
