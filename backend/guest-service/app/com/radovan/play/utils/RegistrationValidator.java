package com.radovan.play.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.data.FormFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class RegistrationValidator {

    private  FormFactory formFactory;
    private  ObjectMapper objectMapper;

    @Inject
    private void initialize(FormFactory formFactory, ObjectMapper objectMapper) {
        this.formFactory = formFactory;
        this.objectMapper = objectMapper;
    }

    public List<String> validate(JsonNode body) {
        List<String> errors = new ArrayList<>();

        // Već postoji validacija za guest...

        // === User validacija ===
        JsonNode user = body.get("user");
        if (user == null) {
            errors.add("Missing 'user' object.");
        } else {
            // firstName
            String firstName = asText(user, "firstName");
            if (firstName == null || firstName.length() < 2 || firstName.length() > 30) {
                errors.add("user.firstName must be between 2 and 30 characters.");
            }

            // lastName
            String lastName = asText(user, "lastName");
            if (lastName == null || lastName.length() < 2 || lastName.length() > 30) {
                errors.add("user.lastName must be between 2 and 30 characters.");
            }

            // email
            String email = asText(user, "email");
            if (email == null || email.length() > 50 || !email.matches(".+@.+\\..+")) {
                errors.add("user.email must be a valid email address (max 50 characters).");
            }

            // password
            String password = asText(user, "password");
            if (password == null || password.length() < 6) {
                errors.add("user.password must be at least 6 characters.");
            }
        }

        return errors;
    }

    private String asText(JsonNode node, String field) {
        JsonNode child = node.get(field);
        if (child == null || child.isNull() || !child.isTextual()) return null;
        return child.asText();
    }

}

