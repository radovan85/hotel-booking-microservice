package com.radovan.play.utils;

import jakarta.inject.Singleton;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@Singleton
public class TimeConversionUtils {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final ZoneId zoneId = ZoneId.of("UTC");

    public  int calculateNumberOfNights(String checkInStr, String checkOutStr) {
        try {
            LocalDateTime checkInLocal = LocalDateTime.parse(checkInStr, formatter);
            LocalDateTime checkOutLocal = LocalDateTime.parse(checkOutStr, formatter);

            ZonedDateTime checkIn = checkInLocal.atZone(zoneId);
            ZonedDateTime checkOut = checkOutLocal.atZone(zoneId);

            long days = ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
            return (int) days;
        } catch (DateTimeParseException ex) {
            System.out.println("❌ ERROR calculating nights: " + ex.getMessage());
            return 0;
        }
    }

    public Timestamp getCurrentUTCTimestamp() {
        ZonedDateTime currentTime = Instant.now().atZone(zoneId);
        return Timestamp.valueOf(currentTime.toLocalDateTime());
    }

    public boolean isValidPeriod(String checkInStr, String checkOutStr) {
        try {
            LocalDateTime checkIn = LocalDateTime.parse(checkInStr, formatter);
            LocalDateTime checkOut = LocalDateTime.parse(checkOutStr, formatter);
            return checkOut.isAfter(checkIn);
        } catch (DateTimeParseException ex) {
            System.out.println("❌ ERROR validating period: " + ex.getMessage());
            return false;
        }
    }

    public Timestamp stringToTimestamp(String str) {
        try {
            ZonedDateTime zoned = LocalDateTime.parse(str, formatter).atZone(zoneId);
            return Timestamp.valueOf(zoned.toLocalDateTime());
        } catch (DateTimeParseException ex) {
            System.out.println("❌ ERROR parsing to timestamp: " + ex.getMessage());
            return null;
        }
    }


    public String timestampToString(Timestamp timestamp) {
        ZonedDateTime utcTime = timestamp.toLocalDateTime().atZone(zoneId);
        return utcTime.format(formatter);
    }




}
