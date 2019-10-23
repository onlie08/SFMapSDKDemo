package com.sfmap.map.demo.basic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.sfmap.map.demo.R;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.MyTrafficStyle;

/**
 * 立得地图中简单介绍矢量地图
 */
public class LayersActivity extends Activity implements OnItemSelectedListener,
		OnClickListener {
	private MapController map;
	private MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layers);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化map对象
	 */
	private void init() {
		if (map == null) {
				map = mapView.getMap();
		}
		CheckBox traffic = (CheckBox) findViewById(R.id.traffic);
		traffic.setOnClickListener(this);
		Spinner spinner = (Spinner) findViewById(R.id.layers_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.layers_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
		myTrafficStyle.setSeriousCongestedColor(0xffEEE8AA);
		myTrafficStyle.setCongestedColor(0xffFF0000);
		myTrafficStyle.setSlowColor(0xffFFA500);
		myTrafficStyle.setSmoothColor(0xff3CB371);
		map.setMyTrafficStyle(myTrafficStyle);
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
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (map != null) {
			setLayer((String) parent.getItemAtPosition(position));
		}
	}

	private void setLayer(String layerName) {
		if (layerName.equals(getString(R.string.Layers_normal))) {
			map.setMapType(MapController.MAP_TYPE_NORMAL);// 矢量地图模式
		}
		else if(layerName.equals(getString(R.string.Layers_night_mode))){
			map.setMapType(MapController.MAP_TYPE_NIGHT);//夜景地图模式
		} else if(layerName.equals(getString(R.string.Layers_navi_mode))){
			map.setMapType(MapController.MAP_TYPE_NAVI);//导航模式
		}else if(layerName.equals(getString(R.string.Layers_streetview))){//街景路网
			map.setMapType(MapController.MAP_TYPE_STREETVIEW);
		}else if(layerName.equals(getString(R.string.Layers_satellite))){//卫星图
			map.setMapType(MapController.MAP_TYPE_SATELLITE);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	/**
	 * 对选择显示交通状况事件的响应
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.traffic) {
			map.setTrafficEnabled(((CheckBox) v).isChecked());// 显示实时交通状况
		}
	}
}
