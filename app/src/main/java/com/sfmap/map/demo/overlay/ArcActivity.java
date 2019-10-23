package com.sfmap.map.demo.overlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.ArcOptions;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.Constants;

/**
 * sfmapMap地图中简单介绍一些Arc的用法.
 */
public class ArcActivity extends Activity {

	private MapController lMap;
	private MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arc);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化lMap对象
	 */
	private void init() {

		try {
			if (lMap == null) {
                lMap = mapView.getMap();
                setUpMap();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpMap() {

		lMap.moveCamera(CameraUpdateFactory.zoomTo(4));
		lMap.setMapTextZIndex(2);

		// 绘制一个经过乌鲁木齐经过北京到哈尔滨弧形
		ArcOptions arcOptions = new ArcOptions().point(
				new LatLng(41.053643, 86.665613), Constants.BEIJING,
				new LatLng(47.941388, 124.196099)).strokeColor(Color.RED);
		lMap.addArc(arcOptions);

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

}
