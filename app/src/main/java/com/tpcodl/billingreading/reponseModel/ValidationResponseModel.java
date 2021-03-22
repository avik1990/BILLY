package com.tpcodl.billingreading.reponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ValidationResponseModel{

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
    public Object userId;
    @SerializedName("server_date_time")
    @Expose
    public String serverDateTime;
    @SerializedName("software_version_no")
    @Expose
    public Object softwareVersionNo;
    @SerializedName("flag")
    @Expose
    public Integer flag;
    @SerializedName("password")
    @Expose
    public Object password;
    @SerializedName("name")
    @Expose
    public Object name;
    @SerializedName("email")
    @Expose
    public Object email;
    @SerializedName("address")
    @Expose
    public Object address;
    @SerializedName("db_server_user_name")
    @Expose
    public Object dbServerUserName;
    @SerializedName("db_server_password")
    @Expose
    public Object dbServerPassword;
    @SerializedName("div_code")
    @Expose
    public Object divCode;

}