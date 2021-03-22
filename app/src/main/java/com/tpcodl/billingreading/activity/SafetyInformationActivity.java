package com.tpcodl.billingreading.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tpcodl.billingreading.R;

public class SafetyInformationActivity extends AppCompatActivity implements View.OnClickListener {
    private CheckBox radio_one, radio_two, radio_three, radio_four;
    private Button button_submit;
    private ImageView iv_back;
    private TextView tv_title;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);
        radio_one = findViewById(R.id.radio_one);
        radio_two = findViewById(R.id.radio_two);
        radio_three = findViewById(R.id.radio_three);
        radio_four = findViewById(R.id.radio_four);
        button_submit = findViewById(R.id.button_submit);
        iv_back=findViewById(R.id.iv_back);
        tv_title=findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.safety_validation_title));
        button_submit.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_submit: {
                if (radio_one.isChecked() && radio_two.isChecked() && radio_three.isChecked() && radio_four.isChecked()) {

                    Intent intent=new Intent(SafetyInformationActivity.this,LandingActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Please fill the details", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.iv_back:{
                finish();
                break;
            }
        }
    }
}
