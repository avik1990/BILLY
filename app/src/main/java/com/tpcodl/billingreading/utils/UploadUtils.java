package com.tpcodl.billingreading.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import com.tpcodl.billingreading.BuildConfig;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.database.DatabaseKeys;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.uploadingrequestModel.ChildReqDetails;
import com.tpcodl.billingreading.uploadingrequestModel.HeaderresDetails;
import com.tpcodl.billingreading.uploadingrequestModel.RequestModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadUtils {
    private RequestModel model;
    private DatabaseHelper mDBHelper;
    private Context mcontext;
    private ActivityUtils utils;
    public String conditon = null;
    int count = 0;
    int count1 = 0;

    public UploadUtils(Context context) {
        mcontext = context;
        mDBHelper = new DatabaseHelper(mcontext);
        model = new RequestModel();
        utils = ActivityUtils.getInstance();

        try {
            mDBHelper.createDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
    }

    public void getHeaderdetails(String condition) {
        Log.e("HeaderCondition", condition);
        List<HeaderresDetails> hlist = null;
        hlist = new ArrayList<>();
        HeaderresDetails hd = null;
        Cursor cursor = null;
        cursor = mDBHelper.getUserheaderdetails(condition);
        model.setUserId(utils.getUserID());
        model.setType(Constant.appType);
        model.setAppVersion(BuildConfig.VERSION_NAME);

        if (PreferenceHandler.getisSBNONSBFLAG(mcontext).equalsIgnoreCase("SBM")) {
            model.setConsumerType(Constant.consymerTypeSBM);
        } else {
            model.setConsumerType(Constant.consymerTypeNONSBM);
        }

        model.setHeader(null);
        /////////need to change//////////
        if (cursor.moveToFirst()) {
            do {
                hd = new HeaderresDetails();
                //Log.e("HeaderInstallation ", count1++ + "Cnt: " + cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_INSTALLATION)));
                //Log.d("DATABASE", cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_NAME)));
                hd.setName(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_NAME)));
                hd.setNewMeterNo(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_NEW_METER_NO)));
                hd.setAmountPayable(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_AMOUNT_PAYABLE)));
                hd.setBillMonth(cursor.getString(cursor.getColumnIndex("BILL_MONTH")));
                hd.setBillYear(cursor.getString(cursor.getColumnIndex("BILL_YEAR")));
                hd.setAddress1(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ADDRESS1)));
                hd.setAddress2(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ADDRESS2)));
                hd.setAifi(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_AIFI)));
                hd.setAvgUnitBilled((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_AVG_UNIT_BILLED))));
                hd.setBank(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BANK)));
                hd.setBill_no(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BILL_NO)));
                hd.setCapturedMobile(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_CAPTURED_MOBILE)));
                hd.setCblass(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_CBLASS)));
                hd.setArrears(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ARREARS)));
                hd.setAsd(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ASD)));
                hd.setAsdaa(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ASDAA)));
                hd.setFlag(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_FLAG)));
                hd.setGpsLatitude(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_GPS_LATITUDE)));
                hd.setGpsLongitude(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_GPS_LONGITUDE)));
                hd.setGstRelevant1(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_GST_RELEVANT1)));
                hd.setGstRelevant2(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_GST_RELEVANT2)));
                hd.setGstRelevant3(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_GST_RELEVANT3)));
                hd.setGstRelevant4(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_GST_RELEVANT4)));
                hd.setHlMonths(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_HL_MONTHS)));
                hd.setHlUnit(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_HL_UNITS)));
                hd.setHostelRbt(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_HOSTEL_RBT)));
                //hd.setChqdt("08-10-2020 18:30:00");
                /*hd.setBillMonth((cursor.getColumnIndex(DatabaseKeys.Key_BILL_MONTH)));
                hd.setBillYear(cursor.getColumnIndex(DatabaseKeys.Key_BILL_YEAR));*/
                hd.setPpac(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PPAC)));
                hd.setChqno(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_CHQNO)));
                hd.setContactReasonId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_CONTACT_REASON_ID)));
                hd.setCurrentBillTotal((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_CURRENT_BILL_TOTAL))));
                hd.setDueDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DUE_DATE)));


                hd.setBuildingCode(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BUILDING_CODE)));
                hd.setBuildingDesc(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BUILDING_DESC)));
                hd.setConsumerOwned(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_CONSUMER_OWNED)));
                hd.setCrAdj(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_CR_ADJ)));

                hd.setDbAdj(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DB_ADJ)));
                hd.setDisconnectionFlg(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DISCONNECTION_FLG)));
                hd.setDiv(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DIV)));

                hd.setDoGenerated(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DO_GENERATED)));
                hd.setDps(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DPS)));
                hd.setDps5(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DPS5)));
                hd.setDpsBilled(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DPS_BILLED)));
                hd.setDpsBlld(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DPS_BLLD)));
                hd.setDpsLvd(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DPS_LVD)));
                hd.setEcSlab1(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_EC_SLAB_1)));
                hd.setEcSlab2(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_EC_SLAB_2)));
                hd.setEcSlab3(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_EC_SLAB_3)));
                hd.setEcSlab4(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_EC_SLAB_4)));
                hd.setEcsLimt(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ECS_LIMT)));
                hd.setEcsValidityPeriod(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ECS_VALIDITY_PERIOD)));
                hd.setEdExempt(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ED_EXEMPT)));
                hd.setEdRbt(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ED_RBT)));
                hd.setFcSlab(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_FC_SLAB)));
                // hd.setDue_date("08-10-2020 18:30:00");
                hd.setEc((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_EC))));
                hd.setEd((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ED))));
                hd.setEltStts(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ELT_STTS)));

                // hd.setInsert_date("08-10-2020 18:30:00");
                hd.setInsertTime((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_INSERT_TIME))));
                //  hd.setInsert_time("08-10-2020 18:30:00");
                hd.setInstallation(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_INSTALLATION)));
                hd.setAverageKwh(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_AVERAGE_KWH)));
                hd.setBillBasis(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BILL_BASIS)));

                hd.setLastPaidAmt(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_LAST_PAID_AMT)));

                hd.setLastPymtRcpt(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_LAST_PYMT_RCPT)));
                hd.setLegacyAccountNo(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_LEGACY_ACCOUNT_NO)));
                hd.setLegacyAccountNo2(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_LEGACY_ACCOUNT_NO2)));
                hd.setMeterMake(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_METER_MAKE)));
                hd.setMeterRent(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_METER_RENT)));
                hd.setMiscCharges(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MISC_CHARGES)));
               /*installationList.add(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_INSTALLATION)));
                Log.e("ArraySize", "" + installationList.size());*/
                hd.setInvoiceNo(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_INVOICE_NO)));
                hd.setMeterHeightId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_METER_HEIGHT_ID)));
                hd.setCa(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_CA)));
                hd.setMeterLoca(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_METER_LOCA)));
                hd.setMeterTypeId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_METER_TYPE_ID)));
                hd.setMmfc((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MMFC))));
                hd.setMrRemarkDet(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MR_REMARK_DET)));
                hd.setMrentCharged((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MRENT_CHARGED))));
                hd.setMrreason(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MRREASON)));

                hd.setMru(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MRU)));
                hd.setMtrImage(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MTR_IMAGE)));
                hd.setNoBilledMonth(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_NO_BILLED_MONTH)));
                hd.setNoOfReg(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_NO_OF_REG)));
                hd.setNotToBillAfter(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_NOT_TO_BILL_AFTER)));
                hd.setNotinuseFlgEnddate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_NOT_IN_USE_FLG_END_DATE)));
                hd.setSdi(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SDI)));


                // hd.setMrreason("s");
                hd.setNewMtrFlg(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_NEW_MTR_FLG)));
                hd.setOldMtrCorFlg(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_OLD_MTR_COR_FLG)));

                hd.setOtherFlgs(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_OTHER_FLGS)));
                hd.setPoleNo(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_POLE_NO)));
                hd.setPortion(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PORTION)));


                hd.setRevisitFlag(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_REVISIT_FLAG)));
                hd.setRqcflg(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_RQC_FLG)));
                hd.setRural(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_RURAL)));
                hd.setSanLoad(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SAN_LOAD)));
                hd.setSanLoadEffectiveDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SAN_LOAD_EFFECTIVE_DATE)));
                hd.setSanLoadUnits(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SAN_LOAD_UNITS)));
                hd.setSecDepositAmt(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SEC_DEPOSIT_AMT)));
                hd.setSection(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SECTION)));


                hd.setSentFlag(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SENT_FLAG)));
                //hd.setOsbill_date("08-10-2020 18:30:00");
                hd.setPaperPasteById(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PAPER_PASTE_BY_ID)));
                hd.setPhone1((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PHONE_1))));
                hd.setPhone2((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PHONE_2))));
                //  hd.setPhoto_upload_flg(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PHOTO_UPLOAD_FLG)));
                hd.setPpac((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PPAC))));
                hd.setPpi(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PPI)));

                hd.setPresentReadingRemark(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PRESENT_READING_REMARK)));

                hd.setPrevBillRemark(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PREV_BILL_REMARK)));
                hd.setPrevBillType(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PREV_BILL_TYPE)));
                hd.setPrevBillUnits(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PREV_BILL_UNITS)));
                hd.setPrevProvAmt(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PREV_PROV_AMT)));
                hd.setPreviousBilledProvUnit(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PREVIOUS_BILLED_PROV_UNIT)));
                hd.setProvEd(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PROV_ED)));
                hd.setProvPptAmt(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PROV_PPT_AMT)));
                hd.setPrvArr(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PRV_ARR)));
                hd.setPrvBilledAmt(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PRV_BILLED_AMT)));
                hd.setRateCategory(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_RATE_CATEGORY)));
                hd.setRateSlab1(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_RATE_SLAB1)));
                hd.setRateSlab2(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_RATE_SLAB2)));
                hd.setRateSlab3(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_RATE_SLAB3)));
                hd.setRateSlab4(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_RATE_SLAB4)));
                hd.setPresentBillType(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PRESENT_BILL_TYPE)));
                hd.setPresentBillUnits((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PRESENT_BILL_UNITS))));
                hd.setPresentMeterStatus(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PRESENT_METER_STATUS)));
                hd.setPresent_reading_remark(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PRESENT_READING_REMARK)));
                hd.setPresentReadingTime((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PRESENT_READING_TIME))));
                hd.setProgressionState(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PROGRESSION_STATE)));
                hd.setRcptamt((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_RCPTAMT))));
                hd.setReasonCdId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_REASON_CD_ID)));
                hd.setRcptno(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_RCPTNO)));
                hd.setRcrdLoad(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_RCRD_LOAD)));
                hd.setReadFlag(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_READ_FLAG)));
                hd.setReadOnly(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_READ_ONLY)));
                hd.setReasonDcId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_REASON_DC_ID)));
                hd.setReasonEnId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_REASON_EN_ID)));
                hd.setReasonMtrStuckId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_REASON_MTR_STUCK_ID)));
                hd.setReasonNvId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_REASON_NV_ID)));
                hd.setReasonPlId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_REASON_PL_ID)));
                hd.setRebate((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_REBATE))));
                hd.setRoundOff((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ROUND_OFF))));

                hd.setSealStsId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SEAL_STS_ID)));
                hd.setSealStts(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SEAL_STTS)));
                //hd.setSenderMobile(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SENDER_MOBILE)));
                hd.setSenderMobile(PreferenceHandler.getisUserId(mcontext));
                hd.setStopPaprBl(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_STOP_PAPR_BL)));
                hd.setTypesObstacleId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_TYPES_OBSTACLE_ID)));
                hd.setSupplySourceId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SUPPLY_SOURCE_ID)));
                hd.setSupplyStatusId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SUPPLY_STATUS_ID)));
                hd.setUnsafeCond(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_UNSAFE_COND)));

                hd.setBillPrnFooter(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BILL_PRN_FOOTER)));
                hd.setBillPrnHeader(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BILL_PRN_HEADER)));

                hd.setSpecialRem(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SPECIAL_REM)));
                hd.setSubDiv(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SUB_DIV)));
                hd.setSubSeq(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SUB_SEQ)));
                hd.setSupplyTypFlg(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SUPPLY_TYP_FLG)));
                hd.setSwjlDhFl(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SWJLDHF1)));
                hd.setTdDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_TD_DATE)));
                hd.setTdFlag(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_TD_FLAG)));
                hd.setTransType(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_TRANS_TYPE)));
                hd.setUlf(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ULF)));
                hd.setUlfMdi(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ULF_MDI)));
                hd.setUnitSlab1(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_UNIT_SLAB1)));
                hd.setUnitSlab2(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_UNIT_SLAB2)));
                hd.setUnitSlab3(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_UNIT_SLAB3)));
                hd.setUnitSlab4(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_UNIT_SLAB4)));
                hd.setUpdFlag1(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_UPD_FLAG1)));
                hd.setUpdFlag2(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_UPD_FLAG2)));
                hd.setUpdateTime(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_UPDATE_TIME)));
                hd.setUsageId(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_USAGE_ID)));
                hd.setUsage(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_USAGE)));
                hd.setUserType(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_USER_TYPE)));
                hd.setWalkingSeqChk(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_WALKING_SEQ_CHK)));
                // hd.setUpdate_date("08-10-2020 18:30:00");
                hd.setSeq(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SEQ)));
                hd.setUpdateTime((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_UPDATE_TIME))));
                // hd.setUpdate_time("08-10-2020 18:30:00");
                hd.setUsage_id(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_USAGE_ID)));
                hd.setWlaking_seq_chk(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_WALKING_SEQ_CHK)));

                //if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SBM_BILL_NO)).contains("SB")) {
                //}
                hd.setAdjBill(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_ADJ_BILL)));

                try {
                    hd.setSbmBillNo((cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SBM_BILL_NO)).replaceAll("SB", "").trim()));
                } catch (Exception e) {

                }


                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_INSERT_DATE)) != null) {
                    hd.setInsertDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_INSERT_DATE))));
                } else {
                    //  hd.setInsertDate("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PREV_BILL_END_DATE)) != null) {
                    hd.setPrevBillEndDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PREV_BILL_END_DATE))));
                } else {
                    //hd.setPrevBillEndDate("");
                }

                try {
                    if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BILL_START_DATE)) != null) {
                        if (!cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BILL_START_DATE)).isEmpty()) {
                            hd.setBillStartDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BILL_START_DATE))));
                        }
                    } else {
                        // hd.setBillStartDate("");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_CHQDT)) != null) {
                    hd.setChqdt(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_CHQDT))));
                } else {
                    // hd.setChqdt("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SCHEDULE_METER_READ_DATE)) != null) {
                    hd.setScheduleMeterReadDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_SCHEDULE_METER_READ_DATE))));
                } else {
                    // hd.setScheduleMeterReadDate("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PRESENT_READING_DATE)) != null) {
                    hd.setPresentReadingDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_PRESENT_READING_DATE))));
                } else {
                    //hd.setPresentReadingDate("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_LAST_PAID_DATE)) != null) {
                    hd.setLastPaidDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_LAST_PAID_DATE))));
                } else {
                    // hd.setLastPaidDate("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BILL_END_DATE)) != null) {
                    hd.setBillEndDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BILL_END_DATE))));
                } else {
                    //  hd.setBillEndDate("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BP_END_DATE)) != null) {
                    hd.setBpEndDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BP_END_DATE))));
                } else {
                    //hd.setBpEndDate("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BP_START_DATE)) != null) {
                    hd.setBpStartDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_BP_START_DATE))));
                } else {
                    // hd.setBpStartDate("");
                }


                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DUE_DATE)) != null) {
                    hd.setDueDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DUE_DATE))));
                } else {
                    //hd.setDueDate("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_INSERT_DATE)) != null) {
                    hd.setInsert_date(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_INSERT_DATE))));
                } else {
                    // hd.setInsert_date("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_LAST_NORMAL_BILL_DATE)) != null) {
                    hd.setLastNormalBillDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_LAST_NORMAL_BILL_DATE))));
                } else {
                    // hd.setLastNormalBillDate("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MOVE_IN_DATE)) != null) {
                    hd.setMoveInDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MOVE_IN_DATE))));
                } else {
                    // hd.setMoveInDate("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_OSBILL_DATE)) != null) {
                    hd.setOsbillDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_OSBILL_DATE))));
                } else {
                    // hd.setOsbillDate("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_REF_MR_DATE)) != null) {
                    hd.setRefMrDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_REF_MR_DATE))));
                } else {
                    //hd.setRefMrDate("");
                }
                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_UPDATE_DATE)) != null) {
                    hd.setUpdateDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_UPDATE_DATE))));
                } else {
                    // hd.setUpdateDate("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DO_EXPIRY)) != null) {
                    hd.setDoExpiry(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_DO_EXPIRY))));
                } else {
                    // hd.setDoExpiry("");
                }

                if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MOVE_IN_DATE)) != null) {
                    hd.setMoveInDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MOVE_IN_DATE))));
                } else {
                    /// hd.setMoveInDate("");
                }

                hd.setModDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Key_MODE_DATE)));

                hlist.add(hd);
            } while (cursor.moveToNext());
            model.setHeader(hlist);
        }
    }

    public RequestModel getChilddetails(String condition) {
        ChildReqDetails cd = null;
        Cursor cursor = null;
        List<ChildReqDetails> clist = null;
        cursor = mDBHelper.getUserChilddetails(condition);
        Log.e("CHildCondition", utils.getSerchCondition());
        clist = new ArrayList<>();
        model.setChilld(null);

        if (cursor.moveToFirst()) {
            do {
                try {
                    cd = new ChildReqDetails();
                    //Log.e("CounterChild", "" + count++);
                    //Log.e("ChildInstallation", "" + cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_INSTALLATION)));
                    cd.setAblbelnr(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_ABLBELNR)));
                    cd.setBilledMd((cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_BILLED_MD))));
                    cd.setConsumptionOldMeter((cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_CONSUMPTION_OLD_METER))));
                    cd.setEquipmentNo(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_EQUIPMENT_NO)));
                    cd.setInstallation(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_INSTALLATION)));
                    cd.setCalMonCnt(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childkey_CAL_MON_CNT)));
                    cd.setBillMonth((cursor.getString(cursor.getColumnIndex("BILL_MONTH"))));
                    cd.setBillYear((cursor.getString(cursor.getColumnIndex("BILL_YEAR"))));
                    cd.setLastOkRdng((cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_LAST_OK_RDNG))));
                    cd.setMeterCondition(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_METER_CONDITION)));
                    cd.setMeter_no(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_METER_NO)));
                    cd.setMrreason(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_MRREASON)));

                    cd.setMeterRemovedOn(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_METER_REMOVED_ON)));
                    cd.setMeterTyp(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_METER_TYP)));
                    cd.setNewMeterFlg(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_NEW_METER_FLG)));


                    cd.setMf(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_MF)));
                    cd.setNoOfDigits(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_NO_OF_DIGITS)));
                    cd.setNoOfReg(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_NO_OF_REG)));
                    cd.setPrevMtrRead(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_PREV_MTR_READ)));

                    cd.setPrsMd(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_PRS_MD)));
                    cd.setUserType(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_USER_TYPE)));
                    //cd.setMrreason("s");
                    cd.setPresentMeterReading((cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_PRESENT_METER_READING))));
                    cd.setPrsMd((cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_PRS_MD))));
                    cd.setRegisterCode(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Childkey_REGISTER_CODE)));

                    try {
                        cd.setPreviousMd(cursor.getString(cursor.getColumnIndex("PREVIOUS_MD")));
                    } catch (Exception e) {

                    }
                 /*   try {
                        Log.e("PRESENT_METER_FLAG", cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_PRESENT_METER_READING)));
                    } catch (Exception e) {
                    }

                    Log.e("REGISTERCODE_CHILD", cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_REGISTER_CODE)));*/

                    /*if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_METER_INSTALL_DATE)) != null) {
                        cd.setMeterInstallDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_METER_INSTALL_DATE))));
                    } else {
                       // cd.setMeterInstallDate("");
                    }*/
                    if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_PREV_READ_DATE)) != null) {
                        cd.setPrevReadDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_PREV_READ_DATE))));
                    } else {
                        //cd.setPrevReadDate("");
                    }

                    if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_SCHEDULE_METER_READ_DATE)) != null) {
                        cd.setScheduleMeterReadDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.childKey_SCHEDULE_METER_READ_DATE))));
                    } else {
                        // cd.setScheduleMeterReadDate("");
                    }

                    if (cursor.getString(cursor.getColumnIndex(DatabaseKeys.Childkey_REFERENCE_DATE)) != null) {
                        cd.setReferenceDate(UtilsClass.getFormateDate(cursor.getString(cursor.getColumnIndex(DatabaseKeys.Childkey_REFERENCE_DATE))));
                    } else {
                        // cd.setReferenceDate("");
                    }

                    clist.add(cd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
             model.setChilld(clist);
        }
        return model;

        //  uploadData();

    }
}





