package com.radovan.play.brokers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.radovan.play.utils.NatsUtils;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import play.mvc.Http;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Singleton
public class ReservationNatsSender {

    private final NatsUtils natsUtils;
    private final ObjectMapper objectMapper;

    @Inject
    public ReservationNatsSender(NatsUtils natsUtils, ObjectMapper objectMapper) {
        this.natsUtils = natsUtils;
        this.objectMapper = objectMapper;
    }

    public Map<String, String> getAuthHeaders(Http.Request request) {
        return request.headers().asMap().entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase("Authorization"))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0)));
    }

    public List<JsonNode> listAllCategories(Http.Request request) {
        try {
            Connection nc = natsUtils.getConnection();
            String inbox = nc.createInbox();
            CompletableFuture<String> responseFuture = new CompletableFuture<>();

            Dispatcher dispatcher = nc.createDispatcher((Message msg) -> {
                responseFuture.complete(new String(msg.getData()));
            });
            dispatcher.subscribe(inbox);

            // publish nakon subscribe
            nc.publish("room.categories.list", inbox, new byte[0]);

            String response = responseFuture.get(); // možeš dodati .get(timeout) za sigurnost
            JsonNode responseNode = objectMapper.readTree(response);

            if (responseNode.isArray()) {
                List<JsonNode> result = new ArrayList<>();
                responseNode.forEach(result::add);
                return result;
            } else {
                System.out.println("❌ Unexpected room-service response: " + responseNode.toPrettyString());
                throw new RuntimeException("Unexpected response from room-service");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to fetch room categories via NATS");
        }
    }

    public List<JsonNode> listAllRoomsByCategoryId(Integer categoryId, Http.Request request) {
        try {
            Connection nc = natsUtils.getConnection();
            String inbox = nc.createInbox();
            CompletableFuture<String> responseFuture = new CompletableFuture<>();

            Dispatcher dispatcher = nc.createDispatcher((Message msg) -> {
                responseFuture.complete(new String(msg.getData()));
            });
            dispatcher.subscribe(inbox);

            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("categoryId", categoryId);

            // publish nakon subscribe
            nc.publish("room.byCategory.list", inbox, payload.toString().getBytes());

            String response = responseFuture.get();
            JsonNode responseNode = objectMapper.readTree(response);

            if (responseNode.isArray()) {
                List<JsonNode> result = new ArrayList<>();
                responseNode.forEach(result::add);
                return result;
            } else {
                System.out.println("❌ Unexpected room-service response: " + responseNode.toPrettyString());
                throw new RuntimeException("Unexpected response from room-service");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to fetch rooms by category via NATS");
        }
    }

    public JsonNode getUserById(Integer userId) {
        try {
            Connection nc = natsUtils.getConnection();
            String inbox = nc.createInbox();
            CompletableFuture<String> responseFuture = new CompletableFuture<>();

            Dispatcher dispatcher = nc.createDispatcher((Message msg) -> {
                responseFuture.complete(new String(msg.getData(), StandardCharsets.UTF_8));
            });
            dispatcher.subscribe(inbox);

            // Publish request
            nc.publish("user.get." + userId, inbox, new byte[0]);

            // Čekaj response
            String response = responseFuture.get(); // možeš dodati timeout

            return objectMapper.readTree(response);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("❌ Failed to fetch user via NATS");
        }
    }

    public JsonNode getRoomById(Integer roomId) {
        try {
            Connection nc = natsUtils.getConnection();
            String inbox = nc.createInbox();
            CompletableFuture<String> responseFuture = new CompletableFuture<>();

            Dispatcher dispatcher = nc.createDispatcher((Message msg) -> {
                responseFuture.complete(new String(msg.getData(), StandardCharsets.UTF_8));
            });
            dispatcher.subscribe(inbox);

            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("roomId", roomId);

            // publish nakon subscribe
            nc.publish("room.byId.get", inbox, payload.toString().getBytes());

            // Čekaj response
            String response = responseFuture.get(); // možeš dodati timeout
            return objectMapper.readTree(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("❌ Failed to fetch room by ID via NATS");
        }
    }

    public JsonNode getGuestById(Integer guestId) {
        try {
            Connection nc = natsUtils.getConnection();
            String inbox = nc.createInbox();
            CompletableFuture<String> responseFuture = new CompletableFuture<>();

            Dispatcher dispatcher = nc.createDispatcher((Message msg) -> {
                responseFuture.complete(new String(msg.getData(), StandardCharsets.UTF_8));
            });
            dispatcher.subscribe(inbox);

            ObjectNode payload = objectMapper.createObjectNode();
            payload.put("guestId", guestId);

            nc.publish("guest.byId.get", inbox, payload.toString().getBytes());

            String response = responseFuture.get(); // možeš dodati timeout
            return objectMapper.readTree(response);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("❌ Failed to fetch guest by ID via NATS");
        }
    }







}
