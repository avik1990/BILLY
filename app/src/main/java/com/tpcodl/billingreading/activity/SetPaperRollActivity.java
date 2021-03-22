package com.tpcodl.billingreading.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.models.PaperModal;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

public class SetPaperRollActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView iv_back;
    private TextView tv_title;
    private TextView paper_name;
    private LinearLayout ll_dynamic;
    private Button btn_cancel;
    private Button btn_submit;
    private DatabaseHelper db;
    private String paperName = "";

    private RadioGroup ll;
    private RadioButton rdbtn;
    private PreferenceHandler phandler;
    private ActivityUtils utils;
    private PaperModal printerModal;
    private List<PaperModal> paperModals = new ArrayList<>();
    private String paperId;
    private String papername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_paper_roll);
        mContext = this;

        db = new DatabaseHelper(mContext);
        utils = ActivityUtils.getInstance();
        phandler = new PreferenceHandler(this);
        initView();

    }

    private void initView() {

        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        paper_name = findViewById(R.id.paper_name);
        ll_dynamic = findViewById(R.id.ll_dynamic);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_submit = findViewById(R.id.btn_submit);


        iv_back.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

        paperName = db.getPreviousPaperType();
        paper_name.setText(paperName);

        new AsyncTaskExample().execute();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }
            case R.id.btn_cancel: {
                onBackPressed();
                break;
            }
            case R.id.btn_submit: {
                if (ll.getCheckedRadioButtonId() == -1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setTitle("No Paper Type selected");
                    alertDialogBuilder.setMessage("Please Select One Paper Type")
                            .setCancelable(false)
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                    // internetStatus.setText("Internet Disconnected.");
                    //  internetStatus.setTextColor(Color.parseColor("#ff0000"));
                } else {

                    int selectedId = ll.getCheckedRadioButtonId();
                    Log.d("DemoApp", " strprintid   " + selectedId);

                    // find the radiobutton by returned id

                    db.resetPreviousPapar();
                    db.resetPaper(selectedId);
                    db.setSAUser(selectedId, utils.getUserID());

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setTitle("Paper Type set Successfully");
                    alertDialogBuilder.setMessage("Paper Type set Successfully")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }

                break;
            }

        }

    }

    private class AsyncTaskExample extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fetchPaperdata();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (printerModal != null) {
                //printerModals.add(printerModal);
                //System.out.println("size==="+printerModal.getPrinterName());

            }

        }
    }

    private void fetchPaperdata() {
        paperModals = db.getPaperType();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showPrinter();
            }
        });

    }

    private void showPrinter() {
        ll = new RadioGroup(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ll_dynamic.addView(ll, p);

        for (int i = 0; i < paperModals.size(); i++) {
            paperId = paperModals.get(i).getPaperId();
            papername = paperModals.get(i).getPaperName();
            rdbtn = new RadioButton(this);
            rdbtn.setText(" " + papername);
            rdbtn.setId(i);
            rdbtn.setTextSize(15);

            if (paperName.equalsIgnoreCase(papername)) {
                rdbtn.setChecked(true);
            }

            //   rdbtn.setOnClickListener(mThisButtonListener);
            ll.addView(rdbtn, p);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}