package com.tpcodl.billingreading;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.tpcodl.billingreading.broadcasts.MobileDataListeners;

public class BillingReadingApplication extends MultiDexApplication {
    private static BillingReadingApplication instance;
    BroadcastReceiver mobileDataBr = new MobileDataListeners();
    BroadcastReceiver mobileDataBr1 = new MobileDataListeners();


    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);

        IntentFilter internetIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        IntentFilter gpsIntentFilter = new IntentFilter(LocationManager.MODE_CHANGED_ACTION);
        getApplicationContext().registerReceiver(mobileDataBr, internetIntentFilter);
        getApplicationContext().registerReceiver(mobileDataBr1, gpsIntentFilter);

    }


    public static BillingReadingApplication getInstance() {
        return instance;
    }
}
