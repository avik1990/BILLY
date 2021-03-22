package com.tpcodl.billingreading.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.database.DatabaseKeys;
import com.tpcodl.billingreading.models.NSBMData;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.reponseModel.UploadDataResponseModel;
import com.tpcodl.billingreading.uploadingrequestModel.ChildReqDetails;
import com.tpcodl.billingreading.uploadingrequestModel.HeaderresDetails;
import com.tpcodl.billingreading.uploadingrequestModel.RequestModel;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.Constant;
import com.tpcodl.billingreading.utils.DialogUtils;
import com.tpcodl.billingreading.utils.UploadUtils;
import com.tpcodl.billingreading.utils.UtilsClass;
import com.tpcodl.billingreading.webservice.ApiInterface;
import com.tpcodl.billingreading.webservice.RetrofitClientInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Math.pow;

public class MassbillActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private ImageView iv_back;
    private TextView tv_title;
    //private Button btn_upload_server;
    private Button btn_upload_local, btn_generate_mass_bill;
    private DialogUtils dUtils;
    private ActivityUtils utils;
    private DialogUtils dutils;
    private RequestModel model;
    private DatabaseHelper mDBHelper;
    private List<HeaderresDetails> hlist;
    private List<String> installationList = new ArrayList<>();
    String from = "";
    List<NSBMData> list = new ArrayList<>();
    UploadUtils uUtils;
    private Button startBtn;
    private ProgressDialog progressDialog1;
    String varCond = "";
    DatabaseHelper db;
    String FLAG = "0";
    private ProgressDialog progressDialogCalculate;
    int countflg = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massbill_upload);
        mContext = this;
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);

        tv_title.setText(mContext.getResources().getString(R.string.upload_activity));
        // btn_upload_server = findViewById(R.id.btn_upload_server);
        btn_generate_mass_bill = findViewById(R.id.btn_generate_mass_bill);
        btn_generate_mass_bill.setOnClickListener(this);
        uUtils = new UploadUtils(mContext);
        //intent.putExtra("FROM", "NONSBM");
        db = new DatabaseHelper(mContext);
        from = getIntent().getStringExtra("FROM");

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

        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                onBackPressed();
                break;
            }
            case R.id.btn_generate_mass_bill: {
                FLAG = "2";
                list.clear();
                String query = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET READ_FLAG='0'";
                db.updateSlab(query);
                list = mDBHelper.getNBSMRInstallationOnly(FLAG);
                if (list.size() > 0) {
                    new AsyncTaskMassUpload().execute();
                } else {
                    UtilsClass.showToastShort(mContext, "Data is not proper!!");
                }
                break;
            }
        }
    }

    private class AsyncTaskMassUpload extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogCalculate = new ProgressDialog(mContext);
            progressDialogCalculate.setCancelable(false);
            progressDialogCalculate.setMessage("Calculating Bill!!");
            progressDialogCalculate.setCanceledOnTouchOutside(false);
            progressDialogCalculate.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        publishProgress((int) ((i * 100) / list.size()));
                        Log.e("listSize", i + " : " + list.size());
                        int QcCheckKwh = 0;
                        int QcOverride = 0;
                        //  int QcCheckMd=0;
                        try {
                            if (!list.get(i).getMRREASON().isEmpty() && list.get(i).getMRREASON() != null) {
                                QcCheckKwh = ChkQCVal(list.get(i).getINSTALLATION(), "kwh", mContext);
                                calculateBill.CalculateBill(list.get(i).getINSTALLATION(), list.get(i).getMRREASON(), mContext, QcCheckKwh);
                            }
                        } catch (Exception e) {
                        }
                        //calculateBill.CalculateBill(list.get(i).getINSTALLATION(), "OK", mContext, 0);
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            UtilsClass.showToastShort(mContext, "No Data to Upload");
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialogCalculate.dismiss();

            if (AppUtils.isInternetAvailable(mContext)) {
                new AsyncUploadSingleTaskExample().execute();
            } else {
                UtilsClass.showToastLong(mContext, "No Internet Connection!!");
            }
        }
    }


    private class AsyncUploadSingleTaskExample extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog1 = new ProgressDialog(mContext);
            progressDialog1.setCancelable(false);
            progressDialog1.setMessage("Uploading data, please wait...");
            progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog1.setProgress(0);
            progressDialog1.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //System.out.println("getting list size::" + list.size());
            progressDialog1.setMax(list.size());
            try {
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        varCond = " where INSTALLATION='" + list.get(i).getINSTALLATION() + "'";
                        Log.e("UploadCount", list.get(i).getINSTALLATION() + " " + i);
                        //Log.e("VARCONTD", varCond);
                        utils.setSerchCondition(varCond);
                        uUtils.getHeaderdetails(varCond);
                        model = uUtils.getChilddetails(varCond);
                        uploadData(varCond);
                        publishProgress(i);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog1.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog1.dismiss();
            UtilsClass.showToastLong(mContext, "Records Uploaded Successfully");
        }
    }


    public void uploadData(String condition) {
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<UploadDataResponseModel> call = service.uploadData(utils.getAuthToken(), model);
        call.enqueue(new Callback<UploadDataResponseModel>() {
            @Override
            public void onResponse(Call<UploadDataResponseModel> call, Response<UploadDataResponseModel> response) {
                UploadDataResponseModel object = response.body();
                try {

                    if (object.statusCode == 200) {
                        db.updatesendFlag1(condition);
                        try {
                            utils.setAppVersion(object.softwareVersionNo.toString().trim());
                        } catch (Exception e) {

                        }
                    } else if (object.statusCode == 410) {
                        db.updatesendFlag1(condition);
                        try {
                            utils.setAppVersion(object.softwareVersionNo.toString().trim());
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<UploadDataResponseModel> call, Throwable t) {
                t.printStackTrace();
                Log.i("TAG", t.getMessage());
                Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }

        });

    }


   /* private int ChkQCVal(String InstalationNo, String typeval, Context context) {
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
              *//*  if(prvBlRemark.equalsIgnoreCase("OK")||prvBlRemark.equalsIgnoreCase("MM")||prvBlRemark.equalsIgnoreCase("GB")||prvBlRemark.equalsIgnoreCase("SB")||prvBlRemark.equalsIgnoreCase("MU")){
                    strMeterCond_Prv="O";
                }else if(prvBlRemark.equalsIgnoreCase("NV")||prvBlRemark.equalsIgnoreCase("MB")||prvBlRemark.equalsIgnoreCase("FU")||prvBlRemark.equalsIgnoreCase("ND")){
                    strMeterCond_Prv="F";
                }else{
                    strMeterCond_Prv="O";
                }*//*
            //to handle Faulty meter to OK
            FromDate = strDate_Ind_LastMR;
            Todate = CurrentDate;


            //handle bill basis
            try {
                if (strMeterCond_Prv.equalsIgnoreCase("O")) {
                    if (mrNote.equalsIgnoreCase("OK") || mrNote.equalsIgnoreCase("MM") || mrNote.equalsIgnoreCase("GB") || mrNote.equalsIgnoreCase("SB") || mrNote.equalsIgnoreCase("MU")) {
                        strBillBasis = "N";
                        // adjflg = 1;
                    }
                }
            } catch (Exception e) {

            }


          *//*  if (strMeterCond_Prv.equalsIgnoreCase("F")) {
                if (mrNote.equalsIgnoreCase("OK") || mrNote.equalsIgnoreCase("MM") || mrNote.equalsIgnoreCase("GB") || mrNote.equalsIgnoreCase("SB") || mrNote.equalsIgnoreCase("MU")) {
                    month_cnt = mon_cntDef;
                    defOkFlg = 1;
                    adjflg = 1;
                    FromDate = PrvOKBillDate;
                    adjflg = 1;
                }
            }*//*
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
      *//*  if(mrNote.equalsIgnoreCase("NP")){
            chkUnt=750;
        }*//*
        if (dMD_prev == 0) {
            dMD_prev = lConLoad;
        }
        //   }
        //RQC Logics

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
            *//*else if (Math.round(lUnits_Kwh / month_cnt) > (5 * lAvgNMUnits) && (Math.round(lUnits_Kwh / month_cnt) > 1000)) {
                chkKwhFlg = 7;
            }*//*



     *//*else if(dialOver==1 && countflg==1){
            chkKwhFlg = 44;
        }*//*

        //BQC logics


        //countflg++;//to check dial over cases
        return chkKwhFlg;
    }*/

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
            try {
                if (strMeterCond_Prv.equalsIgnoreCase("O")) {
                    if (mrNote.equalsIgnoreCase("OK") || mrNote.equalsIgnoreCase("MM") || mrNote.equalsIgnoreCase("GB") || mrNote.equalsIgnoreCase("SB") || mrNote.equalsIgnoreCase("MU")) {
                        strBillBasis = "N";
                        // adjflg = 1;
                    }
                }
            } catch (Exception e) {

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

    private double CalculateMonthCount(String fromDT, String ToDt, int addDays) {
        double monthcnt = 0;
        double fstMonbldays = 0;
        double fstMontotdays = 0;
        double lstMonbldays = 0;
        double lstMontotdays = 0;
        int betweenMonth = 0;
        DatabaseHelper helper = new DatabaseHelper(mContext);
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
        DatabaseHelper helper = new DatabaseHelper(mContext);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}