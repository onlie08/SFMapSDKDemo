package com.sfmap.map.demo.location;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.SfMapLocationClient;
import com.sfmap.api.location.SfMapLocationClientOption;
import com.sfmap.api.location.SfMapLocationListener;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.LocationSource;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.MyLocationStyle;
import com.sfmap.map.demo.R;

/**
* sfmapMap地图中介绍定位三种模式的使用，包括定位，追随，旋转
*/
public class SfLocationModeSourceActivity extends Activity implements
		LocationSource, OnCheckedChangeListener, SfMapLocationListener {
	private MapController map;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private RadioGroup mGPSModeGroup;
    private boolean isFirstFocus = false;
	private int angle=0;
	/**
	 * 定位代理.
	 */
	private SfMapLocationClient locationClient = null;
	private SfMapLocationClientOption locationOption = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
		setContentView(R.layout.activity_locationmodesource);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}
	public void click(View v){
		angle+=10;
		if(angle>360)angle=0;
		map.setMyLocationRotateAngle(360-angle);
	}

	/**
	 * 初始化
	 */
	private void init() {
		try {
			if (map == null) {
                map = mapView.getMap();
                setUpMap();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		mGPSModeGroup = (RadioGroup) findViewById(R.id.gps_radio_group);
		mGPSModeGroup.setOnCheckedChangeListener(this);
	}

	/**
	 * 设置一些MapController的属性
	 */
	private void setUpMap() {
		map.setLocationSource(this);// 设置定位监听
		map.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		map.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		map.setMyLocationType(MapController.LOCATION_TYPE_LOCATE);

		//自定义定位图标
		MyLocationStyle style = new MyLocationStyle();
		style.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.b_poi_h));
		// 圆的颜色
		style.radiusFillColor(Color.argb(0x26, 0xff, 0, 0));
		// 外圈的颜色
		style.strokeColor(Color.argb(0xde, 0xff, 0, 0));
		// 外圈的宽度
		style.strokeWidth(2);


		map.setMyLocationStyle(style);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.gps_locate_button:
			// 设置定位的类型为定位模式
			map.setMyLocationType(MapController.LOCATION_TYPE_LOCATE);
			break;
		case R.id.gps_follow_button:
			// 设置定位的类型为 跟随模式
			map.setMyLocationType(MapController.LOCATION_TYPE_MAP_FOLLOW);
			break;
		case R.id.gps_rotate_button:
			// 设置定位的类型为根据地图面向方向旋转
			map.setMyLocationType(MapController.LOCATION_TYPE_MAP_ROTATE);
			break;
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
		deactivate();
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
		if (null != locationClient) {
			/**
			 * 如果sfmapmapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行sfmapmapLocationClient的onDestroy
			 */
			locationClient.destroy();
			locationClient = null;
			locationOption = null;
		}
	}


    @Override
    public void onLocationChanged(SfMapLocation location) {
		if(location!=null&&location.getErrorCode()!=0){
			//定位发生异常
			Log.e("LocationChangeError", "Location error code：" + location.getErrorCode());
			return ;
		}
        if (mListener != null &&location!=null){
            mListener.onLocationChanged(location);
            //mapView.set
			float r = location.getAccuracy();
            if(!isFirstFocus){
                map.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

                //
                if(r>200){
                    map.moveCamera(CameraUpdateFactory.zoomTo(17));
                }else{
                    map.moveCamera(CameraUpdateFactory.zoomTo(18));
                }
                isFirstFocus = true;
            }

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
			locationOption.setLocationMode(SfMapLocationClientOption.SfMapLocationMode.Battery_Saving);
			// 设置定位间隔为2秒
			locationOption.setInterval(2 * 1000);
			locationOption.setNeedAddress(true);
			// 设置定位监听
			locationClient.setLocationListener(this);
			locationClient.setLocationOption(locationOption);
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


}
