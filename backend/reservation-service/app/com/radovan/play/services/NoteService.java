package com.radovan.play.services;

import com.radovan.play.dto.NoteDto;

import java.util.List;

public interface NoteService {

    NoteDto getNoteById(Integer noteId);

    void deleteNote(Integer noteId);

    List<NoteDto> listAll();

    List<NoteDto> listAllForToday();

    void deleteAllNotes();
}
