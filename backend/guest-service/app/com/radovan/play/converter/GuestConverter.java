package com.radovan.play.converter;

import com.radovan.play.dto.GuestDto;
import com.radovan.play.entity.GuestEntity;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.modelmapper.ModelMapper;

@Singleton
public class GuestConverter {

    private ModelMapper mapper;

    @Inject
    private void initialize(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public GuestDto entityToDto(GuestEntity guestEntity){
        return mapper.map(guestEntity, GuestDto.class);
    }

    public GuestEntity dtoToEntity(GuestDto guestDto){
        return mapper.map(guestDto, GuestEntity.class);
    }
}
