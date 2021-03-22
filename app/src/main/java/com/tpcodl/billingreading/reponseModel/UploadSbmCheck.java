package com.tpcodl.billingreading.reponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadSbmCheck {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
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
    @SerializedName("headerData")
    @Expose
    private List<HeaderDatum> headerData = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
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

    public List<HeaderDatum> getHeaderData() {
        return headerData;
    }

    public void setHeaderData(List<HeaderDatum> headerData) {
        this.headerData = headerData;
    }

    public class HeaderDatum {

        @SerializedName("installation")
        @Expose
        private String installation;
        @SerializedName("scheduleMeterReadDate")
        @Expose
        private String scheduleMeterReadDate;
        @SerializedName("mru")
        @Expose
        private String mru;
        @SerializedName("reverseFlag")
        @Expose
        private String reverseFlag;
        @SerializedName("transType")
        @Expose
        private String transType;

        public String getInstallation() {
            return installation;
        }

        public void setInstallation(String installation) {
            this.installation = installation;
        }

        public String getScheduleMeterReadDate() {
            return scheduleMeterReadDate;
        }

        public void setScheduleMeterReadDate(String scheduleMeterReadDate) {
            this.scheduleMeterReadDate = scheduleMeterReadDate;
        }

        public String getMru() {
            return mru;
        }

        public void setMru(String mru) {
            this.mru = mru;
        }

        public String getReverseFlag() {
            return reverseFlag;
        }

        public void setReverseFlag(String reverseFlag) {
            this.reverseFlag = reverseFlag;
        }

        public String getTransType() {
            return transType;
        }

        public void setTransType(String transType) {
            this.transType = transType;
        }

    }
}
