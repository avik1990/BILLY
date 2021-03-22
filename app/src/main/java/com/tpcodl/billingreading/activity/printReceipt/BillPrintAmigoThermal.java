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

import androidx.appcompat.app.AppCompatActivity;

import com.qps.btgenie.BluetoothManager;
import com.qps.btgenie.QABTPAccessory;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.SearchDataActivity;
import com.tpcodl.billingreading.database.DatabaseAccess;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class BillPrintAmigoThermal extends AppCompatActivity {
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
    private DatabaseAccess databaseAccess = null;
    // TextView myLabel;
    static TextView strPrntMsg;
    private String address = "";
    private String AccNum = "";
    String mmDeviceAdr = null;
    String devicename = "nodevice";
    Double darrear = 0.0;
    //AMIGOS
    BluetoothManager btpObject;
    public static final int DoubleWidth = 3, DoubleHght = 2, Normal = 1;
    boolean closeprinter = false;

    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_print_amigo_thermal);
        btpObject = BluetoothManager.getInstance(this, new QABTPAccessory() {
            @Override
            public void onBluetoothDeviceFound(BluetoothDevice bluetoothDevice) {
                Log.d("DemoApp", "devicename 1  ");
            }

            @Override
            public void onClientConnectionSuccess() {
                Log.d("DemoApp", "devicename 2  ");
                //Do Not start printing here
            }

            @Override
            public void onClientConnectionFail() {
                Log.d("DemoApp", "devicename 3  ");
            }

            @Override
            public void onClientConnecting() {
                Log.d("DemoApp", "devicename 4 ");
            }

            @Override
            public void onClientDisconnectSuccess() {
                Log.d("DemoApp", "devicename 5  ");
            }

            @Override
            public void onNoClientConnected() {
                Log.d("DemoApp", "devicename 6 ");

            }

            @Override
            public void onBluetoothStartDiscovery() {
                Log.d("DemoApp", "devicename 7  ");
            }

            @Override
            public void onBluetoothNotAvailable() {
                Log.d("DemoApp", "devicename 8  ");
            }

            @Override
            public void onBatterystatuscheck(String s) {
                if (closeprinter == true) {       //Added to close the printer
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
        mmOutputStream = null;
        mmInputStream = null;
        mmDevice = null;
        mBluetoothAdapter = null;

        AccNum = "";
        Bundle blprintval = getIntent().getExtras();
        AccNum = blprintval.getString("AcctNo");
        // Log.d("DemoApp", "account num  " + AccNum);
        Log.d("DemoApp", "devicename  " + devicename);

        // Log.d("DemoApp", "account num  " + AccNum);
        Log.d("DemoApp", "devicename  " + devicename);
        if (devicename.equals("nodevice")) {
            try {
                //     Log.d("DemoApp", "Entering findbt  " );
                address = findBT();
                Log.d("DemoApp", "BT found ");
            } catch (Exception ex) {

                //     Log.d("DemoApp", "Exception 1  " + ex);
            }
        }

        try {
            Log.d("DemoApp", "Entering open bt  ");
            if (!btpObject.isConnected() == true && address != null) {
                btpObject.createClient(address);

                Log.d("DemoApp", "BT opened ");
            } else {
                System.out.println("BT Closed!..");
            }

        } catch (Exception ex) {
            //     Log.d("DemoApp", "Exception 2  " + ex);

        }


        try {
            Log.d("DemoApp", "sending data  ");
            sendData();
            //    Log.d("DemoApp", "data sent ");
        } catch (Exception ex) {
            Log.d("DemoApp", "Exception 3 " + ex);
        }
        try {
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
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    // MP300 is the name of the bluetooth printer device
                    mmDevice = device;
                    mmDeviceAdr = device.getAddress();
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
        int blunts = 0;
        String conversion = "";
        int lapdvar = 0;

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
            String Billformat = "";
            String papertyp = "0";
            databaseAccess = DatabaseAccess.getInstance(context);
            databaseAccess.open();
            String strSelectSQL_02 = "select papertyp from SA_USER where userid='" + Usernm + "'";
            Cursor rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
            while (rs1.moveToNext()) {
                papertyp = rs1.getString(0);
            }
            rs1.close();
            rs1 = null;
            databaseAccess.close();
            Log.d("DemoApp", "papertyp  " + papertyp);
            try {
                if (papertyp.equals("1") || papertyp.isEmpty() || papertyp == null) {//0 =pre-printed
                    Billformat = "blank";
                }
            } catch (Exception e) {
                Billformat = "blank";
            }
            Log.d("DemoApp", "Billformat  " + Billformat);
//code for printing
            int disupflg = 0;
            if (btpObject.isConnected() == true) {
                Log.d("DemoApp", "Printing Data ...  ");
                btpObject.printerfilter(btpObject.PRINTER_DEFAULT);

                String doubleHeight = "";
                String widthoff = "";
                String Doublewidth = "";
                String dDate = "";
                String dConno = "";
                String dname = "";
                darrear = 0.0;
                String dcCont = "";
                int Dcprnt = 1;
                Log.d("DemoApp", "Dcprntss " + Dcprnt);
                String BlPrepTm = "";
                Calendar c = Calendar.getInstance();
                SimpleDateFormat month = new SimpleDateFormat("MMM-yy");
                String strmonth = month.format(c.getTime());
                SimpleDateFormat year = new SimpleDateFormat("dd-MM-yy");
                Date vardate = null;
                databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                //to get the current version of software
                strSelectSQL_02 = null;
                rs1 = null;
                strSelectSQL_02 = "select file_name,version_flag from File_desc where version_flag=1";
                rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
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
                /////////////////
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

                while (rs.moveToNext()) {
                    int prvBlTyp=0;
                    prvBlTyp=rs.getInt(103);
                    int noofmonth = rs.getInt(99);
                    monthname = rs.getInt(54);
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


                        dDate = convertDateFormat(rs.getString(79), "DD-MM-YYYY");
                        dConno = rs.getString(110);
                        dname = rs.getString(2);
                        darrear = Double.valueOf(Math.round(rs.getDouble(50)));

                        //  btpObject.sendMessage_HW("123456789abcdefghijklmnopqrstuvwxyz", Normal);
                        btpObject.sendMessage_HW(CenterAppend1("TPCODL", 32), Normal);
                        btpObject.sendMessage_HW(CenterAppend1(" ELECTRICITY BILL", 32), Normal);
                        btpObject.sendMessage_HW("THIS BILL SHALL NOT BE A PROOF", Normal);
                        btpObject.sendMessage_HW("    OF LAWFUL OCCUPATION OF ", Normal);
                        btpObject.sendMessage_HW("   PREMISES IN CASE I-BOND IS ", Normal);
                        btpObject.sendMessage_HW("     MENTIONED HERE UNDER", Normal);
                        btpObject.sendMessage_HW("------------------------------", Normal);
                        btpObject.sendMessage_HW(leftAppend1("BILL MONTH:", strmonth.toUpperCase(), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("DN DT:", rs.getString(107), 32), Normal);
                        String revdt = "";

                        revdt = convertDateFormat(rs.getString(79), "DD-MM-YY");
                        btpObject.sendMessage_HW(leftAppend2("BL DT:", revdt, 6, "TIME:" + BlPrepTm, 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("BILL NO:", rs.getString(102), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1( "VER:" , version, 32), Normal);
                        String revStr = "";
                        revStr = rs.getString(99) + " MONTHS";

                        //btpObject.sendMessage_HW(leftAppend1("MOV IN DT:", rs.getString(21), 32), Normal);
                       // btpObject.sendMessage_HW(leftAppend1("PRV BL TP:", rs.getString(103), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("PRV BL REM:", rs.getString(100), 32), Normal);
                       // btpObject.sendMessage_HW(leftAppend1("FC SLAB:", rs.getString(105), 32), Normal);

                        btpObject.sendMessage_HW(leftAppend1("BILLED FOR:", revStr, 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("DIV:", rs.getString(9), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("SUBDIV:", rs.getString(10), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("SECTION:", rs.getString(11), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("MRU:", rs.getString(6), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("CON.AC NO:", rs.getString(110), 32), DoubleHght);//cons_acc double height
                        btpObject.sendMessage_HW(leftAppend1("CUST ID:", rs.getString(1), 32), Normal); // width off
                        btpObject.sendMessage_HW(leftAppend1("CA NO:", rs.getString(46), 32), Normal); // width off
                        btpObject.sendMessage_HW(leftAppend1("OA NO:", rs.getString(7), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("INST NO:", rs.getString(44), 32), Normal);
                       // btpObject.sendMessage_HW(leftAppend1("CONNECTION DT:", rs.getString(116), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("MTR SL NO:", rs.getString(12), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("MTR INST. DT:", rs.getString(116), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("MTR OWNER:", rs.getString(15), 32), Normal);//mtrowner
                        //btpObject.sendMessage_HW(leftAppend1("MAKE:", " ", 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("METER MF:", rs.getString(14), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("NAME:", rs.getString(2), 32), Normal);//name
                        StringBuilder strAddr = new StringBuilder(rs.getString(3) + "," + rs.getString(4));
                        Log.d("DemoApp", "add 1  " + strAddr.length());
                        btpObject.sendMessage_HW(leftAppend2("ADDRS:", strAddr.toString(), 4, "", 32), Normal);
                        String ibond = "";
                        try {
                            ibond = rs.getString(18);
                            if (rs.getString(18).equals("I")) {
                                ibond = "I-BOND";
                            }
                        } catch (Exception e) {
                        }
                        btpObject.sendMessage_HW(leftAppend1("USAGE:" + ibond, "Ph:" + Phase, 32), Normal);//usage
                        btpObject.sendMessage_HW(leftAppend1("CATEGORY:" + rs.getString(8), "CD:" + rs.getDouble(19) + "", 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("HIST. MD:", MdivalPrev, 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("MD RECORD:", MdivalCurr, 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("BILLED MD:", rs.getString(108), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("MR NOTE:", rs.getString(101), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("LST OK RD:", rs.getString(39), 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("LST OK RD DT:", rs.getString(109), 32), Normal);

                        btpObject.sendMessage_HW(leftAppend1("AVG. UNITS:", rs.getString(30), 32), Normal);
                        btpObject.sendMessage_HW("------------------------------", Normal);
                        btpObject.sendMessage("\n".getBytes());
                        btpObject.sendMessage("\n".getBytes());
                        // btpObject.sendMessage_HW(leftAppend1("AVG. UNITS:",rs.getString(30), 32) , Normal);
                        btpObject.sendMessage_HW("       RDG" + "        DATE" + "      STS", Normal);
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
                        btpObject.sendMessage_HW(leftAppend3("PRES:", pres, 9, revStr, 8, rs.getString(59), 32), Normal);
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

                        btpObject.sendMessage_HW(leftAppend3("PREV:", prvRead, 9, revStr, 8, rs.getString(23), 32), Normal);


                        btpObject.sendMessage_HW(leftAppend1("UNITS ADVANCED:", rs.getString(61), 32), Normal);

                        if (rs.getString(62).equals("N")) {
                            btpObject.sendMessage_HW(leftAppend1("BILL BASIS:", "ACTUAL", 32), Normal);
                        } else {
                            btpObject.sendMessage_HW(leftAppend1("BILL BASIS:", "AVERAGE", 32), Normal);
                        }

                        btpObject.sendMessage_HW("------------------------------", Normal);

                        //  Bill calulation start
                        String formattedData = "";
                        // formattedData = String.format("%.02f", rs.getDouble(64));
                        btpObject.sendMessage_HW(leftAppend1("MFC/CUST CHRG:", String.format("%.02f", rs.getDouble(64)) + "", 32), Normal);
                        blunts = rs.getInt(61);
                        //Bill slab Changed by Santi on 13.01.2016
                        if (rs.getDouble(89) > 0) {
                            btpObject.sendMessage_HW(leftAppend2("EC:", (rs.getString(87) + "X" + rs.getDouble(88) + "0="), 11, String.format("%.02f", rs.getDouble(89)) + "", 32), Normal);
                        } else {
                            //btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getDouble(92) > 0) {
                            btpObject.sendMessage_HW(leftAppend2("EC:", rs.getString(90) + "X" + rs.getDouble(91) + "0=", 3, String.format("%.02f", rs.getDouble(92)) + "", 32), Normal);
                        } else {
                            // btpObject.sendMessage_HW(" " , Normal);
                        }

                        if (rs.getDouble(95) > 0) {
                            btpObject.sendMessage_HW(leftAppend2("EC:", rs.getString(93) + "X" + rs.getDouble(94) + "0=", 3, String.format("%.02f", rs.getDouble(95)) + "", 32), Normal);
                        } else {
                            // btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getDouble(98) > 0) {
                            btpObject.sendMessage_HW(leftAppend2("EC:", rs.getString(96) + "X" + rs.getDouble(97) + "0=", 3, String.format("%.02f", rs.getDouble(98)) + "", 32), Normal);
                        } else {
                            //btpObject.sendMessage_HW(" " , Normal);
                        }
                        //end

                        btpObject.sendMessage_HW(leftAppend1("ED CHRG:", String.format("%.02f", rs.getDouble(66)) + "", 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("METER RENT:", String.format("%.02f", rs.getDouble(65)) + "", 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("DPS CHRG:", String.format("%.02f", rs.getDouble(114)) + "", 32), Normal);
                        btpObject.sendMessage_HW(leftAppend1("PRES.BL:", String.format("%.02f", rs.getDouble(67)) + "", 32), DoubleHght);//double height
                        btpObject.sendMessage_HW("------------------------------", Normal);
                        //btpObject.sendMessage_HW(leftAppend1("PREV FY ARREAR:", String.format("%.02f", rs.getDouble(49)) + "", 32), Normal);//previous yr arr
                        double dAmountArrear=rs.getDouble(50);
                        double dAdj_CR = rs.getDouble(111);
                        double dAdj_DR = rs.getDouble(112);
                        double dpslvd=rs.getDouble(113);
                        dAmountArrear=dAmountArrear+dAdj_CR-dAdj_DR-dpslvd;

                        btpObject.sendMessage_HW(leftAppend1("ARREAR:", String.format("%.02f", dAmountArrear) + "", 32), Normal);


                        if (rs.getInt(27) > 0) { //adjustments
                            btpObject.sendMessage_HW(leftAppend2("ADJUSTMENTS:", "(+)", 14, String.format("%.02f", rs.getDouble(27)) + "", 32), Normal);
                        }
                        if (rs.getInt(26) > 0) {
                            btpObject.sendMessage_HW(leftAppend2("ADJUSTMENTS:", "(-)", 14, String.format("%.02f", rs.getDouble(26)) + "", 32), Normal);
                        } else {
                            btpObject.sendMessage_HW(leftAppend1("ADJUSTMENTS:", "", 32), Normal);
                        }
                        double povamt=0;
                        povamt= rs.getDouble(104)+ rs.getDouble(106);
                        ////if (rs.getInt(103)==1000 || rs.getInt(103)==2000){
                            if (rs.getString(115).equals("Y")) {
                                btpObject.sendMessage_HW(leftAppend1("P.ADJ.AMT EC:", String.format("%.02f", rs.getDouble(104)), 32), Normal);
                                btpObject.sendMessage_HW(leftAppend1("P.ADJ.AMT ED:", String.format("%.02f", rs.getDouble(106)), 32), Normal);
                        //    }
                        } else {
                            // btpObject.sendMessage_HW(" " , Normal);
                        }
                        if (rs.getInt(25) > 0) {
                            btpObject.sendMessage_HW(leftAppend1("MISC CHARG:", String.format("%.02f", rs.getDouble(25)) + "", 32), Normal);
                        } else {
                            btpObject.sendMessage_HW("MISC CHARG:" + " ", Normal);
                        }

                        if (rs.getInt(41) > 0) {
                            btpObject.sendMessage_HW(leftAppend1("SD AVAIL:", String.format("%.02f", rs.getDouble(41)) + "", 32), Normal);
                        } else {
                            btpObject.sendMessage_HW(" ", Normal);
                        }

                        if (rs.getInt(42) > 0) {
                            btpObject.sendMessage_HW(leftAppend1("ASD:", String.format("%.02f", rs.getDouble(42)) + "", 32), Normal);
                        } else {
                            btpObject.sendMessage_HW(" ", Normal);
                        }

                        if (rs.getInt(43) > 0) {
                            btpObject.sendMessage_HW(leftAppend1("ASD ARR:", String.format("%.02f", rs.getDouble(43)) + "", 32), Normal);
                        } else {
                            btpObject.sendMessage_HW(" ", Normal);
                        }

                        // btpObject.sendMessage_HW(" ", Normal); //Penalty amount showing 30.06.2017
                        btpObject.sendMessage_HW("------------------------------", Normal);
                        btpObject.sendMessage_HW(leftAppend1("TOTAL AMOUNT:", String.format("%.02f", rs.getDouble(69)) + "", 32), Normal);//total amount
                        btpObject.sendMessage_HW(leftAppend1("REBATE:", String.format("%.02f", rs.getDouble(68)) + "", 32), Normal);//rebate amount
                        double revamt = 0;
                        revamt = (double) Math.round(rs.getDouble(69) - rs.getDouble(68));
                        btpObject.sendMessage_HW(leftAppend1("BL BY DUE DT:", String.format("%.02f", revamt) + "", 32), DoubleHght);//total bill by due date double height
                        roundupto = Math.round(rs.getDouble(69) - rs.getDouble(68)) - (rs.getDouble(69) - rs.getDouble(68));
                        revamt = 0;
                        if (roundupto > 0) {
                            btpObject.sendMessage_HW(leftAppend2("ROUNDED UPTO:", "(+)", 14, String.format("%.02f", roundupto) + "", 32), Normal);
                        } else {
                            revamt = Math.abs(roundupto);
                            btpObject.sendMessage_HW(leftAppend1("ROUNDED UPTO:", "(-)" + String.format("%.02f", Math.abs(roundupto)), 32), Normal);
                        }
                        btpObject.sendMessage_HW(" ", Normal);
                        btpObject.sendMessage_HW(leftAppend1("REBATE DATE", rs.getString(76), 32), DoubleHght);//rebate date

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
                        btpObject.sendMessage_HW(leftAppend1("PAY AFT DUE DT:", String.format("%.02f", payaftdt) + "", 32), DoubleHght);//pay after due date enable on 31.01.17

                        if (roundupto > 0) {
                            btpObject.sendMessage_HW(leftAppend1("ROUNDED UPTO:", "(+)" + String.format("%.02f", roundupto), 32), Normal);
                        } else {
                            btpObject.sendMessage_HW(leftAppend1("ROUNDED UPTO:", "(-)" + String.format("%.02f", Math.abs(roundupto)), 32), Normal);
                        }
                        btpObject.sendMessage_HW("------------------------------", Normal);
                        btpObject.sendMessage_HW(CenterAppend1("LAST PAYMENT DETAILS", 32), Normal); //dissable on 03.04.2017
                        btpObject.sendMessage_HW("------------------------------", Normal);
                        // btpObject.sendMessage_HW(" " , Normal);//dissable on 03.04.2017
                        btpObject.sendMessage_HW(leftAppend1("BNO-RNO:", rs.getString(32) , 32), Normal);
                        btpObject.sendMessage_HW(leftAppend2("AMT:", rs.getDouble(34) + "", 10, "DT:" + rs.getString(31), 32), Normal);
                        btpObject.sendMessage_HW("------------------------------", Normal);
                        btpObject.sendMessage_HW(CenterAppend1("ECS MESSAGE", 32), Normal);
                        btpObject.sendMessage_HW("------------------------------", Normal);
                        //Ecs Message printing///////

                        if (rs.getDouble(69) < rs.getDouble(55) && rs.getInt(85) == 1) { //here rs(85) is validation date check query CASE WHEN CASE WHEN strftime('%d-%m-%Y',BILL_DATE)<strftime('%d-%m-%Y',ECS_VALID) THEN 1  ELSE 0 END AS ECSMSG
                            btpObject.sendMessage_HW(CenterAppend1("Bg dbtd to Bank thru ECS", 32), Normal);//ECS Message
                        } else if (rs.getDouble(69) > rs.getDouble(55) && rs.getInt(85) == 1) {
                            btpObject.sendMessage_HW(CenterAppend1("Val excd-pay cash/chq", 32), Normal);//ECS Message
                        } else if (rs.getInt(85) == 0 && rs.getString(56) != null) {
                            btpObject.sendMessage_HW(CenterAppend1("Dt lpsd-pay cash/chq", 32), Normal);//ECS Message
                        } else {
                            btpObject.sendMessage_HW(" ", Normal);
                        }

                        btpObject.sendMessage_HW("------------------------------", Normal);
                        btpObject.sendMessage_HW(leftAppend1("MR NAME:",MrName,32), Normal);
                        //added on 03.04.2017 to show additional rebate message
                        double cashlesrbt = 0;
                        cashlesrbt = rs.getDouble(63) * 0.01;//1% cashless rebate of EC
                        if (rs.getString(8).equals("DOM") || (rs.getString(8).equals("GPS") && Phase.equals("01"))) {//added on 27.03.2018
                            btpObject.sendMessage_HW(CenterAppend1("PAY CASHLESS AND AVAIL", 32), Normal);
                            btpObject.sendMessage_HW(CenterAppend1("ADDITIONAL REBATE OF 2%", 32), Normal);
                            /////End 03.04.2017
                        } else {//added on 27.03.2018
                            //  btpObject.sendMessage_HW(" " , Normal);
                            // btpObject.sendMessage_HW(" " , Normal);
                        }
                        //added on 24.07.2020
                        btpObject.sendMessage_HW(CenterAppend1("YOU CAN PAY WITHIN 7", 32), Normal);
                        btpObject.sendMessage_HW(CenterAppend1("DAYS FROM BILL DATE", 32), Normal);

                        if (Math.round(blunts / rs.getDouble(99)) > 30 && (rs.getString(8).equals("DKJ") )) {
                            btpObject.sendMessage_HW(leftAppend1("", "Ur Tariff Change to Domestic", 32), Normal);
                        }

                        // btpObject.sendMessage_HW(leftAppend1("","Win Prize 10000 - Paying Bill Online (TNC apply)", 48) , Normal);
                        try {
                            if (rs.getString(48).length() > 5) {
                                btpObject.sendMessage_HW("------------------------------", Normal);
                                btpObject.sendMessage_HW(leftAppend1("", rs.getString(48), 48), Normal);
                            }
                        } catch (Exception e) {
                        }

                        btpObject.sendMessage_HW(CenterAppend1("Please Stay Away from ", 32), Normal);
                        btpObject.sendMessage_HW(CenterAppend1("Electric line/Sub-Stations", 32), Normal);
                        Log.d("dempapp", "Dcprnt" + Dcprnt);
                        int edexptflg = rs.getInt(36);
                        try {
                            if (edexptflg==1) {//ED_EXEMPT no ed 36////////////////////////////////////////////////
                                Dcprnt = 0;//Ed
                            }
                        } catch (Exception e) {
                            Dcprnt = 0;
                        }
                        disupflg = 0;
                        if (Dcprnt == 1 && Math.round(darrear) > 10000) {
                            Log.d("dempapp", "got");
                            disupflg = 1;
                            btpObject.sendMessage_HW("========================", DoubleHght);
                            btpObject.sendMessage_HW("DISCONNECTION NOTICE", DoubleHght);
                            btpObject.sendMessage_HW("========================", DoubleHght);
                            String NoticeDt = dDate.replace("-", "");
                            btpObject.sendMessage_HW("Notice No:" + dConno + NoticeDt, Normal);
                            btpObject.sendMessage_HW("Date:" + dDate, Normal);
                            btpObject.sendMessage_HW("Consumer No:" + dConno, Normal);
                            btpObject.sendMessage_HW("Name:" + dname, Normal);
                            dcCont = fetchDCContent(revdt);
                            btpObject.sendMessage_HW("You have outstanding amount of", Normal);
                            btpObject.sendMessage_HW("Rs:" + Math.round(darrear) + ".00", DoubleHght);
                            btpObject.sendMessage_HW(dcCont, Normal);

                        }
                        btpObject.sendMessage_HW("------------------------------", Normal);
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
                if (disupflg == 1) {
                    updateDisconflg();
                }
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
                if (devicename.equals("nodevice")) {
                    try {
                        devicename = findBT();
                    } catch (Exception ex) {
                        //  Toast.makeText(BillPrintActivity.this, "message12", Toast.LENGTH_LONG).show();
                    }
                }
                btpObject.createClient(address);


                try {
                    sendData();
                } catch (Exception ex) {//Toast.makeText(BillPrintActivity.this, "message13", Toast.LENGTH_LONG).show();
                }
                try {
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

    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            //  stopWorker = true;

            if (mmOutputStream != null) {
                mmOutputStream.flush();
                mmOutputStream.close();
            }
            if (mmInputStream != null)
                mmInputStream.close();

            try {
                if (btpObject.isConnected() == true) {
                    closeprinter = true;
                    btpObject.Batterystatus();
                    long curntime = System.currentTimeMillis();
                    while (curntime + 10000 < System.currentTimeMillis()) {
                        if (closeprinter == false) {
                            break;
                        }
                    }
                    if (closeprinter == true) {
                        btpObject.closeConnection();
                        closeprinter = false;
                    }
                }
                if (mmSocket != null) {
                    Log.d("DemoApp", "on 10 ");
                    try {
                        Log.d("DemoApp", "on 11 ");
                        mmSocket.close();
                        Log.d("DemoApp", "on 12 ");
                        mmSocket = null;
                    } catch (Exception e) {
                        mmSocket = null;
                        Log.d("DemoApp", "on 8 " + e);
                    }

                    Log.d("DemoApp", "on 13 ");
                }

            } catch (Exception e) {
                Log.d("DemoApp", "on 9 " + e);
            }
            strPrntMsg.setText("Bluetooth Closed");
            ;
        } catch (NullPointerException e) {
            //  Toast.makeText(BillPrintActivity.this, "message10"+e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e) {
            //   Toast.makeText(BillPrintActivity.this, "message11" + e, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public String font_Double_Height_Width_On() {
        byte rf1[] = new byte[3];
        rf1[0] = 28;
        rf1[1] = 33;
        rf1[2] = 8;
        String s = new String(rf1);
        return s;
    }

    public String font_Double_Height_Width_Off() {
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

    public static String CenterAppend1(String str1, int maxlen) {
        String retStr = "";
        String str = "";
        int strlen = 0;
        int lendiff = 0;
        try {
            lendiff = maxlen - str1.length();
            strlen = lendiff / 2;
            Log.d("DemoApp", "strlen " + strlen);
            for (int i = 0; i < strlen; i++) {
                retStr += " ";
            }
            str = str + retStr + str1;
        } catch (Exception e) {
        }
        return str;

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
    }
}

