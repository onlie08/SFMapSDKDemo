package com.sfmap.map.demo.geocoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.sfmap.api.maps.CameraUpdateFactory;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.services.core.LatLonPoint;
import com.sfmap.api.services.geocoder.GeocodeAddress;
import com.sfmap.api.services.geocoder.GeocodeQuery;
import com.sfmap.api.services.geocoder.GeocodeResult;
import com.sfmap.api.services.geocoder.GeocodeSearch;
import com.sfmap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.sfmap.api.services.geocoder.RegeocodeQuery;
import com.sfmap.api.services.geocoder.RegeocodeResult;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.LMapUtil;
import com.sfmap.map.demo.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 地理编码与逆地理编码功能介绍
 */
public class GeocoderActivity extends Activity implements
		OnGeocodeSearchListener, OnClickListener ,MapController.OnMapLongClickListener{
	private ProgressDialog progDialog = null;
	private GeocodeSearch geocoderSearch;
	private String addressName;
	private MapController lMap;
	private MapView mapView;
	private LatLonPoint latLonPoint = new LatLonPoint(39.90865, 116.39751);
	private Marker geoMarker;
	private Marker regeoMarker;

	private EditText searchstr,et_regeo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_geocoder);
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
		//Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化LMap对象
	 */
	private void init() {
		try {
			if (lMap == null) {
                lMap = mapView.getMap();
                lMap.setOnMapLongClickListener(this);
                geoMarker = lMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_BLUE)));
                regeoMarker = lMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_RED)));
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		Button geoButton = (Button) findViewById(R.id.geoButton);
		searchstr = (EditText)findViewById(R.id.searchstr) ;
		et_regeo = (EditText) findViewById(R.id.et_regeo);
		geoButton.setOnClickListener(this);
		Button regeoButton = (Button) findViewById(R.id.regeoButton);
		regeoButton.setOnClickListener(this);
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		progDialog = new ProgressDialog(this);
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

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在获取地址");
		progDialog.show();
	}

	/**
	 * 隐藏进度条对话框
	 */
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 响应地理编码
	 */
	public void getLatlon(final String name) {
		showDialog();
		// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼,城市可为空。
		GeocodeQuery query = new GeocodeQuery(name, "");
		geocoderSearch.getFromLocationNameAsyn(query);// 设置异步地理编码请求
	}

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		showDialog();
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint);// 参数表示一个经纬度。
		query.setShowPoi(false);//是否显示指定位置附近的poi
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	/**
	 * 地理编码查询回调
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		dismissDialog();
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				LatLonPoint latLonPoint = address.getLatLonPoint();
				GeocodeQuery s =  result.getGeocodeQuery();
				com.sfmap.api.services.core.LatLonPoint mLatLonPoint = new com.sfmap.api.services.core.LatLonPoint(latLonPoint.getLatitude(),latLonPoint.getLongitude());
				lMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						LMapUtil.convertToLatLng(mLatLonPoint), 15));
				geoMarker.setPosition(LMapUtil.convertToLatLng(mLatLonPoint));
				addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
						+ address.getFormatAddress();
				ToastUtil.show(GeocoderActivity.this, addressName);
			} else {
				ToastUtil.show(GeocoderActivity.this, R.string.error_no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(GeocoderActivity.this, R.string.error_network);
		} else if (rCode == 102) {
			ToastUtil.show(GeocoderActivity.this, R.string.error_key);
		} else if(rCode == 105){
			ToastUtil.show(GeocoderActivity.this, " 查询结果为空。");
		} else {
			ToastUtil.show(GeocoderActivity.this, R.string.error_other);
		}
	}

	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		dismissDialog();
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddressList() != null
					&& result.getRegeocodeAddressList().size() > 0) {
				addressName = result.getRegeocodeAddressList().get(0).getFormatAddress()
						+ "附近";

				com.sfmap.api.services.core.LatLonPoint mLatLonPoint = new com.sfmap.api.services.core.LatLonPoint(latLonPoint.getLatitude(),latLonPoint.getLongitude());

				lMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						LMapUtil.convertToLatLng(mLatLonPoint), 15));
				regeoMarker.setPosition(LMapUtil.convertToLatLng(mLatLonPoint));
				ToastUtil.show(GeocoderActivity.this, addressName);
			}
		} else if (rCode == 21) {
			ToastUtil.show(GeocoderActivity.this, R.string.error_network);
		} else if (rCode == 102) {
			ToastUtil.show(GeocoderActivity.this, R.string.error_key);
		} else if(rCode == 105){
			ToastUtil.show(GeocoderActivity.this, " 查询结果为空。");
		} else {
			ToastUtil.show(GeocoderActivity.this, R.string.error_other);
		}
	}
	@Override
	public void onMapLongClick(LatLng point) {
		if(et_regeo!=null){
			et_regeo.setText(point.longitude+","+point.latitude);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			/**
			 * 响应地理编码按钮
			 */
			case R.id.geoButton:
				if(searchstr.getText().length()==0){
					getLatlon(searchstr.getHint().toString());
				}else{
					getLatlon(searchstr.getText().toString());
				}

				break;
			/**
			 * 响应逆地理编码按钮
			 */
			case R.id.regeoButton:
				if(et_regeo.getText().length()>0){
					String [] lonlat = et_regeo.getText().toString().split(",");
					if(lonlat.length==2){
						latLonPoint = new LatLonPoint(Double.parseDouble(lonlat[1]),Double.parseDouble(lonlat[0]));
					}
				}else if(et_regeo.getText().length()==0){
					CameraPosition position = lMap.getCameraPosition();
					latLonPoint = new LatLonPoint(position.target.latitude,position.target.longitude);
				}
				getAddress(latLonPoint);
				getAddressBatch(latLonPoint);
				break;
			default:
				break;
		}
	}

	private void getAddressBatch(LatLonPoint latLonPoint) {
//		showDialog();
//		List<LatLonPoint> points = new ArrayList<>();
//		for(int i = 0 ; i < 10 ; i++) {
//			points.add(new LatLonPoint(latLonPoint.getLatitude() + 0.001 * i, latLonPoint.getLongitude() + 0.001 * i));
//		}
//		RegeocodeQuery query = new RegeocodeQuery(points);// 参数表示一个经纬度。
//		query.setShowPoi(false);//是否显示指定位置附近的poi
//		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}


}