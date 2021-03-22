package com.tpcodl.billingreading.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tpcodl.billingreading.ConsolidatedActivity;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.printReceipt.ReportPrintActivity;
import com.tpcodl.billingreading.activity.printReceipt.ReportPrintAmigoImpact;
import com.tpcodl.billingreading.activity.printReceipt.ReportPrintAnalogicImpact;
import com.tpcodl.billingreading.activity.printReceipt.ReportPrintAnalogicNewThermal;
import com.tpcodl.billingreading.activity.printReceipt.ReportPrintAnalogicThermal;
import com.tpcodl.billingreading.activity.printReceipt.ReportPrintEpsonThermal;
import com.tpcodl.billingreading.activity.printReceipt.ReportPrintPhiThermal;
import com.tpcodl.billingreading.activity.printReceipt.ReportPrintSBM;
import com.tpcodl.billingreading.activity.printReceipt.ReportPrintSoftlandImpact;
import com.tpcodl.billingreading.database.DatabaseAccess;
import com.tpcodl.billingreading.utils.UtilsClass;

public class ReportDashBoardActivity extends AppCompatActivity {
    private DatabaseAccess databaseAccess = null;
    private String Usernm = "";
    private int sbmflg = 0;
    private ImageView iv_back;
    TextView strtcval;
    TextView strubval;
    TextView strtbval;
    TextView strbutval;
    TextView strcttval;
    TextView strtcbval;
    TextView strubtval;
    TextView strbtval;
    Button vConsilidateReport;
    Context context;

    protected void onResume() {
        super.onResume();
        UtilsClass.checkGpsConnection(getApplicationContext());
        UtilsClass.checkConnection(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_dash_board);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        context = this;
        Button Backbtn = (Button) findViewById(R.id.Backbtn);
//added on 28.12.2016 by santiranjan
        strtcval = (TextView) findViewById(R.id.tcval);
        strubval = (TextView) findViewById(R.id.ubval);
        strtbval = (TextView) findViewById(R.id.tbval);
        strbutval = (TextView) findViewById(R.id.butval);
        strcttval = (TextView) findViewById(R.id.cttval);
        strtcbval = (TextView) findViewById(R.id.tcbval);
        strubtval = (TextView) findViewById(R.id.ubtval);
        strbtval = (TextView) findViewById(R.id.btval);
        vConsilidateReport = (Button) findViewById(R.id.vConsilidateReport);
        iv_back = findViewById(R.id.iv_back);

        vConsilidateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ConsolidatedActivity.class);
                startActivity(i);
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        ////////////////////
//added on 28.12.2016 by santiranjan
        String tcval = "";
        String ubval = "";
        String tbval = "";
        String butval = "";
        String cttval = "";
        String tcbval = "";
        String ubtval = "";
        String btval = "";
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        String strUpdateSQL_01 = "";
        Cursor rs = null;
        strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM TBL_SPOTBILL_HEADER_DETAILS ";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            tcval = rs.getString(0);
        }
        rs.close();
        strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG =0 ";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            ubval = rs.getString(0);
        }
        rs.close();
        strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            tbval = rs.getString(0);
        }
        rs.close();
        strUpdateSQL_01 = "SELECT   ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            butval = rs.getString(0);
        }

        strUpdateSQL_01 = "SELECT  ifnull(SUM(AMOUNT_PAYABLE),0)  AS TOT_CUR FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG !=0 and strftime('%d-%m-%Y', 'now') =strftime('%d-%m-%Y', osbill_date) ";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            cttval = rs.getString(0);
        }
        rs.close();
        strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG !=0  ";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            tcbval = rs.getString(0);
        }
        rs.close();
        strUpdateSQL_01 = "SELECT   ifnull(SUM(PRESENT_BILL_UNITS),0)  AS TOT_UNIT FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG !=0  ";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            ubtval = rs.getString(0);
        }
        rs.close();
        strUpdateSQL_01 = "SELECT  round(SUM(AMOUNT_PAYABLE),0)  AS TOT_BILL FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG !=0  ";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            btval = rs.getString(0);
        }
        rs.close();
        databaseAccess.close();
        SharedPreferences sessiondata = getApplicationContext().getSharedPreferences("sessionval", 0);
        SharedPreferences.Editor editor = sessiondata.edit();
        Usernm = sessiondata.getString("userID", null);
        databaseAccess.open();
        strUpdateSQL_01 = "SELECT SBMPRV FROM Mst_User WHERE userid = '" + Usernm + "'";
        Log.d("DemoApp", "strUpdateSQL_01  " + strUpdateSQL_01);
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        sbmflg = 0;
        while (rs.moveToNext()) {
            sbmflg = rs.getInt(0);
        }
        //   Log.d("DemoApp", "strUpdateSQL_01  01");
        rs.close();
        databaseAccess.close();
        strtcval.setText(tcval);
        strubval.setText(ubval);
        strtbval.setText(tbval);
        strbutval.setText(butval);
        strcttval.setText(cttval);
        strtcbval.setText(tcbval);
        strubtval.setText(ubtval);
        strbtval.setText(btval);

        ///end 28.12.2016
        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();

            }
        });
        Button DailyRep = (Button) findViewById(R.id.DailyRep);
        DailyRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sbmflg == 1) {
                    Intent reports = new Intent(getApplicationContext(), ReportPrintSBM.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "D");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 2) { //analogic thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAnalogicThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "D");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 3) { //Epson thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintEpsonThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "D");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 4) { //Softland impact blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintSoftlandImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "D");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 5) { //Amigos imapact blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAmigoImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "D");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 6) { //Analogic imapact blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAnalogicImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "D");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 7) { //Phi thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintPhiThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "D");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 8) { //Amigo thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAmigoImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "D");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 9) { //Analogic New thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAnalogicNewThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "D");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else {
                    Intent reports = new Intent(getApplicationContext(), ReportPrintActivity.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "D");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();

                }

            }
        });
        Button SumRep = (Button) findViewById(R.id.SumRep);
        SumRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sbmflg == 1) {
                    Intent reports = new Intent(getApplicationContext(), ReportPrintSBM.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "S");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 2) { //analogic thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAnalogicThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "S");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 3) { //Epson thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintEpsonThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "S");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 4) { //Softland impact blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintSoftlandImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "S");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 5) { //Amigos imapact blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAmigoImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "S");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 6) { //Analogic imapact blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAnalogicImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "S");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 7) { //Phi thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintPhiThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "S");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 8) { //Amigo thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAmigoImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "S");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 9) { //Analogic New thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAnalogicNewThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "S");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else {
                    Intent reports = new Intent(getApplicationContext(), ReportPrintActivity.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "S");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                }


            }
        });
        Button UNBilRep = (Button) findViewById(R.id.UNBilRep);
        UNBilRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sbmflg == 1) {
                    Intent reports = new Intent(getApplicationContext(), ReportPrintSBM.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "U");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 2) { //analogic thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAnalogicThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "U");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 3) { //Epson thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintEpsonThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "U");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 4) { //Softland impact blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintSoftlandImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "U");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 5) { //Amigos imapact blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAmigoImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "U");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 6) { //Analogic imapact blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAnalogicImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "U");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 7) { //Phi thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintPhiThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "U");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 8) { //Amigo thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAmigoImpact.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "U");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else if (sbmflg == 9) { //Analogic New thermal blutooth printer
                    Intent reports = new Intent(getApplicationContext(), ReportPrintAnalogicNewThermal.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "U");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                } else {
                    Intent reports = new Intent(getApplicationContext(), ReportPrintActivity.class);
                    Bundle Report = new Bundle();
                    Report.putString("ReportTyp", "U");
                    reports.putExtras(Report);
                    startActivity(reports);
                    finish();
                }


            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
