/*
package com.tpcodl.billingreading.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tpcodl.billingreading.models.MeterOkNonSbmReadingModel;
import com.tpcodl.billingreading.models.NSBMData;
import com.tpcodl.billingreading.models.PaperModal;
import com.tpcodl.billingreading.models.PrinterModal;
import com.tpcodl.billingreading.reponseModel.bollingModel.BillingResponseModel;
import com.tpcodl.billingreading.reponseModel.bollingModel.ChildDetail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelperBackup extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static String DB_NAME = "SMRD_12_11_2020.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public DatabaseHelperBackup(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17) {

            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

          }

        else {

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

    public NSBMData getNBSMData(String param) {
        NSBMData nSBMDataDatamodel = null;
        String installation = "";

        installation = getInstallatioationNo(param);

    */
/*String selectQuery1 = "select INSTALLATION from TBL_SPOTBILL_HEADER_DETAILS";
    selectQuery1 = selectQuery1 + param;*//*


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

                Log.e("ReadFlag", cursor.getString(cursor.getColumnIndex("READ_FLAG")));
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
        installation = getInstallatioationNo(param);
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

        ArrayList<PrinterModal>printerModals=new ArrayList<>();

         PrinterModal printerModal=null;

        String strSelectSQL_02 = "SELECT PRINTER_NAME,PRINTER_VAL,PRINTERSET_FLG "+
                "FROM MST_PRINTERTYPE";

        //selectQuery = selectQuery + param;
        Log.e("selectQuery", strSelectSQL_02);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = db.rawQuery(strSelectSQL_02, null);

        if (cursor1.moveToFirst()) {
            do {
                String printerid = cursor1.getString(1);
                String printername = cursor1.getString(0);

                printerModal=new PrinterModal();

                printerModal.setPrinterId(printerid);
                printerModal.setPrinterName(printername);
                printerModals.add(printerModal);

            }
            while (cursor1.moveToNext());
        }
        db.close();
        return printerModals;
    }
    public String getPreviousPrinterType(){
        String printerName="";

        String strSelectSQL_01 = "SELECT PRINTER_NAME,PRINTER_VAL,PRINTERSET_FLG "+
        "FROM MST_PRINTERTYPE WHERE PRINTER_STS=1 AND PRINTERSET_FLG=1";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSelectSQL_01, null);
        String printtype = "";
        while (cursor.moveToNext()) {
            printerName= cursor.getString(0);


        }

        db.close();

        return printerName;
    }

    public int getSMPVR(String username){
        int printerName=-1;


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

    public String getPreviousPaperType(){
        String paperName="";

        String strSelectSQL_01 = "SELECT PAPER_NAME,PAPER_VAL,PAPERSET_FLG "+
                "FROM MST_PAPERTYPE WHERE PAPER_STS=1 AND PAPERSET_FLG=1";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_01);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSelectSQL_01, null);
        String printtype = "";
        while (cursor.moveToNext()) {
            paperName= cursor.getString(0);


        }

        db.close();

        return paperName;
    }

    public ArrayList<PaperModal> getPaperType() {

        ArrayList<PaperModal>paperModals=new ArrayList<>();

        PaperModal paperModal=null;

        String strSelectSQL_02 = "SELECT PAPER_NAME,PAPER_VAL,PAPERSET_FLG "+
                "FROM MST_PAPERTYPE";

        //selectQuery = selectQuery + param;
        Log.e("selectQuery", strSelectSQL_02);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = db.rawQuery(strSelectSQL_02, null);

        if (cursor1.moveToFirst()) {
            do {
                String paperId = cursor1.getString(1);
                String papername = cursor1.getString(0);

                paperModal=new PaperModal();

                paperModal.setPaperId(paperId);
                paperModal.setPaperName(papername);
                paperModals.add(paperModal);

            }
            while (cursor1.moveToNext());
        }
        db.close();
        return paperModals;
    }





    public void resetPreviousPrinter(){
        String printerName="";

        String strSelectSQL_01 = "UPDATE MST_PRINTERTYPE SET PRINTERSET_FLG=0 ";
        Log.d("DemoApp", "strSelectSQL_01" + strSelectSQL_01);

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_01);

    }
    public void resetPrinter(int strprintid ){
        String printerName="";

        String strSelectSQL_02 = "UPDATE MST_PRINTERTYPE SET PRINTERSET_FLG=1 WHERE PRINTER_VAL='" + strprintid + "' ";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_02);

    }


    public void resetPreviousPapar(){
        String printerName="";

        String strSelectSQL_01 = "UPDATE MST_PAPERTYPE SET PAPERSET_FLG=0 ";
        Log.d("DemoApp", "strSelectSQL_01" + strSelectSQL_01);

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_01);

    }
    public void resetPaper(int strprintid ){
        String printerName="";

        String strSelectSQL_02 = "UPDATE MST_PAPERTYPE SET PAPERSET_FLG=1 WHERE PAPER_VAL='" + strprintid + "' ";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_02);

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_02);

    }

    public void setSAUser(int strprintid , String Usernm){
        String printerName="";

        String strSelectSQL_03 = "UPDATE _SA_User_old_20200911 SET SBMPRV='" + strprintid + "' WHERE userid = '" + Usernm + "' ";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_03);

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_03);

    }

    public String getInstallatioationNo(String param) {
        String selectQuery = "";
        String installationno = "";
        if (param.contains("METER_NO")) {
            selectQuery = "select INSTALLATION from TBL_SPOTBILL_CHILD_DETAILS";
        } else {
            selectQuery = "select INSTALLATION from TBL_SPOTBILL_HEADER_DETAILS";
        }

        selectQuery = selectQuery + param;
        Log.e("selectQuery", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                installationno = cursor.getString(cursor.getColumnIndex("INSTALLATION"));
            } while (cursor.moveToNext());
        }
        db.close();
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

                "COLONY_CONSUMPTION='" + meterOkNonSbmReadingModel.getSt_colony_consumption() + "'," +
                "READ_FLAG='" + 1 + "'";
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
                    "PRESENT_READING_TIME='" +meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                    "READ_FLAG='" + 1 + "'";
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
                    "READ_FLAG='" + 1 + "'";
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
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 1 + "'";

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
                        "READ_FLAG='" + 1 + "'";

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

                "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                "READ_FLAG='" + 1 + "'";
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

                        "READ_FLAG='" + 1 + "'";
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
                        "READ_FLAG='" + 1 + "'";
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

                        "READ_FLAG='" + 1 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        */
/*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*//*

        db.execSQL(sqlquery);
        db.close();
    }

    public void uosertOkNonSbmReadingCHILD(String value, String keys, String insta) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlquery = "UPDATE TBL_SPOTBILL_CHILD_DETAILS SET " +
                "PRESENT_METER_READING='" + value + "' where REGISTER_CODE='" + keys + "' and INSTALLATION='" + insta + "'";
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
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 1 + "'";

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
                        "SUPPLY_SOURCE_ID='" + meterOkNonSbmReadingModel.getSourceOfSupply() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 1 + "'";

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
                        "METER_TYPE_ID='" + meterOkNonSbmReadingModel.getSt_mtrtype() + "'," +
                        "REASON_MTR_STUCK_ID='" + meterOkNonSbmReadingModel.getResonMeterStuck() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" +meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 1 + "'";

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
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PAPER_PASTE_BY_ID='" + meterOkNonSbmReadingModel.getIsPaperBill() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 1 + "'";

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
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 1 + "'";

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
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 1 + "'";

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
                        "READ_FLAG='" + 1 + "'";

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
                        "READ_FLAG='" + 1 + "'";

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
                        "READ_FLAG='" + 1 + "'";
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
                        "READ_FLAG='" + 1 + "'";
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
                        "READ_FLAG='" + 1 + "'";

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
                        "READ_FLAG='" + 1 + "'";

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
                        "READ_FLAG='" + 1 + "'";

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
                        "READ_FLAG='" + 1 + "'";

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
    public void inserBillingHeader(BillingResponseModel response) {
        String headerNotInstall="";
        List<String>instHeaderNotInsert=new ArrayList<>();
        try {
            long rowInserted = 0;
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();


            for (int i = 0; i < response.response.size(); i++) {

                headerNotInstall=response.response.get(i).headerDetails.installation;


                if (response.response.get(i).headerDetails.legacyAccountNo != null) {
                    values.put(DatabaseKeys.Key_LEGACY_ACCOUNT_NO, response.response.get(i).headerDetails.legacyAccountNo);
                }
                if (response.response.get(i).headerDetails.name != null) {
                    values.put(DatabaseKeys.Key_NAME, response.response.get(i).headerDetails.name);
                }
                if (response.response.get(i).headerDetails.address1 != null) {
                    values.put(DatabaseKeys.Key_ADDRESS1, response.response.get(i).headerDetails.address1);
                }
                if (response.response.get(i).headerDetails.address2 != null) {
                    values.put(DatabaseKeys.Key_ADDRESS2, response.response.get(i).headerDetails.address2);
                }
                if (response.response.get(i).headerDetails.seq != null) {
                    values.put(DatabaseKeys.Key_SEQ, response.response.get(i).headerDetails.seq.toString());
                }
                if (response.response.get(i).headerDetails.subSeq != null) {
                    values.put(DatabaseKeys.Key_SUB_SEQ, response.response.get(i).headerDetails.subSeq.toString());
                }
                if (response.response.get(i).headerDetails.legacyAccountNo2 != null) {
                    values.put(DatabaseKeys.Key_LEGACY_ACCOUNT_NO2, response.response.get(i).headerDetails.legacyAccountNo2);
                }

                if (response.response.get(i).headerDetails.installation != null) {
                    values.put(DatabaseKeys.Key_INSTALLATION, response.response.get(i).headerDetails.installation);
                }
                if (response.response.get(i).headerDetails.rateCategory != null) {
                    values.put(DatabaseKeys.Key_RATE_CATEGORY, response.response.get(i).headerDetails.rateCategory);
                }
                if (response.response.get(i).headerDetails.div != null) {
                    values.put(DatabaseKeys.Key_DIV, response.response.get(i).headerDetails.div);
                }
                if (response.response.get(i).headerDetails.subDiv != null) {
                    values.put(DatabaseKeys.Key_SUB_DIV, response.response.get(i).headerDetails.subDiv);
                }
                if (response.response.get(i).headerDetails.section != null) {
                    values.put(DatabaseKeys.Key_SECTION, response.response.get(i).headerDetails.section.toString());
                }
                if (response.response.get(i).headerDetails.consumerOwned != null) {
                    values.put(DatabaseKeys.Key_CONSUMER_OWNED, response.response.get(i).headerDetails.consumerOwned.toString());
                }
                if (response.response.get(i).headerDetails.meterMake != null) {
                    values.put(DatabaseKeys.Key_METER_MAKE, response.response.get(i).headerDetails.meterMake.toString());
                }
                if (response.response.get(i).headerDetails.usage != null) {
                    values.put(DatabaseKeys.Key_USAGE, response.response.get(i).headerDetails.usage.toString());
                }
                if (response.response.get(i).headerDetails.sanLoad != null) {
                    values.put(DatabaseKeys.Key_SAN_LOAD, response.response.get(i).headerDetails.sanLoad.toString());
                }
                if (response.response.get(i).headerDetails.moveInDate != null) {
                    values.put(DatabaseKeys.Key_MOVE_IN_DATE, response.response.get(i).headerDetails.moveInDate.toString());
                }
                if (response.response.get(i).headerDetails.dps != null) {
                    values.put(DatabaseKeys.Key_DPS, response.response.get(i).headerDetails.dps.toString());
                }
                if (response.response.get(i).headerDetails.miscCharges != null) {
                    values.put(DatabaseKeys.Key_MISC_CHARGES, response.response.get(i).headerDetails.miscCharges.toString());
                }
                if (response.response.get(i).headerDetails.crAdj != null) {
                    values.put(DatabaseKeys.Key_CR_ADJ, response.response.get(i).headerDetails.crAdj.toString());
                }
                if (response.response.get(i).headerDetails.dbAdj != null) {
                    values.put(DatabaseKeys.Key_DB_ADJ, response.response.get(i).headerDetails.dbAdj.toString());
                }
                if (response.response.get(i).headerDetails.prvBilledAmt != null) {
                    values.put(DatabaseKeys.Key_PRV_BILLED_AMT, response.response.get(i).headerDetails.prvBilledAmt.toString());
                }
                if (response.response.get(i).headerDetails.previousBilledProvUnit != null) {
                    values.put(DatabaseKeys.Key_PREVIOUS_BILLED_PROV_UNIT, response.response.get(i).headerDetails.previousBilledProvUnit.toString());
                }
                if (response.response.get(i).headerDetails.lastPaidDate != null) {
                    values.put(DatabaseKeys.Key_LAST_PAID_DATE, response.response.get(i).headerDetails.lastPaidDate.toString());
                }
                if (response.response.get(i).headerDetails.lastPymtRcpt != null) {
                    values.put(DatabaseKeys.Key_LAST_PYMT_RCPT, response.response.get(i).headerDetails.lastPymtRcpt.toString());
                }
                if (response.response.get(i).headerDetails.lastPaidAmt != null) {
                    values.put(DatabaseKeys.Key_LAST_PAID_AMT, response.response.get(i).headerDetails.lastPaidAmt.toString());
                }
                if (response.response.get(i).headerDetails.edExempt != null) {
                    values.put(DatabaseKeys.Key_ED_EXEMPT, response.response.get(i).headerDetails.edExempt.toString());
                }
                if (response.response.get(i).headerDetails.aifi != null) {
                    values.put(DatabaseKeys.Key_AIFI, response.response.get(i).headerDetails.aifi.toString());
                }
                if (response.response.get(i).headerDetails.newMeterNo != null) {
                    values.put(DatabaseKeys.Key_NEW_METER_NO, response.response.get(i).headerDetails.newMeterNo.toString());
                }
                if (response.response.get(i).headerDetails.sdi != null) {
                    values.put(DatabaseKeys.Key_SDI, response.response.get(i).headerDetails.sdi.toString());
                }
                if (response.response.get(i).headerDetails.asd != null) {
                    values.put(DatabaseKeys.Key_ASD, response.response.get(i).headerDetails.asd.toString());
                }
                if (response.response.get(i).headerDetails.asdaa != null) {
                    values.put(DatabaseKeys.Key_ASDAA, response.response.get(i).headerDetails.asdaa.toString());
                }
                if (response.response.get(i).headerDetails.sbmBillNo != null) {
                    values.put(DatabaseKeys.Key_SBM_BILL_NO, response.response.get(i).headerDetails.sbmBillNo.toString());
                }
                if (response.response.get(i).headerDetails.ca != null) {
                    values.put(DatabaseKeys.Key_CA, response.response.get(i).headerDetails.ca.toString());
                }
                if (response.response.get(i).headerDetails.prvArr != null) {
                    values.put(DatabaseKeys.Key_PRV_ARR, response.response.get(i).headerDetails.prvArr.toString());
                }
                if (response.response.get(i).headerDetails.arrears != null) {
                    values.put(DatabaseKeys.Key_ARREARS, response.response.get(i).headerDetails.arrears.toString());
                }
                if (response.response.get(i).headerDetails.ulf != null) {
                    values.put(DatabaseKeys.Key_ULF, response.response.get(i).headerDetails.ulf.toString());
                }
                if (response.response.get(i).headerDetails.prevBillUnits != null) {
                    values.put(DatabaseKeys.Key_PREV_BILL_UNITS, response.response.get(i).headerDetails.prevBillUnits.toString());
                }
                if (response.response.get(i).headerDetails.ecsLimt != null) {
                    values.put(DatabaseKeys.Key_ECS_LIMT, response.response.get(i).headerDetails.ecsLimt.toString());
                }
                if (response.response.get(i).headerDetails.ecsValidityPeriod != null) {
                    values.put(DatabaseKeys.Key_ECS_VALIDITY_PERIOD, response.response.get(i).headerDetails.ecsValidityPeriod.toString());
                }
                if (response.response.get(i).headerDetails.presentReadingRemark != null) {
                    values.put(DatabaseKeys.Key_PRESENT_READING_REMARK, response.response.get(i).headerDetails.presentReadingRemark.toString());
                }
                if (response.response.get(i).headerDetails.presentMeterStatus != null) {
                    values.put(DatabaseKeys.Key_PRESENT_METER_STATUS, response.response.get(i).headerDetails.presentMeterStatus.toString());
                }
                if (response.response.get(i).headerDetails.presentBillUnits != null) {
                    values.put(DatabaseKeys.Key_PRESENT_BILL_UNITS, response.response.get(i).headerDetails.presentBillUnits.toString());
                }
                if (response.response.get(i).headerDetails.presentBillType != null) {
                    values.put(DatabaseKeys.Key_PRESENT_BILL_TYPE, response.response.get(i).headerDetails.presentBillType.toString());
                }
                if (response.response.get(i).headerDetails.ec != null) {
                    values.put(DatabaseKeys.Key_EC, response.response.get(i).headerDetails.ec.toString());
                }

                if (response.response.get(i).headerDetails.mmfc != null) {
                    values.put(DatabaseKeys.Key_MMFC, response.response.get(i).headerDetails.mmfc.toString());
                }

                if (response.response.get(i).headerDetails.mrentCharged != null) {
                    values.put(DatabaseKeys.Key_MRENT_CHARGED, response.response.get(i).headerDetails.mrentCharged.toString());
                }

                if (response.response.get(i).headerDetails.ed != null) {
                    values.put(DatabaseKeys.Key_ED, response.response.get(i).headerDetails.ed.toString());
                }

                if (response.response.get(i).headerDetails.currentBillTotal != null) {
                    values.put(DatabaseKeys.Key_CURRENT_BILL_TOTAL, response.response.get(i).headerDetails.currentBillTotal.toString());
                }

                if (response.response.get(i).headerDetails.rebate != null) {
                    values.put(DatabaseKeys.Key_REBATE, response.response.get(i).headerDetails.rebate.toString());
                }

                if (response.response.get(i).headerDetails.amountPayable != null) {
                    values.put(DatabaseKeys.Key_AMOUNT_PAYABLE, response.response.get(i).headerDetails.amountPayable.toString());
                }

                if (response.response.get(i).headerDetails.avgUnitBilled != null) {
                    values.put(DatabaseKeys.Key_AVG_UNIT_BILLED, response.response.get(i).headerDetails.avgUnitBilled.toString());
                }

                if (response.response.get(i).headerDetails.rcptno != null) {
                    values.put(DatabaseKeys.Key_RCPTNO, response.response.get(i).headerDetails.rcptno.toString());
                }

                if (response.response.get(i).headerDetails.chqno != null) {
                    values.put(DatabaseKeys.Key_CHQNO, response.response.get(i).headerDetails.chqno.toString());
                }

                if (response.response.get(i).headerDetails.chqdt != null) {
                    values.put(DatabaseKeys.Key_CHQDT, response.response.get(i).headerDetails.chqdt.toString());
                }

                if (response.response.get(i).headerDetails.bank != null) {
                    values.put(DatabaseKeys.Key_BANK, response.response.get(i).headerDetails.bank.toString());
                }

                if (response.response.get(i).headerDetails.rcptamt != null) {
                    values.put(DatabaseKeys.Key_RCPTAMT, response.response.get(i).headerDetails.rcptamt.toString());
                }

                if (response.response.get(i).headerDetails.dueDate != null) {
                    values.put(DatabaseKeys.Key_DUE_DATE, response.response.get(i).headerDetails.dueDate.toString());
                }

                if (response.response.get(i).headerDetails.doExpiry != null) {
                    values.put(DatabaseKeys.Key_DO_EXPIRY, response.response.get(i).headerDetails.doExpiry.toString());
                }

                if (response.response.get(i).headerDetails.presentReadingTime != null) {
                    values.put(DatabaseKeys.Key_PRESENT_READING_TIME, response.response.get(i).headerDetails.presentReadingTime.toString());
                }

                if (response.response.get(i).headerDetails.osbillDate != null) {
                    values.put(DatabaseKeys.Key_OSBILL_DATE, response.response.get(i).headerDetails.osbillDate.toString());
                }
                if (response.response.get(i).headerDetails.capturedMobile != null) {
                    values.put(DatabaseKeys.Key_CAPTURED_MOBILE, response.response.get(i).headerDetails.capturedMobile.toString());
                }
                if (response.response.get(i).headerDetails.meterRent != null) {
                    values.put(DatabaseKeys.Key_METER_RENT, response.response.get(i).headerDetails.meterRent.toString());
                }
                if (response.response.get(i).headerDetails.portion != null) {
                    values.put(DatabaseKeys.Key_PORTION, response.response.get(i).headerDetails.portion);
                }
                if (response.response.get(i).headerDetails.mru != null) {
                    values.put(DatabaseKeys.Key_MRU, response.response.get(i).headerDetails.mru);
                }
                if (response.response.get(i).headerDetails.noOfReg != null) {
                    values.put(DatabaseKeys.Key_NO_OF_REG, response.response.get(i).headerDetails.noOfReg.toString());
                }


                if (!response.response.get(i).headerDetails.sanLoadUnits.equals(null)) {
                    values.put(DatabaseKeys.Key_SAN_LOAD_UNITS, response.response.get(i).headerDetails.sanLoadUnits);
                }
                if (response.response.get(i).headerDetails.sanLoadEffectiveDate != null) {
                    values.put(DatabaseKeys.Key_SAN_LOAD_EFFECTIVE_DATE, response.response.get(i).headerDetails.sanLoadEffectiveDate.toString());
                }
                if (response.response.get(i).headerDetails.supplyTypFlg != null) {
                    values.put(DatabaseKeys.Key_SUPPLY_TYP_FLG, response.response.get(i).headerDetails.supplyTypFlg.toString());
                }
                if (response.response.get(i).headerDetails.notinuseFlgEnddate != null) {
                    values.put(DatabaseKeys.Key_NOTINUSE_FLG_ENDDATE, response.response.get(i).headerDetails.notinuseFlgEnddate.toString());
                }
                if (response.response.get(i).headerDetails.otherFlgs != null) {
                    values.put(DatabaseKeys.Key_OTHER_FLGS, response.response.get(i).headerDetails.otherFlgs.toString());
                }
                if (response.response.get(i).headerDetails.edRbt != null) {
                    values.put(DatabaseKeys.Key_ED_RBT, response.response.get(i).headerDetails.edRbt.toString());
                }
                if (response.response.get(i).headerDetails.hostelRbt != null) {
                    values.put(DatabaseKeys.Key_HOSTEL_RBT, response.response.get(i).headerDetails.hostelRbt.toString());
                }
                if (response.response.get(i).headerDetails.prevBillType != null) {
                    values.put(DatabaseKeys.Key_PREV_BILL_TYPE, response.response.get(i).headerDetails.prevBillType.toString());
                }
                if (response.response.get(i).headerDetails.prevBillRemark != null) {
                    values.put(DatabaseKeys.Key_PREV_BILL_REMARK, response.response.get(i).headerDetails.prevBillRemark.toString());
                }
                if (response.response.get(i).headerDetails.prevBillEndDate != null) {
                    values.put(DatabaseKeys.Key_PREV_BILL_END_DATE, response.response.get(i).headerDetails.prevBillEndDate.toString());
                }
                if (response.response.get(i).headerDetails.lastNormalBillDate != null) {
                    values.put(DatabaseKeys.Key_LAST_NORMAL_BILL_DATE, response.response.get(i).headerDetails.lastNormalBillDate.toString());
                }
                if (response.response.get(i).headerDetails.presentReadingDate != null) {
                    values.put(DatabaseKeys.Key_PRESENT_READING_DATE, response.response.get(i).headerDetails.presentReadingDate.toString());
                }
                if (response.response.get(i).headerDetails.averageKwh != null) {
                    values.put(DatabaseKeys.Key_AVERAGE_KWH, response.response.get(i).headerDetails.averageKwh.toString());
                }

                if (response.response.get(i).headerDetails.billBasis != null) {
                    values.put(DatabaseKeys.Key_BILL_BASIS, response.response.get(i).headerDetails.billBasis.toString());
                }

                if (response.response.get(i).headerDetails.billNo != null) {
                    values.put(DatabaseKeys.Key_BILL_NO, response.response.get(i).headerDetails.billNo.toString());
                }

                if (response.response.get(i).headerDetails.invoiceNo != null) {
                    values.put(DatabaseKeys.Key_INVOICE_NO, response.response.get(i).headerDetails.invoiceNo.toString());
                }
                if (response.response.get(i).headerDetails.readOnly != null) {
                    values.put(DatabaseKeys.Key_READ_ONLY, response.response.get(i).headerDetails.readOnly.toString());
                }
                if (response.response.get(i).headerDetails.bpStartDate != null) {
                    values.put(DatabaseKeys.Key_BP_START_DATE, response.response.get(i).headerDetails.bpStartDate.toString());
                }
                if (response.response.get(i).headerDetails.bpEndDate != null) {
                    values.put(DatabaseKeys.Key_BP_END_DATE, response.response.get(i).headerDetails.bpEndDate.toString());
                }

                if (response.response.get(i).headerDetails.ppac != null) {
                    values.put(DatabaseKeys.Key_PPAC, response.response.get(i).headerDetails.ppac.toString());
                }

                if (response.response.get(i).headerDetails.roundOff != null) {
                    values.put(DatabaseKeys.Key_ROUND_OFF, response.response.get(i).headerDetails.roundOff.toString());
                }
                if (response.response.get(i).headerDetails.secDepositAmt != null) {
                    values.put(DatabaseKeys.Key_SEC_DEPOSIT_AMT, response.response.get(i).headerDetails.secDepositAmt.toString());
                }
                if (response.response.get(i).headerDetails.doGenerated != null) {
                    values.put(DatabaseKeys.Key_DO_GENERATED, response.response.get(i).headerDetails.doGenerated.toString());
                }
                if (response.response.get(i).headerDetails.notToBillAfter != null) {
                    values.put(DatabaseKeys.Key_NOT_TO_BILL_AFTER, response.response.get(i).headerDetails.notToBillAfter.toString());
                }

                if (response.response.get(i).headerDetails.insertDate != null) {
                    values.put(DatabaseKeys.Key_INSERT_DATE, ( response.response.get(i).headerDetails.insertDate.toString()));
                }
                if (response.response.get(i).headerDetails.insertTime != null) {
                    values.put(DatabaseKeys.Key_INSERT_TIME, response.response.get(i).headerDetails.insertTime.toString());
                }
                if (response.response.get(i).headerDetails.updateDate != null) {
                    values.put(DatabaseKeys.Key_UPDATE_DATE, response.response.get(i).headerDetails.updateDate.toString());
                }
                if (response.response.get(i).headerDetails.updateTime != null) {
                    values.put(DatabaseKeys.Key_UPDATE_TIME, response.response.get(i).headerDetails.updateTime.toString());
                }

                if (response.response.get(i).headerDetails.phone1 != null) {
                    values.put(DatabaseKeys.Key_PHONE_1, response.response.get(i).headerDetails.phone1);
                }
                if (response.response.get(i).headerDetails.phone2 != null) {
                    values.put(DatabaseKeys.Key_PHONE_2, response.response.get(i).headerDetails.phone2);
                }
                if (response.response.get(i).headerDetails.transType != null) {
                    values.put(DatabaseKeys.Key_TRANS_TYPE, response.response.get(i).headerDetails.transType.toString());
                }
                if (response.response.get(i).headerDetails.tdFlag != null) {
                    values.put(DatabaseKeys.Key_TD_FLAG, response.response.get(i).headerDetails.tdFlag.toString());
                }
                if (response.response.get(i).headerDetails.tdDate != null) {
                    values.put(DatabaseKeys.Key_TD_DATE, response.response.get(i).headerDetails.tdDate.toString());
                }
                if (response.response.get(i).headerDetails.ppi != null) {
                    values.put(DatabaseKeys.Key_PPI, response.response.get(i).headerDetails.ppi.toString());
                }
                if (response.response.get(i).headerDetails.prevProvAmt != null) {
                    values.put(DatabaseKeys.Key_PREV_PROV_AMT, response.response.get(i).headerDetails.prevProvAmt.toString());
                }
                if (response.response.get(i).headerDetails.gstRelevant1 != null) {
                    values.put(DatabaseKeys.Key_GST_RELEVANT1, response.response.get(i).headerDetails.gstRelevant1.toString());
                }
                if (response.response.get(i).headerDetails.gstRelevant2 != null) {
                    values.put(DatabaseKeys.Key_GST_RELEVANT2, response.response.get(i).headerDetails.gstRelevant2.toString());
                }
                if (response.response.get(i).headerDetails.gstRelevant3 != null) {
                    values.put(DatabaseKeys.Key_GST_RELEVANT3, response.response.get(i).headerDetails.gstRelevant3.toString());
                }
                if (response.response.get(i).headerDetails.gstRelevant4 != null) {
                    values.put(DatabaseKeys.Key_GST_RELEVANT4, response.response.get(i).headerDetails.gstRelevant4.toString());
                }
                if (response.response.get(i).headerDetails.eltStts != null) {
                    values.put(DatabaseKeys.Key_ELT_STTS, response.response.get(i).headerDetails.eltStts.toString());
                }
                if (response.response.get(i).headerDetails.sealStts != null) {
                    values.put(DatabaseKeys.Key_SEAL_STTS, response.response.get(i).headerDetails.sealStts.toString());
                }

                if (response.response.get(i).headerDetails.cblass != null) {
                    values.put(DatabaseKeys.Key_CBLASS, response.response.get(i).headerDetails.cblass.toString());
                }
                if (response.response.get(i).headerDetails.usageId != null) {
                    values.put(DatabaseKeys.Key_USAGE_ID, response.response.get(i).headerDetails.usageId.toString());
                }

                if (response.response.get(i).headerDetails.meterTypeId != null) {
                    values.put(DatabaseKeys.Key_METER_TYPE_ID, response.response.get(i).headerDetails.meterTypeId.toString());
                }

                if (response.response.get(i).headerDetails.supplyStatusId != null) {
                    values.put(DatabaseKeys.Key_SUPPLY_STATUS_ID, response.response.get(i).headerDetails.supplyStatusId.toString());
                }

                if (response.response.get(i).headerDetails.reasonDcId != null) {
                    values.put(DatabaseKeys.Key_REASON_DC_ID, response.response.get(i).headerDetails.reasonDcId.toString());
                }

                if (response.response.get(i).headerDetails.supplySourceId != null) {
                    values.put(DatabaseKeys.Key_SUPPLY_SOURCE_ID, response.response.get(i).headerDetails.supplySourceId.toString());
                }

                if (response.response.get(i).headerDetails.reasonNvId != null) {
                    values.put(DatabaseKeys.Key_REASON_NV_ID, response.response.get(i).headerDetails.reasonNvId.toString());
                }

                if (response.response.get(i).headerDetails.reasonCdId != null) {
                    values.put(DatabaseKeys.Key_REASON_CD_ID, response.response.get(i).headerDetails.reasonCdId.toString());
                }

                if (response.response.get(i).headerDetails.reasonEnId != null) {
                    values.put(DatabaseKeys.Key_REASON_EN_ID, response.response.get(i).headerDetails.reasonEnId.toString());
                }

                if (response.response.get(i).headerDetails.reasonMtrStuckId != null) {
                    values.put(DatabaseKeys.Key_REASON_MTR_STUCK_ID, response.response.get(i).headerDetails.reasonMtrStuckId.toString());
                }

                if (response.response.get(i).headerDetails.paperPasteById != null) {
                    values.put(DatabaseKeys.Key_PAPER_PASTE_BY_ID, response.response.get(i).headerDetails.paperPasteById.toString());
                }


                if (response.response.get(i).headerDetails.meterHeightId != null) {
                    values.put(DatabaseKeys.Key_METER_HEIGHT_ID, response.response.get(i).headerDetails.meterHeightId.toString());
                }

                if (response.response.get(i).headerDetails.typesObstacleId != null) {
                    values.put(DatabaseKeys.Key_TYPES_OBSTACLE_ID, response.response.get(i).headerDetails.typesObstacleId.toString());
                }

                if (response.response.get(i).headerDetails.sealStsId != null) {
                    values.put(DatabaseKeys.Key_SEAL_STS_ID, response.response.get(i).headerDetails.sealStsId.toString());
                }

                if (response.response.get(i).headerDetails.contactReasonId != null) {
                    values.put(DatabaseKeys.Key_CONTACT_REASON_ID, response.response.get(i).headerDetails.contactReasonId.toString());
                }

                if (response.response.get(i).headerDetails.reasonPlId != null) {
                    values.put(DatabaseKeys.Key_REASON_PL_ID, response.response.get(i).headerDetails.reasonPlId.toString());
                }

                if (response.response.get(i).headerDetails.hlMonths != null) {
                    values.put(DatabaseKeys.Key_HL_MONTHS, response.response.get(i).headerDetails.hlMonths.toString());
                }
                if (response.response.get(i).headerDetails.buildingDesc != null) {
                    values.put(DatabaseKeys.Key_BUILDING_DESC, response.response.get(i).headerDetails.buildingDesc);
                }
                if (response.response.get(i).headerDetails.buildingCode != null) {
                    values.put(DatabaseKeys.Key_BUILDING_CODE, response.response.get(i).headerDetails.buildingCode);
                }
                if (response.response.get(i).headerDetails.poleNo != null) {
                    values.put(DatabaseKeys.Key_POLE_NO, response.response.get(i).headerDetails.poleNo);
                }
                if (response.response.get(i).headerDetails.flag != null) {
                    values.put(DatabaseKeys.Key_FLAG, response.response.get(i).headerDetails.flag.toString());
                }
                if (response.response.get(i).headerDetails.specialRem != null) {
                    values.put(DatabaseKeys.Key_SPECIAL_REM, response.response.get(i).headerDetails.specialRem);
                }

                if (response.response.get(i).headerDetails.senderMobile != null) {
                    values.put(DatabaseKeys.Key_SENDER_MOBILE, response.response.get(i).headerDetails.senderMobile.toString());
                }

                if (response.response.get(i).headerDetails.consumerType != null) {
                    values.put(DatabaseKeys.Key_CONSUMER_TYPE, response.response.get(i).headerDetails.consumerType);
                }

                if (response.response.get(i).headerDetails.walkingSeqChk != null) {
                    values.put(DatabaseKeys.Key_WALKING_SEQ_CHK, response.response.get(i).headerDetails.walkingSeqChk.toString());
                }

                if (response.response.get(i).headerDetails.stopPaprBl != null) {
                    values.put(DatabaseKeys.Key_STOP_PAPR_BL, response.response.get(i).headerDetails.stopPaprBl.toString());
                }

                if (response.response.get(i).headerDetails.scheduleMeterReadDate != null) {
                    values.put(DatabaseKeys.Key_SCHEDULE_METER_READ_DATE, response.response.get(i).headerDetails.scheduleMeterReadDate.toString());
                }

                if (response.response.get(i).headerDetails.billMonth != null) {
                    values.put(DatabaseKeys.Key_BILL_MONTH, response.response.get(i).headerDetails.billMonth.toString());
                }

                if (response.response.get(i).headerDetails.billYear != null) {
                    values.put(DatabaseKeys.Key_BILL_YEAR, response.response.get(i).headerDetails.billYear.toString());
                }
                if (response.response.get(i).headerDetails.mrreason != null) {
                    values.put(DatabaseKeys.Key_MRREASON, response.response.get(i).headerDetails.mrreason.toString());
                }


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
            Log.e("Data Intertion Error", ex.getMessage());
        }
    }

    public void inserBillingChildData(BillingResponseModel response) {
        String installationNoNotInserted="";
        List<String>installNotInsertedChild=new ArrayList<>();
        try {
            long rowInserted = 0;

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            for (int j = 0; j < response.response.get(j).headerDetails.childDataList.size(); j++) {
                List<ChildDetail> mlist = response.response.get(j).headerDetails.childDataList;
                for (int i = 0; i < mlist.size(); i++) {
                    System.out.println(i + "    " + "Child data size::" + mlist.size());

                    installationNoNotInserted=mlist.get(i).installation;

                    if (mlist.get(i).ablbelnr != null) {
                        values.put(DatabaseKeys.childKey_ABLBELNR, mlist.get(i).ablbelnr.toString());
                    }
                    if (mlist.get(i).billMonth != null) {
                        values.put(DatabaseKeys.childKey_BILL_MONTH, mlist.get(i).billMonth.toString());
                    }
                    if (mlist.get(i).billYear != null) {
                        values.put(DatabaseKeys.childKey_BILL_YEAR, mlist.get(i).billYear.toString());
                    }
                    if (mlist.get(i).billedMd != null) {
                        values.put(DatabaseKeys.childKey_BILLED_MD, mlist.get(i).billedMd.toString());
                    }
                    if (mlist.get(i).consumptionOldMeter != null) {
                        values.put(DatabaseKeys.childKey_CONSUMPTION_OLD_METER, (mlist.get(i).consumptionOldMeter.toString()));
                    }
                    if (mlist.get(i).equipmentNo != null) {
                        values.put(DatabaseKeys.childKey_EQUIPMENT_NO, (mlist.get(i).equipmentNo.toString()));
                    }
                    if (mlist.get(i).installation != null) {
                        values.put(DatabaseKeys.childKey_INSTALLATION, (mlist.get(i).installation.toString()));
                    }
                    if (mlist.get(i).lastOkRdng != null) {
                        values.put(DatabaseKeys.childKey_LAST_OK_RDNG, mlist.get(i).lastOkRdng.toString());
                    }
                    if (mlist.get(i).meterCondition != null) {
                        values.put(DatabaseKeys.childKey_METER_CONDITION, mlist.get(i).meterCondition.toString());
                    }
                    if (mlist.get(i).meterInstallDate != null) {
                        values.put(DatabaseKeys.childKey_METER_INSTALL_DATE, (mlist.get(i).meterInstallDate.toString()));
                    }
                    if (mlist.get(i).meterNo != null) {
                        values.put(DatabaseKeys.childKey_METER_NO, mlist.get(i).meterNo);

                    }
                    if (mlist.get(i).meterRemovedOn != null) {
                        values.put(DatabaseKeys.childKey_METER_REMOVED_ON, (mlist.get(i).meterRemovedOn.toString()));
                    }
                    if (mlist.get(i).meterTyp != null) {
                        values.put(DatabaseKeys.childKey_METER_TYP, (mlist.get(i).meterTyp.toString()));
                    }
                    if (mlist.get(i).mf != null) {
                        values.put(DatabaseKeys.childKey_MF, mlist.get(i).mf.toString());
                    }
                    if (mlist.get(i).mrreason != null) {
                        values.put(DatabaseKeys.childKey_MRREASON, mlist.get(i).mrreason.toString());
                    }
                    if (mlist.get(i).newMeterFlg != null) {
                        values.put(DatabaseKeys.childKey_NEW_METER_FLG, mlist.get(i).newMeterFlg.toString());
                    }

                    if (mlist.get(i).noOfDigits != null) {
                        values.put(DatabaseKeys.childKey_NO_OF_DIGITS, mlist.get(i).noOfDigits.toString());
                    }
                    if (mlist.get(i).presentMeterReading != null) {
                        values.put(DatabaseKeys.childKey_PRESENT_METER_READING, mlist.get(i).presentMeterReading.toString());
                    }
                    if (mlist.get(i).prevMtrRead != null) {
                        values.put(DatabaseKeys.childKey_PREV_MTR_READ, mlist.get(i).prevMtrRead.toString());
                    }
                    if (mlist.get(i).prevMtrRead != null) {
                        values.put(DatabaseKeys.childKey_PREV_READ_DATE, (mlist.get(i).prevMtrRead.toString()));
                    }
                    if (mlist.get(i).previousMd != null) {
                        values.put(DatabaseKeys.childKey_PREVIOUS_MD, mlist.get(i).previousMd.toString());
                    }
                    if (mlist.get(i).prsMd != null) {
                        values.put(DatabaseKeys.childKey_PRS_MD, (mlist.get(i).prsMd.toString()));
                    }
                    if (mlist.get(i).registerCode != null) {
                        values.put(DatabaseKeys.childKey_REGISTER_CODE, mlist.get(i).registerCode.toString());
                    }
                    if (mlist.get(i).scheduleMeterReadDate != null) {
                        values.put(DatabaseKeys.childKey_SCHEDULE_METER_READ_DATE, (mlist.get(i).scheduleMeterReadDate.toString()));
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

            installNotInsertedChild.add(installationNoNotInserted);

            Log.e("Child Intertion Error", ex.getMessage());
        }
    }
    public Cursor getHeaderdetails() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseKeys.TABLE_HEADER , null);
        return cursor;
    }

    public Cursor getChilddetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseKeys.TABLE_CHILD , null);
        return cursor;
    }


    public void deleteDataHeader(String installationNo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "TBL_SPOTBILL_HEADER_DETAILS"+ " WHERE "+"INSTALLATION"+"='"+installationNo+"'");
        db.close();
    }

    public void deleteDataChild(String installationNo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "TBL_SPOTBILL_CHILD_DETAILS"+ " WHERE "+"INSTALLATION"+"='"+installationNo+"'");
        db.close();
    }

    public boolean isExistsHeader(String installationNo)
    {
        SQLiteDatabase sqldb = getWritableDatabase();
        String selectString = "SELECT INSTALLATION FROM " + "TBL_SPOTBILL_HEADER_DETAILS" + " WHERE " + "INSTALLATION" + " =?";
        Cursor cursor = sqldb.rawQuery(selectString, new String[] {installationNo});

        boolean isExist = false;

        if(cursor.moveToFirst()){
            isExist = true;
        }

        sqldb.close();
        return isExist;
    }

    public boolean isExistsChild(String installationNo)
    {
        SQLiteDatabase sqldb = getWritableDatabase();
        String selectString = "SELECT INSTALLATION FROM " + "TBL_SPOTBILL_CHILD_DETAILS" + " WHERE " + "INSTALLATION" + " =?";
        Cursor cursor = sqldb.rawQuery(selectString, new String[] {installationNo});

        boolean isExist = false;

        if(cursor.moveToFirst()){
            isExist = true;
        }

        sqldb.close();
        return isExist;
    }
}
*/
