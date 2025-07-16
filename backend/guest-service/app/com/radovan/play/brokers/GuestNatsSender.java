package com.radovan.play.brokers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.radovan.play.exceptions.ExistingInstanceException;
import com.radovan.play.utils.NatsUtils;
import io.nats.client.Message;
import io.nats.client.impl.Headers;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import play.mvc.Http;

import java.time.Duration;

@Singleton
public class GuestNatsSender {

    private final NatsUtils natsUtils;
    private final ObjectMapper objectMapper;
    private static final String USER_RESPONSE_QUEUE = "user.response";

    @Inject
    public GuestNatsSender(NatsUtils natsUtils, ObjectMapper objectMapper) {
        this.natsUtils = natsUtils;
        this.objectMapper = objectMapper;
    }

    public Integer sendUserCreate(JsonNode userPayload) throws ExistingInstanceException, Exception {
        try {
            byte[] payloadBytes = objectMapper.writeValueAsBytes(userPayload);
            Message reply = natsUtils.getConnection()
                    .request("user.create", payloadBytes, Duration.ofSeconds(2));

            JsonNode response = objectMapper.readTree(reply.getData());
            int status = response.has("status") ? response.get("status").asInt() : 500;

            if (status == 200 && response.has("id")) {
                return response.get("id").asInt();
            } else if (status == 409) {
                throw new ExistingInstanceException("Email already exists.");
            } else {
                String msg = response.has("message") ? response.get("message").asText() : "Unknown error.";
                throw new Exception("User creation failed: " + msg);
            }

        } catch (ExistingInstanceException e) {
            throw e;
        } catch (Exception ex) {
            throw new Exception("NATS user.create failed: " + ex.getMessage(), ex);
        }
    }

    public void sendDeleteUserEvent(Integer userId, Http.Request request) {
        sendUserEvent("user.delete." + userId, userId,request);
    }

    public JsonNode retrieveCurrentUser(Http.Request request) {
        try {
            ObjectNode payload = objectMapper.createObjectNode();
            String token = request.header("Authorization").orElse(null);
            if (token == null || token.isBlank()) {
                throw new RuntimeException("Authorization token missing");
            }

            payload.put("token", token.replace("Bearer ", ""));

            byte[] payloadBytes = objectMapper.writeValueAsBytes(payload);
            Message reply = natsUtils.getConnection()
                    .request("user.get", payloadBytes, Duration.ofSeconds(2));

            if (reply == null || reply.getData() == null) {
                throw new RuntimeException("No reply received from user.get");
            }

            JsonNode response = objectMapper.readTree(reply.getData());
            int status = response.has("status") ? response.get("status").asInt() : 200;

            if (status >= 400) {
                String msg = response.has("message") ? response.get("message").asText() : "Unknown error.";
                throw new RuntimeException("Failed to fetch current user: " + msg);
            }

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving current user: " + e.getMessage(), e);
        }
    }

    public void sendReservationsDeleteRequest(Integer guestId) {
        try {
            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("guestId", guestId);

            byte[] payloadBytes = objectMapper.writeValueAsBytes(payload);
            natsUtils.getConnection().publish("reservation.delete.byGuest", payloadBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error sending reservation deletion by guestId: " + guestId, e);
        }
    }



    private void sendUserEvent(String subject, Integer userId,Http.Request request) {
        try {
            byte[] payload = createUserEventPayload(userId);
            Headers headers = createAuthorizationHeaders(request);
            natsUtils.getConnection().publish(subject, headers, payload);
        } catch (Exception e) {
            throw new RuntimeException("Error sending user event: " + subject, e);
        }
    }

    private byte[] createUserEventPayload(Integer userId) throws Exception {
        ObjectNode nodeRequest = objectMapper.createObjectNode();
        nodeRequest.put("userId", userId);
        return objectMapper.writeValueAsBytes(nodeRequest);
    }

    private Headers createAuthorizationHeaders(Http.Request request) {
        Headers headers = new Headers();
        headers.add("Nats-Reply-To", USER_RESPONSE_QUEUE);
        String token = request.header("Authorization").orElse(null);
        headers.add("Authorization", token);
        return headers;
    }
}
