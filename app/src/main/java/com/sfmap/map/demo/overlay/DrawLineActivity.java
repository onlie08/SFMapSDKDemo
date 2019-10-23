package com.sfmap.map.demo.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.Projection;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.model.Polyline;
import com.sfmap.api.maps.model.PolylineOptions;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.LatLngUtils;
import com.sfmap.map.demo.util.ToastUtil;

import timber.log.Timber;

public class DrawLineActivity extends Activity implements
        View.OnClickListener,
        MapController.OnCameraChangeListener,
        View.OnTouchListener,
        MapController.OnMapLoadedListener,
        MapController.OnMarkerClickListener {

    private MapView mapView;
    private MapController mMapController;
    private View maskView;
    private final ArrayList<LatLng> mDrawingTrackPoints = new ArrayList<>();
    private final LatLng mLocation = new LatLng(22.524654,113.938399);
    private Polyline lineOnMap;
    private final ArrayList<PointOnMap> mRandomPoints = new ArrayList<>();
    private final ArrayList<Marker> mPointMarkers = new ArrayList<>();
    private Random mRandom = new Random();
    private BitmapDrawable bitmapDrawable;

    private class PointOnMap implements LatLngUtils.Locatable {
        private final LatLng latLng;
        private int position = -1;
        PointOnMap(LatLng latLng) {
            this.latLng = latLng;
        }

        @Override
        public LatLng getLocation() {
            return latLng;
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Override
        public int setPosition(int position) {
            return this.position = position;
        }


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_line);
        maskView = findViewById(R.id.viewTouchInterceptMask);
        maskView.setOnTouchListener(this);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        mMapController = mapView.getMap();
        mMapController.setOnCameraChangeListener(this);
        mMapController.setOnMapLoadedListener(this);
        mMapController.setOnMarkerClickListener(this);
        mMapController.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 18));
    }

    private void addMarkers() {

        generateRandomPoints();
        drawMarkers();

    }

    private void drawMarkers() {
        if(!mPointMarkers.isEmpty()) {
            for(Marker marker : mPointMarkers) {
                marker.destroy();
            }
            mPointMarkers.clear();
        }
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for(int index = 0; index < mRandomPoints.size(); index++) {
            PointOnMap pointOnMap = mRandomPoints.get(index);
            MarkerOptions markerOptions = new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .icon(getIconOfPoint(pointOnMap))
                    .position(pointOnMap.getLocation());
            mPointMarkers.add(mMapController.addMarker(markerOptions));
            boundsBuilder.include(mRandomPoints.get(index).getLocation());
        }
        mMapController.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 10));
    }

    private BitmapDescriptor getIconOfPoint(PointOnMap pointOnMap) {
        if(pointOnMap.getPosition() == -1) {
            return BitmapDescriptorFactory.fromResource(R.drawable.b_poi_h);
        } else {
            return getBitmapDes(pointOnMap.getPosition());
        }
    }

    /**
     * 默认聚合点的绘制样式
     */
    private BitmapDescriptor getBitmapDes(int position) {
        TextView textView = new TextView(this);
        String tile = String.valueOf(position);
        textView.setText(tile);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setBackground(getDrawAble());
        return BitmapDescriptorFactory.fromView(textView);

    }

    private Drawable getDrawAble() {

        if (bitmapDrawable == null) {
            bitmapDrawable = new BitmapDrawable(null, drawCircle((int) (dp2px(30) + 0.5),
                    Color.argb(159, 210, 154, 6)));
        }
        return bitmapDrawable;
    }

    private Bitmap drawCircle(int radius, int color) {
        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        RectF rectF = new RectF(0, 0, radius * 2, radius * 2);
        paint.setColor(color);
        canvas.drawArc(rectF, 0, 360, true, paint);
        return bitmap;
    }

    private void generateRandomPoints() {

        int pointCount = 30;
        for(int index = 0; index < pointCount; index++) {
            mRandomPoints.add(new PointOnMap(new LatLng(randomLatitude(), randomLongitude())));
        }

    }

    private double randomLatitude() {
        return mLocation.latitude + (mRandom.nextDouble() - 0.5) * 0.01;
    }

    private double randomLongitude() {
        return mLocation.longitude + (mRandom.nextDouble() - 0.5) * 0.01;
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
            case R.id.buttonSortByTrack:
                sortPointsByTrack();
                break;
        }
    }

    private void tryStopDrawing() {
        maskView.setVisibility(View.GONE);
    }

    private void tryStartDrawing() {
        maskView.setVisibility(View.VISIBLE);
        mDrawingTrackPoints.clear();
        drawLine();
        resetPointOrder();
        drawMarkers();
    }

    private void resetPointOrder() {
        for(PointOnMap point : mRandomPoints) {
            point.setPosition(-1);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.viewTouchInterceptMask:
                onTouchEventOnInterceptMask(event);
                return true;
        }
        return false;

    }

    private void onTouchEventOnInterceptMask(MotionEvent event) {
        addPointToDrawingLine(event.getX(), event.getY());
    }

    private void sortPointsByTrack() {
        if(mDrawingTrackPoints.isEmpty()) {
            return;
        }
        long startTimeNano = SystemClock.elapsedRealtimeNanos();
        List<LatLngUtils.Locatable> locatables = new ArrayList<LatLngUtils.Locatable>(mRandomPoints);
        List<LatLngUtils.Locatable> sortedLocatables = LatLngUtils.sortByTrack(locatables, mDrawingTrackPoints, 100);
        Timber.v("按轨迹排序结果，");
        int index = 0;
        for(LatLngUtils.Locatable locatable : sortedLocatables) {
            Timber.v(locatable.getLocation().toString());
            locatable.setPosition(index);
            index++;
        }

        long nanosUsed = SystemClock.elapsedRealtimeNanos() - startTimeNano;
        Timber.v("Sorting %d locatables by track(%d points) take %d nanos.",
                locatables.size(),
                mDrawingTrackPoints.size(),
                nanosUsed
        );
        drawMarkers();
    }


    private void addPointToDrawingLine(float x, float y) {
        if(mMapController != null) {
            Projection projection = mMapController.getProjection();
            LatLng latlng = projection.fromScreenLocation(new Point((int) x, (int) y));
            Timber.v("touch on %s on map", latlng.toString());
            if(!mDrawingTrackPoints.contains(latlng)) {
                mDrawingTrackPoints.add(latlng);
            }
            drawLine();
        }
    }

    private void drawLine() {
        if(lineOnMap == null) {
            lineOnMap = mMapController.addPolyline(new PolylineOptions());
            lineOnMap.setColor(Color.RED);
            lineOnMap.setWidth(dp2px(3));
        }
        lineOnMap.setPoints(mDrawingTrackPoints);
    }

    private float dp2px(int dp) {
        Resources r = getResources();
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );

    }

    @Override
    public void onMapLoaded() {
        addMarkers();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng position = marker.getPosition();
        Projection projection = mMapController.getProjection();
        Point screenPosition = projection.toScreenLocation(position);
        Timber.v("Click on screen %s relative to map", screenPosition.toString());
        return true;
    }
}
