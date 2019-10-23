package com.sfmap.map.demo.poisearch;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapController.InfoWindowAdapter;
import com.sfmap.api.maps.MapController.OnMarkerClickListener;
import com.sfmap.api.maps.SupportMapFragment;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.overlay.PoiOverlay;
import com.sfmap.api.services.help.Inputtips;
import com.sfmap.api.services.help.Inputtips.InputtipsListener;
import com.sfmap.api.services.help.InputtipsQuery;
import com.sfmap.api.services.help.Tip;
import com.sfmap.api.services.poisearch.PoiItem;
import com.sfmap.api.services.poisearch.PoiResult;
import com.sfmap.api.services.poisearch.PoiSearch;
import com.sfmap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.LMapUtil;
import com.sfmap.map.demo.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * LMapV1地图中简单介绍poisearch搜索
 */
public class PoiKeywordSearchActivity extends FragmentActivity implements
        OnMarkerClickListener, InfoWindowAdapter, TextWatcher,
        OnPoiSearchListener, OnClickListener, InputtipsListener {
    private MapController lMap;
    private AutoCompleteTextView searchText;// 输入搜索关键字
    private String keyWord = "";// 要输入的poi搜索关键字
    private ProgressDialog progDialog = null;// 搜索时进度条
    private EditText editCity;// 要输入的城市名字或者城市区号
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 1;// 当前页面，从1开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poikeywordsearch);
        init();
    }

    /**
     * 初始化LMap对象
     */
    private void init() {
        try {
            if (lMap == null) {
                lMap = ((SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map)).getMap();
                setUpMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置页面监听
     */
    private void setUpMap() {
        Button searButton = (Button) findViewById(R.id.searchButton);
        searButton.setOnClickListener(this);
        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
        searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
        searchText.addTextChangedListener(this);// 添加文本输入框监听事件
        editCity = (EditText) findViewById(R.id.city);
        lMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        lMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件
    }

    /**
     * 点击搜索按钮
     */
    public void searchButton() {
        keyWord = LMapUtil.checkEditText(searchText);
        if ("".equals(keyWord)) {
            ToastUtil.show(PoiKeywordSearchActivity.this, "请输入搜索关键字");
            return;
        } else {
            doSearchQuery();
        }
    }

    /**
     * 点击下一页按钮
     */
    public void nextButton() {
        if (query != null && poiSearch != null && poiResult != null) {
            if (poiResult.getPageCount() > currentPage) {
                currentPage++;
                query.setPageNum(currentPage);// 设置查后一页
                poiSearch.searchPOIAsyn();
            } else {
                ToastUtil.show(PoiKeywordSearchActivity.this,
                        R.string.error_no_result);
            }
        }
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
        progDialog.setMessage("正在搜索:\n" + keyWord);
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
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        showProgressDialog();// 显示进度框
        currentPage = 1;
        query = new PoiSearch.Query(keyWord, "", editCity.getText().toString());
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
                null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());


        return view;
    }


    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        InputtipsQuery inputquery = new InputtipsQuery(newText, editCity.getText().toString());
//        inputquery.setDatasource("busline");
        Inputtips inputTips = new Inputtips(PoiKeywordSearchActivity.this, inputquery);
        inputTips.setInputtipsListener(this);
//        inputTips.requestInputtipsAsyn();
    }


    @Override
    public void onPoiItemSearched(List<PoiItem> result, int errorCode) {

    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始

                    if (poiItems != null && poiItems.size() > 0) {
                        lMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(lMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                    } else {
                        ToastUtil.show(PoiKeywordSearchActivity.this, R.string.error_no_result);
                    }
                }
            } else {
                ToastUtil.show(PoiKeywordSearchActivity.this, R.string.error_no_result);
            }
        } else if (rCode == 21) {
            ToastUtil.show(PoiKeywordSearchActivity.this, R.string.error_network);
        } else if (rCode == 102) {
            ToastUtil.show(PoiKeywordSearchActivity.this, R.string.error_key);
        } else if (rCode == 105) {
            ToastUtil.show(PoiKeywordSearchActivity.this, " 查询结果为空。");
        } else {
            ToastUtil.show(PoiKeywordSearchActivity.this, R.string.error_other);
        }

    }

    /**
     * Button点击事件回调方法
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchButton:
                searchButton();
                break;
            case R.id.nextButton:
                nextButton();
                break;
            default:
                break;
        }
    }


    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == 0) {// 正确返回
            if (tipList != null && !tipList.isEmpty()){
                List<String> listString = new ArrayList<String>();
                for (int i = 0; i < tipList.size(); i++) {
                    listString.add(tipList.get(i).getName());
                }
                ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                        getApplicationContext(),
                        R.layout.route_inputs, listString);
                searchText.setAdapter(aAdapter);
                aAdapter.notifyDataSetChanged();
            }
        }

    }
}
