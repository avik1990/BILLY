package com.tpcodl.billingreading.broadcasts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tpcodl.billingreading.R;


public class DialogInternet extends AppCompatActivity {
    public static Activity fa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet_gps);
        fa = this;

        //TextView textViewGuide = (TextView) findViewById(R.id.txtMessage);
        Button buttonOkDialog = (Button) findViewById(R.id.buttonOkDialog);
        //textViewGuide.setText("Text");
        buttonOkDialog.setText("Retry");
        buttonOkDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // put your all data using put extra


              /*  if(CommonMethods.isConnected(getApplicationContext())){
                    finish();
                }else{
                    Toast.makeText(DialogInternet.this, "Connect to internet to continue", Toast.LENGTH_SHORT).show();
                }*/
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        /*
        if(CommonMethods.isConnected(getApplicationContext())){
            finish();
        }else{
            Toast.makeText(DialogInternet.this, "Connect to internet to continue", Toast.LENGTH_SHORT).show();
        }*/
        finish();
    }
}
