package com.wimds.wimds.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.wimds.wimds.GCM.RegistrationIntentService;
import com.wimds.wimds.R;
import com.wimds.wimds.Util.DeviceInfo;
import com.wimds.wimds.Util.HttpUtil;
import com.wimds.wimds.Util.ListClass;
import com.wimds.wimds.Util.Token;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;


public class IntroActivity extends Activity {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "IntrActivity";

    Handler h;
    public static String android_id;
    URL url;
    boolean result = false;

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String token = intent.getStringExtra("token");
            }

        };
    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        9000).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //인트로화면이므로 타이틀바를 없앤다
        setContentView(R.layout.activity_intro);
        registBroadcastReceiver();
        android_id = android.provider.Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        getInstanceIdToken();
        h = new Handler();
        h.postDelayed(mrun, 5000);
    }

    Runnable mrun = new Runnable(){
        @Override
        public void run(){
            try {
                url = new URL("http://wimdsadmin.cafe24.com/checkAndroid_id/" + IntroActivity.android_id+"/"+Token.token);
                JSONArray jarray = HttpUtil.httpRequest(url);
                JSONObject o = jarray.getJSONObject(0);
                result = o.getBoolean("result");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL("http://wimdsadmin.cafe24.com/getLostDevice/" + IntroActivity.android_id);
                ListClass.lost_list = HttpUtil.httpRequest(url);
            } catch (Exception e) {
            }

            if(result == true){
                try {
                    url = new URL("http://wimdsadmin.cafe24.com/getMyMac/" + IntroActivity.android_id);
                    JSONArray jarray = HttpUtil.httpRequest(url);
                    for (int i = 0 ; i < jarray.length() ; i ++){
                        JSONObject o = jarray.getJSONObject(i);
                            DeviceInfo.b_stat = o.getInt("b_stat");
                            DeviceInfo.b_name = o.getString("b_name");
                            DeviceInfo.b_mac_id = o.getString("b_mac_id");
                            DeviceInfo.c_name = o.getString("c_name");
                            DeviceInfo.c_gender = o.getString("c_gender");
                            DeviceInfo.c_etc = o.getString("c_etc");
                            DeviceInfo.s_number = o.getString("s_number");
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(IntroActivity.this , FragmentActivity.class);
                if(DeviceInfo.b_stat == 1){
                    try {
                        url = new URL("http://wimdsadmin.cafe24.com/checkLostDevice/" + DeviceInfo.b_mac_id);
                        JSONArray jarray = HttpUtil.httpRequest(url);
                        if(jarray.length() != 0) {
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject o = jarray.getJSONObject(i);
                                DeviceInfo.b_lat = o.getString("b_lat");
                                DeviceInfo.b_lng = o.getString("b_lng");
                                DeviceInfo.using = true;
                            }
                            intent.putExtra("findDevice","true");
                        }else{
                            intent.putExtra("findDevice","false");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else{ // TEST
                    DeviceInfo.b_lat = "37.551598";
                    DeviceInfo.b_lng = "127.074311";
                }
                    startActivityForResult(intent, 1004);


            }

            if(result == false){
                Intent intent = new Intent(IntroActivity.this , ListEddystoneActivity.class);
                intent.putExtra(ListEddystoneActivity.EXTRAS_TARGET_ACTIVITY, EddystoneActivity.class.getName());
                startActivityForResult(intent, 1004);
            }

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        h.removeCallbacks(mrun);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1004){
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            finish();
        }
    }

}