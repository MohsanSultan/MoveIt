package com.moveitdriver.models.getAllVehicleResponse;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CarModel implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("vehicleType")
    @Expose
    private String vehicleType;
    @SerializedName("make")
    @Expose
    private String make;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("year")
    @Expose
    private List<String> year = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public List<String> getYear() {
        return year;
    }

    public void setYear(List<String> year) {
        this.year = year;
    }

}
