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

public class ReprintBlAmigoThermal extends AppCompatActivity {
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
        setContentView(R.layout.activity_reprint_bl_amigo_thermal);
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

        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
        strPrntMsg.setText("Printing");
        mmOutputStream=null;
        mmInputStream=null;
        mmDevice=null;
        mBluetoothAdapter=null;

        AccNum="";
        try {
            Bundle Reprintbl = getIntent().getExtras();
            AccNum = Reprintbl.getString("accntnum");
            //  Log.d("DemoApp", "account num  " + AccNum);
        }catch(Exception e){
            Toast.makeText(ReprintBlAmigoThermal.this, "message878   " + e, Toast.LENGTH_LONG).show();
        }
     //   Toast.makeText(ReprintBlAmigoThermal.this, "message888   " + AccNum, Toast.LENGTH_LONG).show();
        // Log.d("DemoApp", "account num  " + AccNum);
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
            String Billformat="";
            String papertyp="0";
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            String strSelectSQL_02 = "select papertyp from SA_USER where userid='" + Usernm + "'";
            Cursor rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
            while (rs1.moveToNext()) {
                papertyp = rs1.getString(0);
            }
            rs1.close();
            rs1=null;
            databaseAccess.close();
            Log.d("DemoApp", "papertyp  " + papertyp);
            try {
                if (papertyp.equals("1") || papertyp.isEmpty() || papertyp == null) {//0 =pre-printed
                    Billformat = "blank";
                }
            }catch (Exception e){
                Billformat="blank";
            }
            Log.d("DemoApp", "Billformat  " + Billformat);
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
                Calendar c = Calendar.getInstance();
                SimpleDateFormat month = new SimpleDateFormat("MMM-yy");
                String strmonth = month.format(c.getTime());
                SimpleDateFormat year = new SimpleDateFormat("dd-MM-yy");
                Date vardate = null;
                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                //to get the current version of software
                strSelectSQL_02=null;
                rs1=null;
                strSelectSQL_02 = "select file_name,version_flag from File_desc where version_flag=1";
                rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
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
                    if (Billformat.equals("blank")) {
                        // btpObject.sendMessage("\n".getBytes());
                        // btpObject.sendMessage("\n".getBytes());
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
                        //  btpObject.sendMessage_HW("123456789abcdefghijklmnopqrstuvwxyz", Normal);
                        btpObject.sendMessage_HW(CenterAppend1("TPCODL", 32),Normal);
                        btpObject.sendMessage_HW(CenterAppend1(" ELECTRICITY BILL",32),Normal);
                        btpObject.sendMessage_HW("THIS BILL SHALL NOT BE A PROOF",Normal);
                        btpObject.sendMessage_HW("    OF LAWFUL OCCUPATION OF ",Normal);
                        btpObject.sendMessage_HW("   PREMISES IN CASE I-BOND IS ",Normal);
                        btpObject.sendMessage_HW("     MENTIONED HERE UNDER",Normal);
                        btpObject.sendMessage_HW("------------------------------",Normal);
                        btpObject.sendMessage_HW(leftAppend1("BILL MONTH:",strmonth.toUpperCase(), 32) , Normal);
                        String revdt = "";

                        revdt = convertDateFormat(rs.getString(79), "DD-MM-YY");
                        btpObject.sendMessage_HW(leftAppend2("BL DT:",revdt, 6,"TIME:"+ BlPrepTm, 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend2("BILL NO:",rs.getString(45), 10, "VER:"+version, 32) , Normal);
                        String revStr = "";
                        revStr = rs.getString(99) + " MONTHS";
                        btpObject.sendMessage_HW(leftAppend1("BILLED FOR:",revStr, 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("DIV:",rs.getString(9), 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("SUBDIV:",rs.getString(10), 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("SECTION:",rs.getString(11), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("SBM NO:","NA", 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("CON.AC NO:",rs.getString(1), 32), DoubleHght);//cons_acc double height
                        btpObject.sendMessage_HW(leftAppend1("CUSTOMER ID:",rs.getString(46), 32) , Normal); // width off
                        btpObject.sendMessage_HW(leftAppend1("OA NO:",rs.getString(7), 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("INST NO:",rs.getString(44), 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("MTR SL NO:",rs.getString(12), 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("MTR OWNER:",rs.getString(15), 32) , Normal);//mtrowner
                        btpObject.sendMessage_HW(leftAppend1("MAKE:"," ", 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("METER MF:",rs.getString(14), 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("NAME:",rs.getString(2), 32) , Normal);//name
                        StringBuilder strAddr = new StringBuilder(rs.getString(3) + "," + rs.getString(4));
                        Log.d("DemoApp", "add 1  " + strAddr.length());
                        btpObject.sendMessage_HW(leftAppend2("ADDRS:",strAddr.toString(), 4, "", 32) , Normal);
                        String ibond = "";
                        ibond = rs.getString(18);
                        if (rs.getString(18).equals("I")) {
                            ibond = "I-BOND";
                        }
                        btpObject.sendMessage_HW(leftAppend1("USAGE:"+ibond, "Ph:" + Phase, 32) , Normal);//usage
                        btpObject.sendMessage_HW(leftAppend1("T.CAT:"+rs.getString(8), "CD:"+ rs.getDouble(19) + "", 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("AVG. UNITS:",rs.getString(30), 32) , Normal);
                        btpObject.sendMessage_HW("------------------------------", Normal);
                        btpObject.sendMessage("\n".getBytes());
                        btpObject.sendMessage("\n".getBytes());
                      //  btpObject.sendMessage_HW(leftAppend1("AVG. UNITS:",rs.getString(30), 32) , Normal);
                        btpObject.sendMessage_HW("       RDG" + "        DATE" + "      STS" , Normal);
                        revStr = "";
                        revStr = convertDateFormat(rs.getString(79), "DD-MM-YYYY");
                        btpObject.sendMessage_HW(leftAppend3("PRES:", rs.getString(57), 9, revStr, 8, rs.getString(59), 32) , Normal);
                        revStr = "";
                        revStr = convertDateFormat(rs.getString(22), "DD-MM-YYYY");
                        btpObject.sendMessage_HW(leftAppend3("PREV:", rs.getString(20), 9, revStr, 8, rs.getString(23), 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("UNITS ADVANCED:",rs.getString(61), 32) , Normal);
                        if (rs.getString(62).equals("N")) {
                            btpObject.sendMessage_HW(leftAppend1("BILL BASIS:","ACTUAL", 32) , Normal);
                        } else {
                            btpObject.sendMessage_HW(leftAppend1("BILL BASIS:","AVERAGE", 32) , Normal);
                        }
                        btpObject.sendMessage_HW("------------------------------",Normal);

                        //  Bill calulation start
                        String formattedData = "";
                        // formattedData = String.format("%.02f", rs.getDouble(64));
                        btpObject.sendMessage_HW(leftAppend1("MFC/CUST CHRG:", String.format("%.02f", rs.getDouble(64)) + "", 32) , Normal);
                        blunts = rs.getInt(61);
                        //Bill slab Changed by Santi on 13.01.2016
                        if (rs.getDouble(89) > 0) {
                            btpObject.sendMessage_HW(leftAppend2("EC:", (rs.getString(87) + "X" + rs.getDouble(88) + "0="), 11, String.format("%.02f", rs.getDouble(89)) + "", 32) , Normal);
                        } else {
                            //btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getDouble(92) > 0) {
                            btpObject.sendMessage_HW(leftAppend2("EC:",rs.getString(90) + "X" + rs.getDouble(91) + "0=", 3, String.format("%.02f", rs.getDouble(92)) + "", 32) , Normal);
                        } else {
                            // btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getDouble(95) > 0) {
                            btpObject.sendMessage_HW(leftAppend2("EC:",rs.getString(93) + "X" + rs.getDouble(94) + "0=", 3, String.format("%.02f", rs.getDouble(95)) + "", 32) , Normal);
                        } else {
                            // btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getDouble(98) > 0) {
                            btpObject.sendMessage_HW(leftAppend2("EC:",rs.getString(96) + "X" + rs.getDouble(97) + "0=", 3, String.format("%.02f", rs.getDouble(98)) + "", 32) , Normal);
                        } else {
                            //btpObject.sendMessage_HW(" " , Normal);
                        }
                        //end

                        btpObject.sendMessage_HW(leftAppend1("ED CHRG:", String.format("%.02f", rs.getDouble(66)) + "", 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("METER RENT:", String.format("%.02f", rs.getDouble(65)) + "", 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("PRES.BL:", String.format("%.02f", rs.getDouble(67)) + "", 32), DoubleHght);//double height
                        btpObject.sendMessage_HW("------------------------------",Normal);
                        btpObject.sendMessage_HW(leftAppend1("PREV FY ARREAR:", String.format("%.02f", rs.getDouble(49)) + "", 32) , Normal);//previous yr arr
                        btpObject.sendMessage_HW(leftAppend1("CURR FY ARREAR:", String.format("%.02f", rs.getDouble(50)) + "", 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend1("DPS CHRG:", String.format("%.02f", rs.getDouble(24)) + "", 32) , Normal);
                        if (rs.getInt(27) > 0) { //adjustments
                            btpObject.sendMessage_HW(leftAppend2("ADJUSTMENTS:","(+)", 14, String.format("%.02f", rs.getDouble(27)) + "", 32) , Normal);

                        } else if (rs.getInt(26) > 0) {
                            btpObject.sendMessage_HW(leftAppend2("ADJUSTMENTS:","(-)", 14, String.format("%.02f", rs.getDouble(26)) + "", 32) , Normal);
                        } else {
                            btpObject.sendMessage_HW(leftAppend1("ADJUSTMENTS:","", 32) , Normal);
                        }
                        if (rs.getString(23).equals("P")) {
                            btpObject.sendMessage_HW(leftAppend1("P.ADJ.AMT :", String.format("%.02f", rs.getDouble(28)), 32) , Normal);
                        } else {
                            // btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getInt(25) > 0) {
                            btpObject.sendMessage_HW(leftAppend1("MISC CHARG:", String.format("%.02f", rs.getDouble(25)) + "", 32) , Normal);
                        } else {
                            btpObject.sendMessage_HW("MISC CHARG:"+" " , Normal);
                        }
                        if (rs.getInt(41) > 0) {
                            btpObject.sendMessage_HW(leftAppend1("SD AVAIL:", String.format("%.02f", rs.getDouble(41)) + "", 32) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getInt(42) > 0) {
                            btpObject.sendMessage_HW(leftAppend1("ASD:", String.format("%.02f", rs.getDouble(42)) + "", 32) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getInt(43) > 0) {
                            btpObject.sendMessage_HW(leftAppend1("ASD ARR:", String.format("%.02f", rs.getDouble(43)) + "", 32) , Normal);
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        // btpObject.sendMessage_HW(" ", Normal); //Penalty amount showing 30.06.2017
                        btpObject.sendMessage_HW("------------------------------",Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL AMOUNT:", String.format("%.02f", rs.getDouble(69)) + "", 32) , Normal);//total amount
                        btpObject.sendMessage_HW(leftAppend1("REBATE:", String.format("%.02f", rs.getDouble(68)) + "", 32) , Normal);//rebate amount
                        double revamt = 0;
                        revamt = (double) Math.round(rs.getDouble(69) - rs.getDouble(68));
                        btpObject.sendMessage_HW(leftAppend1("BL BY DUE DT:", String.format("%.02f", revamt) + "", 32) , DoubleHght);//total bill by due date double height
                        roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                        revamt = 0;
                        if (roundupto > 0) {
                            btpObject.sendMessage_HW(leftAppend2("ROUNDED UPTO:","(+)", 14, String.format("%.02f", roundupto) + "", 32) , Normal);
                        } else {
                            revamt = Math.abs(roundupto);
                            btpObject.sendMessage_HW(leftAppend1("ROUNDED UPTO:","(-)" + String.format("%.02f", Math.abs(roundupto)), 32) , Normal);
                        }
                        btpObject.sendMessage_HW(" ", Normal);
                        btpObject.sendMessage_HW(leftAppend1("REBATE DATE",rs.getString(76), 32) , DoubleHght);//rebate date

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
                        btpObject.sendMessage_HW(leftAppend1("PAY AFT DUE DT:", String.format("%.02f", payaftdt) + "", 32) , DoubleHght);//pay after due date enable on 31.01.17

                        if (roundupto > 0) {
                            btpObject.sendMessage_HW(leftAppend1("ROUNDED UPTO:","(+)" + String.format("%.02f", roundupto), 32) , Normal);
                        } else {
                            btpObject.sendMessage_HW(leftAppend1("ROUNDED UPTO:","(-)" + String.format("%.02f", Math.abs(roundupto)), 32) , Normal);
                        }
                        btpObject.sendMessage_HW("------------------------------",Normal);
                        btpObject.sendMessage_HW(CenterAppend1("LAST PAYMENT DETAILS",32) , Normal); //dissable on 03.04.2017
                        btpObject.sendMessage_HW("------------------------------",Normal);
                        // btpObject.sendMessage_HW(" " , Normal);//dissable on 03.04.2017
                        btpObject.sendMessage_HW(leftAppend1("BNO-RNO:",rs.getString(32) + "-" + rs.getString(33), 32) , Normal);
                        btpObject.sendMessage_HW(leftAppend2("AMT:", rs.getDouble(34) + "", 10, "DT:" + rs.getString(31), 32) , Normal);
                        btpObject.sendMessage_HW("------------------------------",Normal);
                        btpObject.sendMessage_HW(CenterAppend1("ECS MESSAGE",32) , Normal);
                        btpObject.sendMessage_HW("------------------------------",Normal);
                        //Ecs Message printing///////
                        if (rs.getDouble(69) < rs.getDouble(55) && rs.getInt(85) == 1) { //here rs(85) is validation date check query CASE WHEN CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG
                            btpObject.sendMessage_HW(CenterAppend1("Bg dbtd to Bank thru ECS", 32) , Normal);//ECS Message
                        } else if (rs.getDouble(69) > rs.getDouble(55) && rs.getInt(85) == 1) {
                            btpObject.sendMessage_HW(CenterAppend1("Val excd-pay cash/chq", 32) , Normal);//ECS Message
                        } else if (rs.getInt(85) == 0 && rs.getString(56) != null) {
                            btpObject.sendMessage_HW(CenterAppend1("Dt lpsd-pay cash/chq", 32) , Normal);//ECS Message
                        } else {
                            btpObject.sendMessage_HW(" " , Normal);
                        }
                        btpObject.sendMessage_HW("------------------------------",Normal);
                        //added on 03.04.2017 to show additional rebate message
                        double cashlesrbt = 0;
                        cashlesrbt = rs.getDouble(63) * 0.01;//1% cashless rebate of EC
                        if (rs.getString(8).equals("DOM") || (rs.getString(8).equals("GPS") && Phase.equals("01"))) {//added on 27.03.2018
                            btpObject.sendMessage_HW(CenterAppend1("PAY CASHLESS AND AVAIL", 32) , Normal);
                            btpObject.sendMessage_HW(CenterAppend1("ADDITIONAL REBATE OF 2%", 32), Normal);
                            /////End 03.04.2017
                        } else {//added on 27.03.2018
                            //  btpObject.sendMessage_HW(" " , Normal);
                            // btpObject.sendMessage_HW(" " , Normal);
                        }//added on 27.03.2018
                        btpObject.sendMessage_HW(CenterAppend1("YOU CAN PAY WITHIN 7", 32) , Normal);
                        btpObject.sendMessage_HW(CenterAppend1("DAYS FROM BILL DATE", 32), Normal);
                        if ((blunts / rs.getInt(99)) > 30 && (rs.getString(8).equals("RGVY") || rs.getString(8).equals("BJVY") || rs.getString(8).equals("KJ"))) {
                            btpObject.sendMessage_HW(leftAppend1("","Ur Tariff Change to Domestic", 32) , Normal);
                        }

                        // btpObject.sendMessage_HW(leftAppend1("","Win Prize 10000 - Paying Bill Online (TNC apply)", 48) , Normal);
                        if(rs.getString(48).length()>5){
                            btpObject.sendMessage_HW("------------------------------",Normal);
                            btpObject.sendMessage_HW(leftAppend1("",rs.getString(48), 48) , Normal);
                        }
                        btpObject.sendMessage_HW(CenterAppend1("Please Stay Away from ", 32), Normal);
                        btpObject.sendMessage_HW(CenterAppend1("Electric line/Sub-Stations", 32), Normal);
                        btpObject.sendMessage_HW("------------------------------",Normal);;
                        btpObject.sendMessage_HW(CenterAppend1("PLEASE PAY CURR. DUE ALONG WITH", 32), Normal);
                        btpObject.sendMessage_HW(CenterAppend1("ARREAR TO AVOID DISCONNECTION", 32), Normal);
                        btpObject.sendMessage_HW(CenterAppend1("FOR BILLING INFORMATION AND", 32), Normal);
                        btpObject.sendMessage_HW(CenterAppend1("ONLINE PAYMENT VISIT", 32), Normal);
                        btpObject.sendMessage_HW(CenterAppend1("WWW.TPCENTRALODISHA.COM", 32), Normal);
                        btpObject.sendMessage("\n".getBytes());
                        btpObject.sendMessage("\n".getBytes());
                        btpObject.sendMessage("\n".getBytes());
                        btpObject.sendMessage("\n".getBytes());
                        btpObject.sendMessage("\n".getBytes());


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
             //   System.exit(0);

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
    public static String leftAppend1(String str, String str1, int maxlen){
        String retStr="";
        int strlen=0;
        strlen=str.length()+str1.length();
        for(int i=0;i<(maxlen-strlen);i++){
            retStr+=" ";
        }
        str=str+retStr+str1;
        return str;

    }
    public static String leftAppend2(String str0, String str, int leftlen, String Str1, int maxlen){
        String retStr="";
        for(int i=0;i<leftlen-str.length();i++){
            retStr+=" ";
        }
        str=str+retStr;
        str0=str0+str;
        retStr="";
        for(int i=0;i<(maxlen-(str0.length()+Str1.length()));i++){
            retStr+=" ";
        }
        Str1=retStr+Str1;
        str0=str0+Str1;
        return str0;

    }
    public static String leftAppend3(String str0, String str, int rlen, String Str1, int Rlen1, String Str2, int maxlen){

        String retStr="";
        for(int i=0;i<(rlen-str.length());i++){
            retStr+=" ";
        }
        str=str+retStr;
        str0=str0+str;
        retStr="";
        for(int i=0;i<(Rlen1-Str1.length());i++){
            retStr+=" ";
        }
        Str1=Str1+retStr;
        str0=str0+Str1;

        for(int i=0;i<(maxlen-(Str2.length()+str0.length()));i++){
            retStr+=" ";
        }
        Str2=retStr+Str2;
        str0=str0+Str2;
        return str0;

    }
    public static String CenterAppend1(String str1, int maxlen){
        String retStr="";
        String str="";
        int strlen=0;
        int lendiff=0;
        lendiff=maxlen-str1.length();
        strlen=lendiff/2;
        Log.d("DemoApp", "strlen "+strlen );
        for(int i=0;i<strlen;i++){
            retStr+=" ";
        }
        str=str+retStr+str1;
        return str;

    }

}

