package com.tpcodl.billingreading.activity.printReceipt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import com.aem.api.AEMPrinter;
import com.aem.api.AEMScrybeDevice;
import com.aem.api.IAemScrybe;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.database.DatabaseAccess;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class ReprintBlPhiThermal extends AppCompatActivity {
    private AEMScrybeDevice m_Aem;
    private String BlutoothPrinter;
    private  boolean connectPrinterBool;
    private AEMPrinter aemPrinter;
    BluetoothAdapter mBluetoothAdapter;
    private DatabaseAccess databaseAccess=null;
    // TextView myLabel;
    static TextView strPrntMsg;
    final Context context = this;
    private String AccNum="";
    String mmDeviceAdr=null;
    String devicename="nodevice";
    private String address = "";
    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reprint_bl_phi_thermal);
        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
        strPrntMsg.setText("Printing");
        mBluetoothAdapter = null;
        AccNum="";

        //  Bundle blprintval = getIntent().getExtras();
        //   AccNum = blprintval.getString("AcctNo");
        try {
            Bundle Reprintbl = getIntent().getExtras();
            AccNum = Reprintbl.getString("accntnum");
            //  Log.d("DemoApp", "account num  " + AccNum);
        }catch(Exception e){
            Toast.makeText(ReprintBlPhiThermal.this, "message878   " + e, Toast.LENGTH_LONG).show();
        }
        Toast.makeText(ReprintBlPhiThermal.this, "message888   " + AccNum, Toast.LENGTH_LONG).show();
        // Log.d("DemoApp", "account num  " + AccNum);
        Log.d("DemoApp", "devicename  " + devicename);


        m_Aem = new AEMScrybeDevice(new IAemScrybe() {
            @Override
            public void onDiscoveryComplete(ArrayList<String> arrayList) {


            }
        });
        if(devicename.equals("nodevice")){
            try{
                address=findBT();
            }catch (Exception e){}
        }
        Log.d("DemoApp", "address  " + address);
        try{
            sendData();
        }catch (Exception e){}

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
            //    System.exit(0);

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
                    mmDeviceAdr = device.getName();
                    // mmDeviceAdr=device.getAddress();
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
    void sendData()throws IOException {
        Log.d("DemoApp", "address" + address);
        BlutoothPrinter = address;
        try {
            connectPrinterBool = m_Aem.connectToPrinter(BlutoothPrinter);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "catch error", Toast.LENGTH_SHORT).show();
        }
        if (connectPrinterBool) {
            aemPrinter = m_Aem.getAemPrinter();
            Toast.makeText(getApplicationContext(), "Printer connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), " error while connecting", Toast.LENGTH_SHORT).show();
        }
        int blunts=0;
        String conversion="";
        int lapdvar=0;
        //////printing start//////////
        String feeddata = "";
        String billprint="";
        try{
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

                billprint = "";
                try {
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint = "TPCODL \n";
                    billprint += "ELECTRICITY BILL \n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint += "THIS BILL SHALL NOT BE A PROOF\n";
                    billprint += "OF LAWFUL OCCUPATION OF PREMISES\n";
                    billprint += "IN CASE I-BOND IS MENTIONED\n";
                    billprint += "HERE UNDER\n";
                    billprint += "------------------------------\n";
                    aemPrinter.print(billprint);

                    billprint = "";
                    aemPrinter.setLeftAlign();
                    billprint += leftAppend1("BILL MONTH:   ", strmonth.toUpperCase(), 32)+"\n";
                    String revdt = "";
                    revdt = convertDateFormat(rs.getString(79), "DD-MM-YY");
                    billprint += "BL DT: " + revdt + "   TIME:" + BlPrepTm+"\n";
                    billprint += "BILL NO:" + rs.getString(45) + "    VER:" + version+"\n";
                    String revStr = "";
                    revStr = rs.getString(99) + " MONTHS";
                    billprint += leftAppend1("BILLED FOR:", revStr, 32)+"\n";
                    billprint += leftAppend1("DIV:", rs.getString(9), 32)+"\n";
                    billprint += leftAppend1("SUBDIV:", rs.getString(10), 32)+"\n";
                    billprint += leftAppend1("SECTION:", rs.getString(11), 32)+"\n";
                    billprint += "SBM NO:" + "NA"+"\n";
                    aemPrinter.print(billprint);
                    billprint = "";
                    aemPrinter.setTextDoubleHeight();
                    billprint += leftAppend1("CONSUMER ACCNT. NO:", rs.getString(1), 32)+"\n";//cons_acc double height
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint = "";
                    billprint += leftAppend1("CUSTOMER ID:", rs.getString(46), 32)+"\n"; // width off
                    billprint += leftAppend1("OA NO:", rs.getString(7), 32)+"\n";
                    billprint += leftAppend1("INST NO:", rs.getString(44), 32)+"\n";
                    billprint += leftAppend1("MTR SL NO:", rs.getString(12), 32)+"\n";
                    billprint += leftAppend1("MTR OWNER:", rs.getString(15), 32)+"\n";//mtrowner
                    billprint+=leftAppend1("MAKE:", "", 32)+"\n";
                    billprint += leftAppend1("METER MF:",rs.getString(14), 32)+"\n";
                    billprint += "NAME:"+rs.getString(2)+"\n";//name
                    StringBuilder strAddr = new StringBuilder(rs.getString(3) + "," + rs.getString(4));
                    billprint+="ADDRS:"+strAddr.toString()+"\n";//ADDRS
                    String ibond = "";
                    ibond = rs.getString(18);
                    if (rs.getString(18).equals("I")) {
                        ibond = "I-BOND";
                    }
                    billprint+="USAGE:  "+ibond+"  Ph:  " + Phase+"\n";//usage
                    billprint+="T.CAT:  " + rs.getString(8) + "  CD:  " + rs.getDouble(19)+"\n";
                    billprint+=leftAppend1("AVG. UNITS:", rs.getString(30), 32)+"\n";
                    billprint+= "------------------------------"+"\n";
                    billprint+= "      RDG" + "       DATE " + "      STS"+"\n";
                    revStr = "";
                    revStr = convertDateFormat(rs.getString(79), "DD-MM-YY");
                    billprint+=leftAppend3("PRES:", rs.getString(57), 6, revStr,6, rs.getString(59), 32)+"\n";
                    revStr = "";
                    revStr = convertDateFormat(rs.getString(22), "DD-MM-YY");
                    billprint+=leftAppend3("PREV:", rs.getString(20), 6, revStr, 6, rs.getString(23), 32)+"\n";
                    billprint+=leftAppend1("UNITS ADVANCED:", rs.getString(61), 32)+"\n";
                    if (rs.getString(62).equals("N")) {
                        billprint+=leftAppend1("BILL BASIS:", "ACTUAL", 32)+"\n";
                    } else {
                        billprint+=leftAppend1("BILL BASIS:", "AVERAGE", 32)+"\n";
                    }
                    billprint+= "------------------------------"+"\n";
                    aemPrinter.print(billprint);

                    billprint="";
                    //  Bill calulation start
                    String formattedData = "";
                    billprint+=leftAppend1("MFC/CUST CHRG:", String.format("%.02f", rs.getDouble(64)), 32)+"\n";
                    blunts = rs.getInt(61);
                    //Bill slab Changed by Santi on 13.01.2016
                    if (rs.getDouble(89) > 0) {
                        billprint+=leftAppend2("EC:", (rs.getString(87) + "X" + rs.getDouble(88) + "0="), 11, String.format("%.02f", rs.getDouble(89)), 32)+"\n";
                    } else {
                        //  billprint+=leftAppend1(" ","", 32);
                    }

                    if (rs.getDouble(92) > 0) {
                        billprint+=leftAppend2("EC:",(rs.getString(90) + "X" + rs.getDouble(91) + "0="), 11, String.format("%.02f", rs.getDouble(92)), 32)+"\n";
                    } else {
                        //  billprint+=leftAppend1(" ","", 32);
                    }
                    if (rs.getDouble(95) > 0) {
                        billprint+=leftAppend2("EC:",rs.getString(93) + "X" + rs.getDouble(94) + "0=",11, String.format("%.02f", rs.getDouble(95)), 32)+"\n";
                    } else {
                        //    billprint+=leftAppend1(" ","", 32);
                    }
                    if (rs.getDouble(98) > 0) {
                        billprint += leftAppend2("EC:",rs.getString(96) + "X" + rs.getDouble(97) + "0=", 11, String.format("%.02f", rs.getDouble(98)), 32)+"\n";
                    } else {
                        //    billprint+=leftAppend1(" ","", 32);
                    }
                    //end
                    billprint+=leftAppend1("ED CHRG:", String.format("%.02f", rs.getDouble(66)) + "", 32)+"\n";
                    billprint+=leftAppend1("METER RENT:", String.format("%.02f", rs.getDouble(65)) + "", 32)+"\n";
                    aemPrinter.print(billprint);
                    billprint="";
                    aemPrinter.setTextDoubleHeight();
                    billprint+=leftAppend1("PRES. BILL AMT:", String.format("%.02f", rs.getDouble(67)) + "", 32)+"\n";//double height

                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint="";
                    billprint+= "------------------------------"+"\n";


                    billprint+=leftAppend1("PREV FY ARREAR:", String.format("%.02f", rs.getDouble(49)) + "", 32)+"\n";//previous yr arr
                    billprint+=leftAppend1("CURR FY ARREAR:", String.format("%.02f", rs.getDouble(50)) + "", 32)+"\n";
                    billprint+=leftAppend1("DPS CHRG:", String.format("%.02f", rs.getDouble(24)) + "", 32)+"\n";

                    if (rs.getInt(27) > 0) { //adjustments
                        billprint+=leftAppend2("ADJUSTMENTS:","(+)", 3, String.format("%.02f", rs.getDouble(27)), 32)+"\n";

                    } else if (rs.getInt(26) > 0) {
                        billprint+=leftAppend2("ADJUSTMENTS:","(-)",3, String.format("%.02f", rs.getDouble(26)), 32)+"\n";
                    } else {
                        billprint+=leftAppend1("ADJUSTMENTS:"," ", 32)+"\n";
                    }
                    if (rs.getString(23).equals("P")) {
                        billprint+=leftAppend1("P.ADJ.AMT:", String.format("%.02f", rs.getDouble(28)), 32)+"\n";
                    } else {
                        //  st+=printer.printLine(leftAppend1("  ", 32);
                        billprint+=leftAppend1(" ", "", 32)+"\n";
                    }

                    if (rs.getInt(25) > 0) {
                        billprint+=leftAppend1("MISC CHARG:", String.format("%.02f", rs.getDouble(25)) + "", 32)+"\n";
                    } else {
                        billprint+=leftAppend1("MISC CHARG:", "", 32)+"\n";
                    }
                    if (rs.getInt(41) > 0) {
                        billprint+=leftAppend1("SD AVAIL:", String.format("%.02f", rs.getDouble(41)), 32)+"\n";
                    } else {
                        billprint+=leftAppend1(" ", "", 32)+"\n";
                    }
                    if (rs.getInt(42) > 0) {
                        billprint+=leftAppend1("ASD:", String.format("%.02f", rs.getDouble(42)), 32)+"\n";
                    } else {
                        // st+=printer.printLine(leftAppend1(" ", 32);
                        billprint+=leftAppend1(" ", "", 32)+"\n";
                    }
                    if (rs.getInt(43) > 0) {
                        billprint+=leftAppend1("ASD ARR:", String.format("%.02f", rs.getDouble(43)) + "", 32)+"\n";
                    } else {
                        billprint+=leftAppend1(" ", "", 32)+"\n";
                    }
                    billprint+= "------------------------------"+"\n";
                    billprint+=leftAppend1("TOTAL AMOUNT:", String.format("%.02f", rs.getDouble(69)) + "", 32)+"\n";//total amount
                    billprint+=leftAppend1("REBATE:", String.format("%.02f", rs.getDouble(68)) + "", 32)+"\n";//rebate amount
                    double revamt = 0;
                    revamt = (double) Math.round(rs.getDouble(69) - rs.getDouble(68));
                    aemPrinter.print(billprint);
                    billprint="";
                    aemPrinter.setTextDoubleHeight();
                    billprint+=leftAppend1("TOTAL BL BY DUE DT:", String.format("%.02f", revamt) + "", 32)+"\n";//total bill by due date double height
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint="";
                    roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                    revamt = 0;
                    aemPrinter.setFontNormal();
                    if (roundupto > 0) {
                        billprint+=leftAppend1("ROUNDED UPTO:","(+)"+ String.format("%.02f", roundupto) + "", 32)+"\n";
                    } else {
                        revamt = Math.abs(roundupto);
                        billprint+=leftAppend1("ROUNDED UPTO:","(-)"+ String.format("%.02f", Math.abs(roundupto)), 32)+"\n";
                    }
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint="";
                    aemPrinter.setTextDoubleHeight();
                    billprint+=leftAppend1("REBATE DATE", rs.getString(76), 32)+"\n";//rebate date
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint="";
                    //changes on 31.01.2017 if total amount <0 and total amt-rebate amt<0
                    double payaftdt = 0;
                    if (rs.getDouble(69) < 0 && ((double) Math.round(rs.getDouble(69) - rs.getDouble(68))) < 0) {
                        payaftdt = ((double) Math.round(rs.getDouble(69) - rs.getDouble(68)));
                        roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                    } else {
                        payaftdt = (double) Math.round(rs.getDouble(69));
                        roundupto = Math.round(rs.getDouble(69)) - rs.getDouble(69);
                    }
                    aemPrinter.setTextDoubleHeight();
                    billprint+=leftAppend1("PAY AFT DUE DT:", String.format("%.02f", payaftdt) + "", 32)+"\n";//pay after due date enable on 31.01.17
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint="";
                    if (roundupto > 0) {
                        billprint+=leftAppend1("ROUNDED UPTO:","(+)" + String.format("%.02f", roundupto), 32)+"\n";
                    } else {
                        billprint+=leftAppend1("ROUNDED UPTO:","(-)" + String.format("%.02f", Math.abs(roundupto)), 32)+"\n";
                    }
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint="";

                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint+= "LAST PAYMENT DETAILS"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint="";
                    billprint+= "------------------------------"+"\n";
                    billprint+=leftAppend1("BNO-RNO:", rs.getString(32) + "-" + rs.getString(33), 32)+"\n";
                    billprint += leftAppend2("AMT:", rs.getDouble(34) + "", 10, "DT:" + rs.getString(31), 32)+"\n";
                    billprint+= "------------------------------"+"\n";
                    aemPrinter.print(billprint);
                    billprint="";
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint+= "ECS MESSAGE"+"\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint="";
                    billprint+= "------------------------------"+"\n";
                    //Ecs Message printing///////
                    if (rs.getDouble(69) < rs.getDouble(55) && rs.getInt(85) == 1) { //here rs(85) is validation date check query CASE WHEN CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG
                        billprint+=leftAppend1("","Bg dbtd to Bank thru ECS", 32)+"\n";//ECS Message
                    } else if (rs.getDouble(69) > rs.getDouble(55) && rs.getInt(85) == 1) {
                        billprint+=leftAppend1("","Val excd-pay cash/chq", 32)+"\n";//ECS Message
                    } else if (rs.getInt(85) == 0 && rs.getString(56) != null) {
                        billprint+=leftAppend1("","Dt lpsd-pay cash/chq", 32)+"\n";//ECS Message
                    } else {
                        //  st+=printer.printLine(leftAppend1(" ", 32);
                        billprint+=leftAppend1(" ", "", 32)+"\n";
                    }
                    billprint+= "------------------------------"+"\n";
                    //added on 03.04.2017 to show additional rebate message
                    double cashlesrbt = 0;
                    cashlesrbt = rs.getDouble(63) * 0.01;//1% cashless rebate of EC
                    if (rs.getString(8).equals("DOM") || (rs.getString(8).equals("GPS") && Phase.equals("01"))) {//added on 27.03.2018.
                        billprint+=leftAppend1("         ", "PAY CASHLESS AND AVAIL", 32)+"\n";
                        billprint+=leftAppend1("         ", "ADDITIONAL REBATE OF 2%", 32)+"\n";

                        /////End 03.04.2017
                    }
                    billprint+=leftAppend1("", "YOU CAN PAY WITHIN 7", 32)+"\n";
                    billprint+=leftAppend1("", "DAYS FROM BILL DATE", 32)+"\n";
                    if ((blunts / rs.getInt(99)) > 30 && (rs.getString(8).equals("RGVY") || rs.getString(8).equals("BJVY") || rs.getString(8).equals("KJ"))) {
                        billprint+=leftAppend1("", "Ur Tariff Change to Domestic", 32)+"\n";
                    }
                    billprint+= "------------------------------"+"\n";
                    if(rs.getString(48).length()>5){
                        billprint+=leftAppend1("", rs.getString(48), 48)+"\n";
                    }
                    billprint+=leftAppend1("", "Please Stay Away from", 32)+"\n";
                    billprint+=leftAppend1("", "Electric line/Sub-Stations", 32)+"\n";
                    billprint+= "------------------------------"+"\n";
                    billprint+=leftAppend1("", "PEASE PAY CURRENT DUE ALONG WITH", 32)+"\n";
                    billprint+=leftAppend1("", "ARREAR TO AVOID DISCONNECTION.", 32)+"\n";
                    billprint+=leftAppend1("", "FOR BILLING INFORMATION AND", 32)+"\n";
                    billprint+=leftAppend1("", "ONLINE PAYMENT VISIT:WWW.TPCENTRALODISHA.COM", 48)+"\n";
                    billprint+="\n\n\n";
                    aemPrinter.setLeftAlign();
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    try {
                        m_Aem.disConnectPrinter();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                } catch (Exception printerExceptions) {
                    printerExceptions.printStackTrace();
                    Log.e("innn", "createPrintData: " + printerExceptions.getMessage());
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        strPrntMsg.setText("Data Sent to Bluetooth Printer");

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

}


