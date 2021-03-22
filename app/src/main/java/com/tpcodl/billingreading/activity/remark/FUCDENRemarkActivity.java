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

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.BillingCalculation;
import com.tpcodl.billingreading.activity.DecimalDigitsInputFilter;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.activity.calculateBill;
import com.tpcodl.billingreading.activity.viewcaptureddata.ViewCapturedDataActivity;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.listeners.MeterTypeSpinnerCallback;
import com.tpcodl.billingreading.listeners.ReasonOfCdSpinnerCallback;
import com.tpcodl.billingreading.listeners.ReasonOfENSpinnerCallback;
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

public class FUCDENRemarkActivity extends AppCompatActivity implements View.OnClickListener, MeterTypeSpinnerCallback, SupplyStatusSpinnerCallback, UsageSpinnerCallback, ReasonOfENSpinnerCallback, ReasonOfCdSpinnerCallback {

    private String meterType = "";
    private Spinner spinner_status_cd;
    private Spinner spinner_usage_cd;
    private Spinner spinner_meter_type_cd;
    private Spinner spinner_reason_cd;
    private Spinner spinner_reason_en;
    private ConstraintLayout cl_fu;
    private ConstraintLayout cl_cd;
    private ConstraintLayout en_spinner;
    private ConstraintLayout cl_spinner;
    private ConstraintLayout cd_spinner;
    private TextView tv_meter_type_status_cd;
    private View view5_cd;
    private TextView tv_sequence_cd;
    private ImageView iv_back;
    private TextView tv_title;
    private ImageView iv_image_cap;
    private ActivityUtils utils;
    private RadioGroup rg_elt;
    private RadioGroup rg_sequence_cd;
    private RadioGroup rg_meter_location_cd;

    private RadioButton rb_elt_yes;
    private RadioButton rb_elt_no;
    private RadioButton rb_seq_yes;
    private RadioButton rb_seq_no;
    private RadioButton rb_met_loc_yes;
    private RadioButton rb_met_loc_no;
    private Button btn_submit;
    NSBMData nbsmDatamodel;
    MeterOkNonSbmReadingModel meterOkNonSbmReadingModel;
    String isMeterlocation = "";
    String isELTON = "";
    String isSeqCorrect = "";
    DatabaseHelper db;
    private boolean blankSubmit;
    TextView tv_readings;

    String SelChoice, SelAcntno, varCond, sourceOfSupply;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private Uri picUri;
    private String supplyStatusSpinner;
    private String usageSpinner;
    private String reasonENSpinner;
    private Context mContext;
    private String IS_REVISIT;
    private EditText et_add_info;
    private LinearLayout llContainer;
    EditText ed[];
    private String st_mtrtype;
    private DialogUtils dUtils;
    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    private RequestModel model;
    UploadUtils uUtils;
    private PreferenceHandler phandler;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    String installationno;
    EditText tv[];
    LinearLayout llSubContainerText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fucden_remark);
        mContext = this;

        SelChoice = getIntent().getStringExtra(Constant.SEL_CHOICE);
        SelAcntno = getIntent().getStringExtra(Constant.DETAILS_NO);
        meterType = getIntent().getStringExtra(Constant.METER_TYPE);
        IS_REVISIT = getIntent().getStringExtra(Constant.IS_REVISIT);
        installationno = getIntent().getStringExtra(Constant.INSTALLATIONNO);
        phandler = new PreferenceHandler(this);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        initView();

        model = new RequestModel();
        utils = ActivityUtils.getInstance();
        dUtils = new DialogUtils(mContext);
        db = new DatabaseHelper(mContext);
        varCond = AppUtils.getQueryCondition(mContext, SelChoice, SelAcntno, installationno);
        meterOkNonSbmReadingModel = new MeterOkNonSbmReadingModel();

        getCoordinates();
        new AsyncTaskExample().execute();
    }

    private void initView() {
        Log.e("PageName", "FUCDENRemarkActivity");
        spinner_status_cd = findViewById(R.id.spinner_status_cd);
        llSubContainerText = findViewById(R.id.llSubContainerText);
        spinner_usage_cd = findViewById(R.id.spinner_usage_cd);
        spinner_meter_type_cd = findViewById(R.id.spinner_meter_type_cd);
        spinner_reason_cd = findViewById(R.id.spinner_reason_cd);
        spinner_reason_en = findViewById(R.id.spinner_reason_en);
        cl_fu = findViewById(R.id.cl_fu);
        cl_cd = findViewById(R.id.cl_cd);
        en_spinner = findViewById(R.id.en_spinner);
        cl_spinner = findViewById(R.id.cl_spinner);
        cd_spinner = findViewById(R.id.cd_spinner);
        tv_meter_type_status_cd = findViewById(R.id.tv_meter_type_status_cd);
        view5_cd = findViewById(R.id.view5_cd);
        tv_sequence_cd = findViewById(R.id.tv_sequence_cd);
        rg_sequence_cd = findViewById(R.id.rg_sequence_cd);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        iv_image_cap = findViewById(R.id.iv_image_cap);
        rg_elt = findViewById(R.id.rg_elt);
        rb_elt_yes = findViewById(R.id.rb_elt_yes);
        rb_elt_no = findViewById(R.id.rb_elt_no);
        rb_seq_yes = findViewById(R.id.rb_seq_yes);
        rb_seq_no = findViewById(R.id.rb_seq_no);
        rb_met_loc_yes = findViewById(R.id.rb_met_loc_yes);
        rb_met_loc_no = findViewById(R.id.rb_met_loc_no);
        btn_submit = findViewById(R.id.btn_submit);
        et_add_info = findViewById(R.id.et_add_info);
        llContainer = findViewById(R.id.llContainer);
        rg_meter_location_cd = findViewById(R.id.rg_meter_location_cd);
        tv_readings = findViewById(R.id.tv_readings);
        iv_image_cap.setOnClickListener(this);
        utils = ActivityUtils.getInstance();
        iv_back.setOnClickListener(this);
        iv_back.setOnClickListener(view -> finish());
        btn_submit.setOnClickListener(this);

        if (meterType.equalsIgnoreCase("FU")) {
            tv_readings.setVisibility(View.GONE);
            tv_title.setText(getResources().getString(R.string.fu_remarks_string));
            cl_fu.setVisibility(View.VISIBLE);
            cl_cd.setVisibility(View.VISIBLE);
            cl_spinner.setVisibility(View.GONE);
            tv_meter_type_status_cd.setVisibility(View.VISIBLE);
            spinner_meter_type_cd.setVisibility(View.VISIBLE);
            view5_cd.setVisibility(View.VISIBLE);
            tv_sequence_cd.setVisibility(View.VISIBLE);
            rg_sequence_cd.setVisibility(View.VISIBLE);
        } else if (meterType.equalsIgnoreCase("EN")) {
            tv_title.setText(getResources().getString(R.string.en_remarks_string));
            cl_fu.setVisibility(View.GONE);
            cl_cd.setVisibility(View.VISIBLE);
            cl_spinner.setVisibility(View.VISIBLE);
            cd_spinner.setVisibility(View.GONE);
            en_spinner.setVisibility(View.VISIBLE);
            tv_meter_type_status_cd.setVisibility(View.GONE);
            spinner_meter_type_cd.setVisibility(View.GONE);
            view5_cd.setVisibility(View.GONE);
            tv_sequence_cd.setVisibility(View.GONE);
            rg_sequence_cd.setVisibility(View.GONE);
        } else if (meterType.equalsIgnoreCase("CD")) {
            tv_title.setText(getResources().getString(R.string.cd_remarks));

            cl_fu.setVisibility(View.GONE);
            cl_cd.setVisibility(View.VISIBLE);
            cl_spinner.setVisibility(View.VISIBLE);
            cd_spinner.setVisibility(View.VISIBLE);
            en_spinner.setVisibility(View.GONE);
            tv_meter_type_status_cd.setVisibility(View.GONE);
            spinner_meter_type_cd.setVisibility(View.GONE);
            view5_cd.setVisibility(View.GONE);
            tv_sequence_cd.setVisibility(View.GONE);
            rg_sequence_cd.setVisibility(View.GONE);

        }


        rg_elt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_elt_yes:
                        isELTON = "Y";
                        break;
                    case R.id.rb_elt_no:
                        isELTON = "N";
                        break;
                }
            }
        });

        rg_sequence_cd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

        rg_meter_location_cd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_met_loc_yes:
                        isMeterlocation = "I";
                        break;
                    case R.id.rb_met_loc_no:
                        isMeterlocation = "O";
                        break;
                }
            }
        });

        new AsyncTaskExampleInit().execute();

        if (meterType.equalsIgnoreCase("FU")) {
            cl_fu.setVisibility(View.VISIBLE);
            cl_cd.setVisibility(View.VISIBLE);
            cl_spinner.setVisibility(View.GONE);

            tv_meter_type_status_cd.setVisibility(View.VISIBLE);
            spinner_meter_type_cd.setVisibility(View.VISIBLE);
            view5_cd.setVisibility(View.VISIBLE);
            tv_sequence_cd.setVisibility(View.VISIBLE);
            rg_sequence_cd.setVisibility(View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.fu_remarks_string));
        } else if (meterType.equalsIgnoreCase("EN")) {
            cl_fu.setVisibility(View.GONE);
            cl_cd.setVisibility(View.VISIBLE);
            cl_spinner.setVisibility(View.VISIBLE);
            cd_spinner.setVisibility(View.GONE);
            en_spinner.setVisibility(View.VISIBLE);
            tv_meter_type_status_cd.setVisibility(View.GONE);
            spinner_meter_type_cd.setVisibility(View.GONE);
            view5_cd.setVisibility(View.GONE);
            tv_sequence_cd.setVisibility(View.GONE);
            rg_sequence_cd.setVisibility(View.GONE);
            tv_title.setText(getResources().getString(R.string.en_remarks_string));
        } else if (meterType.equalsIgnoreCase("CD")) {
            cl_fu.setVisibility(View.GONE);
            cl_cd.setVisibility(View.VISIBLE);
            cl_spinner.setVisibility(View.VISIBLE);
            cd_spinner.setVisibility(View.VISIBLE);
            en_spinner.setVisibility(View.GONE);
            tv_meter_type_status_cd.setVisibility(View.GONE);
            spinner_meter_type_cd.setVisibility(View.GONE);
            view5_cd.setVisibility(View.GONE);
            tv_sequence_cd.setVisibility(View.GONE);
            rg_sequence_cd.setVisibility(View.GONE);
            tv_title.setText(getResources().getString(R.string.cd_remarks));
        }


    }


    @Override
    public void meterTypeSpinnerSelectedItem(int position, String value, String abbValue) {

        Log.d("csh", "meterTypeSpinnerSelectedItem: " + position + value);
        st_mtrtype = String.valueOf(position);
    }

    @Override
    public void supplyStatusSpinnerSelectedItem(int position, String value, String abbValue) {
        Log.d("csh", "meterTypeSpinnerSelectedItem: " + position + value);

        supplyStatusSpinner = String.valueOf(position);

    }

    @Override
    public void usageSpinnerSelectedItem(int position, String value, String abbValue) {
        Log.d("csh", "meterTypeSpinnerSelectedItem: " + position + value);
        usageSpinner = String.valueOf(position);
    }

    @Override
    public void reasonOfCdSpinnerSelectedItem(int position, String value, String abbValue) {
        Log.d("csh", "meterTypeSpinnerSelectedItem: " + position + value);

    }

    @Override
    public void reasonOfENSpinnerSelectedItem(int position, String value, String abbValue) {
        Log.d("csh", "meterTypeSpinnerSelectedItem: " + position + value);
        reasonENSpinner = String.valueOf(position);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view == iv_image_cap) {
            checkPermissions();
        } else if (view == btn_submit) {
            if (meterType.equalsIgnoreCase("EN")) {
                meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
                meterOkNonSbmReadingModel.setIsMeterlocation(isMeterlocation);
                meterOkNonSbmReadingModel.setSt_supplyStatus(supplyStatusSpinner);
                meterOkNonSbmReadingModel.setSt_usages(usageSpinner);
                meterOkNonSbmReadingModel.setSt_additional(et_add_info.getText().toString().trim());
                meterOkNonSbmReadingModel.setIsRevisit(IS_REVISIT);
                meterOkNonSbmReadingModel.setReasonEN(reasonENSpinner);
                meterOkNonSbmReadingModel.setMr_reason(meterType);

                if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                    if (UtilsClass.CAPTURE_IMAGE_PATH == 1) {
                        new AsyncTaskExampleUpdate().execute();
                    } else {
                        Toast.makeText(FUCDENRemarkActivity.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new AsyncTaskExampleUpdate().execute();
                }

            } else if (meterType.equalsIgnoreCase("FU")) {
                meterOkNonSbmReadingModel = new MeterOkNonSbmReadingModel();

                //meterOkNonSbmReadingModel.setSt_kwh((st_kwh == null || st_kwh.equals("")) ? "0" : (st_kwh));
                //meterOkNonSbmReadingModel.setSt_kvah((st_kvah == null || st_kvah.equals("")) ? "0" : (st_kvah));
                // meterOkNonSbmReadingModel.setSt_kvarh((st_kvarh == null || st_kvarh.equals("")) ? "0" : (st_kvarh));
                meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
                meterOkNonSbmReadingModel.setIsELTON(isELTON);
                meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
                meterOkNonSbmReadingModel.setSt_supplyStatus(supplyStatusSpinner);
                meterOkNonSbmReadingModel.setSt_usages(usageSpinner);
                meterOkNonSbmReadingModel.setSt_mtrtype(st_mtrtype);
                meterOkNonSbmReadingModel.setSt_additional(et_add_info.getText().toString().trim());
                meterOkNonSbmReadingModel.setIsMeterlocation(isMeterlocation);
                meterOkNonSbmReadingModel.setIsRevisit(IS_REVISIT);
                meterOkNonSbmReadingModel.setMr_reason(meterType);
                Map<String, String> values = new LinkedHashMap<>();
                values.clear();
                /*for (int i = 0; i < nbsmDatamodel.getListRegisterCoe().size(); i++) {
                    String tag = ed[i].getTag().toString();
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
                }*/
                /*
               if (blankSubmit) {
                    Toast.makeText(FUCDENRemarkActivity.this, "Please enter reading", Toast.LENGTH_SHORT).show();
                    blankSubmit = false;
                } else {
                    if (UtilsClass.CAPTURE_IMAGE_PATH == 1) {
                        Intent i = new Intent(mContext, ViewCapturedDataActivity.class);
                        i.putExtra(Constant.PASS_DATA, meterOkNonSbmReadingModel);
                        i.putExtra(Constant.METER_TYPE, meterType);
                        i.putExtra(Constant.VARCOND, varCond);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(FUCDENRemarkActivity.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                    }
                }*/

                if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                    if (UtilsClass.CAPTURE_IMAGE_PATH == 1) {
                        new AsyncTaskExampleUpdate().execute();
                    } else {
                        Toast.makeText(FUCDENRemarkActivity.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new AsyncTaskExampleUpdate().execute();
                }


            }
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(FUCDENRemarkActivity.this);
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

    private void pickPicture(final int requestCode) {
        ImagePicker.Companion.with(this)
                .crop()
                .cameraOnly()
                //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            picUri = data != null ? data.getData() : null;
            iv_image_cap.setImageURI(picUri);


            String filePath = picUri.getPath();
            UtilsClass.CAPTURE_IMAGE_PATH = 1;

            File file = new File(filePath);
            String filename = file.getName();
            Log.d("cdf", "onActivityResult: " + filename);

            //   String base64String = AppUtils.readFileAsBase64String(filePath);

            //  AppUtils.UploadImageTask myTask = new AppUtils.UploadImageTask(this);

            //   myTask.execute(utils.getUserID() + "_" + UtilsClass.getDateFolder() + "_" +/*et_kwh.getText().toString().trim()*/"", base64String);


            //    Log.d("ds", "onActivityResult: " + base64String);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.RESULT_ERROR, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }

    private NSBMData fetchNSBMdata() {
        nbsmDatamodel = db.getNBSMData(varCond, -1);
        return nbsmDatamodel;
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
                if (meterType.equalsIgnoreCase("FU")) {
                    //fillData();
                }
            } else {
                UtilsClass.showToastLong(mContext, getString(R.string.msg_nodata));
                finish();
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
            if (meterType.equalsIgnoreCase("EN")) {
                db.updateENRemarksNonSbmReading(meterOkNonSbmReadingModel, nbsmDatamodel.getINSTALLATION());
                db.updateMrReasonChild(nbsmDatamodel.getINSTALLATION(), "EN");
            }
            if (meterType.equalsIgnoreCase("FU")) {
                db.updateFURemarksNonSbmReading(meterOkNonSbmReadingModel, nbsmDatamodel.getINSTALLATION());
                db.updateMrReasonChild(nbsmDatamodel.getINSTALLATION(), "FU");
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            UtilsClass.showToastShort(mContext, getString(R.string.msg_datasaved));
            if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                try {
                    calculateBill.CalculateBill(installationno, meterType, mContext, 0);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                UpdateNONSBMData();
                //  db.updateNONSBMData();
            }

            if (meterType.equalsIgnoreCase("EN")) {
                uUtils = new UploadUtils(mContext);
                uUtils.getHeaderdetails(varCond);
                model = uUtils.getChilddetails(varCond);
                /*uploadData();*/
                if (AppUtils.checkArrersAndUpload(mContext, installationno) == 0) {
                    uploadData();
                } else {
                    UtilsClass.showToastLong(mContext, getString(R.string.arrers_msg));
                    RedirectToPage();
                }
            }
            if (meterType.equalsIgnoreCase("FU")) {
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
                "PRESENT_READING_REMARK= '" + meterType + "'," +
                "MRREASON='" + meterType + "'" +
                " WHERE INSTALLATION='" + installationno + "'";
        Log.e("updateQueryNonSbm", updateQueryNonSbm);
        db.updateMTRCONTD(updateQueryNonSbm);
    }

    private void fillData() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );


        /*for (int i = 0; i < nbsmDatamodel.getListRegisterCoe().size(); i++) {
            ed[i] = new EditText(this);
            params.setMargins(0, 10, 0, 10);
            ed[i].setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_corner_gray_frame_white_solid));
            ed[i].setHint("Enter " + nbsmDatamodel.getListRegisterCoe().get(i) + " : " + nbsmDatamodel.getListMeterNo().get(i));
            ed[i].setHeight(100);
            ed[i].setLayoutParams(params);
            ed[i].setSingleLine();
            if (nbsmDatamodel.getListRegisterCoe().get(i).equalsIgnoreCase("CKWH")) {
                ed[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            }else{
                ed[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
            ed[i].setPadding(20, 30, 20, 30);
            ed[i].setId(i + 1);
            ed[i].setTag(nbsmDatamodel.getListRegisterCoe().get(i) + "|" + nbsmDatamodel.getListMeterNo().get(i));

            llContainer.addView(ed[i]);
        }*/
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

                            // phandler.updateLogindata();
                            //showErrorDialog("Success", object.message);
                            UtilsClass.showToastLong(mContext, object.message);
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
                        showErrorDialog("Error", getString(R.string.something_wrong));
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

    private class AsyncTaskExampleInit extends AsyncTask<Void, Void, Void> {

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
            ArrayAdapter meterAdapter = new ArrayAdapter(FUCDENRemarkActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.usage));
            meterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_usage_cd.setAdapter(meterAdapter);
            spinner_usage_cd.setOnItemSelectedListener(new AppUtils.UsageSpinnerClass(FUCDENRemarkActivity.this, FUCDENRemarkActivity.this));


            ArrayAdapter sourceAdapterMb = new ArrayAdapter(FUCDENRemarkActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterType));
            sourceAdapterMb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_meter_type_cd.setAdapter(sourceAdapterMb);
            spinner_meter_type_cd.setOnItemSelectedListener(new AppUtils.MeterTypeSpinnerClass(FUCDENRemarkActivity.this, FUCDENRemarkActivity.this));

            ArrayAdapter statusAdapterMB = new ArrayAdapter(FUCDENRemarkActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.supplyStatus));
            statusAdapterMB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_status_cd.setAdapter(statusAdapterMB);
            spinner_status_cd.setOnItemSelectedListener(new AppUtils.StatusSpinnerClass(FUCDENRemarkActivity.this, FUCDENRemarkActivity.this));

            ArrayAdapter reasonCDAdapter = new ArrayAdapter(FUCDENRemarkActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.reasonofCD));
            reasonCDAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_reason_cd.setAdapter(reasonCDAdapter);
            spinner_reason_cd.setOnItemSelectedListener(new AppUtils.MeterReasonCDSpinnerClass(FUCDENRemarkActivity.this, FUCDENRemarkActivity.this));

            ArrayAdapter reasonEnAdapter = new ArrayAdapter(FUCDENRemarkActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.reasonofEN));
            reasonEnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_reason_en.setAdapter(reasonEnAdapter);
            spinner_reason_en.setOnItemSelectedListener(new AppUtils.MeterReasonENSpinnerClass(FUCDENRemarkActivity.this, FUCDENRemarkActivity.this));
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