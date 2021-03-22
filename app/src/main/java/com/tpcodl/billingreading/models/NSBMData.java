package com.tpcodl.billingreading.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NSBMData {

    String LEGACY_ACCOUNT_NO;
    String NAME;
    String ADDRESS1;
    String ADDRESS2;
    String SEQ;
    String SUB_SEQ;
    String LEGACY_ACCOUNT_NO2;
    String RATE_CATEGORY;
    String DIV;
    String SUB_DIV;
    String SECTION;
    String CONSUMER_OWNED;
    String METER_MAKE;
    String USAGE;
    String SAN_LOAD;
    String MOVE_IN_DATE;
    String DPS;
    String MISC_CHARGES;
    String CR_ADJ;
    String DB_ADJ;
    String PRV_BILLED_AMT;
    String PREVIOUS_BILLED_PROV_UNIT;
    String LAST_PAID_DATE;
    String LAST_PYMT_RCPT;
    String LAST_PAID_AMT;
    String ED_EXEMPT;
    String AIFI;
    String NEW_METER_NO;
    String SDI;
    String ASD;
    String ASDAA;
    String INSTALLATION;
    String SBM_BILL_NO;
    String CA;
    String PRV_ARR;
    String ARREARS;
    String ULF;
    String PREV_BILL_UNITS;
    String BILL_MONTH;
    String ECS_LIMT;
    String ECS_VALIDITY_PERIOD;
    String PRESENT_READING_REMARK;
    String PRESENT_METER_STATUS;
    String PRESENT_BILL_UNITS;
    String PRESENT_BILL_TYPE;
    String EC;
    String MMFC;
    String MRENT_CHARGED;
    String ED;
    String CURRENT_BILL_TOTAL;
    String REBATE;
    String AMOUNT_PAYABLE;
    String AVG_UNIT_BILLED;
    String RCPTNO;
    String CHQNO;
    String CHQDT;
    String BANK;
    String RCPTAMT;
    String DUE_DATE;
    String DO_EXPIRY;
    String PRESENT_READING_TIME;
    String OSBILL_DATE;
    String CAPTURED_MOBILE;
    String SCHEDULE_METER_READ_DATE;
    String METER_RENT;
    String PORTION;
    String MRU;
    String NO_OF_REG;
    String SAN_LOAD_UNITS;
    String SAN_LOAD_EFFECTIVE_DATE;
    String SUPPLY_TYP_FLG;
    String NOTINUSE_FLG_ENDDATE;
    String OTHER_FLGS;
    String ED_RBT;
    String HOSTEL_RBT;
    String PREV_BILL_TYPE;
    String PREV_BILL_REMARK;
    String PREV_BILL_END_DATE;
    String LAST_NORMAL_BILL_DATE;
    String PRESENT_READING_DATE;
    String AVERAGE_KWH;
    String BILL_BASIS;
    String BILL_NO;
    String INVOICE_NO;
    String READ_ONLY;
    String BP_START_DATE;
    String BP_END_DATE;
    String PPAC;
    String ROUND_OFF;
    String SEC_DEPOSIT_AMT;
    String DO_GENERATED;
    String NOT_TO_BILL_AFTER;
    String INSERT_DATE;
    String INSERT_TIME;
    String UPDATE_DATE;
    String UPDATE_TIME;
    String PROGRESSION_STATE;
    String PHONE_1;
    String PHONE_2;
    String BILL_YEAR;
    String TRANS_TYPE;
    String TD_FLAG;
    String TD_DATE;
    String PPI;
    String PREV_PROV_AMT;
    String GST_RELEVANT1;
    String GST_RELEVANT2;
    String GST_RELEVANT3;
    String GST_RELEVANT4;
    String ELT_STTS;
    String SEAL_STTS;
    String CBLASS;
    String HL_MONTHS;
    String MRREASON;
    String USAGE_ID;
    String METER_TYPE_ID;
    String SUPPLY_STATUS_ID;
    String REASON_DC_ID;
    String SUPPLY_SOURCE_ID;
    String REASON_NV_ID;
    String REASON_CD_ID;
    String REASON_EN_ID;
    String REASON_MTR_STUCK_ID;
    String PAPER_PASTE_BY_ID;
    String METER_HEIGHT_ID;
    String TYPES_OBSTACLE_ID;
    String SEAL_STS_ID;
    String CONTACT_REASON_ID;
    String REASON_PL_ID;
    String BUILDING_DESC;
    String BUILDING_CODE;
    String POLE_NO;
    String FLAG;
    String SPECIAL_REM;
    String GPS_LONGITUDE;
    String GPS_LATITUDE;
    String SENDER_MOBILE;
    String CONSUMER_TYPE;
    String WALKING_SEQ_CHK;
    String METER_LOCA;
    String MR_REMARK_DET;
    String STOP_PAPR_BL;
    String NEW_MTR_FLG;
    String OLD_MTR_COR_FLG;
    String UNSAFE_COND;
    String READ_FLAG;
    String SENT_FLAG;
    String MTR_IMAGE;
    String UPD_FLAG1;
    String UPD_FLAG2;
    ///////////////////
    String ABLBELNR;
    String METER_NO;
    String METER_TYP;
    String MF;
    String NO_OF_DIGITS;
    String PREV_MTR_READ;
    String PREV_READ_DATE;
    String METER_CONDITION;
    String LAST_OK_RDNG;
    String METER_INSTALL_DATE;
    String PRESENT_METER_READING;
    String EQUIPMENT_NO;
    String NEW_METER_FLG;
    String REGISTER_CODE;
    String METER_REMOVED_ON;
    String CONSUMPTION_OLD_METER;
    String PREVIOUS_MD;
    String BILLED_MD;
    String PRS_MD;
    String REVISIT_FLAG;
    String REF_MR_DATE;
    List<String> listRegisterCoe;
    List<String> listMeterNo;
    List<String> listRegisterValue;


    Map<String, String> LinkedHashMapValues = new LinkedHashMap<>();

    public String getLEGACY_ACCOUNT_NO() {
        return LEGACY_ACCOUNT_NO;
    }

    public void setLEGACY_ACCOUNT_NO(String LEGACY_ACCOUNT_NO) {
        this.LEGACY_ACCOUNT_NO = LEGACY_ACCOUNT_NO;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getADDRESS1() {
        return ADDRESS1;
    }

    public void setADDRESS1(String ADDRESS1) {
        this.ADDRESS1 = ADDRESS1;
    }

    public String getADDRESS2() {
        return ADDRESS2;
    }

    public void setADDRESS2(String ADDRESS2) {
        this.ADDRESS2 = ADDRESS2;
    }

    public String getSEQ() {
        return SEQ;
    }

    public void setSEQ(String SEQ) {
        this.SEQ = SEQ;
    }

    public String getSUB_SEQ() {
        return SUB_SEQ;
    }

    public void setSUB_SEQ(String SUB_SEQ) {
        this.SUB_SEQ = SUB_SEQ;
    }

    public String getLEGACY_ACCOUNT_NO2() {
        return LEGACY_ACCOUNT_NO2;
    }

    public void setLEGACY_ACCOUNT_NO2(String LEGACY_ACCOUNT_NO2) {
        this.LEGACY_ACCOUNT_NO2 = LEGACY_ACCOUNT_NO2;
    }

    public String getRATE_CATEGORY() {
        return RATE_CATEGORY;
    }

    public void setRATE_CATEGORY(String RATE_CATEGORY) {
        this.RATE_CATEGORY = RATE_CATEGORY;
    }

    public String getDIV() {
        return DIV;
    }

    public void setDIV(String DIV) {
        this.DIV = DIV;
    }

    public String getSUB_DIV() {
        return SUB_DIV;
    }

    public void setSUB_DIV(String SUB_DIV) {
        this.SUB_DIV = SUB_DIV;
    }

    public String getSECTION() {
        return SECTION;
    }

    public void setSECTION(String SECTION) {
        this.SECTION = SECTION;
    }

    public String getCONSUMER_OWNED() {
        return CONSUMER_OWNED;
    }

    public void setCONSUMER_OWNED(String CONSUMER_OWNED) {
        this.CONSUMER_OWNED = CONSUMER_OWNED;
    }

    public String getMETER_MAKE() {
        return METER_MAKE;
    }

    public void setMETER_MAKE(String METER_MAKE) {
        this.METER_MAKE = METER_MAKE;
    }

    public String getUSAGE() {
        return USAGE;
    }

    public void setUSAGE(String USAGE) {
        this.USAGE = USAGE;
    }

    public String getSAN_LOAD() {
        return SAN_LOAD;
    }

    public void setSAN_LOAD(String SAN_LOAD) {
        this.SAN_LOAD = SAN_LOAD;
    }

    public String getMOVE_IN_DATE() {
        return MOVE_IN_DATE;
    }

    public void setMOVE_IN_DATE(String MOVE_IN_DATE) {
        this.MOVE_IN_DATE = MOVE_IN_DATE;
    }

    public String getDPS() {
        return DPS;
    }

    public void setDPS(String DPS) {
        this.DPS = DPS;
    }

    public String getMISC_CHARGES() {
        return MISC_CHARGES;
    }

    public void setMISC_CHARGES(String MISC_CHARGES) {
        this.MISC_CHARGES = MISC_CHARGES;
    }

    public String getCR_ADJ() {
        return CR_ADJ;
    }

    public void setCR_ADJ(String CR_ADJ) {
        this.CR_ADJ = CR_ADJ;
    }

    public String getDB_ADJ() {
        return DB_ADJ;
    }

    public void setDB_ADJ(String DB_ADJ) {
        this.DB_ADJ = DB_ADJ;
    }

    public String getPRV_BILLED_AMT() {
        return PRV_BILLED_AMT;
    }

    public void setPRV_BILLED_AMT(String PRV_BILLED_AMT) {
        this.PRV_BILLED_AMT = PRV_BILLED_AMT;
    }

    public String getPREVIOUS_BILLED_PROV_UNIT() {
        return PREVIOUS_BILLED_PROV_UNIT;
    }

    public void setPREVIOUS_BILLED_PROV_UNIT(String PREVIOUS_BILLED_PROV_UNIT) {
        this.PREVIOUS_BILLED_PROV_UNIT = PREVIOUS_BILLED_PROV_UNIT;
    }

    public String getLAST_PAID_DATE() {
        return LAST_PAID_DATE;
    }

    public void setLAST_PAID_DATE(String LAST_PAID_DATE) {
        this.LAST_PAID_DATE = LAST_PAID_DATE;
    }

    public String getLAST_PYMT_RCPT() {
        return LAST_PYMT_RCPT;
    }

    public void setLAST_PYMT_RCPT(String LAST_PYMT_RCPT) {
        this.LAST_PYMT_RCPT = LAST_PYMT_RCPT;
    }

    public String getLAST_PAID_AMT() {
        return LAST_PAID_AMT;
    }

    public void setLAST_PAID_AMT(String LAST_PAID_AMT) {
        this.LAST_PAID_AMT = LAST_PAID_AMT;
    }

    public String getED_EXEMPT() {
        return ED_EXEMPT;
    }

    public void setED_EXEMPT(String ED_EXEMPT) {
        this.ED_EXEMPT = ED_EXEMPT;
    }

    public String getAIFI() {
        return AIFI;
    }

    public void setAIFI(String AIFI) {
        this.AIFI = AIFI;
    }

    public String getNEW_METER_NO() {
        return NEW_METER_NO;
    }

    public void setNEW_METER_NO(String NEW_METER_NO) {
        this.NEW_METER_NO = NEW_METER_NO;
    }

    public String getSDI() {
        return SDI;
    }

    public void setSDI(String SDI) {
        this.SDI = SDI;
    }

    public String getASD() {
        return ASD;
    }

    public void setASD(String ASD) {
        this.ASD = ASD;
    }

    public String getASDAA() {
        return ASDAA;
    }

    public void setASDAA(String ASDAA) {
        this.ASDAA = ASDAA;
    }

    public String getINSTALLATION() {
        return INSTALLATION;
    }

    public void setINSTALLATION(String INSTALLATION) {
        this.INSTALLATION = INSTALLATION;
    }

    public String getSBM_BILL_NO() {
        return SBM_BILL_NO;
    }

    public void setSBM_BILL_NO(String SBM_BILL_NO) {
        this.SBM_BILL_NO = SBM_BILL_NO;
    }

    public String getCA() {
        return CA;
    }

    public void setCA(String CA) {
        this.CA = CA;
    }

    public String getPRV_ARR() {
        return PRV_ARR;
    }

    public void setPRV_ARR(String PRV_ARR) {
        this.PRV_ARR = PRV_ARR;
    }

    public String getARREARS() {
        return ARREARS;
    }

    public void setARREARS(String ARREARS) {
        this.ARREARS = ARREARS;
    }

    public String getULF() {
        return ULF;
    }

    public void setULF(String ULF) {
        this.ULF = ULF;
    }

    public String getPREV_BILL_UNITS() {
        return PREV_BILL_UNITS;
    }

    public void setPREV_BILL_UNITS(String PREV_BILL_UNITS) {
        this.PREV_BILL_UNITS = PREV_BILL_UNITS;
    }

    public String getBILL_MONTH() {
        return BILL_MONTH;
    }

    public void setBILL_MONTH(String BILL_MONTH) {
        this.BILL_MONTH = BILL_MONTH;
    }

    public String getECS_LIMT() {
        return ECS_LIMT;
    }

    public void setECS_LIMT(String ECS_LIMT) {
        this.ECS_LIMT = ECS_LIMT;
    }

    public String getECS_VALIDITY_PERIOD() {
        return ECS_VALIDITY_PERIOD;
    }

    public void setECS_VALIDITY_PERIOD(String ECS_VALIDITY_PERIOD) {
        this.ECS_VALIDITY_PERIOD = ECS_VALIDITY_PERIOD;
    }

    public String getPRESENT_READING_REMARK() {
        return PRESENT_READING_REMARK;
    }

    public void setPRESENT_READING_REMARK(String PRESENT_READING_REMARK) {
        this.PRESENT_READING_REMARK = PRESENT_READING_REMARK;
    }

    public String getPRESENT_METER_STATUS() {
        return PRESENT_METER_STATUS;
    }

    public void setPRESENT_METER_STATUS(String PRESENT_METER_STATUS) {
        this.PRESENT_METER_STATUS = PRESENT_METER_STATUS;
    }

    public String getPRESENT_BILL_UNITS() {
        return PRESENT_BILL_UNITS;
    }

    public void setPRESENT_BILL_UNITS(String PRESENT_BILL_UNITS) {
        this.PRESENT_BILL_UNITS = PRESENT_BILL_UNITS;
    }

    public String getPRESENT_BILL_TYPE() {
        return PRESENT_BILL_TYPE;
    }

    public void setPRESENT_BILL_TYPE(String PRESENT_BILL_TYPE) {
        this.PRESENT_BILL_TYPE = PRESENT_BILL_TYPE;
    }

    public String getEC() {
        return EC;
    }

    public void setEC(String EC) {
        this.EC = EC;
    }

    public String getMMFC() {
        return MMFC;
    }

    public void setMMFC(String MMFC) {
        this.MMFC = MMFC;
    }

    public String getMRENT_CHARGED() {
        return MRENT_CHARGED;
    }

    public void setMRENT_CHARGED(String MRENT_CHARGED) {
        this.MRENT_CHARGED = MRENT_CHARGED;
    }

    public String getED() {
        return ED;
    }

    public void setED(String ED) {
        this.ED = ED;
    }

    public String getCURRENT_BILL_TOTAL() {
        return CURRENT_BILL_TOTAL;
    }

    public void setCURRENT_BILL_TOTAL(String CURRENT_BILL_TOTAL) {
        this.CURRENT_BILL_TOTAL = CURRENT_BILL_TOTAL;
    }

    public String getREBATE() {
        return REBATE;
    }

    public void setREBATE(String REBATE) {
        this.REBATE = REBATE;
    }

    public String getAMOUNT_PAYABLE() {
        return AMOUNT_PAYABLE;
    }

    public void setAMOUNT_PAYABLE(String AMOUNT_PAYABLE) {
        this.AMOUNT_PAYABLE = AMOUNT_PAYABLE;
    }

    public String getAVG_UNIT_BILLED() {
        return AVG_UNIT_BILLED;
    }

    public void setAVG_UNIT_BILLED(String AVG_UNIT_BILLED) {
        this.AVG_UNIT_BILLED = AVG_UNIT_BILLED;
    }

    public String getRCPTNO() {
        return RCPTNO;
    }

    public void setRCPTNO(String RCPTNO) {
        this.RCPTNO = RCPTNO;
    }

    public String getCHQNO() {
        return CHQNO;
    }

    public void setCHQNO(String CHQNO) {
        this.CHQNO = CHQNO;
    }

    public String getCHQDT() {
        return CHQDT;
    }

    public void setCHQDT(String CHQDT) {
        this.CHQDT = CHQDT;
    }

    public String getBANK() {
        return BANK;
    }

    public void setBANK(String BANK) {
        this.BANK = BANK;
    }

    public String getRCPTAMT() {
        return RCPTAMT;
    }

    public void setRCPTAMT(String RCPTAMT) {
        this.RCPTAMT = RCPTAMT;
    }

    public String getDUE_DATE() {
        return DUE_DATE;
    }

    public void setDUE_DATE(String DUE_DATE) {
        this.DUE_DATE = DUE_DATE;
    }

    public String getDO_EXPIRY() {
        return DO_EXPIRY;
    }

    public void setDO_EXPIRY(String DO_EXPIRY) {
        this.DO_EXPIRY = DO_EXPIRY;
    }

    public String getPRESENT_READING_TIME() {
        return PRESENT_READING_TIME;
    }

    public void setPRESENT_READING_TIME(String PRESENT_READING_TIME) {
        this.PRESENT_READING_TIME = PRESENT_READING_TIME;
    }

    public String getOSBILL_DATE() {
        return OSBILL_DATE;
    }

    public void setOSBILL_DATE(String OSBILL_DATE) {
        this.OSBILL_DATE = OSBILL_DATE;
    }

    public String getCAPTURED_MOBILE() {
        return CAPTURED_MOBILE;
    }

    public void setCAPTURED_MOBILE(String CAPTURED_MOBILE) {
        this.CAPTURED_MOBILE = CAPTURED_MOBILE;
    }

    public String getSCHEDULE_METER_READ_DATE() {
        return SCHEDULE_METER_READ_DATE;
    }

    public void setSCHEDULE_METER_READ_DATE(String SCHEDULE_METER_READ_DATE) {
        this.SCHEDULE_METER_READ_DATE = SCHEDULE_METER_READ_DATE;
    }

    public String getMETER_RENT() {
        return METER_RENT;
    }

    public void setMETER_RENT(String METER_RENT) {
        this.METER_RENT = METER_RENT;
    }

    public String getPORTION() {
        return PORTION;
    }

    public void setPORTION(String PORTION) {
        this.PORTION = PORTION;
    }

    public String getMRU() {
        return MRU;
    }

    public void setMRU(String MRU) {
        this.MRU = MRU;
    }

    public String getNO_OF_REG() {
        return NO_OF_REG;
    }

    public void setNO_OF_REG(String NO_OF_REG) {
        this.NO_OF_REG = NO_OF_REG;
    }

    public String getSAN_LOAD_UNITS() {
        return SAN_LOAD_UNITS;
    }

    public void setSAN_LOAD_UNITS(String SAN_LOAD_UNITS) {
        this.SAN_LOAD_UNITS = SAN_LOAD_UNITS;
    }

    public String getSAN_LOAD_EFFECTIVE_DATE() {
        return SAN_LOAD_EFFECTIVE_DATE;
    }

    public void setSAN_LOAD_EFFECTIVE_DATE(String SAN_LOAD_EFFECTIVE_DATE) {
        this.SAN_LOAD_EFFECTIVE_DATE = SAN_LOAD_EFFECTIVE_DATE;
    }

    public String getSUPPLY_TYP_FLG() {
        return SUPPLY_TYP_FLG;
    }

    public void setSUPPLY_TYP_FLG(String SUPPLY_TYP_FLG) {
        this.SUPPLY_TYP_FLG = SUPPLY_TYP_FLG;
    }

    public String getNOTINUSE_FLG_ENDDATE() {
        return NOTINUSE_FLG_ENDDATE;
    }

    public void setNOTINUSE_FLG_ENDDATE(String NOTINUSE_FLG_ENDDATE) {
        this.NOTINUSE_FLG_ENDDATE = NOTINUSE_FLG_ENDDATE;
    }

    public String getOTHER_FLGS() {
        return OTHER_FLGS;
    }

    public void setOTHER_FLGS(String OTHER_FLGS) {
        this.OTHER_FLGS = OTHER_FLGS;
    }

    public String getED_RBT() {
        return ED_RBT;
    }

    public void setED_RBT(String ED_RBT) {
        this.ED_RBT = ED_RBT;
    }

    public String getHOSTEL_RBT() {
        return HOSTEL_RBT;
    }

    public void setHOSTEL_RBT(String HOSTEL_RBT) {
        this.HOSTEL_RBT = HOSTEL_RBT;
    }

    public String getPREV_BILL_TYPE() {
        return PREV_BILL_TYPE;
    }

    public void setPREV_BILL_TYPE(String PREV_BILL_TYPE) {
        this.PREV_BILL_TYPE = PREV_BILL_TYPE;
    }

    public String getPREV_BILL_REMARK() {
        return PREV_BILL_REMARK;
    }

    public void setPREV_BILL_REMARK(String PREV_BILL_REMARK) {
        this.PREV_BILL_REMARK = PREV_BILL_REMARK;
    }

    public String getPREV_BILL_END_DATE() {
        return PREV_BILL_END_DATE;
    }

    public void setPREV_BILL_END_DATE(String PREV_BILL_END_DATE) {
        this.PREV_BILL_END_DATE = PREV_BILL_END_DATE;
    }

    public String getLAST_NORMAL_BILL_DATE() {
        return LAST_NORMAL_BILL_DATE;
    }

    public void setLAST_NORMAL_BILL_DATE(String LAST_NORMAL_BILL_DATE) {
        this.LAST_NORMAL_BILL_DATE = LAST_NORMAL_BILL_DATE;
    }

    public String getPRESENT_READING_DATE() {
        return PRESENT_READING_DATE;
    }

    public void setPRESENT_READING_DATE(String PRESENT_READING_DATE) {
        this.PRESENT_READING_DATE = PRESENT_READING_DATE;
    }

    public String getAVERAGE_KWH() {
        return AVERAGE_KWH;
    }

    public void setAVERAGE_KWH(String AVERAGE_KWH) {
        this.AVERAGE_KWH = AVERAGE_KWH;
    }

    public String getBILL_BASIS() {
        return BILL_BASIS;
    }

    public void setBILL_BASIS(String BILL_BASIS) {
        this.BILL_BASIS = BILL_BASIS;
    }

    public String getBILL_NO() {
        return BILL_NO;
    }

    public void setBILL_NO(String BILL_NO) {
        this.BILL_NO = BILL_NO;
    }

    public String getINVOICE_NO() {
        return INVOICE_NO;
    }

    public void setINVOICE_NO(String INVOICE_NO) {
        this.INVOICE_NO = INVOICE_NO;
    }

    public String getREAD_ONLY() {
        return READ_ONLY;
    }

    public void setREAD_ONLY(String READ_ONLY) {
        this.READ_ONLY = READ_ONLY;
    }

    public String getBP_START_DATE() {
        return BP_START_DATE;
    }

    public void setBP_START_DATE(String BP_START_DATE) {
        this.BP_START_DATE = BP_START_DATE;
    }

    public String getBP_END_DATE() {
        return BP_END_DATE;
    }

    public void setBP_END_DATE(String BP_END_DATE) {
        this.BP_END_DATE = BP_END_DATE;
    }

    public String getPPAC() {
        return PPAC;
    }

    public void setPPAC(String PPAC) {
        this.PPAC = PPAC;
    }

    public String getROUND_OFF() {
        return ROUND_OFF;
    }

    public void setROUND_OFF(String ROUND_OFF) {
        this.ROUND_OFF = ROUND_OFF;
    }

    public String getSEC_DEPOSIT_AMT() {
        return SEC_DEPOSIT_AMT;
    }

    public void setSEC_DEPOSIT_AMT(String SEC_DEPOSIT_AMT) {
        this.SEC_DEPOSIT_AMT = SEC_DEPOSIT_AMT;
    }

    public String getDO_GENERATED() {
        return DO_GENERATED;
    }

    public void setDO_GENERATED(String DO_GENERATED) {
        this.DO_GENERATED = DO_GENERATED;
    }

    public String getNOT_TO_BILL_AFTER() {
        return NOT_TO_BILL_AFTER;
    }

    public void setNOT_TO_BILL_AFTER(String NOT_TO_BILL_AFTER) {
        this.NOT_TO_BILL_AFTER = NOT_TO_BILL_AFTER;
    }

    public String getINSERT_DATE() {
        return INSERT_DATE;
    }

    public void setINSERT_DATE(String INSERT_DATE) {
        this.INSERT_DATE = INSERT_DATE;
    }

    public String getINSERT_TIME() {
        return INSERT_TIME;
    }

    public void setINSERT_TIME(String INSERT_TIME) {
        this.INSERT_TIME = INSERT_TIME;
    }

    public String getUPDATE_DATE() {
        return UPDATE_DATE;
    }

    public void setUPDATE_DATE(String UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public String getUPDATE_TIME() {
        return UPDATE_TIME;
    }

    public void setUPDATE_TIME(String UPDATE_TIME) {
        this.UPDATE_TIME = UPDATE_TIME;
    }

    public String getPROGRESSION_STATE() {
        return PROGRESSION_STATE;
    }

    public void setPROGRESSION_STATE(String PROGRESSION_STATE) {
        this.PROGRESSION_STATE = PROGRESSION_STATE;
    }

    public String getPHONE_1() {
        return PHONE_1;
    }

    public void setPHONE_1(String PHONE_1) {
        this.PHONE_1 = PHONE_1;
    }

    public String getPHONE_2() {
        return PHONE_2;
    }

    public void setPHONE_2(String PHONE_2) {
        this.PHONE_2 = PHONE_2;
    }

    public String getBILL_YEAR() {
        return BILL_YEAR;
    }

    public void setBILL_YEAR(String BILL_YEAR) {
        this.BILL_YEAR = BILL_YEAR;
    }

    public String getTRANS_TYPE() {
        return TRANS_TYPE;
    }

    public void setTRANS_TYPE(String TRANS_TYPE) {
        this.TRANS_TYPE = TRANS_TYPE;
    }

    public String getTD_FLAG() {
        return TD_FLAG;
    }

    public void setTD_FLAG(String TD_FLAG) {
        this.TD_FLAG = TD_FLAG;
    }

    public String getTD_DATE() {
        return TD_DATE;
    }

    public void setTD_DATE(String TD_DATE) {
        this.TD_DATE = TD_DATE;
    }

    public String getPPI() {
        return PPI;
    }

    public void setPPI(String PPI) {
        this.PPI = PPI;
    }

    public String getPREV_PROV_AMT() {
        return PREV_PROV_AMT;
    }

    public void setPREV_PROV_AMT(String PREV_PROV_AMT) {
        this.PREV_PROV_AMT = PREV_PROV_AMT;
    }

    public String getGST_RELEVANT1() {
        return GST_RELEVANT1;
    }

    public void setGST_RELEVANT1(String GST_RELEVANT1) {
        this.GST_RELEVANT1 = GST_RELEVANT1;
    }

    public String getGST_RELEVANT2() {
        return GST_RELEVANT2;
    }

    public void setGST_RELEVANT2(String GST_RELEVANT2) {
        this.GST_RELEVANT2 = GST_RELEVANT2;
    }

    public String getGST_RELEVANT3() {
        return GST_RELEVANT3;
    }

    public void setGST_RELEVANT3(String GST_RELEVANT3) {
        this.GST_RELEVANT3 = GST_RELEVANT3;
    }

    public String getGST_RELEVANT4() {
        return GST_RELEVANT4;
    }

    public void setGST_RELEVANT4(String GST_RELEVANT4) {
        this.GST_RELEVANT4 = GST_RELEVANT4;
    }

    public String getELT_STTS() {
        return ELT_STTS;
    }

    public void setELT_STTS(String ELT_STTS) {
        this.ELT_STTS = ELT_STTS;
    }

    public String getSEAL_STTS() {
        return SEAL_STTS;
    }

    public void setSEAL_STTS(String SEAL_STTS) {
        this.SEAL_STTS = SEAL_STTS;
    }

    public String getCBLASS() {
        return CBLASS;
    }

    public void setCBLASS(String CBLASS) {
        this.CBLASS = CBLASS;
    }

    public String getHL_MONTHS() {
        return HL_MONTHS;
    }

    public void setHL_MONTHS(String HL_MONTHS) {
        this.HL_MONTHS = HL_MONTHS;
    }

    public String getMRREASON() {
        return MRREASON;
    }

    public void setMRREASON(String MRREASON) {
        this.MRREASON = MRREASON;
    }

    public String getUSAGE_ID() {
        return USAGE_ID;
    }

    public void setUSAGE_ID(String USAGE_ID) {
        this.USAGE_ID = USAGE_ID;
    }

    public String getMETER_TYPE_ID() {
        return METER_TYPE_ID;
    }

    public void setMETER_TYPE_ID(String METER_TYPE_ID) {
        this.METER_TYPE_ID = METER_TYPE_ID;
    }

    public String getSUPPLY_STATUS_ID() {
        return SUPPLY_STATUS_ID;
    }

    public void setSUPPLY_STATUS_ID(String SUPPLY_STATUS_ID) {
        this.SUPPLY_STATUS_ID = SUPPLY_STATUS_ID;
    }

    public String getREASON_DC_ID() {
        return REASON_DC_ID;
    }

    public void setREASON_DC_ID(String REASON_DC_ID) {
        this.REASON_DC_ID = REASON_DC_ID;
    }

    public String getSUPPLY_SOURCE_ID() {
        return SUPPLY_SOURCE_ID;
    }

    public void setSUPPLY_SOURCE_ID(String SUPPLY_SOURCE_ID) {
        this.SUPPLY_SOURCE_ID = SUPPLY_SOURCE_ID;
    }

    public String getREASON_NV_ID() {
        return REASON_NV_ID;
    }

    public void setREASON_NV_ID(String REASON_NV_ID) {
        this.REASON_NV_ID = REASON_NV_ID;
    }

    public String getREASON_CD_ID() {
        return REASON_CD_ID;
    }

    public void setREASON_CD_ID(String REASON_CD_ID) {
        this.REASON_CD_ID = REASON_CD_ID;
    }

    public String getREASON_EN_ID() {
        return REASON_EN_ID;
    }

    public void setREASON_EN_ID(String REASON_EN_ID) {
        this.REASON_EN_ID = REASON_EN_ID;
    }

    public String getREASON_MTR_STUCK_ID() {
        return REASON_MTR_STUCK_ID;
    }

    public void setREASON_MTR_STUCK_ID(String REASON_MTR_STUCK_ID) {
        this.REASON_MTR_STUCK_ID = REASON_MTR_STUCK_ID;
    }

    public String getPAPER_PASTE_BY_ID() {
        return PAPER_PASTE_BY_ID;
    }

    public void setPAPER_PASTE_BY_ID(String PAPER_PASTE_BY_ID) {
        this.PAPER_PASTE_BY_ID = PAPER_PASTE_BY_ID;
    }

    public String getMETER_HEIGHT_ID() {
        return METER_HEIGHT_ID;
    }

    public void setMETER_HEIGHT_ID(String METER_HEIGHT_ID) {
        this.METER_HEIGHT_ID = METER_HEIGHT_ID;
    }

    public String getTYPES_OBSTACLE_ID() {
        return TYPES_OBSTACLE_ID;
    }

    public void setTYPES_OBSTACLE_ID(String TYPES_OBSTACLE_ID) {
        this.TYPES_OBSTACLE_ID = TYPES_OBSTACLE_ID;
    }

    public String getSEAL_STS_ID() {
        return SEAL_STS_ID;
    }

    public void setSEAL_STS_ID(String SEAL_STS_ID) {
        this.SEAL_STS_ID = SEAL_STS_ID;
    }

    public String getCONTACT_REASON_ID() {
        return CONTACT_REASON_ID;
    }

    public void setCONTACT_REASON_ID(String CONTACT_REASON_ID) {
        this.CONTACT_REASON_ID = CONTACT_REASON_ID;
    }

    public String getREASON_PL_ID() {
        return REASON_PL_ID;
    }

    public void setREASON_PL_ID(String REASON_PL_ID) {
        this.REASON_PL_ID = REASON_PL_ID;
    }

    public String getBUILDING_DESC() {
        return BUILDING_DESC;
    }

    public void setBUILDING_DESC(String BUILDING_DESC) {
        this.BUILDING_DESC = BUILDING_DESC;
    }

    public String getBUILDING_CODE() {
        return BUILDING_CODE;
    }

    public void setBUILDING_CODE(String BUILDING_CODE) {
        this.BUILDING_CODE = BUILDING_CODE;
    }

    public String getPOLE_NO() {
        return POLE_NO;
    }

    public void setPOLE_NO(String POLE_NO) {
        this.POLE_NO = POLE_NO;
    }

    public String getFLAG() {
        return FLAG;
    }

    public void setFLAG(String FLAG) {
        this.FLAG = FLAG;
    }

    public String getSPECIAL_REM() {
        return SPECIAL_REM;
    }

    public void setSPECIAL_REM(String SPECIAL_REM) {
        this.SPECIAL_REM = SPECIAL_REM;
    }

    public String getGPS_LONGITUDE() {
        return GPS_LONGITUDE;
    }

    public void setGPS_LONGITUDE(String GPS_LONGITUDE) {
        this.GPS_LONGITUDE = GPS_LONGITUDE;
    }

    public String getGPS_LATITUDE() {
        return GPS_LATITUDE;
    }

    public void setGPS_LATITUDE(String GPS_LATITUDE) {
        this.GPS_LATITUDE = GPS_LATITUDE;
    }

    public String getSENDER_MOBILE() {
        return SENDER_MOBILE;
    }

    public void setSENDER_MOBILE(String SENDER_MOBILE) {
        this.SENDER_MOBILE = SENDER_MOBILE;
    }

    public String getCONSUMER_TYPE() {
        return CONSUMER_TYPE;
    }

    public void setCONSUMER_TYPE(String CONSUMER_TYPE) {
        this.CONSUMER_TYPE = CONSUMER_TYPE;
    }

    public String getWALKING_SEQ_CHK() {
        return WALKING_SEQ_CHK;
    }

    public void setWALKING_SEQ_CHK(String WALKING_SEQ_CHK) {
        this.WALKING_SEQ_CHK = WALKING_SEQ_CHK;
    }

    public String getMETER_LOCA() {
        return METER_LOCA;
    }

    public void setMETER_LOCA(String METER_LOCA) {
        this.METER_LOCA = METER_LOCA;
    }

    public String getMR_REMARK_DET() {
        return MR_REMARK_DET;
    }

    public void setMR_REMARK_DET(String MR_REMARK_DET) {
        this.MR_REMARK_DET = MR_REMARK_DET;
    }

    public String getSTOP_PAPR_BL() {
        return STOP_PAPR_BL;
    }

    public void setSTOP_PAPR_BL(String STOP_PAPR_BL) {
        this.STOP_PAPR_BL = STOP_PAPR_BL;
    }

    public String getNEW_MTR_FLG() {
        return NEW_MTR_FLG;
    }

    public void setNEW_MTR_FLG(String NEW_MTR_FLG) {
        this.NEW_MTR_FLG = NEW_MTR_FLG;
    }

    public String getOLD_MTR_COR_FLG() {
        return OLD_MTR_COR_FLG;
    }

    public void setOLD_MTR_COR_FLG(String OLD_MTR_COR_FLG) {
        this.OLD_MTR_COR_FLG = OLD_MTR_COR_FLG;
    }

    public String getUNSAFE_COND() {
        return UNSAFE_COND;
    }

    public void setUNSAFE_COND(String UNSAFE_COND) {
        this.UNSAFE_COND = UNSAFE_COND;
    }

    public String getREAD_FLAG() {
        return READ_FLAG;
    }

    public void setREAD_FLAG(String READ_FLAG) {
        this.READ_FLAG = READ_FLAG;
    }

    public String getSENT_FLAG() {
        return SENT_FLAG;
    }

    public void setSENT_FLAG(String SENT_FLAG) {
        this.SENT_FLAG = SENT_FLAG;
    }

    public String getMTR_IMAGE() {
        return MTR_IMAGE;
    }

    public void setMTR_IMAGE(String MTR_IMAGE) {
        this.MTR_IMAGE = MTR_IMAGE;
    }

    public String getUPD_FLAG1() {
        return UPD_FLAG1;
    }

    public void setUPD_FLAG1(String UPD_FLAG1) {
        this.UPD_FLAG1 = UPD_FLAG1;
    }

    public String getUPD_FLAG2() {
        return UPD_FLAG2;
    }

    public void setUPD_FLAG2(String UPD_FLAG2) {
        this.UPD_FLAG2 = UPD_FLAG2;
    }

    public String getABLBELNR() {
        return ABLBELNR;
    }

    public void setABLBELNR(String ABLBELNR) {
        this.ABLBELNR = ABLBELNR;
    }

    public String getMETER_NO() {
        return METER_NO;
    }

    public void setMETER_NO(String METER_NO) {
        this.METER_NO = METER_NO;
    }

    public String getMETER_TYP() {
        return METER_TYP;
    }

    public void setMETER_TYP(String METER_TYP) {
        this.METER_TYP = METER_TYP;
    }

    public String getMF() {
        return MF;
    }

    public void setMF(String MF) {
        this.MF = MF;
    }

    public String getNO_OF_DIGITS() {
        return NO_OF_DIGITS;
    }

    public void setNO_OF_DIGITS(String NO_OF_DIGITS) {
        this.NO_OF_DIGITS = NO_OF_DIGITS;
    }

    public String getPREV_MTR_READ() {
        return PREV_MTR_READ;
    }

    public void setPREV_MTR_READ(String PREV_MTR_READ) {
        this.PREV_MTR_READ = PREV_MTR_READ;
    }

    public String getPREV_READ_DATE() {
        return PREV_READ_DATE;
    }

    public void setPREV_READ_DATE(String PREV_READ_DATE) {
        this.PREV_READ_DATE = PREV_READ_DATE;
    }

    public String getMETER_CONDITION() {
        return METER_CONDITION;
    }

    public void setMETER_CONDITION(String METER_CONDITION) {
        this.METER_CONDITION = METER_CONDITION;
    }

    public String getLAST_OK_RDNG() {
        return LAST_OK_RDNG;
    }

    public void setLAST_OK_RDNG(String LAST_OK_RDNG) {
        this.LAST_OK_RDNG = LAST_OK_RDNG;
    }

    public String getMETER_INSTALL_DATE() {
        return METER_INSTALL_DATE;
    }

    public void setMETER_INSTALL_DATE(String METER_INSTALL_DATE) {
        this.METER_INSTALL_DATE = METER_INSTALL_DATE;
    }

    public String getPRESENT_METER_READING() {
        return PRESENT_METER_READING;
    }

    public void setPRESENT_METER_READING(String PRESENT_METER_READING) {
        this.PRESENT_METER_READING = PRESENT_METER_READING;
    }

    public String getEQUIPMENT_NO() {
        return EQUIPMENT_NO;
    }

    public void setEQUIPMENT_NO(String EQUIPMENT_NO) {
        this.EQUIPMENT_NO = EQUIPMENT_NO;
    }

    public String getNEW_METER_FLG() {
        return NEW_METER_FLG;
    }

    public void setNEW_METER_FLG(String NEW_METER_FLG) {
        this.NEW_METER_FLG = NEW_METER_FLG;
    }

    public String getREGISTER_CODE() {
        return REGISTER_CODE;
    }

    public void setREGISTER_CODE(String REGISTER_CODE) {
        this.REGISTER_CODE = REGISTER_CODE;
    }

    public String getMETER_REMOVED_ON() {
        return METER_REMOVED_ON;
    }

    public void setMETER_REMOVED_ON(String METER_REMOVED_ON) {
        this.METER_REMOVED_ON = METER_REMOVED_ON;
    }

    public String getCONSUMPTION_OLD_METER() {
        return CONSUMPTION_OLD_METER;
    }

    public void setCONSUMPTION_OLD_METER(String CONSUMPTION_OLD_METER) {
        this.CONSUMPTION_OLD_METER = CONSUMPTION_OLD_METER;
    }

    public String getPREVIOUS_MD() {
        return PREVIOUS_MD;
    }

    public void setPREVIOUS_MD(String PREVIOUS_MD) {
        this.PREVIOUS_MD = PREVIOUS_MD;
    }

    public String getBILLED_MD() {
        return BILLED_MD;
    }

    public void setBILLED_MD(String BILLED_MD) {
        this.BILLED_MD = BILLED_MD;
    }

    public String getPRS_MD() {
        return PRS_MD;
    }

    public void setPRS_MD(String PRS_MD) {
        this.PRS_MD = PRS_MD;
    }

    public String getREVISIT_FLAG() {
        return REVISIT_FLAG;
    }

    public void setREVISIT_FLAG(String REVISIT_FLAG) {
        this.REVISIT_FLAG = REVISIT_FLAG;
    }

    public List<String> getListRegisterCoe() {
        return listRegisterCoe;
    }

    public void setListRegisterCoe(List<String> listRegisterCoe) {
        this.listRegisterCoe = listRegisterCoe;
    }

    public List<String> getListMeterNo() {
        return listMeterNo;
    }

    public void setListMeterNo(List<String> listMeterNo) {
        this.listMeterNo = listMeterNo;
    }

    public List<String> getListRegisterValue() {
        return listRegisterValue;
    }

    public void setListRegisterValue(List<String> listRegisterValue) {
        this.listRegisterValue = listRegisterValue;
    }

    public Map<String, String> getLinkedHashMapValues() {
        return LinkedHashMapValues;
    }

    public void setLinkedHashMapValues(Map<String, String> linkedHashMapValues) {
        LinkedHashMapValues = linkedHashMapValues;
    }

    public String getREF_MR_DATE() {
        return REF_MR_DATE;
    }

    public void setREF_MR_DATE(String REF_MR_DATE) {
        this.REF_MR_DATE = REF_MR_DATE;
    }
}
