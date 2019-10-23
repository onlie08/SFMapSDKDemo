package com.sfmap.map.demo.poisearch;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapController.InfoWindowAdapter;
import com.sfmap.api.maps.MapController.OnInfoWindowClickListener;
import com.sfmap.api.maps.MapController.OnMapClickListener;
import com.sfmap.api.maps.MapController.OnMarkerClickListener;
import com.sfmap.api.maps.SupportMapFragment;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.overlay.PoiOverlay;
import com.sfmap.api.services.core.LatLonPoint;
import com.sfmap.api.services.poisearch.ComplexSearch;
import com.sfmap.api.services.poisearch.ComplexSearchResult;
import com.sfmap.api.services.poisearch.PoiItem;
import com.sfmap.api.services.poisearch.PoiResult;
import com.sfmap.api.services.poisearch.PoiSearch;
import com.sfmap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.ToastUtil;

import java.util.List;

/**
 * sfmap地图中简单介绍poisearch周边搜索和区域搜索
 */
public class PoiAroundSearchActivity extends FragmentActivity implements
        OnMarkerClickListener, InfoWindowAdapter, OnPoiSearchListener, OnMapClickListener, OnInfoWindowClickListener,
        OnClickListener, ComplexSearch.OnComplexSearchListener {
    private MapController lMap;
    private ProgressDialog progDialog = null;// 搜索时进度
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 1;// 当前页面，从1开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp = new LatLonPoint(39.911207, 116.462458);// 默认国贸116.462458,39.911207
    private Marker locationMarker; // 选择的点
    private PoiSearch poiSearch;
    private PoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据
    private Button nextButton;// 下一页
    private EditText searchName;//搜索关键字。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poiaroundsearch);
        init();
    }

    /**
     * 初始化MapController对象
     */
    private void init() {
        try {
            if (lMap == null) {
                lMap = ((SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map)).getMap();
                setUpMap();

                Button locationButton = (Button) findViewById(R.id.locationButton);
                locationButton.setOnClickListener(this);
                Button searchButton = (Button) findViewById(R.id.searchButton);
                searchButton.setOnClickListener(this);

                Button searchBoundButton = (Button) findViewById(R.id.searchBoundButton);
                searchBoundButton.setOnClickListener(this);

                searchName = (EditText) findViewById(R.id.keyWord);
                searchName.setOnClickListener(this);

                nextButton = (Button) findViewById(R.id.nextButton);
                nextButton.setOnClickListener(this);
                nextButton.setClickable(false);// 默认下一页按钮不可点
                locationMarker = lMap.addMarker(new MarkerOptions()
                        .anchor(0.5f, 1)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.marker_point))
                        .position(new LatLng(lp.getLatitude(), lp.getLongitude()))
                        .title("国贸为中心点，查其周边"));
                locationMarker.showInfoWindow();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置地图相关。
     */
    private void setUpMap() {
        lMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        lMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件

    }

    /**
     * 注册监听
     */
    private void registerListener() {
        lMap.setOnMapClickListener(PoiAroundSearchActivity.this);
        lMap.setOnMarkerClickListener(PoiAroundSearchActivity.this);
        lMap.setOnInfoWindowClickListener(this);
        lMap.setInfoWindowAdapter(PoiAroundSearchActivity.this);
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索中");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 开始进行poi周边搜索
     */
    protected void doSearchQuery(String search) {

        showProgressDialog();// 显示进度框
        lMap.setOnMapClickListener(null);// 进行poi搜索时清除掉地图点击事件
        currentPage = 0;
        query = new PoiSearch.Query(search, "北京市");// 第一个参数表示搜索字符串，第二个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        if (lp != null) {
            query.setLocation(lp);//中心坐标点，必须传入。
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * 用综合搜索接口进行周边搜索
     * @param search
     */
    protected void doComplexAroundSearch(String search){
        showProgressDialog();// 显示进度框
        lMap.setOnMapClickListener(null);// 进行poi搜索时清除掉地图点击事件
        currentPage = 1;
        String location = lp.getLongitude()+","+lp.getLatitude();
        ComplexSearch.Query query = new ComplexSearch.Query(search, null,
                ComplexSearch.Query.DATASOURCE_TYPE_POI, null, location, 10000, 10, currentPage);
        ComplexSearch complexSearch = new ComplexSearch(this);
        complexSearch.setQuery(query);
        complexSearch.setSearchListener(this);
        complexSearch.searchDataAsyn();
    }

    /**
     * 开始进行poi区域搜索
     */
    protected void doSearchBoundQuery(String search) {
        showProgressDialog();// 显示进度框
        lMap.setOnMapClickListener(null);// 进行poi搜索时清除掉地图点击事件
        currentPage = 1;
        query = new PoiSearch.Query(search, "");// 第一个参数表示搜索字符串,第二个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        poiSearch = new PoiSearch(this, query);

        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(39.80923, 116.397428),
                new LatLonPoint(39.90923, 116.597428)));// 设置区域查询范围的左下角和右上角

        poiSearch.setOnPoiSearchListener(this);

        poiSearch.searchPOIAsyn();// 异步搜索
    }

    /**
     * 点击下一页poi搜索
     */
    public void nextSearch() {
        if (query != null && poiSearch != null && poiResult != null) {
            if (poiResult.getPageCount() > currentPage) {
                currentPage++;

                query.setPageNum(currentPage);// 设置查后一页
                poiSearch.searchPOIAsyn();
            } else {
                ToastUtil
                        .show(PoiAroundSearchActivity.this, R.string.error_no_result);
            }
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public void onPoiItemSearched(List<PoiItem> result, int errorCode) {

    }

    /**
     * POI搜索回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    if (poiItems != null && poiItems.size() > 0) {
                        lMap.clear();// 清理之前的图标
                        poiOverlay = new PoiOverlay(lMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                        nextButton.setClickable(true);// 设置下一页可点
                    } else {
                        ToastUtil.show(PoiAroundSearchActivity.this,
                                R.string.error_no_result);
                    }
                }
            } else {
                ToastUtil
                        .show(PoiAroundSearchActivity.this, R.string.error_no_result);
            }
        } else if (rCode == 21) {
            ToastUtil.show(PoiAroundSearchActivity.this, R.string.error_network);
        } else if (rCode == 102) {
            ToastUtil.show(PoiAroundSearchActivity.this, R.string.error_key);
        } else if (rCode == 105) {
            ToastUtil.show(PoiAroundSearchActivity.this, " 查询结果为空。");
        } else {
            ToastUtil.show(PoiAroundSearchActivity.this, R.string.error_other);
        }
    }


    @Override
    public void onMapClick(LatLng latng) {
        locationMarker = lMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_point))
                .position(latng).title("点击选择为中心点"));
        locationMarker.showInfoWindow();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        locationMarker.hideInfoWindow();
        lp = new LatLonPoint(locationMarker.getPosition().latitude,
                locationMarker.getPosition().longitude);
        locationMarker.destroy();
    }

    @Override
    public void onClick(View v) {


        String search = searchName.getText().toString().trim();
        if ("".equals(search)) {
            ToastUtil.show(PoiAroundSearchActivity.this, "请输入搜索关键字");
            return;
        }

        switch (v.getId()) {
            /**
             * 点击标记按钮
             */
            case R.id.locationButton:
                lMap.clear();// 清理所有marker
                registerListener();
                break;
            /**
             * 点击周边搜索按钮
             */
            case R.id.searchButton:
                doSearchQuery(search);
//                doComplexAroundSearch(search);
                break;

            /**
             * 点击区域搜索按钮
             */
            case R.id.searchBoundButton:
                doSearchBoundQuery(search);
                break;
            /**
             * 点击下一页按钮
             */
            case R.id.nextButton:
                nextSearch();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onComplexSearched(ComplexSearchResult result, int status) {
        dissmissProgressDialog();// 隐藏对话框
        if (status == 0) {
            if (result != null && result.getQuery() != null) { // 搜索poi的结果
//                if (result.getQuery().equals(query)) { // 是否是同一条
//                    poiResult = result;
//                }
                poiItems = result.getPoiItems();// 取得第一页的poiitem数据，页数从数字1开始
                if (poiItems != null && poiItems.size() > 0) {
                    lMap.clear();// 清理之前的图标
                    poiOverlay = new PoiOverlay(lMap, poiItems);
                    poiOverlay.removeFromMap();
                    poiOverlay.addToMap();
                    poiOverlay.zoomToSpan();

                    nextButton.setClickable(true);// 设置下一页可点
                } else {
                    ToastUtil.show(PoiAroundSearchActivity.this,
                            R.string.error_no_result);
                }
            } else {
                ToastUtil
                        .show(PoiAroundSearchActivity.this, R.string.error_no_result);
            }
        } else if (status == 21) {
            ToastUtil.show(PoiAroundSearchActivity.this, R.string.error_network);
        } else if (status == 102) {
            ToastUtil.show(PoiAroundSearchActivity.this, R.string.error_key);
        } else if (status == 105) {
            ToastUtil.show(PoiAroundSearchActivity.this, " 查询结果为空。");
        } else {
            ToastUtil.show(PoiAroundSearchActivity.this, R.string.error_other);
        }
    }
}
