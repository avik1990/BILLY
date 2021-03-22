package com.tpcodl.billingreading.activity.printReceipt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.analogics.impactAPI.Bluetooth_Printer_2inch_Impact;
import com.analogics.impactprinter.AnalogicsImpactPrinter;
import com.softland.printerlib.PrinterSection.CharaStyle;
import com.softland.printerlib.PrinterSection.ConnectionStatus;
import com.softland.printerlib.PrinterSection.Printer;
import com.softland.printerlib.PrinterSection.PrinterExceptions;
import com.softland.printerlib.PrinterSection.iPrinter;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.database.DatabaseAccess;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReprintBlActivity extends AppCompatActivity {
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
    private String AccNum="";
    String mmDeviceAdr=null;
    String devicename="nodevice";
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
        Printer printer;
        AccNum="";
        String dubl="";
        String accnumber="";
       try {
           Bundle Reprintbl = getIntent().getExtras();
           AccNum = Reprintbl.getString("accntnum");
           //  Log.d("DemoApp", "account num  " + AccNum);
       }catch(Exception e){
           Toast.makeText(ReprintBlActivity.this, "message878   " + e, Toast.LENGTH_LONG).show();
           Toast.makeText(ReprintBlActivity.this, "message878   " + e, Toast.LENGTH_LONG).show();
       }
        Toast.makeText(ReprintBlActivity.this, "message888   " + AccNum, Toast.LENGTH_LONG).show();


        if(devicename.equals("nodevice")) {
            try {
                devicename = findBT();
            } catch (Exception ex) {
                Toast.makeText(ReprintBlActivity.this, "message12", Toast.LENGTH_LONG).show();
            }
        }
        if(devicename.equals("BTprinter8127")){
            try{
                openBT();
            } catch (Exception ex) {
                Toast.makeText(ReprintBlActivity.this, "message14", Toast.LENGTH_LONG).show();

            }
        }
        if(devicename.substring(0,5).equals("SILBT")){
            try{
                Log.d("DemoApp", "Entering open bt  ");
                openBT();
                Log.d("DemoApp", "BT opened ");
            } catch (Exception ex) {
                //     Log.d("DemoApp", "Exception 2  " + ex);

            }
        }

        try{
            sendData();
        } catch (Exception ex) {
            Toast.makeText(ReprintBlActivity.this, "message13", Toast.LENGTH_LONG).show();
        }
        //try{
          //  closeBT();
        //} catch (Exception ex) {Toast.makeText(ReprintBlActivity.this, "message14", Toast.LENGTH_LONG).show();
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
                    if (device.getName().equals("IMPACTSPT") || device.getName().substring(0, 3).equals("AT2")) {
                        mmDevice = device;
                        mmDeviceAdr=device.getAddress();
                        Toast.makeText(ReprintBlActivity.this, "paired"+device.getName(), Toast.LENGTH_LONG).show();
                        // break;
                    }else if (device.getName().equals("BTprinter8127")){
                        mmDevice = device;
                        mmDeviceAdr=device.getAddress();
                        Toast.makeText(ReprintBlActivity.this, "paired"+device.getName(), Toast.LENGTH_LONG).show();
                    }else if (device.getName().substring(0,5).equals("SILBT")){
                        mmDevice = device;
                        mmDeviceAdr=device.getAddress();
                        //   Toast.makeText(BillPrintActivity.this, "paired"+device.getName(), Toast.LENGTH_LONG).show();
                        Log.d("DemoApp", "paired   " + device.getName());
                    }
                    else{
                        Toast.makeText(ReprintBlActivity.this, "un piared ", Toast.LENGTH_LONG).show();
                    }
                }

            }
            // myLabel.setText("Bluetooth Device Found");
        } catch (NullPointerException e) {
            Toast.makeText(ReprintBlActivity.this, "message1", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(ReprintBlActivity.this, "message2", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return mmDevice.getName();
    }

    // Tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
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
            Toast.makeText(ReprintBlActivity.this, "open blue tooth"+mmInputStream.toString(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(ReprintBlActivity.this, "Listning data", Toast.LENGTH_LONG).show();
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
            Toast.makeText(ReprintBlActivity.this, "message6"+e11, Toast.LENGTH_LONG).show();
            e11.printStackTrace();
        } catch (Exception e12) {
            Toast.makeText(ReprintBlActivity.this, "message7"+e12, Toast.LENGTH_LONG).show();
            e12.printStackTrace();
        }
    }

    void sendData() throws IOException {
        int blunts=0;
        String conversion="";
        int lapdvar=0;

        try {

            int monthname=0;
            String version="";
            int iBilled_cnt = 0;
            SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
            SharedPreferences.Editor editor = sessiondata.edit();
            String Usernm =sessiondata.getString("userID", null); // getting String
            Log.d("DemoApp", "mmDevice.getName()  " +mmDevice.getName());
            Log.d("DemoApp", "mmDevice.getName()  " +mmDevice.getName());
            String BillContents = "";
            String filldata = "";
            if (mmDevice.getName().equals("IMPACTSPT") || mmDevice.getName().substring(0, 3).equals("AT2") || mmDevice.getName().substring(0,5).equals("SILBT")) {
                String doubleHeight = "";
                String widthoff = "";
                String Doublewidth = "";
                byte cmd = (byte) 0x0A; //softland
                String prevCmd = ""; //softland
                String endstr = "";
                if (mmDevice.getName().equals("IMPACTSPT") || mmDevice.getName().substring(0, 3).equals("AT2")) {
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
                     cmd = (byte) 0x0A;
                    Doublewidth = "<0x09><0x20>";//double widthdoubleHeight
                    widthoff = "<0x00>";
                    doubleHeight = "<0x10>";
                    endstr = "<0x0A>";// its required at the end

                }
                String BlPrepTm = "";
                String Billformat = "PrePrinted";
                Calendar c = Calendar.getInstance();
                SimpleDateFormat month = new SimpleDateFormat("MMM-yy");
                String strmonth = month.format(c.getTime());
                SimpleDateFormat year = new SimpleDateFormat("dd-MM-yy");
                Date vardate = null;
                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                //to get the current version of software
                String strSelectSQL_02 = "select file_name,version_flag from File_desc where version_flag=1";
                Cursor rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
                while (rs1.moveToNext()) {
                    version = rs1.getString(0);
                }
                rs1.close();
                //getting user name

                ////
                String strUpdateSQL_01 = "SELECT strftime('%d-%m-%Y',MOD_ON),CONS_ACC,CON_NAME,CON_ADD1,CON_ADD2,CON_CLST," +
                        " ROUTE_NO,CONS_CODE,CASE WHEN CAT_CODE='01' THEN 'DOM' WHEN CAT_CODE='02' THEN 'RGVY' WHEN CAT_CODE='04' THEN 'BJVY' " +
                        " WHEN CAT_CODE='05' THEN 'KJ' WHEN CAT_CODE='06' THEN 'GPS' WHEN CAT_CODE='22' THEN 'SPP' ELSE 'NIL' END TARIFF,DIVISION," +
                        " SUBDIVISION,SECTION,MTR_SL_NO,MTR_TP, CASE WHEN MF >= 1 THEN MF ELSE 1 END AS MF,CASE WHEN MTR_OWN='C' THEN 'CONSUMER'  " +
                        " WHEN MTR_SL_NO IS NULL THEN ' ' ELSE 'CESU' END AS MTR_OWN ,MTR_DIGIT,MTR_MAKE,USAGE,LOAD,PRV_READ,CON_DATE," +
                        " strftime('%d-%m-%Y',OLD_DATE),OLD_STATUS,DPS,MISC_CHG,CR_ADJ,DB_ADJ,PRV_AMOUNT,HL_UNIT,OLD_AVG,LST_PAY_DATE,LST_BOOK_NO," +
                        " LST_RECT_NO,TOT_PAY,NO_OF_MON,ED_EXEMPT,ARR_INS_FEE,NEW_MTR_NO,NEW_MTR_INI_READ,MTR_CHG_DATE,SD_INT,ASD,ASD_ARR,INSTALLATION_NO," +
                        " BILL_NO,CUST_ID,BILL_PRN_HEADER,BILL_PRN_FOOTER,PRV_YR_ARR,CUR_YR_ARR,ULF,LRC,ABN_READING,BILL_MON,ECS_LIMIT," +
                        " strftime('%d-%m-%Y',ECS_VALID)," +
                        " PST_METER_READ,P_STATUS,MTR_COND,MTR_INS_STATUS,UNITS,BILL_BASIS,EC,MMFC,MTR_RENT,ED,CUR_TOTAL,REBATE,BILL_TOTAL,AVG_UNIT,RECP_NO," +
                        " CHQ_NO,CHQ_DATE,BANK,RECT_AMT,strftime('%d-%m-%Y',DUE_DATE),DISCONNECTION_DATE,MTR_TIME,strftime('%d-%m-%Y',BILL_DATE),BILLED_MD," +
                        " PRS_MD,CESU_DATE,DIV_CODE,BILL_FLAG, CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG," +
                        " SENT_FLAG,UNIT_SLAB1,RATE_SLAB1,EC_SLAB1,UNIT_SLAB2,RATE_SLAB2,EC_SLAB2,UNIT_SLAB3,RATE_SLAB3,EC_SLAB3,UNIT_SLAB4," +
                        " RATE_SLAB4,EC_SLAB4,NO_BILLED_MONTH FROM BILL_SBM_DATA WHERE CONS_ACC = '" + AccNum + "'";
                Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
                Cursor rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    int noofmonth = rs.getInt(99);
                    monthname = rs.getInt(54);
                    // vardate=year.parse(rs.getString(79));
                    double roundupto = 0;
                    String Mtrtype = rs.getString(13);
                    String Phase = "01";
                    if (Mtrtype.equals("02") || Mtrtype.equals("09")) {
                        Phase = "03";
                    }
                    if (Billformat.equals("PrePrinted")) {
                        BillContents = "\n\n";
                        // BillContents += doubleHeight + String.format("%-24s", "    TEST-BILL PILOT");
                        BillContents += doubleHeight + String.format("%-24s", "");
                        // BillContents +=String.format("%-10s%14s","","testing of the consumer where");
                        //BillContents +=String.format("%-10s%-14s","","testing of the consumer where");
                        //BillContents +=String.format("%10s%14s","","testing of the consumer where");
                        //BillContents +=String.format("%10s%-14s","","testing of the consumer where");
                        // this is added to handle mtr time print for 24 hrs at midnight
                        if (rs.getString(78).length() == 5) {
                            BlPrepTm = "0" + rs.getString(78).substring(0, 1) + ":" + rs.getString(78).substring(1, 3);
                        } else if (rs.getString(78).length() == 4) {
                            BlPrepTm = "00:" + rs.getString(78).substring(0, 2);
                        } else if (rs.getString(78).length() == 3) {
                            BlPrepTm = "00:0" + rs.getString(78).substring(0, 1);
                        } else if (rs.getString(78).length() == 2 || rs.getString(78).length() == 1) {
                            BlPrepTm = "00:00";
                        } else {
                            BlPrepTm = rs.getString(78).substring(0, 2) + ":" + rs.getString(78).substring(2, 4);
                        }
                        BillContents += widthoff + String.format("%12s%-12s", "", strmonth.toUpperCase());
                        BillContents += String.format("%16s", convertDateFormat(rs.getString(79), "DD-MM-YYYY")) + String.format("%8s", BlPrepTm);
                        BillContents += String.format("%10s%14s", rs.getInt(45), version);
                        BillContents += String.format("%12s%12s", "", (rs.getInt(99) + " MONTHS"));
                        BillContents += String.format("%4s%20s", "", rs.getString(9));
                        BillContents += String.format("%8s%16s", "", rs.getString(10));
                        BillContents += String.format("%9s%15s", "", rs.getString(11));
                        BillContents += String.format("%10s%14s", "", "NA");
                        BillContents += doubleHeight + String.format("%24s", rs.getString(1));
                        BillContents += widthoff + String.format("%8s%16s", " ", rs.getString(46));
                        //BillContents += doubleHeight + String.format("%10s%14s", " ", rs.getString(46));
                        BillContents += widthoff + String.format("%8s%16s", "", rs.getString(7));
                        BillContents += String.format("%10s%14s", "", rs.getString(44));
                        BillContents += String.format("%10s%14s", "", rs.getString(12));
                        BillContents += String.format("%14s%10s", "", rs.getString(15));//mtrowner
                        BillContents += String.format("%10s%14s", "", "");
                        BillContents += String.format("%12s%-12s", "", rs.getString(14));

                        if (rs.getString(2).trim().length() <= 18) {
                            BillContents += String.format("%6s%-18s", "", rs.getString(2));//name
                            BillContents += "" + "\n";
                        } else {
                            BillContents += String.format("%8s%-40s", "", rs.getString(2));
                        }
                        StringBuilder strAddr = new StringBuilder(rs.getString(3) + "," + rs.getString(4));
                        if (rs.getString(3).trim().length() + rs.getString(4).trim().length() > 17) {
                            // Log.d("DemoApp", "Exception 1  " + strAddr.length());
                            if (strAddr.length() >= 41) {
                                strAddr.setLength(41);
                            }
                            BillContents += String.format("%7s%-41s", "", strAddr);
                        } else {
                            BillContents += String.format("%7s%-17s", "", (rs.getString(3) + "," + rs.getString(4)));
                            BillContents += "" + "\n";
                        }
                        String ibond = "";
                        ibond = rs.getString(18);
                        if (rs.getString(18).equals("I")) {
                            ibond = "I-BOND";
                        }

                        BillContents += String.format("%6s%-6s%6s%-6s", "", ibond, "Ph:", Phase);//usage
                        BillContents += String.format("%6s%-6s%6s%-6.2f", "", rs.getString(8), "", rs.getDouble(19));
                        BillContents += String.format("%10s%-14s", "", rs.getString(30));
                        // BillContents += "\n";
                        //  BillContents += "\n";
                        BillContents += doubleHeight + String.format("%-24s", "");

                        //BillContents += "      RDG" + "    DATE" + "   STS" + "\n";
                        //  BillContents += String.format("%5s%-7s%-10s%1s%-1s","", rs.getString(57), rs.getString(79),"",rs.getString(59));
                        //   BillContents += String.format("%5s%-7s%-10s%1s%-1s","", rs.getString(20), rs.getString(22),"",rs.getString(23));
                        BillContents += widthoff + String.format("%7s%-7s%-8s%1s%-1s", "", rs.getString(57), convertDateFormat(rs.getString(79), "DD-MM-YYYY"), "", rs.getString(59));
                        BillContents += widthoff + String.format("%7s%-7s%-8s%1s%-1s", "", rs.getString(20), convertDateFormat(rs.getString(22), "DD-MM-YYYY"), "", rs.getString(23));

                        BillContents += String.format("%16s%-8s", "", rs.getString(61));
                        if (rs.getString(62).equals("N")) {
                            BillContents += String.format("%10s%-14s", "", "ACTUAL");
                        } else {
                            BillContents += String.format("%14s%-10s", "", "AVERAGE");
                        }
                        // BillContents += doubleHeight+String.format("%-24s", "");
                        BillContents += widthoff + "\n";
                        //  Bill calulation start
                        BillContents += widthoff + String.format("%-10s%14.2f", "", rs.getDouble(64));
                        blunts = rs.getInt(61);
                        //Bill slab Changed by Santi on 13.01.2016
                        if (rs.getDouble(89) > 0) {
                            BillContents += String.format("%-3s%-11s%10.2f", "", rs.getString(87) + "X" + rs.getDouble(88) + "0=", rs.getDouble(89));
                        } else {
                            BillContents += "\n";
                        }
                        if (rs.getDouble(92) > 0) {
                            BillContents += String.format("%-3s%-11s%10.2f", "", rs.getString(90) + "X" + rs.getDouble(91) + "0=", rs.getDouble(92));
                        } else {
                            BillContents += "\n";
                        }
                        if (rs.getDouble(95) > 0) {
                            BillContents += String.format("%-3s%-11s%10.2f", "", rs.getString(93) + "X" + rs.getDouble(94) + "0=", rs.getDouble(95));
                        } else {
                            BillContents += "\n";
                        }
                        if (rs.getDouble(98) > 0) {
                            BillContents += String.format("%-3s%-11s%10.2f", "", rs.getString(96) + "X" + rs.getDouble(97) + "0=", rs.getDouble(98));
                        } else {
                            BillContents += "\n";
                        }
                        //end
                        BillContents += String.format("%-14s%10.2f", "", rs.getDouble(66));
                        BillContents += String.format("%-14s%10.2f", "", rs.getDouble(65));
                        BillContents += doubleHeight + String.format("%-14s%10.2f", "", rs.getDouble(67));
                        BillContents += widthoff + "\n";
                        BillContents += String.format("%-14s%10.2f", "", rs.getDouble(49));//previous yr arr
                        BillContents += String.format("%-14s%10.2f", "", rs.getDouble(50));
                        BillContents += String.format("%-14s%10.2f", "", rs.getDouble(24));
                        if (rs.getInt(27) > 0) { //adjustments
                            BillContents += String.format("%14s%10.2f", "(+)", rs.getDouble(27));
                        } else if (rs.getInt(26) > 0) {
                            BillContents += String.format("%14s%10.2f", "(-)", rs.getDouble(26));
                        } else {
                            BillContents += String.format("%24s", "0.00");
                        }
                        if (rs.getString(23).equals("P")) {
                            BillContents += String.format("%-14s%10.2f", "P.ADJ.AMT :", rs.getDouble(28));
                        } else {
                            BillContents += String.format("%24s", " ");
                        }
                        if (rs.getInt(25) > 0) {
                            BillContents += String.format("%-14s%10.2f", "", rs.getDouble(25));
                        } else {
                            BillContents += String.format("%24s", " ");
                        }
                        if (rs.getInt(41) > 0) {
                            BillContents += String.format("%-14s%10.2f", "SD AVAIL:", rs.getDouble(41));
                        } else {
                            BillContents += String.format("%24s", "");
                        }
                        if (rs.getInt(42) > 0) {
                            BillContents += String.format("%-14s%10.2f", "ASD:", rs.getDouble(42));
                        } else {
                            BillContents += String.format("%24s", "");
                        }
                        if (rs.getInt(43) > 0) {
                            BillContents += String.format("%-14s%10.2f", "ASD ARR:", rs.getDouble(43));
                        } else {
                            BillContents += String.format("%24s", "");
                        }
                        BillContents += "\n"; //Penalty amount showing 30.06.2017
                        // BillContents += String.format("%-8s%16s", "PENALTY:", rs.getString(47));
                        //end
                        BillContents += String.format("%-12s%12.2f", "", rs.getDouble(69));//total amount
                        BillContents += String.format("%-12s%12.2f", "", rs.getDouble(68));//rebate amount
                        BillContents += doubleHeight + String.format("%-14s%10.2f", "", (double) Math.round(rs.getDouble(69) - rs.getDouble(68)));//total bill by due date
                        roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                        if (roundupto > 0) {
                            BillContents += widthoff + String.format("%20s%4.2f", "(+)", roundupto);
                        } else {
                            BillContents += widthoff + String.format("%20s%4.2f", "(-)", Math.abs(roundupto));
                        }
                        BillContents += widthoff + String.format("%-24s", " ");
                        BillContents += doubleHeight + String.format("%-12s%12s", "", rs.getString(76));//rebate date
                        //changes on 31.01.2017 if total amount <0 and total amt-rebate amt<0
                        double payaftdt = 0;
                        if (rs.getDouble(69) < 0 && ((double) Math.round(rs.getDouble(69) - rs.getDouble(68))) < 0) {
                            payaftdt = ((double) Math.round(rs.getDouble(69) - rs.getDouble(68)));
                            roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                        } else {
                            payaftdt = (double) Math.round(rs.getDouble(69));
                            roundupto = Math.round(rs.getDouble(69)) - rs.getDouble(69);
                        }
                        ////
                        // BillContents += String.format("%-14s%10.2f", "", (double) Math.round(rs.getDouble(69)));//pay after due date disable on 31.01.17
                        BillContents += String.format("%-14s%10.2f", "", payaftdt);//pay after due date enable on 31.01.17
                        // roundupto = Math.round(rs.getDouble(69)) - rs.getDouble(69);// go to if else on 31.01.17
                        // Log.d("DemoApp", "payaftdt 1  " + Math.round(payaftdt));
                        //Log.d("DemoApp", "payaftdt 2  " + payaftdt);
                        //  roundupto = Math.round(payaftdt) - payaftdt; //enable on 31.01.17
                        if (roundupto > 0) {
                            BillContents += widthoff + String.format("%20s%4.2f", "(+)", roundupto);
                        } else {
                            BillContents += widthoff + String.format("%20s%4.2f", "(-)", Math.abs(roundupto));
                        }

                        BillContents += "\n"; //dissable on 03.04.2017
                        BillContents += "\n";//dissable on 03.04.2017

                        BillContents += String.format("%3s%21s", "", rs.getString(32) + "-" + rs.getString(33));
                        BillContents += String.format("%3s%-9.2f%2s%-10s", "", rs.getDouble(34), "", rs.getString(31));
                        BillContents += "\n";
                        BillContents += doubleHeight + "\n";
                        //Ecs Message printing///////
                        if (rs.getDouble(69) < rs.getDouble(55) && rs.getInt(85) == 1) { //here rs(85) is validation date check query CASE WHEN CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG
                            BillContents += widthoff + widthoff + String.format("%24s", "Bg dbtd to Bank thru ECS");//ECS Message
                        } else if (rs.getDouble(69) > rs.getDouble(55) && rs.getInt(85) == 1) {
                            BillContents += widthoff + String.format("%-24s", "Val excd-pay cash/chq");//ECS Message
                        } else if (rs.getInt(85) == 0 && rs.getString(56) != null) {
                            BillContents += widthoff + String.format("%-24s", "Dt lpsd-pay cash/chq");//ECS Message
                        } else {
                            BillContents += widthoff + String.format("%-24s", " ");
                        }
                        //added on 03.04.2017 to show additional rebate message
                        double cashlesrbt = 0;
                        cashlesrbt = rs.getDouble(63) * 0.01;//1% cashless rebate of EC

                        if(rs.getString(8).equals("DOM") || (rs.getString(8).equals("GPS") && Phase.equals("01"))) {//added on 27.03.2018
                            BillContents += String.format("%-24s", "PAY CASHLESS AND AVAIL");
                            float percent = 2;
                            // BillContents += String.format("%21s%3s", "ADDITIONL REBATE OF 1"+"%");
                            BillContents += String.format("ADDITIONAL REBATE OF %,2.0f", percent) + "%";
                            /////End 03.04.2017
                        } else{//added on 27.03.2018
                            BillContents += widthoff + String.format("%-24s", " ");
                            BillContents += widthoff + String.format("%-24s", " ");
                        }//added on 27.03.2018
                        //added on 24.07.2020
                        BillContents += String.format("%-24s", "YOU CAN PAY WITHIN 7");
                        BillContents += String.format("%-24s", "DAYS FROM BILL DATE");

                        if ((blunts / rs.getInt(99)) > 30 && (rs.getString(8).equals("RGVY") || rs.getString(8).equals("BJVY") || rs.getString(8).equals("KJ"))) {
                            BillContents += widthoff + String.format("%-48s", "Ur Tariff Change to Domestic");
                        }
                        if(rs.getString(48).length()>5) {
                            BillContents += widthoff + String.format("%-48s", rs.getString(48));
                        }
                        //  BillContents += widthoff + String.format("%-24s", "Bl Gen By: " + Usernm);

                        BillContents += widthoff + String.format("%-48s",  "Plz. Stay Away from Electric line/Sub-Stations");
                        BillContents += widthoff + "\n\n\n";
                    }
                } //while loop close
                rs.close();
                databaseAccess.close();
                AnalogicsImpactPrinter print = new AnalogicsImpactPrinter();
                if (mmDevice.getName().equals("IMPACTSPT") || mmDevice.getName().substring(0,3).equals("AT2")) {
                    //create the object for the AnalogicsImpactPrinter class

                    //  print.FixLengthOf("BILL",24);
                    //call the Call_Printer(String BTaddress, String printdata)method in Call_Printer class
                    //pass the parameters to the Call_Printer method to print the data
                    // print.Call_Printer(mmDevice.getAddress(), BillContents);
                    print.openBT(mmDevice.getAddress());
                    print.printData(BillContents);
                    print.closeBT();
                } else if (mmDevice.getName().substring(0,5).equals("SILBT")) {
                   /* Log.d("DemoApp", "mmDevice.getName()  " +mmDevice.getAddress());
                    printer=new Printer2inch();
                    connectTodevice(mmDevice.getAddress());
                    //getConnectionStatus is used for reprint purpose if necessary .returns value  if printer is connected or not
                    Log.d("DemoApp", "Printing Sucess ");
                    */
                    //doubleHeight = font_Double_Height_Width_On();
                    //widthoff = font_Double_Height_Width_Off();
                    //   printmsg = "<0x09><0x20>Welcome<0x00> to the   new <0x10>world<0x0A>";
                    //  printmsg = doubleHeight+"Welcome";
                    // printmsg += widthoff+"to the new ";
                    // printmsg +=doubleHeight+"world";
                    // String str = printmsg;
                    BillContents += widthoff + "\n\n";
                    print.openBT(mmDevice.getAddress());

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
                                    ;
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

                }//else if condn close

            }//main if condn clos

            else if(mmDevice.getName().equals("BTprinter8127")) {

                BillContents = "";
                filldata = "";
                // Bluetooth_Printer_2inch_Impact BPImpact = new Bluetooth_Printer_2inch_Impact();
                //create the object for the Bluetooth_Printer_2inch_Impact class
                //call the any  method in Bluetooth_Printer_2inch_Impact class and save the return value in String variable
                String doubleHeight = font_Double_Height_Width_On();
                String widthoff =  font_Double_Height_Width_Off();
                String formfeed = "";
                String Billformat = "Thermal";
                Calendar c = Calendar.getInstance();
                SimpleDateFormat month = new SimpleDateFormat("MMM-yy");
                String strmonth = month.format(c.getTime());
                SimpleDateFormat year = new SimpleDateFormat("dd-MM-yy");
                Date vardate = null;
                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();

                //for image to string conversion
                Cursor rs2=null;
                byte[] byteImage2=null;
                String strUpdateSQL_03 = "SELECT BILL_FLAG, MTR_IMAGE,CONS_ACC FROM BILL_SBM_DATA WHERE CONS_ACC = '" + AccNum + "'";
                rs2 = DatabaseAccess.database.rawQuery(strUpdateSQL_03, null);
                byteImage2 = null;
                String flag="";
                while (rs2.moveToNext()) {
                    flag=rs2.getString(0);
                    byteImage2 = rs2.getBlob(1);
                }
                rs2.close();
                String encodedImage = Base64.encodeToString(byteImage2, Base64.DEFAULT);//encoding
                byte[] decodedImage = Base64.decode(encodedImage, Base64.DEFAULT);//decodding
                Log.d("DemoApp","encodedImage "+encodedImage);
                /////////////////////




                //to get the current version of software
                String strSelectSQL_02 = "select file_name,version_flag from File_desc where version_flag=1";
                Cursor rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
                while (rs1.moveToNext()) {
                    version = rs1.getString(0);
                }
                rs1.close();
                String strUpdateSQL_01 = "SELECT strftime('%d-%m-%Y',MOD_ON),CONS_ACC,CON_NAME,CON_ADD1,CON_ADD2,CON_CLST," +
                        " ROUTE_NO,CONS_CODE,CASE WHEN CAT_CODE='01' THEN 'DOM' WHEN CAT_CODE='02' THEN 'RGVY' WHEN CAT_CODE='04' THEN 'BJVY' " +
                        " WHEN CAT_CODE='05' THEN 'KJ' WHEN CAT_CODE='06' THEN 'GPS' WHEN CAT_CODE='22' THEN 'SPP' ELSE 'NIL' END TARIFF,DIVISION," +
                        " SUBDIVISION,SECTION,MTR_SL_NO,MTR_TP, CASE WHEN MF >= 1 THEN MF ELSE 1 END AS MF,CASE WHEN MTR_OWN='C' THEN 'CONSUMER'  " +
                        " WHEN MTR_SL_NO IS NULL THEN ' ' ELSE 'CESU' END AS MTR_OWN ,MTR_DIGIT,MTR_MAKE,USAGE,LOAD,PRV_READ,CON_DATE," +
                        " strftime('%d-%m-%Y',OLD_DATE),OLD_STATUS,DPS,MISC_CHG,CR_ADJ,DB_ADJ,PRV_AMOUNT,HL_UNIT,OLD_AVG,LST_PAY_DATE,LST_BOOK_NO," +
                        " LST_RECT_NO,TOT_PAY,NO_OF_MON,ED_EXEMPT,ARR_INS_FEE,NEW_MTR_NO,NEW_MTR_INI_READ,MTR_CHG_DATE,SD_INT,ASD,ASD_ARR,INSTALLATION_NO," +
                        " BILL_NO,CUST_ID,BILL_PRN_HEADER,BILL_PRN_FOOTER,PRV_YR_ARR,CUR_YR_ARR,ULF,LRC,ABN_READING,BILL_MON,ECS_LIMIT," +
                        " strftime('%d-%m-%Y',ECS_VALID)," +
                        " PST_METER_READ,P_STATUS,MTR_COND,MTR_INS_STATUS,UNITS,BILL_BASIS,EC,MMFC,MTR_RENT,ED,CUR_TOTAL,REBATE,BILL_TOTAL,AVG_UNIT,RECP_NO," +
                        " CHQ_NO,CHQ_DATE,BANK,RECT_AMT,strftime('%d-%m-%Y',DUE_DATE),DISCONNECTION_DATE,MTR_TIME,strftime('%d-%m-%Y',BILL_DATE),BILLED_MD," +
                        " PRS_MD,CESU_DATE,DIV_CODE,BILL_FLAG, CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG," +
                        " SENT_FLAG,UNIT_SLAB1,RATE_SLAB1,EC_SLAB1,UNIT_SLAB2,RATE_SLAB2,EC_SLAB2,UNIT_SLAB3,RATE_SLAB3,EC_SLAB3,UNIT_SLAB4," +
                        " RATE_SLAB4,EC_SLAB4,NO_BILLED_MONTH FROM BILL_SBM_DATA WHERE CONS_ACC = '" + AccNum + "'";
                Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
                Cursor rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    int noofmonth = rs.getInt(99);
                    monthname = rs.getInt(54);
                    // vardate=year.parse(rs.getString(79));
                    double roundupto = 0;
                    if (Billformat.equals("Thermal")) {
                        BillContents = "\n\n";
                        BillContents += doubleHeight + String.format("%-32s", "       TEST-BILL PILOT");
                        BillContents += doubleHeight + String.format("%-32s", "           TPCODL");
                        BillContents += doubleHeight + String.format("%-32s", "       ELECTRICITY BILL");
                        BillContents += widthoff+"------------------------------"+"\n";
                        // BillContents +=String.format("%-10s%14s","","testing of the consumer where");
                        //BillContents +=String.format("%-10s%-14s","","testing of the consumer where");
                        //BillContents +=String.format("%10s%14s","","testing of the consumer where");
                        //BillContents +=String.format("%10s%-14s","","testing of the consumer where");
                        BillContents += widthoff + String.format("%-12s%-20s", "BILL NONTH:", strmonth.toUpperCase());
                        BillContents += String.format("%8s%10s%6s%8s","BILL DT:", convertDateFormat(rs.getString(79), "DD-MM-YYYY")," TIME:",(rs.getString(78).substring(0, 2) + ":" + rs.getString(78).substring(2, 4))); //+ String.format("%10s", (rs.getString(78).substring(0, 2) + ":" + rs.getString(78).substring(2, 4)));
                        BillContents += String.format("%8s%-5s%7s%12s","BILL NO:", rs.getInt(45),"VER:", version);
                        BillContents += String.format("%-12s%-20s", "BILLED FOR:", (rs.getInt(99) + " MONTHS"));
                        BillContents += String.format("%-5s%27s", "DIV:", rs.getString(9));
                        BillContents += String.format("%8s%24s", "SUB DIV:", rs.getString(10));
                        BillContents += String.format("%-9s%23s", "SECTION:", rs.getString(11));
                        BillContents += widthoff+ String.format("%-10s%-22s", "SBM NO:", "NA");
                        BillContents += doubleHeight + String.format("%-16s%16s","CONSUMER A/C NO:", rs.getString(1));
                        BillContents += widthoff + String.format("%8s%-24s", "CUST ID:", rs.getString(46));
                        BillContents += String.format("%-8s%-24s", "OA NO:", rs.getString(7));
                        BillContents += String.format("%-10s%-22s", "INST NO:", rs.getString(44));
                        BillContents += String.format("%-10s%-22s", "MTR SL NO:", rs.getString(12));
                        BillContents += String.format("%-14s%-18s", "MTR OWNER:", rs.getString(15));//mtrowner
                        BillContents += String.format("%-10s%-22s", "MAKE:", "");
                        BillContents += String.format("%-10s%-22s", "METER MF:", rs.getString(14));

                        if (rs.getString(2).trim().length() <= 26) {
                            BillContents += String.format("%-6s%-26s", "NAME:", rs.getString(2));
                            BillContents += "" + "\n";
                        } else {
                            BillContents += String.format("%-6s%-58s", "NAME:", rs.getString(2));
                        }
                        StringBuilder strAddr = new StringBuilder(rs.getString(3) + "," + rs.getString(4));
                        if (rs.getString(3).trim().length() + rs.getString(4).trim().length() > 24) {
                            strAddr.setLength(48);
                            BillContents += String.format("%-7s%-57s", "ADDRS:", strAddr);
                        } else {
                            BillContents += String.format("%-7s%-25s", "ADDRS:", (rs.getString(3) + "," + rs.getString(4)));
                            BillContents += "" + "\n";
                        }
                        BillContents += String.format("%-10s%-22s", "USAGE:", "");//usage
                        BillContents += String.format("%6s%-6s%6s%-14.2f", "T.CAT:", rs.getString(8), "CD:", rs.getDouble(19));
                        BillContents += String.format("%10s%-22s", "AVG.UNITS:", rs.getString(30));
                        BillContents += "------------------------------"+"\n";
                        //  BillContents += "\n";
                        // BillContents += doubleHeight + String.format("%-24s", "");

                        BillContents += "        RDG" + "      DATE" + "      STS" + "\n";
                        //  BillContents += String.format("%5s%-7s%-10s%1s%-1s","", rs.getString(57), rs.getString(79),"",rs.getString(59));
                        //   BillContents += String.format("%5s%-7s%-10s%1s%-1s","", rs.getString(20), rs.getString(22),"",rs.getString(23));
                        BillContents += widthoff + String.format("%-7s%-8s%-8s%1s%4s%4s", "PRES:", rs.getString(57), convertDateFormat(rs.getString(79), "DD-MM-YYYY"), "", rs.getString(59),"");
                        BillContents += widthoff + String.format("%-7s%-8s%-8s%1s%4s%4s", "PREV:", rs.getString(20), convertDateFormat(rs.getString(22), "DD-MM-YYYY"), "", rs.getString(23),"");

                        BillContents += String.format("%-16s%-16s", "UNITS ADVANCE:", rs.getString(61));
                        if (rs.getString(62).equals("N")) {
                            BillContents += String.format("%-10s%-22s", "BILL BASIS:", "ACTUAL");
                        } else {
                            BillContents += String.format("%-14s%-18s", "", "AVERAGE");
                        }
                        BillContents += "------------------------------"+"\n";
                        //  BillContents += widthoff + "\n";
                        //  Bill calulation start
                        BillContents += widthoff + String.format("%-10s%22.2f", "MFC CHRG:", rs.getDouble(64));
                        blunts = rs.getInt(61);
                        //Bill slab Changed by Santi on 13.01.2016
                        if (rs.getDouble(89) > 0) {
                            BillContents += String.format("%-4s%-10s%18.2f", "EC1:", rs.getString(87) + "X" + rs.getString(88) + "0=", rs.getDouble(89));
                        } else {
                            BillContents += "\n";
                        }
                        if (rs.getDouble(92) > 0) {
                            BillContents += String.format("%-4s%-10s%18.2f", "EC2:", rs.getString(90) + "X" + rs.getString(91) + "0=", rs.getDouble(92));
                        } else {
                            BillContents += "\n";
                        }
                        if (rs.getDouble(95) > 0) {
                            BillContents += String.format("%-4s%-10s%18.2f", "EC3:", rs.getString(93) + "X" + rs.getString(94) + "0=", rs.getDouble(95));
                        } else {
                            BillContents += "\n";
                        }
                        if (rs.getDouble(98) > 0) {
                            BillContents += String.format("%-4s%-10s%18.2f", "EC4:", rs.getString(96) + "X" + rs.getString(97) + "0=", rs.getDouble(98));
                        } else {
                            BillContents += "\n";
                        }
                        //end
                        BillContents += String.format("%-14s%18.2f", "ED CHRG:", rs.getDouble(66));
                        BillContents += widthoff+ String.format("%-14s%18.2f", "METER RENT:", rs.getDouble(65));
                        BillContents += doubleHeight + String.format("%-14s%18.2f", "PRES.BILL AMT:", rs.getDouble(67));
                        BillContents += widthoff+"------------------------------"+"\n";
                        BillContents += String.format("%-15s%17.2f", "PREV FY ARREAR:", rs.getDouble(49));
                        BillContents += String.format("%-15s%17.2f", "CURR FY ARREAR:", rs.getDouble(50));//prv yr arr
                        Log.d("DemoApp", "DPS  " + rs.getDouble(24));
                        BillContents += String.format("%-16s%16.2f", "DPS CHRG:", rs.getDouble(24));
                        if (rs.getInt(27) > 0) {
                            BillContents += String.format("%-16s%4s%12.2f", "ADJUSTMENTS:", "(+)", rs.getDouble(27));
                        } else if (rs.getInt(26) > 0) {
                            BillContents += String.format("%-16s%4s%12.2f","ADJUSTMENTS:", "(-)", rs.getDouble(26));
                        } else {
                            BillContents += String.format("%-16s%16s","ADJUSTMENTS:", "0.00");
                        }
                        if (rs.getString(23).equals("P")) {
                            BillContents += String.format("%-14s%18.2f", "P.ADJ.AMT :", rs.getDouble(28));
                        } else {
                            BillContents += String.format("%32s", " ");
                        }
                        if (rs.getInt(25) > 0) {
                            BillContents += String.format("%-14s%18.2f", "MISC.CHRG:", rs.getDouble(25));
                        } else {
                            BillContents += String.format("%-14s%18s", "MISC.CHRG:", "0.00");
                        }
                        if (rs.getInt(41) > 0) {
                            BillContents += String.format("%-14s%18.2f", "", rs.getDouble(41));
                        } else {
                            BillContents += String.format("%-32s", "-TEST BILL -- TEST BILL");
                        }
                        if (rs.getInt(42) > 0) {
                            BillContents += String.format("%-14s%18.2f", "", rs.getDouble(42));
                        } else {
                            BillContents += String.format("%-32s", "-TEST BILL -- TEST BILL");
                        }
                        if (rs.getInt(43) > 0) {
                            BillContents += String.format("%-14s%18.2f", "", rs.getDouble(43));
                        } else {
                            BillContents += String.format("%-32s", "-TEST BILL -- TEST BILL");
                        }
                        BillContents += "------------------------------"+"\n";
                        BillContents += String.format("%-16s%16.2f", "TOTAL AMOUNT:", rs.getDouble(69));
                        BillContents += widthoff+ String.format("%-12s%20.2f", "REBATE:", rs.getDouble(68));
                        BillContents += doubleHeight + String.format("%-19s%13.2f", "TOTAL BL BY DUE DT:", (double) Math.round(rs.getDouble(69) - rs.getDouble(68)));

                        roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                        if (roundupto > 0) {
                            BillContents += widthoff + String.format("%-20s%4s%8.2f","ROUNDED UPTO:", "(+)", roundupto);
                        } else {
                            BillContents += widthoff + String.format("%-20s%4s%8.2f","ROUNDED UPTO:", "(-)", Math.abs(roundupto));
                        }
                        BillContents += doubleHeight + String.format("%-16s%16s", "REBATE DATE:", rs.getString(76));
                        BillContents += doubleHeight+ String.format("%-18s%14.2f", "PAY AFT DUE DT:", (double) Math.round(rs.getDouble(69)));
                        roundupto = Math.round(rs.getDouble(69)) - rs.getDouble(69);
                        if (roundupto > 0) {
                            BillContents += widthoff + String.format("%-20s%4s%8.2f","ROUNDED UPTO:", "(+)", roundupto);
                        } else {
                            BillContents += widthoff + String.format("%-20s%4s%8.2f","ROUNDED UPTO:", "(-)", Math.abs(roundupto));
                        }
                        BillContents += widthoff+"------------------------------"+"\n";
                        BillContents += doubleHeight+"       LAST PAYMENT DETAILS"+"\n";
                        BillContents += widthoff+"--------------------------------";
                        BillContents += String.format("%9s%-23s", "B.NO-RNO:", rs.getString(32) + "-" + rs.getString(33));
                        BillContents += String.format("%4s%-12.2f%4s%-12s", "AMT:", rs.getDouble(34), "DT:", rs.getString(31));
                        BillContents += widthoff+"------------------------------"+"\n";
                        BillContents += doubleHeight+"          ECS MESSAGE" + "\n";
                        BillContents += widthoff+"------------------------------"+"\n";
                        //Ecs Message printing///////
                        if (rs.getDouble(69) < rs.getDouble(55) && rs.getInt(85) == 1) { //here rs(85) is validation date check query CASE WHEN CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG
                            BillContents += widthoff + widthoff + String.format("%-32s", "Bg dbtd to Bank thru ECS");//ECS Message
                        } else if (rs.getDouble(69) > rs.getDouble(55) && rs.getInt(85) == 1) {
                            BillContents += widthoff + String.format("%-32s", "Val excd-pay cash/chq");//ECS Message
                        } else if (rs.getInt(85) == 0 && rs.getString(56) != null) {
                            BillContents += widthoff + String.format("%-32s", "Dt lpsd-pay cash/chq");//ECS Message
                        } else {
                            BillContents += widthoff + String.format("%-32s", " ")+"\n";
                        }
                        BillContents += "------------------------------"+"\n";
                        if ((blunts / rs.getInt(99)) > 30 && (rs.getString(8).equals("RGVY") || rs.getString(8).equals("BJVY") || rs.getString(8).equals("KJ"))) {
                            BillContents += widthoff + String.format("%-64s", "Ur Tariff Change to Domestic")+"\n";
                        } else {
                            //BillContents += widthoff + String.format("%-64s", "")+"\n";
                        }
                        BillContents+="PLEASE PAY CURRENT DUE ALONG WITH ARREAR TO AVOID DISCONNECTION"+"\n";
                        BillContents+="FOR BILLING INFORMATION AND ONLINE PAYMENT:";
                        BillContents+="VISIT WWW.TPCENTRALODISHA.COM"+"\n";
                        BillContents += widthoff + String.format("%-32s", "Bl Generated By: " + Usernm)+"\n";
                        //BillContents += doubleHeight + String.format("%-24s", " ");
                        // BillContents += doubleHeight + String.format("%-24s", " ");
                        BillContents += widthoff + "\n\n\n\n";
                    }
                } //while loop close
                rs.close();
                //  Bill1=format+Bill1+"sdsd";
                mmOutputStream.write(BillContents.getBytes());
            }//main else if condn close

        } catch (NullPointerException e22) {
            e22.printStackTrace();
            Toast.makeText(ReprintBlActivity.this, "message8"+e22, Toast.LENGTH_LONG).show();
            Toast.makeText(ReprintBlActivity.this, "message120"+lapdvar, Toast.LENGTH_LONG).show();
        } catch (Exception e23) {
            Toast.makeText(ReprintBlActivity.this, "message9"+e23, Toast.LENGTH_LONG).show();
            Toast.makeText(ReprintBlActivity.this, "message120"+lapdvar, Toast.LENGTH_LONG).show();
            e23.printStackTrace();
        }
        strPrntMsg.setText("Data Sent to Bluetooth Printer");
        //Reprint The Bill
        Button ReprntBl = (Button) findViewById(R.id.ReprntBl);
        ReprntBl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Intent reprint = new Intent(getParent(),BillPrintActivity.class);
                //    startActivity(reprint);

                if(devicename.equals("nodevice")) {
                    try {
                        devicename = findBT();
                    } catch (Exception ex) {
                        Toast.makeText(ReprintBlActivity.this, "message12", Toast.LENGTH_LONG).show();
                    }
                }
                if(devicename.equals("BTprinter8127")){
                    try{
                        openBT();
                    } catch (Exception ex) {
                        Toast.makeText(ReprintBlActivity.this, "message14", Toast.LENGTH_LONG).show();

                    }
                }
                if(devicename.substring(0,5).equals("SILBT")){//mmDevice.getName().equals("SILBT-1070") || mmDevice.getName().equals("SILBT-2618")
                    try{
                            openBT();
                    } catch (Exception ex) {
                        //   Toast.makeText(BillPrintActivity.this, "message14", Toast.LENGTH_LONG).show();

                    }
                }
                try{
                    sendData();
                } catch (Exception ex) {
                    Toast.makeText(ReprintBlActivity.this, "message13", Toast.LENGTH_LONG).show();
                }try{
                    if(!devicename.substring(0,5).equals("SILBT")) {
                        closeBT();
                    }else{
                        closeBT();
                    }
                } catch (Exception ex) {
                    Toast.makeText(ReprintBlActivity.this, "message14", Toast.LENGTH_LONG).show();
                }

            }
        });//end
        //Exit
        Button Exit = (Button) findViewById(R.id.ExitPaperfd);
        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
             //   System.exit(0);

            }
        });//end
        //Continue
        Button contd = (Button) findViewById(R.id.contd);
        contd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reports1 = new Intent(getApplicationContext(), SearchDataActivity.class);
                startActivity(reports1);
                finish();
                //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //     .setAction("Action", null).show();

            }
        });//end
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
            Log.d("DemoApp", "e   " + e);
        }
        return strTokenValueRevDt;
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
            Toast.makeText(ReprintBlActivity.this, "message10"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(ReprintBlActivity.this, "message11"+e, Toast.LENGTH_LONG).show();
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
    private void createPrintData() {
        int blunts=0;
        String conversion="";
        int lapdvar=0;
        String st="";

        try {
            st=printer.reset();
            String BlPrepTm = "";
            String Billformat = "PrePrinted";
            String version="";
            Calendar c = Calendar.getInstance();
            SimpleDateFormat month = new SimpleDateFormat("MMM-yy");
            String strmonth = month.format(c.getTime());
            SimpleDateFormat year = new SimpleDateFormat("dd-MM-yy");
            Date vardate = null;
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            //to get the current version of software
            String strSelectSQL_02 = "select file_name,version_flag from File_desc where version_flag=1";
            Cursor rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
            while (rs1.moveToNext()) {
                version = rs1.getString(0);
            }
            rs1.close();
            //getting user name
            ////
            String strUpdateSQL_01 = "SELECT strftime('%d-%m-%Y',MOD_ON),CONS_ACC,CON_NAME,CON_ADD1,CON_ADD2,CON_CLST," +
                    " ROUTE_NO,CONS_CODE,CASE WHEN CAT_CODE='01' THEN 'DOM' WHEN CAT_CODE='02' THEN 'RGVY' WHEN CAT_CODE='04' THEN 'BJVY' " +
                    " WHEN CAT_CODE='05' THEN 'KJ' WHEN CAT_CODE='06' THEN 'GPS' WHEN CAT_CODE='22' THEN 'SPP' ELSE 'NIL' END TARIFF,DIVISION," +
                    " SUBDIVISION,SECTION,MTR_SL_NO,MTR_TP, CASE WHEN MF >= 1 THEN MF ELSE 1 END AS MF,CASE WHEN MTR_OWN='C' THEN 'CONSUMER'  " +
                    " WHEN MTR_SL_NO IS NULL THEN ' ' ELSE 'CESU' END AS MTR_OWN ,MTR_DIGIT,MTR_MAKE,USAGE,LOAD,PRV_READ,CON_DATE," +
                    " strftime('%d-%m-%Y',OLD_DATE),OLD_STATUS,DPS,MISC_CHG,CR_ADJ,DB_ADJ,PRV_AMOUNT,HL_UNIT,OLD_AVG,LST_PAY_DATE,LST_BOOK_NO," +
                    " LST_RECT_NO,TOT_PAY,NO_OF_MON,ED_EXEMPT,ARR_INS_FEE,NEW_MTR_NO,NEW_MTR_INI_READ,MTR_CHG_DATE,SD_INT,ASD,ASD_ARR,INSTALLATION_NO," +
                    " BILL_NO,CUST_ID,BILL_PRN_HEADER,BILL_PRN_FOOTER,PRV_YR_ARR,CUR_YR_ARR,ULF,LRC,ABN_READING,BILL_MON,ECS_LIMIT," +
                    " strftime('%d-%m-%Y',ECS_VALID)," +
                    " PST_METER_READ,P_STATUS,MTR_COND,MTR_INS_STATUS,UNITS,BILL_BASIS,EC,MMFC,MTR_RENT,ED,CUR_TOTAL,REBATE,BILL_TOTAL,AVG_UNIT,RECP_NO," +
                    " CHQ_NO,CHQ_DATE,BANK,RECT_AMT,strftime('%d-%m-%Y',DUE_DATE),DISCONNECTION_DATE,MTR_TIME,strftime('%d-%m-%Y',BILL_DATE),BILLED_MD," +
                    " PRS_MD,CESU_DATE,DIV_CODE,BILL_FLAG, CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG," +
                    " SENT_FLAG,UNIT_SLAB1,RATE_SLAB1,EC_SLAB1,UNIT_SLAB2,RATE_SLAB2,EC_SLAB2,UNIT_SLAB3,RATE_SLAB3,EC_SLAB3,UNIT_SLAB4," +
                    " RATE_SLAB4,EC_SLAB4,NO_BILLED_MONTH FROM BILL_SBM_DATA WHERE CONS_ACC = '" + AccNum + "'";
            Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
            Cursor rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
            ////
            while (rs.moveToNext()) {
                int noofmonth = rs.getInt(99);
                int monthname = rs.getInt(54);
                // vardate=year.parse(rs.getString(79));
                double roundupto = 0;
                String Mtrtype = rs.getString(13);
                String Phase = "01";
                if (Mtrtype.equals("02") || Mtrtype.equals("09")) {
                    Phase = "03";
                }
                if (rs.getString(78).length() == 5) {
                    BlPrepTm = "0" + rs.getString(78).substring(0, 1) + ":" + rs.getString(78).substring(1, 3);
                } else if (rs.getString(78).length() == 4) {
                    BlPrepTm = "00:" + rs.getString(78).substring(0, 2);
                } else if (rs.getString(78).length() == 3) {
                    BlPrepTm = "00:0" + rs.getString(78).substring(0, 1);
                } else if (rs.getString(78).length() == 2 || rs.getString(78).length() == 1) {
                    BlPrepTm = "00:00";
                } else {
                    BlPrepTm = rs.getString(78).substring(0, 2) + ":" + rs.getString(78).substring(2, 4);
                }
                st=printer.reset();
                st+=printer.printNewLine();
                st+=printer.printLine(" ");
                st+=printer.printNewLine();
                st+=printer.printLine(leftAppend1(strmonth.toUpperCase(), 24));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                String revdt = "";
                revdt = convertDateFormat(rs.getString(79), "DD-MM-YY");
                st+= printer.printLine(leftAppend2(revdt, 6, BlPrepTm, 24));
                st += printer.printLine(leftAppend2(rs.getString(45), 10, version, 24));
                String revStr = "";
                revStr = rs.getString(99) + " MONTHS";
                st+=printer.printLine(leftAppend1(revStr, 24));
                st+=printer.printLine(leftAppend1(rs.getString(9), 24));
                st+=printer.printLine(leftAppend1(rs.getString(10), 24));
                st+=printer.printLine(leftAppend1(rs.getString(11), 24));
                st+=printer.printLine(leftAppend1("NA", 24));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st += printer.printLine(leftAppend1(rs.getString(1), 24));//cons_acc double height
                printer.restCHARASTYLE();
                st += printer.printLine(leftAppend1(rs.getString(46), 24)); // width off
                st += printer.printLine(leftAppend1(rs.getString(7), 24));
                st += printer.printLine(leftAppend1(rs.getString(44), 24));
                st += printer.printLine(leftAppend1(rs.getString(12), 24));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                st += printer.printLine(leftAppend1(rs.getString(15), 24));//mtrowner
                st+=printer.printLine(leftAppend1(" ", 24));
                st += printer.printLine(leftAppend1(rs.getString(14), 24));
                if (rs.getString(2).trim().length() <= 18) {
                    st += printer.printLine(leftAppend1(rs.getString(2), 24));//name
                    // st+= printer.printLine(leftAppend1("  ", 24));
                    st+=printer.printNewLine();
                } else {
                    st+= printer.printLine(leftAppend2(rs.getString(2), 4, "", 48));

                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                StringBuilder strAddr = new StringBuilder(rs.getString(3) + "," + rs.getString(4));
                Log.d("DemoApp", "add 1  " + strAddr.length());
                if (rs.getString(3).trim().length() + rs.getString(4).trim().length() > 17) {
                    Log.d("DemoApp", "add 2  " + strAddr.length());
                    if (strAddr.length() >= 42) {
                        strAddr.setLength(42);
                    }
                    if (strAddr.length() <= 19) {
                        st+=printer.printLine(leftAppend2(strAddr.toString(), 4, "", 24));
                        st+=" ";
                    } else {
                        st+=printer.printLine(leftAppend2(strAddr.toString(), 4, "", 48));
                    }
                } else {
                    st+=printer.printLine(leftAppend1((rs.getString(3) + "," + rs.getString(4)), 24));
                    //  st+= printer.printLine(leftAppend1("  ", 24));
                    st+=printer.printNewLine();
                }
                String ibond = "";
                ibond = rs.getString(18);
                if (rs.getString(18).equals("I")) {
                    ibond = "I-BOND";
                }
                st+=printer.printLine(leftAppend2(ibond, 6, "Ph:" + Phase, 24));//usage
                st+=printer.printLine(leftAppend2(rs.getString(8), 6, rs.getDouble(19) + "", 24));
                st+=printer.printLine(leftAppend1(rs.getString(30), 24));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                // BillContents += "\n";
                //  BillContents += "\n";
                st+=printer.printNewLine();
                st+=printer.printNewLine();
                revStr = "";
                revStr = convertDateFormat(rs.getString(79), "DD-MM-YYYY");
                st+=printer.printLine(leftAppend3(rs.getString(57), 7, revStr, 1, rs.getString(59), 24));
                revStr = "";
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                revStr = convertDateFormat(rs.getString(22), "DD-MM-YYYY");
                st+=printer.printLine(leftAppend3(rs.getString(20), 7, revStr, 1, rs.getString(23), 24));
                st+=printer.printLine(leftAppend1(rs.getString(61), 24));
                if (rs.getString(62).equals("N")) {
                    st+=printer.printLine(leftAppend1("ACTUAL", 24));
                } else {
                    st+=printer.printLine(leftAppend1("AVERAGE", 24));
                }
                // BillContents += doubleHeight+String.format("%-24s", "");
                // st+= printer.printLine(leftAppend1("  ", 24));
                st+=printer.printNewLine();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                //  Bill calulation start
                String formattedData = "";
                // formattedData = String.format("%.02f", rs.getDouble(64));
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(64)), 24));
                blunts = rs.getInt(61);
                //Bill slab Changed by Santi on 13.01.2016
                if (rs.getDouble(89) > 0) {
                    st+=printer.printLine(leftAppend2(rs.getString(87) + "X" + rs.getDouble(88) + "0=", 3, String.format("%.02f", rs.getDouble(89)) + "", 24));
                } else {
                    st+=printer.printLine(leftAppend1(" ", 24));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                if (rs.getDouble(92) > 0) {
                    st+=printer.printLine(leftAppend2(rs.getString(90) + "X" + rs.getDouble(91) + "0=", 3, String.format("%.02f", rs.getDouble(92)) + "", 24));
                } else {
                    // st+=printer.printLine(leftAppend1(" ", 24));
                    st+=printer.printNewLine();
                }
                if (rs.getDouble(95) > 0) {
                    st+=printer.printLine(leftAppend2(rs.getString(93) + "X" + rs.getDouble(94) + "0=", 3, String.format("%.02f", rs.getDouble(95)) + "", 24));
                } else {
                    // st+=printer.printLine(leftAppend1(" ", 24));
                    st+=printer.printNewLine();
                }
                if (rs.getDouble(98) > 0) {
                    st += printer.printLine(leftAppend2(rs.getString(96) + "X" + rs.getDouble(97) + "0=", 3, String.format("%.02f", rs.getDouble(98)) + "", 24));
                } else {
                    // st+=printer.printLine(leftAppend1(" ", 24));
                    st+=printer.printNewLine();
                }
                //end
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(66)) + "", 24));
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(65)) + "", 24));
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(67)) + "", 24));//double height
                printer.restCHARASTYLE();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                //  st+=printer.printLine(leftAppend1(" ", 24));
                st+=printer.printNewLine();
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(49)) + "", 24));//previous yr arr
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(50)) + "", 24));
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(24)) + "", 24));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                if (rs.getInt(27) > 0) { //adjustments
                    st+=printer.printLine(leftAppend2("(+)", 14, String.format("%.02f", rs.getDouble(27)) + "", 24));

                } else if (rs.getInt(26) > 0) {
                    st+=printer.printLine(leftAppend2("(-)", 14, String.format("%.02f", rs.getDouble(26)) + "", 24));
                } else {
                    st+=printer.printLine(leftAppend1("0.00", 24));
                }
                if (rs.getString(23).equals("P")) {
                    st+=printer.printLine(leftAppend2("P.ADJ.AMT :", 0, String.format("%.02f", rs.getDouble(28)) + "", 24));
                } else {
                    //  st+=printer.printLine(leftAppend1("  ", 24));
                    st+=printer.printNewLine();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                if (rs.getInt(25) > 0) {
                    st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(25)) + "", 24));
                } else {
                    // st+=printer.printLine(leftAppend1(" ", 24));
                    st+=printer.printNewLine();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                if (rs.getInt(41) > 0) {
                    st+=printer.printLine(leftAppend2("SD AVAIL:", 0, String.format("%.02f", rs.getDouble(41)) + "", 24));
                } else {
                    // st+=printer.printLine(leftAppend1(" ", 24));
                    st+=printer.printNewLine();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                if (rs.getInt(42) > 0) {
                    st+=printer.printLine(leftAppend2("ASD:", 0, String.format("%.02f", rs.getDouble(42)) + "", 24));
                } else {
                    // st+=printer.printLine(leftAppend1(" ", 24));
                    st+=printer.printNewLine();
                }
                if (rs.getInt(43) > 0) {
                    st+=printer.printLine(leftAppend2("ASD ARR:", 0, String.format("%.02f", rs.getDouble(43)) + "", 24));
                } else {
                    // st+=printer.printLine(leftAppend1(" ", 24));
                    st+=printer.printNewLine();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                st+=printer.printLine(leftAppend1(" ", 24));//Penalty amount showing 30.06.2017
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(69)) + "", 24));//total amount
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(68)) + "", 24));//rebate amount
                double revamt = 0;
                revamt = (double) Math.round(rs.getDouble(69) - rs.getDouble(68));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1(String.format("%.02f", revamt) + "", 24));//total bill by due date double height
                printer.restCHARASTYLE();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                revamt = 0;
                if (roundupto > 0) {
                    st+=printer.printLine(leftAppend2("(+)", 14, String.format("%.02f", roundupto) + "", 24));
                } else {
                    revamt = Math.abs(roundupto);
                    st+=printer.printLine(leftAppend1("(-)" + String.format("%.02f", Math.abs(roundupto)), 24));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                //st+=printer.printLine(leftAppend1(" ", 24));
                st+=printer.printNewLine();
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1(rs.getString(76), 24));//rebate date
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                //  printer.restCHARASTYLE();
                //changes on 31.01.2017 if total amount <0 and total amt-rebate amt<0
                double payaftdt = 0;
                if (rs.getDouble(69) < 0 && ((double) Math.round(rs.getDouble(69) - rs.getDouble(68))) < 0) {
                    payaftdt = ((double) Math.round(rs.getDouble(69) - rs.getDouble(68)));
                    roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                } else {
                    payaftdt = (double) Math.round(rs.getDouble(69));
                    roundupto = Math.round(rs.getDouble(69)) - rs.getDouble(69);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                ////
                // st+=leftAppend("%-14s%10.2f", "", (double) Math.round(rs.getDouble(69)));//pay after due date disable on 31.01.17
                // printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1(String.format("%.02f", payaftdt) + "", 24));//pay after due date enable on 31.01.17
                printer.restCHARASTYLE();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                if (roundupto > 0) {
                    st+=printer.printLine(leftAppend1("(+)" + String.format("%.02f", roundupto), 24));
                } else {
                    st+=printer.printLine(leftAppend1("(-)" + String.format("%.02f", Math.abs(roundupto)), 24));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                //  st+=printer.printLine(leftAppend1(" ", 24)); //dissable on 03.04.2017
                // st+=printer.printLine(leftAppend1(" ", 24));//dissable on 03.04.2017
                st+=printer.printNewLine();
                st+=printer.printNewLine();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                st+=printer.printLine(leftAppend1(rs.getString(32) + "-" + rs.getString(33), 24));
                st+=printer.printLine(leftAppend2(rs.getDouble(34) + "", 3, rs.getString(31), 24));
                st+=printer.printNewLine();
                st+=printer.printNewLine();
                //  st+=printer.printLine(leftAppend1(" ", 24));
                //  st+=printer.printLine(leftAppend1(" ", 24));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                //Ecs Message printing///////
                if (rs.getDouble(69) < rs.getDouble(55) && rs.getInt(85) == 1) { //here rs(85) is validation date check query CASE WHEN CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG
                    st+=printer.printLine(leftAppend1("Bg dbtd to Bank thru ECS", 24));//ECS Message
                } else if (rs.getDouble(69) > rs.getDouble(55) && rs.getInt(85) == 1) {
                    st+=printer.printLine(leftAppend1("Val excd-pay cash/chq", 24));//ECS Message
                } else if (rs.getInt(85) == 0 && rs.getString(56) != null) {
                    st+=printer.printLine(leftAppend1("Dt lpsd-pay cash/chq", 24));//ECS Message
                } else {
                    //  st+=printer.printLine(leftAppend1(" ", 24));
                    st+=printer.printNewLine();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                //added on 03.04.2017 to show additional rebate message
                double cashlesrbt = 0;
                cashlesrbt = rs.getDouble(63) * 0.01;//1% cashless rebate of EC
                if (rs.getString(8).equals("DOM") || (rs.getString(8).equals("GPS") && Phase.equals("01"))) {//added on 27.03.2018
                    st+=printer.printLine(leftAppend1("PAY CASHLESS AND AVAIL", 24));
                    st+=printer.printLine(leftAppend1("ADDITIONAL REBATE OF %2", 24));
                    /////End 03.04.2017
                } else {//added on 27.03.2018
                    st+=printer.printLine(leftAppend1(" ", 24));
                    st+=printer.printLine(leftAppend1(" ", 24));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
                //added on 27.03.2018
                // st+=printer.printLine(leftAppend1(" ", 24));
                // st+=printer.printLine(leftAppend1(" ", 24));
                st+=printer.printNewLine();
                st+=printer.printNewLine();
                if ((blunts / rs.getInt(99)) > 30 && (rs.getString(8).equals("RGVY") || rs.getString(8).equals("BJVY") || rs.getString(8).equals("KJ"))) {
                    st+=printer.printLine(leftAppend1("Ur Tariff Change to Domestic", 46));
                } else {
                    st+=printer.printLine(leftAppend1(rs.getString(48), 46));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
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
            }
            //+printer.printNewLine();

            //  Log.d("innn", "Connection Status Success: "+printer.getConnectionStatus() );
            // Log.d("innn : : :", ""+st );
            printer.printText(st);

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
                createPrintData();
                printer.disonnected();
            }

            @Override
            public void onFailure(String s) {
                Log.e("innn", "Connection Status Failure: " + printer.getConnectionStatus());
            }
        });
    }

}

