package com.tpcodl.billingreading.broadcasts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.tpcodl.billingreading.R;


public class DialogGPS extends AppCompatActivity {
    public static Activity fa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_gps_connection);
        fa = this;

        //TextView textViewGuide = (TextView) findViewById(R.id.txtMessage);
        Button buttonOkDialog = (Button) findViewById(R.id.buttonOkDialog);
        Button btnGoToSettings = (Button) findViewById(R.id.btnGoToSettings);
        //textViewGuide.setText("Text");
        buttonOkDialog.setText("Retry");
        buttonOkDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if(CommonMethods.isGpsConnected(getApplicationContext())){
                    finish();
                }else{
                    Toast.makeText(DialogGPS.this, "Enable your location to continue", Toast.LENGTH_SHORT).show();
                }*/
                finish();
            }
        });
        btnGoToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });

    }

    @Override
    public void onBackPressed() {
       /* if(CommonMethods.isGpsConnected(getApplicationContext())){
            finish();
        }else{
            Toast.makeText(DialogGPS.this, "Enable your location to continue", Toast.LENGTH_SHORT).show();
        }*/
        finish();
    }
}

