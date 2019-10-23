package com.sfmap.map.demo.overlay;

import android.app.Activity;
import android.os.Bundle;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.GroundOverlay;
import com.sfmap.api.maps.model.GroundOverlayOptions;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.map.demo.R;

/**
 * sfmapMap地图中简单介绍一些GroundOverlay的用法.
 */
public class GroundOverlayActivity extends Activity {

	private MapController lMap;
	private MapView mapview;
	private GroundOverlay groundoverlay;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groundoverlay);
		mapview = (MapView) findViewById(R.id.map);
		mapview.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化lMap对象
	 */
	private void init() {
		try {
			if (lMap == null) {
                lMap = mapview.getMap();
                addOverlayToMap();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 往地图上添加一个groundoverlay覆盖物
	 */
	private void addOverlayToMap() {
		lMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.992806,
				116.396887), 15));// 设置当前地图显示为北京市恭王府
//		LatLngBounds bounds = new LatLngBounds.Builder()
//				.include(new LatLng(39.935029, 116.384377))
//				.include(new LatLng(39.939577, 116.388331)).build();
		LatLngBounds bounds = new LatLngBounds.Builder()
				.include(new LatLng(39.99590, 116.3922))
				.include(new LatLng(39.99000, 116.39957)).build();
		groundoverlay = lMap.addGroundOverlay(new GroundOverlayOptions()
				.anchor(0.5f, 0.5f)
				.transparency(0.1f)
				.image(BitmapDescriptorFactory
						.fromResource(R.drawable.sfmap_groundoverlay))
				.positionFromBounds(bounds));
	}

	/**
	 * 方法必须重写
	 */
	protected void onResume() {
		super.onResume();
		mapview.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapview.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapview.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
	}
}
