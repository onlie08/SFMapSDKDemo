package com.sfmap.map.demo.busline;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapController.InfoWindowAdapter;
import com.sfmap.api.maps.MapController.OnMarkerClickListener;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.overlay.BusLineOverlay;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.ToastUtil;
import com.sfmap.api.services.busline.BusLineItem;
import com.sfmap.api.services.busline.BusLineQuery;
import com.sfmap.api.services.busline.BusLineQuery.SearchType;
import com.sfmap.api.services.busline.BusLineResult;
import com.sfmap.api.services.busline.BusLineSearch;
import com.sfmap.api.services.busline.BusLineSearch.OnBusLineSearchListener;
import com.sfmap.api.services.busline.BusStationItem;
import com.sfmap.api.services.busline.BusStationQuery;
import com.sfmap.api.services.busline.BusStationResult;
import com.sfmap.api.services.busline.BusStationSearch;
import com.sfmap.api.services.busline.BusStationSearch.OnBusStationSearchListener;

import java.util.List;

/**
 * sfmapMap地图中简单介绍公交线路搜索
 */
public class BuslineActivity extends Activity implements OnMarkerClickListener,
		InfoWindowAdapter, OnItemSelectedListener, OnBusLineSearchListener,
		OnBusStationSearchListener, OnClickListener {
	private MapController lMap;
	private MapView mapView;
	private ProgressDialog progDialog = null;
	private EditText searchName;
	private Spinner selectCity;
    private String[] itemCitys = { "北京-010", "郑州-0371", "上海-021" };
    private String cityCode = "";
    private int currentpage =1;
    private BusStationResult busStationResult;
    private BusLineResult busLineResult;
    private List<BusLineItem> lineItems = null;
    private BusLineQuery busLineQuery;
    private List<BusStationItem> stationItems;
    private BusStationQuery busStationQuery;
    private BusLineSearch busLineSearch;

        @Override
        protected void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            setContentView(R.layout.activity_busline);
            mapView = (MapView) findViewById(R.id.map);
            mapView.onCreate(bundle);// 此方法必须重写
            init();
        }

        /**
         * 初始化LMap对象
         */
        private void init() {
            try {
                if (lMap == null) {
                    lMap = mapView.getMap();
                    setUpMap();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Button searchByName = (Button) findViewById(R.id.searchbyname);
            Button searchByStationName = (Button) findViewById(R.id.searchbystationname);
            searchByName.setOnClickListener(this);
            searchByStationName.setOnClickListener(this);
            selectCity = (Spinner) findViewById(R.id.cityName);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, itemCitys);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectCity.setAdapter(adapter);
            selectCity.setPrompt("请选择城市：");
            selectCity.setOnItemSelectedListener(this);
            searchName = (EditText) findViewById(R.id.busName);

        }

        /**
         * 设置marker的监听和信息窗口的监听
         */
        private void setUpMap() {
            lMap.setOnMarkerClickListener(this);
            lMap.setInfoWindowAdapter(this);
        }

        /**
         * 方法重写
         */
        @Override
        protected void onResume() {
            super.onResume();
            mapView.onResume();
        }

        /**
         * 方法重写
         */
        @Override
        protected void onPause() {
            super.onPause();
            mapView.onPause();
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
            mapView.onDestroy();
        }

        /**
         * 公交站点搜索
         */
        public void searchStation() {
            currentpage = 1;// 第一页默认从1开始
            showProgressDialog();
            String search = searchName.getText().toString().trim();
            if ("".equals(search)) {
                search = "中关村北";
                searchName.setText(search);
            }
            busStationQuery = new BusStationQuery(search, BusStationQuery.StopSearchType.BY_STOP_NAME,
                    cityCode);// 第一个参数表示公交站点名称，第二个参数表示公交站点查询类型，第三个参数表示所在城市名或者城市区号
            busStationQuery.setPageSize(10);// 设置每页返回多少条数据
            busStationQuery.setPageNumber(currentpage);// 设置查询第几页，第一页从1开始算起
            BusStationSearch busStationSearch = new BusStationSearch(this,busStationQuery);;// 设置条件
            busStationSearch.setOnBusStationSearchListener(this);// 设置查询结果的监听
            busStationSearch.searchBusStationAsyn();// 异步查询公交站点

        }

        /**
         * 公交线路搜索
         */
        public void searchLine() {
            currentpage = 1;// 第一页默认从1开始
            showProgressDialog();
            String search = searchName.getText().toString().trim();
            if ("".equals(search)) {
                search = "115";
                searchName.setText(search);
            }
            busLineQuery = new BusLineQuery(search, SearchType.BY_LINE_NAME,
                    cityCode);// 第一个参数表示公交线路名，第二个参数表示公交线路查询类型，第三个参数表示所在城市名或者城市区号
            busLineQuery.setPageSize(10);// 设置每页返回多少条数据
            busLineQuery.setPageNumber(currentpage);// 设置查询第几页，第一页从0开始算起
            busLineSearch = new BusLineSearch(this, busLineQuery);// 设置条件
            busLineSearch.setOnBusLineSearchListener(this);// 设置查询结果的监听
            busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称

        }

        /**
         * 显示进度框
         */
        private void showProgressDialog() {
            if (progDialog == null)
                progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setIndeterminate(false);
            progDialog.setCancelable(true);
            progDialog.setMessage("正在搜索:\n");
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
         * 提供一个给默认信息窗口定制内容的方法
         */
        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        /**
         * 提供一个个性化定制信息窗口的方法
         */
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        /**
         * 点击marker回调函数
         */
        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;// 点击marker时把此marker显示在地图中心点
        }

        /**
         * 选择城市
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                   long id) {
            String cityString = itemCitys[position];
            cityCode = cityString.substring(cityString.indexOf("-") + 1);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            cityCode = "010";
        }

        /**
         * 公交线路搜索返回的结果显示在dialog中
         */
        public void showResultList(List<BusLineItem> busLineItems) {
            BusLineDialog busLineDialog = new BusLineDialog(this, busLineItems);
            busLineDialog.onListItemClicklistener(new OnListItemlistener() {
                @Override
                public void onListItemClick(BusLineDialog dialog,
                                            final BusLineItem item) {
                    showProgressDialog();

                    String lineId = item.getBusLineId();// 得到当前点击item公交线路id
                    busLineQuery = new BusLineQuery(lineId, SearchType.BY_LINE_ID,
                            cityCode);// 第一个参数表示公交线路id，第二个参数表示公交线路id查询，第三个参数表示所在城市名或者城市区号
                    BusLineSearch busLineSearch = new BusLineSearch(
                            BuslineActivity.this, busLineQuery);
                    busLineSearch.setOnBusLineSearchListener(BuslineActivity.this);
                    busLineSearch.searchBusLineAsyn();// 异步查询公交线路id
                }
            });
            busLineDialog.show();

        }

        interface OnListItemlistener {
		public void onListItemClick(BusLineDialog dialog, BusLineItem item);
	}

	/**
	 * 所有公交线路显示页面
	 */
	class BusLineDialog extends Dialog implements OnClickListener {

		private List<BusLineItem> busLineItems;
		private BusLineAdapter busLineAdapter;
		private Button preButton, nextButton;
		private ListView listView;
		protected OnListItemlistener onListItemlistener;

		public BusLineDialog(Context context, int theme) {
			super(context, theme);
		}

		public void onListItemClicklistener(
				OnListItemlistener onListItemlistener) {
			this.onListItemlistener = onListItemlistener;

		}

		public BusLineDialog(Context context, List<BusLineItem> busLineItems) {
			this(context, android.R.style.Theme_NoTitleBar);
			this.busLineItems = busLineItems;
			busLineAdapter = new BusLineAdapter(context, busLineItems);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_busline);
			preButton = (Button) findViewById(R.id.preButton);
			nextButton = (Button) findViewById(R.id.nextButton);
			listView = (ListView) findViewById(R.id.listview);
			listView.setAdapter(busLineAdapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					onListItemlistener.onListItemClick(BusLineDialog.this,
							busLineItems.get(arg2));
					dismiss();

				}
			});
			preButton.setOnClickListener(this);
			nextButton.setOnClickListener(this);
			if (currentpage <= 1) {
				preButton.setEnabled(false);
			}
			if (currentpage >= busLineResult.getPageCount()) {
				nextButton.setEnabled(false);
			}

		}

		@Override
		public void onClick(View v) {
			this.dismiss();
			if (v.equals(preButton)) {
				currentpage--;
			} else if (v.equals(nextButton)) {
				currentpage++;
			}
			showProgressDialog();
			busLineQuery.setPageNumber(currentpage);// 设置公交查询第几页
			busLineSearch.setOnBusLineSearchListener(BuslineActivity.this);
			busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
		}
	}

	/**
	 * 公交站点查询结果回调
	 */
	@Override
	public void onBusStationSearched(BusStationResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getBusStations() != null
					&& result.getBusStations().size() > 0) {
				busStationResult = result;
				stationItems = result.getBusStations();

                ToastUtil.show(BuslineActivity.this, getDistrictInfoStr(stationItems.get(0)));
			}
		} else if (rCode == 27) {
			ToastUtil.show(BuslineActivity.this, R.string.error_network);
		} else if (rCode == 102) {
			ToastUtil.show(BuslineActivity.this, R.string.error_key);
		} else if(rCode == 105){
            ToastUtil.show(BuslineActivity.this, " 查询结果为空。");
        } else {
			ToastUtil.show(BuslineActivity.this, R.string.error_other);
		}
	}

	/**
	 * 公交线路查询结果回调
	 */
	@Override
	public void onBusLineSearched(BusLineResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getQuery() != null
					&& result.getQuery().equals(busLineQuery)) {
				if (result.getQuery().getCategory() == SearchType.BY_LINE_NAME) {
					if ( result.getBusLines() != null && result.getPageCount() >0
							&& result.getBusLines().size() > 0) {
						busLineResult = result;
						lineItems = result.getBusLines();

						showResultList(lineItems);
					}
				} else if (result.getQuery().getCategory() == SearchType.BY_LINE_ID) {
                    lMap.clear();// 清理地图上的marker
					busLineResult = result;
					lineItems = busLineResult.getBusLines();
					BusLineOverlay busLineOverlay = new BusLineOverlay(this,
                            lMap, lineItems.get(0));
					busLineOverlay.removeFromMap();
					busLineOverlay.addToMap();
					busLineOverlay.zoomToSpan();
				}
			}
		} else if (rCode == 21) {
			ToastUtil.show(BuslineActivity.this, R.string.error_network);
		} else if (rCode == 102) {
            ToastUtil.show(BuslineActivity.this, R.string.error_key);
        } else if(rCode == 105){
            ToastUtil.show(BuslineActivity.this, " 查询结果为空。");
        } else {
            ToastUtil.show(BuslineActivity.this, R.string.error_other);
        }
	}

	/**
	 * 查询公交线路
	 */
	@Override
	public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchbyname:
                searchLine();
                break;
            case R.id.searchbystationname:
                lMap.clear();// 清理地图上的marker
                searchStation();
                break;

            default:
                break;
        }

	}


    /**
     * 获取站点的信息字符串
     * @param busStationItem
     *
     * */
    private String getDistrictInfoStr(BusStationItem busStationItem){
        StringBuffer sb = new StringBuffer();
        String name = busStationItem.getBusStationName();
        String adcode = busStationItem.getAdCode();
        String citycode = busStationItem.getCitycode();
        String lineName = busStationItem.getBusLineItems().get(0).getBusLineName();
        sb.append("站点名称:" + name + "\n");
        sb.append("行政区编号:" + adcode + "\n");
        sb.append("区号:" + citycode + "\n");
        sb.append("经过的公交线路名称:" + lineName + "\n");

        return sb.toString();
    }
}
