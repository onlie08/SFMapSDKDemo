package com.sfmap.map.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapOptions;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.model.TileOverlayOptions;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.ToastUtil;

import timber.log.Timber;

/**
 * sfmapMap中介绍如何显示一个基本地图
 */
public class BasicMapActivity extends Activity implements MapController.OnMapLoadedListener, MapController.OnCameraChangeListener {
	private MapView mapView;
	private MapController mMapController;
	private boolean boundSet;
	View infoWindow = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basicmap);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须调用
		init();
		addMarker();
	}

	private void addMarker() {
		LatLng latLng = new LatLng(22.524644,113.93761);
		mMapController.addMarker(new MarkerOptions().position(latLng).title("测试marker").snippet("xxxxxx"));

	}

	/**
	 * 初始化地图控制器
	 */
	private void init() {
		mMapController = mapView.getMap();
		//设置比例尺绝对位置， 第一个参数表示横坐标，第二个表示纵坐标
//		mMapController.setViewPosition(MapOptions.POSITION_SCALE,100,800);
		mMapController.getUiSettings().setScaleControlsEnabled(true);
//		mMapController.getUiSettings().setLogoPosition(-50);
		mMapController.setOnMapLoadedListener(this);
		mMapController.moveCamera(
				CameraUpdateFactory.newLatLngZoom(
						new LatLng(22.524644,113.93761),
						18)
		);
		mMapController.setOnCameraChangeListener(this);
		Timber.v("Start loading map...");

		mMapController.setOnMarkerClickListener(new MapController.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				return false;
			}
		});
		mMapController.setInfoWindowAdapter(new MapController.InfoWindowAdapter() {
			@Override
			public View getInfoWindow(Marker marker) {
				if(infoWindow == null) {
					infoWindow = LayoutInflater.from(getApplicationContext()).inflate(
							R.layout.custom_info_window, null);
				}
				TextView title = infoWindow.findViewById(R.id.title);
				TextView snippet = infoWindow.findViewById(R.id.snippet);
				title.setText(marker.getTitle());
				snippet.setText(marker.getSnippet());
				return infoWindow;
			}

			@Override
			public View getInfoContents(Marker marker) {
				return null;
			}
		});
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

	@Override
	public void onMapLoaded() {
		Timber.v("onMapLoaded() callback called");
		ToastUtil.show(this, "地图加载完成");
	}

	@Override
	public void onCameraChange(CameraPosition cameraPosition) {

	}

	@Override
	public void onCameraChangeFinish(CameraPosition cameraPosition) {
		Timber.v("OnCameraChangeFinish:%s", cameraPosition.toString());
		if(!boundSet) {
			LatLngBounds bounds = LatLngBounds.builder()
					.include(new LatLng(22.382, 114.188))
					.include(new LatLng(22.382, 114.188))
					.include(new LatLng(22.382, 114.188))
					.include(new LatLng(22.382, 114.188))
					.include(new LatLng(0, 0))
					.build();
			mMapController.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
			boundSet = true;
		}
	}
}
