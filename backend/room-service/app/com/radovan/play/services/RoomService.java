package com.radovan.play.services;

import com.radovan.play.dto.RoomDto;

import java.util.List;

public interface RoomService {

    RoomDto addRoom(RoomDto room);

    RoomDto getRoomById(Integer roomId);

    void deleteRoom(Integer roomId);

    List<RoomDto> listAll();

    List<RoomDto> listAllByCategoryId(Integer categoryId);

    void deleteAllByCategoryId(Integer categoryId);

    RoomDto updateRoom(RoomDto room,Integer roomId);
}
