package com.tpcodl.billingreading.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tpcodl.billingreading.R;

import org.w3c.dom.Text;


public class CustomDialogWithTwoButton extends Dialog implements View.OnClickListener {

    private Context mContext;
    private CustomDialogWithTwoBtmsCallBack customDialogWithTwoBtmsCallBack;
    private String dialogText = "";
    private String positiveBtnText = "";
    private String negativeBtnText = "";
    private TextView tvPassionHeader;
    private TextView tvSubmit;
    private TextView tvCancel;
    private TextView tv_total_received_value;
    private TextView tv_total_inserted_value;
    private TextView tv_total_failed_value;
    private String totalVaue, insertedValues, failedValues;
    TextView tv_total_headercntVal, tv_total_childcntVal;
    String headerDiff, childDiff;

    public CustomDialogWithTwoButton(@NonNull Context context, String dialogText, String positiveBtnText, String negativeBtnText, CustomDialogWithTwoBtmsCallBack customDialogWithTwoBtmsCallBack,
                                     String totalValue, String insertedValue, String failedValue, String headerDiff, String childDiff) {
        super(context);
        this.mContext = context;
        this.dialogText = dialogText;
        this.positiveBtnText = positiveBtnText;
        this.negativeBtnText = negativeBtnText;
        this.customDialogWithTwoBtmsCallBack = customDialogWithTwoBtmsCallBack;
        totalVaue = totalValue;
        insertedValues = insertedValue;
        failedValues = failedValue;
        this.headerDiff = headerDiff;
        this.childDiff = childDiff;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_unavailable_chat_dialog);
        setCancelable(false);
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        Log.v("width", width + "");
        getWindow().setLayout((int) (1.0 * width), (int) (1.0 * height));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initialize();
        listener();

        tvPassionHeader.setText(dialogText);
        tvSubmit.setText(positiveBtnText);
        tvCancel.setText(negativeBtnText);
    }

    private void initialize() {
        tv_total_headercntVal = findViewById(R.id.tv_total_headercntVal);
        tv_total_childcntVal = findViewById(R.id.tv_total_childcntVal);
        tvPassionHeader = findViewById(R.id.customVedaDialog_tvPassionHeader);
        tvSubmit = findViewById(R.id.customVedaDialog_tvSubmit);
        tvCancel = findViewById(R.id.customVedaDialog_tvCancel);
        tv_total_received_value = findViewById(R.id.tv_total_received_value);
        tv_total_inserted_value = findViewById(R.id.tv_total_inserted_value);
        tv_total_failed_value = findViewById(R.id.tv_total_failed_value);

        tv_total_received_value.setText(totalVaue);
        tv_total_inserted_value.setText(insertedValues);
        tv_total_failed_value.setText(failedValues);
        tv_total_headercntVal.setText(headerDiff);
        tv_total_childcntVal.setText(childDiff);

    }

    private void listener() {
        tvSubmit.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customVedaDialog_tvSubmit:
                customDialogWithTwoBtmsCallBack.positiveBtmCallBack();
                dismiss();
                break;
            case R.id.customVedaDialog_tvCancel:
                customDialogWithTwoBtmsCallBack.negativeBtmCallBack();
                dismiss();
                break;
        }
    }

    public interface CustomDialogWithTwoBtmsCallBack {
        void positiveBtmCallBack();

        void negativeBtmCallBack();
    }
}
