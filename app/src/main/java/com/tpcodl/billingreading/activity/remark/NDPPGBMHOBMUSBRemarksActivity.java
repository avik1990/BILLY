package com.tpcodl.billingreading.activity.remark;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.BillingCalculation;
import com.tpcodl.billingreading.activity.DecimalDigitsInputFilter;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.activity.calculateBill;
import com.tpcodl.billingreading.activity.viewcaptureddata.ViewCapturedDataActivity;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.listeners.MeterHeightSpinnerCallback;
import com.tpcodl.billingreading.listeners.MeterObstacleSpinnerCallback;
import com.tpcodl.billingreading.listeners.MeterSealSpinnerCallback;
import com.tpcodl.billingreading.listeners.MeterStucksSpinnerCallback;
import com.tpcodl.billingreading.listeners.MeterTypeSpinnerCallback;
import com.tpcodl.billingreading.listeners.PaperPasteSpinnerCallback;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tpcodl.billingreading.utils.UtilsClass.meterPhoto;
import static com.tpcodl.billingreading.utils.UtilsClass.testerPhoto;

public class NDPPGBMHOBMUSBRemarksActivity extends AppCompatActivity implements View.OnClickListener, MeterTypeSpinnerCallback,
        SupplyStatusSpinnerCallback, UsageSpinnerCallback, MeterObstacleSpinnerCallback, MeterSealSpinnerCallback,
        MeterStucksSpinnerCallback,
        SourceSupplySpinnerCallback, PaperPasteSpinnerCallback, MeterHeightSpinnerCallback {

    private String meterType = "";
    private ConstraintLayout cl_gb;
    private ConstraintLayout cl_spinner;
    private ConstraintLayout cl_paper_pasted;
    private ConstraintLayout cl_meter_stuck;
    private ConstraintLayout cl_meter_height;
    private ConstraintLayout cl_meter_height_spin;
    private ConstraintLayout cl_meter_source_supply;
    private ConstraintLayout cl_meter_seal_status;
    private ConstraintLayout cl_obstacle_type;
    private ConstraintLayout cl_meter_image;
    private ConstraintLayout cl_tester_photo;
    private TextView tv_elt_no;
    private RadioGroup rg_elt;
    private TextView tv_meter_type_status;
    private View view5;
    private TextView tv_unsafe_condition;
    private RadioGroup rg_unsafe;
    private Context mContext;
    private Spinner spinner_status;
    private Spinner spinner_usage;
    private Spinner spinner_meter_type;
    private Spinner spinner_meter_source_supply;
    private Spinner spinner_meter_height;
    private Spinner spinner_paper_paste;
    private Spinner spinner_meter_stuck;
    private Spinner spinner_meter_seal_status;
    private Spinner spinner_type_obstacle;
    private ImageView iv_back;
    private TextView tv_title;
    private ImageView iv_image_cap;
    private ImageView iv_image__tester;
    private ActivityUtils utils;
    boolean meterCapture = false;
    boolean testerCapture = false;
    private RadioGroup rg_seq;
    private RadioGroup rg_meter_location;
    private RadioButton rb_elt_yes;
    private RadioButton rb_elt_no;
    private RadioButton rb_seq_yes;
    private RadioButton rb_seq_no;
    private RadioButton rb_meter_loc_yes;
    private RadioButton rb_meter_loc_no;
    private RadioButton rb_meter_height_yes;
    private RadioButton rb_meter_height_no;
    private EditText et_add_info;
    String SelChoice, SelAcntno, varCond, MeterType, sourceOfSupply;
    MeterOkNonSbmReadingModel meterOkNonSbmReadingModel;
    NSBMData nbsmDatamodel;
    DatabaseHelper db;
    private static String IS_REVISIT = "";

    String st_supplyStatus;
    String st_usages;
    String st_mtrType;
    String st_mtrHeight;
    String st_meterSeal;
    String st_meterObstacle;
    String st_meterStuck;
    String st_paperPaste;

    private DialogUtils dUtils;
    private RequestModel model;
    UploadUtils uUtils;

    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private Uri picUri1;
    private EditText et_kwh, et_kw;
    private Uri picUriTester;
    private String isSeqCorrect = "";
    private String isELTON = "";
    private String isMeterlocation = "";
    private Button btn_submit;
    private String isMeterUnsafe = "";
    LinearLayout llContainer;
    EditText ed[];
    String installationno;
    ProgressDialog pDialog;
    private boolean blankSubmit;
    private TextView tv_title_image;

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    LinearLayout llSubContainerText;
    EditText tv[];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nd_pp_gb_mh);
        mContext = this;
        model = new RequestModel();
        utils = ActivityUtils.getInstance();
        dUtils = new DialogUtils(mContext);
        db = new DatabaseHelper(mContext);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        SelChoice = getIntent().getStringExtra(Constant.SEL_CHOICE);
        SelAcntno = getIntent().getStringExtra(Constant.DETAILS_NO);
        MeterType = getIntent().getStringExtra(Constant.METER_TYPE);
        IS_REVISIT = getIntent().getStringExtra(Constant.IS_REVISIT);
        installationno = getIntent().getStringExtra(Constant.INSTALLATIONNO);

        utils = ActivityUtils.getInstance();
        meterOkNonSbmReadingModel = new MeterOkNonSbmReadingModel();

        getCoordinates();

        pDialog = new ProgressDialog(mContext); //Your Activity.this
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        initView();


    }

    private void initView() {
        Log.e("PageName", "NDPPGBMHOBMUSBRemarksActivity");
        tv_title_image = findViewById(R.id.tv_title_image);
        llSubContainerText = findViewById(R.id.llSubContainerText);
        cl_gb = findViewById(R.id.cl_gb);
        cl_spinner = findViewById(R.id.cl_spinner);
        cl_paper_pasted = findViewById(R.id.cl_paper_pasted);
        cl_meter_stuck = findViewById(R.id.cl_meter_stuck);
        cl_meter_height = findViewById(R.id.cl_meter_height);
        cl_meter_height_spin = findViewById(R.id.cl_meter_height_spin);
        cl_meter_source_supply = findViewById(R.id.cl_meter_source_supply);
        cl_meter_seal_status = findViewById(R.id.cl_meter_seal_status);
        cl_obstacle_type = findViewById(R.id.cl_obstacle_type);
        cl_meter_image = findViewById(R.id.cl_meter_image);
        cl_tester_photo = findViewById(R.id.cl_tester_photo);
        tv_elt_no = findViewById(R.id.tv_elt_no);
        rg_elt = findViewById(R.id.rg_elt);
        tv_meter_type_status = findViewById(R.id.tv_meter_type_status);
        view5 = findViewById(R.id.view5);
        rg_seq = findViewById(R.id.rg_seq);
        rg_meter_location = findViewById(R.id.rg_meter_location);
        rg_meter_location = findViewById(R.id.rg_meter_location);
        rb_elt_yes = findViewById(R.id.rb_elt_yes);
        rb_elt_no = findViewById(R.id.rb_elt_no);
        rb_seq_yes = findViewById(R.id.rb_seq_yes);
        rb_seq_no = findViewById(R.id.rb_seq_no);
        rb_meter_loc_yes = findViewById(R.id.rb_meter_loc_yes);
        rb_meter_loc_no = findViewById(R.id.rb_meter_loc_no);
        et_add_info = findViewById(R.id.et_add_info);
        rb_meter_height_yes = findViewById(R.id.rb_meter_unsafe_yes);
        rb_meter_height_no = findViewById(R.id.rb_meter_unsafe_no);
        iv_image__tester = findViewById(R.id.iv_image__tester);
        iv_image_cap = findViewById(R.id.iv_image_cap);
        btn_submit = findViewById(R.id.btn_submit);
        llContainer = findViewById(R.id.llContainer);


        utils = ActivityUtils.getInstance();

        iv_image_cap.setOnClickListener(this);
        iv_image__tester.setOnClickListener(this);
        tv_unsafe_condition = findViewById(R.id.tv_unsafe_condition);
        rg_unsafe = findViewById(R.id.rg_unsafe);
        spinner_status = findViewById(R.id.spinner_status);
        spinner_usage = findViewById(R.id.spinner_usage);
        spinner_meter_type = findViewById(R.id.spinner_meter_type);
        spinner_meter_source_supply = findViewById(R.id.spinner_meter_source_supply);
        spinner_paper_paste = findViewById(R.id.spinner_paper_paste);
        spinner_meter_stuck = findViewById(R.id.spinner_meter_stuck);
        spinner_meter_height = findViewById(R.id.spinner_meter_height);
        spinner_meter_seal_status = findViewById(R.id.spinner_meter_seal_status);
        spinner_type_obstacle = findViewById(R.id.spinner_type_obstacle);
        et_kwh = findViewById(R.id.et_kwh);
        et_kw = findViewById(R.id.et_kw);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);

        btn_submit.setOnClickListener(this);


        rg_seq.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_seq_yes:
                    isSeqCorrect = "Y";
                    break;
                case R.id.rb_seq_no:
                    isSeqCorrect = "N";
                    break;
            }
        });
        rg_elt.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_elt_yes:
                    isELTON = "Y";
                    break;
                case R.id.rb_elt_no:
                    isELTON = "N";
                    break;
            }
        });

        rg_meter_location.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_meter_loc_yes:
                    isMeterlocation = "I";
                    break;
                case R.id.rb_meter_loc_no:
                    isMeterlocation = "O";
                    break;
            }
        });

        rg_unsafe.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_meter_unsafe_yes:
                        isMeterUnsafe = "Y";
                        break;
                    case R.id.rb_meter_unsafe_no:
                        isMeterUnsafe = "N";
                        break;
                }

            }
        });


        iv_back.setOnClickListener(this);
        iv_back.setOnClickListener(view -> finish());

        varCond = AppUtils.getQueryCondition(mContext, SelChoice, SelAcntno, installationno);

        getIntentData();


        switch (meterType) {
            case "ND": {
                cl_gb.setVisibility(View.GONE);
                cl_spinner.setVisibility(View.VISIBLE);
                cl_paper_pasted.setVisibility(View.GONE);
                cl_meter_stuck.setVisibility(View.VISIBLE);
                cl_meter_height.setVisibility(View.GONE);
                cl_meter_height_spin.setVisibility(View.GONE);
                cl_meter_source_supply.setVisibility(View.GONE);
                cl_meter_seal_status.setVisibility(View.GONE);
                cl_obstacle_type.setVisibility(View.GONE);
                cl_meter_image.setVisibility(View.VISIBLE);
                cl_tester_photo.setVisibility(View.VISIBLE);
                tv_elt_no.setVisibility(View.VISIBLE);
                rg_elt.setVisibility(View.VISIBLE);
                tv_meter_type_status.setVisibility(View.VISIBLE);
                view5.setVisibility(View.VISIBLE);
                spinner_meter_type.setVisibility(View.VISIBLE);
                tv_unsafe_condition.setVisibility(View.GONE);
                rg_unsafe.setVisibility(View.GONE);
                tv_elt_no.setVisibility(View.GONE);
                rg_elt.setVisibility(View.GONE);


                break;
            }

            case "PP": {
                cl_gb.setVisibility(View.GONE);
                cl_spinner.setVisibility(View.VISIBLE);
                cl_paper_pasted.setVisibility(View.VISIBLE);
                cl_meter_stuck.setVisibility(View.GONE);
                cl_meter_height.setVisibility(View.GONE);
                cl_meter_height_spin.setVisibility(View.GONE);
                cl_meter_source_supply.setVisibility(View.GONE);
                cl_meter_seal_status.setVisibility(View.GONE);
                cl_obstacle_type.setVisibility(View.GONE);
                cl_meter_image.setVisibility(View.VISIBLE);
                cl_tester_photo.setVisibility(View.GONE);
                tv_elt_no.setVisibility(View.VISIBLE);
                rg_elt.setVisibility(View.VISIBLE);
                tv_elt_no.setVisibility(View.GONE);
                rg_elt.setVisibility(View.GONE);
                tv_meter_type_status.setVisibility(View.VISIBLE);
                view5.setVisibility(View.VISIBLE);
                spinner_meter_type.setVisibility(View.VISIBLE);
                tv_unsafe_condition.setVisibility(View.GONE);
                rg_unsafe.setVisibility(View.GONE);

                tv_elt_no.setVisibility(View.GONE);
                rg_elt.setVisibility(View.GONE);
                tv_title_image.setText("Paper Shield Image");
                break;
            }

            case "GB": {
                cl_gb.setVisibility(View.VISIBLE);
                cl_spinner.setVisibility(View.GONE);
                cl_paper_pasted.setVisibility(View.GONE);
                cl_meter_stuck.setVisibility(View.GONE);
                cl_meter_height.setVisibility(View.GONE);
                cl_meter_height_spin.setVisibility(View.GONE);
                cl_meter_source_supply.setVisibility(View.GONE);
                cl_meter_seal_status.setVisibility(View.GONE);
                cl_obstacle_type.setVisibility(View.VISIBLE);
                cl_meter_image.setVisibility(View.VISIBLE);
                cl_tester_photo.setVisibility(View.GONE);
                tv_elt_no.setVisibility(View.VISIBLE);
                rg_elt.setVisibility(View.VISIBLE);
                tv_meter_type_status.setVisibility(View.VISIBLE);
                view5.setVisibility(View.VISIBLE);
                spinner_meter_type.setVisibility(View.VISIBLE);
                tv_unsafe_condition.setVisibility(View.GONE);
                rg_unsafe.setVisibility(View.GONE);
                tv_elt_no.setVisibility(View.GONE);
                rg_elt.setVisibility(View.GONE);

                break;
            }
            case "MH": {
                cl_gb.setVisibility(View.GONE);
                cl_spinner.setVisibility(View.VISIBLE);
                cl_paper_pasted.setVisibility(View.GONE);
                cl_meter_stuck.setVisibility(View.GONE);
                cl_meter_height.setVisibility(View.VISIBLE);
                cl_meter_height_spin.setVisibility(View.VISIBLE);
                cl_meter_source_supply.setVisibility(View.GONE);
                cl_meter_seal_status.setVisibility(View.GONE);
                cl_obstacle_type.setVisibility(View.GONE);
                cl_meter_image.setVisibility(View.VISIBLE);
                cl_tester_photo.setVisibility(View.GONE);
                tv_elt_no.setVisibility(View.GONE);
                rg_elt.setVisibility(View.GONE);
                tv_meter_type_status.setVisibility(View.VISIBLE);
                view5.setVisibility(View.VISIBLE);
                spinner_meter_type.setVisibility(View.VISIBLE);
                tv_unsafe_condition.setVisibility(View.VISIBLE);
                rg_unsafe.setVisibility(View.VISIBLE);
                break;
            }
            case "OB": {
                cl_gb.setVisibility(View.GONE);
                cl_spinner.setVisibility(View.VISIBLE);
                cl_paper_pasted.setVisibility(View.GONE);
                cl_meter_stuck.setVisibility(View.GONE);
                cl_meter_height.setVisibility(View.VISIBLE);
                cl_meter_height_spin.setVisibility(View.GONE);
                cl_meter_source_supply.setVisibility(View.GONE);
                cl_meter_seal_status.setVisibility(View.GONE);
                cl_obstacle_type.setVisibility(View.VISIBLE);
                cl_meter_image.setVisibility(View.VISIBLE);
                cl_tester_photo.setVisibility(View.GONE);
                tv_meter_type_status.setVisibility(View.GONE);
                view5.setVisibility(View.GONE);
                spinner_meter_type.setVisibility(View.GONE);
                tv_elt_no.setVisibility(View.GONE);
                rg_elt.setVisibility(View.GONE);
                tv_unsafe_condition.setVisibility(View.VISIBLE);
                rg_unsafe.setVisibility(View.VISIBLE);


                break;
            }
            case "MU": {
                cl_gb.setVisibility(View.VISIBLE);
                cl_spinner.setVisibility(View.VISIBLE);
                cl_paper_pasted.setVisibility(View.GONE);
                cl_meter_stuck.setVisibility(View.GONE);
                cl_meter_height.setVisibility(View.VISIBLE);
                cl_meter_height_spin.setVisibility(View.GONE);
                cl_meter_source_supply.setVisibility(View.VISIBLE);
                cl_meter_seal_status.setVisibility(View.GONE);
                cl_obstacle_type.setVisibility(View.GONE);
                cl_meter_image.setVisibility(View.VISIBLE);
                cl_tester_photo.setVisibility(View.GONE);
                tv_elt_no.setVisibility(View.GONE);
                rg_elt.setVisibility(View.GONE);
                tv_meter_type_status.setVisibility(View.VISIBLE);
                view5.setVisibility(View.VISIBLE);
                spinner_meter_type.setVisibility(View.VISIBLE);
                tv_unsafe_condition.setVisibility(View.VISIBLE);
                rg_unsafe.setVisibility(View.VISIBLE);


                break;
            }
            case "SB": {
                cl_gb.setVisibility(View.VISIBLE);
                cl_spinner.setVisibility(View.VISIBLE);
                cl_paper_pasted.setVisibility(View.GONE);
                cl_meter_stuck.setVisibility(View.GONE);
                cl_meter_height.setVisibility(View.VISIBLE);
                cl_meter_height_spin.setVisibility(View.GONE);
                cl_meter_source_supply.setVisibility(View.GONE);
                cl_meter_seal_status.setVisibility(View.VISIBLE);
                cl_obstacle_type.setVisibility(View.GONE);
                cl_meter_image.setVisibility(View.VISIBLE);
                cl_tester_photo.setVisibility(View.GONE);
                tv_elt_no.setVisibility(View.VISIBLE);
                rg_elt.setVisibility(View.VISIBLE);
                tv_meter_type_status.setVisibility(View.VISIBLE);
                view5.setVisibility(View.VISIBLE);
                spinner_meter_type.setVisibility(View.VISIBLE);
                tv_unsafe_condition.setVisibility(View.GONE);
                rg_unsafe.setVisibility(View.GONE);
                tv_elt_no.setVisibility(View.GONE);
                rg_elt.setVisibility(View.GONE);

                break;
            }
        }


        new AsyncTaskExampleUI().execute();
        new AsyncTaskExample().execute();


    }

    @Override
    public void onClick(View view) {
        if (view == iv_image__tester) {
            checkPermissions();
            meterCapture = false;
            testerCapture = true;
        }

        if (view == iv_image_cap) {
            checkPermissions();
            meterCapture = true;
            testerCapture = false;
        }
        if (view == btn_submit) {
            switch (meterType) {
                case "ND": {
                    meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
                    meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
                    meterOkNonSbmReadingModel.setIsMeterlocation(isMeterlocation);
                    meterOkNonSbmReadingModel.setSt_supplyStatus(st_supplyStatus);
                    meterOkNonSbmReadingModel.setSt_usages(st_usages);
                    meterOkNonSbmReadingModel.setSt_additional(et_add_info.getText().toString());
                    meterOkNonSbmReadingModel.setIsRevisit(IS_REVISIT);
                    meterOkNonSbmReadingModel.setIsELTON(isELTON);
                    meterOkNonSbmReadingModel.setSt_mtrtype(st_mtrType);
                    meterOkNonSbmReadingModel.setResonMeterStuck(st_meterStuck);
                    meterOkNonSbmReadingModel.setMr_reason(MeterType);
                    if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                        if (meterPhoto) {
                            new AsyncTaskExampleUpdate().execute();
                        } else {
                            Toast.makeText(NDPPGBMHOBMUSBRemarksActivity.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        new AsyncTaskExampleUpdate().execute();
                    }

                    break;
                }
                case "PP": {
                    meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
                    meterOkNonSbmReadingModel.setIsELTON(isELTON);
                    meterOkNonSbmReadingModel.setSt_supplyStatus(st_supplyStatus);
                    meterOkNonSbmReadingModel.setSt_usages(st_usages);
                    meterOkNonSbmReadingModel.setSt_mtrtype(st_mtrType);
                    meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
                    meterOkNonSbmReadingModel.setIsMeterlocation(isMeterlocation);
                    meterOkNonSbmReadingModel.setSt_additional(et_add_info.getText().toString());
                    meterOkNonSbmReadingModel.setIsRevisit(IS_REVISIT);
                    meterOkNonSbmReadingModel.setMr_reason(MeterType);
                    meterOkNonSbmReadingModel.setIsPaperBill(et_add_info.getText().toString());
                    if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                        if (meterPhoto) {
                            new AsyncTaskExampleUpdate().execute();
                        } else {
                            Toast.makeText(NDPPGBMHOBMUSBRemarksActivity.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        new AsyncTaskExampleUpdate().execute();
                    }

                    break;
                }
                case "MH": {
                    meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
                    meterOkNonSbmReadingModel.setSt_supplyStatus(st_supplyStatus);
                    meterOkNonSbmReadingModel.setSt_usages(st_usages);
                    meterOkNonSbmReadingModel.setSt_mtrtype(st_mtrType);
                    meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
                    meterOkNonSbmReadingModel.setIsMeterlocation(isMeterlocation);
                    meterOkNonSbmReadingModel.setSt_additional(et_add_info.getText().toString());
                    meterOkNonSbmReadingModel.setIsRevisit(IS_REVISIT);
                    meterOkNonSbmReadingModel.setMeterHeight(st_mtrHeight);
                    meterOkNonSbmReadingModel.setMr_reason(MeterType);
                    meterOkNonSbmReadingModel.setUnsafeCondition(isMeterUnsafe);
                    if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                        if (meterPhoto) {
                            new AsyncTaskExampleUpdate().execute();
                        } else {
                            Toast.makeText(NDPPGBMHOBMUSBRemarksActivity.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        new AsyncTaskExampleUpdate().execute();
                    }


                    break;

                }
                case "OB": {
                    meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
                    meterOkNonSbmReadingModel.setSt_supplyStatus(st_supplyStatus);
                    meterOkNonSbmReadingModel.setSt_usages(st_usages);
                    meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
                    meterOkNonSbmReadingModel.setIsMeterlocation(isMeterlocation);
                    meterOkNonSbmReadingModel.setSt_additional(et_add_info.getText().toString());
                    meterOkNonSbmReadingModel.setIsRevisit(IS_REVISIT);
                    meterOkNonSbmReadingModel.setMr_reason(MeterType);
                    meterOkNonSbmReadingModel.setUnsafeCondition(isMeterUnsafe);
                    meterOkNonSbmReadingModel.setTypeOfObstacle(st_meterObstacle);
                    if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                        if (UtilsClass.CAPTURE_IMAGE_PATH == 1) {
                            new AsyncTaskExampleUpdate().execute();
                        } else {
                            Toast.makeText(NDPPGBMHOBMUSBRemarksActivity.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        new AsyncTaskExampleUpdate().execute();
                    }
                    break;
                }
                case "SB": {
                    meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
                    meterOkNonSbmReadingModel.setIsELTON(isELTON);
                    meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
                    meterOkNonSbmReadingModel.setSt_supplyStatus(st_supplyStatus);
                    meterOkNonSbmReadingModel.setSt_usages(st_usages);
                    meterOkNonSbmReadingModel.setSt_mtrtype(meterType);
                    meterOkNonSbmReadingModel.setSt_additional(et_add_info.getText().toString().trim());
                    meterOkNonSbmReadingModel.setIsMeterlocation(isMeterlocation);
                    meterOkNonSbmReadingModel.setResonSealStatus(st_meterSeal);
                    meterOkNonSbmReadingModel.setMr_reason(MeterType);
                    meterOkNonSbmReadingModel.setIsRevisit(IS_REVISIT);
                    Map<String, String> values = new LinkedHashMap<>();
                    values.clear();
                    for (int i = 0; i < nbsmDatamodel.getListRegisterCoe().size(); i++) {
                        String tag = ed[i].getTag().toString() + "|" + nbsmDatamodel.getMETER_NO();
                        String val = ed[i].getText().toString().trim();

                        Log.d("Value ", ed[i].getTag().toString());
                        Log.d("Value ", ed[i].getText().toString().trim());
                        values.put(tag, val);

                        if (ed[i].getTag().toString().equalsIgnoreCase("CKWH")) {
                            if ((ed[i].getText().toString().trim().length() == 0) || ((ed[i].getText().toString().trim().isEmpty()))) {
                                blankSubmit = true;
                            }
                        }

                        meterOkNonSbmReadingModel.setLinkedHashMapValues(values);
                        ///String update = "update TBL_SPOTBILL_CHILD_DETAILS set PRESENT_METER_READING=" + val + " where REGISTER_CODE=" + tag + " and INSTALLATION='1233212'";
                        //Log.d("TestQuery", update);
                    }

                    if (blankSubmit) {
                        Toast.makeText(NDPPGBMHOBMUSBRemarksActivity.this, "Please enter reading", Toast.LENGTH_SHORT).show();
                        blankSubmit = false;
                    } else {
                        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                            if (UtilsClass.CAPTURE_IMAGE_PATH == 1) {
                                Intent i = new Intent(mContext, ViewCapturedDataActivity.class);
                                i.putExtra(Constant.PASS_DATA, meterOkNonSbmReadingModel);
                                i.putExtra(Constant.METER_TYPE, MeterType);
                                i.putExtra(Constant.VARCOND, varCond);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(NDPPGBMHOBMUSBRemarksActivity.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Intent i = new Intent(mContext, ViewCapturedDataActivity.class);
                            i.putExtra(Constant.PASS_DATA, meterOkNonSbmReadingModel);
                            i.putExtra(Constant.METER_TYPE, MeterType);
                            i.putExtra(Constant.VARCOND, varCond);
                            startActivity(i);
                            finish();
                        }

                        break;
                    }
                }

                case "GB": {
                    meterOkNonSbmReadingModel = new MeterOkNonSbmReadingModel();
                    //meterOkNonSbmReadingModel.setSt_kwh((st_kwh == null || st_kwh.equals("")) ? "0" : (st_kwh));
                    //meterOkNonSbmReadingModel.setSt_kvah((st_kvah == null || st_kvah.equals("")) ? "0" : (st_kvah));
                    // meterOkNonSbmReadingModel.setSt_kvarh((st_kvarh == null || st_kvarh.equals("")) ? "0" : (st_kvarh));
                    meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
                    meterOkNonSbmReadingModel.setIsELTON(isELTON);
                    meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
                    meterOkNonSbmReadingModel.setSt_supplyStatus(st_supplyStatus);
                    meterOkNonSbmReadingModel.setSt_usages(st_usages);
                    meterOkNonSbmReadingModel.setSt_mtrtype(st_mtrType);
                    meterOkNonSbmReadingModel.setSt_additional(et_add_info.getText().toString().trim());
                    meterOkNonSbmReadingModel.setIsMeterlocation(isMeterlocation);
                    meterOkNonSbmReadingModel.setMr_reason(MeterType);
                    meterOkNonSbmReadingModel.setIsRevisit(IS_REVISIT);

                    Map<String, String> values = new LinkedHashMap<>();
                    values.clear();
                    for (int i = 0; i < nbsmDatamodel.getListRegisterCoe().size(); i++) {
                        String tag = ed[i].getTag().toString() + "|" + nbsmDatamodel.getMETER_NO();
                        String val = ed[i].getText().toString().trim();

                        Log.d("Value ", ed[i].getTag().toString());
                        Log.d("Value ", ed[i].getText().toString().trim());
                        values.put(tag, val);
                        if (ed[i].getTag().toString().equalsIgnoreCase("CKWH")) {
                            if ((ed[i].getText().toString().trim().length() == 0) || ((ed[i].getText().toString().trim().isEmpty()))) {
                                blankSubmit = true;
                            }
                        }
                        meterOkNonSbmReadingModel.setLinkedHashMapValues(values);
                    }

                    if (blankSubmit) {
                        Toast.makeText(NDPPGBMHOBMUSBRemarksActivity.this, "Please enter reading", Toast.LENGTH_SHORT).show();
                        blankSubmit = false;
                    } else {
                        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                            if (UtilsClass.CAPTURE_IMAGE_PATH == 1) {
                                Intent i = new Intent(mContext, ViewCapturedDataActivity.class);
                                i.putExtra(Constant.PASS_DATA, meterOkNonSbmReadingModel);
                                i.putExtra(Constant.METER_TYPE, MeterType);
                                i.putExtra(Constant.VARCOND, varCond);
                                startActivity(i);
                                finish();

                            } else {
                                Toast.makeText(NDPPGBMHOBMUSBRemarksActivity.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Intent i = new Intent(mContext, ViewCapturedDataActivity.class);
                            i.putExtra(Constant.PASS_DATA, meterOkNonSbmReadingModel);
                            i.putExtra(Constant.METER_TYPE, MeterType);
                            i.putExtra(Constant.VARCOND, varCond);
                            startActivity(i);
                            finish();
                        }

                    }

                }

                case "MU": {
                    meterOkNonSbmReadingModel = new MeterOkNonSbmReadingModel();
                    //meterOkNonSbmReadingModel.setSt_kwh((st_kwh == null || st_kwh.equals("")) ? "0" : (st_kwh));
                    //meterOkNonSbmReadingModel.setSt_kvah((st_kvah == null || st_kvah.equals("")) ? "0" : (st_kvah));
                    // meterOkNonSbmReadingModel.setSt_kvarh((st_kvarh == null || st_kvarh.equals("")) ? "0" : (st_kvarh));
                    meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
                    meterOkNonSbmReadingModel.setIsELTON(isELTON);
                    meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
                    meterOkNonSbmReadingModel.setSt_supplyStatus(st_supplyStatus);
                    meterOkNonSbmReadingModel.setSt_usages(st_usages);
                    meterOkNonSbmReadingModel.setSt_mtrtype(st_mtrType);
                    meterOkNonSbmReadingModel.setSt_additional(et_add_info.getText().toString().trim());
                    meterOkNonSbmReadingModel.setIsMeterlocation(isMeterlocation);
                    meterOkNonSbmReadingModel.setSourceOfSupply(sourceOfSupply);
                    meterOkNonSbmReadingModel.setUnsafeCondition(isMeterUnsafe);
                    meterOkNonSbmReadingModel.setIsRevisit(IS_REVISIT);
                    meterOkNonSbmReadingModel.setMr_reason(MeterType);
                    Map<String, String> values = new LinkedHashMap<>();
                    values.clear();
                    for (int i = 0; i < nbsmDatamodel.getListRegisterCoe().size(); i++) {
                        String tag = ed[i].getTag().toString() + "|" + nbsmDatamodel.getMETER_NO();
                        String val = ed[i].getText().toString().trim();

                        Log.d("Value ", ed[i].getTag().toString());
                        Log.d("Value ", ed[i].getText().toString().trim());
                        values.put(tag, val);


                        if (ed[i].getTag().toString().equalsIgnoreCase("CKWH")) {
                            if ((ed[i].getText().toString().trim().length() == 0) || ((ed[i].getText().toString().trim().isEmpty()))) {
                                blankSubmit = true;
                            }
                        }

                        meterOkNonSbmReadingModel.setLinkedHashMapValues(values);
                        ///String update = "update TBL_SPOTBILL_CHILD_DETAILS set PRESENT_METER_READING=" + val + " where REGISTER_CODE=" + tag + " and INSTALLATION='1233212'";
                        //Log.d("TestQuery", update);
                    }
                    if (blankSubmit) {
                        Toast.makeText(NDPPGBMHOBMUSBRemarksActivity.this, "Please enter reading", Toast.LENGTH_SHORT).show();
                        blankSubmit = false;
                    } else {
                        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                            if (UtilsClass.CAPTURE_IMAGE_PATH == 1) {
                                Intent i = new Intent(mContext, ViewCapturedDataActivity.class);
                                i.putExtra(Constant.PASS_DATA, meterOkNonSbmReadingModel);
                                i.putExtra(Constant.METER_TYPE, MeterType);
                                i.putExtra(Constant.VARCOND, varCond);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(NDPPGBMHOBMUSBRemarksActivity.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Intent i = new Intent(mContext, ViewCapturedDataActivity.class);
                            i.putExtra(Constant.PASS_DATA, meterOkNonSbmReadingModel);
                            i.putExtra(Constant.METER_TYPE, MeterType);
                            i.putExtra(Constant.VARCOND, varCond);
                            startActivity(i);
                            finish();
                        }

                    }
                }
            }
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        meterType = intent.getStringExtra("meter_type");
    }

    @Override
    public void meterTypeSpinnerSelectedItem(int position, String value, String abbValue) {
        st_mtrType = String.valueOf(position);
        //UtilsClass.showToastShort(mContext,st_mtrType);
    }

    @Override
    public void supplyStatusSpinnerSelectedItem(int position, String value, String abbValue) {
        st_supplyStatus = String.valueOf(position);

    }

    @Override
    public void usageSpinnerSelectedItem(int position, String value, String abbValue) {
        st_usages = String.valueOf(position);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void meterObstacleSpinnerSelectedItem(int position, String value, String sortedValue) {
        st_meterObstacle = String.valueOf(position);
    }

    @Override
    public void meterSealSpinnerSelectedItem(int position, String value, String abbValue) {
        st_meterSeal = String.valueOf(position);
    }

    @Override
    public void meterStuckSpinnerSelectedItem(int position, String value, String abbValue) {
        st_meterStuck = String.valueOf(position);
    }

    @Override
    public void sourceSupplySpinnerSelectedItem(int position, String value, String abbValue) {
        sourceOfSupply = String.valueOf(position);
    }

    @Override
    public void paperPasteSpinnerSelectedItem(int position, String value, String abbValue) {
        st_paperPaste = String.valueOf(position);
    }

    @Override
    public void meterHeightSpinnerSelectedItem(int position, String value, String sortedValue) {
        st_mtrHeight = String.valueOf(position);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(NDPPGBMHOBMUSBRemarksActivity.this);
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
            } else if (testerCapture) {
                picUriTester = data != null ? data.getData() : null;
                iv_image__tester.setImageURI(picUriTester);
                filePath = picUriTester.getPath();
                testerCapture = true;
                testerPhoto = true;
            }


            File file = new File(filePath);
            UtilsClass.CAPTURE_IMAGE_PATH = 1;

            String filename = file.getName();
            Log.d("cdf", "onActivityResult: " + filename);

            //     String base64String = AppUtils.readFileAsBase64String(filePath);

            //      AppUtils.UploadImageTask myTask = new AppUtils.UploadImageTask(this);

            //      myTask.execute(utils.getUserID() + "" + UtilsClass.getDateFolder() + "" +/et_kwh.getText().toString().trim()/"", base64String);


            //    Log.d("ds", "onActivityResult: " + base64String);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.RESULT_ERROR, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }


    private class AsyncTaskExampleUpdate extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (meterType.equalsIgnoreCase("ND")) {
                db.updateNDRemarksNonSbmReading(meterOkNonSbmReadingModel, nbsmDatamodel.getINSTALLATION());
                db.updateMrReasonChild(nbsmDatamodel.getINSTALLATION(), "ND");
            }

            if (meterType.equalsIgnoreCase("PP")) {
                db.updatePPRemarksNonSbmReading(meterOkNonSbmReadingModel, nbsmDatamodel.getINSTALLATION());
                db.updateMrReasonChild(nbsmDatamodel.getINSTALLATION(), "PP");
            }
            if (meterType.equalsIgnoreCase("GB")) {
                // db.updatePPRemarksNonSbmReading(meterOkNonSbmReadingModel, nbsmDatamodel.getINSTALLATION());
            }
            if (meterType.equalsIgnoreCase("MH")) {
                db.updateMHRemarksNonSbmReading(meterOkNonSbmReadingModel, nbsmDatamodel.getINSTALLATION());
                db.updateMrReasonChild(nbsmDatamodel.getINSTALLATION(), "MH");

            }
            if (meterType.equalsIgnoreCase("OB")) {
                db.updateOBRemarksNonSbmReading(meterOkNonSbmReadingModel, nbsmDatamodel.getINSTALLATION());
                db.updateMrReasonChild(nbsmDatamodel.getINSTALLATION(), "OB");
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                try {
                    calculateBill.CalculateBill(installationno, meterType, mContext, 0);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                UpdateNONSBMData();
            }


            if (meterType.equalsIgnoreCase("ND")) {
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
            if (meterType.equalsIgnoreCase("PP")) {
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
            if (meterType.equalsIgnoreCase("GB")) {
                // db.updatePPRemarksNonSbmReading(meterOkNonSbmReadingModel, nbsmDatamodel.getINSTALLATION());
            }
            if (meterType.equalsIgnoreCase("MH")) {
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
            if (meterType.equalsIgnoreCase("OB")) {
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
        DatabaseHelper db = new DatabaseHelper(mContext);
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
                "  WHERE A.INSTALLATION=B.INSTALLATION AND A.INSTALLATION='" + installationno + "' " +
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

        String SbmblNo = installationno.substring(4) + strCurDt.substring(2);
        String updateQueryNonSbm = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                "PRESENT_METER_STATUS= '" + prs_mtrcond + "'," +
                "PRESENT_READING_TIME= '" + strMrTime + "'," +
                "OSBILL_DATE= '" + strBillDate + "'," +
                "PRESENT_READING_DATE= '" + lastdayofprvmonth + "'," +
                "READ_FLAG = 1 ," +
                "SBM_BILL_NO= '" + SbmblNo + "'," +
                "PRESENT_READING_REMARK= '" + MeterType + "'," +
                "MRREASON='" + MeterType + "'" +
                " WHERE INSTALLATION='" + installationno + "'";


        Log.e("updateQueryNonSbm", updateQueryNonSbm);
        db.updateMTRCONTD(updateQueryNonSbm);
    }

    private class AsyncTaskExample extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
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
                if ((MeterType.equalsIgnoreCase("GB")) || (MeterType.equalsIgnoreCase("SB")) || (MeterType.equalsIgnoreCase("MU"))) {
                    fillData();
                }
            } else {
                UtilsClass.showToastLong(NDPPGBMHOBMUSBRemarksActivity.this, getString(R.string.msg_nodata));
                finish();
            }
            pDialog.dismiss();
        }
    }

    private void fillData() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );

        ed = new EditText[nbsmDatamodel.getListRegisterCoe().size()];
        tv = new EditText[nbsmDatamodel.getListRegisterCoe().size()];
        for (int i = 0; i < nbsmDatamodel.getListRegisterCoe().size(); i++) {
            ed[i] = new EditText(this);
            tv[i] = new EditText(this);
            params.setMargins(5, 10, 0, 10);
            ed[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_corner_gray_frame_white_solid));
            //tv[i].setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corner_gray_frame_white_solid));
            tv[i].setBackground(null);
            ed[i].setHint(nbsmDatamodel.getListRegisterCoe().get(i) + " : " + nbsmDatamodel.getListMeterNo().get(i));
            tv[i].setText(nbsmDatamodel.getListMeterNo().get(i) + " :-");
            tv[i].setTextColor(getResources().getColor(R.color.color_loginbg_n));
            //tv[i].setTypeface(null, Typeface.BOLD);
            tv[i].setEnabled(false);
            ed[i].setHeight(100);
            tv[i].setHeight(100);

            //non-sbm 8,2
            //sbm -7,2
            if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                if (nbsmDatamodel.getListRegisterCoe().get(i).equalsIgnoreCase("CKWH")) {
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



            ed[i].setLayoutParams(params);
            ed[i].setSingleLine();
            ed[i].setPadding(10, 5, 5, 20);
            ed[i].setId(i + 1);
            ed[i].setTag(nbsmDatamodel.getListRegisterCoe().get(i) + "|" + nbsmDatamodel.getListMeterNo().get(i));


            tv[i].setLayoutParams(params);
            tv[i].setSingleLine();
            tv[i].setPadding(10, 5, 5, 20);
            tv[i].setId(i + 1);
            tv[i].setTag(nbsmDatamodel.getListRegisterCoe().get(i) + "|" + nbsmDatamodel.getListMeterNo().get(i));
            llContainer.addView(ed[i]);
            llSubContainerText.addView(tv[i]);
        }
    }

    public void uploadData() {
        if (AppUtils.isInternetAvailable(this)) {
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
                            try {
                                utils.setAppVersion(object.softwareVersionNo.toString().trim());
                            } catch (Exception e) {

                            }
                            //showErrorDialog("Success", object.message);
                            UtilsClass.showToastLong(mContext, object.message);
                            /*Intent intent = new Intent(mContext, BillingCalculation.class);
                            intent.putExtra("installationno", nbsmDatamodel.getINSTALLATION());
                            intent.putExtra("MR_REASON", MeterType);
                            startActivity(intent);*/
                            RedirectToPage();
                        } else if (object.statusCode == 410) {
                            /*UtilsClass.showToastLong(mContext, object.message);
                            db.updatesendFlag(utils.getSerchCondition());
                            dUtils.dismissDialog();
                            RedirectToPage();*/
                            try {
                                utils.setAppVersion(object.softwareVersionNo.toString().trim());
                            } catch (Exception e) {

                            }
                            db.updatesendFlag(utils.getSerchCondition());
                            dUtils.dismissDialog();
                            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                            alertDialog.setTitle("Message");
                            alertDialog.setMessage(object.message);
                            alertDialog.setCancelable(false);
                            alertDialog.setCanceledOnTouchOutside(false);
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
                        e.printStackTrace();
                        showErrorDialog("Error", getString(R.string.something_wrong));
                        /*Intent intent = new Intent(mContext, BillingCalculation.class);
                        intent.putExtra("installationno", nbsmDatamodel.getINSTALLATION());
                        intent.putExtra("MR_REASON", meterType);
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
            RedirectToPage();
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void RedirectToPage() {
        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            Intent intent = new Intent(mContext, BillingCalculation.class);
            intent.putExtra("installationno", nbsmDatamodel.getINSTALLATION());
            intent.putExtra("MR_REASON", meterType);
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

    private NSBMData fetchNSBMdata() {
        nbsmDatamodel = db.getNBSMData(varCond, -1);
        return nbsmDatamodel;
    }

    private class AsyncTaskExampleUI extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch (MeterType) {
                case "ND": {
                    tv_title.setText(getResources().getString(R.string.nd_remarks_string));
                    ArrayAdapter statusAdapterMB = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.supplyStatus));
                    statusAdapterMB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_status.setAdapter(statusAdapterMB);
                    spinner_status.setOnItemSelectedListener(new AppUtils.StatusSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    ArrayAdapter meterAdapter = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.usage));
                    meterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_usage.setAdapter(meterAdapter);
                    spinner_usage.setOnItemSelectedListener(new AppUtils.UsageSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    ArrayAdapter sourceAdapterMb = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterType));
                    sourceAdapterMb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_meter_type.setAdapter(sourceAdapterMb);
                    spinner_meter_type.setOnItemSelectedListener(new AppUtils.MeterTypeSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    //  spinner_meter_type.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter meterStuck = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterstuck));
                    meterStuck.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_meter_stuck.setAdapter(meterStuck);
                    spinner_meter_stuck.setOnItemSelectedListener(new AppUtils.MeterStuckSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_meter_stuck.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    break;
                }
                case "PP": {
                    tv_title.setText(getResources().getString(R.string.pp_remarks));
                    ArrayAdapter statusAdapterMB = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.supplyStatus));
                    statusAdapterMB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_status.setAdapter(statusAdapterMB);
                    //spinner_status.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    spinner_status.setOnItemSelectedListener(new AppUtils.StatusSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    ArrayAdapter meterAdapter = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.usage));
                    meterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_usage.setAdapter(meterAdapter);
                    spinner_usage.setOnItemSelectedListener(new AppUtils.UsageSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    //spinner_usage.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter sourceAdapterMb = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterType));
                    sourceAdapterMb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_meter_type.setAdapter(sourceAdapterMb);
                    spinner_meter_type.setOnItemSelectedListener(new AppUtils.MeterTypeSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_meter_type.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter meterPaperPaste = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.paperpastedby));
                    meterPaperPaste.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_paper_paste.setAdapter(meterPaperPaste);
                    spinner_paper_paste.setOnItemSelectedListener(new AppUtils.PaperPasteSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    //spinner_paper_paste.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    break;
                }
                case "MH": {
                    tv_title.setText(getResources().getString(R.string.mh_remarks_string));
                    ArrayAdapter statusAdapterMB = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.supplyStatus));
                    statusAdapterMB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_status.setAdapter(statusAdapterMB);
                    spinner_status.setOnItemSelectedListener(new AppUtils.StatusSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_status.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter meterAdapter = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.usage));
                    meterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_usage.setAdapter(meterAdapter);
                    spinner_usage.setOnItemSelectedListener(new AppUtils.UsageSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    //spinner_usage.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter meterHeight = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterheightfeet));
                    meterHeight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_meter_height.setAdapter(meterHeight);
                    spinner_meter_height.setOnItemSelectedListener(new AppUtils.MeterHeightSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_meter_height.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter sourceAdapterMb = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterType));
                    sourceAdapterMb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_meter_type.setAdapter(sourceAdapterMb);
                    spinner_meter_type.setOnItemSelectedListener(new AppUtils.MeterTypeSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_meter_type.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    break;
                }
                case "OB": {
                    tv_title.setText(getResources().getString(R.string.ob_remarks_string));
                    ArrayAdapter statusAdapterMB = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.supplyStatus));
                    statusAdapterMB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_status.setAdapter(statusAdapterMB);
                    spinner_status.setOnItemSelectedListener(new AppUtils.StatusSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_status.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter meterAdapter = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.usage));
                    meterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_usage.setAdapter(meterAdapter);
                    spinner_usage.setOnItemSelectedListener(new AppUtils.UsageSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_usage.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter meterObstacle = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.typeofobstacle));
                    meterObstacle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_type_obstacle.setAdapter(meterObstacle);
                    spinner_type_obstacle.setOnItemSelectedListener(new AppUtils.MeterObstacleSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_type_obstacle.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    break;
                }
                case "SB": {
                    tv_title.setText(getResources().getString(R.string.sb_remarks));
                    ArrayAdapter statusAdapterMB = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.supplyStatus));
                    statusAdapterMB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_status.setAdapter(statusAdapterMB);
                    spinner_status.setOnItemSelectedListener(new AppUtils.StatusSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_status.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter meterAdapter = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.usage));
                    meterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_usage.setAdapter(meterAdapter);
                    spinner_usage.setOnItemSelectedListener(new AppUtils.UsageSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    //  spinner_usage.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter sourceAdapterMb = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterType));
                    sourceAdapterMb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_meter_type.setAdapter(sourceAdapterMb);
                    spinner_meter_type.setOnItemSelectedListener(new AppUtils.MeterTypeSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_meter_type.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter meterSeal = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sealstatus));
                    meterSeal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_meter_seal_status.setAdapter(meterSeal);
                    spinner_meter_seal_status.setOnItemSelectedListener(new AppUtils.MeterSealSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    //   spinner_meter_seal_status.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    break;
                }
                case "GB": {
                    tv_title.setText(getResources().getString(R.string.gb_remarks));
                    ArrayAdapter statusAdapterMB = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.supplyStatus));
                    statusAdapterMB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_status.setAdapter(statusAdapterMB);
                    spinner_status.setOnItemSelectedListener(new AppUtils.StatusSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    //spinner_status.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter meterAdapter = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.usage));
                    meterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_usage.setAdapter(meterAdapter);
                    spinner_usage.setOnItemSelectedListener(new AppUtils.UsageSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_usage.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter sourceAdapterMb = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterType));
                    sourceAdapterMb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_meter_type.setAdapter(sourceAdapterMb);
                    spinner_meter_type.setOnItemSelectedListener(new AppUtils.MeterTypeSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    // spinner_meter_type.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    break;
                }
                case "MU": {
                    tv_title.setText(getResources().getString(R.string.mu_remarks));
                    ArrayAdapter statusAdapterMB = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.supplyStatus));
                    statusAdapterMB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_status.setAdapter(statusAdapterMB);
                    spinner_status.setOnItemSelectedListener(new AppUtils.StatusSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    //   spinner_status.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter meterAdapter = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.usage));
                    meterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_usage.setAdapter(meterAdapter);
                    spinner_usage.setOnItemSelectedListener(new AppUtils.UsageSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    //  spinner_usage.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter sourceAdapterMb = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterType));
                    sourceAdapterMb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_meter_type.setAdapter(sourceAdapterMb);
                    spinner_meter_type.setOnItemSelectedListener(new AppUtils.MeterTypeSpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    //   spinner_meter_type.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    ArrayAdapter sourceSupply = new ArrayAdapter(NDPPGBMHOBMUSBRemarksActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sourceofsupply));
                    sourceSupply.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_meter_source_supply.setAdapter(sourceSupply);
                    spinner_meter_source_supply.setOnItemSelectedListener(new AppUtils.SourceSupplySpinnerClass(mContext, NDPPGBMHOBMUSBRemarksActivity.this));
                    //  spinner_meter_source_supply.setOnItemSelectedListener(NDPPGBMHOBMUSBRemarksActivity.this);
                    break;
                }
            }
        }
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
        Log.e("latitude", "" + meterOkNonSbmReadingModel.getGpsLatitude());
        Log.e("longitude", "" + meterOkNonSbmReadingModel.getGpsLongitude());
    }

}
