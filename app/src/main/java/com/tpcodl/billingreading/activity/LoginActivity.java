package com.tpcodl.billingreading.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;
import com.tpcodl.billingreading.BuildConfig;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.reponseModel.LoginResponseModel;
import com.tpcodl.billingreading.reponseModel.RetroPhoto;
import com.tpcodl.billingreading.reponseModel.ValidationResponseModel;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.Constant;
import com.tpcodl.billingreading.utils.DialogUtils;
import com.tpcodl.billingreading.utils.UtilsClass;
import com.tpcodl.billingreading.webservice.ApiInterface;
import com.tpcodl.billingreading.webservice.RetrofitClientInstance;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText userID;
    ShowHidePasswordEditText userPSW;
    private Button login;
    private TextView tv_version, forgotPSW;
    private PreferenceHandler phandler;
    private ActivityUtils utils;
    private DialogUtils dUtils;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userID = findViewById(R.id.edit_user_id);
        userPSW = findViewById(R.id.edit_user_password);
        login = findViewById(R.id.button_login);
        forgotPSW = findViewById(R.id.text_forgot_psw);
        tv_version = findViewById(R.id.tv_version);
        context = this;
        // tv_title = findViewById(R.id.tv_title);
        // tv_title.setText(getResources().getString(R.string.login_title));
        tv_version.setText("App Version: " + getResources().getString(R.string.version));
        login.setOnClickListener(this);
        forgotPSW.setOnClickListener(this);
        utils = ActivityUtils.getInstance();
        phandler = new PreferenceHandler(this);
        dUtils = new DialogUtils(this);
        phandler.getValidationdata();
        userID.setText(utils.getUserID());

        // Log.e("Enable", PreferenceHandler.getEnableBilling(context));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login: {
                validateData(userID.getText().toString(), userPSW.getText().toString());
                break;
            }

            case R.id.text_forgot_psw: {
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            }
            case R.id.iv_back: {
                finish();
                break;
            }
        }
    }

    private void validateData(String id, String psw) {
        if (id.equalsIgnoreCase("")) {
            userID.requestFocus();
            userID.setError("Please enter mobile no");
        } else if (psw.equalsIgnoreCase("")) {
            userPSW.requestFocus();
            userPSW.setError("Please enter password");
        } else if (id.equalsIgnoreCase("") && psw.equalsIgnoreCase("")) {
            userID.requestFocus();
            userPSW.requestFocus();
            userID.setError("Please enter mobile no");
            userPSW.setError("please enter password");
        } else if (id.length() < 10) {
            userID.requestFocus();
            userID.setError("Mobile no is not valid");
        } else {


            if (TextUtils.isEmpty(utils.getServreDate())) {
                if (UtilsClass.isvalidVersion(utils.getAppVersion(), context)) {
                    callLoginApi();
                } else {
                    Toast.makeText(this, "App Version is not updated.Please use updated app version", Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                    Date currentDate = sdf.parse(DateFormat.format("dd-MM-yyyy", date.getTime()).toString());
                    Date serverDate = sdf.parse(utils.getServreDate());

                    if (currentDate.compareTo(serverDate) > 0) {
                        if (UtilsClass.isvalidVersion(utils.getAppVersion(), context)) {
                            callLoginApi();
                        } else {
                            Toast.makeText(this, "App Version is not updated.Please use updated app version", Toast.LENGTH_SHORT).show();
                        }
                    } else if (currentDate.compareTo(serverDate) < 0) {
                        Toast.makeText(this, "Device Date is Incorrect. Please Check...", Toast.LENGTH_SHORT).show();
                    } else if (currentDate.compareTo(serverDate) == 0) {
                        if (isDetailsvalid()) {
                            if (AppUtils.isInternetAvailable(this)) {
                                callLoginApi();
                            } else {
                                if (UtilsClass.isvalidVersion(utils.getAppVersion(), context)) {
                                    startActivity(new Intent(this, SafetyInformationActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "App Version is not updated.Please use updated app version", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void callLoginApi() {
        if (isDetailsvalid()) {
            if (AppUtils.isInternetAvailable(this)) {
                dUtils.showDialog("Login", "Please wait..");
                ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
                JsonObject object = new JsonObject();
                object.addProperty("password", userPSW.getText().toString());
                object.addProperty("type", Constant.appType);
                object.addProperty("user_id", userID.getText().toString());
                Call<LoginResponseModel> call = service.loginUser(object);
                call.enqueue(new Callback<LoginResponseModel>() {

                    @Override
                    public void onResponse(Call<LoginResponseModel> call, Response<LoginResponseModel> response) {
                        LoginResponseModel object = response.body();
                        dUtils.dismissDialog();
                        try {
                            if (object.flag == 1) {
                                try {
                                    utils.setAppVersion(object.softwareVersionNo.toString().trim());
                                } catch (Exception e) {

                                }
                                phandler.saveLogindata(object.token,
                                        object.serverDateTime,
                                        String.valueOf(object.sbmBilling),
                                        String.valueOf(object.nonSbmBilling),
                                        String.valueOf(object.billDistributionFlag),
                                        String.valueOf(object.qualityCheckFlag),
                                        String.valueOf(object.theftFlag),
                                        String.valueOf(object.consumerFbFlag),
                                        String.valueOf(object.extraConnFlag),
                                        String.valueOf(object.billFlag),
                                        String.valueOf(object.accountCollFlag),
                                        String.valueOf(object.softwareVersionNo));

                                utils.setServreDate(object.serverDateTime);
                                utils.setAppVersion(object.softwareVersionNo);
                                // utils.setAppVersion("1.1.0");
                                Date date = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                                Date currentDate = sdf.parse(DateFormat.format("dd-MM-yyyy", date.getTime()).toString());
                                Date serverDate = sdf.parse(utils.getServreDate());

                                if (UtilsClass.isvalidVersion(utils.getAppVersion(), context)) {
                                    if (currentDate.compareTo(serverDate) == 0) {
                                        startActivity(new Intent(LoginActivity.this, SafetyInformationActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(context, "Device Date is Incorrect. Please Check...", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "App Version is not updated.Please use updated app version", Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                Toast.makeText(context, object.message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "Something went wrong.Please try again!!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<LoginResponseModel> call, Throwable t) {
                        dUtils.dismissDialog();
                        Toast.makeText(LoginActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                dUtils.dismissDialog();
                Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        }

    }

    public boolean isDetailsvalid() {
        if (!userID.getText().toString().equals(utils.getUserID())) {
            userID.requestFocus();
            userID.setError(" incorrect mobile no");
            return false;
        } else if (!userPSW.getText().toString().equals(utils.getUserPassword())) {
            userPSW.requestFocus();
            userPSW.setError("incorrect password");
            return false;
        } else {
            return true;
        }
    }


}
