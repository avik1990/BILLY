package com.tpcodl.billingreading.activity.remark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.BillingCalculation;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.activity.calculateBill;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.listeners.MeterTypeSpinnerCallback;
import com.tpcodl.billingreading.listeners.ReasonDCSpinnerCallback;
import com.tpcodl.billingreading.listeners.SourceSupplySpinnerCallback;
import com.tpcodl.billingreading.listeners.SupplyStatusSpinnerCallback;
import com.tpcodl.billingreading.listeners.UsageSpinnerCallback;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tpcodl.billingreading.utils.UtilsClass.meterPhoto;
import static com.tpcodl.billingreading.utils.UtilsClass.testerPhoto;

public class NPDCRemarkActivity extends AppCompatActivity implements View.OnClickListener,
        UsageSpinnerCallback,
        SupplyStatusSpinnerCallback,
        MeterTypeSpinnerCallback,
        SourceSupplySpinnerCallback,
        ReasonDCSpinnerCallback {

    private ConstraintLayout cl_np;
    private ConstraintLayout cl_dc;
    private Spinner spinner_status;
    private Spinner spinner_usage;
    private Spinner spinner_meter_type;
    private Spinner spinner_reason_dc;
    private Spinner source_spinner;

    private ImageView iv_back;
    private TextView tv_title;
    private Context mContext;

    private ImageView iv_image_cap;
    private ImageView iv_image_cap_dc;
    private ImageView iv_tester_cap_dc;

    private ActivityUtils utils;
    MeterOkNonSbmReadingModel meterOkNonSbmReadingModel;
    boolean meterCapture = false;
    boolean testerCapture = false;
    boolean imageDc = false;
    NSBMData nSBMData;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    private DialogUtils dUtils;
    private RequestModel model;
    UploadUtils uUtils;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private Uri picUri1;
    private Uri picUriDC;
    private Uri picUriTester;
    String SelChoice, SelAcntno, MeterType, IS_REVISIT;
    DatabaseHelper db;

    String isSeqCorrect = "";
    String isMeterlocation = "";
    String st_supplyStatus;
    String st_usages;
    String st_mtrtype;
    String st_additional;
    String st_reason_for_dc;
    String st_source_supply;
    Button btnSubmit, btnSubmitNP;
    EditText et_add_info_dc, et_add_info;
    RadioGroup rg_seq, rg_meter_location, rg_np;
    String varCond;
    String installationno;

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npdc_remark);
        mContext = this;

        model = new RequestModel();
        utils = ActivityUtils.getInstance();
        dUtils = new DialogUtils(mContext);
        db = new DatabaseHelper(mContext);
        meterOkNonSbmReadingModel = new MeterOkNonSbmReadingModel();
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        installationno = getIntent().getStringExtra(Constant.INSTALLATIONNO);

        getCoordinates();

        getIntentData();
        initView();
    }

    private void getIntentData() {
        SelChoice = getIntent().getStringExtra(Constant.SEL_CHOICE);
        SelAcntno = getIntent().getStringExtra(Constant.DETAILS_NO);
        MeterType = getIntent().getStringExtra(Constant.METER_TYPE);
        IS_REVISIT = getIntent().getStringExtra(Constant.IS_REVISIT);
        installationno = getIntent().getStringExtra(Constant.INSTALLATIONNO);

        varCond = AppUtils.getQueryCondition(mContext, SelChoice, SelAcntno, installationno);

        nSBMData = db.getNBSMData(varCond, -1);
    }

    private void initView() {
        Log.e("PageName", "NPDCRemarkActivity");
        rg_np = findViewById(R.id.rg_np);
        rg_seq = findViewById(R.id.rg_sequence);
        rg_meter_location = findViewById(R.id.rg_meter_location);
        btnSubmitNP = findViewById(R.id.btnSubmitNP);
        et_add_info = findViewById(R.id.et_add_info);
        et_add_info_dc = findViewById(R.id.et_add_info_dc);
        btnSubmit = findViewById(R.id.btnSubmit);
        cl_dc = findViewById(R.id.cl_dc);
        cl_np = findViewById(R.id.cl_np);
        spinner_status = findViewById(R.id.spinner_status);
        spinner_usage = findViewById(R.id.spinner_usage);
        spinner_meter_type = findViewById(R.id.spinner_meter_type);
        spinner_reason_dc = findViewById(R.id.spinner_reason_dc);
        source_spinner = findViewById(R.id.source_spinner);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);

        iv_image_cap = findViewById(R.id.iv_image_cap);
        iv_image_cap_dc = findViewById(R.id.iv_image_cap_dc);
        iv_tester_cap_dc = findViewById(R.id.iv_tester_cap_dc);

        utils = ActivityUtils.getInstance();
        iv_back.setOnClickListener(this);
        iv_back.setOnClickListener(view -> finish());
        iv_image_cap.setOnClickListener(this);
        iv_image_cap_dc.setOnClickListener(this);
        iv_tester_cap_dc.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnSubmitNP.setOnClickListener(this);

        if (nSBMData == null) {
            return;
        }

        if (MeterType.equalsIgnoreCase("NP")) {
            cl_dc.setVisibility(View.GONE);
            cl_np.setVisibility(View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.np_remarks_string));
            btnSubmitNP.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        } else {
            cl_dc.setVisibility(View.VISIBLE);
            cl_np.setVisibility(View.GONE);
            tv_title.setText(getResources().getString(R.string.dc_remarks_string));
            btnSubmitNP.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        }

        rg_seq.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_seq_yes:
                        isSeqCorrect = "Y";
                        break;
                    case R.id.rb_seq_no:
                        isSeqCorrect = "N";
                        break;
                }
            }
        });
        rg_np.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_seq_yes_np:
                        isSeqCorrect = "Y";
                        break;
                    case R.id.rb_seq_no_np:
                        isSeqCorrect = "N";
                        break;
                }
            }
        });

        rg_meter_location.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_met_inside:
                        isMeterlocation = "I";
                        break;
                    case R.id.rb_met_outside:
                        isMeterlocation = "O";
                        break;
                }
            }
        });

        ArrayAdapter<String> usages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.usage));
        usages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_usage.setAdapter(usages);
        spinner_usage.setOnItemSelectedListener(new AppUtils.UsageSpinnerClass(mContext, this));

        ArrayAdapter<String> status = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.supplyStatus));
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_status.setAdapter(status);
        spinner_status.setOnItemSelectedListener(new AppUtils.StatusSpinnerClass(mContext, this));

        ArrayAdapter<String> meterTypes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterType));
        meterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_meter_type.setAdapter(meterTypes);
        spinner_meter_type.setOnItemSelectedListener(new AppUtils.MeterTypeSpinnerClass(mContext, this));


        ArrayAdapter<String> reasonAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.reasonofDC));
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_reason_dc.setAdapter(reasonAdapter);
        spinner_reason_dc.setOnItemSelectedListener(new AppUtils.MeterReasonDCSpinnerClass(mContext, this));

        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sourceofsupply));
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        source_spinner.setAdapter(sourceAdapter);
        source_spinner.setOnItemSelectedListener(new AppUtils.SourceSupplySpinnerClass(mContext, this));

    }

    @Override
    public void onClick(View view) {
        if (view == iv_tester_cap_dc) {
            meterCapture = false;
            testerCapture = true;
            imageDc = false;
            checkPermissions();
        } else if (view == iv_image_cap) {
            meterCapture = true;
            testerCapture = false;
            imageDc = false;
            checkPermissions();
        } else if (view == iv_image_cap_dc) {
            meterCapture = false;
            testerCapture = false;
            imageDc = true;
            checkPermissions();
        } else if (view == btnSubmit) {
            meterOkNonSbmReadingModel.setSt_supplyStatus(st_supplyStatus);
            meterOkNonSbmReadingModel.setSt_mtrtype(st_mtrtype);
            meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
            meterOkNonSbmReadingModel.setSt_reason_for_dc(st_reason_for_dc);
            meterOkNonSbmReadingModel.setIsMeterlocation(isMeterlocation);
            meterOkNonSbmReadingModel.setSt_additional(et_add_info_dc.getText().toString().trim());
            meterOkNonSbmReadingModel.setSourceOfSupply(st_source_supply);
            meterOkNonSbmReadingModel.setMr_reason(MeterType);

            if (MeterType.equalsIgnoreCase("DC") || MeterType.equalsIgnoreCase("ND")) {
                testerPhoto = true;
            }

            if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                if (meterPhoto) {
                    new AsyncTaskExampleUpdate().execute();
                } else {
                    Toast.makeText(mContext, "Please capture image first.", Toast.LENGTH_SHORT).show();
                }
            } else {
                new AsyncTaskExampleUpdate().execute();
            }


        } else if (view == btnSubmitNP) {
            meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
            meterOkNonSbmReadingModel.setSt_usages(st_usages);
            meterOkNonSbmReadingModel.setSt_additional(et_add_info.getText().toString().trim());
            meterOkNonSbmReadingModel.setMr_reason(MeterType);
            // if (meterPhoto) {
            new AsyncTaskExampleUpdate().execute();
          /*  } else {
                Toast.makeText(mContext, "Please capture image first.", Toast.LENGTH_SHORT).show();

            }*/
        }

    }

    public void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        int REQUEST_CODE_FILE_UPLOAD = 5902;
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        if (permissionStatus.getBoolean(REQUIRED_SDK_PERMISSIONS[0], true)) {
                            //Previously Permission Request was cancelled with 'Dont Ask Again',
                            // Redirect to Settings after showing Information about why you need the permission
                            AlertDialog.Builder builder = new AlertDialog.Builder(NPDCRemarkActivity.this);
                            builder.setTitle("Need Storage Permission");
                            builder.setMessage("This app needs storage permission.");
                            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    sentToSettings = true;
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                    Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                        return;
                    }
                }
                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                editor.commit();
                pickPicture(REQUEST_CODE_FILE_UPLOAD);
                break;
        }
    }


    private void pickPicture(int requestCode) {
        ImagePicker.Companion.with(this)
                .crop()
                .cameraOnly()
                //Crop image(Optional), Check Customization for more option
                .compress(1024)
                //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = "";
        if (resultCode == Activity.RESULT_OK) {
            if (meterCapture) {
                picUri1 = data != null ? data.getData() : null;
                iv_image_cap.setImageURI(picUri1);
                filePath = picUri1.getPath();
                meterPhoto = true;
                meterOkNonSbmReadingModel.setUpload_image_NP(AppUtils.readFileAsBase64String(filePath));
            } else if (testerCapture) {
                picUriTester = data != null ? data.getData() : null;
                iv_tester_cap_dc.setImageURI(picUriTester);
                filePath = picUriTester.getPath();
                testerPhoto = true;
                meterOkNonSbmReadingModel.setPhoto_tester(AppUtils.readFileAsBase64String(filePath));
            } else if (imageDc) {
                picUriDC = data != null ? data.getData() : null;
                iv_image_cap_dc.setImageURI(picUriDC);
                filePath = picUriDC.getPath();
                meterPhoto = true;
                meterOkNonSbmReadingModel.setPhoto_meter(AppUtils.readFileAsBase64String(filePath));
            }

            File file = new File(filePath);
            String filename = file.getName();
         /*   Log.d("cdf", "onActivityResult: " + filename);

            String base64String = AppUtils.readFileAsBase64String(filePath);

            AppUtils.UploadImageTask myTask = new AppUtils.U qploadImageTask(this);

            myTask.execute(utils.getUserID() + "_" + UtilsClass.getDateFolder() + "_" +*//*et_kwh.getText().toString().trim()*//*"", base64String);
            Log.d("ds", "onActivityResult: " + base64String);*/

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.RESULT_ERROR, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void meterTypeSpinnerSelectedItem(int position, String value, String abbNamme) {
        st_mtrtype = String.valueOf(position);
    }

    @Override
    public void reasonDCSSpinnerSelectedItem(int position, String value, String abbName) {
        st_reason_for_dc = String.valueOf(position);

    }

    @Override
    public void sourceSupplySpinnerSelectedItem(int position, String value, String abbName) {
        st_source_supply = String.valueOf(position);
    }

    @Override
    public void supplyStatusSpinnerSelectedItem(int position, String value, String abbName) {
        st_supplyStatus = String.valueOf(position);
    }

    @Override
    public void usageSpinnerSelectedItem(int position, String value, String abbName) {
        st_usages = String.valueOf(position);
    }

    private class AsyncTaskExampleUpdate extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (MeterType.equalsIgnoreCase("NP")) {
                db.UpdateNpDC(meterOkNonSbmReadingModel, varCond, "NP", nSBMData.getINSTALLATION());
                db.updateMrReasonChild(nSBMData.getINSTALLATION(), MeterType);
            } else if (MeterType.equalsIgnoreCase("DC")) {
                db.UpdateNpDC(meterOkNonSbmReadingModel, varCond, "DC", nSBMData.getINSTALLATION());
                db.updateMrReasonChild(nSBMData.getINSTALLATION(), MeterType);
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            UtilsClass.showToastShort(mContext, getString(R.string.msg_datasaved));
            if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                try {
                    calculateBill.CalculateBill(installationno, MeterType, mContext, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                UpdateNONSBMData();
            }

            if (MeterType.equalsIgnoreCase("NP")) {
                uUtils = new UploadUtils(mContext);
                uUtils.getHeaderdetails(varCond);
                model = uUtils.getChilddetails(varCond);
                if (AppUtils.checkArrersAndUpload(mContext, installationno) == 0) {
                    uploadData();
                } else {
                    UtilsClass.showToastLong(mContext, getString(R.string.arrers_msg));
                    RedirectToPage();
                }
            } else if (MeterType.equalsIgnoreCase("DC")) {
                uUtils = new UploadUtils(mContext);
                uUtils.getHeaderdetails(varCond);
                model = uUtils.getChilddetails(varCond);
                if (AppUtils.checkArrersAndUpload(mContext, installationno) == 0) {
                    uploadData();
                } else {
                    UtilsClass.showToastLong(mContext, getString(R.string.arrers_msg));
                    RedirectToPage();
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
        strSelectSQL_01 = "SELECT CASE WHEN A.RATE_CATEGORY='DOM_OTH' THEN 'DOM' WHEN A.RATE_CATEGORY='DKJ' THEN 'BPL' WHEN A.RATE_CATEGORY='LT_GENPRPS' THEN 'GPS' WHEN A.RATE_CATEGORY='LT_SPBLPRS' THEN 'SPP' ELSE 'DOM' END TARIFF " +
                " ,CASE WHEN B.MF >= 1 THEN B.MF ELSE 1 END AS MF,A.CONSUMER_OWNED,B.NO_OF_DIGITS,A.SAN_LOAD,CAST(B.PREV_MTR_READ AS LONG) as PREV_MTR_READ,A.CR_ADJ, A.DB_ADJ,A.PRV_BILLED_AMT, " +
                " A.PREVIOUS_BILLED_PROV_UNIT,A.AVERAGE_KWH,A.ED_EXEMPT,B.PRESENT_METER_READING,B.METER_CONDITION P_STATUS,B.METER_CONDITION,B.METER_TYP,A.HL_MONTHS NO_OF_MON " +
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
        String lastdayofprvmonth = "";
        //String q = "SELECT date('" + strBillDate + "','start of month','0 month','-1 day') as LastDate";
        String q = "SELECT date(SCHEDULE_METER_READ_DATE,'start of month','0 month','-1 day') as LastDate from TBL_SPOTBILL_HEADER_DETAILS where INSTALLATION='" + installationno + "'";

        Log.e("QUERYLastDate", q);
        Cursor rs1 = db.getCalculateedData(q);
        while (rs1.moveToNext()) {
            lastdayofprvmonth = rs1.getString(0);
        }

        rs1.close();

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

    public void uploadData() {
        if (AppUtils.isInternetAvailable(this)) {
            try {
                dUtils.showDialog("Downloading", "Please wait..");
            } catch (Exception e) {

            }

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
                            UtilsClass.showToastLong(mContext, object.message);
                            /*Intent intent = new Intent(mContext, BillingCalculation.class);
                            intent.putExtra("installationno", nSBMData.getINSTALLATION());
                            intent.putExtra("MR_REASON", MeterType);
                            startActivity(intent);*/
                            try {
                                utils.setAppVersion(object.softwareVersionNo.toString().trim());
                            } catch (Exception e) {

                            }

                            RedirectToPage();
                        } else if (object.statusCode == 410) {
                            /*db.updatesendFlag(utils.getSerchCondition());
                            dUtils.dismissDialog();
                            UtilsClass.showToastLong(mContext, object.message);
                            RedirectToPage();*/
                            try {
                                utils.setAppVersion(object.softwareVersionNo.toString().trim());
                            } catch (Exception e) {

                            }

                            db.updatesendFlag(utils.getSerchCondition());
                            dUtils.dismissDialog();
                            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                            alertDialog.setTitle("Message");
                            alertDialog.setCancelable(false);
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setMessage(object.message);
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "PROCEED",
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
                        /*Intent intent = new Intent(mContext, BillingCalculation.class);
                        intent.putExtra("installationno", nSBMData.getINSTALLATION());
                        intent.putExtra("MR_REASON", MeterType);
                        startActivity(intent);*/
                        RedirectToPage();
                    }
                }

                @Override
                public void onFailure(Call<UploadDataResponseModel> call, Throwable t) {
                    dUtils.dismissDialog();
                    t.printStackTrace();
                    Log.i("TAG", t.getMessage());
                    Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            dUtils.dismissDialog();
            /*Intent intent = new Intent(mContext, BillingCalculation.class);
            intent.putExtra("installationno", nSBMData.getINSTALLATION());
            intent.putExtra("MR_REASON", MeterType);
            startActivity(intent);*/
            RedirectToPage();
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }


    public void RedirectToPage() {
        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            Intent intent = new Intent(mContext, BillingCalculation.class);
            intent.putExtra("installationno", nSBMData.getINSTALLATION());
            intent.putExtra("MR_REASON", MeterType);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(mContext, SearchDataActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void showErrorDialog(String title, String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getCoordinates() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(
                mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    }


}