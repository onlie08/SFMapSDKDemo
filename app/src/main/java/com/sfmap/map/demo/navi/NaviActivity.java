package com.sfmap.map.demo.navi;

import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.SfMapLocationClient;
import com.sfmap.api.location.SfMapLocationClientOption;
import com.sfmap.api.location.SfMapLocationListener;
import com.sfmap.api.navi.Navi;
import com.sfmap.api.navi.NaviView;
import com.sfmap.api.navi.NaviViewOptions;
import com.sfmap.api.navi.model.NaviLatLng;
import com.sfmap.map.demo.R;

import java.util.ArrayList;
import java.util.List;

public class NaviActivity extends NaviBaseActivity implements SfMapLocationListener {
    // 默认多策略
    public final static int CARROUTE_INDEX_DEFAULT = 9;

    // 推荐道路
    public final static int CARROUTE_INDEX_0 = 0;
    // 避免收费
    public static final int CARROUTE_INDEX_1 = 1;
    // 距离最短
    public final static int CARROUTE_INDEX_2 = 2;
    // 高速优先
    public final static int CARROUTE_INDEX_3 = 3;

    // 驾车导航
    public final static int ROUTE_TYPE_CAR = 1;
    // 货车导航
    public final static int ROUTE_TYPE_TRUCK = 3;

    private String TAG = "NaviActivity";
    private NaviView mNaviView;
    private Navi mNavi;

    //算路终点坐标
    protected NaviLatLng mEndLatlng = new NaviLatLng(22.606078, 114.028124);
    //算路起点坐标
    protected NaviLatLng mStartLatlng = new NaviLatLng(22.524158, 113.941194);
    //算路起点坐标
    protected TruckInfo mTruckInfo;
    //存储算路起点的列表
    protected final List<NaviLatLng> startPoints = new ArrayList<>();
    //存储算路终点的列表
    protected final List<NaviLatLng> endPoints = new ArrayList<>();
    private NaviViewOptions mNaviViewOptions;
    private LocationManager mLocationManager;
    //    private LocationManager locationManager;
    private int planMode = 9;
    private int routeType = 1;
    private SFSpeechSyntesizer sfSpeechSyntesizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_demo);
        initView(savedInstanceState);

        String startLatlng = getIntent().getStringExtra("startLatlng");
        String endLatlng = getIntent().getStringExtra("endLatlng");
        String truckInfo = getIntent().getStringExtra("truckInfo");
        routeType = getIntent().getIntExtra("routeType",ROUTE_TYPE_CAR);
        planMode = getIntent().getIntExtra("planMode",CARROUTE_INDEX_2);

        if(!TextUtils.isEmpty(startLatlng) && !TextUtils.isEmpty(endLatlng)){
            mStartLatlng = new Gson().fromJson(startLatlng, NaviLatLng.class);
            mEndLatlng = new Gson().fromJson(endLatlng, NaviLatLng.class);
        }
        if(!TextUtils.isEmpty(truckInfo)){
            mTruckInfo = new Gson().fromJson(truckInfo, TruckInfo.class);
        }
        sfSpeechSyntesizer = SFSpeechSyntesizer.getInstance(NaviActivity.this);
        initNaviData();
        initSfLocationClient();

    }

    private void initNaviData() {
        mNavi = Navi.getInstance(this);
        mNavi.addNaviListener(this);
        //设置模拟导航的行车速度
        mNavi.setEmulatorNaviSpeed(300);
        startPoints.add(mStartLatlng);
        endPoints.add(mEndLatlng);
        mNavi.setSoTimeout(15000);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mNaviViewOptions = mNaviView.getViewOptions();
    }

    private void initView(Bundle savedInstanceState) {
        mNaviView = findViewById(R.id.navi_view);
        mNaviView.setMapNaviViewListener(this);
        mNaviView.onCreate(savedInstanceState);
        mNaviViewOptions = mNaviView.getViewOptions();
        mNaviView.getMap().getUiSettings().setCompassEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try{
            mSfMapLocationClient.stopLocation();
            mSfMapLocationClient.destroy();

            mNavi.stopNavi();
            mNavi.destroy();
            mNaviView.onDestroy();
            sfSpeechSyntesizer.destroy();
        }catch (Exception e){

        }
    }

    @Override
    public void onGetNavigationText(int i, String s) {
        sfSpeechSyntesizer.startSpeaking(s);
    }

    @Override
    public void onReCalculateRouteForYaw() {
        sfSpeechSyntesizer.startSpeaking("您已偏航,已为您重新规划路线");
    }

    @Override
    public void onArrivedWayPoint(int i) {
        sfSpeechSyntesizer.startSpeaking("到达第" + i + "个途径点");
    }

    /**
     * 导航结束
     */
    @Override
    public void onArriveDestination() {
        sfSpeechSyntesizer.startSpeaking("到达目的地,本次导航结束");
        finish();
    }

    private void startCarNavigation() {
        if(!isGpsEnabled()) {
            Toast.makeText(getApplicationContext(),"GPS没有打开，请先打开GPS" ,Toast.LENGTH_LONG).show();
            return;
        }
        // 驾车算路
        mNavi.calculateDriveRoute(
                startPoints,//指定的导航起点。支持多个起点，起点列表的尾点为实际导航起点，其他坐标点为辅助信息，带有方向性，可有效避免算路到马路的另一侧；
                endPoints,//指定的导航终点。支持一个终点。
                new ArrayList<NaviLatLng>(), //途经点，同时支持最多16个途经点的路径规划；
                planMode, //驾车路径规划的计算策略
                false //是否为本地算路,true 本地算路,false 网络算路
        );
    }

    private void startTruckNavigation() {
        if(null == mTruckInfo){
            Toast.makeText(getApplicationContext(),"请先设置货车信息" ,Toast.LENGTH_LONG).show();
            return;
        }
        if(!isGpsEnabled()) {
            Toast.makeText(getApplicationContext(),"GPS没有打开，请先打开GPS" ,Toast.LENGTH_LONG).show();
            return;
        }
        String carVehicle = mTruckInfo.getTruckType(); //1:小车 4:拖挂车 5:微型货车 6:轻型货车 7:中型货车 8:中型货车 9:危险品运输车
        String carWeight = mTruckInfo.getWeight();
        String carAxleNumber = mTruckInfo.getAxleNum();
        String carHeight = mTruckInfo.getHeight();
        String carPlate = mTruckInfo.getPlate();

        mNavi.setPlate(carPlate);
        mNavi.setVehicle(Integer.parseInt(carVehicle));
        mNavi.setWeight(Double.parseDouble(carWeight));
        mNavi.setHeight(Double.parseDouble(carHeight));
        mNavi.setAxleNumber(Integer.parseInt(carAxleNumber.substring(0, 1)));
        mNavi.calculateDriveRoute(startPoints, endPoints, new ArrayList<NaviLatLng>(), planMode, false);
    }

    private boolean isGpsEnabled() {
        return mLocationManager == null || mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 界面右下角功能设置按钮的回调接口。
     */
    @Override
    public void onNaviSetting() {
        Toast.makeText(getApplicationContext(),"在这里弹出自定义设置界面",Toast.LENGTH_LONG).show();
    }

    /**
     * 导航页面左下角返回按钮点击后弹出的 "退出导航对话框" 中选择 "确定" 后的回调接口。
     */
    @Override
    public void onNaviCancel() {
        mNavi.stopNavi();
        mNavi.destroy();
        mNaviView.onDestroy();
        sfSpeechSyntesizer.destroy();
        finish();
    }
    /**
     * 导航创建成功时的回调函数。
     */
    @Override
    public void onInitNaviSuccess() {
        switch (routeType){
            case 1:
                startCarNavigation();
                break;
            case 3:
                startTruckNavigation();
                break;
            default:
                break;
        }
    }

    /**
     * 驾车路径规划成功后的回调函数。
     */
    @Override
    public void onCalculateRouteSuccess() {
//        mNavi.startNavi(Navi.GPSNaviMode);
        mNavi.startNavi(Navi.EmulatorNaviMode);
    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] routeIds) {
        mNavi.startNavi(Navi.EmulatorNaviMode);
    }

    SfMapLocationClient mSfMapLocationClient;
    private void initSfLocationClient() {
        //初始定位客户端
        mSfMapLocationClient = new SfMapLocationClient(this);

        //设置监听回调
        mSfMapLocationClient.setLocationListener(this);

        //初始化定位参数
        SfMapLocationClientOption locationOption = new SfMapLocationClientOption();
        //设置定位间隔 或者设置单词定位


        locationOption.setInterval(5 * 1000);

        locationOption.setLocationMode(SfMapLocationClientOption.SfMapLocationMode.High_Accuracy);
        //mSfMapLocationClientOption.setOnceLocation(true);

        locationOption.setNeedAddress(true);
        locationOption.setOnceLocation(true);
        //设置参数
        mSfMapLocationClient.setLocationOption(locationOption);
        mSfMapLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(SfMapLocation location) {
        location.setSpeed(50);
        mNavi.setGPSInfo(1,location);
        Log.d(TAG, "onLocationChanged: " + " Latitude:" + location.getLatitude() + " Longitude:" + location.getLongitude());
    }
}