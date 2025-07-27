package com.radovan.play.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.play.dto.GuestDto;

import java.io.Serializable;

public class RegistrationForm implements Serializable {

    private static final long serialVersionUID = 1L;


    private JsonNode user;

    private GuestDto guest;

    public JsonNode getUser() {
        return user;
    }

    public void setUser(JsonNode user) {
        this.user = user;
    }

    public GuestDto getGuest() {
        return guest;
    }

    public void setGuest(GuestDto guest) {
        this.guest = guest;
    }
}
