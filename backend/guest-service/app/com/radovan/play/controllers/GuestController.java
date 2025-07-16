package com.radovan.play.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radovan.play.security.JwtAuthAction;
import com.radovan.play.security.RoleSecured;
import com.radovan.play.services.GuestService;
import com.radovan.play.utils.RegistrationForm;
import com.radovan.play.utils.RegistrationValidator;
import jakarta.inject.Inject;
import play.libs.Json;
import play.mvc.*;

import java.util.List;

public class GuestController extends Controller {

    private GuestService guestService;
    private RegistrationValidator validator;
    private ObjectMapper objectMapper;

    @Inject
    private void initialize(GuestService guestService, RegistrationValidator validator, ObjectMapper objectMapper) {
        this.guestService = guestService;
        this.validator = validator;
        this.objectMapper = objectMapper;
    }

    @With(JwtAuthAction.class)
    @RoleSecured({"ROLE_ADMIN"})
    public Result listAllGuests() {
        return ok(Json.toJson(guestService.listAll()));
    }

    @With(JwtAuthAction.class)
    @RoleSecured({"ROLE_ADMIN"})
    public Result deleteGuest(Integer guestId, Http.Request request) {
        guestService.deleteGuest(guestId, request);
        return ok(Json.toJson("The guest with id " + guestId + " has been permanently deleted"));
    }

    @With(JwtAuthAction.class)
    @RoleSecured({"ROLE_ADMIN"})
    public Result guestDetails(Integer guestId) {
        return ok(Json.toJson(guestService.getGuestById(guestId)));
    }

    public Result createGuest(Http.Request request) throws JsonProcessingException {
        JsonNode body = request.body().asJson();

        List<String> validationErrors = validator.validate(body);
        if (!validationErrors.isEmpty()) {
            return Results.badRequest(Json.toJson(validationErrors));
        }

        RegistrationForm form = objectMapper.treeToValue(body, RegistrationForm.class);

        guestService.storeGuest(form);
        return ok(Json.toJson("Registration completed!"));


    }

    public Result getCurrentGuest(Http.Request request){
        return ok(Json.toJson(guestService.getCurrentGuest(request)));
    }

}
