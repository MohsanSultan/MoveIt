package com.moveitdriver.models.updateUserDetailResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("next_step")
    @Expose
    private String nextStep;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("firstname")
    @Expose
    private String firstname;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("sms_otp")
    @Expose
    private String smsOtp;
    @SerializedName("device_token")
    @Expose
    private Object deviceToken;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("driverLicenceExpires")
    @Expose
    private String driverLicenceExpires;
    @SerializedName("driverLicenceNumber")
    @Expose
    private String driverLicenceNumber;
    @SerializedName("driverLicenceState")
    @Expose
    private String driverLicenceState;
    @SerializedName("validLiscenseExpires")
    @Expose
    private String validLiscenseExpires;
    @SerializedName("validVehicleTypeLiscence")
    @Expose
    private String validVehicleTypeLiscence;
    @SerializedName("driverLicenceFrom")
    @Expose
    private String driverLicenceFrom;
    @SerializedName("driverLicenceTill")
    @Expose
    private String driverLicenceTill;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("is_assigned")
    @Expose
    private Boolean isAssigned;
    @SerializedName("skip_sms_verify")
    @Expose
    private Boolean skipSmsVerify;
    @SerializedName("call_verify")
    @Expose
    private Boolean callVerify;
    @SerializedName("sms_verify")
    @Expose
    private Boolean smsVerify;
    @SerializedName("mail_verify")
    @Expose
    private Boolean mailVerify;
    @SerializedName("isSuspend")
    @Expose
    private Boolean isSuspend;
    @SerializedName("isDeleted")
    @Expose
    private Boolean isDeleted;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;
    @SerializedName("reg_type")
    @Expose
    private String regType;
    @SerializedName("social_media_id")
    @Expose
    private String socialMediaId;
    @SerializedName("profile_image")
    @Expose
    private String profileImage;
    @SerializedName("email")
    @Expose
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getSmsOtp() {
        return smsOtp;
    }

    public void setSmsOtp(String smsOtp) {
        this.smsOtp = smsOtp;
    }

    public Object getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(Object deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDriverLicenceExpires() {
        return driverLicenceExpires;
    }

    public void setDriverLicenceExpires(String driverLicenceExpires) {
        this.driverLicenceExpires = driverLicenceExpires;
    }

    public String getDriverLicenceNumber() {
        return driverLicenceNumber;
    }

    public void setDriverLicenceNumber(String driverLicenceNumber) {
        this.driverLicenceNumber = driverLicenceNumber;
    }

    public String getDriverLicenceState() {
        return driverLicenceState;
    }

    public void setDriverLicenceState(String driverLicenceState) {
        this.driverLicenceState = driverLicenceState;
    }

    public String getValidLiscenseExpires() {
        return validLiscenseExpires;
    }

    public void setValidLiscenseExpires(String validLiscenseExpires) {
        this.validLiscenseExpires = validLiscenseExpires;
    }

    public String getValidVehicleTypeLiscence() {
        return validVehicleTypeLiscence;
    }

    public void setValidVehicleTypeLiscence(String validVehicleTypeLiscence) {
        this.validVehicleTypeLiscence = validVehicleTypeLiscence;
    }

    public String getDriverLicenceFrom() {
        return driverLicenceFrom;
    }

    public void setDriverLicenceFrom(String driverLicenceFrom) {
        this.driverLicenceFrom = driverLicenceFrom;
    }

    public String getDriverLicenceTill() {
        return driverLicenceTill;
    }

    public void setDriverLicenceTill(String driverLicenceTill) {
        this.driverLicenceTill = driverLicenceTill;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getIsAssigned() {
        return isAssigned;
    }

    public void setIsAssigned(Boolean isAssigned) {
        this.isAssigned = isAssigned;
    }

    public Boolean getSkipSmsVerify() {
        return skipSmsVerify;
    }

    public void setSkipSmsVerify(Boolean skipSmsVerify) {
        this.skipSmsVerify = skipSmsVerify;
    }

    public Boolean getCallVerify() {
        return callVerify;
    }

    public void setCallVerify(Boolean callVerify) {
        this.callVerify = callVerify;
    }

    public Boolean getSmsVerify() {
        return smsVerify;
    }

    public void setSmsVerify(Boolean smsVerify) {
        this.smsVerify = smsVerify;
    }

    public Boolean getMailVerify() {
        return mailVerify;
    }

    public void setMailVerify(Boolean mailVerify) {
        this.mailVerify = mailVerify;
    }

    public Boolean getIsSuspend() {
        return isSuspend;
    }

    public void setIsSuspend(Boolean isSuspend) {
        this.isSuspend = isSuspend;
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

    public String getRegType() {
        return regType;
    }

    public void setRegType(String regType) {
        this.regType = regType;
    }

    public String getSocialMediaId() {
        return socialMediaId;
    }

    public void setSocialMediaId(String socialMediaId) {
        this.socialMediaId = socialMediaId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
