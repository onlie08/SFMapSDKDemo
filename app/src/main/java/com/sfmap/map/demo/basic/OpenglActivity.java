package com.sfmap.map.demo.basic;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.sfmap.api.maps.CustomRenderer;
import com.sfmap.api.maps.ExtralDrawPolygon;
import com.sfmap.api.maps.ExtralDrawPolyline;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.view.MyCustomRender;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 立得地图中介绍如何显示一个基本地图
 */
public class OpenglActivity extends Activity implements CustomRenderer {
	private LatLng latlng1 = new LatLng(39.945343, 116.3978675);
	private LatLng latlng2 = new LatLng(39.624563, 116.6567466);
	private LatLng latlng3 = new LatLng(39.423455, 116.5465867);

	private MapView mapView;
	private MapController lMap;
	private FloatBuffer lineVertexBuffer;
	private ArrayList<LatLng> latLngPolygon = new ArrayList<LatLng>();
	ExtralDrawPolyline drawPolyline = null;
	ExtralDrawPolygon drawPolygon = null;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opengl);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		drawPolyline = new ExtralDrawPolyline(mapView);
		drawPolygon = new ExtralDrawPolygon(mapView);
		init();

	}

	/**
	 * 初始化lMap对象
	 */
	private void init() {
			if (lMap == null) {
                lMap = mapView.getMap();
                latLngPolygon.add(latlng1);
                latLngPolygon.add(latlng2);
                latLngPolygon.add(latlng3);
                lMap.addCustomRenderer(new MyCustomRender(mapView));//添加多个图层
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
		lMap.clearCustomRenderer();
		mapView.onDestroy();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
			try {
				if (lineVertexBuffer == null) {
					lineVertexBuffer = drawPolygon.calMapFPoint(this.latLngPolygon);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		if (lineVertexBuffer != null) {
			/**
			 * 绘制polygone
			 */
			drawPolygon.drawPolygon(gl, Color.RED,Color.BLUE, lineVertexBuffer,5, latLngPolygon.size());

		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

	}

	@Override
	public void onMapReferencechanged() {
		try {
			this.lineVertexBuffer = drawPolygon.calMapFPoint(this.latLngPolygon);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
