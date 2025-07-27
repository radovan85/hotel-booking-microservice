package com.radovan.play.controllers;

import com.radovan.play.dto.RoomDto;
import com.radovan.play.exceptions.DataNotValidatedException;
import com.radovan.play.security.JwtAuthAction;
import com.radovan.play.security.RoleSecured;
import com.radovan.play.services.RoomService;
import jakarta.inject.Inject;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

@With(JwtAuthAction.class)
public class RoomController extends Controller {

    private RoomService roomService;
    private FormFactory formFactory;

    @Inject
    private void initialize(RoomService roomService, FormFactory formFactory) {
        this.roomService = roomService;
        this.formFactory = formFactory;
    }

    public Result getAllRooms(){
        return ok(Json.toJson(roomService.listAll()));
    }

    public Result getRoomDetails(Integer roomId){
        return ok(Json.toJson(roomService.getRoomById(roomId)));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result saveRoom(Http.Request request){
        Form<RoomDto> roomForm = formFactory.form(RoomDto.class).bindFromRequest(request);
        if (roomForm.hasErrors()) {
            throw new DataNotValidatedException("Room data is not valid!");
        }

        RoomDto room = roomForm.get();
        RoomDto storedRoom = roomService.addRoom(room);
        return ok(Json.toJson("Room with id " + storedRoom.getRoomId() + " has been stored!"));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result updateRoom(Http.Request request, Integer roomId){
        Form<RoomDto> roomForm = formFactory.form(RoomDto.class).bindFromRequest(request);
        if (roomForm.hasErrors()) {
            throw new DataNotValidatedException("Room data is not valid!");
        }

        RoomDto room = roomForm.get();
        RoomDto updatedRoom = roomService.updateRoom(room, roomId);
        return ok(Json.toJson("Room with id " + updatedRoom.getRoomId() + " has been updated without any issues!"));
    }

    @RoleSecured({"ROLE_ADMIN"})
    public Result deleteRoom(Integer roomId){
        roomService.deleteRoom(roomId);
        return ok(Json.toJson("Room with id " + roomId + " has been permanently deleted!"));
    }


}
