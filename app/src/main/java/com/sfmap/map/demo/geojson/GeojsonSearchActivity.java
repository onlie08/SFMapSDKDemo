package com.sfmap.map.demo.geojson;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.services.cloud.CloudDatasetItem;
import com.sfmap.api.services.cloud.CloudDatasetSearch;
import com.sfmap.api.services.cloud.CloudDatasetSearchResult;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.cloud.CloudSearchListResultActivity;
import com.sfmap.map.demo.util.Constants;
import com.sfmap.map.demo.util.LMapUtil;
import com.sfmap.map.demo.util.ToastUtil;

import java.util.ArrayList;

public class GeojsonSearchActivity extends Activity implements View.OnClickListener, CloudDatasetSearch.OnCloudDatasetSearchListener {

    private MapView mMapView;
    private MapController mapController;
    private TextView tv_search;
    private CloudDatasetSearch datasetSearch;
    //提示框
    private ProgressDialog progDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geojson_search);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        try {
            mapController = mMapView.getMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.setOnClickListener(this);

        datasetSearch = new CloudDatasetSearch(this);
        datasetSearch.setSearchListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == tv_search.getId()){
                String ak = LMapUtil.getApiKey(this);
                if (TextUtils.isEmpty(ak) || getResources().getString(R.string.tv_keytip_default).equals(ak)){
                    ToastUtil.show(this, getResources().getString(R.string.tv_tips_nokey));
                } else {
                    //查询全部
                    searchDatasetAll();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onIDSearched(CloudDatasetSearchResult result, int status) {

    }

    @Override
    public void onPageSearched(CloudDatasetSearchResult result, int status) {

    }

    @Override
    public void onAllSearched(CloudDatasetSearchResult result, int status) {
        dismissProgressDialog();
        if(result == null){
            ToastUtil.show(this, getResources().getString(R.string.tv_tips_nodataset));
            return;
        }
        final ArrayList<CloudDatasetItem> cloudDatasetItemList = (ArrayList<CloudDatasetItem>) result.getResults();
        if(cloudDatasetItemList == null || cloudDatasetItemList.size() == 0){
            ToastUtil.show(this, getResources().getString(R.string.tv_tips_nodataset));
            return;
        }
        // 传递数据
        Intent intent = new Intent();
        intent.setClass(this, CloudSearchListResultActivity.class);
        intent.putExtra(Constants.BUNDLE_GEOJSON_DATASET, result);
        GeojsonSearchActivity.this.startActivity(intent);
    }

    /**
     * 数据集条件查询全部
     */
    private void searchDatasetAll(){
        showProgressDialog();
        CloudDatasetSearch.Query query = new CloudDatasetSearch.Query(0, 0, "软件产业基地", 0);
        datasetSearch.setQuery(query);
        datasetSearch.searchCloudAsyn();
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
        progDialog.setMessage("正在查询,请稍后!");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dismissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
}
