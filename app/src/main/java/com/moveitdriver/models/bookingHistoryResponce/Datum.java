
package com.moveitdriver.models.bookingHistoryResponce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Datum implements Serializable{

    @SerializedName("bookingId")
    @Expose
    private String bookingId;
    @SerializedName("totalAmount")
    @Expose
    private String totalAmount;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("carName")
    @Expose
    private String carName;
    @SerializedName("drop_address")
    @Expose
    private DropAddress dropAddress;
    @SerializedName("pickup_address")
    @Expose
    private PickupAddress pickupAddress;
    @SerializedName("bookingDate")
    @Expose
    private String bookingDate;
    @SerializedName("bookingStatus")
    @Expose
    private String bookingStatus;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public DropAddress getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(DropAddress dropAddress) {
        this.dropAddress = dropAddress;
    }

    public PickupAddress getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(PickupAddress pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

}
