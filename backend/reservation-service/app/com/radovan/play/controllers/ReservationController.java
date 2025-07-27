package com.radovan.play.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.play.dto.ReservationDto;
import com.radovan.play.exceptions.DataNotValidatedException;
import com.radovan.play.security.JwtAuthAction;
import com.radovan.play.security.RoleSecured;
import com.radovan.play.services.ReservationService;
import com.radovan.play.utils.ReservationTimeForm;
import jakarta.inject.Inject;
import play.libs.Json;
import play.mvc.*;

import java.util.List;

@With(JwtAuthAction.class)
public class ReservationController extends Controller {

    private ReservationService reservationService;

    @Inject
    private void initialize(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @RoleSecured({"ROLE_USER"})
    public Result provideReservations(Http.Request request) {
        JsonNode body = request.body().asJson();
        if (body == null || !body.has("checkInDate") || !body.has("checkOutDate")) {
            throw new DataNotValidatedException("Reservation data is not valid!");
        }

        ReservationTimeForm form = Json.fromJson(body, ReservationTimeForm.class);
        List<ReservationDto> availableReservations = reservationService.listAvailableBookings(form, request);

        return ok(Json.toJson(availableReservations));
    }

    @RoleSecured({"ROLE_USER"})
    @BodyParser.Of(BodyParser.Json.class)
    public Result addReservation(Http.Request request) {
        JsonNode body = request.body().asJson();
        if (body == null || !body.has("roomId") || !body.has("guestId") || !body.has("checkInDateStr") || !body.has("checkOutDateStr")) {
            throw new DataNotValidatedException("Booking payload is incomplete!");
        }

        ReservationDto reservation = Json.fromJson(body, ReservationDto.class);
        ReservationDto result = reservationService.addReservation(reservation, request);

        return ok(Json.toJson(result));
    }

    @RoleSecured({"ROLE_USER"})
    public Result provideMyReservations(Http.Request request){
        return ok(Json.toJson(reservationService.getMyReservations(request)));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result getAllReservations(){
        return ok(Json.toJson(reservationService.listAll()));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result getAllActiveReservations(){
        return ok(Json.toJson(reservationService.listAllActive()));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result getAllExpiredReservations(){
        return ok(Json.toJson(reservationService.listAllExpired()));
    }

    @RoleSecured({"ROLE_USER"})
    public Result cancelReservation(Integer reservationId,Http.Request request){
        reservationService.cancelReservation(reservationId,request);
        return ok(Json.toJson("Your reservation has been cancelled"));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result deleteReservation(Integer reservationId){
        reservationService.deleteReservation(reservationId);
        return ok(Json.toJson("Reservation with id: " + reservationId + " has been cancelled"));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result getReservationDetails(Integer reservationId){
        return ok(Json.toJson(reservationService.getReservationById(reservationId)));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result findAlternativeRooms(Integer reservationId,Http.Request request){
        return ok(Json.toJson(reservationService.retrieveRoomAlternatives(reservationId, request)));
    }

    @RoleSecured({"ROLE_ADMIN"})
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateReservation(Integer reservationId,Http.Request request){
        JsonNode body = request.body().asJson();
        if (body == null || !body.has("roomId") || !body.has("guestId") || !body.has("price") || !body.has("numberOfNights")) {
            throw new DataNotValidatedException("Updating payload is incomplete!");
        }

        ReservationDto reservation = Json.fromJson(body, ReservationDto.class);
        ReservationDto updatedReservation = reservationService.updateReservation(reservation,reservationId);
        return ok(Json.toJson("Reservation with id " + updatedReservation.getReservationId() + " has been updated!"));
    }

}
