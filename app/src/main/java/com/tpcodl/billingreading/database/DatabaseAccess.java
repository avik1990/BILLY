package com.tpcodl.billingreading.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    public static SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    public DatabaseAccess(Context context) {
        this.openHelper = new DatabaseHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
            Log.d("DemoApp", "Database Close Sucessfully");
        }
    }

    public String getAuthenticate(String strUserid, String strPwd) {
        String dbuserID="";
        String dbpwd="";
        String prv_flg="0";
      //  try {
            String strSelectSQL_01 = "SELECT userid,passkey,valid_startdate,valid_enddate,lock_flag,retries,user_name,prv_flg " +
                    " FROM sa_user  where date('now')>=valid_startdate and date('now')<=valid_enddate and lock_flag=0 " +
                    " and userid='" + strUserid + "' and passkey='" + strPwd + "'";
            Cursor cursor = database.rawQuery(strSelectSQL_01, null);
            Log.d("DemoApp", "Query SQL " + strSelectSQL_01);
            while (cursor.moveToNext()) {
                dbuserID = cursor.getString(1);
                dbpwd = cursor.getString(2);
                prv_flg = cursor.getString(7);
                Log.d("DemoApp", "in Loop" + dbuserID);
                Log.d("DemoApp", "in Loop" + dbpwd);
                Log.d("DemoApp", "in Loop" + prv_flg);
            }
            cursor.close();
      //  }catch(Exception e){
          //  prv_flg="0";
       // }
        Log.d("DemoApp", "Query prv_flg " + prv_flg);
        return prv_flg;
    }
}