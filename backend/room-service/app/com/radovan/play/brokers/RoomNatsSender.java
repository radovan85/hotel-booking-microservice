package com.radovan.play.brokers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.radovan.play.utils.NatsUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import play.mvc.Http;

import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class RoomNatsSender {

    private final NatsUtils natsUtils;
    private final ObjectMapper objectMapper;

    @Inject
    public RoomNatsSender(NatsUtils natsUtils, ObjectMapper objectMapper) {
        this.natsUtils = natsUtils;
        this.objectMapper = objectMapper;
    }

    public Map<String, String> getAuthHeaders(Http.Request request) {
        return request.headers().asMap().entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase("Authorization"))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0)));
    }

    public void sendRoomDeletedEvent(Integer roomId) {
        try {
            Map<String, Integer> payload = Map.of("roomId", roomId);
            byte[] data = objectMapper.writeValueAsBytes(payload);
            natsUtils.getConnection().publish("reservation.delete.byRoom", data);
        } catch (Exception e) {
            System.err.println("Failed to send RoomDeletedEvent: " + e.getMessage());
        }
    }

}
