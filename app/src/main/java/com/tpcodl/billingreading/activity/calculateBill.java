package com.tpcodl.billingreading.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tpcodl.billingreading.BuildConfig;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.database.DatabaseHelper;

import java.sql.SQLException;
import java.util.StringTokenizer;

import static java.lang.Math.pow;
import static java.lang.Math.round;

/**
 * Created by cesu-user on 24-11-2015.
 */
public class calculateBill {
    private static Context mContext;
    public static long lULimit = 10000;

    public static String deriveSupplyType(double dK_Volt) {
        String StrSupply = "NA";
        if (dK_Volt >= 0 && dK_Volt < 11) {
            StrSupply = "LT";
        } else if (dK_Volt >= 11 && dK_Volt <= 66) {
            StrSupply = "HT";
        } else if (dK_Volt > 66) {
            StrSupply = "EHT";
        } else {
            StrSupply = "NA";
        }
        return StrSupply;
    }

    public static int tariffMasterFormation(String strRateType, String StrSupplyType, String strTariff, long lConLoad, String strConLoadUoM, double month_cnt) {
        int sucFlg = 0;
        DatabaseHelper helper = new DatabaseHelper(mContext);
        double noofmon = month_cnt;
        String strSelectSQL_03 = "DELETE FROM MST_TARIFFS_TEMP";
        Log.d("DemoApp", "strSelectSQL_03 " + strSelectSQL_03);
        helper.deleletTariffTemp(strSelectSQL_03);
        delTariffTable(strSelectSQL_03);
        //DatabaseAccess.database.execSQL(strSelectSQL_03);

        String strSelectSQL_02 = "SELECT TARIFF,SUPPLY_TYPE,RATE_TYPE,SLAB_START,SLAB_END,UNIT_INCREMENT," +
                "VALIDITY_START,VALIDITY_END,RATE,RATE_ABSOLUTE,CD_LIMIT_START,CD_LIMIT_END," +
                "SLAB_TYPE,TARIFF_ALIAS,SLAB_SERIAL,DC_UNIT,RST_SRL,TARIFF_SECOND,TARIFF_SECOND_UNIT,REMARKS"
                + " FROM MST_TARIFFS WHERE RATE_TYPE = '" + strRateType + "'"
                + " AND TARIFF = '" + strTariff + "'"
                + " AND SUPPLY_TYPE = '" + StrSupplyType + "'"
                + " AND " + lConLoad * deriveLoadUoM_Factor(strConLoadUoM) + " BETWEEN CD_LIMIT_START AND CASE WHEN CD_LIMIT_END= -1 THEN 9999999999 ELSE CD_LIMIT_END END "
                + " AND DATE('now') BETWEEN VALIDITY_START AND VALIDITY_END"
                + " ORDER BY SLAB_START ASC";
        Log.d("DemoApp", "strUpdateSQL_01 for tariff  " + strSelectSQL_02);
        Cursor rs1 = helper.getCalculateedData(strSelectSQL_02);

        double dSlabStartr = 0;
        double dSlabEndr = 0;
        double dSlabunt = 0;
        double valrndup = 0;
        double dSlabEnd = 0;
        String strSQL = "";
        while (rs1.moveToNext()) {
            sucFlg = 1;
            double dSlabStart = rs1.getDouble(3);//0  100  300
            dSlabStartr = dSlabEndr; //0     123   368
            dSlabEnd = rs1.getDouble(4);//100  300 -1
            dSlabunt = dSlabEnd - dSlabStart;//100   200   300


            if (dSlabEnd != -1) {
                dSlabunt = dSlabunt * noofmon;//122.58  245.16
                dSlabEnd = dSlabunt;//122.58    //245.16
                dSlabEnd = dSlabEnd + valrndup;//122.58    244.74
                dSlabEndr = round(dSlabEnd);//123        245
                valrndup = dSlabEnd - dSlabEndr;//-.42    -0.26
                dSlabEndr = dSlabStartr + dSlabEndr;//123   123+245
                //  dSlabunt=dSlabunt*noofmon;//122.58  245.16
            } else {
                dSlabEndr = dSlabEnd;
            }

            Log.d("DemoApp", " found dSlabEnd" + dSlabEnd);
            Log.d("DemoApp", " found dSlabEndr" + dSlabEndr);
            Log.d("DemoApp", " found valrndup" + valrndup);
            //  dSlabStartr=dSlabStart;
            //if(dSlabStart!=0){
            //  dSlabStartr=dSlabEndr;
            //}
            String tariff = rs1.getString(0);
            String suptyp = rs1.getString(1);
            String rttyp = rs1.getString(2);
            String untcr = rs1.getString(5);
            String valst = rs1.getString(6);
            String valend = rs1.getString(7);
            String rate = rs1.getString(8);
            String rateabs = rs1.getString(9);
            String cdstrt = rs1.getString(10);
            String cdend = rs1.getString(11);
            String slbtyp = rs1.getString(12);
            String tarali = rs1.getString(13);
            String slbser = rs1.getString(14);
            String dcunt = rs1.getString(15);
            String rst = rs1.getString(16);
            String tarsec = rs1.getString(17);
            String tarunt = rs1.getString(18);
            String rem = rs1.getString(19);
            strSQL = "INSERT INTO MST_TARIFFS_TEMP(TARIFF,SUPPLY_TYPE,RATE_TYPE,SLAB_START,SLAB_END,UNIT_INCREMENT," +
                    "VALIDITY_START,VALIDITY_END,RATE,RATE_ABSOLUTE,CD_LIMIT_START,CD_LIMIT_END," +
                    "SLAB_TYPE,TARIFF_ALIAS,SLAB_SERIAL,DC_UNIT,RST_SRL,TARIFF_SECOND,TARIFF_SECOND_UNIT,REMARKS) " +
                    " VALUES('" + tariff + "','" + suptyp + "','" + rttyp + "','" + dSlabStartr + "','" + dSlabEndr + "'," +
                    " '" + untcr + "','" + valst + "','" + valend + "','" + rate + "'," +
                    " '" + rateabs + "','" + cdstrt + "','" + cdend + "','" + slbtyp + "','" + tarali + "', " +
                    " '" + slbser + "','" + dcunt + "','" + rst + "','" + tarsec + "','" + tarunt + "', " +
                    " '" + rem + "')";
            Log.d("DemoApp", "strSelectSQ tariff insset" + strSQL);
            //  DatabaseAccess.database.execSQL(strSQL);
            helper.insertIntoTEMPTARIFF(strSQL);
        }

//////////////////////////////////////////////////////////////////////
        return sucFlg;
    }

    public static double deriveLoadUoM_Factor(String strConLoadUoM) {
        double dLoadUoM_Factor = 0;
        if (strConLoadUoM.equals("KW")) {
            dLoadUoM_Factor = 1;
        } else if (strConLoadUoM.equals("KVA")) {
            dLoadUoM_Factor = 0.909;
        } else if (strConLoadUoM.equals("HP")) {
            dLoadUoM_Factor = 0.746;
        } else {
            dLoadUoM_Factor = 1;
        }
        return 0;
    }

    public static double getED_Applicable(Double strED_Exempt) {
        double lED_Applicable = 0;
        if (strED_Exempt == 1) {
            lED_Applicable = 0;
        } else if (strED_Exempt == 0) {
            lED_Applicable = 1;
        }
        return lED_Applicable;
    }

    //move to here///
    static double UNIT_SLAB1 = 0;
    static double UNIT_SLAB2 = 0;
    static double UNIT_SLAB3 = 0;
    static double UNIT_SLAB4 = 0;
    static double RATE_SLAB1 = 0;
    static double RATE_SLAB2 = 0;
    static double RATE_SLAB3 = 0;
    static double RATE_SLAB4 = 0;
    static double EC_SLAB1 = 0;
    static double EC_SLAB2 = 0;
    static double EC_SLAB3 = 0;
    static double EC_SLAB4 = 0;

    /////
    public static String getBasicRate(String strAccNo, String strRateType, double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, double lUnits, double month_cnt) throws SQLException {
        DatabaseHelper helper = new DatabaseHelper(mContext);
        double lUnitsTot = lUnits;
        double lUnitsRemain = lUnits;
        double dTotValue = 0;
        String strResult = "";

        String StrSupplyType = deriveSupplyType(dK_Volt);
        ////////////////////SAP//////////////////////////////////////
        int sucFlg = tariffMasterFormation(strRateType, StrSupplyType, strTariff, lConLoad, strConLoadUoM, month_cnt);
////////////////////////////////////////////////
        String strSelectSQL_01 = "SELECT SLAB_START, CASE WHEN SLAB_END = -1 THEN 9999999999 ELSE SLAB_END END AS SLAB_END, "
                + " UNIT_INCREMENT, RATE, RATE_ABSOLUTE, SLAB_TYPE, DC_UNIT, TARIFF_SECOND, TARIFF_SECOND_UNIT , SLAB_SERIAL"
                + " FROM MST_TARIFFS_TEMP WHERE RATE_TYPE = '" + strRateType + "'"
                + " AND TARIFF = '" + strTariff + "'"
                + " AND SUPPLY_TYPE = '" + StrSupplyType + "'"
                + " AND " + lConLoad * deriveLoadUoM_Factor(strConLoadUoM) + " BETWEEN CD_LIMIT_START AND CASE WHEN CD_LIMIT_END= -1 THEN 9999999999 ELSE CD_LIMIT_END END "
                + " AND DATE('now') BETWEEN VALIDITY_START AND VALIDITY_END"
                + " AND " + lUnits + " >= SLAB_START"
                + " ORDER BY SLAB_START DESC";
        Log.e("BaseRateCal", strSelectSQL_01);
        Log.d("DemoApp", "strUpdateSQL_01 for tariff  " + strSelectSQL_01);

        Cursor rs = helper.getCalculateedData(strSelectSQL_01);

        while (rs.moveToNext()) {
            double dSlabStart = rs.getDouble(0);
            double dSlabEnd = rs.getDouble(1);
            double dUnitIncr = rs.getDouble(2);
            double dRate = rs.getDouble(3);
            Integer dRateAbsolute = rs.getInt(4);
            Integer dSlabType = rs.getInt(5);
            String strDCBillUnit = rs.getString(6);
            String strSecTariff = rs.getString(7);
            double dSecTariffULimit = rs.getDouble(8);
            Integer iSlabSerial = rs.getInt(9);
            double dSlabUnits = 0;
            double dSlabValue = 0;
            double dSlabValue1 = 0;
            if (dRateAbsolute == 2) {
                dSlabUnits = Math.min(dSlabEnd, lUnitsRemain) - dSlabStart;
                lUnitsRemain = lUnitsRemain - dSlabUnits;
                dSlabValue = Math.ceil(dSlabUnits / dUnitIncr) * dRate / 100;
                dTotValue = dTotValue + dSlabValue;
            } else {
                dSlabUnits = Math.min(dSlabEnd, lUnitsRemain) - dSlabStart;
                lUnitsRemain = lUnitsRemain - dSlabUnits;
                dSlabValue = Math.ceil(dSlabUnits / dUnitIncr) * dRate;
                dSlabValue1 = Math.round(dSlabUnits / dUnitIncr) * dRate;
                dTotValue = dTotValue + dSlabValue;
                Log.d("DemoApp", " found dSlabUnits" + dSlabUnits);
                Log.d("DemoApp", " found dUnitIncr" + dUnitIncr);
                Log.d("DemoApp", " found lUnitsRemain" + lUnitsRemain);
                Log.d("DemoApp", " found dSlabValue" + dSlabValue);
                Log.d("DemoApp", " found dSlabValue1  " + dSlabValue1);
                Log.d("DemoApp", " found dTotValue" + dTotValue);
            }
            String strResultRecord = dSlabUnits + "|" + dRate + "|" + dRateAbsolute + "|" + dSlabType + "|" + dSlabValue + ";";
            strResult = strResult + strResultRecord;
            //disable on 27.2.17
            //   if (iSlabSerial == 1 ){ UNIT_SLAB1 = dSlabUnits;RATE_SLAB1=dRate;EC_SLAB1=dSlabValue;}
            //   else if (iSlabSerial == 2 ){ UNIT_SLAB2 = dSlabUnits;RATE_SLAB2=dRate;EC_SLAB2=dSlabValue;}
            //    else if (iSlabSerial == 3 ){ UNIT_SLAB3 = dSlabUnits;RATE_SLAB3=dRate;EC_SLAB3=dSlabValue;}
            //   else if (iSlabSerial == 4 ){ UNIT_SLAB4 = dSlabUnits;RATE_SLAB4=dRate;EC_SLAB4=dSlabValue;}
            //    else { UNIT_SLAB1 = 0;RATE_SLAB1=0;EC_SLAB1=0;}
            ////end///
            ////Added for multiple months 27.02.17
            if (strRateType.equals("KWH")) {
                if (iSlabSerial == 1) {
                    UNIT_SLAB1 = UNIT_SLAB1 + dSlabUnits;
                    RATE_SLAB1 = dRate;
                    EC_SLAB1 = EC_SLAB1 + dSlabValue;
                } else if (iSlabSerial == 2) {
                    UNIT_SLAB2 = UNIT_SLAB2 + dSlabUnits;
                    RATE_SLAB2 = dRate;
                    EC_SLAB2 = EC_SLAB2 + dSlabValue;
                } else if (iSlabSerial == 3) {
                    UNIT_SLAB3 = UNIT_SLAB3 + dSlabUnits;
                    RATE_SLAB3 = dRate;
                    EC_SLAB3 = EC_SLAB3 + dSlabValue;
                } else if (iSlabSerial == 4) {
                    UNIT_SLAB4 = UNIT_SLAB4 + dSlabUnits;
                    RATE_SLAB4 = dRate;
                    EC_SLAB4 = EC_SLAB4 + dSlabValue;
                } else {
                    UNIT_SLAB1 = 0;
                    RATE_SLAB1 = 0;
                    EC_SLAB1 = 0;
                }
            }
            /////
        }
        strResult = "#" + dTotValue + "#;" + strResult;
        //  if (ConnectDBSqllite.print_flag == 1) System.out.println("##### Fetching Rates ::" + strTariff + "|" + strRateType + "|" + strResult);
        rs.close();
        //for slabwwise data in kwh
        if (strRateType.equals("KWH")) {

            String strUpdateSQL_01 = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET "
                    + " UNIT_SLAB4= " + UNIT_SLAB4 + ","
                    + " RATE_SLAB4= " + RATE_SLAB4 + ","
                    + " EC_SLAB4= " + EC_SLAB4 + ","
                    + " UNIT_SLAB3= " + UNIT_SLAB3 + ","
                    + " RATE_SLAB3= " + RATE_SLAB3 + ","
                    + " EC_SLAB3= " + EC_SLAB3 + ","
                    + " UNIT_SLAB2= " + UNIT_SLAB2 + ","
                    + " RATE_SLAB2= " + RATE_SLAB2 + ","
                    + " EC_SLAB2= " + EC_SLAB2 + ","
                    + " UNIT_SLAB1= " + UNIT_SLAB1 + ","
                    + " RATE_SLAB1= " + RATE_SLAB1 + ","
                    + " EC_SLAB1= " + EC_SLAB1
                    + " WHERE INSTALLATION = '" + strAccNo + "'";
            Log.d("DemoApp", " found CalculateEC_KWH strUpdateSQL_01" + strUpdateSQL_01);
            helper.updateSlab(strUpdateSQL_01);
            // DatabaseAccess.database.execSQL(strUpdateSQL_01);
        }
        return strResult;
    }

    public static double CalculateEC_KWH(String strAccNo, double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, long lUnits, double month_cnt) {
        DatabaseHelper helper = new DatabaseHelper(mContext);
        String strRateType = "KWH";
        String strResult = "";
        double fResultVal = 0;
        double fResultValTot = 0;
        //added on 27.02.2017///////
        UNIT_SLAB1 = 0;
        UNIT_SLAB2 = 0;
        UNIT_SLAB3 = 0;
        UNIT_SLAB4 = 0;
        RATE_SLAB1 = 0;
        RATE_SLAB2 = 0;
        RATE_SLAB3 = 0;
        RATE_SLAB4 = 0;
        EC_SLAB1 = 0;
        EC_SLAB2 = 0;
        EC_SLAB3 = 0;
        EC_SLAB4 = 0;
        //////////// End /////////////

        /*try {
            strResult = getBasicRate (strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, lUnits);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
            */
        try {
            Log.d("DemoApp", " found CalculateEC_KWH" + iBillMonths);
            long slabUnits = round((lUnits / month_cnt));
            // int slabUnitsRest = (int) (lUnits - (slabUnits * (iBillMonths - 1)));
            int slabUnitsRest = (int) (lUnits);
            if (slabUnits > 30 && strTariff.equals("BPL")) {
                strTariff = "DOM";
            }
          /*  for (int i=0; i< iBillMonths-1 ;i++)
            {
                Log.d("DemoApp", " found CalculateEC_KWH"+slabUnits);
                strResult = getBasicRate(strAccNo, strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, slabUnits);
                fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
                fResultValTot = fResultValTot + fResultVal;
                Log.d("DemoApp", " found CalculateEC_KWH"+fResultValTot);
            }*/
            Log.d("DemoApp", " found CalculateEC_KWH" + slabUnitsRest);
            strResult = getBasicRate(strAccNo, strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, slabUnitsRest, month_cnt);
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
            fResultValTot = fResultValTot + fResultVal;
            Log.d("DemoApp", " found CalculateEC_KWH" + fResultValTot);
            //fResultVal = fResultVal*iBillMonths;

        } catch (Exception e) {
            Log.d("DemoApp", " found CalculateEC_KWH" + e);
            e.printStackTrace();
        }
        //return fResultVal;
        return fResultValTot;
    }

    public static double CalculateMFC(String strAccNo, int iNewConnMonths, double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, long lUnits, long lUnits_Kwh, double month_cnt) {
        DatabaseHelper helper = new DatabaseHelper(mContext);
        Log.d("DemoApp", "mfc  ");
        String strRateType = "DC";
        String strResult = "";
        double fResultVal = 0;
        //  int slabUnits = Math.round((lUnits_Kwh / iBillMonths));

        long slabUnits = Math.round((lUnits_Kwh / month_cnt));
        int slabUnitsRest = (int) (lUnits_Kwh - (slabUnits * (iBillMonths - 1)));
        if (slabUnits > 30 && strTariff.equals("BPL")) {
            strTariff = "DOM";
        } else if (slabUnits <= 30 && strTariff.equals("BPL")) {
            lUnits = 1;// connected load
            lConLoad = 1;
        }
        try {
            strResult = getBasicRate(strAccNo, strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, lUnits, iBillMonths);

        } catch (SQLException e) {
            Log.d("DemoApp", " found getBasicRate" + e);
            e.printStackTrace();
        }
        Log.e("lConLoad", "" + lConLoad);

        try {
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
            Log.e("strResult", "" + fResultVal);
            //For Previous HLK
            //if (iBillMonths>iNewConnMonths){fResultVal = fResultVal*iNewConnMonths;}
            //For Other thaanPrevious HLK
            //else { fResultVal = fResultVal*iBillMonths;}

            //add on 19/02/2021
            fResultVal = fResultVal * iBillMonths;
            ////////////////////////////////////////
            Log.d("DemoApp", " MFC fResultVal" + fResultVal);

            //   fResultVal =  fResultVal*month_cnt;//sap
            Log.d("DemoApp", " MFC fResultVal SAP " + fResultVal);
            //fResultVal = fResultVal;
        } catch (Exception e) {
            Log.d("DemoApp", " found fResultVal" + e);
            e.printStackTrace();
        }
        return fResultVal;
    }

    public static double CalculateMR(String strAccNo, double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, long lUnits) {
        String strRateType = "MR";
        String strResult = "";
        double fResultVal = 0;
        try {
            strResult = getBasicRate(strAccNo, strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, lUnits, iBillMonths);
            Log.d("DemoApp", " found strResult" + strResult);
        } catch (SQLException e) {
            Log.d("DemoApp", " found CalculateMR" + e);
            e.printStackTrace();
        }
        try {
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
            Log.d("DemoApp", " found fResultVal" + fResultVal);
        } catch (Exception e) {
            Log.d("DemoApp", " found CalculateMR2" + e);
            e.printStackTrace();
        }
        return fResultVal;
    }

    public static double CalculateED(String strAccNo, double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, double lUnits) {
        String strRateType = "ED";
        String strResult = "";
        double fResultVal = 0;
        try {
            strResult = getBasicRate(strAccNo, strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, lUnits, iBillMonths);
        } catch (SQLException e) {
            Log.d("DemoApp", " found CalculateED" + e);
            e.printStackTrace();
        }
        try {
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
        } catch (Exception e) {
            Log.d("DemoApp", " found CalculateED2" + e);
            e.printStackTrace();
        }
        return fResultVal;
    }

    public static double CalculateRebate(String strAccNo, double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, double lUnits) {
        String strRateType = "REB";
        String strResult = "";
        double fResultVal = 0;
        try {
            strResult = getBasicRate(strAccNo, strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, lUnits, iBillMonths);
        } catch (SQLException e) {
            Log.d("DemoApp", " found CalculateRebate" + e);
            e.printStackTrace();
        }
        try {
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
        } catch (Exception e) {
            Log.d("DemoApp", " found CalculateRebate2" + e);
            e.printStackTrace();
        }
        return fResultVal;
    }

    public static double CalculateSplRebate(String strAccNo, double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, double lUnits) {
        String strRateType = "SREB";
        String strResult = "";
        double fResultVal = 0;
        try {
            strResult = getBasicRate(strAccNo, strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, lUnits, iBillMonths);
        } catch (SQLException e) {
            Log.d("DemoApp", " found CalculateSplRebate" + e);
            e.printStackTrace();
        }
        try {
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
        } catch (Exception e) {
            Log.d("DemoApp", " found CalculateSplRebate2" + e);
            e.printStackTrace();
        }
        return fResultVal;
    }

    public static void CalculateBill(String strAccNo, String mrReason, Context context, int rqc) throws SQLException {
        mContext = context;
        DatabaseHelper helper = new DatabaseHelper(context);
        Log.e("mrNoteReason", mrReason);

        if (mrReason.isEmpty()) {
            return;
        }

        try {
            String strBillDate = "";
            String strDueDate = "";
            String strDueDateSPP = "";
            String prs_mtrcond = "";
            double dAmountMFC = 0;
            double dAmountEC_KWH = 0;
            double dAmountMR = 0;
            double dAmountRebateBillCurr = 0;
            double dAmountRebateBillTot = 0;
            double dAmountRebateBillCurrRnd = 0;
            double dAmountRebateBillTotRnd = 0;
            double dAmountBillCurr = 0;
            double dAmountBillTot = 0;
            double dAmountBillCurrRnd = 0;
            double dAmountBillTotRnd = 0;
            double dAmountED = 0;
            double dPrsentBlAmount = 0;
            double dAmountRebate = 0;
            double lUnits_Kwh = 0;
            double dAmountArrear = 0;
            String strTariff = "";
            long lConLoad = 0;
            long ulfMDI = 0;
            int iBillMonths = 0;
            String strConLoadUoM = "";
            String strBillBasis = "A";
            String strMrTime = "";
            int iMeterType = 1;
            int iBilled_cnt = 0;
            double month_cnt = 0;
            ///added on 27.02.2017//
            UNIT_SLAB1 = 0;
            UNIT_SLAB2 = 0;
            UNIT_SLAB3 = 0;
            UNIT_SLAB4 = 0;
            RATE_SLAB1 = 0;
            RATE_SLAB2 = 0;
            RATE_SLAB3 = 0;
            RATE_SLAB4 = 0;
            EC_SLAB1 = 0;
            EC_SLAB2 = 0;
            EC_SLAB3 = 0;
            EC_SLAB4 = 0;
            //////end//////////
            long lReadMF_Mdival = 0;
            double MdivalCurr = 0;
            double MdivalPrev = 0;
            String MdMtrCond = "";
            String SplBlremark = "";
            long consmpOldMtr = 0;
            /*String strSelectSQL_02 = "SELECT PREVIOUS_MD,MF,PRESENT_METER_READING,METER_CONDITION FROM TBL_SPOTBILL_CHILD_DETAILS " +
                    " WHERE INSTALLATION='" + strAccNo + "' " +
                    "  AND REGISTER_CODE ='MDKW'";*/
            String strSelectSQL_02 = "SELECT B.PREVIOUS_MD,B.MF,LTRIM(B.PRESENT_METER_READING,'0'),B.METER_CONDITION,A.RCRD_LOAD FROM TBL_SPOTBILL_HEADER_DETAILS A, " +
                    " TBL_SPOTBILL_CHILD_DETAILS B WHERE " +
                    " A.INSTALLATION=B.INSTALLATION AND A.INSTALLATION='" + strAccNo + "' " +
                    " AND B.REGISTER_CODE ='MDKW'";
            Log.e("BillingQC", strSelectSQL_02);
            Log.d("DemoApp", "strSelectSQL_02  " + strSelectSQL_02);
            //ResultSet rs = statement.executeQuery(strSelectSQL_01);
            Cursor rs1 = helper.getCalculateedData(strSelectSQL_02);

            while (rs1.moveToNext()) {
                //MdivalPrev = rs1.getDouble(0);
                MdivalPrev = rs1.getDouble(4);
                lReadMF_Mdival = rs1.getLong(1);
                MdivalCurr = rs1.getDouble(2);
                MdMtrCond = rs1.getString(3);
            }

            rs1.close();
            double cmdchk = 0;
            double dMD_Current = Math.round(lReadMF_Mdival * MdivalCurr);

            // double dMD_Current = (lReadMF_Mdival * MdivalCurr);
            double dMD_prev = Math.round(lReadMF_Mdival * MdivalPrev);
            cmdchk = MdivalCurr;
             /* cmdchk=Math.round(dMD_Current*2)/2.0;
             if(dMD_Current-(Math.floor(dMD_Current))>0.25){
                 dMD_Current=Math.ceil(dMD_Current);
             }else{
                 dMD_Current=Math.floor(dMD_Current);
             }*/

            /*Log.e("dMD_Current_avik", "" + dMD_Current);
            Log.e("dMD_prev_avik", "" + dMD_prev);
            Log.e("cmdchkv_avik", "" + cmdchk);*/
            String strSelectSQL_01 = "SELECT CASE WHEN A.RATE_CATEGORY='DOM_OTH' THEN 'DOM' WHEN A.RATE_CATEGORY='DKJ' THEN 'BPL' WHEN A.RATE_CATEGORY='LT_GENPRPS' THEN 'GPS' WHEN A.RATE_CATEGORY='LT_SPBLPRS' THEN 'SPP' ELSE 'DOM' END TARIFF " +
                    " ,CASE WHEN B.MF >= 1 THEN B.MF ELSE 1 END AS MF,A.CONSUMER_OWNED,B.NO_OF_DIGITS,A.SAN_LOAD,CAST(B.PREV_MTR_READ AS LONG) as PREV_MTR_READ,A.CR_ADJ, A.DB_ADJ,A.PRV_BILLED_AMT, " +
                    " A.PREVIOUS_BILLED_PROV_UNIT,A.AVERAGE_KWH,A.ED_EXEMPT,CAST(B.PRESENT_METER_READING AS LONG) as PRESENT_METER_READING,B.METER_CONDITION P_STATUS,B.METER_CONDITION,B.METER_TYP,A.HL_MONTHS NO_OF_MON " +
                    " ,B.PREVIOUS_MD,A.HOSTEL_RBT,A.MOVE_IN_DATE, PREV_READ_DATE " +
                    " ,(strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', B.PREV_READ_DATE)*12 + strftime('%m', B.PREV_READ_DATE)) NEWCONN_MONTHS " +
                    " ,A.AVERAGE_KWH,A.PREV_BILL_UNITS,A.PRESENT_BILL_TYPE,B.METER_CONDITION MTR_COND_PRV,A.AVERAGE_KWH,A.DPS,A.MISC_CHARGES,A.PRV_ARR,A.ARREARS,A.AIFI " +
                    " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', B.PREV_READ_DATE)*12 + strftime('%m', B.PREV_READ_DATE))-1 +((strftime('%d',DATE(B.PREV_READ_DATE,'start of month','+1 month','-1 day'))-strftime('%d',B.PREV_READ_DATE)*1.0))/strftime('%d',DATE(B.PREV_READ_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) MONTHS_CNT  " +
                    " ,B.REGISTER_CODE,cast(A.METER_RENT as Long) as METER_RENT,A.ED_RBT,A.ULF,A.MRREASON,LTRIM(B.CONSUMPTION_OLD_METER,'0'),LTRIM(B.LAST_OK_RDNG,'0'),A.LAST_NORMAL_BILL_DATE  " +
                    " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', A.LAST_NORMAL_BILL_DATE)*12 + strftime('%m', A.LAST_NORMAL_BILL_DATE))-1 +((strftime('%d',DATE(A.LAST_NORMAL_BILL_DATE,'start of month','+1 month','-1 day'))-strftime('%d',A.LAST_NORMAL_BILL_DATE)*1.0)+1)/strftime('%d',DATE(A.LAST_NORMAL_BILL_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) DFMON_CNT  " +
                    ",A.AVG_UNIT_BILLED,A.PREV_BILL_REMARK,A.PREV_BILL_TYPE " +//44
                    " ,round((strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', A.MOVE_IN_DATE)*12 + strftime('%m', A.MOVE_IN_DATE))-1 +((strftime('%d',DATE(A.MOVE_IN_DATE,'start of month','+1 month','-1 day'))-strftime('%d',A.MOVE_IN_DATE)*1.0)+1)/strftime('%d',DATE(A.MOVE_IN_DATE,'start of month','+1 month','-1 day'))+(strftime('%d','now')*1.0)/strftime('%d',DATE('now','start of month','+1 month','-1 day')),4) movin_CNT1  " +
                    "  ,A.LAST_NORMAL_BILL_DATE,DATE('now', 'localtime'),A.PREV_PROV_AMT,CAST(A.FC_SLAB AS LONG) AS FC_SLAB,A.PROV_ED,A.PROV_PPT_AMT,A.SUPPLY_STATUS_ID,a.new_mtr_flg,a.new_meter_no,LTRIM(b.METER_NO,'0'),A.insert_date,A.DPS_5,A.DPS_LVD,B.NEW_METER_FLG,A.PREV_BILL_END_DATE , ifnull(A.ULF_MDI,0) " +
                    "  FROM TBL_SPOTBILL_HEADER_DETAILS A,TBL_SPOTBILL_CHILD_DETAILS B " +
                    "  WHERE A.INSTALLATION=B.INSTALLATION AND A.INSTALLATION='" + strAccNo + "' " +
                    "  AND B.REGISTER_CODE ='CKWH' " +
                    "  ORDER BY B.REGISTER_CODE";

            Log.e("BillingQuerryyyy", strSelectSQL_01);

            Log.d("DemoApp", "strUpdateSQL_01  " + strSelectSQL_01);
            Cursor rs = helper.getCalculateedData(strSelectSQL_01);
            double dAmountSplRebate = 0;
            int counter = 0;
            long lReadCurr = 0;
            long lReadPrev = 0;
            long lReadPrevDup = 0;
            long lReadMF_Kwh = 0;
            String strMeterCond = "";
            String strMeterCondmdi = "";
            String Meterrent = "";
            long LstOKRead = 0;
            String lstOkReadDate = "";
            double mon_cntDef = 0;
            long avgUntBld = 0;
            String mrNote = "";
            long defProvunit = 0;
            int defOkFlg = 0;
            long billedMd = 0;
            String prvBlRemark = "";
            int prvBlTyp = 0;
            int fcSlabCnt = 1;
            double avgunitblled = 0;
            int adjflg = 0;
            int newConsadj = 0;
            double movinCnt = 0;
            long sanLoad = 0;
            double MonthCnt = 0;
            String MovinDate = "";
            // String PrvReadDate="";
            String PrvOKBillDate = "";
            String CurrentDate = "";
            String FromDate = "";
            String Todate = "";
            int CntParam = 0;
            double MonthCntprvCnt = 0;
            double MonthCntmovCnt = 0;
            int stsID = 0;
            String adjBl = "N";
            double dpsbilled = 0;
            while (rs.moveToNext()) {
                MovinDate = rs.getString(19);
                //PrvReadDate=rs.getString(20);
                PrvOKBillDate = rs.getString(46);
                CurrentDate = rs.getString(47);
                double provisional_amt = rs.getDouble(48);
                double prvisional_Ed = rs.getDouble(50);
                double provAdjRebate = rs.getDouble(51);

                stsID = rs.getInt(52);
                String newMtrflg = "";
                String newmtrNo = "";
                String MeterNo = "";
                try {
                    newMtrflg = rs.getString(53);
                } catch (Exception e) {
                    newMtrflg = "N";
                }
                try {
                    newmtrNo = rs.getString(54);
                } catch (Exception e) {
                    newmtrNo = "N";
                }
                try {
                    MeterNo = rs.getString(55);
                } catch (Exception e) {
                    MeterNo = "N";
                }
                String insertDt = rs.getString(56);
                double dps5 = rs.getDouble(57);
                double dpslvd = rs.getDouble(58);

                String newMtrFlg = rs.getString(59);
                String prevBlMtrDate = rs.getString(60);
                double ulfMdi = 0;


                Log.d("DemoApp", " found ");
                consmpOldMtr = rs.getInt(38);
                LstOKRead = rs.getLong(39);
                lstOkReadDate = rs.getString(40);
                mon_cntDef = rs.getDouble(41);
                // avgUntBld=rs.getLong(42);
                prvBlRemark = rs.getString(43);
                prvBlTyp = rs.getInt(44);
                movinCnt = rs.getDouble(45);
                Meterrent = rs.getString(34);
                String Regcode = rs.getString(33);
                int iMeterChargeFlag = 1;
                strTariff = rs.getString(0);
                double dConLoad = rs.getDouble(4);
                lConLoad = (long) Math.ceil(dConLoad);
                if (lConLoad < 1) lConLoad = 1;
                sanLoad = lConLoad;
                Log.d("DemoApp", "not sanLoad " + sanLoad);
                //   if (Regcode.equalsIgnoreCase("CKWH")) {
                lReadMF_Kwh = rs.getLong(1);
                lReadPrev = rs.getLong(5);
                lReadCurr = rs.getLong(12);
                strMeterCond = rs.getString(14);//not used
                lReadPrevDup = lReadPrev;
                //   }
                Log.d("DemoApp", "not lReadCurr " + lReadCurr);
                // String   lReadPrevrrrrr = rs.getString(5);

                try {
                    ulfMdi = rs.getDouble(61);
                    ulfMDI = (long) Math.ceil(ulfMdi);
                } catch (Exception e) {
                    ulfMDI = sanLoad;
                }
                if (ulfMDI == 0) {
                    ulfMDI = sanLoad;
                }
                Log.d("DemoApp", "not lReadPrev tyrtyr " + lReadPrev);
                Log.d("DemoApp", "not MdivalPrev " + MdivalPrev);
                Log.d("DemoApp", "not MdivalCurr " + MdivalCurr);
                Log.d("DemoApp", "not provAdjRebate " + provAdjRebate);


                double dAdj_CR = rs.getDouble(6);
                double dAdj_DR = rs.getDouble(7);
                String strMeterOwner = rs.getString(2);

                Double strED_Exempt = rs.getDouble(35);
                Double lED_Applicable = getED_Applicable(strED_Exempt);
                //  Double lED_Applicable =strED_Exempt;

                try {
                    fcSlabCnt = rs.getInt(49);  //hl months ??????????????????????????????????????????????????????????????????????
                } catch (Exception e) {
                    fcSlabCnt = 0;
                }
                Log.e("fcSlabCntfcSlabCnt", "" + fcSlabCnt);


                Log.d("DemoApp", "not iBillMonths 1   " + iBillMonths);
                Log.d("DemoApp", "not dMD_Current   " + dMD_Current);
                Log.d("DemoApp", "not dMD_prev   " + dMD_prev);
                Log.d("DemoApp", "not iBillMonths   " + iBillMonths);
                Log.d("DemoApp", "not fcSlabCnt   " + fcSlabCnt);
                //lConLoad = (long) Math.max(dMD_Current, dMD_prev);
                if (dMD_prev == 0) {
                    dMD_prev = lConLoad;
                }
                if ((long) Math.max(dMD_Current, dMD_prev) <= 0) {
                    billedMd = lConLoad;
                } else {
                    billedMd = (long) Math.max(dMD_Current, dMD_prev);
                    // fcSlabCnt = 0;
                }
                Log.e("billedMd", "" + billedMd);
                lConLoad = billedMd;

                if (fcSlabCnt == 0 || fcSlabCnt == 1) {
                    iBillMonths = 1;
                } else {
                    iBillMonths = fcSlabCnt;
                }

                Log.d("DemoApp", "not dMD_prev lConLoad  " + lConLoad);
                String iSplReb_Flag;
                iSplReb_Flag = rs.getString(18);//hostel rebate

                try {
                    if (iSplReb_Flag == null || iSplReb_Flag.isEmpty()) {
                        iSplReb_Flag = "0";
                    }
                } catch (Exception e) {
                    iSplReb_Flag = "0";
                }


                Log.d("DemoApp", " found iSplReb_Flag" + iSplReb_Flag);
                String strDate_Ind_Conn = rs.getString(19);
                String strDate_Ind_LastMR = rs.getString(20);
                int iNewConnMonths = rs.getInt(21);
                long lLRCUnits = rs.getLong(23);
                String strMeterCond_Prv = rs.getString(25);
                long lAvgNMUnits = rs.getLong(26);
                lAvgNMUnits = Math.abs(lAvgNMUnits);
                Log.d("DemoApp", "lAvgNMUnits 1" + lAvgNMUnits);
                double dps = rs.getDouble(27);
                double misc_chg = rs.getDouble(28);
                // double provisional_amt = rs.getDouble(8);
                int Hlunits = rs.getInt(9);
                defProvunit = Hlunits;
                double prvdps = rs.getDouble(29);
                double prsyrarr = rs.getDouble(30);
                int aifi = rs.getInt(31);//added on 29.03.2017
                mrNote = rs.getString(37);
                long lLFUnits = rs.getInt(36);
                if (lLFUnits < 0) {
                    lLFUnits = 0;
                }
                month_cnt = rs.getDouble(32);//sap

                MonthCntprvCnt = rs.getDouble(32);//sap
                MonthCntmovCnt = movinCnt;

                //to handle Faulty meter to OK
                FromDate = strDate_Ind_LastMR;
                Todate = CurrentDate;

                //Preent Meter condition
                prs_mtrcond = strMeterCond_Prv;
              /*  if (strMeterCond_Prv.equalsIgnoreCase("O")) {
                    if (mrNote.equalsIgnoreCase("NM")) {
                        prs_mtrcond = "N";
                    }
                    if (mrNote.equalsIgnoreCase("NV") || mrNote.equalsIgnoreCase("MB") || mrNote.equalsIgnoreCase("GU") || mrNote.equalsIgnoreCase("ND") || mrNote.equalsIgnoreCase("FU")) {
                        prs_mtrcond = "F";
                    }
                }
                if (strMeterCond_Prv.equalsIgnoreCase("F")) {
                    if (mrNote.equalsIgnoreCase("OK") || mrNote.equalsIgnoreCase("MM") || mrNote.equalsIgnoreCase("GB") || mrNote.equalsIgnoreCase("SB") || mrNote.equalsIgnoreCase("MU")) {
                        prs_mtrcond = "O";
                    }

                    if (mrNote.equalsIgnoreCase("NM")) {
                        prs_mtrcond = "N";
                    }
                }*/
                //handle bill basis
                if (strMeterCond_Prv.equalsIgnoreCase("O")) {
                    if (mrNote.equalsIgnoreCase("OK") || mrNote.equalsIgnoreCase("MM") || mrNote.equalsIgnoreCase("GB") || mrNote.equalsIgnoreCase("SB") || mrNote.equalsIgnoreCase("MU")) {
                        strBillBasis = "N";
                        // adjflg = 1;
                    }
                }
                Log.d("DemoApp", " rqc" + rqc);
                Log.d("DemoApp", " strBillBasis" + strBillBasis);
                //putting bqc rqc logic
                /*if (rqc == 1 || rqc == 7) {//consumption unit>10000
                    strBillBasis = "A";
                } else if (rqc == 2 || rqc == 21) {
                    strBillBasis = "A";
                    lConLoad = sanLoad;
                } else if (rqc == 3) {
                    if (stsID == 2) {
                        strBillBasis = "A";
                    }
                }*/

                if (rqc == 1 || rqc == 5 || rqc == 8) {//consumption unit>10000 changed
                    if (strBillBasis.equalsIgnoreCase("N")) {
                        strBillBasis = "A";
                        SplBlremark = "OA";
                    }
                } else if (rqc == 2 || rqc == 21) {
                    lConLoad = sanLoad;
                }
                Log.d("DemoApp", " newMtrflg" + newMtrflg);
                Log.d("DemoApp", " strBillBasis 11 after" + strBillBasis);
                //Average  cases MDI changed

                if (strBillBasis.equalsIgnoreCase("A")) {
                    lConLoad = ulfMDI;
                }

              /*  try {
                    if (newMtrflg.equalsIgnoreCase("NEW")) {
                        if (!newmtrNo.equalsIgnoreCase(MeterNo)) {
                            strBillBasis = "A";
                        }
                    }
                } catch (Exception e) {
                }*/
                Log.d("DemoApp", " strBillBasis after" + strBillBasis);

                //Actual Bill - Actual, Round Complete Cases
                Log.d("DemoApp", " lReadCurr ffff " + lReadCurr);
                Log.d("DemoApp", " lReadPrev ffff " + lReadPrev);
                Log.d("DemoApp", " lReadPrev ffff " + lReadMF_Kwh);
                Log.d("DemoApp", " lReadPrev ffff adjflg" + adjflg);
                CntParam = 0;
                newConsadj = 0;
                if (prvBlTyp == 5000) {
                    month_cnt = movinCnt;
                    FromDate = MovinDate;
                    CntParam = 1;
                    if (provisional_amt > 0 || prvisional_Ed > 0) {
                        newConsadj = 1;
                        adjflg = 1;
                    }
                } else if (prvBlTyp == 1000 || prvBlTyp == 2000) {
                    // FromDate = PrvOKBillDate;
                    adjflg = 0;
                    CntParam = 0;
                    if (strBillBasis.equalsIgnoreCase("N")) {
                        FromDate = PrvOKBillDate;
                        adjflg = 1;
                        lReadPrev = LstOKRead;
                        if (MovinDate.equalsIgnoreCase(PrvOKBillDate)) {
                            CntParam = 1;
                        } else {
                            CntParam = 0;
                        }
                    }
                }
                try {
                    if (newMtrFlg.equalsIgnoreCase("X")) {
                        FromDate = prevBlMtrDate;
                        adjflg = 0;
                        CntParam = 1;
                        lReadPrev = lReadPrevDup;
                    }
                } catch (Exception e) {
                }
                Log.d("DemoApp", " FromDate  " + FromDate);
                Log.d("DemoApp", " Todate" + Todate);
                Log.d("DemoApp", " CntParam" + CntParam);
                month_cnt = 0;
                MonthCnt = CalculateMonthCount(FromDate, Todate, CntParam);
                month_cnt = MonthCnt;
                int TotalDays = 0;
                TotalDays = CalNoOfDaysAvg(FromDate, Todate, CntParam);
                Log.d("DemoApp", " LstOKRead" + LstOKRead);
                Log.d("DemoApp", " MonthCntprvCnt" + MonthCntprvCnt);
                Log.d("DemoApp", " month_cnt" + month_cnt);
                Log.d("DemoApp", " MonthCntmovCnt" + MonthCntmovCnt);
                double iMeterDigits = rs.getDouble(3);
                if (iMeterDigits == 0) {
                    iMeterDigits = String.valueOf(lReadPrev).length();
                }

                if (strBillBasis.equalsIgnoreCase("N")) {
                    if (lReadCurr >= lReadPrev) {
                        lUnits_Kwh = (lReadCurr - lReadPrev) * lReadMF_Kwh;
                        Log.d("DemoApp", " lReadPrev xxxxxx " + lUnits_Kwh);
                    }
                    // Dial-Around Case
                    else {
                        lUnits_Kwh = (pow(10, iMeterDigits) + lReadCurr - lReadPrev) * lReadMF_Kwh;
                        // prs_mtrcond = "R";
                    }
                } else {
                    if (lAvgNMUnits >= 1) { //&& lAvgNMUnits < lULimit --deleted
                        lUnits_Kwh = lAvgNMUnits;
                    } else {
                        lUnits_Kwh = lLFUnits;
                    }
                    Log.d("DemoApp", " Average ffff " + lUnits_Kwh);
                    //Calulation for derivation of Average units
                    lUnits_Kwh = Math.round((lUnits_Kwh / 30) * TotalDays);
                    // lUnits_Kwh = Math.floor(lUnits_Kwh * month_cnt);
                    avgunitblled = lUnits_Kwh;
                }
                Log.d("DemoApp", " lUnits_Kwh ffff " + lUnits_Kwh);
                Log.d("DemoApp", " TotalDays ffff " + TotalDays);
                // to handle meter change case
                if (consmpOldMtr > 0) {
                    lUnits_Kwh = lUnits_Kwh + consmpOldMtr;
                }
                /////
                if (strBillBasis.equalsIgnoreCase("A")) {
                    if (newConsadj == 1) {
                        adjflg = 1;
                    } else {
                        adjflg = 0;
                    }
                }

                Log.d("DemoApp", " iBillMonths found " + iBillMonths);
                Log.d("DemoApp", " lUnits_Kwh 2 avg " + lUnits_Kwh);
                //to handle meter rent
                try {
                    if ((strMeterCond_Prv.equals("N") || strMeterCond_Prv.equals("F"))) {
                        if (strBillBasis.equalsIgnoreCase("A")) {
                            iMeterChargeFlag = 0;
                        }
                    }
                    if (Math.round(lUnits_Kwh / month_cnt) <= 30 && strTariff.equals("BPL")) {
                        iMeterChargeFlag = 0;
                    }
                    if (strMeterOwner.equalsIgnoreCase("C")) {
                        iMeterChargeFlag = 0;
                    }
                    Log.d("DemoApp", " xx strMeterOwner " + strMeterOwner);
                    Log.d("DemoApp", "  xx iMeterChargeFlag " + iMeterChargeFlag);

                   /* if(strMeterCond_Prv.equals("O") ){
                        if(mrNote.equalsIgnoreCase("NM") || mrNote.equalsIgnoreCase("MB") || mrNote.equalsIgnoreCase("ND")){
                            iMeterChargeFlag = 0;
                        }
                    }*/
                    Log.d("DemoApp", "iMeterChargeFlag  " + iMeterChargeFlag);
                } catch (Exception e) {
                    Log.d("DemoApp", "77 mfc  " + e);
                }
                ///DPS Calculation Started
                //   dps  dps5     dpslvd
                int dpsNoofDays = CalNoOfDaysAvg(insertDt, Todate, 0);
                double dpscalcVal = 0;

                if (dpsNoofDays == 0) {
                    dpscalcVal = dps;
                } else {
                    dpscalcVal = ((dps5 - dps) / 5) * dpsNoofDays;
                    dpscalcVal = dps + Math.round(dpscalcVal * 100.0) / 100.0;
                }
                dpsbilled = dpslvd + dpscalcVal;
                ///End

                //if (ConnectDBSqllite.print_flag == 1) System.out.println("##### lReadCurr, lReadPrev & lUnits_Kwh ::" + lReadCurr + " | " + lReadPrev + " | " + lUnits_Kwh);
                double dK_Volt = 0.230;
                strConLoadUoM = "KW";
                long lUnitsDC = lConLoad;
                Log.d("DemoApp", " counter C1 " + counter);
                Log.d("DemoApp", "---------------------------------------------------------------------------------  ");
                /////////////////
                Log.d("DemoApp", " dAmountMFC C1 ");
                dAmountMFC = CalculateMFC(strAccNo, iNewConnMonths, dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths, lUnitsDC, (long) lUnits_Kwh, month_cnt);
                Log.e("MFC_AVIK", "" + dAmountMFC);
                Log.d("DemoApp", " dAmountMFC  " + dAmountMFC);
                dAmountEC_KWH = CalculateEC_KWH(strAccNo, dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths, (long) lUnits_Kwh, month_cnt);
                Log.e("MFC_AVIK", "" + dAmountEC_KWH);
                // dAmountMR = CalculateMR(strAccNo, dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths, iMeterChargeFlag); not required
                // dAmountMR = 0;
                // to handle bpl<=30 units metrent 0
                dAmountMR = Double.parseDouble(Meterrent);
                Log.d("DemoApp", " dAmountMR XX " + dAmountMR);
                dAmountMR = dAmountMR * iBillMonths;
                Log.d("DemoApp", " dAmountMR Xy " + dAmountMR);

                if (iMeterChargeFlag == 0) {
                    dAmountMR = 0;
                }
                Log.d("DemoApp", " dAmountMR yy " + dAmountMR);
                //  if (ConnectDBSqllite.print_flag == 1) System.out.println("##### strTariff, dAmountEC_KWH ::" + strTariff + " | " +  dAmountEC_KWH);
                // Rebate handling for PIN / SPP

                if (strTariff.equalsIgnoreCase("SPP") || strTariff.equalsIgnoreCase("PIN")) {
                    dAmountRebate = CalculateRebate(strAccNo, dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths, (dAmountEC_KWH + dAmountMFC));

                    if (iSplReb_Flag.equalsIgnoreCase("X")) {
                        dAmountSplRebate = CalculateSplRebate(strAccNo, dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths, lUnits_Kwh);
                        dAmountRebate = dAmountRebate + dAmountSplRebate;
                    }
                } else {
                    dAmountRebate = CalculateRebate(strAccNo, dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths, (lUnits_Kwh));
                }
                if (adjflg == 1) {
                    dAmountRebate = dAmountRebate - provAdjRebate;
                }

                // to handle bpl<=30 units
                if (Math.round(lUnits_Kwh / month_cnt) <= 30 && strTariff.equals("BPL")) {
                    dAmountRebate = 0;
                }
                //to handle -ve rebate
                if (dAmountRebate < 0) {
                    dAmountRebate = 0;
                }
                dAmountRebate = Math.round(dAmountRebate * 100.0) / 100.0;
                dAmountED = CalculateED(strAccNo, dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths, (dAmountEC_KWH * lED_Applicable));
                dAmountED = Math.round(dAmountED * 100.0) / 100.0;
                dAmountEC_KWH = Math.round(dAmountEC_KWH * 100.0) / 100.0;


                dPrsentBlAmount = dAmountMFC + dAmountEC_KWH + dAmountMR + dAmountED + dpsbilled;
                dPrsentBlAmount = Math.round(dPrsentBlAmount * 100.0) / 100.0;
                dAmountArrear = prsyrarr;
                dAmountArrear = dAmountArrear + dAdj_CR - dAdj_DR - dpslvd;
                Log.d("DemoApp", " provisional_amt " + provisional_amt);

                if (adjflg == 1) {
                    adjBl = "Y";
                    dAmountBillTot = dPrsentBlAmount - dAdj_CR + dAdj_DR + dAmountArrear + misc_chg - provisional_amt - prvisional_Ed;
                } else {
                    dAmountBillTot = dPrsentBlAmount - dAdj_CR + dAdj_DR + dAmountArrear + misc_chg;
                }
                dAmountBillTot = Math.round(dAmountBillTot * 100.0) / 100.0;
            }
            rs.close();
            ///rounding total value
            double roundupto = 0;
            if (dAmountBillTot < 0 && ((double) Math.floor(dAmountBillTot - dAmountRebate)) < 0) {
                roundupto = Math.ceil(dAmountBillTot - dAmountRebate) - (dAmountBillTot - dAmountRebate);
            } else {
                roundupto = Math.floor(dAmountBillTot) - dAmountBillTot;
            }
            roundupto = Math.round(roundupto * 100.0) / 100.0;
            /////
            // strSelectSQL_01 = "SELECT DATE('now', 'localtime'), CASE WHEN DATE('now', '+15 days', 'localtime') > DATE('now','start of month','+1 month','-1 day') THEN DATE('now','start of month','+1 month','-1 day') ELSE DATE('now', '+15 days', 'localtime') END AS DUE_DATE,"
            // + "strftime('%H%M%S','now', 'localtime'), CASE WHEN DATE('now', '+4 days', 'localtime') > DATE('now','start of month','+1 month','-1 day') THEN DATE('now','start of month','+1 month','-1 day') ELSE DATE('now', '+4 days', 'localtime') END AS SPP_DUE_DATE ";
            //upper statement disable on 010620 due to change in rebate date from 15 days to 7 days
            // strSelectSQL_01 = "SELECT DATE('now', 'localtime'), CASE WHEN DATE('now', '+7 days', 'localtime') > DATE('now','start of month','+1 month','-1 day') THEN DATE('now','start of month','+1 month','-1 day') ELSE DATE('now', '+7 days', 'localtime') END AS DUE_DATE,"
            //+ "strftime('%H%M%S','now', 'localtime'), CASE WHEN DATE('now', '+4 days', 'localtime') > DATE('now','start of month','+1 month','-1 day') THEN DATE('now','start of month','+1 month','-1 day') ELSE DATE('now', '+4 days', 'localtime') END AS SPP_DUE_DATE ";
            strSelectSQL_01 = "SELECT DATE('now', 'localtime'), DATE('now', '+7 days', 'localtime') AS DUE_DATE,"
                    + "strftime('%H%M%S','now', 'localtime'), DATE('now', '+4 days', 'localtime')  AS SPP_DUE_DATE,strftime('%Y%m%d','now') as curdate ";
            Log.e("SelectQuery312", strSelectSQL_01);
            //rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
            rs = helper.getCalculateedData(strSelectSQL_01);
            String strCurDt = "";
            while (rs.moveToNext()) {
                strBillDate = rs.getString(0);
                strDueDate = rs.getString(1);
                strMrTime = rs.getString(2);
                strDueDateSPP = rs.getString(3);
                strCurDt = rs.getString(4);
            }

            //Added on this logic for shorting walking sequence////////
            if (strMrTime.length() == 4) {
                strMrTime = "00" + strMrTime;
            } else if (strMrTime.length() == 5) {
                strMrTime = "0" + strMrTime;
            }
            //
            if (strTariff.equalsIgnoreCase("SPP") || strTariff.equalsIgnoreCase("PIN")) {
                strDueDate = strDueDateSPP;
            }
            // if (ConnectDBSqllite.print_flag == 1) System.out.println("##### Billing Date & Rebate Date ::" + strBillDate + " | " + strDueDate);
            rs.close();
            strSelectSQL_02 = "select BILLED_COUNT from File_desc where version_flag=2";
            ///Cursor rs2 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
            ///Cursor rs2 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
            Cursor rs2 = helper.getCalculateedData(strSelectSQL_02);

            while (rs2.moveToNext()) {
                iBilled_cnt = rs2.getInt(0);
            }

            rs2.close();
            iBilled_cnt = iBilled_cnt + 1;

            /*String SbmblNo = strAccNo.substring(4) + strCurDt.substring(2);
            Log.e("SBM_BILL_NO", SbmblNo);*/
            if (SplBlremark.equalsIgnoreCase("OA")) {
                mrNote = SplBlremark;
            }

            Log.d("DemoApp", "iBilled_cnt  " + iBilled_cnt);//TBL_SPOTBILL_CHILD_DETAILS
            /*String strUpdateSQL_01 = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET BP_START_DATE= '" + FromDate + "',BP_END_DATE= '" + Todate + "'," +
                    " PRESENT_METER_STATUS= '" + prs_mtrcond + "',PRESENT_BILL_UNITS= " + lUnits_Kwh + "," +
                    " BILL_BASIS= '" + strBillBasis + "',EC= " + dAmountEC_KWH + ",MMFC= " + dAmountMFC + ",MRENT_CHARGED= " + dAmountMR + "," +//
                    " ED= " + dAmountED + ",CURRENT_BILL_TOTAL= " + dPrsentBlAmount + ",REBATE= " + dAmountRebate + ",PRESENT_READING_TIME= '" + strMrTime + "',AMOUNT_PAYABLE= " + dAmountBillTot + ",ROUND_OFF='" + roundupto + "'," +
                    " DUE_DATE= '" + strDueDate + "',DO_EXPIRY= '" + strDueDate + "',OSBILL_DATE= '" + strBillDate + "' ,PRESENT_READING_DATE= '" + strBillDate + "', READ_FLAG = 1 , NO_BILLED_MONTH= " + month_cnt + ", " +
                    " SBM_BILL_NO= '" + SbmblNo + "',PRESENT_READING_REMARK='" + mrNote + "',PRESENT_BILL_TYPE='" + strBillBasis + "',AVG_UNIT_BILLED= " + avgunitblled + ", " +
                    " RQCFLG= '" + rqc + "',BILL_START_DATE= '" + FromDate + "',BILL_END_DATE= '" + Todate + "',BILLED_MD= '" + lConLoad + "',ADJ_BILL= '" + adjBl + "',DPS_BILLED='" + dpsbilled + "',DPS_BLLD='" + dpsbilled + "',PPI= " + dAmountRebate + "" +    //
                    " WHERE INSTALLATION = '" + strAccNo + "'";*/
            String strUpdateSQL_01 = "UPDATE TBL_SPOTBILL_HEADER_DETAILS SET BP_START_DATE= '" + FromDate + "',BP_END_DATE= '" + Todate + "'," +
                    " PRESENT_METER_STATUS= '" + prs_mtrcond + "',PRESENT_BILL_UNITS= " + lUnits_Kwh + "," +
                    " BILL_BASIS= '" + strBillBasis + "',EC= " + dAmountEC_KWH + ",MMFC= " + dAmountMFC + ",MRENT_CHARGED= " + dAmountMR + "," +//
                    " ED= " + dAmountED + ",CURRENT_BILL_TOTAL= " + dPrsentBlAmount + ",REBATE= " + dAmountRebate + ",PRESENT_READING_TIME= '" + strMrTime + "',AMOUNT_PAYABLE= " + dAmountBillTot + ",ROUND_OFF='" + roundupto + "'," +
                    " DUE_DATE= '" + strDueDate + "',DO_EXPIRY= '" + strDueDate + "',OSBILL_DATE= '" + strBillDate + "' ,PRESENT_READING_DATE= '" + strBillDate + "', READ_FLAG = 1 , NO_BILLED_MONTH= " + month_cnt + ", " +
                    " PRESENT_READING_REMARK='" + mrNote + "', BANK='" + mContext.getResources().getString(R.string.version) + "',PRESENT_BILL_TYPE='" + strBillBasis + "',AVG_UNIT_BILLED= " + avgunitblled + ", " +
                    " RQCFLG= '" + rqc + "',BILL_START_DATE= '" + FromDate + "',BILL_END_DATE= '" + Todate + "',BILLED_MD= '" + lConLoad + "',ADJ_BILL= '" + adjBl + "',DPS_BILLED='" + dpsbilled + "',DPS_BLLD='" + dpsbilled + "',PPI= " + dAmountRebate + "" +    //
                    " WHERE INSTALLATION = '" + strAccNo + "'";

            Log.d("DemoApp", "strMrTime  " + strMrTime);
            Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
            helper.updateMTRCONTD(strUpdateSQL_01);

            Log.d("DemoApp", "iBilled_cnt  " + iBilled_cnt);//TBL_SPOTBILL_CHILD_DETAILS
            strUpdateSQL_01 = "";
            strUpdateSQL_01 = "UPDATE TBL_SPOTBILL_CHILD_DETAILS SET " +
                    " BILLED_MD='" + lConLoad + "',PRS_MD='" + cmdchk + "', READ_FLAG='1' " +
                    " WHERE INSTALLATION = '" + strAccNo + "' AND REGISTER_CODE ='MDKW'";

            String strUpdateSQL_03 = "UPDATE TBL_SPOTBILL_CHILD_DETAILS SET " +
                    " READ_FLAG='1' " +
                    " WHERE INSTALLATION = '" + strAccNo + "'";
            Log.d("DemoAppcUPDATWEhild", strUpdateSQL_03);

            Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
            helper.updateMTRCONTD(strUpdateSQL_01);
            helper.updateMTRCONTD(strUpdateSQL_03);

            // DatabaseAccess.database.execSQL(strUpdateSQL_01);
            String strUpdateSQL_02 = "UPDATE FILE_DESC SET BILLED_COUNT = '" + iBilled_cnt + "'";
            helper.updateMTRCONTD(strUpdateSQL_02);
            //DatabaseAccess.database.execSQL(strUpdateSQL_02);
        } catch (Exception e) {
            Log.d("DemoApp", "Exception  calculate bill" + e);
        }
    }


    /* public void CalculateBill_All() throws SQLException
     {
         int iCnt = 0;
         // ConnectDBSqllite.getDBConnectionSqlLite();
         // Connection con=ConnectDBSqllite.dbConnection;
         // Statement statement = con.createStatement();
         // statement.setQueryTimeout(30);  // set Timeout to 30 sec.

         String strSelectSQL_01= "SELECT CONS_ACC FROM BILL_SBM_DATA WHERE BILL_FLAG = 0 "; // AND CAT_CODE='22' LIMIT 10
         //  ResultSet rs = statement.executeQuery(strSelectSQL_01);
         Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);

         while(rs.moveToNext())
         {
          //   CalculateBill(rs.getString(0));
             iCnt++;
             // if (ConnectDBSqllite.print_flag == 1)
             // System.out.println("######################### Consumer Bill ::"+ iCnt +" ###################################");
         }
         // rs.close();
         // statement.close();
     }*/
    public static String GetToken(String strLine, String strDelim, int TokenNo) throws Exception {
        int iCnt = 1;
        String strValue = strLine;
        StringTokenizer st = new StringTokenizer(strLine, strDelim);
        while (st.hasMoreTokens()) {
            strValue = st.nextToken();
            //System.out.println(strValue );
            if (TokenNo == iCnt) {
                break;
            }
            iCnt++;
        }
        return strValue;
    }


    private static int chkProration(String rate_type, String strTariff, Double dK_Volt, String strDate_Ind_LastMR) {

        DatabaseHelper helper = new DatabaseHelper(mContext);
        String strSelectSQL_03 = " SELECT count(1),DATE('now'),VALIDITY_START,SLAB_START, VALIDITY_END,CASE WHEN SLAB_END = -1 THEN 9999999999 ELSE SLAB_END END AS SLAB_END,  UNIT_INCREMENT, RATE, RATE_ABSOLUTE, SLAB_TYPE, DC_UNIT, TARIFF_SECOND, TARIFF_SECOND_UNIT , SLAB_SERIAL FROM MST_TARIFFS " +
                " WHERE RATE_TYPE = 'KWH' AND TARIFF = '" + strTariff + "' AND SUPPLY_TYPE = 'LT' AND 0.0 BETWEEN CD_LIMIT_START AND CASE WHEN CD_LIMIT_END= -1 THEN 9999999999 ELSE CD_LIMIT_END END " +
                " AND DATE('" + strDate_Ind_LastMR + "')<=VALIDITY_END " +
                "AND 2.0>= SLAB_START " +
                "GROUP BY SLAB_START " +
                "ORDER BY SLAB_START DESC";
        Log.d("DemoApp", "strSelectSQL_03 " + strSelectSQL_03);
        Cursor rs = helper.getCalculateedData(strSelectSQL_03);
        int cntTariffFlg = 1;
        while (rs.moveToNext()) {
            cntTariffFlg = rs.getInt(0);
        }
        rs.close();
        return cntTariffFlg;
    }

    private static int CalNoOfDays(String prvdate) {
        int noofdays = 0;
        DatabaseHelper helper = new DatabaseHelper(mContext);
        String strSelectSQL_03 = "Select Cast (( " +
                " JulianDay('now') - JulianDay('" + prvdate + "') " +
                " ) As Integer) ";
        Log.d("DemoApp", "strSelectSQL_03 " + strSelectSQL_03);
        Cursor rs = helper.getCalculateedData(strSelectSQL_03);
        while (rs.moveToNext()) {
            noofdays = rs.getInt(0);
        }
        rs.close();
        return noofdays;
    }

    public static int CalNoOfDaysAvg(String FromDt, String ToDt, int Adddays) {
        int totdays = 0;
        DatabaseHelper helper = new DatabaseHelper(mContext);
        String strSelectSQL_03 = "Select Cast (( " +
                " JulianDay('" + ToDt + "') - JulianDay('" + FromDt + "') " +
                " ) As Integer) ";
        Log.d("DemoApp", "strSelectSQL_03 totdays" + strSelectSQL_03);
        Cursor rs = helper.getCalculateedData(strSelectSQL_03);
        while (rs.moveToNext()) {
            totdays = rs.getInt(0);
        }
        totdays = totdays;
        rs.close();
        return totdays;

    }

    public static void delTariffTable(String delQuery) {
        DatabaseHelper helper = new DatabaseHelper(mContext);
        String strSelectSQL_03 = "DELETE FROM MST_TARIFFS_TEMP";
        Log.d("DemoApp", "delQuery " + delQuery);
        helper.deleletTariffTemp(delQuery);
    }

    private static double CalculateMonthCount(String fromDT, String ToDt, int addDays) {
        double monthcnt = 0;
        double fstMonbldays = 0;
        double fstMontotdays = 0;
        double lstMonbldays = 0;
        double lstMontotdays = 0;
        int betweenMonth = 0;
        DatabaseHelper helper = new DatabaseHelper(mContext);
        String strSelectSQLMonCnt = "SELECT " +
                "  (strftime('%Y','" + ToDt + "')*12 + strftime('%m','" + ToDt + "'))-(strftime('%Y', '" + fromDT + "')*12 + strftime('%m', '" + fromDT + "'))-1 betmon  " +
                " ,(strftime('%d',DATE('" + fromDT + "','start of month','+1 month','-1 day'))-strftime('%d','" + fromDT + "')*1.0)/strftime('%d',DATE('" + fromDT + "','start of month','+1 month','-1 day')) first_mon  " +
                " ,(strftime('%d','" + ToDt + "')*1.0)/strftime('%d',DATE('" + ToDt + "','start of month','+1 month','-1 day')) lastmon  " +
                " ,(strftime('%d','" + ToDt + "')*1) lstmonbldays  " +
                " ,strftime('%d',DATE('" + ToDt + "','start of month','+1 month','-1 day')) lstmontotdays  " +
                " ,(strftime('%d',DATE('" + fromDT + "','start of month','+1 month','-1 day'))-strftime('%d','" + fromDT + "'))*1 fstmonbldays  " +
                " ,strftime('%d',DATE('" + fromDT + "','start of month','+1 month','-1 day')) fstmontotdays ";

        Log.d("DemoApp", "strSelectSQL_03 " + strSelectSQLMonCnt);
        Cursor rs = helper.getCalculateedData(strSelectSQLMonCnt);
        while (rs.moveToNext()) {
            fstMonbldays = rs.getDouble(5);
            fstMontotdays = rs.getDouble(6);
            lstMonbldays = rs.getDouble(3);
            lstMontotdays = rs.getDouble(4);
            betweenMonth = rs.getInt(0);
        }
        rs.close();
        monthcnt = ((fstMonbldays + addDays) / fstMontotdays) + betweenMonth + (lstMonbldays / lstMontotdays);
        monthcnt = Math.round(monthcnt * 10000.0) / 10000.0;
        return monthcnt;
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

}
