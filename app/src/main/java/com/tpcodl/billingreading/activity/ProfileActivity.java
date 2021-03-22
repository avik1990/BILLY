package com.tpcodl.billingreading.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.JsonObject;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.reponseModel.ChangePasswordResponseModel;
import com.tpcodl.billingreading.reponseModel.LoginResponseModel;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.AppUtils;
import com.tpcodl.billingreading.utils.Constant;
import com.tpcodl.billingreading.utils.DialogUtils;
import com.tpcodl.billingreading.webservice.ApiInterface;
import com.tpcodl.billingreading.webservice.RetrofitClientInstance;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView ivUserImg;
    private ImageView iv_add_image, iv_back, et_password_view, et_password_view_off;
    private TextView tv_name, change_password, tv_title;
    private EditText et_Phone_number, et_email, et_password, et_address, confirmPassword, oldPassword, newPassword;
    private Button btnSubmit, btnCancel;
    private Context mContext;
    private ActivityUtils utils;
    private DialogUtils dutils;
    private String confirm_password;
    private PreferenceHandler pHandler;
    private AlertDialog dialog;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;


    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Uri picUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext = this;
        utils = ActivityUtils.getInstance();
        dutils = new DialogUtils(this);
        pHandler = new PreferenceHandler(this);
        initView();
        clickListener();
    }

    private void initView() {
        ivUserImg = findViewById(R.id.ivUserImg);
        iv_add_image = findViewById(R.id.iv_add_image);
        change_password = findViewById(R.id.change_password);
        tv_name = findViewById(R.id.tv_name);
        et_Phone_number = findViewById(R.id.et_Phone_number);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_password_view = findViewById(R.id.et_password_view);
        et_password_view_off = findViewById(R.id.et_password_view_off);
        et_address = findViewById(R.id.et_address);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(mContext.getString(R.string.edit_profile));

        if ((utils.getUserName()!=null)&&(!(utils.getUserName().equalsIgnoreCase("null")))){
            tv_name.setText(utils.getUserName());
        }
        if ((utils.getUserID()!=null)&&(!(utils.getUserID().equalsIgnoreCase("null")))){
            et_Phone_number.setText(utils.getUserID());
        }
        if ((utils.getUserEmail()!=null)&&(!(utils.getUserEmail().equalsIgnoreCase("null")))){
            et_email.setText(utils.getUserEmail());
        }
        if ((utils.getUserPassword()!=null)&&(!(utils.getUserPassword().equalsIgnoreCase("null")))){
            et_password.setText(utils.getUserPassword());
        }
        if ((utils.getUserAddress()!=null)&&(!(utils.getUserAddress().equalsIgnoreCase("null")))){
            et_address.setText(utils.getUserAddress());

        }

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.change__password_dialog, null);
        builder.setView(customLayout);
        oldPassword = customLayout.findViewById(R.id.old_password);
        newPassword = customLayout.findViewById(R.id.new_password);
        confirmPassword = customLayout.findViewById(R.id.confirm_password);
        Button submit = customLayout.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_password = oldPassword.getText().toString();
                String new_password = newPassword.getText().toString();
                confirm_password = confirmPassword.getText().toString();
                if (old_password.length() <= 0) {
                    oldPassword.requestFocus();
                    oldPassword.setError("Enter old password");
                } else if (new_password.length() <= 0) {
                    newPassword.requestFocus();
                    newPassword.setError("Enter new password");
                } else if (confirm_password.length() <= 0) {
                    confirmPassword.requestFocus();
                    confirmPassword.setError("Enter confirm password");
                } else if (!old_password.equals(utils.getUserPassword())) {
                    oldPassword.requestFocus();
                    oldPassword.setError("Old Password is wrong");
                } else if (old_password.equals(confirm_password)) {
                    confirmPassword.requestFocus();
                    confirmPassword.setError("Old Password and new password are same");
                } else {
                    if (!new_password.equalsIgnoreCase(confirm_password)) {
                        confirmPassword.requestFocus();
                        confirmPassword.setError("New password and confirm password are not same");
                    } else {
                        CallchnagePasswordapi();

                    }
                }


            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void clickListener() {

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        ivUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermissions();
            }
        });
        iv_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivUserImg.performClick();
            }
        });

        et_password_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_password_view_off.setVisibility(View.VISIBLE);
                et_password_view.setVisibility(View.GONE);
                et_password_view.setImageDrawable(getResources().getDrawable(R.drawable.icon_view_on));
                et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);


            }
        });

        et_password_view_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_password_view.setVisibility(View.VISIBLE);
                et_password_view_off.setVisibility(View.GONE);
                et_password_view.setImageDrawable(getResources().getDrawable(R.drawable.icon_view_off));
                et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  submitEditProfile();
                if (AppUtils.isInternetAvailable(ProfileActivity.this)) {

                    if (!(et_Phone_number.getText().toString().length() > 0)) {
                        et_Phone_number.setError("Phone number can not be null");
                    } else if (!(et_email.getText().toString().length() > 0)) {
                        et_email.setError("Email can not be null");
                    } else if (!(et_password.getText().toString().length() > 0)) {
                        et_password.setError("Password can not be null");
                    } else if (!(et_address.getText().toString().length() > 0)) {
                        et_address.setError("Address can not be null");

                    } else {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
                            submitEditProfile();
                        } else {
                            et_email.requestFocus();
                            et_email.setError("Please enter valid email");
                        }
                    }
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void submitEditProfile() {
        // BluetoothActivity
        Intent intent = new Intent(ProfileActivity.this, BluetoothActivity.class);
        startActivity(intent);
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
                        if (permissionStatus.getBoolean(REQUIRED_SDK_PERMISSIONS[0],true)) {
                            //Previously Permission Request was cancelled with 'Dont Ask Again',
                            // Redirect to Settings after showing Information about why you need the permission
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
             /*   SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
                editor.commit();*/
                pickPicture(REQUEST_CODE_FILE_UPLOAD);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            picUri = data != null ? data.getData() : null;
            ivUserImg.setImageURI(picUri);


            String filePath = picUri.getPath();

            String base64String = readFileAsBase64String(filePath);
            Log.d("ds", "onActivityResult: " + base64String);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.RESULT_ERROR, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }

    private String readFileAsBase64String(String path) {
        try {
            InputStream is = new FileInputStream(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Base64OutputStream b64os = new Base64OutputStream(baos, Base64.DEFAULT);
            byte[] buffer = new byte[8192];
            int bytesRead;
            try {
                while ((bytesRead = is.read(buffer)) > -1) {
                    b64os.write(buffer, 0, bytesRead);
                }
                return baos.toString();
            } catch (IOException e) {
                Log.e("TAG", "Cannot read file " + path, e);
                // Or throw if you prefer
                return "";
            } finally {
                closeQuietly(is);
                closeQuietly(b64os); // This also closes baos
            }
        } catch (FileNotFoundException e) {
            Log.e("TAG", "File not found " + path, e);
            // Or throw if you prefer
            return "";
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void CallchnagePasswordapi() {
        if (AppUtils.isInternetAvailable(this)) {
            dutils.showDialog("Login", "Please wait..");
            ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
            JsonObject object = new JsonObject();
            object.addProperty("new_password", confirmPassword.getText().toString());
            object.addProperty("type", Constant.appType);
            object.addProperty("old_password", oldPassword.getText().toString());
            object.addProperty("user_id", et_Phone_number.getText().toString());
            Call<ChangePasswordResponseModel> call = service.changePassword(utils.getAuthToken(), object);
            call.enqueue(new Callback<ChangePasswordResponseModel>() {
                @Override
                public void onResponse(Call<ChangePasswordResponseModel> call, Response<ChangePasswordResponseModel> response) {
                    ChangePasswordResponseModel object = response.body();
                    if (object.statusCode == 200) {
                        dutils.dismissDialog();
                        Toast.makeText(ProfileActivity.this, object.message, Toast.LENGTH_SHORT).show();
                        pHandler.saveChangedPassword(confirmPassword.getText().toString());
                        Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        dialog.dismiss();
                        finish();
                        startActivity(i);
                    } else {
                        Toast.makeText(ProfileActivity.this, object.message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        dialog.cancel();
                    }


                }

                @Override
                public void onFailure(Call<ChangePasswordResponseModel> call, Throwable t) {
                    dutils.dismissDialog();
                    Toast.makeText(ProfileActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            dutils.dismissDialog();
            Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }


}