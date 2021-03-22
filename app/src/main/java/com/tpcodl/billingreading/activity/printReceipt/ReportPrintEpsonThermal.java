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
import android.widget.TextView;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.database.DatabaseAccess;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

public class ReportPrintEpsonThermal extends Activity implements ReceiveListener {

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
    // TextView myLabel;
    static TextView strPrntMsg;
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
        ReportTyp="";
        try {
            Bundle Report = getIntent().getExtras();
            ReportTyp = Report.getString("ReportTyp");
            Log.d("DemoApp", "ReportTyp   " + ReportTyp);
        }catch(Exception e){
            //  Toast.makeText(ReportPrintActivity.this, "message878   " + e, Toast.LENGTH_LONG).show();
        }

        runPrintReceiptSequence();

    }


    private void runPrintReceiptSequence() {

        initializeObject();
        createReceiptData();
        printData();

    }

    private boolean createReceiptData() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String strUpdateSQL_01="";
        Cursor rs=null;
        StringBuilder textData = new StringBuilder();
        if (mPrinter == null) {
            return false;
        }
        if(ReportTyp.equals("D")){
            try {
                mPrinter.addFeedLine(1);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(2, 2);
                textData.append("DAILY REPORT" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("----------------------------------------" + "\n");
                textData.append("DATE:" + dateFormat.format(cal.getTime()) + "\n");
                textData.append("----------------------------------------" + "\n");
               /* strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("RT NO:"+rs.getString(0)+"\n");
                }
                rs.close();*/
                textData.append("----------------------------------------" + "\n");
                strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM tbl_spotbill_header_details";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("TOTAL CONSUMER:"+rs.getString(0)+"\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        textData.append("DOMESTIC   :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("02")){
                        textData.append("RGGVY      :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("04")){
                        textData.append("BGJY       :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("05")){
                        textData.append("KUTIR JYOTI:"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("06")){
                        textData.append("GPS        :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("22")){
                        textData.append("SPP        :"+rs.getString(1)+"\n");
                    }
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("CONSUMER BILLED:"+rs.getString(0)+"\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        textData.append("DOMESTIC   :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("02")){
                        textData.append("RGGVY      :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("04")){
                        textData.append("BGJY       :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("05")){
                        textData.append("KUTIR JYOTI:"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("06")){
                        textData.append("GPS        :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("22")){
                        textData.append("SPP        :"+rs.getString(1)+"\n");
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("CONSUMER UNBILLED:"+rs.getString(0)+"\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0 GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        textData.append("DOMESTIC   :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("02")){
                        textData.append("RGGVY      :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("04")){
                        textData.append("BGJY       :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("05")){
                        textData.append("KUTIR JYOTI:"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("06")){
                        textData.append("GPS        :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("22")){
                        textData.append("SPP        :"+rs.getString(1)+"\n");
                    }
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("CURRENT AMT :"+rs.getString(0)+"\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        textData.append("DOMESTIC   :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("02")){
                        textData.append("RGGVY      :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("04")){
                        textData.append("BGJY       :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("05")){
                        textData.append("KUTIR JYOTI:"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("06")){
                        textData.append("GPS        :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("22")){
                        textData.append("SPP        :"+rs.getString(1)+"\n");
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT   ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("BILLED PRESENT_BILL_UNITS :"+rs.getString(0)+"\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        textData.append("DOMESTIC   :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("02")){
                        textData.append("RGGVY      :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("04")){
                        textData.append("BGJY       :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("05")){
                        textData.append("KUTIR JYOTI:"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("06")){
                        textData.append("GPS        :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("22")){
                        textData.append("SPP        :"+rs.getString(1)+"\n");
                    }
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date)  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("TOTAL AMT:"+rs.getString(0)+"\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if(rs.getString(0).equals("01")){
                        textData.append("DOMESTIC   :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("02")){
                        textData.append("RGGVY      :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("04")){
                        textData.append("BGJY       :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("05")){
                        textData.append("KUTIR JYOTI:"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("06")){
                        textData.append("GPS        :"+rs.getString(1)+"\n");
                    }
                    if(rs.getString(0).equals("22")){
                        textData.append("SPP        :"+rs.getString(1)+"\n");
                    }
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addFeedLine(1);
                mPrinter.addFeedLine(1);
                mPrinter.addFeedLine(1);
                mPrinter.addFeedLine(1);
            }catch (Exception e){e.printStackTrace();
                return false;}
//////////////////////////////////

        }else if(ReportTyp.equals("S")){
            try {
                mPrinter.addFeedLine(1);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(2, 2);
                textData.append("SUMMARY REPORT" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("----------------------------------------" + "\n");


                textData.append("DATE:" + dateFormat.format(cal.getTime()) + "\n");
                textData.append("....................." + "\n");
            /*    strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("RT NO:" + rs.getString(0) + "\n");
                }
                rs.close();*/
                textData.append("....................." + "\n");
                strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM tbl_spotbill_header_details";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("TOTAL CONSUMER:" + rs.getString(0) + "\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if (rs.getString(0).equals("01")) {
                        textData.append("DOMESTIC   :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("02")) {
                        textData.append("RGGVY      :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("04")) {
                        textData.append("BGJY       :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("05")) {
                        textData.append("KUTIR JYOTI:" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("06")) {
                        textData.append("GPS        :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("22")) {
                        textData.append("SPP        :" + rs.getString(1) + "\n");
                    }
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("CONSUMER BILLED:" + rs.getString(0) + "\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if (rs.getString(0).equals("01")) {
                        textData.append("DOMESTIC   :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("02")) {
                        textData.append("RGGVY      :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("04")) {
                        textData.append("BGJY       :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("05")) {
                        textData.append("KUTIR JYOTI:" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("06")) {
                        textData.append("GPS        :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("22")) {
                        textData.append("SPP        :" + rs.getString(1) + "\n");
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("CONSUMER UNBILLED:" + rs.getString(0) + "\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY,ifnull(COUNT(1),0)  AS TOT_CON FROM tbl_spotbill_header_details WHERE READ_FLAG =0 GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if (rs.getString(0).equals("01")) {
                        textData.append("DOMESTIC   :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("02")) {
                        textData.append("RGGVY      :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("04")) {
                        textData.append("BGJY       :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("05")) {
                        textData.append("KUTIR JYOTI:" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("06")) {
                        textData.append("GPS        :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("22")) {
                        textData.append("SPP        :" + rs.getString(1) + "\n");
                    }
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("CURRENT AMT :" + rs.getString(0) + "\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if (rs.getString(0).equals("01")) {
                        textData.append("DOMESTIC   :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("02")) {
                        textData.append("RGGVY      :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("04")) {
                        textData.append("BGJY       :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("05")) {
                        textData.append("KUTIR JYOTI:" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("06")) {
                        textData.append("GPS        :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("22")) {
                        textData.append("SPP        :" + rs.getString(1) + "\n");
                    }
                }
                rs.close();
                strUpdateSQL_01 = "SELECT   ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("BILLED PRESENT_BILL_UNITS :" + rs.getString(0) + "\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if (rs.getString(0).equals("01")) {
                        textData.append("DOMESTIC   :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("02")) {
                        textData.append("RGGVY      :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("04")) {
                        textData.append("BGJY       :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("05")) {
                        textData.append("KUTIR JYOTI:" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("06")) {
                        textData.append("GPS        :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("22")) {
                        textData.append("SPP        :" + rs.getString(1) + "\n");
                    }
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0  ";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("TOTAL AMT:" + rs.getString(0) + "\n");
                }
                rs.close();
                strUpdateSQL_01 = "SELECT  RATE_CATEGORY, ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM tbl_spotbill_header_details WHERE READ_FLAG !=0 GROUP BY RATE_CATEGORY";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    if (rs.getString(0).equals("01")) {
                        textData.append("DOMESTIC   :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("02")) {
                        textData.append("RGGVY      :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("04")) {
                        textData.append("BGJY       :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("05")) {
                        textData.append("KUTIR JYOTI:" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("06")) {
                        textData.append("GPS        :" + rs.getString(1) + "\n");
                    }
                    if (rs.getString(0).equals("22")) {
                        textData.append("SPP        :" + rs.getString(1) + "\n");
                    }
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addFeedLine(1);
                mPrinter.addFeedLine(1);
                mPrinter.addFeedLine(1);
                mPrinter.addFeedLine(1);
            }catch (Exception e)  {e.printStackTrace(); return false;}
        }else if(ReportTyp.equals("U")){
            try {

                mPrinter.addFeedLine(1);
                mPrinter.addTextAlign(Printer.ALIGN_CENTER);
                mPrinter.addTextSize(2, 2);
                textData.append("UNBILLED REPORT" + "\n");
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addTextSize(1, 1);
                mPrinter.addTextAlign(Printer.ALIGN_LEFT);
                textData.append("----------------------------------------" + "\n");
                textData.append("DATE:" + dateFormat.format(cal.getTime()) + "\n");
                textData.append("....................." + "\n");
           /*     strUpdateSQL_01 = "SELECT file_name FROM file_desc";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("RT NO:" + rs.getString(0) + "\n");
                }
                rs.close();*/
                textData.append("....................." + "\n");
                strUpdateSQL_01 = "SELECT   LEGACY_ACCOUNT_NO2 AS TOT_CON FROM tbl_spotbill_header_details where READ_FLAG=0";
                rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                while (rs.moveToNext()) {
                    textData.append("CONSUMER NO:  " + rs.getString(0) + "\n");
                }
                rs.close();
                mPrinter.addText(textData.toString());
                textData.delete(0, textData.length());
                mPrinter.addFeedLine(1);
                mPrinter.addFeedLine(1);
                mPrinter.addFeedLine(1);
                mPrinter.addFeedLine(1);
            }catch (Exception e){e.printStackTrace(); return false;}
        }
        databaseAccess.close();
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
    @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        //  System.runFinalization();
        //   System.run
        //  System.exit(0);
        super.onDestroy();
    }


}
