package com.radovan.play.repositories;


import com.radovan.play.entity.RoomCategoryEntity;

import java.util.List;
import java.util.Optional;

public interface RoomCategoryRepository {

    Optional<RoomCategoryEntity> findById(Integer categoryId);

    Optional<RoomCategoryEntity> findByName(String name);

    RoomCategoryEntity save(RoomCategoryEntity roomCategoryEntity);

    List<RoomCategoryEntity> findAll();

    void deleteById(Integer categoryId);
}
