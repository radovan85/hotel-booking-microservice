package com.radovan.play.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "room_categories")
public class RoomCategoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer roomCategoryId;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false)
    private Float price;

    @Column(nullable = false, name = "wi-fi")
    private Byte wifi;

    @Column(nullable = false)
    private Byte wc;

    @Column(nullable = false)
    private Byte tv;

    @Column(nullable = false)
    private Byte bar;

    @OneToMany(mappedBy = "roomCategory", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<RoomEntity> rooms;

    public Integer getRoomCategoryId() {
        return roomCategoryId;
    }

    public void setRoomCategoryId(Integer roomCategoryId) {
        this.roomCategoryId = roomCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Byte getWifi() {
        return wifi;
    }

    public void setWifi(Byte wifi) {
        this.wifi = wifi;
    }

    public Byte getWc() {
        return wc;
    }

    public void setWc(Byte wc) {
        this.wc = wc;
    }

    public Byte getTv() {
        return tv;
    }

    public void setTv(Byte tv) {
        this.tv = tv;
    }

    public Byte getBar() {
        return bar;
    }

    public void setBar(Byte bar) {
        this.bar = bar;
    }

    public List<RoomEntity> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomEntity> rooms) {
        this.rooms = rooms;
    }

}