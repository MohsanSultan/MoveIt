
package com.moveitdriver.models.getAllVehicleResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("vehicleInsuranceEffectiveDate")
    @Expose
    private String vehicleInsuranceEffectiveDate;
    @SerializedName("vehicleInsuranceCertificateExpires")
    @Expose
    private String vehicleInsuranceCertificateExpires;
    @SerializedName("vehicleInspectionReport")
    @Expose
    private String vehicleInspectionReport;
    @SerializedName("vehicleInsuranceType")
    @Expose
    private String vehicleInsuranceType;
    @SerializedName("vehicleInsuranceCompanyName")
    @Expose
    private String vehicleInsuranceCompanyName;
    @SerializedName("vehicleInsuranceCertificatePic")
    @Expose
    private String vehicleInsuranceCertificatePic;
    @SerializedName("seats")
    @Expose
    private String seats;
    @SerializedName("registrationExpiry")
    @Expose
    private String registrationExpiry;
    @SerializedName("registrationDate")
    @Expose
    private String registrationDate;
    @SerializedName("carColor")
    @Expose
    private String carColor;
    @SerializedName("carYear")
    @Expose
    private String carYear;
    @SerializedName("registrationNumber")
    @Expose
    private String registrationNumber;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("carManufacturer")
    @Expose
    private CarManufacturer carManufacturer;
    @SerializedName("carModel")
    @Expose
    private CarModel carModel;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleInsuranceEffectiveDate() {
        return vehicleInsuranceEffectiveDate;
    }

    public void setVehicleInsuranceEffectiveDate(String vehicleInsuranceEffectiveDate) {
        this.vehicleInsuranceEffectiveDate = vehicleInsuranceEffectiveDate;
    }

    public String getVehicleInsuranceCertificateExpires() {
        return vehicleInsuranceCertificateExpires;
    }

    public void setVehicleInsuranceCertificateExpires(String vehicleInsuranceCertificateExpires) {
        this.vehicleInsuranceCertificateExpires = vehicleInsuranceCertificateExpires;
    }

    public String getVehicleInspectionReport() {
        return vehicleInspectionReport;
    }

    public void setVehicleInspectionReport(String vehicleInspectionReport) {
        this.vehicleInspectionReport = vehicleInspectionReport;
    }

    public String getVehicleInsuranceType() {
        return vehicleInsuranceType;
    }

    public void setVehicleInsuranceType(String vehicleInsuranceType) {
        this.vehicleInsuranceType = vehicleInsuranceType;
    }

    public String getVehicleInsuranceCompanyName() {
        return vehicleInsuranceCompanyName;
    }

    public void setVehicleInsuranceCompanyName(String vehicleInsuranceCompanyName) {
        this.vehicleInsuranceCompanyName = vehicleInsuranceCompanyName;
    }

    public String getVehicleInsuranceCertificatePic() {
        return vehicleInsuranceCertificatePic;
    }

    public void setVehicleInsuranceCertificatePic(String vehicleInsuranceCertificatePic) {
        this.vehicleInsuranceCertificatePic = vehicleInsuranceCertificatePic;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getRegistrationExpiry() {
        return registrationExpiry;
    }

    public void setRegistrationExpiry(String registrationExpiry) {
        this.registrationExpiry = registrationExpiry;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getCarYear() {
        return carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CarManufacturer getCarManufacturer() {
        return carManufacturer;
    }

    public void setCarManufacturer(CarManufacturer carManufacturer) {
        this.carManufacturer = carManufacturer;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

}
