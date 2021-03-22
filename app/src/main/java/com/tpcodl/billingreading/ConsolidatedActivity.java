package com.tpcodl.billingreading;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.activity.printReceipt.BillPrintActivity;
import com.tpcodl.billingreading.activity.printReceipt.BillPrintAmigoImpact;
import com.tpcodl.billingreading.activity.printReceipt.BillPrintAmigoThermal;
import com.tpcodl.billingreading.activity.printReceipt.BillPrintAnalogicImpact;
import com.tpcodl.billingreading.activity.printReceipt.BillPrintAnalogicNewThermal;
import com.tpcodl.billingreading.activity.printReceipt.BillPrintAnalogicThermal;
import com.tpcodl.billingreading.activity.printReceipt.BillPrintEpsonThermal;
import com.tpcodl.billingreading.activity.printReceipt.BillPrintPhiThermal;
import com.tpcodl.billingreading.activity.printReceipt.BillPrintSBM;
import com.tpcodl.billingreading.activity.printReceipt.BillPrintSoftlandImpact;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.models.NSBMData;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.math.BigDecimal;

public class ConsolidatedActivity extends AppCompatActivity implements
        View.OnClickListener {

    Context context;
    private ActivityUtils utils;
    DatabaseHelper db;

    ImageView iv_back;
    TextView tv_title;

    TextView tvTotalBillRecords, tvTotalBillRecordsUploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consolitated_report);
        context = this;
        utils = ActivityUtils.getInstance();
        db = new DatabaseHelper(context);

        initView();
        showConsolidatedReport();
    }

    private void showConsolidatedReport() {
        String query = "select SCHEDULE_METER_READ_DATE from TBL_SPOTBILL_HEADER_DETAILS  order by SCHEDULE_METER_READ_DATE  desc limit 1";
        Log.e("QQQQ", query);
        String SCHEDULE_METER_READ_DATE = "";
        String BILL_RECORDS = "";
        String BILL_RECORDS_UPLOADED = "";

        Cursor rs1 = db.getCalculateedData(query);
        while (rs1.moveToNext()) {
            SCHEDULE_METER_READ_DATE = rs1.getString(0);
        }
        rs1.close();

        String selectQuery = "select sum(TOT_CON) as BILL_RECORDS from (" +
                "select count(1) as TOT_CON from TBL_SPOTBILL_HEADER_DETAILS where SCHEDULE_METER_READ_DATE='" + SCHEDULE_METER_READ_DATE + "'" +
                " UNION all " +
                "select count(1) as TOT_CON from TBL_SPOTBILL_HEADER_DETAILS_TEMP where SCHEDULE_METER_READ_DATE='" + SCHEDULE_METER_READ_DATE + "' )";
        Log.e("QQQQ", selectQuery);
        rs1 = db.getCalculateedData(selectQuery);
        while (rs1.moveToNext()) {
            BILL_RECORDS = rs1.getString(0);
        }
        rs1.close();


        String selectQuery1 = "select sum(TOT_CON) as BILL_RECORDS_UPLOADED from (" +
                "select count(1) as TOT_CON from TBL_SPOTBILL_HEADER_DETAILS where SCHEDULE_METER_READ_DATE='" + SCHEDULE_METER_READ_DATE + "' and SENT_FLAG='1'" +
                "UNION all " +
                "select count(1) as TOT_CON from TBL_SPOTBILL_HEADER_DETAILS_TEMP where SCHEDULE_METER_READ_DATE='" + SCHEDULE_METER_READ_DATE + "' )";
        Log.e("QQQQ", selectQuery1);
        rs1 = db.getCalculateedData(selectQuery1);
        while (rs1.moveToNext()) {
            BILL_RECORDS_UPLOADED = rs1.getString(0);
        }
        rs1.close();

        tvTotalBillRecords.setText("Total Bill Records: " + BILL_RECORDS);
        tvTotalBillRecordsUploaded.setText("Total Bill Records Uploaded: " + BILL_RECORDS_UPLOADED);
    }


    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("View Summary");

        tvTotalBillRecords = findViewById(R.id.tvTotalBillRecords);
        tvTotalBillRecordsUploaded = findViewById(R.id.tvTotalBillRecordsUploaded);

    }

    @Override
    public void onClick(View view) {
        if (view == iv_back) {
            finish();
        }
    }
}