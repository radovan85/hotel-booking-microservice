<template>
  <section class="mt-5 mb-5">
    <div v-if="isReady" class="container py-5" style="font-family: 'Rajdhani', sans-serif; color: #12044f; font-weight: 600;">
      <div class="text-center mb-5">
        <h2 class="fw-bold">📋 Today's Notes Overview</h2>
        <p class="text-muted">
          System-generated notes created on <strong>today's date</strong>
        </p>
      </div>

      <!-- Filter buttons -->
      <div class="d-flex justify-content-center flex-wrap mb-4 gap-3">
        <router-link class="btn btn-outline-info px-4" to="/notes">All</router-link>
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
                  <button class="btn btn-sm btn-primary" @click="noteService.redirectNoteDetails(tempNote.noteId)">Details</button>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>

      <div class="pagination" v-if="noteList.length > 0">
        <button @click="prevPage" :disabled="currentPage === 1">Previous</button>
        <span>Page {{ currentPage }} of {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages">Next</button>
      </div>
    </div>

    <div v-else class="text-center mt-5">
      <div class="spinner-border text-primary mt-5" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import NoteService from "@/services/NoteService";
import { defineComponent } from "vue";

export default defineComponent({
  name: "NoteTodayView",

  data() {
    return {
      isReady: false,
      noteService: new NoteService(),
      noteList: [] as any[],
      paginatedNotes: [] as any[],
      pageSize: 6,
      currentPage: 1,
      totalPages: 1,
    };
  },

  methods: {
    listAllNotes(): Promise<void> {
      return this.noteService.getTodaysNotes()
        .then((response) => {
          this.noteList = response.data;
          this.totalPages = Math.ceil(this.noteList.length / this.pageSize);
          this.setPage(1);
        });
    },

    setPage(page: number): void {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page;
      this.paginatedNotes = this.noteList.slice(
        (page - 1) * this.pageSize,
        page * this.pageSize
      );
    },

    nextPage(): void {
      this.setPage(this.currentPage + 1);
    },

    prevPage(): void {
      this.setPage(this.currentPage - 1);
    },
  },

  created() {
    Promise.all([this.listAllNotes()])
      .then(() => {
        this.isReady = true;
      })
      .catch((error) => {
        console.error("Error loading today's notes:", error);
      });
  },
});
</script>
