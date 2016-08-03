package com.wimds.wimds.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wimds.wimds.R;
import com.wimds.wimds.Util.DeviceInfo;
import com.wimds.wimds.Util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;


public class DeleteActivity extends AppCompatActivity implements View.OnClickListener {
    private JSONArray result;
    private JSONObject result_object;
    private Boolean result_check;
    private String b_mac_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.delete_button)
        {
            try{
                b_mac_id = DeviceInfo.b_mac_id;
                URL url = new URL("http://wimdsadmin.cafe24.com/deleteDevice/"+b_mac_id+"/");
                result = HttpUtil.httpRequest(url);
                result_object = result.getJSONObject(0);
                result_check = result_object.getBoolean("result");

               if(result_check == true){
                    Intent intent = new Intent(DeleteActivity.this , IntroActivity.class);
                    startActivity(intent);
                }
               else{
                   finish();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if(v.getId() == R.id.cancel_button)
        {
            Toast.makeText(getApplicationContext(), "취소되었습니다", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        }
    }
}
