package com.radovan.play.services;

import com.radovan.play.dto.GuestDto;
import com.radovan.play.utils.RegistrationForm;
import play.mvc.Http;

import java.util.List;

public interface GuestService {

    GuestDto getGuestById(Integer guestId);

    GuestDto getGuestByUserId(Integer userId);

    void deleteGuest(Integer guestId, Http.Request request);

    List<GuestDto> listAll();

    GuestDto storeGuest(RegistrationForm form);

    GuestDto getCurrentGuest(Http.Request request);
}
