package com.sfmap.map.demo.overlay;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.cluster.Cluster;
import com.sfmap.api.maps.cluster.ClusterClickListener;
import com.sfmap.api.maps.cluster.ClusterItem;
import com.sfmap.api.maps.cluster.ClusterOverlay;
import com.sfmap.api.maps.cluster.ClusterRender;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.LatLngBounds;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.cluster.RegionItem;

/**
 * 地图中简单介绍点聚合功能。
 */
public class ClusterActivity extends Activity implements
        ClusterClickListener, ClusterRender{
    private static final String TAG = "ClusterActivity";
    private MapView mapView;
    private Button btn_cluster_marker;
    private Button btn_cluster_ploygon;
    private Button btn_cluster_both;

    private int clusterRadius = 48; //聚合半径(单位dp）
    private List<ClusterItem> items = new CopyOnWriteArrayList<>();
    private ClusterOverlay mClusterOverlay;
    private List<LatLng> latLngs = new ArrayList<>();
    private Map<Integer, Drawable> mBackDrawAbles = new HashMap<Integer, Drawable>();
    private MapController mMapController;
    private Marker curMarker;
    private boolean winShowing =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
        initMapView();
        initData();

    }

    private void initData() {
        addMarksData();
    }


    private void initMapView() {
        mMapController = mapView.getMap();
        mMapController.setOnCameraChangeListener(new MapController.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition position) {
                if(mClusterOverlay != null) {
                    mClusterOverlay.updateClusters();//camera变化调用聚合方法
                }
            }
        });

        mMapController.setOnMarkerClickListener(new MapController.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                curMarker = marker;
                winShowing = true;
                return false;
            }
        });
        mMapController.setInfoWindowAdapter(new MapController.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View infoWindow = LayoutInflater.from(getApplicationContext()).inflate(
                            R.layout.custom_info_window, null);
                TextView title = infoWindow.findViewById(R.id.title);
                TextView snippet = infoWindow.findViewById(R.id.snippet);
                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    /**
     * 初始化Map对象
     */
    private void init() {

        btn_cluster_marker = (Button) findViewById(R.id.btn_cluster_marker);
        btn_cluster_ploygon = (Button) findViewById(R.id.btn_cluster_ploygon);
        btn_cluster_both = (Button) findViewById(R.id.btn_cluster_both);
        btn_cluster_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清理掉地图上的所有标识后重绘
                mMapController.clear();
                if(mClusterOverlay!=null){
                    mClusterOverlay.onDestroy();
                    mClusterOverlay = null;
                }
                initClusterMarker();
                setMarkersCenter();
            }
        });
        btn_cluster_ploygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清理掉地图上的所有标识后重绘
                mMapController.clear();
                if(mClusterOverlay!=null){
                    mClusterOverlay.onDestroy();
                    mClusterOverlay = null;
                }
                initClusterPolygon();
                setMarkersCenter();
            }
        });
        btn_cluster_both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清理掉地图上的所有标识后重绘
                mMapController.clear();
                if(mClusterOverlay!=null){
                    mClusterOverlay.onDestroy();
                    mClusterOverlay = null;
                }
                initClusterBoth();
                setMarkersCenter();
            }
        });

//
    }

    private void initClusterBoth() {
        mClusterOverlay = new ClusterOverlay(mMapController, items,
                dp2px(this, clusterRadius),
                this);
        mClusterOverlay.setOnClusterClickListener(this);
        mClusterOverlay.setClusterRenderer(this);
    }

    /**
     * 初始化多边形聚合
     */
    private void initClusterPolygon() {
        mClusterOverlay = new ClusterOverlay(mMapController, items,
                dp2px(this, clusterRadius),
                this);
        mClusterOverlay.setOnClusterClickListener(this);

        //关闭默认显示聚合显示视图范围内的数据，计算及渲染时间会随着聚合数据点增长而增长，注意OOM
        //可以在地图视图变化完成的回调接口里面 判读如果缩放级别没变，可以不用重新计算
//        mClusterOverlay.setClusterVisibleOnly(false);
        mClusterOverlay.setClusterRenderer(new ClusterRender() {
            @Override
            public MarkerOptions getClusterMarkerOptions(Cluster cluster) {
                LatLng latlng = cluster.getCenterLatLng();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions
                        .anchor(0.5f, 0.5f)
                        .icon(getBitmapDes(cluster.getClusterCount()))
                        .position(latlng);
                return markerOptions;
            }

            /**
             * 默认聚合点的绘制样式
             */
            private BitmapDescriptor getBitmapDes(int num) {
                TextView textView = new TextView(ClusterActivity.this);
                if (num > 1) {
                    String tile = String.valueOf(num);
                    textView.setText(tile);
                }
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                textView.setBackground(getDrawAble(num));
                return BitmapDescriptorFactory.fromView(textView);

            }

            private Drawable getDrawAble(int clusterNum) {
                Drawable bitmapDrawable = mBackDrawAbles.get(6);
                if (bitmapDrawable == null) {
                    bitmapDrawable = new BitmapDrawable(null, drawCircle(dp2px(ClusterActivity.this, 80),
                            Color.argb(159, 210, 154, 6)));
                    mBackDrawAbles.put(6, bitmapDrawable);
                }
                return bitmapDrawable;
            }
        });
    }

    private void addMarksData() {
        //随机10000个点
        for (int i = 0; i < 1000; i++) {
            double lat = Math.random() * 0.1 + 39.906086;
            double lon = Math.random() * 0.1 +  116.399101;
            LatLng latLng = new LatLng(lat, lon, false);
            latLngs.add(latLng);
            RegionItem regionItem = new RegionItem(latLng,
                    "test" + i);
            items.add(regionItem);
        }
    }

    /**
     * 自定义聚合显示的图标
     * @param clusterNum 有多少个点
     * @return
     */
    private Drawable getDrawAble(int clusterNum) {
        int radius = dp2px(this, 80);
        if (clusterNum == 1) {//一个点的时候调显示图标R.drawable.b_poi_h
            Drawable bitmapDrawable = mBackDrawAbles.get(1);
            if (bitmapDrawable == null) {
                bitmapDrawable =
                        this.getResources().getDrawable(
                                R.drawable.b_poi_h);
                mBackDrawAbles.put(1, bitmapDrawable);
            }

            return bitmapDrawable;
        } else if (clusterNum < 5) {//小于5个点的时候调显示图标R.drawable.b_poi_h

            Drawable bitmapDrawable = mBackDrawAbles.get(2);
            if (bitmapDrawable == null) {
                bitmapDrawable =
                        this.getResources().getDrawable(
                                R.drawable.b_poi_h);
                mBackDrawAbles.put(2, bitmapDrawable);
            }

            return bitmapDrawable;
        } else if (clusterNum < 10) {//5到10个点的时候显示绘制颜色Color.argb(199, 217, 114, 0)的圆
            Drawable bitmapDrawable = mBackDrawAbles.get(3);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(199, 217, 114, 0)));
                mBackDrawAbles.put(3, bitmapDrawable);
            }

            return bitmapDrawable;
        } else {//大于10个点的时候显示绘制颜色Color.argb(235, 215, 66, 2)的圆
            Drawable bitmapDrawable = mBackDrawAbles.get(4);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(235, 215, 66, 2)));
                mBackDrawAbles.put(4, bitmapDrawable);
            }
            return bitmapDrawable;
        }
    }

    /**
     * 根据传入的地址明细，计算得出地图的缩放比，确保在初始界面中加载所有传入的地址
     */
    public void setMarkersCenter() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLngBounds bounds = null;
        for (int i = 0; i < latLngs.size(); i++) {
            builder.include(latLngs.get(i));//把你所有的坐标点放进去
        }
        bounds = builder.build();

        bounds = centerBoundsAt(bounds, new LatLng(39.906086, 116.399101));
        mMapController.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    /**
     * 基于将经纬度简化为平面二维坐标系,
     * 粗略计算含了已知区域并且以某个经纬度点为中心点包的区域
     * @param bounds 已知区域
     * @param centerLatLng 中心点经纬度坐标
     * @return 结果区域
     */
    private LatLngBounds centerBoundsAt(LatLngBounds bounds, LatLng centerLatLng) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng symmetricSouthWest = calculateSymmetricPoint(centerLatLng, bounds.southwest);
        LatLng symmetricNorthEast = calculateSymmetricPoint(centerLatLng, bounds.northeast);
        builder.include(bounds.northeast);
        builder.include(bounds.southwest);
        builder.include(symmetricNorthEast);
        builder.include(symmetricSouthWest);
        return builder.build();
    }

    /**
     * 基于将经纬度简化为平面二维坐标,
     * 粗略计算目标点相对于中心点的对称点，
     * @param center 中心点
     * @param point 目标点
     * @return 对称点
     */
    private LatLng calculateSymmetricPoint(LatLng center, LatLng point) {
        return new LatLng(
                2 * center.latitude - point.latitude,
                2 * center.longitude - point.longitude
        );
    }

    /**
     * 初始化点聚合
     */
    private void initClusterMarker() {
        mClusterOverlay = new ClusterOverlay(mMapController, items,
                dp2px(this, clusterRadius),
                this);
        mClusterOverlay.setOnClusterClickListener(this);
        mClusterOverlay.setClusterRenderer(new ClusterRender() {
            @Override
            public MarkerOptions getClusterMarkerOptions(Cluster cluster) {
                LatLng latlng = cluster.getCenterLatLng();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions
                        .anchor(0.5f, 0.5f)
                        .icon(getBitmapDes(cluster.getClusterCount()))
                        .title("title")
                        .snippet("snippet")
                        .position(latlng);
                return markerOptions;
            }

            /**
             * 默认聚合点的绘制样式
             */
            private BitmapDescriptor getBitmapDes(int num) {
                TextView textView = new TextView(ClusterActivity.this);
                if (num > 1) {
                    String tile = String.valueOf(num);
                    textView.setText(tile);
                }
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                textView.setBackground(getDrawAble(num));
                return BitmapDescriptorFactory.fromView(textView);

            }

            private Drawable getDrawAble(int clusterNum) {
                Drawable bitmapDrawable = mBackDrawAbles.get(5);
                if (bitmapDrawable == null) {
                    bitmapDrawable = getResources().getDrawable(
                            R.drawable.b_poi_h);
                    mBackDrawAbles.put(5, bitmapDrawable);
                }
                return bitmapDrawable;
            }
        });
    }
    /**
     * 方法重写
     */
    @Override
    protected void onResume() {
        Log.e("@@@", "onResume-------------------");
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法重写
     */
    @Override
    protected void onPause() {
        Log.e("@@@", "onPause-------------------");
        mapView.onPause();
        super.onPause();
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
        if(mClusterOverlay!=null){
            mClusterOverlay.onDestroy();
            mClusterOverlay = null;
        }
        mapView.onDestroy();
    }


    /**
     * 自定义绘制聚合显示的圆
     * @param radius 半径
     * @param color 颜色值
     * @return
     */
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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onClick(Marker marker, List<ClusterItem> clusterItems) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (ClusterItem clusterItem : clusterItems) {
            builder.include(clusterItem.getPosition());
        }
        LatLngBounds latLngBounds = builder.build();
        mMapController.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0)
        );

    }

//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        curMarker = marker;
////        if (marker.getObject().getClass().equals(Cluster.class)
////                && mClusterOverlay != null) {
////            mClusterOverlay.responseClusterClickEvent(marker);
////            mMap.setMapCenter(marker.getPosition());
////            if(curMarker != null){
////                curMarker.showInfoWindow();
////            }
////            return false;
////        }
//        mMapController.setMapCenter(marker.getPosition());
//        if(curMarker != null){
//            curMarker.showInfoWindow();
//        }
//        return false;
//    }

    @Override
    public MarkerOptions getClusterMarkerOptions(Cluster cluster) {
        LatLng latlng = cluster.getCenterLatLng();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions
                .anchor(0.5f, 0.5f)
                .title("title")
                .snippet("snippet")
                .icon(getBitmapDes(cluster.getClusterCount()))
                .position(latlng);
        return markerOptions;
    }

    /**
     * 默认聚合点的绘制样式
     */
    private BitmapDescriptor getBitmapDes(int num) {
        TextView textView = new TextView(this);
        if (num > 1) {
            String tile = String.valueOf(num);
            textView.setText(tile);
        }
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setBackground(getDrawAble(num));
        return BitmapDescriptorFactory.fromView(textView);

    }
}

