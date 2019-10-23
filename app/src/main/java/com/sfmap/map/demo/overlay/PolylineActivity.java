package com.sfmap.map.demo.overlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.sfmap.api.maps.CameraUpdateFactory;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Polyline;
import com.sfmap.api.maps.model.PolylineOptions;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * sfmapMap地图中简单介绍一些Polyline的用法.
 */
public class PolylineActivity extends Activity implements
		OnSeekBarChangeListener {
	private static final int WIDTH_MAX = 50;
	private static final int HUE_MAX = 255;
	private static final int ALPHA_MAX = 255;

	private MapController lMap;
	private MapView mapView;
	private Polyline polyline,polyline_add;
	private SeekBar mColorBar;
	private SeekBar mAlphaBar;
	private SeekBar mWidthBar;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_polyline);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化lMap对象
	 */
	private void init() {
		mColorBar = (SeekBar) findViewById(R.id.hueSeekBar);
		mColorBar.setMax(HUE_MAX);
		mColorBar.setProgress(50);

		mAlphaBar = (SeekBar) findViewById(R.id.alphaSeekBar);
		mAlphaBar.setMax(ALPHA_MAX);
		mAlphaBar.setProgress(255);

		mWidthBar = (SeekBar) findViewById(R.id.widthSeekBar);
		mWidthBar.setMax(WIDTH_MAX);
		mWidthBar.setProgress(10);
		try {
			if (lMap == null) {
                lMap = mapView.getMap();
                setUpMap();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpMap() {
		mColorBar.setOnSeekBarChangeListener(this);
		mAlphaBar.setOnSeekBarChangeListener(this);
		mWidthBar.setOnSeekBarChangeListener(this);
		lMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.300299, 106.347656), 4));
		lMap.setMapTextZIndex(2);
		polyline = lMap.addPolyline((new PolylineOptions())
				.add(Constants.SHANGHAI, Constants.BEIJING, Constants.GUANGZHOU)
				.width(10).geodesic(true)
				.color(Color.argb(255, 1, 1, 1)));
		lMap.addPolyline((new PolylineOptions())
				.add(new LatLng(36.285643, 83.222875), new LatLng(43.902099, 125.242301))
				.geodesic(true).setDottedLine(true).color(Color.RED));
		polyline_add = lMap.addPolyline((new PolylineOptions())
				.add(new LatLng(29.719141,106.403469), new LatLng(30.550709, 98.16148))
				.width(64)
				.setCustomTexture(BitmapDescriptorFactory.fromAsset("sfmap_route.png")));

		// 多个颜色
		List<Integer> colorList = new ArrayList<Integer>();
		colorList.add(Color.argb(255, 1, 1, 1));
		colorList.add(Color.YELLOW);

		lMap.addPolyline((new PolylineOptions())
				.add(new LatLng(37.99616, 114.47754), new LatLng(37.80544, 112.56592),
						new LatLng(34.66936, 113.68652))
				.width(10)
				.colorValues(colorList));

		// 多纹理
		List<BitmapDescriptor> bitmapDescriptors = new ArrayList<>();
		bitmapDescriptors.add(BitmapDescriptorFactory.fromAsset("sfmap_route.png"));
		bitmapDescriptors.add(BitmapDescriptorFactory.fromAsset("routetexture.png"));
		List<Integer> customTextureList = new ArrayList<>();
		customTextureList.add(0);
		customTextureList.add(1);

		lMap.addPolyline((new PolylineOptions())
				.add(new LatLng(40.74726, 111.70898), new LatLng(38.30718, 106.25977),
						new LatLng(36.35053, 101.7334))
				.width(30)
				.setCustomTextureList(bitmapDescriptors)
				.setCustomTextureIndex(customTextureList));
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

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (polyline == null) {
			return;
		}
//		polyline.setDottedLine(true);
		if (seekBar == mColorBar) {
			polyline.setColor(Color.argb(progress, 1, 1, 1));
		} else if (seekBar == mAlphaBar) {
			float[] prevHSV = new float[3];
			Color.colorToHSV(polyline.getColor(), prevHSV);
			polyline.setColor(Color.HSVToColor(progress, prevHSV));
		} else if (seekBar == mWidthBar) {
			polyline.setWidth(progress);
		}
	}
}
