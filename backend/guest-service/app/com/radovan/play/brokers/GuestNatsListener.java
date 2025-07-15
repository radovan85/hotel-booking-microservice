package com.radovan.play.brokers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.radovan.play.dto.GuestDto;
import com.radovan.play.services.GuestService;
import com.radovan.play.utils.NatsUtils;
import io.nats.client.Message;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import io.nats.client.Dispatcher;

@Singleton
public class GuestNatsListener {

    private final ObjectMapper objectMapper;
    private final GuestService guestService;
    private final NatsUtils natsUtils;

    @Inject
    public GuestNatsListener(ObjectMapper objectMapper, GuestService guestService, NatsUtils natsUtils) {
        this.objectMapper = objectMapper;
        this.guestService = guestService;
        this.natsUtils = natsUtils;
        initListeners();
    }

    private void initListeners() {
        Dispatcher dispatcher = natsUtils.getConnection().createDispatcher(this::handleGuestById);
        dispatcher.subscribe("guest.byId.get"); // mora subject!
    }


    private void handleGuestById(Message msg){
        try {
            String payload = new String(msg.getData());
            JsonNode requestNode = objectMapper.readTree(payload);

            if (!requestNode.has("guestId")) {
                respondWithError(msg, "Missing guestId");
                return;
            }

            Integer guestId = requestNode.get("guestId").asInt();
            GuestDto guest = guestService.getGuestById(guestId);
            JsonNode guestNode = objectMapper.valueToTree(guest);

            if (msg.getReplyTo() != null) {
                natsUtils.getConnection().publish(msg.getReplyTo(), guestNode.toString().getBytes());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            respondWithError(msg, "Failed to fetch guest by ID");
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
