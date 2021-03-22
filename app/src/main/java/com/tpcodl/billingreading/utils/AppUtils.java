package com.tpcodl.billingreading.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.tpcodl.billingreading.BillingReadingApplication;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.listeners.MeterHeightSpinnerCallback;
import com.tpcodl.billingreading.listeners.MeterObstacleSpinnerCallback;
import com.tpcodl.billingreading.listeners.MeterReasonNVSpinnerCallback;
import com.tpcodl.billingreading.listeners.MeterSealSpinnerCallback;
import com.tpcodl.billingreading.listeners.MeterStucksSpinnerCallback;
import com.tpcodl.billingreading.listeners.MeterTypeSpinnerCallback;
import com.tpcodl.billingreading.listeners.PaperPasteSpinnerCallback;
import com.tpcodl.billingreading.listeners.ReasonDCSpinnerCallback;
import com.tpcodl.billingreading.listeners.ReasonOfCdSpinnerCallback;
import com.tpcodl.billingreading.listeners.ReasonOfENSpinnerCallback;
import com.tpcodl.billingreading.listeners.ReasonPLSpinnerCallback;
import com.tpcodl.billingreading.listeners.ReasonTLSpinnerCallback;
import com.tpcodl.billingreading.listeners.SourceSupplySpinnerCallback;
import com.tpcodl.billingreading.listeners.SupplyStatusSpinnerCallback;
import com.tpcodl.billingreading.listeners.UsageSpinnerCallback;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Provides convenience methods and abstractions to some tasks in Android
 * <p/>
 * <br/>
 * <br/>
 *
 * @author Jay
 */

public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();
    public static final String NEWS_TASK_CREATE_TIME_FORMAT = "MMMM dd 'at,' hh:mm a";
    public static final String NEWS_TASK_FILTER_DATE_FORMAT = "dd MMM, yyyy";
    public static final String NEWS_REWARDS_DATE_FORMAT = "EEEE, dd MMM, yyyy";
    public static final String NEWS_REWARDS_T_FORMAT = "hh:mm a";
    public static final String DAILY_TASK_FILTER_DATE_FORMAT = "dd MMMM yyyy";
    public static final String SERVER_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SERVER_TIME_FORMAT_SHORT = "yyyy-MM-dd";
    public static final String DOB_TIME_FORMAT_SHORT = "dd/MM/yyyy";
    public static final String DOB_SERVER_TIME_FORMAT_SHORT = "MM/dd/yyyy";
    //    public static final String DOB_SERVER_TIME_FORMAT_SHORT = "MM-dd-yyyy";
    public static final String THEME_COLOR = "#5684f5";
    private static ProgressDialog pgdialog;
    public static String strInputFilePath = Environment.getExternalStorageDirectory() + "/cesuapp/input/";//here the input text file to be placed

    public static String strOutputFilePath = Environment.getExternalStorageDirectory() + "/cesuapp/output/";//here the output text file to be placed
    public static String strOutputFileName = "RT_OUT.TXT";
    public static String strInputUPDATE_APK_URL = "http://portal.tpcentralodisha.com:8087/UPDATE_APK/";
    public static String strInputUPDATED_APK = "update.apk";
    public static String strDownloadedUPDATEAPKPath = Environment.getExternalStorageDirectory() + "/cesuapp/update/";
    private static int totrec = 0;
    private static String exportflag = "0";
    @SuppressWarnings("WeakerAccess")
    static ProgressDialog mProgressDialog;
    private static Dialog imageViewDialog;
    @SuppressLint("StaticFieldLeak")

    private static Dialog dialogComingSoon;
    private static AlertDialog alertDialog;


    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Shows a long time duration toast message.
     *
     * @param msg Message to be show in the toast.
     * @return Toast object just shown
     **/
    public static Toast showToast(Context ctx, CharSequence msg) {
        return showToast(ctx, msg, Toast.LENGTH_LONG);
    }

    /**
     * Shows the message passed in the parameter in the Toast.
     *
     * @param msg      Message to be show in the toast.
     * @param duration Duration in milliseconds for which the toast should be shown
     * @return Toast object just shown
     **/
    public static Toast showToast(Context ctx, CharSequence msg, int duration) {
        Toast toast = null;
        if (ctx != null) {
            toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
            toast.setDuration(duration);
            toast.show();
        }
        return toast;
    }


    public static String getQueryCondition(Context context, String SelChoice, String SelAcntno, String installationno) {
        String varCond = "";
        if (!SelChoice.equalsIgnoreCase("SeqNo")) {
            if (SelChoice.equals("consno")) {
                varCond = " where  CA='" + SelAcntno + "'";
            } else if (SelChoice.equals("MtrNo")) {
                varCond = " where METER_NO like  '%" + SelAcntno + "%'";
            } else if (SelChoice.equals("Phone")) {
                varCond = " where PHONE_1='" + SelAcntno + "'";
            } else if (SelChoice.equals("InstNo")) {
                varCond = " where INSTALLATION='" + SelAcntno + "'";
            } else if (SelChoice.equals("Legacy")) {
                if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                    varCond = " where substr(LEGACY_ACCOUNT_NO2,5)='" + SelAcntno + "'";
                } else {
                    varCond = " where LEGACY_ACCOUNT_NO=UPPER('" + SelAcntno + "')";
                }
            } else {
                varCond = " where LEGACY_ACCOUNT_NO=UPPER('" + SelAcntno + "')";
            }
        } else {
            varCond = " where INSTALLATION='" + installationno + "'";
        }
        return varCond;
    }


    public static int checkArrersAndUpload(Context context, String installation) {
        int val = 0;
        DatabaseHelper db = new DatabaseHelper(context);
        String strSelectSQL_01 = "SELECT ARREARS from TBL_SPOTBILL_HEADER_DETAILS where  INSTALLATION = '" + installation + "'";
        Log.e("SelectQuery312", strSelectSQL_01);
        // rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Cursor rs = db.getCalculateedData(strSelectSQL_01);
        String strCurDt = "";
        double ARREARS = 0.0;
        while (rs.moveToNext()) {
            ARREARS = rs.getDouble(0);
        }
        rs.close();

        if (ARREARS > 10000) {
            val = 1;
        }
        return val;
    }


    public static int checkCurrentBillAMTandPrint(Context context, String installation) {
        int val = 0;
        DatabaseHelper db = new DatabaseHelper(context);
        String strSelectSQL_01 = "SELECT CURRENT_BILL_TOTAL from TBL_SPOTBILL_HEADER_DETAILS where  INSTALLATION = '" + installation + "'";
        Log.e("SelectQuery312", strSelectSQL_01);
        // rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
        Cursor rs = db.getCalculateedData(strSelectSQL_01);
        String strCurDt = "";
        double CURRENT_BILL_TOTAL = 0.0;
        while (rs.moveToNext()) {
            CURRENT_BILL_TOTAL = rs.getDouble(0);
        }
        rs.close();

        if (CURRENT_BILL_TOTAL > 100000) {
            val = 1;
        }
        return val;
    }

    public static int checkNewMeterFlag(Context context, String installation) {
        int val = 0;
        DatabaseHelper db = new DatabaseHelper(context);
        String strSelectSQL_01 = "SELECT NEW_METER_FLG from TBL_SPOTBILL_CHILD_DETAILS where  INSTALLATION = '" + installation + "'";
        Log.e("SelectQuery312", strSelectSQL_01);
        Cursor rs = db.getCalculateedData(strSelectSQL_01);
        String new_meter = "";
        while (rs.moveToNext()) {
            new_meter = rs.getString(0);
        }
        rs.close();

        try {
            if (new_meter.equalsIgnoreCase("X")) {
                val = 1;
            }
        } catch (Exception e) {
            val = 0;
        }
        return val;
    }

    public static double checkMonthCount(Context context, String installation) {
        double val = 0.0;
        DatabaseHelper db = new DatabaseHelper(context);
        String strSelectSQL_01 = "SELECT NO_BILLED_MONTH  from TBL_SPOTBILL_HEADER_DETAILS where  INSTALLATION = '" + installation + "'";
        Log.e("SelectQuery312", strSelectSQL_01);
        Cursor rs = db.getCalculateedData(strSelectSQL_01);
        double month_count = 0.0;
        while (rs.moveToNext()) {
            month_count = rs.getFloat(0);
        }
        rs.close();

        try {
            if (month_count==0.0) {
                val = 1.0;
            }
        } catch (Exception e) {
            val = 0.0;
        }

        return val;
    }



    public static String cyclicLeftShift(String s, int n) { //'n' is the number of characters to shift left
        n = n % s.length();
        return s.substring(n) + s.substring(0, n);
    }

    public static String cyclicRightShift(String s, int n) { //'n' is the number of characters to shift right
        n = n % s.length();
        return s.substring(s.length() - n, s.length()) + s.substring(0, s.length() - n);
    }

   /* public static String SBMRTFile(int Param, String Paramtype1, Context context) throws SQLException, IOException
    {
        //creating blank output file
        File file = new File(strOutputFilePath+strOutputFileName);
        boolean blnCreated = false;
        try
        {
            blnCreated = file.createNewFile();
        }
        catch(IOException ioe)
        {
            Log.v("","Error while creating a new empty file :" + ioe);
        }
        ////
        Log.d("DemoApp", "Entering SBM Utilities");
        int Paramval=Param;
        String Paramtype=Paramtype1;
        ArrayList<String> listtFilefound = new ArrayList<String>();
        long lTimeStart = System.currentTimeMillis();
        //getDBConnectionSqlLite();
        try {
            // GlobalData.ConfigDataHash=ConfigLoad.ConfigLoad();
            //  print_flag=Integer.parseInt(GlobalData.ConfigDataHash.get("PRINT_FLAG").toString());;
            // dbConnection = CreateConnection.getConnectionSqlLite(GlobalData.ConfigDataHash);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (Paramval == 1 && Paramtype.equals("I"))// migrated to SBMUtilitiesActivity // not running here
        {
            //DownloadFileHTTP.DownloadFileHTTP(strInputURL, strInputFileName, strInputFilePath);
            listtFilefound = MovingFile.checkFile(strInputFilePath, ".TXT", "RT");
            Log.d("DemoApp", "2 Inside SBM "+listtFilefound.size());
            if ( listtFilefound.size()>0)
            {
                for ( int j=0; j<listtFilefound.size(); j++ )
                {
                    Log.d("DemoApp", "21 Inside SBM "+listtFilefound.get(j));
                    //SQLiteBulkDataIn.Import_All(strInputFilePath, strInputFileName);
                    //added bys shanti to know the total records in a file to maintain progress bar 01.01.2016
                    try {
                        LineNumberReader lnr = new LineNumberReader(new FileReader(new File(strInputFilePath+ listtFilefound.get(j))));
                        lnr.skip(Long.MAX_VALUE);
                        totrec=lnr.getLineNumber() + 1;
                        Log.d("DemoApp", "row num is =" +totrec);//Add 1 because line index starts at 0
                        // Finally, the LineNumberReader object should be closed to prevent resource leak
                        lnr.close();
                    }catch(Exception e){
                        Log.d("DemoApp", "row num is =" +e);
                    }
                    //end
                    Import_All(strInputFilePath, listtFilefound.get(j), j);
                }
            }
        }
        else if (Paramval == 1 && Paramtype.equals("E"))
        {
            Log.d("DemoApp", "Entering 2");

            exportflag=SQLiteBulkDataOut.Export_All(strOutputFilePath, strOutputFileName,context);

        }
        else if (Paramval == 2 && Paramtype.equals("O"))
        {
            //     SQLiteBulkDataOut.Export_One(args[1]);
        }
        else if (Paramval == 1 && Paramtype.equals("C"))
        {
            //    CalculateBill.CalculateBill_All();
        }
        else if (Paramval == 1 && Paramtype.length()==8 )
        {
            //   CalculateBill.CalculateBill(args[0]);
        }
        else
        {
            //  System.out.println("Usage:: Call the program With Argument (E: Export; I: Import; C: Calculate All; CONS_ACC: For Individual Calculation;)");
        }
        long lTimeEnd = System.currentTimeMillis();
        double dTime = 1 + (lTimeEnd-lTimeStart)/1000;
        //   System.out.println("########## Time Elapsed :: " + dTime + " Seconds");
        return exportflag;
    }
    ////////////////Initial method/////////////////////////
    public static String Import_All(String strInputFilePath, String strInputFileName, int filecounter) throws SQLException
    {
        if (filecounter==0)
        {
            SQLiteBulkDataIn.InitializeImport();// deleting the data
        }

        String strLine = null;
        File directory = new File (".");
       *//* try {
            // System.out.println ("Current Directory's Canonical path: " + directory.getCanonicalPath());
            Log.d("DemoApp", "Current Directory's getAbsolutePath : " + directory.getAbsolutePath());
        }
        catch(Exception e)
        {
            //  System.out.println("Exception1 is ="+e.getMessage());
            Log.d("DemoApp", "Exception1 is ="+e.getMessage());
        }*//*
        try
        {

            FileReader input = new FileReader(strInputFilePath+strInputFileName);
            BufferedReader bufRead = new BufferedReader(input);
            int count = 0;  // Line number of count
            //  Log.d("DemoApp", "strLine ="+strLine);
            // Read first line

            strLine = bufRead.readLine();
            // Read through file one line at time. Print line # and line
            while (strLine != null)
            {
                SQLiteBulkDataIn.ProcessDataLine(strLine);
                strLine = bufRead.readLine();
                count++;
            }
            Log.d("DemoApp", "222 strLine =" + count);
            Log.d("DemoApp", "Read File: " + strInputFilePath + strInputFileName + "Read Line: " + count);
            bufRead.close();
            //System.out.println("Read File: " + strInputFilePath+strInputFileName + "Read Line: " + count );
            Log.d("DemoApp", "Read File: " + strInputFilePath + strInputFileName + "Read Line: " + count);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // System.out.println("Unable to read File: " + strInputFilePath+strInputFileName);
            Log.d("DemoApp", "Unable to read File: " + strInputFilePath+strInputFileName);
        }
        return strLine;
    }*/

    /**
     * Checks if the Internet connection is available.
     *
     * @return Returns true if the Internet connection is available. False otherwise.
     **/

    public static boolean isInternetAvailable(Context ctx) {
        // using received context (typically activity) to get SystemService
        // causes memory link as this holds strong reference to that activity.
        // use application level context instead, which is available until the app dies.
        NetworkInfo networkInfo = null;
        if (ctx != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }


        //noinspection ConstantConditions


        // if network is NOT available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();

    }

    /**
     * Checks if the SD Card is mounted on the device.
     **/
    @SuppressWarnings("unused")
    public static boolean isSdCardMounted() {
        String status = Environment.getExternalStorageState();
        return status != null && status.equals(Environment.MEDIA_MOUNTED);

    }

    /*Method to show progress dialog*/
    public static void showDialog(Context mContext, String color) {
        try {
            if ((color == null || color.isEmpty()) || (!color.equalsIgnoreCase(THEME_COLOR)
                    && (!color.equalsIgnoreCase("#FFFFFF")
                    && !color.equalsIgnoreCase("FFF")))) {
                color = THEME_COLOR;
            }

            if (pgdialog != null) if (pgdialog.isShowing())
                pgdialog.dismiss();

            pgdialog = ProgressDialog.show(mContext, null, null);
            ProgressBar spinner = new ProgressBar(mContext, null,
                    android.R.attr.progressBarStyle);
            spinner.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"),
                    Mode.SRC_IN);
            pgdialog.setContentView(spinner);
            //noinspection ConstantConditions
            pgdialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            pgdialog.setProgressDrawable(new ColorDrawable(Color.parseColor(color)));
            pgdialog.setIndeterminateDrawable(new ColorDrawable(Color.parseColor(color)));
            pgdialog.setCancelable(false);
            pgdialog.setCanceledOnTouchOutside(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Method to show progress dialog*/


    /*Method to dismiss dialog*/
    public static void dismissDialog() {
        try {
            if (pgdialog != null)
                if (pgdialog.isShowing())
                    pgdialog.dismiss();
        } catch (Exception ignored) {
        }
    }

    /**
     * Shows an alert dialog with the OK button. When the user presses OK button, the dialog
     * dismisses.
     **/
    @SuppressWarnings("unused")
    public static void showAlertDialog(Context context, @StringRes int titleResId,
                                       @StringRes int bodyResId) {
        showAlertDialog(context, context.getString(titleResId), context.getString(bodyResId),
                true, null);
    }

    /**
     * Shows an alert dialog with the OK button. When the user presses OK button, the dialog
     * dismisses.
     **/
    @SuppressWarnings("unused")
    public static void showAlertDialog(Context context, String title, String body) {
        showAlertDialog(context, title, body, true, null);
    }

    /**
     * Method to return the file provider uri with content scheme
     *
     * @param mContext context
     * @param file     file in reference
     * @return uri with content scheme
     */
    public static Uri getFileProviderUri(Context mContext, File file) {
        return FileProvider.getUriForFile(
                mContext,
                mContext.getApplicationContext().getPackageName() + ".provider",
                file);
    }

    /**
     * Shows an alert dialog with OK button
     **/
    public static void showAlertDialog(Context context, String title, String body,
                                       boolean cancelable, DialogInterface.OnClickListener okListener) {
        showAlertDialog(context, title, body, "Ok", cancelable, okListener);
    }

    /**
     * Shows an alert dialog with OK button
     **/
    public static void showAlertDialog(Context context, String title, String body,
                                       String positiveButton, boolean cancelable,
                                       DialogInterface.OnClickListener okListener) {

        dismissAlertDialog();

        if (okListener == null) {
            okListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setCancelable(cancelable)
                .setMessage(body).setPositiveButton(positiveButton, okListener);

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Shows an alert dialog with OK button
     **/
    public static void showAlertDialog(Context context, String title, String body,
                                       boolean cancelable, boolean negativeButton,
                                       DialogInterface.OnClickListener okListener,
                                       DialogInterface.OnClickListener cancelListener) {
        showAlertDialog(context, title, body, cancelable, negativeButton,
                "Ok",
                context.getString(R.string.cancel),
                okListener, cancelListener);
    }


    /**
     * Shows an alert dialog with OK button
     **/
    public static void showAlertDialog(Context context, String title, String body,
                                       boolean cancelable, boolean showNegButton,
                                       String positiveButton, String negButton,
                                       DialogInterface.OnClickListener okListener,
                                       DialogInterface.OnClickListener cancelListener) {

        dismissAlertDialog();

        if (okListener == null) {
            okListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
        }

        if (cancelListener == null) {
            cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setCancelable(cancelable)
                .setMessage(body).setPositiveButton(positiveButton, okListener);

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        if (showNegButton) {
            builder.setNegativeButton(negButton, cancelListener);
        }

        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void dismissAlertDialog() {
        if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
    }

    /**
     * Shows an alert dialog with OK button
     **/
    public static void showAlertDialog(Context context, String title, String body
            , String positiveButton, String negativeButton, boolean cancelable
            , DialogInterface.OnClickListener okListener) {

        dismissAlertDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCancelable(cancelable)
                .setMessage(body).setPositiveButton(positiveButton, okListener)
                .setNegativeButton(negativeButton, okListener);

        if (okListener == null) {
            //noinspection UnusedAssignment
            okListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            };
        }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * Serializes the Bitmap to Base64
     *
     * @return Base64 string value of a {@linkplain Bitmap} passed in as a parameter
     * @throws NullPointerException If the parameter bitmap is null.
     **/
    @SuppressWarnings("unused")
    public static String toBase64(Bitmap bitmap) {

        if (bitmap == null) {
            throw new NullPointerException("Bitmap cannot be null");
        }

        //noinspection UnusedAssignment
        String base64Bitmap = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBitmap = stream.toByteArray();
        base64Bitmap = Base64.encodeToString(imageBitmap, Base64.DEFAULT);

        return base64Bitmap;
    }

    /**
     * Converts the passed in drawable to Bitmap representation
     *
     * @throws NullPointerException If the parameter drawable is null.
     **/
    @SuppressWarnings("unused")
    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable == null) {
            throw new NullPointerException("Drawable to convert should NOT be null");
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        if (drawable.getIntrinsicWidth() <= 0 && drawable.getIntrinsicHeight() <= 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Converts the given bitmap to {@linkplain InputStream}.
     *
     * @throws NullPointerException If the parameter bitmap is null.
     **/
    @SuppressWarnings("unused")
    public static InputStream bitmapToInputStream(Bitmap bitmap) throws NullPointerException {

        if (bitmap == null) {
            throw new NullPointerException("Bitmap cannot be null");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Shows a progress dialog with a spinning animation in it. This method must preferably called
     * from a UI thread.
     *
     * @param ctx           Activity context
     * @param title         Title of the progress dialog
     * @param body          Body/Message to be shown in the progress dialog
     * @param isCancellable True if the dialog can be cancelled on back button press, false otherwise
     **/
    @SuppressWarnings("unused")
    public static void showProgressDialog(Context ctx, String title, String body, boolean isCancellable) {
        showProgressDialog(ctx, title, body, null, isCancellable);
    }

    /**
     * Shows a progress dialog with a spinning animation in it. This method must preferably called
     * from a UI thread.
     *
     * @param ctx           Activity context
     * @param title         Title of the progress dialog
     * @param body          Body/Message to be shown in the progress dialog
     * @param icon          Icon to show in the progress dialog. It can be null.
     * @param isCancellable True if the dialog can be cancelled on back button press, false otherwise
     **/
    @SuppressWarnings("WeakerAccess")
    public static void showProgressDialog(Context ctx, String title, String body,
                                          @SuppressWarnings("SameParameterValue") Drawable icon,
                                          boolean isCancellable) {

        if (ctx instanceof Activity) {
            if (!((Activity) ctx).isFinishing()) {
                mProgressDialog = ProgressDialog.show(ctx, title, body, true);
                mProgressDialog.setIcon(icon);
                mProgressDialog.setCancelable(isCancellable);
            }
        }
    }

    /**
     * this method is used to get the date with month name
     *
     * @param completeDate complete date
     * @return string formated date
     */
    @SuppressWarnings("unused")
    public static String getFullSimpleDate(String completeDate) {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        @SuppressLint("SimpleDateFormat") DateFormat targetFormat =
                new SimpleDateFormat("dd MMMM, yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(completeDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return targetFormat.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getSimpleDateTimeFormat(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * Check if the {@link ProgressDialog} is visible in the UI.
     **/
    @SuppressWarnings("unused")
    public static boolean isProgressDialogVisible() {
        return (mProgressDialog != null);
    }

    /**
     * Dismiss the progress dialog if it is visible.
     **/
    @SuppressWarnings("unused")
    public static void dismissProgressDialog() {

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = null;
    }

    /**
     * Gives the device independent constant which can be used for scaling images, manipulating view
     * sizes and changing dimension and display pixels etc.
     **/
    @SuppressWarnings("unused")
    public static float getDensityMultiplier(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A int value to represent dp equivalent to px value
     */
    @SuppressWarnings("unused")
    public static int getDip(int px, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }

    /**
     * Creates a confirmation dialog with Yes-No Button. By default the buttons just dismiss the
     * dialog.
     *
     * @param ctx         context
     * @param message     Message to be shown in the dialog.
     * @param yesListener Yes click handler
     * @param noListener  listener
     **/
    @SuppressWarnings("unused")
    public static void showConfirmDialog(Context ctx, String message,
                                         DialogInterface.OnClickListener yesListener,
                                         DialogInterface.OnClickListener noListener) {
        showConfirmDialog(ctx, message, yesListener, noListener, "Yes", "No");
    }

    /**
     * Creates a confirmation dialog with Yes-No Button. By default the buttons just dismiss the
     * dialog.
     *
     * @param ctx         context
     * @param message     Message to be shown in the dialog.
     * @param yesListener Yes click handler
     * @param noListener  listener
     * @param yesLabel    Label for yes button
     * @param noLabel     Label for no button
     **/
    @SuppressWarnings("WeakerAccess")
    public static void showConfirmDialog(Context ctx, String message,
                                         DialogInterface.OnClickListener yesListener,
                                         DialogInterface.OnClickListener noListener,
                                         @SuppressWarnings("SameParameterValue") String yesLabel,
                                         @SuppressWarnings("SameParameterValue") String noLabel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        if (yesListener == null) {
            yesListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }

        if (noListener == null) {
            noListener = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }

        builder.setMessage(message).setPositiveButton(yesLabel, yesListener)
                .setNegativeButton(noLabel, noListener).show();
    }

    /**
     * Creates a confirmation dialog that show a pop-up with button labeled as parameters labels.
     *
     * @param ctx                 {@link Activity} {@link Context}
     * @param message             Message to be shown in the dialog.
     * @param dialogClickListener dialog listener
     * @param positiveBtnLabel    For e.g. "Yes"
     * @param negativeBtnLabel    For e.g. "No"
     **/
    public static void showDialog(Context ctx, String message, String positiveBtnLabel,
                                  String negativeBtnLabel,
                                  DialogInterface.OnClickListener dialogClickListener) {

        if (dialogClickListener == null) {
            throw new NullPointerException("Action listener cannot be null");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        builder.setMessage(message).setPositiveButton(positiveBtnLabel,
                dialogClickListener).setNegativeButton(negativeBtnLabel, dialogClickListener).show();
    }

    /**
     * Gets the version name of the application. For e.g. 1.9.3
     **/
    @SuppressWarnings("unused")
    public static String getApplicationVersionNumber(Context context) {

        String versionName = null;

        if (context == null) {
            return null;
        }

        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    public static String getDeviceId(Context context) {
        @SuppressLint("HardwareIds") String androidId =
                Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * Gets the version code of the application. For e.g. Maverick Meerkat or 2013050301
     **/
    @SuppressWarnings("unused")
    public static int getApplicationVersionCode(Context ctx) {

        int versionCode = 0;

        try {
            versionCode = ctx.getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    /**
     * Gets the version number of the Android OS For e.g. 2.3.4 or 4.1.2
     **/
    @SuppressWarnings("unused")
    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Checks if the service with the given name is currently running on the device.
     *
     * @param serviceName Fully qualified name of the server. <br/>
     *                    For e.g. nl.changer.myservice.name
     **/
    @SuppressWarnings("unused")
    public static boolean isServiceRunning(Context ctx, String serviceName) {

        if (serviceName == null) {
            throw new NullPointerException("Service name cannot be null");
        }

        // use application level context to avoid unnecessary leaks.
        ActivityManager manager = (ActivityManager) ctx.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        //noinspection ConstantConditions
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals(serviceName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the device unique id called IMEI. Sometimes, this returns 00000000000000000 for the
     * rooted devices.
     **/
    @SuppressWarnings("unused")
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDeviceImei(Context ctx) {
        // use application level context to avoid unnecessary leaks.
        TelephonyManager telephonyManager = (TelephonyManager) ctx.getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        assert telephonyManager != null;
        return telephonyManager.getDeviceId();
    }

    /**
     * Shares an application over the social network like Facebook, Twitter etc.
     *
     * @param sharingMsg   Message to be pre-populated when the 3rd party app dialog opens up.
     * @param emailSubject Message that shows up as a subject while sharing through email.
     * @param title        Title of the sharing options prompt. For e.g. "Share via" or "Share using"
     **/
    public static void share(Context ctx, String sharingMsg, String emailSubject, String title) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);

        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharingMsg);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);

        ctx.startActivity(Intent.createChooser(sharingIntent, title));
    }

    /**
     * Checks the type of data connection that is currently available on the device.
     *
     * @return <code>ConnectivityManager.TYPE_*</code> as a type of internet connection on the
     * device. Returns -1 in case of error or none of
     * <code>ConnectivityManager.TYPE_*</code> is found.
     **/
    @SuppressWarnings("unused")
    public static int getDataConnectionType(Context ctx) {

        // use application level context to avoid unnecessary leaks.
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null
                && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                return ConnectivityManager.TYPE_MOBILE;
            } else if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                return ConnectivityManager.TYPE_WIFI;
            }
        }

        return -1;
    }

    /**
     * Checks if the input parameter is a valid email.
     *
     * @param email email
     * @return boolean
     */
    @SuppressWarnings("unused")
    public static boolean isValidEmail(String email) {

        if (email == null) {
            return false;
        }

        final String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Matcher matcher;
        Pattern pattern = Pattern.compile(emailPattern);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    /**
     * Capitalizes each word in the string.
     *
     * @param string string
     * @return string
     */
    @SuppressWarnings("unused")
    @Nullable
    public static String capitalizeString(String string) {

        if (string == null) {
            return null;
        }

        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You
                // can add other
                // chars here
                found = false;
            }
        } // end for

        return String.valueOf(chars);
    }

    /**
     * Checks if the DB with the given name is present on the device.
     *
     * @param packageName package name
     * @param dbName      database
     * @return boolean
     */
    @SuppressLint("SdCardPath")
    @SuppressWarnings("unused")
    public static boolean isDatabasePresent(String packageName, String dbName) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/" + packageName
                    + "/databases/" + dbName, null, SQLiteDatabase.OPEN_READONLY);
            sqLiteDatabase.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            e.printStackTrace();
            Log.e(TAG, "The database does not exist." + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception " + e.getMessage());
        }

        return (sqLiteDatabase != null);
    }

    /**
     * Get the file path from the Media Content Uri for video, audio or images.
     *
     * @param mediaContentUri Media content Uri.
     **/
    @SuppressWarnings("unused")
    public static String getPathForMediaUri(Context context, Uri mediaContentUri) {

        Cursor cur = null;
        String path = null;

        try {
            String[] projection = {MediaColumns.DATA};
            cur = context.getContentResolver().query(mediaContentUri, projection,
                    null, null, null);

            if (cur != null && cur.getCount() != 0) {
                cur.moveToFirst();
                path = cur.getString(cur.getColumnIndexOrThrow(MediaColumns.DATA));
            }

            // Log.v( TAG, "#getRealPathFromURI Path: " + path );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cur != null && !cur.isClosed())
                cur.close();
        }

        return path;
    }

    @SuppressWarnings("unused")
    public static ArrayList<String> toStringArray(JSONArray jsonArr) {

        if (jsonArr == null || jsonArr.length() == 0) {
            return null;
        }

        ArrayList<String> stringArray = new ArrayList<>();

        for (int i = 0, count = jsonArr.length(); i < count; i++) {
            try {
                String str = jsonArr.getString(i);
                stringArray.add(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return stringArray;
    }

    /**
     * Convert a given list of {@link String} into a {@link JSONArray}
     **/
    @SuppressWarnings("unused")
    public static JSONArray toJSONArray(ArrayList<String> stringArr) {
        JSONArray jsonArr = new JSONArray();

        for (int i = 0; i < stringArr.size(); i++) {
            String value = stringArr.get(i);
            jsonArr.put(value);
        }

        return jsonArr;
    }

    /**
     * Gets the data storage directory(pictures dir) for the device. If the external storage is not
     * available, this returns the reserved application data storage directory. SD Card storage will
     * be preferred over internal storage.
     *
     * @param dirName if the directory name is specified, it is created inside the DIRECTORY_PICTURES
     *                directory.
     * @return Data storage directory on the device. Maybe be a directory on SD Card or internal
     * storage of the device.
     **/
    @SuppressWarnings("WeakerAccess")
    public static File getStorageDirectory(Context ctx,
                                           @SuppressWarnings("SameParameterValue") String dirName) {

        if (TextUtils.isEmpty(dirName)) {
            dirName = "atemp";
        }

        //noinspection UnusedAssignment
        File f = null;

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES) + "/" + dirName);
        } else {
            // media is removed, unmounted etc
            // Store image in
            // /data/data/<package-name>/cache/atemp/photograph.jpeg
            f = new File(ctx.getCacheDir() + "/" + dirName);
        }

        if (!f.exists()) {
            //noinspection ResultOfMethodCallIgnored
            f.mkdirs();
        }

        return f;
    }

    /**
     * Given a file name, this method creates a {@link File} on best chosen device storage and
     * returns the file object. You can get the file path using {@link File#getAbsolutePath()}
     **/
    @SuppressWarnings("unused")
    public static File getFile(Context ctx, String fileName) {
        File dir = getStorageDirectory(ctx, null);
        return new File(dir, fileName);
    }

    /**
     * @return Path of the image file that has been written.
     * @deprecated Use {@link Context , byte[]}
     * Writes the given image to the external storage of the device. If external storage is not
     * available, the image is written to the application private directory
     **/
    public static String writeImage(Context ctx, byte[] imageData) {

        final String FILE_NAME = "photograph.jpeg";
        //noinspection UnusedAssignment
        File dir = null;
        //noinspection UnusedAssignment
        String filePath = null;
        OutputStream imageFileOS;

        dir = getStorageDirectory(ctx, null);
        File f = new File(dir, FILE_NAME);

        try {
            imageFileOS = new FileOutputStream(f);
            imageFileOS.write(imageData);
            imageFileOS.flush();
            imageFileOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        filePath = f.getAbsolutePath();

        return filePath;
    }

    /**
     * Inserts an image into {@link Media} content provider of the device.
     *
     * @return The media content Uri to the newly created image, or null if the image failed to be
     * stored for any reason.
     **/
    @SuppressWarnings("unused")
    public static String writeImageToMedia(Context ctx, Bitmap image,
                                           String title, String description) {
        // TODO: move to MediaUtils
        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }

        return Media.insertImage(ctx.getContentResolver(), image, title, description);
    }

    /**
     * Gets the name of the application that has been defined in AndroidManifest.xml
     *
     * @throws NameNotFoundException
     **/

    public static URL getPathFromUrl(URL url) {

        if (url != null) {
            String urlStr = url.toString();
            String urlWithoutQueryString = urlStr.split("\\?")[0];
            try {
                return new URL(urlWithoutQueryString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Transforms Calendar to ISO 8601 string.
     **/
    @SuppressWarnings("WeakerAccess")
    public static String fromCalendar(final Calendar calendar) {
        // TODO: move this method to DateUtils
        Date date = calendar.getTime();
        @SuppressLint("SimpleDateFormat") String formatted =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * Gets current date and time formatted as ISO 8601 string.
     **/
    @SuppressWarnings("unused")
    public static String now() {
        // TODO: move this method to DateUtils
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /**
     * Transforms ISO 8601 string to Calendar.
     **/
    @SuppressLint("SimpleDateFormat")
    @SuppressWarnings("unused")
    public static Calendar toCalendar(final String iso8601string) throws ParseException {
        // TODO: move this method to DateUtils
        Calendar calendar = GregorianCalendar.getInstance();
        String s = iso8601string.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);
        } catch (IndexOutOfBoundsException e) {
            // throw new org.apache.http.ParseException();
            e.printStackTrace();
        }

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar;
    }

    /**
     * @param time ISO formatted time when the event occurred in local time zone.
     * @deprecated Totally bloated code.
     * Calculates the elapsed time after the given parameter date.
     **/


    /**
     * Set Mock Location for test device. DDMS cannot be used to mock location on an actual device.
     * So this method should be used which forces the GPS Provider to mock the location on an actual
     * device.
     **/
    @SuppressWarnings("unused")
    public static void setMockLocation(Context ctx, double longitude, double latitude) {
        // use application level context to avoid unnecessary leaks.
        LocationManager locationManager = (LocationManager) ctx.getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        assert locationManager != null;
        locationManager.addTestProvider(LocationManager.GPS_PROVIDER,
                false, false, false, false,
                false, false, false,

                android.location.Criteria.POWER_LOW, android.location.Criteria.ACCURACY_FINE);

        Location newLocation = new Location(LocationManager.GPS_PROVIDER);

        newLocation.setLongitude(longitude);
        newLocation.setLatitude(latitude);
        newLocation.setTime(new Date().getTime());

        newLocation.setAccuracy(500);

        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

        locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER,
                LocationProvider.AVAILABLE, null, System.currentTimeMillis());

        // http://jgrasstechtips.blogspot.it/2012/12/android-incomplete-location-object.html
        makeLocationObjectComplete(newLocation);

        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);
    }

    private static void makeLocationObjectComplete(Location newLocation) {
        Method locationJellyBeanFixMethod = null;
        try {
            locationJellyBeanFixMethod = Location.class.getMethod("makeComplete");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (locationJellyBeanFixMethod != null) {
            try {
                locationJellyBeanFixMethod.invoke(newLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Gets random color integer
     **/
    @SuppressWarnings("unused")
    public static int getRandomColor() {
        Random random = new Random();
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);

        return Color.argb(255, red, green, blue);
    }

    /**
     * Converts a given bitmap to byte array
     */
    @SuppressWarnings("unused")
    public static byte[] toBytes(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Resizes an image to the given width and height parameters Prefer using
     *
     * @param sourceBitmap Bitmap to be resized
     * @param newWidth     Width of resized bitmap
     * @param newHeight    Height of the resized bitmap
     */
    @SuppressWarnings("unused")
    public static Bitmap resizeImage(Bitmap sourceBitmap, int newWidth,
                                     int newHeight, @SuppressWarnings("unused") boolean filter) {
        // TODO: move this method to ImageUtils
        if (sourceBitmap == null) {
            throw new NullPointerException("Bitmap to be resized cannot be null");
        }

        //noinspection UnusedAssignment
        Bitmap resized = null;

        if (sourceBitmap.getWidth() < sourceBitmap.getHeight()) {
            // image is portrait
            //noinspection UnusedAssignment,SuspiciousNameCombination
            resized = Bitmap.createScaledBitmap(sourceBitmap, newHeight, newWidth, true);
        } else {
            // image is landscape
            //noinspection UnusedAssignment
            resized = Bitmap.createScaledBitmap(sourceBitmap, newWidth, newHeight, true);
        }

        resized = Bitmap.createScaledBitmap(sourceBitmap, newWidth, newHeight, true);

        return resized;
    }

    /**
     * <br/>
     * <br/>
     *
     * @param compressionFactor Powers of 2 are often faster/easier for the decoder to honor
     */
    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("unused")
    public static Bitmap compressImage(Bitmap sourceBitmap, int compressionFactor) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Config.ARGB_8888;
        opts.inSampleSize = compressionFactor;

        if (Build.VERSION.SDK_INT >= 10) {
            opts.inPreferQualityOverSpeed = true;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opts);
    }

    @SuppressWarnings("unused")
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * Provide the height to which the sourceImage is to be resized. This method will calculate the
     * resultant height. Use scaleDownBitmap from {@link } wherever possible
     */
    @SuppressWarnings("unused")
    public Bitmap resizeImageByHeight(int height, Bitmap sourceImage) {
        // TODO: move this method to ImageUtils
        int widthO; // original width
        int heightO; // original height
        int widthNew;
        int heightNew;

        widthO = sourceImage.getWidth();
        heightO = sourceImage.getHeight();
        heightNew = height;

        // Maintain the aspect ratio
        // of the original banner image.
        widthNew = (heightNew * widthO) / heightO;

        return Bitmap.createScaledBitmap(sourceImage, widthNew, heightNew, true);
    }

    /**
     * Checks if the url is valid
     */
    @SuppressWarnings("unused")
    public static boolean isValidURL(String url) {
        URL urlObj;

        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            urlObj.toURI();
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }


    /**
     * @return Lower case string for one of above listed media type
     * @deprecated Use
     * Get the type of the media. Audio, Video or Image.
     */
    @SuppressWarnings("unused")
    @Nullable
    public static String getMediaType(String contentType) {
        if (isMedia(contentType)) {
            if (isVideo(contentType)) {
                return "video";
            } else if (isAudio(contentType)) {
                return "audio";
            } else if (isImage(contentType)) {
                return "image";
            }
        }
        return null;
    }

    /**
     * @param mimeType standard MIME type of the data.
     * @deprecated {@link #( String )}
     * Identifies if the content represented by the parameter mimeType is media. Image, Audio and
     * Video is treated as media by this method. You can refer to standard MIME type here. <a
     * href="http://www.iana.org/assignments/media-types/media-types.xhtml" >Standard MIME
     * types.</a>
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static boolean isMedia(String mimeType) {
        boolean isMedia = false;

        if (mimeType != null) {
            if (mimeType.startsWith("image/")
                    || mimeType.startsWith("video/") || mimeType.startsWith("audio/")) {
                isMedia = true;
            }
        } else {
            isMedia = false;
        }

        return isMedia;
    }

    /**
     * Gets the Uri without the fragment. For e.g if the uri is
     * content://com.android.storage/data/images/48829#is_png the part after '#' is called as
     * fragment. This method strips the fragment and returns the url.
     */
    @SuppressWarnings("unused")
    public static String removeUriFragment(String url) {

        if (url == null || url.length() == 0) {
            return null;
        }

        String[] arr = url.split("#");

        if (arr.length == 2) {
            return arr[0];
        } else {
            return url;
        }
    }

    /**
     * Removes the parameters from the query from the uri
     */
    @SuppressWarnings("unused")
    public static String removeQueryParameters(Uri uri) {
        assert (uri.getAuthority() != null);
        assert (uri.getPath() != null);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(uri.getScheme());
        builder.encodedAuthority(uri.getAuthority());
        builder.encodedPath(uri.getPath());
        return builder.build().toString();
    }

    /**
     * Returns true if the mime type is a standard image mime type
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    /**
     * Returns true if the mime type is a standard audio mime type
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean isAudio(String mimeType) {
        return mimeType != null && mimeType.startsWith("audio/");
    }

    /**
     * @deprecated Use {@link ( String )}
     * Returns true if the mime type is a standard video mime type
     */
    public static boolean isVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith("video/");
    }

    /**
     * @param context Context object
     * @param uri     Media content uri of the image, audio or video resource
     * @deprecated This is a monster that will lead to OutOfMemory exception some day and the world
     * will come to an end.
     * Gets the media data from the one of the following media {@link android.content.ContentProvider} This method
     * should not be called from the main thread of the application. Calling this method may have
     * performance issues as this may allocate a huge memory array.
     * <ul>
     * <li>{@link Media}</li>
     * <li>{@link MediaStore.Audio.Media}</li>
     * <li>{@link Video.Media}</li>
     * </ul>
     */
    @Nullable
    public static byte[] getMediaData(Context context, Uri uri) {
        if (uri == null) {
            throw new NullPointerException("Uri cannot be null");
        }

        Cursor cursor = context.getContentResolver().query(uri, new String[]{Media.DATA},
                null, null, null);
        byte[] data = null;

        try {
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(Media.DATA));

                    try {
                        File file = new File(path);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        data = readStreamToBytes(fileInputStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Log.v( TAG, "#getVideoData byte.size: " + data.length );
                } // end while
            } else {
                Log.e(TAG, "#getMediaData cur is null or blank");
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return data;
    }

    /**
     * Convert {@linkplain InputStream} to byte array.
     *
     * @throws NullPointerException If input parameter {@link InputStream} is null
     **/
    @SuppressWarnings("WeakerAccess")
    public static byte[] readStreamToBytes(InputStream inputStream) {

        if (inputStream == null) {
            throw new NullPointerException("InputStream is null");
        }

        byte[] bytesData = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            bytesData = buffer.toByteArray();

            // Log.d( TAG, "#readStream data: " + data );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (reader != null) {
                try {
                    reader.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }    // finally

        return bytesData;
    }

    /**
     * @param mediaUri uri to the media resource. For e.g. content://media/external/images/media/45490 or
     *                 content://media/external/video/media/45490
     * @return Size in bytes
     * @deprecated Use {@link ( Context , Uri )}
     * Gets the size of the media resource pointed to by the paramter mediaUri.
     * <p/>
     * Known bug: for unknown reason, the image size for some images was found to be 0
     **/
    public static long getMediaSize(Context context, Uri mediaUri) {
        Cursor cur = context.getContentResolver().query(mediaUri, new String[]{Media.SIZE},
                null, null, null);
        long size = -1;

        try {
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    size = cur.getLong(cur.getColumnIndex(Media.SIZE));

                    // for unknown reason, the image size for image was found to
                    // be 0
                    // Log.v( TAG, "#getSize byte.size: " + size );

                    if (size == 0)
                        Log.w(TAG, "#getSize The media size was found to be 0. Reason: UNKNOWN");

                } // end while
            } else {
                assert cur != null;
                if (cur.getCount() == 0) {
                    Log.e(TAG, "#getMediaSize cur size is 0. File may not exist");
                } else {
                    Log.e(TAG, "#getMediaSize cur is null");
                }
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return size;
    }

    /**
     * @deprecated {@link Context , Uri)}
     * Gets media file name.
     **/
    public static String getMediaFileName(Context ctx, Uri mediaUri) {
        String colName = MediaColumns.DISPLAY_NAME;
        Cursor cur = ctx.getContentResolver().query(mediaUri, new String[]{colName},
                null, null, null);
        String dispName = null;

        try {
            if (cur != null && cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    dispName = cur.getString(cur.getColumnIndex(colName));

                    // for unknown reason, the image size for image was found to
                    // be 0
                    // Log.v( TAG, "#getMediaFileName byte.size: " + size );

                    if (TextUtils.isEmpty(colName)) {
                        Log.w(TAG, "#getMediaFileName The file name is blank or null. Reason: UNKNOWN");
                    }

                } // end while
            } else if (cur != null && cur.getCount() == 0) {
                Log.e(TAG, "#getMediaFileName File may not exist");
            } else {
                Log.e(TAG, "#getMediaFileName cur is null");
            }
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return dispName;
    }

    /**
     * @deprecated Use
     * Gets media type from the Uri.
     */
    @SuppressWarnings("unused")
    @Nullable
    public static String getMediaType(Uri uri) {
        if (uri == null) {
            return null;
        }

        String uriStr = uri.toString();

        if (uriStr.contains("video")) {
            return "video";
        } else if (uriStr.contains("audio")) {
            return "audio";
        } else if (uriStr.contains("image")) {
            return "image";
        } else {
            return null;
        }
    }

    /**
     * @param sourceText String to be converted to bold.
     * @deprecated Use {@link #toBold(String, String)}
     * Returns {@link android.text.SpannableString} in Bold typeface
     */
    public static SpannableStringBuilder toBold(String sourceText) {

        if (sourceText == null) {
            throw new NullPointerException("String to convert cannot be bold");
        }

        final SpannableStringBuilder sb = new SpannableStringBuilder(sourceText);

        // Span to set text color to some RGB value
        final StyleSpan bss = new StyleSpan(Typeface.BOLD);

        // set text bold
        sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }

    /**
     * Typefaces the string as bold.
     * If sub-string is null, entire string will be typefaced as bold and returned.
     *
     * @param string    string path
     * @param subString The subString within the string to bold. Pass null to bold entire string.
     * @return {@link android.text.SpannableString}
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public static SpannableStringBuilder toBold(String string, String subString) {
        if (TextUtils.isEmpty(string)) {
            return new SpannableStringBuilder("");
        }

        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(string);

        StyleSpan bss = new StyleSpan(Typeface.BOLD);
        if (subString != null) {
            int substringNameStart = string.toLowerCase().indexOf(subString);
            if (substringNameStart > -1) {
                spannableBuilder.setSpan(bss, substringNameStart, substringNameStart + subString.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else {
            // set entire text to bold
            spannableBuilder.setSpan(bss, 0, spannableBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return spannableBuilder;
    }

    /**
     * Formats given size in bytes to KB, MB, GB or whatever. This will work up to 1000 TB
     */
    @SuppressWarnings("unused")
    public static String formatSize(long size) {

        if (size <= 0) return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    /**
     * Formats given size in bytes to KB, MB, GB or whatever. Preferably use this method for
     * performance efficiency.
     *
     * @param si Controls byte value precision. If true, formatting is done using approx. 1000 Uses
     *           1024 if false.
     **/
    @SuppressLint("DefaultLocale")
    @SuppressWarnings("unused")
    public static String formatSize(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;

        if (bytes < unit) {
            return bytes + " B";
        }

        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Creates the uri to a file located on external storage or application internal storage.
     */
    @SuppressWarnings("unused")
    public static Uri createUri(Context ctx) {
        File root = getStorageDirectory(ctx, null);
        //noinspection ResultOfMethodCallIgnored
        root.mkdirs();
        File file = new File(root, Long.toString(new Date().getTime()));

        return Uri.fromFile(file);
    }

    /**
     * @param ctx               context
     * @param savingUri         uri
     * @param durationInSeconds duration in seconds
     * @return Creates an intent to take a video from camera or gallery or any other application that can
     * handle the intent.
     */
    @SuppressWarnings("unused")
    public static Intent createTakeVideoIntent(Activity ctx, Uri savingUri, int durationInSeconds) {

        if (savingUri == null) {
            throw new NullPointerException("Uri cannot be null");
        }

        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        final PackageManager packageManager = ctx.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, savingUri);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, durationInSeconds);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("video/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        return chooserIntent;
    }

    /**
     * @param savingUri Uri to store a high resolution image at. If the user takes the picture using the
     *                  camera the image will be stored at this uri.
     *                  Creates a ACTION_IMAGE_CAPTURE photo & ACTION_GET_CONTENT intent. This intent will be
     *                  aggregation of intents required to take picture from Gallery and Camera at the minimum. The
     *                  intent will also be directed towards the apps that are capable of sourcing the image data.
     *                  For e.g. Dropbox, Astro file manager.
     **/
    @SuppressWarnings("unused")
    public static Intent createTakePictureIntent(Activity ctx, Uri savingUri) {

        if (savingUri == null) {
            throw new NullPointerException("Uri cannot be null");
        }

        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = ctx.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, savingUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        return chooserIntent;
    }


    /**
     * @deprecated Use
     * Creates external content:// scheme uri to save the images at. The image saved at this
     * {@link Uri} will be visible via the gallery application on the device.
     */
    @SuppressWarnings("unused")
    @Nullable
    public static Uri createImageUri(Context ctx) throws IOException {

        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }

        //noinspection UnusedAssignment
        Uri imageUri = null;

        ContentValues values = new ContentValues();
        values.put(MediaColumns.TITLE, "");
        values.put(ImageColumns.DESCRIPTION, "");
        imageUri = ctx.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);

        return imageUri;
    }

    /**
     * @deprecated Use
     * Creates external content:// scheme uri to save the videos at.
     */
    @SuppressWarnings("unused")
    @Nullable
    public static Uri createVideoUri(Context ctx) throws IOException {

        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }

        Uri imageUri;

        ContentValues values = new ContentValues();
        values.put(MediaColumns.TITLE, "");
        values.put(ImageColumns.DESCRIPTION, "");
        imageUri = ctx.getContentResolver().insert(Video.Media.EXTERNAL_CONTENT_URI, values);

        return imageUri;
    }

    /**
     * @param firstName First name
     * @param lastName  Last name
     * @return Returns correctly formatted full name. Returns null if both the values are null.
     * @deprecated Use {#setTextValues} or {#getNullEmptyCheckedValue}
     * Get the correctly appended name from the given name parameters
     **/
    @Nullable
    public static String getName(String firstName, String lastName) {
        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)) {
            return firstName + " " + lastName;
        } else if (!TextUtils.isEmpty(firstName)) {
            return firstName;
        } else if (!TextUtils.isEmpty(lastName)) {
            return lastName;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static Bitmap roundBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    /**
     * Checks if given url is a relative path.
     *
     * @param url url
     * @return false if parameter url is null or false
     */
    @SuppressWarnings("unused")
    public static boolean isRelativeUrl(String url) {

        if (TextUtils.isEmpty(url)) {
            return false;
        }

        Uri uri = Uri.parse(url);

        return uri.getScheme() == null;
    }

    /**
     * Checks if the parameter {@link Uri} is a content uri.
     **/
    @SuppressWarnings("unused")
    public static boolean isContentUri(Uri uri) {
        return uri.toString().contains("content://");
    }

    /**
     * Hides the already popped up keyboard from the screen.
     *
     * @param context context
     */
    @SuppressWarnings("unused")
    public static void hideKeyboard(Context context) {
        try {
            // use application level context to avoid unnecessary leaks.
            InputMethodManager inputManager = (InputMethodManager) context.getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            //noinspection ConstantConditions
            inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            Log.e(TAG, "Sigh, cant even hide keyboard " + e.getMessage());
        }
    }

    /**
     * Checks if the build version passed as the parameter is
     * lower than the current build version.
     *
     * @param buildVersion One of the values from {@link Build.VERSION_CODES}
     * @return boolean
     */
    @SuppressWarnings("unused")
    public static boolean isBuildBelow(int buildVersion) {
        return Build.VERSION.SDK_INT < buildVersion;
    }

    /**
     * Sets the two parameter values to the parameter {@link TextView}
     * in null-safe and empty-safe way. Such method can be used when setting firstname-lastname
     * to a textview in the UI.
     *
     * @param textView    text view
     * @param firstValue  String "null" will be treated as null value.
     * @param secondValue String "null" will be treated as null value.
     */
    @SuppressWarnings("unused")
    public static void setTextValues(@NonNull TextView textView, @Nullable String firstValue,
                                     @Nullable String secondValue) {
        String nullEmptyCheckedVal = getNullEmptyCheckedValue(firstValue, secondValue, null);
        textView.setText(nullEmptyCheckedVal);
    }

    /**
     * Returns concatenated values of atleast to or more strings provided to it
     * in a null safe manner.
     *
     * @param firstValue  first val
     * @param secondValue second val
     * @param delimiter   Delimiter to be used to concatnate the parameter strings.
     *                    If null, space characer will be used.
     * @param moreValues  Optional
     * @return String
     */
    @SuppressWarnings("WeakerAccess")
    public static String getNullEmptyCheckedValue(@Nullable String firstValue,
                                                  @Nullable String secondValue,
                                                  @SuppressWarnings("SameParameterValue") @Nullable
                                                          String delimiter, String... moreValues) {
        if (TextUtils.isEmpty(delimiter)) {
            delimiter = " ";
        }

        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(firstValue) && !firstValue.equalsIgnoreCase("null")
                && secondValue != null && !secondValue.equalsIgnoreCase("null")) {
            builder.append(firstValue);
            builder.append(delimiter);
            builder.append(secondValue);
        } else if (!TextUtils.isEmpty(firstValue) && !firstValue.equalsIgnoreCase("null")) {
            builder.append(firstValue);
        } else if (!TextUtils.isEmpty(secondValue) && !secondValue.equalsIgnoreCase("null")) {
            builder.append(secondValue);
        }

        if (moreValues != null) {
            for (String value : moreValues) {
                if (!TextUtils.isEmpty(value) && !value.equalsIgnoreCase("null")) {
                    builder.append(delimiter);
                    builder.append(value);
                }
            }
        }

        return builder.toString();
    }

    /**
     * Partially capitalizes the string from paramter start and offset.
     *
     * @param string String to be formatted
     * @param start  Starting position of the substring to be capitalized
     * @param offset Offset of characters to be capitalized
     * @return String
     */
    @SuppressWarnings("unused")
    @Nullable
    public static String capitalizeString(String string, int start, int offset) {
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return string.substring(start, offset).toUpperCase()
                + string.substring(offset, string.length());
    }

    /**
     * Generates SHA-512 hash for given binary data.
     *
     * @param stringToHash string to hash
     * @return String
     */
    @SuppressWarnings("unused")
    @Nullable
    public static String getSha512Hash(String stringToHash) {
        if (stringToHash == null) {
            return null;
        } else {
            return getSha512Hash(stringToHash.getBytes());
        }
    }

    /**
     * Generates SHA-512 hash for given binary data.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public static String getSha512Hash(byte[] dataToHash) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (md != null) {
            md.update(dataToHash);
            byte byteData[] = md.digest();
            return Base64.encodeToString(byteData, Base64.DEFAULT);
        }
        return null;
    }

    /**
     * Gets the extension of a file.
     */
    @SuppressWarnings("unused")
    @Nullable
    public static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }

        return ext;
    }

    /**
     * Method to parse the date and time
     *
     * @param dateString       input date string
     * @param outputDateFormat required output string format
     * @param inputDateFormat  input string format
     * @return String formated date time value
     */
    @SuppressLint("SimpleDateFormat")
    public static String getFormattedDateTime(@NonNull String dateString,
                                              @NonNull String outputDateFormat,
                                              @NonNull String inputDateFormat) {
        try {
            return new SimpleDateFormat(outputDateFormat).format(
                    new SimpleDateFormat(inputDateFormat).parse(dateString));
        } catch (NullPointerException | java.text.ParseException e) {
//            e.printStackTrace();
            return dateString;
        }
    }

    /**
     * Method to parse the date and time
     *
     * @param outputDateFormat required output string format
     * @return String formated date time value
     */
    @SuppressLint("SimpleDateFormat")
    public static String getFormattedDateTime(@NonNull Date date,
                                              @NonNull String outputDateFormat) {
        return new SimpleDateFormat(outputDateFormat).format(date);
    }

    /**
     * Method to parse the date and time
     *
     * @param date            date string
     * @param inputDateFormat date string format
     * @return String formated date time value
     */
    @SuppressLint("SimpleDateFormat")
    public static Date getFormattedDateTime(@NonNull String date,
                                            @NonNull String inputDateFormat) {
        try {
            return new SimpleDateFormat(inputDateFormat).parse(date);
        } catch (java.text.ParseException ignored) {
            return new Date();
        }
    }

    /**
     * Method to parse the date and time
     *
     * @param dateStart       date string
     * @param dateEnd         date string
     * @param inputDateFormat date string format
     * @return String formated date time value
     */
    @SuppressWarnings("unused")
    @SuppressLint("SimpleDateFormat")
    public static String getDifference(@SuppressWarnings("unused") @NonNull String dateStart,
                                       @NonNull String dateEnd,
                                       @NonNull String inputDateFormat) {
//        Date dateStartTime = getFormattedDateTime(dateStart, inputDateFormat);
        Date dateStartTime = new Date();
        Date dateEndTime = getFormattedDateTime(dateEnd, inputDateFormat);
        if (dateStartTime.before(dateEndTime)) {
            int expiryTime = Math.round((dateEndTime.getTime() - dateStartTime.getTime())
                    / (1000 * 60 * 60)); //hours


            StringBuilder stringBuilder = new StringBuilder();
            if (expiryTime / 23 > 99) {
                return "100 or more days left";
            } else if (expiryTime / 23 > 0) {
                stringBuilder.append(expiryTime / 23).append(expiryTime / 23 > 1 ? " days " : " day ");
            }
            stringBuilder.append(expiryTime % 23).append(expiryTime % 23 > 1 ? " hours " : " hour ");
            //stringBuilder.append(timeInMillis);
            return stringBuilder.toString();
        } else {
            return "task expired";
        }
    }

//    public static void setTimer(int timeInMillis){
//        new CountDownTimer(timeInMillis, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//               // holder.binding.tvExpireTime.setText(" " + millisUntilFinished / 1000);
//                //here you can have your logic to set text to edittext
//            }
//
//            public void onFinish() {
//                //holder.binding.tvExpireTime.setText("done!");
//            }
//
//        }.start();
//    }


    /**
     * Method to generate the new key for the file to be uploaded on S3 Amazon
     *
     * @param type file type
     * @return unique key
     */

    public static String generateNewKey(@SuppressWarnings("SameParameterValue") String type) {
        return "android" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + type;
    }

    /**
     * API for generating Thumbnail from particular time frame
     *
     * @param filePath      - video file path
     * @param timeInSeconds - thumbnail to generate at time
     * @return thhumbnail bitmap
     */
    @SuppressWarnings("unused")
    public static Bitmap createThumbnailAtTime(String filePath, int timeInSeconds) {
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(filePath);
        //api time unit is microseconds
        return mMMR.getFrameAtTime(timeInSeconds * 1000000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }


    @SuppressWarnings("WeakerAccess")
    public static void dismissComingSoonDialog() {
        if (dialogComingSoon != null && dialogComingSoon.isShowing()) dialogComingSoon.dismiss();
    }


    public static long getDifferenceInMilis(String endDate, String inputFormat)
            throws java.text.ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
        return sdf.parse(endDate).getTime() - new Date().getTime();
    }

    public static void setClipBoardText(Context context, String text) {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
    }

    public static Date getDateFromCalendarParams(int year, int month, int day) {
        return new Date(year - 1900, month, day); //year+1900 is set by the date jdk 1.1
    }

    /**
     * Method to force logout the user out of the app
     *
     * @param context activity context
     */


    /**
     * Method to compare the two dates regardless of time
     *
     * @param createdDate  date 1
     * @param createdDate1 date 2
     * @return true of ddMMyyyy of date 1 not equals with the date 2
     */
    public static boolean compareDates(String createdDate, String createdDate1) {
        return !AppUtils.getFormattedDateTime(
                createdDate, "ddMMyyyy", SERVER_TIME_FORMAT)
                .equalsIgnoreCase(AppUtils.getFormattedDateTime(
                        createdDate1, "ddMMyyyy", SERVER_TIME_FORMAT));
    }

    public static void openPlayStore(Activity mActivity, String appPackageName) {
        try {
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    /**
     * Method to get the formatted date and time
     *
     * @param milis input milis
     * @return formated date time
     */
    @SuppressLint("DefaultLocale")
    public static String getFormatedCounter(long milis) {
        long hrs = 0, minutes = 0;
        StringBuilder countDownTime = new StringBuilder();

        if (milis > 0) {
            String days = String.format("%s",
                    TimeUnit.MILLISECONDS.toDays(milis));

            if (!days.equalsIgnoreCase("0")) {
                countDownTime.append(days).append("d ");
            }

            //show hours always when there is a day
            if (!(TimeUnit.MILLISECONDS.toHours(milis) - TimeUnit.DAYS.toHours(
                    TimeUnit.MILLISECONDS.toDays(milis)) == 0) || !days.equalsIgnoreCase("0")) {
                hrs = TimeUnit.MILLISECONDS.toHours(milis) - TimeUnit.DAYS.toHours(
                        TimeUnit.MILLISECONDS.toDays(milis));
                countDownTime.append(String.format("%dh", hrs))
                        .append(" ");
            }

            //show minutes always
            if (!(TimeUnit.MILLISECONDS.toMinutes(milis) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(milis)) == 0) || hrs > 0) {
                minutes = TimeUnit.MILLISECONDS.toMinutes(milis) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(milis));
                countDownTime.append(String.format("%dm", minutes))
                        .append(" ");
            }

            //show seconds always
            if (!(TimeUnit.MILLISECONDS.toSeconds(milis) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(milis)) == 0) || minutes > 0) {
                countDownTime.append(String.format("%ds",
                        TimeUnit.MILLISECONDS.toSeconds(milis) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(milis))));
            }

            return countDownTime.toString();
        }
        return "";
    }

    /**
     * Method to share or generate the file name for the download destination
     *
     * @param filename filename
     * @return uri of the destination file
     */
    public static Uri getFileDestination(String filename) {
        return Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), filename));
//        return Uri.parse("file://" + "Downloads/ipac/" + "/" + filename);
    }

    /**
     * Method to get the file name from the post url
     *
     * @param postUrl post url
     * @return filename
     */
    public static String getFileNameFromUrl(String postUrl) {
        return URLUtil.guessFileName(postUrl, null, null);
//        return postUrl.substring(postUrl.lastIndexOf('/') + 1);
    }


    @SuppressWarnings("unused")
    public static int convertDpToPx(int dp) {
        return Math.round(dp * (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    @SuppressWarnings("unused")
    public static int convertPxToDp(int px) {
        return Math.round(px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @SuppressWarnings("unused")
    public static float convertPixelsToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    @SuppressWarnings("unused")
    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }


    public static void share(Activity activity, StringBuilder desc, int image) {
        ShareCompat.IntentBuilder
                .from(activity)
                .setText(desc.toString())
                .setStream(getUri(activity, image))
                .setType("text/plain")
                .startChooser();
    }

    public static Uri getUri(Activity mActivity, int asset) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(), asset, options);
            File file = new File(mActivity.getCacheDir(), "ShareImage" + ".png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            return FileProvider.getUriForFile(mActivity, BillingReadingApplication.getInstance().getApplicationContext().getPackageName() + ".provider", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return true;
        } catch (Exception ignored) {

        }
        return false;
    }

    public static String readFileAsBase64String(String path) {
        try {
            InputStream is = new FileInputStream(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Base64OutputStream b64os = new Base64OutputStream(baos, Base64.DEFAULT);
            byte[] buffer = new byte[8192];
            int bytesRead;
            try {
                while ((bytesRead = is.read(buffer)) > -1) {
                    b64os.write(buffer, 0, bytesRead);
                }
                return baos.toString();
            } catch (IOException e) {
                Log.e("TAG", "Cannot read file " + path, e);
                // Or throw if you prefer
                return "";
            } finally {
                closeQuietly(is);
                closeQuietly(b64os); // This also closes baos
            }
        } catch (FileNotFoundException e) {
            Log.e("TAG", "File not found " + path, e);
            // Or throw if you prefer
            return "";
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
        }
    }


    public static class UsageSpinnerClass implements AdapterView.OnItemSelectedListener {
        Context mContext;
        UsageSpinnerCallback usageSpinnerCallback;
        String sortedValue = "";

        public UsageSpinnerClass(Context context, UsageSpinnerCallback usageSpinnerCallback) {
            mContext = context;
            this.usageSpinnerCallback = usageSpinnerCallback;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            if (mContext.getResources().getStringArray(R.array.usage)[position].equalsIgnoreCase("Shop")) {
                sortedValue = "SP";
            } else if (mContext.getResources().getStringArray(R.array.usage)[position].equalsIgnoreCase("House")) {
                sortedValue = "HO";
            } else if (mContext.getResources().getStringArray(R.array.usage)[position].equalsIgnoreCase("Factory")) {
                sortedValue = "FA";
            } else if (mContext.getResources().getStringArray(R.array.usage)[position].equalsIgnoreCase("Godown")) {
                sortedValue = "GO";
            } else if (mContext.getResources().getStringArray(R.array.usage)[position].equalsIgnoreCase("Industry")) {
                sortedValue = "IN";
            } else if (mContext.getResources().getStringArray(R.array.usage)[position].equalsIgnoreCase("Other")) {
                sortedValue = "OT";
            } else if (mContext.getResources().getStringArray(R.array.usage)[position].equalsIgnoreCase("Religious Place")) {
                sortedValue = "RS";
            } else if (mContext.getResources().getStringArray(R.array.usage)[position].equalsIgnoreCase("School")) {
                sortedValue = "SL";
            } else if (mContext.getResources().getStringArray(R.array.usage)[position].equalsIgnoreCase("Hospital")) {
                sortedValue = "HL";
            } else if (mContext.getResources().getStringArray(R.array.usage)[position].equalsIgnoreCase("Institution")) {
                sortedValue = "INS";
            }

            usageSpinnerCallback.usageSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.usage)[position], sortedValue);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class StatusSpinnerClass implements AdapterView.OnItemSelectedListener {
        Context mContext;
        SupplyStatusSpinnerCallback supplyStatusSpinnerCallback;
        String sortedValue = "";

        public StatusSpinnerClass(Context context, SupplyStatusSpinnerCallback supplyStatus) {
            mContext = context;
            supplyStatusSpinnerCallback = supplyStatus;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            if (mContext.getResources().getStringArray(R.array.supplyStatus)[position].equalsIgnoreCase("Less In Use")) {
                sortedValue = "LU";
            } else if (mContext.getResources().getStringArray(R.array.supplyStatus)[position].equalsIgnoreCase("In Use")) {
                sortedValue = "IU";
            } else if (mContext.getResources().getStringArray(R.array.supplyStatus)[position].equalsIgnoreCase("Not In Use")) {
                sortedValue = "NU";
            }

            supplyStatusSpinnerCallback.supplyStatusSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.supplyStatus)[position], sortedValue);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class MeterTypeSpinnerClass implements AdapterView.OnItemSelectedListener {
        MeterTypeSpinnerCallback meterTypeSpinnerCallback;
        String sortedValue = "";

        Context mContext;

        public MeterTypeSpinnerClass(Context context, MeterTypeSpinnerCallback meterTypeSpinnerCallback) {
            mContext = context;
            this.meterTypeSpinnerCallback = meterTypeSpinnerCallback;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            if (mContext.getResources().getStringArray(R.array.meterType)[position].equalsIgnoreCase("Electronic")) {
                sortedValue = "EL";
            } else if (mContext.getResources().getStringArray(R.array.meterType)[position].equalsIgnoreCase("Counter")) {
                sortedValue = "CO";
            } else if (mContext.getResources().getStringArray(R.array.meterType)[position].equalsIgnoreCase("Mechanical")) {
                sortedValue = "ME";
            } else if (mContext.getResources().getStringArray(R.array.meterType)[position].equalsIgnoreCase("Smart Meter")) {
                sortedValue = "SE";
            }


            meterTypeSpinnerCallback.meterTypeSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.meterType)[position], sortedValue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }


    public static class MeterReasonCDSpinnerClass implements AdapterView.OnItemSelectedListener {

        private Context mContext;
        private ReasonOfCdSpinnerCallback reasonOfCdSpinnerCallbacks;
        String sortedValue = "";

        public MeterReasonCDSpinnerClass(Context context, ReasonOfCdSpinnerCallback reasonOfCdSpinnerCallback) {
            mContext = context;
            reasonOfCdSpinnerCallbacks = reasonOfCdSpinnerCallback;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            if (mContext.getResources().getStringArray(R.array.reasonofCD)[position].equalsIgnoreCase("Freedom Unit Damage")) {
                sortedValue = "FUD";
            } else if (mContext.getResources().getStringArray(R.array.reasonofCD)[position].equalsIgnoreCase("Service line broken")) {
                sortedValue = "SLB";
            } else if (mContext.getResources().getStringArray(R.array.reasonofCD)[position].equalsIgnoreCase("Others")) {
                sortedValue = "OTH";
            }

            reasonOfCdSpinnerCallbacks.reasonOfCdSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.reasonofCD)[position], sortedValue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class MeterReasonENSpinnerClass implements AdapterView.OnItemSelectedListener {
        private Context mContext;
        private ReasonOfENSpinnerCallback reasonOfENSpinnerCallbacks;
        String sortedValue = "";

        public MeterReasonENSpinnerClass(Context context, ReasonOfENSpinnerCallback reasonOfENSpinnerCallback) {
            mContext = context;
            reasonOfENSpinnerCallbacks = reasonOfENSpinnerCallback;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            if (mContext.getResources().getStringArray(R.array.reasonofEN)[position].equalsIgnoreCase("No male member at home")) {
                sortedValue = "NMMH";
            } else if (mContext.getResources().getStringArray(R.array.reasonofEN)[position].equalsIgnoreCase("Recently Reading done")) {
                sortedValue = "RRD";
            } else if (mContext.getResources().getStringArray(R.array.reasonofEN)[position].equalsIgnoreCase("Key not available")) {
                sortedValue = "KNA";
            } else if (mContext.getResources().getStringArray(R.array.reasonofEN)[position].equalsIgnoreCase("Late evening reading not allowed")) {
                sortedValue = "LERNA";
            } else if (mContext.getResources().getStringArray(R.array.reasonofEN)[position].equalsIgnoreCase("Suspected Consumer")) {
                sortedValue = "SC";
            } else if (mContext.getResources().getStringArray(R.array.reasonofEN)[position].equalsIgnoreCase("Other")) {
                sortedValue = "OTH";
            }


            reasonOfENSpinnerCallbacks.reasonOfENSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.reasonofEN)[position], sortedValue);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class SourceSupplySpinnerClass implements AdapterView.OnItemSelectedListener {
        private Context mContext;
        private SourceSupplySpinnerCallback sourceSupplySpinnerCallback;
        String sortedValue = "";

        public SourceSupplySpinnerClass(Context context, SourceSupplySpinnerCallback sourceSupplySpinnerCallbacks) {
            mContext = context;
            sourceSupplySpinnerCallback = sourceSupplySpinnerCallbacks;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            if (mContext.getResources().getStringArray(R.array.sourceofsupply)[position].equalsIgnoreCase("Supply Bypass by zone")) {
                sortedValue = "SBZ";
            } else if (mContext.getResources().getStringArray(R.array.sourceofsupply)[position].equalsIgnoreCase("Other Meter")) {
                sortedValue = "OMT";
            } else if (mContext.getResources().getStringArray(R.array.sourceofsupply)[position].equalsIgnoreCase("No SUpply")) {
                sortedValue = "NSP";
            } else if (mContext.getResources().getStringArray(R.array.sourceofsupply)[position].equalsIgnoreCase("Direct Theft from bus bar")) {
                sortedValue = "DTB";
            } else if (mContext.getResources().getStringArray(R.array.sourceofsupply)[position].equalsIgnoreCase("Direct Theft from Pole")) {
                sortedValue = "DTP";
            }

            sourceSupplySpinnerCallback.sourceSupplySpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.sourceofsupply)[position], sortedValue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class PaperPasteSpinnerClass implements AdapterView.OnItemSelectedListener {
        private PaperPasteSpinnerCallback paperPasteSpinnerCallback;
        private Context mContext;
        String sortedValue = "";

        public PaperPasteSpinnerClass(Context context, PaperPasteSpinnerCallback paperPasteSpinnerCallback) {
            this.paperPasteSpinnerCallback = paperPasteSpinnerCallback;
            mContext = context;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            if (mContext.getResources().getStringArray(R.array.paperpastedby)[position].equalsIgnoreCase("Zone")) {
                sortedValue = "ZONE";
            } else if (mContext.getResources().getStringArray(R.array.paperpastedby)[position].equalsIgnoreCase("EAC")) {
                sortedValue = "EAC";
            } else if (mContext.getResources().getStringArray(R.array.paperpastedby)[position].equalsIgnoreCase("CEG")) {
                sortedValue = "CEG";
            } else if (mContext.getResources().getStringArray(R.array.paperpastedby)[position].equalsIgnoreCase("Others")) {
                sortedValue = "OTH";
            }

            paperPasteSpinnerCallback.paperPasteSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.paperpastedby)[position], sortedValue);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class MeterStuckSpinnerClass implements AdapterView.OnItemSelectedListener {
        private Context mContext;
        private MeterStucksSpinnerCallback meterStucksSpinnerCallback;
        String sortedValue = "";


        public MeterStuckSpinnerClass(Context context, MeterStucksSpinnerCallback meterStucksSpinnerCallback) {
            this.meterStucksSpinnerCallback = meterStucksSpinnerCallback;
            mContext = context;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            if (mContext.getResources().getStringArray(R.array.meterstuck)[position].equalsIgnoreCase("Low Voltage")) {
                sortedValue = "LV";
            } else if (mContext.getResources().getStringArray(R.array.meterstuck)[position].equalsIgnoreCase("888888")) {
                sortedValue = "88";
            } else if (mContext.getResources().getStringArray(R.array.meterstuck)[position].equalsIgnoreCase("Tamper")) {
                sortedValue = "TP";
            } else if (mContext.getResources().getStringArray(R.array.meterstuck)[position].equalsIgnoreCase("Stop at MDI")) {
                sortedValue = "SAM";
            } else if (mContext.getResources().getStringArray(R.array.meterstuck)[position].equalsIgnoreCase("Blank Display")) {
                sortedValue = "BD";
            } else if (mContext.getResources().getStringArray(R.array.meterstuck)[position].equalsIgnoreCase("Others")) {
                sortedValue = "OTH";
            }

            meterStucksSpinnerCallback.meterStuckSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.meterstuck)[position], sortedValue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class MeterSealSpinnerClass implements AdapterView.OnItemSelectedListener {

        private Context mContext;
        private MeterSealSpinnerCallback meterSealSpinnerCallback;
        String sortedValue = "";

        public MeterSealSpinnerClass(Context mContext, MeterSealSpinnerCallback meterSealSpinnerCallback) {
            this.mContext = mContext;
            this.meterSealSpinnerCallback = meterSealSpinnerCallback;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            if (mContext.getResources().getStringArray(R.array.sealstatus)[position].equalsIgnoreCase("Seal Broken")) {
                sortedValue = "SB";
            } else if (mContext.getResources().getStringArray(R.array.sealstatus)[position].equalsIgnoreCase("No Seal")) {
                sortedValue = "NS";
            }


            meterSealSpinnerCallback.meterSealSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.sealstatus)[position], sortedValue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class MeterObstacleSpinnerClass implements AdapterView.OnItemSelectedListener {
        private Context mContext;
        private MeterObstacleSpinnerCallback meterObstacleSpinnerCallback;
        String sortedValue = "";

        public MeterObstacleSpinnerClass(Context mContext, MeterObstacleSpinnerCallback meterObstacleSpinnerCallback) {
            this.mContext = mContext;
            this.meterObstacleSpinnerCallback = meterObstacleSpinnerCallback;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {


            if (mContext.getResources().getStringArray(R.array.typeofobstacle)[position].equalsIgnoreCase("Boxes blocked the way")) {
                sortedValue = "BBTW";
            }

            meterObstacleSpinnerCallback.meterObstacleSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.typeofobstacle)[position], sortedValue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class MeterHeightSpinnerClass implements AdapterView.OnItemSelectedListener {
        private Context mContext;
        private MeterHeightSpinnerCallback meterHeightSpinnerCallback;
        String sortedValue = "";

        public MeterHeightSpinnerClass(Context mContext, MeterHeightSpinnerCallback meterHeightSpinnerCallback) {
            this.mContext = mContext;
            this.meterHeightSpinnerCallback = meterHeightSpinnerCallback;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            if (mContext.getResources().getStringArray(R.array.meterheightfeet)[position].equalsIgnoreCase("8 to 12 ft")) {
                sortedValue = "8FT";
            } else if (mContext.getResources().getStringArray(R.array.meterheightfeet)[position].equalsIgnoreCase("12 to 16 ft")) {
                sortedValue = "12FT";
            } else if (mContext.getResources().getStringArray(R.array.meterheightfeet)[position].equalsIgnoreCase("16 to 20 ft")) {
                sortedValue = "16FT";
            } else if (mContext.getResources().getStringArray(R.array.meterheightfeet)[position].equalsIgnoreCase("More Than 20 ft")) {
                sortedValue = "20FT";
            }


            meterHeightSpinnerCallback.meterHeightSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.meterheightfeet)[position], sortedValue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class MeterReasonNVSpinnerClass implements AdapterView.OnItemSelectedListener {
        private Context mContext;
        private MeterReasonNVSpinnerCallback meterReasonNVSpinnerCallback;
        String sortedValue = "";

        public MeterReasonNVSpinnerClass(Context mContext, MeterReasonNVSpinnerCallback meterReasonNVSpinnerCallback) {
            this.mContext = mContext;
            this.meterReasonNVSpinnerCallback = meterReasonNVSpinnerCallback;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            if (mContext.getResources().getStringArray(R.array.reasonofNV)[position].equalsIgnoreCase("Meter Drop inside the Box")) {
                sortedValue = "MDIB";
            } else if (mContext.getResources().getStringArray(R.array.reasonofNV)[position].equalsIgnoreCase("Box Dirty")) {
                sortedValue = "BD";
            } else if (mContext.getResources().getStringArray(R.array.reasonofNV)[position].equalsIgnoreCase("Sunlight")) {
                sortedValue = "SL";
            } else if (mContext.getResources().getStringArray(R.array.reasonofNV)[position].equalsIgnoreCase("Unsafe condition")) {
                sortedValue = "UC";
            } else if (mContext.getResources().getStringArray(R.array.reasonofNV)[position].equalsIgnoreCase("Unclear Glass")) {
                sortedValue = "UG";
            }

            meterReasonNVSpinnerCallback.meterReasonNVSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.reasonofNV)[position], sortedValue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class MeterReasonDCSpinnerClass implements AdapterView.OnItemSelectedListener {
        private Context mContext;
        private ReasonDCSpinnerCallback reasonDCSpinnerCallback;
        String sortedValue = "";

        public MeterReasonDCSpinnerClass(Context mContext, ReasonDCSpinnerCallback reasonDCSpinnerCallback) {
            this.mContext = mContext;
            this.reasonDCSpinnerCallback = reasonDCSpinnerCallback;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            if (mContext.getResources().getStringArray(R.array.reasonofDC)[position].equalsIgnoreCase("Unsafe Condition on Site")) {
                sortedValue = "UCAS";
            } else if (mContext.getResources().getStringArray(R.array.reasonofDC)[position].equalsIgnoreCase("Consumer not living in premise not in use")) {
                sortedValue = "CNL";
            } else if (mContext.getResources().getStringArray(R.array.reasonofDC)[position].equalsIgnoreCase("Site demolished")) {
                sortedValue = "SD";
            } else if (mContext.getResources().getStringArray(R.array.reasonofDC)[position].equalsIgnoreCase("Site under construction")) {
                sortedValue = "SUC";
            } else if (mContext.getResources().getStringArray(R.array.reasonofDC)[position].equalsIgnoreCase("Disconnected From pole")) {
                sortedValue = "DFP";
            } else if (mContext.getResources().getStringArray(R.array.reasonofDC)[position].equalsIgnoreCase("Disconnected From Meter/Bus Bar")) {
                sortedValue = "DFM";
            }

            reasonDCSpinnerCallback.reasonDCSSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.reasonofDC)[position], sortedValue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class ReasonPLSpinnerClass implements AdapterView.OnItemSelectedListener {
        private Context mContext;
        private ReasonPLSpinnerCallback reasonPLSpinnerCallback;
        String sortedValue = "";

        public ReasonPLSpinnerClass(Context mContext, ReasonPLSpinnerCallback reasonPLSpinnerCallback) {
            this.mContext = mContext;
            this.reasonPLSpinnerCallback = reasonPLSpinnerCallback;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            if (mContext.getResources().getStringArray(R.array.reasonPL)[position].equalsIgnoreCase("Appointment Fixed After 3 Days")) {
                sortedValue = "AFAD";
            } else if (mContext.getResources().getStringArray(R.array.reasonPL)[position].equalsIgnoreCase("Whatsapp Reading")) {
                sortedValue = "WAR";
            } else if (mContext.getResources().getStringArray(R.array.reasonPL)[position].equalsIgnoreCase("Reading  Tomorrow")) {
                sortedValue = "RT";
            } else if (mContext.getResources().getStringArray(R.array.reasonPL)[position].equalsIgnoreCase("Sat/Sun/Holiday/After 6 PM and Early Morning")) {
                sortedValue = "SSHEM";
            } else if (mContext.getResources().getStringArray(R.array.reasonPL)[position].equalsIgnoreCase("Tenant/ON Rent")) {
                sortedValue = "TR";
            } else if (mContext.getResources().getStringArray(R.array.reasonPL)[position].equalsIgnoreCase("Key not available")) {
                sortedValue = "KNA";
            } else if (mContext.getResources().getStringArray(R.array.reasonPL)[position].equalsIgnoreCase("Village/Out of resident")) {
                sortedValue = "VOR";
            } else if (mContext.getResources().getStringArray(R.array.reasonPL)[position].equalsIgnoreCase("Occasionally Open")) {
                sortedValue = "OO";
            } else if (mContext.getResources().getStringArray(R.array.reasonPL)[position].equalsIgnoreCase("Weekly off")) {
                sortedValue = "WO";
            } else if (mContext.getResources().getStringArray(R.array.reasonPL)[position].equalsIgnoreCase("Meter inside but wire disconnected from pole")) {
                sortedValue = "MIWD";
            } else if (mContext.getResources().getStringArray(R.array.reasonPL)[position].equalsIgnoreCase("Other")) {
                sortedValue = "OTH";
            }

            reasonPLSpinnerCallback.reasonPLSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.reasonPL)[position], sortedValue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class ReasonTLSpinnerClass implements AdapterView.OnItemSelectedListener {

        private Context mContext;
        private ReasonTLSpinnerCallback reasonTLSpinnerCallback;
        String sortedValue = "";

        public ReasonTLSpinnerClass(Context mContext, ReasonTLSpinnerCallback reasonTLSpinnerCallback) {
            this.mContext = mContext;
            this.reasonTLSpinnerCallback = reasonTLSpinnerCallback;
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            if (mContext.getResources().getStringArray(R.array.contactReason)[position].equalsIgnoreCase("Wrong Contact")) {
                sortedValue = "WC";
            } else if (mContext.getResources().getStringArray(R.array.contactReason)[position].equalsIgnoreCase("Out of Reach")) {
                sortedValue = "OR";
            } else if (mContext.getResources().getStringArray(R.array.contactReason)[position].equalsIgnoreCase("Switched OFF")) {
                sortedValue = "SO";
            }
            reasonTLSpinnerCallback.reasonTLSpinnerSelectedItem(position, mContext.getResources().getStringArray(R.array.contactReason)[position], sortedValue);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    public static class UploadImageTask extends AsyncTask<String, Void, String> {
        @SuppressLint("StaticFieldLeak")
        Context mContext;

        public UploadImageTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(context, "Please wait while uploading data", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return (new UploadImage().uploadFile(strings[0], strings[1]));
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d("OnPostExec:", s);
            if (s.equals("0")) {
                updateImageRow(0, mContext);
            } else {
                //  Toast.makeText(mContext, "Image failed to ic_baseline_cloud_upload_24", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private static void updateImageRow(int udpFlag, Context mContext) {
        ContentValues cv = new ContentValues();
        if (udpFlag == 1) {
           /* cv.put("MTR_IMAGE", imgconvert);
            cv.put("BILL_FLAG", 1);
            cv.put("UPD_FLAG1", 1);*/
        } else {
            cv.put("UPD_FLAG1", 0);
        }
     /*   databaseAccess.open();
        DatabaseAccess.database.update("MTR_NSBM_DATA",cv,"CONS_NO = '" + valcons_no + "'",null);*/
        //   String strUpdateSQL_01 = "UPDATE BILL_SBM_DATA SET MTR_IMAGE=  " +
        // " WHERE CONS_ACC = '" + AccountNum + "'";
        // DatabaseAccess.database.execSQL(strUpdateSQL_01);
        if (udpFlag == 1) {
            Toast.makeText(mContext, "Image Inserted Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Image uploaded to server successfully", Toast.LENGTH_SHORT).show();
        }

    }

    public static String getCommonSeperatedString(ArrayList<String> actionObjects) {

        StringBuilder sbString = new StringBuilder("");

        //iterate through ArrayList
        for (String language : actionObjects) {

            //append ArrayList element followed by comma
            sbString.append(language).append(",");
        }
        //convert StringBuffer to String
        String strList = sbString.toString();

        //remove last comma from String if you want
        if (strList.length() > 0)
            strList = strList.substring(0, strList.length() - 1);
        System.out.println("inserted value==" + strList);

        return strList;
    }

}