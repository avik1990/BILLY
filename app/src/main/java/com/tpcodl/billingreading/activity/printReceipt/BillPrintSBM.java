package com.tpcodl.billingreading.activity.printReceipt;

import android.os.Bundle;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.database.DatabaseAccess;
import com.tpcodl.billingreading.utils.UtilsClass;
//import com.analogics.printerApi.PrinterApi;
//import com.analogics.printerApi.Printer_2inch_Impact;
//import com.analogics.printerApi.Bluetooth_Printer_2inch_Impact;
//import com.analogics.printerApi.PrinterApi;

public class BillPrintSBM extends AppCompatActivity {

    int counter;
    volatile boolean stopWorker;
    private DatabaseAccess databaseAccess=null;
    // TextView myLabel;
    static TextView strPrntMsg;
  //  static PrinterApi printer=null;
   // static Printer_2inch_Impact api=null;
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
        setContentView(R.layout.activity_bill_print_sbm);

        /*
        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
        strPrntMsg.setText("Printing");
        AccNum="";
        String dubl="";
        String accnumber="";
        Bundle blprintval = getIntent().getExtras();
        AccNum = blprintval.getString("AcctNo");
        // Log.d("DemoApp", "account num  " + AccNum);
        Log.d("DemoApp", "devicename  " + devicename);

        try{
         sendData();
        }catch (Exception e){}
    }

    final Context context = this;
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
            String BillContents = "";
            String filldata = "";
                String doubleHeight = "";
                String widthoff = "";
                String Doublewidth = "";

           // Bluetooth_Printer_2inch_Impact printapi = new Bluetooth_Printer_2inch_Impact();
           // PrinterApi printerap = new PrinterApi();
            api=new Printer_2inch_Impact();
            printer=new PrinterApi();

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
                        printer.printData("\n\n\n\n");
                        Feedline(3);
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(" ");
                        printer.setPrintCommand(	api.font_Double_Height_Width_Off());

                        // BillContents += doubleHeight + String.format("%-24s", "    TEST-BILL PILOT");
                       // BillContents += doubleHeight + String.format("%-24s", "");

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
                        //printerap.setPrintCommand(printapi.font_Double_Height_Width_Off());
                      //  printer.setPrintCommand(api.font_Double_Height_Width_Off());
                       // line=leftAppend("Name", 5);
                       // line=line+rightAppend("Age", 8);
                       // System.out.println(line);

                      //  BillContents += widthoff + String.format("%12s%-12s", "", strmonth.toUpperCase())+ "\n";

                     // BillContents += String.format("%16s", convertDateFormat(rs.getString(79), "DD-MM-YYYY")) + String.format("%8s", BlPrepTm)+ "\n";
                        printer.printData(leftAppend1(strmonth.toUpperCase(), 23) + "\n");
                        Feedline(2);
                        String revdt="";
                        revdt=convertDateFormat(rs.getString(79), "DD-MM-YY");
                        printer.printData(leftAppend2(revdt, 6, BlPrepTm, 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend2(rs.getString(45), 10, version, 23) + "\n");
                        Feedline(2);
                        String revStr="";
                        revStr=rs.getString(99) + " MONTHS";
                        printer.printData(leftAppend1(revStr, 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1(rs.getString(9), 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1(rs.getString(10), 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1(rs.getString(11), 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1("NA", 23) + "\n");
                        Feedline(2);
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1(rs.getString(1), 23) + "\n");//cons_acc double height
                        printer.setPrintCommand(api.font_Double_Height_Width_Off());
                        Feedline(2);
                        printer.printData(leftAppend1(rs.getString(46), 23) + "\n"); // width off
                        Feedline(2);
                        printer.printData(leftAppend1(rs.getString(7), 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1(rs.getString(44), 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1(rs.getString(12), 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1(rs.getString(15), 23) + "\n");//mtrowner
                        Feedline(2);
                        printer.printData(leftAppend1(" ", 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1(rs.getString(14), 23) + "\n");
                        Feedline(2);

                        if (rs.getString(2).trim().length() <= 18) {
                            printer.printData(leftAppend1(rs.getString(2), 23) + "\n");//name
                            Feedline(2);
                            printer.printData(" " + "\n");
                        } else {
                            printer.printData(leftAppend2(rs.getString(2),4,"",23) + "\n");
                            Feedline(4);
                        }
                        StringBuilder strAddr = new StringBuilder(rs.getString(3) + "," + rs.getString(4));
                        Log.d("DemoApp", "add 1  " + strAddr.length());
                        if (rs.getString(3).trim().length() + rs.getString(4).trim().length() > 17) {
                             Log.d("DemoApp", "add 2  " + strAddr.length());
                            if (strAddr.length() >= 42) {
                                strAddr.setLength(42);
                            }
                            if(strAddr.length() <= 19){
                                printer.printData(leftAppend2(strAddr.toString(), 4, "", 23) + "\n");
                                Feedline(2);
                                printer.printData(" " + "\n");

                            }else{
                                printer.printData(leftAppend2(strAddr.toString(), 4, "", 23) + "\n");
                                Feedline(6);
                            }

                        } else {
                            printer.printData(leftAppend1((rs.getString(3) + "," + rs.getString(4)),23) + "\n");
                            Feedline(2);
                            printer.printData(" " + "\n");
                            Feedline(2);
                        }
                        String ibond = "";
                        ibond = rs.getString(18);
                        if (rs.getString(18).equals("I")) {
                            ibond = "I-BOND";
                        }

                        printer.printData(leftAppend2(ibond, 6, "Ph:" + Phase, 23) + "\n");//usage
                        Feedline(2);
                        printer.printData(leftAppend2(rs.getString(8), 6, rs.getDouble(19) + "", 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1(rs.getString(30), 23) + "\n");
                        Feedline(4);
                        // BillContents += "\n";
                        //  BillContents += "\n";
                        printer.printData("\n\n");
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                       //BillContents += "      RDG" + "    DATE" + "   STS" + "\n");
                        //  printer.printData(leftAppend("%5s%-7s%-10s%1s%-1s","", rs.getString(57), rs.getString(79),"",rs.getString(59));
                        //   printer.printData(leftAppend("%5s%-7s%-10s%1s%-1s","", rs.getString(20), rs.getString(22),"",rs.getString(23));
                        revStr="";
                        revStr=convertDateFormat(rs.getString(79), "DD-MM-YYYY");
                        printer.printData(leftAppend3(rs.getString(57), 7, revStr, 1, rs.getString(59), 23)+ "\n");
                        Feedline(2);
                        revStr="";
                        revStr=convertDateFormat(rs.getString(22), "DD-MM-YYYY");
                        printer.printData(leftAppend3(rs.getString(20),7, revStr, 1, rs.getString(23),23)+ "\n");
                        Feedline(2);

                        printer.printData(leftAppend1(rs.getString(61), 23) + "\n");
                        Feedline(2);
                        if (rs.getString(62).equals("N")) {
                            printer.printData(leftAppend1("ACTUAL",23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(leftAppend1("AVERAGE",23)+ "\n");
                            Feedline(2);
                        }
                        // BillContents += doubleHeight+String.format("%-24s", "");
                        printer.printData(" " + "\n");
                        Feedline(2);
                        //  Bill calulation start
                        String formattedData="";
                       // formattedData = String.format("%.02f", rs.getDouble(64));
                        printer.printData(leftAppend1(String.format("%.02f",rs.getDouble(64))+"",23)+ "\n");
                        Feedline(2);
                        blunts = rs.getInt(61);
                        //Bill slab Changed by Santi on 13.01.2016
                        if (rs.getDouble(89) > 0) {
                            printer.printData(leftAppend2(rs.getString(87) + "X" + rs.getDouble(88) + "0=", 3, String.format("%.02f",rs.getDouble(89))+"",23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(" " + "\n");
                            Feedline(2);
                        }
                        if (rs.getDouble(92) > 0) {
                            printer.printData(leftAppend2(rs.getString(90) + "X" + rs.getDouble(91) + "0=", 3, String.format("%.02f",rs.getDouble(92)) + "", 23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(" " + "\n");
                            Feedline(2);
                        }
                        if (rs.getDouble(95) > 0) {
                            printer.printData(leftAppend2(rs.getString(93) + "X" + rs.getDouble(94) + "0=", 3, String.format("%.02f",rs.getDouble(95))+"",23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(" " + "\n");
                            Feedline(2);
                        }
                        if (rs.getDouble(98) > 0) {
                            printer.printData(leftAppend2(rs.getString(96) + "X" + rs.getDouble(97) + "0=", 3, String.format("%.02f",rs.getDouble(98))+"",23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(" " + "\n");
                            Feedline(2);
                        }
                        //end

                        printer.printData(leftAppend1(String.format("%.02f", rs.getDouble(66)) + "", 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1(String.format("%.02f", rs.getDouble(65)) + "", 23) + "\n");
                        Feedline(2);
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1(String.format("%.02f", rs.getDouble(67)) + "", 23) + "\n");//double height
                        printer.setPrintCommand(api.font_Double_Height_Width_Off());
                        Feedline(2);
                        printer.printData(" " + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1(String.format("%.02f", rs.getDouble(49)) + "", 23) + "\n");//previous yr arr
                        Feedline(2);
                        printer.printData(leftAppend1(String.format("%.02f", rs.getDouble(50)) + "", 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend1(String.format("%.02f", rs.getDouble(24)) + "", 23) + "\n");
                        Feedline(2);
                        if (rs.getInt(27) > 0) { //adjustments
                            printer.printData(leftAppend2("(+)",14, String.format("%.02f", rs.getDouble(27))+"",23)+ "\n");
                            Feedline(2);
                        } else if (rs.getInt(26) > 0) {
                            printer.printData(leftAppend2("(-)",14, String.format("%.02f", rs.getDouble(26)) + "",23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(leftAppend1("0.00",23)+ "\n");
                            Feedline(2);
                        }
                        if (rs.getString(23).equals("P")) {
                            printer.printData(leftAppend2("P.ADJ.AMT :", 0, String.format("%.02f", rs.getDouble(28))+"",23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(" " + "\n");
                        }

                        if (rs.getInt(25) > 0) {
                            printer.printData(leftAppend1(String.format("%.02f", rs.getDouble(25))+"", 23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(" " + "\n");
                        }
                        if (rs.getInt(41) > 0) {
                            printer.printData(leftAppend2("SD AVAIL:", 0,String.format("%.02f", rs.getDouble(41)) + "", 23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(" " + "\n");
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        if (rs.getInt(42) > 0) {
                            printer.printData(leftAppend2("ASD:", 0,String.format("%.02f", rs.getDouble(42)) + "", 23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(" " + "\n");

                        }
                        if (rs.getInt(43) > 0) {
                            printer.printData(leftAppend2("ASD ARR:", 0,String.format("%.02f", rs.getDouble(43))+"",23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(" " + "\n");

                        }
                        printer.printData(" " + "\n"); //Penalty amount showing 30.06.2017
                        Feedline(4);
                        // printer.printData(leftAppend("%-8s%16s", "PENALTY:", rs.getString(47));
                        //end
                        printer.printData(leftAppend1(String.format("%.02f", rs.getDouble(69)) + "", 23) + "\n");//total amount
                        Feedline(2);
                        printer.printData(leftAppend1(String.format("%.02f",rs.getDouble(68)) + "", 23) + "\n");//rebate amount
                        Feedline(2);
                        double revamt=0;
                        revamt=(double) Math.round(rs.getDouble(69) - rs.getDouble(68));
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1(String.format("%.02f", revamt) + "", 23) + "\n");//total bill by due date double height

                        printer.setPrintCommand(api.font_Double_Height_Width_Off());
                        Feedline(2);
                        roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                        revamt=0;
                        if (roundupto > 0) {
                            printer.printData(leftAppend2("(+)", 14, String.format("%.02f", roundupto)+ "", 23)+ "\n");
                            Feedline(2);
                        } else {
                            revamt=Math.abs(roundupto);
                            printer.printData(leftAppend1("(-)" + String.format("%.02f", Math.abs(roundupto)), 23)+ "\n");
                            Feedline(2);
                        }
                        printer.printData(" " + "\n");
                        Feedline(2);
                        printer.setPrintCommand(api.font_Double_Height_On());
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        printer.printData(leftAppend1(rs.getString(76), 23) + "\n");//rebate date
                        Feedline(2);
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
                        // printer.printData(leftAppend("%-14s%10.2f", "", (double) Math.round(rs.getDouble(69)));//pay after due date disable on 31.01.17
                        printer.printData(leftAppend1(String.format("%.02f", payaftdt) + "", 23)+ "\n");//pay after due date enable on 31.01.17
                        Feedline(2);
                        printer.setPrintCommand(api.font_Double_Height_Width_Off());

                        // roundupto = Math.round(rs.getDouble(69)) - rs.getDouble(69);// go to if else on 31.01.17
                        // Log.d("DemoApp", "payaftdt 1  " + Math.round(payaftdt));
                        //Log.d("DemoApp", "payaftdt 2  " + payaftdt);
                        //  roundupto = Math.round(payaftdt) - payaftdt; //enable on 31.01.17
                        if (roundupto > 0) {
                            printer.printData(leftAppend1("(+)"+String.format("%.02f",roundupto),23)+ "\n");
                            Feedline(2);
                        } else {
                            printer.printData(leftAppend1( "(-)"+ String.format("%.02f",Math.abs(roundupto)),23)+ "\n");
                            Feedline(2);
                        }

                        printer.printData(" "+"\n"); //dissable on 03.04.2017
                        Feedline(2);
                        printer.printData(" " + "\n");//dissable on 03.04.2017
                        Feedline(2);
                        printer.printData(leftAppend1(rs.getString(32) + "-" + rs.getString(33), 23) + "\n");
                        Feedline(2);
                        printer.printData(leftAppend2(rs.getDouble(34) + "", 3, rs.getString(31), 23) + "\n");
                        Feedline(2);
                        printer.printData(" " + "\n");
                        Feedline(2);
                        printer.printData(" " + "\n");
                        Feedline(2);
                        //Ecs Message printing///////
                        if (rs.getDouble(69) < rs.getDouble(55) && rs.getInt(85) == 1) { //here rs(85) is validation date check query CASE WHEN CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG
                            printer.printData(leftAppend1( "Bg dbtd to Bank thru ECS",23)+ "\n");//ECS Message
                            Feedline(2);
                        } else if (rs.getDouble(69) > rs.getDouble(55) && rs.getInt(85) == 1) {
                            printer.printData(leftAppend1("Val excd-pay cash/chq",23)+ "\n");//ECS Message
                            Feedline(2);
                        } else if (rs.getInt(85) == 0 && rs.getString(56) != null) {
                            printer.printData(leftAppend1( "Dt lpsd-pay cash/chq",23)+ "\n");//ECS Message
                            Feedline(2);
                        } else {
                            printer.printData(" "+"\n");
                            Feedline(2);
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
                            printer.printData(leftAppend1("PAY CASHLESS AND AVAIL", 23)+ "\n");
                            Feedline(2);
                            float percent = 2;
                            // printer.printData(leftAppend("%21s%3s", "ADDITIONL REBATE OF 1"+"%");
                            printer.printData(leftAppend1("ADDITIONL REBATE OF 2", 23) + "%"+ "\n");
                            Feedline(2);
                            /////End 03.04.2017
                        } else {//added on 27.03.2018
                            printer.printData(" "+"\n");
                            Feedline(2);
                            printer.printData(" " + "\n");
                            Feedline(2);
                        }//added on 27.03.2018
                        printer.printData(" "+"\n");
                        Feedline(2);
                        printer.printData(" " + "\n");
                        Feedline(2);

                        if ((blunts / rs.getInt(99)) > 30 && (rs.getString(8).equals("RGVY") || rs.getString(8).equals("BJVY") || rs.getString(8).equals("KJ"))) {
                            printer.printData(leftAppend1( "Ur Tariff Change to Domestic",46)+ "\n");
                            Feedline(2);
                        }
                        if(rs.getString(48).length()>5) {
                            printer.printData(leftAppend1(rs.getString(48),46)+ "\n");
                            Feedline(2);
                        }
                        //  BillContents += widthoff + String.format("%-24s", "Bl Gen By: " + Usernm);
                        Feedline(2);
                        printer.printData(" " + "\n\n\n\n\n\n\n");
                        // printerap.printData(BillContents);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }
                } //while loop close
                rs.close();
                databaseAccess.close();

            //main if condn clos

            ///////


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
                try{
                    sendData();
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
                System.exit(0);

            }
        });//end
        //Continue
        Button contd = (Button) findViewById(R.id.contd);
        contd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reports2 = new Intent(getApplicationContext(), BillSearchactivity.class);
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
            Log.d("DemoApp","e   "+e);
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
                printer.setPrintCommand(	api.dotline_Feed());
            }
            */
    }
}

