package com.wimds.wimds.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wimds.wimds.R;
import com.wimds.wimds.Util.DeviceInfo;
import com.wimds.wimds.Util.LostDeviceInfo;

/**
 * Created by han on 2016-05-26.
 */
public class Fragment_map extends Fragment{

    GoogleMap map;
    View v;
    View w_v;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // 중복뷰 제거
    // 프래그먼트가 화면에서 사라질때, 컨테이너 뷰(parent)에서 프래그먼트(v)를 제거하여
    // 중복 추가되는 것을 방지하는 원리
    // onDestroyView : 프래그먼트의 계층 뷰가 제거 될때 호출
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if( v != null ) {
            ViewGroup parent = (ViewGroup)v.getParent();
            if(parent != null) {
                parent.removeView(v);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (LostDeviceInfo.using) {
            try {
                LatLng lostDevice = new LatLng(Double.parseDouble(LostDeviceInfo.b_lat), Double.parseDouble(LostDeviceInfo.b_lng));
                v = inflater.inflate(R.layout.fragment_map, container, false);
                w_v = inflater.inflate(R.layout.infowindow, container, false);

                // Get a handle to the Map Fragment
                map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
                map.setMyLocationEnabled(true);
                MarkerOptions device_location = new MarkerOptions();
                device_location.position(lostDevice);
                device_location.title("미아");
                device_location.snippet("미아 이름 : " + LostDeviceInfo.c_name + "\n미아 성별 : " + LostDeviceInfo.c_gender + "\n미아 정보 : " + LostDeviceInfo.c_etc);

                Marker marker = map.addMarker(device_location);
                marker.showInfoWindow();
                map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        TextView tvTitle = ((TextView)w_v.findViewById(R.id.title));
                        tvTitle.setText(marker.getTitle());
                        TextView tvSnippet = ((TextView)w_v.findViewById(R.id.snippet));
                        tvSnippet.setText(marker.getSnippet());
                        return w_v;
                    }
                });

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lostDevice, 15));

            } catch (InflateException e) {

            }

            return v;
        } else {
            if (DeviceInfo.using) {
                try {
                    LatLng findDevice = new LatLng(Double.parseDouble(DeviceInfo.b_lat), Double.parseDouble(DeviceInfo.b_lng));
                    v = inflater.inflate(R.layout.fragment_map, container, false);
                    map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
                    map.setMyLocationEnabled(true);
                    MarkerOptions device_location = new MarkerOptions();
                    device_location.position(findDevice);
                    device_location.title("미아");

                    map.addMarker(device_location).showInfoWindow();
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(findDevice, 15));

                } catch (InflateException e) {

                }
                return v;
            }else{
                try {
                    LatLng findDevice = new LatLng(Double.parseDouble(DeviceInfo.b_lat), Double.parseDouble(DeviceInfo.b_lng));
                    v = inflater.inflate(R.layout.fragment_map, container, false);
                    map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
                    map.setMyLocationEnabled(true);
                    MarkerOptions device_location = new MarkerOptions();
                    device_location.position(findDevice);
                    device_location.title("내 아이");

                    map.addMarker(device_location).showInfoWindow();
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(findDevice, 15));

                } catch (InflateException e) {

                }

                return v;
            }

        }
    }
}