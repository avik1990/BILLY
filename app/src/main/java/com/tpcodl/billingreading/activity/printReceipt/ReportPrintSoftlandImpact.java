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

public class ReportPrintSoftlandImpact extends AppCompatActivity {

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
            Toast.makeText(ReportPrintSoftlandImpact.this, "message13", Toast.LENGTH_LONG).show();
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
                    if (device.getName().substring(0,5).equals("SILBT")){
                        mmDevice = device;
                        mmDeviceAdr=device.getAddress();
                        //   Toast.makeText(BillPrintActivity.this, "paired"+device.getName(), Toast.LENGTH_LONG).show();
                        Log.d("DemoApp", "paired   " + device.getName());
                    }else{
                        Toast.makeText(ReportPrintSoftlandImpact.this, "un piared ", Toast.LENGTH_LONG).show();
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
                String BillContents = "";
                String doubleHeight = "";
                String widthoff = "";
                String Doublewidth = "";
                byte cmd = (byte) 0x0A; //softland
                String prevCmd = ""; //softland
                String endstr = "";
                //  String filldata="";

                String Billformat = "PrePrinted";

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Calendar cal = Calendar.getInstance();

                if (mmDevice.getName().substring(0, 5).equals("SILBT")) {
                    Log.d("DemoApp", "mmDevice.getName()  " + mmDevice.getAddress());
                    printer = new Printer2inch();

                    connectTodevice(mmDevice.getAddress());

                    //getConnectionStatus is used for reprint purpose if necessary .returns value  if printer is connected or not

                    Log.d("DemoApp", "Printing Sucess ");
                    try {
                        Thread.sleep(5500);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
            Intent reports = new Intent(getApplicationContext(), SBMBillingDashboard.class);
            startActivity(reports);
            finish();

        } catch (NullPointerException e22) {
            e22.printStackTrace();

        } catch (Exception e23) {
            Toast.makeText(ReportPrintSoftlandImpact.this, "message9"+e23, Toast.LENGTH_LONG).show();
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

           /*     strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
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

          /*      strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
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

