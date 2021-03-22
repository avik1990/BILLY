
package com.tpcodl.billingreading.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tpcodl.billingreading.BuildConfig;
import com.tpcodl.billingreading.R;
import com.tpcodl.billingreading.activity.viewcaptureddata.ViewDataActivityReadMode;
import com.tpcodl.billingreading.database.DatabaseHelper;
import com.tpcodl.billingreading.models.NSBMData;
import com.tpcodl.billingreading.prefrences.PreferenceHandler;
import com.tpcodl.billingreading.utils.ActivityUtils;
import com.tpcodl.billingreading.utils.Constant;
import com.tpcodl.billingreading.utils.UtilsClass;

import okhttp3.internal.Util;

public class SearchDataActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_title;
    private ImageView iv_back;
    private RadioGroup radiogroup;
    private EditText et_account;
    private Button btn_submit;
    private String searchType;
    String SelChoice = "Legacy";
    String varCond = "";
    DatabaseHelper db;
    Context context;
    NSBMData nbsmDatamodel;
    RelativeLayout account_param;
    int offset = -1;
    String detailNo = "";
    ActivityUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_data);
        context = this;
        utils = ActivityUtils.getInstance();
        if (!UtilsClass.isvalidVersion(utils.getAppVersion(), context)) {
            androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context).create();
            alertDialog.setTitle("Update!!");
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setMessage("You are using an outdated Version of this Application. Please update");
            alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
        }


        account_param = findViewById(R.id.account_param);
        tv_title = findViewById(R.id.tv_title);
        radiogroup = findViewById(R.id.rg_group);
        et_account = findViewById(R.id.et_account);
        iv_back = findViewById(R.id.iv_back);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        tv_title.setText(getResources().getString(R.string.search_data));
        iv_back.setOnClickListener(this);
        searchType = getResources().getString(R.string.installation_no);
        db = new DatabaseHelper(this);

        if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
            et_account.setInputType(InputType.TYPE_CLASS_NUMBER);
            et_account.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        } else {
            et_account.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_installation:
                        et_account.setInputType(InputType.TYPE_CLASS_NUMBER);
                        et_account.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                        SelChoice = "InstNo";
                        et_account.setText("");
                        account_param.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_meter_no:
                        et_account.setInputType(InputType.TYPE_CLASS_TEXT);
                        SelChoice = "MtrNo";
                        et_account.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                        et_account.setText("");
                        account_param.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_legacy_accnono:
                        if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                            et_account.setInputType(InputType.TYPE_CLASS_NUMBER);
                        } else {
                            et_account.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                        et_account.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                        SelChoice = "Legacy";
                        et_account.setText("");
                        account_param.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_sequence:
                        //et_account.setInputType(InputType.TYPE_CLASS_NUMBER);
                        hideKeyboard();
                        SelChoice = "SeqNo";
                        detailNo = "";
                        et_account.setText("");
                        account_param.setVisibility(View.GONE);
                        break;
                    case R.id.rb_phone_no:
                        et_account.setInputType(InputType.TYPE_CLASS_NUMBER);
                        et_account.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
                        SelChoice = "Phone";
                        et_account.setText("");
                        account_param.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_sequence_w:
                        et_account.setInputType(InputType.TYPE_CLASS_NUMBER);
                        SelChoice = "SeqNoW";
                        account_param.setVisibility(View.GONE);
                        break;
                    case R.id.rb_ca_no:
                        et_account.setInputType(InputType.TYPE_CLASS_NUMBER);
                        SelChoice = "consno";
                        et_account.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                        account_param.setVisibility(View.VISIBLE);
                        et_account.setText("");
                        break;
                }
            }
        });
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit: {
                if (UtilsClass.isvalidVersion(utils.getAppVersion(), context)) {
                    if (!SelChoice.equalsIgnoreCase("SeqNo")) {
                        detailNo = et_account.getText().toString().trim();
                        if (et_account.getText().toString().length() <= 0) {
                            et_account.requestFocus();
                            et_account.setError("Please enter the details");
                        } else if ((detailNo.length() < 8 || detailNo.isEmpty()) && SelChoice.equals("AcctNo")) {
                            et_account.setError("Invalid consumer No Entry");
                        } else if ((detailNo.length() < 10 || detailNo.isEmpty()) && SelChoice.equals("Phone")) {
                            et_account.setError("10 digit phone no required");
                        } else {
                            if (SelChoice.equals("consno")) {
                                varCond = " where  CA='" + detailNo + "'";
                            } else if (SelChoice.equals("MtrNo")) {
                                varCond = " where METER_NO like  '%" + detailNo + "%'";
                            } else if (SelChoice.equals("Phone")) {
                                varCond = " where PHONE_1='" + detailNo + "'";
                            } else if (SelChoice.equals("InstNo")) {
                                varCond = " where INSTALLATION='" + detailNo + "'";
                            } else if (SelChoice.equals("Legacy")) {
                                if (PreferenceHandler.getisSBNONSBFLAG(context).equalsIgnoreCase("SBM")) {
                                    varCond = " where substr(LEGACY_ACCOUNT_NO2,5)='" + detailNo + "'";
                                } else {
                                    varCond = " where LEGACY_ACCOUNT_NO=UPPER('" + detailNo + "')";
                                }
                                Log.e("varCond", varCond);
                            } else {
                                varCond = " where LEGACY_ACCOUNT_NO=UPPER('" + detailNo + "')";
                            }
                            offset = -1;
                            nbsmDatamodel = db.getNBSMData(varCond, offset);

                            if (nbsmDatamodel != null) {
                                try {
                                    if (nbsmDatamodel.getREAD_FLAG() != null) {
                                        if ((nbsmDatamodel.getREAD_FLAG().equalsIgnoreCase("1") /*&& nbsmDatamodel.getREVISIT_FLAG().equalsIgnoreCase("0")*/)) {
                                            Intent i = new Intent(context, ViewDataActivityReadMode.class);
                                            i.putExtra(Constant.VARCOND, varCond);
                                            startActivity(i);
                                            UtilsClass.showToastShort(context, "Data already captured");
                                        } else {
                                            Intent intent = new Intent(this, BaseMeterActivity.class);
                                            intent.putExtra(Constant.DETAILS_NO, detailNo);
                                            intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(this, BaseMeterActivity.class);
                                        intent.putExtra(Constant.DETAILS_NO, detailNo);
                                        intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                                        startActivity(intent);
                                    }
                                } catch (Exception e) {
                                    Intent intent = new Intent(this, BaseMeterActivity.class);
                                    intent.putExtra(Constant.DETAILS_NO, detailNo);
                                    intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                                    startActivity(intent);
                                }
                            }

                            else {
                                UtilsClass.showToastLong(context, getString(R.string.msg_nodata));
                            }
                        }
                    } else {
                        offset = 0;
                        nbsmDatamodel = db.getNBSMData(varCond, offset);
                        Intent intent = new Intent(this, BaseMeterActivity.class);
                        intent.putExtra(Constant.DETAILS_NO, detailNo);
                        //intent.putExtra("varCond", varCond);
                        intent.putExtra(Constant.SEL_CHOICE, SelChoice);
                        startActivity(intent);
                    }
                } else {
                    //if (UtilsClass.isvalidVersion(utils.getAppVersion())) {
                    androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Update!!");
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setMessage("You are using an outdated Version of this Application. Please update");
                    alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    alertDialog.show();
                    //   }
                }
                break;
            }
            case R.id.iv_back: {
                finish();
                break;
            }
        }
    }

}