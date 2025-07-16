package com.radovan.play.brokers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radovan.play.services.ReservationService;
import com.radovan.play.utils.NatsUtils;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ReservationNatsListener {

    private static final Logger logger = LoggerFactory.getLogger(ReservationNatsListener.class);

    private final ReservationService reservationService;
    private final NatsUtils natsUtils;
    private final ObjectMapper objectMapper;

    @Inject
    public ReservationNatsListener(ReservationService reservationService, NatsUtils natsUtils, ObjectMapper objectMapper) {
        this.reservationService = reservationService;
        this.natsUtils = natsUtils;
        this.objectMapper = objectMapper;
        initListeners();
    }

    private void initListeners() {
        Dispatcher dispatcher = natsUtils.getConnection().createDispatcher();

        dispatcher.subscribe("reservation.delete.byGuest", this::handleDeleteAllByGuestId);
        dispatcher.subscribe("reservation.delete.byRoom", this::handleDeleteAllByRoomId);

        logger.info("Reservation NATS listeners initialized.");
    }

    private void handleDeleteAllByGuestId(Message msg) {
        try {
            JsonNode json = objectMapper.readTree(msg.getData());
            Integer guestId = json.get("guestId").asInt();
            reservationService.deleteAllByGuestId(guestId);
            logger.info("Deleted all reservations for guestId={}", guestId);
        } catch (Exception e) {
            logger.error("Failed to delete reservations by guest ID", e);
        }
    }

    private void handleDeleteAllByRoomId(Message msg) {
        try {
            JsonNode json = objectMapper.readTree(msg.getData());
            Integer roomId = json.get("roomId").asInt();
            reservationService.deleteAllByRoomId(roomId);
            logger.info("Deleted all reservations for roomId={}", roomId);
        } catch (Exception e) {
            logger.error("Failed to delete reservations by room ID", e);
        }
    }
}
