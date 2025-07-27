package com.radovan.play.services.impl;

import com.radovan.play.brokers.RoomNatsSender;
import com.radovan.play.converter.TempConverter;
import com.radovan.play.dto.RoomCategoryDto;
import com.radovan.play.dto.RoomDto;
import com.radovan.play.entity.RoomEntity;
import com.radovan.play.exceptions.ExistingInstanceException;
import com.radovan.play.exceptions.InstanceUndefinedException;
import com.radovan.play.repositories.RoomRepository;
import com.radovan.play.services.RoomCategoryService;
import com.radovan.play.services.RoomService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class RoomServiceImpl implements RoomService {

    private RoomRepository roomRepository;
    private TempConverter tempConverter;
    private RoomCategoryService categoryService;
    private RoomNatsSender natsSender;

    @Inject
    private void initialize(RoomRepository roomRepository, TempConverter tempConverter, RoomCategoryService categoryService, RoomNatsSender natsSender) {
        this.roomRepository = roomRepository;
        this.tempConverter = tempConverter;
        this.categoryService = categoryService;
        this.natsSender = natsSender;
    }

    @Override
    public RoomDto addRoom(RoomDto room) {
        RoomCategoryDto roomCategory = categoryService.getCategoryById(room.getRoomCategoryId());
        Integer roomId = room.getRoomId();
        Optional<RoomEntity> existingRoomOptional = roomRepository.findByRoomNumber(room.getRoomNumber());
        existingRoomOptional.ifPresent(existingRoom -> {
            if(Objects.equals(existingRoom.getRoomNumber(), room.getRoomNumber())){
                throw new ExistingInstanceException("This room number exists already!");
            }
        });

        room.setPrice(roomCategory.getPrice());
        RoomEntity roomEntity = tempConverter.roomDtoToEntity(room);
        RoomEntity storedRoom = roomRepository.save(roomEntity);
        return tempConverter.roomEntityToDto(storedRoom);
    }

    @Override
    public RoomDto getRoomById(Integer roomId) {
        RoomEntity roomEntity = roomRepository.findById(roomId)
                .orElseThrow(() -> new InstanceUndefinedException("The room has not been found!"));
        return tempConverter.roomEntityToDto(roomEntity);
    }

    @Override
    public void deleteRoom(Integer roomId) {
        getRoomById(roomId);
        roomRepository.deleteById(roomId);
        natsSender.sendRoomDeletedEvent(roomId);
    }

    @Override
    public List<RoomDto> listAll() {
        List<RoomEntity> allRooms = roomRepository.findAll();
        return allRooms.stream().map(tempConverter::roomEntityToDto).collect(Collectors.toList());
    }

    @Override
    public List<RoomDto> listAllByCategoryId(Integer categoryId) {
        categoryService.getCategoryById(categoryId);
        List<RoomEntity> allRooms = roomRepository.findAllByCategoryId(categoryId);
        return allRooms.stream().map(tempConverter::roomEntityToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteAllByCategoryId(Integer categoryId) {
        categoryService.getCategoryById(categoryId);
        listAllByCategoryId(categoryId).forEach(room -> deleteRoom(room.getRoomId()));
    }

    @Override
    public RoomDto updateRoom(RoomDto room, Integer roomId) {
        RoomCategoryDto roomCategory = categoryService.getCategoryById(room.getRoomCategoryId());
        RoomDto currentRoom = getRoomById(roomId);
        Optional<RoomEntity> existingRoomOptional = roomRepository.findByRoomNumber(room.getRoomNumber());
        existingRoomOptional.ifPresent(existingRoom -> {
            if(Objects.equals(existingRoom.getRoomNumber(), room.getRoomNumber())){
                if(!Objects.equals(currentRoom.getRoomId(), existingRoom.getRoomId())){
                    throw new ExistingInstanceException("This room number exists already!");
                }
            }
        });
        room.setRoomId(currentRoom.getRoomId());
        room.setPrice(roomCategory.getPrice());
        RoomEntity updatedRoom = roomRepository.save(tempConverter.roomDtoToEntity(room));
        return tempConverter.roomEntityToDto(updatedRoom);
    }
}
