package com.tpcodl.billingreading.utils;

public class ActivityUtils {
    private String serchCondition;
    private boolean isNonsbmilling;
    public static ActivityUtils instance;
    private String userID;
    private String dbServerUserName;
    private String dbServerPassword;

    public String getSerchCondition() {
        return serchCondition;
    }

    public void setSerchCondition(String serchCondition) {
        this.serchCondition = serchCondition;
    }

    private String divCode;
    private String userEmail;

    public String getFlag_sbmBilling() {
        return Flag_sbmBilling;
    }

    public void setFlag_sbmBilling(String flag_sbmBilling) {
        Flag_sbmBilling = flag_sbmBilling;
    }

    public String getFlag_nonsbmBilling() {
        return Flag_nonsbmBilling;
    }

    public void setFlag_nonsbmBilling(String flag_nonsbmBilling) {
        Flag_nonsbmBilling = flag_nonsbmBilling;
    }

    public String getFlag_billDistribution() {
        return Flag_billDistribution;
    }

    public void setFlag_billDistribution(String flag_billDistribution) {
        Flag_billDistribution = flag_billDistribution;
    }

    public String getFlag_qualityCheck() {
        return Flag_qualityCheck;
    }

    public void setFlag_qualityCheck(String flag_qualityCheck) {
        Flag_qualityCheck = flag_qualityCheck;
    }

    public String getFlag_theft() {
        return Flag_theft;
    }

    public void setFlag_theft(String flag_theft) {
        Flag_theft = flag_theft;
    }

    public String getFlag_consumerFB() {
        return Flag_consumerFB;
    }

    public void setFlag_consumerFB(String flag_consumerFB) {
        Flag_consumerFB = flag_consumerFB;
    }

    public String getFlag_extraconnection() {
        return Flag_extraconnection;
    }

    public void setFlag_extraconnection(String flag_extraconnection) {
        Flag_extraconnection = flag_extraconnection;
    }

    public String getFlag_bill() {
        return Flag_bill;
    }

    public void setFlag_bill(String flag_bill) {
        Flag_bill = flag_bill;
    }

    public String getFlaf_accountColl() {
        return Flaf_accountColl;
    }

    public void setFlaf_accountColl(String flaf_accountColl) {
        Flaf_accountColl = flaf_accountColl;
    }

    private String servreDate;
    private String userFlag;
    private String appVersion;
    private String authToken;
    private String userName;
    private String Flag_sbmBilling;
    private String Flag_nonsbmBilling;
    private String Flag_billDistribution;
    private String Flag_qualityCheck;
    private String Flag_theft;
    private String Flag_consumerFB;
    private String Flag_extraconnection;
    private String Flag_bill;
    private String Flaf_accountColl;


    public String getDbServerUserName() {
        return dbServerUserName;
    }

    public void setDbServerUserName(String dbServerUserName) {
        this.dbServerUserName = dbServerUserName;
    }

    public String getDbServerPassword() {
        return dbServerPassword;
    }

    public void setDbServerPassword(String dbServerPassword) {
        this.dbServerPassword = dbServerPassword;
    }

    public String getDivCode() {
        return divCode;
    }

    public void setDivCode(String divCode) {
        this.divCode = divCode;
    }


    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    private String userPassword;

    public String getUserFlag() {
        return userFlag;
    }

    public void setUserFlag(String userFlag) {
        this.userFlag = userFlag;
    }


    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    private String userAddress;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public String getServreDate() {
        return servreDate;
    }

    public void setServreDate(String servreDate) {
        this.servreDate = servreDate;
    }


    public static void setInstance(ActivityUtils instance) {
        ActivityUtils.instance = instance;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public static ActivityUtils getInstance() {
        if (instance == null) {
            instance = new ActivityUtils();
        }
        return instance;
    }

    public boolean isNonsbmilling() {
        return isNonsbmilling;
    }

    public void setNonsbmilling(boolean nonsbmilling) {
        isNonsbmilling = nonsbmilling;
    }
}
