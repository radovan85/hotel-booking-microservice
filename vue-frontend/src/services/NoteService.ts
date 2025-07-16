import router from "@/router";
import axios from "axios";

export default class NoteService {

    private targetUrl = `http://localhost:8090/api/notes`;

    getAllNotes():Promise<any> {
        return axios.get(`${this.targetUrl}`);
    }

    getTodaysNotes():Promise<any> {
        return axios.get(`${this.targetUrl}/today`);
    }

    getNoteDetails(noteId:any):Promise<any> {
        return axios.get(`${this.targetUrl}/${noteId}`);
    }

    async deleteNote(noteId:any):Promise<any> {
        return await axios.delete(`${this.targetUrl}/${noteId}`);
    }

    async deleteAll():Promise<any> {
        return await axios.delete(`${this.targetUrl}/removeAll`);
    }

    redirectNoteDetails(noteId:any){
        router.push(`/notes/noteDetails/${noteId}`);
    }
}