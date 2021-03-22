package com.tpcodl.billingreading.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.viewcaptureddata.ViewCapturedDataActivity;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.reponseModel.EnablebillingResponse;
import com.tpcodl.billingreading.reponseModel.UploadDataResponseModel;
import com.tpcodl.billingreading.requestModel.UserEnableModel;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.DialogUtils;
import com.tpcodl.billingreading.utils.UtilsClass;
import com.tpcodl.billingreading.webservice.ApiInterface;
import com.tpcodl.billingreading.webservice.RetrofitClientInstance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetUpDashboardActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_lock, btn_printer_setup, btn_paper_roll_type, btn_update_app, btn_scan_device, btn_camera_setup, btn_get_gps;
    Button btn_get_localdb, btn_overridebilling;
    private DialogUtils dUtils;
    Context context;
    private ActivityUtils utils;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_dashboard);
        context = this;
        dUtils = new DialogUtils(context);
        utils = ActivityUtils.getInstance();
        initView();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        btn_overridebilling = findViewById(R.id.btn_overridebilling);
        btn_get_localdb = findViewById(R.id.btn_get_localdb);
        btn_lock = findViewById(R.id.btn_lock);
        btn_printer_setup = findViewById(R.id.btn_printer_setup);
        btn_paper_roll_type = findViewById(R.id.btn_paper_roll_type);
        btn_update_app = findViewById(R.id.btn_update_app);
        btn_scan_device = findViewById(R.id.btn_scan_device);
        btn_camera_setup = findViewById(R.id.btn_camera_setup);
        btn_get_gps = findViewById(R.id.btn_get_gps);

        btn_lock.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(this);
        btn_lock.setOnClickListener(this);
        btn_printer_setup.setOnClickListener(this);
        btn_paper_roll_type.setOnClickListener(this);
        btn_update_app.setOnClickListener(this);
        btn_scan_device.setOnClickListener(this);
        btn_camera_setup.setOnClickListener(this);
        btn_get_gps.setOnClickListener(this);
        btn_get_localdb.setOnClickListener(this);
        btn_overridebilling.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_lock: {
              /*  Intent i = new Intent(context, MassbillActivity.class);
                startActivity(i);*/
                break;
            }
            case R.id.btn_printer_setup: {
                Intent intent = new Intent(SetUpDashboardActivity.this, SetPrinterTypeActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.btn_paper_roll_type: {
                Intent intent = new Intent(SetUpDashboardActivity.this, SetPaperRollActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_update_app: {
                break;
            }
            case R.id.btn_scan_device: {
                Intent intent = new Intent(SetUpDashboardActivity.this, BTPrinterSetupActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_camera_setup: {
                Intent intent = new Intent(SetUpDashboardActivity.this, SetUpCameraActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_get_gps: {
                Intent intent = new Intent(SetUpDashboardActivity.this, GPSTrackingActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_get_localdb: {
                new ExportDatabaseFileTask().execute();
                break;
            }
            case R.id.btn_overridebilling: {
                uploadData();
                break;
            }
            case R.id.iv_back: {
                finish();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }


    private class ExportDatabaseFileTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(context);

        // can use UI thread here
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {
            File dbFile = new File(Environment.getDataDirectory() + "/data/com.tpcodl.billingreading/databases/SMRD.db");
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");

            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyy_hh:mm:ss");
            String format = s.format(new Date());

            String localDBName = "SMRD_" + utils.getUserID() + "_" + format + ".db";
            File file = new File(exportDir, localDBName.trim());

            try {
                file.createNewFile();
                this.copyFile(dbFile, file);
                return true;
            } catch (IOException e) {
                Log.e("mypck", e.getMessage(), e);
                return false;
            }
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            if (success) {
                Toast.makeText(context, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show();
            }
        }

        void copyFile(File src, File dst) throws IOException {
            FileChannel inChannel = new FileInputStream(src).getChannel();
            FileChannel outChannel = new FileOutputStream(dst).getChannel();
            try {
                inChannel.transferTo(0, inChannel.size(), outChannel);
            } finally {
                if (inChannel != null)
                    inChannel.close();
                if (outChannel != null)
                    outChannel.close();
            }
        }
    }

    public void uploadData() {
        if (AppUtils.isInternetAvailable(this)) {
            UserEnableModel u = new UserEnableModel();
            u.setUserId(PreferenceHandler.getisUserId(context));
            //Gson g = new Gson();
           // Log.e("GSON", g.toJson(u));

            dUtils.showDialog("Uploading", "Please wait..");
            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
            Call<EnablebillingResponse> call = service.enabledisableUser(utils.getAuthToken(), u);
           /* Gson g1 = new Gson();
            Log.e("UPLOADJSON123456", g1.toJson(u));*/

            call.enqueue(new Callback<EnablebillingResponse>() {

                @Override
                public void onResponse(Call<EnablebillingResponse> call, Response<EnablebillingResponse> response) {
                    EnablebillingResponse object = response.body();
                    dUtils.dismissDialog();
                    try {
                        if (object.getStatusCode() == 200) {
                            UtilsClass.showToastLong(context, object.getMessage());
                            if (object.getEnabledBillFlag().equalsIgnoreCase("0")) {
                                PreferenceHandler.setEnableBilling(context, "1");
                            } else {
                                UtilsClass.showToastShort(context, "You are not allowed to bill for now!!");
                                PreferenceHandler.setEnableBilling(context, "");
                            }

                            try {
                                utils.setAppVersion(object.getSoftwareVersionNo().toString().trim());
                            } catch (Exception e) {
                            }
                        } else if (object.getStatusCode() == 404) {
                            UtilsClass.showToastLong(context, object.getMessage());
                            try {
                                utils.setAppVersion(object.getSoftwareVersionNo().toString().trim());
                            } catch (Exception e) {
                            }
                        } else {
                            showErrorDialog("Error", object.getSoftwareVersionNo());
                        }
                    } catch (Exception e) {
                        showErrorDialog("Error", "Something went wrong!!");
                    }
                }

                @Override
                public void onFailure(Call<EnablebillingResponse> call, Throwable t) {
                    dUtils.dismissDialog();
                    t.printStackTrace();
                    Log.i("TAG", t.getMessage());
                    Toast.makeText(context, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            dUtils.dismissDialog();
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
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
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}