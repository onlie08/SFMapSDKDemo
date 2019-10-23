package com.sfmap.map.demo.overlay;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapOptions;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.ToastUtil;

import timber.log.Timber;

/**
 * sfmapMap中介绍如何显示一个基本地图
 */
public class TileProviderActivity extends Activity implements MapController.OnMapLoadedListener, MapController.OnCameraChangeListener, RadioGroup.OnCheckedChangeListener {
	private MapView mapView;
	private MapController mMapController;
	private TileOverlayXyz sfTileOverlay;
	private TileOverlayWms tileOverlayWms;
	private TileOverlayXyz tileOverlayXyz;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tile_layer_provider);
		mapView = findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须调用
		init();
	}

	/**
	 * 初始化地图控制器
	 */
	private void init() {
		mMapController = mapView.getMap();
		//设置比例尺绝对位置， 第一个参数表示横坐标，第二个表示纵坐标
		mMapController.setViewPosition(MapOptions.POSITION_SCALE,100,800);
		mMapController.getUiSettings().setScaleControlsEnabled(true);
		mMapController.getUiSettings().setLogoPosition(-50);
		mMapController.setOnMapLoadedListener(this);
		mMapController.moveCamera(
				CameraUpdateFactory.newLatLngZoom(
						new LatLng(22.524644,113.93761),
						18)
		);
		mMapController.setOnCameraChangeListener(this);
		Timber.v("Start loading map...");

		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupLayers);
		radioGroup.setOnCheckedChangeListener(this);

//		sfTileOverlay = new TileOverlayXyz(mMapController, "http://gis-lps.sf-express.com/");
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
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		clearOldLayers();
		if(checkedId == R.id.radioButtonWmsLayer) {
			setUpWmsLayer();
		}

		if(checkedId == R.id.radioButtonXyzLayer) {
			setUpXyzLayer();
		}
	}

	private void clearOldLayers() {
		if(tileOverlayWms != null) {
			tileOverlayWms.destroy();
		}

		if(tileOverlayXyz != null) {
			tileOverlayXyz.destroy();
		}
	}

	private void setUpXyzLayer() {
		tileOverlayXyz = new TileOverlayXyz(mMapController, "http://gis-lps.sit.sf-express.com:45396/");
	}

	private void setUpWmsLayer() {
		//配置对应的图层服务器地址
		tileOverlayWms = new TileOverlayWms(mMapController, "http://gis-aos-aoi.sit.sf-express.com/");
	}
}
