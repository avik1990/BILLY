package com.tpcodl.billingreading.activity.remark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MeterOkNonSbmReading extends AppCompatActivity implements View.OnClickListener, MeterTypeSpinnerCallback, SupplyStatusSpinnerCallback, UsageSpinnerCallback {

    Context context;
    EditText et_kwh;
    EditText et_kvah;
    EditText et_kvarh;
    EditText et_md_peak;
    EditText et_md_off_peak;
    EditText et_tod;
    EditText et_colony_consumption;
    ImageView iv_upload;
    Button btn_submit;
    //String st_kwh, st_kvah, st_kvarh;
    String st_md_peak, st_md_off_peak;
    String st_tod, st_colony_consumption;
    MeterOkNonSbmReadingModel meterOkNonSbmReadingModel;
    DatabaseHelper db;
    String SelChoice, SelAcntno, varCond, MeterType;
    NSBMData nbsmDatamodel;
    private static final String TAG = "meterTypeSpinnerSelectedItem";
    private ImageView iv_image_cap;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private Uri picUri;
    String IS_REVISIT;
    private ActivityUtils utils;
    private Spinner spinner_status;
    private Spinner spinner_usage;
    private Spinner spinner_meter_type;

    RadioGroup rg_elt;
    RadioButton rb_elt_yes;
    RadioButton rb_elt_no;

    RadioGroup rg_seq;
    RadioButton rb_seq_yes;
    RadioButton rb_seq_no;
    RadioGroup rg_meter_location;
    RadioButton rb_met_inside;
    RadioButton rb_met_outside;
    EditText et_add_info;
    RadioGroup rg_meter_stop;
    RadioButton rb_ppr_bill_stop;
    RadioButton rb_ppr_bill_no;

    String isELTON = "";
    String isSeqCorrect = "";
    String isMeterlocation = "";
    String st_supplyStatus;
    String st_usages;
    String st_mtrtype;
    String st_additional;
    String isPaperBill = "";
    LinearLayout llContainer;
    EditText ed[];
    String installationno = "";
    TextView tv_title;
    boolean blankSubmit = false;
    private ImageView iv_back;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    LinearLayout llSubContainerText;
    EditText tv[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_ok_non_sbm_reading);
        context = this;
        SelChoice = getIntent().getStringExtra(Constant.SEL_CHOICE);
        SelAcntno = getIntent().getStringExtra(Constant.DETAILS_NO);
        MeterType = getIntent().getStringExtra(Constant.METER_TYPE);
        IS_REVISIT = getIntent().getStringExtra(Constant.IS_REVISIT);
        installationno = getIntent().getStringExtra(Constant.INSTALLATIONNO);
        db = new DatabaseHelper(context);

        varCond = AppUtils.getQueryCondition(context, SelChoice, SelAcntno, installationno);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        meterOkNonSbmReadingModel = new MeterOkNonSbmReadingModel();

        getCoordinates();
        new AsyncTaskExample().execute();

        initViews();
    }

    private void initViews() {
        Log.e("PageName", "MeterOkNonSbmReading");
        iv_back = findViewById(R.id.iv_back);
        llContainer = findViewById(R.id.llContainer);
        llSubContainerText = findViewById(R.id.llSubContainerText);
        et_kwh = findViewById(R.id.et_kwh);
        et_kvah = findViewById(R.id.et_kvah);
        et_kvarh = findViewById(R.id.et_kvarh);
        et_md_peak = findViewById(R.id.et_md_peak);
        et_md_off_peak = findViewById(R.id.et_md_off_peak);
        et_tod = findViewById(R.id.et_tod);
        et_colony_consumption = findViewById(R.id.et_colony_consumption);
        iv_upload = findViewById(R.id.iv_upload);
        btn_submit = findViewById(R.id.btn_submit);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.ok_meter_string));
        iv_image_cap = findViewById(R.id.iv_image_cap);
        spinner_status = findViewById(R.id.spinner_status);
        spinner_usage = findViewById(R.id.spinner_usage);
        spinner_meter_type = findViewById(R.id.spinner_meter_type);
        utils = ActivityUtils.getInstance();
        rg_elt = findViewById(R.id.rg_elt);
        rb_elt_yes = findViewById(R.id.rb_elt_yes);
        rb_elt_no = findViewById(R.id.rb_elt_no);
        rg_seq = findViewById(R.id.rg_seq);
        rb_seq_yes = findViewById(R.id.rb_seq_yes);
        rb_seq_no = findViewById(R.id.rb_seq_no);
        rg_meter_location = findViewById(R.id.rg_meter_location);
        rb_met_inside = findViewById(R.id.rb_met_inside);
        rb_met_outside = findViewById(R.id.rb_met_outside);
        et_add_info = findViewById(R.id.et_add_info);
        rg_meter_stop = findViewById(R.id.rg_meter_stop);
        rb_ppr_bill_stop = findViewById(R.id.rb_ppr_bill_stop);
        rb_ppr_bill_no = findViewById(R.id.rb_ppr_bill_no);
        iv_back.setOnClickListener(this);
        iv_back.setOnClickListener(view -> finish());
        iv_image_cap.setOnClickListener(this);

        ArrayAdapter<String> usages = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.usage));
        usages.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_usage.setAdapter(usages);
        spinner_usage.setOnItemSelectedListener(new AppUtils.UsageSpinnerClass(MeterOkNonSbmReading.this, this));

        ArrayAdapter<String> status = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.supplyStatus));
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_status.setAdapter(status);
        spinner_status.setOnItemSelectedListener(new AppUtils.StatusSpinnerClass(MeterOkNonSbmReading.this, this));

        ArrayAdapter<String> meterTypes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.meterType));
        meterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_meter_type.setAdapter(meterTypes);
        spinner_meter_type.setOnItemSelectedListener(new AppUtils.MeterTypeSpinnerClass(MeterOkNonSbmReading.this, this));

        btn_submit.setOnClickListener(this);


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

        rg_meter_stop.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_ppr_bill_stop:
                        isPaperBill = "Y";
                        break;
                    case R.id.rb_ppr_bill_no:
                        isPaperBill = "N";
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btn_submit) {
            //st_kwh = et_kwh.getText().toString().trim();
            //st_kvah = et_kvah.getText().toString().trim();
            //st_kvarh = et_kvarh.getText().toString().trim();
            st_md_peak = et_md_peak.getText().toString().trim();
            st_md_off_peak = et_md_off_peak.getText().toString().trim();
            st_tod = et_tod.getText().toString().trim();
            st_colony_consumption = et_colony_consumption.getText().toString().trim();
            st_additional = et_add_info.getText().toString().trim();


            //meterOkNonSbmReadingModel.setSt_kwh((st_kwh == null || st_kwh.equals("")) ? "0" : (st_kwh));
            //meterOkNonSbmReadingModel.setSt_kvah((st_kvah == null || st_kvah.equals("")) ? "0" : (st_kvah));
            // meterOkNonSbmReadingModel.setSt_kvarh((st_kvarh == null || st_kvarh.equals("")) ? "0" : (st_kvarh));
            //  meterOkNonSbmReadingModel.setSt_md_peak((st_md_peak == null || st_md_peak.equals("")) ? "0" : (st_md_peak));
            //  meterOkNonSbmReadingModel.setSt_md_off_peak((st_md_off_peak == null || st_md_off_peak.equals("")) ? "0" : (st_md_off_peak));
            //  meterOkNonSbmReadingModel.setSt_tod(st_tod);

            meterOkNonSbmReadingModel.setSt_currentdatetime(UtilsClass.getCurrentDate());
            meterOkNonSbmReadingModel.setSt_colony_consumption(st_colony_consumption);

            meterOkNonSbmReadingModel.setIsELTON(isELTON);
            meterOkNonSbmReadingModel.setIsSeqCorrect(isSeqCorrect);
            meterOkNonSbmReadingModel.setIsMeterlocation(isMeterlocation);
            meterOkNonSbmReadingModel.setSt_supplyStatus(st_supplyStatus);
            meterOkNonSbmReadingModel.setSt_usages(st_usages);
            meterOkNonSbmReadingModel.setSt_mtrtype(st_mtrtype);
            meterOkNonSbmReadingModel.setSt_additional(st_additional);
            meterOkNonSbmReadingModel.setIsPaperBill(isPaperBill);
            meterOkNonSbmReadingModel.setIsRevisit(IS_REVISIT);
            meterOkNonSbmReadingModel.setMr_reason(MeterType);

            Map<String, String> values = new LinkedHashMap<>();
            values.clear();
            for (int i = 0; i < nbsmDatamodel.getListRegisterCoe().size(); i++) {
                String tag = ed[i].getTag().toString();
                String val = ed[i].getText().toString().trim();

                Log.d("Value ", ed[i].getTag().toString());
                Log.d("Value ", ed[i].getText().toString().trim());
                values.put(tag, val);

                if (ed[i].getTag().toString().contains("CKWH")) {
                    if ((ed[i].getText().toString().trim().length() == 0) || ((ed[i].getText().toString().trim().isEmpty()))) {
                        blankSubmit = true;
                    }
                }

                meterOkNonSbmReadingModel.setLinkedHashMapValues(values);
                ///String update = "update TBL_SPOTBILL_CHILD_DETAILS set PRESENT_METER_READING=" + val + " where REGISTER_CODE=" + tag + " and INSTALLATION='1233212'";
                //Log.d("TestQuery", update);
            }

            if (blankSubmit) {
                Toast.makeText(MeterOkNonSbmReading.this, "Please enter reading", Toast.LENGTH_SHORT).show();
                blankSubmit = false;
            } else {
                if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                    if (UtilsClass.CAPTURE_IMAGE_PATH == 1) {
                        Intent i = new Intent(context, ViewCapturedDataActivity.class);
                        i.putExtra(Constant.PASS_DATA, meterOkNonSbmReadingModel);
                        i.putExtra(Constant.METER_TYPE, MeterType);
                        i.putExtra(Constant.VARCOND, varCond);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(MeterOkNonSbmReading.this, "Please capture image first.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent i = new Intent(context, ViewCapturedDataActivity.class);
                    i.putExtra(Constant.PASS_DATA, meterOkNonSbmReadingModel);
                    i.putExtra(Constant.METER_TYPE, MeterType);
                    i.putExtra(Constant.VARCOND, varCond);
                    startActivity(i);
                    finish();
                }

            }

        } else if (v == iv_image_cap) {
            checkPermissions();

        }
    }

    private NSBMData fetchNSBMdata() {
        nbsmDatamodel = db.getNBSMData(varCond, -1);
        return nbsmDatamodel;
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
       /* if (nbsmDatamodel.getPRS_KWH_FLG().equalsIgnoreCase("1")) {
            et_kwh.setVisibility(View.VISIBLE);
        }

        if (nbsmDatamodel.getPRS_KVAH_FLG().equalsIgnoreCase("1")) {
            et_kvah.setVisibility(View.VISIBLE);
        }

        if (nbsmDatamodel.getPRS_KVARH_FLG().equalsIgnoreCase("1")) {
            et_kvarh.setVisibility(View.VISIBLE);
        }

        if (nbsmDatamodel.getMD_PEAK_FLG().equalsIgnoreCase("1")) {
            et_md_peak.setVisibility(View.VISIBLE);
        }

        if (nbsmDatamodel.getMD_OFFPEAK_FLG().equalsIgnoreCase("1")) {
            et_md_off_peak.setVisibility(View.VISIBLE);
        }*/

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
            //tv[i].setTextSize(12);
            Log.e("NOIFDIGITS", nbsmDatamodel.getNO_OF_DIGITS());

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

    private void pickPicture(final int requestCode) {
        ImagePicker.Companion.with(this)
                .crop()
                .cameraOnly()//Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    protected void checkPermissions() {
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(MeterOkNonSbmReading.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            picUri = data != null ? data.getData() : null;
            iv_image_cap.setImageURI(picUri);
            String filePath = picUri.getPath();
            UtilsClass.CAPTURE_IMAGE_PATH = 1;
            String base64String = AppUtils.readFileAsBase64String(filePath);

            Map<String, String> listImages = new LinkedHashMap<>();
            listImages.clear();
            listImages.put(base64String, Constant.UPLOAD_IMAGE);
            meterOkNonSbmReadingModel.setLinkedHashMapImages(listImages);
            Log.d("ds", "onActivityResult: " + base64String);
            //AppUtils.UploadImageTask myTask = new AppUtils.UploadImageTask(this);
            // myTask.execute(utils.getUserID() + "_" + UtilsClass.getDateFolder() + "_" + et_kwh.getText().toString().trim(), base64String);
            // Log.d("ds", "onActivityResult: " + base64String);
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            //Toast.makeText(this, ImagePicker.RESULT_ERROR, Toast.LENGTH_SHORT).show();
        } else {
            //   Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
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

    }
}