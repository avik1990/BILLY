package com.tpcodl.billingreading.prefrences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.Constant;

public class PreferenceHandler {
    public final String MyPREFERENCES = "MyPrefs";
    private SharedPreferences sharedpreferences;
    private Context context;
    private boolean isLogin = false;
    private ActivityUtils utils;

    public PreferenceHandler(Context mcontext) {
        context = mcontext;
        utils = ActivityUtils.getInstance();
        sharedpreferences = mcontext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public void saveValidationdata(String userID, String dbServerUserName, String password, String dbServerPassword, String divCode, String flag, String app_version, String userName, String userEmail, String userAddress) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constant.userID, userID);
        //editor.putString(Constant.dbServerUserName, dbServerUserName);
        //editor.putString(Constant.dbServerPassword, dbServerPassword);
        editor.putString(Constant.userPassword, password);
        editor.putString(Constant.divCode, divCode);
        editor.putString(Constant.userFlag, flag);
        editor.putString(Constant.appVersion, app_version);
        editor.putString(Constant.userName, userName);
        editor.putString(Constant.userEmail, userEmail);
        editor.putString(Constant.userAdrress, userAddress);
        editor.commit();
    }

    // Store the updated password
    public void saveChangedPassword(String password) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constant.userPassword, password);
        editor.commit();


    }

    // getiing the Validattion details
    public void getValidationdata() {
        utils.setUserID(sharedpreferences.getString(Constant.userID, ""));
        utils.setUserPassword(sharedpreferences.getString(Constant.userPassword, ""));
        utils.setUserEmail(sharedpreferences.getString(Constant.userEmail, ""));
        utils.setUserName(sharedpreferences.getString(Constant.userName, ""));
        utils.setUserAddress(sharedpreferences.getString(Constant.userAdrress, ""));
        utils.setUserFlag(sharedpreferences.getString(Constant.userFlag, ""));
        utils.setAppVersion(sharedpreferences.getString(Constant.appVersion, ""));
        utils.setDbServerPassword(sharedpreferences.getString(Constant.dbServerPassword, ""));
        utils.setDbServerUserName(sharedpreferences.getString(Constant.dbServerUserName, ""));
        utils.setDivCode(sharedpreferences.getString(Constant.divCode, ""));
    }


    //Storing login details
    public void saveLogindata(String token, String serverTime, String sbmBilling,
                              String nonSbmBilling, String billDistribution, String qualityCheck, String theft,
                              String consumerFB, String extraConn, String bill, String accountColl, String version) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constant.authToken, token);
        editor.putString(Constant.serverDate, serverTime);
        editor.putString(Constant.Flag_sbmBilling, sbmBilling);
        editor.putString(Constant.Flag_nonsbmBilling, nonSbmBilling);
        editor.putString(Constant.Flag_billDistribution, billDistribution);
        editor.putString(Constant.Flag_qualityCheck, qualityCheck);
        editor.putString(Constant.Flag_theft, theft);
        editor.putString(Constant.Flag_consumerFB, consumerFB);
        editor.putString(Constant.Flag_extraconnection, extraConn);
        editor.putString(Constant.Flag_bill, bill);
        editor.putString(Constant.Flaf_accountColl, accountColl);
        editor.putString(Constant.appVersion, version);
        editor.commit();
    }


    //Storing login details
    public void updateLogindata(String sbmBilling,String serverDate) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constant.Flag_sbmBilling, sbmBilling);
        editor.putString(Constant.serverDate, serverDate);
        editor.commit();
    }

    // Getting LiginDetails
    public void getLoginData() {
        utils.setServreDate(sharedpreferences.getString(Constant.serverDate, ""));
        utils.setAuthToken(sharedpreferences.getString(Constant.authToken, ""));
        utils.setAppVersion(sharedpreferences.getString(Constant.appVersion, ""));
        utils.setFlag_sbmBilling(sharedpreferences.getString(Constant.Flag_sbmBilling, ""));
        utils.setFlag_nonsbmBilling(sharedpreferences.getString(Constant.Flag_nonsbmBilling, ""));
        utils.setFlag_billDistribution(sharedpreferences.getString(Constant.Flag_billDistribution, ""));
        utils.setFlag_qualityCheck(sharedpreferences.getString(Constant.Flag_qualityCheck, ""));
        utils.setFlag_theft(sharedpreferences.getString(Constant.Flag_theft, ""));
        utils.setFlag_consumerFB(sharedpreferences.getString(Constant.Flag_consumerFB, ""));
        utils.setFlag_extraconnection(sharedpreferences.getString(Constant.Flag_extraconnection, ""));
        utils.setFlag_bill(sharedpreferences.getString(Constant.Flag_bill, ""));
        utils.setFlaf_accountColl(sharedpreferences.getString(Constant.Flaf_accountColl, ""));
    }

    public static String getisSBNONSBFLAG(Context mContext) {
        SharedPreferences loginPreferences = mContext.getSharedPreferences("billing", 0); // 0 - for private mode
        String a_key = loginPreferences.getString("isClicked", "");
        return a_key;
    }

    public static void setisSBNONSBFLAG(Context mContext, String isVerified) {
        SharedPreferences preferences = mContext.getSharedPreferences("billing", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("isClicked", isVerified);
        editor.commit();
    }


    public static String getEnableBilling(Context mContext) {
        SharedPreferences loginPreferences = mContext.getSharedPreferences("billing", 0); // 0 - for private mode
        String a_key = loginPreferences.getString("isAllowedBill", "");
        return a_key;
    }

    public static void setEnableBilling(Context mContext, String isVerified) {
        SharedPreferences preferences = mContext.getSharedPreferences("billing", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("isAllowedBill", isVerified);
        editor.commit();
    }

    public static String getisUserId(Context mContext) {
        SharedPreferences loginPreferences = mContext.getSharedPreferences("billing", 0); // 0 - for private mode
        String a_key = loginPreferences.getString("UserId", "");
        return a_key;
    }

    public static void setisUserId(Context mContext, String isVerified) {
        SharedPreferences preferences = mContext.getSharedPreferences("billing", 0); // 0 - for private mode
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserId", isVerified);
        editor.commit();
    }
}
