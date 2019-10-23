package com.sfmap.map.demo.basic;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapController.OnMarkerClickListener;
import com.sfmap.api.maps.MapController.OnPOIClickListener;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.model.Poi;
import com.sfmap.map.demo.R;

/**  
 * 介绍底图poi点击事件
 */
public class PoiClickActivity extends Activity implements OnPOIClickListener,
		OnMarkerClickListener {

	private MapView mMapView;

	private MapController lMap;
	
 

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poiclick);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		try {
			lMap = mMapView.getMap();
			lMap.setOnPOIClickListener(this);
			lMap.setOnMarkerClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPOIClick(Poi poi) {
		lMap.clear();
		MarkerOptions markOptiopns = new MarkerOptions();
		markOptiopns.position(poi.getCoordinate());
		TextView textView = new TextView(getApplicationContext());
		textView.setText(poi.getName());
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.BLACK);
		textView.setBackgroundResource(R.drawable.marker_custom_info_bubble);
		markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));
		 lMap.addMarker(markOptiopns);
		

	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		return false;
	}

	/**
	 * 方法重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 方法重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}
}
