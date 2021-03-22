/*
package com.tpcodl.billingreading.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.UtilsClass;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HouseLockActivity extends AppCompatActivity {
    private DatabaseHelper db;
    Context context = this;
    private static Button BtnHl,valPass;
    private static TextView strtxt;
    private static ProgressBar pb=null;
    private static TextView tv=null;
    private static int count=0;
    private static int fincnt=0;
    private static int progrcnt=1;
    private static int totrec=0;
    private static int totrec1=0;
    private static int progressStatus = 0;
    Cursor rs=null;
    Cursor rs1=null;
    Button backbtn=null;
    private Context mContext;
    private static EditText txtpwd ;
    private PreferenceHandler phandler;
    private ActivityUtils utils;
    private TextView msg1=null;
    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hl);

        db = new DatabaseHelper(mContext);
        utils = ActivityUtils.getInstance();
        phandler = new PreferenceHandler(this);
        pb = (ProgressBar) findViewById(R.id.pb);
        tv = (TextView) findViewById(R.id.tv);
        valPass = (Button) findViewById(R.id.valPass);
        BtnHl = (Button) findViewById(R.id.BtnHl);//HL Bill
        strtxt=(TextView) findViewById(R.id.strtxt);//HL Bill
        BtnHl.setEnabled(false);
        BtnHl.setClickable(false);
        BtnHl.setVisibility(View.GONE);
        strtxt.setVisibility(View.GONE);
        backbtn = (Button) findViewById(R.id.DwldBack);
       // backbtn.setVisibility(Button.GONE);
        msg1 = (TextView) findViewById(R.id.msg1);
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        valPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtpwd = (EditText) findViewById(R.id.txtpwd);
                String strPwd = txtpwd.getText().toString();
                Log.d("DemoApp", " strPwd " + strPwd);
                SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
                String chkdt = sdf.format(new Date());
                Log.d("DemoApp", " chkdt " + chkdt);
                try {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = sdf1.parse("2020-03-26");
                    Date date2 = sdf1.parse("2028-12-31");
                    Log.d("DemoApp", " date1 " + date1);
                    Log.d("DemoApp", " date2 " + date2);
                    Date curdt = sdf1.parse(sdf1.format(new Date()));
                    Log.d("DemoApp", " curdt " + curdt);

                    if (curdt.compareTo(date1) >= 0 && curdt.compareTo(date2) <= 0) {
                        Log.d("DemoApp", " 1 ");
                        if (strPwd.equals(chkdt)) {
                            BtnHl.setEnabled(true);
                            BtnHl.setClickable(true);
                            BtnHl.setVisibility(View.VISIBLE);
                            txtpwd.setVisibility(View.GONE);
                            valPass.setVisibility(View.GONE);
                            msg1.setVisibility(View.GONE);
                            Log.d("DemoApp", " 2 ");
                        } else {
                            BtnHl.setEnabled(false);
                            BtnHl.setClickable(false);
                            BtnHl.setVisibility(View.GONE);
                            backbtn.setVisibility(View.VISIBLE);
                            Log.d("DemoApp", " 3 ");
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setTitle("Password Mismatch");
                            alertDialogBuilder.setMessage("Enter Password Correctly Refer e-mail Dated 27.03.2020 ")
                                    .setCancelable(false)
                                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            HouseLockActivity.this.finish();
                                        }
                                    });
                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            // show it
                            alertDialog.show();
                        }
                    } else {
                        BtnHl.setEnabled(false);
                        BtnHl.setClickable(false);
                        BtnHl.setVisibility(View.GONE);
                        backbtn.setVisibility(View.VISIBLE);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Valid Period Over ");
                        alertDialogBuilder.setMessage("No Aceess ")
                                .setCancelable(false)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        HouseLockActivity.this.finish();
                                    }
                                });
                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });//end
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });//end
        BtnHl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strSelectSQL_03= "DELETE FROM BILL_SBM_DATA_HL";
                Log.d("DemoApp", "strSelectSQL_03 "+strSelectSQL_03);
                DatabaseAccess.database.execSQL(strSelectSQL_03);

                String strUpdateSQL_02 ="INSERT INTO BILL_SBM_DATA_HL SELECT * FROM BILL_SBM_DATA WHERE BILL_FLAG = 0";
                Log.d("DemoApp", "strUpdateSQL_02  " + strUpdateSQL_02);
                DatabaseAccess.database.execSQL(strUpdateSQL_02);

                String strUpdateSQL_01 = "UPDATE BILL_SBM_DATA SET BILL_BASIS = 'H',PRV_AMOUNT=0,HL_UNIT=0,NO_OF_MON=0 WHERE BILL_FLAG = 0 AND (OLD_STATUS!='S' AND OLD_STATUS!='D')";
                Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
                DatabaseAccess.database.execSQL(strUpdateSQL_01);

                totrec = 0;
                String strSelectSQL_01 = "SELECT COUNT(1) FROM BILL_SBM_DATA WHERE BILL_FLAG=0 ";
                rs = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
                while (rs.moveToNext()) {
                    totrec = rs.getInt(0);
                }
                rs.close();
                rs=null;
                tv.setText("Start Hl Billing");
                Log.d("DemoApp", "Total Consumer to be HL Billed" + totrec);
                if(totrec>0) {
                    new ProgressTask().execute(totrec);
                }else{
                    backbtn.setVisibility(View.VISIBLE);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle("No unbill records to be HL ");
                    alertDialogBuilder.setMessage("No unbill Records found ")
                            .setCancelable(false)
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    HouseLockActivity.this.finish();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }

            }
        });//end
    }
    // to handle download progress
    private class ProgressTask extends AsyncTask<Integer, Integer, Void> {

        protected void onPreExecute() {
            super.onPreExecute(); ///////???????
            //  prgs.setMax(100); // set maximum progress to 100.

            tv.setText("Start Hl Billing Wait !!\n" + "Total no of consumers to be Calulated=" + totrec);
            pb.setMax(totrec - 1);
            fincnt=0;
            Log.d("DemoApp", "  totrec =" + totrec);
            Log.d("DemoApp", "  progressStatus =" + progressStatus);
            txtpwd.setVisibility(View.GONE);
            valPass.setVisibility(View.GONE);
            msg1.setVisibility(View.GONE);
        }
        protected void onCancelled() {
            //  prgs.setMax(0); // stop the progress
            Log.v("Progress", "Cancelled");
        }
        protected Void doInBackground(Integer... params) {
            int start=params[0];
            String AccountNum="";
            Log.d("DemoApp", "  start =" + start);
            Log.d("DemoApp", "  totrec =" + totrec);
            Log.d("DemoApp", "  progressStatus =" + progressStatus);
            int isucess=0;
            try{
                String strSelectSQL_01 = "SELECT CONS_ACC FROM BILL_SBM_DATA_HL WHERE BILL_FLAG=0";
                rs1 = DatabaseAccess.database.rawQuery(strSelectSQL_01, null);
                count = 0;  // Line number of count
            }catch(Exception e){
                Log.d("DemoApp", "224 exception =" + e);
            }
            progressStatus=0;
            try{
                while (rs1.moveToNext() && progressStatus<start) {
                    AccountNum=rs1.getString(0);
                    try {
                        calculateBillAll.CalculateBill(AccountNum);
                    }catch(Exception e){
                        Log.d("DemoApp", "222 exception =" + e);
                    }
                    publishProgress(progrcnt);
                    count++;
                    progrcnt++;
                    progressStatus +=1;
                }
            }catch(Exception e){
                Log.d("DemoApp", "  Exception =" + e);

            }
            return null;
        }

        protected void onProgressUpdate(Integer... values) {
            tv.setText("Total no of consumers to be HL="+totrec);
            fincnt= values[0];
            pb.setProgress(progressStatus);
            tv.setText("Calulating HL..." + fincnt + ">>" + totrec);

            BtnHl.setVisibility(View.GONE);
            strtxt.setVisibility(View.VISIBLE);
            strtxt.setText("Wait!! donot press any key!! ");

        }

        protected void onPostExecute(Void result) {
            // async task finished
            try {
            }catch(Exception e){}
            tv.setText("HL BILL calculation completed");

            Log.d("DemoApp", "fincnt " + fincnt);
            Log.d("DemoApp","totrec "+totrec);

            if((fincnt)==totrec) {

                strtxt.setText("Tot Cons. HL billed Sucessfully:=" + fincnt);
                fincnt=0;
                progrcnt=1;

                try {
                    rs.close();
                    rs1.close();
                    databaseAccess.close();
                    //migrated to main screen
                    backbtn.setVisibility(Button.VISIBLE);
                    BtnHl.setVisibility(Button.GONE);

                } catch (Exception e) {
                    Log.d("DemoApp", "Error in Buffer close and data to main sbm_data");
                }
            }
        }

    }

}
*/
