package com.wimds.wimds.Util;

import android.os.StrictMode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by han on 2016-05-16.
 */
public class HttpUtil {

    public void HttpUtil(){

    }

    public static JSONArray httpRequest(URL url){
        HttpURLConnection urlConnection = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        JSONArray result = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while( (inputLine = in.readLine()) != null ){
                try {
                    JSONObject jo = new JSONObject(inputLine);
                    result = jo.getJSONArray("result");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
