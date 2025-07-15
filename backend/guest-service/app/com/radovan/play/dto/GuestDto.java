package com.radovan.play.dto;

import play.data.validation.Constraints;

import java.io.Serializable;

public class GuestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer guestId;

    @Constraints.Required
    @Constraints.MinLength(9)
    @Constraints.MaxLength(15)
    private String phoneNumber;

    @Constraints.Required
    @Constraints.MinLength(6)
    @Constraints.MaxLength(12)
    private Long idNumber;

    private Integer userId;

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

    public @Constraints.Required @Constraints.MinLength(9) @Constraints.MaxLength(15) String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Constraints.Required @Constraints.MinLength(9) @Constraints.MaxLength(15) String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public @Constraints.Required @Constraints.MinLength(6) @Constraints.MaxLength(12) Long getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(@Constraints.Required @Constraints.MinLength(6) @Constraints.MaxLength(12) Long idNumber) {
        this.idNumber = idNumber;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
