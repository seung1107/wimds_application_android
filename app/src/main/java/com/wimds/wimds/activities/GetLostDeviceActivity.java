package com.wimds.wimds.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.eddystone.Eddystone;
import com.wimds.wimds.R;
import com.wimds.wimds.Util.HttpUtil;
import com.wimds.wimds.Util.ListClass;
import com.wimds.wimds.Util.LostDeviceInfo;
import com.wimds.wimds.adapters.EddystonesListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by dongdor on 2016. 5. 23..
 */
public class GetLostDeviceActivity extends AppCompatActivity{

    public static final int GET_LOST_DEVICE = 2002;
    private JSONArray mac_list;
    private BeaconManager beaconManager;
    private EddystonesListAdapter adapter;
    private boolean check_list = false;
    private Region region;
    public static String lost_LAT;
    public static String lost_LNG;
    private JSONArray result_lost;
    String android_id = IntroActivity.android_id;

    public static final String PAGE_VIEW = "PAGET_VIEW";
    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String LOST_DEVICE = "LOST_DEVICE";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //인트로화면이므로 타이틀바를 없앤다
        setContentView(R.layout.activity_lostdevice);
        this.setFinishOnTouchOutside(false);
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
        try
        {
            URL url = new URL("http://wimdsadmin.cafe24.com/getLostDevice/" + android_id);
            ListClass.lost_list = HttpUtil.httpRequest(url);
        }
        catch (Exception e)
        {

        }

        // Configure device list.
        adapter = new EddystonesListAdapter(this);
        ListView list = (ListView) findViewById(R.id.lost_device_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(createOnItemClickListener());
        beaconManager = new BeaconManager(this);
    }

    public void cancel_lost_device_list(View v){
//        Intent intent = new Intent(getBaseContext(), FragmentActivity.class);
//        startActivityForResult(intent , 1004);
        beaconManager.stopRanging(region);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.stopRanging(region);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.stopRanging(region);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            startScanning();
        }

    }

    private void startScanning() {


        adapter.replaceWith(Collections.<Eddystone>emptyList(), 2);

        beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
            @Override public void onEddystonesFound(List<Eddystone> eddystones) {
                adapter.replaceWith(eddystones, 3);
            }


        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                beaconManager.startEddystoneScanning();
            }
        });
    }






    private AdapterView.OnItemClickListener createOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {

            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null) {
                    try {

                        Class<?> clazz = Class.forName(getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY));

                        Intent intent = new Intent(getBaseContext(), FragmentActivity.class);
                        LostDeviceInfo.using = true;
                        LostDeviceInfo.b_mac_id = adapter.getItem(position).macAddress.toStandardString();
                        LostDeviceInfo.b_lat = "37.547716";
                        LostDeviceInfo.b_lng = "127.073830";
                        LostDeviceInfo.rssi = adapter.getItem(position).rssi;
                        try {
                            URL url = new URL("http://wimdsadmin.cafe24.com/getLostDeviceInfo/" + LostDeviceInfo.b_mac_id);
                            result_lost = HttpUtil.httpRequest(url);
                            for (int i = 0 ; i < result_lost.length() ; i ++){
                                JSONObject o = result_lost.getJSONObject(i);
                                LostDeviceInfo.b_name = o.getString("b_name");
                                LostDeviceInfo.c_name = o.getString("c_name");
                                LostDeviceInfo.c_gender = o.getString("c_gender");
                                LostDeviceInfo.c_etc = o.getString("c_etc");
                                LostDeviceInfo.s_number = o.getString("s_number");
                            }
                        } catch (Exception e) {
                        }
                        intent.putExtra("getLostDevice","true");
                        intent.putExtra(PAGE_VIEW,1);
                        startActivityForResult(intent , 1004);
                        finish();

                    } catch (Exception e) {


                    }
                }
            }
        };
    }



}