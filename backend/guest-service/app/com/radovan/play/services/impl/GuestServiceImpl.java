package com.radovan.play.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.play.brokers.GuestNatsSender;
import com.radovan.play.converter.GuestConverter;
import com.radovan.play.dto.GuestDto;
import com.radovan.play.entity.GuestEntity;
import com.radovan.play.exceptions.ExistingInstanceException;
import com.radovan.play.exceptions.InstanceUndefinedException;
import com.radovan.play.repositories.GuestRepository;
import com.radovan.play.services.GuestService;
import com.radovan.play.utils.RegistrationForm;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import play.mvc.Http;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class GuestServiceImpl implements GuestService {

    private GuestRepository guestRepository;
    private GuestConverter guestConverter;
    private GuestNatsSender natsSender;

    @Inject
    private void initialize(GuestRepository guestRepository, GuestConverter guestConverter, GuestNatsSender natsSender) {
        this.guestRepository = guestRepository;
        this.guestConverter = guestConverter;
        this.natsSender = natsSender;
    }

    @Override
    public GuestDto getGuestById(Integer guestId) {
        GuestEntity guestEntity = guestRepository.findById(guestId)
                .orElseThrow(() -> new InstanceUndefinedException("The guest has not been found!"));
        return guestConverter.entityToDto(guestEntity);
    }

    @Override
    public GuestDto getGuestByUserId(Integer userId) {
        GuestEntity guestEntity = guestRepository.findByUserId(userId)
                .orElseThrow(() -> new InstanceUndefinedException("The guest has not been found!"));
        return guestConverter.entityToDto(guestEntity);
    }

    @Override
    public void deleteGuest(Integer guestId, Http.Request request) {
        GuestDto guest =  getGuestById(guestId);
        guestRepository.deleteById(guestId);
        natsSender.sendDeleteUserEvent(guest.getUserId(), request);
        natsSender.sendReservationsDeleteRequest(guest.getGuestId());
    }

    @Override
    public List<GuestDto> listAll() {
        List<GuestEntity> allGuests = guestRepository.findAll();
        return allGuests.stream().map(guestConverter::entityToDto).collect(Collectors.toList());
    }

    @Override
    public GuestDto storeGuest(RegistrationForm form) {
        try {
            JsonNode user = form.getUser();
            GuestDto guest = form.getGuest();
            guest.setUserId(natsSender.sendUserCreate(user));
            GuestEntity storedGuest = guestRepository.save(guestConverter.dtoToEntity(guest));
            return guestConverter.entityToDto(storedGuest);
        } catch (ExistingInstanceException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user via NATS", e);
        }
    }

    @Override
    public GuestDto getCurrentGuest(Http.Request request) {
        JsonNode userData = natsSender.retrieveCurrentUser(request);
        Integer userId = userData.get("id").asInt();
        return getGuestByUserId(userId);
    }

}
