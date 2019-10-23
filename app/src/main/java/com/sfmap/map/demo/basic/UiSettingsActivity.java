package com.sfmap.map.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.SfMapLocationClient;
import com.sfmap.api.location.SfMapLocationClientOption;
import com.sfmap.api.location.SfMapLocationListener;
import com.sfmap.api.maps.CameraUpdateFactory;

import com.sfmap.api.maps.LocationSource;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapOptions;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.UiSettings;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.ToastUtil;

/**
 * UI settings 选项设置响应事件
 */
public class UiSettingsActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener, LocationSource, SfMapLocationListener {
	private MapController map;
	private MapView mapView;
	private UiSettings mUiSettings;
	private RadioGroup zoomRadioGroup;
	private boolean isFirstFocus = false;
	private OnLocationChangedListener mListener;

	/**
	 * 定位代理.
	 */
	private SfMapLocationClient locationClient = null;
	private SfMapLocationClientOption locationOption = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui_settings);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写

		init();
	}

	private void init() {
		try {
			if (map == null) {
                map = mapView.getMap();
                mUiSettings = map.getUiSettings();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		Button buttonScale = (Button) findViewById(R.id.buttonScale);
		buttonScale.setOnClickListener(this);
		CheckBox scaleToggle = (CheckBox) findViewById(R.id.scale_toggle);
		scaleToggle.setOnClickListener(this);
		CheckBox zoomToggle = (CheckBox) findViewById(R.id.zoom_toggle);
		zoomToggle.setOnClickListener(this);
		zoomRadioGroup = (RadioGroup) findViewById(R.id.zoom_position);
		zoomRadioGroup.setOnCheckedChangeListener(this);
		CheckBox compassToggle = (CheckBox) findViewById(R.id.compass_toggle);
		compassToggle.setOnClickListener(this);
		CheckBox mylocationToggle = (CheckBox) findViewById(R.id.mylocation_toggle);
		mylocationToggle.setOnClickListener(this);
		CheckBox scrollToggle = (CheckBox) findViewById(R.id.scroll_toggle);
		scrollToggle.setOnClickListener(this);
		CheckBox zoom_gesturesToggle = (CheckBox) findViewById(R.id.zoom_gestures_toggle);
		zoom_gesturesToggle.setOnClickListener(this);
		CheckBox tiltToggle = (CheckBox) findViewById(R.id.tilt_toggle);
		tiltToggle.setOnClickListener(this);
		CheckBox rotateToggle = (CheckBox) findViewById(R.id.rotate_toggle);
		rotateToggle.setOnClickListener(this);
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.logo_position);
		radioGroup.setOnCheckedChangeListener(this);

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
		deactivate();
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
		if (null != locationClient) {
			/**
			 * 如果sfmapmapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行sfmapmapLocationClient的onDestroy
			 */
			locationClient.destroy();
			locationClient = null;
			locationOption = null;
		}
		mapView.onDestroy();
	}

	/**
	 * 设置logo位置，左下，底部居中，右下
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (map != null) {
			if (checkedId == R.id.bottom_left) {
				mUiSettings
						.setLogoPosition(MapOptions.LOGO_POSITION_BOTTOM_LEFT);// 设置地图logo显示在左下方
			} else if (checkedId == R.id.bottom_center) {
				mUiSettings
						.setLogoPosition(MapOptions.LOGO_POSITION_BOTTOM_CENTER);// 设置地图logo显示在底部居中
			} else if (checkedId == R.id.bottom_right) {
				mUiSettings
						.setLogoPosition(MapOptions.LOGO_POSITION_BOTTOM_RIGHT);// 设置地图logo显示在右下方
			} else if (checkedId == R.id.zoom_bottom_right) {
				mUiSettings
						.setZoomPosition(MapOptions.ZOOM_POSITION_RIGHT_BUTTOM);//设置缩放按钮显示在右下方
			} else if (checkedId == R.id.zoom_center_right) {
				mUiSettings
						.setZoomPosition(MapOptions.ZOOM_POSITION_RIGHT_CENTER);//设置缩放按钮显示在右侧居中
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		/**
		 * 一像素代表多少米
		 */
		case R.id.buttonScale:
			float scale = map.getScalePerPixel();
			ToastUtil.show(UiSettingsActivity.this, "每像素代表" + scale + "米");
			break;
		/**
		 * 设置地图比例尺显示
		 */
		case R.id.scale_toggle:
			mUiSettings.setScaleControlsEnabled(((CheckBox) view).isChecked());

			break;
		/**
		 * 设置地图缩放按钮显示
		 */
		case R.id.zoom_toggle:
			mUiSettings.setZoomControlsEnabled(((CheckBox) view).isChecked());
			zoomRadioGroup.setVisibility(((CheckBox) view).isChecked()?View.VISIBLE:View.GONE);
			break;
		/**
		 * 设置地图指南针显示
		 */
		case R.id.compass_toggle:
			mUiSettings.setCompassEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图定位按钮显示
		 */
		case R.id.mylocation_toggle:
			map.setLocationSource(this);// 设置定位监听
			mUiSettings.setMyLocationButtonEnabled(((CheckBox) view)
					.isChecked()); // 是否显示默认的定位按钮
			map.setMyLocationEnabled(((CheckBox) view).isChecked());// 是否可触发定位并显示定位层
			break;
		/**
		 * 设置地图手势滑动
		 */
		case R.id.scroll_toggle:
			mUiSettings.setScrollGesturesEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图手势缩放
		 */
		case R.id.zoom_gestures_toggle:
			mUiSettings.setZoomGesturesEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图可以倾斜
		 */
		case R.id.tilt_toggle:
			mUiSettings.setTiltGesturesEnabled(((CheckBox) view).isChecked());
			break;
		/**
		 * 设置地图可以旋转
		 */
		case R.id.rotate_toggle:
			mUiSettings.setRotateGesturesEnabled(((CheckBox) view).isChecked());
			break;
		default:
			break;
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if(locationClient==null){
			locationClient = new SfMapLocationClient(this.getApplicationContext());
			locationOption = new SfMapLocationClientOption();
			// 设置定位模式为低功耗模式
			locationOption.setInterval(2000);
			// 设置定位监听
			locationClient.setLocationListener(this);
			locationClient.startLocation();
		}

	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (locationClient != null) {
			locationClient.stopLocation();
		}
	}
	@Override
	public void onLocationChanged(SfMapLocation location) {
		if(location!=null&&location.getErrorCode()!=0){
			//定位发生异常
			Log.e("onLocationChanged",location.getErrorCode()+"");
			return ;
		}
		if (mListener != null &&location!=null){
			mListener.onLocationChanged(location);
			float r=location.getAccuracy();
			if(!isFirstFocus){
				map.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
				if(r>200){
					map.moveCamera(CameraUpdateFactory.zoomTo(17));
				}else{
					map.moveCamera(CameraUpdateFactory.zoomTo(18));
				}
				isFirstFocus = true;
			}

		}
	}

}
