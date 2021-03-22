package com.tpcodl.billingreading.activity;

import android.database.Cursor;
import android.util.Log;

import com.tpcodl.billingreading.database.DatabaseAccess;

import java.sql.SQLException;
import java.util.StringTokenizer;

import static java.lang.Math.pow;

/**
 * Created by ITCC05 on 23-05-2019.
 */
public class calculateBillAll {
    public static long lULimit = 10000;
    public static String deriveSupplyType (double dK_Volt)
    {
        String StrSupply = "NA";
        if (dK_Volt >= 0 && dK_Volt < 11)
        {
            StrSupply = "LT";
        }
        else if (dK_Volt >= 11 && dK_Volt <= 66)
        {
            StrSupply = "HT";
        }
        else if (dK_Volt > 66)
        {
            StrSupply = "EHT";
        }
        else
        {
            StrSupply = "NA";
        }
        return StrSupply;
    }

    public static double deriveLoadUoM_Factor (String strConLoadUoM)
    {
        double dLoadUoM_Factor = 0;
        if (strConLoadUoM.equals("KW"))
        {
            dLoadUoM_Factor = 1;
        }
        else if (strConLoadUoM.equals("KVA"))
        {
            dLoadUoM_Factor = 0.909;
        }
        else if (strConLoadUoM.equals("HP"))
        {
            dLoadUoM_Factor = 0.746;
        }
        else
        {
            dLoadUoM_Factor = 1;
        }
        return 0;
    }

    public static long getED_Applicable(String strED_Exempt)
    {
        long lED_Applicable = 0;
        if (strED_Exempt.equals("Y"))
        {
            lED_Applicable = 0;
        }
        else
        {
            lED_Applicable = 1;
        }
        return lED_Applicable;
    }
    //move to here///
    static double UNIT_SLAB1 = 0;
    static double UNIT_SLAB2 = 0;
    static double UNIT_SLAB3 = 0;
    static double UNIT_SLAB4 = 0;
    static  double RATE_SLAB1 = 0;
    static double RATE_SLAB2 = 0;
    static double RATE_SLAB3 = 0;
    static double RATE_SLAB4 = 0;
    static double EC_SLAB1 = 0;
    static double EC_SLAB2 = 0;
    static  double EC_SLAB3 = 0;
    static double EC_SLAB4 = 0;
    /////
    public static String getBasicRate (String strAccNo,String strRateType, double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, double lUnits) throws SQLException
    {
        double lUnitsTot = lUnits;
        double lUnitsRemain = lUnits;
        double dTotValue = 0;
        String strResult="";

        // taken to static up//

        // Statement statement = ConnectDBSqllite.dbConnection.createStatement();
        //statement.setQueryTimeout(30);  // set timeout to 30 sec.
        String StrSupplyType = deriveSupplyType (dK_Volt);

        String strSelectSQL_01= "SELECT SLAB_START, CASE WHEN SLAB_END = -1 THEN 9999999999 ELSE SLAB_END END AS SLAB_END, "
                + " UNIT_INCREMENT, RATE, RATE_ABSOLUTE, SLAB_TYPE, DC_UNIT, TARIFF_SECOND, TARIFF_SECOND_UNIT , SLAB_SERIAL"
                + " FROM MST_TARIFFS WHERE RATE_TYPE = '" + strRateType + "'"
                + " AND TARIFF = '" + strTariff +"'"
                + " AND SUPPLY_TYPE = '" + StrSupplyType +"'"
                + " AND "+ lConLoad * deriveLoadUoM_Factor (strConLoadUoM) + " BETWEEN CD_LIMIT_START AND CASE WHEN CD_LIMIT_END= -1 THEN 9999999999 ELSE CD_LIMIT_END END "
                + " AND DATE('now') BETWEEN VALIDITY_START AND VALIDITY_END"
                + " AND "+ lUnits + " >= SLAB_START"
                + " ORDER BY SLAB_START DESC";
        //Log.d("DemoApp", "strUpdateSQL_01 for tariff  " + strSelectSQL_01);
        //   if (strRateType == "MR")
        //  {
        //    if (ConnectDBSqllite.print_flag == 1) System.out.println("For " + strRateType + " strSelectSQL_01::" + "\n" + strSelectSQL_01);
        //  }

        //if (ConnectDBSqllite.print_flag == 1) System.out.println("##### strSelectSQL_01 ::" + strSelectSQL_01);
        Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        //  ResultSet rs = statement.executeQuery(strSelectSQL_01);
        while(rs.moveToNext())
        {
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
            if (dRateAbsolute == 2)
            {
                dSlabUnits = Math.min(dSlabEnd, lUnitsRemain) - dSlabStart;
                lUnitsRemain = lUnitsRemain - dSlabUnits;
                dSlabValue = Math.ceil(dSlabUnits/dUnitIncr) * dRate/100;
                dTotValue = dTotValue + dSlabValue;
            }
            else
            {
                dSlabUnits = Math.min(dSlabEnd, lUnitsRemain) - dSlabStart;
                lUnitsRemain = lUnitsRemain - dSlabUnits;
                dSlabValue = Math.ceil(dSlabUnits/dUnitIncr) * dRate;
                dTotValue = dTotValue + dSlabValue;
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
                if (iSlabSerial == 1 ){ UNIT_SLAB1 = UNIT_SLAB1+dSlabUnits;RATE_SLAB1=dRate;EC_SLAB1=EC_SLAB1+dSlabValue;}
                else if (iSlabSerial == 2 ){ UNIT_SLAB2 = UNIT_SLAB2+dSlabUnits;RATE_SLAB2=dRate;EC_SLAB2=EC_SLAB2+dSlabValue;}
                else if (iSlabSerial == 3 ){ UNIT_SLAB3 = UNIT_SLAB3+dSlabUnits;RATE_SLAB3=dRate;EC_SLAB3=EC_SLAB3+dSlabValue;}
                else if (iSlabSerial == 4 ){ UNIT_SLAB4 = UNIT_SLAB4+dSlabUnits;RATE_SLAB4=dRate;EC_SLAB4=EC_SLAB4+dSlabValue;}
                else { UNIT_SLAB1 = 0;RATE_SLAB1=0;EC_SLAB1=0;}
            }
            /////
        }
        strResult = "#" + dTotValue + "#;" + strResult;
        //  if (ConnectDBSqllite.print_flag == 1) System.out.println("##### Fetching Rates ::" + strTariff + "|" + strRateType + "|" + strResult);
        rs.close();

        // Log.d("DemoApp", " UNIT_SLAB1 " + UNIT_SLAB1);
        //  Log.d("DemoApp", " UNIT_SLAB2 " + UNIT_SLAB2);
        //   Log.d("DemoApp", " UNIT_SLAB3 " + UNIT_SLAB3);
        //  Log.d("DemoApp", " UNIT_SLAB4 " + UNIT_SLAB4);

        //for slabwwise data in kwh
        if (strRateType.equals("KWH")) {

            String strUpdateSQL_01 = "UPDATE BILL_SBM_DATA SET "
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
                    + " WHERE CONS_ACC = '" + strAccNo + "'";
        //    Log.d("DemoApp", " found CalculateEC_KWH strUpdateSQL_01" + strUpdateSQL_01);
            DatabaseAccess.database.execSQL(strUpdateSQL_01);
        }
        return strResult;
    }

    public static double CalculateEC_KWH(String strAccNo,double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, long lUnits)
    {
        String strRateType="KWH";
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
          //  Log.d("DemoApp", " found CalculateEC_KWH" + iBillMonths);
            int slabUnits = Math.round((lUnits / iBillMonths));
            int slabUnitsRest = (int) (lUnits - (slabUnits * (iBillMonths - 1)));
            if (slabUnits > 30 && strTariff.equals("BPL")){
                strTariff = "DOM";
            }
            for (int i=0; i< iBillMonths-1 ;i++)
            {
           //     Log.d("DemoApp", " found CalculateEC_KWH"+slabUnits);
                strResult = getBasicRate(strAccNo, strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, slabUnits);
                fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
                fResultValTot = fResultValTot + fResultVal;
              //  Log.d("DemoApp", " found CalculateEC_KWH"+fResultValTot);
            }
         //   Log.d("DemoApp", " found CalculateEC_KWH"+slabUnitsRest);
            strResult = getBasicRate(strAccNo, strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, slabUnitsRest);
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
            fResultValTot = fResultValTot + fResultVal;
         //   Log.d("DemoApp", " found CalculateEC_KWH"+fResultValTot);
            //fResultVal = fResultVal*iBillMonths;

        } catch (Exception e) {
         //   Log.d("DemoApp", " found CalculateEC_KWH"+e);
            e.printStackTrace();
        }
        //return fResultVal;
        return fResultValTot;
    }

    public static double CalculateMFC (String strAccNo,int iNewConnMonths,double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, long lUnits,long lUnits_Kwh)
    {
      //  Log.d("DemoApp", "mfc  " );
        String strRateType="DC";
        String strResult = "";
        double fResultVal = 0;
        int slabUnits = Math.round((lUnits_Kwh / iBillMonths));
        int slabUnitsRest = (int) (lUnits_Kwh - (slabUnits * (iBillMonths - 1)));
        if (slabUnits > 30 && strTariff.equals("BPL")){
            strTariff = "DOM";
        }else if (slabUnits <= 30 && strTariff.equals("BPL")){
            lUnits=1;// connected load
            lConLoad=1;
        }
        try {
            strResult = getBasicRate (strAccNo,strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, lUnits);
        } catch (SQLException e) {
        //    Log.d("DemoApp", " found getBasicRate"+e);
            e.printStackTrace();
        }
        try {
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
            //For Previous HLK
            if (iBillMonths>iNewConnMonths){fResultVal = fResultVal*iNewConnMonths;}
            //For Other thaanPrevious HLK
            else { fResultVal = fResultVal*iBillMonths;}
        } catch (Exception e) {
       //     Log.d("DemoApp", " found fResultVal"+e);
            e.printStackTrace();
        }
        return fResultVal;
    }

    public static double CalculateMR(String strAccNo,double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, long lUnits)
    {
        String strRateType="MR";
        String strResult = "";
        double fResultVal = 0;
        try {
            strResult = getBasicRate (strAccNo,strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, lUnits);
          //  Log.d("DemoApp", " found strResult"+strResult);
        } catch (SQLException e) {
       //     Log.d("DemoApp", " found CalculateMR"+e);
            e.printStackTrace();
        }
        try {
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
          //  Log.d("DemoApp", " found fResultVal"+fResultVal);
        } catch (Exception e) {
         //   Log.d("DemoApp", " found CalculateMR2"+e);
            e.printStackTrace();
        }
        return fResultVal;
    }

    public static double CalculateED(String strAccNo,double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, double lUnits)
    {
        String strRateType="ED";
        String strResult = "";
        double fResultVal = 0;
        try {
            strResult = getBasicRate (strAccNo,strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, lUnits);
        } catch (SQLException e) {
          //  Log.d("DemoApp", " found CalculateED"+e);
            e.printStackTrace();
        }
        try {
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
        } catch (Exception e) {
         //   Log.d("DemoApp", " found CalculateED2"+e);
            e.printStackTrace();
        }
        return fResultVal;
    }
    public static double CalculateRebate(String strAccNo,double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, double lUnits)
    {
        String strRateType="REB";
        String strResult = "";
        double fResultVal = 0;
        try {
            strResult = getBasicRate (strAccNo,strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, lUnits);
        } catch (SQLException e) {
           // Log.d("DemoApp", " found CalculateRebate"+e);
            e.printStackTrace();
        }
        try {
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
        } catch (Exception e) {
           // Log.d("DemoApp", " found CalculateRebate2"+e);
            e.printStackTrace();
        }
        return fResultVal;
    }
    public static double CalculateSplRebate(String strAccNo,double dK_Volt, String strTariff, long lConLoad, String strConLoadUoM, int iBillMonths, double lUnits)
    {
        String strRateType="SREB";
        String strResult = "";
        double fResultVal = 0;
        try {
            strResult = getBasicRate (strAccNo,strRateType, dK_Volt, strTariff, lConLoad, strConLoadUoM, lUnits);
        } catch (SQLException e) {
           // Log.d("DemoApp", " found CalculateSplRebate"+e);
            e.printStackTrace();
        }
        try {
            fResultVal = Double.parseDouble(GetToken(strResult, ";", 1).replace("#", ""));
        } catch (Exception e) {
          //  Log.d("DemoApp", " found CalculateSplRebate2"+e);
            e.printStackTrace();
        }
        return fResultVal;
    }

    public static void CalculateBill (String strAccNo) throws SQLException {
        //modify on 26.3.20 to calculate HL case
        /*
        //added on 23.05.2019
        String strAccNo="";
        String strSelectSQL_01 = "SELECT CONS_ACC,CASE WHEN CAT_CODE='01' THEN 'DOM' WHEN CAT_CODE='02' THEN 'BPL' WHEN CAT_CODE='04' THEN 'BPL' WHEN CAT_CODE='05' THEN 'BPL' WHEN CAT_CODE='06' THEN 'GPS' WHEN CAT_CODE='22' THEN 'SPP' ELSE 'NIL' END TARIFF,"
                + " CASE WHEN MF >= 1 THEN MF ELSE 1 END AS MF, MTR_OWN, MTR_DIGIT, LOAD, PRV_READ, CR_ADJ, DB_ADJ, PRV_AMOUNT,"//8
                + " HL_UNIT, OLD_AVG, ED_EXEMPT, PST_METER_READ, P_STATUS, MTR_COND, MTR_TP, NO_OF_MON, PRS_MD, CASE WHEN USAGE=1 THEN 1 ELSE 0 END AS SREB_FLAG,"//18
                + " REPLACE(REPLACE(REPLACE(CON_DATE,'-',''),'/',''),' ','') CONN_DATE, STRFTIME('%d%m%Y',OLD_DATE) LAST_MR_DATE,"//20
                + " (strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', OLD_DATE)*12 + strftime('%m', OLD_DATE)) NEWCONN_MONTHS,"
                + " AVG_UNIT, LRC, BILL_BASIS, OLD_STATUS MTR_COND_PRV, OLD_AVG AVG_NM,DPS,MISC_CHG,PRV_YR_ARR,CUR_YR_ARR,ARR_INS_FEE "
                + " FROM BILL_SBM_DATA WHERE CONS_ACC = '" + strAccNo1 + "'";
       // Log.d("DemoApp", "strUpdateSQL_01  " + strSelectSQL_01);
        Cursor rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        */
        int Billcnt=0;
        /*
        while (rs1.moveToNext()) {
            strAccNo = rs1.getString(0);
         //   Log.d("DemoApp", "consumer No  " + strAccNo);
            */
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
                int iBillMonths = 0;
                String strConLoadUoM = "";
                String strBillBasis = "N";
                String strMrTime = "";
                int iMeterType=1;
                int iBilled_cnt = 0;
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
                // Statement statement = ConnectDBSqllite.dbConnection.createStatement();
                //   statement.setQueryTimeout(30);  // set Timeout to 30 sec.
                // CHANGED BY SANTI IN QUERY
                //MTR_OWN TO --CASE WHEN MTR_OWN!='C' AND MTR_SL_NO IS NOT NULL THEN 'G' END
                String strSelectSQL_01 ="";
                strSelectSQL_01 = "SELECT CASE WHEN CAT_CODE='01' THEN 'DOM' WHEN CAT_CODE='02' THEN 'BPL' WHEN CAT_CODE='04' THEN 'BPL' WHEN CAT_CODE='05' THEN 'BPL' WHEN CAT_CODE='06' THEN 'GPS' WHEN CAT_CODE='22' THEN 'SPP' ELSE 'NIL' END TARIFF,"
                + " CASE WHEN MF >= 1 THEN MF ELSE 1 END AS MF, MTR_OWN, MTR_DIGIT, LOAD, PRV_READ, CR_ADJ, DB_ADJ, PRV_AMOUNT,"//8
                + " HL_UNIT, OLD_AVG, ED_EXEMPT, PST_METER_READ, P_STATUS, MTR_COND, MTR_TP, NO_OF_MON, PRS_MD, CASE WHEN USAGE=1 THEN 1 ELSE 0 END AS SREB_FLAG,"//18
                + " REPLACE(REPLACE(REPLACE(CON_DATE,'-',''),'/',''),' ','') CONN_DATE, STRFTIME('%d%m%Y',OLD_DATE) LAST_MR_DATE,"//20
                + " (strftime('%Y',DATETIME('now', 'localtime'))*12 + strftime('%m',DATETIME('now', 'localtime')))-(strftime('%Y', OLD_DATE)*12 + strftime('%m', OLD_DATE)) NEWCONN_MONTHS,"
                + " AVG_UNIT, LRC, BILL_BASIS, OLD_STATUS MTR_COND_PRV, OLD_AVG AVG_NM,DPS,MISC_CHG,PRV_YR_ARR,CUR_YR_ARR,ARR_INS_FEE "
                + " FROM BILL_SBM_DATA WHERE CONS_ACC = '" + strAccNo + "'";
             //   Log.d("DemoApp", "strUpdateSQL_01  " + strSelectSQL_01);
                //ResultSet rs = statement.executeQuery(strSelectSQL_01);
                Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
               // Log.d("DemoApp", "not found");
                double dAmountSplRebate = 0;
                while (rs.moveToNext()) {
                //    Log.d("DemoApp", " found1 ");
                    int iMeterChargeFlag = 0;
                    strTariff = rs.getString(0);
                    double dConLoad = rs.getDouble(4);
                    lConLoad = (long) Math.ceil(dConLoad);
                    if (lConLoad < 1) lConLoad = 1;
                    long lReadPrev = rs.getLong(5);
                    long lReadCurr = rs.getLong(12);
                    long lReadMF_Kwh = rs.getLong(1);
                    double iMeterDigits = rs.getDouble(3);
                    if(iMeterDigits==0){
                        iMeterDigits=5;
                    }
                    //String Edesmpt=rs.getString(11);
                    double dAdj_CR = rs.getDouble(6);
                    double dAdj_DR = rs.getDouble(7);
                    String strMeterOwner = rs.getString(2);
                   // String strMeterCond = rs.getString(14);
                    try {
                        iMeterType = Integer.parseInt(rs.getString(15));
                    }catch(Exception e){
                        iMeterType=1;
                    }
                    //   Log.d("DemoApp", "not found12");
                    String strED_Exempt = rs.getString(11);
                    long lED_Applicable = getED_Applicable(strED_Exempt);
                    // Log.d("DemoApp", "not found13");
                    iBillMonths = rs.getInt(16);
                    //   Log.d("DemoApp", "not found14");

                    double dMD_Current = rs.getDouble(17);
                    int iSplReb_Flag = rs.getInt(18);
                //    Log.d("DemoApp", " found iSplReb_Flag" + iSplReb_Flag);
                    String strDate_Ind_Conn = rs.getString(19);
                    String strDate_Ind_LastMR = rs.getString(20);
                    int iNewConnMonths = rs.getInt(21);
                    long lAvgUnits = rs.getLong(22);
                    long lLRCUnits = rs.getLong(23);
                    strBillBasis = rs.getString(24);
                    String strMeterCond_Prv = rs.getString(25);
                    long lAvgNMUnits = rs.getLong(26);
                    double dps = rs.getDouble(27);
                    double misc_chg = rs.getDouble(28);
                    double provisional_amt = rs.getDouble(8);
                    int Hlunits = rs.getInt(9);
                    double prvyrarr = rs.getDouble(29);
                    double prsyrarr = rs.getDouble(30);
                    int aifi=rs.getInt(31);//added on 29.03.2017
                    prs_mtrcond = strMeterCond_Prv;
                    if (prs_mtrcond.equals("P")) {
                        prs_mtrcond = "O";
                    }
                    // New Consumer Case
                    if (strDate_Ind_Conn.equals(strDate_Ind_LastMR)) {
                        iNewConnMonths=iNewConnMonths+1;
                    }
                    if (!strMeterCond_Prv.equals("P")) {
                        provisional_amt = 0;
                        Hlunits = 0;
                        iBillMonths = iNewConnMonths;
                      //  Log.d("DemoApp", " found22 ");
                    } else {
                      //  iBillMonths = iBillMonths + iNewConnMonths; changed
                        provisional_amt = 0;
                        Hlunits = 0;
                        iBillMonths = iNewConnMonths;
                       // Log.d("DemoApp", " found222 ");
                    }

                    //Log.d("DemoApp", " found3 ");
                    long lLFUnits = lConLoad * 144;
                    if (strTariff.equalsIgnoreCase("DOM")) lLFUnits = lConLoad * 144;
                    else if (strTariff.equalsIgnoreCase("BPL")) lLFUnits = 30;
                    else if (strTariff.equalsIgnoreCase("GPS")) lLFUnits = lConLoad * 216;
                    else if (strTariff.equalsIgnoreCase("SPP")) lLFUnits = lConLoad * 216;
                    else lLFUnits = lConLoad * 144;
                    if (strMeterCond_Prv.equalsIgnoreCase("D") || strMeterCond_Prv.equalsIgnoreCase("S"))
                        strBillBasis = "A";
                 //   if (strMeterCond_Prv.equalsIgnoreCase("H") )
                      //  strBillBasis = "P";
                    //Actual Bill - Actual, Round Complete Cases
                    ///  if (strBillBasis.equalsIgnoreCase("A") || strBillBasis.equalsIgnoreCase("R"))
                   /*
                    if (strMeterCond_Prv.equalsIgnoreCase("O") || strMeterCond_Prv.equalsIgnoreCase("R") || strMeterCond_Prv.equalsIgnoreCase("P")) {
                        if (lReadCurr >= lReadPrev) {
                            lUnits_Kwh = (lReadCurr - lReadPrev) * lReadMF_Kwh;
                        }
                        // Dial-Around Case
                        else {
                            lUnits_Kwh = (pow(10, iMeterDigits) + lReadCurr - lReadPrev) * lReadMF_Kwh;
                            prs_mtrcond = "R";
                        }
                        // if (lUnits_Kwh >= lULimit) // commented as abnormal billing on LRC when unit>10000
                        //    lUnits_Kwh = lLRCUnits; // commented as abnormal billing on LRC when unit>10000
                    }*/
                    //Average Bill - No Meter, Temporary Connection, Assessed Average Cases
                    ///  else if (strBillBasis.equalsIgnoreCase("V") || strBillBasis.equalsIgnoreCase("T"))
                    if (strMeterCond_Prv.equalsIgnoreCase("D") || strMeterCond_Prv.equalsIgnoreCase("S") || strMeterCond_Prv.equalsIgnoreCase("H")) {
                        if (lAvgNMUnits >= 1) //&& lAvgNMUnits < lULimit --deleted
                            lUnits_Kwh = lAvgNMUnits;
                        else
                            lUnits_Kwh = lLFUnits;
                    }
                  //  Log.d("DemoApp", " found4 ");
                    //LRC Bill - DF Meter, HL Cases
                  // /* commented as hl billing disable at present scenario
                    if (strBillBasis.equals("H"))
                    {
                        prs_mtrcond = "P";
                        strBillBasis="A";
                        /*
                        if (lLRCUnits >= 1)// && lLRCUnits < lULimit--deleted
                            lUnits_Kwh = lLRCUnits;
                        else lUnits_Kwh = lAvgNMUnits;
                        */
                        //disable on 010620 upper code
                        if (lAvgNMUnits >= 1)// && lLRCUnits < lULimit--deleted
                            lUnits_Kwh = lAvgNMUnits;
                        else lUnits_Kwh = lLRCUnits;

                    }else{
                        if (lAvgNMUnits >= 1) //&& lAvgNMUnits < lULimit --deleted
                            lUnits_Kwh = lAvgNMUnits;
                        else
                            lUnits_Kwh = lLFUnits;
                    }
                  //  */


                    //ADDED BY SANTI ON 11.01.2016 TO CATCH OTHER MTR_TYP
                //    Log.d("DemoApp", "iMeterType  " + iMeterType);
                    if (iMeterType != 1 && iMeterType != 2 && iMeterType != 3 && iMeterType != 9) {
                        iMeterType = 1;
                    }
               //     Log.d("DemoApp", "iMeterType  " + iMeterType);
                    ///if (strMeterCond.equalsIgnoreCase("O") && !strMeterOwner.equalsIgnoreCase("C")) iMeterChargeFlag = iMeterType;
                    try {
                        //&& !strMeterOwner.equals("C")
                        if ((strMeterCond_Prv.equals("D") || strMeterCond_Prv.equals("S")) ) {
                            iMeterChargeFlag=0;
                        }else{
                            if(strMeterOwner.equals("C")){
                                iMeterChargeFlag=0;
                            }else {
                                iMeterChargeFlag = iMeterType;
                            }
                        }

                        //else {
                        //  iMeterChargeFlag = 1;
                        //}
                 //       Log.d("DemoApp", "strMeterOwner  " + strMeterOwner);
                  //      Log.d("DemoApp", "iMeterChargeFlag  " + iMeterChargeFlag);
                    } catch (Exception e) {
                   //     Log.d("DemoApp", " found C1 " + e);
                  //      Log.d("DemoApp", "77 mfc  " + e);
                    }
                    //if (ConnectDBSqllite.print_flag == 1) System.out.println("##### lReadCurr, lReadPrev & lUnits_Kwh ::" + lReadCurr + " | " + lReadPrev + " | " + lUnits_Kwh);
                    double dK_Volt = 0.230;
                    strConLoadUoM = "KW";
                    long lUnitsDC = lConLoad;
                    //if (ConnectDBSqllite.print_flag == 1) System.out.println("##### lUnitsDC, dConLoad ::" + lUnitsDC + " | " + dConLoad);
                    dAmountMFC = CalculateMFC(strAccNo,iNewConnMonths, dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths, lUnitsDC, (long) lUnits_Kwh);
                    dAmountEC_KWH = CalculateEC_KWH(strAccNo,dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths, (long) lUnits_Kwh);
                    dAmountMR = CalculateMR(strAccNo,dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths, iMeterChargeFlag);
                    // added by santi as mr not available in db
                    dAmountMR = 0;
                    if (iMeterChargeFlag == 1) {
                        dAmountMR = 20 * iNewConnMonths;
                    } else if (iMeterChargeFlag == 2) {
                        dAmountMR = 40 * iNewConnMonths;
                    } else if (iMeterChargeFlag == 3) {
                        dAmountMR = 40 * iNewConnMonths;
                    } else if (iMeterChargeFlag == 9) {
                        dAmountMR = 150 * iNewConnMonths;
                    }
                    // to handle bpl<=30 units metrent 0
                    if(Math.round(lUnits_Kwh/iBillMonths)<=30&&strTariff.equals("BPL")){
                        dAmountMR=0;
                    }
                    //end mr calculation
                    ///
                    //  if (ConnectDBSqllite.print_flag == 1) System.out.println("##### strTariff, dAmountEC_KWH ::" + strTariff + " | " +  dAmountEC_KWH);
                    // Rebate handling for PIN / SPP
                    if (strTariff.equalsIgnoreCase("SPP") || strTariff.equalsIgnoreCase("PIN")) {
                        dAmountRebate = CalculateRebate(strAccNo,dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths,  (dAmountEC_KWH + dAmountMFC));
                        if (iSplReb_Flag == 1) {
                            dAmountSplRebate = CalculateSplRebate(strAccNo,dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths,  (lUnits_Kwh - Hlunits));
                            dAmountRebate = dAmountRebate + dAmountSplRebate;
                        }
                    } else {
                        dAmountRebate = CalculateRebate(strAccNo,dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths,  (lUnits_Kwh - Hlunits));
                        //Added on 29.03.2017 t get rebate for rural consumer domestic
                        if(aifi==1){
                          //  dAmountRebate=dAmountRebate+(0.05*(lUnits_Kwh - Hlunits));
                        }
                        ///end
                    }
                    // to handle bpl<=30 units
                    if(Math.round(lUnits_Kwh/iBillMonths)<=30&&strTariff.equals("BPL")){
                        dAmountRebate=0;
                    }
                    //to handle -ve rebate
                    if (dAmountRebate < 0) {
                        dAmountRebate = 0;
                    }

                    // CaseWise To be Handled
                    //if (ConnectDBSqllite.print_flag == 1) System.out.println("##### lED_Applicable, strED_Exempt, (dAmountEC_KWH * lED_Applicable), dAmountEC_KWH ::" + lED_Applicable + " | " + strED_Exempt + " | " + (dAmountEC_KWH * lED_Applicable) + " | " +  dAmountEC_KWH);
                    dAmountED = CalculateED(strAccNo,dK_Volt, strTariff, lConLoad, strConLoadUoM, iBillMonths, (dAmountEC_KWH * lED_Applicable));
                    dPrsentBlAmount = dAmountMFC + dAmountEC_KWH + dAmountMR + dAmountED;
                    dAmountArrear = prvyrarr + prsyrarr;
                    // dAmountRebateBillCurr = dAmountMFC + dAmountEC_KWH + dAmountMR + dAmountED - dAmountRebate - dAmountSplRebate - dAdj_CR + dAdj_DR;
                    // dAmountRebateBillTot = dAmountMFC + dAmountEC_KWH + dAmountMR + dAmountED - dAmountRebate - dAmountSplRebate - dAdj_CR + dAdj_DR + dAmountArrear;
                    // dAmountRebateBillCurrRnd = Math.round(dAmountMFC + dAmountEC_KWH + dAmountMR + dAmountED - dAmountRebate - dAmountSplRebate - dAdj_CR + dAdj_DR);
                    // dAmountRebateBillTotRnd = Math.round(dAmountMFC + dAmountEC_KWH + dAmountMR + dAmountED - dAmountRebate - dAmountSplRebate - dAdj_CR + dAdj_DR + dAmountArrear);

                    // dAmountBillCurr = dAmountMFC + dAmountEC_KWH + dAmountMR + dAmountED - dAdj_CR + dAdj_DR;
                    dAmountBillTot = dPrsentBlAmount - dAdj_CR + dAdj_DR + dAmountArrear + dps + misc_chg - provisional_amt;
                    // dAmountBillCurrRnd = Math.round(dAmountMFC + dAmountEC_KWH + dAmountMR + dAmountED - dAdj_CR + dAdj_DR);
                    //dAmountBillTotRnd = Math.round(dAmountMFC + dAmountEC_KWH + dAmountMR + dAmountED - dAdj_CR + dAdj_DR + dAmountArrear);
                }
                rs.close();

                //strSelectSQL_01 = "SELECT DATE('now', 'localtime'), CASE WHEN DATE('now', '+15 days', 'localtime') > DATE('now','start of month','+1 month','-1 day') THEN DATE('now','start of month','+1 month','-1 day') ELSE DATE('now', '+15 days', 'localtime') END AS DUE_DATE,"
                  //      + "strftime('%H%M%S','now', 'localtime'), CASE WHEN DATE('now', '+4 days', 'localtime') > DATE('now','start of month','+1 month','-1 day') THEN DATE('now','start of month','+1 month','-1 day') ELSE DATE('now', '+4 days', 'localtime') END AS SPP_DUE_DATE ";
                //upper statement disable on 010620 due to change in rebate date from 15 days to 7 days
                strSelectSQL_01 = "SELECT DATE('now', 'localtime'), CASE WHEN DATE('now', '+7 days', 'localtime') > DATE('now','start of month','+1 month','-1 day') THEN DATE('now','start of month','+1 month','-1 day') ELSE DATE('now', '+7 days', 'localtime') END AS DUE_DATE,"
                        + "strftime('%H%M%S','now', 'localtime'), CASE WHEN DATE('now', '+4 days', 'localtime') > DATE('now','start of month','+1 month','-1 day') THEN DATE('now','start of month','+1 month','-1 day') ELSE DATE('now', '+4 days', 'localtime') END AS SPP_DUE_DATE ";

                rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
                while (rs.moveToNext()) {
                    strBillDate = rs.getString(0);
                    strDueDate = rs.getString(1);
                    strMrTime = rs.getString(2);
                    strDueDateSPP = rs.getString(3);
                }
                //Added on this logic for shorting walking sequence////////
                if(strMrTime.length()==5){
                    strMrTime="0"+strMrTime;
                }
                //
                if (strTariff.equalsIgnoreCase("SPP") || strTariff.equalsIgnoreCase("PIN")) {
                    strDueDate = strDueDateSPP;
                }
                // if (ConnectDBSqllite.print_flag == 1) System.out.println("##### Billing Date & Rebate Date ::" + strBillDate + " | " + strDueDate);
                rs.close();
                String strSelectSQL_02="select BILLED_COUNT from File_desc where version_flag=2";
                Cursor rs2 = DatabaseAccess.database.rawQuery(strSelectSQL_02, null);
                while (rs2.moveToNext()) {
                    iBilled_cnt=rs2.getInt(0);
                }
                rs2.close();
                iBilled_cnt=iBilled_cnt+1;

               // Log.d("DemoApp", "iBilled_cnt  " + iBilled_cnt);
                String strUpdateSQL_01 = "UPDATE BILL_SBM_DATA SET MTR_COND= '" + prs_mtrcond + "',UNITS= " + lUnits_Kwh + "," +
                        " BILL_BASIS= '" + strBillBasis + "',EC= " + dAmountEC_KWH + ",MMFC= " + dAmountMFC + ",MTR_RENT= " + dAmountMR + ",BILL_NO= " + iBilled_cnt + "," +
                        " ED= " + dAmountED + ",CUR_TOTAL= " + dPrsentBlAmount + ",REBATE= " + dAmountRebate + ",MTR_TIME= '" + strMrTime + "',BILL_TOTAL= " + dAmountBillTot + "," +
                        " DUE_DATE= '" + strDueDate + "',DISCONNECTION_DATE= '" + strDueDate + "',BILL_DATE= '" + strBillDate + "' , BILL_FLAG = 1 , NO_BILLED_MONTH= " + iBillMonths +
                        " WHERE CONS_ACC = '" + strAccNo + "'";
              //  Log.d("DemoApp", "strMrTime  " + strMrTime);
                // Log.d("DemoApp", "strTariff  " + strTariff);
                //  Log.d("DemoApp", "lConLoad  " + lConLoad);
                //   Log.d("DemoApp", "iBillMonths  " + iBillMonths);
                //  Log.d("DemoApp", "strConLoadUoM  " + strConLoadUoM);
                //    Log.d("DemoApp", "dAmountMFC  " + dAmountMFC);
                //   Log.d("DemoApp", "dAmountEC_KWH  " + dAmountEC_KWH);
                //    Log.d("DemoApp", "dAmountED  " + dAmountED);
                //    Log.d("DemoApp", "dAmountMR  " + dAmountMR);
                //     Log.d("DemoApp", "dPrsentBlAmount  " + dPrsentBlAmount);
                //    Log.d("DemoApp", "dAmountRebate  " + dAmountRebate);
                //    Log.d("DemoApp", "strBillDate  " + strBillDate);
                     Log.d("DemoApp", "Bll Processed count" + Billcnt);

                //statement.executeUpdate(strUpdateSQL_01);
                // statement.close();
             //   Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
                DatabaseAccess.database.execSQL(strUpdateSQL_01);
                String strUpdateSQL_02 = "UPDATE FILE_DESC SET BILLED_COUNT = BILLED_COUNT +1";
                DatabaseAccess.database.execSQL(strUpdateSQL_02);
            }catch (Exception e)      {
                Log.d("DemoApp", "Exception  calculate bill" + e);
            }
            Billcnt++;

    }
    public static void  CalculateBill_All () throws SQLException
    {
/*
        int iCnt = 0;
        //PUTTING HL
        String strUpdateSQL_01 = "UPDATE BILL_SBM_DATA SET BILL_BASIS = 'H' WHERE BILL_FLAG = 0 ";
        Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
        DatabaseAccess.database.execSQL(strUpdateSQL_01);

        String strSelectSQL_01= "SELECT CONS_ACC FROM BILL_SBM_DATA WHERE BILL_FLAG = 0 "; // AND CAT_CODE='22' LIMIT 10
        //  ResultSet rs = statement.executeQuery(strSelectSQL_01);
        Cursor rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        while(rs.moveToNext())
        {
            CalculateBill(rs.getString(0));
            iCnt++;
        }
          iCnt++;
*/
    }
    public static String GetToken(String strLine, String strDelim, int TokenNo) throws Exception
    {
        int iCnt = 1;
        String strValue=strLine;
        StringTokenizer st = new StringTokenizer(strLine, strDelim);
        while(st.hasMoreTokens())
        {
            strValue = st.nextToken();
            //System.out.println(strValue );
            if (TokenNo == iCnt)
            {
                break;
            }
            iCnt++;
        }
        return strValue;
    }

}

