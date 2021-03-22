package com.tpcodl.billingreading.reponseModel.bollingModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChildDetail {
    @SerializedName("transType")
    @Expose
    public String transType;
    @SerializedName("noOfReg")
    @Expose
    public String noOfReg;
    @SerializedName("ablbelnr")
    @Expose
    public String ablbelnr;
    @SerializedName("billMonth")
    @Expose
    public String billMonth;
    @SerializedName("billYear")
    @Expose
    public String billYear;
    @SerializedName("billedMd")
    @Expose
    public String billedMd;
    @SerializedName("consumptionOldMeter")
    @Expose
    public String consumptionOldMeter;
    @SerializedName("equipmentNo")
    @Expose
    public String equipmentNo;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("installation")
    @Expose
    public String installation;
    @SerializedName("lastOkRdng")
    @Expose
    public String lastOkRdng;
    @SerializedName("meterCondition")
    @Expose
    public String meterCondition;
    @SerializedName("meterInstallDate")
    @Expose
    public String meterInstallDate;
    @SerializedName("meterNo")
    @Expose
    public String meterNo;
    @SerializedName("meterRemovedOn")
    @Expose
    public String meterRemovedOn;
    @SerializedName("meterTyp")
    @Expose
    public String meterTyp;
    @SerializedName("mf")
    @Expose
    public String mf;
    @SerializedName("mrreason")
    @Expose
    public String mrreason;
    @SerializedName("newMeterFlg")
    @Expose
    public String newMeterFlg;
    @SerializedName("noOfDigits")
    @Expose
    public String noOfDigits;
    @SerializedName("presentMeterReading")
    @Expose
    public String presentMeterReading;
    @SerializedName("prevMtrRead")
    @Expose
    public String prevMtrRead;
    @SerializedName("prevReadDate")
    @Expose
    public String prevReadDate;
    @SerializedName("previousMd")
    @Expose
    public String previousMd;
    @SerializedName("prsMd")
    @Expose
    public String prsMd;
    @SerializedName("registerCode")
    @Expose
    public String registerCode;
    @SerializedName("scheduleMeterReadDate")
    @Expose
    public String scheduleMeterReadDate;

}