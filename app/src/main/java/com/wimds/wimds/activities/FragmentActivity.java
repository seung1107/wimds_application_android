package com.wimds.wimds.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.eddystone.Eddystone;
import com.wimds.wimds.R;
import com.wimds.wimds.Util.DeviceInfo;
import com.wimds.wimds.Util.HttpUtil;
import com.wimds.wimds.Util.ListClass;
import com.wimds.wimds.Util.LostDeviceInfo;
import com.wimds.wimds.adapters.EddystonesListAdapter;
import com.wimds.wimds.adapters.SetFragmentPagerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class FragmentActivity extends AppCompatActivity {
    private JSONArray result_lost;
    private Handler h;
    private Region region;
    private ViewPager viewPager;
    private String TAG = "Wimds";
    private JSONArray mac_list;
    private EddystonesListAdapter adapter;
    private BeaconManager beaconManager;
    URL url;
    private boolean check_my_android = true;
    String lostParentPhone=null;
    Button btn_lost = null;
    Button btn_cancel = null ;
    Button btn_found = null;
    TextView lostState = null;
    TextView currentDevice = null;
    TextView currentName = null; //추가
    public static int rsssi=0;
    private int check_rssi = 0;
    private int count_rssi = 0;
    static int LostState; //미아 신고가 된 상태인지 아닌지 확인
    private boolean result=false;
    private String myAndroid_id= Settings.Secure.ANDROID_ID;
    ImageView img_circle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        DeviceInfo.getMyDevice();
        DeviceInfo.b_lat = "37.551598";
        DeviceInfo.b_lng = "127.074311";
        adapter = new EddystonesListAdapter(this);
        beaconManager = new BeaconManager(this);
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
        LostState = DeviceInfo.b_stat;
        h = new Handler();
        h.postDelayed(mrun, 5000);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SetFragmentPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabsStrip.setViewPager(viewPager);
        try{
            Intent i = getIntent();
            if(i.getStringExtra("findDevice").equals("true")){
                viewPager.setCurrentItem(1);
            }
        }catch(Exception e){

        }
        try{
            Intent i = getIntent();
           if(i.getStringExtra("getLostDevice").equals("true")){
                viewPager.setCurrentItem(1);
            }
        }catch(Exception e){

        }

    }

    public void setButton(int state){
        btn_lost = (Button) findViewById(R.id.btn_notify);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
    }

    //내 아이 찾기
    public void btnMyChildClicked(View v){
        Toast.makeText(getApplicationContext(), "내 아이 찾기", Toast.LENGTH_SHORT).show();
        LostDeviceInfo.using = false;
        DeviceInfo.b_lat = "37.551598";
        DeviceInfo.b_lng = "127.074311";
        viewPager.setAdapter(new SetFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(1);

    }

    //주변 미아 찾기
    public void btnLostChildrenClicked(View v){
        Toast.makeText(getApplicationContext(), "주변 미아 찾기", Toast.LENGTH_SHORT).show();
        //beaconManager.stopRanging(region);
        if(ListClass.lost_list.length() != 0){
            Intent intent = new Intent(getApplicationContext(),GetLostDeviceActivity.class);
            intent.putExtra(ListEddystoneActivity.EXTRAS_TARGET_ACTIVITY, EddystoneActivity.class.getName());
            startActivityForResult(intent, 1004);
        }else{
            Toast.makeText(getApplicationContext(), "등록된 미아가 없습니다.", Toast.LENGTH_SHORT).show();
           // startScanning();
        }

    }

    //지도 탭에서 미아 찾음
    public void btnChildrenFoundClicked(View v){

        if(LostDeviceInfo.rssi == 0){
            Toast.makeText(getApplicationContext(), "미아 없습니다.", Toast.LENGTH_SHORT).show();
        }
        else if(LostDeviceInfo.rssi >= -80)
        {
        btn_found = (Button)findViewById(R.id.btnFound);

            lostParentPhone = "tel:"+LostDeviceInfo.s_number;
            new AlertDialog.Builder(FragmentActivity.this)
                    .setTitle("WIMDS")
                    .setMessage("미아의 부모님에게 전화를 하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(lostParentPhone));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("아니오", null)
                    .show();
        }else{
            Toast.makeText(getApplicationContext(), "미아가 멀리있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deviceModifyClicked(View v) //'설정'탭에서 '디바이스 수정' 버튼 클릭시
    {
        Intent intent = new Intent(getApplicationContext(),ModifyActivity.class);
        startActivity(intent);
    }

    //'설정'탭에서 '디바이스 삭제' 버튼 클릭시
    public void deviceDeleteClicked(View v)
    {
        Intent intent = new Intent(getApplicationContext(),DeleteActivity.class);
        startActivity(intent);
    }

    //'홈'탭에서 '미아 신고' 버튼 클릭시
    public void btnNotifyClicked(View v) {
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_lost = (Button) findViewById(R.id.btn_notify);
        new AlertDialog.Builder(FragmentActivity.this)
                .setTitle("WIMDS")
                .setMessage("미아신고를 하시겠습니까?\n제3자에게 개인정보의 제공을 동의하게됩니다.")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try
                        {
                            URL url = new URL("http://wimdsadmin.cafe24.com/registerLostDevice/"+IntroActivity.android_id);
                            mac_list = HttpUtil.httpRequest(url);
                            for (int i = 0 ; i < mac_list.length() ; i ++){
                                JSONObject o = mac_list.getJSONObject(i);
                                result = o.getBoolean("result");
                                if(result){
                                    LostState = 1; //신고완료면 로컬에서 신고된 상태인지 확인할 수 있게 LostState를 True. 1이면 미아
                                    DeviceInfo.using = true;
                                    Toast.makeText(getApplicationContext(), "미아 신고 되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("아니오", null)
                .show();

    }

    public void btnCancelClicked(View v) { //'홈'탭에서 '미아 취소' 버튼 클릭시
        btn_lost = (Button) findViewById(R.id.btn_notify);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        try
        {
            URL url = new URL("http://wimdsadmin.cafe24.com/cancelLostDevice/"+IntroActivity.android_id);
            mac_list = HttpUtil.httpRequest(url);
            for (int i = 0 ; i < mac_list.length() ; i ++){
                JSONObject o = mac_list.getJSONObject(i);
                result = o.getBoolean("result");
                if(result)
                {
                    LostState = 0; //신고취소 완료면 로컬에서 취소된 상태인지 확인할 수 있게 LostState를 false.
                    Toast.makeText(this.getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    DeviceInfo.using = false;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public boolean onKeyDown( int KeyCode, KeyEvent event )
    {
        if((KeyCode == KeyEvent.KEYCODE_BACK)){
            new AlertDialog.Builder(FragmentActivity.this)
                    .setTitle("WIMDS")
                    .setMessage("WIMDS를 종료 하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            /*setResult(1004);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            finish();*/
                            beaconManager.stopRanging(region);
                            finishAffinity(); // 모든 Activity 종료
                        }
                    })
                    .setNegativeButton("아니오", null)
                    .show();
        }
        return super.onKeyDown(KeyCode, event);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
               startScanning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.stopRanging(region);
    }

    Runnable mrun = new Runnable(){
        @Override
        public void run(){
            try {
                URL url = new URL("http://wimdsadmin.cafe24.com/getLostDevice/" + IntroActivity.android_id);
                ListClass.lost_list = HttpUtil.httpRequest(url);
                h.postDelayed(mrun, 5000);
            } catch (Exception e) {
            }
        }
    };

    public void btnFoundClicked(View v) { //'홈'탭에서 '미아 찾음' 버튼 클릭시
        Toast.makeText(getApplicationContext(), "미아 찾음!", Toast.LENGTH_SHORT).show();
    }

    private void startScanning() {
        //adapter.replaceWith(Collections.<Eddystone>emptyList(), 1);
        beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
            @Override public void onEddystonesFound(List<Eddystone> eddystones) {
                adapter.replaceWith(eddystones, 2);

                if(adapter.getCount() == 0) DeviceInfo.rssi = 0;
                try {
                    img_circle = (ImageView) findViewById(R.id.circle_blue);

                    // Prepare the View for the animation
                    img_circle.setVisibility(View.VISIBLE);
                    img_circle.setAlpha(0.5f);
                    // Start the animation
                    img_circle.animate()
                            .alpha(1.0f);
                }catch(Exception e) {

                }

                try{
                    TextView rssiValue = (TextView) findViewById(R.id.valueRssi);
                    lostState = (TextView) findViewById(R.id.LostState);
                    currentDevice = (TextView) findViewById(R.id.currentDevice);
                    currentDevice.setText("Device Name : "+DeviceInfo.b_name);
                    currentName = (TextView) findViewById(R.id.currentName);
                    currentName.setText("Child Name : " + DeviceInfo.c_name);
                    if(DeviceInfo.rssi == 0) {
                        img_circle.setImageDrawable(getResources().getDrawable(R.drawable.circle_red_2));
                        rssiValue.setText("아이와 연결이 되지 않습니다.");
                    }
                    else if(DeviceInfo.rssi >= -70) {
                        img_circle.setImageDrawable(getResources().getDrawable(R.drawable.circle_green));
                        rssiValue.setText("아이가 근처에 있습니다.");
                    }
                    else if(DeviceInfo.rssi >= -80) {
                        img_circle.setImageDrawable(getResources().getDrawable(R.drawable.circle_orange));
                        rssiValue.setText("아이가 멀어지고 있습니다.");
                    }
                    else if(DeviceInfo.rssi < -90) {
                        img_circle.setImageDrawable(getResources().getDrawable(R.drawable.circle_red_2));
                        rssiValue.setText("아이를 찾을 수 없습니다.");
                    }else{
                        img_circle.setImageDrawable(getResources().getDrawable(R.drawable.circle_red_2));
                        rssiValue.setText("아이와 연결이 되지 않습니다.");
                    }
                    if(LostState == 1){
                        lostState.setText("미아 상태");
                    }
                    else if(LostState ==0){
                        lostState.setText(" ");
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                beaconManager.startEddystoneScanning();
            }
        });
    }

}

