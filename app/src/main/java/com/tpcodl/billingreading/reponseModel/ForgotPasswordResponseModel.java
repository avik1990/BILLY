package com.tpcodl.billingreading.reponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponseModel {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("status_code")
    @Expose
    public Integer statusCode;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("server_date_time")
    @Expose
    public String serverDateTime;
    @SerializedName("software_version_no")
    @Expose
    public String softwareVersionNo;

}