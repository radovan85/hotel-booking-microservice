<template>
  <section class="mt-5">
    <div
      class="container py-5"
      style="font-family: 'Rajdhani', sans-serif; color: #12044f"
    >
      <div class="text-center mb-4">
        <h2 class="fw-bold">📝 Note Details</h2>
        <p class="text-muted">Overview of a system-generated note</p>
      </div>

      <div class="table-responsive shadow-sm rounded">
        <table class="table table-bordered table-striped align-middle">
          <tbody>
            <tr>
              <th class="text-end px-4" style="width: 25%">Note ID</th>
              <td class="text-start px-4" v-html="note.noteId"></td>
            </tr>
            <tr>
              <th class="text-end px-4">Subject</th>
              <td class="text-start px-4" v-html="note.subject"></td>
            </tr>
            <tr>
              <th class="text-end px-4">Created</th>
              <td class="text-start px-4" v-html="note.createTimeStr"></td>
            </tr>
            <tr>
              <th class="text-end px-4">Text</th>
              <td class="text-start px-4" v-html="note.text"></td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="text-center mt-5 d-flex justify-content-center gap-3">
        <button class="btn btn-secondary px-4" @click="goBack">Back</button>
        <button class="btn btn-danger px-4" @click="deleteNote(note.noteId)">Remove</button>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import Note from "@/classes/Note";
import NoteService from "@/services/NoteService";
import { defineComponent } from "vue";
import { useRoute, useRouter } from "vue-router";

export default defineComponent({
  data() {
    return {
      router: useRouter(),
      route: useRoute(),
      noteService: new NoteService(),
      note: new Note(),
    };
  },

  methods: {
    getNoteDetails(noteId: any): Promise<any> {
      return new Promise(() => {
        this.noteService.getNoteDetails(noteId).then((response) => {
          this.note = response.data;
        });
      });
    },

    goBack() {
      this.router.back();
    },

    deleteNote(noteId:any):Promise<any>{
        return new Promise(() => {
          if(confirm(`Remove this note?`)){
            this.noteService.deleteNote(noteId)
            .then(() => {
                this.router.push(`/notes`);
            })

            .catch((error) => {
                alert(`Failed!`);
                console.log(`error:  ${error}`);
            })
          }
        })
    }
  },

  created() {
    Promise.all([this.getNoteDetails(this.route.params.id)])
    .catch((error) => {
      console.log(`Error loading functions  ${error}`);
    });
  },
});
</script>
