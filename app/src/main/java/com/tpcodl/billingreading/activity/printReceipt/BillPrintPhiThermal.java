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
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class BillPrintPhiThermal extends AppCompatActivity {
    private AEMScrybeDevice m_Aem;
    private String BlutoothPrinter;
    private boolean connectPrinterBool;
    private AEMPrinter aemPrinter;
    BluetoothAdapter mBluetoothAdapter;
    private DatabaseAccess databaseAccess = null;
    // TextView myLabel;
    static TextView strPrntMsg;
    final Context context = this;
    private String AccNum = "";
    String mmDeviceAdr = null;
    String devicename = "nodevice";
    private String address = "";
    Double darrear = 0.0;

    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_print_phi_thermal);
        strPrntMsg = (TextView) findViewById(R.id.PrntMsg);
        strPrntMsg.setText("Printing");
        mBluetoothAdapter = null;
        AccNum = "";
        Bundle blprintval = getIntent().getExtras();
        Log.e("ActivityName", "PHIHIHIHIHIHIUHI");
        AccNum = blprintval.getString("AcctNo");
        // Log.d("DemoApp", "account num  " + AccNum);
        Log.d("DemoApp", "devicename  " + devicename);
        m_Aem = new AEMScrybeDevice(new IAemScrybe() {
            @Override
            public void onDiscoveryComplete(ArrayList<String> arrayList) {


            }
        });
        if (devicename.equals("nodevice")) {
            try {
                address = findBT();
            } catch (Exception e) {
            }
        }
        Log.d("DemoApp", "address  " + address);
        try {
            sendData();
        } catch (Exception e) {
        }

        //Reprint The Bill
        Button ReprntBl = (Button) findViewById(R.id.ReprntBl);
        ReprntBl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
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

    void sendData() throws IOException {
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
        int blunts = 0;
        String conversion = "";
        String dDate = "";
        String dConno = "";
        String dname = "";
        darrear = 0.0;
        String dcCont = "";
        int Dcprnt = 1;
        int lapdvar = 0;
        int disupflg = 0;
        //////printing start//////////
        String feeddata = "";
        String billprint = "";
        try {
            String BlPrepTm = "";
            String Billformat = "normal";
            String version = "";
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
            String MrName = "";
            strSelectSQL_02 = "SELECT USER_NAME FROM MST_USER";
            Log.d("DemoApp", "strSelectSQL_02  " + strSelectSQL_02);
            //ResultSet rs = statement.executeQuery(strSelectSQL_01);
            rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
            while (rs1.moveToNext()) {
                MrName = rs1.getString(0);
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
                int prvBlTyp = 0;
                prvBlTyp = rs.getInt(103);
                int noofmonth = rs.getInt(99);
                int monthname = rs.getInt(54);
                // vardate=year.parse(rs.getString(79));
                double roundupto = 0;
                String Mtrtype = rs.getString(13);
                String Phase = "03";
                try {
                    if (Mtrtype.equals("S")) {
                        Phase = "01";
                    }
                } catch (Exception e) {
                    Phase = "01";
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

                dDate = convertDateFormat(rs.getString(79), "DD-MM-YYYY");
                dConno = rs.getString(110);
                dname = rs.getString(2);
                darrear = Double.valueOf(Math.round(rs.getDouble(50)));
                Log.d("DemoApp", "Dcprntss " + Dcprnt);

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
                    billprint += leftAppend1("BILL MONTH:   ", strmonth.toUpperCase(), 32) + "\n";
                    billprint += "DN DT:" + rs.getString(107) + "\n";
                    convertDateFormat(rs.getString(79), "DD-MM-YY");
                    String revdt = "";
                    revdt = convertDateFormat(rs.getString(79), "DD-MM-YY");
                    billprint += "BL DT: " + revdt + "   TIME:" + BlPrepTm + "\n";
                    billprint += "BILL NO:" + rs.getString(102) + "\n";
                    billprint += leftAppend1("VER:", version, 32) + "\n";
                    String revStr = "";
                    revStr = rs.getString(99) + " MONTHS";

                    //billprint += leftAppend1("MOV IN DT:", rs.getString(21), 32) + "\n";
                    //  billprint += leftAppend1("PRV BL TP:", rs.getString(103), 32) + "\n";
                    billprint += leftAppend1("PRV BL REM:", rs.getString(100), 32) + "\n";
                    //billprint += leftAppend1("FC SLAB:", rs.getString(105), 32) + "\n";

                    billprint += leftAppend1("BILLED FOR:", revStr, 32) + "\n";
                    billprint += leftAppend1("DIV:", rs.getString(9), 32) + "\n";
                    billprint += leftAppend1("SUBDIV:", rs.getString(10), 32) + "\n";
                    billprint += leftAppend1("SECTION:", rs.getString(11), 32) + "\n";
                    billprint += leftAppend1("MRU:", rs.getString(6), 32) + "\n";
                    aemPrinter.print(billprint);
                    billprint = "";
                    aemPrinter.setTextDoubleHeight();
                    billprint += leftAppend1("CONSUMER ACCNT. NO:", rs.getString(110), 32) + "\n";//cons_acc double height
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint = "";
                    billprint += leftAppend1("CUST ID:", rs.getString(1), 32) + "\n"; // width off
                    billprint += leftAppend1("CA NO:", rs.getString(46), 32) + "\n"; // width off
                    billprint += leftAppend1("OA NO:", rs.getString(7), 32) + "\n";

                    billprint += leftAppend1("INST NO:", rs.getString(44), 32) + "\n";
                    //  billprint += leftAppend1("CONNECTION DT:", rs.getString(116), 32) + "\n";
                    billprint += leftAppend1("MTR SL NO:", rs.getString(12), 32) + "\n";
                    billprint += leftAppend1("MTR INST. DT:", rs.getString(116), 32) + "\n";
                    billprint += leftAppend1("MTR OWNER:", rs.getString(15), 32) + "\n";//mtrowner
                    // billprint += leftAppend1("MAKE:", "", 32) + "\n";
                    billprint += leftAppend1("METER MF:", rs.getString(14), 32) + "\n";
                    billprint += "NAME:" + rs.getString(2) + "\n";//name
                    StringBuilder strAddr = new StringBuilder(rs.getString(3) + "," + rs.getString(4));
                    billprint += "ADDRS:" + strAddr.toString() + "\n";//ADDRS
                    String ibond = "";
                    try {
                        ibond = rs.getString(18);

                        if (rs.getString(18).equals("I")) {
                            ibond = "I-BOND";
                        }
                    } catch (Exception e) {
                    }
                    billprint += leftAppend1("USAGE:  " + ibond, "  Ph:  " + Phase, 32) + "\n";//usage
                    billprint += leftAppend1("CATEGORY:  " + rs.getString(8), "  CD:  " + rs.getDouble(19), 32) + "\n";
                    billprint += leftAppend1("HIST. MD:", MdivalPrev, 32) + "\n";
                    billprint += leftAppend1("MD RECORD:", MdivalCurr, 32) + "\n";
                    billprint += leftAppend1("BILLED MD:", rs.getString(108), 32) + "\n";
                    billprint += leftAppend1("MR NOTE:", rs.getString(101), 32) + "\n";
                    billprint += leftAppend1("LST OK RD:", rs.getString(39), 32) + "\n";
                    billprint += leftAppend1("LST OK RD DT:", rs.getString(109), 32) + "\n";
                    billprint += leftAppend1("AVG. UNITS:", rs.getString(30), 32) + "\n";
                    billprint += "------------------------------" + "\n";
                    billprint += "      RDG" + "       DATE " + "      STS" + "\n";
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
                    String prvcond = rs.getString(23);
                    if (prvcond.equalsIgnoreCase("F")) {
                        pres = "";
                    }
                    billprint += leftAppend3("PRES:", pres, 6, revStr, 6, rs.getString(59), 32) + "\n";
                    revStr = "";

                    revStr = rs.getString(22);//previous reading date
                    String lstokDt = rs.getString(109);
                    String movinDt = rs.getString(21);
                    String prvRead = rs.getString(20);
                    String lReadPrevDup = prvRead;
                    if (prvBlTyp == 1000 || prvBlTyp == 2000) {
                        if (rs.getString(62).equals("N")) {
                            revStr = lstokDt;
                            prvRead = rs.getString(39);
                        }
                    } else if (prvBlTyp == 5000) {
                        revStr = movinDt;
                    }

                    String newMtrFlg = "";
                    String prevBlMtrDate = "";
                    try {
                        newMtrFlg = rs.getString(117);
                        prevBlMtrDate = rs.getString(118);
                        if (newMtrFlg.equalsIgnoreCase("X")) {
                            revStr = prevBlMtrDate;
                            prvRead = lReadPrevDup;
                        }
                    } catch (Exception e) {
                    }

                    billprint += leftAppend3("PREV:", prvRead, 6, revStr, 6, rs.getString(23), 32) + "\n";

                    billprint += leftAppend1("UNITS ADVANCED:", rs.getString(61), 32) + "\n";

                    if (rs.getString(62).equals("N")) {
                        billprint += leftAppend1("BILL BASIS:", "ACTUAL", 32) + "\n";
                    } else {
                        billprint += leftAppend1("BILL BASIS:", "AVERAGE", 32) + "\n";
                    }
                    billprint += "------------------------------" + "\n";
                    aemPrinter.print(billprint);

                    billprint = "";
                    //  Bill calulation start
                    String formattedData = "";
                    billprint += leftAppend1("MFC/CUST CHRG:", String.format("%.02f", rs.getDouble(64)), 32) + "\n";
                    blunts = rs.getInt(61);

                    /*billprint += printer.font_Courier_24_VIP(leftAppend3("EC UNT", "", 0, "RATE", 1, "TOT AMT", 24));
                    billprint += printer.font_Courier_24_VIP("-----------------------");*/
                    // billprint += leftAppend1("EC UNT", "", 32) + "\n";
                    //  billprint += leftAppend3("EC UNT", "", 0, "RATE", 1, "TOT AMT", 24);
                    // billprint += "------------------------------" + "\n";

                    //Bill slab Changed by Santi on 13.01.2016
                    if (rs.getDouble(89) > 0) {
                        billprint += leftAppend2("EC:", (rs.getString(87) + "X" + rs.getDouble(88) + "0="), 11, String.format("%.02f", rs.getDouble(89)), 32) + "\n";
                    } else {
                        //  billprint+=leftAppend1(" ","", 32);
                    }

                    if (rs.getDouble(92) > 0) {
                        billprint += leftAppend2("EC:", (rs.getString(90) + "X" + rs.getDouble(91) + "0="), 11, String.format("%.02f", rs.getDouble(92)), 32) + "\n";
                    } else {
                        //  billprint+=leftAppend1(" ","", 32);
                    }
                    if (rs.getDouble(95) > 0) {
                        billprint += leftAppend2("EC:", rs.getString(93) + "X" + rs.getDouble(94) + "0=", 11, String.format("%.02f", rs.getDouble(95)), 32) + "\n";
                    } else {
                        //    billprint+=leftAppend1(" ","", 32);
                    }
                    if (rs.getDouble(98) > 0) {
                        billprint += leftAppend2("EC:", rs.getString(96) + "X" + rs.getDouble(97) + "0=", 11, String.format("%.02f", rs.getDouble(98)), 32) + "\n";
                    } else {
                        //    billprint+=leftAppend1(" ","", 32);
                    }
                    //end
                    billprint += leftAppend1("ED CHRG:", String.format("%.02f", rs.getDouble(66)) + "", 32) + "\n";
                    billprint += leftAppend1("METER RENT:", String.format("%.02f", rs.getDouble(65)) + "", 32) + "\n";
                    billprint += leftAppend1("DPS CHRG:", String.format("%.02f", rs.getDouble(114)) + "", 32) + "\n";
                    aemPrinter.print(billprint);
                    billprint = "";
                    aemPrinter.setTextDoubleHeight();
                    billprint += leftAppend1("PRES. BILL AMT:", String.format("%.02f", rs.getDouble(67)) + "", 32) + "\n";//double height

                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint = "";
                    billprint += "------------------------------" + "\n";
                    // billprint += leftAppend1("PREV FY ARREAR:", String.format("%.02f", rs.getDouble(49)) + "", 32) + "\n";//previous yr arr
                    double dAmountArrear = rs.getDouble(50);
                    double dAdj_CR = rs.getDouble(111);
                    double dAdj_DR = rs.getDouble(112);
                    double dpslvd = rs.getDouble(113);
                    dAmountArrear = dAmountArrear + dAdj_CR - dAdj_DR - dpslvd;

                    billprint += leftAppend1("ARREAR:", String.format("%.02f", dAmountArrear) + "", 32) + "\n";


                    if (rs.getInt(27) > 0) { //adjustments
                        billprint += leftAppend2("ADJUSTMENTS:", "(+)", 3, String.format("%.02f", rs.getDouble(27)), 32) + "\n";
                    }
                    if (rs.getInt(26) > 0) {
                        billprint += leftAppend2("ADJUSTMENTS:", "(-)", 3, String.format("%.02f", rs.getDouble(26)), 32) + "\n";
                    } else {
                        billprint += leftAppend1("ADJUSTMENTS:", " ", 32) + "\n";
                    }
                    double povamt = 0;
                    povamt = rs.getDouble(104) + rs.getDouble(106);
                    try {
                        //  if (rs.getInt(103)==1000 || rs.getInt(103)==2000){
                        if (rs.getString(115).equals("Y")) {
                            billprint += leftAppend1("P.ADJ.AMT EC:", String.format("%.02f", rs.getDouble(104)), 32) + "\n";
                            billprint += leftAppend1("P.ADJ.AMT ED:", String.format("%.02f", rs.getDouble(106)), 32) + "\n";
                            //     }
                        } else {
                            //  st+=printer.printLine(leftAppend1("  ", 32);
                            billprint += leftAppend1(" ", "", 32) + "\n";
                        }
                    } catch (Exception e) {

                    }

                    if (rs.getInt(25) > 0) {
                        billprint += leftAppend1("MISC CHARG:", String.format("%.02f", rs.getDouble(25)) + "", 32) + "\n";
                    } else {
                        billprint += leftAppend1("MISC CHARG:", "", 32) + "\n";
                    }
                    if (rs.getInt(41) > 0) {
                        billprint += leftAppend1("SD AVAIL:", String.format("%.02f", rs.getDouble(41)), 32) + "\n";
                    } else {
                        billprint += leftAppend1(" ", "", 32) + "\n";
                    }
                    if (rs.getInt(42) > 0) {
                        billprint += leftAppend1("ASD:", String.format("%.02f", rs.getDouble(42)), 32) + "\n";
                    } else {
                        // st+=printer.printLine(leftAppend1(" ", 32);
                        billprint += leftAppend1(" ", "", 32) + "\n";
                    }
                    if (rs.getInt(43) > 0) {
                        billprint += leftAppend1("ASD ARR:", String.format("%.02f", rs.getDouble(43)) + "", 32) + "\n";
                    } else {
                        billprint += leftAppend1(" ", "", 32) + "\n";
                    }
                    billprint += "------------------------------" + "\n";
                    billprint += leftAppend1("TOTAL AMOUNT:", String.format("%.02f", rs.getDouble(69)) + "", 32) + "\n";//total amount
                    billprint += leftAppend1("REBATE:", String.format("%.02f", rs.getDouble(68)) + "", 32) + "\n";//rebate amount
                    double revamt = 0;
                    revamt = (double) Math.round(rs.getDouble(69) - rs.getDouble(68));
                    aemPrinter.print(billprint);
                    billprint = "";
                    aemPrinter.setTextDoubleHeight();
                    billprint += leftAppend1("TOTAL BL BY DUE DT:", String.format("%.02f", revamt) + "", 32) + "\n";//total bill by due date double height
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint = "";
                    roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                    revamt = 0;
                    aemPrinter.setFontNormal();
                    if (roundupto > 0) {
                        billprint += leftAppend1("ROUNDED UPTO:", "(+)" + String.format("%.02f", roundupto) + "", 32) + "\n";
                    } else {
                        revamt = Math.abs(roundupto);
                        billprint += leftAppend1("ROUNDED UPTO:", "(-)" + String.format("%.02f", Math.abs(roundupto)), 32) + "\n";
                    }
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint = "";
                    aemPrinter.setTextDoubleHeight();
                    billprint += leftAppend1("REBATE DATE", rs.getString(76), 32) + "\n";//rebate date
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint = "";
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
                    billprint += leftAppend1("PAY AFT DUE DT:", String.format("%.02f", payaftdt) + "", 32) + "\n";//pay after due date enable on 31.01.17
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint = "";
                    if (roundupto > 0) {
                        billprint += leftAppend1("ROUNDED UPTO:", "(+)" + String.format("%.02f", roundupto), 32) + "\n";
                    } else {
                        billprint += leftAppend1("ROUNDED UPTO:", "(-)" + String.format("%.02f", Math.abs(roundupto)), 32) + "\n";
                    }
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    billprint = "";

                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint += "LAST PAYMENT DETAILS" + "\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint += "------------------------------" + "\n";
                    billprint += leftAppend1("BNO-RNO:", rs.getString(32) + "-" + rs.getString(33), 32) + "\n";
                    billprint += leftAppend2("AMT:", rs.getDouble(34) + "", 10, "DT:" + rs.getString(31), 32) + "\n";
                    billprint += "------------------------------" + "\n";
                    aemPrinter.print(billprint);
                    billprint = "";
                    aemPrinter.setTextDoubleHeight();
                    aemPrinter.setCenterAlign();
                    billprint += "ECS MESSAGE" + "\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    billprint += "------------------------------" + "\n";
                    //Ecs Message printing///////
                    if (rs.getDouble(69) < rs.getDouble(55) && rs.getInt(85) == 1) { //here rs(85) is validation date check query CASE WHEN CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG
                        billprint += leftAppend1("", "Bg dbtd to Bank thru ECS", 32) + "\n";//ECS Message
                    } else if (rs.getDouble(69) > rs.getDouble(55) && rs.getInt(85) == 1) {
                        billprint += leftAppend1("", "Val excd-pay cash/chq", 32) + "\n";//ECS Message
                    } else if (rs.getInt(85) == 0 && rs.getString(56) != null) {
                        billprint += leftAppend1("", "Dt lpsd-pay cash/chq", 32) + "\n";//ECS Message
                    } else {
                        //  st+=printer.printLine(leftAppend1(" ", 32);
                        billprint += leftAppend1(" ", "", 32) + "\n";
                    }
                    billprint += "------------------------------" + "\n";
                    billprint += leftAppend1("MR NAME:", MrName, 32) + "\n";
                    //added on 03.04.2017 to show additional rebate message
                    double cashlesrbt = 0;
                    cashlesrbt = rs.getDouble(63) * 0.01;//1% cashless rebate of EC
                    if (rs.getString(8).equals("DOM") || (rs.getString(8).equals("GPS") && Phase.equals("01"))) {//added on 27.03.2018.
                        billprint += ("PAY CASHLESS AND AVAIL") + "\n";
                        billprint += ("ADDITIONAL REBATE OF 2%") + "\n";

                        /////End 03.04.2017
                    }
                    if (Math.round(blunts / rs.getDouble(99)) > 30 && (rs.getString(8).equals("DKJ"))) {
                        billprint += leftAppend1("", "Ur Tariff Change to Domestic", 32) + "\n";
                    }
                    billprint += "------------------------------" + "\n";

                    try {
                        if (rs.getString(48).length() > 5) {
                            billprint += leftAppend1("", rs.getString(48), 48) + "\n";
                        }
                    } catch (Exception e) {
                    }
                    billprint += ("YOU CAN PAY WITHIN 7") + "\n";
                    billprint += ("DAYS FROM BILL DATE") + "\n";
                    billprint += ("Please Stay Away from") + "\n";
                    billprint += ("Electric line/Sub-Stations") + "\n";
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();
                    billprint = "";
                    Log.d("dempapp", "Dcprnt" + Dcprnt);

                    int edexptflg = rs.getInt(36);
                    try {
                        if (edexptflg == 1) {//ED_EXEMPT no ed 36////////////////////////////////////////////////
                            Dcprnt = 0;//Ed
                        }
                    } catch (Exception e) {
                        Dcprnt = 0;
                    }

                    Log.d("dempapp", "edexptflg" + edexptflg);
                    disupflg = 0;
                    if (Dcprnt == 1 && Math.round(darrear) > 10000) {
                        disupflg = 1;
                        Log.d("dempapp", "got");
                        billprint = "";
                        aemPrinter.setTextDoubleHeight();
                        aemPrinter.setCenterAlign();
                        billprint += "==============================" + "\n";
                        billprint += "DISCONNECTION NOTICE" + "\n";
                        billprint += "==============================" + "\n";
                        aemPrinter.print(billprint);
                        aemPrinter.setFontNormal();
                        aemPrinter.setLeftAlign();
                        String NoticeDt = dDate.replace("-", "");
                        billprint = "";
                        billprint += ("Notice No:" + dConno + NoticeDt) + "\n";
                        billprint += ("Date:" + dDate) + "\n";
                        billprint += ("Consumer No:" + dConno) + "\n";
                        billprint += ("Name:" + dname) + "\n";
                        billprint += ("You have outstanding amount of") + "\n";
                        aemPrinter.print(billprint);
                        billprint = "";
                        aemPrinter.setTextDoubleHeight();
                        aemPrinter.setCenterAlign();
                        billprint += "Rs:" + Math.round(darrear) + ".00" + "\n";
                        aemPrinter.print(billprint);
                        aemPrinter.setFontNormal();
                        aemPrinter.setLeftAlign();
                        billprint = "";
                        dcCont = fetchDCContent(revdt);
                        billprint += (dcCont) + "\n";
                        aemPrinter.print(billprint);
                        aemPrinter.setFontNormal();
                        aemPrinter.setLeftAlign();
                    }
                    billprint = "";
                    billprint += "------------------------------" + "\n";
                    billprint += ("PLEASE PAY CURRENT DUES  WITH") + "\n";
                    billprint += ("ARREAR TO AVOID DISCONNECTION.") + "\n";
                    billprint += ("FOR BILLING INFORMATION AND") + "\n";
                    billprint += ("ONLINE PAYMENT VISIT:WWW.TPCENTRALODISHA.COM") + "\n";
                    billprint += "\n\n\n";
                    aemPrinter.setLeftAlign();
                    aemPrinter.print(billprint);
                    aemPrinter.setFontNormal();
                    aemPrinter.setLeftAlign();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //updating disconnection notice flag
                if (disupflg == 1) {
                    updateDisconflg();
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
    public static String convertDateFormat(String strTokenValue, String strDataFormat) {
        String strTokenValueRevDt = "";
        String strTokenValueOrgDt = strTokenValue;
        int idxSDate = strDataFormat.indexOf("DD");
        int idxSMonth = strDataFormat.indexOf("MM");
        int idxSYear = strDataFormat.indexOf("Y");
        int idxEYear = strDataFormat.lastIndexOf("Y");
        int idxSHour = strDataFormat.indexOf("HH");

        try {
            strTokenValueRevDt = strTokenValueOrgDt.substring(idxSDate, idxSDate + 2) + "-" +
                    strTokenValueOrgDt.substring(idxSMonth, idxSMonth + 2) + "-" +
                    strTokenValueOrgDt.substring(idxSYear + 2, idxSYear + 4);

        } catch (Exception e) {
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

    public static String leftAppend1(String str, String str1, int maxlen) {
        String retStr = "";
        int strlen = 0;
        try {
            strlen = str.length() + str1.length();
            for (int i = 0; i < (maxlen - strlen); i++) {
                retStr += " ";
            }
            str = str + retStr + str1;
        } catch (Exception e) {

        }
        return str;

    }

    public static String leftAppend2(String str0, String str, int leftlen, String Str1, int maxlen) {
        String retStr = "";
        try {
            for (int i = 0; i < leftlen - str.length(); i++) {
                retStr += " ";
            }
            str = str + retStr;
            str0 = str0 + str;
            retStr = "";
            for (int i = 0; i < (maxlen - (str0.length() + Str1.length())); i++) {
                retStr += " ";
            }
            Str1 = retStr + Str1;
            str0 = str0 + Str1;
        } catch (Exception e) {
        }
        return str0;

    }

    public static String leftAppend3(String str0, String str, int rlen, String Str1, int Rlen1, String Str2, int maxlen) {
        String retStr = "";
        try {
            for (int i = 0; i < (rlen - str.length()); i++) {
                retStr += " ";
            }
            str = str + retStr;
            str0 = str0 + str;
            retStr = "";
            for (int i = 0; i < (Rlen1 - Str1.length()); i++) {
                retStr += " ";
            }
            Str1 = Str1 + retStr;
            str0 = str0 + Str1;

            for (int i = 0; i < (maxlen - (Str2.length() + str0.length())); i++) {
                retStr += " ";
            }
            Str2 = retStr + Str2;
            str0 = str0 + Str2;
        } catch (Exception e) {

        }
        return str0;
    }

    String fetchDCContent(String bldt) {
        String dcContent = "towards electricity dues as on" + "\n" + bldt + ".Please ensure payment" + "\n";
        dcContent += "within 15 days from today," + "\n" + "failing which supply to your" + "\n";
        dcContent += "premises will be disconnected" + "\n" + "without further notice as per" + "\n" + "section 56(1) of Electricity" + "\n" + "Act 2003. If paid,kindly inform" + "\n";
        dcContent += "our Electricity Call Center 1912" + "\n" + "and ignore this notice." + "\n";
        dcContent += "----------------------------" + "\n";
        dcContent += "IGNORE THIS NOTICE FOR ANY PART" + "\n" + "OF DEMAND IF ANY STAY ORDER BY" + "\n" + "ANY COURT/FORUM IS OPERATIVE FOR" + "THAT PART OF DEMAND." + "\n";
        dcContent += "This is computer generated" + "\n" + "notice so no signature require." + "\n";
        return dcContent;
    }

    void updateDisconflg() {
        DatabaseHelper helper = new DatabaseHelper(context);
        String strUpdateSQL_01 = "";
        strUpdateSQL_01 = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET DISCONNECTION_FLG=1 WHERE INSTALLATION = '" + AccNum + "' ";
        // DatabaseAccess.database.execSQL(strUpdateSQL_01);
        Log.d("DemoApp", "strUpdateSQL_01 " + strUpdateSQL_01);
        helper.updateMTRCONTD(strUpdateSQL_01);
        // databaseAccess.close();
    }
}


