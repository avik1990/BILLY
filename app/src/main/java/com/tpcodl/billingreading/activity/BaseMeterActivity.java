package com.tpcodl.billingreading.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.remark.FUCDENRemarkActivity;
import com.tpcodl.billingreading.activity.remark.MeterOKMMRemarkActivity;
import com.tpcodl.billingreading.activity.remark.MeterOkNonSbmReading;
import com.tpcodl.billingreading.activity.remark.NDPPGBMHOBMUSBRemarksActivity;
import com.tpcodl.billingreading.activity.remark.NMNVMBRemarkActivity;
import com.tpcodl.billingreading.activity.remark.NPDCRemarkActivity;
import com.tpcodl.billingreading.activity.remark.PLTLWLSPNABLRemarksActivity;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseMeterActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener {

    private TextView tv_title, tv_update_contact_no;
    private ImageView iv_back;
    private Spinner spinner;
    private Button btn_submit;
    private String mrNotes;
    private ActivityUtils utils;
    Context context;
    MeterOkNonSbmReadingModel meterOkNonSbmReadingModel;
    DatabaseHelper db;
    NSBMData nbsmDatamodel;
    UploadUtils uUtils;
    ///
    TextView tv_installation_no;
    TextView tv_ca_no;
    TextView tv_building_dec;
    TextView tv_building_code_value;
    TextView tv_pole_number;
    TextView tv_flag_value;
    TextView tv_meter_number;
    TextView tv_mru;
    TextView tv_name_add;
    TextView tv_remark;
    TextView tv_contact_no;

    String SelChoice = "";
    String SelAcntno = "";
    String varCond = "";
    ToggleButton chk_base_type;
    TextView btn_np, btn_pl;
    String isRevisit = "0";
    String mrNoteAbbName = "";
    int offset = -1;
    TextView btnNext, btnPrev;
    private RequestModel model;
    private DialogUtils dUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_meter);
        context = this;
        utils = ActivityUtils.getInstance();
        dUtils = new DialogUtils(context);
        SelAcntno = getIntent().getStringExtra(Constant.DETAILS_NO);
        SelChoice = getIntent().getStringExtra(Constant.SEL_CHOICE);
        db = new DatabaseHelper(this);
        meterOkNonSbmReadingModel = new MeterOkNonSbmReadingModel();
        initView();

        if (!SelChoice.equalsIgnoreCase("SeqNo")) {
            btnNext.setVisibility(View.GONE);
            btnPrev.setVisibility(View.GONE);
            if (SelChoice.equals("consno")) {
                varCond = " where  CA='" + SelAcntno + "'";
            } else if (SelChoice.equals("MtrNo")) {
                varCond = " where METER_NO like  '%" + SelAcntno + "%'";
            } else if (SelChoice.equals("Phone")) {
                varCond = " where PHONE_1='" + SelAcntno + "'";
            } else if (SelChoice.equals("InstNo")) {
                varCond = " where INSTALLATION='" + SelAcntno + "'";
            } else if (SelChoice.equals("Legacy")) {
                if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                    varCond = " where substr(LEGACY_ACCOUNT_NO2,5)='" + SelAcntno + "'";
                } else {
                    varCond = " where LEGACY_ACCOUNT_NO=Upper('" + SelAcntno + "')";

                }
            } else {
                varCond = " where LEGACY_ACCOUNT_NO=UPPER('" + SelAcntno + "')";
            }
            utils.setSerchCondition(varCond);
        } else {
            offset = 0;
            btnNext.setVisibility(View.VISIBLE);
            btnPrev.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncTaskExample().execute();
    }

    private NSBMData fetchNSBMdata() {
        nbsmDatamodel = db.getNBSMData(varCond, offset);
        return nbsmDatamodel;
    }

    private void initView() {
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        tv_installation_no = findViewById(R.id.tv_installation_no);
        tv_ca_no = findViewById(R.id.tv_ca_no);
        tv_building_dec = findViewById(R.id.tv_building_dec);
        tv_building_code_value = findViewById(R.id.tv_building_code_value);
        tv_pole_number = findViewById(R.id.tv_pole_number);
        tv_flag_value = findViewById(R.id.tv_flag_value);
        tv_meter_number = findViewById(R.id.tv_meter_number);
        tv_mru = findViewById(R.id.tv_mru);
        tv_name_add = findViewById(R.id.tv_name_add);
        tv_remark = findViewById(R.id.tv_remark);
        tv_contact_no = findViewById(R.id.tv_contact_no);
        btn_np = findViewById(R.id.btn_np);
        btn_pl = findViewById(R.id.btn_pl);
        chk_base_type = findViewById(R.id.chk_base_type);

        spinner = findViewById(R.id.spinner);
        btn_submit = findViewById(R.id.btn_submit);
        tv_title = findViewById(R.id.tv_title);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_title.setText(getResources().getString(R.string.biling_details));
        spinner.setOnItemSelectedListener(this);
        tv_update_contact_no = findViewById(R.id.tv_update_contact_no);
        tv_update_contact_no.setOnClickListener(this);
        btn_np.setOnClickListener(this);
        btn_pl.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterStatus));
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        btn_submit.setOnClickListener(view -> {

            if (checkDateTime()) {
                if (db.checkBillingAllowedFlag(nbsmDatamodel.getINSTALLATION(), nbsmDatamodel.getREF_MR_DATE(), nbsmDatamodel.getSCHEDULE_METER_READ_DATE()) == 1) {
                    CallMrActivity(mrNotes);
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Your bill date is over for this installation.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });


        chk_base_type.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isRevisit = "1";
                chk_base_type.setTextColor(Color.parseColor("#ffffff"));
            } else {
                isRevisit = "0";
                chk_base_type.setTextColor(Color.parseColor("#000000"));
            }
        });
    }

    private boolean checkDateTime() {
        boolean flag = false;
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date currentDate = sdf.parse(DateFormat.format("dd-MM-yyyy", date.getTime()).toString());
            Date serverDate = sdf.parse(utils.getServreDate());
            Log.e("currentDate", currentDate + "----" + serverDate);

            if (currentDate.compareTo(serverDate) > 0) {
                flag = false;
                Toast.makeText(this, "Device Date is Incorrect. Please Check...", Toast.LENGTH_SHORT).show();
            } else if (currentDate.compareTo(serverDate) < 0) {
                flag = false;
                Toast.makeText(this, "Device Date is Incorrect. Please Check...", Toast.LENGTH_SHORT).show();
            } else if (currentDate.compareTo(serverDate) == 0) {
                flag = true;
            }
        } catch (Exception e) {
            flag = true;
        }

        return flag;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        mrNotes = getResources().getStringArray(R.array.meterStatus)[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }
            case R.id.tv_update_contact_no: {
                showDialog();
                break;
            }
            case R.id.btn_np: {
                Intent intent = new Intent(this, NPDCRemarkActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "NP");
                startActivity(intent);
                finish();
                break;
            }
            case R.id.btn_pl: {
                Intent intent = new Intent(this, PLTLWLSPNABLRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra("meter_type", "PL");
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                startActivity(intent);
                finish();
                break;
            }

            case R.id.btnNext: {
                offset++;
                //Log.e("offset", "" + offset);
                new AsyncTaskExample().execute();
                break;
            }

            case R.id.btnPrev: {
                offset--;
                if (offset == -1) {
                    UtilsClass.showToastShort(context, "No Previous Data!!");
                    return;
                }
                new AsyncTaskExample().execute();
            }
        }
    }

    private void CallMrActivity(String mrNotes) {
        switch (mrNotes) {
            case "OK-Normal Readings": {
                Intent intent = new Intent(this, MeterOkNonSbmReading.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.METER_TYPE, "OK");
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                startActivity(intent);
                finish();
                break;
            }
            case "MM-Meter Number Mismatch": {
                Intent intent = new Intent(this, MeterOKMMRemarkActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "MM");
                startActivity(intent);
                finish();
                break;
            }
            case "NP-NO Power Cases": {
                Intent intent = new Intent(this, NPDCRemarkActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "NP");
                startActivity(intent);
                finish();
                break;
            }

            case "DC-Disconnected Cases": {
                Intent intent = new Intent(this, NPDCRemarkActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "DC");
                startActivity(intent);
                finish();
                break;
            }
            case "NM-NO Meter": {
                Intent intent = new Intent(this, NMNVMBRemarkActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "NM");
                startActivity(intent);
                finish();
                break;
            }
            case "HL-House Locked": {
                /*Intent intent = new Intent(this, PLTLWLSPNABLRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "PL");
                startActivity(intent);
                finish();
                break;*/
                Intent intent = new Intent(this, PLTLWLSPNABLRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "PL");
                startActivity(intent);
                finish();
                break;
            }
            case "TL-Temporary Locked": {
                Intent intent = new Intent(this, PLTLWLSPNABLRemarksActivity.class);
                intent.putExtra("meter_type", "TL");
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                startActivity(intent);
                finish();
                break;
            }
            case "BL-Meter Box/Cabin Locked": {
                Intent intent = new Intent(this, PLTLWLSPNABLRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "BL");
                startActivity(intent);
                finish();
                break;
            }
            case "NV-Reading Not Visible/Clear": {
                Intent intent = new Intent(this, NMNVMBRemarkActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra("meter_type", "NV");
                startActivity(intent);
                finish();
                break;
            }

            case "MB-Meter Burnt": {
                Intent intent = new Intent(this, NMNVMBRemarkActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "MB");
                startActivity(intent);
                finish();
                break;
            }
            case "FU-Figure Upset": {
                Intent intent = new Intent(this, FUCDENRemarkActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "FU");
                startActivity(intent);
                finish();
                break;
            }
            case "EN-Entry Not Allowed": {
                Intent intent = new Intent(this, FUCDENRemarkActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "EN");
                startActivity(intent);
                finish();
                break;
            }
            case "ND-No Display": {
                Intent intent = new Intent(this, NDPPGBMHOBMUSBRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra("meter_type", "ND");
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                startActivity(intent);
                finish();
                break;
            }
            case "PP-Paper Pasted": {
                Intent intent = new Intent(this, NDPPGBMHOBMUSBRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "PP");
                startActivity(intent);
                finish();
                break;
            }

            case "GB-Glass Broken": {
                Intent intent = new Intent(this, NDPPGBMHOBMUSBRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra("meter_type", "GB");
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                startActivity(intent);
                finish();
                break;
            }
            case "MH-Meter Position Height/Low": {
                Intent intent = new Intent(this, NDPPGBMHOBMUSBRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra("meter_type", "MH");
                startActivity(intent);
                finish();
                break;
            }
            case "NA-Address Not Available": {
                new AsyncTaskExampleUpdate().execute();
                break;
            }
            case "OB-Obstacle For Meter Reading": {
                Intent intent = new Intent(this, NDPPGBMHOBMUSBRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "OB");
                startActivity(intent);
                finish();
                break;
            }
            case "SP-Sealed Premises": {
                Intent intent = new Intent(this, PLTLWLSPNABLRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra("meter_type", "SP");
                startActivity(intent);
                finish();
                break;
            }
            case "WL-Water Logging": {
                Intent intent = new Intent(this, PLTLWLSPNABLRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                intent.putExtra("meter_type", "WL");
                startActivity(intent);
                finish();
                break;
            }
            case "SB-Seal Broken": {
                Intent intent = new Intent(this, NDPPGBMHOBMUSBRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra("meter_type", "SB");
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                startActivity(intent);
                finish();
                break;
            }
            case "MU- Meter Unsafe": {
                Intent intent = new Intent(this, NDPPGBMHOBMUSBRemarksActivity.class);
                intent.putExtra(Constant.DETAILS_NO, SelAcntno);
                intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                intent.putExtra(Constant.IS_REVISIT, isRevisit);
                intent.putExtra("meter_type", "MU");
                intent.putExtra(Constant.INSTALLATIONNO, nbsmDatamodel.getINSTALLATION());
                startActivity(intent);
                finish();
            }
        }
    }

    private void showDialog() {
        AlertDialog builder = new AlertDialog.Builder(this).create();
        final View customLayout = getLayoutInflater().inflate(R.layout.update_contact_details, null);
        builder.setView(customLayout);
        //EditText oldMobileno = customLayout.findViewById(R.id.old_mobile_no);
        EditText newMobileno = customLayout.findViewById(R.id.new_mobile_no);
        Button btnCancel = customLayout.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> builder.dismiss());
        Button submit = customLayout.findViewById(R.id.submit);

        submit.setOnClickListener(v -> {
            //String old_mo = oldMobileno.getText().toString();
            String new_no = newMobileno.getText().toString();
            /*if (old_mo.length() == 0 || old_mo.length() < 10) {
                oldMobileno.requestFocus();
                oldMobileno.setError(getString(R.string.msg_old_mobile));
            } else*/
            if (new_no.length() == 0 || new_no.length() < 10) {
                newMobileno.requestFocus();
                newMobileno.setError(getString(R.string.msg_new_mobile));
            } else {
                /*if (!nbsmDatamodel.getPHONE_1().equalsIgnoreCase(old_mo)) {
                    UtilsClass.showToastShort(context, getString(R.string.msg_old_mobile_matches));
                    return;
                }*/
                if (db.updateMobileNo(new_no, nbsmDatamodel.getINSTALLATION())) {
                    UtilsClass.showToastShort(context, getString(R.string.msg_mobile_updated));
                    tv_contact_no.setText(new_no);
                } else {
                    UtilsClass.showToastShort(context, getString(R.string.msg_error));
                }
                try {
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } catch (Exception e) {

                }

                builder.dismiss();
            }
        });
        builder.show();
    }


    private class AsyncTaskExample extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fetchNSBMdata();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (nbsmDatamodel != null) {
                fillData();
            } else {
                UtilsClass.showToastLong(context, getString(R.string.msg_nodata));
                finish();
            }
        }
    }

    private void fillData() {
        try {
            if (SelChoice.equalsIgnoreCase("SeqNo")) {
                varCond = " where INSTALLATION='" + nbsmDatamodel.getINSTALLATION() + "'";
                utils.setSerchCondition(varCond);
            }

            if (utils.getSerchCondition().contains("LEGACY_ACCOUNT_NO2")) {
                varCond = " where INSTALLATION='" + nbsmDatamodel.getINSTALLATION() + "'";
                utils.setSerchCondition(varCond);
            } else if (utils.getSerchCondition().contains("CA")) {
                varCond = " where INSTALLATION='" + nbsmDatamodel.getINSTALLATION() + "'";
                utils.setSerchCondition(varCond);
            } else if (utils.getSerchCondition().contains("PHONE_1")) {
                varCond = " where INSTALLATION='" + nbsmDatamodel.getINSTALLATION() + "'";
                utils.setSerchCondition(varCond);
            }

            Log.e("varcooooo", varCond);

            if ((nbsmDatamodel.getINSTALLATION() != null) && (!(nbsmDatamodel.getINSTALLATION().equalsIgnoreCase("null")))) {
                tv_installation_no.setText(nbsmDatamodel.getINSTALLATION());
            }

            if ((nbsmDatamodel.getCA() != null) && (!(nbsmDatamodel.getCA().equalsIgnoreCase("null")))) {
                tv_ca_no.setText(nbsmDatamodel.getCA());
            }
            if ((nbsmDatamodel.getBUILDING_DESC() != null) && (!(nbsmDatamodel.getBUILDING_DESC().equalsIgnoreCase("null")))) {
                tv_building_dec.setText(nbsmDatamodel.getBUILDING_DESC());
            }

            if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                if ((nbsmDatamodel.getLEGACY_ACCOUNT_NO2() != null) && (!(nbsmDatamodel.getLEGACY_ACCOUNT_NO2().equalsIgnoreCase("null")))) {
                    tv_building_code_value.setText(nbsmDatamodel.getLEGACY_ACCOUNT_NO2());
                }
            } else {
                if ((nbsmDatamodel.getLEGACY_ACCOUNT_NO() != null) && (!(nbsmDatamodel.getLEGACY_ACCOUNT_NO().equalsIgnoreCase("null")))) {
                    tv_building_code_value.setText(nbsmDatamodel.getLEGACY_ACCOUNT_NO());
                }
            }

            if ((nbsmDatamodel.getPOLE_NO() != null) && (!(nbsmDatamodel.getPOLE_NO().equalsIgnoreCase("null")))) {
                tv_pole_number.setText(nbsmDatamodel.getPOLE_NO());
            }
            if ((nbsmDatamodel.getWALKING_SEQ_CHK() != null) && (!(nbsmDatamodel.getWALKING_SEQ_CHK().equalsIgnoreCase("null")))) {
                tv_flag_value.setText(nbsmDatamodel.getWALKING_SEQ_CHK());
            }
            if ((nbsmDatamodel.getMETER_NO() != null) && (!(nbsmDatamodel.getMETER_NO().equalsIgnoreCase("null")))) {
                tv_meter_number.setText(nbsmDatamodel.getMETER_NO());
            }
            if ((nbsmDatamodel.getMRU() != null) && (!(nbsmDatamodel.getMRU().equalsIgnoreCase("null")))) {
                tv_mru.setText(nbsmDatamodel.getMRU());
            }

            String address = nbsmDatamodel.getADDRESS1();
            String address1 = nbsmDatamodel.getADDRESS2();

            String namee = nbsmDatamodel.getNAME();

            if ((namee == null) || (namee.equalsIgnoreCase("null"))) {
                namee = "";
            }

            if ((address == null) || (address.equalsIgnoreCase("null"))) {
                address = "";
            }

            if ((address1 == null) || (address1.equalsIgnoreCase("null"))) {
                address1 = "";
            }

            tv_name_add.setText(namee + "\n" + address + " " + address1);

            String specialRemark = "";
            String meterCond = "";
            specialRemark = nbsmDatamodel.getSPECIAL_REM();
            meterCond = nbsmDatamodel.getMETER_CONDITION();

            if ((specialRemark == null) || (specialRemark.equalsIgnoreCase("null"))) {
                specialRemark = "";
                tv_remark.setText(meterCond);
            }
            if ((meterCond == null) || (meterCond.equalsIgnoreCase("null"))) {
                meterCond = "";
                tv_remark.setText(meterCond);
            }

            if (specialRemark.equalsIgnoreCase("")) {
                tv_remark.setText(meterCond);
            } else if (meterCond.equalsIgnoreCase("")) {
                tv_remark.setText(specialRemark);
            } else {
                tv_remark.setText(specialRemark + " / " + meterCond);
            }

            if ((nbsmDatamodel.getPHONE_1() != null) && (!(nbsmDatamodel.getPHONE_1().equalsIgnoreCase("null")))) {
                tv_contact_no.setText(nbsmDatamodel.getPHONE_1());
            }

            try {
                if (nbsmDatamodel.getREVISIT_FLAG().equalsIgnoreCase("1")) {
                    chk_base_type.setChecked(true);
                } else {
                    chk_base_type.setChecked(false);
                }
            } catch (Exception e) {
                chk_base_type.setChecked(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        btn_submit.setEnabled(true);
        btn_submit.setBackgroundColor(Color.parseColor("#3F51B5"));

        if (nbsmDatamodel.getSAN_LOAD() == null && PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
            btn_submit.setEnabled(false);
            btn_submit.setBackgroundColor(Color.parseColor("#696969"));
            //btn_submit.setAlpha(1);

            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("You can't bill this Installation as SAN LOAD is '0'.Please Contact Admin");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });


            AlertDialog alert11 = builder1.create();
            alert11.show();
        } else if ((nbsmDatamodel.getSAN_LOAD() != null) && (!(nbsmDatamodel.getSAN_LOAD().equalsIgnoreCase("null")))) {
            if (Double.parseDouble(nbsmDatamodel.getSAN_LOAD()) == 0 && PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                btn_submit.setEnabled(false);
                btn_submit.setBackgroundColor(Color.parseColor("#696969"));
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("You can't bill this Installation as SAN LOAD is '0'. Please Contact Admin");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        } else if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
            String query = "SELECT COUNT(1) as meter_count FROM (SELECT A.INSTALLATION,A.SCHEDULE_METER_READ_DATE,B.READ_ONLY,B.USER_TYPE,A.REGISTER_CODE,A.METER_NO " +
                    "FROM TBL_SPOTBILL_CHILD_DETAILS A,TBL_SPOTBILL_HEADER_DETAILS B " +
                    "WHERE A.INSTALLATION=B.INSTALLATION " +
                    "AND A.SCHEDULE_METER_READ_DATE=B.SCHEDULE_METER_READ_DATE " +
                    "AND B.USER_TYPE='S' AND  A.INSTALLATION='" + nbsmDatamodel.getINSTALLATION() + "' GROUP BY A.METER_NO,A.INSTALLATION) GROUP BY INSTALLATION";
            Cursor rs1 = db.getCalculateedData(query);
            int meterCount = 0;
            while (rs1.moveToNext()) {
                meterCount = rs1.getInt(0);
            }
            rs1.close();

            //means user having multiple meters then dont't allow to bill the user
            if (meterCount > 1) {
                btn_submit.setEnabled(false);
                btn_submit.setBackgroundColor(Color.parseColor("#696969"));
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("You can't bill this Installation as it has multiple meter count(" + meterCount + "). Please Contact Admin");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();
            }

        }
    }


    private class AsyncTaskExampleUpdate extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
            meterOkNonSbmReadingModel.setIsRevisit(isRevisit);
            //meterOkNonSbmReadingModel.setSt_supplyStatus(supplyStatusSpinner);
            // meterOkNonSbmReadingModel.setSt_usages(usageSpinner);
            //meterOkNonSbmReadingModel.setSt_additional(et_add_info_dc.getText().toString().trim());
            meterOkNonSbmReadingModel.setMr_reason("NA");
            // if (meterType.equalsIgnoreCase("NA")) {
            db.updateNARemarksNonSbmReading(meterOkNonSbmReadingModel, nbsmDatamodel.getINSTALLATION());
            db.updateMrReasonChild(nbsmDatamodel.getINSTALLATION(), "NA");
            //}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                try {
                    calculateBill.CalculateBill(nbsmDatamodel.getINSTALLATION(), "NA", context, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //  db.updateNONSBMData();
            }
            // if (meterType.equalsIgnoreCase("NA")) {
            uUtils = new UploadUtils(context);
            uUtils.getHeaderdetails(varCond);
            model = uUtils.getChilddetails(varCond);
            uploadData();
            //}
        }
    }

    public void uploadData() {
        if (AppUtils.isInternetAvailable(this)) {
            Gson g = new Gson();
            Log.e("clist", g.toJson(model));
            dUtils.showDialog("Uploading", "Please wait..");
            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
            Call<UploadDataResponseModel> call = service.uploadData(utils.getAuthToken(), model);
            call.enqueue(new Callback<UploadDataResponseModel>() {
                @Override
                public void onResponse(Call<UploadDataResponseModel> call, Response<UploadDataResponseModel> response) {
                    UploadDataResponseModel object = response.body();
                    try {
                        if (object.statusCode == 200) {
                            db.updatesendFlag(utils.getSerchCondition());
                            dUtils.dismissDialog();
                            //showErrorDialog("Success", object.message);
                            UtilsClass.showToastLong(context, object.message);
                            /*Intent intent = new Intent(context, BillingCalculation.class);
                            intent.putExtra("installationno", nbsmDatamodel.getINSTALLATION());
                            intent.putExtra("MR_REASON", "NA");
                            startActivity(intent);
                            finish();*/
                            RedirectToPage();
                        } else if (object.statusCode == 410) {
                            db.updatesendFlag(utils.getSerchCondition());
                            dUtils.dismissDialog();
                            try {
                                utils.setAppVersion(object.softwareVersionNo.toString().trim());
                            } catch (Exception e) {

                            }

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
                        } else {
                            showErrorDialog("Error", object.message);
                        }
                    } catch (Exception e) {
                        showErrorDialog("Error", getString(R.string.something_wrong));
                        UtilsClass.showToastLong(context, object.message);
                        /*Intent intent = new Intent(context, BillingCalculation.class);
                        intent.putExtra("installationno", nbsmDatamodel.getINSTALLATION());
                        intent.putExtra("MR_REASON", "NA");
                        startActivity(intent);
                        finish();*/
                        RedirectToPage();
                    }
                }

                @Override
                public void onFailure(Call<UploadDataResponseModel> call, Throwable t) {
                    dUtils.dismissDialog();
                    t.printStackTrace();
                    Log.i("TAG", t.getMessage());
                    Toast.makeText(context, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            dUtils.dismissDialog();
            /*Intent intent = new Intent(context, BillingCalculation.class);
            intent.putExtra("installationno", nbsmDatamodel.getINSTALLATION());
            intent.putExtra("MR_REASON", "NA");
            startActivity(intent);
            finish();*/
            RedirectToPage();
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }


    public void RedirectToPage() {
        if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
            Intent intent = new Intent(context, BillingCalculation.class);
            intent.putExtra("installationno", nbsmDatamodel.getINSTALLATION());
            intent.putExtra("MR_REASON", "NA");
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }
}



