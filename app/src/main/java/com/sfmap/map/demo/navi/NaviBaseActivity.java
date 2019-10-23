package com.sfmap.map.demo.navi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.sfmap.api.navi.NaviListener;
import com.sfmap.api.navi.NaviViewListener;
import com.sfmap.api.navi.enums.PathPlanningErrCode;
import com.sfmap.api.navi.enums.VoiceType;
import com.sfmap.api.navi.model.NaviCross;
import com.sfmap.api.navi.model.NaviInfo;
import com.sfmap.api.navi.model.NaviLaneInfo;
import com.sfmap.api.navi.model.NaviLocation;
import com.sfmap.api.navi.model.NaviServiceFacilityInfo;
import com.sfmap.api.navi.model.NaviTrafficFacilityInfo;

public class NaviBaseActivity extends Activity implements NaviListener, NaviViewListener {
    private String TAG = "NaviActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 导航创建失败时的回调函数。
     */
    @Override
    public void onInitNaviFailure() {
        Toast.makeText(getApplicationContext(),"导航初始化失败了", Toast.LENGTH_LONG).show();
    }

    /**
     * 导航创建成功时的回调函数。
     */
    @Override
    public void onInitNaviSuccess() {
    }

    /**
     * 启动导航后回调函数。
     * @param type - 导航类型，1：实时导航，2：模拟导航
     */
    @Override
    public void onStartNavi(int type) {

    }

    /**
     * 当前方路况光柱信息有更新时回调函数。
     */
    @Override
    public void onTrafficStatusUpdate() {

    }


    /**
     * 当GPS位置有更新时的回调函数。
     * @param location - 当前位置的定位信息。
     */
    @Override
    public void onLocationChange(NaviLocation location) {

    }

    /**
     * 导航播报信息回调函数。
     * @param voiceType - 播报语音类型，包含如开始导航、结束导航、偏航等。参见 {@link VoiceType }。
     * @param text - 播报文字。
     */
    @Override
    public void onGetNavigationText(int voiceType, String text) {

    }

    /**
     * 模拟导航停止后回调函数。
     */
    @Override
    public void onEndEmulatorNavi() {

    }

    /**
     * 到达目的地后回调函数。
     */
    @Override
    public void onArriveDestination() {

    }

    /**
     * 驾车路径规划成功后的回调函数。
     */
    @Override
    public void onCalculateRouteSuccess() {
    }

    /**
     * 驾车路径规划失败后的回调函数。
     * @param errorInfo - 计算路径的错误码，参见 {@link PathPlanningErrCode }。
     */
    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        Toast.makeText(getApplicationContext(),"路径规划错误码：" + errorInfo, Toast.LENGTH_LONG).show();
    }

    /**
     * 驾车导航时,出现偏航后需要重新计算路径的回调函数。
     */
    @Override
    public void onReCalculateRouteForYaw() {

    }

    /**
     * 驾车导航时，如果前方遇到拥堵时需要重新计算路径的回调。
     */
    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    /**
     * 驾车路径导航到达某个途经点的回调函数。
     * @param wayID - 到达途径点的编号，标号从1开始，依次累加。到达终点时wayID取值为0。
     */
    @Override
    public void onArrivedWayPoint(int wayID) {

    }

    /**
     * 用户手机GPS设置是否开启的回调函数。
     * @param enabled - true,开启;false,未开启。
     */
    @Override
    public void onGpsOpenStatus(boolean enabled) {

    }

    /**
     * 导航引导信息回调 naviinfo 是导航信息类。
     * @param naviInfo - 导航信息对象。
     */
    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    /**
     * 摄像头信息更新回调。
     * @param trafficFacilityInfo - 摄像头信息
     */
    @Override
    public void onUpdateTrafficFacility(NaviTrafficFacilityInfo[] trafficFacilityInfo) {

    }

    /**
     * 显示路口放大图回调。
     * @param naviCross - 路口放大图类，可以获得此路口放大图bitmap
     */
    @Override
    public void showCross(NaviCross naviCross) {

    }

    /**
     * 关闭路口放大图回调。
     */
    @Override
    public void hideCross() {

    }

    /**
     * 显示车道线信息视图回调。
     * @param laneInfos - 车道线信息数组，可获得各条道路分别是什么类型，可用于用户使用自己的素材完全自定义显示。
     * @param laneBackgroundInfo - 车道线背景数据数组
     * @param laneRecommendedInfo - 车道线推荐数据数组
     */
    @Override
    public void showLaneInfo(NaviLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {

    }

    /**
     * 关闭车道线信息视图回调。
     */
    @Override
    public void hideLaneInfo() {

    }

    /**
     * 多路线算路成功回调。
     * @param routeIds - 路线id数组
     */
    @Override
    public void onCalculateMultipleRoutesSuccess(int[] routeIds) {

    }

    /**
     * 服务区、收费站信息更新通知回调。
     * @param serviceFacilityInfos 服务区和收费站信息数组
     */
    @Override
    public void updateServiceFacility(NaviServiceFacilityInfo[] serviceFacilityInfos) {

    }
    //Navi listener end;

    //NaviView listener start;

    /**
     * 界面右下角功能设置按钮的回调接口。
     */
    @Override
    public void onNaviSetting() {
        Toast.makeText(getApplicationContext(),"设置回调", Toast.LENGTH_LONG).show();
    }

    /**
     * 导航页面左下角返回按钮点击后弹出的 "退出导航对话框" 中选择 "确定" 后的回调接口。
     */
    @Override
    public void onNaviCancel() {

    }

    /**
     * 导航页面左下角返回按钮的回调接口
     * @return false-由SDK主动弹出"退出导航"对话框，true-SDK不主动弹出"退出导航对话框"，由用户自定义
     */
    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    /**
     * 导航界面地图状态的回调。
     * @param isLock - 地图状态，0:车头朝上状态；1:非锁车状态,即车标可以任意显示在地图区域内。
     */
    @Override
    public void onNaviMapMode(int isLock) {

    }

    /**
     * 界面左上角转向操作的点击回调。
     */
    @Override
    public void onNaviTurnClick() {

    }

    /**
     * 界面下一道路名称的点击回调。
     */
    @Override
    public void onNextRoadClick() {

    }

    /**
     * 界面全览按钮的点击回调。
     */
    @Override
    public void onScanViewButtonClick() {

    }

    /**
     * 是否锁定地图的回调。
     * @param isLock true 代表锁定， false代表不锁定
     */
    @Override
    public void onLockMap(boolean isLock) {

    }

    /**
     * 开启避免拥堵后,拥堵时的回调
     * @return false -由SDK主动弹出"切换路径"对话框,true-SDK不主动弹出"切换路径"对话框,由用户自定义
     */
    @Override
    public boolean onReRouteForTraffic() {
        return false;
    }

}