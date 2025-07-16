package com.radovan.play.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import play.data.validation.Constraints;

import java.io.Serializable;

public class RoomDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer roomId;

    @Constraints.Required
    @Constraints.Min(1)
    private Integer roomNumber;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Float price;

    @Constraints.Required
    private Integer roomCategoryId;

    public @Constraints.Required @Constraints.Min(1) Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(@Constraints.Required @Constraints.Min(1) Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public @Constraints.Required Integer getRoomCategoryId() {
        return roomCategoryId;
    }

    public void setRoomCategoryId(@Constraints.Required Integer roomCategoryId) {
        this.roomCategoryId = roomCategoryId;
    }
}
