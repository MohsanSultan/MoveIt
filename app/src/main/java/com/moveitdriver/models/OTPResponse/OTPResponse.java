package com.moveitdriver.models.OTPResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OTPResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("sms_verify")
    @Expose
    private Boolean smsVerify;
    @SerializedName("data")
    @Expose
    private List<Object> data = null;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getSmsVerify() {
        return smsVerify;
    }

    public void setSmsVerify(Boolean smsVerify) {
        this.smsVerify = smsVerify;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}