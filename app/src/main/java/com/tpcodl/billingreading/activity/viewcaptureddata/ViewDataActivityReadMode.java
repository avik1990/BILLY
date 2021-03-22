package com.tpcodl.billingreading.activity.viewcaptureddata;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.BillingCalculation;
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
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.Constant;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewDataActivityReadMode extends AppCompatActivity implements View.OnClickListener {
    Context context;
    LinearLayout llContainer;
    ///model calss for output
    String MeterType = "", varCond = "";
    DatabaseHelper db;
    //model class for input
    NSBMData nSBMData;
    ///for ok
    TextView txtconno;
    TextView txtmtno;
    TextView txtrdmon;

    EditText prskwh1;
    EditText prskvah1;
    EditText prskvarh1;
    EditText prsmdpeak;
    EditText prsmdoffpeak;
    TextView tvInstallationNo;
    TextView tvLegacyNo;

    // EditText prsmdtod;
    // EditText prsmdcol_consump;

    LinearLayout llkwh;
    LinearLayout llkvah;
    LinearLayout llkvarh;
    // LinearLayout llmdpeak;
    // LinearLayout llmdoffpeak;
    LinearLayout llSubContainer, llSubContainerText;
    TextView ed[];
    TextView tv[];
    /////
    Button back;
    Button print;
    List<String> listKeys = new ArrayList<>();
    private ActivityUtils utils;
    private String Usernm;
    private int sbmflg;
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
    BigDecimal diff;

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
    ConstraintLayout cclayout;
    TextView tvHeader;
    ImageView iv_back;
    TextView tv_title;

    @Override
    public ClassLoader getClassLoader() {
        return super.getClassLoader();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_readmode_data);
        context = this;
        db = new DatabaseHelper(context);
        varCond = getIntent().getStringExtra(Constant.VARCOND);
        utils = ActivityUtils.getInstance();

        initViews();

        if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
            cclayout.setVisibility(View.VISIBLE);
            tvHeader.setVisibility(View.VISIBLE);
            showBillingData();
        } else {
            tvHeader.setVisibility(View.GONE);
            cclayout.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("Input & Billed Data");

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tvHeader = findViewById(R.id.tvHeader);
        cclayout = findViewById(R.id.cclayout);
        tv_legacyno = findViewById(R.id.tv_legacyno);
        back = findViewById(R.id.back);
        tv_installation_no = findViewById(R.id.tv_installation_no);
        tv_tariff = findViewById(R.id.tv_tariff);

        print = findViewById(R.id.print);
        tv_meter_read_date = findViewById(R.id.tv_meter_read_date);
        tv_prev_arr = findViewById(R.id.tv_prev_arr);
        tv_current_reader = findViewById(R.id.tv_current_reader);
        tv_ed = findViewById(R.id.tv_ed);
        tv_dps = findViewById(R.id.tv_dps);
        tv_dc = findViewById(R.id.tv_dc);
        tv_mr = findViewById(R.id.tv_mr);
        tv_adj = findViewById(R.id.tv_adj);

        tv_prev_mtr_read = findViewById(R.id.tv_prev_mtr_read);
        tv_present_mtr_read = findViewById(R.id.tv_present_mtr_read);
        tv_movein_date = findViewById(R.id.tv_movein_date);
        tv_prev_billtype = findViewById(R.id.tv_prev_billtype);
        tv_prev_readdate = findViewById(R.id.tv_prev_readdate);
        tv_present_mtr_read_MD = findViewById(R.id.tv_present_mtr_read_MD);
        tv_prev_mtr_read_MD = findViewById(R.id.tv_prev_mtr_read_MD);
        tv_nobilled_month = findViewById(R.id.tv_nobilled_month);


        tv_rbt_date = findViewById(R.id.tv_rbt_date);
        tv_rbt_total = findViewById(R.id.tv_rbt_total);
        tv_rbt_after = findViewById(R.id.tv_rbt_after);

        print.setOnClickListener(this);
        back.setOnClickListener(this);

        llContainer = findViewById(R.id.llContainer);
        tvPresentBIlling = findViewById(R.id.tvPresentBIlling);
        tvCurrentBilling = findViewById(R.id.tvCurrentBilling);
        tvBillBasis = findViewById(R.id.tvBillBasis);
        tvAmountPayable = findViewById(R.id.tvAmountPayable);





        // tvInstallationNo=findViewById(R.id.tvInstallationNo);
        // tvLegacyNo=findViewById(R.id.tvLegacyNo);


        ////for fetching data from database
        nSBMData = db.getNBSMReadData(varCond);

        Log.d("sadf", "initViews: "+nSBMData.getINSTALLATION());

        if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
            if (AppUtils.checkCurrentBillAMTandPrint(context, nSBMData.getINSTALLATION()) == 0) {
                print.setVisibility(View.VISIBLE);
            } else {
                print.setVisibility(View.GONE);
            }
            if (AppUtils.checkMonthCount(context, nSBMData.getINSTALLATION()) == 0.0) {

                print.setVisibility(View.VISIBLE);
            } else {
                print.setVisibility(View.GONE);
            }
        } else {
            print.setVisibility(View.GONE);
        }
        if (nSBMData == null) {
            return;
        }

        fetchdataAndSubmitUsingFlag();



    }


    //String selectQuery = "select PRESENT_BILL_UNITS,CURRENT_BILL_TOTAL,BILL_BASIS,AMOUNT_PAYABLE,MRENT_CHARGED,DPS,OSBILL_DATE,DUE_DATE,ED,REBATE,MMFC,EC,DB_ADJ,ARREARS from TBL_SPOTBILL_HEADER_DETAILS where INSTALLATION='" + nSBMData.getINSTALLATION() + "'";
    private void showBillingData() {
        String selectQuery = "select a.PRESENT_BILL_UNITS,a.CURRENT_BILL_TOTAL,a.BILL_BASIS,a.AMOUNT_PAYABLE,a.MRENT_CHARGED,a.DPS,a.OSBILL_DATE,a.DUE_DATE,a.ED,a.REBATE,a.MMFC,a.EC,a.DB_ADJ, a.ARREARS," +
                "b.PREV_MTR_READ,b.PRESENT_METER_READING,a.MOVE_IN_DATE,a.PREV_BILL_TYPE,b.PREV_READ_DATE,a.NO_BILLED_MONTH,b.REGISTER_CODE,a.INSTALLATION,a.RATE_CATEGORY,a.LEGACY_ACCOUNT_NO2 from TBL_SPOTBILL_HEADER_DETAILS a,TBL_SPOTBILL_CHILD_DETAILS b where a.INSTALLATION =b.INSTALLATION and a.INSTALLATION='" + nSBMData.getINSTALLATION() + "' ";

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

    private void fetchdataAndSubmitUsingFlag() {
        //if (MeterType.equalsIgnoreCase("Ok")) {
        View child = getLayoutInflater().inflate(R.layout.row_view_ok, null);
        llkwh = child.findViewById(R.id.llkwh);
        llkvah = child.findViewById(R.id.llkvah);
        llkvarh = child.findViewById(R.id.llkvarh);
        //llmdpeak = child.findViewById(R.id.llmdpeak);
        //llmdoffpeak = child.findViewById(R.id.llmdoffpeak);
        llSubContainer = child.findViewById(R.id.llSubContainer);
        llSubContainerText = child.findViewById(R.id.llSubContainerText);
        txtconno = child.findViewById(R.id.txtconno);
        txtmtno = child.findViewById(R.id.txtmtno);
        txtrdmon = child.findViewById(R.id.txtrdmon);
        tvLegacyNo = child.findViewById(R.id.tvLegacyNo);
        tvInstallationNo = child.findViewById(R.id.tvInstallationNo);

        prskwh1 = child.findViewById(R.id.prskwh1);
        prskvah1 = child.findViewById(R.id.prskvah1);
        prskvarh1 = child.findViewById(R.id.prskvarh1);
        prsmdpeak = child.findViewById(R.id.prsmdpeak);
        prsmdoffpeak = child.findViewById(R.id.prsmdoffpeak);

        //prsmdtod = child.findViewById(R.id.prsmdtod);
        //prsmdcol_consump = child.findViewById(R.id.prsmdcol_consump);
        //prskwh1.setText(meterOkNonSbmReadingModel.getSt_kwh());
        //prskvah1.setText(meterOkNonSbmReadingModel.getSt_kvah());
        //prskvarh1.setText(meterOkNonSbmReadingModel.getSt_kvarh());
        //prsmdpeak.setText(meterOkNonSbmReadingModel.getSt_md_peak());
        //prsmdoffpeak.setText(meterOkNonSbmReadingModel.getSt_md_off_peak());
        //prsmdtod.setText(meterOkNonSbmReadingModel.getSt_tod());
        //prsmdcol_consump.setText(meterOkNonSbmReadingModel.getSt_colony_consumption());

        if (nSBMData.getLinkedHashMapValues() != null) {
            int i = -1;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );

            llSubContainer.removeAllViews();
            llSubContainerText.removeAllViews();

            for (Map.Entry<String, String> entry : nSBMData.getLinkedHashMapValues().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.e("Values", key + "  " + value);
                i++;
                tv = new TextView[nSBMData.getLinkedHashMapValues().size()];
                ed = new TextView[nSBMData.getLinkedHashMapValues().size()];
                ed[i] = new TextView(this);
                tv[i] = new TextView(this);
                params.setMargins(0, 10, 0, 10);
                //tv[i].setGravity(Gravity.CENTER_VERTICAL);
                ed[i].setId(i + 1);
                ed[i].setTag(key.trim());
                Log.e("KEYS", key);
                ed[i].setBackground(ContextCompat.getDrawable(context, R.color.textBack));
                ed[i].setHint("Enter " + key);
                //ed[i].setHeight(100);
                ed[i].setLayoutParams(params);
                ed[i].setSingleLine();
                ed[i].setTypeface(ed[i].getTypeface(), Typeface.BOLD);
                ed[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                ed[i].setPadding(5, 0, 0, 0);
                tv[i].setPadding(5, 0, 0, 0);
                ed[i].setText((value == null || value.equals("")) ? "0" : (value));
                tv[i].setText(key + ": ");
                tv[i].setLayoutParams(params);
                tv[i].setTypeface(tv[i].getTypeface(), Typeface.BOLD);
                tv[i].setAllCaps(true);
                //tv[i].setHeight(48);
                tv[i].setTextColor(Color.parseColor("#040404"));
                //tv[i].setBackground(ContextCompat.getDrawable(context, R.color.textBack));

                llSubContainerText.addView(tv[i]);
                llSubContainer.addView(ed[i]);
            }
        }

        if ((nSBMData.getINSTALLATION() != null) && (!(nSBMData.getINSTALLATION().equalsIgnoreCase("null")))) {
            tvInstallationNo.setText("Inst No: " + nSBMData.getINSTALLATION());
        }
        if ((nSBMData.getLEGACY_ACCOUNT_NO() != null) && (!(nSBMData.getLEGACY_ACCOUNT_NO().equalsIgnoreCase("null")))) {
            tvLegacyNo.setText("Legacy No: " + nSBMData.getLEGACY_ACCOUNT_NO2());
        }

        if ((nSBMData.getCA() != null) && (!(nSBMData.getCA().equalsIgnoreCase("null")))) {
            txtconno.setText("Cons No: " + nSBMData.getCA());
        }
        if ((nSBMData.getMETER_NO() != null) && (!(nSBMData.getMETER_NO().equalsIgnoreCase("null")))) {
            txtmtno.setText("Mtr No: " + nSBMData.getMETER_NO());
        }

        if ((nSBMData.getBILL_MONTH() != null) && (!(nSBMData.getBILL_MONTH().equalsIgnoreCase("null")))) {
            txtrdmon.setText("Month: " + db.getMonthName(nSBMData.getBILL_MONTH()));
        }
        llContainer.addView(child);
        // }
    }

    @Override
    public void onClick(View v) {
        if (v == back || v == iv_back) {
            finish();
        } else if (v == print) {
            Usernm = utils.getUserID();
            //to get SBM print
            sbmflg = db.getSMPVR(Usernm);
            ////
            if (sbmflg == 1) {
                Intent blprint = new Intent(getApplicationContext(), BillPrintSBM.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", nSBMData.getINSTALLATION());
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 2) { //analogic thermal blutooth printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintAnalogicThermal.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", nSBMData.getINSTALLATION());
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 3) { //Epson thermal blutooth printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintEpsonThermal.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", nSBMData.getINSTALLATION());
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 4) { //SOFTLAND IMPACT blutooth printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintSoftlandImpact.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", nSBMData.getINSTALLATION());
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 5) { //amigo IMPACT blutooth printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintAmigoImpact.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", nSBMData.getINSTALLATION());
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 6) { //Analogic IMPACT blutooth printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintAnalogicImpact.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", nSBMData.getINSTALLATION());
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 7) { //Phi Bluetooth Thermal printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintPhiThermal.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", nSBMData.getINSTALLATION());
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 8) { //Amigo Bluetooth Thermal printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintAmigoThermal.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo",nSBMData.getINSTALLATION());
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else if (sbmflg == 9) { //Analologic new Bluetooth Thermal printer
                Intent blprint = new Intent(getApplicationContext(), BillPrintAnalogicNewThermal.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", nSBMData.getINSTALLATION());
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            } else {
                Intent blprint = new Intent(getApplicationContext(), BillPrintActivity.class);
                Bundle blprintval = new Bundle();
                blprintval.putString("AcctNo", nSBMData.getINSTALLATION());
                blprint.putExtras(blprintval);
                startActivity(blprint);
                finish();
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
