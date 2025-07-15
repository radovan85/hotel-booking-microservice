package com.radovan.play.repositories;

import com.radovan.play.entity.RoomEntity;

import java.util.List;
import java.util.Optional;

public interface RoomRepository {

    Optional<RoomEntity> findById(Integer roomId);

    RoomEntity save(RoomEntity roomEntity);

    void deleteById(Integer roomId);

    List<RoomEntity> findAll();

    List<RoomEntity> findAllByCategoryId(Integer categoryId);

    Optional<RoomEntity> findByRoomNumber(Integer roomNumber);
}
