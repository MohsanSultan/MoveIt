
package com.moveitdriver.models.UserDetailResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Booking {

    @SerializedName("fareEstimate")
    @Expose
    private FareEstimate fareEstimate;
    @SerializedName("drop_address")
    @Expose
    private DropAddress dropAddress;
    @SerializedName("pickup_address")
    @Expose
    private PickupAddress pickupAddress;
    @SerializedName("vehicleId")
    @Expose
    private String vehicleId;
    @SerializedName("bookedBy")
    @Expose
    private String bookedBy;
    @SerializedName("bookingType")
    @Expose
    private String bookingType;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("bookingDate")
    @Expose
    private String bookingDate;
    @SerializedName("booking_driver")
    @Expose
    private Object bookingDriver;
    @SerializedName("invoice")
    @Expose
    private Object invoice;
    @SerializedName("id")
    @Expose
    private String id;

    public FareEstimate getFareEstimate() {
        return fareEstimate;
    }

    public void setFareEstimate(FareEstimate fareEstimate) {
        this.fareEstimate = fareEstimate;
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

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Object getBookingDriver() {
        return bookingDriver;
    }

    public void setBookingDriver(Object bookingDriver) {
        this.bookingDriver = bookingDriver;
    }

    public Object getInvoice() {
        return invoice;
    }

    public void setInvoice(Object invoice) {
        this.invoice = invoice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
