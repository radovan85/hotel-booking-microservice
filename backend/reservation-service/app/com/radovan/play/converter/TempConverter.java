package com.radovan.play.converter;

import com.radovan.play.dto.NoteDto;
import com.radovan.play.dto.ReservationDto;
import com.radovan.play.entity.NoteEntity;
import com.radovan.play.entity.ReservationEntity;
import com.radovan.play.utils.TimeConversionUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.modelmapper.ModelMapper;

import java.sql.Timestamp;
import java.util.Optional;

@Singleton
public class TempConverter {

    private ModelMapper mapper;
    private TimeConversionUtils conversionUtils;

    @Inject
    private void initialize(TimeConversionUtils conversionUtils, ModelMapper mapper) {
        this.conversionUtils = conversionUtils;
        this.mapper = mapper;
    }

    public NoteDto noteEntityToDto(NoteEntity noteEntity){
        NoteDto returnValue = mapper.map(noteEntity, NoteDto.class);
        Optional<Timestamp> createTimeOptional = Optional.ofNullable(noteEntity.getCreateTime());
        createTimeOptional.ifPresent(createTime -> {
            returnValue.setCreateTimeStr(conversionUtils.timestampToString(createTime));
        });

        return returnValue;
    }

    public NoteEntity noteDtoToEntity(NoteDto noteDto) {
        return mapper.map(noteDto, NoteEntity.class);
    }

    public ReservationDto reservationEntityToDto(ReservationEntity reservation){
        ReservationDto returnValue = mapper.map(reservation, ReservationDto.class);
        Optional<Timestamp> checkInDateOptional = Optional.ofNullable(reservation.getCheckInDate());
        checkInDateOptional.ifPresent(checkInDate -> {
            returnValue.setCheckInDateStr(conversionUtils.timestampToString(checkInDate));
        });

        Optional<Timestamp> checkOutDateOptional = Optional.ofNullable(reservation.getCheckOutDate());
        checkOutDateOptional.ifPresent(checkOutDate -> {
            returnValue.setCheckOutDateStr(conversionUtils.timestampToString(checkOutDate));
        });

        Optional<Timestamp> createTimeOptional = Optional.ofNullable(reservation.getCreateTime());
        createTimeOptional.ifPresent(createTime -> {
            returnValue.setCreateTimeStr(conversionUtils.timestampToString(createTime));
        });

        Optional<Timestamp> updateTimeOptional = Optional.ofNullable(reservation.getUpdateTime());
        updateTimeOptional.ifPresent(updateTime -> {
            returnValue.setUpdateTimeStr(conversionUtils.timestampToString(updateTime));
        });

        return returnValue;
    }

    public ReservationEntity reservationDtoToEntity(ReservationDto reservation){
        return mapper.map(reservation, ReservationEntity.class);
    }


}
