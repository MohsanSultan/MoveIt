
package com.moveitdriver.models.getAllVehicleResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CarManufacturer {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("vehicle_country")
    @Expose
    private String vehicleCountry;
    @SerializedName("brandlogo")
    @Expose
    private String brandlogo;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("make")
    @Expose
    private String make;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleCountry() {
        return vehicleCountry;
    }

    public void setVehicleCountry(String vehicleCountry) {
        this.vehicleCountry = vehicleCountry;
    }

    public String getBrandlogo() {
        return brandlogo;
    }

    public void setBrandlogo(String brandlogo) {
        this.brandlogo = brandlogo;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

}
