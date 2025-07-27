package com.radovan.play.repositories;

import com.radovan.play.entity.GuestEntity;

import java.util.List;
import java.util.Optional;

public interface GuestRepository {

    Optional<GuestEntity> findById(Integer guestId);

    Optional<GuestEntity> findByUserId(Integer userId);

    GuestEntity save(GuestEntity guestEntity);

    void deleteById(Integer guestId);

    List<GuestEntity> findAll();
}
