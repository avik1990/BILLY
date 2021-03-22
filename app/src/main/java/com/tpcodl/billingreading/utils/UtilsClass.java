package com.tpcodl.billingreading.utils;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.tpcodl.billingreading.BuildConfig;
import com.tpcodl.billingreading.broadcasts.DialogGPS;
import com.tpcodl.billingreading.broadcasts.DialogInternet;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.LOCATION_SERVICE;

public class UtilsClass {

    public static int CAPTURE_IMAGE_PATH = 0;
    public static boolean meterPhoto = false;
    public static boolean testerPhoto = false;
    public static boolean rriImage = false;
    public static boolean wlImage = false;


    public static void showToastShort(Context mContext, String msg) {
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public static void showToastLong(Context mContext, String msg) {
        Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static String getCurrentDate() {
        SimpleDateFormat parserSDF = new SimpleDateFormat("dd-MM-yyyyHH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        String formated_date = parserSDF.format(date);
        return formated_date;
    }


    public static String getFormateDate(String inputDate) {
        if (inputDate != null) {
            if (!inputDate.isEmpty()) {
                String startDateStrNewFormat = "";
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

                Date date = null;
                try {
                    date = inputFormat.parse(inputDate);
                    startDateStrNewFormat = outputFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return startDateStrNewFormat;
            }

        }
        return null;
    }

    public static boolean isvalidVersion(String appVersion, Context context) {
        boolean a = false;
        if (PreferenceHandler.getEnableBilling(context).equalsIgnoreCase("1")) {
            a = true;
        } else {
            try {
                if (appVersion == null) {
                    a = true;
                } else if (appVersion.equalsIgnoreCase("null")) {
                    a = true;
                } else if (appVersion.equals(BuildConfig.VERSION_NAME)) {
                    a = true;
                } else {
                    a = false;
                }
            } catch (Exception e) {
                a = true;
            }
        }

        return a;
    }





    /*public static String checkDateFormate(String inputDate){

        if (inputDate!=null){
            String startDateStrNewFormat="";
            DateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date date = null;
            try {
                date = inputFormat.parse(inputDate);
                startDateStrNewFormat   = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                startDateStrNewFormat=inputDate;
            }
            return startDateStrNewFormat;
        }
        return null;
    }*/

    public static String getFormattedDate(String normal_date) {
        String anni = normal_date;
        String formated_date = "";
        if (anni.length() > 6) {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date;
            try {
                date = originalFormat.parse(anni);
                formated_date = targetFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            formated_date = anni;
        }
        return formated_date;
    }

    public static boolean isConnected(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static boolean isValidNumber(String s) {
        Pattern p = Pattern.compile("(0/91)?[6-9][0-9]{9}");
        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

   /* public static String getMonthName(String monthNumber) {
        if (monthNumber != null) {
            System.out.println("scdfghu==" + monthNumber);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            cal.set(Calendar.MONTH, (Integer.parseInt(monthNumber) - 1));
            String month_name = month_date.format(cal.getTime());
            return month_name;
        }
        return null;
    }*/




   /* public static String getMonthNameMonthName(String monthNumber) {
        String month_name = "";
        if (monthNumber != null) {
            switch(month_name) {
                case "1":
                    month_name ="January";
                    break;
                case "2":
                    month_name = "February";
                    break;
                case "3":
                    month_name = "March";
                    break;
                case "4":
                    month_name = "April";
                    break;
                case "5":
                    month_name = "May";
                    break;
                case "6":
                    month_name = "June";
                    break;
                case "7":
                    month_name = "July";
                    break;
                case "8":
                    month_name = "August";
                    break;
                case "9":
                    month_name = "September";
                    break;
                case "10":
                    month_name = "October";
                    break;
                case "11":
                    month_name = "November";
                    break;
                case "12":
                    month_name = "December";
                    break;


            return month_name;
        }
        return null;
    }*/

    public static void hideKeyboard(View view) {
        try {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception var2) {
        }
    }

    public static String getDateFolder() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        String todaysDate = df.format(c);
        return todaysDate;
    }

    public static void checkGpsConnection(Context context) {

        if (isGpsConnected(context)) {
            if (DialogGPS.fa != null) {
                DialogGPS.fa.finish();
            }
        } else {
          /*  Intent intent1 = new Intent(context.getApplicationContext(), DialogGPS.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);*/
        }
    }

    public static void gpsListner(Context context) {

        if (isGpsConnected(context)) {
            if (DialogGPS.fa != null) {
                DialogGPS.fa.finish();
            }
        } else {
            Intent intent1 = new Intent(context.getApplicationContext(), DialogGPS.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }

    public static Boolean isGpsConnected(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }

    public static void internetListner(Context context) {
        if (isConnected(context)) {
            //DialogGPS.fa.finish();
            if (DialogInternet.fa != null) {
                DialogInternet.fa.finish();
            }
        } else {
            //showConnectivityPopup(context);
            //Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(context.getApplicationContext(), DialogInternet.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }

    public static void checkConnection(Context context) {
        if (isConnected(context)) {
            //DialogGPS.fa.finish();
            if (DialogInternet.fa != null) {
                DialogInternet.fa.finish();
            }
        } else {
            //showConnectivityPopup(context);
            //Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
           /* Intent intent1 = new Intent(context.getApplicationContext(), DialogInternet.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);*/
        }
    }


    public static void restrictNumberDecimal(int i, EditText ed[], int beforeDecimal1, int afterDecimal1) {
        int finalI = i;

        ed[i].setFilters(new InputFilter[]{new DigitsKeyListener(Boolean.FALSE, Boolean.TRUE) {
            int beforeDecimal = beforeDecimal1, afterDecimal = afterDecimal1;

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String etText = ed[finalI].getText().toString();
                if (etText.isEmpty()) {
                    return null;
                }
                String temp = ed[finalI].getText() + source.toString();
                if (temp.equals(".")) {
                    return "0.";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = ed[finalI].getSelectionStart();
                    if (etText.indexOf(".") == -1) {
                        dotPosition = temp.indexOf(".");
                    } else {
                        dotPosition = etText.indexOf(".");
                    }
                    if (cursorPositon <= dotPosition) {
                        String beforeDot = etText.substring(0, dotPosition);
                        if (beforeDot.length() < beforeDecimal) {
                            return source;
                        } else {
                            if (source.toString().equalsIgnoreCase(".")) {
                                return source;
                            } else {
                                return "";
                            }
                        }
                    } else {
                        temp = temp.substring(temp.indexOf(".") + 1);
                        if (temp.length() > afterDecimal) {
                            return "";
                        }
                    }
                }

                return super.filter(source, start, end, dest, dstart, dend);
            }
        }});


    }
}

