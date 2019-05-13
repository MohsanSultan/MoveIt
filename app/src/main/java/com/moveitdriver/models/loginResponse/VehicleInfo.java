
package com.moveitdriver.models.loginResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehicleInfo {

    @SerializedName("vehicleId")
    @Expose
    private String vehicleId;
    @SerializedName("vehicleTypeid")
    @Expose
    private String vehicleTypeid;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleTypeid() {
        return vehicleTypeid;
    }

    public void setVehicleTypeid(String vehicleTypeid) {
        this.vehicleTypeid = vehicleTypeid;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
