package com.tpcodl.billingreading.reponseModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateContactResponseModel {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("status_code")
    @Expose
    public Integer statusCode;
    @SerializedName("message")
    @Expose
    public String message;

}
