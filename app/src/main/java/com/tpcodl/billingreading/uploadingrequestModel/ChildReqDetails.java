package com.tpcodl.billingreading.uploadingrequestModel;

public class ChildReqDetails {
    public String ablbelnr;

    public String billMonth;

    public String billYear;

    public String billedMd;

    public String calMonCnt;

    public String consumptionOldMeter;

    public String equipmentNo;

    public String installation;

    public String lastOkRdng;

    public String meterCondition;

    public String meterInstallDate;

    public String meterNo;

    public String meterRemovedOn;

    public String meterTyp;

    public String mf;

    public String mrreason;

    public String newMeterFlg;

    public String noOfDigits;

    public String noOfReg;
    public String presentMeterReading;

    public String prevMtrRead;

    public String prevReadDate;

    public String previousMd;

    public String prsMd;

    public String referenceDate;

    public String registerCode;

    public String scheduleMeterReadDate;

    public String userType;

    public String transType;

    public String getAblbelnr() {
        return ablbelnr;
    }

    public void setAblbelnr(String ablbelnr) {
        this.ablbelnr = ablbelnr;
    }

    public void setBillMonth(String billMonth) {
        this.billMonth = billMonth;
    }

    public void setBillYear(String billYear) {
        this.billYear = billYear;
    }

    public String getBilledMd() {
        return billedMd;
    }

    public void setBilledMd(String billedMd) {
        this.billedMd = billedMd;
    }

    public String getCalMonCnt() {
        return calMonCnt;
    }

    public void setCalMonCnt(String calMonCnt) {
        this.calMonCnt = calMonCnt;
    }

    public String getConsumptionOldMeter() {
        return consumptionOldMeter;
    }

    public void setConsumptionOldMeter(String consumptionOldMeter) {
        this.consumptionOldMeter = consumptionOldMeter;
    }

    public String getEquipmentNo() {
        return equipmentNo;
    }

    public void setEquipmentNo(String equipmentNo) {
        this.equipmentNo = equipmentNo;
    }

    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public String getLastOkRdng() {
        return lastOkRdng;
    }

    public void setLastOkRdng(String lastOkRdng) {
        this.lastOkRdng = lastOkRdng;
    }

    public String getMeterCondition() {
        return meterCondition;
    }

    public void setMeterCondition(String meterCondition) {
        this.meterCondition = meterCondition;
    }

    public String getMeterInstallDate() {
        return meterInstallDate;
    }

    public void setMeterInstallDate(String meterInstallDate) {
        this.meterInstallDate = meterInstallDate;
    }

    public String getMeterNo() {
        return meterNo;
    }

    public void setMeterNo(String meterNo) {
        this.meterNo = meterNo;
    }

    public String getMeterRemovedOn() {
        return meterRemovedOn;
    }

    public void setMeterRemovedOn(String meterRemovedOn) {
        this.meterRemovedOn = meterRemovedOn;
    }

    public String getMeterTyp() {
        return meterTyp;
    }

    public void setMeterTyp(String meterTyp) {
        this.meterTyp = meterTyp;
    }

    public String getMf() {
        return mf;
    }

    public void setMf(String mf) {
        this.mf = mf;
    }

    public String getNewMeterFlg() {
        return newMeterFlg;
    }

    public void setNewMeterFlg(String newMeterFlg) {
        this.newMeterFlg = newMeterFlg;
    }

    public String getNoOfDigits() {
        return noOfDigits;
    }

    public void setNoOfDigits(String noOfDigits) {
        this.noOfDigits = noOfDigits;
    }

    public String getNoOfReg() {
        return noOfReg;
    }

    public void setNoOfReg(String noOfReg) {
        this.noOfReg = noOfReg;
    }

    public String getPresentMeterReading() {
        return presentMeterReading;
    }

    public void setPresentMeterReading(String presentMeterReading) {
        this.presentMeterReading = presentMeterReading;
    }

    public String getPrevMtrRead() {
        return prevMtrRead;
    }

    public void setPrevMtrRead(String prevMtrRead) {
        this.prevMtrRead = prevMtrRead;
    }

    public String getPrevReadDate() {
        return prevReadDate;
    }

    public void setPrevReadDate(String prevReadDate) {
        this.prevReadDate = prevReadDate;
    }

    public String getPreviousMd() {
        return previousMd;
    }

    public void setPreviousMd(String previousMd) {
        this.previousMd = previousMd;
    }

    public String getPrsMd() {
        return prsMd;
    }

    public void setPrsMd(String prsMd) {
        this.prsMd = prsMd;
    }

    public String getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(String referenceDate) {
        this.referenceDate = referenceDate;
    }

    public String getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(String registerCode) {
        this.registerCode = registerCode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }




    public static ChildReqDetails getInstance() {
        if (instance == null) {
            instance = new ChildReqDetails();
        }
        return instance;
    }

    public static void setInstance(ChildReqDetails instance) {
        ChildReqDetails.instance = instance;
    }

    public static ChildReqDetails instance;

    public String getMeter_no() {
        return meterNo;
    }

    public void setMeter_no(String meter_no) {
        this.meterNo = meter_no;
    }

    public String getRegister_code() {
        return registerCode;
    }

    public void setRegister_code(String register_code) {
        this.registerCode = register_code;
    }


    public String getMrreason() {
        return mrreason;
    }

    public void setMrreason(String mrreason) {
        this.mrreason = mrreason;
    }


    public String getScheduleMeterReadDate() {
        return scheduleMeterReadDate;
    }

    public void setScheduleMeterReadDate(String scheduleMeterReadDate) {
        this.scheduleMeterReadDate = scheduleMeterReadDate;
    }

    public String getBillMonth() {
        return billMonth;
    }

    public String getBillYear() {
        return billYear;
    }

    public String getInstallation() {
        return installation;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }
}
