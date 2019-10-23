package com.sfmap.map.demo.offlinemap;

import android.app.Activity;
import android.os.Bundle;

import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.map.demo.R;

public class BuiltinOfflineMapActivity extends Activity {

    private MapView mapView;
    private MapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_builtin_offline_map);

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        mapController = mapView.getMap();
        //必须将离线地图数据放到 外部存储 /sfmap/data/off/目录里面
        mapController.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.816365, 108.366569), 16));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
