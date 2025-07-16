package com.radovan.play.services.impl;

import com.radovan.play.converter.TempConverter;
import com.radovan.play.dto.NoteDto;
import com.radovan.play.entity.NoteEntity;
import com.radovan.play.exceptions.InstanceUndefinedException;
import com.radovan.play.repositories.NoteRepository;
import com.radovan.play.services.NoteService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class NoteServiceImpl implements NoteService {

    private NoteRepository noteRepository;
    private TempConverter tempConverter;

    @Inject
    private void initialize(TempConverter tempConverter, NoteRepository noteRepository) {
        this.tempConverter = tempConverter;
        this.noteRepository = noteRepository;
    }


    @Override
    public NoteDto getNoteById(Integer noteId) {
        NoteEntity noteEntity = noteRepository.findById(noteId)
                .orElseThrow(() -> new InstanceUndefinedException("The note has not been found!"));
        return tempConverter.noteEntityToDto(noteEntity);
    }

    @Override
    public void deleteNote(Integer noteId) {
        getNoteById(noteId);
        noteRepository.deleteById(noteId);
    }

    @Override
    public List<NoteDto> listAll() {
        List<NoteEntity> allNotes = noteRepository.findAll();
        return allNotes.stream().map(tempConverter::noteEntityToDto).collect(Collectors.toList());
    }

    @Override
    public List<NoteDto> listAllForToday() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        LocalDateTime startOfDayLocal = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDayLocal = now.toLocalDate().atTime(23, 59, 59, 999000000);

        Timestamp startOfDay = Timestamp.valueOf(startOfDayLocal);
        Timestamp endOfDay = Timestamp.valueOf(endOfDayLocal);

        List<NoteEntity> notes = noteRepository.findAll().stream()
                .filter(note -> note.getCreateTime().after(startOfDay) && note.getCreateTime().before(endOfDay)).toList();

        return notes.stream().map(note -> tempConverter.noteEntityToDto(note)).collect(Collectors.toList());
    }

    @Override
    public void deleteAllNotes() {
        noteRepository.deleteAll();
    }
}
