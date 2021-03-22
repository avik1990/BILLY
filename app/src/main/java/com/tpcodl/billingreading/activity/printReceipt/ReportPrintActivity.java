package com.tpcodl.billingreading.activity.printReceipt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.analogics.impactAPI.Bluetooth_Printer_2inch_Impact;
import com.analogics.impactprinter.AnalogicsImpactPrinter;
import com.softland.printerlib.PrinterSection.CharaStyle;
import com.softland.printerlib.PrinterSection.ConnectionStatus;
import com.softland.printerlib.PrinterSection.Printer;
import com.softland.printerlib.PrinterSection.Printer2inch;
import com.softland.printerlib.PrinterSection.PrinterExceptions;
import com.softland.printerlib.PrinterSection.iPrinter;
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
import java.util.UUID;

public class ReportPrintActivity extends AppCompatActivity {

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
    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reprint_bl_sbm);

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
          //  Toast.makeText(ReportPrintActivity.this, "message878   " + e, Toast.LENGTH_LONG).show();
        }
    //    Toast.makeText(ReportPrintActivity.this, "message888   " + ReportTyp, Toast.LENGTH_LONG).show();
        Log.d("DemoApp", "devicename   " + devicename);
        if(devicename.equals("nodevice")) {
            Log.d("DemoApp", "entering finding   ");
            try {
                devicename = findBT();
            } catch (Exception ex) {
         //       Toast.makeText(ReportPrintActivity.this, "message12", Toast.LENGTH_LONG).show();
            }
        }

        if(devicename.equals("BTprinter8127")){
            try{
                openBT();
            } catch (Exception ex) {
          //      Toast.makeText(ReportPrintActivity.this, "message14", Toast.LENGTH_LONG).show();
            }
        }
        if(devicename.substring(0,5).equals("SILBT")){
            try{
                Log.d("DemoApp", "Entering open bt  ");
              //  openBT();
                Log.d("DemoApp", "BT opened ");
            } catch (Exception ex) {
                //     Log.d("DemoApp", "Exception 2  " + ex);

            }
        }

        try{
            sendData();
        } catch (Exception ex) {
            Toast.makeText(ReportPrintActivity.this, "message13", Toast.LENGTH_LONG).show();
        }
        //try{
       //     closeBT();
       // } catch (Exception ex) {Toast.makeText(ReportPrintActivity.this, "message14", Toast.LENGTH_LONG).show();
      //  }
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
                    if (device.getName().equals("IMPACTSPT") || device.getName().substring(0,3).equals("AT2") || device.getName().equals("Dual-SPP")) {
                        mmDevice = device;
                        mmDeviceAdr=device.getAddress();
                        Toast.makeText(ReportPrintActivity.this, "paired"+device.getName(), Toast.LENGTH_LONG).show();
                        // break;
                    }else if (device.getName().equals("BTprinter8127")){
                        mmDevice = device;
                        mmDeviceAdr=device.getAddress();
                        Toast.makeText(ReportPrintActivity.this, "paired"+device.getName(), Toast.LENGTH_LONG).show();
                    }else if (device.getName().substring(0,5).equals("SILBT")){
                        mmDevice = device;
                        mmDeviceAdr=device.getAddress();
                        //   Toast.makeText(BillPrintActivity.this, "paired"+device.getName(), Toast.LENGTH_LONG).show();
                        Log.d("DemoApp", "paired   " + device.getName());
                    }else{
                        Toast.makeText(ReportPrintActivity.this, "un piared ", Toast.LENGTH_LONG).show();
                    }
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
        return mmDevice.getName();
    }

    // Tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        ////////////////
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mmDeviceAdr);
        try {
            mmSocket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            // AlertBox("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }
        ////////////////////
        int openflag=1;
        try {
            // Standard SerialPortService ID
            ParcelUuid list[] = mmDevice.getUuids();
            Log.d("DemoApp", "openbt 1   " +list[0]);
            //  mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            //   mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
            //   mmSocket= mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            if(mmSocket.isConnected()){
                Log.d("DemoApp", "openbt 1 socket connected " );
            }else{
                Log.d("DemoApp", "openbt 1  socket close " );
            }
            mmSocket.connect();
            Log.d("DemoApp", "openbt connect success   ");
            //  mBluetoothAdapter.cancelDiscovery();
            mmInputStream=null;
            mmOutputStream=null;
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
            strPrntMsg.setText("Bluetooth Opened");
        } catch (NullPointerException e) {
            Log.d("DemoApp", "Exception 7  " + e);
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("DemoApp", "Exception 8  " + e);
            e.printStackTrace();
        }

    }

    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.
    void beginListenForData() {
        try {
            final Handler handler = new Handler();
          //  Toast.makeText(ReportPrintActivity.this, "Listning data", Toast.LENGTH_LONG).show();
            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                //  myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e11) {
         //   Toast.makeText(ReportPrintActivity.this, "message6"+e11, Toast.LENGTH_LONG).show();
            e11.printStackTrace();
        } catch (Exception e12) {
           // Toast.makeText(ReportPrintActivity.this, "message7"+e12, Toast.LENGTH_LONG).show();
            e12.printStackTrace();
        }
    }

    void sendData() throws IOException {

        try {

            int monthname=0;
            //  String version="1.00";
            if (mmDevice.getName().equals("IMPACTSPT") || mmDevice.getName().substring(0,3).equals("AT2") || mmDevice.getName().equals("Dual-SPP") || mmDevice.getName().substring(0,5).equals("SILBT")) {
                String BillContents = "";
                String doubleHeight = "";
                String widthoff = "";
                String Doublewidth = "";
                byte cmd = (byte) 0x0A; //softland
                String prevCmd = ""; //softland
                String endstr = "";
                //  String filldata="";
                if (mmDevice.getName().equals("IMPACTSPT") || mmDevice.getName().substring(0,3).equals("AT2") || mmDevice.getName().equals("Dual-SPP")) {
                    Bluetooth_Printer_2inch_Impact BPImpact = new Bluetooth_Printer_2inch_Impact();
                    //create the object for the Bluetooth_Printer_2inch_Impact class
                    //call the any  method in Bluetooth_Printer_2inch_Impact class and save the return value in String variable
                    doubleHeight = BPImpact.font_Double_Height_On();
                    String lnfeed = BPImpact.line_Feed();
                    String widthon = BPImpact.font_Double_Height_Width_On();
                    widthoff = BPImpact.font_Double_Height_Width_Off();
                    String formfeed = BPImpact.form_Feed();
                }
                if (mmDevice.getName().substring(0,5).equals("SILBT")) {
/*
                    cmd = (byte) 0x0A;
                 //   doubleHeight = "<0x09><0x20>";
                    widthoff = "<0x00>";
                  //  Doublewidth = "<0x10>"; //double width

                    endstr = "<0x0A>";// its required at the end
                    Doublewidth = "<0x09><0x20>";
                    doubleHeight = "<0x10>"; //double width
*/
                }
                String Billformat="PrePrinted";

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
                    strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM tbl_spotbill_header_details";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"TOTAL CONSUMER:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details GROUP BY RATE_CATEGORY";
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
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"CONSUMER BILLED:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
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
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"CONSUMER UNBILLED:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0 GROUP BY RATE_CATEGORY";
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
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"CURRENT AMT :"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
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
                    strUpdateSQL_01 = "SELECT   ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"BILLED UNITS :"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT RATE_CATEGORY, ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
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
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date)  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"TOTAL AMT:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
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
                    strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM tbl_spotbill_header_details";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"TOTAL CONSUMER:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT RATE_CATEGORY,ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details GROUP BY RATE_CATEGORY";
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
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"CONSUMER BILLED:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
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
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"CONSUMER UNBILLED:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0 GROUP BY RATE_CATEGORY";
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
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"CURRENT AMT :"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
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
                    strUpdateSQL_01 = "SELECT   ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"BILLED PRESENT_BILL_UNITS :"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT RATE_CATEGORY, ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
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
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"TOTAL AMT:"+rs.getString(0)+"\n";
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
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
                   /* strUpdateSQL_01 = "SELECT file_name FROM file_desc";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=doubleHeight+"RT NO:"+rs.getString(0)+"\n";
                    }
                    rs.close();*/
                    BillContents+=widthoff+"....................."+"\n";
                    strUpdateSQL_01 = "SELECT   LEGACY_ACCOUNT_NO2 AS TOT_CON FROM tbl_spotbill_header_details where READ_FLAG=0";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        BillContents+=widthoff+"CONSUMER NO:  "+rs.getString(0)+"\n";
                    }
                    rs.close();
                }
                databaseAccess.close();

                //create the object for the AnalogicsImpactPrinter class
                             //
                //call the Call_Printer(String BTaddress, String printdata)method in Call_Printer class
                //pass the parameters to the Call_Printer method to print the data
                BillContents+="\n\n\n\n\n\n";
                //String ac="\nhjhjj\n";
               // print.Call_Printer(mmDevice.getAddress(), BillContents);
                if (mmDevice.getName().equals("IMPACTSPT") || mmDevice.getName().substring(0,3).equals("AT2") || mmDevice.getName().equals("Dual-SPP")) {
                    AnalogicsImpactPrinter print = new AnalogicsImpactPrinter();
                    //create the object for the AnalogicsImpactPrinter class

                    //  print.FixLengthOf("BILL",24);
                    //call the Call_Printer(String BTaddress, String printdata)method in Call_Printer class
                    //pass the parameters to the Call_Printer method to print the data
                    // print.Call_Printer(mmDevice.getAddress(), BillContents);
                    print.openBT(mmDevice.getAddress());
                    print.printData(BillContents);
                    print.closeBT();
                } else if (mmDevice.getName().substring(0,5).equals("SILBT")) {
                    Log.d("DemoApp", "mmDevice.getName()  " +mmDevice.getAddress());
                    printer=new Printer2inch();

                    connectTodevice(mmDevice.getAddress());

                    //getConnectionStatus is used for reprint purpose if necessary .returns value  if printer is connected or not

                    Log.d("DemoApp", "Printing Sucess ");
                    try {
                        Thread.sleep(5500);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }

/*
                    BillContents += widthoff + "\n\n\n\n";
                    //print.openBT(mmDevice.getAddress());

                    String str = BillContents;
                    String[] strArray = str.split("<");
                    try {
                        for (int i = 0; i < strArray.length; i++) {
                            String mstr = "<" + strArray[i];
                            Pattern pattern = Pattern.compile("<(.*?)>");
                            Matcher matcher = pattern.matcher(mstr);
                            cmd = (byte) 0x10;
                            String strPrintArray = "...........";
                            try {
                                if (matcher.find()) {
                                    strPrintArray = mstr.replace("<" + matcher.group(1) + ">", "");
                                    try {
                                        if (matcher.group(1).equals("0x09")) {
                                            byte[] m = new byte[2];
                                            byte[] m2 = new byte[3];
                                            m[0] = (byte) 0x1b;
                                            m[1] = (byte) 0x40;
                                            mmOutputStream.write(m);
                                            m2[0] = (byte) 0x1b;
                                            m2[1] = (byte) 0x21;
                                            m2[2] = (byte) 0x00;
                                            mmOutputStream.write(m2);

                                        } else if (matcher.group(1).equals("0x00")) {
                                            //NORMAL
                                            byte[] m2 = new byte[3];
                                            m2[0] = (byte) 0x1b;
                                            m2[1] = (byte) 0x21;
                                            m2[2] = (byte) 0x00;
                                            mmOutputStream.write(m2);
                                        } else if (matcher.group(1).equals("0x20")) {
                                            //DOUBLE WIDTH
                                            byte[] m = new byte[3];
                                            m[0] = (byte) 0x1b;
                                            m[1] = (byte) 0x21;
                                            m[2] = (byte) 0x20;
                                            mmOutputStream.write(m);

                                        } else if (matcher.group(1).equals("0x10")) {
                                            //DOUBLE HEIGHT
                                            byte[] m = new byte[3];
                                            m[0] = (byte) 0x1b;
                                            m[1] = (byte) 0x21;
                                            m[2] = (byte) 0x10;
                                            if (i == 1) {
                                                Thread.sleep(1050);
                                            }
                                            mmOutputStream.write(m);

                                        } else if (matcher.group(1).equals("0x0A")) {
                                            //LINE FEED
                                            cmd = (byte) 0x0A;
                                            mmOutputStream.write(cmd);
                                        }

                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    prevCmd = matcher.group(1);
                                    byte[] byteStr = strPrintArray.getBytes();
                                    try {

                                        this.mmOutputStream.write(byteStr);
                                    } catch (Exception e) {
                                        Log.d("DemoApp", "on 2s ");
                                    }
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        Log.d("DemoApp", "on 1  ");
                        this.mmOutputStream.flush();
                        try {
                            Log.d("DemoApp", "on 2  ");
                            Thread.sleep(1000L);
                        } catch (InterruptedException var4) {
                            var4.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d("DemoApp", "on 2 ");
                   // print.closeBT();
                    closeBT();
*/
                }//else if condn close

            }else if(mmDevice.getName().equals("BTprinter8127")){

                String BillContents = "";
                //  String filldata="";
                //create the object for the Bluetooth_Printer_2inch_Impact class
                //call the any  method in Bluetooth_Printer_2inch_Impact class and save the return value in String variable
                String doubleHeight = font_Double_Height_Width_On();
                String widthoff =  font_Double_Height_Width_Off();
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
                 /*   strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
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
                /*    strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
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
                /*    strUpdateSQL_01 = "SELECT file_name FROM file_desc";
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

                BillContents+="\n\n\n\n";
                //String ac="\nhjhjj\n";

                mmOutputStream.write(BillContents.getBytes());
            }

            Intent reports = new Intent(getApplicationContext(), SBMBillingDashboard.class);
            startActivity(reports);
            finish();

        } catch (NullPointerException e22) {
            e22.printStackTrace();

        } catch (Exception e23) {
            Toast.makeText(ReportPrintActivity.this, "message9"+e23, Toast.LENGTH_LONG).show();
            e23.printStackTrace();
        }
        strPrntMsg.setText("Data Sent to Bluetooth Printer");

    }

    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            //  stopWorker = true;

            mmOutputStream.flush();
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            strPrntMsg.setText("Bluetooth Closed");
        } catch (NullPointerException e) {
          //  Toast.makeText(ReportPrintActivity.this, "message10"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
         //   Toast.makeText(ReportPrintActivity.this, "message11"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    public String font_Double_Height_Width_On()
    {

        byte rf1[] = new byte[3];
        rf1[0] = 28;
        rf1[1] = 33;
        rf1[2] = 8;
        String s = new String(rf1);
        return s;
    }
    public String font_Double_Height_Width_Off()
    {

        byte rf1[] = new byte[3];
        rf1[0] = 28;
        rf1[1] = 33;
        rf1[2] = 0;
        String s = new String(rf1);
        return s;
    }

    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
         System.runFinalization();
        //   System.run
         // System.exit(0);
        super.onDestroy();
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
    private void createPrintData1() {
        int blunts=0;
        String conversion="";
        int lapdvar=0;
        String st="";

        try {
            st=printer.reset();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Calendar cal = Calendar.getInstance();

            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            String strUpdateSQL_01="";
            Cursor rs=null;
            st=printer.reset();
            if(ReportTyp.equals("D")){

                st+= printer.printLine(leftAppend1(".....................", 24));

                st+= printer.printLine(leftAppend1(".....................", 24));

                 printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                st+= printer.printLine(leftAppend1("   DAILY REPORT", 24));

                printer.restCHARASTYLE();

                st+= printer.printLine(leftAppend1(".....................", 24));

                st+= printer.printLine(leftAppend1(".....................", 24));

                st+= printer.printLine(" " + "\n");
                String formater="";
                st+= printer.printLine(leftAppend1("DATE:"+dateFormat.format(cal.getTime()).toString(), 24));

                st+= printer.printLine(leftAppend1(".....................", 24));

         /*       strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("RT NO:" + rs.getString(0), 24));

                }
                rs.close();*/
                printer.restCHARASTYLE();

                st+= printer.printLine(leftAppend1(".....................", 24));

                strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM bill_sbm_data";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("TOTAL CONSUMER:"+rs.getString(0), 24));

                }
                rs.close();
                printer.restCHARASTYLE();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("CONSUMER BILLED:"+rs.getString(0), 24));

                }
                rs.close();
                printer.restCHARASTYLE();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }

                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("CONSUMER UNBILLED:"+rs.getString(0), 24));

                }
                rs.close();
                printer.restCHARASTYLE();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("CURRENT AMT :"+rs.getString(0), 24));

                }
                rs.close();
                printer.restCHARASTYLE();
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT   ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("BILLED UNITS :"+rs.getString(0), 24));

                }
                rs.close();
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }
                }
                rs.close();
                printer.restCHARASTYLE();
                strUpdateSQL_01 = "SELECT  ifnull(SUM(bill_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date)  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("TOTAL AMT:"+rs.getString(0), 24));

                }
                rs.close();
                printer.restCHARASTYLE();
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(BILL_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }
                }
                rs.close();
            }else if(ReportTyp.equals("S")){
                st+= printer.printLine(" " + "\n");
                st+= printer.printLine(" " + "\n");
                st+= printer.printLine(leftAppend1(".....................", 24));

                st+= printer.printLine(leftAppend1(".....................", 24));


                 printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                st+= printer.printLine(leftAppend1("    SUMMARY REPORT", 24));

                printer.restCHARASTYLE();

                st+= printer.printLine(leftAppend1(".....................", 24));

                st+= printer.printLine(leftAppend1(".....................", 24));

                st+= printer.printLine(" " + "\n");
                st+= printer.printLine(leftAppend1("DATE:"+dateFormat.format(cal.getTime()),23)+ "\n");

                st+= printer.printLine(leftAppend1(".....................",23)+ "\n");
          /*      strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                    st+= printer.printLine(leftAppend1("RT NO:" + rs.getString(0), 24));


                }
                rs.close();*/
                printer.restCHARASTYLE();
                st+= printer.printLine(leftAppend1(".....................",23)+ "\n");
                strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM bill_sbm_data";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("TOTAL CONSUMER:"+ rs.getString(0), 24));


                }

                rs.close();
                printer.restCHARASTYLE();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("\"CONSUMER BILLED:"+ rs.getString(0), 24));


                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                rs.close();
                printer.restCHARASTYLE();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("CONSUMER UNBILLED:"+ rs.getString(0), 24));


                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                rs.close();
                printer.restCHARASTYLE();
                strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                    st+= printer.printLine(leftAppend1("CURRENT AMT :"+ rs.getString(0), 24));


                }
                rs.close();
                printer.restCHARASTYLE();
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT   ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("BILLED UNITS :"+ rs.getString(0), 24));


                }
                rs.close();
                printer.restCHARASTYLE();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(SUM(bill_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("TOTAL AMT:"+ rs.getString(0), 24));

                }
                rs.close();
                printer.restCHARASTYLE();
                strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(BILL_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        st+= printer.printLine(leftAppend1("DOMESTIC   :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("02")){
                        st+= printer.printLine(leftAppend1("RGGVY      :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("04")){
                        st+= printer.printLine(leftAppend1("BGJY       :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("05")){
                        st+= printer.printLine(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("06")){
                        st+= printer.printLine(leftAppend1("GPS        :"+rs.getString(1), 24));

                    }
                    if(rs.getString(0).equals("22")){
                        st+= printer.printLine(leftAppend1("SPP        :"+rs.getString(1), 24));

                    }
                }
                rs.close();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }else if(ReportTyp.equals("U")){
                st+= printer.printLine(" " + "\n");
                st+= printer.printLine(" " + "\n");
                st+= printer.printLine(leftAppend1(".....................", 24));

                st+= printer.printLine(leftAppend1(".....................", 24));

                 printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                st+= printer.printLine(leftAppend1("   UNBILLED REPORT", 24));

                printer.restCHARASTYLE();

                st+= printer.printLine(leftAppend1(".....................", 24));

                st+= printer.printLine(leftAppend1(".....................", 24));

                st+= printer.printLine(" " + "\n");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                st+= printer.printLine(leftAppend1("DATE:"+dateFormat.format(cal.getTime()),23)+ "\n");

                st+= printer.printLine(leftAppend1(".....................", 24));

     /*           strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                     printer.setCHARASTYLE(CharaStyle.DoubleHeight);

                    st+= printer.printLine(leftAppend1("RT NO:" + rs.getString(0), 24));


                }
                rs.close();*/
                printer.restCHARASTYLE();
                st+= printer.printLine(leftAppend1(".....................", 24));

                strUpdateSQL_01 = "SELECT   cons_acc AS TOT_CON FROM bill_sbm_data where bill_flag=0";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    st+= printer.printLine(leftAppend1("CONSUMER NO:  "+rs.getString(0), 24));

                }
                rs.close();
            }
            databaseAccess.close();
            st+=printer.printNewLine();
            st+=printer.printNewLine();
            st+=printer.printNewLine();

            st+=printer.printNewLine();
            st+=printer.printNewLine();
            st+=printer.printNewLine();
            st+=printer.printNewLine();
            st+=printer.printNewLine();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }









            printer.printText(st);
            // printer.disonnected();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        } catch (PrinterExceptions printerExceptions) {
            printerExceptions.printStackTrace();
            Log.e("innn", "createPrintData: "+printerExceptions.getMessage() );
        }
    }
    void connectTodevice(String address)
    {
        printer.connect(iPrinter.ConnectionType.BT, address, new ConnectionStatus() {
            @Override
            public void onSucess() {
                Log.e("innn", "Already connected");
                createPrintData1();
                printer.disonnected();
            }

            @Override
            public void onFailure(String s) {
                Log.e("innn", "Connection Status Failure: " + printer.getConnectionStatus());
            }
        });
    }

}

