package com.sfmap.map.demo.overlay;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sfmap.api.maps.CameraUpdateFactory;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapController.InfoWindowAdapter;
import com.sfmap.api.maps.MapController.OnInfoWindowClickListener;
import com.sfmap.api.maps.MapController.OnMapLoadedListener;
import com.sfmap.api.maps.MapController.OnMarkerClickListener;
import com.sfmap.api.maps.MapController.OnMarkerDragListener;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.Projection;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.model.Text;
import com.sfmap.api.maps.model.TextOptions;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.Constants;
import com.sfmap.map.demo.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * mapV2地图中简单介绍一些Marker的用法.
 */
public class MarkerActivity extends Activity implements OnMarkerClickListener,
		OnInfoWindowClickListener, OnMarkerDragListener, OnMapLoadedListener,
		OnClickListener, InfoWindowAdapter {
	private static final String TAG = MarkerActivity.class.getSimpleName();
	private MarkerOptions markerOption;
	private TextView markerText;
	private RadioGroup radioOption;
	private MapController map;
	private MapView mapView;
	private Marker marker2;// 有跳动效果的marker对象
	private LatLng latlng = new LatLng(36.061, 103.834);
	private Text mTextMark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_marker);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState); // 此方法必须重写
		init();
	}
	/**
	 * 初始化map对象
	 */
	private void init() {
		markerText = (TextView) findViewById(R.id.mark_listenter_text);
		radioOption = (RadioGroup) findViewById(R.id.custom_info_window_options);
		Button clearMap = (Button) findViewById(R.id.clearMap);
		clearMap.setOnClickListener(this);
		Button resetMap = (Button) findViewById(R.id.resetMap);
		resetMap.setOnClickListener(this);
		try {
			if (map == null) {
                map = mapView.getMap();
                setUpMap();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpMap() {
		map.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
		map.setOnMapLoadedListener(this);// 设置map加载成功事件监听器
		map.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		map.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		map.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		map.setOnCameraChangeListener(new MapController.OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				Log.v(TAG, "First camera change listener onCameraChange() called");
			}

			@Override
			public void onCameraChangeFinish(CameraPosition cameraPosition) {
				Log.v(TAG, "First camera change listener onCameraChangeFinish() called");
			}
		});

		map.setOnCameraChangeListener(new MapController.OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {
				Log.v(TAG, "Second camera change listener onCameraChange() called");
			}

			@Override
			public void onCameraChangeFinish(CameraPosition cameraPosition) {
				Log.v(TAG, "Second camera change listener onCameraChangeFinish() called");
			}
		});
		addMarkersToMap();// 往地图上添加marker
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
	 * 在地图上添加marker
	 */
	private void addMarkersToMap() {
		//文字显示标注，可以设置显示内容，位置，字体大小颜色，背景色旋转角度
				TextOptions textOptions = new TextOptions().position(Constants.BEIJING)
						.text("我是Text").fontColor(Color.YELLOW)
						.backgroundColor(Color.GREEN).fontSize(30).rotate(25).align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
						.zIndex(1.f).typeface(Typeface.DEFAULT_BOLD)
						;
			mTextMark =	map.addText(textOptions);
		
		Marker marker = map.addMarker(new MarkerOptions()
			 
				.title("我是Marker")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_AZURE))
				.draggable(true));
		marker.setRotateAngle(270);// 设置marker旋转90度
		marker.setPositionByPixels(400, 400);
		marker.showInfoWindow();// 设置默认显示一个infowinfow
		
		markerOption = new MarkerOptions();
		markerOption.position(Constants.WUHAN);
		markerOption.title("武汉市").snippet("武汉市：30.593454, 114.295733");
 
		markerOption.draggable(true).anchor(0.5f, 0.5f);
		markerOption.icon(
				BitmapDescriptorFactory.fromBitmap(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.marker_location_marker)));
		// 将Marker设置为贴地显示，可以双指下拉看效果
		markerOption.setFlat(true);

		ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_BLUE));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_RED));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_YELLOW));

		MarkerOptions markerOption1 = new MarkerOptions()
				.anchor(0.5f, 0.5f)
				.position(Constants.GUANGZHOU)
				.title("广州市")
				.snippet("广州市:23.142752, 113.268493")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_RED))
				.zIndex(2)
			 .draggable(true).period(50);

		MarkerOptions markerOption2 = new MarkerOptions()
				.anchor(0.5f, 0.5f)
				.position(Constants.GUANGZHOU)
				.title("广州市")
				.snippet("广州市:GUANGZHOU_OFFSET")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_GREEN))
				.draggable(true)
				.zIndex(1)
				.period(50);

		ArrayList<MarkerOptions> markerOptionList = new ArrayList<>();
		markerOptionList.add(markerOption);
		markerOptionList.add(markerOption1);
		markerOptionList.add(markerOption2);
		List<Marker> markerList = map.addMarkers(markerOptionList, false);
		marker2 = markerList.get(0);

		map.addMarker(markerOption);


	}

	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(final Marker marker) {
		if (marker.equals(marker2)) {
			if (map != null) {
				jumpPoint(marker);
			}
		}
		markerText.setText("你点击的是" + marker.getSnippet());
		return true;
	}

	/**
	 * marker点击时跳动一下
	 */
	public void jumpPoint(final Marker marker) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = map.getProjection();
		Point startPoint = proj.toScreenLocation(Constants.WUHAN);
		startPoint.offset(0, -100);
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 1500;

		final Interpolator interpolator = new BounceInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * Constants.WUHAN.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * Constants.WUHAN.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});
	}

	/**
	 * 监听点击infowindow窗口事件回调
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		ToastUtil.show(this, "你点击了infoWindow窗口" + marker.getTitle());
		ToastUtil.show(MarkerActivity.this, "当前地图可视区域内Marker数量:"
				+ map.getMapScreenMarkers().size());
	}

	/**
	 * 监听拖动marker时事件回调
	 */
	@Override
	public void onMarkerDrag(Marker marker) {
		String curDes = marker.getTitle() + "拖动时当前位置:(lat,lng)\n("
				+ marker.getPosition().latitude + ","
				+ marker.getPosition().longitude + ")";
		markerText.setText(curDes);
	}

	/**
	 * 监听拖动marker结束事件回调
	 */
	@Override
	public void onMarkerDragEnd(Marker marker) {
		markerText.setText(marker.getTitle() + "停止拖动");
	}

	/**
	 * 监听开始拖动marker事件回调
	 */
	@Override
	public void onMarkerDragStart(Marker marker) {
		markerText.setText(marker.getTitle() + "开始拖动");
	}

	/**
	 * 监听map地图加载成功事件回调
	 */
	@Override
	public void onMapLoaded() {
		// 设置所有maker显示在当前可视区域地图中
		LatLngBounds bounds = new LatLngBounds.Builder()
				.include(Constants.BEIJING).include(Constants.GUANGZHOU)
				.build();
		map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
	}

	/**
	 * 监听自定义infowindow窗口的infocontents事件回调
	 */
	@Override
	public View getInfoContents(Marker marker) {
		if (radioOption.getCheckedRadioButtonId() != R.id.custom_info_contents) {
			return null;
		}
		View infoContent = getLayoutInflater().inflate(
				R.layout.custom_info_contents, null);
		render(marker, infoContent);
		return infoContent;
	}

	/**
	 * 监听自定义infowindow窗口的infowindow事件回调
	 */
	@Override
	public View getInfoWindow(Marker marker) {
		if (radioOption.getCheckedRadioButtonId() != R.id.custom_info_window) {
			return null;
		}
		View infoWindow = getLayoutInflater().inflate(
				R.layout.custom_info_window, null);

		render(marker, infoWindow);
		return infoWindow;
	}

	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {
		if (radioOption.getCheckedRadioButtonId() == R.id.custom_info_contents) {
			((ImageView) view.findViewById(R.id.badge))
					.setImageResource(R.drawable.marker_badge_sa);
		} else if (radioOption.getCheckedRadioButtonId() == R.id.custom_info_window) {
			ImageView imageView = (ImageView) view.findViewById(R.id.badge);
			imageView.setImageResource(R.drawable.marker_badge_wa);
		}
		String title = marker.getTitle();
		TextView titleUi = ((TextView) view.findViewById(R.id.title));
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
					titleText.length(), 0);
			titleUi.setTextSize(15);
			titleUi.setText(titleText);

		} else {
			titleUi.setText("");
		}
		String snippet = marker.getSnippet();
		TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
		if (snippet != null) {
			SpannableString snippetText = new SpannableString(snippet);
			snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
					snippetText.length(), 0);
			snippetUi.setTextSize(20);
			snippetUi.setText(snippetText);
		} else {
			snippetUi.setText("");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 清空地图上所有已经标注的marker
		 */
		case R.id.clearMap:
			if (map != null) {
				map.clear();
			}
			break;
		/**
		 * 重新标注所有的marker
		 */
		case R.id.resetMap:
			if (map != null) {
				map.clear();
				addMarkersToMap();
			}
			break;
		default:
			break;
		}
	}
	public static Bitmap convertViewToBitmap(View view) {
		if(view == null){
			return null;
		}
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
}
