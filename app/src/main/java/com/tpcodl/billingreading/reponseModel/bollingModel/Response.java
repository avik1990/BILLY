package com.tpcodl.billingreading.reponseModel.bollingModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response{

    @SerializedName("header_details")
    @Expose
    public List<HeaderDetails> headerDetails = null;

}