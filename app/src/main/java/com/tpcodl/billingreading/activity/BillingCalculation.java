package com.tpcodl.billingreading.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.tpcodl.billingreading.R;
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

public class BillingCalculation extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener {

    Button btnPrint, btnCancel;
    String installationno;
    Context context;
    private ActivityUtils utils;
    private int sbmflg;
    DatabaseHelper db;
    private String Usernm;
    NSBMData nSBMData;
    String varCond;
    TextView tvPresentBIlling, tvCurrentBilling, tvBillBasis, tvAmountPayable;

    TextView tv_meter_read_date;
    TextView tv_prev_arr;
    TextView tv_current_reader;
    TextView tv_ed;
    TextView tv_dps;
    TextView tv_dc;
    TextView tv_mr;
    TextView tv_adj;
    TextView tv_rbt_date;
    TextView tv_rbt_total;
    TextView tv_rbt_after;

    TextView tv_prev_mtr_read;
    TextView tv_present_mtr_read;
    TextView tv_movein_date;
    TextView tv_prev_billtype;
    TextView tv_prev_readdate;
    TextView tv_present_mtr_read_MD;
    TextView tv_prev_mtr_read_MD;
    TextView tv_nobilled_month;
    TextView tv_installation_no;
    TextView tv_tariff;
    TextView tv_legacyno;
    BigDecimal diff;

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    ImageView iv_back;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_calculation);
        context = this;
        installationno = getIntent().getStringExtra("installationno");
        varCond = " where INSTALLATION='" + installationno + "'";
        utils = ActivityUtils.getInstance();
        db = new DatabaseHelper(context);
        UtilsClass.CAPTURE_IMAGE_PATH = 0;
        UtilsClass.meterPhoto = false;
        UtilsClass.testerPhoto = false;
        UtilsClass.rriImage = false;
        UtilsClass.wlImage = false;

        initView();
        showBillingData();
    }

    private void showBillingData() {
        String selectQuery = "select a.PRESENT_BILL_UNITS,a.CURRENT_BILL_TOTAL,a.BILL_BASIS,a.AMOUNT_PAYABLE,a.MRENT_CHARGED,a.DPS,a.OSBILL_DATE,a.DUE_DATE,a.ED,a.REBATE,a.MMFC,a.EC,a.DB_ADJ, a.ARREARS," +
                "b.PREV_MTR_READ,b.PRESENT_METER_READING,a.MOVE_IN_DATE,a.PREV_BILL_TYPE,b.PREV_READ_DATE,a.NO_BILLED_MONTH,b.REGISTER_CODE,a.INSTALLATION,a.RATE_CATEGORY,a.LEGACY_ACCOUNT_NO2 from TBL_SPOTBILL_HEADER_DETAILS a,TBL_SPOTBILL_CHILD_DETAILS b where a.INSTALLATION =b.INSTALLATION and a.INSTALLATION='" + installationno + "' ";

        Log.d("DemoApp", "strUpdateSQL_01 for tariff" + selectQuery);
        Cursor rs1 = db.getCalculateedData(selectQuery);
        String STREGISTER_CODE = "";
        String PRESENT_BILL_UNITS = "";
        String CURRENT_BILL_TOTAL = "";
        String BILL_BASIS = "";
        String AMOUNT_PAYABLE = "";
        String MRENT_CHARGED = "";
        String DPS = "";
        String OSBILL_DATE = "";
        String DUE_DATE = "";
        String ED = "";
        String REBATE = "";
        String MMFC = "";
        String EC = "";
        String DB_ADJ = "";
        String ARREARS = "";

        String PREV_MTR_READ = "";
        String PRESENT_METER_READING = "";
        String MOVE_IN_DATE = "";
        String PREV_BILL_TYPE = "";
        String PREV_READ_DATE = "";
        String NO_BILLED_MONTH = "";
        String prv_md = "", prs_md = "";

        String INSTALLATION = "";
        String TARIFF = "";
        String LEGACY_ACCOUNT_NO2 = "";


        while (rs1.moveToNext()) {
            STREGISTER_CODE = rs1.getString(20);
            INSTALLATION = rs1.getString(21);
            LEGACY_ACCOUNT_NO2 = rs1.getString(23);

            if (STREGISTER_CODE.equalsIgnoreCase("CKWH")) {

                PRESENT_BILL_UNITS = rs1.getString(0);
                CURRENT_BILL_TOTAL = rs1.getString(1);
                BILL_BASIS = rs1.getString(2);
                AMOUNT_PAYABLE = rs1.getString(3);
                MRENT_CHARGED = rs1.getString(4);
                DPS = rs1.getString(5);
                OSBILL_DATE = rs1.getString(6);
                DUE_DATE = rs1.getString(7);
                ED = rs1.getString(8);
                REBATE = rs1.getString(9);
                MMFC = rs1.getString(10);
                EC = rs1.getString(11);
                DB_ADJ = rs1.getString(12);
                ARREARS = rs1.getString(13);

                PREV_MTR_READ = rs1.getString(14);
                PRESENT_METER_READING = rs1.getString(15);
                MOVE_IN_DATE = rs1.getString(16);
                PREV_BILL_TYPE = rs1.getString(17);
                PREV_READ_DATE = rs1.getString(18);
                NO_BILLED_MONTH = rs1.getString(19);
                TARIFF = rs1.getString(22);

                if (BILL_BASIS != null) {
                    if (BILL_BASIS.equalsIgnoreCase("N")) {
                        BILL_BASIS = "Actual";
                    } else {
                        BILL_BASIS = "Average";
                    }
                }
            } else {
                prv_md = rs1.getString(14);
                prs_md = rs1.getString(15);
            }

            tv_meter_read_date.setText(OSBILL_DATE);
            tv_prev_arr.setText(ARREARS);
            tv_current_reader.setText(EC);
            tv_ed.setText(ED);
            tv_dps.setText(DPS);
            tv_dc.setText(MMFC);
            tv_mr.setText(MRENT_CHARGED);
            tv_adj.setText(DB_ADJ);
            tv_rbt_date.setText(DUE_DATE);
            tv_rbt_total.setText(REBATE);
            tv_legacyno.setText(LEGACY_ACCOUNT_NO2);

            tv_installation_no.setText(INSTALLATION);
            tv_tariff.setText(TARIFF);

            tv_prev_readdate.setText(PREV_READ_DATE);
            tv_prev_mtr_read.setText(PREV_MTR_READ);
            tv_present_mtr_read.setText(PRESENT_METER_READING);
            tv_movein_date.setText(MOVE_IN_DATE);
            tv_prev_billtype.setText(PREV_BILL_TYPE);
            tv_nobilled_month.setText(NO_BILLED_MONTH);
            tv_present_mtr_read_MD.setText(prv_md);
            tv_prev_mtr_read_MD.setText(prs_md);


            //double val = Double.parseDouble(AMOUNT_PAYABLE) - Double.parseDouble(REBATE);

            try {
                BigDecimal payableAmt = new BigDecimal(AMOUNT_PAYABLE);
                BigDecimal rebate = new BigDecimal(REBATE);

                diff = payableAmt.subtract(rebate);
                tv_rbt_after.setText(String.valueOf(diff));
                tv_rbt_after.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                tv_rbt_after.setVisibility(View.GONE);
            }

            tvPresentBIlling.setText(PRESENT_BILL_UNITS);
            tvCurrentBilling.setText(CURRENT_BILL_TOTAL);
            tvBillBasis.setText(BILL_BASIS);
            tvAmountPayable.setText(AMOUNT_PAYABLE);


        }

    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("Input & Billed Data");
        tv_legacyno = findViewById(R.id.tv_legacyno);
        tv_installation_no = findViewById(R.id.tv_installation_no);
        tv_tariff = findViewById(R.id.tv_tariff);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        btnPrint = findViewById(R.id.btnPrint);

        btnPrint.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


        nSBMData = db.getNBSMReadData(installationno);

        tvPresentBIlling = findViewById(R.id.tvPresentBIlling);
        tvCurrentBilling = findViewById(R.id.tvCurrentBilling);
        tvBillBasis = findViewById(R.id.tvBillBasis);
        tvAmountPayable = findViewById(R.id.tvAmountPayable);


        tv_meter_read_date = findViewById(R.id.tv_meter_read_date);
        tv_prev_arr = findViewById(R.id.tv_prev_arr);
        tv_current_reader = findViewById(R.id.tv_current_reader);
        tv_ed = findViewById(R.id.tv_ed);
        tv_dps = findViewById(R.id.tv_dps);
        tv_dc = findViewById(R.id.tv_dc);
        tv_mr = findViewById(R.id.tv_mr);
        tv_adj = findViewById(R.id.tv_adj);
        tv_rbt_date = findViewById(R.id.tv_rbt_date);
        tv_rbt_total = findViewById(R.id.tv_rbt_total);
        tv_rbt_after = findViewById(R.id.tv_rbt_after);

        tv_legacyno = findViewById(R.id.tv_legacyno);
        tv_prev_mtr_read = findViewById(R.id.tv_prev_mtr_read);
        tv_present_mtr_read = findViewById(R.id.tv_present_mtr_read);
        tv_movein_date = findViewById(R.id.tv_movein_date);
        tv_prev_billtype = findViewById(R.id.tv_prev_billtype);
        tv_prev_readdate = findViewById(R.id.tv_prev_readdate);
        tv_present_mtr_read_MD = findViewById(R.id.tv_present_mtr_read_MD);
        tv_prev_mtr_read_MD = findViewById(R.id.tv_prev_mtr_read_MD);
        tv_nobilled_month = findViewById(R.id.tv_nobilled_month);

        if (AppUtils.checkCurrentBillAMTandPrint(context, installationno) == 0) {
            btnPrint.setVisibility(View.VISIBLE);
        } else {
            btnPrint.setVisibility(View.GONE);
        }

        if (AppUtils.checkNewMeterFlag(context, installationno) == 0) {
            btnPrint.setVisibility(View.VISIBLE);
        } else {
            btnPrint.setVisibility(View.GONE);
        }
        if (AppUtils.checkMonthCount(context, installationno) == 0.0) {

            btnPrint.setVisibility(View.VISIBLE);
        } else {
            btnPrint.setVisibility(View.GONE);

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(BillingCalculation.this);
            alertDialogBuilder.setTitle("Input data improper");
            alertDialogBuilder.setMessage("Bill can not be printed")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }

    }

    @Override
    public void onClick(View view) {
        if (view == btnPrint) {
            Usernm = utils.getUserID();
            //to get SBM print
            sbmflg = db.getSMPVR(Usernm);
            ////
            if (sbmflg == 1) {
                Intent blprint = new Intent(getApplicationContext(), BillPrintSBM.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", installationno);
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 2) { //analogic thermal blutooth printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintAnalogicThermal.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", installationno);
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 3) { //Epson thermal blutooth printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintEpsonThermal.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", installationno);
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 4) { //SOFTLAND IMPACT blutooth printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintSoftlandImpact.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", installationno);
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 5) { //amigo IMPACT blutooth printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintAmigoImpact.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", installationno);
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 6) { //Analogic IMPACT blutooth printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintAnalogicImpact.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", installationno);
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 7) { //Phi Bluetooth Thermal printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintPhiThermal.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", installationno);
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 8) { //Amigo Bluetooth Thermal printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintAmigoThermal.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", installationno);
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 9) { //Analologic new Bluetooth Thermal printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintAnalogicNewThermal.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", installationno);
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else {
                Intent blprint = new Intent(getApplicationContext(), BillPrintActivity.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", installationno);
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            }
        } else if (view == btnCancel) {
            Intent intent = new Intent(context, SearchDataActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else if (view == iv_back) {
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}