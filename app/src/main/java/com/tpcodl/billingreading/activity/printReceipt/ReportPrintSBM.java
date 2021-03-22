package com.tpcodl.billingreading.activity.printReceipt;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.utils.UtilsClass;


//import com.analogics.printerApi.PrinterApi;
//import com.analogics.printerApi.Printer_2inch_Impact;

public class ReportPrintSBM extends AppCompatActivity {
    int counter;
    volatile boolean stopWorker;
    private DatabaseHelper databaseAccess=null;
    // TextView myLabel;
    private static TextView strPrntMsg;
 //   static PrinterApi printer=null;
 //   static Printer_2inch_Impact api=null;
    private String ReportTyp="";
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
        setContentView(R.layout.activity_report_print_sbm);

        /*
        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
       // strPrntMsg.setText("Printing");


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
        try{
            sendData();
        } catch (Exception ex) {
            Toast.makeText(ReportPrintSBM.this, "message13", Toast.LENGTH_LONG).show();
        }
    }

    final Context context = this;
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
            api=new Printer_2inch_Impact();
            printer=new PrinterApi();

                String Billformat="PrePrinted";

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Calendar cal = Calendar.getInstance();

                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                String strUpdateSQL_01="";
                Cursor rs=null;

                if(ReportTyp.equals("D")){
                    BillContents = "";
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    printer.setPrintCommand(api.font_Double_Height_On());
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                    printer.printData(leftAppend1("   DAILY REPORT", 23) + "\n");
                    Feedline(2);
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());

                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    printer.printData(" " + "\n");
                    String formater="";
                    printer.printData(leftAppend1("DATE:"+dateFormat.format(cal.getTime()).toString(), 23) + "\n");
                    Feedline(2);
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("RT NO:" + rs.getString(0), 23) + "\n");
                        Feedline(2);
                    }
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    rs.close();
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM bill_sbm_data";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("TOTAL CONSUMER:"+rs.getString(0), 23) + "\n");
                        Feedline(2);
                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("CONSUMER BILLED:"+rs.getString(0), 23) + "\n");
                        Feedline(2);
                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }

                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("CONSUMER UNBILLED:"+rs.getString(0), 23) + "\n");
                        Feedline(2);
                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0 GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("CURRENT AMT :"+rs.getString(0), 23) + "\n");
                        Feedline(2);
                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT   ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("BILLED UNITS :"+rs.getString(0), 23) + "\n");
                        Feedline(2);
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(bill_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date)  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("TOTAL AMT:"+rs.getString(0), 23) + "\n");
                        Feedline(2);
                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(BILL_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', bill_date) GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                    }
                    rs.close();
                }else if(ReportTyp.equals("S")){
                    printer.printData(" " + "\n");
                    printer.printData(" " + "\n");
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    printer.printData(leftAppend1(".....................", 23) + "\n");

                    Feedline(2);
                    printer.setPrintCommand(api.font_Double_Height_On());
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                    printer.printData(leftAppend1("    SUMMARY REPORT", 23) + "\n");
                    Feedline(2);
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());

                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    printer.printData(" " + "\n");
                    printer.printData(leftAppend1("DATE:"+dateFormat.format(cal.getTime()),23)+ "\n");
                    Feedline(2);
                    printer.printData(leftAppend1(".....................",23)+ "\n");
                    strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("RT NO:" + rs.getString(0), 23) + "\n");
                        Feedline(2);

                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    printer.printData(leftAppend1(".....................",23)+ "\n");
                    strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM bill_sbm_data";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("TOTAL CONSUMER:"+ rs.getString(0), 23) + "\n");
                        Feedline(2);

                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("\"CONSUMER BILLED:"+ rs.getString(0), 23) + "\n");
                        Feedline(2);

                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("CONSUMER UNBILLED:"+ rs.getString(0), 23) + "\n");
                        Feedline(2);

                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  CAT_CODE,ifnull(COUNT(1),0)  AS TOT_CON FROM bill_sbm_data WHERE BILL_FLAG =0 GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("CURRENT AMT :"+ rs.getString(0), 23) + "\n");
                        Feedline(2);

                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(CUR_TOTAL),0)  AS TOT_CUR FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT   ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("BILLED UNITS :"+ rs.getString(0), 23) + "\n");
                        Feedline(2);

                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(UNITS),0)  AS TOT_UNIT FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                    }
                    rs.close();
                    strUpdateSQL_01 = "SELECT  ifnull(SUM(bill_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0  ";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("TOTAL AMT:"+ rs.getString(0), 23) + "\n");
                        Feedline(2);
                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    strUpdateSQL_01 = "SELECT  CAT_CODE, ifnull(SUM(BILL_TOTAL),0)  AS TOT_BILL FROM bill_sbm_data WHERE BILL_FLAG !=0 GROUP BY CAT_CODE";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        if(rs.getString(0).equals("01")){
                            printer.printData(leftAppend1("DOMESTIC   :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("02")){
                            printer.printData(leftAppend1("RGGVY      :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("04")){
                            printer.printData(leftAppend1("BGJY       :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("05")){
                            printer.printData(leftAppend1("KUTIR JYOTI:"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("06")){
                            printer.printData(leftAppend1("GPS        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                        if(rs.getString(0).equals("22")){
                            printer.printData(leftAppend1("SPP        :"+rs.getString(1), 23) + "\n");
                            Feedline(2);
                        }
                    }
                    rs.close();

                }else if(ReportTyp.equals("U")){
                    printer.printData(" " + "\n");
                    printer.printData(" " + "\n");
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    printer.setPrintCommand(api.font_Double_Height_On());
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                    printer.printData(leftAppend1("   UNBILLED REPORT", 23) + "\n");
                    Feedline(2);
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());

                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    printer.printData(" " + "\n");

                    printer.printData(leftAppend1("DATE:"+dateFormat.format(cal.getTime()),23)+ "\n");
                    Feedline(2);
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    strUpdateSQL_01 = "SELECT file_name FROM file_desc where version_flag=2";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1("RT NO:" + rs.getString(0), 23) + "\n");
                        Feedline(2);

                    }
                    rs.close();
                    printer.setPrintCommand(api.font_Double_Height_Width_Off());
                    printer.printData(leftAppend1(".....................", 23) + "\n");
                    Feedline(2);
                    strUpdateSQL_01 = "SELECT   cons_acc AS TOT_CON FROM bill_sbm_data where bill_flag=0";
                    rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
                    while (rs.moveToNext()) {
                        printer.printData(leftAppend1("CONSUMER NO:  "+rs.getString(0), 23) + "\n");
                        Feedline(2);
                    }
                    rs.close();
                }
                databaseAccess.close();


            printer.printData(" " + "\n\n\n\n\n\n");

                Thread.sleep(500);



            Intent reports = new Intent(getApplicationContext(), BillDashBoard.class);
            startActivity(reports);
            finish();

        } catch (NullPointerException e22) {
            e22.printStackTrace();

        } catch (Exception e23) {
            Toast.makeText(ReportPrintSBM.this, "message9"+e23, Toast.LENGTH_LONG).show();
            e23.printStackTrace();
        }
        strPrntMsg.setText("Data Sent to Bluetooth Printer");

    }

   @Override
    protected void onDestroy() {
        System.runFinalizersOnExit(true);
        System.runFinalization();
        //   System.run
        System.exit(0);
        super.onDestroy();
    }

    public static String leftAppend1(String str,int maxlen){
        String retStr="";
        for(int i=0;i<(maxlen-str.length());i++){
            retStr+=" ";
        }
        str=retStr+str;
        return str;

    }
    public static String leftAppend2(String str,int leftlen,String Str1,int maxlen){
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
    public static String leftAppend3(String str,int leftlen,String Str1,int Rlen1,String Str2,int maxlen){
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
    public void Feedline(int maxlen){
        for(int i=0;i<maxlen;i++){
            printer.setPrintCommand(api.dotline_Feed());
        }*/
    }
}


