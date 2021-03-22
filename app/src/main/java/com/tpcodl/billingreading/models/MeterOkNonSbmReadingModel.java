package com.tpcodl.billingreading.models;

import java.io.Serializable;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class MeterOkNonSbmReadingModel implements Serializable {

    String st_kwh, st_kvah, st_kvarh, st_md_peak, st_md_off_peak, st_tod, st_colony_consumption, st_currentdatetime;
    String isELTON;
    String isSeqCorrect;
    String isMeterlocation;
    String st_supplyStatus;
    String st_usages;
    String st_mtrtype;
    String st_resonNV;
    String st_additional;
    String isPaperBill;
    String isRevisit;
    String sourceOfSupply;
    String resonMeterStuck;
    String resonSealStatus;
    String resonContact;
    String st_reason_for_dc;
    String photo_meter;
    String photo_tester;
    String upload_image_NP;
    String reasonPl;
    String typeOfObstacle;

    String meterHeight;
    String unsafeCondition;
    String reasonEN;

    String meterNumber;
    String st_newMeter;
    String st_oldMeterCorrection;
    String mr_reason;
    String overriceQC;

    String gpsLatitude;
    String gpsLongitude;

    Map<String, String> LinkedHashMapValues = new LinkedHashMap<>();
    Map<String, String> LinkedHashMapImages = new LinkedHashMap<>();


    public String getOverriceQC() {
        return overriceQC;
    }

    public void setOverriceQC(String overriceQC) {
        this.overriceQC = overriceQC;
    }

    public String getSt_kwh() {
        return st_kwh;
    }

    public void setSt_kwh(String st_kwh) {
        this.st_kwh = st_kwh;
    }

    public String getSt_kvah() {
        return st_kvah;
    }

    public void setSt_kvah(String st_kvah) {
        this.st_kvah = st_kvah;
    }

    public String getSt_kvarh() {
        return st_kvarh;
    }

    public void setSt_kvarh(String st_kvarh) {
        this.st_kvarh = st_kvarh;
    }

    public String getSt_md_peak() {
        return st_md_peak;
    }

    public void setSt_md_peak(String st_md_peak) {
        this.st_md_peak = st_md_peak;
    }

    public String getSt_md_off_peak() {
        return st_md_off_peak;
    }

    public void setSt_md_off_peak(String st_md_off_peak) {
        this.st_md_off_peak = st_md_off_peak;
    }

    public String getSt_tod() {
        return st_tod;
    }

    public void setSt_tod(String st_tod) {
        this.st_tod = st_tod;
    }

    public String getSt_colony_consumption() {
        return st_colony_consumption;
    }

    public void setSt_colony_consumption(String st_colony_consumption) {
        this.st_colony_consumption = st_colony_consumption;
    }

    public String getSt_currentdatetime() {
        return st_currentdatetime;
    }

    public void setSt_currentdatetime(String st_currentdatetime) {
        this.st_currentdatetime = st_currentdatetime;
    }

    public String getIsELTON() {
        return isELTON;
    }

    public void setIsELTON(String isELTON) {
        this.isELTON = isELTON;
    }

    public String getIsSeqCorrect() {
        return isSeqCorrect;
    }

    public void setIsSeqCorrect(String isSeqCorrect) {
        this.isSeqCorrect = isSeqCorrect;
    }

    public String getIsMeterlocation() {
        return isMeterlocation;
    }

    public void setIsMeterlocation(String isMeterlocation) {
        this.isMeterlocation = isMeterlocation;
    }

    public String getSt_supplyStatus() {
        return st_supplyStatus;
    }

    public void setSt_supplyStatus(String st_supplyStatus) {
        this.st_supplyStatus = st_supplyStatus;
    }

    public String getSt_usages() {
        return st_usages;
    }

    public void setSt_usages(String st_usages) {
        this.st_usages = st_usages;
    }

    public String getSt_mtrtype() {
        return st_mtrtype;
    }

    public void setSt_mtrtype(String st_mtrtype) {
        this.st_mtrtype = st_mtrtype;
    }

    public String getSt_resonNV() {
        return st_resonNV;
    }

    public void setSt_resonNV(String st_resonNV) {
        this.st_resonNV = st_resonNV;
    }

    public String getSt_additional() {
        return st_additional;
    }

    public void setSt_additional(String st_additional) {
        this.st_additional = st_additional;
    }

    public String getIsPaperBill() {
        return isPaperBill;
    }

    public void setIsPaperBill(String isPaperBill) {
        this.isPaperBill = isPaperBill;
    }

    public String getIsRevisit() {
        return isRevisit;
    }

    public void setIsRevisit(String isRevisit) {
        this.isRevisit = isRevisit;
    }

    public String getSourceOfSupply() {
        return sourceOfSupply;
    }

    public void setSourceOfSupply(String sourceOfSupply) {
        this.sourceOfSupply = sourceOfSupply;
    }

    public String getResonMeterStuck() {
        return resonMeterStuck;
    }

    public void setResonMeterStuck(String resonMeterStuck) {
        this.resonMeterStuck = resonMeterStuck;
    }

    public String getResonSealStatus() {
        return resonSealStatus;
    }

    public void setResonSealStatus(String resonSealStatus) {
        this.resonSealStatus = resonSealStatus;
    }

    public String getResonContact() {
        return resonContact;
    }

    public void setResonContact(String resonContact) {
        this.resonContact = resonContact;
    }

    public String getSt_reason_for_dc() {
        return st_reason_for_dc;
    }

    public void setSt_reason_for_dc(String st_reason_for_dc) {
        this.st_reason_for_dc = st_reason_for_dc;
    }

    public String getPhoto_meter() {
        return photo_meter;
    }

    public void setPhoto_meter(String photo_meter) {
        this.photo_meter = photo_meter;
    }

    public String getPhoto_tester() {
        return photo_tester;
    }

    public void setPhoto_tester(String photo_tester) {
        this.photo_tester = photo_tester;
    }

    public String getUpload_image_NP() {
        return upload_image_NP;
    }

    public void setUpload_image_NP(String upload_image_NP) {
        this.upload_image_NP = upload_image_NP;
    }

    public String getReasonPl() {
        return reasonPl;
    }

    public void setReasonPl(String reasonPl) {
        this.reasonPl = reasonPl;
    }

    public String getTypeOfObstacle() {
        return typeOfObstacle;
    }

    public void setTypeOfObstacle(String typeOfObstacle) {
        this.typeOfObstacle = typeOfObstacle;
    }

    public String getMeterHeight() {
        return meterHeight;
    }

    public void setMeterHeight(String meterHeight) {
        this.meterHeight = meterHeight;
    }

    public String getUnsafeCondition() {
        return unsafeCondition;
    }

    public void setUnsafeCondition(String unsafeCondition) {
        this.unsafeCondition = unsafeCondition;
    }

    public String getReasonEN() {
        return reasonEN;
    }

    public void setReasonEN(String reasonEN) {
        this.reasonEN = reasonEN;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getSt_newMeter() {
        return st_newMeter;
    }

    public void setSt_newMeter(String st_newMeter) {
        this.st_newMeter = st_newMeter;
    }

    public String getSt_oldMeterCorrection() {
        return st_oldMeterCorrection;
    }

    public void setSt_oldMeterCorrection(String st_oldMeterCorrection) {
        this.st_oldMeterCorrection = st_oldMeterCorrection;
    }

    public Map<String, String> getLinkedHashMapValues() {
        return LinkedHashMapValues;
    }

    public void setLinkedHashMapValues(Map<String, String> linkedHashMapValues) {
        LinkedHashMapValues = linkedHashMapValues;
    }

    public String getMr_reason() {
        return mr_reason;
    }

    public void setMr_reason(String mr_reason) {
        this.mr_reason = mr_reason;
    }

    public Map<String, String> getLinkedHashMapImages() {
        return LinkedHashMapImages;
    }

    public void setLinkedHashMapImages(Map<String, String> linkedHashMapImages) {
        LinkedHashMapImages = linkedHashMapImages;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }
}
