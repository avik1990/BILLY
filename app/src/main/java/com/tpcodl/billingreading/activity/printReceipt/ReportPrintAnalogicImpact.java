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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.analogics.impactAPI.Bluetooth_Printer_2inch_Impact;
import com.analogics.impactprinter.AnalogicsImpactPrinter;
import com.softland.printerlib.PrinterSection.Printer;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.remark.SBMBillingDashboard;
import com.tpcodl.billingreading.database.DatabaseAccess;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class ReportPrintAnalogicImpact extends AppCompatActivity {

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
    static TextView strPrntMsg;
    Printer printer;
    private String ReportTyp="";
    private String mmDeviceAdr=null;
    private String devicename="nodevice";
    private String address = "";
    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reprint_bl);

        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
        strPrntMsg.setText("Printing");
        mmOutputStream=null;
        mmInputStream=null;
        mmDevice=null;
        mBluetoothAdapter=null;

        ReportTyp="";
        String dubl="";
        String accnumber="";
        try {
            Bundle Report = getIntent().getExtras();
            ReportTyp = Report.getString("ReportTyp");
            Log.d("DemoApp", "ReportTyp   " + ReportTyp);
        }catch(Exception e){
        }
        if(devicename.equals("nodevice")) {
            Log.d("DemoApp", "entering finding   ");
            try {
                address = findBT();
            } catch (Exception ex) {
                //       Toast.makeText(ReportPrintActivity.this, "message12", Toast.LENGTH_LONG).show();
            }
        }

        try{
            sendData();
        } catch (Exception ex) {
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

    final Context context = this;

    // This will find a bluetooth printer device
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
            //    Toast.makeText(ReportPrintActivity.this, "message1", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            //   Toast.makeText(ReportPrintActivity.this, "message2", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return mmDeviceAdr;
    }
    void sendData() throws IOException {
        try {
            String BillContents = "";
            String doubleHeight = "";
            String widthoff = "";
            Bluetooth_Printer_2inch_Impact BPImpact = new Bluetooth_Printer_2inch_Impact();
            doubleHeight = BPImpact.font_Double_Height_On();
            String lnfeed = BPImpact.line_Feed();
            String widthon = BPImpact.font_Double_Height_Width_On();
            widthoff = BPImpact.font_Double_Height_Width_Off();

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            String strUpdateSQL_01="";
            Cursor rs=null;

            if(ReportTyp.equals("D")){
                BillContents = "";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=doubleHeight+"   DAILY REPORT"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+="\n";
                BillContents+=widthoff+"DATE:"+dateFormat.format(cal.getTime())+"\n";
                BillContents+=widthoff+"....................."+"\n";
               /* strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"RT NO:"+rs.getString(0)+"\n";
                }
                rs.close();*/
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM bill_sbm_data";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"TOTAL CONSUMER:"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"CONSUMER BILLED:"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"CONSUMER UNBILLED:"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"CURRENT AMT :"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT   ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"BILLED UNITS :"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(SUM(bill_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date)  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"TOTAL AMT:"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(BILL_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
            }else if(ReportTyp.equals("S")){
                BillContents = "";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=doubleHeight+"    SUMMARY REPORT"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+="\n";
                BillContents+=widthoff+"DATE:"+dateFormat.format(cal.getTime())+"\n";
                BillContents+=widthoff+"....................."+"\n";
               /* strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"RT NO:"+rs.getString(0)+"\n";
                }
                rs.close();*/
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM bill_sbm_data";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"TOTAL CONSUMER:"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"CONSUMER BILLED:"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"CONSUMER UNBILLED:"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"CURRENT AMT :"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT   ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"BILLED UNITS :"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(SUM(bill_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"TOTAL AMT:"+rs.getString(0)+"\n";
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(BILL_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        BillContents+=widthoff+"DOMESTIC   :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("02")){
                        BillContents+=widthoff+"RGGVY      :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("04")){
                        BillContents+=widthoff+"BGJY       :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("05")){
                        BillContents+=widthoff+"KUTIR JYOTI:"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("06")){
                        BillContents+=widthoff+"GPS        :"+rs.getString(1)+"\n";
                    }
                    if(rs.getString(0).equals("22")){
                        BillContents+=widthoff+"SPP        :"+rs.getString(1)+"\n";
                    }
                }
                rs.close();
            }else if(ReportTyp.equals("U")){
                BillContents = "";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=doubleHeight+"   UNBILLED REPORT"+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+=widthoff+"....................."+"\n";
                BillContents+="\n";
                BillContents+=widthoff+"DATE:"+dateFormat.format(cal.getTime())+"\n";
                BillContents+=widthoff+"....................."+"\n";
                /*strUpdateSQL_01 = "SELECT file_name FROM file_desc";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=doubleHeight+"RT NO:"+rs.getString(0)+"\n";
                }
                rs.close();*/
                BillContents+=widthoff+"....................."+"\n";
                strUpdateSQL_01 = "SELECT   cons_acc AS TOT_CON FROM bill_sbm_data where bill_flag=0";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    BillContents+=widthoff+"CONSUMER NO:  "+rs.getString(0)+"\n";
                }
                rs.close();
            }
            databaseAccess.close();
            BillContents+="\n\n\n\n\n\n";
            AnalogicsImpactPrinter print = new AnalogicsImpactPrinter();
            print.openBT(mmDevice.getAddress());
            print.printData(BillContents);
            print.closeBT();
            Intent reports = new Intent(getApplicationContext(), SBMBillingDashboard.class);
            startActivity(reports);
            finish();

        } catch (NullPointerException e22) {
            e22.printStackTrace();

        } catch (Exception e23) {
            Toast.makeText(ReportPrintAnalogicImpact.this, "message9"+e23, Toast.LENGTH_LONG).show();
            e23.printStackTrace();
        }
        strPrntMsg.setText("Data Sent to Bluetooth Printer");

    }


    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        System.runFinalization();
        //   System.run
       // System.exit(0);
        super.onDestroy();
    }

}

