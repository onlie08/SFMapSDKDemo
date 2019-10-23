package com.sfmap.map.demo.route;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapController.InfoWindowAdapter;
import com.sfmap.api.maps.MapController.OnInfoWindowClickListener;
import com.sfmap.api.maps.MapController.OnMapClickListener;
import com.sfmap.api.maps.MapController.OnMarkerClickListener;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.MapsInitializer;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;
import com.sfmap.api.maps.overlay.BusRouteOverlay;
import com.sfmap.api.maps.overlay.DrivingRouteOverlay;
import com.sfmap.api.maps.overlay.WalkRouteOverlay;
import com.sfmap.api.services.core.LatLonPoint;
import com.sfmap.api.services.poisearch.PoiItem;
import com.sfmap.api.services.poisearch.PoiResult;
import com.sfmap.api.services.poisearch.PoiSearch;
import com.sfmap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.sfmap.api.services.route.BusPath;
import com.sfmap.api.services.route.BusRouteResult;
import com.sfmap.api.services.route.DrivePath;
import com.sfmap.api.services.route.DriveRouteResult;
import com.sfmap.api.services.route.RouteSearch;
import com.sfmap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.sfmap.api.services.route.WalkPath;
import com.sfmap.api.services.route.WalkRouteResult;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.LMapUtil;
import com.sfmap.map.demo.util.OffLineMapUtils;
import com.sfmap.map.demo.util.ToastUtil;

import java.util.List;

/**
 * sfmapMap地图中简单介绍route搜索
 */
public class RouteActivity extends Activity implements OnMarkerClickListener,
		OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter,
		OnPoiSearchListener, OnRouteSearchListener, OnClickListener {
	private MapController lMap;
	private MapView mapView;
	private Button drivingButton;
	private Button busButton;
	private Button walkButton;

	private ImageButton startImageButton;
	private ImageButton endImageButton;
	private ImageButton routeSearchImagebtn;

	private EditText startTextView;
	private EditText endTextView;
	private ProgressDialog progDialog = null;// 搜索时进度条
	private BusRouteResult busRouteResult;// 公交模式查询结果
	private DriveRouteResult driveRouteResult;// 驾车模式查询结果
	private WalkRouteResult walkRouteResult;// 步行模式查询结果
	private int routeType = 1;// 1代表公交模式，2代表驾车模式，3代表步行模式
	private String strStart;
	private String strEnd;
	private LatLonPoint startPoint = null;
	private LatLonPoint endPoint = null;
	private PoiSearch.Query startSearchQuery;
	private PoiSearch.Query endSearchQuery;

	private boolean isClickStart = false;
	private boolean isClickTarget = false;
	private Marker startMk, targetMk;
	private RouteSearch routeSearch;
	public ArrayAdapter<String> aAdapter;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_route);
        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(bundle);// 此方法必须重写
		init();
	}

	private void init() {
			if (lMap == null) {
                lMap = mapView.getMap();
                registerListener();
            }
		routeSearch = new RouteSearch(RouteActivity.this);
		routeSearch.setRouteSearchListener(this);
		startTextView = (EditText) findViewById(R.id.autotextview_roadsearch_start);
		endTextView = (EditText) findViewById(R.id.autotextview_roadsearch_goals);
		busButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_transit);
		busButton.setOnClickListener(this);
		drivingButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_driving);
		drivingButton.setOnClickListener(this);
		walkButton = (Button) findViewById(R.id.imagebtn_roadsearch_tab_walk);
		walkButton.setOnClickListener(this);
		startImageButton = (ImageButton) findViewById(R.id.imagebtn_roadsearch_startoption);
		startImageButton.setOnClickListener(this);
		endImageButton = (ImageButton) findViewById(R.id.imagebtn_roadsearch_endoption);
		endImageButton.setOnClickListener(this);
		routeSearchImagebtn = (ImageButton) findViewById(R.id.imagebtn_roadsearch_search);
		routeSearchImagebtn.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	private void busRoute() {
		routeType = 1;// 标识为公交模式
		drivingButton.setBackgroundResource(R.drawable.route_mode_driving_off);
		busButton.setBackgroundResource(R.drawable.route_mode_transit_on);
		walkButton.setBackgroundResource(R.drawable.route_mode_walk_off);

	}

	private void drivingRoute() {
		routeType = 2;// 标识为驾车模式
		drivingButton.setBackgroundResource(R.drawable.route_mode_driving_on);
		busButton.setBackgroundResource(R.drawable.route_mode_transit_off);
		walkButton.setBackgroundResource(R.drawable.route_mode_walk_off);
	}

	private void walkRoute() {
		routeType = 3;// 标识为步行模式
		drivingButton.setBackgroundResource(R.drawable.route_mode_driving_off);
		busButton.setBackgroundResource(R.drawable.route_mode_transit_off);
		walkButton.setBackgroundResource(R.drawable.route_mode_walk_on);
	}

	private void startImagePoint() {
		ToastUtil.show(RouteActivity.this, "在地图上点击您的起点");
		isClickStart = true;
		isClickTarget = false;
		registerListener();
	}

	private void endImagePoint() {
		ToastUtil.show(RouteActivity.this, "在地图上点击您的终点");
		isClickTarget = true;
		isClickStart = false;
		registerListener();
	}

	public void searchRoute() {
		strStart = startTextView.getText().toString().trim();
		strEnd = endTextView.getText().toString().trim();
		if (strStart == null || strStart.length() == 0) {
			ToastUtil.show(RouteActivity.this, "请选择起点");
			return;
		}
		if (strEnd == null || strEnd.length() == 0) {
			ToastUtil.show(RouteActivity.this, "请选择终点");
			return;
		}
		if (strStart.equals(strEnd)) {
			ToastUtil.show(RouteActivity.this, "起点与终点距离很近，您可以步行前往");
			return;
		}

		startSearchResult();// 开始搜终点
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		isClickStart = false;
		isClickTarget = false;
		if (marker.equals(startMk)) {
			startTextView.setText("地图上的起点");
            com.sfmap.api.services.core.LatLonPoint mapLatlon = LMapUtil.convertToLatLonPoint(startMk.getPosition());
            startPoint = new LatLonPoint(mapLatlon.getLatitude(),mapLatlon.getLongitude());
			startMk.hideInfoWindow();
			startMk.remove();
		} else if (marker.equals(targetMk)) {
			endTextView.setText("地图上的终点");
            com.sfmap.api.services.core.LatLonPoint mapLatlon = LMapUtil.convertToLatLonPoint(targetMk.getPosition());
            endPoint = new LatLonPoint(mapLatlon.getLatitude(),mapLatlon.getLongitude());
			targetMk.hideInfoWindow();
			targetMk.remove();
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (marker.isInfoWindowShown()) {
			marker.hideInfoWindow();
		} else {
			marker.showInfoWindow();
		}
		return false;
	}

	@Override
	public void onMapClick(LatLng latng) {
		if (isClickStart) {
			startMk = lMap.addMarker(new MarkerOptions()
					.anchor(0.5f, 1)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.marker_point)).position(latng)
					.title("点击选择为起点"));
			startMk.showInfoWindow();
		} else if (isClickTarget) {
			targetMk = lMap.addMarker(new MarkerOptions()
					.anchor(0.5f, 1)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.marker_point)).position(latng)
					.title("点击选择为目的地"));
			targetMk.showInfoWindow();
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

	/**
	 * 注册监听
	 */
	private void registerListener() {
        lMap.setOnMapClickListener(RouteActivity.this);
        lMap.setOnMarkerClickListener(RouteActivity.this);
        lMap.setOnInfoWindowClickListener(RouteActivity.this);
        lMap.setInfoWindowAdapter(RouteActivity.this);
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
		progDialog.setMessage("正在搜索");
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
	 * 查询路径规划起点
	 */
	public void startSearchResult() {
		strStart = startTextView.getText().toString().trim();
		if (startPoint != null && strStart.equals("地图上的起点")) {
			endSearchResult();
		} else {
			showProgressDialog();

			startSearchQuery = new PoiSearch.Query(strStart, "", "北京"); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					startSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn();// 异步poi查询
		}
	}

	/**
	 * 查询路径规划终点
	 */
	public void endSearchResult() {
		strEnd = endTextView.getText().toString().trim();
		if (endPoint != null && strEnd.equals("地图上的终点")) {
			searchRouteResult(startPoint, endPoint);
		} else {
			showProgressDialog();
			endSearchQuery = new PoiSearch.Query(strEnd, "", "北京"); // 第一个参数表示查询关键字，第二参数表示poi搜索类型，第三个参数表示城市区号或者城市名
			endSearchQuery.setPageNum(0);// 设置查询第几页，第一页从0开始
			endSearchQuery.setPageSize(20);// 设置每页返回多少条数据

			PoiSearch poiSearch = new PoiSearch(RouteActivity.this,
					endSearchQuery);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.searchPOIAsyn(); // 异步poi查询
		}
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		showProgressDialog();
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);
		if (routeType == 1) {// 公交路径规划
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo);//参数表示路径规划的起点和终点
			routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == 2) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo);// 参数表示路径规划的起点和终点
//			LatLonPoint s = new LatLonPoint(39.91553, 116.396989);
//			ArrayList<LatLonPoint> list = new ArrayList<LatLonPoint>();
//			list.add(s);
//			query.setPassedByPoints(list);

			routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == 3) {// 步行路径规划
			RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);//参数表示路径规划的起点和终点
			routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}


    @Override
    public void onPoiItemSearched(List<PoiItem> result, int errorCode) {

    }

    /**
	 * POI搜索结果回调
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {// 返回成功
            if (result != null && result.getQuery() != null
                    && result.getPois() != null && result.getPois().size() > 0) {// 搜索poi的结果
                if (result.getQuery().equals(startSearchQuery)) {
                    List<PoiItem> poiItems = result.getPois();// 取得poiitem数据
                    RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
                            RouteActivity.this, poiItems);
                    dialog.setTitle("您要找的起点是:");
                    dialog.show();
                    dialog.setOnListClickListener(new RouteSearchPoiDialog.OnListItemClick() {
                        @Override
                        public void onListItemClick(
                                RouteSearchPoiDialog dialog,
                                PoiItem startpoiItem) {
                            startPoint = startpoiItem.getLatLonPoint();
                            strStart = startpoiItem.getTitle();
                            startTextView.setText(strStart);
                            endSearchResult();// 开始搜终点
                        }

                    });
                } else if (result.getQuery().equals(endSearchQuery)) {
                    List<PoiItem> poiItems = result.getPois();// 取得poiitem数据
                    RouteSearchPoiDialog dialog = new RouteSearchPoiDialog(
                            RouteActivity.this, poiItems);
                    dialog.setTitle("您要找的终点是:");
                    dialog.show();
                    dialog.setOnListClickListener(new RouteSearchPoiDialog.OnListItemClick() {
                        @Override
                        public void onListItemClick(
                                RouteSearchPoiDialog dialog, PoiItem endpoiItem) {
                            endPoint = endpoiItem.getLatLonPoint();
                            strEnd = endpoiItem.getTitle();
                            endTextView.setText(strEnd);
                            searchRouteResult(startPoint, endPoint);// 进行路径规划搜索
                        }

                    });
                }
            }
        } else if (rCode == 21) {
            ToastUtil.show(RouteActivity.this, R.string.error_network);
        } else if (rCode == 102) {
            ToastUtil.show(RouteActivity.this, R.string.error_key);
        } else if(rCode == 105){
            ToastUtil.show(RouteActivity.this, " 查询结果为空。");
        }  else if(rCode == 10000){
            ToastUtil.show(RouteActivity.this, " 引擎异常。");
        }else {
            ToastUtil.show(RouteActivity.this, R.string.error_other);
        }
    }

	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				busRouteResult = result;
				BusPath busPath = busRouteResult.getPaths().get(0);
                lMap.clear();// 清理地图上的所有覆盖物
                LatLonPoint s = busRouteResult.getStartPosInfo().getLocation();
                LatLonPoint end= busRouteResult.getTargetPosInfo().getLocation();
				BusRouteOverlay routeOverlay = new BusRouteOverlay(this, lMap,
						busPath, busRouteResult.getStartPos(),
						busRouteResult.getTargetPos());
				routeOverlay.removeFromMap();
				routeOverlay.addToMap();
				routeOverlay.zoomToSpan();
			} else {
				ToastUtil.show(RouteActivity.this, R.string.error_no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(RouteActivity.this, R.string.error_network);
		} else if (rCode == 102) {
            ToastUtil.show(RouteActivity.this, R.string.error_key);
        } else if(rCode == 105){
            ToastUtil.show(RouteActivity.this, " 查询结果为空。");
        } else {
            ToastUtil.show(RouteActivity.this, R.string.error_other);
        }
	}

	/**
	 * 驾车结果回调
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
                lMap.clear();// 清理地图上的所有覆盖物
				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
						this, lMap, drivePath, driveRouteResult.getStartPos(),
						driveRouteResult.getTargetPos());
				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.zoomToSpan();
			} else {
				ToastUtil.show(RouteActivity.this, R.string.error_no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(RouteActivity.this, R.string.error_network);
		} else if (rCode == 102) {
            ToastUtil.show(RouteActivity.this, R.string.error_key);
        } else if(rCode == 105){
            ToastUtil.show(RouteActivity.this, " 查询结果为空。");
        } else {
            ToastUtil.show(RouteActivity.this, R.string.error_other);
        }
	}

	/**
	 * 步行路线结果回调
	 */
	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                walkRouteResult = result;
                WalkPath walkPath = walkRouteResult.getPaths().get(0);

                lMap.clear();// 清理地图上的所有覆盖物
                WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,
                        lMap, walkPath, walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.addToMap();
                walkRouteOverlay.zoomToSpan();
            }
        }else if (rCode == 27) {
                ToastUtil.show(RouteActivity.this, R.string.error_network);
            } else if (rCode == 102) {
                ToastUtil.show(RouteActivity.this, R.string.error_key);
            } else if(rCode == 105){
                ToastUtil.show(RouteActivity.this, " 查询结果为空。");
            } else if(rCode == 10000) {
                ToastUtil.show(RouteActivity.this, " 引擎异常。");
            } else {
                ToastUtil.show(RouteActivity.this, R.string.error_other);
            }
        }


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imagebtn_roadsearch_startoption:
			startImagePoint();
			break;
		case R.id.imagebtn_roadsearch_endoption:
			endImagePoint();
			break;
		case R.id.imagebtn_roadsearch_tab_transit:
			busRoute();
			break;
		case R.id.imagebtn_roadsearch_tab_driving:
			drivingRoute();
			break;
		case R.id.imagebtn_roadsearch_tab_walk:
			walkRoute();
			break;
		case R.id.imagebtn_roadsearch_search:
			searchRoute();
			break;
		default:
			break;
		}
	}


}
