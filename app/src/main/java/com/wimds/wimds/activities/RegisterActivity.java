package com.wimds.wimds.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.estimote.sdk.MacAddress;
import com.estimote.sdk.eddystone.Eddystone;
import com.wimds.wimds.R;
import com.wimds.wimds.Util.HttpUtil;
import com.wimds.wimds.Util.Token;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;


/**
 * Created by dongdor on 2016. 5. 15..
 */
public class RegisterActivity extends Activity implements View.OnClickListener{

    private String beacon_address;
    private String smartphone_addresss;
    private String gps_info;
    private MacAddress mac_id;
    private Eddystone selected_eddystone;
    private String s_number;
    private JSONObject result_object;
    RadioGroup rg;
    private JSONArray result;
    Boolean result_check;
    private static final String TAG = "Wimds";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent intent = getIntent();
        selected_eddystone = getIntent().getParcelableExtra(ListEddystoneActivity.EXTRAS_EDDYSTONE);
        rg = (RadioGroup) findViewById(R.id.gender_checked);
    }
    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.register_button)
        {
            EditText b_name = (EditText)this.findViewById(R.id.s_name);
            EditText c_name = (EditText)this.findViewById(R.id.c_name);
            EditText c_etc = (EditText)this.findViewById(R.id.c_etc);
            mac_id = selected_eddystone.macAddress;
            TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            s_number = mgr.getLine1Number();
            RadioButton rd = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
            String b_url_name = b_name.getText().toString();
            String c_url_name = c_name.getText().toString();
            String c_url_etc = c_etc.getText().toString();
            String mac_url_id = mac_id.toStandardString();
            String s_url_number = s_number;
            String s_url_id = Token.token;
            String c_url_gender = rd.getText().toString();
            String android_id = IntroActivity.android_id;
            try{
                URL url = new URL("http://wimdsadmin.cafe24.com/insertDevice/"+android_id+"/"+s_url_id+"/"+b_url_name+"/"+s_url_number+"/"+mac_url_id+"/"+c_url_name+"/"+ c_url_gender +"/"+c_url_etc+"/");
                result = HttpUtil.httpRequest(url);
                result_object = result.getJSONObject(0);
                result_check = result_object.getBoolean("result");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            Intent resultIntent = new Intent();
            int i = 0 ;
            resultIntent.putExtra("result",result_check);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        else if(v.getId() == R.id.cancel_button)
        {
            Intent intent = new Intent(RegisterActivity.this , ListEddystoneActivity.class);
            intent.putExtra(ListEddystoneActivity.EXTRAS_TARGET_ACTIVITY, EddystoneActivity.class.getName());
            startActivityForResult(intent,1004);
            finish();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1004){
            finish();
        }
    }
}