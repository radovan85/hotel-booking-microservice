package com.radovan.play.repositories;

import com.radovan.play.entity.NoteEntity;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {

    NoteEntity save(NoteEntity noteEntity);

    Optional<NoteEntity> findById(Integer noteId);

    void deleteById(Integer noteId);

    List<NoteEntity> findAll();

    void deleteAll();
}
