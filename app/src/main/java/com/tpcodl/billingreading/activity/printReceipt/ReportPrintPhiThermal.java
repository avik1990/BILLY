package com.tpcodl.billingreading.activity.printReceipt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aem.api.AEMPrinter;
import com.aem.api.AEMScrybeDevice;
import com.aem.api.IAemScrybe;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.database.DatabaseAccess;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class ReportPrintPhiThermal extends AppCompatActivity {
    private AEMScrybeDevice m_Aem;
    private String BlutoothPrinter;
    private  boolean connectPrinterBool;
    private AEMPrinter aemPrinter;
    BluetoothAdapter mBluetoothAdapter;
    private DatabaseAccess databaseAccess=null;
    // TextView myLabel;
  //  static TextView strPrntMsg;
    final Context context = this;
    private String AccNum="";
    String mmDeviceAdr=null;
    String devicename="nodevice";
    private String address = "";
    private String ReportTyp="";
    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_print_phi_thermal);
       // strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
       // strPrntMsg.setText("Printing");
        mBluetoothAdapter = null;
        AccNum="";
        ReportTyp="";
        String dubl="";
        String accnumber="";
        try {
            Bundle Report = getIntent().getExtras();
            ReportTyp = Report.getString("ReportTyp");
            Log.d("DemoApp", "ReportTyp   " + ReportTyp);
        }catch(Exception e){
            //  Toast.makeText(ReportPrintActivity.this, "message878   " + e, Toast.LENGTH_LONG).show();
        }
        Log.d("DemoApp", "devicename  " + devicename);

        m_Aem = new AEMScrybeDevice(new IAemScrybe() {
            @Override
            public void onDiscoveryComplete(ArrayList<String> arrayList) {


            }
        });
        if(devicename.equals("nodevice")){
            try{
                address=findBT();
            }catch (Exception e){}
        }
        Log.d("DemoApp", "address  " + address);
        try{
            sendData();
        }catch (Exception e){}

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
               // System.exit(0);

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
                    mmDeviceAdr = device.getName();
                    // mmDeviceAdr=device.getAddress();
                    Log.d("DemoApp", "mmDeviceAdr  " + mmDeviceAdr);
                }
            }
            // myLabel.setText("Bluetooth Device Found");
        } catch (NullPointerException e) {
            Log.d("DemoApp", "Exception 5  " + e);
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
        int blunts=0;
        String conversion="";
        int lapdvar=0;
        Log.d("DemoApp", "address" + address);
        BlutoothPrinter = address;
        try {
            connectPrinterBool = m_Aem.connectToPrinter(BlutoothPrinter);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "catch error", Toast.LENGTH_SHORT).show();
        }
        if (connectPrinterBool) {
            aemPrinter = m_Aem.getAemPrinter();
            Toast.makeText(getApplicationContext(), "Printer connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), " error while connecting", Toast.LENGTH_SHORT).show();
        }
       
        try {            
            String billprint="";
            //////printing start//////////
            try{
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Calendar cal = Calendar.getInstance();

                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                String strUpdateSQL_01="";
                Cursor rs=null;

                if(ReportTyp.equals("D")){
                    billprint = "";
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint = "\n"+" DAILY REPORT"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint+= "---------------------------"+"\n";
                    billprint+= "---------------------------"+"\n";

                    billprint+= "DATE:"+dateFormat.format(cal.getTime())+"\n";
                    billprint+= "..................."+"\n";
             /*       strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "RT NO:"+rs.getString(0)+"\n";
                    }
                    rs.close();*/
                    billprint+= "................."+"\n";
                    strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM tbl_spotbill_header_details";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "TOTAL CONSUMER:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "CONSUMER BILLED:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "CONSUMER UNBILLED:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0 GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "CURRENT AMT :"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT   ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "BILLED PRESENT_BILL_UNITS :"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date)  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "TOTAL AMT:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                }else if(ReportTyp.equals("S")){

                    billprint = "";
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint+= "\n"+"SUMMARY REPORT"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint+= "---------------------------"+"\n";
                    billprint+= "DATE:"+dateFormat.format(cal.getTime())+"\n";
                    billprint+= "....................."+"\n";
                /*    strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "RT NO:"+rs.getString(0)+"\n";
                    }
                    rs.close();*/
                    billprint+= "....................."+"\n";
                    strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM tbl_spotbill_header_details";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "TOTAL CONSUMER:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "CONSUMER BILLED:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "CONSUMER UNBILLED:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0 GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "CURRENT AMT :"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT   ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "BILLED PRESENT_BILL_UNITS :"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "TOTAL AMT:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            billprint+= "DOMESTIC   :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("02")){
                            billprint+= "RGGVY      :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("04")){
                            billprint+= "BGJY       :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("05")){
                            billprint+= "KUTIR JYOTI:"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("06")){
                            billprint+= "GPS        :"+rs.getString(1)+"\n";
                        }
                        if(rs.getString(0).equals("22")){
                            billprint+= "SPP        :"+rs.getString(1)+"\n";
                        }
                    }
                    rs.close();
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                }else if(ReportTyp.equals("U")){
                    billprint = "";
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint+= "\nUNBILLED REPORT"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint+= "---------------------------"+"\n";

                    billprint+= "DATE:"+dateFormat.format(cal.getTime())+"\n";
                    billprint+= "....................."+"\n";
           /*         strUpdateSQL_01 = "SELECT file_name FROM file_desc";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "RT NO:"+rs.getString(0)+"\n";
                    }
                    rs.close();*/
                    billprint+= "....................."+"\n";
                    strUpdateSQL_01 = "SELECT   LEGACY_ACCOUNT_NO2 AS TOT_CON FROM tbl_spotbill_header_details where READ_FLAG=0";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        billprint+= "CONSUMER NO:  "+rs.getString(0)+"\n";
                    }
                    rs.close();
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                }
                databaseAccess.close();
            }catch (Exception e){
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
            m_Aem.disConnectPrinter();
        } catch (IOException e) {
            e.printStackTrace();
        }

       // strPrntMsg.setText("Data Sent to Bluetooth Printer");


    }


    //DATE CONVERSION
    public static String convertDateFormat(String strTokenValue, String strDataFormat)
    {
        String strTokenValueRevDt = "";
        String strTokenValueOrgDt = strTokenValue;
        int idxSDate = strDataFormat.indexOf("DD");
        int idxSMonth =strDataFormat.indexOf("MM");
        int idxSYear = strDataFormat.indexOf("Y");
        int idxEYear = strDataFormat.lastIndexOf("Y");
        int idxSHour = strDataFormat.indexOf("HH");

        try{
            strTokenValueRevDt = strTokenValueOrgDt.substring(idxSDate, idxSDate+2)+ "-" +
                    strTokenValueOrgDt.substring(idxSMonth, idxSMonth+2) + "-" +
                    strTokenValueOrgDt.substring(idxSYear+2, idxSYear+4);

        }
        catch (Exception e)
        {
            strTokenValueRevDt = "01-01-99";
            Log.d("DemoApp","e   "+e);
        }
        return strTokenValueRevDt;
    }




    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        //  System.runFinalization();
        //   System.run
        //  System.exit(0);
        super.onDestroy();
    }
}
