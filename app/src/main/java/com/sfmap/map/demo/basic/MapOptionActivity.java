package com.sfmap.map.demo.basic;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.sfmap.map.demo.util.Constants;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapOptions;
import com.sfmap.api.maps.SupportMapFragment;
import com.sfmap.api.maps.model.CameraPosition;

/**
 * 通过Java代码添加一个SupportMapFragment对象
 */
public class MapOptionActivity extends FragmentActivity {

	private static final String MAP_FRAGMENT_TAG = "map";
	static final CameraPosition wuhan = new CameraPosition.Builder()
			.target(Constants.WUHAN).zoom(18).bearing(0).tilt(30).build();
	private MapController lMap;
	private SupportMapFragment lMapFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	/**
	 * 初始化lMap对象
	 */
	private void init() {
		MapOptions aOptions = new MapOptions();
		aOptions.zoomGesturesEnabled(false);// 禁止通过手势缩放地图
		aOptions.scrollGesturesEnabled(false);// 禁止通过手势移动地图
		aOptions.tiltGesturesEnabled(false);// 禁止通过手势倾斜地图
		aOptions.camera(wuhan);
		if (lMapFragment == null) {
			lMapFragment = SupportMapFragment.newInstance(aOptions);
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(android.R.id.content, lMapFragment,
					MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		initMap();
	}

	/**
	 * 初始化lMap对象
	 */
	private void initMap() {
		if (lMap == null) {
				lMap = lMapFragment.getMap();
		}
	}
}
