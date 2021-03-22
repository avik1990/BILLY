package com.tpcodl.billingreading.reponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnablebillingResponse {
    @SerializedName("forceDownloadFlag")
    @Expose
    private String forceDownloadFlag;
    @SerializedName("enabledBillFlag")
    @Expose
    private String enabledBillFlag;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("status_code")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("server_date_time")
    @Expose
    private String serverDateTime;
    @SerializedName("software_version_no")
    @Expose
    private String softwareVersionNo;

    public String getForceDownloadFlag() {
        return forceDownloadFlag;
    }

    public void setForceDownloadFlag(String forceDownloadFlag) {
        this.forceDownloadFlag = forceDownloadFlag;
    }

    public String getEnabledBillFlag() {
        return enabledBillFlag;
    }

    public void setEnabledBillFlag(String enabledBillFlag) {
        this.enabledBillFlag = enabledBillFlag;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServerDateTime() {
        return serverDateTime;
    }

    public void setServerDateTime(String serverDateTime) {
        this.serverDateTime = serverDateTime;
    }

    public String getSoftwareVersionNo() {
        return softwareVersionNo;
    }

    public void setSoftwareVersionNo(String softwareVersionNo) {
        this.softwareVersionNo = softwareVersionNo;
    }


}
