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
import android.widget.AdapterView;
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
import com.tpcodl.billingreading.activity.DecimalDigitsInputFilter;
import com.tpcodl.billingreading.activity.viewcaptureddata.ViewCapturedDataActivity;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.listeners.MeterTypeSpinnerCallback;
import com.tpcodl.billingreading.listeners.SupplyStatusSpinnerCallback;
import com.tpcodl.billingreading.listeners.UsageSpinnerCallback;
import com.tpcodl.billingreading.models.MeterOkNonSbmReadingModel;
import com.tpcodl.billingreading.models.NSBMData;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.Constant;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MeterOKMMRemarkActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, MeterTypeSpinnerCallback, SupplyStatusSpinnerCallback, UsageSpinnerCallback {
    private Spinner spinner_status;
    private Spinner spinner_usage;
    private Spinner spinner_meter_type;
    private ConstraintLayout cl_new_meter;
    private ConstraintLayout cl_ok;
    private ImageView iv_back;
    private TextView tv_title;
    private ActivityUtils utils;
    private ImageView iv_image_cap;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private Uri picUri;
    MeterOkNonSbmReadingModel meterOkNonSbmReadingModel;
    DatabaseHelper db;
    NSBMData nbsmDatamodel;
    String IS_REVISIT;

    String isELTON = "";
    String isSeqCorrect = "";
    String isMeterlocation = "";
    String st_supplyStatus;
    String st_usages;
    String st_mtrtype;
    String st_additional;
    String isPaperBill = "";
    String isNewMeter = "NEW";
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    private boolean blankSubmit = false;

    LinearLayout llContainer;
    EditText ed[];
    private Context context;
    String SelChoice, SelAcntno, varCond, MeterType;
    private RadioGroup rg_new_meter, rg_elt, rg_seq, rg_meter_location, rg_paper_stop;

    private RadioButton rg_meter_new_yes, rg_meter_new_no, rb_paper_stop_yes, rb_paper_stop_no, rb_meter_no, rb_meter_yes, rb_seq_no, rb_seq_yes;
    private EditText et_meter_number;
    private Button btn_submit;
    String installationno;

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    EditText tv[];
    LinearLayout llSubContainerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_ok_mm_remark);
        context = this;
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        db = new DatabaseHelper(context);
        SelChoice = getIntent().getStringExtra(Constant.SEL_CHOICE);
        SelAcntno = getIntent().getStringExtra(Constant.DETAILS_NO);
        MeterType = getIntent().getStringExtra(Constant.METER_TYPE);
        IS_REVISIT = getIntent().getStringExtra(Constant.IS_REVISIT);
        installationno = getIntent().getStringExtra(Constant.INSTALLATIONNO);
        meterOkNonSbmReadingModel = new MeterOkNonSbmReadingModel();

        initView();
        varCond = AppUtils.getQueryCondition(context, SelChoice, SelAcntno, installationno);

        Log.e("Vaaaaaaaa", varCond);

        getCoordinates();
        new AsyncTaskExample().execute();
    }

    private void initView() {
        Log.e("PageName", "MeterOKMMRemarkActivity");
        spinner_status = findViewById(R.id.spinner_status);
        llSubContainerText = findViewById(R.id.llSubContainerText);
        spinner_usage = findViewById(R.id.spinner_usage);
        spinner_meter_type = findViewById(R.id.spinner_meter_type);
        cl_new_meter = findViewById(R.id.cl_new_meter);
        cl_ok = findViewById(R.id.cl_ok);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        iv_image_cap = findViewById(R.id.iv_image_cap);
        rg_new_meter = findViewById(R.id.rg_new_meter);
        rg_elt = findViewById(R.id.rg_elt);
        rg_seq = findViewById(R.id.rg_seq);
        rg_meter_location = findViewById(R.id.rg_meter_location);
        rg_paper_stop = findViewById(R.id.rg_paper_stop);
        rg_meter_new_yes = findViewById(R.id.rg_meter_new_yes);
        rg_meter_new_no = findViewById(R.id.rg_meter_new_no);
        rb_paper_stop_yes = findViewById(R.id.rb_paper_stop_yes);
        rb_paper_stop_no = findViewById(R.id.rb_paper_stop_no);
        rb_meter_no = findViewById(R.id.rb_meter_no);
        rb_meter_yes = findViewById(R.id.rb_meter_yes);
        rb_seq_no = findViewById(R.id.rb_seq_no);
        rb_seq_yes = findViewById(R.id.rb_seq_yes);
        et_meter_number = findViewById(R.id.et_meter_number);
        btn_submit = findViewById(R.id.btn_submit);
        llContainer = findViewById(R.id.llContainer);

        btn_submit.setOnClickListener(this);

        iv_image_cap.setOnClickListener(this);

        utils = ActivityUtils.getInstance();
        //  UtilsClass.showToastShort(context,MeterType);
        if (MeterType.equalsIgnoreCase("OK")) {
            cl_new_meter.setVisibility(View.GONE);
            cl_ok.setVisibility(View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.ok_meter_string));
        } else {
            cl_new_meter.setVisibility(View.VISIBLE);
            cl_ok.setVisibility(View.GONE);
            tv_title.setText(getResources().getString(R.string.bl_remarks_stringmm_remark));
        }

        iv_back.setOnClickListener(this);
        iv_back.setOnClickListener(view -> finish());


        ArrayAdapter<String> usages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.usage));
        usages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_usage.setAdapter(usages);
        spinner_usage.setOnItemSelectedListener(new AppUtils.UsageSpinnerClass(MeterOKMMRemarkActivity.this, this));

        ArrayAdapter<String> status = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.supplyStatus));
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_status.setAdapter(status);
        spinner_status.setOnItemSelectedListener(new AppUtils.StatusSpinnerClass(MeterOKMMRemarkActivity.this, this));

        ArrayAdapter<String> meterTypes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterType));
        meterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_meter_type.setAdapter(meterTypes);
        spinner_meter_type.setOnItemSelectedListener(new AppUtils.MeterTypeSpinnerClass(MeterOKMMRemarkActivity.this, this));


        rg_elt.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_elt_yes:
                    isELTON = "Y";
                    break;
                case R.id.rb_elt_no:
                    isELTON = "N";
                    break;
            }
        });

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

        rg_meter_location.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_met_inside:
                    isMeterlocation = "I";
                    break;
                case R.id.rb_met_outside:
                    isMeterlocation = "O";
                    break;
            }
        });

        rg_paper_stop.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_paper_stop_yes:
                    isPaperBill = "Y";
                    break;
                case R.id.rb_paper_stop_no:
                    isPaperBill = "N";
                    break;
            }
        });

        rg_new_meter.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.rg_meter_new_yes:
                    isNewMeter = "NEW";
                    break;
                case R.id.rg_meter_new_no:
                    isNewMeter = "OLD";
                    break;
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view == iv_image_cap) {
            checkPermissions();
        } else if (view == btn_submit) {
            if (MeterType.equalsIgnoreCase("MM")) {
                //meterOkNonSbmReadingModel.setSt_kwh((st_kwh == null || st_kwh.equals("")) ? "0" : (st_kwh));
                //meterOkNonSbmReadingModel.setSt_kvah((st_kvah == null || st_kvah.equals("")) ? "0" : (st_kvah));
                // meterOkNonSbmReadingModel.setSt_kvarh((st_kvarh == null || st_kvarh.equals("")) ? "0" : (st_kvarh));
                meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
                meterOkNonSbmReadingModel.setIsELTON(isELTON);
                meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
                meterOkNonSbmReadingModel.setSt_supplyStatus(st_supplyStatus);
                meterOkNonSbmReadingModel.setSt_usages(st_usages);
                meterOkNonSbmReadingModel.setSt_mtrtype(st_mtrtype);
                // meterOkNonSbmReadingModel.setSt_additional(st_additional);
                meterOkNonSbmReadingModel.setSt_newMeter(isNewMeter);
                meterOkNonSbmReadingModel.setMeterNumber(et_meter_number.getText().toString().trim());
                meterOkNonSbmReadingModel.setIsPaperBill(isPaperBill);

                if (isNewMeter.equalsIgnoreCase("NEW")) {
                    meterOkNonSbmReadingModel.setSt_newMeter(isNewMeter);
                } else {
                    meterOkNonSbmReadingModel.setSt_oldMeterCorrection(isNewMeter);
                }

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
                }

                if (blankSubmit) {
                    Toast.makeText(MeterOKMMRemarkActivity.this, "Please enter reading", Toast.LENGTH_SHORT).show();
                    blankSubmit = false;
                } else {
                    if (!(et_meter_number.getText().toString().trim().length() == 0) || (!(et_meter_number.getText().toString().isEmpty()))) {
                        if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                            if (UtilsClass.CAPTURE_IMAGE_PATH == 1) {
                                Intent i = new Intent(context, ViewCapturedDataActivity.class);
                                i.putExtra(Constant.PASS_DATA, meterOkNonSbmReadingModel);
                                i.putExtra(Constant.METER_TYPE, MeterType);
                                i.putExtra(Constant.VARCOND, varCond);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(MeterOKMMRemarkActivity.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Intent i = new Intent(context, ViewCapturedDataActivity.class);
                            i.putExtra(Constant.PASS_DATA, meterOkNonSbmReadingModel);
                            i.putExtra(Constant.METER_TYPE, MeterType);
                            i.putExtra(Constant.VARCOND, varCond);
                            startActivity(i);
                            finish();
                        }

                    } else {
                        Toast.makeText(context, "Please Enter Meter Number", Toast.LENGTH_SHORT).show();
                    }
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(MeterOKMMRemarkActivity.this);
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
            picUri = data != null ? data.getData() : null;
            iv_image_cap.setImageURI(picUri);
            filePath = picUri.getPath();


            File file = new File(filePath);
            String filename = file.getName();
            UtilsClass.CAPTURE_IMAGE_PATH = 1;
            String base64String = AppUtils.readFileAsBase64String(filePath);
            Log.d("cdf", "onActivityResult: " + filename);
            Map<String, String> listImages = new LinkedHashMap<>();
            listImages.clear();
            listImages.put(base64String, Constant.UPLOAD_IMAGE);
            meterOkNonSbmReadingModel.setLinkedHashMapImages(listImages);
            //String base64String = AppUtils.readFileAsBase64String(filePath);
            //AppUtils.UploadImageTask myTask = new AppUtils.UploadImageTask(this);
            //myTask.execute(utils.getUserID() + "_" + UtilsClass.getDateFolder() + "_" +/*et_kwh.getText().toString().trim()*/"", base64String);


            //  Log.d("ds", "onActivityResult: " + base64String);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.RESULT_ERROR, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void meterTypeSpinnerSelectedItem(int position, String value, String abbValue) {
        st_mtrtype = String.valueOf(position);
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

    private NSBMData fetchNSBMdata() {
        nbsmDatamodel = db.getNBSMData(varCond, -1);
        return nbsmDatamodel;
    }

    private void fillData() {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        /*ed = new EditText[nbsmDatamodel.getListRegisterCoe().size()];
        for (int i = 0; i < nbsmDatamodel.getListRegisterCoe().size(); i++) {
            ed[i] = new EditText(this);
            params.setMargins(20, 10, 20, 10);
            ed[i].setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corner_gray_frame_white_solid));
            ed[i].setHint("Enter " + nbsmDatamodel.getListRegisterCoe().get(i) + " : " + nbsmDatamodel.getListMeterNo().get(i));
            ed[i].setHeight(100);
            if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                if (nbsmDatamodel.getListRegisterCoe().get(i).equalsIgnoreCase("CKWH")) {
                    ed[i].setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    ed[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }
            } else {
                ed[i].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
            ed[i].setLayoutParams(params);
            ed[i].setSingleLine();
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
            ed[i].setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corner_gray_frame_white_solid));
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
            if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
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