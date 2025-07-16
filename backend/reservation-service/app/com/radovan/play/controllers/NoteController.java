package com.radovan.play.controllers;

import com.radovan.play.security.JwtAuthAction;
import com.radovan.play.security.RoleSecured;
import com.radovan.play.services.NoteService;
import jakarta.inject.Inject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

@With(JwtAuthAction.class)
@RoleSecured({"ROLE_ADMIN"})
public class NoteController extends Controller {

    private NoteService noteService;

    @Inject
    private void initialize(NoteService noteService) {
        this.noteService = noteService;
    }

    public Result getNoteDetails(Integer noteId){
        return ok(Json.toJson(noteService.getNoteById(noteId)));
    }

    public Result deleteNote(Integer noteId){
        noteService.deleteNote(noteId);
        return ok(Json.toJson("Note with id " + noteId + " has been removed"));
    }

    public Result removeAllNotes(){
        noteService.deleteAllNotes();
        return ok(Json.toJson("All notes have been removed!"));
    }

    public Result getAllNotes(){
        return ok(Json.toJson(noteService.listAll()));
    }

    public Result getTodaysNotes(){
        return ok(Json.toJson(noteService.listAllForToday()));
    }
}
