package com.tpcodl.billingreading.webservice;

import com.google.gson.JsonObject;
import com.tpcodl.billingreading.reponseModel.ChangePasswordResponseModel;
import com.tpcodl.billingreading.reponseModel.EnablebillingResponse;
import com.tpcodl.billingreading.reponseModel.ForgotPasswordResponseModel;
import com.tpcodl.billingreading.reponseModel.LoginResponseModel;
import com.tpcodl.billingreading.reponseModel.RetroPhoto;
import com.tpcodl.billingreading.reponseModel.UploadDataResponseModel;
import com.tpcodl.billingreading.reponseModel.UploadSbmCheck;
import com.tpcodl.billingreading.reponseModel.ValidationResponseModel;
import com.tpcodl.billingreading.reponseModel.bollingModel.BillingResponseModel;
import com.tpcodl.billingreading.reponseModel.bollingModel.Response;
import com.tpcodl.billingreading.requestModel.UserEnableModel;
import com.tpcodl.billingreading.uploadingrequestModel.RequestModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @GET("/photos")
    Call<List<RetroPhoto>> getAllPhotos();

    // Validate User
    @POST("users/validate-user-mobile")
    Call<ValidationResponseModel> validateUser(@Body JsonObject object);

    //Login User
    @POST("users/login")
    Call<LoginResponseModel> loginUser(@Body JsonObject object);

    //Change Password
    @POST("users/change-password")
    Call<ChangePasswordResponseModel> changePassword(@Header("auth_token") String token, @Body JsonObject object);

    //Forgot Password
    @POST("users/forgot-password")
    Call<ForgotPasswordResponseModel> forgotPassword(@Header("auth_token") String token, @Body JsonObject object);

    // Billing Data
    @POST("sbm/download-spot-billing-data")
    Call<BillingResponseModel> getBillingdata(@Header("auth_token") String token, @Body JsonObject object);

    //Upload Data
    @POST("sbm/upload-spot-billing-data")
    Call<UploadDataResponseModel> uploadData(@Header("auth_token") String token, @Body RequestModel model);

    //Upload Data
    @POST("users/get-enable-disable-billing-data")
    Call<EnablebillingResponse> enabledisableUser(@Header("auth_token") String token, @Body UserEnableModel model);


    //Upload Check Data
    @POST("sbm/upload-sbm-check")
    Call<UploadSbmCheck> uploadCheckData(@Header("auth_token") String token, @Body JsonObject object);
}
