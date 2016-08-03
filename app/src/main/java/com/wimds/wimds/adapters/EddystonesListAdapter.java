package com.wimds.wimds.adapters;

/**
 * Created by dongdor on 2016. 5. 15..
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.estimote.sdk.Utils;
import com.estimote.sdk.eddystone.Eddystone;
import com.wimds.wimds.R;
import com.wimds.wimds.Util.DeviceInfo;
import com.wimds.wimds.Util.HttpUtil;
import com.wimds.wimds.Util.ListClass;
import com.wimds.wimds.Util.LostDeviceInfo;
import com.wimds.wimds.Util.ParseClass;
import com.wimds.wimds.activities.IntroActivity;


import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Displays basic information about nearable.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */

public class EddystonesListAdapter extends BaseAdapter {

    private ArrayList<Eddystone> eddystones;
    private ArrayList<Eddystone> tempEddystones;
    private LayoutInflater inflater;
    private String TAG = "Eddystone_Wimds";
    private Eddystone my_wimds;
    private JSONArray mac_address_list;
    public EddystonesListAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.eddystones = new ArrayList<>();
    }

    public void setEddystone(Eddystone eddy){
        eddystones.add(eddy);
    }

    /*
    step 1 : ListEddstoneActivity 에서 사용할 경우
    step 2 : FragmentActivity 에서 사용할 경우
    step 3 : GetLostDeviceActivity 에서 사용할 경우
     */
    public void replaceWith(Collection<Eddystone> newEddystones , int step) {
        this.eddystones.clear();

        int count = 0;
        switch(step){
            case 1:
                for (Eddystone eddystone : newEddystones ) {
                    for (int j = 0; j < ListClass.wimds_able_list.length(); j++) {
                        try {
                            JSONObject mac_object = ListClass.wimds_able_list.getJSONObject(j);
                            if (eddystone.macAddress.toStandardString().equals(mac_object.getString("wd_mac_id"))) {
                                eddystones.add(eddystone);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case 2: // Fragment Activity
                for (Eddystone eddystone : newEddystones ) {
                    for (int j = 0; j < ListClass.lost_list.length(); j++) {
                        if(eddystone.macAddress.toStandardString().equals(LostDeviceInfo.b_mac_id))
                        {
                            LostDeviceInfo.rssi = eddystone.rssi;
                        }
                        try {
                            JSONObject mac_object = ListClass.lost_list.getJSONObject(j);
                            if (eddystone.macAddress.toStandardString().equals(mac_object.getString("b_mac_id"))) {
                                String gps_data = eddystone.namespace + eddystone.instance;
                                String gps_lat = gps_data.substring(0,16);
                                String gps_lng = gps_data.substring(16,32);
                                LostDeviceInfo.b_lat = "" + ParseClass.GPStoWGS(ParseClass.hexToByteArray(gps_lat));
                                LostDeviceInfo.b_lng = "" + ParseClass.GPStoWGS(ParseClass.hexToByteArray(gps_lng));
                                URL url = new URL("http://wimdsadmin.cafe24.com/findLostDevice/" + IntroActivity.android_id + "/" + eddystone.macAddress.toStandardString() + "/" +LostDeviceInfo.b_lat+"/"+LostDeviceInfo.b_lng+"/");
                                JSONArray result_list = HttpUtil.httpRequest(url);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        JSONObject mac_object = ListClass.my_list.getJSONObject(0);
                        if(eddystone.macAddress.toStandardString().equals(mac_object.getString("b_mac_id")))
                        {
                            eddystones.add(eddystone);
                            DeviceInfo.rssi = eddystone.rssi;
                            String gps_data = eddystone.namespace + eddystone.instance;
                            String gps_lat = gps_data.substring(0,16);
                            String gps_lng = gps_data.substring(16,32);

                            DeviceInfo.b_lat = "" + ParseClass.GPStoWGS(ParseClass.hexToByteArray(gps_lat));

                            DeviceInfo.b_lng = "" + ParseClass.GPStoWGS(ParseClass.hexToByteArray(gps_lng));

                            URL url = new URL("http://wimdsadmin.cafe24.com/updateDevice/" + DeviceInfo.b_mac_id + "/" + DeviceInfo.b_lat + "/" + DeviceInfo.b_lng );
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                for (Eddystone eddystone : newEddystones ) {
                    for (int j = 0; j < ListClass.lost_list.length(); j++) {
                        try {
                            JSONObject mac_object = ListClass.lost_list.getJSONObject(j);
                            if (eddystone.macAddress.toStandardString().equals(mac_object.getString("b_mac_id"))) {
                                eddystones.add(eddystone);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }

        notifyDataSetChanged();
    }

    public void remove(int index){
        eddystones.remove(index);
    }
    @Override
    public int getCount() {
        return eddystones.size();
    }

    @Override
    public Eddystone getItem(int position) {
        return eddystones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(getItem(position), view);
        return view;
    }

    private void bind(Eddystone eddystone, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();

        holder.macTextView.setText("WIMDS 디바이스");
        //holder.rssiTextView.setText("RSSI: " + eddystone.rssi);
        //holder.eddystoneNamespaceTextView.setText("Namespace: " + (eddystone.namespace == null ? "-" : eddystone.namespace));
        //holder.eddystoneInstanceIdTextView.setText("Instance ID: " + (eddystone.instance == null ? "-" : eddystone.instance));
        holder.eddystoneUrlTextView.setText(String.format("MAC: %s (%.2fm)", eddystone.macAddress.toStandardString(), Utils.computeAccuracy(eddystone)));


    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.eddystone_item, null);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }

    static class ViewHolder {
        final TextView macTextView;
        final TextView rssiTextView;
        final TextView eddystoneNamespaceTextView;
        final TextView eddystoneInstanceIdTextView;
        final TextView eddystoneUrlTextView;

        ViewHolder(View view) {
            macTextView = (TextView) view.findViewWithTag("mac");
            rssiTextView = (TextView) view.findViewWithTag("rssi");
            eddystoneNamespaceTextView = (TextView) view.findViewWithTag("eddystone_namespace");
            eddystoneInstanceIdTextView = (TextView) view.findViewWithTag("eddystone_instance_id");
            eddystoneUrlTextView = (TextView) view.findViewWithTag("eddystone_url");
        }
    }


}
