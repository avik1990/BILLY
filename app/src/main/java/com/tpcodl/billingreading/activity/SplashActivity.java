package com.tpcodl.billingreading.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.reponseModel.RetroPhoto;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.UtilsClass;
import com.tpcodl.billingreading.webservice.ApiInterface;
import com.tpcodl.billingreading.webservice.RetrofitClientInstance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private Context mContext;
    private PreferenceHandler phalder;
    private ActivityUtils util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        phalder = new PreferenceHandler(this);
        util = ActivityUtils.getInstance();
        mContext = this;
        intView();

        /*String date="2021-01-01";
        Log.e("CHangeFormat",UtilsClass.getFormateDate(date));*/
    }

    private void intView() {
        redirectToNextPage();
    }

    private void redirectToNextPage() {
        new Handler().postDelayed(() -> {
            checkLoginstatus();
        }, 2000);
    }

    private void checkLoginstatus() {
        phalder.getLoginData();
        if (!TextUtils.isEmpty(util.getServreDate())) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, UserValidationActivity.class));
            finish();
        }
    }


}