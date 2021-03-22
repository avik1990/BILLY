package com.tpcodl.billingreading.uploadingrequestModel;

import com.tpcodl.billingreading.reponseModel.bollingModel.ChildDetail;

import java.util.List;

public class RequestModel {
    private String userId;
    private String type;
    private String upload_time;
    private String consumerType;
    private List<ChildReqDetails> childDataList;
    private List<HeaderresDetails> headerDataList;
    public String appVersion;

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }


    public String getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<HeaderresDetails> getHeader() {
        return headerDataList;
    }

    public void setHeader(List<HeaderresDetails> header) {
        this.headerDataList = header;
    }

    public List<ChildReqDetails> getChilld() {
        return childDataList;
    }

    public void setChilld(List<ChildReqDetails> chilld) {
        this.childDataList = chilld;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
