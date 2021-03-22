package com.tpcodl.billingreading.activity.remark;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.DownloadActivity;
import com.tpcodl.billingreading.activity.ReportNSBMActivity;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.activity.UploadActivity;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.DialogUtils;

public class NonSBMBillingDashboard extends AppCompatActivity implements View.OnClickListener {

    private Button btn_billing, btn_download_data, btn_report, btn_upload_data;
    private TextView tv_title;
    private ImageView iv_image;
    private DialogUtils dUtils;
    private ActivityUtils utils;
    private DialogUtils dutils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_sbm_billing_dashboard);
        btn_download_data = findViewById(R.id.btn_download_data);
        btn_upload_data = findViewById(R.id.btn_upload_data);
        btn_download_data.setOnClickListener(this);
        btn_upload_data.setOnClickListener(this);
        dUtils = new DialogUtils(this);
        utils = ActivityUtils.getInstance();
        initView();

    }

    private void initView() {
        btn_billing = findViewById(R.id.btn_billing);
        iv_image = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        btn_report = findViewById(R.id.btn_report);

        dutils = new DialogUtils(this);
        tv_title.setText(getResources().getString(R.string.non_sbm_billing_dashbpard));
        btn_billing.setOnClickListener(this);
        iv_image.setOnClickListener(this);
        btn_report.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_billing: {
                Intent intent = new Intent(NonSBMBillingDashboard.this, SearchDataActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.iv_back: {
                finish();
                break;
            }
            case R.id.btn_download_data: {

                Intent intent = new Intent(this, DownloadActivity.class);
                intent.putExtra("FROM", "NONSBM");
                startActivity(intent);
                //downloadBillingdata();
                break;
            }

            case R.id.btn_report: {
                Intent intent = new Intent(NonSBMBillingDashboard.this, ReportNSBMActivity.class);
                intent.putExtra("FROM", "NONSBM");
                startActivity(intent);
                break;
            }
            case R.id.btn_upload_data: {
                Intent intent = new Intent(NonSBMBillingDashboard.this, UploadActivity.class);
                intent.putExtra("FROM", "NONSBM");
                startActivity(intent);
                break;

            }
        }

    }

}