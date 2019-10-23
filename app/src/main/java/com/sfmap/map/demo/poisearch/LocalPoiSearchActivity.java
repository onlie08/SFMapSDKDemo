package com.sfmap.map.demo.poisearch;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.SupportMapFragment;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.services.core.SearchException;
import com.sfmap.api.services.localsearch.ADCodeLevel;
import com.sfmap.api.services.localsearch.LocalPoiResult;
import com.sfmap.api.services.localsearch.LocalPoiSearch;
import com.sfmap.api.services.localsearch.SearchType;
import com.sfmap.api.services.localsearch.model.SearchResultInfo;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.LMapUtil;
import com.sfmap.map.demo.util.ToastUtil;

import java.util.List;

/**
 * 本地POI搜索介绍，主要对名称关键字和地址关键字查找进行使用介绍。
 */
public class LocalPoiSearchActivity extends FragmentActivity implements View.OnClickListener ,MapController.OnMarkerClickListener{
    private MapController lMap;
    private AutoCompleteTextView searchText;// 输入搜索关键字
    private String keyWord = "";// 要输入的poi搜索关键字
    private ProgressDialog progDialog = null;// 搜索时进度条
    private LocalPoiResult poiResult; // poi返回的结果
    private int currentPage =1;// 当前页面，从1开始计数
    private LocalPoiSearch.Query query;// Poi查询条件类
    private LocalPoiSearch poiSearch;// POI搜索
    private Spinner spinner_searchTypes;
    String [] typeCodes = new String[0];
    private int searchType = SearchType.SEARCH_TYPE_POINAME;
    private int adcode = 110100;
    String snPath;
    String dataPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localsearch);
        getSnPath();
        getDataPath();
        init();

    }


    /**
     * 初始化LMap对象
     */
    private void init() {
            if (lMap == null) {
                lMap = ((SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map)).getMap();
                setUpMap();
                try {
                    poiSearch = new LocalPoiSearch(this,snPath,dataPath);
                } catch (SearchException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.searchButton){//搜索
            searchButton();
        }else if(v.getId() == R.id.nextButton){//下一页
            nextButton();
        }
    }
    /**
     * 点击搜索按钮
     */
    public void searchButton() {
        keyWord = LMapUtil.checkEditText(searchText);
        if ("".equals(keyWord)) {
            ToastUtil.show(LocalPoiSearchActivity.this, "请输入搜索关键字");
            return;
        } else {
            doSearchQuery();
        }
    }
    private  void doSearchQuery(){
        getAdcode();
        currentPage = 1;
        query = new LocalPoiSearch.Query(keyWord,typeCodes,this.adcode,ADCodeLevel.ADCODE_LEVEL_CITY,searchType);// 第一个参数表示搜索字符串，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        try{
            LocalPoiResult result = poiSearch.searchPOI(this.query);
            searchRes(result);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
    /**
     * 点击下一页按钮
     */
    public void nextButton() {
        if (query != null && poiSearch != null && poiResult != null) {
            if (poiResult.getPageCount()  > currentPage) {
                currentPage++;
                query.setPageNum(currentPage);// 设置查后一页
               try{
                   LocalPoiResult result = poiSearch.searchPOI(this.query);
                   searchRes(result);
               }catch (Exception ex){
                   ex.printStackTrace();
               }
            } else {
                ToastUtil.show(LocalPoiSearchActivity.this,
                        R.string.error_no_result);
            }
        }else{
            ToastUtil.show(LocalPoiSearchActivity.this,
                    R.string.error_no_result);
        }
    }
    private void searchRes(LocalPoiResult result ) {
        lMap.clear();
        if (result != null){
            if (result.getMessage() == null || result.getMessage().length() == 0) {
                if (result.getPois() != null&&result.getPois().size()>0){
                    this.poiResult = result;
                    List res = result.getPois();
                    int size  = res.size();
                    for(int i=0;i<size;i++){
                        SearchResultInfo resInfo = (SearchResultInfo)res.get(i);
                        lMap.addMarker(getMarkerOpt(resInfo));
                    }
                }
            } else {
                ToastUtil.show(this, result.getMessage());
            }
        }else{
            ToastUtil.show(this, "无搜索结果");
        }
    }
    private MarkerOptions getMarkerOpt(SearchResultInfo resInfo)
    {
        return new MarkerOptions().position(new LatLng(resInfo.latitude,resInfo.longitude)).title(resInfo.name);
    }
    /**
     * 初始化页面控件
     */
    private void setUpMap() {
        Button searButton = (Button) findViewById(R.id.searchButton);
        searButton.setOnClickListener(this);
        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
        spinner_searchTypes = (Spinner) findViewById(R.id.spinner_ns_nametypes);
        spinner_searchTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                switch (pos)
                {
                    case 0:
                        searchType = SearchType.SEARCH_TYPE_POINAME;
                        break;
                    case 1:
                        searchType = SearchType.SEARCH_TYPE_ADDRNAME;
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void getSnPath(){
        this.snPath = LMapUtil.getDataRootPath(this);
    }
    private void getDataPath(){
        this.dataPath = LMapUtil.getDataRootPath(this);
    }
    private void getAdcode(){
//        if(poiSearch!=null&&lMap!=null){
//            CameraPosition position = lMap.getCameraPosition();
//            this.adcode = poiSearch.getAdcode(position.target.longitude,position.target.latitude);
//            Log.e("localsearch","____adcode:"+adcode);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    @Override
    protected void onDestroy() {
        if(lMap!=null){
            lMap.clear();
        }
        if(poiSearch!=null){
            poiSearch.destroy();
        }
        super.onDestroy();
    }

}
