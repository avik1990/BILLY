package com.tpcodl.billingreading.activity;

import android.content.Context;

import android.database.Cursor;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.adapter.NonSBMBilledDataAdapter;
import com.tpcodl.billingreading.database.DatabaseAccess;

import java.util.ArrayList;
import java.util.List;


public class ReportNSBMActivity extends AppCompatActivity {

    private Context mContext;
    private ImageView iv_back;
    private TextView tv_title;
    private String Usernm ="";
    private int sbmflg = 0;
    private Button btn_back;
    private TextView tv_tot_load_value,tv_reading_not_taken_value,tv_reading_taken_value;
    private DatabaseAccess databaseAccess=null;
    private String tbval="";
    private RecyclerView rv_details_recycler;
    private List<String>installation=new ArrayList<>();
    private String installations="";
    private NonSBMBilledDataAdapter nonSBMBilledDataAdapter;
    private TextView tv_title_billed;
    private View tv_view,tv_view1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_nsbm);
        mContext=this;
        btn_back   = (Button) findViewById(R.id.btn_back);

        tv_tot_load_value=(TextView) findViewById(R.id.tv_tot_load_value);
        tv_reading_not_taken_value=(TextView) findViewById(R.id.tv_reading_not_taken_value);
        tv_reading_taken_value=(TextView) findViewById(R.id.tv_reading_taken_value);
        rv_details_recycler=findViewById(R.id.rv_details_recycler);
        iv_back=findViewById(R.id.iv_back);
        tv_title=findViewById(R.id.tv_title);
        tv_title_billed=findViewById(R.id.tv_title_billed);
        tv_view=findViewById(R.id.tv_view);
        tv_view1=findViewById(R.id.tv_view1);
        initAdapter();

        tv_title.setText(mContext.getResources().getString(R.string.report_details));
        ////////////////////
        //added on 28.12.2016 by santiranjan
        String tcval="";
        String ubval="";
        String tcbval="";

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        String strUpdateSQL_01="";
        Cursor rs=null;
        strUpdateSQL_01 = "SELECT   ifnull(count(1),0) AS TOT_CON FROM TBL_SPOTBILL_HEADER_DETAILS where USER_TYPE='X'";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            tcval=rs.getString(0);
        }
        rs.close();
        strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0)  AS TOT_CON FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG =0 AND USER_TYPE='X'";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            ubval=rs.getString(0);
        }
        rs.close();
        strUpdateSQL_01 = "SELECT  ifnull(COUNT(1),0) AS TOT_CON FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG !=0 AND USER_TYPE='X'";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);
        while (rs.moveToNext()) {
            tbval=rs.getString(0);
        }
        rs.close();


        installation.clear();
        strUpdateSQL_01 = "SELECT  INSTALLATION FROM TBL_SPOTBILL_HEADER_DETAILS WHERE READ_FLAG !=0 AND USER_TYPE='X'";
        rs = DatabaseAccess.database.rawQuery(strUpdateSQL_01, null);

        if (rs.moveToFirst()) {
            do
            {
                installations = rs.getString(rs.getColumnIndex("INSTALLATION"));
                installation.add(installations);

            }while (rs.moveToNext());
        }
        rs.close();

        if (Integer.parseInt(tbval)>0){
            tv_title_billed.setVisibility(View.VISIBLE);
            rv_details_recycler.setVisibility(View.VISIBLE);
            tv_view.setVisibility(View.VISIBLE);
            tv_view1.setVisibility(View.VISIBLE);
        }
        else {
            tv_title_billed.setVisibility(View.GONE);
            rv_details_recycler.setVisibility(View.GONE);
            tv_view.setVisibility(View.GONE);
            tv_view1.setVisibility(View.GONE);
        }

        nonSBMBilledDataAdapter.notifyDataSetChanged();


        tv_tot_load_value.setText(tcval);
        tv_reading_not_taken_value.setText(ubval);
        tv_reading_taken_value.setText(tbval);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    private void initAdapter() {
        rv_details_recycler.setLayoutManager(new LinearLayoutManager(mContext));

        nonSBMBilledDataAdapter = new NonSBMBilledDataAdapter(mContext, installation);
        rv_details_recycler.setAdapter(nonSBMBilledDataAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
