package com.tpcodl.billingreading.database;
///QA CODE
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.dialog.CustomDialogWithTwoButton;
import com.tpcodl.billingreading.models.MeterOkNonSbmReadingModel;
import com.tpcodl.billingreading.models.NSBMData;
import com.tpcodl.billingreading.models.PaperModal;
import com.tpcodl.billingreading.models.PrinterModal;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.reponseModel.bollingModel.BillingResponseModel;
import com.tpcodl.billingreading.reponseModel.bollingModel.ChildDetail;
import com.tpcodl.billingreading.reponseModel.bollingModel.HeaderDetails;
import com.tpcodl.billingreading.utils.DialogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.tpcodl.billingreading.utils.AppUtils.cyclicLeftShift;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static String DB_NAME = "SMRD.db";
    private static String DB_PATH = "";
    private static String DB_PATH1 = "";
    public static String strInputFilePath = Environment.getExternalStorageDirectory() + "/cesuapp/input/";
    private static final int DB_VERSION = 2;
    private static SQLiteDatabase db1;
    private SQLiteDatabase mDataBase;
    private final Context mContext;
    //  private static Context mContext1 = null;
    private DialogUtils dUtils;
    int Headercount = 0;
    int ChildCount = 0;

    int HeaderChkCnt = 0;
    int ChildChkCnt = 0;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";


        }

        System.out.println("sdfg==" + DB_PATH);
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
                //Log.e(TAG, "createDatabase database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    public void open() {
        this.mDataBase = this.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.e("dbFile", dbFile + "   " + dbFile.exists());
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
        if (newVersion > oldVersion) {
            try {
                String query = "ALTER TABLE TBL_SPOTBILL_CHILD_DETAILS  ADD COLUMN READ_FLAG";
                db.execSQL(query);
            } catch (Exception e) {

            }
            try {
                String query1 = "CREATE TABLE IF NOT EXISTS TBL_SPOTBILL_CHILD_DETAILS_TEMP (ABLBELNR TEXT,METER_NO TEXT,METER_TYP TEXT,MF TEXT,NO_OF_DIGITS TEXT,PREV_MTR_READ TEXT,PREV_READ_DATE TEXT,METER_CONDITION TEXT,LAST_OK_RDNG TEXT,METER_INSTALL_DATE TEXT,INSTALLATION TEXT,BILL_MONTH TEXT,PRESENT_METER_READING TEXT,SCHEDULE_METER_READ_DATE TEXT,EQUIPMENT_NO TEXT,NEW_METER_FLG TEXT,REGISTER_CODE TEXT,METER_REMOVED_ON TEXT,CONSUMPTION_OLD_METER TEXT,BILL_YEAR TEXT,MRREASON TEXT,PREVIOUS_MD TEXT,BILLED_MD TEXT,PRS_MD TEXT,REFERENCE_DATE TEXT,CAL_MON_CNT TEXT,USER_TYPE TEXT,NO_OF_REG TEXT,TRANS_TYPE TEXT,READ_FLAG TEXT)";
                db.execSQL(query1);
            } catch (Exception e) {

            }

            String query2 = "CREATE TABLE IF NOT EXISTS TBL_SPOTBILL_HEADER_DETAILS_TEMP (LEGACY_ACCOUNT_NO TEXT,NAME TEXT,ADDRESS1 TEXT,ADDRESS2 TEXT,SEQ TEXT,SUB_SEQ TEXT,LEGACY_ACCOUNT_NO2 TEXT,RATE_CATEGORY TEXT,DIV TEXT,SUB_DIV TEXT,SECTION TEXT,CONSUMER_OWNED TEXT,METER_MAKE TEXT,USAGE TEXT,SAN_LOAD TEXT,MOVE_IN_DATE TEXT,DPS TEXT,MISC_CHARGES TEXT,CR_ADJ TEXT,DB_ADJ TEXT,PRV_BILLED_AMT TEXT,PREVIOUS_BILLED_PROV_UNIT TEXT,LAST_PAID_DATE TEXT,LAST_PYMT_RCPT TEXT,LAST_PAID_AMT TEXT,ED_EXEMPT TEXT,AIFI TEXT,NEW_METER_NO TEXT,SDI TEXT,ASD TEXT,ASDAA TEXT,INSTALLATION TEXT,SBM_BILL_NO TEXT,CA TEXT,PRV_ARR TEXT,ARREARS TEXT,ULF TEXT,PREV_BILL_UNITS TEXT,BILL_MONTH TEXT,ECS_LIMT TEXT,ECS_VALIDITY_PERIOD TEXT,PRESENT_READING_REMARK TEXT,PRESENT_METER_STATUS TEXT,PRESENT_BILL_UNITS TEXT,PRESENT_BILL_TYPE TEXT,EC TEXT,MMFC TEXT,MRENT_CHARGED TEXT,ED TEXT,CURRENT_BILL_TOTAL TEXT,REBATE TEXT,AMOUNT_PAYABLE TEXT,AVG_UNIT_BILLED TEXT,RCPTNO TEXT,CHQNO TEXT,CHQDT TEXT,BANK TEXT,RCPTAMT TEXT,DUE_DATE TEXT,DO_EXPIRY TEXT,PRESENT_READING_TIME TEXT,OSBILL_DATE TEXT,CAPTURED_MOBILE TEXT,SCHEDULE_METER_READ_DATE TEXT,METER_RENT TEXT,PORTION TEXT,MRU TEXT,NO_OF_REG TEXT,SAN_LOAD_UNITS TEXT,SAN_LOAD_EFFECTIVE_DATE TEXT,SUPPLY_TYP_FLG TEXT,NOTINUSE_FLG_ENDDATE TEXT,OTHER_FLGS TEXT,ED_RBT TEXT,HOSTEL_RBT TEXT,PREV_BILL_TYPE TEXT,PREV_BILL_REMARK TEXT,PREV_BILL_END_DATE TEXT,LAST_NORMAL_BILL_DATE TEXT,PRESENT_READING_DATE TEXT,AVERAGE_KWH TEXT,BILL_BASIS TEXT,BILL_NO TEXT,INVOICE_NO TEXT,READ_ONLY TEXT,BP_START_DATE TEXT,BP_END_DATE TEXT,PPAC TEXT,ROUND_OFF TEXT,SEC_DEPOSIT_AMT TEXT,DO_GENERATED TEXT,NOT_TO_BILL_AFTER TEXT,INSERT_DATE TEXT,INSERT_TIME TEXT,UPDATE_DATE TEXT,UPDATE_TIME TEXT,PROGRESSION_STATE TEXT,PHONE_1 TEXT,PHONE_2 TEXT,BILL_YEAR TEXT,TRANS_TYPE TEXT,TD_FLAG TEXT,TD_DATE TEXT,PPI TEXT,PREV_PROV_AMT TEXT,GST_RELEVANT1 TEXT,GST_RELEVANT2 TEXT,GST_RELEVANT3 TEXT,GST_RELEVANT4 TEXT,ELT_STTS TEXT,SEAL_STTS TEXT,CBLASS TEXT,HL_MONTHS TEXT,MRREASON TEXT,USAGE_ID TEXT,METER_TYPE_ID TEXT,SUPPLY_STATUS_ID TEXT,REASON_DC_ID TEXT,SUPPLY_SOURCE_ID TEXT,REASON_NV_ID TEXT,REASON_CD_ID TEXT,REASON_EN_ID TEXT,REASON_MTR_STUCK_ID TEXT,PAPER_PASTE_BY_ID TEXT,METER_HEIGHT_ID TEXT,TYPES_OBSTACLE_ID TEXT,SEAL_STS_ID TEXT,CONTACT_REASON_ID TEXT,REASON_PL_ID TEXT,BUILDING_DESC TEXT,BUILDING_CODE TEXT,POLE_NO TEXT,FLAG TEXT,SPECIAL_REM TEXT,GPS_LONGITUDE TEXT,GPS_LATITUDE TEXT,SENDER_MOBILE TEXT,CONSUMER_TYPE TEXT,WALKING_SEQ_CHK TEXT,METER_LOCA TEXT,MR_REMARK_DET TEXT,STOP_PAPR_BL TEXT,NEW_MTR_FLG TEXT,OLD_MTR_COR_FLG TEXT,UNSAFE_COND TEXT,READ_FLAG TEXT,SENT_FLAG TEXT,MTR_IMAGE TEXT,UPD_FLAG1 TEXT,UPD_FLAG2 TEXT,REVISIT_FLAG TEXT,UNIT_SLAB1 TEXT,RATE_SLAB1 TEXT,EC_SLAB1 TEXT,UNIT_SLAB2 TEXT,RATE_SLAB2 TEXT,EC_SLAB2 TEXT,UNIT_SLAB3 TEXT,RATE_SLAB3 TEXT,EC_SLAB3 TEXT,UNIT_SLAB4 TEXT,RATE_SLAB4 TEXT,EC_SLAB4 TEXT,BILL_PRN_FOOTER TEXT,BILL_PRN_HEADER TEXT,HL_UNIT TEXT,REFERENCE_DATE TEXT,NO_BILLED_MONTH TEXT,REF_MR_DATE TEXT,RURAL TEXT,SWJL_DH_FL TEXT,RCRD_LOAD TEXT,ULF_MDI TEXT,DPS_5 TEXT,DPS_BILLED TEXT,DPS_LVD TEXT,PROV_PPT_AMT TEXT,DPS_BLLD TEXT,FC_SLAB TEXT,USER_TYPE TEXT,PROV_ED TEXT,MOD_DATE TEXT,RQCFLG TEXT,BILL_START_DATE TEXT,BILL_END_DATE TEXT,DISCONNECTION_FLG TEXT,BILLED_MD TEXT,OFFICE_CODE TEXT,ADJ_BILL TEXT)";
            db.execSQL(query2);

        }
    }

    public NSBMData getNBSMData(String param, int offset) {
      //  Log.e("offsetVAL", "" + offset);
        NSBMData nSBMDataDatamodel = null;
        String installation = "";
        installation = getInstallatioationNo(param, offset);
        String USER_TYPE = "";

        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            USER_TYPE = "S";
        } else {
            USER_TYPE = "X";
        }
        String selectQuery = "";
        if (USER_TYPE.equalsIgnoreCase("S")) {
            selectQuery = "select * from TBL_SPOTBILL_HEADER_DETAILS a ,TBL_SPOTBILL_CHILD_DETAILS b " +
                    "where a.INSTALLATION=b.INSTALLATION  " + "and a.INSTALLATION='" + installation + "' and a.USER_TYPE='" + USER_TYPE + "' GROUP by b.REGISTER_CODE";
        } else {
            selectQuery = "select * from TBL_SPOTBILL_HEADER_DETAILS a ,TBL_SPOTBILL_CHILD_DETAILS b " +
                    "where a.INSTALLATION=b.INSTALLATION  " + "and a.INSTALLATION='" + installation + "' and a.USER_TYPE='" + USER_TYPE + "' GROUP by b.METER_NO,b.NO_OF_REG, b.REGISTER_CODE";
        }


        //Log.e("selectQuery", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();
        list.clear();
        list1.clear();
        if (cursor.moveToFirst()) {
            do {
                nSBMDataDatamodel = new NSBMData();
                nSBMDataDatamodel.setNO_OF_DIGITS(cursor.getString(cursor.getColumnIndex("NO_OF_DIGITS")));
                nSBMDataDatamodel.setSCHEDULE_METER_READ_DATE(cursor.getString(cursor.getColumnIndex("SCHEDULE_METER_READ_DATE")));
                nSBMDataDatamodel.setLEGACY_ACCOUNT_NO(cursor.getString(cursor.getColumnIndex("LEGACY_ACCOUNT_NO")));
                nSBMDataDatamodel.setMETER_CONDITION(cursor.getString(cursor.getColumnIndex("METER_CONDITION")));
                nSBMDataDatamodel.setNAME(cursor.getString(cursor.getColumnIndex("NAME")));
                nSBMDataDatamodel.setADDRESS1(cursor.getString(cursor.getColumnIndex("ADDRESS1")));
                nSBMDataDatamodel.setADDRESS2(cursor.getString(cursor.getColumnIndex("ADDRESS2")));
                nSBMDataDatamodel.setSEQ(cursor.getString(cursor.getColumnIndex("SEQ")));
                nSBMDataDatamodel.setSUB_SEQ(cursor.getString(cursor.getColumnIndex("SUB_SEQ")));
                nSBMDataDatamodel.setREF_MR_DATE(cursor.getString(cursor.getColumnIndex("REF_MR_DATE")));
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

                nSBMDataDatamodel.setMRU(cursor.getString(cursor.getColumnIndex("MRU")));
                nSBMDataDatamodel.setSPECIAL_REM(cursor.getString(cursor.getColumnIndex("SPECIAL_REM")));

                nSBMDataDatamodel.setPHONE_1(cursor.getString(cursor.getColumnIndex("PHONE_1")));
                nSBMDataDatamodel.setREVISIT_FLAG(cursor.getString(cursor.getColumnIndex("REVISIT_FLAG")));

                nSBMDataDatamodel.setREAD_FLAG(cursor.getString(cursor.getColumnIndex("READ_FLAG")));
                list.add(cursor.getString(cursor.getColumnIndex("REGISTER_CODE")));
                nSBMDataDatamodel.setListRegisterCoe(list);

                nSBMDataDatamodel.setMETER_NO(cursor.getString(cursor.getColumnIndex("METER_NO")));
                list1.add(cursor.getString(cursor.getColumnIndex("METER_NO")));
                nSBMDataDatamodel.setListMeterNo(list1);
            } while (cursor.moveToNext());
        }
        db.close();
        return nSBMDataDatamodel;
    }

    public NSBMData getNBSMReadData(String param) {
        NSBMData nSBMDataDatamodel = null;
        String installation = "";
        installation = getInstallatioationNo(param, -1);
        /*String selectQuery1 = "select INSTALLATION from TBL_SPOTBILL_HEADER_DETAILS";
        selectQuery1 = selectQuery1 + param;*/
        Map<String, String> LinkedHashMapValues = new LinkedHashMap<>();
        LinkedHashMapValues.clear();
       /* String selectQuery = "select * from TBL_SPOTBILL_HEADER_DETAILS h left join TBL_SPOTBILL_CHILD_DETAILS c " +
                "where h.INSTALLATION=c.INSTALLATION and c.INSTALLATION='" + installation + "'";*/
        String selectQuery = "select * from TBL_SPOTBILL_HEADER_DETAILS a ,TBL_SPOTBILL_CHILD_DETAILS b where a.INSTALLATION=b.INSTALLATION  " +
                "and a.INSTALLATION='" + installation + "' group by b.METER_NO,b.NO_OF_REG, b.REGISTER_CODE";
        //selectQuery = selectQuery + param;
        //Log.e("selectQuery", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<String> list = new ArrayList<>();
        list.clear();
        if (cursor.moveToFirst()) {
            do {
                nSBMDataDatamodel = new NSBMData();
                nSBMDataDatamodel.setNO_OF_DIGITS(cursor.getString(cursor.getColumnIndex("NO_OF_DIGITS")));
                nSBMDataDatamodel.setLEGACY_ACCOUNT_NO(cursor.getString(cursor.getColumnIndex("LEGACY_ACCOUNT_NO")));
                nSBMDataDatamodel.setNAME(cursor.getString(cursor.getColumnIndex("NAME")));
                nSBMDataDatamodel.setSCHEDULE_METER_READ_DATE(cursor.getString(cursor.getColumnIndex("SCHEDULE_METER_READ_DATE")));
                nSBMDataDatamodel.setMETER_CONDITION(cursor.getString(cursor.getColumnIndex("METER_CONDITION")));
                nSBMDataDatamodel.setADDRESS1(cursor.getString(cursor.getColumnIndex("ADDRESS1")));
                nSBMDataDatamodel.setADDRESS2(cursor.getString(cursor.getColumnIndex("ADDRESS2")));
                nSBMDataDatamodel.setSEQ(cursor.getString(cursor.getColumnIndex("SEQ")));
                nSBMDataDatamodel.setREF_MR_DATE(cursor.getString(cursor.getColumnIndex("REF_MR_DATE")));
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
                String Key = cursor.getString(cursor.getColumnIndex("REGISTER_CODE")) + "|" + cursor.getString(cursor.getColumnIndex("METER_NO"));

                LinkedHashMapValues.put(Key, cursor.getString(cursor.getColumnIndex("PRESENT_METER_READING")));
                nSBMDataDatamodel.setLinkedHashMapValues(LinkedHashMapValues);

                list.add(cursor.getString(cursor.getColumnIndex("REGISTER_CODE")));
                nSBMDataDatamodel.setListRegisterCoe(list);

            } while (cursor.moveToNext());
        }
        db.close();
        return nSBMDataDatamodel;
    }

    public List<NSBMData> getNBSMRInstallationOnly(String flag) {
        String selectQuery = "";
        //flag 2 is used for mass upload
        if (flag.equalsIgnoreCase("2")) {
            selectQuery = "SELECT INSTALLATION, MRREASON FROM TBL_SPOTBILL_HEADER_DETAILS where READ_FLAG='0' and MRREASON not null"; // use for mass upload not required
        } else {
            selectQuery = "SELECT INSTALLATION, MRREASON FROM TBL_SPOTBILL_HEADER_DETAILS where READ_FLAG='1' AND SENT_FLAG='0' and OSBILL_DATE IS NOT NULL";
        }

        NSBMData nSBMDataDatamodel = null;
        List<NSBMData> list = null;
        list = new ArrayList<>();
        //Log.e("selectQuery", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        list.clear();
        if (cursor.moveToFirst()) {
            do {
                nSBMDataDatamodel = new NSBMData();
                nSBMDataDatamodel.setINSTALLATION(cursor.getString(cursor.getColumnIndex("INSTALLATION")));
                nSBMDataDatamodel.setMRREASON(cursor.getString(cursor.getColumnIndex("MRREASON")));
                list.add(nSBMDataDatamodel);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public ArrayList<PrinterModal> getPrinterType() {

        ArrayList<PrinterModal> printerModals = new ArrayList<>();

        PrinterModal printerModal = null;

        String strSelectSQL_02 = "SELECT PRINTER_NAME,PRINTER_VAL,PRINTERSET_FLG " +
                "FROM MST_PRINTERTYPE";

        //selectQuery = selectQuery + param;
       // Log.e("selectQuery", strSelectSQL_02);

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
        String strUpdateSQL_01 = "SELECT SBMPRV FROM Mst_User WHERE userid = '" + username + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(strUpdateSQL_01, null);
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
        String strSelectSQL_03 = "UPDATE Mst_User SET SBMPRV='" + strprintid + "' WHERE userid = '" + Usernm + "'";
        Log.d("DemoApp", "strSelectSQL_02" + strSelectSQL_03);
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(strSelectSQL_03);
    }

    public String getInstallatioationNo(String param, int offset) {
        String selectQuery = "";
        String installationno = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String USER_TYPE = "";
        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            USER_TYPE = "S";
        } else {
            USER_TYPE = "X";
        }

        if (offset >= 0) {
            selectQuery = "select INSTALLATION from TBL_SPOTBILL_HEADER_DETAILS where READ_FLAG ='0' and USER_TYPE='" + USER_TYPE + "' order by LEGACY_ACCOUNT_NO ASC LIMIT 1 OFFSET " + offset + "";
        } else {
            if (param.contains("METER_NO")) {
                selectQuery = "select INSTALLATION from TBL_SPOTBILL_CHILD_DETAILS";
            } else {
                selectQuery = "select INSTALLATION from TBL_SPOTBILL_HEADER_DETAILS";
            }
            if (param.contains("where")) {
                selectQuery = selectQuery + param;
            }
            Log.e("Resultswwwwww", selectQuery);
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


    public String getInstallatioationNoBYCA(String param) {
        String selectQuery = "";
        String installationno = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String USER_TYPE = "";

        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            USER_TYPE = "S";
        } else {
            USER_TYPE = "X";
        }
        selectQuery = "select INSTALLATION from TBL_SPOTBILL_HEADER_DETAILS";

        if (param.contains("where")) {
            selectQuery = selectQuery + param + " AND USER_TYPE='" + USER_TYPE + "'";
        }
        Log.e("Resultswwwwww", selectQuery);

        Log.e("selectQueryOnlyINST", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                installationno = cursor.getString(cursor.getColumnIndex("INSTALLATION"));
            } while (cursor.moveToNext());
        }
        return installationno;
    }


    public void UpdateNpDC(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String param, String flag, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        String sqlquery = "";
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
        if (flag.equalsIgnoreCase("NP")) {
            sqlquery = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                    "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                    "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                    "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                    "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                    "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                    "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                    "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                    "MRREASON='" + "NP" + "'," +
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
                    "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                    "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                    "MRREASON='" + "DC" + "'," +
                    "READ_FLAG='" + 0 + "'";
        }
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }


    public void updateNMRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNMRemarksNonSb", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateENRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNMRemarksNonSb", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "REASON_EN_ID='" + meterOkNonSbmReadingModel.getReasonEN() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void uosertOkNonSbmReadingHeader(MeterOkNonSbmReadingModel
                                                    meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
        String sqlquery = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                /*"MD_PEAK='" + meterOkNonSbmReadingModel.getSt_md_peak() + "'," +
                "MD_OFFPEAK='" + meterOkNonSbmReadingModel.getSt_md_off_peak() + "'," +
                "READ_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                "TOD='" + meterOkNonSbmReadingModel.getSt_tod() + "'," +*/
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
                "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                //"OTHER_FLGS='" + meterOkNonSbmReadingModel.getOverriceQC() + "'," +
                "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateMMRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();

        Gson g = new Gson();
        Log.e("updateSBRemarksNonSbmR", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        //"OTHER_FLGS='" + meterOkNonSbmReadingModel.getOverriceQC() + "'," +
                        "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateFURemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateSBRemarksNonSbmR", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        //"OTHER_FLGS='" + meterOkNonSbmReadingModel.getOverriceQC() + "'," +
                        "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateGBRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateSBRemarksNonSbmR", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        // "OTHER_FLGS='" + meterOkNonSbmReadingModel.getOverriceQC() + "'," +
                        "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void uosertOkNonSbmReadingCHILD(String value, String keys, String installno, String mrReason) {
        //Log.e("Keys", keys);
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlquery = "UPDATE TBL_SPOTBILL_CHILD_DETAILS SET " +
                "PRESENT_METER_READING='" + value + "', " +
                "MRREASON='" + mrReason + "', " +
                "READ_FLAG='1' " +
                "where REGISTER_CODE='" + keys.substring(0, keys.indexOf("|")) + "' " +
                "and METER_NO='" + keys.substring(keys.indexOf("|") + 1) + "'" +
                "and INSTALLATION='" + installno + "'";
        //Log.e("METRETTTTTTeadasdada", keys.substring(0, keys.indexOf("|")) + ":" + keys.substring(keys.indexOf("|") + 1));
        //Log.e("updateQueryChild", sqlquery);
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateNVRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateMBRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateNDRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updatePPRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateMHRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateOBRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }


    public void updateSBRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        //"OTHER_FLGS='" + meterOkNonSbmReadingModel.getOverriceQC() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateMURemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("updateNDRemarksNonSbm", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
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
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        //"OTHER_FLGS='" + meterOkNonSbmReadingModel.getOverriceQC() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }


    public void updateBLRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        //  "CONTACT_REASON_ID='" + meterOkNonSbmReadingModel.getResonContact() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateTLRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        String READ_FLAG = "";

        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/

        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "CONTACT_REASON_ID='" + meterOkNonSbmReadingModel.getResonContact() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "READ_FLAG='" + 0 + "'";
        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }


    public void updatePLRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "REASON_PL_ID='" + meterOkNonSbmReadingModel.getReasonPl() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateSPRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +

                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "METER_LOCA='" + meterOkNonSbmReadingModel.getIsMeterlocation() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }

    public void updateWLRemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +

                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "WALKING_SEQ_CHK='" + meterOkNonSbmReadingModel.getIsSeqCorrect() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
        db.execSQL(sqlquery);
        db.close();
    }


    public void updateNARemarksNonSbmReading(MeterOkNonSbmReadingModel meterOkNonSbmReadingModel, String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson g = new Gson();
        Log.e("ObjectClass", g.toJson(meterOkNonSbmReadingModel));
        /*if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            READ_FLAG = "1";
        } else {
            READ_FLAG = "0";
        }*/
        String sqlquery =
                "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET " +
                        "USAGE_ID='" + meterOkNonSbmReadingModel.getSt_usages() + "'," +
                        "MR_REMARK_DET='" + meterOkNonSbmReadingModel.getSt_additional() + "'," +
                        "SUPPLY_STATUS_ID='" + meterOkNonSbmReadingModel.getSt_supplyStatus() + "'," +
                        "REVISIT_FLAG='" + meterOkNonSbmReadingModel.getIsRevisit() + "'," +
                        "PRESENT_READING_TIME='" + meterOkNonSbmReadingModel.getSt_currentdatetime() + "'," +
                        "MRREASON='" + meterOkNonSbmReadingModel.getMr_reason() + "'," +
                        "GPS_LATITUDE='" + meterOkNonSbmReadingModel.getGpsLatitude() + "'," +
                        "GPS_LONGITUDE='" + meterOkNonSbmReadingModel.getGpsLongitude() + "'," +
                        "READ_FLAG='" + 0 + "'";

        sqlquery = sqlquery + "where INSTALLATION='" + installationNo + "'";
        Log.e("updateQuery", sqlquery);
        /*Cursor c = db.rawQuery(sqlquery, null);
        c.moveToFirst();
        c.close();*/
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
        new InsertHeader(response).execute();
    }

    public void updateFieldDescTable(Map<String, String> linkedHashMapImages, NSBMData nSBMData, String Mrreason) {
        String UserType = "";
        SQLiteDatabase db = this.getWritableDatabase();
        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            UserType = "S";
        } else {
            UserType = "X";
        }
        Log.e("linkedHashMapImages", "" + linkedHashMapImages.size());
        for (Map.Entry<String, String> entry : linkedHashMapImages.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue() + "-" + Mrreason;
            //System.out.println("Key: " + k + ", Value: " + v);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.ENGLISH);
                Date date = new Date();
                String strSQL = "INSERT INTO TBL_FILE_DETAILS(FILE,FILE_FLAG,INSTALLATION,CA,BILL_MONTH,BILL_YEAR,SENT_FLAG,USER_TYPE,INSERT_DATE) " +
                        " VALUES('" + k + "','" + v + "','" + nSBMData.getINSTALLATION() + "','" + nSBMData.getCA() + "','" + nSBMData.getBILL_MONTH() + "'," +
                        " '" + nSBMData.getBILL_YEAR() + "','" + "0" + "','" + UserType + "','" + formatter.format(date) + "')";
                Log.e("InsertFlg", strSQL);
                db.execSQL(strSQL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkSentFlag() {
        boolean isExist = true;
        String count = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String selectString = "SELECT COUNT(1) FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG='1' AND SENT_FLAG='0'";
        Log.e("selectString", selectString);
        Cursor cursor = db.rawQuery(selectString, null);
        while (cursor.moveToNext()) {
            count = cursor.getString(0);
        }

        Log.e("COuntet", count);
        if (Integer.parseInt(count) > 0) {
            isExist = false;
        }
        cursor.close();
        return isExist;
    }

    public int checkBillingAllowedFlag(String installation, String MrRefDate,String SCHEDULE_METER_READ_DATE) {
        int isAllowed = 0;
        String count = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String selectString = "";

        try {
            if (MrRefDate == null || SCHEDULE_METER_READ_DATE.equalsIgnoreCase(MrRefDate)) {
                selectString = "Select DATE('now'), installation,insert_date,not_to_bill_after," +
                        "CASE WHEN DATE('now')>date(insert_date, '+' || (SELECT cast(not_to_bill_after As Integer) FROM " +
                        "tbl_spotbill_header_details where installation='" + installation + "') || ' day' ) AND cast(not_to_bill_after As Integer)!=0 then 0 ELSE 1 END blstopDt" +
                        " from TBL_SPOTBILL_HEADER_DETAILS where installation='" + installation + "'";
                Log.e("selectString1", selectString);
            } else {
                selectString = "SELECT REF_MR_DATE,INSTALLATION,INSTALLATION,INSTALLATION," +
                        "CASE WHEN date('now','localtime') <= '" + MrRefDate + "'" +
                        "THEN " +
                        "1 ELSE 0 " +
                        "END CHKVAL FROM " +
                        "TBL_SPOTBILL_HEADER_DETAILS where installation='" + installation + "'";
                Log.e("selectString2", selectString);
            }


            Cursor cursor = db.rawQuery(selectString, null);
            while (cursor.moveToNext()) {
                count = cursor.getString(4);
            }
            Log.e("COuntet", count);
            if (Integer.parseInt(count) > 0) {
                isAllowed = 1;
            }
            cursor.close();
        } catch (Exception e) {
            isAllowed = 1;
        }
        return isAllowed;
    }

    public class InsertHeader extends AsyncTask<Void, Integer, String> {
        private PreferenceHandler phandler;
        BillingResponseModel response;
        ProgressDialog progressDialog1;
        long rowInserted = 0;

        public InsertHeader(BillingResponseModel responses) {
            response = responses;
            Headercount = 0;
            Log.e("HeadercountVal", "" + Headercount);
            phandler = new PreferenceHandler(mContext);
        }

        @Override
        protected void onPreExecute() {
            progressDialog1 = new ProgressDialog(mContext);
            progressDialog1.setCancelable(false);
            progressDialog1.setMessage("Downloading data, please wait...");
            progressDialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog1.setProgress(0);
            progressDialog1.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String headerNotInstall = "";
            HashMap<String, ArrayList<String>> totalData = new HashMap<>();
            ArrayList<String> instHeaderNotInsert = new ArrayList<>();
            ArrayList<String> instChildNotInsert = new ArrayList<>();
            String senderMobile = PreferenceHandler.getisUserId(mContext);
            try {
                SQLiteDatabase db = getWritableDatabase();
                ContentValues values = new ContentValues();

                for (int i = 0; i < response.response.size(); i++) {
                    headerNotInstall = response.response.get(i).headerDetails.get(i).installation;
                    List<HeaderDetails> headrelist = response.response.get(i).headerDetails;
                    progressDialog1.setMax(headrelist.size());
                    Headercount = headrelist.size();

                    for (int j = 0; j < headrelist.size(); j++) {
                        publishProgress(j);
                        values.put(DatabaseKeys.Key_SENT_FLAG, "0");

                        String val = "";
                        val = headrelist.get(j).readOnly;

                        try {
                            if (!val.equalsIgnoreCase("X")) {
                                val = "S";
                            }
                        } catch (Exception e) {
                            val = "S";
                        }

                        System.out.println("user_type==" + val);
                        if (val != null) {
                            values.put(DatabaseKeys.Key_USER_TYPE, val);
                           /* if (headrelist.get(j).readOnly.equalsIgnoreCase("X")) {
                                values.put(DatabaseKeys.Key_USER_TYPE, "X");
                            } else {
                                values.put(DatabaseKeys.Key_USER_TYPE, "S");
                            }*/
                        } else {
                            values.put(DatabaseKeys.Key_USER_TYPE, val);
                        }


                        if (headrelist.get(j).readOnly != null) {
                            values.put(DatabaseKeys.Key_READ_ONLY, headrelist.get(j).readOnly.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_READ_ONLY, DatabaseKeys.EMPTY_STRING);
                        }



                      /*  if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                            values.put(DatabaseKeys.Key_USER_TYPE, "S");
                        } else {
                            values.put(DatabaseKeys.Key_USER_TYPE, "X");
                        }
*/
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HHmmss", Locale.ENGLISH);
                            Date date = new Date();
                            values.put("MOD_DATE", formatter.format(date));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        values.put("SENDER_MOBILE", senderMobile);
                        Log.e("senderMobile", senderMobile);
                       /* try {

                            Log.e("SENDER_MOBILE", PreferenceHandler.getisUserId(mContext));
                            //Log.e("DATEEEEFORMATTT", formatter.format(date));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                        //added by Santi
                        if (headrelist.get(j).fcSlab != null) {
                            values.put(DatabaseKeys.Key_FC_SLAB, headrelist.get(j).fcSlab.trim());
                        } else {
                            values.put(DatabaseKeys.Key_FC_SLAB, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).rural != null) {
                            values.put(DatabaseKeys.Key_RURAL, headrelist.get(j).rural.trim());
                        } else {
                            values.put(DatabaseKeys.Key_RURAL, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).swJlDhFl != null) {
                            values.put(DatabaseKeys.Key_SWJL_DH_FL, headrelist.get(j).swJlDhFl.trim());
                        } else {
                            values.put(DatabaseKeys.Key_SWJL_DH_FL, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).rcRdLoad != null) {
                            values.put(DatabaseKeys.Key_RCRD_LOAD, headrelist.get(j).rcRdLoad.trim());
                        } else {
                            values.put(DatabaseKeys.Key_RCRD_LOAD, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).ulfMdi != null) {
                            values.put(DatabaseKeys.Key_ULF_MDI, headrelist.get(j).ulfMdi.trim());
                        } else {
                            values.put(DatabaseKeys.Key_ULF_MDI, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).dps5 != null) {
                            values.put(DatabaseKeys.Key_DPS_5, headrelist.get(j).dps5.trim());
                        } else {
                            values.put(DatabaseKeys.Key_DPS_5, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).dpsBilled != null) {
                            values.put(DatabaseKeys.Key_DPS_BILLED, headrelist.get(j).dpsBilled.trim());
                        } else {
                            values.put(DatabaseKeys.Key_DPS_BILLED, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).dpsLvd != null) {
                            values.put(DatabaseKeys.Key_DPS_LVD, headrelist.get(j).dpsLvd.trim());
                        } else {
                            values.put(DatabaseKeys.Key_DPS_LVD, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).provPpiAmt != null) {
                            values.put(DatabaseKeys.Key_PROV_PPT_AMT, headrelist.get(j).provPpiAmt.trim());
                        } else {
                            values.put(DatabaseKeys.Key_PROV_PPT_AMT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).provEd != null) {
                            values.put(DatabaseKeys.Key_PROV_ED, headrelist.get(j).provEd.trim());
                        } else {
                            values.put(DatabaseKeys.Key_PROV_ED, DatabaseKeys.EMPTY_STRING);
                        }
                        //end
                        if (headrelist.get(j).refMrDate != null) {
                            values.put(DatabaseKeys.REF_MR_DATE, headrelist.get(j).refMrDate.trim());
                        } else {
                            values.put(DatabaseKeys.REF_MR_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).refDate != null) {
                            values.put(DatabaseKeys.REFERENCE_DATE, headrelist.get(j).refDate.trim());
                        } else {
                            values.put(DatabaseKeys.REFERENCE_DATE, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).officeCode != null) {
                            values.put(DatabaseKeys.Key_OFFICECODE, headrelist.get(j).officeCode.trim());
                        } else {
                            values.put(DatabaseKeys.Key_OFFICECODE, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).billPrintFooter != null) {
                            values.put(DatabaseKeys.Key_BILL_PRN_FOOTER, headrelist.get(j).billPrintFooter.trim());
                        } else {
                            values.put(DatabaseKeys.Key_BILL_PRN_FOOTER, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).billPrintHeader != null) {
                            values.put(DatabaseKeys.Key_BILL_PRN_HEADER, headrelist.get(j).billPrintHeader.trim());
                        } else {
                            values.put(DatabaseKeys.Key_BILL_PRN_HEADER, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).legacyAccountNo != null) {
                            values.put(DatabaseKeys.Key_LEGACY_ACCOUNT_NO, headrelist.get(j).legacyAccountNo.trim());
                        } else {
                            values.put(DatabaseKeys.Key_LEGACY_ACCOUNT_NO, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).name != null) {
                            values.put(DatabaseKeys.Key_NAME, headrelist.get(j).name.trim());
                        } else {
                            values.put(DatabaseKeys.Key_NAME, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).address1 != null) {
                            values.put(DatabaseKeys.Key_ADDRESS1, headrelist.get(j).address1.trim());
                        } else {
                            values.put(DatabaseKeys.Key_ADDRESS1, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).address2 != null) {
                            values.put(DatabaseKeys.Key_ADDRESS2, headrelist.get(j).address2.trim());
                        } else {
                            values.put(DatabaseKeys.Key_ADDRESS2, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).seq != null) {
                            values.put(DatabaseKeys.Key_SEQ, headrelist.get(j).seq.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SEQ, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).subSeq != null) {
                            values.put(DatabaseKeys.Key_SUB_SEQ, headrelist.get(j).subSeq.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SUB_SEQ, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).legacyAccountNo2 != null) {
                            values.put(DatabaseKeys.Key_LEGACY_ACCOUNT_NO2, headrelist.get(j).legacyAccountNo2.trim());
                        } else {
                            values.put(DatabaseKeys.Key_LEGACY_ACCOUNT_NO2, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).installation != null) {
                            values.put(DatabaseKeys.Key_INSTALLATION, headrelist.get(j).installation.trim());
                        } else {
                            values.put(DatabaseKeys.Key_INSTALLATION, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).rateCategory != null) {
                            values.put(DatabaseKeys.Key_RATE_CATEGORY, headrelist.get(j).rateCategory);
                        } else {
                            values.put(DatabaseKeys.Key_RATE_CATEGORY, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).div != null) {
                            values.put(DatabaseKeys.Key_DIV, headrelist.get(j).div.trim());
                        } else {
                            values.put(DatabaseKeys.Key_DIV, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).subDiv != null) {
                            values.put(DatabaseKeys.Key_SUB_DIV, headrelist.get(j).subDiv.trim());
                        } else {
                            values.put(DatabaseKeys.Key_SUB_DIV, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).section != null) {
                            values.put(DatabaseKeys.Key_SECTION, headrelist.get(j).section.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SECTION, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).consumerOwned != null) {
                            values.put(DatabaseKeys.Key_CONSUMER_OWNED, headrelist.get(j).consumerOwned.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_CONSUMER_OWNED, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).meterMake != null) {
                            values.put(DatabaseKeys.Key_METER_MAKE, headrelist.get(j).meterMake.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_METER_MAKE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).usage != null) {
                            values.put(DatabaseKeys.Key_USAGE, headrelist.get(j).usage.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_USAGE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).sanLoad != null) {
                            values.put(DatabaseKeys.Key_SAN_LOAD, headrelist.get(j).sanLoad.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SAN_LOAD, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).moveInDate != null) {
                            //values.put(DatabaseKeys.Key_MOVE_IN_DATE, UtilsClass.checkDateFormate(headrelist.get(j).moveInDate.toString().trim()));
                            values.put(DatabaseKeys.Key_MOVE_IN_DATE, headrelist.get(j).moveInDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_MOVE_IN_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).dps != null) {
                            values.put(DatabaseKeys.Key_DPS, headrelist.get(j).dps.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_DPS, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).miscCharges != null) {
                            values.put(DatabaseKeys.Key_MISC_CHARGES, headrelist.get(j).miscCharges.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_MISC_CHARGES, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).crAdj != null) {
                            values.put(DatabaseKeys.Key_CR_ADJ, headrelist.get(j).crAdj.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_CR_ADJ, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).dbAdj != null) {
                            values.put(DatabaseKeys.Key_DB_ADJ, headrelist.get(j).dbAdj.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_DB_ADJ, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).prvBilledAmt != null) {
                            values.put(DatabaseKeys.Key_PRV_BILLED_AMT, headrelist.get(j).prvBilledAmt.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PRV_BILLED_AMT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).previousBilledProvUnit != null) {
                            values.put(DatabaseKeys.Key_PREVIOUS_BILLED_PROV_UNIT, headrelist.get(j).previousBilledProvUnit.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PREVIOUS_BILLED_PROV_UNIT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).lastPaidDate != null) {
                            values.put(DatabaseKeys.Key_LAST_PAID_DATE, headrelist.get(j).lastPaidDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_LAST_PAID_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).lastPymtRcpt != null) {
                            values.put(DatabaseKeys.Key_LAST_PYMT_RCPT, headrelist.get(j).lastPymtRcpt.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_LAST_PYMT_RCPT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).lastPaidAmt != null) {
                            values.put(DatabaseKeys.Key_LAST_PAID_AMT, headrelist.get(j).lastPaidAmt.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_LAST_PAID_AMT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).edExempt != null) {
                            values.put(DatabaseKeys.Key_ED_EXEMPT, headrelist.get(j).edExempt.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_ED_EXEMPT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).aifi != null) {
                            values.put(DatabaseKeys.Key_AIFI, headrelist.get(j).aifi.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_AIFI, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).newMeterNo != null) {
                            values.put(DatabaseKeys.Key_NEW_METER_NO, headrelist.get(j).newMeterNo.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_NEW_METER_NO, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).sdi != null) {
                            values.put(DatabaseKeys.Key_SDI, headrelist.get(j).sdi.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SDI, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).asd != null) {
                            values.put(DatabaseKeys.Key_ASD, headrelist.get(j).asd.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_ASD, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).asdaa != null) {
                            values.put(DatabaseKeys.Key_ASDAA, headrelist.get(j).asdaa.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_ASDAA, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).sbmBillNo != null) {
                            values.put(DatabaseKeys.Key_SBM_BILL_NO, headrelist.get(j).sbmBillNo.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SBM_BILL_NO, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).ca != null) {
                            values.put(DatabaseKeys.Key_CA, headrelist.get(j).ca.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_CA, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).prvArr != null) {
                            values.put(DatabaseKeys.Key_PRV_ARR, headrelist.get(j).prvArr.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PRV_ARR, DatabaseKeys.EMPTY_STRING);
                        }
                        try {
                            if (headrelist.get(j).arrears != null) {
                                if (headrelist.get(j).arrears.trim().substring(headrelist.get(j).arrears.trim().length() - 1).equals("-")) {
                                    values.put(DatabaseKeys.Key_ARREARS, cyclicLeftShift(headrelist.get(j).arrears.toString().trim(), (headrelist.get(j).arrears.toString().trim().length() - 1)));
                                } else {
                                    values.put(DatabaseKeys.Key_ARREARS, headrelist.get(j).arrears.toString().trim());
                                }
                            } else {
                                values.put(DatabaseKeys.Key_ARREARS, DatabaseKeys.EMPTY_STRING);
                            }
                        } catch (Exception e) {

                        }
                        if (headrelist.get(j).ulf != null) {
                            values.put(DatabaseKeys.Key_ULF, headrelist.get(j).ulf.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_ULF, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).prevBillUnits != null) {
                            values.put(DatabaseKeys.Key_PREV_BILL_UNITS, headrelist.get(j).prevBillUnits.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PREV_BILL_UNITS, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).ecsLimt != null) {
                            values.put(DatabaseKeys.Key_ECS_LIMT, headrelist.get(j).ecsLimt.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_ECS_LIMT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).ecsValidityPeriod != null) {
                            values.put(DatabaseKeys.Key_ECS_VALIDITY_PERIOD, headrelist.get(j).ecsValidityPeriod.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_ECS_VALIDITY_PERIOD, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).presentReadingRemark != null) {
                            values.put(DatabaseKeys.Key_PRESENT_READING_REMARK, headrelist.get(j).presentReadingRemark.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PRESENT_READING_REMARK, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).presentMeterStatus != null) {
                            values.put(DatabaseKeys.Key_PRESENT_METER_STATUS, headrelist.get(j).presentMeterStatus.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PRESENT_METER_STATUS, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).presentBillUnits != null) {
                            values.put(DatabaseKeys.Key_PRESENT_BILL_UNITS, headrelist.get(j).presentBillUnits.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PRESENT_BILL_UNITS, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).presentBillType != null) {
                            values.put(DatabaseKeys.Key_PRESENT_BILL_TYPE, headrelist.get(j).presentBillType.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PRESENT_BILL_TYPE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).ec != null) {
                            values.put(DatabaseKeys.Key_EC, headrelist.get(j).ec.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_EC, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).mmfc != null) {
                            values.put(DatabaseKeys.Key_MMFC, headrelist.get(j).mmfc.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_MMFC, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).mrentCharged != null) {
                            values.put(DatabaseKeys.Key_MRENT_CHARGED, headrelist.get(j).mrentCharged.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_MRENT_CHARGED, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).ed != null) {
                            values.put(DatabaseKeys.Key_ED, headrelist.get(j).ed.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_ED, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).currentBillTotal != null) {
                            values.put(DatabaseKeys.Key_CURRENT_BILL_TOTAL, headrelist.get(j).currentBillTotal.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_CURRENT_BILL_TOTAL, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).rebate != null) {
                            values.put(DatabaseKeys.Key_REBATE, headrelist.get(j).rebate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_REBATE, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).amountPayable != null) {
                            values.put(DatabaseKeys.Key_AMOUNT_PAYABLE, headrelist.get(j).amountPayable.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_AMOUNT_PAYABLE, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).avgUnitBilled != null) {
                            values.put(DatabaseKeys.Key_AVG_UNIT_BILLED, headrelist.get(j).avgUnitBilled.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_AVG_UNIT_BILLED, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).rcptno != null) {
                            values.put(DatabaseKeys.Key_RCPTNO, headrelist.get(j).rcptno.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_RCPTNO, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).chqno != null) {
                            values.put(DatabaseKeys.Key_CHQNO, headrelist.get(j).chqno.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_CHQNO, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).chqdt != null) {
                            values.put(DatabaseKeys.Key_CHQDT, headrelist.get(j).chqdt.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_CHQDT, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).bank != null) {
                            values.put(DatabaseKeys.Key_BANK, headrelist.get(j).bank.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_BANK, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).rcptamt != null) {
                            values.put(DatabaseKeys.Key_RCPTAMT, headrelist.get(j).rcptamt.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_RCPTAMT, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).dueDate != null) {
                            values.put(DatabaseKeys.Key_DUE_DATE, headrelist.get(j).dueDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_DUE_DATE, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).doExpiry != null) {
                            values.put(DatabaseKeys.Key_DO_EXPIRY, headrelist.get(j).doExpiry.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_DO_EXPIRY, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).presentReadingTime != null) {
                            values.put(DatabaseKeys.Key_PRESENT_READING_TIME, headrelist.get(j).presentReadingTime.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PRESENT_READING_TIME, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).osbillDate != null) {
                            values.put(DatabaseKeys.Key_OSBILL_DATE, headrelist.get(j).osbillDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_OSBILL_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).capturedMobile != null) {
                            values.put(DatabaseKeys.Key_CAPTURED_MOBILE, headrelist.get(j).capturedMobile.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_CAPTURED_MOBILE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).meterRent != null) {
                            values.put(DatabaseKeys.Key_METER_RENT, headrelist.get(j).meterRent.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_METER_RENT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).portion != null) {
                            values.put(DatabaseKeys.Key_PORTION, headrelist.get(j).portion.trim());
                        } else {
                            values.put(DatabaseKeys.Key_PORTION, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).mru != null) {
                            values.put(DatabaseKeys.Key_MRU, headrelist.get(j).mru.trim());
                        } else {
                            values.put(DatabaseKeys.Key_MRU, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).sanLoadUnits != null) {
                            values.put(DatabaseKeys.Key_SAN_LOAD_UNITS, headrelist.get(j).sanLoadUnits.trim());
                        } else {
                            values.put(DatabaseKeys.Key_SAN_LOAD_UNITS, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).sanLoadEffectiveDate != null) {
                            values.put(DatabaseKeys.Key_SAN_LOAD_EFFECTIVE_DATE, headrelist.get(j).sanLoadEffectiveDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SAN_LOAD_EFFECTIVE_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).supplyTypFlg != null) {
                            values.put(DatabaseKeys.Key_SUPPLY_TYP_FLG, headrelist.get(j).supplyTypFlg.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SUPPLY_TYP_FLG, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).notinuseFlgEnddate != null) {
                            values.put(DatabaseKeys.Key_NOTINUSE_FLG_ENDDATE, headrelist.get(j).notinuseFlgEnddate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_NOTINUSE_FLG_ENDDATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).otherFlgs != null) {
                            values.put(DatabaseKeys.Key_OTHER_FLGS, headrelist.get(j).otherFlgs.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_OTHER_FLGS, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).edRbt != null) {
                            values.put(DatabaseKeys.Key_ED_RBT, headrelist.get(j).edRbt.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_ED_RBT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).hostelRbt != null) {
                            values.put(DatabaseKeys.Key_HOSTEL_RBT, headrelist.get(j).hostelRbt.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_HOSTEL_RBT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).prevBillType != null) {
                            values.put(DatabaseKeys.Key_PREV_BILL_TYPE, headrelist.get(j).prevBillType.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PREV_BILL_TYPE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).prevBillRemark != null) {
                            values.put(DatabaseKeys.Key_PREV_BILL_REMARK, headrelist.get(j).prevBillRemark.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PREV_BILL_REMARK, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).prevBillEndDate != null) {
                            values.put(DatabaseKeys.Key_PREV_BILL_END_DATE, headrelist.get(j).prevBillEndDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PREV_BILL_END_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).lastNormalBillDate != null) {
                            values.put(DatabaseKeys.Key_LAST_NORMAL_BILL_DATE, headrelist.get(j).lastNormalBillDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_LAST_NORMAL_BILL_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).presentReadingDate != null) {
                            values.put(DatabaseKeys.Key_PRESENT_READING_DATE, headrelist.get(j).presentReadingDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PRESENT_READING_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).averageKwh != null) {
                            values.put(DatabaseKeys.Key_AVERAGE_KWH, headrelist.get(j).averageKwh.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_AVERAGE_KWH, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).billBasis != null) {
                            values.put(DatabaseKeys.Key_BILL_BASIS, headrelist.get(j).billBasis.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_BILL_BASIS, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).billNo != null) {
                            values.put(DatabaseKeys.Key_BILL_NO, headrelist.get(j).billNo.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_BILL_NO, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).invoiceNo != null) {
                            values.put(DatabaseKeys.Key_INVOICE_NO, headrelist.get(j).invoiceNo.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_INVOICE_NO, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).bpStartDate != null) {
                            values.put(DatabaseKeys.Key_BP_START_DATE, headrelist.get(j).bpStartDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_BP_START_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).bpEndDate != null) {
                            values.put(DatabaseKeys.Key_BP_END_DATE, headrelist.get(j).bpEndDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_BP_END_DATE, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).ppac != null) {
                            values.put(DatabaseKeys.Key_PPAC, headrelist.get(j).ppac.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PPAC, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).roundOff != null) {
                            values.put(DatabaseKeys.Key_ROUND_OFF, headrelist.get(j).roundOff.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_ROUND_OFF, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).secDepositAmt != null) {
                            values.put(DatabaseKeys.Key_SEC_DEPOSIT_AMT, headrelist.get(j).secDepositAmt.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SEC_DEPOSIT_AMT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).doGenerated != null) {
                            values.put(DatabaseKeys.Key_DO_GENERATED, headrelist.get(j).doGenerated.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_DO_GENERATED, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).notToBillAfter != null) {
                            values.put(DatabaseKeys.Key_NOT_TO_BILL_AFTER, headrelist.get(j).notToBillAfter.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_NOT_TO_BILL_AFTER, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).insertDate != null) {
                            //values.put(DatabaseKeys.Key_INSERT_DATE, UtilsClass.checkDateFormate(headrelist.get(j).insertDate.toString().trim()));
                            values.put(DatabaseKeys.Key_INSERT_DATE, headrelist.get(j).insertDate.toString().trim());

                        } else {
                            values.put(DatabaseKeys.Key_INSERT_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).insertTime != null) {
                            values.put(DatabaseKeys.Key_INSERT_TIME, headrelist.get(j).insertTime.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_INSERT_TIME, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).updateDate != null) {
                            //values.put(DatabaseKeys.Key_UPDATE_DATE, UtilsClass.checkDateFormate(headrelist.get(j).updateDate.toString().trim()));
                            values.put(DatabaseKeys.Key_UPDATE_DATE, headrelist.get(j).updateDate.toString().trim());

                        } else {
                            values.put(DatabaseKeys.Key_UPDATE_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).updateTime != null) {
                            values.put(DatabaseKeys.Key_UPDATE_TIME, headrelist.get(j).updateTime.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_UPDATE_TIME, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).phone1 != null) {
                            values.put(DatabaseKeys.Key_PHONE_1, headrelist.get(j).phone1.trim());
                        } else {
                            values.put(DatabaseKeys.Key_PHONE_1, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).phone2 != null) {
                            values.put(DatabaseKeys.Key_PHONE_2, headrelist.get(j).phone2.trim());
                        } else {
                            values.put(DatabaseKeys.Key_PHONE_2, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).transType != null) {
                            values.put(DatabaseKeys.Key_TRANS_TYPE, headrelist.get(j).transType.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_TRANS_TYPE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).tdFlag != null) {
                            values.put(DatabaseKeys.Key_TD_FLAG, headrelist.get(j).tdFlag.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_TD_FLAG, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).tdDate != null) {
                            values.put(DatabaseKeys.Key_TD_DATE, headrelist.get(j).tdDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_TD_DATE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).ppi != null) {
                            values.put(DatabaseKeys.Key_PPI, headrelist.get(j).ppi.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PPI, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).prevProvAmt != null) {
                            values.put(DatabaseKeys.Key_PREV_PROV_AMT, headrelist.get(j).prevProvAmt.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PREV_PROV_AMT, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).gstRelevant1 != null) {
                            values.put(DatabaseKeys.Key_GST_RELEVANT1, headrelist.get(j).gstRelevant1.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_GST_RELEVANT1, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).gstRelevant2 != null) {
                            values.put(DatabaseKeys.Key_GST_RELEVANT2, headrelist.get(j).gstRelevant2.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_GST_RELEVANT2, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).gstRelevant3 != null) {
                            values.put(DatabaseKeys.Key_GST_RELEVANT3, headrelist.get(j).gstRelevant3.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_GST_RELEVANT3, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).gstRelevant4 != null) {
                            values.put(DatabaseKeys.Key_GST_RELEVANT4, headrelist.get(j).gstRelevant4.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_GST_RELEVANT4, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).eltStts != null) {
                            values.put(DatabaseKeys.Key_ELT_STTS, headrelist.get(j).eltStts.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_ELT_STTS, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).sealStts != null) {
                            values.put(DatabaseKeys.Key_SEAL_STTS, headrelist.get(j).sealStts.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SEAL_STTS, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).cblass != null) {
                            values.put(DatabaseKeys.Key_CBLASS, headrelist.get(j).cblass.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_CBLASS, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).usageId != null) {
                            values.put(DatabaseKeys.Key_USAGE_ID, headrelist.get(j).usageId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_USAGE_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).meterTypeId != null) {
                            values.put(DatabaseKeys.Key_METER_TYPE_ID, headrelist.get(j).meterTypeId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_METER_TYPE_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).supplyStatusId != null) {
                            values.put(DatabaseKeys.Key_SUPPLY_STATUS_ID, headrelist.get(j).supplyStatusId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SUPPLY_STATUS_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).reasonDcId != null) {
                            values.put(DatabaseKeys.Key_REASON_DC_ID, headrelist.get(j).reasonDcId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_REASON_DC_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).supplySourceId != null) {
                            values.put(DatabaseKeys.Key_SUPPLY_SOURCE_ID, headrelist.get(j).supplySourceId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SUPPLY_SOURCE_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).reasonNvId != null) {
                            values.put(DatabaseKeys.Key_REASON_NV_ID, headrelist.get(j).reasonNvId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_REASON_NV_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).reasonCdId != null) {
                            values.put(DatabaseKeys.Key_REASON_CD_ID, headrelist.get(j).reasonCdId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_REASON_CD_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).reasonEnId != null) {
                            values.put(DatabaseKeys.Key_REASON_EN_ID, headrelist.get(j).reasonEnId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_REASON_EN_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).reasonMtrStuckId != null) {
                            values.put(DatabaseKeys.Key_REASON_MTR_STUCK_ID, headrelist.get(j).reasonMtrStuckId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_REASON_MTR_STUCK_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).paperPasteById != null) {
                            values.put(DatabaseKeys.Key_PAPER_PASTE_BY_ID, headrelist.get(j).paperPasteById.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_PAPER_PASTE_BY_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).meterHeightId != null) {
                            values.put(DatabaseKeys.Key_METER_HEIGHT_ID, headrelist.get(j).meterHeightId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_METER_HEIGHT_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).typesObstacleId != null) {
                            values.put(DatabaseKeys.Key_TYPES_OBSTACLE_ID, headrelist.get(j).typesObstacleId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_TYPES_OBSTACLE_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).sealStsId != null) {
                            values.put(DatabaseKeys.Key_SEAL_STS_ID, headrelist.get(j).sealStsId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SEAL_STS_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).contactReasonId != null) {
                            values.put(DatabaseKeys.Key_CONTACT_REASON_ID, headrelist.get(j).contactReasonId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_CONTACT_REASON_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).reasonPlId != null) {
                            values.put(DatabaseKeys.Key_REASON_PL_ID, headrelist.get(j).reasonPlId.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_REASON_PL_ID, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).hlMonths != null) {
                            values.put(DatabaseKeys.Key_HL_MONTHS, headrelist.get(j).hlMonths.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_HL_MONTHS, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).buildingDesc != null) {
                            values.put(DatabaseKeys.Key_BUILDING_DESC, headrelist.get(j).buildingDesc.trim());
                        } else {
                            values.put(DatabaseKeys.Key_BUILDING_DESC, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).buildingCode != null) {
                            values.put(DatabaseKeys.Key_BUILDING_CODE, headrelist.get(j).buildingCode.trim());
                        } else {
                            values.put(DatabaseKeys.Key_BUILDING_CODE, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).poleNo != null) {
                            values.put(DatabaseKeys.Key_POLE_NO, headrelist.get(j).poleNo.trim());
                        } else {
                            values.put(DatabaseKeys.Key_POLE_NO, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).flag != null) {
                            values.put(DatabaseKeys.Key_FLAG, headrelist.get(j).flag.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_FLAG, DatabaseKeys.EMPTY_STRING);
                        }
                        if (headrelist.get(j).specialRem != null) {
                            values.put(DatabaseKeys.Key_SPECIAL_REM, headrelist.get(j).specialRem.trim());
                        } else {
                            values.put(DatabaseKeys.Key_SPECIAL_REM, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).senderMobile != null) {
                            values.put(DatabaseKeys.Key_SENDER_MOBILE, headrelist.get(j).senderMobile.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SENDER_MOBILE, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).consumerType != null) {
                            values.put(DatabaseKeys.Key_CONSUMER_TYPE, headrelist.get(j).consumerType.trim());
                        } else {
                            values.put(DatabaseKeys.Key_CONSUMER_TYPE, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).walkingSeqChk != null) {
                            values.put(DatabaseKeys.Key_WALKING_SEQ_CHK, headrelist.get(j).walkingSeqChk.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_WALKING_SEQ_CHK, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).stopPaprBl != null) {
                            values.put(DatabaseKeys.Key_STOP_PAPR_BL, headrelist.get(j).stopPaprBl.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_STOP_PAPR_BL, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).scheduleMeterReadDate != null) {
                            // values.put(DatabaseKeys.Key_SCHEDULE_METER_READ_DATE, UtilsClass.checkDateFormate(headrelist.get(j).scheduleMeterReadDate.toString().trim()));
                            values.put(DatabaseKeys.Key_SCHEDULE_METER_READ_DATE, headrelist.get(j).scheduleMeterReadDate.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_SCHEDULE_METER_READ_DATE, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).billMonth != null) {
                            values.put(DatabaseKeys.Key_BILL_MONTH, (headrelist.get(j).billMonth.toString().trim()));
                        } else {
                            values.put(DatabaseKeys.Key_BILL_MONTH, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).billYear != null) {
                            values.put(DatabaseKeys.Key_BILL_YEAR, headrelist.get(j).billYear.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_BILL_YEAR, DatabaseKeys.EMPTY_STRING);
                        }

                        if (headrelist.get(j).mrreason != null) {
                            values.put(DatabaseKeys.Key_MRREASON, headrelist.get(j).mrreason.toString().trim());
                        } else {
                            values.put(DatabaseKeys.Key_MRREASON, DatabaseKeys.EMPTY_STRING);
                        }

                        values.put(DatabaseKeys.Key_READ_FLAG, "0");
                        rowInserted = db.insert(DatabaseKeys.TABLE_HEADER, null, values);
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
                instHeaderNotInsert.add(headerNotInstall);
                totalData.put("header", instHeaderNotInsert);
                totalData.put("child", instChildNotInsert);
            }

            return "Complete";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            progressDialog1.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog1.dismiss();
            if (rowInserted != -1) {
                new AsyncTaskInsertChild(response).execute();
            }
        }
    }

    private class AsyncTaskInsertChild extends AsyncTask<Void, Void, Void> {
        BillingResponseModel response;
        long rowInserted = -1;

        public AsyncTaskInsertChild(BillingResponseModel response) {
            dUtils = new DialogUtils(mContext);
            this.response = response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dUtils.showDialog("Message", "Processing Data");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            rowInserted = inserBillingChildData(response);
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dUtils.dismissDialog();
            if (rowInserted > 0) {
                /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                alertDialogBuilder.setMessage("Data Downloaded")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ((Activity) mContext).finish();
                                    }
                                });

                AlertDialog alert = alertDialogBuilder.create();
                alert.show();*/
                int totalCount = Headercount + ChildCount;
                String strSelectSQL_01 = "";
                try {
                    DatabaseHelper helper = new DatabaseHelper(mContext);
                    strSelectSQL_01 = "Select count(1) from TBL_SPOTBILL_HEADER_DETAILS";
                    Cursor rs = helper.getCalculateedData(strSelectSQL_01);
                   /* int HeaderChkCnt = 0;
                    int ChildChkCnt = 0;*/
                    while (rs.moveToNext()) {
                        HeaderChkCnt = rs.getInt(0);
                    }
                    rs.close();

                    strSelectSQL_01 = "Select count(1) from TBL_SPOTBILL_CHILD_DETAILS";
                    rs = helper.getCalculateedData(strSelectSQL_01);
                    while (rs.moveToNext()) {
                        ChildChkCnt = rs.getInt(0);
                    }
                    rs.close();

                    HeaderChkCnt = HeaderChkCnt - Headercount;
                    ChildChkCnt = ChildChkCnt - ChildCount;
                } catch (Exception e) {

                }
                new CustomDialogWithTwoButton(mContext, "Download Status",
                        mContext.getResources().getString(R.string.Yes),
                        mContext.getResources().getString(R.string.cancel),
                        new CustomDialogWithTwoButton.CustomDialogWithTwoBtmsCallBack() {

                            @Override
                            public void positiveBtmCallBack() {
                                try {
                                    ((Activity) mContext).finish();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                            @Override
                            public void negativeBtmCallBack() {

                            }

                        }, String.valueOf(totalCount), String.valueOf(Headercount), String.valueOf(ChildCount), String.valueOf(HeaderChkCnt), String.valueOf(ChildChkCnt)).show();
            } else {
                Toast.makeText(mContext, "child Something wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public long inserBillingChildData(BillingResponseModel response) {
        long rowInserted = 0;
        ChildCount = 0;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            for (int k = 0; k < response.response.size(); k++) {
                for (int m = 0; m < response.response.get(k).headerDetails.size(); m++) {
                    List<ChildDetail> mlist = response.response.get(k).headerDetails.get(m).childDataList;
                    if (mlist != null) {
                        Log.e("childdata::", "" + mlist.size());
                        for (int i = 0; i < mlist.size(); i++) {
                            ChildCount++;
                            /*  if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
                                    values.put(DatabaseKeys.Key_USER_TYPE, "S");
                            } else {
                                    values.put(DatabaseKeys.Key_USER_TYPE, "X");
                            }*/

                            if (mlist.get(i).ablbelnr != null) {
                                values.put(DatabaseKeys.childKey_ABLBELNR, mlist.get(i).ablbelnr.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_ABLBELNR, DatabaseKeys.EMPTY_STRING);
                            }

                            if (mlist.get(i).transType != null) {
                                values.put(DatabaseKeys.childKey_TRANSTYPE, mlist.get(i).transType.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_TRANSTYPE, DatabaseKeys.EMPTY_STRING);
                            }

                            if (mlist.get(i).noOfReg != null) {
                                values.put(DatabaseKeys.childKey_NO_OF_REG, mlist.get(i).noOfReg.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_NO_OF_REG, DatabaseKeys.EMPTY_STRING);
                            }

                            if (mlist.get(i).billMonth != null) {
                                values.put(DatabaseKeys.childKey_BILL_MONTH, mlist.get(i).billMonth.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_BILL_MONTH, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).billYear != null) {
                                values.put(DatabaseKeys.childKey_BILL_YEAR, mlist.get(i).billYear.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_BILL_YEAR, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).billedMd != null) {
                                values.put(DatabaseKeys.childKey_BILLED_MD, mlist.get(i).billedMd.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_BILLED_MD, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).consumptionOldMeter != null) {
                                values.put(DatabaseKeys.childKey_CONSUMPTION_OLD_METER, (mlist.get(i).consumptionOldMeter.toString().trim()));
                            } else {
                                values.put(DatabaseKeys.childKey_CONSUMPTION_OLD_METER, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).equipmentNo != null) {
                                values.put(DatabaseKeys.childKey_EQUIPMENT_NO, (mlist.get(i).equipmentNo.toString().trim()));
                            } else {
                                values.put(DatabaseKeys.childKey_EQUIPMENT_NO, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).installation != null) {
                                values.put(DatabaseKeys.childKey_INSTALLATION, (mlist.get(i).installation.toString().trim()));
                            } else {
                                values.put(DatabaseKeys.childKey_INSTALLATION, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).lastOkRdng != null) {
                                values.put(DatabaseKeys.childKey_LAST_OK_RDNG, mlist.get(i).lastOkRdng.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_LAST_OK_RDNG, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).meterCondition != null) {
                                values.put(DatabaseKeys.childKey_METER_CONDITION, mlist.get(i).meterCondition.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_METER_CONDITION, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).meterInstallDate != null) {
                                values.put(DatabaseKeys.childKey_METER_INSTALL_DATE, mlist.get(i).meterInstallDate.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_METER_INSTALL_DATE, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).meterNo != null) {
                                values.put(DatabaseKeys.childKey_METER_NO, mlist.get(i).meterNo.trim());

                            } else {
                                values.put(DatabaseKeys.childKey_METER_NO, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).meterRemovedOn != null) {
                                values.put(DatabaseKeys.childKey_METER_REMOVED_ON, (mlist.get(i).meterRemovedOn.toString().trim()));
                            } else {
                                values.put(DatabaseKeys.childKey_METER_REMOVED_ON, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).meterTyp != null) {
                                values.put(DatabaseKeys.childKey_METER_TYP, (mlist.get(i).meterTyp.toString().trim()));
                            } else {
                                values.put(DatabaseKeys.childKey_METER_TYP, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).mf != null) {
                                values.put(DatabaseKeys.childKey_MF, mlist.get(i).mf.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_MF, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).mrreason != null) {
                                values.put(DatabaseKeys.childKey_MRREASON, mlist.get(i).mrreason.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_MRREASON, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).newMeterFlg != null) {
                                values.put(DatabaseKeys.childKey_NEW_METER_FLG, mlist.get(i).newMeterFlg.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_NEW_METER_FLG, DatabaseKeys.EMPTY_STRING);
                            }
                            if (mlist.get(i).noOfDigits != null) {
                                values.put(DatabaseKeys.childKey_NO_OF_DIGITS, mlist.get(i).noOfDigits.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_NO_OF_DIGITS, DatabaseKeys.EMPTY_STRING);
                            }

                            if (mlist.get(i).presentMeterReading != null) {
                                values.put(DatabaseKeys.childKey_PRESENT_METER_READING, mlist.get(i).presentMeterReading.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_PRESENT_METER_READING, DatabaseKeys.EMPTY_STRING);
                            }

                            if (mlist.get(i).prevMtrRead != null) {
                                values.put(DatabaseKeys.childKey_PREV_MTR_READ, mlist.get(i).prevMtrRead.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_PREV_MTR_READ, DatabaseKeys.EMPTY_STRING);
                            }

                            if (mlist.get(i).prevReadDate != null) {
                                values.put(DatabaseKeys.childKey_PREV_READ_DATE, (mlist.get(i).prevReadDate.toString().trim()));
                            } else {
                                values.put(DatabaseKeys.childKey_PREV_READ_DATE, DatabaseKeys.EMPTY_STRING);
                            }

                            if (mlist.get(i).previousMd != null) {
                                values.put(DatabaseKeys.childKey_PREVIOUS_MD, mlist.get(i).previousMd.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_PREVIOUS_MD, DatabaseKeys.EMPTY_STRING);
                            }

                            if (mlist.get(i).prsMd != null) {
                                values.put(DatabaseKeys.childKey_PRS_MD, (mlist.get(i).prsMd.toString().trim()));
                            } else {
                                values.put(DatabaseKeys.childKey_PRS_MD, DatabaseKeys.EMPTY_STRING);
                            }

                            if (mlist.get(i).registerCode != null) {
                                values.put(DatabaseKeys.childKey_REGISTER_CODE, mlist.get(i).registerCode.toString().trim());
                            } else {
                                values.put(DatabaseKeys.childKey_REGISTER_CODE, DatabaseKeys.EMPTY_STRING);
                            }

                            if (mlist.get(i).scheduleMeterReadDate != null) {
                                values.put(DatabaseKeys.childKey_SCHEDULE_METER_READ_DATE, (mlist.get(i).scheduleMeterReadDate.toString().trim()));
                            } else {
                                values.put(DatabaseKeys.childKey_SCHEDULE_METER_READ_DATE, DatabaseKeys.EMPTY_STRING);
                            }
                            rowInserted = db.insert(DatabaseKeys.TABLE_CHILD, null, values);
                        }
                        Log.e("ChildCountVal", "" + ChildCount);
                    }
                }


            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("Child Intertion Error", ex.getMessage());
        }

        return rowInserted;
    }


    public Cursor getHeaderdetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String USER_TYPE = "";
        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            USER_TYPE = "S";
        } else {
            USER_TYPE = "X";
        }
/*
        String query = "select * from " + DatabaseKeys.TABLE_HEADER + " WHERE SENT_FLAG = '0' and READ_FLAG='1' and USER_TYPE='" + USER_TYPE + "'";
*/

        String query = "select LEGACY_ACCOUNT_NO,NAME,ADDRESS1,ADDRESS2,SEQ,SUB_SEQ,LEGACY_ACCOUNT_NO2,RATE_CATEGORY,DIV,SUB_DIV,SECTION,CONSUMER_OWNED," +
                "METER_MAKE,USAGE,SAN_LOAD,MOVE_IN_DATE,DPS," +
                "case when substr(ifnull(MISC_CHARGES,0),length(ifnull(MISC_CHARGES,0))-1,1) = '-' then (substr(ifnull(MISC_CHARGES,0),1,length(ifnull(MISC_CHARGES,0)))+0)*-1 else ifnull(MISC_CHARGES,0)+0 end MISC_CHARGES," +
                "case when substr(ifnull(CR_ADJ,0),length(ifnull(CR_ADJ,0)),1) ='-' then (substr(ifnull(CR_ADJ,0),1,length(ifnull(CR_ADJ,0))-1)+0)*-1 else ifnull(CR_ADJ,0)+0 end CR_ADJ," +
                "case when substr(ifnull(DB_ADJ,0),length(ifnull(DB_ADJ,0))-1,1) ='-' then (substr(ifnull(DB_ADJ,0),1,length(ifnull(DB_ADJ,0))-1)+0)*-1 else ifnull(DB_ADJ,0)+0 end DB_ADJ," +
                "PRV_BILLED_AMT," +
                "case when substr(ifnull(PREVIOUS_BILLED_PROV_UNIT,0),length(ifnull(PREVIOUS_BILLED_PROV_UNIT,0)),1) = '-' then (substr(ifnull(PREVIOUS_BILLED_PROV_UNIT,0),1,length(ifnull(PREVIOUS_BILLED_PROV_UNIT,0))-1)+0)*-1 else ifnull(PREVIOUS_BILLED_PROV_UNIT,0)+0 end PREVIOUS_BILLED_PROV_UNIT," +
                "LAST_PAID_DATE,LAST_PYMT_RCPT," +
                "case when substr(ifnull(LAST_PAID_AMT,0),length(ifnull(LAST_PAID_AMT,0)),1) ='-' then (substr(ifnull(LAST_PAID_AMT,0),1,length(ifnull(LAST_PAID_AMT,0))-1)+0)*-1 else ifnull(LAST_PAID_AMT,0)+0 end LAST_PAID_AMT," +
                "ED_EXEMPT,AIFI,NEW_METER_NO,SDI,ASD,ASDAA,INSTALLATION,SBM_BILL_NO,CA,PRV_ARR," +
                "case when substr(ifnull(ARREARS,0),length(ifnull(ARREARS,0)),1) ='-' then (substr(ifnull(ARREARS,0),1,length(ifnull(ARREARS,0))-1)+0)*-1 else ifnull(ARREARS,0)+0 end ARREARS," +
                "case when  substr(ifnull(ULF,0),length(ifnull(ULF,0))-1,1) ='-' then (substr(ifnull(ULF,0),1,length(ifnull(ULF,0))-1)+0)*-1 else ifnull(ULF,0)+0 end ULF," +
                "case when substr(ifnull(PREV_BILL_UNITS,0),length(ifnull(PREV_BILL_UNITS,0)),1) ='-' then (substr(ifnull(PREV_BILL_UNITS,0),1,length(ifnull(PREV_BILL_UNITS,0))-1)+0)*-1 else ifnull(PREV_BILL_UNITS,0)+0 end PREV_BILL_UNITS," +
                "BILL_MONTH," +
                "case when substr(ifnull(ECS_LIMT,0),length(ifnull(ECS_LIMT,0)),1) ='-' then (substr(ifnull(ECS_LIMT,0),1,length(ifnull(ECS_LIMT,0))-1)+0)*-1 else ifnull(ECS_LIMT,0)+0 end ECS_LIMT," +
                "ECS_VALIDITY_PERIOD,PRESENT_READING_REMARK,PRESENT_METER_STATUS," +
                "case when substr(ifnull(PRESENT_BILL_UNITS,0),length(ifnull(PRESENT_BILL_UNITS,0)),1) ='-' then (substr(ifnull(PRESENT_BILL_UNITS,0),1,length(ifnull(PRESENT_BILL_UNITS,0))-1)+0)*-1 else ifnull(PRESENT_BILL_UNITS,0)+0 end PRESENT_BILL_UNITS," +
                "PRESENT_BILL_TYPE," +
                "case when substr(ifnull(EC,0),length(ifnull(EC,0)),1) ='-' then (substr(ifnull(EC,0),1,length(ifnull(EC,0))-1)+0)*-1 else ifnull(EC,0)+0 end EC," +
                "case when substr(ifnull(MMFC,0),length(ifnull(MMFC,0)),1) ='-' then (substr(ifnull(MMFC,0),1,length(ifnull(MMFC,0))-1)+0)*-1 else ifnull(MMFC,0)+0 end MMFC," +
                "case when substr(ifnull(MRENT_CHARGED,0),length(ifnull(MRENT_CHARGED,0))-1,1) ='-' then (substr(ifnull(MRENT_CHARGED,0),1,length(ifnull(MRENT_CHARGED,0))-1)+0)*-1 else ifnull(MRENT_CHARGED,0)+0 end MRENT_CHARGED," +
                "case when substr(ifnull(ED,0),length(ifnull(ED,0)),1) ='-' then (substr(ifnull(ED,0),1,length(ifnull(ED,0))-1)+0)*-1 else ifnull(ED,0)+0 end ED," +
                "CURRENT_BILL_TOTAL,REBATE,AMOUNT_PAYABLE,AVG_UNIT_BILLED,RCPTNO,CHQNO,CHQDT,BANK,RCPTAMT,DUE_DATE,DO_EXPIRY,PRESENT_READING_TIME,OSBILL_DATE,CAPTURED_MOBILE,SCHEDULE_METER_READ_DATE,METER_RENT,PORTION,MRU,NO_OF_REG,SAN_LOAD_UNITS,SAN_LOAD_EFFECTIVE_DATE,SUPPLY_TYP_FLG,NOTINUSE_FLG_ENDDATE,OTHER_FLGS,ED_RBT,HOSTEL_RBT,PREV_BILL_TYPE,PREV_BILL_REMARK,PREV_BILL_END_DATE,LAST_NORMAL_BILL_DATE,PRESENT_READING_DATE," +
                "case when substr(ifnull(AVERAGE_KWH,0),length(ifnull(AVERAGE_KWH,0)),1) ='-' then (substr(ifnull(AVERAGE_KWH,0),1,length(ifnull(AVERAGE_KWH,0))-1)+0)*-1 else ifnull(AVERAGE_KWH,0)+0 end AVERAGE_KWH," +
                "BILL_BASIS,BILL_NO,INVOICE_NO,READ_ONLY,BP_START_DATE,BP_END_DATE,PPAC,ROUND_OFF," +
                "case when substr(ifnull(SEC_DEPOSIT_AMT,0),length(ifnull(SEC_DEPOSIT_AMT,0)),1) ='-' then (substr(ifnull(SEC_DEPOSIT_AMT,0),1,length(ifnull(SEC_DEPOSIT_AMT,0))-1)+0)*-1 else ifnull(SEC_DEPOSIT_AMT,0)+0 end SEC_DEPOSIT_AMT," +
                "DO_GENERATED,NOT_TO_BILL_AFTER,INSERT_DATE,INSERT_TIME,UPDATE_DATE,UPDATE_TIME,PROGRESSION_STATE,PHONE_1,PHONE_2,BILL_YEAR,TRANS_TYPE,TD_FLAG,TD_DATE," +
                "case when substr(ifnull(PPI,0),length(ifnull(PPI,0)),1) ='-' then (substr(ifnull(PPI,0),1,length(ifnull(PPI,0))-1)+0)*-1 else ifnull(PPI,0)+0 end PPI," +
                "PREV_PROV_AMT,GST_RELEVANT1,GST_RELEVANT2,GST_RELEVANT3,GST_RELEVANT4,ELT_STTS,SEAL_STTS,CBLASS,HL_MONTHS,MRREASON,USAGE_ID,METER_TYPE_ID,SUPPLY_STATUS_ID,REASON_DC_ID,SUPPLY_SOURCE_ID,REASON_NV_ID,REASON_CD_ID,REASON_EN_ID,REASON_MTR_STUCK_ID,PAPER_PASTE_BY_ID,METER_HEIGHT_ID,TYPES_OBSTACLE_ID,SEAL_STS_ID,CONTACT_REASON_ID,REASON_PL_ID,BUILDING_DESC,BUILDING_CODE,POLE_NO,FLAG,SPECIAL_REM,GPS_LONGITUDE,GPS_LATITUDE,SENDER_MOBILE,CONSUMER_TYPE,WALKING_SEQ_CHK,METER_LOCA,MR_REMARK_DET,STOP_PAPR_BL,NEW_MTR_FLG,OLD_MTR_COR_FLG,UNSAFE_COND,READ_FLAG,SENT_FLAG,MTR_IMAGE,UPD_FLAG1,UPD_FLAG2,REVISIT_FLAG,UNIT_SLAB1,RATE_SLAB1,EC_SLAB1,UNIT_SLAB2,RATE_SLAB2,EC_SLAB2,UNIT_SLAB3,RATE_SLAB3,EC_SLAB3,UNIT_SLAB4,RATE_SLAB4,EC_SLAB4,BILL_PRN_FOOTER,BILL_PRN_HEADER,HL_UNIT,REFERENCE_DATE,NO_BILLED_MONTH,REF_MR_DATE,RURAL,SWJL_DH_FL,RCRD_LOAD,ULF_MDI,DPS_5," +
                "case when substr(ifnull(DPS_BILLED,0),length(ifnull(DPS_BILLED,0)),1) ='-' then (substr(ifnull(DPS_BILLED,0),1,length(ifnull(DPS_BILLED,0))-1)+0)*-1 else ifnull(DPS_BILLED,0)+0 end DPS_BILLED," +
                "DPS_LVD,PROV_PPT_AMT,DPS_BLLD,FC_SLAB,USER_TYPE,PROV_ED,MOD_DATE,RQCFLG,BILL_START_DATE,BILL_END_DATE,DISCONNECTION_FLG,BILLED_MD,OFFICE_CODE,ADJ_BILL " +
                " from " + DatabaseKeys.TABLE_HEADER + " WHERE SENT_FLAG = '0' and READ_FLAG='1' and USER_TYPE='" + USER_TYPE + "' group by INSTALLATION";
        Log.e("HeaderQuery", query);
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getChilddetails(String installcommaseperated) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        String USER_TYPE = "";
        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            USER_TYPE = "S";
        } else {
            USER_TYPE = "X";
        }
        if (installcommaseperated.isEmpty()) {
            query = "select * from TBL_SPOTBILL_CHILD_DETAILS";
        } else {
            query = "select * from TBL_SPOTBILL_CHILD_DETAILS WHERE INSTALLATION in " + installcommaseperated;
        }

        Log.e("ChildFetchQuery", query);

        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getUserheaderdetails(String condition) {
        //Log.e("HeaderCondition", condition);
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();

        if (condition.contains("METER_NO")) {
            String installationno = getInstallatioationNo(condition, -1);
            condition = " where INSTALLATION='" + installationno + "'";
        }

        /*String query = "SELECT * FROM " + DatabaseKeys.TABLE_HEADER + condition;*/
        String query = "select LEGACY_ACCOUNT_NO,NAME,ADDRESS1,ADDRESS2,SEQ,SUB_SEQ,LEGACY_ACCOUNT_NO2,RATE_CATEGORY,DIV,SUB_DIV,SECTION,CONSUMER_OWNED," +
                "METER_MAKE,USAGE,SAN_LOAD,MOVE_IN_DATE,DPS," +
                "case when substr(ifnull(MISC_CHARGES,0),length(ifnull(MISC_CHARGES,0))-1,1) = '-' then (substr(ifnull(MISC_CHARGES,0),1,length(ifnull(MISC_CHARGES,0)))+0)*-1 else ifnull(MISC_CHARGES,0)+0 end MISC_CHARGES," +
                "case when substr(ifnull(CR_ADJ,0),length(ifnull(CR_ADJ,0)),1) ='-' then (substr(ifnull(CR_ADJ,0),1,length(ifnull(CR_ADJ,0))-1)+0)*-1 else ifnull(CR_ADJ,0)+0 end CR_ADJ," +
                "case when substr(ifnull(DB_ADJ,0),length(ifnull(DB_ADJ,0))-1,1) ='-' then (substr(ifnull(DB_ADJ,0),1,length(ifnull(DB_ADJ,0))-1)+0)*-1 else ifnull(DB_ADJ,0)+0 end DB_ADJ," +
                "PRV_BILLED_AMT," +
                "case when substr(ifnull(PREVIOUS_BILLED_PROV_UNIT,0),length(ifnull(PREVIOUS_BILLED_PROV_UNIT,0)),1) = '-' then (substr(ifnull(PREVIOUS_BILLED_PROV_UNIT,0),1,length(ifnull(PREVIOUS_BILLED_PROV_UNIT,0))-1)+0)*-1 else ifnull(PREVIOUS_BILLED_PROV_UNIT,0)+0 end PREVIOUS_BILLED_PROV_UNIT," +
                "LAST_PAID_DATE,LAST_PYMT_RCPT," +
                "case when substr(ifnull(LAST_PAID_AMT,0),length(ifnull(LAST_PAID_AMT,0)),1) ='-' then (substr(ifnull(LAST_PAID_AMT,0),1,length(ifnull(LAST_PAID_AMT,0))-1)+0)*-1 else ifnull(LAST_PAID_AMT,0)+0 end LAST_PAID_AMT," +
                "ED_EXEMPT,AIFI,NEW_METER_NO,SDI,ASD,ASDAA,INSTALLATION,SBM_BILL_NO,CA,PRV_ARR," +
                "case when substr(ifnull(ARREARS,0),length(ifnull(ARREARS,0)),1) ='-' then (substr(ifnull(ARREARS,0),1,length(ifnull(ARREARS,0))-1)+0)*-1 else ifnull(ARREARS,0)+0 end ARREARS," +
                "case when  substr(ifnull(ULF,0),length(ifnull(ULF,0))-1,1) ='-' then (substr(ifnull(ULF,0),1,length(ifnull(ULF,0))-1)+0)*-1 else ifnull(ULF,0)+0 end ULF," +
                "case when substr(ifnull(PREV_BILL_UNITS,0),length(ifnull(PREV_BILL_UNITS,0)),1) ='-' then (substr(ifnull(PREV_BILL_UNITS,0),1,length(ifnull(PREV_BILL_UNITS,0))-1)+0)*-1 else ifnull(PREV_BILL_UNITS,0)+0 end PREV_BILL_UNITS," +
                "BILL_MONTH," +
                "case when substr(ifnull(ECS_LIMT,0),length(ifnull(ECS_LIMT,0)),1) ='-' then (substr(ifnull(ECS_LIMT,0),1,length(ifnull(ECS_LIMT,0))-1)+0)*-1 else ifnull(ECS_LIMT,0)+0 end ECS_LIMT," +
                "ECS_VALIDITY_PERIOD,PRESENT_READING_REMARK,PRESENT_METER_STATUS," +
                "case when substr(ifnull(PRESENT_BILL_UNITS,0),length(ifnull(PRESENT_BILL_UNITS,0)),1) ='-' then (substr(ifnull(PRESENT_BILL_UNITS,0),1,length(ifnull(PRESENT_BILL_UNITS,0))-1)+0)*-1 else ifnull(PRESENT_BILL_UNITS,0)+0 end PRESENT_BILL_UNITS," +
                "PRESENT_BILL_TYPE," +
                "case when substr(ifnull(EC,0),length(ifnull(EC,0)),1) ='-' then (substr(ifnull(EC,0),1,length(ifnull(EC,0))-1)+0)*-1 else ifnull(EC,0)+0 end EC," +
                "case when substr(ifnull(MMFC,0),length(ifnull(MMFC,0)),1) ='-' then (substr(ifnull(MMFC,0),1,length(ifnull(MMFC,0))-1)+0)*-1 else ifnull(MMFC,0)+0 end MMFC," +
                "case when substr(ifnull(MRENT_CHARGED,0),length(ifnull(MRENT_CHARGED,0))-1,1) ='-' then (substr(ifnull(MRENT_CHARGED,0),1,length(ifnull(MRENT_CHARGED,0))-1)+0)*-1 else ifnull(MRENT_CHARGED,0)+0 end MRENT_CHARGED," +
                "case when substr(ifnull(ED,0),length(ifnull(ED,0)),1) ='-' then (substr(ifnull(ED,0),1,length(ifnull(ED,0))-1)+0)*-1 else ifnull(ED,0)+0 end ED," +
                "CURRENT_BILL_TOTAL,REBATE,AMOUNT_PAYABLE,AVG_UNIT_BILLED,RCPTNO,CHQNO,CHQDT,BANK,RCPTAMT,DUE_DATE,DO_EXPIRY,PRESENT_READING_TIME,OSBILL_DATE,CAPTURED_MOBILE,SCHEDULE_METER_READ_DATE,METER_RENT,PORTION,MRU,NO_OF_REG,SAN_LOAD_UNITS,SAN_LOAD_EFFECTIVE_DATE,SUPPLY_TYP_FLG,NOTINUSE_FLG_ENDDATE,OTHER_FLGS,ED_RBT,HOSTEL_RBT,PREV_BILL_TYPE,PREV_BILL_REMARK,PREV_BILL_END_DATE,LAST_NORMAL_BILL_DATE,PRESENT_READING_DATE," +
                "case when substr(ifnull(AVERAGE_KWH,0),length(ifnull(AVERAGE_KWH,0)),1) ='-' then (substr(ifnull(AVERAGE_KWH,0),1,length(ifnull(AVERAGE_KWH,0))-1)+0)*-1 else ifnull(AVERAGE_KWH,0)+0 end AVERAGE_KWH," +
                "BILL_BASIS,BILL_NO,INVOICE_NO,READ_ONLY,BP_START_DATE,BP_END_DATE,PPAC,ROUND_OFF," +
                "case when substr(ifnull(SEC_DEPOSIT_AMT,0),length(ifnull(SEC_DEPOSIT_AMT,0)),1) ='-' then (substr(ifnull(SEC_DEPOSIT_AMT,0),1,length(ifnull(SEC_DEPOSIT_AMT,0))-1)+0)*-1 else ifnull(SEC_DEPOSIT_AMT,0)+0 end SEC_DEPOSIT_AMT," +
                "DO_GENERATED,NOT_TO_BILL_AFTER,INSERT_DATE,INSERT_TIME,UPDATE_DATE,UPDATE_TIME,PROGRESSION_STATE,PHONE_1,PHONE_2,BILL_YEAR,TRANS_TYPE,TD_FLAG,TD_DATE," +
                "case when substr(ifnull(PPI,0),length(ifnull(PPI,0)),1) ='-' then (substr(ifnull(PPI,0),1,length(ifnull(PPI,0))-1)+0)*-1 else ifnull(PPI,0)+0 end PPI," +
                "PREV_PROV_AMT,GST_RELEVANT1,GST_RELEVANT2,GST_RELEVANT3,GST_RELEVANT4,ELT_STTS,SEAL_STTS,CBLASS,HL_MONTHS,MRREASON,USAGE_ID,METER_TYPE_ID,SUPPLY_STATUS_ID,REASON_DC_ID,SUPPLY_SOURCE_ID,REASON_NV_ID,REASON_CD_ID,REASON_EN_ID,REASON_MTR_STUCK_ID,PAPER_PASTE_BY_ID,METER_HEIGHT_ID,TYPES_OBSTACLE_ID,SEAL_STS_ID,CONTACT_REASON_ID,REASON_PL_ID,BUILDING_DESC,BUILDING_CODE,POLE_NO,FLAG,SPECIAL_REM,GPS_LONGITUDE,GPS_LATITUDE,SENDER_MOBILE,CONSUMER_TYPE,WALKING_SEQ_CHK,METER_LOCA,MR_REMARK_DET,STOP_PAPR_BL,NEW_MTR_FLG,OLD_MTR_COR_FLG,UNSAFE_COND,READ_FLAG,SENT_FLAG,MTR_IMAGE,UPD_FLAG1,UPD_FLAG2,REVISIT_FLAG,UNIT_SLAB1,RATE_SLAB1,EC_SLAB1,UNIT_SLAB2,RATE_SLAB2,EC_SLAB2,UNIT_SLAB3,RATE_SLAB3,EC_SLAB3,UNIT_SLAB4,RATE_SLAB4,EC_SLAB4,BILL_PRN_FOOTER,BILL_PRN_HEADER,HL_UNIT,REFERENCE_DATE,NO_BILLED_MONTH,REF_MR_DATE,RURAL,SWJL_DH_FL,RCRD_LOAD,ULF_MDI,DPS_5," +
                "case when substr(ifnull(DPS_BILLED,0),length(ifnull(DPS_BILLED,0)),1) ='-' then (substr(ifnull(DPS_BILLED,0),1,length(ifnull(DPS_BILLED,0))-1)+0)*-1 else ifnull(DPS_BILLED,0)+0 end DPS_BILLED," +
                "DPS_LVD,PROV_PPT_AMT,DPS_BLLD,FC_SLAB,USER_TYPE,PROV_ED,MOD_DATE,RQCFLG,BILL_START_DATE,BILL_END_DATE,DISCONNECTION_FLG,BILLED_MD,OFFICE_CODE,ADJ_BILL " +
                " from " + DatabaseKeys.TABLE_HEADER + condition + " GROUP by INSTALLATION";
        Log.e("conditionHeader", condition);
        Log.e("UserHeaderdeatils", query);
        cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor getUserChilddetails(String condition) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.e("ChildQueryCOndition", condition);
        //String query = "SELECT distinct * FROM " + DatabaseKeys.TABLE_CHILD + condition;
        //String query = "select * from (SELECT distinct * FROM " + DatabaseKeys.TABLE_CHILD + condition+")";
        /*String query = "select * from(select distinct ABLBELNR,METER_INSTALL_DATE,PREV_READ_DATE,SCHEDULE_METER_READ_DATE,REFERENCE_DATE, BILL_MONTH,USER_TYPE,CAL_MON_CNT, BILL_YEAR, BILLED_MD, CONSUMPTION_OLD_METER, EQUIPMENT_NO, INSTALLATION, LAST_OK_RDNG, METER_CONDITION, METER_NO, METER_REMOVED_ON, METER_TYP, MF, MRREASON, NEW_METER_FLG, NO_OF_DIGITS, PRESENT_METER_READING, PREV_MTR_READ, PREV_READ_DATE, PREVIOUS_MD, PRS_MD, REFERENCE_DATE, REGISTER_CODE," +
                "SCHEDULE_METER_READ_DATE,  NO_OF_REG, TRANS_TYPE " +
                "from TBL_SPOTBILL_CHILD_DETAILS " + condition + ")";*/
        /*if (condition.isEmpty() || condition.length() < 5) {
            Log.e("CONDITIONPROB", condition);
        }
*/
        if (condition.contains("CA")) {
            condition = "where INSTALLATION='" + getInstallatioationNoBYCA(condition) + "'";
        }

        if (condition.contains("LEGACY_ACCOUNT_NO")) {
            condition = "where INSTALLATION='" + getInstallatioationNoBYCA(condition) + "'";
        }

        if (condition.contains("PHONE_1")) {
            condition = "where INSTALLATION='" + getInstallatioationNoBYCA(condition) + "'";
        }

        Log.e("ChildQueryCOndition1", condition);

        String query = "SELECT * FROM (select * from(select distinct ABLBELNR,PREV_READ_DATE,SCHEDULE_METER_READ_DATE," +
                "REFERENCE_DATE," +
                "BILL_MONTH,USER_TYPE,CAL_MON_CNT, BILL_YEAR, BILLED_MD, CONSUMPTION_OLD_METER, EQUIPMENT_NO," +
                " INSTALLATION, LAST_OK_RDNG, METER_CONDITION, METER_NO, METER_REMOVED_ON, METER_TYP, MF," +
                " MRREASON, NEW_METER_FLG, NO_OF_DIGITS, PRESENT_METER_READING, PREV_MTR_READ, PREVIOUS_MD," +
                " PRS_MD, REGISTER_CODE,  NO_OF_REG, TRANS_TYPE from TBL_SPOTBILL_CHILD_DETAILS " + condition + "))" + " GROUP BY INSTALLATION, METER_NO, REGISTER_CODE";

        Log.e("ChildQuery", query);
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void updatesendFlag(String condition) {
        Log.e("Update1234condition", condition);
        String USER_TYPE = "";
        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            USER_TYPE = "S";
        } else {
            USER_TYPE = "X";
        }
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            if (condition.contains("METER_NO")) {
                String installation = "";
                installation = getInstallatioationNo(condition, -1);
                Log.e("FAKEINSTALLAION", installation);
                condition = " where installation='" + installation + "'";
            }
            String strSelectSQL_02 = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET SENT_FLAG=1" + condition + "  and USER_TYPE='" + USER_TYPE + "'";
            db.execSQL(strSelectSQL_02);
            Log.e("SingleUpdateFlag", strSelectSQL_02);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    public void updatesendFlag1(String condition) {
        Log.e("Update1234condition", condition);
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String strSelectSQL_02 = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET SENT_FLAG=1" + condition + "";
            db.execSQL(strSelectSQL_02);
            Log.e("SingleUpdateFlag", strSelectSQL_02);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }


    public int CheckHeqaderDuplicate() {
        //String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
        String countQuery = "SELECT COUNT(*) FROM (select INSTALLATION,CA,COUNT(*) FROM TBL_SPOTBILL_HEADER_DETAILS GROUP BY INSTALLATION,CA HAVING COUNT(*) > 1)";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public int CheckChildDuplicate() {
        //String countQuery = "SELECT  * FROM " + Note.TABLE_NAME;
        String countQuery = "SELECT COUNT(*) FROM (SELECT INSTALLATION,REGISTER_CODE,COUNT(*) FROM TBL_SPOTBILL_CHILD_DETAILS GROUP BY INSTALLATION,REGISTER_CODE HAVING COUNT(*) > 1)";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    ///////////////////avik need to change the function///////////////////
    public void updateMassSentflag(String installcommaseperated) {
        String USER_TYPE = "";
        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            USER_TYPE = "S";
        } else {
            USER_TYPE = "X";
        }

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET SENT_FLAG=1" + " where" + " INSTALLATION in " + installcommaseperated;
            Log.d("SentMassFlag", query);
            db.execSQL(query);
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


    public void deleteMSTUSER() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Mst_User");
        db.close();
    }

    public void deleteDataChild(String installationNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "TBL_SPOTBILL_CHILD_DETAILS" + " WHERE " + "INSTALLATION" + "='" + installationNo + "'");
        db.close();
    }


    public void deleteDataHeaderInstallationWise(String USER_TYPE) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM TBL_SPOTBILL_HEADER_DETAILS where USER_TYPE='X'");
        db.close();
    }

    public void deleteDataChildInstallationWise(String USER_TYPE) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE TBL_SPOTBILL_CHILD_DETAILS set  USER_TYPE='X' where INSTALLATION in(select  INSTALLATION FROM TBL_SPOTBILL_HEADER_DETAILS where USER_TYPE='X')");
        db.execSQL("DELETE FROM TBL_SPOTBILL_CHILD_DETAILS where USER_TYPE='X'");
        db.close();
    }

    public void deleteDataHeaderInstallationWiseSBM(String USER_TYPE) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE TBL_SPOTBILL_CHILD_DETAILS set  USER_TYPE='S' where INSTALLATION in(select  INSTALLATION FROM TBL_SPOTBILL_HEADER_DETAILS where USER_TYPE='S')");
        db.execSQL("DELETE FROM TBL_SPOTBILL_HEADER_DETAILS where USER_TYPE='S'");
        db.close();
    }

    public void deleteDataChildInstallationWiseSBM(String USER_TYPE) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM TBL_SPOTBILL_CHILD_DETAILS where USER_TYPE='S'");
        db.close();
    }


    public void deleteDataHeader1(String USER_TYPE) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM TBL_SPOTBILL_HEADER_DETAILS");
        db.close();
    }

    public void deleteDataChild1(String USER_TYPE) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM TBL_SPOTBILL_CHILD_DETAILS");
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

    public void updateFileDesc(String strSQL) {
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


    public void updateMrReasonChild(String installation, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        String USER_TYPE = "";
        if (PreferenceHandler.getisSBNONSBFLAG(mContext).equalsIgnoreCase("SBM")) {
            USER_TYPE = "S";
        } else {
            USER_TYPE = "X";
        }

        String sqlquery = "";
        sqlquery = "UPDATE TBL_SPOTBILL_CHILD_DETAILS SET MRREASON= '" + reason + "' and READ_FLAG='1'";
        sqlquery = sqlquery + " where INSTALLATION='" + installation + "'";
        Log.e("updateQuery", sqlquery);
        db.execSQL(sqlquery);
        db.close();
    }


    public void updateNONSBMData(String installation, String reason) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlquery = "";
        //sqlquery = "UPDATE TBL_SPOTBILL_CHILD_DETAILS SET MRREASON= '" + reason + "' ";
        sqlquery = "UPDATE TBL_SPOTBILL_CHILD_DETAILS SET MRREASON= '" + reason + "' ";
        sqlquery = sqlquery + " where INSTALLATION='" + installation + "'";
        Log.e("updateQuery", sqlquery);
        db.execSQL(sqlquery);
        db.close();
    }

    public String getReportData(String query) {
        String count = "";
        Log.e("selectQuery", query);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        List<String> list = new ArrayList<>();
        list.clear();
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getString(cursor.getColumnIndex("LEGACY_ACCOUNT_NO"));
            } while (cursor.moveToNext());
        }
        db.close();
        return count;
    }


    public String getUserType(String Installation) {
        String USER_TYPE = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select USER_TYPE from TBL_SPOTBILL_HEADER_DETAILS WHERE INSTALLATION='" + Installation + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                USER_TYPE = cursor.getString(cursor.getColumnIndex("USER_TYPE"));
            }
        }

        return USER_TYPE;
    }

    public void MoveDataToTemp() {
        //String strSelectSQL_01= "DELETE FROM TBL_SPOTBILL_HEADER_DETAILS_TEMP WHERE (strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', OSBILL_DATE)*12 + strftime('%m', OSBILL_DATE))>3";
        SQLiteDatabase db = this.getWritableDatabase();

        ///move header from main table to Temp table
        String q2 = "INSERT INTO TBL_SPOTBILL_HEADER_DETAILS_TEMP SELECT * FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG = '1'";
        db.execSQL(q2);

        ///move child from main table to Temp table
        //String q3 = "INSERT INTO TBL_SPOTBILL_CHILD_DETAILS_TEMP SELECT A.* FROM TBL_SPOTBILL_CHILD_DETAILS A, TBL_SPOTBILL_HEADER_DETAILS B WHERE A.INSTALLATION = B.INSTALLATION AND B.READ_FLAG='1'";
        String q3 = "INSERT INTO TBL_SPOTBILL_CHILD_DETAILS_TEMP SELECT * FROM TBL_SPOTBILL_CHILD_DETAILS WHERE READ_FLAG = '1'";
        db.execSQL(q3);

       /* ///delete all data from header table
        String q0 = "DELETE FROM TBL_SPOTBILL_HEADER_DETAILS";
        db.execSQL(q0);

        ///delete all data from child table
        String q4 = "DELETE FROM TBL_SPOTBILL_CHILD_DETAILS";
        db.execSQL(q4);*/

        /////DELETE AFTER 60 DAYS FROM TEMP TABLE after checking date
        String q5 = "DELETE FROM TBL_SPOTBILL_HEADER_DETAILS_TEMP WHERE (strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', SCHEDULE_METER_READ_DATE)*12 + strftime('%m', SCHEDULE_METER_READ_DATE))>2";
        db.execSQL(q5);

        String q6 = "DELETE FROM TBL_SPOTBILL_CHILD_DETAILS_TEMP WHERE (strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', SCHEDULE_METER_READ_DATE)*12 + strftime('%m', SCHEDULE_METER_READ_DATE))>2";
        db.execSQL(q6);

        db.close();
    }


    public String getMonthName(String monthNumber) {
        String month_name = "";
        Log.e("monthNumbermonthNumber", monthNumber);
        if (monthNumber != null) {
            try {
                SQLiteDatabase db = this.getReadableDatabase();
                String query = "SELECT " +
                        "case when '" + monthNumber + "'='01' OR '" + monthNumber + "'='1' then 'JANUARY' " +
                        "when '" + monthNumber + "'='02' OR '" + monthNumber + "'='2' then 'FEBRUARY' " +
                        "when '" + monthNumber + "'='03' OR '" + monthNumber + "'='3' then 'MARCH' " +
                        "when '" + monthNumber + "'='04' OR '" + monthNumber + "'='4' then 'APRIL' " +
                        "when '" + monthNumber + "'='05' OR '" + monthNumber + "'='5' then 'MAY' " +
                        "when '" + monthNumber + "'='06' OR '" + monthNumber + "'='6' then 'JUNE' " +
                        "when '" + monthNumber + "'='07' OR '" + monthNumber + "'='7' then 'JULY' " +
                        "when '" + monthNumber + "'='08' OR '" + monthNumber + "'='8' then 'AUGUST' " +
                        "when '" + monthNumber + "'='09' OR '" + monthNumber + "'='9' then 'SEPTEMBER' " +
                        "when '" + monthNumber + "'='10' OR '" + monthNumber + "'='10' then 'OCTOBER' " +
                        "when '" + monthNumber + "'='11' OR '" + monthNumber + "'='11' then 'NOVEMBER' " +
                        "when '" + monthNumber + "'='12' OR '" + monthNumber + "'='12' then 'DECEMBER' " +
                        "end MONTH ";
                Log.e("MONTHQUERY", query);
                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    do {
                        month_name = cursor.getString(cursor.getColumnIndex("MONTH"));
                    } while (cursor.moveToNext());
                }

                Log.e("month_name", month_name);
                // db.execSQL(month_name);
                return month_name;
            } catch (Exception e) {
            }
        }
        return month_name;
    }

    public long insertNote(String note, String installation) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put("RAWDATA", note);

        // insert row
        long id = db.insert("TBL_UPLOAD_RAW_DATA", null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }
}






