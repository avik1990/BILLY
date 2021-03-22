package com.tpcodl.billingreading.activity.printReceipt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class ReprintBlAmigoImpact extends AppCompatActivity {
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
    private String address="";
    private String AccNum="";
    String mmDeviceAdr=null;
    String devicename="nodevice";
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
        setContentView(R.layout.activity_reprint_bl_amigo_impact);
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
            Bundle Reprintbl = getIntent().getExtras();
            AccNum = Reprintbl.getString("accntnum");
            //  Log.d("DemoApp", "account num  " + AccNum);
        }catch(Exception e){
            Toast.makeText(ReprintBlAmigoImpact.this, "message878   " + e, Toast.LENGTH_LONG).show();
            Toast.makeText(ReprintBlAmigoImpact.this, "message878   " + e, Toast.LENGTH_LONG).show();
        }
        Toast.makeText(ReprintBlAmigoImpact.this, "message888   " + AccNum, Toast.LENGTH_LONG).show();
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
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("DemoApp", "Exception 6  " + e);
            e.printStackTrace();
        }
        return mmDeviceAdr;
    }


    void sendData() throws IOException {
        int blunts=0;
        String conversion="";
        int lapdvar=0;
        try {

            int monthname = 0;
            String version = "";
            int iBilled_cnt = 0;
            SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
            SharedPreferences.Editor editor = sessiondata.edit();
            String Usernm = sessiondata.getString("userID", null); // getting String
            Log.d("DemoApp", "mmDevice.getName()  " + mmDevice.getName());
            Log.d("DemoApp", "mmDevice.getName()  " + mmDevice.getName());
            String BillContents = "";
            String filldata = "";


//code for printing

            if(btpObject.isConnected() == true) {
                Log.d("DemoApp", "Printing Data ...  ");
                btpObject.printerfilter(btpObject.PRINTER_DEFAULT);
                /*btpObject.sendMessage_HW(" ", Normal);
                    btpObject.sendMessage_HW("Printing Double TEXT", DoubleHght);
                    btpObject.sendMessage("\n".getBytes());
                    btpObject.sendMessage_HW("Printing Normal TEXT", Normal);
                    btpObject.varblankdotlinespace(1);
                    btpObject.sendMessage_HW("Printing Double  TEXT", DoubleWidth);
                    btpObject.varblankdotlinespace(1);
                    btpObject.varblankdotlinespace(1);
                    btpObject.varblankdotlinespace(1);
                    btpObject.varblankdotlinespace(1);
                    btpObject.varblankdotlinespace(1);
                    btpObject.varblankdotlinespace(1);
                    btpObject.varblankdotlinespace(1);
                    btpObject.varblankdotlinespace(1);
                    btpObject.varblankdotlinespace(1);
                    btpObject.varblankdotlinespace(1);
                    btpObject.sendMessage_HW("Printing Normal TEXT", Normal);
                   Log.d("DemoApp", "Printing Data success ");
                */

                String doubleHeight = "";
                String widthoff = "";
                String Doublewidth = "";

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
                        btpObject.sendMessage("\n".getBytes());
                        btpObject.sendMessage("\n".getBytes());
                        btpObject.sendMessage("\n".getBytes());
                        btpObject.sendMessage("\n".getBytes());

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
                        btpObject.sendMessage_HW(leftAppend1(strmonth.toUpperCase(), 24) , Normal);
                        String revdt = "";
                        revdt = convertDateFormat(rs.getString(79), "DD-MM-YY");
                        btpObject.sendMessage_HW(leftAppend2(revdt, 6, BlPrepTm, 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend2(rs.getString(45), 10, version, 24) , Normal);
                        String revStr = "";
                        revStr = rs.getString(99) + " MONTHS";
                        btpObject.sendMessage_HW(leftAppend1(revStr, 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(9), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(10), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(11), 24), Normal);
                        btpObject.sendMessage_HW(leftAppend1("NA", 24), Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(1), 24), DoubleHght);//cons_acc double height
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(46), 24) , Normal); // width off
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(7), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(44), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(12), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(15), 24) , Normal);//mtrowner
                        btpObject.sendMessage_HW(leftAppend1(" ", 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(14), 24) , Normal);
                        if (rs.getString(2).trim().length() <= 18) {
                            btpObject.sendMessage_HW(leftAppend1(rs.getString(2), 24) , Normal);//name

                            btpObject.sendMessage_HW(" " , Normal);
                        } else {
                            btpObject.sendMessage_HW(leftAppend2(rs.getString(2), 4, "", 24) , Normal);

                        }
                        StringBuilder strAddr = new StringBuilder(rs.getString(3) + "," + rs.getString(4));
                        Log.d("DemoApp", "add 1  " + strAddr.length());
                        if (rs.getString(3).trim().length() + rs.getString(4).trim().length() > 17) {
                            Log.d("DemoApp", "add 2  " + strAddr.length());
                            if (strAddr.length() >= 42) {
                                strAddr.setLength(42);
                            }
                            if (strAddr.length() <= 19) {
                                btpObject.sendMessage_HW(leftAppend2(strAddr.toString(), 4, "", 24) , Normal);
                                btpObject.sendMessage_HW(" " , Normal);
                            } else {
                                btpObject.sendMessage_HW(leftAppend2(strAddr.toString(), 4, "", 24) , Normal);
                            }
                        } else {
                            btpObject.sendMessage_HW(leftAppend1((rs.getString(3) + "," + rs.getString(4)), 24) , Normal);
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        String ibond = "";
                        ibond = rs.getString(18);
                        if (rs.getString(18).equals("I")) {
                            ibond = "I-BOND";
                        }
                        btpObject.sendMessage_HW(leftAppend2(ibond, 6, "Ph:" + Phase, 24) , Normal);//usage
                        btpObject.sendMessage_HW(leftAppend2(rs.getString(8), 6, rs.getDouble(19) + "", 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(30), 24) , Normal);

                        // BillContents += "\n";
                        //  BillContents += "\n";
                        btpObject.sendMessage("\n".getBytes());
                        btpObject.sendMessage("\n".getBytes());
                        revStr = "";
                        revStr = convertDateFormat(rs.getString(79), "DD-MM-YYYY");
                        btpObject.sendMessage_HW(leftAppend3(rs.getString(57), 7, revStr, 1, rs.getString(59), 24) , Normal);
                        revStr = "";
                        revStr = convertDateFormat(rs.getString(22), "DD-MM-YYYY");
                        btpObject.sendMessage_HW(leftAppend3(rs.getString(20), 7, revStr, 1, rs.getString(23), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(61), 24) , Normal);
                        if (rs.getString(62).equals("N")) {
                            btpObject.sendMessage_HW(leftAppend1("ACTUAL", 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(leftAppend1("AVERAGE", 24) , Normal);
                        }
                        // BillContents += doubleHeight+String.format("%-24s", "");
                        btpObject.sendMessage_HW(" " , Normal);
                        //  Bill calulation start
                        String formattedData = "";
                        // formattedData = String.format("%.02f", rs.getDouble(64));
                        btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(64)) + "", 24) , Normal);
                        blunts = rs.getInt(61);
                        //Bill slab Changed by Santi on 13.01.2016
                        if (rs.getDouble(89) > 0) {
                            btpObject.sendMessage_HW(leftAppend2(rs.getString(87) + "X" + rs.getDouble(88) + "0=", 3, String.format("%.02f", rs.getDouble(89)) + "", 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getDouble(92) > 0) {
                            btpObject.sendMessage_HW(leftAppend2(rs.getString(90) + "X" + rs.getDouble(91) + "0=", 3, String.format("%.02f", rs.getDouble(92)) + "", 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getDouble(95) > 0) {
                            btpObject.sendMessage_HW(leftAppend2(rs.getString(93) + "X" + rs.getDouble(94) + "0=", 3, String.format("%.02f", rs.getDouble(95)) + "", 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getDouble(98) > 0) {
                            btpObject.sendMessage_HW(leftAppend2(rs.getString(96) + "X" + rs.getDouble(97) + "0=", 3, String.format("%.02f", rs.getDouble(98)) + "", 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        //end

                        btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(66)) + "", 24), Normal);
                        btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(65)) + "", 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(67)) + "", 24), DoubleHght);//double height
                        btpObject.sendMessage_HW(" " , Normal);
                        btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(49)) + "", 24) , Normal);//previous yr arr
                        btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(50)) + "", 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(24)) + "", 24) , Normal);
                        if (rs.getInt(27) > 0) { //adjustments
                            btpObject.sendMessage_HW(leftAppend2("(+)", 14, String.format("%.02f", rs.getDouble(27)) + "", 24) , Normal);

                        } else if (rs.getInt(26) > 0) {
                            btpObject.sendMessage_HW(leftAppend2("(-)", 14, String.format("%.02f", rs.getDouble(26)) + "", 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(leftAppend1("0.00", 24) , Normal);
                        }
                        if (rs.getString(23).equals("P")) {
                            btpObject.sendMessage_HW(leftAppend2("P.ADJ.AMT :", 0, String.format("%.02f", rs.getDouble(28)) + "", 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);                        }

                        if (rs.getInt(25) > 0) {
                            btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(25)) + "", 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getInt(41) > 0) {
                            btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(41)) + "", 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getInt(42) > 0) {
                            btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(42)) + "", 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getInt(43) > 0) {
                            btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(43)) + "", 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        btpObject.sendMessage_HW(" ", Normal); //Penalty amount showing 30.06.2017
                        btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(69)) + "", 24) , Normal);//total amount
                        btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", rs.getDouble(68)) + "", 24) , Normal);//rebate amount
                        double revamt = 0;
                        revamt = (double) Math.round(rs.getDouble(69) - rs.getDouble(68));
                        btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", revamt) + "", 24) , DoubleHght);//total bill by due date double height
                        roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                        revamt = 0;
                        if (roundupto > 0) {
                            btpObject.sendMessage_HW(leftAppend2("(+)", 14, String.format("%.02f", roundupto) + "", 24) , Normal);
                        } else {
                            revamt = Math.abs(roundupto);
                            btpObject.sendMessage_HW(leftAppend1("(-)" + String.format("%.02f", Math.abs(roundupto)), 24) , Normal);
                        }
                        btpObject.sendMessage_HW(" ", Normal);
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(76), 24) , DoubleHght);//rebate date

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
                        // btpObject.sendMessage_HW(leftAppend("%-14s%10.2f", "", (double) Math.round(rs.getDouble(69)));//pay after due date disable on 31.01.17
                        btpObject.sendMessage_HW(leftAppend1(String.format("%.02f", payaftdt) + "", 24) , DoubleHght);//pay after due date enable on 31.01.17

                        if (roundupto > 0) {
                            btpObject.sendMessage_HW(leftAppend1("(+)" + String.format("%.02f", roundupto), 24) , Normal);
                        } else {
                            btpObject.sendMessage_HW(leftAppend1("(-)" + String.format("%.02f", Math.abs(roundupto)), 24) , Normal);
                        }

                        btpObject.sendMessage_HW(" " , Normal); //dissable on 03.04.2017
                        btpObject.sendMessage_HW(" " , Normal);//dissable on 03.04.2017
                        btpObject.sendMessage_HW(leftAppend1(rs.getString(32) + "-" + rs.getString(33), 24) , Normal);
                        btpObject.sendMessage_HW(leftAppend2(rs.getDouble(34) + "", 3, rs.getString(31), 24) , Normal);
                        btpObject.sendMessage_HW(" " , Normal);
                        btpObject.sendMessage_HW(" " , Normal);

                        //Ecs Message printing///////
                        if (rs.getDouble(69) < rs.getDouble(55) && rs.getInt(85) == 1) { //here rs(85) is validation date check query CASE WHEN CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG
                            btpObject.sendMessage_HW(leftAppend1("Bg dbtd to Bank thru ECS", 24) , Normal);//ECS Message
                        } else if (rs.getDouble(69) > rs.getDouble(55) && rs.getInt(85) == 1) {
                            btpObject.sendMessage_HW(leftAppend1("Val excd-pay cash/chq", 24) , Normal);//ECS Message
                        } else if (rs.getInt(85) == 0 && rs.getString(56) != null) {
                            btpObject.sendMessage_HW(leftAppend1("Dt lpsd-pay cash/chq", 24) , Normal);//ECS Message
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        //added on 03.04.2017 to show additional rebate message
                        double cashlesrbt = 0;
                        cashlesrbt = rs.getDouble(63) * 0.01;//1% cashless rebate of EC
                        if (rs.getString(8).equals("DOM") || (rs.getString(8).equals("GPS") && Phase.equals("01"))) {//added on 27.03.2018
                            btpObject.sendMessage_HW(leftAppend1("PAY CASHLESS AND AVAIL", 24) , Normal);
                            btpObject.sendMessage_HW(leftAppend1("ADDITIONAL REBATE OF 2P", 24), Normal);
                            /////End 03.04.2017
                        } else {//added on 27.03.2018
                            btpObject.sendMessage_HW(" " , Normal);
                            btpObject.sendMessage_HW(" " , Normal);
                        }//added on 27.03.2018

                        if ((blunts / rs.getInt(99)) > 30 && (rs.getString(8).equals("RGVY") || rs.getString(8).equals("BJVY") || rs.getString(8).equals("KJ"))) {
                            btpObject.sendMessage_HW(leftAppend1("Ur Tariff Change to Domestic", 46) , Normal);
                        }
                        if(rs.getString(48).length()>5){
                            btpObject.sendMessage_HW(leftAppend1(rs.getString(48), 46) , Normal);
                        }else{
                            btpObject.sendMessage_HW(" " , Normal);
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        btpObject.sendMessage_HW(leftAppend1("Plz. Stay Away from Electric line/Sub-Stations", 46) , Normal);





                        // printerap.printData(BillContents);


                    }
                } //while loop close
                rs.close();
                databaseAccess.close();

                //main if condn clos
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }    ///////



        } catch (NullPointerException e22) {
            e22.printStackTrace();
            Log.d("DemoApp", "Exception 13  " + e22);

        } catch (Exception e23) {
            Log.d("DemoApp", "Exception 14  " + e23);
            e23.printStackTrace();
        }
        strPrntMsg.setText("Data Sent to Bluetooth Printer");
        //Reprint The Bill
        Button ReprntBl = (Button) findViewById(R.id.ReprntBl);
        ReprntBl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(devicename.equals("nodevice")) {
                    try {
                        devicename = findBT();
                    } catch (Exception ex) {
                        //  Toast.makeText(BillPrintActivity.this, "message12", Toast.LENGTH_LONG).show();
                    }
                }


                btpObject.createClient(address);


                try{
                    sendData();
                } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message13", Toast.LENGTH_LONG).show();
                }try{
                    //workerThread.sleep(20000);
                    // Thread.sleep(20000);
                    closeBT();
                } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message14", Toast.LENGTH_LONG).show();
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
            strPrntMsg.setText("Bluetooth Closed");;
        } catch (NullPointerException e) {
            //  Toast.makeText(BillPrintActivity.this, "message10"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            //   Toast.makeText(BillPrintActivity.this, "message11" + e, Toast.LENGTH_LONG).show();
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
        //  System.runFinalization();
        //   System.run
        //  System.exit(0);
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

}

