package com.sfmap.map.demo.basic;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.sfmap.map.demo.R;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.SupportMapFragment;

public class BaseMapFragmentActivity extends FragmentActivity {
	private MapController mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_basemap);
		setUpMapIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
				mMap = ((SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map)).getMap();
		}
	}

}
