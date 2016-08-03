package com.wimds.wimds.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.eddystone.Eddystone;
import com.wimds.wimds.R;
import com.wimds.wimds.Util.HttpUtil;
import com.wimds.wimds.Util.ListClass;
import com.wimds.wimds.adapters.EddystonesListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by dongdor on 2016. 5. 15..
 */
public class ListEddystoneActivity extends BaseActivity {

    public static final int REQUEST_CODE_REGISTER = 1001;
    private static final String TAG = ListEddystoneActivity.class.getSimpleName();
    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_EDDYSTONE = "extrasEddystone";
    private JSONArray mac_list;
    private BeaconManager beaconManager;
    private EddystonesListAdapter adapter;
    private Iterator<String> mac_name;
    private boolean check_list = false;
    private boolean server_check = true;
    private Region region;

    @Override
    protected int getLayoutResId() {

        return R.layout.main;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            URL url = new URL("http://wimdsadmin.cafe24.com/getMac_id_list/");
            ListClass.wimds_able_list = HttpUtil.httpRequest(url);
        }
        catch (Exception e)
        {
            for(int i = 0;i<adapter.getCount();i++)
            {
                adapter.remove(i);
            }
            e.printStackTrace();
        }

        adapter = new EddystonesListAdapter(this);
        ListView list = (ListView) findViewById(R.id.device_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(createOnItemClickListener());
        beaconManager = new BeaconManager(this);
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

    }

    public boolean onKeyDown( int KeyCode, KeyEvent event )
    {
        if((KeyCode == KeyEvent.KEYCODE_BACK)){
            new AlertDialog.Builder(ListEddystoneActivity.this)
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
    protected void onDestroy() {
        beaconManager.stopRanging(region);
        beaconManager.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            startScanning();
        }
    }

    @Override
    protected void onStop() {
        beaconManager.stopRanging(region);
        beaconManager.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.stopRanging(region);
    }

    private void startScanning() {
        toolbar.setSubtitle("WIMDS 디바이스 검색중...");
        adapter.replaceWith(Collections.<Eddystone>emptyList(), 1);
        beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
            @Override public void onEddystonesFound(List<Eddystone> eddystones) {
                adapter.replaceWith(eddystones, 1);
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
            //parent : click이 일어나는 AdapterView를 보여준다
            //view : adpaterView중에 클릭된 apaterView이다
            //position : 말그대로 그 뷰의 포시션이다
            //id : id
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null) {
                    try {
                        Class<?> clazz = Class.forName(getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY));
                        Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                        intent.putExtra(EXTRAS_EDDYSTONE, adapter.getItem(position));
                        startActivityForResult(intent,REQUEST_CODE_REGISTER);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_CODE_REGISTER == requestCode){
            Intent i = new Intent(ListEddystoneActivity.this , FragmentActivity.class);
            startActivity(i);
        }
    }
}