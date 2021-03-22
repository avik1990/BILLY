package com.tpcodl.billingreading.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.reponseModel.ForgotPasswordResponseModel;
import com.tpcodl.billingreading.reponseModel.LoginResponseModel;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.Constant;
import com.tpcodl.billingreading.utils.DialogUtils;
import com.tpcodl.billingreading.webservice.ApiInterface;
import com.tpcodl.billingreading.webservice.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_title;
    private ImageView back;
    private Button button_submit;
    private EditText edit_email;
    private DialogUtils dUtils;
    private PreferenceHandler phandler;
    private ActivityUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.forgot_password_title));
        back = findViewById(R.id.iv_back);
        edit_email = findViewById(R.id.edit_email);
        back.setOnClickListener(this);
        button_submit = findViewById(R.id.button_submit);
        button_submit.setOnClickListener(this);
        dUtils = new DialogUtils(this);
        phandler = new PreferenceHandler(this);
        utils = ActivityUtils.getInstance();
        phandler.getLoginData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }
            case R.id.button_submit: {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(edit_email.getText()).matches()) {
                    edit_email.requestFocus();
                    edit_email.setError("Incorrect email ID");
                } else {
                    if(TextUtils.isEmpty(utils.getAuthToken())){
                        Toast.makeText(this, "Please Login with default login credentials first.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        callForgotapi();
                    }
                }
            }
        }
    }

    private void callForgotapi() {
        if (AppUtils.isInternetAvailable(this)) {
            dUtils.showDialog("Checking ", "Please wait..");
            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
            JsonObject object = new JsonObject();
            object.addProperty("email", edit_email.getText().toString());
            object.addProperty("type", Constant.appType);
            object.addProperty("user_id", utils.getUserID());
            Call<ForgotPasswordResponseModel> call = service.forgotPassword(utils.getAuthToken(), object);
            call.enqueue(new Callback<ForgotPasswordResponseModel>() {
                @Override
                public void onResponse(Call<ForgotPasswordResponseModel> call, Response<ForgotPasswordResponseModel> response) {
                    ForgotPasswordResponseModel object = response.body();
                    dUtils.dismissDialog();


                    if ((object!=null)&&((object.statusCode == 200))) {
                        Toast.makeText(ForgotPasswordActivity.this, object.message, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(i);
                    }

                    else {
                        Toast.makeText(ForgotPasswordActivity.this, object.message, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ForgotPasswordResponseModel> call, Throwable t) {
                    dUtils.dismissDialog();
                    Toast.makeText(ForgotPasswordActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            dUtils.dismissDialog();
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

}