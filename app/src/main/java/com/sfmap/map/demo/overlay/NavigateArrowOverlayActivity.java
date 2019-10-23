package com.sfmap.map.demo.overlay;

import android.app.Activity;
import android.os.Bundle;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.NavigateArrowOptions;
import com.sfmap.map.demo.R;

/**
 * sfmapMap地图中介绍如何显示一个基本地图
 */
public class NavigateArrowOverlayActivity extends Activity {
	private MapView mapView;
	private MapController lMap;
	private LatLng latlng1 = new LatLng(39.9871, 116.4789);
	private LatLng latlng2 = new LatLng(39.9879, 116.4777);
	private LatLng latlng3 = new LatLng(39.9897, 116.4797);
	private LatLng latlng4 = new LatLng(39.9887, 116.4813);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basicmap);
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

                lMap.addNavigateArrow(new NavigateArrowOptions().add(latlng1,
                        latlng2, latlng3, latlng4).width(20));
                lMap.moveCamera(CameraUpdateFactory
                        .newCameraPosition(new CameraPosition(new LatLng(39.9875,
                                116.48047), 16f, 38.5f, 300)));

            }
		} catch (Exception e) {
			e.printStackTrace();
		}
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
