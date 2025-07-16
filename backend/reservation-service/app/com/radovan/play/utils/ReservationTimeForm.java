package com.radovan.play.utils;

import play.data.validation.Constraints;

public class ReservationTimeForm {

    @Constraints.Required
    private String checkInDate;

    @Constraints.Required
    private String checkOutDate;

    public @Constraints.Required String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(@Constraints.Required String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public @Constraints.Required String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(@Constraints.Required String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
}
