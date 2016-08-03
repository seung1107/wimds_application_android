package com.wimds.wimds.Util;

import com.wimds.wimds.activities.IntroActivity;

import org.json.JSONObject;

import java.net.URL;

/**
 * Created by Johnny K on 2016-05-23.
 */
public class DeviceInfo {
    public static String b_name;
    public static int b_stat;
    public static String b_mac_id;
    public static String s_number;
    public static String c_name;
    public static String c_gender;
    public static String c_etc;
    public static String b_lat;
    public static String b_lng;
    public static int rssi;
    public static boolean using = false;

    public static void getMyDevice(){
        try {
            URL url = new URL("http://wimdsadmin.cafe24.com/getMyMac/" + IntroActivity.android_id);
            ListClass.my_list = HttpUtil.httpRequest(url);
            for (int i = 0 ; i < ListClass.my_list.length() ; i ++){
                JSONObject o = ListClass.my_list.getJSONObject(i);
                DeviceInfo.b_stat = o.getInt("b_stat");
                DeviceInfo.b_name = o.getString("b_name");
                DeviceInfo.b_mac_id = o.getString("b_mac_id");
                DeviceInfo.c_gender = o.getString("c_gender");
                DeviceInfo.c_etc = o.getString("c_etc");
                DeviceInfo.s_number = o.getString("s_number");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
