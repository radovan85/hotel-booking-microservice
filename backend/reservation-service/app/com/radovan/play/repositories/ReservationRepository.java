package com.radovan.play.repositories;


import com.radovan.play.entity.ReservationEntity;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    List<ReservationEntity> findAllByRoomId(Integer roomId);

    ReservationEntity save(ReservationEntity reservationEntity);

    List<ReservationEntity> findAll();

    List<ReservationEntity> findAllByGuestId(Integer guestId);

    void deleteById(Integer reservationId);

    Optional<ReservationEntity> findById(Integer reservationId);

    void deleteAllByGuestId(Integer guestId);

    void deleteAllByRoomId(Integer roomId);
}
