package com.sfmap.map.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.sfmap.map.demo.util.Constants;
import com.sfmap.map.demo.util.ToastUtil;
import com.sfmap.map.demo.R;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.api.maps.model.VisibleRegion;

/**
 * 立得地图中简单介绍OnMapClickListener, OnMapLongClickListener,
 * OnCameraChangeListener三种监听器用法
 */

public class EventsActivity extends Activity implements MapController.OnMapClickListener,
		MapController.OnMapLongClickListener, MapController.OnCameraChangeListener, MapController.OnMapTouchListener {
	private MapController Map;
	private MapView mapView;
	private TextView mTapTextView;
	private TextView mCameraTextView;
	private TextView mTouchTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化Map对象
	 */
	private void init() {
		if (Map == null) {
				Map = mapView.getMap();
			setUpMap();
		}
		mTapTextView = (TextView) findViewById(R.id.tap_text);
		mCameraTextView = (TextView) findViewById(R.id.camera_text);
		mTouchTextView = (TextView) findViewById(R.id.touch_text);
	}

	/**
	 * Map添加一些事件监听器
	 */
	private void setUpMap() {

        Map.setOnMapClickListener(this);
        Map.setOnMapLongClickListener(this);
        Map.setOnCameraChangeListener(this);
        Map.setOnMapTouchListener(this);
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
	 * 对单击地图事件回调
	 */
	@Override
	public void onMapClick(LatLng point) {
		mTapTextView.setText("tapped, marker_point=" + point);
	}

	/**
	 * 对长按地图事件回调
	 */
	@Override
	public void onMapLongClick(LatLng point) {
		mTapTextView.setText("long pressed, marker_point=" + point);
	}

	/**
	 * 对正在移动地图事件回调
	 */
	@Override
	public void onCameraChange(CameraPosition cameraPosition) {
		mCameraTextView.setText("onCameraChange:" + cameraPosition.toString());
	}

	/**
	 * 对移动地图结束事件回调
	 */
	@Override
	public void onCameraChangeFinish(CameraPosition cameraPosition) {
		mCameraTextView.setText("onCameraChangeFinish:"
				+ cameraPosition.toString());
		VisibleRegion visibleRegion = Map.getProjection().getVisibleRegion(); // 获取可视区域、

		LatLngBounds latLngBounds = visibleRegion.latLngBounds;// 获取可视区域的Bounds
		boolean isContain = latLngBounds.contains(Constants.WUHAN);// 判断武汉经纬度是否包括在当前地图可见区域
		if (isContain) {
			ToastUtil.show(EventsActivity.this, "武汉市在当前地图可见区域内");
		} else {
			ToastUtil.show(EventsActivity.this, "武汉市不在当前地图可见区域");
		}
	}

	/**
	 * 对触摸地图事件回调
	 */
	@Override
	public void onTouch(MotionEvent event) {

		mTouchTextView.setText("触摸事件：屏幕位置" + event.getX() + " " + event.getY());
	}
}
