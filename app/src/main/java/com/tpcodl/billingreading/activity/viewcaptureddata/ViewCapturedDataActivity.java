package com.tpcodl.billingreading.activity.viewcaptureddata;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.gson.Gson;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.BillingCalculation;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.activity.calculateBill;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.models.MeterOkNonSbmReadingModel;
import com.tpcodl.billingreading.models.NSBMData;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.reponseModel.UploadDataResponseModel;
import com.tpcodl.billingreading.uploadingrequestModel.RequestModel;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.Constant;
import com.tpcodl.billingreading.utils.DialogUtils;
import com.tpcodl.billingreading.utils.UploadUtils;
import com.tpcodl.billingreading.utils.UtilsClass;
import com.tpcodl.billingreading.webservice.ApiInterface;
import com.tpcodl.billingreading.webservice.RetrofitClientInstance;

//import org.apache.commons.collections4.MultiMap;
//import org.apache.commons.collections4.map.MultiValueMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.pow;

public class ViewCapturedDataActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    LinearLayout llContainer;
    ///model calss for output
    MeterOkNonSbmReadingModel meterOkNonSbmReadingModel;
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
    // EditText prsmdtod;
    // EditText prsmdcol_consump;

    LinearLayout llkwh;
    LinearLayout llkvah;
    LinearLayout llkvarh;
    // LinearLayout llmdpeak;
    // LinearLayout llmdoffpeak;
    LinearLayout llSubContainer, llSubContainerText;
    EditText ed[];
    TextView tv[];
    private DialogUtils dUtils;
    private ActivityUtils utils;
    private RequestModel model;
    UploadUtils uUtils;
    /////
    Button back;
    Button submit;
    List<String> listKeys = new ArrayList<>();
    TextView tvInstallationNo, tvLegacyNo;
    int countflg = 0;
    int insertCounter = 0;
    Spinner spOverrideQc;
    String stQCFlag = "";
    TextView tv_title;
    ImageView iv_back;

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;

    @Override
    public ClassLoader getClassLoader() {
        return super.getClassLoader();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_captured_data);
        context = this;
        model = new RequestModel();
        utils = ActivityUtils.getInstance();

        UtilsClass.CAPTURE_IMAGE_PATH = 0;
        UtilsClass.meterPhoto = false;
        UtilsClass.testerPhoto = false;
        UtilsClass.rriImage = false;
        UtilsClass.wlImage = false;

        dUtils = new DialogUtils(context);
        db = new DatabaseHelper(context);
        MeterType = getIntent().getStringExtra(Constant.METER_TYPE);
        varCond = getIntent().getStringExtra(Constant.VARCOND);
        meterOkNonSbmReadingModel = (MeterOkNonSbmReadingModel) getIntent().getSerializableExtra(Constant.PASS_DATA);

        initViews();
    }

    private void initViews() {
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("Confirmation");

        iv_back = findViewById(R.id.iv_back);
        back = findViewById(R.id.back);
        spOverrideQc = findViewById(R.id.spOverrideQc);
        llContainer = findViewById(R.id.llContainer);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        back.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        //for fetching data from database
        nSBMData = db.getNBSMData(varCond, -1);

        if (nSBMData == null) {
            return;
        }

        fetchdataAndSubmitUsingFlag();
        getCoordinates();

        /*spOverrideQc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String TypeName = spOverrideQc.getSelectedItem().toString();
                if (TypeName.equalsIgnoreCase("Yes")) {
                    stQCFlag = "1";
                } else {
                    stQCFlag = "0";
                }
                Toast.makeText(context, TypeName + " stQCFlag", Toast.LENGTH_SHORT).show();
                meterOkNonSbmReadingModel.setOverriceQC(stQCFlag);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    private void fetchdataAndSubmitUsingFlag() {
        //if (MeterType.equalsIgnoreCase("Ok")) {
        View child = getLayoutInflater().inflate(R.layout.row_view_ok, null);
        llkwh = child.findViewById(R.id.llkwh);
        tvInstallationNo = child.findViewById(R.id.tvInstallationNo);
        tvLegacyNo = child.findViewById(R.id.tvLegacyNo);
        llkvah = child.findViewById(R.id.llkvah);
        llkvarh = child.findViewById(R.id.llkvarh);
        //llmdpeak = child.findViewById(R.id.llmdpeak);
        //llmdoffpeak = child.findViewById(R.id.llmdoffpeak);
        llSubContainer = child.findViewById(R.id.llSubContainer);
        llSubContainerText = child.findViewById(R.id.llSubContainerText);
        txtconno = child.findViewById(R.id.txtconno);
        txtmtno = child.findViewById(R.id.txtmtno);
        txtrdmon = child.findViewById(R.id.txtrdmon);

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
        prsmdpeak.setText(meterOkNonSbmReadingModel.getSt_md_peak());
        prsmdoffpeak.setText(meterOkNonSbmReadingModel.getSt_md_off_peak());
        //prsmdtod.setText(meterOkNonSbmReadingModel.getSt_tod());
        //prsmdcol_consump.setText(meterOkNonSbmReadingModel.getSt_colony_consumption());

        if (meterOkNonSbmReadingModel.getLinkedHashMapValues() != null) {
            int i = -1;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );

            llSubContainer.removeAllViews();
            llSubContainerText.removeAllViews();

            listKeys.clear();
            String firstVAL = "";
            String MDKWVAL = "";
            for (Map.Entry<String, String> entry : meterOkNonSbmReadingModel.getLinkedHashMapValues().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key.contains("CKWH")) {
                    firstVAL = value;
                }

                if (key.contains("MDKW")) {
                    MDKWVAL = value;
                }

                ///Log.e("MDKWVAL", MDKWVAL);
                ///Log.e("Values", key + "  " + value);
                i++;
                listKeys.add(key);
                tv = new TextView[meterOkNonSbmReadingModel.getLinkedHashMapValues().size()];
                ed = new EditText[meterOkNonSbmReadingModel.getLinkedHashMapValues().size()];
                ed[i] = new EditText(this);
                tv[i] = new TextView(this);
                params.setMargins(0, 10, 0, 10);
                //tv[i].setGravity(Gravity.CENTER_VERTICAL);
                ed[i].setId(i + 1);
                ed[i].setTag(key.trim());
                Log.e("KEYS", key);
                ed[i].setBackground(ContextCompat.getDrawable(context, R.color.textBack));
                ed[i].setHint("Enter " + key);
                ed[i].setHeight(100);
                ed[i].setLayoutParams(params);
                ed[i].setSingleLine();
                ed[i].setTypeface(ed[i].getTypeface(), Typeface.BOLD);

                //ed[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                //non-sbm - 8,2
                //sbm - 7,2
                if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                    if (key.contains("CKWH")) {
                        ed[i].setInputType(InputType.TYPE_CLASS_NUMBER);
                        UtilsClass.restrictNumberDecimal(i, ed, 7, 0);
                    } else {
                        ed[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        UtilsClass.restrictNumberDecimal(i, ed, 7, 2);
                    }
                } else {
                    ed[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    UtilsClass.restrictNumberDecimal(i, ed, 8, 2);
                }

                ed[i].setPadding(5, 5, 5, 5);

                if (key.contains("CKVAH")) {
                    if (value.isEmpty()) {
                        double vals = 0.0;
                        vals = Double.parseDouble(firstVAL) / 0.93;
                        ed[i].setText(String.format("%.02f", vals));
                    } else if (Double.parseDouble(value) == 0) {
                        double vals = 0.0;
                        vals = Double.parseDouble(firstVAL) / 0.93;
                        ed[i].setText(String.format("%.02f", vals));
                    } else {
                        ed[i].setText((value == null || value.equals("")) ? "0" : (value));
                    }
                } else if (key.contains("MDKVA")) {
                    if (value.isEmpty()) {
                        if (!MDKWVAL.isEmpty()) {
                            ed[i].setText(MDKWVAL);
                        } else {
                            ed[i].setText((value == null || value.equals("")) ? "0" : (value));
                        }
                    } else if (Double.parseDouble(value) == 0) {
                        if (!MDKWVAL.isEmpty()) {
                            ed[i].setText(MDKWVAL);
                        } else {
                            ed[i].setText((value == null || value.equals("")) ? "0" : (value));
                        }
                    } else {
                        ed[i].setText((value == null || value.equals("")) ? "0" : (value));
                    }
                } else {
                    ed[i].setText((value == null || value.equals("")) ? "0" : (value));
                }

                tv[i].setText(key + ": ");
                tv[i].setLayoutParams(params);
                tv[i].setTypeface(tv[i].getTypeface(), Typeface.BOLD);
                tv[i].setAllCaps(true);
                tv[i].setHeight(60);
                //tv[i].setPadding(5, 5, 5, 5);
                tv[i].setTextColor(Color.parseColor("#040404"));
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
        if ((nSBMData.getMETER_NO() != null) && (!(nSBMData.getMETER_NO().equalsIgnoreCase("null")))) {
            txtmtno.setText("Mtr No: " + nSBMData.getMETER_NO());
        }
        String month = nSBMData.getBILL_MONTH();
        String years = nSBMData.getBILL_YEAR();

        if ((month == null) || (month.equalsIgnoreCase("null"))) {
            month = "";
        }
        if ((years == null) || (years.equalsIgnoreCase("null"))) {
            years = "";
        }

        txtrdmon.setText("Month: " + db.getMonthName(month) + " " + years);
        llContainer.addView(child);
        // }
    }

    @Override
    public void onClick(View v) {
        if (v == back || v == iv_back) {
            finish();
        } else if (v == submit) {
            int count = llSubContainer.getChildCount();
            for (int i = 0; i < count; i++) {
                View v1 = llSubContainer.getChildAt(i);
                if (v1 instanceof EditText) {
                    EditText s = (EditText) v1;
                    Log.e("VALUEEEEETEST", s.getText().toString().trim());
                    db.uosertOkNonSbmReadingCHILD((s.getText().toString().trim() == null || s.getText().toString().trim().equals("")) ? "0" : (s.getText().toString().trim()), listKeys.get(i), nSBMData.getINSTALLATION(), meterOkNonSbmReadingModel.getMr_reason());
                }
            }

            new AsyncTaskExample().execute();
        }
    }

    private class AsyncTaskExample extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e("insertCounter", "-1" + insertCounter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("meterOkNonSbm", "" + meterOkNonSbmReadingModel.getLinkedHashMapImages().size());
            if (MeterType.equalsIgnoreCase("MM")) {
                db.updateMMRemarksNonSbmReading(meterOkNonSbmReadingModel, nSBMData.getINSTALLATION());
            } else if (MeterType.equalsIgnoreCase("FU")) {
                db.updateFURemarksNonSbmReading(meterOkNonSbmReadingModel, nSBMData.getINSTALLATION());
            } else if (MeterType.equalsIgnoreCase("GB")) {
                db.updateGBRemarksNonSbmReading(meterOkNonSbmReadingModel, nSBMData.getINSTALLATION());
            } else if (MeterType.equalsIgnoreCase("SB")) {
                db.updateSBRemarksNonSbmReading(meterOkNonSbmReadingModel, nSBMData.getINSTALLATION());
            } else if (MeterType.equalsIgnoreCase("MU")) {
                db.updateMURemarksNonSbmReading(meterOkNonSbmReadingModel, nSBMData.getINSTALLATION());
            } else {
                db.uosertOkNonSbmReadingHeader(meterOkNonSbmReadingModel, nSBMData.getINSTALLATION());
            }

            Log.e("insertCounter", "" + insertCounter);
            //inserting_images
           /* if (insertCounter == 0) {
                if (meterOkNonSbmReadingModel.getLinkedHashMapImages().size() > 0) {
                    db.updateFieldDescTable(meterOkNonSbmReadingModel.getLinkedHashMapImages(), nSBMData, MeterType);
                }
            }*/
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            insertCounter = 1;

            if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                int QcCheckKwh = 0;
                int QcOverride = 0;
                //  int QcCheckMd=0;
                QcCheckKwh = ChkQCVal(nSBMData.getINSTALLATION(), "kwh", context);
                Log.d("DemoApp", " QcCheckKwh" + QcCheckKwh);
                Log.d("DemoApp", " countflg" + countflg);
                if (QcCheckKwh == 4 || QcCheckKwh == 44) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    if (QcCheckKwh == 4) {
                        alertDialogBuilder.setTitle("Dial Over Case  ");
                    } else {
                        alertDialogBuilder.setTitle("RQC LOGIC ON ");
                    }
                    alertDialogBuilder.setMessage("Please Check the Reading")
                            .setCancelable(false)
                            .setPositiveButton("Check", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //   Intent setmenuect = new Intent(getApplicationContext(), SearchDataActivity.class);
                                    // startActivity(setmenuect);
                                    //finish();
                                    dialog.cancel();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else {
                    try {
                   /* QcOverride=chkQCOverride(nSBMData.getINSTALLATION(), "kwh", context);
                    if(QcOverride==1){
                        QcCheckKwh=0;
                    }*/
                        calculateBill.CalculateBill(nSBMData.getINSTALLATION(), MeterType, context, QcCheckKwh);
                        //calculateBill.CalculateBill(nSBMData.getINSTALLATION(), MeterType, context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (UtilsClass.isConnected(context)) {
                        uUtils = new UploadUtils(context);
                        uUtils.getHeaderdetails(varCond);
                        model = uUtils.getChilddetails(varCond);
                        if (AppUtils.checkArrersAndUpload(context, nSBMData.getINSTALLATION()) == 0) {
                            uploadData();
                        } else {
                            UtilsClass.showToastLong(context, getString(R.string.arrers_msg));
                            RedirectToPage();
                        }
                    } else {
                        UtilsClass.showToastShort(context, getString(R.string.msg_datasaved));
                        RedirectToPage();
                    }
                }
            } else {
                //Non-SBM
                int QcCheckKwh = 0;
                int QcOverride = 0;
                //  int QcCheckMd=0;
                QcCheckKwh = ChkQCValNsbm(nSBMData.getINSTALLATION(), "kwh", context);
                Log.d("DemoApp", " QcCheckKwh" + QcCheckKwh);
                Log.d("DemoApp", " countflg" + countflg);
                if (QcCheckKwh == 4 || QcCheckKwh == 44) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    if (QcCheckKwh == 4) {
                        alertDialogBuilder.setTitle("Dial Over Case  ");
                    } else {
                        alertDialogBuilder.setTitle("RQC LOGIC ON ");
                    }
                    alertDialogBuilder.setMessage("Please Check the Reading")
                            .setCancelable(false)
                            .setPositiveButton("Check", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //   Intent setmenuect = new Intent(getApplicationContext(), SearchDataActivity.class);
                                    // startActivity(setmenuect);
                                    //finish();
                                    dialog.cancel();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else {
                    try {
                        UpdateNONSBMData();  ///
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (UtilsClass.isConnected(context)) {
                        uUtils = new UploadUtils(context);
                        uUtils.getHeaderdetails(varCond);
                        model = uUtils.getChilddetails(varCond);
                        uploadData();
                    } else {
                        UtilsClass.showToastShort(context, getString(R.string.msg_datasaved));
                        RedirectToPage();
                    }
                }
            }
        }
    }


    private void UpdateNONSBMData() {
        String prs_mtrcond = "";
        String strMrTime = "";
        String strBillDate = "";
        String strCurDt = "";
        String strSelectSQL_01 = "";
        DatabaseHelper db = new DatabaseHelper(context);
        strSelectSQL_01 = "SELECT CASE WHEN A.RATE_CATEGORY='DOM_OTH' THEN 'DOM' WHEN A.RATE_CATEGORY='DKJ' THEN 'BPL' WHEN A.RATE_CATEGORY='LT_GENPRPS' THEN 'GPS' WHEN A.RATE_CATEGORY='LT_SPBLPRS' THEN 'SPP' ELSE 'DOM' END TARIFF " +
                " ,CASE WHEN B.MF >= 1 THEN B.MF ELSE 1 END AS MF,A.CONSUMER_OWNED,B.NO_OF_DIGITS,A.SAN_LOAD,CAST(B.PREV_MTR_READ AS LONG) as PREV_MTR_READ,A.CR_ADJ, A.DB_ADJ,A.PRV_BILLED_AMT, " +
                " A.PREVIOUS_BILLED_PROV_UNIT,A.AVERAGE_KWH,A.ED_EXEMPT,CAST(B.PRESENT_METER_READING AS LONG) as PRESENT_METER_READING,B.METER_CONDITION P_STATUS,B.METER_CONDITION,B.METER_TYP,A.HL_MONTHS NO_OF_MON " +
                " ,B.PREVIOUS_MD,A.HOSTEL_RBT,A.MOVE_IN_DATE, PREV_READ_DATE " +
                " ,(strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', B.PREV_READ_DATE)*12 + strftime('%m', B.PREV_READ_DATE)) NEWCONN_MONTHS " +
                " ,A.AVERAGE_KWH,A.PREV_BILL_UNITS,A.PRESENT_BILL_TYPE,B.METER_CONDITION MTR_COND_PRV,A.AVERAGE_KWH,A.DPS,A.MISC_CHARGES,A.PRV_ARR,A.ARREARS,A.AIFI " +
                " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', B.PREV_READ_DATE)*12 + strftime('%m', B.PREV_READ_DATE))-1 +((strftime('%d',DATE(B.PREV_READ_DATE,'start of month','+1 month','-1 day'))-strftime('%d',B.PREV_READ_DATE)*1.0))/strftime('%d',DATE(B.PREV_READ_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) MONTHS_CNT  " +
                " ,B.REGISTER_CODE,A.METER_RENT,A.ED_RBT,A.ULF,A.MRREASON,LTRIM(B.CONSUMPTION_OLD_METER,'0'),LTRIM(B.LAST_OK_RDNG,'0'),A.LAST_NORMAL_BILL_DATE  " +
                " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', A.LAST_NORMAL_BILL_DATE)*12 + strftime('%m', A.LAST_NORMAL_BILL_DATE))-1 +((strftime('%d',DATE(A.LAST_NORMAL_BILL_DATE,'start of month','+1 month','-1 day'))-strftime('%d',A.LAST_NORMAL_BILL_DATE)*1.0)+1)/strftime('%d',DATE(A.LAST_NORMAL_BILL_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) DFMON_CNT  " +
                ",A.AVG_UNIT_BILLED,A.PREV_BILL_REMARK,A.PREV_BILL_TYPE " +//44
                " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', A.MOVE_IN_DATE)*12 + strftime('%m', A.MOVE_IN_DATE))-1 +((strftime('%d',DATE(A.MOVE_IN_DATE,'start of month','+1 month','-1 day'))-strftime('%d',A.MOVE_IN_DATE)*1.0)+1)/strftime('%d',DATE(A.MOVE_IN_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) movin_CNT1  " +
                "  ,A.LAST_NORMAL_BILL_DATE,DATE('now', 'localtime'),A.PREV_PROV_AMT,A.FC_SLAB,A.PROV_ED,A.PROV_PPT_AMT,A.SUPPLY_STATUS_ID,a.new_mtr_flg,a.new_meter_no,LTRIM(b.METER_NO,'0'),A.insert_date,A.DPS_5,A.DPS_LVD  " +
                "  FROM TBL_SPOTBILL_HEADER_DETAILS A,TBL_SPOTBILL_CHILD_DETAILS B " +
                "  WHERE A.INSTALLATION=B.INSTALLATION AND A.INSTALLATION='" + nSBMData.getINSTALLATION() + "' " +
                "  AND B.REGISTER_CODE ='CKWH' " +
                "  ORDER BY B.REGISTER_CODE";

        Log.d("DemoApp", "strUpdateSQL_01  " + strSelectSQL_01);
        Cursor rs = db.getCalculateedData(strSelectSQL_01);
        while (rs.moveToNext()) {
            String strMeterCond_Prv = rs.getString(25);
            prs_mtrcond = strMeterCond_Prv;
        }

        rs.close();

        strSelectSQL_01 = "SELECT DATE('now', 'localtime'), DATE('now', '+7 days', 'localtime') AS DUE_DATE,"
                + "strftime('%H%M%S','now', 'localtime'), DATE('now', '+4 days', 'localtime')  AS SPP_DUE_DATE,strftime('%Y%m%d','now') as curdate ";
        rs = db.getCalculateedData(strSelectSQL_01);
        while (rs.moveToNext()) {
            strMrTime = rs.getString(2);
            strBillDate = rs.getString(0);
            strCurDt = rs.getString(4);
           /*strDueDate = rs.getString(1);
            strMrTime = rs.getString(2);
            strDueDateSPP = rs.getString(3);
            strCurDt = rs.getString(4);*/
        }
        rs.close();

        //Added on this logic for shorting walking sequence////////
        if (strMrTime.length() == 5) {
            strMrTime = "0" + strMrTime;
        }

        /// update last day of previous month
        String lastdayofprvmonth = "";
        //String q = "SELECT date('" + strBillDate + "','start of month','0 month','-1 day') as LastDate";
        String q = "SELECT date(SCHEDULE_METER_READ_DATE,'start of month','0 month','-1 day') as LastDate from TBL_SPOTBILL_HEADER_DETAILS where INSTALLATION='" + nSBMData.getINSTALLATION() + "'";

        Log.e("QUERYLastDate", q);
        Cursor rs1 = db.getCalculateedData(q);
        while (rs1.moveToNext()) {
            lastdayofprvmonth = rs1.getString(0);
        }

        rs1.close();

        ///for non-sbm update query
        String SbmblNo = nSBMData.getINSTALLATION().substring(4) + strCurDt.substring(2);
        String updateQueryNonSbm = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                "PRESENT_METER_STATUS= '" + prs_mtrcond + "'," +
                "PRESENT_READING_TIME= '" + strMrTime + "'," +
                "OSBILL_DATE= '" + strBillDate + "'," +
                "PRESENT_READING_DATE= '" + lastdayofprvmonth + "'," +
                "READ_FLAG = 1 ," +
                "SBM_BILL_NO= '" + SbmblNo + "'," +
                "PRESENT_READING_REMARK= '" + MeterType + "'," +
                "MRREASON='" + MeterType + "'" +
                " WHERE INSTALLATION='" + nSBMData.getINSTALLATION() + "'";
        Log.e("updateQueryNonSbm", updateQueryNonSbm);
        db.updateMTRCONTD(updateQueryNonSbm);
    }


    @Override
    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }

    public void uploadData() {
        if (AppUtils.isInternetAvailable(this)) {
            dUtils.showDialog("Uploading", "Please wait..");
            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
            Call<UploadDataResponseModel> call = service.uploadData(utils.getAuthToken(), model);
            Gson g1 = new Gson();
            Log.e("UPLOADJSON123456", g1.toJson(model));

            call.enqueue(new Callback<UploadDataResponseModel>() {

                @Override
                public void onResponse(Call<UploadDataResponseModel> call, Response<UploadDataResponseModel> response) {
                    UploadDataResponseModel object = response.body();
                    dUtils.dismissDialog();
                    try {
                        if (object.statusCode == 200) {
                            db.updatesendFlag(utils.getSerchCondition());
                            UtilsClass.showToastLong(context, object.message);
                            try {
                                utils.setAppVersion(object.softwareVersionNo.toString().trim());
                            } catch (Exception e) {

                            }

                            RedirectToPage();
                        } else if (object.statusCode == 410) {
                            /*UtilsClass.showToastLong(context, object.message);
                            db.updatesendFlag(utils.getSerchCondition());
                            RedirectToPage();*/
                            try {
                                utils.setAppVersion(object.softwareVersionNo.toString().trim());
                            } catch (Exception e) {

                            }

                            db.updatesendFlag(utils.getSerchCondition());
                            dUtils.dismissDialog();
                            androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context).create();
                            alertDialog.setTitle("Message");
                            alertDialog.setCancelable(false);
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setMessage(object.message);
                            alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "PROCEED",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            RedirectToPage();

                                        }
                                    });
                            alertDialog.show();
                            // dUtils.dismissDialog();
                        } else if (object.statusCode == 404) {

                            UtilsClass.showToastLong(context, object.message);
                            /*db.updatesendFlag(utils.getSerchCondition());*/
                            RedirectToPage();
                            // dUtils.dismissDialog();
                        } else {
                            showErrorDialog("Error", object.message);
                        }
                    } catch (Exception e) {
                        showErrorDialog("Error", "Something went wrong!!");
                        RedirectToPage();
                    }
                }

                @Override
                public void onFailure(Call<UploadDataResponseModel> call, Throwable t) {
                    dUtils.dismissDialog();
                    t.printStackTrace();
                    Log.i("TAG", t.getMessage());
                    Toast.makeText(ViewCapturedDataActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            dUtils.dismissDialog();
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            RedirectToPage();
        }
    }


    public void RedirectToPage() {
        if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
            Intent intent = new Intent(context, BillingCalculation.class);
            intent.putExtra("installationno", nSBMData.getINSTALLATION());
            intent.putExtra("MR_REASON", MeterType);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(context, SearchDataActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private int chkQCOverride(String InstalationNo, String typeval, Context context) {
        int cntOverride = 0;
        DatabaseHelper helper = new DatabaseHelper(context);
        //if(typeval.equalsIgnoreCase("kwh")){
        String strSelectSQL_01 = "SELECT OTHER_FLGS FROM TBL_SPOTBILL_HEADER_DETAILS WHERE INSTALLATION='" + InstalationNo + "' ";
        Log.d("DemoApp", "strUpdateSQL_01  " + strSelectSQL_01);
        Cursor rs = helper.getCalculateedData(strSelectSQL_01);
        String qcoride = "0";
        try {
            while (rs.moveToNext()) {
                qcoride = rs.getString(0);
            }
        } catch (Exception e) {
            qcoride = "0";
        }
        rs.close();
        if (qcoride.equalsIgnoreCase("1")) {
            cntOverride = 1;
        }
        return cntOverride;

    }

    private int ChkQCVal(String InstalationNo, String typeval, Context context) {
        int chkKwhFlg = 0;
        long lReadMF_Mdival = 0;
        double MdivalCurr = 0;
        double MdivalPrev = 0;
        String MdMtrCond = "";
        long consmpOldMtr = 0;
        double dAmountSplRebate = 0;
        int counter = 0;
        long lReadCurr = 0;
        long lReadPrev = 0;
        long lReadMF_Kwh = 0;
        String strMeterCond = "";
        String strMeterCondmdi = "";
        String Meterrent = "";
        long LstOKRead = 0;
        String lstOkReadDate = "";
        double mon_cntDef = 0;
        long avgUntBld = 0;
        String mrNote = "";
        long defProvunit = 0;
        int defOkFlg = 0;
        long billedMd = 0;
        double dAmountED = 0;
        double dPrsentBlAmount = 0;
        double dAmountRebate = 0;
        double lUnits_Kwh = 0;
        double dAmountArrear = 0;
        String strTariff = "";
        long lConLoad = 0;
        int iBillMonths = 0;
        String strConLoadUoM = "";
        String strBillBasis = "A";
        String strMrTime = "";
        int iMeterType = 1;
        int iBilled_cnt = 0;
        double month_cnt = 0;
        String prvBlRemark = "";
        int dialOver = 0;
        DatabaseHelper helper = new DatabaseHelper(context);
        //if(typeval.equalsIgnoreCase("kwh")){
        String strSelectSQL_01 = "SELECT CASE WHEN A.RATE_CATEGORY='DOM_OTH' THEN 'DOM' WHEN A.RATE_CATEGORY='DKJ' THEN 'BPL' WHEN A.RATE_CATEGORY='LT_GENPRPS' THEN 'GPS' WHEN A.RATE_CATEGORY='LT_SPBLPRS' THEN 'SPP' ELSE 'DOM' END TARIFF " +
                " ,CASE WHEN B.MF >= 1 THEN B.MF ELSE 1 END AS MF,A.CONSUMER_OWNED,B.NO_OF_DIGITS,A.SAN_LOAD,CAST(B.PREV_MTR_READ AS LONG) as PREV_MTR_READ,A.CR_ADJ, A.DB_ADJ,A.PRV_BILLED_AMT, " +
                " A.PREVIOUS_BILLED_PROV_UNIT,A.AVERAGE_KWH,A.ED_EXEMPT,CAST(B.PRESENT_METER_READING AS LONG) as PRESENT_METER_READING,B.METER_CONDITION P_STATUS,B.METER_CONDITION,B.METER_TYP,A.HL_MONTHS NO_OF_MON " +
                " ,B.PREVIOUS_MD,A.HOSTEL_RBT,A.MOVE_IN_DATE, PREV_READ_DATE " +
                " ,(strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', B.PREV_READ_DATE)*12 + strftime('%m', B.PREV_READ_DATE)) NEWCONN_MONTHS " +
                " ,A.AVERAGE_KWH,A.PREV_BILL_UNITS,A.PRESENT_BILL_TYPE,B.METER_CONDITION MTR_COND_PRV,A.AVERAGE_KWH,A.DPS,A.MISC_CHARGES,A.PRV_ARR,A.ARREARS,A.AIFI " +
                " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', B.PREV_READ_DATE)*12 + strftime('%m', B.PREV_READ_DATE))-1 +((strftime('%d',DATE(B.PREV_READ_DATE,'start of month','+1 month','-1 day'))-strftime('%d',B.PREV_READ_DATE)*1.0))/strftime('%d',DATE(B.PREV_READ_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) MONTHS_CNT  " +
                " ,B.REGISTER_CODE,A.METER_RENT,A.ED_RBT,A.ULF,A.MRREASON,B.CONSUMPTION_OLD_METER,LTRIM(B.LAST_OK_RDNG,'0'),A.LAST_NORMAL_BILL_DATE  " +
                " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', A.LAST_NORMAL_BILL_DATE)*12 + strftime('%m', A.LAST_NORMAL_BILL_DATE))-1 +((strftime('%d',DATE(A.LAST_NORMAL_BILL_DATE,'start of month','+1 month','-1 day'))-strftime('%d',A.LAST_NORMAL_BILL_DATE)*1.0)+1)/strftime('%d',DATE(A.LAST_NORMAL_BILL_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) DFMON_CNT  " +
                ",A.AVG_UNIT_BILLED,A.PREV_BILL_REMARK,A.PREV_BILL_TYPE " +//44
                " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', A.MOVE_IN_DATE)*12 + strftime('%m', A.MOVE_IN_DATE))-1 +((strftime('%d',DATE(A.MOVE_IN_DATE,'start of month','+1 month','-1 day'))-strftime('%d',A.MOVE_IN_DATE)*1.0)+1)/strftime('%d',DATE(A.MOVE_IN_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) movin_CNT1  " +
                "  ,A.LAST_NORMAL_BILL_DATE,DATE('now', 'localtime'),A.PREV_PROV_AMT,A.FC_SLAB,A.PROV_ED,A.PROV_PPT_AMT  " +
                "  FROM TBL_SPOTBILL_HEADER_DETAILS A,TBL_SPOTBILL_CHILD_DETAILS B " +
                "  WHERE A.INSTALLATION=B.INSTALLATION AND A.INSTALLATION='" + InstalationNo + "' " +
                "  AND B.REGISTER_CODE ='CKWH' " +
                "  ORDER BY B.REGISTER_CODE";

        Log.d("DemoApp", "strUpdateSQL_01  " + strSelectSQL_01);
        Cursor rs = helper.getCalculateedData(strSelectSQL_01);


        int prvBlTyp = 0;
        int fcSlabCnt = 1;
        double avgunitblled = 0;
        int adjflg = 0;
        double movinCnt = 0;

        double MonthCnt = 0;
        String MovinDate = "";
        // String PrvReadDate="";
        String PrvOKBillDate = "";
        String CurrentDate = "";
        String FromDate = "";
        String Todate = "";
        int CntParam = 0;
        double MonthCntprvCnt = 0;
        double MonthCntmovCnt = 0;
        long lAvgNMUnits = 0;
        while (rs.moveToNext()) {
            MovinDate = rs.getString(19);
            //PrvReadDate=rs.getString(20);
            PrvOKBillDate = rs.getString(46);
            CurrentDate = rs.getString(47);
            double provisional_amt = rs.getDouble(48);
            double prvisional_Ed = rs.getDouble(50);
            double provAdjRebate = rs.getDouble(51);
            Log.d("DemoApp", " found ");
            consmpOldMtr = rs.getInt(38);
            LstOKRead = rs.getLong(39);
            lstOkReadDate = rs.getString(40);
            mon_cntDef = rs.getDouble(41);
            // avgUntBld=rs.getLong(42);
            prvBlRemark = rs.getString(43);
            prvBlTyp = rs.getInt(44);
            movinCnt = rs.getDouble(45);
            Meterrent = rs.getString(34);
            String Regcode = rs.getString(33);
            int iMeterChargeFlag = 1;
            strTariff = rs.getString(0);
            double dConLoad = rs.getDouble(4);
            lConLoad = (long) Math.ceil(dConLoad);
            if (lConLoad < 1) lConLoad = 1;
            //   if (Regcode.equalsIgnoreCase("CKWH")) {
            lReadMF_Kwh = rs.getLong(1);
            lReadPrev = rs.getLong(5);
            lReadCurr = rs.getLong(12);
            strMeterCond = rs.getString(14);//not used
            //   }
            Log.d("DemoApp", "not lReadCurr " + lReadCurr);
            // String   lReadPrevrrrrr = rs.getString(5);


            Log.d("DemoApp", "not lReadPrev tyrtyr " + lReadPrev);
            Log.d("DemoApp", "not MdivalPrev " + MdivalPrev);
            Log.d("DemoApp", "not MdivalCurr " + MdivalCurr);
            Log.d("DemoApp", "not provAdjRebate " + provAdjRebate);


            double dAdj_CR = rs.getDouble(6);
            double dAdj_DR = rs.getDouble(7);
            String strMeterOwner = rs.getString(2);
            //  try {
            //    iMeterType = Integer.parseInt(rs.getString(15));
            //} catch (Exception e) {
            //  iMeterType = 1;
            //}
            //   Log.d("DemoApp", "not found12");

            Double strED_Exempt = rs.getDouble(35);
            // Double lED_Applicable = getED_Applicable(strED_Exempt);
            //  Double lED_Applicable =strED_Exempt;

            try {
                fcSlabCnt = rs.getInt(49);  //hl months ??????????????????????????????????????????????????????????????????????
            } catch (Exception e) {
                fcSlabCnt = 1;
            }

            if (fcSlabCnt >= 1) {
                iBillMonths = fcSlabCnt + 1;
            } else {
                iBillMonths = 1;
            }

            Log.d("DemoApp", "not iBillMonths 1   " + iBillMonths);
            Log.d("DemoApp", "not iBillMonths   " + iBillMonths);
            Log.d("DemoApp", "not fcSlabCnt   " + fcSlabCnt);
            //lConLoad = (long) Math.max(dMD_Current, dMD_prev);


            int iSplReb_Flag = rs.getInt(18);//hostel rebate
            Log.d("DemoApp", " found iSplReb_Flag" + iSplReb_Flag);
            String strDate_Ind_Conn = rs.getString(19);
            String strDate_Ind_LastMR = rs.getString(20);
            int iNewConnMonths = rs.getInt(21);
            // long lAvgUnits = rs.getLong(22); // not required
            //Log.d("DemoApp", "lAvgUnits 1" + lAvgUnits);
            long lLRCUnits = rs.getLong(23);
            String strMeterCond_Prv = rs.getString(25);   ////
            lAvgNMUnits = rs.getLong(26);
            lAvgNMUnits = Math.abs(lAvgNMUnits);
            Log.d("DemoApp", "lAvgNMUnits 1" + lAvgNMUnits);
            double dps = rs.getDouble(27);
            double misc_chg = rs.getDouble(28);
            // double provisional_amt = rs.getDouble(8);
            int Hlunits = rs.getInt(9);
            defProvunit = Hlunits;
            double prvdps = rs.getDouble(29);
            double prsyrarr = rs.getDouble(30);
            int aifi = rs.getInt(31);//added on 29.03.2017
            mrNote = rs.getString(37);
            long lLFUnits = rs.getInt(36);
            month_cnt = rs.getDouble(32);//sap

            MonthCntprvCnt = rs.getDouble(32);//sap
            MonthCntmovCnt = movinCnt;
            //  prvBlRemark = "OK";//////////////////////////////////////////////////////////////////////////////////////////////
            ///Handle previous meter condition from SAP
              /*  if(prvBlRemark.equalsIgnoreCase("OK")||prvBlRemark.equalsIgnoreCase("MM")||prvBlRemark.equalsIgnoreCase("GB")||prvBlRemark.equalsIgnoreCase("SB")||prvBlRemark.equalsIgnoreCase("MU")){
                    strMeterCond_Prv="O";
                }else if(prvBlRemark.equalsIgnoreCase("NV")||prvBlRemark.equalsIgnoreCase("MB")||prvBlRemark.equalsIgnoreCase("FU")||prvBlRemark.equalsIgnoreCase("ND")){
                    strMeterCond_Prv="F";
                }else{
                    strMeterCond_Prv="O";
                }*/
            //to handle Faulty meter to OK
            FromDate = strDate_Ind_LastMR;
            Todate = CurrentDate;


            //handle bill basis
            if (strMeterCond_Prv.equalsIgnoreCase("O")) {
                if (mrNote.equalsIgnoreCase("OK") || mrNote.equalsIgnoreCase("MM") || mrNote.equalsIgnoreCase("GB") || mrNote.equalsIgnoreCase("SB") || mrNote.equalsIgnoreCase("MU")) {
                    strBillBasis = "N";
                    // adjflg = 1;
                }
            }


          /*  if (strMeterCond_Prv.equalsIgnoreCase("F")) {
                if (mrNote.equalsIgnoreCase("OK") || mrNote.equalsIgnoreCase("MM") || mrNote.equalsIgnoreCase("GB") || mrNote.equalsIgnoreCase("SB") || mrNote.equalsIgnoreCase("MU")) {
                    month_cnt = mon_cntDef;
                    defOkFlg = 1;
                    adjflg = 1;
                    FromDate = PrvOKBillDate;
                    adjflg = 1;
                }
            }*/
            CntParam = 0;
            if (prvBlTyp == 5000) {
                month_cnt = movinCnt;
                FromDate = MovinDate;
                CntParam = 1;
                adjflg = 0;
            } else if (prvBlTyp == 1000 || prvBlTyp == 2000) {
                adjflg = 0;
                CntParam = 0;
                if (strBillBasis.equalsIgnoreCase("N")) {
                    FromDate = PrvOKBillDate;
                    adjflg = 1;
                    lReadPrev = LstOKRead;
                    if (MovinDate.equalsIgnoreCase(PrvOKBillDate)) {
                        CntParam = 1;
                    } else {
                        CntParam = 0;
                    }
                }
            }
            month_cnt = 0;
            MonthCnt = CalculateMonthCount(FromDate, Todate, CntParam);
            month_cnt = MonthCnt;
            int TotalDays = 0;
            TotalDays = CalNoOfDaysAvg(FromDate, Todate, CntParam);
            Log.d("DemoApp", " LstOKRead" + LstOKRead);
            Log.d("DemoApp", " MonthCntprvCnt" + MonthCntprvCnt);
            Log.d("DemoApp", " month_cnt" + month_cnt);
            Log.d("DemoApp", " MonthCntmovCnt" + MonthCntmovCnt);

            //Actual Bill - Actual, Round Complete Cases
            Log.d("DemoApp", " lReadCurr RQCff " + lReadCurr);
            Log.d("DemoApp", " lReadPrev RQCff " + lReadPrev);
            Log.d("DemoApp", " lReadPrev RQCff " + lReadMF_Kwh);
            Log.d("DemoApp", " lReadPrev RQCff adjflg" + adjflg);
            double iMeterDigits = rs.getDouble(3);
            if (iMeterDigits == 0) {
                iMeterDigits = String.valueOf(lReadPrev).length();
            }
            if (strBillBasis.equalsIgnoreCase("N")) {
                if (lReadCurr >= lReadPrev) {
                    lUnits_Kwh = (lReadCurr - lReadPrev) * lReadMF_Kwh;
                    Log.d("DemoApp", " lReadPrev xxxxxx " + lUnits_Kwh);
                }
                // Dial-Around Case
                else {
                    lUnits_Kwh = (pow(10, iMeterDigits) + lReadCurr - lReadPrev) * lReadMF_Kwh;
                    // prs_mtrcond = "R";
                    dialOver = 1;
                }
            } else {
                if (lAvgNMUnits >= 1) { //&& lAvgNMUnits < lULimit --deleted
                    lUnits_Kwh = lAvgNMUnits;
                } else {
                    lUnits_Kwh = lLFUnits;
                }
                Log.d("DemoApp", " Average ffff " + lUnits_Kwh);
                //Calulation for derivation of Average units
                lUnits_Kwh = Math.round((lUnits_Kwh / 30) * TotalDays);
                // lUnits_Kwh = Math.floor(lUnits_Kwh * month_cnt);
                avgunitblled = lUnits_Kwh;
            }
            Log.d("DemoApp", " lUnits_Kwh ffff " + lUnits_Kwh);
            Log.d("DemoApp", " TotalDays ffff " + TotalDays);
            // to handle meter change case
            if (consmpOldMtr > 0) {
                lUnits_Kwh = lUnits_Kwh + consmpOldMtr;
            }
        }
        rs.close();
        //     }

        //   else if(typeval.equalsIgnoreCase("md")){

        String strSelectSQL_02 = "SELECT B.PREVIOUS_MD,B.MF,LTRIM(B.PRESENT_METER_READING,'0'),B.METER_CONDITION,A.RCRD_LOAD FROM TBL_SPOTBILL_HEADER_DETAILS A, " +
                " TBL_SPOTBILL_CHILD_DETAILS B WHERE " +
                " A.INSTALLATION=B.INSTALLATION AND A.INSTALLATION='" + InstalationNo + "' " +
                " AND B.REGISTER_CODE ='MDKW'";
        Log.d("DemoApp", "strSelectSQL_02  " + strSelectSQL_02);
        //ResultSet rs = statement.executeQuery(strSelectSQL_01);
        Cursor rs1 = helper.getCalculateedData(strSelectSQL_02);
        while (rs1.moveToNext()) {
            MdivalPrev = rs1.getDouble(4);
            lReadMF_Mdival = rs1.getLong(1);
            MdivalCurr = rs1.getDouble(2);
            MdMtrCond = rs1.getString(3);
        }
        rs1.close();
        double dMD_Current = Math.round(lReadMF_Mdival * MdivalCurr);
        double dMD_prev = Math.round(lReadMF_Mdival * MdivalPrev);
      /*  if(mrNote.equalsIgnoreCase("NP")){
            chkUnt=750;
        }*/
        if (dMD_prev == 0) {
            dMD_prev = lConLoad;
        }
        //   }
        //RQC Logics

        if (countflg == 0) { //to check dial over cases
            if (dialOver == 1) {
                chkKwhFlg = 4;
            } else {
                if (lUnits_Kwh > 10000) {
                    chkKwhFlg = 44;
                } else if (month_cnt > 12) {
                    chkKwhFlg = 44;
                } else if ((Math.round(lAvgNMUnits * month_cnt) > 100)) {
                    if (Math.round(lUnits_Kwh) > (5 * lAvgNMUnits * month_cnt)) {
                        chkKwhFlg = 44;
                    }
                } else if (lUnits_Kwh == 0) {
                    chkKwhFlg = 44;
                } else if (dMD_prev > 0 && (dMD_Current / dMD_prev) > 4) {
                    chkKwhFlg = 44;
                }
            }
            Log.d("DemoApp", " chkKwhFlg yyyy " + chkKwhFlg);
            if (dMD_Current > (lConLoad * 5) && dMD_Current >= 10) {
                chkKwhFlg = 44;
            } else if (dMD_prev > 0 && (dMD_Current / dMD_prev) > 4) {
                chkKwhFlg = 44;
            }
            if (chkKwhFlg != 44 && chkKwhFlg != 4) {
                chkKwhFlg = 0;
            }

            Log.d("DemoApp", " lUnits_Kwh yyyy " + lUnits_Kwh);
            Log.d("DemoApp", " lConLoad yyyy " + lConLoad);
            Log.d("DemoApp", " chkKwhFlg yyyy " + chkKwhFlg);
        } else {
            if (lUnits_Kwh > 10000) {
                chkKwhFlg = 1;
            } else if (month_cnt > 12) {
                chkKwhFlg = 8;
            } else if ((Math.round(lAvgNMUnits * month_cnt) > 100)) {
                if (Math.round(lUnits_Kwh) > (5 * lAvgNMUnits * month_cnt)) {
                    chkKwhFlg = 5;
                }
            } else if (lUnits_Kwh == 0) {
                chkKwhFlg = 3;
            }
            if (chkKwhFlg == 0) {
                if (dMD_Current > (lConLoad * 5) && dMD_Current >= 10) {
                    chkKwhFlg = 2;
                } else if (dMD_prev > 0 && (dMD_Current / dMD_prev) > 4) {
                    chkKwhFlg = 21;
                }
            }

            /*else if (Math.round(lUnits_Kwh / month_cnt) > (5 * lAvgNMUnits) && (Math.round(lUnits_Kwh / month_cnt) > 1000)) {
                chkKwhFlg = 7;
            }*/
        }
        Log.d("DemoApp", " dMD_Current xxxx " + dMD_Current);
        Log.d("DemoApp", " lConLoad xxxx " + lConLoad);
        Log.d("DemoApp", " chkKwhFlg xxxx " + chkKwhFlg);


       /*else if(dialOver==1 && countflg==1){
            chkKwhFlg = 44;
        }*/

        //BQC logics


        countflg++;//to check dial over cases
        return chkKwhFlg;
    }

    private int ChkQCValNsbm(String InstalationNo, String typeval, Context context) {
        int chkKwhFlg = 0;
        long lReadMF_Mdival = 0;
        double MdivalCurr = 0;
        double MdivalPrev = 0;
        String MdMtrCond = "";
        long consmpOldMtr = 0;
        double dAmountSplRebate = 0;
        int counter = 0;
        long lReadCurr = 0;
        long lReadPrev = 0;
        long lReadMF_Kwh = 0;
        String strMeterCond = "";
        String strMeterCondmdi = "";
        String Meterrent = "";
        long LstOKRead = 0;
        String lstOkReadDate = "";
        double mon_cntDef = 0;
        long avgUntBld = 0;
        String mrNote = "";
        long defProvunit = 0;
        int defOkFlg = 0;
        long billedMd = 0;
        double dAmountED = 0;
        double dPrsentBlAmount = 0;
        double dAmountRebate = 0;
        double lUnits_Kwh = 0;
        double dAmountArrear = 0;
        String strTariff = "";
        long lConLoad = 0;
        int iBillMonths = 0;
        String strConLoadUoM = "";
        String strBillBasis = "A";
        String strMrTime = "";
        int iMeterType = 1;
        int iBilled_cnt = 0;
        double month_cnt = 0;
        String prvBlRemark = "";
        int dialOver = 0;
        DatabaseHelper helper = new DatabaseHelper(context);
        //if(typeval.equalsIgnoreCase("kwh")){
        String strSelectSQL_01 = "SELECT CASE WHEN A.RATE_CATEGORY='DOM_OTH' THEN 'DOM' WHEN A.RATE_CATEGORY='DKJ' THEN 'BPL' WHEN A.RATE_CATEGORY='LT_GENPRPS' THEN 'GPS' WHEN A.RATE_CATEGORY='LT_SPBLPRS' THEN 'SPP' ELSE 'DOM' END TARIFF " +
                " ,CASE WHEN B.MF >= 1 THEN B.MF ELSE 1 END AS MF,A.CONSUMER_OWNED,B.NO_OF_DIGITS,A.SAN_LOAD,CAST(B.PREV_MTR_READ AS LONG) as PREV_MTR_READ,A.CR_ADJ, A.DB_ADJ,A.PRV_BILLED_AMT, " +
                " A.PREVIOUS_BILLED_PROV_UNIT,A.AVERAGE_KWH,A.ED_EXEMPT,CAST(B.PRESENT_METER_READING AS LONG) as PRESENT_METER_READING,B.METER_CONDITION P_STATUS,B.METER_CONDITION,B.METER_TYP,A.HL_MONTHS NO_OF_MON " +
                " ,B.PREVIOUS_MD,A.HOSTEL_RBT,A.MOVE_IN_DATE, PREV_READ_DATE " +
                " ,(strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', B.PREV_READ_DATE)*12 + strftime('%m', B.PREV_READ_DATE)) NEWCONN_MONTHS " +
                " ,A.AVERAGE_KWH,A.PREV_BILL_UNITS,A.PRESENT_BILL_TYPE,B.METER_CONDITION MTR_COND_PRV,A.AVERAGE_KWH,A.DPS,A.MISC_CHARGES,A.PRV_ARR,A.ARREARS,A.AIFI " +
                " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', B.PREV_READ_DATE)*12 + strftime('%m', B.PREV_READ_DATE))-1 +((strftime('%d',DATE(B.PREV_READ_DATE,'start of month','+1 month','-1 day'))-strftime('%d',B.PREV_READ_DATE)*1.0))/strftime('%d',DATE(B.PREV_READ_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) MONTHS_CNT  " +
                " ,B.REGISTER_CODE,A.METER_RENT,A.ED_RBT,A.ULF,A.MRREASON,B.CONSUMPTION_OLD_METER,LTRIM(B.LAST_OK_RDNG,'0'),A.LAST_NORMAL_BILL_DATE  " +
                " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', A.LAST_NORMAL_BILL_DATE)*12 + strftime('%m', A.LAST_NORMAL_BILL_DATE))-1 +((strftime('%d',DATE(A.LAST_NORMAL_BILL_DATE,'start of month','+1 month','-1 day'))-strftime('%d',A.LAST_NORMAL_BILL_DATE)*1.0)+1)/strftime('%d',DATE(A.LAST_NORMAL_BILL_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) DFMON_CNT  " +
                ",A.AVG_UNIT_BILLED,A.PREV_BILL_REMARK,A.PREV_BILL_TYPE " +//44
                " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', A.MOVE_IN_DATE)*12 + strftime('%m', A.MOVE_IN_DATE))-1 +((strftime('%d',DATE(A.MOVE_IN_DATE,'start of month','+1 month','-1 day'))-strftime('%d',A.MOVE_IN_DATE)*1.0)+1)/strftime('%d',DATE(A.MOVE_IN_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) movin_CNT1  " +
                "  ,A.LAST_NORMAL_BILL_DATE,DATE('now', 'localtime'),A.PREV_PROV_AMT,A.FC_SLAB,A.PROV_ED,A.PROV_PPT_AMT  " +
                "  FROM TBL_SPOTBILL_HEADER_DETAILS A,TBL_SPOTBILL_CHILD_DETAILS B " +
                "  WHERE A.INSTALLATION=B.INSTALLATION AND A.INSTALLATION='" + InstalationNo + "' " +
                "  AND B.REGISTER_CODE ='CKWH' " +
                "  ORDER BY B.REGISTER_CODE";

        Log.d("DemoApp", "strUpdateSQL_01  " + strSelectSQL_01);
        Cursor rs = helper.getCalculateedData(strSelectSQL_01);


        int prvBlTyp = 0;
        int fcSlabCnt = 1;
        double avgunitblled = 0;
        int adjflg = 0;
        double movinCnt = 0;

        double MonthCnt = 0;
        String MovinDate = "";
        // String PrvReadDate="";
        String PrvOKBillDate = "";
        String CurrentDate = "";
        String FromDate = "";
        String Todate = "";
        int CntParam = 0;
        double MonthCntprvCnt = 0;
        double MonthCntmovCnt = 0;
        long lAvgNMUnits = 0;
        while (rs.moveToNext()) {
            MovinDate = rs.getString(19);
            //PrvReadDate=rs.getString(20);
            PrvOKBillDate = rs.getString(46);
            CurrentDate = rs.getString(47);
            double provisional_amt = rs.getDouble(48);
            double prvisional_Ed = rs.getDouble(50);
            double provAdjRebate = rs.getDouble(51);
            Log.d("DemoApp", " found ");
            consmpOldMtr = rs.getInt(38);
            LstOKRead = rs.getLong(39);
            lstOkReadDate = rs.getString(40);
            mon_cntDef = rs.getDouble(41);
            // avgUntBld=rs.getLong(42);
            prvBlRemark = rs.getString(43);
            prvBlTyp = rs.getInt(44);
            movinCnt = rs.getDouble(45);
            Meterrent = rs.getString(34);
            String Regcode = rs.getString(33);
            int iMeterChargeFlag = 1;
            strTariff = rs.getString(0);
            double dConLoad = rs.getDouble(4);
            lConLoad = (long) Math.ceil(dConLoad);
            if (lConLoad < 1) lConLoad = 1;
            //   if (Regcode.equalsIgnoreCase("CKWH")) {
            lReadMF_Kwh = rs.getLong(1);
            lReadPrev = rs.getLong(5);
            lReadCurr = rs.getLong(12);
            strMeterCond = rs.getString(14);//not used
            //   }
            Log.d("DemoApp", "not lReadCurr " + lReadCurr);
            // String   lReadPrevrrrrr = rs.getString(5);


            Log.d("DemoApp", "not lReadPrev tyrtyr " + lReadPrev);
            Log.d("DemoApp", "not MdivalPrev " + MdivalPrev);
            Log.d("DemoApp", "not MdivalCurr " + MdivalCurr);


            Log.d("DemoApp", "not iBillMonths 1   " + iBillMonths);
            Log.d("DemoApp", "not iBillMonths   " + iBillMonths);
            Log.d("DemoApp", "not fcSlabCnt   " + fcSlabCnt);
            //lConLoad = (long) Math.max(dMD_Current, dMD_prev);


            int iSplReb_Flag = rs.getInt(18);//hostel rebate
            Log.d("DemoApp", " found iSplReb_Flag" + iSplReb_Flag);
            String strDate_Ind_Conn = rs.getString(19);
            String strDate_Ind_LastMR = rs.getString(20);
            int iNewConnMonths = rs.getInt(21);
            // long lAvgUnits = rs.getLong(22); // not required
            //Log.d("DemoApp", "lAvgUnits 1" + lAvgUnits);
            long lLRCUnits = rs.getLong(23);
            String strMeterCond_Prv = rs.getString(25);   ////
            lAvgNMUnits = rs.getLong(26);
            lAvgNMUnits = Math.abs(lAvgNMUnits);
            Log.d("DemoApp", "lAvgNMUnits 1" + lAvgNMUnits);
            double dps = rs.getDouble(27);
            double misc_chg = rs.getDouble(28);
            // double provisional_amt = rs.getDouble(8);
            int Hlunits = rs.getInt(9);
            defProvunit = Hlunits;
            double prvdps = rs.getDouble(29);
            double prsyrarr = rs.getDouble(30);
            int aifi = rs.getInt(31);//added on 29.03.2017
            mrNote = rs.getString(37);
            long lLFUnits = rs.getInt(36);
            month_cnt = rs.getDouble(32);//sap


            //handle bill basis

            try {
                if (strMeterCond_Prv.equalsIgnoreCase("O")) {
                    if (mrNote.equalsIgnoreCase("OK") || mrNote.equalsIgnoreCase("MM") || mrNote.equalsIgnoreCase("GB") || mrNote.equalsIgnoreCase("SB") || mrNote.equalsIgnoreCase("MU")) {
                        strBillBasis = "N";
                        // adjflg = 1;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                strBillBasis = "Z";
            }




            /*CntParam = 0;
            if (prvBlTyp == 5000) {
                month_cnt = movinCnt;
                FromDate = MovinDate;
                CntParam = 1;
                adjflg = 0;
            } else if (prvBlTyp == 1000 || prvBlTyp == 2000) {
                adjflg = 0;
                CntParam = 0;
                if (strBillBasis.equalsIgnoreCase("N")) {
                    FromDate = PrvOKBillDate;
                    adjflg = 1;
                    lReadPrev = LstOKRead;
                    if (MovinDate.equalsIgnoreCase(PrvOKBillDate)) {
                        CntParam = 1;
                    } else {
                        CntParam = 0;
                    }
                }
            }
            month_cnt = 0;
            MonthCnt = CalculateMonthCount(FromDate, Todate, CntParam);
            month_cnt = MonthCnt;
            int TotalDays = 0;
            TotalDays = CalNoOfDaysAvg(FromDate, Todate, CntParam);
            Log.d("DemoApp", " LstOKRead" + LstOKRead);
            Log.d("DemoApp", " MonthCntprvCnt" + MonthCntprvCnt);
            Log.d("DemoApp", " month_cnt" + month_cnt);
            Log.d("DemoApp", " MonthCntmovCnt" + MonthCntmovCnt);

            //Actual Bill - Actual, Round Complete Cases
            Log.d("DemoApp", " lReadCurr RQCff " + lReadCurr);
            Log.d("DemoApp", " lReadPrev RQCff " + lReadPrev);
            Log.d("DemoApp", " lReadPrev RQCff " + lReadMF_Kwh);
            Log.d("DemoApp", " lReadPrev RQCff adjflg" + adjflg);*/
            double iMeterDigits = rs.getDouble(3);
            if (iMeterDigits == 0) {
                iMeterDigits = String.valueOf(lReadPrev).length();
            }
            if (strBillBasis.equalsIgnoreCase("N")) {
                if (lReadCurr >= lReadPrev) {
                    lUnits_Kwh = (lReadCurr - lReadPrev) * lReadMF_Kwh;
                    Log.d("DemoApp", " lReadPrev xxxxxx " + lUnits_Kwh);
                }
                // Dial-Around Case
                else {
                    lUnits_Kwh = (pow(10, iMeterDigits) + lReadCurr - lReadPrev) * lReadMF_Kwh;
                    // prs_mtrcond = "R";
                    dialOver = 1;
                }
            } else {
                if (lAvgNMUnits >= 1) { //&& lAvgNMUnits < lULimit --deleted
                    lUnits_Kwh = lAvgNMUnits;
                } else {
                    lUnits_Kwh = lLFUnits;
                }
                Log.d("DemoApp", " Average ffff " + lUnits_Kwh);
                //Calulation for derivation of Average units
                //lUnits_Kwh = Math.round((lUnits_Kwh / 30) * TotalDays);
                // lUnits_Kwh = Math.floor(lUnits_Kwh * month_cnt);
                avgunitblled = lUnits_Kwh;
            }
            Log.d("DemoApp", " lUnits_Kwh ffff " + lUnits_Kwh);
            //  Log.d("DemoApp", " TotalDays ffff " + TotalDays);
            // to handle meter change case
            if (consmpOldMtr > 0) {
                lUnits_Kwh = lUnits_Kwh + consmpOldMtr;
            }
        }
        rs.close();
        //     }

        //   else if(typeval.equalsIgnoreCase("md")){

        String strSelectSQL_02 = "SELECT B.PREVIOUS_MD,B.MF,LTRIM(B.PRESENT_METER_READING,'0'),B.METER_CONDITION,A.RCRD_LOAD FROM TBL_SPOTBILL_HEADER_DETAILS A, " +
                " TBL_SPOTBILL_CHILD_DETAILS B WHERE " +
                " A.INSTALLATION=B.INSTALLATION AND A.INSTALLATION='" + InstalationNo + "' " +
                " AND B.REGISTER_CODE ='MDKW'";
        Log.d("DemoApp", "strSelectSQL_02  " + strSelectSQL_02);
        //ResultSet rs = statement.executeQuery(strSelectSQL_01);
        Cursor rs1 = helper.getCalculateedData(strSelectSQL_02);
        while (rs1.moveToNext()) {
            MdivalPrev = rs1.getDouble(4);
            lReadMF_Mdival = rs1.getLong(1);
            MdivalCurr = rs1.getDouble(2);
            MdMtrCond = rs1.getString(3);
        }
        rs1.close();
        double dMD_Current = Math.round(lReadMF_Mdival * MdivalCurr);
        double dMD_prev = Math.round(lReadMF_Mdival * MdivalPrev);
      /*  if(mrNote.equalsIgnoreCase("NP")){
            chkUnt=750;
        }*/
        if (dMD_prev == 0) {
            dMD_prev = lConLoad;
        }
        //   }
        //RQC Logics

        if (countflg == 0) { //to check dial over cases
            if (dialOver == 1) {
                chkKwhFlg = 4;
            } else {
                if (lUnits_Kwh > 50000) {
                    chkKwhFlg = 44;
                } else if (lUnits_Kwh == 0) {
                    chkKwhFlg = 44;
                }
            }
            Log.d("DemoApp", " chkKwhFlg yyyy " + chkKwhFlg);
            if (dMD_Current > (lConLoad * 5) && dMD_Current >= 10) {
                chkKwhFlg = 44;
            } else if (dMD_prev > 0 && (dMD_Current / dMD_prev) > 4) {
                chkKwhFlg = 44;
            }
            if (chkKwhFlg != 44 && chkKwhFlg != 4) {
                chkKwhFlg = 0;
            }

            Log.d("DemoApp", " lUnits_Kwh yyyy " + lUnits_Kwh);
            Log.d("DemoApp", " lConLoad yyyy " + lConLoad);
            Log.d("DemoApp", " chkKwhFlg yyyy " + chkKwhFlg);
        }

        //BQC logics


        countflg++;//to check dial over cases
        return chkKwhFlg;
    }

    private double CalculateMonthCount(String fromDT, String ToDt, int addDays) {
        double monthcnt = 0;
        double fstMonbldays = 0;
        double fstMontotdays = 0;
        double lstMonbldays = 0;
        double lstMontotdays = 0;
        int betweenMonth = 0;
        DatabaseHelper helper = new DatabaseHelper(context);
        String strSelectSQLMonCnt = "SELECT " +
                "  (strftime('%Y','" + ToDt + "')*12 + strftime('%m','" + ToDt + "'))-(strftime('%Y', '" + fromDT + "')*12 + strftime('%m', '" + fromDT + "'))-1 betmon  " +
                " ,(strftime('%d',DATE('" + fromDT + "','start of month','+1 month','-1 day'))-strftime('%d','" + fromDT + "')*1.0)/strftime('%d',DATE('" + fromDT + "','start of month','+1 month','-1 day')) first_mon  " +
                " ,(strftime('%d','" + ToDt + "')*1.0)/strftime('%d',DATE('" + ToDt + "','start of month','+1 month','-1 day')) lastmon  " +
                " ,(strftime('%d','" + ToDt + "')*1) lstmonbldays  " +
                " ,strftime('%d',DATE('" + ToDt + "','start of month','+1 month','-1 day')) lstmontotdays  " +
                " ,(strftime('%d',DATE('" + fromDT + "','start of month','+1 month','-1 day'))-strftime('%d','" + fromDT + "'))*1 fstmonbldays  " +
                " ,strftime('%d',DATE('" + fromDT + "','start of month','+1 month','-1 day')) fstmontotdays ";

        Log.d("DemoApp", "strSelectSQL_03 " + strSelectSQLMonCnt);
        Cursor rs = helper.getCalculateedData(strSelectSQLMonCnt);
        while (rs.moveToNext()) {
            fstMonbldays = rs.getDouble(5);
            fstMontotdays = rs.getDouble(6);
            lstMonbldays = rs.getDouble(3);
            lstMontotdays = rs.getDouble(4);
            betweenMonth = rs.getInt(0);
        }
        rs.close();
        monthcnt = ((fstMonbldays + addDays) / fstMontotdays) + betweenMonth + (lstMonbldays / lstMontotdays);
        monthcnt = Math.round(monthcnt * 10000.0) / 10000.0;
        return monthcnt;
    }

    public int CalNoOfDaysAvg(String FromDt, String ToDt, int Adddays) {
        int totdays = 0;
        DatabaseHelper helper = new DatabaseHelper(context);
        String strSelectSQL_03 = "Select Cast (( " +
                " JulianDay('" + ToDt + "') - JulianDay('" + FromDt + "') " +
                " ) As Integer) ";
        Log.d("DemoApp", "strSelectSQL_03 totdays" + strSelectSQL_03);
        Cursor rs = helper.getCalculateedData(strSelectSQL_03);
        while (rs.moveToNext()) {
            totdays = rs.getInt(0);
        }
        totdays = totdays;
        rs.close();
        return totdays;
    }


    private void getCoordinates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        try {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        } else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        } else {
            latitude = 0.0;
            longitude = 0.0;
        }

        meterOkNonSbmReadingModel.setGpsLatitude(String.valueOf(latitude));
        meterOkNonSbmReadingModel.setGpsLongitude(String.valueOf(longitude));

        Log.e("latitude", "" + meterOkNonSbmReadingModel.getGpsLatitude());
        Log.e("longitude", "" + meterOkNonSbmReadingModel.getGpsLongitude());
    }


}
