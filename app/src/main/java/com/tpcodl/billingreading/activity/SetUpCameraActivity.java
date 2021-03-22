package com.tpcodl.billingreading.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.tpcodl.billingreading.R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class SetUpCameraActivity extends AppCompatActivity {
    // Request code for camera
    final static int CAMERA_OUTPUT = 0;
    ImageView imv;
    private final int REQUEST_CODE_CAMERA_IMAGE = 1000;
    private final int REQUEST_CODE_EXTERNAL_IMAGE = 2000;
    private final int REQUEST_CODE_STORAGE_PERMS = 321;
    private  ImageView iv_back;
    private TextView tv_title;

    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_camera);
        iv_back=findViewById(R.id.iv_back);
        tv_title=findViewById(R.id.tv_title);

        tv_title.setText("Camera SetUp");

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            if (!hasPermissions()) {
                // your app doesn't have permissions, ask for them.
                requestNecessaryPermissions();
            } else {
                // your app already have permissions allowed.
                // do what you want.
                TakePictureIntent();
            }
        } else {
            Toast.makeText(SetUpCameraActivity.this, "Camera not supported", Toast.LENGTH_LONG).show();
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private boolean hasPermissions() {
        int res = 0;
        // list all permissions which you want to check are granted or not.
        String[] permissions = new String[] {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                // it return false because your app dosen't have permissions.
                return false;
            }

        }
        // it return true, your app has permissions.
        return true;
    }

    private void requestNecessaryPermissions() {
        // make array of permissions which you want to ask from user.
        String[] permissions = new String[] {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // have arry for permissions to requestPermissions method.
            // and also send unique Request code.
            requestPermissions(permissions, REQUEST_CODE_STORAGE_PERMS);
        }
    }

    /* when user grant or deny permission then your app will check in
      onRequestPermissionsReqult about user's response. */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandResults) {
        // this boolean will tell us that user granted permission or not.
        boolean allowed = true;
        switch (requestCode) {
            case REQUEST_CODE_STORAGE_PERMS:
                for (int res : grandResults) {
                    // if user granted all required permissions then 'allowed' will return true.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                // if user denied then 'allowed' return false.
                allowed = false;
                break;
        }
        if (allowed) {
            // if user granted permissions then do your work.
            TakePictureIntent();
        }
        else {
            // else give any custom waring message.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    Toast.makeText(SetUpCameraActivity.this, "Camera Permissions denied", Toast.LENGTH_SHORT).show();
                }
                else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(SetUpCameraActivity.this, "Storage Permissions denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
    private void TakePictureIntent() {
        Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(i,REQUEST_CODE_CAMERA_IMAGE);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String picpath="/cesuapp/"+ System.currentTimeMillis()+".jpg";

        Toast.makeText(SetUpCameraActivity.this, " requestCode"+requestCode , Toast.LENGTH_LONG).show();
        Toast.makeText(SetUpCameraActivity.this, "resultCode"+resultCode , Toast.LENGTH_LONG).show();
        if (resultCode == RESULT_OK)

        {
            Bundle picextras = data.getExtras();
            //  Bitmap bmp = (Bitmap) picextras.get("data");
            //Writting on external
            Bitmap thumbnail = (Bitmap) picextras.get("data");;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(),picpath);
            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            imv = (ImageView) findViewById(R.id.ResultingImage);
            imv.setImageBitmap(thumbnail);
            String imgPath="";
            String dcimimgPath="";
            imgPath = destination.getAbsolutePath();
            dcimimgPath= Environment.DIRECTORY_DCIM;

            File filedelete = new File(imgPath);
            filedelete.delete();
            dcimimgPath= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            File filedeldcim = new File(dcimimgPath);
            filedeldcim.delete();
        }else{
            Toast.makeText(SetUpCameraActivity.this, "Sorry Device has no Camera" , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}