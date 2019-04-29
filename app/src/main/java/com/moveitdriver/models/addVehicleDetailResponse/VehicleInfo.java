
package com.moveitdriver.models.addVehicleDetailResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehicleInfo {

    @SerializedName("vehicleTypeid")
    @Expose
    private String vehicleTypeid;

    public String getVehicleTypeid() {
        return vehicleTypeid;
    }

    public void setVehicleTypeid(String vehicleTypeid) {
        this.vehicleTypeid = vehicleTypeid;
    }

}
