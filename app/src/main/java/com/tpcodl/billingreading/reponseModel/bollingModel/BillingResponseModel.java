package com.tpcodl.billingreading.reponseModel.bollingModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BillingResponseModel{

    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("response")
    @Expose
    public List<Response> response = null;
    @SerializedName("server_date_time")
    @Expose
    public String serverDateTime;
    @SerializedName("software_version_no")
    @Expose
    public String softwareVersionNo;
    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("status_code")
    @Expose
    public Integer statusCode;
    @SerializedName("user_id")
    @Expose
    public String userId;

}