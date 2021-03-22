package com.tpcodl.billingreading.activity.printReceipt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.utils.UtilsClass;


public class ReprintAnalogicNewThermal extends AppCompatActivity {

    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_print_analogic_new_thermal);
    }
}
