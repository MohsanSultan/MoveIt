
package com.moveitdriver.models.loginResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActiveBooking {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("booking")
    @Expose
    private Booking booking;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

}
