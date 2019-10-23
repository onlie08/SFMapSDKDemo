package com.sfmap.map.demo.offlinemap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.offlinemap.OfflineMapCity;
import com.sfmap.api.maps.offlinemap.OfflineMapManager;
import com.sfmap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.sfmap.api.maps.offlinemap.OfflineMapProvince;
import com.sfmap.api.maps.offlinemap.OfflineMapStatus;
import com.sfmap.map.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * sfmapMap地图中简单介绍离线地图下载
 */
public class OfflineMapActivity extends Activity implements
		OfflineMapDownloadListener, OnClickListener, OnPageChangeListener {

	private OfflineMapManager mapManager = null;// 离线地图下载控制器
	private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();// 保存一级目录的省直辖市

	private TextView mDownloadText;
	private TextView mDownloadedText;
	private ImageView mBackImage;

	private ViewPager mContentViewPage;
	private ExpandableListView mAllOfflineMapList;
	private ListView mDownLoadedList;

	private OfflineListAdapter adapter;
	private OfflineDownloadedAdapter mDownloadedAdapter;
	private PagerAdapter mPageAdapter;

	private MapView mapView;

	// 刚进入该页面时初始化弹出的dialog
	private ProgressDialog initDialog;

	/**
	 * 更新所有列表
	 */
	private final static int UPDATE_LIST = 0;
	/**
	 * 显示toast log
	 */
	private final static int SHOW_MSG = 1;

	private final static int DISMISS_INIT_DIALOG = 2;
	private final static int SHOW_INIT_DIALOG = 3;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
				case UPDATE_LIST:
					if (mContentViewPage.getCurrentItem() == 0) {
						((BaseExpandableListAdapter) adapter)
								.notifyDataSetChanged();
					} else {
						mDownloadedAdapter.notifyDataChange();
					}

					break;
				case SHOW_MSG:
					ToastUtil.showShortToast(OfflineMapActivity.this, (String)msg.obj);
					break;

				case DISMISS_INIT_DIALOG:
					initDialog.dismiss();
					handler.sendEmptyMessage(UPDATE_LIST);
					break;
				case SHOW_INIT_DIALOG:
					if (initDialog != null) {
						initDialog.show();
					}

					break;

				default:
					break;
			}
		}

	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offline_map_layout);
		init();
	}

	/**
	 * 初始化如果已下载的城市多的话，会比较耗时
	 */
	private void initDialog() {

		initDialog = new ProgressDialog(this);
		initDialog.setMessage("正在获取离线城市列表");
		initDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		initDialog.setCancelable(false);
		initDialog.show();

		handler.sendEmptyMessage(SHOW_INIT_DIALOG);

		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();

				final Handler handler1 = new Handler();
				handler1.postDelayed(new Runnable() {
					@Override
					public void run() {
						// Do Work
						init();
						handler.sendEmptyMessage(DISMISS_INIT_DIALOG);
						handler.removeCallbacks(this);
						Looper.myLooper().quit();
					}
				}, 10);
				Looper.loop();
			}
		}).start();
	}

	/**
	 * 初始化UI布局文件
	 */
	private void init() {

		// 此版本限制，使用离线地图，请初始化一个MapView
		mapView = new MapView(this);

		initAllCityList();
		initDownloadedList();

		// 顶部
		mDownloadText = (TextView) findViewById(R.id.download_list_text);
		mDownloadedText = (TextView) findViewById(R.id.downloaded_list_text);

		mDownloadText.setOnClickListener(this);
		mDownloadedText.setOnClickListener(this);
		mBackImage = (ImageView) findViewById(R.id.back_image_view);
		mBackImage.setOnClickListener(this);

		// view pager 用到了所有城市list和已下载城市list所有放在最后初始化
		mContentViewPage = (ViewPager) findViewById(R.id.content_viewpage);

		mPageAdapter = new OfflinePagerAdapter(mContentViewPage,
				mAllOfflineMapList, mDownLoadedList);

		mContentViewPage.setAdapter(mPageAdapter);
		mContentViewPage.setCurrentItem(0);
		mContentViewPage.setOnPageChangeListener(this);

	}

	/**
	 * 初始化所有城市列表
	 */
	public void initAllCityList() {
		// 扩展列表
		View provinceContainer = LayoutInflater.from(OfflineMapActivity.this)
				.inflate(R.layout.offline_province_listview, null);
		mAllOfflineMapList = (ExpandableListView) provinceContainer
				.findViewById(R.id.province_download_list);

		mapManager = new OfflineMapManager(this, this);

		initProvinceListAndCityMap();

		adapter = new OfflineListAdapter(provinceList, mapManager,
				OfflineMapActivity.this);
		mAllOfflineMapList.setAdapter(adapter);
		mAllOfflineMapList.setOnGroupCollapseListener(adapter);
		mAllOfflineMapList.setOnGroupExpandListener(adapter);
		mAllOfflineMapList.setGroupIndicator(null);
	}

	private void initProvinceListAndCityMap() {

		List<OfflineMapProvince> lists = mapManager
				.getOfflineMapProvinceList();
		provinceList.clear();
		ArrayList<OfflineMapCity> cityList = new ArrayList<>();// 以市格式保存直辖市、港澳、全国概要图
		ArrayList<OfflineMapCity> gangaoList = new ArrayList<>();// 保存港澳城市
		ArrayList<OfflineMapCity> gaiyaotuList = new ArrayList<>();// 保存概要图
		// 添加，概要图，直辖市，港口
		OfflineMapProvince gaiyaotu = new OfflineMapProvince();
		gaiyaotu.setProvinceName("概要图");
		gaiyaotu.setCityList(gaiyaotuList);
		provinceList.add(gaiyaotu);

		OfflineMapProvince zhixiashi = new OfflineMapProvince();
		zhixiashi.setProvinceName("直辖市");
		zhixiashi.setCityList(cityList);
		provinceList.add( zhixiashi);

		OfflineMapProvince gaogao = new OfflineMapProvince();
		gaogao.setProvinceName("港澳");
		gaogao.setCityList(gangaoList);
		provinceList.add( gaogao);
		for (int i = 0; i < lists.size(); i++) {
			OfflineMapProvince province = lists.get(i);
			if (province.getCityList().size() != 1) {
				// 普通省份
				provinceList.add( province);
			} else {
				String name = province.getProvinceName();
				if (name.contains("澳门")) {
					gangaoList.addAll(province.getCityList());
				} else if (name.contains("全国概要图")) {
					gaiyaotuList.addAll(province.getCityList());
				} else {
					cityList.addAll(province.getCityList());
				}
			}
		}




	}

	/**
	 * 初始化已下载列表
	 */
	public void initDownloadedList() {
		mDownLoadedList = (ListView) LayoutInflater.from(
				OfflineMapActivity.this).inflate(
				R.layout.offline_downloaded_list, null);
		android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(
				android.widget.AbsListView.LayoutParams.MATCH_PARENT,
				android.widget.AbsListView.LayoutParams.WRAP_CONTENT);
		mDownLoadedList.setLayoutParams(params);
		mDownloadedAdapter = new OfflineDownloadedAdapter(this, mapManager);
		mDownLoadedList.setAdapter(mDownloadedAdapter);
	}

	/**
	 * 暂停所有下载和等待
	 */
	private void stopAll() {
		if (mapManager != null) {
			mapManager.stop();
		}
	}

	/**
	 * 继续下载所有暂停中
	 */
	private void startAllInPause() {
		if (mapManager == null) {
			return;
		}
		for (OfflineMapCity mapCity : mapManager.getDownloadingCityList()) {
			if (mapCity.getState() == OfflineMapStatus.PAUSE) {
				try {
					mapManager.downloadByCityName(mapCity.getCity());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 取消所有<br>
	 */
	private void cancelAll() {
		if (mapManager == null) {
			return;
		}
		for (OfflineMapCity mapCity : mapManager.getDownloadingCityList()) {
			if (mapCity.getState() == OfflineMapStatus.PAUSE) {
				mapManager.remove(mapCity.getCity());
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mapView != null) {
			mapView.onDestroy();
		}
		if (mapManager != null) {
			mapManager.destroy();
		}

		if(initDialog != null) {
			initDialog.dismiss();
			initDialog.cancel();
		}
	}

	private void logList() {
		ArrayList<OfflineMapCity> list = mapManager.getDownloadingCityList();

		for (OfflineMapCity offlineMapCity : list) {
			Log.i("map-city-loading: ", offlineMapCity.getCity() + ","
					+ offlineMapCity.getState());
		}

		ArrayList<OfflineMapCity> list1 = mapManager
				.getDownloadOfflineMapCityList();

		for (OfflineMapCity offlineMapCity : list1) {
			Log.i("map-city-loaded: ", offlineMapCity.getCity() + ","
					+ offlineMapCity.getState());
		}
	}

	/**
	 * 离线地图下载回调方法
	 */
	@Override
	public void onDownload(int status, int completeCode, String downName) {

		switch (status) {
			case OfflineMapStatus.SUCCESS:
				break;
			case OfflineMapStatus.LOADING:
				Log.d("map-download", "download: " + completeCode + "%" + ","
						+ downName);
				break;
			case OfflineMapStatus.UNZIP:
				Log.d("map-unzip", "unzip: " + completeCode + "%" + "," + downName);
				break;
			case OfflineMapStatus.WAITING:
				Log.d("map-waiting", "WAITING: " + completeCode + "%" + ","
						+ downName);
				break;
			case OfflineMapStatus.PAUSE:
				Log.d("map-pause", "pause: " + completeCode + "%" + "," + downName);
				break;
			case OfflineMapStatus.STOP:
				break;
			case OfflineMapStatus.ERROR:
				Log.e("map-download", "download: " + " ERROR " + downName);
				break;
			case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
				Log.e("map-download", "download: " + " EXCEPTION_NETWORK_LOADING "
						+ downName);
				Toast.makeText(OfflineMapActivity.this, "网络异常", Toast.LENGTH_SHORT)
						.show();
				mapManager.pause();
				break;
			case OfflineMapStatus.EXCEPTION_SDCARD:
				Log.e("map-download", "download: " + " EXCEPTION_SDCARD "
						+ downName);
				break;
			default:
				break;
		}
		handler.sendEmptyMessage(UPDATE_LIST);

	}

	@Override
	public void onCheckUpdate(boolean hasNew, String name) {
		Log.i("map-demo", "onCheckUpdate " + name + " : " + hasNew);
		Message message = new Message();
		message.what = SHOW_MSG;
		message.obj = "CheckUpdate " + name + " : " + hasNew;
		handler.sendMessage(message);

	}

	@Override
	public void onRemove(boolean success, String name, String describe) {
		Log.i("map-demo", "onRemove " + name + " : " + success + " , "
				+ describe);
		handler.sendEmptyMessage(UPDATE_LIST);

		Message message = new Message();
		message.what = SHOW_MSG;
		message.obj = "onRemove " + name + " : " + success + " , " + describe;
		handler.sendMessage(message);

	}

	@Override
	public void reloadCityList() {
		//重新获取城市列表
		List<OfflineMapProvince> lists = mapManager
				.getOfflineMapProvinceList();
		initProvinceListAndCityMap();
		adapter.notifyDataSetChanged();
//		mPageAdapter.notifyDataSetChanged();
		Log.i("map-demo", "getCityList："+lists.size());

	}

	@Override
	public void onClick(View v) {
		if (v.equals(mDownloadText)) {
			int paddingHorizontal = mDownloadText.getPaddingLeft();
			int paddingVertical = mDownloadText.getPaddingTop();
			mContentViewPage.setCurrentItem(0);

			mDownloadText
					.setBackgroundResource(R.drawable.offline_arrow_tab1_pressed);
			mDownloadText.setTextColor(Color.parseColor("#ffffff"));
			mDownloadedText
					.setBackgroundResource(R.drawable.offline_arrow_tab2_normal);
			mDownloadedText.setTextColor(Color.parseColor("#2CA09E"));
			mDownloadedText.setPadding(paddingHorizontal, paddingVertical,
					paddingHorizontal, paddingVertical);

			mDownloadText.setPadding(paddingHorizontal, paddingVertical,
					paddingHorizontal, paddingVertical);

			mDownloadedAdapter.notifyDataChange();

		} else if (v.equals(mDownloadedText)) {
			int paddingHorizontal = mDownloadedText.getPaddingLeft();
			int paddingVertical = mDownloadedText.getPaddingTop();
			mContentViewPage.setCurrentItem(1);

			mDownloadText
					.setBackgroundResource(R.drawable.offline_arrow_tab1_normal);
			mDownloadText.setTextColor(Color.parseColor("#2CA09E"));
			mDownloadedText
					.setBackgroundResource(R.drawable.offline_arrow_tab2_pressed);
			mDownloadedText.setTextColor(Color.parseColor("#ffffff"));
			mDownloadedText.setPadding(paddingHorizontal, paddingVertical,
					paddingHorizontal, paddingVertical);
			mDownloadText.setPadding(paddingHorizontal, paddingVertical,
					paddingHorizontal, paddingVertical);

			mDownloadedAdapter.notifyDataChange();

		} else if (v.equals(mBackImage)) {
			finish();
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		int paddingHorizontal = mDownloadedText.getPaddingLeft();
		int paddingVertical = mDownloadedText.getPaddingTop();

		switch (arg0) {
			case 0:
				mDownloadText
						.setBackgroundResource(R.drawable.offline_arrow_tab1_pressed);
				mDownloadText.setTextColor(Color.parseColor("#ffffff"));
				mDownloadedText
						.setBackgroundResource(R.drawable.offline_arrow_tab2_normal);
				mDownloadedText.setTextColor(Color.parseColor("#2CA09E"));
				break;
			case 1:
				mDownloadText
						.setBackgroundResource(R.drawable.offline_arrow_tab1_normal);
				mDownloadText.setTextColor(Color.parseColor("#2CA09E"));
				mDownloadedText
						.setBackgroundResource(R.drawable.offline_arrow_tab2_pressed);
				mDownloadedText.setTextColor(Color.parseColor("#FFFFFF"));
				break;
		}
		handler.sendEmptyMessage(UPDATE_LIST);
		mDownloadedText.setPadding(paddingHorizontal, paddingVertical,
				paddingHorizontal, paddingVertical);
		mDownloadText.setPadding(paddingHorizontal, paddingVertical,
				paddingHorizontal, paddingVertical);

	}
}
