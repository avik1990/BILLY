package com.tpcodl.billingreading.activity.remark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.DownloadActivity;
import com.tpcodl.billingreading.activity.ReportDashBoardActivity;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.activity.UploadActivity;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.uploadingrequestModel.RequestModel;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.DialogUtils;

import java.io.IOException;

public class SBMBillingDashboard extends AppCompatActivity implements View.OnClickListener {

    private Button btn_billing, btn_download_data, btn_upload_date;
    private TextView tv_title;
    private ImageView iv_image;
    private DialogUtils dUtils;
    private ActivityUtils utils;
    private DialogUtils dutils;
    private RequestModel model;
    private DatabaseHelper mDBHelper;
    private Button btn_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sbm_billing_dashboard);
        dUtils = new DialogUtils(this);
        utils = ActivityUtils.getInstance();
        dutils = new DialogUtils(this);
        model = new RequestModel();
        mDBHelper = new DatabaseHelper(this);

        try {
            mDBHelper.createDataBase();
            Log.e("DatabaseHelper", "Created");
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        initView();
    }

    private void initView() {
        btn_billing = findViewById(R.id.btn_billing);
        iv_image = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.billing_dashbpard));
        btn_download_data = findViewById(R.id.btn_download_data);
        btn_report = findViewById(R.id.btn_report);

        btn_download_data.setOnClickListener(this);
        btn_report.setOnClickListener(this);

        btn_billing.setOnClickListener(this);
        iv_image.setOnClickListener(this);
        btn_upload_date = findViewById(R.id.btn_upload_date);
        btn_upload_date.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_billing: {
                Intent intent = new Intent(SBMBillingDashboard.this, SearchDataActivity.class);
                intent.putExtra("FROM", "SBM");
                startActivity(intent);
                break;
            }
            case R.id.iv_back: {
                finish();
                break;
            }
            case R.id.btn_report: {
                Intent intent = new Intent(SBMBillingDashboard.this, ReportDashBoardActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_download_data: {
                Intent intent = new Intent(this, DownloadActivity.class);
                intent.putExtra("FROM", "SBM");
                startActivity(intent);
                break;
            }
            case R.id.btn_upload_date: {
                Intent intent = new Intent(SBMBillingDashboard.this, UploadActivity.class);
                intent.putExtra("FROM", "SBM");
                startActivity(intent);
                break;
            }
        }

    }


}

