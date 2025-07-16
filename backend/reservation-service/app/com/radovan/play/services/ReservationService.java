package com.radovan.play.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.play.dto.ReservationDto;
import com.radovan.play.utils.ReservationTimeForm;
import play.mvc.Http;

import java.net.http.HttpRequest;
import java.sql.Timestamp;
import java.util.List;

public interface ReservationService {

    List<ReservationDto> listAvailableBookings(ReservationTimeForm timeForm, Http.Request request);

    boolean isRoomAvailable(Integer roomId, Timestamp checkIn, Timestamp checkOut);

    ReservationDto addReservation(ReservationDto reservation,Http.Request request);

    List<ReservationDto> listAll();

    List<ReservationDto> listAllByGuestId(Integer guestId);

    List<ReservationDto> getMyReservations(Http.Request request);

    void cancelReservation(Integer reservationId,Http.Request request);

    void deleteReservation(Integer reservationId);

    ReservationDto getReservationById(Integer reservationId);

    void deleteAllByGuestId(Integer guestId);

    void deleteAllByRoomId(Integer roomId);

    List<ReservationDto> listAllActive();

    List<ReservationDto> listAllExpired();

    List<JsonNode> retrieveRoomAlternatives(Integer reservationId, Http.Request request);

    ReservationDto updateReservation(ReservationDto reservation,Integer reservationId);
}
