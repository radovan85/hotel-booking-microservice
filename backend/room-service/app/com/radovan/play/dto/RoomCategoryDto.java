package com.radovan.play.dto;

import play.data.validation.Constraints;

import java.io.Serializable;
import java.util.List;

public class RoomCategoryDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer roomCategoryId;

    @Constraints.Required
    @Constraints.MinLength(2)
    @Constraints.MaxLength(30)
    private String name;

    @Constraints.Required
    @Constraints.Min(5)
    private Float price;

    @Constraints.Required
    @Constraints.Min(0)
    @Constraints.Max(1)
    private Short wifi;

    @Constraints.Required
    @Constraints.Min(0)
    @Constraints.Max(1)
    private Short wc;

    @Constraints.Required
    @Constraints.Min(0)
    @Constraints.Max(1)
    private Short tv;

    @Constraints.Required
    @Constraints.Min(0)
    @Constraints.Max(1)
    private Short bar;

    private List<Integer> roomsIds;

    public Integer getRoomCategoryId() {
        return roomCategoryId;
    }

    public void setRoomCategoryId(Integer roomCategoryId) {
        this.roomCategoryId = roomCategoryId;
    }

    public @Constraints.Required @Constraints.MinLength(2) @Constraints.MaxLength(30) String getName() {
        return name;
    }

    public void setName(@Constraints.Required @Constraints.MinLength(2) @Constraints.MaxLength(30) String name) {
        this.name = name;
    }

    public @Constraints.Required @Constraints.Min(5) Float getPrice() {
        return price;
    }

    public void setPrice(@Constraints.Required @Constraints.Min(5) Float price) {
        this.price = price;
    }

    public @Constraints.Required @Constraints.Min(0) @Constraints.Max(1) Short getWifi() {
        return wifi;
    }

    public void setWifi(@Constraints.Required @Constraints.Min(0) @Constraints.Max(1) Short wifi) {
        this.wifi = wifi;
    }

    public @Constraints.Required @Constraints.Min(0) @Constraints.Max(1) Short getWc() {
        return wc;
    }

    public void setWc(@Constraints.Required @Constraints.Min(0) @Constraints.Max(1) Short wc) {
        this.wc = wc;
    }

    public @Constraints.Required @Constraints.Min(0) @Constraints.Max(1) Short getTv() {
        return tv;
    }

    public void setTv(@Constraints.Required @Constraints.Min(0) @Constraints.Max(1) Short tv) {
        this.tv = tv;
    }

    public @Constraints.Required @Constraints.Min(0) @Constraints.Max(1) Short getBar() {
        return bar;
    }

    public void setBar(@Constraints.Required @Constraints.Min(0) @Constraints.Max(1) Short bar) {
        this.bar = bar;
    }

    public List<Integer> getRoomsIds() {
        return roomsIds;
    }

    public void setRoomsIds(List<Integer> roomsIds) {
        this.roomsIds = roomsIds;
    }
}
