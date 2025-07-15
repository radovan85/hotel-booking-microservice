package com.radovan.play.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.radovan.play.brokers.ReservationNatsSender;
import com.radovan.play.converter.TempConverter;
import com.radovan.play.dto.NoteDto;
import com.radovan.play.dto.ReservationDto;
import com.radovan.play.entity.NoteEntity;
import com.radovan.play.entity.ReservationEntity;
import com.radovan.play.exceptions.InstanceUndefinedException;
import com.radovan.play.exceptions.OperationNotAllowedException;
import com.radovan.play.repositories.NoteRepository;
import com.radovan.play.repositories.ReservationRepository;
import com.radovan.play.services.ReservationService;
import com.radovan.play.utils.ReservationTimeForm;
import com.radovan.play.utils.ServiceUrlProvider;
import com.radovan.play.utils.TimeConversionUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Http;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ReservationServiceImpl implements ReservationService {

    private NoteRepository noteRepository;
    private ReservationRepository reservationRepository;
    private TempConverter tempConverter;
    private TimeConversionUtils conversionUtils;
    private WSClient wsClient;
    private ReservationNatsSender natsSender;
    private ServiceUrlProvider urlProvider;
    private XmlMapper xmlMapper;
    private final ZoneId zoneId = ZoneId.of("UTC");

    @Inject
    private void initialize(ReservationRepository reservationRepository, NoteRepository noteRepository, TempConverter tempConverter, TimeConversionUtils conversionUtils, WSClient wsClient, ReservationNatsSender natsSender, ServiceUrlProvider urlProvider, XmlMapper xmlMapper) {
        this.reservationRepository = reservationRepository;
        this.noteRepository = noteRepository;
        this.tempConverter = tempConverter;
        this.conversionUtils = conversionUtils;
        this.wsClient = wsClient;
        this.natsSender = natsSender;
        this.urlProvider = urlProvider;
        this.xmlMapper = xmlMapper;
    }

    @Override
    public List<ReservationDto> listAvailableBookings(ReservationTimeForm timeForm, Http.Request request) {
        String checkInStr = timeForm.getCheckInDate() + " 14:00";   // default check-in time
        String checkOutStr = timeForm.getCheckOutDate() + " 12:00"; // default check-out time

        conversionUtils.isValidPeriod(checkInStr, checkOutStr);

        List<ReservationDto> returnValue = new ArrayList<>();
        int numberOfNights = conversionUtils.calculateNumberOfNights(checkInStr, checkOutStr);

        JsonNode currentGuest = fetchCurrentGuest(request);
        assert currentGuest != null;
        Integer guestId = currentGuest.get("guestId").asInt();

        List<JsonNode> allCategories = natsSender.listAllCategories(request);
        Timestamp checkIn = conversionUtils.stringToTimestamp(checkInStr);
        Timestamp checkOut = conversionUtils.stringToTimestamp(checkOutStr);

        allCategories.forEach(categoryNode -> {
            if (!categoryNode.has("roomCategoryId") || !categoryNode.has("price")) return;

            Integer categoryId = categoryNode.get("roomCategoryId").asInt();
            Float pricePerNight = categoryNode.get("price").floatValue();
            List<JsonNode> rooms = natsSender.listAllRoomsByCategoryId(categoryId, request);

            for (JsonNode roomNode : rooms) {
                if (!roomNode.has("roomId")) continue;

                Integer roomId = roomNode.get("roomId").asInt();

                if (isRoomAvailable(roomId, checkIn, checkOut)) {
                    ReservationDto dto = new ReservationDto();
                    dto.setGuestId(guestId);
                    dto.setRoomId(roomId);
                    dto.setCheckInDateStr(timeForm.getCheckInDate());     // ovo ostaje bez vremena, za prikaz
                    dto.setCheckOutDateStr(timeForm.getCheckOutDate());
                    dto.setNumberOfNights(numberOfNights);
                    dto.setPrice(pricePerNight * numberOfNights);

                    returnValue.add(dto);
                    break; // idemo na sledeću kategoriju
                }
            }
        });

        return returnValue;
    }


    public boolean isRoomAvailable(Integer roomId, Timestamp checkIn, Timestamp checkOut) {
        List<ReservationEntity> reservations = reservationRepository.findAllByRoomId(roomId);

        for (ReservationEntity r : reservations) {
            if (checkIn.before(r.getCheckOutDate()) && checkOut.after(r.getCheckInDate())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ReservationDto addReservation(ReservationDto reservation,Http.Request request) {
        Timestamp checkInDate = conversionUtils.stringToTimestamp(reservation.getCheckInDateStr() + " 14:00");
        Timestamp checkOutDate = conversionUtils.stringToTimestamp(reservation.getCheckOutDateStr() + " 12:00");
        JsonNode authUser = fetchCurrentUser(request);
        String fname = getSafeText(authUser, "firstName");
        String lname = getSafeText(authUser, "lastName");
        ReservationEntity reservationEntity = tempConverter.reservationDtoToEntity(reservation);
        reservationEntity.setCreateTime(conversionUtils.getCurrentUTCTimestamp());
        reservationEntity.setUpdateTime(conversionUtils.getCurrentUTCTimestamp());
        reservationEntity.setCheckInDate(checkInDate);
        reservationEntity.setCheckOutDate(checkOutDate);
        ReservationEntity storedReservation = reservationRepository.save(reservationEntity);
        NoteDto note = new NoteDto();
        note.setSubject("Reservation Created");
        String text = "User " + fname + " " + lname + " has reserved room "
                + storedReservation.getRoomId() + ". Check-in is scheduled for "
                + reservation.getCheckInDateStr() + ".";
        note.setText(text);
        NoteEntity noteEntity = tempConverter.noteDtoToEntity(note);
        noteEntity.setCreateTime(conversionUtils.getCurrentUTCTimestamp());
        noteRepository.save(noteEntity);
        return tempConverter.reservationEntityToDto(storedReservation);
    }

    @Override
    public List<ReservationDto> listAll() {
        List<ReservationEntity> allReservations = reservationRepository.findAll();
        return allReservations.stream().map(tempConverter::reservationEntityToDto).collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> listAllByGuestId(Integer guestId) {
        List<ReservationEntity> allReservations = reservationRepository.findAllByGuestId(guestId);
        return allReservations.stream().map(tempConverter::reservationEntityToDto).collect(Collectors.toList());
    }

    @Override
    public List<ReservationDto> getMyReservations(Http.Request request) {
        JsonNode currentGuest = fetchCurrentGuest(request);
        assert currentGuest != null;
        Integer guestId = currentGuest.get("guestId").asInt();

        return reservationRepository.findAll().stream()
                .filter(res -> res.getGuestId().equals(guestId))
                .sorted(Comparator.comparing(ReservationEntity::getCheckInDate))
                .map(tempConverter::reservationEntityToDto)
                .collect(Collectors.toList());
    }


    @Override
    public void cancelReservation(Integer reservationId,Http.Request request) {
        ReservationDto resevation = getReservationById(reservationId);
        JsonNode currentGuest = fetchCurrentGuest(request);
        assert currentGuest != null;
        Integer guestId = currentGuest.get("guestId").asInt();
        if(!guestId.equals(resevation.getGuestId()) || !resevation.possibleCancel()){
            throw new OperationNotAllowedException("Operation not allowed!");
        }

        JsonNode authUser = fetchCurrentUser(request);
        JsonNode room = natsSender.getRoomById(resevation.getRoomId());
        String firstName = getSafeText(authUser, "firstName");
        String lastName = getSafeText(authUser, "lastName");
        Integer roomNumber = room.get("roomNumber").asInt();
        NoteDto noteDto = new NoteDto();
        noteDto.setSubject("Reservation cancelled");
        String text = "User " + firstName + " " + lastName +
                " has cancelled their reservation for room No " + roomNumber +
                ", scheduled on " + resevation.getCheckInDateStr() + ".";
        noteDto.setText(text);
        NoteEntity noteEntity = tempConverter.noteDtoToEntity(noteDto);
        noteEntity.setCreateTime(conversionUtils.getCurrentUTCTimestamp());
        noteRepository.save(noteEntity);
        reservationRepository.deleteById(reservationId);
    }

    @Override
    public void deleteReservation(Integer reservationId) {
        getReservationById(reservationId);
        reservationRepository.deleteById(reservationId);
    }

    @Override
    public ReservationDto getReservationById(Integer reservationId) {
        ReservationEntity reservationEntity = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new InstanceUndefinedException("Reservation has not been found!"));
        return tempConverter.reservationEntityToDto(reservationEntity);
    }

    @Override
    public void deleteAllByGuestId(Integer guestId) {
        reservationRepository.deleteAllByGuestId(guestId);
    }

    @Override
    public void deleteAllByRoomId(Integer roomId) {
        reservationRepository.deleteAllByRoomId(roomId);
    }

    @Override
    public List<ReservationDto> listAllActive() {
        ZonedDateTime currentDate = conversionUtils.getCurrentUTCTimestamp().toLocalDateTime().atZone(zoneId);

        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getCheckOutDate()
                        .toLocalDateTime()
                        .atZone(zoneId)
                        .isAfter(currentDate))
                .sorted(Comparator.comparing(ReservationEntity::getCheckInDate))
                .map(tempConverter::reservationEntityToDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<ReservationDto> listAllExpired() {
        ZonedDateTime currentDate = conversionUtils.getCurrentUTCTimestamp().toLocalDateTime().atZone(zoneId);
        return reservationRepository.findAll().stream().filter(
                        reservation -> reservation.getCheckOutDate().toLocalDateTime().atZone(zoneId).isBefore(currentDate))
                .map(tempConverter::reservationEntityToDto).collect(Collectors.toList());
    }

    @Override
    public List<JsonNode> retrieveRoomAlternatives(Integer reservationId, Http.Request request) {
        List<JsonNode> returnValue = new ArrayList<>();

        ReservationDto reservation = getReservationById(reservationId);
        JsonNode currentRoom = natsSender.getRoomById(reservation.getRoomId());
        Integer categoryId = currentRoom.get("roomCategoryId").asInt();

        List<JsonNode> roomsList = natsSender.listAllRoomsByCategoryId(categoryId, request);

        for (JsonNode room : roomsList) {
            Integer roomId = room.get("roomId").asInt();

            // skipuj trenutnu sobu
            if (roomId.equals(reservation.getRoomId())) {
                continue;
            }

            // proveri da li je slobodna
            Timestamp checkIn = conversionUtils.stringToTimestamp(reservation.getCheckInDateStr());
            Timestamp checkOut = conversionUtils.stringToTimestamp(reservation.getCheckOutDateStr());
            boolean available = isRoomAvailable(roomId, checkIn, checkOut);

            if (available) {
                returnValue.add(room); // direktan JsonNode ubacujemo
            }
        }

        return returnValue;
    }

    @Override
    public ReservationDto updateReservation(ReservationDto reservation, Integer reservationId) {
        ReservationDto currentReservation = getReservationById(reservationId);
        JsonNode guest = natsSender.getGuestById(currentReservation.getGuestId());
        Integer userId = guest.get("userId").asInt();
        JsonNode user = natsSender.getUserById(userId);
        String firstName = getSafeText(user, "firstName");
        String lastName = getSafeText(user, "lastName");
        JsonNode room = natsSender.getRoomById(reservation.getRoomId());
        Integer roomNumber = room.get("roomNumber").asInt();
        reservation.setReservationId(currentReservation.getReservationId());
        ReservationEntity reservationEntity = tempConverter.reservationDtoToEntity(reservation);
        reservationEntity.setCheckInDate(conversionUtils.stringToTimestamp(currentReservation.getCheckInDateStr()));
        reservationEntity.setCheckOutDate(conversionUtils.stringToTimestamp(currentReservation.getCheckOutDateStr()));
        reservationEntity.setCreateTime(conversionUtils.stringToTimestamp(currentReservation.getCreateTimeStr()));
        reservationEntity.setUpdateTime(conversionUtils.getCurrentUTCTimestamp());
        ReservationEntity updatedReservation = reservationRepository.save(reservationEntity);
        NoteDto noteDto = new NoteDto();
        noteDto.setSubject("Reservation updated");
        String text = "Reservation #" + reservation.getReservationId()
                + " assigned to user " + firstName + " " + lastName
                + ", scheduled for " + currentReservation.getCheckInDateStr()
                + ", has been successfully updated with a new room assignment (Room " + roomNumber + ").";
        noteDto.setText(text);
        NoteEntity noteEntity = tempConverter.noteDtoToEntity(noteDto);
        noteEntity.setCreateTime(conversionUtils.getCurrentUTCTimestamp());
        noteRepository.save(noteEntity);
        return tempConverter.reservationEntityToDto(updatedReservation);
    }


    private JsonNode fetchCurrentGuest(Http.Request request) {
        try {
            String guestServiceUrl = urlProvider.getGuestServiceUrl() + "/api/guests/me"; // prilagodi port ako treba
            WSResponse response = wsClient.url(guestServiceUrl)
                    .addHeader("Authorization", request.header("Authorization").orElse(""))
                    .get()
                    .toCompletableFuture()
                    .get(); // blokira jer si u sync servisu


            if (response.getStatus() == 200) {
                return response.asJson();
            } else {
                System.out.println("❌ Guest service error: " + response.getStatus());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private JsonNode fetchCurrentUser(Http.Request request) {
        try {
            String authServiceUrl = urlProvider.getAuthServiceUrl() + "/api/auth/me";

            WSResponse response = wsClient.url(authServiceUrl)
                    .addHeader("Authorization", request.header("Authorization").orElse(""))
                    .get()
                    .toCompletableFuture()
                    .get();

            String contentType = response.getContentType();
            String rawBody = response.getBody();

            if (response.getStatus() == 200) {
                if (contentType.contains("application/json")) {
                    return response.asJson();
                } else if (contentType.contains("application/xml")) {
                    return xmlMapper.readTree(rawBody);
                } else {
                    System.out.println("⚠ Unknown content type: " + contentType);
                }
            } else {
                System.out.println("❌ Auth service responded with error status: " + response.getStatus());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String getSafeText(JsonNode node, String field) {
        if (node != null && node.has(field) && !node.get(field).isNull()) {
            return node.get(field).asText();
        }
        return "";
    }



}
