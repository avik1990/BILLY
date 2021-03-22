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
import com.tpcodl.billingreading.models.PrinterModal;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

public class SetPrinterTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_cancel, btn_submit;
    private LinearLayout ll_dynamic;
    private Context mContext;
    private DatabaseHelper db;
    private String printerName = "";
    private TextView printer_name;
    private PrinterModal printerModal;
    private List<PrinterModal> printerModals = new ArrayList<>();
    private RadioGroup ll;
    private RadioButton rdbtn;
    private String printerId;
    private String printername;
    private String strprintid;
    private PreferenceHandler phandler;
    private ActivityUtils utils;
    private ImageView iv_back;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_printer_type);

        mContext = this;
        db = new DatabaseHelper(mContext);
        utils = ActivityUtils.getInstance();
        phandler = new PreferenceHandler(this);
        initView();


    }

    private void initView() {
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_submit = findViewById(R.id.btn_submit);
        ll_dynamic = findViewById(R.id.ll_dynamic);
        printer_name = findViewById(R.id.printer_name);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);

        tv_title.setText("Printer Type");

        btn_submit.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        printerName = db.getPreviousPrinterType();

        printer_name.setText(printerName);
        new AsyncTaskExample().execute();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit: {
                if (ll.getCheckedRadioButtonId() == -1) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setTitle("No Printer selected");
                    alertDialogBuilder.setMessage("Please Select One printer")
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
                } else {
                    String Usernm = utils.getUserID();
                    Log.d("DemoApp", " Usernm   " + Usernm);

                    int selectedId = ll.getCheckedRadioButtonId();
                    Log.d("DemoApp", " strprintid   " + selectedId);

                    // find the radiobutton by returned id

                    db.resetPreviousPrinter();
                    db.resetPrinter(selectedId);
                    db.setSAUser(selectedId, utils.getUserID());

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setTitle("Printer set Successfully");
                    alertDialogBuilder.setMessage("Printer set Successfully")
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

            case R.id.btn_cancel:
            case R.id.iv_back: {
                onBackPressed();
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
            fetchPrinterdata();
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


    private void showPrinter() {
        ll = new RadioGroup(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        ll_dynamic.addView(ll, p);

        for (int i = 0; i < printerModals.size(); i++) {
            printerId = printerModals.get(i).getPrinterId();
            printername = printerModals.get(i).getPrinterName();
            rdbtn = new RadioButton(this);
            rdbtn.setText(" " + printername);
            rdbtn.setId(i);
            rdbtn.setTextSize(15);

            if (printerName.equalsIgnoreCase(printername)) {
                rdbtn.setChecked(true);
            }

            //rdbtn.setOnClickListener(mThisButtonListener);
            ll.addView(rdbtn, p);
        }


    }

    private void fetchPrinterdata() {
        printerModals = db.getPrinterType();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showPrinter();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}