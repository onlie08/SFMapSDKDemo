package com.sfmap.map.demo.cloud;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.map.demo.R;

import java.util.ArrayList;

/**
 * 云检索
 */
public class CloudSearchActivity extends FragmentActivity {
    private MapView mapView;
    private MapController lMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_save);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();

        Bundle bundle = getIntent().getExtras();
        ArrayList<CloudPointResult> cloudDatasetItemList = (ArrayList<CloudPointResult>) bundle.getSerializable("cloudDataItem");
        //draw
        for(int n=0;n<cloudDatasetItemList.size();n++){
            CloudPointResult cloudItem = cloudDatasetItemList.get(n);
            LatLng latLng = new LatLng(cloudItem.mLat,cloudItem.mLon);
            CloudUtil.addMarker(lMap,CloudSearchActivity.this, R.drawable.bubble_wrongcheck, latLng);
            //定位到第一个坐标上
            if(n == 0){
                lMap.setMapCenter(latLng);
            }
        }
    }

    /**
     * 初始化
     */
    private void init() {
        try {
            if (lMap == null) {
                lMap = mapView.getMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
