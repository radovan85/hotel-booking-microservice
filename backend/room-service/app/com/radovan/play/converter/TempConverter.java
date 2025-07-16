package com.radovan.play.converter;

import com.radovan.play.dto.RoomDto;
import com.radovan.play.dto.RoomCategoryDto;
import com.radovan.play.entity.RoomCategoryEntity;
import com.radovan.play.entity.RoomEntity;
import com.radovan.play.repositories.RoomCategoryRepository;
import com.radovan.play.repositories.RoomRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class TempConverter {

    private ModelMapper mapper;
    private RoomRepository roomRepository;
    private RoomCategoryRepository categoryRepository;

    @Inject
    private void initialize(RoomRepository roomRepository, ModelMapper mapper, RoomCategoryRepository categoryRepository) {
        this.roomRepository = roomRepository;
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
    }

    public RoomCategoryDto roomCategoryEntityToDto(RoomCategoryEntity categoryEntity){
        RoomCategoryDto returnValue = mapper.map(categoryEntity, RoomCategoryDto.class);
        Optional<Byte> wifiOptional = Optional.ofNullable(categoryEntity.getWifi());
        wifiOptional.ifPresent(wifi -> returnValue.setWifi(wifi.shortValue()));

        Optional<Byte> wcOptional = Optional.ofNullable(categoryEntity.getWc());
        wcOptional.ifPresent(wc -> returnValue.setWc(wc.shortValue()));

        Optional<Byte> tvOptional = Optional.ofNullable(categoryEntity.getTv());
        tvOptional.ifPresent(tv -> returnValue.setTv(tv.shortValue()));

        Optional<Byte> barOptional = Optional.ofNullable(categoryEntity.getBar());
        barOptional.ifPresent(bar -> returnValue.setBar(bar.shortValue()));

        Optional<List<RoomEntity>> roomsOptional = Optional.ofNullable(categoryEntity.getRooms());
        List<Integer> roomsIds = new ArrayList<>();
        roomsOptional.ifPresent(rooms -> {
            rooms.forEach(roomEntity -> roomsIds.add(roomEntity.getRoomId()));
        });
        returnValue.setRoomsIds(roomsIds);

        return returnValue;
    }

    public RoomCategoryEntity roomCategoryDtoToEntity(RoomCategoryDto categoryDto){
        RoomCategoryEntity returnValue = mapper.map(categoryDto,RoomCategoryEntity.class);
        Optional<Short> wcOptional = Optional.ofNullable(categoryDto.getWc());
        wcOptional.ifPresent(wc -> returnValue.setWc(wc.byteValue()));

        Optional<Short> wifiOptional = Optional.ofNullable(categoryDto.getWifi());
        wifiOptional.ifPresent(wifi -> returnValue.setWifi(wifi.byteValue()));

        Optional<Short> tvOptional = Optional.ofNullable(categoryDto.getTv());
        tvOptional.ifPresent(tv -> returnValue.setTv(tv.byteValue()));

        Optional<Short> barOptional = Optional.ofNullable(categoryDto.getBar());
        barOptional.ifPresent(bar -> returnValue.setBar(bar.byteValue()));

        Optional<List<Integer>> roomsIdsOptional = Optional.ofNullable(categoryDto.getRoomsIds());
        List<RoomEntity> rooms = new ArrayList<>();
        roomsIdsOptional.ifPresent(roomsIds -> {
            roomsIds.forEach(roomId -> {
                roomRepository.findById(roomId).ifPresent(rooms::add);
            });
        });
        returnValue.setRooms(rooms);

        return returnValue;
    }

    public RoomDto roomEntityToDto(RoomEntity roomEntity){
        RoomDto returnValue = mapper.map(roomEntity, RoomDto.class);
        Optional<RoomCategoryEntity> categoryOptional = Optional.ofNullable(roomEntity.getRoomCategory());
        categoryOptional.ifPresent(categoryEntity -> returnValue.setRoomCategoryId(categoryEntity.getRoomCategoryId()));
        return returnValue;
    }

    public RoomEntity roomDtoToEntity(RoomDto roomDto){
        RoomEntity returnValue = mapper.map(roomDto,RoomEntity.class);
        Optional<Integer> categoryIdOptional = Optional.ofNullable(roomDto.getRoomCategoryId());
        categoryIdOptional.flatMap(categoryId -> categoryRepository.findById(categoryId)).ifPresent(returnValue::setRoomCategory);
        return returnValue;
    }
}
