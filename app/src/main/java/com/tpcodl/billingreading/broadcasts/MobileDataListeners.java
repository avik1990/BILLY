package com.tpcodl.billingreading.broadcasts;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import com.tpcodl.billingreading.utils.UtilsClass;


public class MobileDataListeners extends BroadcastReceiver {
    AlertDialog alert;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().matches(LocationManager.MODE_CHANGED_ACTION)){
           // Toast.makeText(context, "GPS Trigger", Toast.LENGTH_SHORT).show();

            UtilsClass.gpsListner(context);

        }
        if(intent.getAction().matches(ConnectivityManager.CONNECTIVITY_ACTION)){
            UtilsClass.internetListner(context);
            //This has been done, disconnecting.
        }

    }

    private void showGPSDisabledAlertToUser(final Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Please enable it to proceed")
                .setCancelable(false)
                .setPositiveButton("Go to Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


}
