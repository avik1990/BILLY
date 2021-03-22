/*
package com.tpcodl.billingreading.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tpcodl.billingreading.models.MeterOkNonSbmReadingModel;
import com.tpcodl.billingreading.models.NSBMData;
import com.tpcodl.billingreading.models.PaperModal;
import com.tpcodl.billingreading.models.PrinterModal;
import com.tpcodl.billingreading.reponseModel.bollingModel.BillingResponseModel;
import com.tpcodl.billingreading.reponseModel.bollingModel.ChildDetail;
import com.tpcodl.billingreading.uploadingrequestModel.HeaderresDetails;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper_bck extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static String DB_NAME = "SMRD.db";
    private static String DB_PATH = "";
    private static String DB_PATH1 = "";
    public static String strInputFilePath = Environment.getExternalStorageDirectory() + "/cesuapp/input/";
    private static final int DB_VERSION = 1;
    private static SQLiteDatabase db1;
    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private static Context mContext1 = null;
    private boolean mNeedUpdate = false;

    public DatabaseHelper_bck(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17) {

            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

        } else {

            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";

        }

        this.mContext = context;
    }


    public void createDataBase() throws IOException {
        //If the database does not exist, copy it from the assets.
        boolean mDataBaseExist = checkDataBase();
        if (!mDataBaseExist) {
            this.getReadableDatabase();
            this.close();
            try {
                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    public void open() {
        this.mDataBase = this.getWritableDatabase();
    }

    */
/**
     * Close the database connection.
     *//*

    public boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        Log.e("dbFile", dbFile + "   " + dbFile.exists());
        return dbFile.exists();
    }

    //Copy the database from assets
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();


    }

    //Copy the database from assets
    public static void copyDataBase1() throws IOException {

        InputStream mInput = mContext1.getAssets().open(DB_NAME);
        String outFileName = strInputFilePath + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();

    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }

    public NSBMData getNBSMData(String param, int offset) {
        NSBMData nSBMDataDatamodel = null;
        String installation = "";
        installation = getInstallatioationNo(param, offset);

        String selectQuery = "select * from TBL_SPOTBILL_HEADER_DETAILS a ,TBL_SPOTBILL_CHILD_DETAILS b " +
                "where a.INSTALLATION=b.INSTALLATION  " + "and a.INSTALLATION='" + installation + "'";
        */
/*String selectQuery = "select LEGACY_ACCOUNT_NO,NAME,ADDRESS1||ADDRESS2 ADDRESS1,SEQ,SUB_SEQ,LEGACY_ACCOUNT_NO2,RATE_CATEGORY,DIV,SUB_DIV,SECTION,CONSUMER_OWNED,METER_MAKE,USAGE,SAN_LOAD,MOVE_IN_DATE,DPS,MISC_CHARGES,CR_ADJ,DB_ADJ,PRV_BILLED_AMT,PREVIOUS_BILLED_PROV_UNIT,LAST_PAID_DATE,LAST_PYMT_RCPT,LAST_PAID_AMT,ED_EXEMPT,AIFI,NEW_METER_NO,SDI,ASD,ASDAA,INSTALLATION,SBM_BILL_NO,CA,PRV_ARR,ARREARS,ULF,PREV_BILL_UNITS,BILL_MONTH,ECS_LIMT,ECS_VALIDITY_PERIOD,PRESENT_READING_REMARK,PRESENT_METER_STATUS,PRESENT_BILL_UNITS,PRESENT_BILL_TYPE,EC,MMFC,MRENT_CHARGED,ED,CURRENT_BILL_TOTAL,REBATE,AMOUNT_PAYABLE,AVG_UNIT_BILLED,RCPTNO,CHQNO,CHQDT,BANK,RCPTAMT,DUE_DATE,DO_EXPIRY,PRESENT_READING_TIME,OSBILL_DATE,CAPTURED_MOBILE,SCHEDULE_METER_READ_DATE,METER_RENT,PORTION,MRU,NO_OF_REG,SAN_LOAD_UNITS,SAN_LOAD_EFFECTIVE_DATE,SUPPLY_TYP_FLG,NOTINUSE_FLG_ENDDATE,OTHER_FLGS,ED_RBT,HOSTEL_RBT,PREV_BILL_TYPE,PREV_BILL_REMARK,PREV_BILL_END_DATE,LAST_NORMAL_BILL_DATE,PRESENT_READING_DATE,AVERAGE_KWH,BILL_BASIS,BILL_NO,INVOICE_NO,READ_ONLY,BP_START_DATE,BP_END_DATE,PPAC,ROUND_OFF,SEC_DEPOSIT_AMT,DO_GENERATED,NOT_TO_BILL_AFTER,INSERT_DATE,INSERT_TIME,UPDATE_DATE,UPDATE_TIME,PROGRESSION_STATE,PHONE_1,PHONE_2,BILL_YEAR,TRANS_TYPE,TD_FLAG,TD_DATE,PPI,PREV_PROV_AMT,GST_RELEVANT1,GST_RELEVANT2,GST_RELEVANT3,GST_RELEVANT4,ELT_STTS,SEAL_STTS,CBLASS,HL_MONTHS,MRREASON,USAGE_ID,METER_TYPE_ID,SUPPLY_STATUS_ID,REASON_DC_ID,SUPPLY_SOURCE_ID,REASON_NV_ID,REASON_CD_ID,REASON_EN_ID,REASON_MTR_STUCK_ID,PAPER_PASTE_BY_ID,METER_HEIGHT_ID,TYPES_OBSTACLE_ID,SEAL_STS_ID,CONTACT_REASON_ID,REASON_PL_ID,BUILDING_DESC,BUILDING_CODE,POLE_NO,FLAG,SPECIAL_REM,GPS_LONGITUDE,GPS_LATITUDE,SENDER_MOBILE,CONSUMER_TYPE,WALKING_SEQ_CHK,METER_LOCA,MR_REMARK_DET,STOP_PAPR_BL,NEW_MTR_FLG,OLD_MTR_COR_FLG,UNSAFE_COND,READ_FLAG,SENT_FLAG,MTR_IMAGE,UPD_FLAG1,UPD_FLAG2,REVISIT_FLAG,UNIT_SLAB1,RATE_SLAB1,EC_SLAB1,UNIT_SLAB2,RATE_SLAB2,EC_SLAB2,UNIT_SLAB3,RATE_SLAB3,EC_SLAB3,UNIT_SLAB4,RATE_SLAB4,EC_SLAB4,BILL_PRN_FOOTER,BILL_PRN_HEADER,HL_UNIT,REFERENCE_DATE from TBL_SPOTBILL_HEADER_DETAILS a ,TBL_SPOTBILL_CHILD_DETAILS b " +
                " where a.INSTALLATION=b.INSTALLATION  " + "and a.INSTALLATION='" + installation + "'";*//*

        Log.e("selectQuery", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<String> list = new ArrayList<>();
        list.clear();
        if (cursor.moveToFirst()) {
            do {
                nSBMDataDatamodel = new NSBMData();
                nSBMDataDatamodel.setLEGACY_ACCOUNT_NO(cursor.getString(cursor.getColumnIndex("LEGACY_ACCOUNT_NO")));
                nSBMDataDatamodel.setNAME(cursor.getString(cursor.getColumnIndex("NAME")));
                nSBMDataDatamodel.setADDRESS1(cursor.getString(cursor.getColumnIndex("ADDRESS1")) + " " + cursor.getString(cursor.getColumnIndex("ADDRESS2")));
                //nSBMDataDatamodel.setADDRESS2(cursor.getString(cursor.getColumnIndex("ADDRESS2")));
                nSBMDataDatamodel.setSEQ(cursor.getString(cursor.getColumnIndex("SEQ")));
                nSBMDataDatamodel.setSUB_SEQ(cursor.getString(cursor.getColumnIndex("SUB_SEQ")));
                nSBMDataDatamodel.setLEGACY_ACCOUNT_NO2(cursor.getString(cursor.getColumnIndex("LEGACY_ACCOUNT_NO2")));
                nSBMDataDatamodel.setRATE_CATEGORY(cursor.getString(cursor.getColumnIndex("RATE_CATEGORY")));
                nSBMDataDatamodel.setDIV(cursor.getString(cursor.getColumnIndex("DIV")));
                nSBMDataDatamodel.setSUB_DIV(cursor.getString(cursor.getColumnIndex("SUB_DIV")));
                nSBMDataDatamodel.setSECTION(cursor.getString(cursor.getColumnIndex("SECTION")));
                nSBMDataDatamodel.setCONSUMER_OWNED(cursor.getString(cursor.getColumnIndex("CONSUMER_OWNED")));
                nSBMDataDatamodel.setMETER_MAKE(cursor.getString(cursor.getColumnIndex("METER_MAKE")));
                nSBMDataDatamodel.setUSAGE(cursor.getString(cursor.getColumnIndex("USAGE")));
                nSBMDataDatamodel.setSAN_LOAD(cursor.getString(cursor.getColumnIndex("SAN_LOAD")));
                nSBMDataDatamodel.setMOVE_IN_DATE(cursor.getString(cursor.getColumnIndex("MOVE_IN_DATE")));
                nSBMDataDatamodel.setDPS(cursor.getString(cursor.getColumnIndex("DPS")));
                nSBMDataDatamodel.setMISC_CHARGES(cursor.getString(cursor.getColumnIndex("MISC_CHARGES")));
                nSBMDataDatamodel.setBILL_MONTH(cursor.getString(cursor.getColumnIndex("BILL_MONTH")));

                nSBMDataDatamodel.setCR_ADJ(cursor.getString(cursor.getColumnIndex("CR_ADJ")));
                nSBMDataDatamodel.setDB_ADJ(cursor.getString(cursor.getColumnIndex("DB_ADJ")));
                nSBMDataDatamodel.setPRV_BILLED_AMT(cursor.getString(cursor.getColumnIndex("PRV_BILLED_AMT")));
                nSBMDataDatamodel.setPREVIOUS_BILLED_PROV_UNIT(cursor.getString(cursor.getColumnIndex("PREVIOUS_BILLED_PROV_UNIT")));
                nSBMDataDatamodel.setLAST_PAID_DATE(cursor.getString(cursor.getColumnIndex("LAST_PAID_DATE")));
                nSBMDataDatamodel.setLAST_PYMT_RCPT(cursor.getString(cursor.getColumnIndex("LAST_PYMT_RCPT")));
                nSBMDataDatamodel.setLAST_PAID_AMT(cursor.getString(cursor.getColumnIndex("LAST_PAID_AMT")));
                nSBMDataDatamodel.setED_EXEMPT(cursor.getString(cursor.getColumnIndex("ED_EXEMPT")));
                nSBMDataDatamodel.setAIFI(cursor.getString(cursor.getColumnIndex("AIFI")));
                nSBMDataDatamodel.setNEW_METER_NO(cursor.getString(cursor.getColumnIndex("NEW_METER_NO")));
                nSBMDataDatamodel.setSDI(cursor.getString(cursor.getColumnIndex("SDI")));
                nSBMDataDatamodel.setASD(cursor.getString(cursor.getColumnIndex("ASD")));
                nSBMDataDatamodel.setASDAA(cursor.getString(cursor.getColumnIndex("ASDAA")));
                nSBMDataDatamodel.setINSTALLATION(cursor.getString(cursor.getColumnIndex("INSTALLATION")));
                nSBMDataDatamodel.setSBM_BILL_NO(cursor.getString(cursor.getColumnIndex("SBM_BILL_NO")));

                nSBMDataDatamodel.setCA(cursor.getString(cursor.getColumnIndex("CA")));
                nSBMDataDatamodel.setPRV_ARR(cursor.getString(cursor.getColumnIndex("PRV_ARR")));
                nSBMDataDatamodel.setARREARS(cursor.getString(cursor.getColumnIndex("ARREARS")));
                nSBMDataDatamodel.setULF(cursor.getString(cursor.getColumnIndex("ULF")));
                nSBMDataDatamodel.setPREV_BILL_UNITS(cursor.getString(cursor.getColumnIndex("PREV_BILL_UNITS")));

                nSBMDataDatamodel.setBUILDING_DESC(cursor.getString(cursor.getColumnIndex("BUILDING_DESC")));
                nSBMDataDatamodel.setBUILDING_CODE(cursor.getString(cursor.getColumnIndex("BUILDING_CODE")));
                nSBMDataDatamodel.setPOLE_NO(cursor.getString(cursor.getColumnIndex("POLE_NO")));
                nSBMDataDatamodel.setFLAG(cursor.getString(cursor.getColumnIndex("FLAG")));
                nSBMDataDatamodel.setMETER_NO(cursor.getString(cursor.getColumnIndex("METER_NO")));
                nSBMDataDatamodel.setMRU(cursor.getString(cursor.getColumnIndex("MRU")));
                nSBMDataDatamodel.setSPECIAL_REM(cursor.getString(cursor.getColumnIndex("SPECIAL_REM")));

                nSBMDataDatamodel.setPHONE_1(cursor.getString(cursor.getColumnIndex("PHONE_1")));
                nSBMDataDatamodel.setREVISIT_FLAG(cursor.getString(cursor.getColumnIndex("REVISIT_FLAG")));

                nSBMDataDatamodel.setREAD_FLAG(cursor.getString(cursor.getColumnIndex("READ_FLAG")));
                list.add(cursor.getString(cursor.getColumnIndex("REGISTER_CODE")));
                nSBMDataDatamodel.setListRegisterCoe(list);

            } while (cursor.moveToNext());
        }
        db.close();
        return nSBMDataDatamodel;
    }

    public NSBMData getNBSMReadData(String param) {
        NSBMData nSBMDataDatamodel = null;
        String installation = "";
        installation = getInstallatioationNo(param, -1);
        */
/*String selectQuery1 = "select INSTALLATION from TBL_SPOTBILL_HEADER_DETAILS";
        selectQuery1 = selectQuery1 + param;*//*

        Map<String, String> LinkedHashMapValues = new LinkedHashMap<>();
        LinkedHashMapValues.clear();
       */
/* String selectQuery = "select * from TBL_SPOTBILL_HEADER_DETAILS h left join TBL_SPOTBILL_CHILD_DETAILS c " +
                "where h.INSTALLATION=c.INSTALLATION and c.INSTALLATION='" + installation + "'";*//*

        String selectQuery = "select * from TBL_SPOTBILL_HEADER_DETAILS a ,TBL_SPOTBILL_CHILD_DETAILS b where a.INSTALLATION=b.INSTALLATION  " +
                "and a.INSTALLATION='" + installation + "'";

        //selectQuery = selectQuery + param;
        Log.e("selectQuery", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<String> list = new ArrayList<>();
        list.clear();
        if (cursor.moveToFirst()) {
            do {
                nSBMDataDatamodel = new NSBMData();
                nSBMDataDatamodel.setLEGACY_ACCOUNT_NO(cursor.getString(cursor.getColumnIndex("LEGACY_ACCOUNT_NO")));
                nSBMDataDatamodel.setNAME(cursor.getString(cursor.getColumnIndex("NAME")));
                nSBMDataDatamodel.setADDRESS1(cursor.getString(cursor.getColumnIndex("ADDRESS1")));
                nSBMDataDatamodel.setADDRESS2(cursor.getString(cursor.getColumnIndex("ADDRESS2")));
                nSBMDataDatamodel.setSEQ(cursor.getString(cursor.getColumnIndex("SEQ")));
                nSBMDataDatamodel.setSUB_SEQ(cursor.getString(cursor.getColumnIndex("SUB_SEQ")));
                nSBMDataDatamodel.setLEGACY_ACCOUNT_NO2(cursor.getString(cursor.getColumnIndex("LEGACY_ACCOUNT_NO2")));
                nSBMDataDatamodel.setRATE_CATEGORY(cursor.getString(cursor.getColumnIndex("RATE_CATEGORY")));
                nSBMDataDatamodel.setDIV(cursor.getString(cursor.getColumnIndex("DIV")));
                nSBMDataDatamodel.setSUB_DIV(cursor.getString(cursor.getColumnIndex("SUB_DIV")));
                nSBMDataDatamodel.setSECTION(cursor.getString(cursor.getColumnIndex("SECTION")));
                nSBMDataDatamodel.setCONSUMER_OWNED(cursor.getString(cursor.getColumnIndex("CONSUMER_OWNED")));
                nSBMDataDatamodel.setMETER_MAKE(cursor.getString(cursor.getColumnIndex("METER_MAKE")));
                nSBMDataDatamodel.setUSAGE(cursor.getString(cursor.getColumnIndex("USAGE")));
                nSBMDataDatamodel.setSAN_LOAD(cursor.getString(cursor.getColumnIndex("SAN_LOAD")));
                nSBMDataDatamodel.setMOVE_IN_DATE(cursor.getString(cursor.getColumnIndex("MOVE_IN_DATE")));
                nSBMDataDatamodel.setDPS(cursor.getString(cursor.getColumnIndex("DPS")));
                nSBMDataDatamodel.setMISC_CHARGES(cursor.getString(cursor.getColumnIndex("MISC_CHARGES")));
                nSBMDataDatamodel.setBILL_MONTH(cursor.getString(cursor.getColumnIndex("BILL_MONTH")));

                nSBMDataDatamodel.setCR_ADJ(cursor.getString(cursor.getColumnIndex("CR_ADJ")));
                nSBMDataDatamodel.setDB_ADJ(cursor.getString(cursor.getColumnIndex("DB_ADJ")));
                nSBMDataDatamodel.setPRV_BILLED_AMT(cursor.getString(cursor.getColumnIndex("PRV_BILLED_AMT")));
                nSBMDataDatamodel.setPREVIOUS_BILLED_PROV_UNIT(cursor.getString(cursor.getColumnIndex("PREVIOUS_BILLED_PROV_UNIT")));
                nSBMDataDatamodel.setLAST_PAID_DATE(cursor.getString(cursor.getColumnIndex("LAST_PAID_DATE")));
                nSBMDataDatamodel.setLAST_PYMT_RCPT(cursor.getString(cursor.getColumnIndex("LAST_PYMT_RCPT")));
                nSBMDataDatamodel.setLAST_PAID_AMT(cursor.getString(cursor.getColumnIndex("LAST_PAID_AMT")));
                nSBMDataDatamodel.setED_EXEMPT(cursor.getString(cursor.getColumnIndex("ED_EXEMPT")));
                nSBMDataDatamodel.setAIFI(cursor.getString(cursor.getColumnIndex("AIFI")));
                nSBMDataDatamodel.setNEW_METER_NO(cursor.getString(cursor.getColumnIndex("NEW_METER_NO")));
                nSBMDataDatamodel.setSDI(cursor.getString(cursor.getColumnIndex("SDI")));
                nSBMDataDatamodel.setASD(cursor.getString(cursor.getColumnIndex("ASD")));
                nSBMDataDatamodel.setASDAA(cursor.getString(cursor.getColumnIndex("ASDAA")));
                nSBMDataDatamodel.setINSTALLATION(cursor.getString(cursor.getColumnIndex("INSTALLATION")));
                nSBMDataDatamodel.setSBM_BILL_NO(cursor.getString(cursor.getColumnIndex("SBM_BILL_NO")));

                nSBMDataDatamodel.setCA(cursor.getString(cursor.getColumnIndex("CA")));
                nSBMDataDatamodel.setPRV_ARR(cursor.getString(cursor.getColumnIndex("PRV_ARR")));
                nSBMDataDatamodel.setARREARS(cursor.getString(cursor.getColumnIndex("ARREARS")));
                nSBMDataDatamodel.setULF(cursor.getString(cursor.getColumnIndex("ULF")));
                nSBMDataDatamodel.setPREV_BILL_UNITS(cursor.getString(cursor.getColumnIndex("PREV_BILL_UNITS")));

                nSBMDataDatamodel.setBUILDING_DESC(cursor.getString(cursor.getColumnIndex("BUILDING_DESC")));
                nSBMDataDatamodel.setBUILDING_CODE(cursor.getString(cursor.getColumnIndex("BUILDING_CODE")));
                nSBMDataDatamodel.setPOLE_NO(cursor.getString(cursor.getColumnIndex("POLE_NO")));
                nSBMDataDatamodel.setFLAG(cursor.getString(cursor.getColumnIndex("FLAG")));
                nSBMDataDatamodel.setMETER_NO(cursor.getString(cursor.getColumnIndex("METER_NO")));
                nSBMDataDatamodel.setMRU(cursor.getString(cursor.getColumnIndex("MRU")));
                nSBMDataDatamodel.setSPECIAL_REM(cursor.getString(cursor.getColumnIndex("SPECIAL_REM")));

                nSBMDataDatamodel.setPHONE_1(cursor.getString(cursor.getColumnIndex("PHONE_1")));
                nSBMDataDatamodel.setREVISIT_FLAG(cursor.getString(cursor.getColumnIndex("REVISIT_FLAG")));
                nSBMDataDatamodel.setREAD_FLAG(cursor.getString(cursor.getColumnIndex("READ_FLAG")));

                ///fetching data into map for register code and present meter value
                LinkedHashMapValues.put(cursor.getString(cursor.getColumnIndex("REGISTER_CODE")), cursor.getString(cursor.getColumnIndex("PRESENT_METER_READING")));
                nSBMDataDatamodel.setLinkedHashMapValues(LinkedHashMapValues);

                list.add(cursor.getString(cursor.getColumnIndex("REGISTER_CODE")));
                nSBMDataDatamodel.setListRegisterCoe(list);

            } while (cursor.moveToNext());
        }
        db.close();
        return nSBMDataDatamodel;
    }

    public ArrayList<PrinterModal> getPrinterType() {

        ArrayList<PrinterModal> printerModals = new ArrayList<>();

        PrinterModal printerModal = null;

        String strSelectSQL_02 = "SELECT PRINTER_NAME,PRINTER_VAL,PRINTERSET_FLG " +
                "FROM MST_PRINTERTYPE";

        //selectQuery = selectQuery + param;
        Log.e("selectQuery", strSelectSQL_02);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = db.rawQuery(strSelectSQL_02, null);

        if (cursor1.moveToFirst()) {
            do {
                String printerid = cursor1.getString(1);
                String printername = cursor1.getString(0);

                printerModal = new PrinterModal();

                printerModal.setPrinterId(printerid);
                printerModal.setPrinterName(printername);
                printerModals.add(printerModal);

            }
            while (cursor1.moveToNext());
        }
        db.close();
        return printerModals;
    }

    public String getPreviousPrinterType() {
        String printerName = "";
        String strSelectSQL_01 = "SELECT PRINTER_NAME,PRINTER_VAL,PRINTERSET_FLG " +
                "FROM MST_PRINTERTYPE WHERE PRINTER_STS=1 AND PRINTERSET_FLG=1";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSelectSQL_01, null);
        String printtype = "";
        while (cursor.moveToNext()) {
            printerName = cursor.getString(0);
        }
        db.close();
        return printerName;
    }

    public int getSMPVR(String username) {
        int printerName = -1;
        String strUpdateSQL_01 = "SELECT SBMPRV FROM _SA_User_old_20200911 WHERE userid = '" + username + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(strUpdateSQL_01, null);
        String printtype = "";
        while (cursor.moveToNext()) {
            printerName = cursor.getInt(0);
        }
        db.close();
        return printerName;
    }

    public String getPreviousPaperType() {
        String paperName = "";
        String strSelectSQL_01 = "SELECT PAPER_NAME,PAPER_VAL,PAPERSET_FLG " +
                "FROM MST_PAPERTYPE WHERE PAPER_STS=1 AND PAPERSET_FLG=1";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSelectSQL_01, null);
        String printtype = "";
        while (cursor.moveToNext()) {
            paperName = cursor.getString(0);
        }
        db.close();
        return paperName;
    }

    public ArrayList<PaperModal> getPaperType() {
        ArrayList<PaperModal> paperModals = new ArrayList<>();
        PaperModal paperModal = null;
        String strSelectSQL_02 = "SELECT PAPER_NAME,PAPER_VAL,PAPERSET_FLG " +
                "FROM MST_PAPERTYPE";
        //selectQuery = selectQuery + param;
        Log.e("selectQuery", strSelectSQL_02);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = db.rawQuery(strSelectSQL_02, null);
        if (cursor1.moveToFirst()) {
            do {
                String paperId = cursor1.getString(1);
                String papername = cursor1.getString(0);

                paperModal = new PaperModal();

                paperModal.setPaperId(paperId);
                paperModal.setPaperName(papername);
                paperModals.add(paperModal);

            }
            while (cursor1.moveToNext());
        }
        db.close();
        return paperModals;
    }


    public void resetPreviousPrinter() {
        String printerName = "";
        String strSelectSQL_01 = "UPDATE MST_PRINTERTYPE SET PRINTERSET_FLG=0 ";
        Log.d("DemoApp", "strSelectSQL_01" + strSelectSQL_01);
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_01);
    }

    public void resetPrinter(int strprintid) {
        String printerName = "";
        String strSelectSQL_02 = "UPDATE MST_PRINTERTYPE SET PRINTERSET_FLG=1 WHERE PRINTER_VAL='" + strprintid + "' ";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_02);

    }


    public void resetPreviousPapar() {
        String printerName = "";

        String strSelectSQL_01 = "UPDATE MST_PAPERTYPE SET PAPERSET_FLG=0 ";
        Log.d("DemoApp", "strSelectSQL_01" + strSelectSQL_01);

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_01);

    }

    public void resetPaper(int strprintid) {
        String printerName = "";

        String strSelectSQL_02 = "UPDATE MST_PAPERTYPE SET PAPERSET_FLG=1 WHERE PAPER_VAL='" + strprintid + "' ";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_02);

    }

    public void setSAUser(int strprintid, String Usernm) {
        String printerName = "";

        String strSelectSQL_03 = "UPDATE _SA_User_old_20200911 SET SBMPRV='" + strprintid + "' WHERE userid = '" + Usernm + "'";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_03);

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_03);

    }

    public String getInstallatioationNo(String param, int offset) {
        String selectQuery = "";
        String installationno = "";
        SQLiteDatabase db = this.getReadableDatabase();

        if (offset >= 0) {
            selectQuery = "select INSTALLATION from TBL_SPOTBILL_HEADER_DETAILS where SEQ IS NOT NULL and READ_FLAG ='1' order by CAST(SEQ AS INTEGER) ASC LIMIT 1 OFFSET " + offset + "";
        } else {
            if (param.contains("METER_NO")) {
                selectQuery = "select INSTALLATION from TBL_SPOTBILL_CHILD_DETAILS";
            } else {
                selectQuery = "select INSTALLATION from TBL_SPOTBILL_HEADER_DETAILS";
            }

            if (param.contains("where")) {
                selectQuery = selectQuery + param;
            }
        }

        Log.e("selectQueryOnlyINST", selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                installationno = cursor.getString(cursor.getColumnIndex("INSTALLATION"));
            } while (cursor.moveToNext());
        }

        return installationno;
    }

    public void uosertOkNonSbmReading(MeterOkNonSbmReadingModel
                                              meterOkNonSbmReadingModel, String param) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        String sqlquery = "UPDATE MTR_NSBM_DATA SET " +
                "PRS_KWH='" + meterOkNonSbmReadingModel.getSt_kwh() + "'," +
                "PRS_KVAH='" + meterOkNonSbmReadingModel.getSt_kvah() + "'" + "," +
                "PRS_KVARH='" + meterOkNonSbmReadingModel.getSt_kvarh() + "'," +
                "MD_PEAK='" + meterOkNonSbmReadingModel.getSt_md_peak() + "'," +
                "MD_OFFPEAK='" + meterOkNonSbmReadingModel.getSt_md_off_peak() + "'," +
                "READ_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                "TOD='" + meterOkNonSbmReadingModel.getSt_tod() + "'," +
                "ELT_ON='" + meterOkNonSbmReadingModel.getIsELTON() + "'," +
                "SUPPLY_STS='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                "USAGE='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                "METER_TYPE='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                "REMARK='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                "PAPER_PASTE_BY='" + meterOkNonSbmReadingModel.getIsPaperBill() + "'," +
                "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                "COLONY_CONSUMPTION='" + meterOkNonSbmReadingModel.getSt_colony_consumption() + "'," +
                "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + param;
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void UpdateNpDC(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String param, String flag, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        String sqlquery = "";

        if (flag.equalsIgnoreCase("NP")) {
            sqlquery = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                    "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                    "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                    "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                    "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                    "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                    "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                    "READ_FLAG='" + 0 + "'";
        } else {
            sqlquery = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                    "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                    "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                    "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                    "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                    "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                    "SUPPLY_SOURCE_ID='" + meterOkNonSbmReadingModel.getSourceOfSupply() + "'," +
                    "REASON_DC_ID='" + meterOkNonSbmReadingModel.getSt_reason_for_dc() + "'," +
                    "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                    "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                    "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                    "READ_FLAG='" + 0 + "'";
        }

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }


    public void updateNMRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNMRemarksNonSb", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "SUPPLY_SOURCE_ID='" + meterOkNonSbmReadingModel.getSourceOfSupply() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateENRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNMRemarksNonSb", g.toJson(meterOkNonSbmReadingModel));


        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "REASON_EN_ID='" + meterOkNonSbmReadingModel.getReasonEN() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void uosertOkNonSbmReadingHeader(MeterOkNonSbmReadingModel
                                                    meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        String sqlquery = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                */
/*"MD_PEAK='" + meterOkNonSbmReadingModel.getSt_md_peak() + "'," +
                "MD_OFFPEAK='" + meterOkNonSbmReadingModel.getSt_md_off_peak() + "'," +
                "READ_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                "TOD='" + meterOkNonSbmReadingModel.getSt_tod() + "'," +*//*

                "ELT_STTS='" + meterOkNonSbmReadingModel.getIsELTON() + "'," +
                "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                "STOP_PAPR_BL='" + meterOkNonSbmReadingModel.getIsPaperBill() + "'," +
                "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateMMRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson g = new Gson();
        Log.e("updateSBRemarksNonSbmR", g.toJson(meterOkNonSbmReadingModel));
        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "ELT_STTS='" + meterOkNonSbmReadingModel.getIsELTON() + "'," +
                        "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "NEW_METER_NO='" + meterOkNonSbmReadingModel.getMeterNumber() + "'," +
                        "NEW_MTR_FLG='" + meterOkNonSbmReadingModel.getSt_newMeter() + "'," +
                        "OLD_MTR_COR_FLG='" + meterOkNonSbmReadingModel.getSt_oldMeterCorrection() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateFURemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateSBRemarksNonSbmR", g.toJson(meterOkNonSbmReadingModel));
        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "ELT_STTS='" + meterOkNonSbmReadingModel.getIsELTON() + "'," +
                        "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateGBRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateSBRemarksNonSbmR", g.toJson(meterOkNonSbmReadingModel));
        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "ELT_STTS='" + meterOkNonSbmReadingModel.getIsELTON() + "'," +
                        "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void uosertOkNonSbmReadingCHILD(String value, String keys, String installno, String mrReason) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlquery = "UPDATE TBL_SPOTBILL_CHILD_DETAILS SET " +
                "PRESENT_METER_READING='" + value + "', " +
                "MRREASON='" + mrReason + "' " +
                "where REGISTER_CODE='" + keys + "' " +
                "and INSTALLATION='" + installno + "'";
        Log.e("updateQuery", sqlquery);
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateNVRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "ELT_STTS='" + meterOkNonSbmReadingModel.getIsELTON() + "'," +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                        "REASON_NV_ID='" + meterOkNonSbmReadingModel.getSt_resonNV() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateMBRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));


        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "ELT_STTS='" + meterOkNonSbmReadingModel.getIsELTON() + "'," +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "SUPPLY_SOURCE_ID='" + meterOkNonSbmReadingModel.getSourceOfSupply() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateNDRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "ELT_STTS='" + meterOkNonSbmReadingModel.getIsELTON() + "'," +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                        "REASON_MTR_STUCK_ID='" + meterOkNonSbmReadingModel.getResonMeterStuck() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updatePPRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "ELT_STTS='" + meterOkNonSbmReadingModel.getIsELTON() + "'," +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PAPER_PASTE_BY_ID='" + meterOkNonSbmReadingModel.getIsPaperBill() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateMHRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                        "METER_HEIGHT_ID='" + meterOkNonSbmReadingModel.getMeterHeight() + "'," +
                        "UNSAFE_COND='" + meterOkNonSbmReadingModel.getUnsafeCondition() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateOBRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "METER_HEIGHT_ID='" + meterOkNonSbmReadingModel.getMeterHeight() + "'," +
                        "UNSAFE_COND='" + meterOkNonSbmReadingModel.getUnsafeCondition() + "'," +
                        "TYPES_OBSTACLE_ID='" + meterOkNonSbmReadingModel.getTypeOfObstacle() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }


    public void updateSBRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "ELT_STTS='" + meterOkNonSbmReadingModel.getIsELTON() + "'," +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                        "SEAL_STTS='" + meterOkNonSbmReadingModel.getResonSealStatus() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateMURemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                        "SUPPLY_SOURCE_ID='" + meterOkNonSbmReadingModel.getSourceOfSupply() + "'," +
                        "UNSAFE_COND='" + meterOkNonSbmReadingModel.getUnsafeCondition() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }


    public void updateBLRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        //  "CONTACT_REASON_ID='" + meterOkNonSbmReadingModel.getResonContact() + "'," +

                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateTLRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "CONTACT_REASON_ID='" + meterOkNonSbmReadingModel.getResonContact() + "'," +

                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }


    public void updatePLRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +

                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "REASON_PL_ID='" + meterOkNonSbmReadingModel.getReasonPl() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateSPRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +

                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void updateWLRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +

                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }


    public void updateNARemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }


    public boolean updateMobileNo(String mobile, String param) {
        boolean isOk = false;

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sqlquery = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET PHONE_1 = " + mobile + " WHERE INSTALLATION = '" + param + "' ";
            Log.e("updateQuery", sqlquery);
            db.execSQL(sqlquery);
            db.close();
            isOk = true;
        } catch (Exception e) {
            isOk = false;
        }
        return isOk;
    }

    // inserting data in sqlight
    public HashMap<String, ArrayList<String>> inserBillingHeader(BillingResponseModel response) {
        String headerNotInstall = "";

        HashMap<String, ArrayList<String>> totalData = new HashMap<>();

        ArrayList<String> instHeaderNotInsert = new ArrayList<>();
        ArrayList<String> instChildNotInsert = new ArrayList<>();
        try {
            long rowInserted = 0;
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            String inserrtSql = "";
            String valuesSql = "";

            for (int i = 0; i < response.response.size(); i++) {

                headerNotInstall = response.response.get(i).headerDetails.installation;

               */
/* inserrtSql="Legacy_ACCOUNT_NO",response.response.get(i).headerDetails.legacyAccountNo;
                valuesSql="0";

                inserrtSql="SENT_FLAG";
                valuesSql="0";*//*


                values.put(DatabaseKeys.Key_SENT_FLAG, "0");

                if (response.response.get(i).headerDetails.legacyAccountNo != null) {
                    values.put(DatabaseKeys.Key_LEGACY_ACCOUNT_NO, response.response.get(i).headerDetails.legacyAccountNo.trim());
                }
                if (response.response.get(i).headerDetails.name != null) {
                    values.put(DatabaseKeys.Key_NAME, response.response.get(i).headerDetails.name.trim());
                }
                if (response.response.get(i).headerDetails.address1 != null) {
                    values.put(DatabaseKeys.Key_ADDRESS1, response.response.get(i).headerDetails.address1.trim());
                }
                if (response.response.get(i).headerDetails.address2 != null) {
                    values.put(DatabaseKeys.Key_ADDRESS2, response.response.get(i).headerDetails.address2.trim());
                }
                if (response.response.get(i).headerDetails.seq != null) {
                    values.put(DatabaseKeys.Key_SEQ, response.response.get(i).headerDetails.seq.toString().trim());
                }
                if (response.response.get(i).headerDetails.subSeq != null) {
                    values.put(DatabaseKeys.Key_SUB_SEQ, response.response.get(i).headerDetails.subSeq.toString().trim());
                }
                if (response.response.get(i).headerDetails.legacyAccountNo2 != null) {
                    values.put(DatabaseKeys.Key_LEGACY_ACCOUNT_NO2, response.response.get(i).headerDetails.legacyAccountNo2.trim());
                }

                if (response.response.get(i).headerDetails.installation != null) {
                    values.put(DatabaseKeys.Key_INSTALLATION, response.response.get(i).headerDetails.installation.trim());
                }
                if (response.response.get(i).headerDetails.rateCategory != null) {
                    values.put(DatabaseKeys.Key_RATE_CATEGORY, response.response.get(i).headerDetails.rateCategory.trim());
                }
                if (response.response.get(i).headerDetails.div != null) {
                    values.put(DatabaseKeys.Key_DIV, response.response.get(i).headerDetails.div.trim());
                }
                if (response.response.get(i).headerDetails.subDiv != null) {
                    values.put(DatabaseKeys.Key_SUB_DIV, response.response.get(i).headerDetails.subDiv.trim());
                }
                if (response.response.get(i).headerDetails.section != null) {
                    values.put(DatabaseKeys.Key_SECTION, response.response.get(i).headerDetails.section.toString().trim());
                }
                if (response.response.get(i).headerDetails.consumerOwned != null) {
                    values.put(DatabaseKeys.Key_CONSUMER_OWNED, response.response.get(i).headerDetails.consumerOwned.toString().trim());
                }
                if (response.response.get(i).headerDetails.meterMake != null) {
                    values.put(DatabaseKeys.Key_METER_MAKE, response.response.get(i).headerDetails.meterMake.toString().trim());
                }
                if (response.response.get(i).headerDetails.usage != null) {
                    values.put(DatabaseKeys.Key_USAGE, response.response.get(i).headerDetails.usage.toString().trim());
                }
                if (response.response.get(i).headerDetails.sanLoad != null) {
                    values.put(DatabaseKeys.Key_SAN_LOAD, response.response.get(i).headerDetails.sanLoad.toString().trim());
                }
                if (response.response.get(i).headerDetails.moveInDate != null) {
                    values.put(DatabaseKeys.Key_MOVE_IN_DATE, response.response.get(i).headerDetails.moveInDate.toString().trim());
                }
                if (response.response.get(i).headerDetails.dps != null) {
                    values.put(DatabaseKeys.Key_DPS, response.response.get(i).headerDetails.dps.toString().trim());
                }
                if (response.response.get(i).headerDetails.miscCharges != null) {
                    values.put(DatabaseKeys.Key_MISC_CHARGES, response.response.get(i).headerDetails.miscCharges.toString().trim());
                }
                if (response.response.get(i).headerDetails.crAdj != null) {
                    values.put(DatabaseKeys.Key_CR_ADJ, response.response.get(i).headerDetails.crAdj.toString().trim());
                }
                if (response.response.get(i).headerDetails.dbAdj != null) {
                    values.put(DatabaseKeys.Key_DB_ADJ, response.response.get(i).headerDetails.dbAdj.toString().trim());
                }
                if (response.response.get(i).headerDetails.prvBilledAmt != null) {
                    values.put(DatabaseKeys.Key_PRV_BILLED_AMT, response.response.get(i).headerDetails.prvBilledAmt.toString().trim());
                }
                if (response.response.get(i).headerDetails.previousBilledProvUnit != null) {
                    values.put(DatabaseKeys.Key_PREVIOUS_BILLED_PROV_UNIT, response.response.get(i).headerDetails.previousBilledProvUnit.toString().trim());
                }
                if (response.response.get(i).headerDetails.lastPaidDate != null) {
                    values.put(DatabaseKeys.Key_LAST_PAID_DATE, response.response.get(i).headerDetails.lastPaidDate.toString().trim());
                }
                if (response.response.get(i).headerDetails.lastPymtRcpt != null) {
                    values.put(DatabaseKeys.Key_LAST_PYMT_RCPT, response.response.get(i).headerDetails.lastPymtRcpt.toString().trim());
                }
                if (response.response.get(i).headerDetails.lastPaidAmt != null) {
                    values.put(DatabaseKeys.Key_LAST_PAID_AMT, response.response.get(i).headerDetails.lastPaidAmt.toString().trim());
                }
                if (response.response.get(i).headerDetails.edExempt != null) {
                    values.put(DatabaseKeys.Key_ED_EXEMPT, response.response.get(i).headerDetails.edExempt.toString().trim());
                }
                if (response.response.get(i).headerDetails.aifi != null) {
                    values.put(DatabaseKeys.Key_AIFI, response.response.get(i).headerDetails.aifi.toString().trim());
                }
                if (response.response.get(i).headerDetails.newMeterNo != null) {
                    values.put(DatabaseKeys.Key_NEW_METER_NO, response.response.get(i).headerDetails.newMeterNo.toString().trim());
                }
                if (response.response.get(i).headerDetails.sdi != null) {
                    values.put(DatabaseKeys.Key_SDI, response.response.get(i).headerDetails.sdi.toString().trim());
                }
                if (response.response.get(i).headerDetails.asd != null) {
                    values.put(DatabaseKeys.Key_ASD, response.response.get(i).headerDetails.asd.toString().trim());
                }
                if (response.response.get(i).headerDetails.asdaa != null) {
                    values.put(DatabaseKeys.Key_ASDAA, response.response.get(i).headerDetails.asdaa.toString().trim());
                }
                if (response.response.get(i).headerDetails.sbmBillNo != null) {
                    values.put(DatabaseKeys.Key_SBM_BILL_NO, response.response.get(i).headerDetails.sbmBillNo.toString().trim());
                }
                if (response.response.get(i).headerDetails.ca != null) {
                    values.put(DatabaseKeys.Key_CA, response.response.get(i).headerDetails.ca.toString().trim());
                }
                if (response.response.get(i).headerDetails.prvArr != null) {
                    values.put(DatabaseKeys.Key_PRV_ARR, response.response.get(i).headerDetails.prvArr.toString().trim());
                }
                if (response.response.get(i).headerDetails.arrears != null) {
                    values.put(DatabaseKeys.Key_ARREARS, response.response.get(i).headerDetails.arrears.toString().trim());
                }
                if (response.response.get(i).headerDetails.ulf != null) {
                    values.put(DatabaseKeys.Key_ULF, response.response.get(i).headerDetails.ulf.toString().trim());
                }
                if (response.response.get(i).headerDetails.prevBillUnits != null) {
                    values.put(DatabaseKeys.Key_PREV_BILL_UNITS, response.response.get(i).headerDetails.prevBillUnits.toString().trim());
                }
                if (response.response.get(i).headerDetails.ecsLimt != null) {
                    values.put(DatabaseKeys.Key_ECS_LIMT, response.response.get(i).headerDetails.ecsLimt.toString().trim());
                }
                if (response.response.get(i).headerDetails.ecsValidityPeriod != null) {
                    values.put(DatabaseKeys.Key_ECS_VALIDITY_PERIOD, response.response.get(i).headerDetails.ecsValidityPeriod.toString().trim());
                }
                if (response.response.get(i).headerDetails.presentReadingRemark != null) {
                    values.put(DatabaseKeys.Key_PRESENT_READING_REMARK, response.response.get(i).headerDetails.presentReadingRemark.toString().trim());
                }
                if (response.response.get(i).headerDetails.presentMeterStatus != null) {
                    values.put(DatabaseKeys.Key_PRESENT_METER_STATUS, response.response.get(i).headerDetails.presentMeterStatus.toString().trim());
                }
                if (response.response.get(i).headerDetails.presentBillUnits != null) {
                    values.put(DatabaseKeys.Key_PRESENT_BILL_UNITS, response.response.get(i).headerDetails.presentBillUnits.toString().trim());
                }
                if (response.response.get(i).headerDetails.presentBillType != null) {
                    values.put(DatabaseKeys.Key_PRESENT_BILL_TYPE, response.response.get(i).headerDetails.presentBillType.toString().trim());
                }
                if (response.response.get(i).headerDetails.ec != null) {
                    values.put(DatabaseKeys.Key_EC, response.response.get(i).headerDetails.ec.toString().trim());
                }

                if (response.response.get(i).headerDetails.mmfc != null) {
                    values.put(DatabaseKeys.Key_MMFC, response.response.get(i).headerDetails.mmfc.toString().trim());
                }

                if (response.response.get(i).headerDetails.mrentCharged != null) {
                    values.put(DatabaseKeys.Key_MRENT_CHARGED, response.response.get(i).headerDetails.mrentCharged.toString().trim());
                }

                if (response.response.get(i).headerDetails.ed != null) {
                    values.put(DatabaseKeys.Key_ED, response.response.get(i).headerDetails.ed.toString().trim());
                }

                if (response.response.get(i).headerDetails.currentBillTotal != null) {
                    values.put(DatabaseKeys.Key_CURRENT_BILL_TOTAL, response.response.get(i).headerDetails.currentBillTotal.toString().trim());
                }

                if (response.response.get(i).headerDetails.rebate != null) {
                    values.put(DatabaseKeys.Key_REBATE, response.response.get(i).headerDetails.rebate.toString().trim());
                }

                if (response.response.get(i).headerDetails.amountPayable != null) {
                    values.put(DatabaseKeys.Key_AMOUNT_PAYABLE, response.response.get(i).headerDetails.amountPayable.toString().trim());
                }

                if (response.response.get(i).headerDetails.avgUnitBilled != null) {
                    values.put(DatabaseKeys.Key_AVG_UNIT_BILLED, response.response.get(i).headerDetails.avgUnitBilled.toString().trim());
                }

                if (response.response.get(i).headerDetails.rcptno != null) {
                    values.put(DatabaseKeys.Key_RCPTNO, response.response.get(i).headerDetails.rcptno.toString().trim());
                }

                if (response.response.get(i).headerDetails.chqno != null) {
                    values.put(DatabaseKeys.Key_CHQNO, response.response.get(i).headerDetails.chqno.toString().trim());
                }

                if (response.response.get(i).headerDetails.chqdt != null) {
                    values.put(DatabaseKeys.Key_CHQDT, response.response.get(i).headerDetails.chqdt.toString().trim());
                }

                if (response.response.get(i).headerDetails.bank != null) {
                    values.put(DatabaseKeys.Key_BANK, response.response.get(i).headerDetails.bank.toString().trim());
                }

                if (response.response.get(i).headerDetails.rcptamt != null) {
                    values.put(DatabaseKeys.Key_RCPTAMT, response.response.get(i).headerDetails.rcptamt.toString().trim());
                }

                if (response.response.get(i).headerDetails.dueDate != null) {
                    values.put(DatabaseKeys.Key_DUE_DATE, response.response.get(i).headerDetails.dueDate.toString().trim());
                }

                if (response.response.get(i).headerDetails.doExpiry != null) {
                    values.put(DatabaseKeys.Key_DO_EXPIRY, response.response.get(i).headerDetails.doExpiry.toString().trim());
                }

                if (response.response.get(i).headerDetails.presentReadingTime != null) {
                    values.put(DatabaseKeys.Key_PRESENT_READING_TIME, response.response.get(i).headerDetails.presentReadingTime.toString().trim());
                }

                if (response.response.get(i).headerDetails.osbillDate != null) {
                    values.put(DatabaseKeys.Key_OSBILL_DATE, response.response.get(i).headerDetails.osbillDate.toString().trim());
                }
                if (response.response.get(i).headerDetails.capturedMobile != null) {
                    values.put(DatabaseKeys.Key_CAPTURED_MOBILE, response.response.get(i).headerDetails.capturedMobile.toString().trim());
                }
                if (response.response.get(i).headerDetails.meterRent != null) {
                    values.put(DatabaseKeys.Key_METER_RENT, response.response.get(i).headerDetails.meterRent.toString().trim());
                }
                if (response.response.get(i).headerDetails.portion != null) {
                    values.put(DatabaseKeys.Key_PORTION, response.response.get(i).headerDetails.portion.trim());
                }
                if (response.response.get(i).headerDetails.mru != null) {
                    values.put(DatabaseKeys.Key_MRU, response.response.get(i).headerDetails.mru.trim());
                }
                if (response.response.get(i).headerDetails.noOfReg != null) {
                    values.put(DatabaseKeys.Key_NO_OF_REG, response.response.get(i).headerDetails.noOfReg.toString().trim());
                }

                if (!response.response.get(i).headerDetails.sanLoadUnits.equals(null)) {
                    values.put(DatabaseKeys.Key_SAN_LOAD_UNITS, response.response.get(i).headerDetails.sanLoadUnits.trim());
                }
                if (response.response.get(i).headerDetails.sanLoadEffectiveDate != null) {
                    values.put(DatabaseKeys.Key_SAN_LOAD_EFFECTIVE_DATE, response.response.get(i).headerDetails.sanLoadEffectiveDate.toString().trim());
                }
                if (response.response.get(i).headerDetails.supplyTypFlg != null) {
                    values.put(DatabaseKeys.Key_SUPPLY_TYP_FLG, response.response.get(i).headerDetails.supplyTypFlg.toString().trim());
                }
                if (response.response.get(i).headerDetails.notinuseFlgEnddate != null) {
                    values.put(DatabaseKeys.Key_NOTINUSE_FLG_ENDDATE, response.response.get(i).headerDetails.notinuseFlgEnddate.toString().trim());
                }
                if (response.response.get(i).headerDetails.otherFlgs != null) {
                    values.put(DatabaseKeys.Key_OTHER_FLGS, response.response.get(i).headerDetails.otherFlgs.toString().trim());
                }
                if (response.response.get(i).headerDetails.edRbt != null) {
                    values.put(DatabaseKeys.Key_ED_RBT, response.response.get(i).headerDetails.edRbt.toString().trim());
                }
                if (response.response.get(i).headerDetails.hostelRbt != null) {
                    values.put(DatabaseKeys.Key_HOSTEL_RBT, response.response.get(i).headerDetails.hostelRbt.toString().trim());
                }
                if (response.response.get(i).headerDetails.prevBillType != null) {
                    values.put(DatabaseKeys.Key_PREV_BILL_TYPE, response.response.get(i).headerDetails.prevBillType.toString().trim());
                }
                if (response.response.get(i).headerDetails.prevBillRemark != null) {
                    values.put(DatabaseKeys.Key_PREV_BILL_REMARK, response.response.get(i).headerDetails.prevBillRemark.toString().trim());
                }
                if (response.response.get(i).headerDetails.prevBillEndDate != null) {
                    values.put(DatabaseKeys.Key_PREV_BILL_END_DATE, response.response.get(i).headerDetails.prevBillEndDate.toString().trim());
                }
                if (response.response.get(i).headerDetails.lastNormalBillDate != null) {
                    values.put(DatabaseKeys.Key_LAST_NORMAL_BILL_DATE, response.response.get(i).headerDetails.lastNormalBillDate.toString().trim());
                }
                if (response.response.get(i).headerDetails.presentReadingDate != null) {
                    values.put(DatabaseKeys.Key_PRESENT_READING_DATE, response.response.get(i).headerDetails.presentReadingDate.toString().trim());
                }
                if (response.response.get(i).headerDetails.averageKwh != null) {
                    values.put(DatabaseKeys.Key_AVERAGE_KWH, response.response.get(i).headerDetails.averageKwh.toString().trim());
                }

                if (response.response.get(i).headerDetails.billBasis != null) {
                    values.put(DatabaseKeys.Key_BILL_BASIS, response.response.get(i).headerDetails.billBasis.toString().trim());
                }

                if (response.response.get(i).headerDetails.billNo != null) {
                    values.put(DatabaseKeys.Key_BILL_NO, response.response.get(i).headerDetails.billNo.toString().trim());
                }

                if (response.response.get(i).headerDetails.invoiceNo != null) {
                    values.put(DatabaseKeys.Key_INVOICE_NO, response.response.get(i).headerDetails.invoiceNo.toString().trim());
                }
                if (response.response.get(i).headerDetails.readOnly != null) {
                    values.put(DatabaseKeys.Key_READ_ONLY, response.response.get(i).headerDetails.readOnly.toString().trim());
                }
                if (response.response.get(i).headerDetails.bpStartDate != null) {
                    values.put(DatabaseKeys.Key_BP_START_DATE, response.response.get(i).headerDetails.bpStartDate.toString().trim());
                }
                if (response.response.get(i).headerDetails.bpEndDate != null) {
                    values.put(DatabaseKeys.Key_BP_END_DATE, response.response.get(i).headerDetails.bpEndDate.toString().trim());
                }

                if (response.response.get(i).headerDetails.ppac != null) {
                    values.put(DatabaseKeys.Key_PPAC, response.response.get(i).headerDetails.ppac.toString().trim());
                }

                if (response.response.get(i).headerDetails.roundOff != null) {
                    values.put(DatabaseKeys.Key_ROUND_OFF, response.response.get(i).headerDetails.roundOff.toString().trim());
                }
                if (response.response.get(i).headerDetails.secDepositAmt != null) {
                    values.put(DatabaseKeys.Key_SEC_DEPOSIT_AMT, response.response.get(i).headerDetails.secDepositAmt.toString().trim());
                }
                if (response.response.get(i).headerDetails.doGenerated != null) {
                    values.put(DatabaseKeys.Key_DO_GENERATED, response.response.get(i).headerDetails.doGenerated.toString().trim());
                }
                if (response.response.get(i).headerDetails.notToBillAfter != null) {
                    values.put(DatabaseKeys.Key_NOT_TO_BILL_AFTER, response.response.get(i).headerDetails.notToBillAfter.toString().trim());
                }

                if (response.response.get(i).headerDetails.insertDate != null) {
                    values.put(DatabaseKeys.Key_INSERT_DATE, (response.response.get(i).headerDetails.insertDate.toString().trim()));
                }
                if (response.response.get(i).headerDetails.insertTime != null) {
                    values.put(DatabaseKeys.Key_INSERT_TIME, response.response.get(i).headerDetails.insertTime.toString().trim());
                }
                if (response.response.get(i).headerDetails.updateDate != null) {
                    values.put(DatabaseKeys.Key_UPDATE_DATE, response.response.get(i).headerDetails.updateDate.toString().trim());
                }
                if (response.response.get(i).headerDetails.updateTime != null) {
                    values.put(DatabaseKeys.Key_UPDATE_TIME, response.response.get(i).headerDetails.updateTime.toString().trim());
                }

                if (response.response.get(i).headerDetails.phone1 != null) {
                    values.put(DatabaseKeys.Key_PHONE_1, response.response.get(i).headerDetails.phone1.trim());
                }
                if (response.response.get(i).headerDetails.phone2 != null) {
                    values.put(DatabaseKeys.Key_PHONE_2, response.response.get(i).headerDetails.phone2.trim());
                }
                if (response.response.get(i).headerDetails.transType != null) {
                    values.put(DatabaseKeys.Key_TRANS_TYPE, response.response.get(i).headerDetails.transType.toString().trim());
                }
                if (response.response.get(i).headerDetails.tdFlag != null) {
                    values.put(DatabaseKeys.Key_TD_FLAG, response.response.get(i).headerDetails.tdFlag.toString().trim());
                }
                if (response.response.get(i).headerDetails.tdDate != null) {
                    values.put(DatabaseKeys.Key_TD_DATE, response.response.get(i).headerDetails.tdDate.toString().trim());
                }
                if (response.response.get(i).headerDetails.ppi != null) {
                    values.put(DatabaseKeys.Key_PPI, response.response.get(i).headerDetails.ppi.toString().trim());
                }
                if (response.response.get(i).headerDetails.prevProvAmt != null) {
                    values.put(DatabaseKeys.Key_PREV_PROV_AMT, response.response.get(i).headerDetails.prevProvAmt.toString().trim());
                }
                if (response.response.get(i).headerDetails.gstRelevant1 != null) {
                    values.put(DatabaseKeys.Key_GST_RELEVANT1, response.response.get(i).headerDetails.gstRelevant1.toString().trim());
                }
                if (response.response.get(i).headerDetails.gstRelevant2 != null) {
                    values.put(DatabaseKeys.Key_GST_RELEVANT2, response.response.get(i).headerDetails.gstRelevant2.toString().trim());
                }
                if (response.response.get(i).headerDetails.gstRelevant3 != null) {
                    values.put(DatabaseKeys.Key_GST_RELEVANT3, response.response.get(i).headerDetails.gstRelevant3.toString().trim());
                }
                if (response.response.get(i).headerDetails.gstRelevant4 != null) {
                    values.put(DatabaseKeys.Key_GST_RELEVANT4, response.response.get(i).headerDetails.gstRelevant4.toString().trim());
                }
                if (response.response.get(i).headerDetails.eltStts != null) {
                    values.put(DatabaseKeys.Key_ELT_STTS, response.response.get(i).headerDetails.eltStts.toString().trim());
                }
                if (response.response.get(i).headerDetails.sealStts != null) {
                    values.put(DatabaseKeys.Key_SEAL_STTS, response.response.get(i).headerDetails.sealStts.toString().trim());
                }

                if (response.response.get(i).headerDetails.cblass != null) {
                    values.put(DatabaseKeys.Key_CBLASS, response.response.get(i).headerDetails.cblass.toString().trim());
                }
                if (response.response.get(i).headerDetails.usageId != null) {
                    values.put(DatabaseKeys.Key_USAGE_ID, response.response.get(i).headerDetails.usageId.toString().trim());
                }

                if (response.response.get(i).headerDetails.meterTypeId != null) {
                    values.put(DatabaseKeys.Key_METER_TYPE_ID, response.response.get(i).headerDetails.meterTypeId.toString().trim());
                }

                if (response.response.get(i).headerDetails.supplyStatusId != null) {
                    values.put(DatabaseKeys.Key_SUPPLY_STATUS_ID, response.response.get(i).headerDetails.supplyStatusId.toString().trim());
                }

                if (response.response.get(i).headerDetails.reasonDcId != null) {
                    values.put(DatabaseKeys.Key_REASON_DC_ID, response.response.get(i).headerDetails.reasonDcId.toString().trim());
                }

                if (response.response.get(i).headerDetails.supplySourceId != null) {
                    values.put(DatabaseKeys.Key_SUPPLY_SOURCE_ID, response.response.get(i).headerDetails.supplySourceId.toString().trim());
                }

                if (response.response.get(i).headerDetails.reasonNvId != null) {
                    values.put(DatabaseKeys.Key_REASON_NV_ID, response.response.get(i).headerDetails.reasonNvId.toString().trim());
                }

                if (response.response.get(i).headerDetails.reasonCdId != null) {
                    values.put(DatabaseKeys.Key_REASON_CD_ID, response.response.get(i).headerDetails.reasonCdId.toString().trim());
                }

                if (response.response.get(i).headerDetails.reasonEnId != null) {
                    values.put(DatabaseKeys.Key_REASON_EN_ID, response.response.get(i).headerDetails.reasonEnId.toString().trim());
                }

                if (response.response.get(i).headerDetails.reasonMtrStuckId != null) {
                    values.put(DatabaseKeys.Key_REASON_MTR_STUCK_ID, response.response.get(i).headerDetails.reasonMtrStuckId.toString().trim());
                }

                if (response.response.get(i).headerDetails.paperPasteById != null) {
                    values.put(DatabaseKeys.Key_PAPER_PASTE_BY_ID, response.response.get(i).headerDetails.paperPasteById.toString().trim());
                }


                if (response.response.get(i).headerDetails.meterHeightId != null) {
                    values.put(DatabaseKeys.Key_METER_HEIGHT_ID, response.response.get(i).headerDetails.meterHeightId.toString().trim());
                }

                if (response.response.get(i).headerDetails.typesObstacleId != null) {
                    values.put(DatabaseKeys.Key_TYPES_OBSTACLE_ID, response.response.get(i).headerDetails.typesObstacleId.toString().trim());
                }

                if (response.response.get(i).headerDetails.sealStsId != null) {
                    values.put(DatabaseKeys.Key_SEAL_STS_ID, response.response.get(i).headerDetails.sealStsId.toString().trim());
                }

                if (response.response.get(i).headerDetails.contactReasonId != null) {
                    values.put(DatabaseKeys.Key_CONTACT_REASON_ID, response.response.get(i).headerDetails.contactReasonId.toString().trim());
                }

                if (response.response.get(i).headerDetails.reasonPlId != null) {
                    values.put(DatabaseKeys.Key_REASON_PL_ID, response.response.get(i).headerDetails.reasonPlId.toString().trim());
                }

                if (response.response.get(i).headerDetails.hlMonths != null) {
                    values.put(DatabaseKeys.Key_HL_MONTHS, response.response.get(i).headerDetails.hlMonths.toString().trim());
                }
                if (response.response.get(i).headerDetails.buildingDesc != null) {
                    values.put(DatabaseKeys.Key_BUILDING_DESC, response.response.get(i).headerDetails.buildingDesc.trim());
                }
                if (response.response.get(i).headerDetails.buildingCode != null) {
                    values.put(DatabaseKeys.Key_BUILDING_CODE, response.response.get(i).headerDetails.buildingCode.trim());
                }
                if (response.response.get(i).headerDetails.poleNo != null) {
                    values.put(DatabaseKeys.Key_POLE_NO, response.response.get(i).headerDetails.poleNo.trim());
                }
                if (response.response.get(i).headerDetails.flag != null) {
                    values.put(DatabaseKeys.Key_FLAG, response.response.get(i).headerDetails.flag.toString().trim());
                }
                if (response.response.get(i).headerDetails.specialRem != null) {
                    values.put(DatabaseKeys.Key_SPECIAL_REM, response.response.get(i).headerDetails.specialRem.trim());
                }

                if (response.response.get(i).headerDetails.senderMobile != null) {
                    values.put(DatabaseKeys.Key_SENDER_MOBILE, response.response.get(i).headerDetails.senderMobile.toString().trim());
                }

                if (response.response.get(i).headerDetails.consumerType != null) {
                    values.put(DatabaseKeys.Key_CONSUMER_TYPE, response.response.get(i).headerDetails.consumerType.trim());
                }

                if (response.response.get(i).headerDetails.walkingSeqChk != null) {
                    values.put(DatabaseKeys.Key_WALKING_SEQ_CHK, response.response.get(i).headerDetails.walkingSeqChk.toString().trim());
                }

                if (response.response.get(i).headerDetails.stopPaprBl != null) {
                    values.put(DatabaseKeys.Key_STOP_PAPR_BL, response.response.get(i).headerDetails.stopPaprBl.toString().trim());
                }

                if (response.response.get(i).headerDetails.scheduleMeterReadDate != null) {
                    values.put(DatabaseKeys.Key_SCHEDULE_METER_READ_DATE, response.response.get(i).headerDetails.scheduleMeterReadDate.toString().trim());
                }

                if (response.response.get(i).headerDetails.billMonth != null) {
                    values.put(DatabaseKeys.Key_BILL_MONTH, response.response.get(i).headerDetails.billMonth.toString().trim());
                }

                if (response.response.get(i).headerDetails.billYear != null) {
                    values.put(DatabaseKeys.Key_BILL_YEAR, response.response.get(i).headerDetails.billYear.toString().trim());
                }
                if (response.response.get(i).headerDetails.mrreason != null) {
                    values.put(DatabaseKeys.Key_MRREASON, response.response.get(i).headerDetails.mrreason.toString().trim());
                }

                values.put(DatabaseKeys.Key_READ_FLAG, "0");
                rowInserted = db.insert(DatabaseKeys.TABLE_HEADER, null, values);

            }
            if (rowInserted != -1) {
                inserBillingChildData(response);
                Toast.makeText(mContext, "New header row added, row id: " + rowInserted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "header Something wrong", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            instHeaderNotInsert.add(headerNotInstall);

            totalData.put("header", instHeaderNotInsert);
            totalData.put("child", instChildNotInsert);
        }

        return totalData;
    }

    public void inserBillingChildData(BillingResponseModel response) {
        try {
            long rowInserted = 0;
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            for (int j = 0; j < response.response.get(j).headerDetails.childDataList.size(); j++) {
                List<ChildDetail> mlist = response.response.get(j).headerDetails.childDataList;
                for (int i = 0; i < mlist.size(); i++) {
                    System.out.println(i + "    " + "Child data size::" + mlist.size());
                    if (mlist.get(i).ablbelnr != null) {
                        values.put(DatabaseKeys.childKey_ABLBELNR, mlist.get(i).ablbelnr.toString().trim());
                    }
                    if (mlist.get(i).billMonth != null) {
                        values.put(DatabaseKeys.childKey_BILL_MONTH, mlist.get(i).billMonth.toString().trim());
                    }
                    if (mlist.get(i).billYear != null) {
                        values.put(DatabaseKeys.childKey_BILL_YEAR, mlist.get(i).billYear.toString().trim());
                    }
                    if (mlist.get(i).billedMd != null) {
                        values.put(DatabaseKeys.childKey_BILLED_MD, mlist.get(i).billedMd.toString().trim());
                    }
                    if (mlist.get(i).consumptionOldMeter != null) {
                        values.put(DatabaseKeys.childKey_CONSUMPTION_OLD_METER, (mlist.get(i).consumptionOldMeter.toString().trim()));
                    }
                    if (mlist.get(i).equipmentNo != null) {
                        values.put(DatabaseKeys.childKey_EQUIPMENT_NO, (mlist.get(i).equipmentNo.toString().trim()));
                    }
                    if (mlist.get(i).installation != null) {
                        values.put(DatabaseKeys.childKey_INSTALLATION, (mlist.get(i).installation.toString().trim()));
                    }
                    if (mlist.get(i).lastOkRdng != null) {
                        values.put(DatabaseKeys.childKey_LAST_OK_RDNG, mlist.get(i).lastOkRdng.toString().trim());
                    }
                    if (mlist.get(i).meterCondition != null) {
                        values.put(DatabaseKeys.childKey_METER_CONDITION, mlist.get(i).meterCondition.toString().trim());
                    }
                    if (mlist.get(i).meterInstallDate != null) {
                        values.put(DatabaseKeys.childKey_METER_INSTALL_DATE, (mlist.get(i).meterInstallDate.toString().trim()));
                    }
                    if (mlist.get(i).meterNo != null) {
                        values.put(DatabaseKeys.childKey_METER_NO, mlist.get(i).meterNo.trim());

                    }
                    if (mlist.get(i).meterRemovedOn != null) {
                        values.put(DatabaseKeys.childKey_METER_REMOVED_ON, (mlist.get(i).meterRemovedOn.toString().trim()));
                    }
                    if (mlist.get(i).meterTyp != null) {
                        values.put(DatabaseKeys.childKey_METER_TYP, (mlist.get(i).meterTyp.toString().trim()));
                    }
                    if (mlist.get(i).mf != null) {
                        values.put(DatabaseKeys.childKey_MF, mlist.get(i).mf.toString().trim());
                    }
                    if (mlist.get(i).mrreason != null) {
                        values.put(DatabaseKeys.childKey_MRREASON, mlist.get(i).mrreason.toString().trim());
                    }
                    if (mlist.get(i).newMeterFlg != null) {
                        values.put(DatabaseKeys.childKey_NEW_METER_FLG, mlist.get(i).newMeterFlg.toString().trim());
                    }
                    if (mlist.get(i).noOfDigits != null) {
                        values.put(DatabaseKeys.childKey_NO_OF_DIGITS, mlist.get(i).noOfDigits.toString().trim());
                    }

                    if (mlist.get(i).presentMeterReading != null) {
                        values.put(DatabaseKeys.childKey_PRESENT_METER_READING, mlist.get(i).presentMeterReading.toString().trim());
                    }

                    if (mlist.get(i).prevMtrRead != null) {
                        values.put(DatabaseKeys.childKey_PREV_MTR_READ, mlist.get(i).prevMtrRead.toString().trim());
                    }

                    if (mlist.get(i).prevMtrRead != null) {
                        values.put(DatabaseKeys.childKey_PREV_READ_DATE, (mlist.get(i).prevMtrRead.toString().trim()));
                    }

                    if (mlist.get(i).previousMd != null) {
                        values.put(DatabaseKeys.childKey_PREVIOUS_MD, mlist.get(i).previousMd.toString().trim());
                    }

                    if (mlist.get(i).prsMd != null) {
                        values.put(DatabaseKeys.childKey_PRS_MD, (mlist.get(i).prsMd.toString().trim()));
                    }

                    if (mlist.get(i).registerCode != null) {
                        values.put(DatabaseKeys.childKey_REGISTER_CODE, mlist.get(i).registerCode.toString().trim());
                    }

                    if (mlist.get(i).scheduleMeterReadDate != null) {
                        values.put(DatabaseKeys.childKey_SCHEDULE_METER_READ_DATE, (mlist.get(i).scheduleMeterReadDate.toString().trim()));
                    }
                    rowInserted = db.insert(DatabaseKeys.TABLE_CHILD, null, values);
                }

                if (rowInserted != -1) {
                    Toast.makeText(mContext, "New child row added, row id: " + rowInserted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "child Something wrong", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("Child Intertion Error", ex.getMessage());
        }
    }


    public Cursor getHeaderdetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + DatabaseKeys.TABLE_HEADER + " WHERE SENT_FLAG = '0'";
        System.out.println("Query::" + query);
        Cursor cursor = db.rawQuery("select * from " + DatabaseKeys.TABLE_HEADER + " WHERE SENT_FLAG = '0'", null);
        return cursor;
    }

    public Cursor getChilddetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseKeys.TABLE_CHILD, null);
        return cursor;
    }

    public Cursor getUserheaderdetails(String condition) {
        Log.e("HeaderCondition", condition);
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();

        if (condition.contains("METER_NO")) {
            String installationno = getInstallatioationNo(condition, -1);
            condition = " where INSTALLATION='" + installationno + "'";
        }
        String query = "SELECT * FROM " + DatabaseKeys.TABLE_HEADER + condition;
        Log.e("UserHeaderdeatils", query);
        cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getUserChilddetails(String condition) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseKeys.TABLE_CHILD + condition, null);
        return cursor;
    }

    public void updatesendFlag(String condition) {
        Log.e("condition", condition);
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String strSelectSQL_02 = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET SENT_FLAG=1" + condition + " ";
            System.out.println("query:::" + strSelectSQL_02);
            db.execSQL("UPDATE TBL_SPOTBILL_HEADER_DETAILS SET SENT_FLAG=1" + condition + " ");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    ///////////////////avik need to change the function
    public void updateMassSentflag(List<HeaderresDetails> details) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            for (int i = 0; i < details.size(); i++) {
                System.out.println("getting the data::" + details.get(i).getInstallation());
                db.execSQL("UPDATE TBL_SPOTBILL_HEADER_DETAILS SET SENT_FLAG=1" + " " + "WHere INSTALLATION =" + details.get(i).getInstallation() + " ");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }


    public void deleteDataHeader(String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "TBL_SPOTBILL_HEADER_DETAILS" + " WHERE " + "INSTALLATION" + "='" + installationNo + "'");
        db.close();
    }

    public void deleteDataChild(String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "TBL_SPOTBILL_CHILD_DETAILS" + " WHERE " + "INSTALLATION" + "='" + installationNo + "'");
        db.close();
    }

    public boolean isExistsHeader(String installationNo) {
        SQLiteDatabase sqldb = getWritableDatabase();
        String selectString = "SELECT INSTALLATION FROM " + "TBL_SPOTBILL_HEADER_DETAILS" + " WHERE " + "INSTALLATION" + " =?";
        Cursor cursor = sqldb.rawQuery(selectString, new String[]{installationNo});

        boolean isExist = false;

        if (cursor.moveToFirst()) {
            isExist = true;
        }

        sqldb.close();
        return isExist;
    }

    public boolean isExistsChild(String installationNo) {
        SQLiteDatabase sqldb = getWritableDatabase();
        String selectString = "SELECT INSTALLATION FROM " + "TBL_SPOTBILL_CHILD_DETAILS" + " WHERE " + "INSTALLATION" + " =?";
        Cursor cursor = sqldb.rawQuery(selectString, new String[]{installationNo});

        boolean isExist = false;

        if (cursor.moveToFirst()) {
            isExist = true;
        }

        sqldb.close();
        return isExist;
    }

    public void updateReferenceDateIfEmpty() {

        String strSelectSQL_01 = "UPDATE TBL_SPOTBILL_HEADER_DETAILS  SET " + "REFERENCE_DATE=" + "SCHEDULE_METER_READ_DATE" + "WHERE REFERENCE_DATE" +
                "IS NULL" +
                "OR LENGTH(REFERENCE_DATE)=0";

        Log.d("DemoApp", "strSelectSQL_01" + strSelectSQL_01);
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_01);

    }

    public String getScheduleDate() {

        String scheduleDate = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SCHEDULE_METER_READ_DATE FROM TBL_SPOTBILL_HEADER_DETAILS";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                scheduleDate = cursor.getString(cursor.getColumnIndex("SCHEDULE_METER_READ_DATE"));
            }
        }

        return scheduleDate;
    }


    public NSBMData getNBSMReadData1212121() {
        NSBMData nSBMDataDatamodel = null;
        */
/*String installation = "";
        installation = getInstallatioationNo(param, -1);*//*

        */
/*String selectQuery1 = "select INSTALLATION from TBL_SPOTBILL_HEADER_DETAILS";
        selectQuery1 = selectQuery1 + param;*//*

        Map<String, String> LinkedHashMapValues = new LinkedHashMap<>();
        LinkedHashMapValues.clear();
        */
/*String selectQuery = "select * from TBL_SPOTBILL_HEADER_DETAILS h left join TBL_SPOTBILL_CHILD_DETAILS c " +
                "where h.INSTALLATION=c.INSTALLATION and c.INSTALLATION='" + installation + "'";*//*

        String selectQuery = "SELECT strftime('%d-%m-%Y',h.INSERT_DATE),h.LEGACY_ACCOUNT_NO,h.NAME,h.ADDRESS1,h.ADDRESS2,h.SEQ,h.SUB_SEQ,c.ABLBELNR,CASE WHEN h.RATE_CATEGORY='01' THEN 'DOM' WHEN h.RATE_CATEGORY='02' THEN 'RGVY' WHEN h.RATE_CATEGORY='04' THEN 'BJVY' " +
                " WHEN h.RATE_CATEGORY='05' THEN 'KJ' WHEN h.RATE_CATEGORY='06' THEN 'GPS' WHEN h.RATE_CATEGORY='22' THEN 'SPP' ELSE 'NIL' END TARIFF,h.DIV," +
                " h.SUB_DIV,h.SECTION,c.METER_NO,c.METER_TYP, CASE WHEN c.MF >= 1 THEN c.MF ELSE 1 END AS MF, CASE WHEN h.CONSUMER_OWNED='C' THEN 'CONSUMER' " +
                " WHEN c.METER_NO IS NULL THEN ' ' ELSE 'CESU' END AS CONSUMER_OWNED ,c.NO_OF_DIGITS,h.METER_MAKE,h.USAGE,h.SAN_LOAD,c.PREV_MTR_READ,h.MOVE_IN_DATE, " +
                " strftime('%d-%m-%Y',c.PREV_READ_DATE),c.METER_CONDITION,h.DPS,h.MISC_CHARGES,h.CR_ADJ,h.DB_ADJ,h.PRV_BILLED_AMT,h.HL_UNIT,h.PREVIOUS_BILLED_PROV_UNIT,h.LAST_PAID_DATE, " +
                " h.LAST_PYMT_RCPT,h.LAST_PAID_AMT,c.CAL_MON_CNT,h.ED_EXEMPT,h.NEW_METER_NO,c.LAST_OK_RDNG,c.METER_INSTALL_DATE,h.SDI,h.ASD,h.ASDAA,h.INSTALLATION, " +
                " h.BILL_NO,h.CA,h.BILL_PRN_HEADER,h.BILL_PRN_FOOTER,h.PRV_ARR,h.ARREARS,h.ULF,h.PREV_BILL_UNITS,h.BILL_MONTH,h.ECS_LIMT, " +
                " strftime('%d-%m-%Y',h.ECS_VALIDITY_PERIOD), " +
                " h.PRESENT_READING_REMARK,h.PRESENT_METER_STATUS,h.PRESENT_BILL_UNITS,h.BILL_BASIS,h.EC,h.MMFC,h.METER_RENT,h.ED,h.CURRENT_BILL_TOTAL,h.REBATE,h.AMOUNT_PAYABLE,h.AVG_UNIT_BILLED,h.RCPTNO, " +
                " h.CHQNO,h.CHQDT,h.BANK,h.RCPTAMT,strftime('%d-%m-%Y',h.DUE_DATE),h.DO_EXPIRY,h.PRESENT_READING_TIME,strftime('%d-%m-%Y',h.OSBILL_DATE),c.BILLED_MD, " +
                " c.PRS_MD,READ_FLAG, CASE WHEN strftime('%d-%m-%Y',h.OSBILL_DATE)<strftime('%d-%m-%Y',h.ECS_VALIDITY_PERIOD) THEN 1  ELSE 0 END AS ECSMSG, " +
                " SENT_FLAG,h.UNIT_SLAB1,h.RATE_SLAB1,h.EC_SLAB1,h.UNIT_SLAB2,h.RATE_SLAB2,h.EC_SLAB2,h.UNIT_SLAB3,h.RATE_SLAB3,h.EC_SLAB3,h.UNIT_SLAB4, " +
                " h.RATE_SLAB4,h.EC_SLAB4 FROM TBL_SPOTBILL_HEADER_DETAILS h, TBL_SPOTBILL_CHILD_DETAILS c WHERE h.INSTALLATION = c.INSTALLATION and h.INSTALLATION='9000000184'";

        //selectQuery = selectQuery + param;
        Log.e("selectQuery", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<String> list = new ArrayList<>();
        list.clear();
        if (cursor.moveToFirst()) {
            do {
                nSBMDataDatamodel = new NSBMData();
                nSBMDataDatamodel.setLEGACY_ACCOUNT_NO(cursor.getString(cursor.getColumnIndex("LEGACY_ACCOUNT_NO")));

                nSBMDataDatamodel.setListRegisterCoe(list);

            } while (cursor.moveToNext());
        }
        db.close();
        return nSBMDataDatamodel;
    }

   */
/* public static Cursor getBillParameterdetails(String Param, Context context) {
        try {
            copyDataBase1();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //SQLiteDatabase db = this.getReadableDatabase();
        String query = "Param";
        System.out.println("Query::" + query);
        Cursor cursor = db1.rawQuery(query, null);
        return cursor;
    }*//*


    public Cursor getCalculateedData(String query) {


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void deleletTariffTemp(String strSelectSQL_03) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(strSelectSQL_03);
        //db.close();
    }

    public void updateSlab(String strUpdateSQL_01) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(strUpdateSQL_01);
    }

    public void updateFieldDesc(String strUpdateSQL_02) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(strUpdateSQL_02);
    }

    public void updateMTRCONTD(String strUpdateSQL_01) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(strUpdateSQL_01);
    }

    public void insertIntoTEMPTARIFF(String strSQL) {


        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(strSQL);
    }


    public NSBMData getBillingData(String installationno) {
        NSBMData nSBMDataDatamodel = null;

        String selectQuery = "select PRESENT_BILL_UNITS,CURRENT_BILL_TOTAL,BILL_BASIS,AMOUNT_PAYABLE from TBL_SPOTBILL_HEADER_DETAILS where INSTALLATION='" + installationno + "'";

        //selectQuery = selectQuery + param;
        Log.e("selectQuery", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<String> list = new ArrayList<>();
        list.clear();
        if (cursor.moveToFirst()) {
            do {
                nSBMDataDatamodel = new NSBMData();
                nSBMDataDatamodel.setLEGACY_ACCOUNT_NO(cursor.getString(cursor.getColumnIndex("LEGACY_ACCOUNT_NO")));

                nSBMDataDatamodel.setListRegisterCoe(list);

            } while (cursor.moveToNext());
        }
        db.close();
        return nSBMDataDatamodel;
    }

}*/
