
package com.moveitdriver.models.getInvoiceResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Data implements Serializable {

    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("totalAmount")
    @Expose
    private String totalAmount;
    @SerializedName("perKmFareCAlculation")
    @Expose
    private String perKmFareCAlculation;
    @SerializedName("perMinFareCalculation")
    @Expose
    private String perMinFareCalculation;
    @SerializedName("vehicle_id")
    @Expose
    private String vehicleId;
    @SerializedName("totalTime")
    @Expose
    private String totalTime;
    @SerializedName("totalDistance")
    @Expose
    private String totalDistance;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("addedDate")
    @Expose
    private String addedDate;

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPerKmFareCAlculation() {
        return perKmFareCAlculation;
    }

    public void setPerKmFareCAlculation(String perKmFareCAlculation) {
        this.perKmFareCAlculation = perKmFareCAlculation;
    }

    public String getPerMinFareCalculation() {
        return perMinFareCalculation;
    }

    public void setPerMinFareCalculation(String perMinFareCalculation) {
        this.perMinFareCalculation = perMinFareCalculation;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

}
