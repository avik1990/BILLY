package com.tpcodl.billingreading.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.remark.NonSBMBillingDashboard;
import com.tpcodl.billingreading.activity.remark.SBMBillingDashboard;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.IOException;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_title, user_name;
    private ImageView iv_back;
    private Button btn_spot_billing, btn_non_sbm_reading, edit_profile, quality_check, theft, extra_connection, feedback, printer;
    private ActivityUtils utils;
    private PreferenceHandler pHandler;
    private ActivityUtils aUtils;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    Context context;
    boolean doubleBackToExitPressedOnce = false;

    TextView TOTAL_SBM_CONSUMER, TOTAL_SBM_BILL, TOTAL_SBM_BILL_TO_UPLOAD, TOTAL_SBM_BILL_UPLOAD, TOTAL_NSBM_CONSUMER, TOTAL_NSBM_READ, TOTAL_NSBM_READ_TO_UPLOAD, TOTAL_NSBM_READ_UPLOAD;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        context = this;
        db = new DatabaseHelper(context);

        initViews();
        user_name.setText("Welcome" + " " + aUtils.getUserName().toUpperCase());

    }

    private void initViews() {
        tv_title = findViewById(R.id.tv_title);
        iv_back = findViewById(R.id.iv_back);
        user_name = findViewById(R.id.user_name);
        edit_profile = findViewById(R.id.edit_profile);
        btn_spot_billing = findViewById(R.id.btn_spot_billing);
        btn_non_sbm_reading = findViewById(R.id.btn_non_sbm_reading);
        extra_connection = findViewById(R.id.extra_connection);
        theft = findViewById(R.id.theft);
        feedback = findViewById(R.id.feedback);
        quality_check = findViewById(R.id.quality_check);
        printer = findViewById(R.id.printer);
        quality_check.setOnClickListener(this);
        theft.setOnClickListener(this);
        feedback.setOnClickListener(this);
        extra_connection.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        edit_profile.setOnClickListener(this);
        btn_spot_billing.setOnClickListener(this);
        btn_non_sbm_reading.setOnClickListener(this);
        printer.setOnClickListener(this);
        tv_title.setText(getResources().getString(R.string.landing_title));
        utils = ActivityUtils.getInstance();
        pHandler = new PreferenceHandler(this);
        pHandler.getValidationdata();
        pHandler.getLoginData();
        aUtils = ActivityUtils.getInstance();
        mDBHelper = new DatabaseHelper(this);

        TOTAL_SBM_CONSUMER = findViewById(R.id.TOTAL_SBM_CONSUMER);
        TOTAL_SBM_BILL = findViewById(R.id.TOTAL_SBM_BILL);
        TOTAL_SBM_BILL_TO_UPLOAD = findViewById(R.id.TOTAL_SBM_BILL_TO_UPLOAD);
        TOTAL_SBM_BILL_UPLOAD = findViewById(R.id.TOTAL_SBM_BILL_UPLOAD);
        TOTAL_NSBM_CONSUMER = findViewById(R.id.TOTAL_NSBM_CONSUMER);
        TOTAL_NSBM_READ = findViewById(R.id.TOTAL_NSBM_READ);
        TOTAL_NSBM_READ_TO_UPLOAD = findViewById(R.id.TOTAL_NSBM_READ_TO_UPLOAD);
        TOTAL_NSBM_READ_UPLOAD = findViewById(R.id.TOTAL_NSBM_READ_UPLOAD);

        try {
            mDBHelper.createDataBase();
            Log.e("DatabaseHelper", "Created");
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
    }

    protected void onResume() {
        super.onResume();
        showReport();
    }

    private void showReport() {
        int count = 0;
        Cursor rs;
        String Query1 = "SELECT COUNT(1) TOTAL_SBM_CONSUMER FROM TBL_SPOTBILL_HEADER_DETAILS where USER_TYPE='S'";
        rs = db.getCalculateedData(Query1);
        count = 0;
        while (rs.moveToNext()) {
            count = rs.getInt(0);
        }
        rs.close();
        TOTAL_SBM_CONSUMER.setText("TOTAL SBM CONSUMER: " + String.valueOf(count));
        //////////
        String Query2 = "SELECT COUNT(1) TOTAL_SBM_BILL FROM TBL_SPOTBILL_HEADER_DETAILS where READ_FLAG='1' and USER_TYPE='S'";
        rs = db.getCalculateedData(Query2);
        count = 0;
        while (rs.moveToNext()) {
            count = rs.getInt(0);
        }
        rs.close();
        TOTAL_SBM_BILL.setText("TOTAL SBM BILL: " + String.valueOf(count));


        String Query3 = "SELECT COUNT(1) TOTAL_SBM_BILL_TO_UPLOAD FROM TBL_SPOTBILL_HEADER_DETAILS where READ_FLAG='1' and USER_TYPE='S' AND SENT_FLAG='0'";
        rs = db.getCalculateedData(Query3);
        count = 0;
        while (rs.moveToNext()) {
            count = rs.getInt(0);
        }
        rs.close();
        TOTAL_SBM_BILL_TO_UPLOAD.setText("TOTAL SBM BILL TO UPLOAD: " + String.valueOf(count));
        String Query4 = "SELECT COUNT(1) TOTAL_SBM_BILL_UPLOAD FROM TBL_SPOTBILL_HEADER_DETAILS where READ_FLAG='1' and USER_TYPE='S' AND SENT_FLAG='1'";
        rs = db.getCalculateedData(Query4);
        count = 0;
        while (rs.moveToNext()) {
            count = rs.getInt(0);
        }
        rs.close();
        TOTAL_SBM_BILL_UPLOAD.setText("TOTAL SBM BILL UPLOAD: " + String.valueOf(count));
        String Query5 = "SELECT COUNT(1) TOTAL_NSBM_CONSUMER FROM TBL_SPOTBILL_HEADER_DETAILS where USER_TYPE='X'";
        rs = db.getCalculateedData(Query5);
        count = 0;
        while (rs.moveToNext()) {
            count = rs.getInt(0);
        }
        rs.close();
        TOTAL_NSBM_CONSUMER.setText("TOTAL NSBM CONSUMER: " + String.valueOf(count));
        String Query6 = "SELECT COUNT(1) TOTAL_NSBM_READ FROM TBL_SPOTBILL_HEADER_DETAILS where READ_FLAG='1' and USER_TYPE='X'";
        rs = db.getCalculateedData(Query6);
        count = 0;
        while (rs.moveToNext()) {
            count = rs.getInt(0);
        }
        rs.close();
        TOTAL_NSBM_READ.setText("TOTAL NSBM READ: " + String.valueOf(count));
        String Query7 = "SELECT COUNT(1) TOTAL_NSBM_READ_TO_UPLOAD FROM TBL_SPOTBILL_HEADER_DETAILS where READ_FLAG='1' and USER_TYPE='X' AND SENT_FLAG='0'";
        rs = db.getCalculateedData(Query7);
        count = 0;
        while (rs.moveToNext()) {
            count = rs.getInt(0);
        }
        rs.close();
        TOTAL_NSBM_READ_TO_UPLOAD.setText("TOTAL NSBM READ TO UPLOAD:  " + String.valueOf(count));
        String Query8 = "SELECT COUNT(1) TOTAL_NSBM_READ_UPLOAD FROM TBL_SPOTBILL_HEADER_DETAILS where READ_FLAG='1' and USER_TYPE='X' AND SENT_FLAG='1'";
        rs = db.getCalculateedData(Query8);
        count = 0;
        while (rs.moveToNext()) {
            count = rs.getInt(0);
        }
        rs.close();
        TOTAL_NSBM_READ_UPLOAD.setText("TOTAL NSBM READ UPLOAD: " + String.valueOf(count));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile: {
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            }
            case R.id.iv_back: {
                onBackPressed();
                break;
            }
            case R.id.btn_spot_billing: {
                if (aUtils.getFlag_sbmBilling().equals("1")) {
                    PreferenceHandler.setisSBNONSBFLAG(context, "SBM");
                    startActivity(new Intent(this, SBMBillingDashboard.class));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.access_issue), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.btn_non_sbm_reading: {
                if (aUtils.getFlag_nonsbmBilling().equals("1")) {
                    PreferenceHandler.setisSBNONSBFLAG(context, "NONSBM");
                    startActivity(new Intent(this, NonSBMBillingDashboard.class));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.access_issue), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.quality_check: {
                if (aUtils.getFlag_qualityCheck().equals("1")) {
                    PreferenceHandler.setisSBNONSBFLAG(context, "");
                    Toast.makeText(this, "Access Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.access_issue), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.theft: {
                if (aUtils.getFlag_theft().equals("1")) {
                    PreferenceHandler.setisSBNONSBFLAG(context, "");
                    Toast.makeText(this, "Access Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.access_issue), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.feedback: {
                if (aUtils.getFlag_consumerFB().equals("1")) {
                    PreferenceHandler.setisSBNONSBFLAG(context, "");
                    Toast.makeText(this, "Access Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.access_issue), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.extra_connection: {
                if (aUtils.getFlag_extraconnection().equals("1")) {
                    PreferenceHandler.setisSBNONSBFLAG(context, "");
                    Toast.makeText(this, "Access Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.access_issue), Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.printer: {
                PreferenceHandler.setisSBNONSBFLAG(context, "");
                startActivity(new Intent(this, SetUpDashboardActivity.class));
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}


