package com.radovan.play.brokers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.radovan.play.dto.RoomCategoryDto;
import com.radovan.play.dto.RoomDto;
import com.radovan.play.services.RoomCategoryService;
import com.radovan.play.services.RoomService;
import com.radovan.play.utils.NatsUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import io.nats.client.Message;
import io.nats.client.Dispatcher;

import java.util.List;

@Singleton
public class RoomNatsListener {

    private final ObjectMapper objectMapper;
    private final NatsUtils natsUtils;
    private final RoomService roomService;
    private final RoomCategoryService categoryService;

    @Inject
    public RoomNatsListener(ObjectMapper objectMapper, NatsUtils natsUtils, RoomService roomService, RoomCategoryService categoryService) {
        this.objectMapper = objectMapper;
        this.natsUtils = natsUtils;
        this.roomService = roomService;
        this.categoryService = categoryService;
        initListeners();
    }

    private void initListeners() {
        Dispatcher dispatcher = natsUtils.getConnection().createDispatcher();
        dispatcher.subscribe("room.categories.list", this::handleCategories);
        dispatcher.subscribe("room.byCategory.list", this::handleRoomsByCategory);
        dispatcher.subscribe("room.byId.get", this::handleRoomById);

    }

    private void handleCategories(Message msg) {
        try {
            //System.out.println("🔔 Received 'room.categories.list' request");
            List<RoomCategoryDto> categories = categoryService.listAll();
            JsonNode categoryArray = objectMapper.valueToTree(categories);

            if (msg.getReplyTo() != null) {
                natsUtils.getConnection().publish(msg.getReplyTo(), categoryArray.toString().getBytes());
                //System.out.println("📤 Responded with " + categories.size() + " categories");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            if (msg.getReplyTo() != null) {
                ObjectNode error = objectMapper.createObjectNode();
                error.put("error", "Failed to fetch room categories");
                natsUtils.getConnection().publish(msg.getReplyTo(), error.toString().getBytes());
            }
        }
    }

    private void handleRoomsByCategory(Message msg) {
        try {
            String payload = new String(msg.getData());
            JsonNode requestNode = objectMapper.readTree(payload);

            if (!requestNode.has("categoryId")) {
                System.out.println("⚠️ Missing categoryId in payload");
                respondWithError(msg, "Missing categoryId");
                return;
            }

            Integer categoryId = requestNode.get("categoryId").asInt();

            List<RoomDto> rooms = roomService.listAllByCategoryId(categoryId);
            JsonNode roomArray = objectMapper.valueToTree(rooms);

            if (msg.getReplyTo() != null) {
                natsUtils.getConnection().publish(msg.getReplyTo(), roomArray.toString().getBytes());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            respondWithError(msg, "Failed to fetch rooms by category");
        }
    }

    private void handleRoomById(Message msg) {
        try {
            String payload = new String(msg.getData());
            JsonNode requestNode = objectMapper.readTree(payload);

            if (!requestNode.has("roomId")) {
                respondWithError(msg, "Missing roomId");
                return;
            }

            Integer roomId = requestNode.get("roomId").asInt();
            RoomDto room = roomService.getRoomById(roomId);
            JsonNode roomNode = objectMapper.valueToTree(room);

            if (msg.getReplyTo() != null) {
                natsUtils.getConnection().publish(msg.getReplyTo(), roomNode.toString().getBytes());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            respondWithError(msg, "Failed to fetch room by ID");
        }
    }


    private void respondWithError(Message msg, String errorMessage) {
        if (msg.getReplyTo() != null) {
            ObjectNode error = objectMapper.createObjectNode();
            error.put("error", errorMessage);
            natsUtils.getConnection().publish(msg.getReplyTo(), error.toString().getBytes());
            System.out.println("❌ Error response sent: " + errorMessage);
        }
    }
}
