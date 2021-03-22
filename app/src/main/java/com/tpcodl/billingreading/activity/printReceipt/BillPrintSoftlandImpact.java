package com.tpcodl.billingreading.activity.printReceipt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.softland.printerlib.PrinterSection.CharaStyle;
import com.softland.printerlib.PrinterSection.ConnectionStatus;
import com.softland.printerlib.PrinterSection.Printer;
import com.softland.printerlib.PrinterSection.Printer2inch;
import com.softland.printerlib.PrinterSection.iPrinter;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.database.DatabaseAccess;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class BillPrintSoftlandImpact extends AppCompatActivity {
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
    final Context context = this;
    private String AccNum="";
    String mmDeviceAdr=null;
    String devicename="nodevice";
    private String address = "";
    private Printer printer;
    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_print_softland_impact);
        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
        strPrntMsg.setText("Printing");
        mmOutputStream = null;
        mmInputStream = null;
        mmDevice = null;
        mBluetoothAdapter = null;
        AccNum="";
        Bundle blprintval = getIntent().getExtras();
        AccNum = blprintval.getString("AcctNo");
        // Log.d("DemoApp", "account num  " + AccNum);
        Log.d("DemoApp", "devicename  " + devicename);
        printer=new Printer2inch();
        if(devicename.equals("nodevice")){
            try{
                address=findBT();
            }catch (Exception e){}
        }
        Log.d("DemoApp", "address  " + address);
        connectTodevice(address);
        Button ReprntBl = (Button) findViewById(R.id.ReprntBl);
        ReprntBl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    connectTodevice(address);
                } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message13", Toast.LENGTH_LONG).show();
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
                    Log.d("DemoApp", "mmDeviceAdr  " + mmDeviceAdr);
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
    void connectTodevice(String address)
    {
        printer.connect(iPrinter.ConnectionType.BT,address, new ConnectionStatus()
        {
            @Override
            public void onSucess()
            {
                Log.e("innn", "Already connected");
                Log.d("DemoApp", "rrrrr 10  ");
                sendData();
                printer.disonnected();
            }
            @Override
            public void onFailure(String s)
            {
                Log.d("DemoApp", "rrrrr 11 " );
                Log.e("innn", "Connection Status Failure: "+printer.getConnectionStatus() );
            }
        });
    }
    private void sendData() {
        int blunts=0;
        String conversion="";
        int lapdvar=0;
        String st="";
        try{
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

                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st += printer.printLine(leftAppend1(rs.getString(1), 24));//cons_acc double height
                printer.restCHARASTYLE();
                st += printer.printLine(leftAppend1(rs.getString(46), 24)); // width off
                st += printer.printLine(leftAppend1(rs.getString(7), 24));
                st += printer.printLine(leftAppend1(rs.getString(44), 24));
                st += printer.printLine(leftAppend1(rs.getString(12), 24));

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

                // BillContents += "\n";
                //  BillContents += "\n";
                st+=printer.printNewLine();
                st+=printer.printNewLine();
                revStr = "";
                revStr = convertDateFormat(rs.getString(79), "DD-MM-YYYY");
                st+=printer.printLine(leftAppend3(rs.getString(57), 7, revStr, 1, rs.getString(59), 24));
                revStr = "";

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

                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(66)) + "", 24));
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(65)) + "", 24));
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(67)) + "", 24));//double height
                printer.restCHARASTYLE();

                //  st+=printer.printLine(leftAppend1(" ", 24));
                st+=printer.printNewLine();
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(49)) + "", 24));//previous yr arr
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(50)) + "", 24));
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(24)) + "", 24));

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

                if (rs.getInt(25) > 0) {
                    st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(25)) + "", 24));
                } else {
                    // st+=printer.printLine(leftAppend1(" ", 24));
                    st+=printer.printNewLine();
                }

                if (rs.getInt(41) > 0) {
                    st+=printer.printLine(leftAppend2("SD AVAIL:", 0, String.format("%.02f", rs.getDouble(41)) + "", 24));
                } else {
                    // st+=printer.printLine(leftAppend1(" ", 24));
                    st+=printer.printNewLine();
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

                st+=printer.printLine(leftAppend1(" ", 24));//Penalty amount showing 30.06.2017
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(69)) + "", 24));//total amount
                st+=printer.printLine(leftAppend1(String.format("%.02f", rs.getDouble(68)) + "", 24));//rebate amount
                double revamt = 0;
                revamt = (double) Math.round(rs.getDouble(69) - rs.getDouble(68));

                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1(String.format("%.02f", revamt) + "", 24));//total bill by due date double height
                printer.restCHARASTYLE();

                roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                revamt = 0;
                if (roundupto > 0) {
                    st+=printer.printLine(leftAppend2("(+)", 14, String.format("%.02f", roundupto) + "", 24));
                } else {
                    revamt = Math.abs(roundupto);
                    st+=printer.printLine(leftAppend1("(-)" + String.format("%.02f", Math.abs(roundupto)), 24));
                }

                //st+=printer.printLine(leftAppend1(" ", 24));
                st+=printer.printNewLine();
                printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1(rs.getString(76), 24));//rebate date

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

                ////
                // st+=leftAppend("%-14s%10.2f", "", (double) Math.round(rs.getDouble(69)));//pay after due date disable on 31.01.17
                // printer.setCHARASTYLE(CharaStyle.DoubleHeight);
                st+=printer.printLine(leftAppend1(String.format("%.02f", payaftdt) + "", 24));//pay after due date enable on 31.01.17
                printer.restCHARASTYLE();

                if (roundupto > 0) {
                    st+=printer.printLine(leftAppend1("(+)" + String.format("%.02f", roundupto), 24));
                } else {
                    st+=printer.printLine(leftAppend1("(-)" + String.format("%.02f", Math.abs(roundupto)), 24));
                }

                //  st+=printer.printLine(leftAppend1(" ", 24)); //dissable on 03.04.2017
                // st+=printer.printLine(leftAppend1(" ", 24));//dissable on 03.04.2017
                st+=printer.printNewLine();
                st+=printer.printNewLine();

                st+=printer.printLine(leftAppend1(rs.getString(32) + "-" + rs.getString(33), 24));
                st+=printer.printLine(leftAppend2(rs.getDouble(34) + "", 3, rs.getString(31), 24));
                st+=printer.printNewLine();
                st+=printer.printNewLine();
                //  st+=printer.printLine(leftAppend1(" ", 24));
                //  st+=printer.printLine(leftAppend1(" ", 24));

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

                //added on 03.04.2017 to show additional rebate message
                double cashlesrbt = 0;
                cashlesrbt = rs.getDouble(63) * 0.01;//1% cashless rebate of EC
                Log.d("DemoApp", "Phase num  " + Phase);
                Log.d("DemoApp", "getString(8)  " + rs.getString(8));
                try {
                    if (rs.getString(8).equals("DOM") || (rs.getString(8).equals("GPS") && Phase.equals("01"))) {//added on 27.03.2018
                        st += printer.printLine(leftAppend1("PAY CASHLESS AND AVAIL", 24));
                        st += printer.printLine(leftAppend1("ADDITIONAL REBATE OF 2%", 24));
                        /////End 03.04.2017
                    } else {//added on 27.03.2018
                        st += printer.printLine(leftAppend1(" ", 24));
                        st += printer.printLine(leftAppend1(" ", 24));
                    }
                }catch (Exception e){ Log.d("DemoApp", "exception   " );e.printStackTrace();}
                //added on 27.03.2018
                // st+=printer.printLine(leftAppend1(" ", 24));
                // st+=printer.printLine(leftAppend1(" ", 24));
                st += printer.printLine(leftAppend1("YOU CAN PAY WITHIN 7", 24));
                st += printer.printLine(leftAppend1("DAYS FROM BILL DATE", 24));
                if ((blunts / rs.getInt(99)) > 30 && (rs.getString(8).equals("RGVY") || rs.getString(8).equals("BJVY") || rs.getString(8).equals("KJ"))) {
                    st+=printer.printLine(leftAppend1("Ur Tariff Change to Domestic", 46));
                }
                if(rs.getString(48).length()>5) {
                    st+=printer.printLine(leftAppend1(rs.getString(48), 46));
                }
                st+=printer.printLine(leftAppend1("Plz. Stay Away from Electric line/Sub-Stations", 46));
                st += printer.printNewLine();
                st+=printer.printNewLine();
                st+=printer.printNewLine();
                st+=printer.printNewLine();

            }
            //+printer.printNewLine();

            //  Log.d("innn", "Connection Status Success: "+printer.getConnectionStatus() );
            // Log.d("innn : : :", ""+st );
            printer.printText(st);
            // printer.disonnected();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }
        catch (NullPointerException e22) {
            e22.printStackTrace();
            Toast.makeText(BillPrintSoftlandImpact.this, "message8" + e22, Toast.LENGTH_LONG).show();
          //  Toast.makeText(BillPrintSoftlandImpact.this, "message120"+lapdvar, Toast.LENGTH_LONG).show();
        } catch (Exception e23) {
            Toast.makeText(BillPrintSoftlandImpact.this, "message9"+e23, Toast.LENGTH_LONG).show();
         //   Toast.makeText(BillPrintSoftlandImpact.this, "message120"+lapdvar, Toast.LENGTH_LONG).show();
            e23.printStackTrace();
        }

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
