package com.sfmap.map.demo.cloud;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.ToastUtil;

/**
 * 云存储
 */
public class CloudSaveActivity extends FragmentActivity implements MapController.OnMapLongClickListener,MapController.OnMapTouchListener{
    private MapView mapView;
    public MapController lMap;
    CloudSaveFragment oneFragment = new CloudSaveFragment();
    //是否允许长按地图
    public boolean longPress = false;
    public float y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_save);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        init();
        //添加fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.container, oneFragment, CloudSaveFragment.class.getName());
        transaction.commit();
    }

    /**
     * 初始化
     */
    private void init() {
        if (lMap == null) {
            try {
                lMap = mapView.getMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        lMap.setOnMapLongClickListener(this);
        lMap.setOnMapTouchListener(this);
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

    /**
     * 在地图上增加单点marker
     *
     * @param latLng
     */
    public Marker addSinglePoiMarker(LatLng latLng) {
        return CloudUtil.addMarker(lMap,CloudSaveActivity.this, R.drawable.b_poi_hl, latLng);
    }

    /**
     * 地图长按事件
     * @param point 用户所点击的地理坐标。
     */
    @Override
    public void onMapLongClick(LatLng point) {
        if(!longPress){
            ToastUtil.show(CloudSaveActivity.this,"请点击创建点数据，进行数据创建。");
            return;
        }

        CloudUtil.removeMapAllMark(lMap);
        addSinglePoiMarker(point);
        oneFragment.hideEdit();
        int ly = oneFragment.screentLocation();
        if(y < ly){
            //重新设置位置
            lMap.setMapCenter(getCenterLatLng(point));
        }
        oneFragment.longPressMapShow(point);
    }

    /**
     * 地图触摸事件
     * @param event 系统自带的移动事件。
     */
    @Override
    public void onTouch(MotionEvent event) {
        y = event.getY();
    }

    /**
     * 坐标向下偏移200的px
     * @param p
     * @return
     */
    public LatLng getCenterLatLng(LatLng p){
        Point point = lMap.getProjection().toScreenLocation(p);
        point.offset(0,-200);
        return lMap.getProjection().fromScreenLocation(point);
    }
}
