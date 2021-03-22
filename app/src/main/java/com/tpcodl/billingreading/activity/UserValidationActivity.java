package com.tpcodl.billingreading.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;
import com.tpcodl.billingreading.BuildConfig;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.reponseModel.ValidationResponseModel;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.Constant;
import com.tpcodl.billingreading.utils.DialogUtils;
import com.tpcodl.billingreading.utils.UtilsClass;
import com.tpcodl.billingreading.webservice.ApiInterface;
import com.tpcodl.billingreading.webservice.RetrofitClientInstance;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserValidationActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button_validate;
    private EditText edit_mobile;
    private TextView tv_version;
    private PreferenceHandler pHanlder;
    private DialogUtils dutils;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 101;

    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;
    DatabaseHelper db;

    Context context;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ActivityUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_validation);

        context = this;
        button_validate = findViewById(R.id.button_validate);
        edit_mobile = findViewById(R.id.edit_mobile);
        button_validate.setOnClickListener(this);
        db = new DatabaseHelper(context);
        tv_version = findViewById(R.id.tv_version);
        tv_version.setText("App Version: " + getResources().getString(R.string.version));
        pHanlder = new PreferenceHandler(this);
        dutils = new DialogUtils(this);
        utils = ActivityUtils.getInstance();

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        try {
            db.createDataBase();
            Log.e("DatabaseHelper", "Created");
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        //  Log.e("EnableFLAGGGGGG", PreferenceHandler.getEnableBilling(context));

        checkPermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_validate: {
                validateData();
                break;
            }
            case R.id.iv_back: {
                finish();
                break;
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

                        // finish();
                        if (permissionStatus.getBoolean(REQUIRED_SDK_PERMISSIONS[0], true)) {
                            //Previously Permission Request was cancelled with 'Dont Ask Again',
                            // Redirect to Settings after showing Information about why you need the permission
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserValidationActivity.this);
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

                // pickPicture(REQUEST_CODE_FILE_UPLOAD);
                break;
        }
    }

    private void validateData() {
        if (edit_mobile.getText().toString().equalsIgnoreCase("")) {
            edit_mobile.requestFocus();
            edit_mobile.setError("Please enter the mobile number");
        } else if (edit_mobile.getText().length() < 10) {
            edit_mobile.requestFocus();
            edit_mobile.setError("Invalid mobile number");

        } else if (edit_mobile.getText().toString().substring(0, 1).equalsIgnoreCase("6") |
                edit_mobile.getText().toString().substring(0, 1).equalsIgnoreCase("7") |
                edit_mobile.getText().toString().substring(0, 1).equalsIgnoreCase("8") |
                edit_mobile.getText().toString().substring(0, 1).equalsIgnoreCase("9")) {
            callValidateapi();
        } else {
            edit_mobile.requestFocus();
            edit_mobile.setError("Invalid Mobile Number");

        }

    }

    private void callValidateapi() {
        if (AppUtils.isInternetAvailable(this)) {
            dutils.showDialog("Please wait..", "Validation is in process");
            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
            JsonObject object = new JsonObject();
            object.addProperty("current_timestamp", System.currentTimeMillis());
            object.addProperty("type", Constant.appType);
            object.addProperty("user_id", edit_mobile.getText().toString());
            Call<ValidationResponseModel> call = service.validateUser(object);
            call.enqueue(new Callback<ValidationResponseModel>() {
                @Override
                public void onResponse(Call<ValidationResponseModel> call, Response<ValidationResponseModel> response) {
                    dutils.dismissDialog();
                    ValidationResponseModel object = response.body();

                    try {
                        if (object.statusCode == 200) {
                            if (object.flag == 1) {
                                try {
                                    utils.setAppVersion(object.softwareVersionNo.toString().trim());
                                } catch (Exception e) {
                                }
                                if (UtilsClass.isvalidVersion(utils.getAppVersion(), context)) {
                                    if (object.name == null) {
                                        object.name = "";
                                    }
                                    if (object.address == null) {
                                        object.address = "";
                                    }
                                    if (object.email == null) {
                                        object.email = "";
                                    }

                                    db.deleteMSTUSER();
                                    String strSQL = "INSERT INTO Mst_User(Userid,passkey,user_name,user_email,user_Address) " +
                                            " VALUES('" + object.userId + "','" + object.password + "' ,'" + object.name + "','" + object.email + "','" + object.address + "')";
                                    Log.d("DemoApp", "strSelectSQ tariff insset" + strSQL);
                                    db.updateFileDesc(strSQL);
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    Date date = new Date();
                                    //values.put("MOD_DATE", formatter.format(date));

                                    String updateQuery = "update FILE_DESC SET FILE_NAME ='" + getResources().getString(R.string.version) + "', MOD_ON='" + formatter.format(date) + "' WHERE ID='1'";
                                    db.updateFileDesc(updateQuery);
                                    //db.updateAppVersion(strSQL);
                                    PreferenceHandler.setisUserId(context, object.userId.toString());
                                    Log.e("USERIDPREF", object.userId.toString());
                                    pHanlder.saveValidationdata(object.userId.toString(), "", object.password.toString(), "", object.divCode.toString(), String.valueOf(object.flag), object.softwareVersionNo.toString(), object.name.toString(), object.email.toString(), object.address.toString());

                                    Intent intent = new Intent(UserValidationActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context).create();
                                    alertDialog.setTitle("Update!!");
                                    alertDialog.setCancelable(false);
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.setMessage("You are using an outdated Version of this Application. Please update");
                                    alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                }
                            } else {
                                dutils.showErrorDialog("Error", object.message);
                            }
                        } else {
                            dutils.showErrorDialog("Error", object.message);
                        }
                    } catch (Exception e) {
                        dutils.showErrorDialog("Error", "Something went wrong...Please try later!");
                    }
                }

                @Override
                public void onFailure(Call<ValidationResponseModel> call, Throwable t) {
                    dutils.dismissDialog();
                    dutils.showErrorDialog("Error", t.getMessage());
                }
            });
        } else {
            dutils.dismissDialog();
            dutils.showErrorDialog("Error", getResources().getString(R.string.no_internet_connection));
        }
    }

  /*  private boolean isvalidVersion() {
        Log.e("AppVersion", utils.getAppVersion());
        if (utils.getAppVersion().equals(BuildConfig.VERSION_NAME)) {
            return true;
        } else {
            return false;
        }
    }*/
}
