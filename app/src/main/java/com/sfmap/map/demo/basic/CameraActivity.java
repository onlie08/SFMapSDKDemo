package com.sfmap.map.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.Constants;
import com.sfmap.map.demo.util.ToastUtil;
import com.sfmap.api.maps.CameraUpdate;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.MarkerOptions;

/**
 * sfmapMap中简单介绍一些Camera的用法.
 */
public class CameraActivity extends Activity implements OnClickListener,
		MapController.CancelableCallback {
	private static final int SCROLL_BY_PX = 100;
	private MapView mapView;
	private MapController lMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化LMap对象
	 */
	private void init() {
		if (lMap == null) {
				lMap = mapView.getMap();
			setUpMap();
		}
		Button stopAnimation = (Button) findViewById(R.id.stop_animation);
		stopAnimation.setOnClickListener(this);
		
		ToggleButton animate = (ToggleButton) findViewById(R.id.animate);
		animate.setOnClickListener(this);
		
		Button zhengzhou = (Button) findViewById(R.id.zhengzhou);
		zhengzhou.setOnClickListener(this);
		
		Button beijing = (Button) findViewById(R.id.beijing);
		beijing.setOnClickListener(this);
		
		Button scrollLeft = (Button) findViewById(R.id.scroll_left);
		scrollLeft.setOnClickListener(this);
		
		Button scrollRight = (Button) findViewById(R.id.scroll_right);
		scrollRight.setOnClickListener(this);
		
		Button scrollUp = (Button) findViewById(R.id.scroll_up);
		scrollUp.setOnClickListener(this);
		
		Button scrollDown = (Button) findViewById(R.id.scroll_down);
		scrollDown.setOnClickListener(this);
		
		Button zoomIn = (Button) findViewById(R.id.zoom_in);
		zoomIn.setOnClickListener(this);
		
		Button zoomOut = (Button) findViewById(R.id.zoom_out);
		zoomOut.setOnClickListener(this);
	}

	/**
	 * 添加一个marker
	 */
	private void setUpMap() {
        lMap.setPointToCenter(0,0);
        lMap.addMarker(new MarkerOptions()
				.position(Constants.BEIJING)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_RED)));
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
	 * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
	 */
	private void changeCamera(CameraUpdate update, MapController.CancelableCallback callback) {
		boolean animated = ((CompoundButton) findViewById(R.id.animate))
				.isChecked();
		if (animated) {
            lMap.animateCamera(update, 1000, callback);
		} else {
            lMap.moveCamera(update);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.stop_animation:
            lMap.stopAnimation();
			break;
		case R.id.beijing:
			changeCamera(
					CameraUpdateFactory.newCameraPosition(new CameraPosition(
							Constants.BEIJING, 18, 0, 30)), null);
			break;
		case R.id.zhengzhou:
			changeCamera(
					CameraUpdateFactory.newCameraPosition(new CameraPosition(
							Constants.WUHAN, 18, 30, 0)), this);
			break;
		case R.id.scroll_left:
			changeCamera(CameraUpdateFactory.scrollBy(-SCROLL_BY_PX, 0), null);
			break;
		case R.id.scroll_right:
			changeCamera(CameraUpdateFactory.scrollBy(SCROLL_BY_PX, 0), null);
			break;
		case R.id.scroll_up:
			changeCamera(CameraUpdateFactory.scrollBy(0, -SCROLL_BY_PX), null);
			break;
		case R.id.scroll_down:
			changeCamera(CameraUpdateFactory.scrollBy(0, SCROLL_BY_PX), null);
			break;
		case R.id.zoom_in:
			changeCamera(CameraUpdateFactory.zoomIn(), null);
			break;
		case R.id.zoom_out:
			changeCamera(CameraUpdateFactory.zoomOut(), null);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCancel() {
		ToastUtil.show(CameraActivity.this, "动画到 武汉 取消");
	}

	@Override
	public void onFinish() {
		ToastUtil.show(CameraActivity.this, "动画到 武汉 成功");
	}
}
