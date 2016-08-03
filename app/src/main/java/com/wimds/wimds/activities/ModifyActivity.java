package com.wimds.wimds.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.wimds.wimds.R;
import com.wimds.wimds.Util.DeviceInfo;
import com.wimds.wimds.Util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;


public class ModifyActivity extends AppCompatActivity implements View.OnClickListener {
    private JSONArray result;
    private JSONObject result_object;
    private Boolean result_check;
    private EditText b_name;
    private EditText c_name;
    private EditText c_etc;
    private RadioGroup gender_checked;
    private RadioButton gender_boy;
    private RadioButton gender_girl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        b_name = (EditText) findViewById(R.id.s_name);
        b_name.setText(DeviceInfo.b_name);
        c_name = (EditText) findViewById(R.id.c_name);
        c_name.setText(DeviceInfo.c_name);
        c_etc = (EditText) findViewById(R.id.c_etc);
        c_etc.setText(DeviceInfo.c_etc);
        gender_boy = (RadioButton) findViewById(R.id.gender_boy);
        gender_girl = (RadioButton) findViewById(R.id.gender_girl);
        if(DeviceInfo.c_gender.equals("m")) gender_boy.setChecked(true);
        if(DeviceInfo.c_gender.equals("fm")) gender_girl.setChecked(true);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.modify_button)
        {
            String new_b_name = b_name.getText().toString();
            String new_c_name = c_name.getText().toString();
            String new_c_etc = c_etc.getText().toString();
            String new_gender = null;
            if(gender_boy.isChecked()) new_gender = "m";
            else if(gender_girl.isChecked()) new_gender="fm";
            try{
                URL url = new URL("http://wimdsadmin.cafe24.com/modifyDevice/"+IntroActivity.android_id+"/"+new_b_name+"/"+new_c_name+"/"+new_gender+"/"+new_c_etc+"/");
                result = HttpUtil.httpRequest(url);
                result_object = result.getJSONObject(0);
                result_check = result_object.getBoolean("result");
                DeviceInfo.b_name = new_b_name;
                DeviceInfo.c_name = new_c_name;
                DeviceInfo.c_gender = new_gender;
                DeviceInfo.c_etc = new_c_etc;
                //Modify하고 제대로된 값이 넘어오면
               if(result_check){
                   Toast.makeText(this.getApplicationContext(), "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                   finish();
                }

               //Modify하고 제대로된 값이 안넘어오면
               else{
                    Toast.makeText(this.getApplicationContext(), "수정 에러", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                Toast.makeText(this.getApplicationContext(), "서비스가 없습니다.", Toast.LENGTH_SHORT).show();
            }

        }
        if(v.getId() == R.id.cancel_button)
        {
            Toast.makeText(getApplicationContext(), "취소되었습니다", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED,resultIntent);
            finish();
        }
    }
}
