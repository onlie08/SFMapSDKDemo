package com.sfmap.map.demo.cloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.services.cloud.CloudDatasetItem;
import com.sfmap.api.services.cloud.CloudItem;
import com.sfmap.api.services.cloud.CloudStorage;
import com.sfmap.api.services.cloud.DBFieldInfo;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 云存储页面的fragment
 */
public class CloudSaveFragment extends Fragment implements View.OnClickListener {
    //放置地图容器的acitivity
    private CloudSaveActivity cloudSaveActivity;
    //创建点数据
    private Button btn_click;
    //顶部布局
    private View topView;
    //布局
    private View view;
    //名称 和 地址
    private EditText ed_name,ed_address;
    //经纬度
    private TextView e_lon,e_lat;
    //关闭按钮
    private ImageView img_close;
    private RelativeLayout ll_close;
    //上传按钮
    private Button btn_updown;
    //提示框
    private ProgressDialog progDialog = null;
    //云存储对象
    private CloudStorage cloudStorage;
    //数据集id
    private long datasetId = 264;

    public CloudSaveFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.cloudSaveActivity = (CloudSaveActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cloud_save,
                container, false);
        initView();
        return view;
    }

    /**
     * 初始化地图
     */
    public void initView(){
        //创建按钮
        btn_click = (Button) view.findViewById(R.id.btn_click);
        topView = view.findViewById(R.id.l_topInfo);

        ed_name = (EditText) view.findViewById(R.id.ed_name);
        ed_address = (EditText) view.findViewById(R.id.ed_address);
        e_lon = (TextView) view.findViewById(R.id.e_lon);
        e_lat = (TextView) view.findViewById(R.id.e_lat);
        img_close = (ImageView) view.findViewById(R.id.img_close);
        btn_updown = (Button) view.findViewById(R.id.btn_updown);
        ll_close = (RelativeLayout) view.findViewById(R.id.ll_close);
        //不显示
        topView.setVisibility(View.INVISIBLE);

        btn_click.setOnClickListener(this);
        img_close.setOnClickListener(this);
        btn_updown.setOnClickListener(this);
        ll_close.setOnClickListener(this);

        setUpdownBtn();
        ed_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setUpdownBtn();
            }
        });

        ed_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setUpdownBtn();
            }
        });

        cloudStorage = new CloudStorage(getActivity());
//        cloudStorage.setClounStorageListener(this);

        //先申请数据集
        addset();
        // 测试删除数据的接口
//        delData();
//        delSet();
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(getActivity());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在上传,请稍等！");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_click:
                ToastUtil.show(getActivity(),"长按地图选点");
                cloudSaveActivity.longPress = true;
                //清理地图上的点
                cloudSaveActivity.lMap.clear();
                break;
            case R.id.ll_close:
                clickClose(true);
                break;
            case R.id.btn_updown:
                boolean check = checkInfo();
                if(check){
//                    add();
                    updateData();
                }
                break;
        }
    }

    /**
     * 点击关闭事件
     */
    public void clickClose(boolean clearMark){
        cloudSaveActivity.longPress = false;
        showEdit(clearMark);
    }

    /**
     * 检查信息
     */
    public boolean checkInfo(){
        String name = ed_name.getText().toString();
        if(TextUtils.isEmpty(name)){
            ToastUtil.show(getActivity(),"请输入名称");
            return false;
        }
        String adress = ed_address.getText().toString();
        if(TextUtils.isEmpty(adress)){
            ToastUtil.show(getActivity(),"请输入地址");
            return false;
        }

        String lon = e_lon.getText().toString();
        String lat = e_lat.getText().toString();
        if(TextUtils.isEmpty(lon) || TextUtils.isEmpty(lat)){
            ToastUtil.show(getActivity(),"经纬度不能为空");
            return false;
        }
        return true;
    }

    /**
     * 创建点数据
     */
    private void add(){
        if(datasetId == -1){
            ToastUtil.show(getActivity(),"无法获取数据集，数据点创建失败。");
            return;
        }

        showProgressDialog();
        String name = ed_name.getText().toString();
        String adress = ed_address.getText().toString();
        String lon = e_lon.getText().toString();
        String lat = e_lat.getText().toString();

        HashMap<String,Object> extras=new HashMap<>();
        extras.put("name",name);
        extras.put("address",adress);
        CloudItem item=new CloudItem(Double.parseDouble(lon),Double.parseDouble(lat),extras);
        CloudItem[] items={item};
//        cloudStorage.addAysn(datasetId,items);
    }

    // 更新数据
    private void updateData(){
        if(datasetId == -1){
            ToastUtil.show(getActivity(),"无法更新数据，数据集id是"+datasetId);
            return;
        }

        showProgressDialog();
        String name = ed_name.getText().toString();
        String adress = ed_address.getText().toString();
        String lon = e_lon.getText().toString();
        String lat = e_lat.getText().toString();

        HashMap<String,Object> extras=new HashMap<>();
        extras.put("name",name);
        extras.put("address",adress);
        CloudItem item=new CloudItem(Double.parseDouble(lon),Double.parseDouble(lat),extras);
        CloudItem[] items={item};
        long[] dataIds = {12};
//        cloudStorage.updataAysn(datasetId, dataIds, items);
    }

    // 删除数据
    private void delData(){
        if(datasetId == -1){
            ToastUtil.show(getActivity(), "无法删除数据，数据集id是" + datasetId);
            return;
        }

        long[] dataIds = {12};
//        cloudStorage.delAysn(datasetId, dataIds);
    }

    /**
     * 创建数据集
     */
    private void addset(){
        List<DBFieldInfo> infos=new ArrayList<>();
        DBFieldInfo nameInfo=new DBFieldInfo("name","名称", DBFieldInfo.FieldType.type_varchar,0);
        DBFieldInfo adressInfo=new DBFieldInfo("address","地址", DBFieldInfo.FieldType.type_varchar,0);
        infos.add(nameInfo);
        infos.add(adressInfo);
        CloudDatasetItem item=new CloudDatasetItem("demoTest",1,infos);
        cloudStorage.addSetAysn(item);
    }

    // 删除数据集
    private void delSet(){
        if(datasetId == -1){
            ToastUtil.show(getActivity(), "无法删除数据集，数据集id是" + datasetId);
            return;
        }

//        cloudStorage.delSetAysn(datasetId);
    }

    /**
     * 获取顶部控件屏幕y方向坐标
     */
    public int screentLocation(){
        //底部控件坐标
        int[] location = new int[2];
        topView.getLocationOnScreen(location);
        //title控件坐标
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        return location[1] - viewLocation[1] + topView.getHeight();
    }

    /**
     * 长按地图显示
     */
    public void longPressMapShow(LatLng latLng){
        if(latLng == null){
            return;
        }
        topView.setVisibility(View.VISIBLE);
        e_lon.setText(String.valueOf(latLng.longitude));
        e_lat.setText(String.valueOf(latLng.latitude));
    }

    /**
     * 隐藏编辑框
     */
    public void hideEdit(){
        btn_click.setVisibility(View.GONE);
    }

    /**
     * 显示编辑框
     */
    public void showEdit(boolean clearMark){
        btn_click.setVisibility(View.VISIBLE);
        topView.setVisibility(View.INVISIBLE);
        ed_name.setText("");
        ed_address.setText("");
        setUpdownBtn();
        if(clearMark)
        CloudUtil.removeMapAllMark(cloudSaveActivity.lMap);
    }

    /**
     * 检查内容
     */
    public boolean checkEditContent(){
        if(null == ed_name || ed_name.getText().toString().equals("")){
            return false;
        }

        if(null == ed_address || ed_address.getText().toString().equals("")){
            return false;
        }
        return true;
    }

    /**
     * 设置上传按钮状态
     */
    public void setUpdownBtn(){
        boolean updowning = checkEditContent();
        if(updowning){
            btn_updown.setClickable(true);
            btn_updown.setTextColor(Color.BLACK);
        }else{
            btn_updown.setClickable(false);
            btn_updown.setTextColor(Color.GRAY);
        }
    }
//
//    @Override
//    public void onAdd(long[] dataId, int errorCode) {
//        if(errorCode == 0){
//            ToastUtil.show(getActivity(),"创建点数据成功");
//        }else{
//            ToastUtil.show(getActivity(),"创建点数据失败，错误码是"+errorCode);
//        }
//        clickClose(false);
//        dissmissProgressDialog();
//    }
//
//    @Override
//    public void onAddSet(long dataSetId, int errorCode) {
//        this.datasetId = dataSetId;
//    }
//
//    @Override
//    public void onUpdata(long[] dataId, int errorCode) {
//        if(errorCode == 0){
//            ToastUtil.show(getActivity(),"更新点数据成功");
//        }else{
//            ToastUtil.show(getActivity(),"更新点数据失败，错误码是"+errorCode);
//        }
//        clickClose(false);
//        dissmissProgressDialog();
//    }
//
//    @Override
//    public void onDel(long[] dataId, int errorCode) {
//        if(errorCode == 0){
//            ToastUtil.show(getActivity(),"删除点数据成功");
//        }else{
//            ToastUtil.show(getActivity(),"删除点数据失败，错误码是"+errorCode);
//        }
//        clickClose(false);
//        dissmissProgressDialog();
//    }
//
//    @Override
//    public void onDelSet(int errorCode) {
//        if(errorCode == 0){
//            ToastUtil.show(getActivity(),"删除数据集成功");
//        }else{
//            ToastUtil.show(getActivity(),"删除数据集失败，错误码是"+errorCode);
//        }
//        clickClose(false);
//        dissmissProgressDialog();
//    }
}
