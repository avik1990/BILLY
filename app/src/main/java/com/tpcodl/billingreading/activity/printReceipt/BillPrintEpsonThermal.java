package com.tpcodl.billingreading.activity.printReceipt;

import android.app.Activity;
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


import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.database.DatabaseAccess;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class BillPrintEpsonThermal extends Activity implements  ReceiveListener {

    private static final int REQUEST_PERMISSION = 100;
    private Context mContext = null;
    public static Printer mPrinter = null;
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
    Double darrear=0.0;
    // TextView myLabel;
    static TextView strPrntMsg;
    final Context context = this;
    private String AccNum="";
    String mmDeviceAdr=null;
    String devicename="nodevice";
    private  String address = "";
    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_print_epson_thermal);
        //     requestRuntimePermission();
        mContext = this;
        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
        strPrntMsg.setText("Printing");
        mmOutputStream = null;
        mmInputStream = null;
        mmDevice = null;
        mBluetoothAdapter = null;
        AccNum="";
        Bundle blprintval = getIntent().getExtras();
        AccNum = blprintval.getString("AcctNo");

        runPrintReceiptSequence();


        strPrntMsg.setText("Data Sent to Bluetooth Printer");
        //Reprint The Bill
        Button ReprntBl = (Button) findViewById(R.id.ReprntBl);
        ReprntBl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    runPrintReceiptSequence();
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


    private void runPrintReceiptSequence() {

        initializeObject();
        createReceiptData();
        printData();

    }

    private boolean createReceiptData() {

        int blunts=0;
        String conversion="";
        int lapdvar=0;
        int disupflg=0;
        String method = "";
        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (mPrinter == null) {
            return false;
        }
        try {

        /////////////////////////////////////////////////////////////////
            String dDate="";
            String dConno="";
            String  dname="";
            darrear=0.0;
            String dcCont="";
            int Dcprnt=1;
            Log.d("DemoApp", "Dcprntss " +Dcprnt);
            String BlPrepTm = "";
            String Billformat = "normal";
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

            String lReadMF_Mdival = "0";
            String MdivalCurr = "";
            String MdivalPrev = "";
            strSelectSQL_02 = "SELECT B.PREVIOUS_MD,B.MF,B.PRESENT_METER_READING,B.METER_CONDITION,A.RCRD_LOAD,b.PRS_MD FROM TBL_SPOTBILL_HEADER_DETAILS A, " +
                    " TBL_SPOTBILL_CHILD_DETAILS B WHERE " +
                    " A.INSTALLATION=B.INSTALLATION AND A.INSTALLATION='" + AccNum + "' " +
                    " AND B.REGISTER_CODE ='MDKW'";

               /* strSelectSQL_02 = "SELECT PREVIOUS_MD,MF,PRESENT_METER_READING FROM TBL_SPOTBILL_CHILD_DETAILS " +
                        " WHERE INSTALLATION='" + AccNum + "' " +
                        "  AND REGISTER_CODE ='MDKW'";*/

            Log.d("DemoApp", "strSelectSQL_02  " + strSelectSQL_02);
            //ResultSet rs = statement.executeQuery(strSelectSQL_01);
            rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
            while (rs1.moveToNext()) {
                MdivalPrev = String.valueOf(rs1.getDouble(4));
                // lReadMF_Mdival = rs1.getString(1);
                MdivalCurr = String.valueOf(rs1.getDouble(5));
                //   MdMtrCond = rs1.getString(3);
            }
            rs1.close();
            //getting user name
            String MrName="";
            strSelectSQL_02 = "SELECT USER_NAME FROM MST_USER";
            Log.d("DemoApp", "strSelectSQL_02  " + strSelectSQL_02);
            //ResultSet rs = statement.executeQuery(strSelectSQL_01);
            rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
            while (rs1.moveToNext()) {
                MrName= rs1.getString(0);
            }
            rs1.close();
            ////
            String strUpdateSQL_01 = "SELECT strftime('%d-%m-%Y',h.INSERT_DATE),h.LEGACY_ACCOUNT_NO2,h.NAME,h.ADDRESS1,ifnull(h.ADDRESS2,''),h.SEQ," +//5
                    " h.MRU,H.LEGACY_ACCOUNT_NO,CASE WHEN h.RATE_CATEGORY='DOM_OTH' THEN 'DOM' WHEN h.RATE_CATEGORY='02' THEN 'RGVY' WHEN h.RATE_CATEGORY='04' THEN 'BJVY' " +//7
                    " WHEN h.RATE_CATEGORY='DKJ' THEN 'DKJ' WHEN h.RATE_CATEGORY='LT_GENPRPS' THEN 'GPS' WHEN h.RATE_CATEGORY='LT_SPBLPRS' THEN 'SPP' ELSE 'NIL' END TARIFF,h.DIV," +//9
                    " h.SUB_DIV,h.SECTION,ltrim(c.METER_NO,'0'),c.METER_TYP, CASE WHEN CAST(c.MF AS INT)>= 1 THEN CAST(c.MF AS INT) ELSE 1 END AS MF, CASE WHEN h.CONSUMER_OWNED='C' THEN 'CONSUMER' " +//15
                    " WHEN c.METER_NO IS NULL THEN ' ' ELSE 'TPCODL' END AS CONSUMER_OWNED ,c.NO_OF_DIGITS,h.METER_MAKE,ifnull(h.USAGE,''),h.SAN_LOAD,CAST(c.PREV_MTR_READ AS INT),strftime('%d-%m-%Y',h.MOVE_IN_DATE), " +//21
                    " strftime('%d-%m-%Y',c.PREV_READ_DATE),c.METER_CONDITION,h.DPS,h.MISC_CHARGES,h.CR_ADJ,h.DB_ADJ,h.PRV_BILLED_AMT,h.PREVIOUS_BILLED_PROV_UNIT,CAST(h.AVERAGE_KWH AS INT),ifnull(strftime('%d-%m-%Y',h.LAST_PAID_DATE),''),ifnull(h.LAST_PYMT_RCPT,''), " +//32
                    " ifnull(h.LAST_PYMT_RCPT,''),ifnull(h.LAST_PAID_AMT,''),H.HL_MONTHS,h.ED_RBT,H.AIFI,h.NEW_METER_NO,CAST(c.LAST_OK_RDNG AS INT),c.METER_INSTALL_DATE,h.SEC_DEPOSIT_AMT,h.ASD,h.ASDAA,h.INSTALLATION, " +//44
                    " h.BILL_NO,h.CA,h.BILL_PRN_HEADER,h.BILL_PRN_FOOTER,h.PRV_ARR,h.ARREARS,h.ULF,h.PREV_BILL_UNITS,C.CONSUMPTION_OLD_METER,h.BILL_MONTH,h.ECS_LIMT, " +//55
                    " strftime('%d-%m-%Y',h.ECS_VALIDITY_PERIOD), " +
                    " C.PRESENT_METER_READING,H.PRESENT_READING_REMARK,h.PRESENT_METER_STATUS,h.PRESENT_METER_STATUS,CAST(h.PRESENT_BILL_UNITS AS INT),h.BILL_BASIS,h.EC,h.MMFC,h.MRENT_CHARGED,h.ED,h.CURRENT_BILL_TOTAL,h.REBATE,h.AMOUNT_PAYABLE,h.AVG_UNIT_BILLED,h.RCPTNO, " +//71
                    " h.CHQNO,h.CHQDT,h.BANK,h.RCPTAMT,strftime('%d-%m-%Y',h.DUE_DATE),h.DO_EXPIRY,h.PRESENT_READING_TIME,strftime('%d-%m-%Y',h.OSBILL_DATE),c.BILLED_MD, " +//80
                    " c.PRS_MD,H.READ_FLAG,H.READ_FLAG, H.READ_FLAG,CASE WHEN strftime('%d-%m-%Y',h.OSBILL_DATE)<strftime('%d-%m-%Y',h.ECS_VALIDITY_PERIOD) THEN 1  ELSE 0 END AS ECSMSG, " +//84
                    " H.SENT_FLAG,h.UNIT_SLAB1,h.RATE_SLAB1,h.EC_SLAB1,h.UNIT_SLAB2,h.RATE_SLAB2,h.EC_SLAB2,h.UNIT_SLAB3,h.RATE_SLAB3,h.EC_SLAB3,h.UNIT_SLAB4, " +//95
                    " h.RATE_SLAB4,h.EC_SLAB4,H.NO_BILLED_MONTH,H.PREV_BILL_REMARK,CASE WHEN c.METER_CONDITION='F' THEN 'MF'  ELSE  h.MRREASON  END MRREASON," +
                    " H.SBM_BILL_NO,h.PREV_BILL_TYPE,h.PREV_PROV_AMT,H.FC_SLAB,H.PROV_ED,H.MOD_DATE,H.BILLED_MD,strftime('%d-%m-%Y',H.LAST_NORMAL_BILL_DATE)," +
                    " substr(h.LEGACY_ACCOUNT_NO2,5),h.CR_ADJ,h.DB_ADJ,h.DPS_LVD,h.DPS_BILLED,h.ADJ_BILL,strftime('%d-%m-%Y',c.METER_INSTALL_DATE),C.NEW_METER_FLG,h.PREV_BILL_END_DATE   FROM TBL_SPOTBILL_HEADER_DETAILS h, TBL_SPOTBILL_CHILD_DETAILS c WHERE h.INSTALLATION = c.INSTALLATION and h.INSTALLATION='" + AccNum + "' AND C.REGISTER_CODE ='CKWH'";

            Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
            Cursor rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
            ////
            while (rs.moveToNext()) {
                int prvBlTyp=0;
                prvBlTyp=rs.getInt(103);
                int noofmonth = rs.getInt(99);
                int monthname = rs.getInt(54);
                // vardate=year.parse(rs.getString(79));
                double roundupto = 0;
                String Mtrtype = rs.getString(13);
                String Phase = "03";
                try{
                    if (Mtrtype.equals("S")) {
                        Phase = "01";
                    }
                }catch(Exception e) {
                    Phase="01";
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
                dDate=convertDateFormat(rs.getString(79), "DD-MM-YYYY");
                dConno=rs.getString(110);
                dname=rs.getString(2);
                darrear= Double.valueOf(Math.round(rs.getDouble(50)));
                mPrinter.addFeedLine(1);

                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(2, 2);
               // textData.append("TEST BILL" + "\n");
                textData.append("TPCODL" + "\n");
                textData.append("ELECTRICITY BILL" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);

                textData.append("THIS BILL SHALL NOT BE A PROOF  OF" + "\n");
                textData.append("LAWFUL OCCUPATION OF PREMISES IN CASE" + "\n");
                textData.append("I-BOND IS MENTIONED HERE UNDER" + "\n");
                textData.append("----------------------------------------" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                
                textData.append(leftAppend1("BILL MONTH:", strmonth.toUpperCase(), 36) + "\n");
                textData.append(leftAppend1("DN DT:" , rs.getString(107),36) + "\n");
                String revdt = "";
                revdt = convertDateFormat(rs.getString(79), "DD-MM-YY");
                textData.append(leftAppend1("BL DT:" + revdt,"" + "TIME:" + BlPrepTm, 36) + "\n");
                textData.append(leftAppend1("BILL NO:" , rs.getString(102),36) + "\n");
                textData.append(leftAppend1( "VER:" , version, 36) + "\n");
                String revStr = "";
                revStr = rs.getString(99) + " MONTHS";

              //  textData.append(leftAppend1("MOV IN DT:",rs.getString(21), 36)+ "\n");
               // textData.append(leftAppend1("PRV BL TP:",rs.getString(103), 36)+ "\n");
                textData.append(leftAppend1("PRV BL REM:",rs.getString(100), 36)+ "\n");
                //textData.append(leftAppend1("FC SLAB:",rs.getString(105), 36)+ "\n");

                textData.append(leftAppend1("BILLED FOR:",revStr, 36)+  "\n");
                textData.append(leftAppend1("DIV:",rs.getString(9), 36)+ "\n");
                textData.append(leftAppend1("SUBDIV:",rs.getString(10), 36)+ "\n");
                textData.append(leftAppend1("SECTION:",rs.getString(11), 36)+ "\n");
                textData.append(leftAppend1("MRU:" , rs.getString(6), 36)+ "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(2, 2);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
               textData.append(" CON.AC NO:"+ rs.getString(110)+ "\n");//cons_acc double height
               textData.append(" CUST ID:"+ rs.getString(1)+ "\n"); // width off
               textData.append(" CA NO:"+ rs.getString(46)+ "\n"); // width off
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
               textData.append(leftAppend1("OA NO:",rs.getString(7), 36)+ "\n");
               textData.append(leftAppend1("INST NO:",rs.getString(44), 36)+ "\n");
               // textData.append(leftAppend1("CONNECTION DT:",rs.getString(116), 36)+ "\n");
               textData.append(leftAppend1("MTR SL NO:",rs.getString(12), 36)+ "\n");
                textData.append(leftAppend1("MTR INST. DT:",rs.getString(116), 36)+ "\n");
               textData.append(leftAppend1("MTR OWNER:",rs.getString(15), 36)+ "\n");//mtrowner
              // textData.append(leftAppend1("MAKE:","", 36)+ "\n");
               textData.append(leftAppend1("METER MF:",rs.getString(14), 36)+ "\n");
               textData.append("  NAME:"+rs.getString(2)+ "\n");//name
                StringBuilder strAddr = new StringBuilder(rs.getString(3) + "," + rs.getString(4));
                textData.append("  ADDRS:"+strAddr.toString()+ "\n");//ADDRS
                String ibond = "";
                ibond = rs.getString(18);
                if (rs.getString(18).equals("I")) {
                    ibond = "I-BOND";
                }
                textData.append("  USAGE:"+ibond+"  Ph:" + Phase+ "\n");//usage
                textData.append("CATEGORY:" + rs.getString(8) + "  CD:" + rs.getDouble(19)+ "\n");
                textData.append(leftAppend1("HIST. MD:",MdivalPrev, 36)+ "\n");
                textData.append(leftAppend1("MD RECORD:",MdivalCurr, 36)+ "\n");
                textData.append(leftAppend1("BILLED MD:", rs.getString(108), 36)+ "\n");
                textData.append(leftAppend1("MR NOTE:", rs.getString(101), 36)+ "\n");
                textData.append(leftAppend1("LST OK RD:", rs.getString(39), 36)+ "\n");
                textData.append(leftAppend1("LST OK RD DT:", rs.getString(109), 36)+ "\n");

                textData.append(leftAppend1("AVG. UNITS:", rs.getString(30), 36)+ "\n");
                textData.append("----------------------------------------" + "\n");
               textData.append("        RDG" + "         DATE " + "        STS"+ "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());

                revStr = "";
                revStr = rs.getString(79);
                String pres = "";
                try {
                    pres = rs.getString(57);
                } catch (Exception e) {
                    pres = "";
                }

                if (pres == null || pres.isEmpty()) {
                    pres = "";
                }
                String prvcond=rs.getString(23);
                if(prvcond.equalsIgnoreCase("F")){
                    pres = "";
                }
                textData.append(leftAppend3("PRES:", pres, 11, revStr,8, rs.getString(59), 36)+ "\n");
                revStr = "";

                revStr = rs.getString(22);//previous reading date
                String lstokDt=rs.getString(109);
                String movinDt=rs.getString(21);
                String prvRead=rs.getString(20);
                String lReadPrevDup=prvRead;
                if(prvBlTyp == 1000 || prvBlTyp == 2000 ){
                    if(rs.getString(62).equals("N")){
                        revStr=lstokDt;
                        prvRead=rs.getString(39);
                    }
                }else if(prvBlTyp == 5000){
                    revStr=movinDt;
                }

                String newMtrFlg="";
                String prevBlMtrDate="";
                try {
                    newMtrFlg=rs.getString(117);
                    prevBlMtrDate=rs.getString(118);
                    if (newMtrFlg.equalsIgnoreCase("X")) {
                        revStr = prevBlMtrDate;
                        prvRead = lReadPrevDup;
                    }
                }catch(Exception e){
                }

                textData.append(leftAppend3("PREV:", prvRead, 11, revStr, 8, rs.getString(23), 36)+ "\n");


                textData.append(leftAppend1("UNITS ADVANCED:",rs.getString(61), 36)+ "\n");
                if (rs.getString(62).equals("N")) {
                    textData.append(leftAppend1("BILL BASIS:","ACTUAL", 36)+ "\n");
                } else {
                    textData.append(leftAppend1("BILL BASIS:","AVERAGE", 36)+ "\n");
                }
                textData.append("----------------------------------------" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                //  Bill calulation start
                String formattedData = "";
                textData.append(leftAppend1("MFC/CUST CHRG:",String.format("%.02f", rs.getDouble(64)), 36)+ "\n");
                blunts = rs.getInt(61);
                //Bill slab Changed by Santi on 13.01.2016
                if (rs.getDouble(89) > 0) {
                    textData.append(leftAppend2("EC:", (rs.getString(87) + "X" + rs.getDouble(88) + "0="), 11, String.format("%.02f", rs.getDouble(89)), 36)+ "\n");
                } else {
                    //  textData.append(leftAppend1(" ","", 36)+ "\n");
                }

                if (rs.getDouble(92) > 0) {
                    textData.append(leftAppend2("EC:",(rs.getString(90) + "X" + rs.getDouble(91) + "0="), 11, String.format("%.02f", rs.getDouble(92)), 36)+ "\n");
                } else {
                    //  textData.append(leftAppend1(" ","", 36)+ "\n");
                }
                if (rs.getDouble(95) > 0) {
                    textData.append(leftAppend2("EC:",rs.getString(93) + "X" + rs.getDouble(94) + "0=",11, String.format("%.02f", rs.getDouble(95)), 36)+ "\n");
                } else {
                    //    textData.append(leftAppend1(" ","", 36)+ "\n");
                }
                if (rs.getDouble(98) > 0) {
                   textData.append(leftAppend2("EC:",rs.getString(96) + "X" + rs.getDouble(97) + "0=", 11, String.format("%.02f", rs.getDouble(98)), 36)+ "\n");
                } else {
                    //    textData.append(leftAppend1(" ","", 36)+ "\n");
                }
                //end
                textData.append(leftAppend1("ED CHRG:", String.format("%.02f", rs.getDouble(66)) + "", 36) + "\n");
                textData.append(leftAppend1("METER RENT:", String.format("%.02f", rs.getDouble(65)) + "", 36) + "\n");
                textData.append(leftAppend1("DPS CHRG:", String.format("%.02f", rs.getDouble(114)) + "", 36)+ "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(2, 2);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append(" PRES.BL:" + String.format("%.02f", rs.getDouble(67))+ "\n");//double height
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                textData.append("----------------------------------------" + "\n");
               // textData.append(leftAppend1("PREV FY ARREAR:", String.format("%.02f", rs.getDouble(49)) + "", 36)+ "\n");//previous yr arr
                double dAmountArrear=rs.getDouble(50);
                double dAdj_CR = rs.getDouble(111);
                double dAdj_DR = rs.getDouble(112);
                double dpslvd=rs.getDouble(113);
                dAmountArrear=dAmountArrear+dAdj_CR-dAdj_DR-dpslvd;

                textData.append(leftAppend1("ARREAR:",String.format("%.02f",dAmountArrear) + "", 36)+ "\n");


                if (rs.getInt(27) > 0) { //adjustments
                    textData.append(leftAppend2("ADJUSTMENTS:","(+)", 3, String.format("%.02f", rs.getDouble(27)), 36)+ "\n");
                }
                if (rs.getInt(26) > 0) {
                    textData.append(leftAppend2("ADJUSTMENTS:","(-)",3, String.format("%.02f", rs.getDouble(26)), 36)+ "\n");
                } else {
                    textData.append(leftAppend1("ADJUSTMENTS:"," ", 36)+ "\n");
                }
                double povamt=0;
                povamt= rs.getDouble(104)+ rs.getDouble(106);
              //  if (rs.getInt(103)==1000 || rs.getInt(103)==2000){
                    if (rs.getString(115).equals("Y")) {
                        textData.append(leftAppend1("P.ADJ.AMT EC:", String.format("%.02f", rs.getDouble(104)), 36) + "\n");
                        textData.append(leftAppend1("P.ADJ.AMT ED:", String.format("%.02f", rs.getDouble(106)), 36) + "\n");
                 //   }
                } else {
                    //  st+=printer.printLine(leftAppend1("  ", 36)+ "\n");
                    textData.append(leftAppend1(" ", "", 36)+ "\n");
                }

                if (rs.getInt(25) > 0) {
                    textData.append(leftAppend1("MISC CHARG:",String.format("%.02f", rs.getDouble(25)) + "", 36)+ "\n");
                } else {
                    textData.append(leftAppend1("MISC CHARG:", "", 36)+ "\n");
                }
                if (rs.getInt(41) > 0) {
                    textData.append(leftAppend1("SD AVAIL:", String.format("%.02f", rs.getDouble(41)), 36)+ "\n");
                } else {
                    textData.append(leftAppend1(" ", "", 36)+ "\n");
                }
                if (rs.getInt(42) > 0) {
                    textData.append(leftAppend1("ASD:", String.format("%.02f", rs.getDouble(42)), 36)+ "\n");
                } else {
                    // st+=printer.printLine(leftAppend1(" ", 36)+ "\n");
                    textData.append(leftAppend1(" ", "", 36)+ "\n");
                }
                if (rs.getInt(43) > 0) {
                    textData.append(leftAppend1("ASD ARR:", String.format("%.02f", rs.getDouble(43)) + "", 36)+ "\n");
                } else {
                    textData.append(leftAppend1(" ", "", 36)+ "\n");
                }
                textData.append("----------------------------------------" + "\n");
                textData.append(leftAppend1("TOTAL AMOUNT:", String.format("%.02f", rs.getDouble(69)) + "", 36) + "\n");//total amount                        
                textData.append(leftAppend1("REBATE:", String.format("%.02f", rs.getDouble(68)) + "", 36) + "\n");//rebate amount
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());

                double revamt = 0;
                revamt = (double) Math.round(rs.getDouble(69) - rs.getDouble(68));
                mPrinter.addTextSize(2, 2);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append(" BL DUE DT:" + String.format("%.02f", revamt) + "\n");//total bill by due date double height
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                revamt = 0;
                if (roundupto > 0) {
                    textData.append(leftAppend1("ROUNDED UPTO:","(+)"+ String.format("%.02f", roundupto) + "", 36)+ "\n");
                } else {
                    revamt = Math.abs(roundupto);
                    textData.append(leftAppend1("ROUNDED UPTO:","(-)"+String.format("%.02f", Math.abs(roundupto)), 36)+ "\n");
                }
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(2, 2);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append(" REBATE DT:"+ rs.getString(76) + "\n");//rebate date
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                //changes on 31.01.2017 if total amount <0 and total amt-rebate amt<0
                mPrinter.addTextSize(1, 1);
                double payaftdt = 0;
                if (rs.getDouble(69) < 0 && ((double) Math.round(rs.getDouble(69) - rs.getDouble(68))) < 0) {
                    payaftdt = ((double) Math.round(rs.getDouble(69) - rs.getDouble(68)));
                    roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                } else {
                    payaftdt = (double) Math.round(rs.getDouble(69));
                    roundupto = Math.round(rs.getDouble(69)) - rs.getDouble(69);
                }
                mPrinter.addTextSize(2, 2);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append(" AFT DUE DT:"+ String.format("%.02f", payaftdt) + "\n");//pay after due date enable on 31.01.17
              //  textData.append("123456789123456789123456789"+ "\n");//pay after due date enable on 31.01.17
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                if (roundupto > 0) {
                    textData.append(leftAppend1("ROUNDED UPTO:","(+)" + String.format("%.02f", roundupto), 36)+ "\n");
                } else {
                    textData.append(leftAppend1("ROUNDED UPTO:","(-)" + String.format("%.02f", Math.abs(roundupto)), 36)+ "\n");
                }
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(2, 2);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER); 
               textData.append("LAST PAYMENT DETAILS" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("----------------------------------------" + "\n");
                textData.append(leftAppend1("BNO-RNO:", rs.getString(32) , 36) + "\n");
                textData.append(leftAppend2("AMT:", rs.getDouble(34) + "", 10, "DT:" + rs.getString(31), 36)+ "\n");
                textData.append("----------------------------------------" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(2, 2);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                textData.append("ECS MESSAGE" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("----------------------------------------" + "\n");
                
                //Ecs Message printing///////
                if (rs.getDouble(69) < rs.getDouble(55) && rs.getInt(85) == 1) { //here rs(85) is validation date check query CASE WHEN CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG
                    textData.append(leftAppend1("","Bg dbtd to Bank thru ECS", 36)+ "\n");//ECS Message
                } else if (rs.getDouble(69) > rs.getDouble(55) && rs.getInt(85) == 1) {
                    textData.append(leftAppend1("","Val excd-pay cash/chq", 36)+ "\n");//ECS Message
                } else if (rs.getInt(85) == 0 && rs.getString(56) != null) {
                    textData.append(leftAppend1("","Dt lpsd-pay cash/chq", 36)+ "\n");//ECS Message
                } else {
                    //  st+=printer.printLine(leftAppend1(" ", 36)+ "\n");
                    textData.append(leftAppend1(" ", "", 36)+ "\n");
                }
                textData.append("----------------------------------------" + "\n");
                textData.append(leftAppend1("MR NAME:", MrName, 36)+ "\n");
                //added on 03.04.2017 to show additional rebate message
                double cashlesrbt = 0;
                cashlesrbt = rs.getDouble(63) * 0.01;//1% cashless rebate of EC
                if (rs.getString(8).equals("DOM") || (rs.getString(8).equals("GPS") && Phase.equals("01"))) {//added on36.03.2018.
                    textData.append(" PAY CASHLESS AND AVAIL ADDITIONAL"+ "\n");
                    textData.append(" REBATE OF 2%"+ "\n");
                    mPrinter.addText(textData.toString());
                    textData.delete(0, textData.length());
                    /////End 03.04.2017
                }
                if (Math.round(blunts / rs.getDouble(99)) > 30 && (rs.getString(8).equals("DKJ") )) {
                    textData.append(leftAppend1("", " Ur Tariff Change to Domestic", 36) + "\n");
                }
                textData.append("----------------------------------------" + "\n");
                if(rs.getString(48).length()>5){
                    textData.append(" "+rs.getString(48) + "\n");
                }
                textData.append(" YOU CAN PAY WITHIN 7"+ "\n");
                textData.append(" DAYS FROM BILL DATE"+ "\n");
                textData.append(" Please Stay Away from"+ "\n");
                textData.append(" Electric line/Sub-Stations"+ "\n");
                Log.d("dempapp","Dcprnt"+Dcprnt);
                int edexptflg = rs.getInt(36);
                try {
                    if (edexptflg==1) {//ED_EXEMPT no ed 36////////////////////////////////////////////////
                        Dcprnt = 0;//Ed
                    }
                } catch (Exception e) {
                    Dcprnt = 0;
                }
                Log.d("dempapp","edexptflg"+edexptflg);
                disupflg=0;
                if(Dcprnt==1 && Math.round(darrear)>10000){
                    Log.d("dempapp","got");
                    disupflg=1;
                    mPrinter.addText(textData.toString());
                    textData.delete(0, textData.length());
                    mPrinter.addTextSize(2, 2);
                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    textData.append("====================" + "\n");
                    textData.append("DISCONNECTION NOTICE" + "\n");
                    textData.append("====================" + "\n");
                    mPrinter.addText(textData.toString());
                    textData.delete(0, textData.length());
                    mPrinter.addTextSize(1, 1);
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    String NoticeDt=dDate.replace("-","");
                    textData.append(" Notice No:"+dConno+NoticeDt + "\n");
                    textData.append(" Date:"+dDate + "\n");
                    textData.append(" Consumer No:"+dConno + "\n");
                    textData.append(" Name:"+dname + "\n");
                    textData.append("You have outstanding amount of" + "\n");
                    mPrinter.addText(textData.toString());
                    textData.delete(0, textData.length());
                    mPrinter.addTextSize(2, 2);
                    mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                    textData.append("Rs:"+Math.round(darrear)+".00" + "\n");
                    mPrinter.addText(textData.toString());
                    textData.delete(0, textData.length());
                    mPrinter.addTextSize(1, 1);
                    mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                    dcCont=fetchDCContent(revdt);
                    textData.append(dcCont + "\n");
                }
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("----------------------------------------" + "\n");
               textData.append("PEASE PAY CURRENT DUE ALONG WITH"+ "\n");
               textData.append("ARREAR TO AVOID DISCONNECTION."+ "\n");
                textData.append("FOR BILLING INFORMATION AND ONLINE PAYMENT" + "\n");
                textData.append("VISIT:WWW.TPCENTRALODISHA.COM" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());

            }
            //updating disconnection notice flag
            if(disupflg==1) {
                updateDisconflg();
            }
            mPrinter.addFeedLine(1);
            mPrinter.addFeedLine(1);
            mPrinter.addFeedLine(1);


        } catch (Exception e) {
            //  ShowMsg.showException(e, method, mContext);
            return false;
        }
        ////////////////////////////////////////////////////////////////////////////////////////


        textData = null;

        return true;
    }


    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();

        //    dispPrinterWarnings(status);

        if (!isPrintable(status)) {
            //ShowMsg.showMsg(makeErrorMessage(status), mContext);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        try {
            mPrinter.sendData(Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            //  ShowMsg.showException(e, "sendData", mContext);
            try {
                mPrinter.disconnect();
            } catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_M10, Printer.MODEL_ANK, this);
        } catch (Exception e) {
            //  ShowMsg.showException(e, "Printer", mContext);
            return false;
        }
        mPrinter.setReceiveEventListener(this);
        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;
        if(devicename.equals("nodevice")){
            try{
                address=findBT();
            }catch (Exception e){}
        }
        Log.d("DemoApp", "address  " + address);

        if (mPrinter == null) {
            return false;
        }

        try {//BT:00:01:90:C2:DB:00
            mPrinter.connect("BT:" + address, Printer.PARAM_DEFAULT);
        } catch (Exception e) {
            //ShowMsg.showException(e, "connect", mContext);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            // ShowMsg.showException(e, "beginTransaction", mContext);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            } catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    //  ShowMsg.showException(e, "endTransaction", mContext);
                }
            });
        }

        try {
            mPrinter.disconnect();
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public synchronized void run() {
                    //    ShowMsg.showException(e, "disconnect", mContext);
                }
            });
        }

        finalizeObject();
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        } else if (status.getOnline() == Printer.FALSE) {
            return false;
        } else {
            ;//print available
        }

        return true;
    }

    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                //   ShowMsg.showResult(code, makeErrorMessage(status), mContext);

                //  dispPrinterWarnings(status);

                //   updateButtonState(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
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

  

    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        //  System.runFinalization();
        //   System.run
        //  System.exit(0);
        super.onDestroy();
    }
    public static String leftAppend1(String str,String str1,int maxlen){
        String retStr="";
        int strlen=0;
        strlen=str.length()+str1.length();
        for(int i=0;i<(maxlen-strlen);i++){
            retStr+=" ";
        }
        str="  "+str+retStr+str1;
        return str;

    }
    public static String leftAppend2(String str0,String str,int leftlen,String Str1,int maxlen){
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
        str0="  "+str0+Str1;
        return str0;

    }
    public static String leftAppend3(String str0,String str,int rlen,String Str1,int Rlen1,String Str2,int maxlen){

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
        str0="  "+str0+Str2;
        return str0;

    }
    String fetchDCContent(String bldt){
        String dcContent="towards electricity dues as on "+bldt+"."+"\n"+" Please ensure payment within 15 days from"+"\n";
        dcContent+="today,failing which supply to your premise"+"\n"+" will be disconnected without further"+"\n";
        dcContent+="notice as per section 56(1) of Electricity"+"\n"+" Act 2003.If paid,kindly inform our"+"\n";
        dcContent+="Electricity Call Center 1912 and ignore"+"\n"+" this notice."+"\n";
        dcContent+="-----------------------------------------"+"\n";
        dcContent+="IGNORE THIS NOTICE FOR ANY PART OF DEMAND"+"\n"+"IF ANY STAY ORDER BY ANY COURT/FORUM IS"+"\n"+"OPERATIVE FOR THAT PART OF DEMAND."+"\n";
        dcContent+="This is computer generated"+"\n"+" notice so no signature require."+"\n";
        return dcContent;
    }
    void  updateDisconflg(){
        DatabaseHelper helper = new DatabaseHelper(context);
        String strUpdateSQL_01 = "";
        strUpdateSQL_01 = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET DISCONNECTION_FLG=1 WHERE INSTALLATION = '" + AccNum + "' ";
        // DatabaseAccess.database.execSQL(strUpdateSQL_01);
        Log.d("DemoApp", "strUpdateSQL_01 " + strUpdateSQL_01);
        helper.updateMTRCONTD(strUpdateSQL_01);
    }
}
