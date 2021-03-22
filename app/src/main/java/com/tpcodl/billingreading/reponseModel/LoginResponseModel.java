package com.tpcodl.billingreading.reponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponseModel {

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
    @SerializedName("address")
    @Expose
    public Object address;
    @SerializedName("flag")
    @Expose
    public Integer flag;
    @SerializedName("sbm_billing")
    @Expose
    public Integer sbmBilling;
    @SerializedName("non_sbm_billing")
    @Expose
    public Integer nonSbmBilling;
    @SerializedName("bill_distribution_flag")
    @Expose
    public Integer billDistributionFlag;
    @SerializedName("quality_check_flag")
    @Expose
    public Integer qualityCheckFlag;
    @SerializedName("theft_flag")
    @Expose
    public Integer theftFlag;
    @SerializedName("consumer_fb_flag")
    @Expose
    public Integer consumerFbFlag;
    @SerializedName("extra_conn_flag")
    @Expose
    public Integer extraConnFlag;
    @SerializedName("bill_flag")
    @Expose
    public Integer billFlag;
    @SerializedName("account_coll_flag")
    @Expose
    public Integer accountCollFlag;
    @SerializedName("db_server_user_name")
    @Expose
    public String dbServerUserName;
    @SerializedName("db_server_password")
    @Expose
    public String dbServerPassword;
    @SerializedName("div_code")
    @Expose
    public String divCode;
    @SerializedName("token")
    @Expose
    public String token;

}