package com.radovan.play.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservations")
public class ReservationEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;


    @Column(name = "room_id",nullable = false)
    private Integer roomId;

    @Column(name = "guest_id",nullable = false)
    private Integer guestId;

    @Column(name = "check_in", nullable = false)
    private Timestamp checkInDate;

    @Column(name = "check_out", nullable = false)
    private Timestamp checkOutDate;

    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;

    @Column(nullable = false)
    private Float price;

    @Column(name = "num_of_nights", nullable = false)
    private Integer numberOfNights;

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

    public Timestamp getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Timestamp checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Timestamp getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Timestamp checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(Integer numberOfNights) {
        this.numberOfNights = numberOfNights;
    }
}
