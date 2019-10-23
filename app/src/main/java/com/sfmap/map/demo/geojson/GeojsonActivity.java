package com.sfmap.map.demo.geojson;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.services.cloud.CloudDatasetItem;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.Constants;
import com.sfmap.map.demo.util.LMapUtil;


public class GeojsonActivity extends Activity  {
    private MapView mMapView;
    private MapController mapController;
//    GeoJsonLayerOptions options;
    private long datasetId;
    private TextView tv_marker;
    private TextView tv_polyline;
    private TextView tv_polylgon;

    private String url="https://cloudmap.ishowchina.com/gds";
    private String path="/search/bbox" + "?";
    String ak = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geojson);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        try {
            mapController = mMapView.getMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ak = LMapUtil.getApiKey(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        initGeojsonOverlay();
//
//        tv_marker = (TextView) findViewById(R.id.tv_marker);
//        tv_polyline = (TextView) findViewById(R.id.tv_polyline);
//        tv_polylgon = (TextView) findViewById(R.id.tv_polylgon);
//        tv_marker.setOnClickListener(this);
//        tv_polyline.setOnClickListener(this);
//        tv_polylgon.setOnClickListener(this);
    }

//    private void initGeojsonOverlay(){
//        options = new GeoJsonLayerOptions();
//        // 点9 线7 面8
////        datasetId = 1167;
//        CloudDatasetItem datasetItem = getIntent().getParcelableExtra(Constants.BUNDLE_GEOJSON_DATASETITEM);
//        datasetId = datasetItem.getId();
//        int geoType = datasetItem.getGeoType();
//        options.setDataSetId(datasetId);
//        if (geoType == 1){
//
//        } else if (geoType == 2){
//            GeojsonPolylineOptions polylineOptions = new GeojsonPolylineOptions();
//            polylineOptions.width(30)
//                    .setCustomTexture(BitmapDescriptorFactory.fromAsset("sfmap_route.png"));
//            options.setPolylineOptions(polylineOptions);
//        } else if (geoType == 3){
//            GeojsonPolygonOptions polygonOptions = new GeojsonPolygonOptions();
//            polygonOptions.fillColor(Color.argb(50, 1, 1, 1)).strokeColor(Color.RED).strokeWidth(1);
//            options.setPolygonOptions(polygonOptions);
//        }
//        mapController.setGeoJsonServerListener(this);
//        mapController.setGeojsonOptions(options);
//    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

//    @Override
//    public void onCameraChange(CameraPosition position) {
//
//    }

//    @Override
//    public void onCameraChangeFinish(CameraPosition position) {
//        Log.e("@@@", "geojson onCameraChangeFinish");
//        LatLng latLng = position.target;
//        double centerLon = latLng.longitude; // 经度
//        double centerLat = latLng.latitude; // 纬度
//        if (centerLat >= 30 && centerLat <= 32 && centerLon >= 120 && centerLon <= 123){
//
//        }
//    }
//
//    private void addPoints(){
//
//    }

//    @Override
//    public void onClick(View v) {
//        // 点9 线7 面8
//        if (v.getId() == tv_marker.getId()){
//            tv_marker.setBackgroundColor(Color.parseColor("#00E5EE"));
//            tv_polyline.setBackgroundColor(Color.parseColor("#ffffff"));
//            tv_polylgon.setBackgroundColor(Color.parseColor("#ffffff"));
//            datasetId = 1167;
//            options.setDataSetId(datasetId);
//            mapController.setGeojsonOptions(options);
//        } else if (v.getId() == tv_polyline.getId()){
//            tv_marker.setBackgroundColor(Color.parseColor("#ffffff"));
//            tv_polyline.setBackgroundColor(Color.parseColor("#00E5EE"));
//            tv_polylgon.setBackgroundColor(Color.parseColor("#ffffff"));
//            datasetId = 1194;
//            options.setDataSetId(datasetId);
//            GeojsonPolylineOptions polylineOptions = new GeojsonPolylineOptions();
//            polylineOptions.width(30)
//                    .setCustomTexture(BitmapDescriptorFactory.fromAsset("sfmap_route.png"));
//            options.setPolylineOptions(polylineOptions);
//            mapController.setGeojsonOptions(options);
//        } else if (v.getId() == tv_polylgon.getId()){
//            tv_marker.setBackgroundColor(Color.parseColor("#ffffff"));
//            tv_polyline.setBackgroundColor(Color.parseColor("#ffffff"));
//            tv_polylgon.setBackgroundColor(Color.parseColor("#00E5EE"));
//            datasetId = 1196;
//            options.setDataSetId(datasetId);
//            GeojsonPolygonOptions polygonOptions = new GeojsonPolygonOptions();
//            polygonOptions.fillColor(Color.argb(50, 1, 1, 1)).strokeColor(Color.RED).strokeWidth(1);
//            options.setPolygonOptions(polygonOptions);
//            mapController.setGeojsonOptions(options);
//        }
//    }

//    @Override
//    public String getGegJsonServerUrl(LatLng minLatLon, LatLng maxLatLon, long mashid) {
//        if (minLatLon == null || maxLatLon == null)return null;
//
//        String parmas = "";
//        String bbox = "";
//        bbox = minLatLon.longitude + "," + minLatLon.latitude + ";"
//                    + maxLatLon.longitude + "," + maxLatLon.latitude;
//        parmas += "bbox=" + bbox + "&type=geojson&datasetId=" + datasetId + "&ak=" + ak;
//        return url+path+parmas;
//    }
}
