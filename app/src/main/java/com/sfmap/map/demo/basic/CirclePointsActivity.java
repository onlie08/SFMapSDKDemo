package com.sfmap.map.demo.basic;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.Projection;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.model.Polygon;
import com.sfmap.api.maps.model.PolygonOptions;
import com.sfmap.api.maps.model.Polyline;
import com.sfmap.api.maps.model.PolylineOptions;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.LatLngUtils;
import com.sfmap.map.demo.util.ToastUtil;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import timber.log.Timber;

public class CirclePointsActivity extends Activity implements
        View.OnClickListener,
        View.OnTouchListener,
        MapController.OnCameraChangeListener {

    private static final int CUSTOM_GRAY_MAP_STYLE = 1;
    private View maskView;
    private MapView mapView;
    private MapController mMap;
    private final List<LatLng> mPolygonPoints = new ArrayList<>();
    private final List<LatLng> mPointsOnMap = new ArrayList<>();
    private final Map<LatLng, Marker> mPointMarkerMap = new HashMap<>();
    private Polyline mPolylineDrew;
    private Polygon mPolygonDrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_points);
        maskView = findViewById(R.id.viewTouchInterceptMask);
        maskView.setOnTouchListener(this);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        mMap = mapView.getMap();
        mMap.setMapStyleType(CUSTOM_GRAY_MAP_STYLE);
        mMap.setOnCameraChangeListener(this);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.52451, 113.939681), 18));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStartDrawing:
                tryStartDrawing();
                break;
            case R.id.buttonStopDrawing:
                tryStopDrawing();
                break;
        }
    }

    private void tryStopDrawing() {
        clearMultipleLine();
        generateRandomPointsOnMap();
        maskView.setVisibility(View.GONE);
    }

    private void tryStartDrawing() {
        maskView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startCollectingPoints(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                addMovingPoint(event);
                return true;
            case MotionEvent.ACTION_UP:
                stopCollectingPoints(event);
                return true;
            case MotionEvent.ACTION_CANCEL:
                clearMultipleLine();
                return true;
        }
        return false;
    }

    private void clearMultipleLine() {
        mPolygonPoints.clear();
        drawMultiLineOnMap();

    }

    private void stopCollectingPoints(MotionEvent event) {
        LatLng latLng = calculateLatLngOnMap(event.getX(), event.getY());
        if(latLng != null) {
            mPolygonPoints.add(latLng);
        }

        //多于两个点才会首尾点相连形成环
        if(mPolygonPoints.size() > 3) {
            //将第一个点放到队尾，形成闭环
            mPolygonPoints.add(mPolygonPoints.get(0));
            updateMarkersInCircle();
            drawPolygonOnMap();
        } else {
            ToastUtil.show(this, "画的点数量太少了，无法圈成圆圈");
        }


    }

    private void drawPolygonOnMap() {
        if(mPolylineDrew != null) {
            mPolylineDrew.remove();
            mPolylineDrew = null;
        }

        PolygonOptions polygonOptions = new PolygonOptions()
                .addAll(mPolygonPoints)
                .fillColor(Color.argb(128, 255, 0, 0))
                .strokeColor(Color.argb(255, 255, 0,0))
                .strokeWidth(dp2px(3));

        mPolygonDrew =  mMap.addPolygon(polygonOptions);

    }

    private void updateMarkersInCircle() {

        if(mPolygonPoints.size() < 3) {
            ToastUtil.show(this, "画的点数太少了");
            return;
        }


        Projection projection = mMap.getProjection();
        LatLngBounds visibleBounds = projection.getVisibleRegion().latLngBounds;
        double longitudeRangeOfScreen = visibleBounds.northeast.longitude - visibleBounds.southwest.longitude;

        //计算用户画线收尾闭合之后形成的并集多边形
        Geometry simplifiedPolygon =
                LatLngUtils.simplifyAndValidatePolygon(mPolygonPoints, longitudeRangeOfScreen / 50);

        //返回并集多边形的顶点数据组
        Coordinate[] points = simplifiedPolygon.getCoordinates();


        //绘制新的顶点多边形
        mPolygonPoints.clear();
        for(Coordinate point : points) {
            mPolygonPoints.add(new LatLng(point.y, point.x));
        }

        //遍历完所有点是否在用户画的并集多边形内
        for(Map.Entry<LatLng, Marker> entry : mPointMarkerMap.entrySet()) {
            final LatLng latLng = entry.getKey();
            Marker marker = entry.getValue();


            if(LatLngUtils.isCoveredBy(simplifiedPolygon, new LatLngUtils.GeometryCoverable() {
                @Override
                public LatLng getLocation() {
                    return latLng;
                }
            })) {
                marker.setIcon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_GREEN));
            } else {
                marker.setIcon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_AZURE));
            }
        }
        Timber.v("计算结束");
        ToastUtil.show(this, "计算结束");
    }

    private void addMovingPoint(MotionEvent event) {
        LatLng latLng = calculateLatLngOnMap(event.getX(), event.getY());
        if(latLng != null) {
            mPolygonPoints.add(latLng);
            if(mPolygonPoints.size() > 1) {
                drawMultiLineOnMap();
            }
        }
    }

    private void drawMultiLineOnMap() {
        if(mPolylineDrew == null) {
            mPolylineDrew = mMap.addPolyline(new PolylineOptions());
            mPolylineDrew.setColor(Color.RED);
            mPolylineDrew.setWidth(dp2px(3));
        }
        //not else, all conditions
        mPolylineDrew.setPoints(mPolygonPoints);

        if(mPolygonDrew != null) {
            mPolygonDrew.remove();
            mPolygonDrew = null;
        }

    }

    private void startCollectingPoints(MotionEvent event) {
        mPolygonPoints.clear();
        LatLng latLng = calculateLatLngOnMap(event.getX(), event.getY());
        if(latLng != null) {
            mPolygonPoints.add(latLng);
        }
    }

    /**
     * 将地图控件上的屏幕坐标系点转换为经纬度
     * @param x 地图控件坐标系 x
     * @param y 地图控件坐标系 y
     * @return 根据当前地图映射关系转换的经纬度
     */
    private LatLng calculateLatLngOnMap(float x, float y) {
        if(mMap != null) {
            Projection projection = mMap.getProjection();
            return projection.fromScreenLocation(new Point((int) x, (int) y));
        }
        return null;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        clearMultipleLine();
        generateRandomPointsOnMap();
    }

    private void generateRandomPointsOnMap() {
        mPointsOnMap.clear();

        for(Map.Entry<LatLng, Marker> entry :  mPointMarkerMap.entrySet()) {
            Marker marker = entry.getValue();
            marker.remove();
        }

        Projection projection = mMap.getProjection();
        LatLngBounds visibleBounds = projection.getVisibleRegion().latLngBounds;
        int generatedCount = 50;
        Random random = new Random();
        for(int index = 0; index < generatedCount; index++) {
            generateRandomLatLng(visibleBounds, random);
        }

        for(LatLng point : mPointsOnMap) {
            drawMarkerPointOnMap(point);
        }
    }

    private void generateRandomLatLng(LatLngBounds visileBounds, Random random) {
        double southLatitude = visileBounds.southwest.latitude;
        double northLatitude = visileBounds.northeast.latitude;
        double westLongitude = visileBounds.southwest.longitude;
        double eastLongitude = visileBounds.northeast.longitude;

        double latitude = southLatitude + (northLatitude - southLatitude) * random.nextDouble();
        double longitude = westLongitude + (eastLongitude - westLongitude) * random.nextDouble();
        mPointsOnMap.add(new LatLng(latitude, longitude));
    }

    private void drawMarkerPointOnMap(LatLng point) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_AZURE))
                .position(point)
                .draggable(false));
        mPointMarkerMap.put(point, marker);
    }

    private float dp2px(int dp) {
        Resources r = getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );

    }
}
