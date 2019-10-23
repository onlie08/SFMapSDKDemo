 

package com.sfmap.map.demo.district;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.sfmap.api.maps.CameraUpdateFactory;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.PolylineOptions;
import com.sfmap.api.services.core.LatLonPoint;
import com.sfmap.api.services.district.DistrictItem;
import com.sfmap.api.services.district.DistrictResult;
import com.sfmap.api.services.district.DistrictSearch;
import com.sfmap.api.services.district.DistrictSearch.OnDistrictSearchListener;
import com.sfmap.api.services.district.DistrictSearchQuery;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.ToastUtil;


public class DistrictWithBoundaryActivity extends Activity implements OnClickListener,
		OnDistrictSearchListener {

	private Button mButton;
	private EditText mEditText;
	private MapView mMapView;

	private MapController mLMap;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_district_boundary);
		mButton = (Button) findViewById(R.id.search_button);
		mEditText = (EditText) findViewById(R.id.city_text);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		try {
			mLMap = mMapView.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mButton.setOnClickListener(this);

	}

	/**
	 * 方法重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 方法重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
        mLMap.clear();
		DistrictSearch search = new DistrictSearch(getApplicationContext());
        DistrictSearchQuery query = new DistrictSearchQuery(mEditText.getText().toString());
        search.setQuery(query);
		search.setOnDistrictSearchListener(this);

		search.searchDistrictAsyn();

	}

	@Override
	public void onDistrictSearched(DistrictResult districtResult,int rCode) {
        if(rCode == 0){
            final DistrictItem item = districtResult.getDistrict().get(0);
            if (item == null) {
                return;
            }
            LatLonPoint centerLatLng=item.getCenter();

            if(centerLatLng!=null){
                mLMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(centerLatLng.getLatitude(), centerLatLng.getLongitude()), 8));
            }


            new Thread() {
                public void run() {
                    String[] polyStr = item.districtBoundary();
                    if (polyStr == null || polyStr.length == 0) {
                        return;
                    }
                    for (String str : polyStr) {

                        String[] lat = str.split(";");
                        PolylineOptions polylineOption = new PolylineOptions();
                        boolean isFirst=true;
                        LatLng firstLatLng=null;
                        String[] lats = null;
                        for (String latstr : lat) {
                            lats = latstr.split(",");
                            if(isFirst){
                                isFirst=false;
                                firstLatLng=new LatLng(Double .parseDouble(lats[1]), Double .parseDouble(lats[0]));
                            }
                            polylineOption.add(new LatLng(Double .parseDouble(lats[1]), Double .parseDouble(lats[0])));
                        }
                        if(firstLatLng!=null){
                            polylineOption.add(firstLatLng);
                        }

                        polylineOption.width(10).color(Color.BLUE);
                        mLMap.addPolyline(polylineOption);
                    }
                }
            }.start();

        }else if (rCode == 21) {
            ToastUtil.show(DistrictWithBoundaryActivity.this, R.string.error_network);
        } else if (rCode == 102) {
            ToastUtil.show(DistrictWithBoundaryActivity.this, R.string.error_key);
        } else if(rCode == 105){
            ToastUtil.show(DistrictWithBoundaryActivity.this, " 查询结果为空。");
        } else {
            ToastUtil.show(DistrictWithBoundaryActivity.this, R.string.error_other);
        }

	}
}
