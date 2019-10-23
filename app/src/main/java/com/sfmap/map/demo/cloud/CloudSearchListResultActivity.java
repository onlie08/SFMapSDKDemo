package com.sfmap.map.demo.cloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sfmap.api.services.cloud.CloudDatasetItem;
import com.sfmap.api.services.cloud.CloudDatasetSearch;
import com.sfmap.api.services.cloud.CloudDatasetSearchResult;
import com.sfmap.api.services.cloud.CloudItem;
import com.sfmap.api.services.cloud.CloudSearch;
import com.sfmap.api.services.cloud.CloudSearchResult;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.geojson.GeojsonActivity;
import com.sfmap.map.demo.util.Constants;
import com.sfmap.map.demo.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据集 结果列表
 */
public class CloudSearchListResultActivity extends Activity implements  CloudDatasetSearch.OnCloudDatasetSearchListener, CloudSearch.OnCloudSearchListener{
    //数据展示listview
    private ListView listSearchView;
    //适配器
    private SearchAdapter searchAdapter;
    //云检索类
    private CloudSearch cloudSearch;
    private CloudDatasetSearch datasetSearch;
    //提示框
    private ProgressDialog progDialog = null;
    //没有数据时候提示文本
    private TextView tv_tip;
    private CloudDatasetSearchResult geojson_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_result);
        listSearchView = (ListView) findViewById(R.id.list_search);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        searchAdapter = new SearchAdapter();
        listSearchView.setAdapter(searchAdapter);
        listSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long dataId = searchAdapter.getCloudListId(position);
                CloudDatasetItem datasetItem = (CloudDatasetItem) searchAdapter.getItem(position);
                if (getIntent().getParcelableExtra(Constants.BUNDLE_GEOJSON_DATASET) != null){
                    Intent intent = new Intent();
                    intent.setClass(CloudSearchListResultActivity.this, GeojsonActivity.class);
                    intent.putExtra(Constants.BUNDLE_GEOJSON_DATASETITEM, datasetItem);
                    startActivity(intent);
                } else {
                    showProgressDialog();
                    searchDatasetById(dataId);
                }
            }
        });

        datasetSearch = new CloudDatasetSearch(this);
        datasetSearch.setSearchListener(this);
        cloudSearch = new CloudSearch(this);
        cloudSearch.setSearchListener(this);

        if (getIntent().getParcelableExtra(Constants.BUNDLE_GEOJSON_DATASET) != null){
            geojson_result = (CloudDatasetSearchResult)getIntent().getParcelableExtra(Constants.BUNDLE_GEOJSON_DATASET);
            final ArrayList<CloudDatasetItem> cloudDatasetItemList = (ArrayList<CloudDatasetItem>) geojson_result.getResults();
            searchAdapter.update(cloudDatasetItemList);
        } else {
            //查询全部
            searchDatasetAll();
        }
    }

    @Override
    public void onIDSearched(CloudDatasetSearchResult result, int status) {

    }

    @Override
    public void onPageSearched(CloudDatasetSearchResult result, int status) {
    }

    @Override
    public void onAllSearched(CloudDatasetSearchResult result, int status) {
        dissmissProgressDialog();
        if(result == null){
            return;
        }
        final ArrayList<CloudDatasetItem> cloudDatasetItemList = (ArrayList<CloudDatasetItem>) result.getResults();
        if(cloudDatasetItemList == null || cloudDatasetItemList.size() == 0){
            notData();
            return;
        }
        searchAdapter.update(cloudDatasetItemList);
    }

    @Override
    public void onBBoxSearched(CloudSearchResult result, int status) {

    }

    @Override
    public void onConditionSearched(CloudSearchResult result, int status) {
        dissmissProgressDialog();
        ArrayList<CloudItem> cloudDatasetItemList = (ArrayList<CloudItem>) result.getResults();
        if(cloudDatasetItemList == null || cloudDatasetItemList.size() == 0){
            ToastUtil.show(CloudSearchListResultActivity.this,"当前数据集无数据!");
            return;
        }

        ArrayList<CloudPointResult> cloudPointResults = new ArrayList<CloudPointResult>();
        for(int n=0;n<cloudDatasetItemList.size();n++){
            CloudItem cloudItem = cloudDatasetItemList.get(n);
            CloudPointResult cloudPointResult = new CloudPointResult();
            cloudPointResult.mLon = cloudItem.getLon();
            cloudPointResult.mLat = cloudItem.getLat();
            cloudPointResults.add(cloudPointResult);
        }

        Intent intent=new Intent(CloudSearchListResultActivity.this,CloudSearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cloudDataItem", cloudPointResults);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onIDSearched(CloudSearchResult result, int status) {
        Log.e("onIDSearched status",String.valueOf(status));
    }

    public class SearchAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public SearchAdapter(){
            mInflater = LayoutInflater.from(CloudSearchListResultActivity.this);
        }
        private List<CloudDatasetItem> cloudDatasetItemList = new ArrayList<CloudDatasetItem>();
        public void update(List<CloudDatasetItem> cloudDatasetItemList){
            this.cloudDatasetItemList.clear();
            this.cloudDatasetItemList.addAll(cloudDatasetItemList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.cloudDatasetItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return this.cloudDatasetItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_search, null);
                holder.title = (TextView)convertView.findViewById(R.id.tv_content);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }

            long id = cloudDatasetItemList.get(position).getId();
            StringBuffer sb = new StringBuffer();
            sb.append("数据集类型 : ");
            int geoType = cloudDatasetItemList.get(position).getGeoType();
            if (geoType == 1){
                sb.append("点");
            } else if (geoType == 2){
                sb.append("线");
            } else if (geoType == 3){
                sb.append("面");
            }
            sb.append("，数据集ID : ");
            sb.append(id);
            holder.title.setText(sb.toString());
            return convertView;
        }

        class ViewHolder {
            public TextView title;
        }

        public long getCloudListId(int position){
            return cloudDatasetItemList.get(position).getId();
        }
    }


    /**
     * 数据集id检索
     */
    private void searchDatasetById(long datasetId){
        CloudSearch.Query query = new CloudSearch.Query(datasetId, "", 1, 10);
        cloudSearch.setQuery(query);
//        cloudSearch.searchDataAsyn();
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
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 数据集条件查询全部
     */
    private void searchDatasetAll(){
        showProgressDialog();
        CloudDatasetSearch.Query query = new CloudDatasetSearch.Query(0, 0, "", 1);
        datasetSearch.setQuery(query);
//        datasetSearch.searchDatasetAsyn();
    }

    /**
     * 没有数据
     */
    public void notData(){
        ToastUtil.show(this,"当前无数据!");
        listSearchView.setVisibility(View.GONE);
        tv_tip.setVisibility(View.VISIBLE);
        tv_tip.setText("没有数据");
    }
}
