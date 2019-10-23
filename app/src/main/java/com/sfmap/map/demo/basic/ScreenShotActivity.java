package com.sfmap.map.demo.basic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapController.OnMapScreenShotListener;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.Constants;
import com.sfmap.map.demo.util.ToastUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 立得地图中对截屏简单介绍
 */
public class ScreenShotActivity extends Activity implements
		OnMapScreenShotListener {
	private MapController lMap;
	private MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screenshot);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化lMap对象
	 */
	private void init() {
		try {
			if (lMap == null) {
                lMap = mapView.getMap();
                setUpMap();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 对地图添加一个marker
	 */
	private void setUpMap() {
		lMap.addMarker(new MarkerOptions().position(Constants.BEIJING)
				.title("北京").snippet("北京"));
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
	 * 对地图进行截屏
	 */
	public void getMapScreenShot(View v) {
		lMap.getMapScreenShot(this);
	}

	@Override
	public void onMapScreenShot(Bitmap bitmap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		if(null == bitmap){
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(
					Environment.getExternalStorageDirectory() + "/test_"
							+ sdf.format(new Date()) + ".png");
			boolean b = bitmap.compress(CompressFormat.PNG, 100, fos);
			try {
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (b)
				ToastUtil.show(ScreenShotActivity.this, "截屏成功");
			else {
				ToastUtil.show(ScreenShotActivity.this, "截屏失败");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMapScreenShot(Bitmap bitmap, int status) {

	}
}
