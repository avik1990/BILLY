package com.tpcodl.billingreading.activity.printReceipt;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
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

import com.analogics.impactAPI.Bluetooth_Printer_2inch_Impact;
import com.analogics.impactprinter.AnalogicsImpactPrinter;

import com.softland.printerlib.PrinterSection.Printer;
import com.softland.printerlib.PrinterSection.Printer2inch;
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

public class BillPrintAnalogicImpact extends AppCompatActivity {
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
        setContentView(R.layout.activity_bill_print_analogic_impact_parent);
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
        try{
            sendData();
        } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message13", Toast.LENGTH_LONG).show();
        }

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
                    mmDevice = device;
                    mmDeviceAdr = device.getAddress();
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

    private void sendData() {
        int blunts=0;
        String conversion="";
        int lapdvar=0;
        String doubleHeight = "";
        String widthoff = "";
        try{

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
            Bluetooth_Printer_2inch_Impact BPImpact = new Bluetooth_Printer_2inch_Impact();
            doubleHeight = BPImpact.font_Double_Height_On();
            String lnfeed = BPImpact.line_Feed();
            String widthon = BPImpact.font_Double_Height_Width_On();
            widthoff = BPImpact.font_Double_Height_Width_Off();
            String formfeed = BPImpact.form_Feed();
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
                        }
                        //added on 24.07.2020
                        BillContents += String.format("%-24s", "YOU CAN PAY WITHIN 7");
                        BillContents += String.format("%-24s", "DAYS FROM BILL DATE");

                        if ((blunts / rs.getInt(99)) > 30 && (rs.getString(8).equals("RGVY") || rs.getString(8).equals("BJVY") || rs.getString(8).equals("KJ"))) {
                            BillContents += widthoff + String.format("%-48s", "Ur Tariff Change to Domestic");
                        }
                        if(rs.getString(48).length()>5){
                            BillContents += widthoff + String.format("%-48s", rs.getString(48));
                        }
                        //  BillContents += widthoff + String.format("%-24s", "Bl Gen By: " + Usernm);
                        BillContents += widthoff + String.format("%-48s",  "Plz. Stay Away from Electric line/Sub-Stations");
                        BillContents += widthoff + "\n\n\n";
                    }
                } //while loop close
                rs.close();
                AnalogicsImpactPrinter print = new AnalogicsImpactPrinter();

                    print.openBT(mmDevice.getAddress());
                    print.printData(BillContents);
                    print.closeBT();


        }
        catch (NullPointerException e22) {
            e22.printStackTrace();
          //  Toast.makeText(BillPrintAnalogicImpact.this, "message8" + e22, Toast.LENGTH_LONG).show();
            //  Toast.makeText(BillPrintSoftlandImpact.this, "message120"+lapdvar, Toast.LENGTH_LONG).show();


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

        } catch (Exception e23) {
          //  Toast.makeText(BillPrintAnalogicImpact.this, "message9"+e23, Toast.LENGTH_LONG).show();
            //   Toast.makeText(BillPrintSoftlandImpact.this, "message120"+lapdvar, Toast.LENGTH_LONG).show();


            e23.printStackTrace();
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
}
