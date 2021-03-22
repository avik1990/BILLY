package com.tpcodl.billingreading.activity.printReceipt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.qps.btgenie.BluetoothManager;
import com.qps.btgenie.QABTPAccessory;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.database.DatabaseAccess;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class ReportPrintAmigoImpact extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    private DatabaseAccess databaseAccess=null;
    // TextView myLabel;
   // static TextView strPrntMsg;
    private String AccNum="";
    String mmDeviceAdr=null;
    String devicename="nodevice";
    private String address = "";
    private String ReportTyp="";
    //AMIGOS
    BluetoothManager btpObject;
    public static final int DoubleWidth = 3,DoubleHght = 2,Normal = 1;
    boolean closeprinter = false;
    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_print_amigo_impact);
      //  strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
//        strPrntMsg.setText("Printing");
        mmOutputStream = null;
        mmInputStream = null;
        mmDevice = null;
        mBluetoothAdapter = null;
        AccNum="";
        ReportTyp="";
        String dubl="";
        String accnumber="";
        btpObject= BluetoothManager.getInstance(this, new QABTPAccessory() {
            @Override
            public void onBluetoothDeviceFound(BluetoothDevice bluetoothDevice) {
                Log.d("DemoApp", "devicename 1  ");
            }

            @Override
            public void onClientConnectionSuccess() {
                Log.d("DemoApp", "devicename 2  " );
                //Do Not start printing here
            }

            @Override
            public void onClientConnectionFail() {
                Log.d("DemoApp", "devicename 3  " );
            }

            @Override
            public void onClientConnecting() {
                Log.d("DemoApp", "devicename 4 " );
            }

            @Override
            public void onClientDisconnectSuccess() {
                Log.d("DemoApp", "devicename 5  " );
            }

            @Override
            public void onNoClientConnected() {
                Log.d("DemoApp", "devicename 6 " );

            }

            @Override
            public void onBluetoothStartDiscovery() {
                Log.d("DemoApp", "devicename 7  " );
            }

            @Override
            public void onBluetoothNotAvailable() {
                Log.d("DemoApp", "devicename 8  " );
            }

            @Override
            public void onBatterystatuscheck(String s) {
                if(closeprinter == true){       //Added to close the printer
                    btpObject.closeConnection();
                    closeprinter = false;
                }
            }

            @Override
            public void onresponsefrmBluetoothdevice(String s) {

            }

            @Override
            public void onError(String s) {

            }

        });

        try {
            Bundle Report = getIntent().getExtras();
            ReportTyp = Report.getString("ReportTyp");
            Log.d("DemoApp", "ReportTyp   " + ReportTyp);
        }catch(Exception e){
            //  Toast.makeText(ReportPrintActivity.this, "message878   " + e, Toast.LENGTH_LONG).show();
        }
        Log.d("DemoApp", "devicename  " + devicename);

        if(devicename.equals("nodevice")) {
            try {
                //     Log.d("DemoApp", "Entering findbt  " );
                address = findBT();
                Log.d("DemoApp", "BT found " );
            } catch (Exception ex) {

                //     Log.d("DemoApp", "Exception 1  " + ex);
            }
        }

        try{
            Log.d("DemoApp", "Entering open bt  " );
            if(!btpObject.isConnected() == true && address != null) {
                btpObject.createClient(address);

                Log.d("DemoApp", "BT opened ");
            }else{
                System.out.println("BT Closed!..");
            }

        } catch (Exception ex) {
            //     Log.d("DemoApp", "Exception 2  " + ex);

        }


        try{
            Log.d("DemoApp", "sending data  ");
            sendData();
            //    Log.d("DemoApp", "data sent ");
        } catch (Exception ex) {
            Log.d("DemoApp", "Exception 3 " + ex);
            if(devicename.equals("nodevice")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("No Printer!");
                alertDialogBuilder.setMessage("No Printer Connected. Please connect to printer. ")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        }try{
            //workerThread.sleep(20000);
            // Thread.sleep(20000);
            closeBT();
        } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message14", Toast.LENGTH_LONG).show();
        }


    }

    final Context context = this;
    // This will find a bluetooth printer device
    String findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                //  myLabel.setText("No bluetooth adapter available");
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    // MP300 is the name of the bluetooth printer device
                    mmDevice = device;
                    mmDeviceAdr=device.getAddress();

                }

            }
            // myLabel.setText("Bluetooth Device Found");
        } catch (NullPointerException e) {
            Log.d("DemoApp", "Exception 5  " + e);
            if(devicename.equals("nodevice")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("No Printer!");
                alertDialogBuilder.setMessage("No Printer Connected. Please connect to printer. ")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("DemoApp", "Exception 6  " + e);
            e.printStackTrace();
            if(devicename.equals("nodevice")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("No Printer!");
                alertDialogBuilder.setMessage("No Printer Connected. Please connect to printer. ")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        }
        return mmDeviceAdr;
    }
    void sendData() throws IOException {
        {
            int blunts=0;
            String conversion="";
            int lapdvar=0;

           
            try {
                if(btpObject.isConnected() == true) {
                    Log.d("DemoApp", "Printing Data ...  ");
                    btpObject.printerfilter(btpObject.PRINTER_DEFAULT);

                    String billprint = "";
                    //////printing start//////////
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Calendar cal = Calendar.getInstance();

                        databaseAccess = DatabaseAccess.getInstance(context);
                        databaseAccess.open();
                        String strUpdateSQL_01 = "";
                        Cursor rs = null;

                        if (ReportTyp.equals("D")) {
                            btpObject.sendMessage("\n".getBytes());
                            btpObject.sendMessage("\n".getBytes());


                            billprint = "";
                           btpObject.sendMessage_HW("------------------", Normal);
                           btpObject.sendMessage_HW("------------------", Normal);
                           btpObject.sendMessage_HW("DAILY REPORT", Normal);
                           btpObject.sendMessage_HW("...................", Normal);
                           btpObject.sendMessage_HW("..................", Normal);

                           btpObject.sendMessage_HW("DATE:" + dateFormat.format(cal.getTime()) + "\n", Normal);
                           btpObject.sendMessage_HW("...................", Normal);
                     /*       strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("RT NO:" + rs.getString(0), Normal);
                            }
                            rs.close();*/
                           btpObject.sendMessage_HW(".................", Normal);
                            strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM tbl_spotbill_header_details";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("TOTAL CONSUMER:" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("CONSUMER BILLED:" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0  ";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("CONSUMER UNBILLED:" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0 GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("CURRENT AMT :" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT   ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("BILLED PRESENT_BILL_UNITS :" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date)  ";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("TOTAL AMT:" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                        } else if (ReportTyp.equals("S")) {
                            billprint = "";
                           btpObject.sendMessage_HW("...................", Normal);
                           btpObject.sendMessage_HW("...................", Normal);
                           btpObject.sendMessage_HW("    SUMMARY REPORT", Normal);
                           btpObject.sendMessage_HW("...................", Normal);
                           btpObject.sendMessage_HW("...................", Normal);

                           btpObject.sendMessage_HW("DATE:" + dateFormat.format(cal.getTime()), Normal);
                           btpObject.sendMessage_HW(".....................", Normal);
                          /*  strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("RT NO:" + rs.getString(0), Normal);
                            }
                            rs.close();*/
                           btpObject.sendMessage_HW(".....................", Normal);
                            strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM tbl_spotbill_header_details";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("TOTAL CONSUMER:" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("CONSUMER BILLED:" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0  ";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("CONSUMER UNBILLED:" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0 GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("CURRENT AMT :" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT   ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("BILLED PRESENT_BILL_UNITS :" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0  ";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("TOTAL AMT:" + rs.getString(0), Normal);
                            }
                            rs.close();
                            strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                                if (rs.getString(0).equals("01")) {
                                   btpObject.sendMessage_HW("DOMESTIC   :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("02")) {
                                   btpObject.sendMessage_HW("RGGVY      :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("04")) {
                                   btpObject.sendMessage_HW("BGJY       :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("05")) {
                                   btpObject.sendMessage_HW("KUTIR JYOTI:" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("06")) {
                                   btpObject.sendMessage_HW("GPS        :" + rs.getString(1), Normal);
                                }
                                if (rs.getString(0).equals("22")) {
                                   btpObject.sendMessage_HW("SPP        :" + rs.getString(1), Normal);
                                }
                            }
                            rs.close();
                        } else if (ReportTyp.equals("U")) {
                            billprint = "";
                           btpObject.sendMessage_HW("...................", Normal);
                           btpObject.sendMessage_HW("...................", Normal);
                           btpObject.sendMessage_HW("   UNBILLED REPORT", Normal);
                           btpObject.sendMessage_HW("...................", Normal);
                           btpObject.sendMessage_HW("...................", Normal);

                           btpObject.sendMessage_HW("DATE:" + dateFormat.format(cal.getTime()), Normal);
                           btpObject.sendMessage_HW(".....................", Normal);
                          /*  strUpdateSQL_01 = "SELECT file_name FROM file_desc";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("RT NO:" + rs.getString(0), Normal);
                            }
                            rs.close();*/
                           btpObject.sendMessage_HW(".....................", Normal);
                            strUpdateSQL_01 = "SELECT   LEGACY_ACCOUNT_NO2 AS TOT_CON FROM tbl_spotbill_header_details where READ_FLAG=0";
                            rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                            while (rs.moveToNext()) {
                               btpObject.sendMessage_HW("CONSUMER NO:  " + rs.getString(0), Normal);
                            }
                            rs.close();
                        }
                        databaseAccess.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    btpObject.sendMessage("\n".getBytes());
                    btpObject.sendMessage("\n".getBytes());
                    btpObject.sendMessage("\n".getBytes());
                    btpObject.sendMessage("\n".getBytes());


                }
            } catch (Exception e) {
                e.printStackTrace();
                if(devicename.equals("nodevice")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("No Printer!");
                    alertDialogBuilder.setMessage("No Printer Connected. Please connect to printer. ")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }
            }
            try {
                closeBT();
            } catch (IOException ex) {
                if(devicename.equals("nodevice")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("No Printer!");
                    alertDialogBuilder.setMessage("No Printer Connected. Please connect to printer. ")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }
            }

         //   strPrntMsg.setText("Data Sent to Bluetooth Printer");
            //Reprint The Bill
            Button ReprntBl = (Button) findViewById(R.id.ReprntBl);
            ReprntBl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        sendData();
                    } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message13", Toast.LENGTH_LONG).show();
                        if(devicename.equals("nodevice")){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("No Printer!");
                            alertDialogBuilder.setMessage("No Printer Connected. Please connect to printer. ")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();
                        }
                    }
                }
            });//end
            //Exit
            Button Exit = (Button) findViewById(R.id.ExitPaperfd);
            Exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                  //  System.exit(0);

                }
            });//end
            //Continue
            Button contd = (Button) findViewById(R.id.contd);
            contd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent reports2 = new Intent(getApplicationContext(), SearchDataActivity.class);
                    startActivity(reports2);
                    finish();
                    //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    //     .setAction("Action", null).show();

                }
            });//end
        }
    }
    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        //  System.runFinalization();
        //   System.run
        //  System.exit(0);
        super.onDestroy();
    }
    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            //  stopWorker = true;

            if(mmOutputStream!= null) {
                mmOutputStream.flush();
                mmOutputStream.close();
            }
            if(mmInputStream != null)
                mmInputStream.close();

            try{
                if(btpObject.isConnected() == true) {
                    closeprinter = true;
                    btpObject.Batterystatus();
                    long curntime = System.currentTimeMillis();
                    while(curntime+10000 < System.currentTimeMillis()){
                        if(closeprinter == false){
                            break;
                        }
                    }
                    if(closeprinter == true) {
                        btpObject.closeConnection();
                        closeprinter = false;
                    }
                }
                if (mmSocket != null) {
                    Log.d("DemoApp", "on 10 " );
                    try {
                        Log.d("DemoApp", "on 11 " );mmSocket.close();
                        Log.d("DemoApp", "on 12 ");mmSocket = null;} catch (Exception e) { mmSocket = null;
                        Log.d("DemoApp", "on 8 "+e );}

                    Log.d("DemoApp", "on 13 " );
                }

            } catch (Exception e) { Log.d("DemoApp", "on 9 "+e );}
           // strPrntMsg.setText("Bluetooth Closed");;
        } catch (NullPointerException e) {
            //  Toast.makeText(BillPrintActivity.this, "message10"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            //   Toast.makeText(BillPrintActivity.this, "message11" + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    public static String leftAppend1(String str, int maxlen){
        String retStr="";
        for(int i=0;i<(maxlen-str.length());i++){
            retStr+=" ";
        }
        str=retStr+str;
        return str;

    }
    public static String leftAppend2(String str, int leftlen, String Str1, int maxlen){
        String retStr="";
        for(int i=0;i<leftlen;i++){
            retStr+=" ";
        }
        str=retStr+str;
        retStr="";
        for(int i=0;i<(maxlen-(str.length()+Str1.length()));i++){
            retStr+=" ";
        }
        Str1=retStr+Str1;
        str=str+Str1;
        return str;

    }
    public static String leftAppend3(String str, int leftlen, String Str1, int Rlen1, String Str2, int maxlen){
        String retStr="";
        for(int i=0;i<leftlen;i++){
            retStr+=" ";
        }
        str=retStr+str;

        retStr="";
        for(int i=0;i<Rlen1;i++){
            retStr+=" ";
        }
        Str1=Str1+retStr;
        Str1=Str1+Str2;
        retStr="";
        for(int i=0;i<(maxlen-(str.length()+Str1.length()));i++){
            retStr+=" ";
        }
        str=str+retStr+Str1;
        return str;
    }
}
