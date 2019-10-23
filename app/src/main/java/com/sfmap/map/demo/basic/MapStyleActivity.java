package com.sfmap.map.demo.basic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.map.demo.R;
import com.sfmap.map.demo.util.StyleHttpUtil;
import com.sfmap.map.demo.util.StyleInfo;
import com.sfmap.map.demo.util.ToastUtil;

import java.io.File;
import java.util.List;

/**
 * 立得地图中介绍如何设置自定义地图的样式
 */
public class MapStyleActivity extends Activity implements View.OnClickListener{
    private MapView mapView;
    private MapController lMap;
    private StyleHttpUtil styleUtil;
    private ProgressDialog progDialog = null;// 进度框
    private String sytleUrl = "https://cloudmap.ishowchina.com/gms/map/publishList.json";
    String sdPaht = Environment.getExternalStorageDirectory().getPath();
    String basePath = sdPaht + "/sfmap/stylefile";
    private List<StyleInfo> infos;
    private View frameView;
    private ViewPager viewPager;
    private ImageView pageup;
    private ImageView pagedown;
    private int viewPagerIndex;
    private static int selected=-1;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    showProgressDialog();
                    break;
                case 1:
                    dissmissProgressDialog();
                    setViewPagerAdapter();
                    break;
                case 2:
                    ToastUtil.show(MapStyleActivity.this,(String)msg.obj);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapstyle);
        mapView = (MapView) findViewById(R.id.map);
        ImageButton button = (ImageButton) findViewById(R.id.button);
        Button close = (Button) findViewById(R.id.close);
        frameView = findViewById(R.id.framelayout);
        pageup = (ImageView) findViewById(R.id.pageup);
        pagedown = (ImageView) findViewById(R.id.pagedown);
        frameView.setVisibility(View.GONE);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        close.setOnClickListener(this);
        pageup.setOnClickListener(this);
        pagedown.setOnClickListener(this);
        frameView.setOnClickListener(this);
        button.setOnClickListener(this);

        init();
        loadData();

    }

    public void setViewPagerAdapter() {
        ViewPagerAdapter myAdapter=new ViewPagerAdapter(infos);

        viewPager.setAdapter(myAdapter);
        final int count=myAdapter.getCount();
        if(count>1)pagedown.setVisibility(View.VISIBLE);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPagerIndex=position;
                if(position==0){
                    pageup.setVisibility(View.INVISIBLE);
                }else
                    pageup.setVisibility(View.VISIBLE);
                if(position==count-1){
                    pagedown.setVisibility(View.INVISIBLE);
                }else
                    pagedown.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化对象
     */
    private void init() {
        if (lMap == null) {
            try {
                lMap = mapView.getMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (styleUtil == null)
            styleUtil = new StyleHttpUtil();
    }

    /**
     * 加载样式文件
     */
    private void loadData() {
        new Thread() {
            @Override
            public void run() {
                handler.sendEmptyMessageDelayed(0,0);
                //检查是否有新样式
              List<StyleInfo>  styles = styleUtil.getDataForHttp(sytleUrl, MapStyleActivity.this);
                if (styles != null&&styles.size()>0) {
                    File file = new File(basePath);
                    if (!file.exists())
                        file.mkdirs();
                    for (StyleInfo styleInfo : styles) {
                        boolean down=styleUtil.downFile(styleInfo,MapStyleActivity.this);
                        if(!down){
                            Message message=new Message();
                            message.what=2;
                            message.obj=styleInfo.getName()+",下载失败";
                            handler.sendMessage(message);}
                    }
                }
                //读取样式
                infos = styleUtil.readStyleFile(basePath);
                handler.sendEmptyMessageDelayed(1,0);
            }
        }.start();
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
        progDialog.setMessage("下载自定义样式.....");
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
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        //关闭样式列表
        if(id== R.id.framelayout||id== R.id.close){
            frameView.setVisibility(View.GONE);
        }
        //打开样式列表
        if(id== R.id.button){
            if(infos==null||infos.size()<=0)
                    ToastUtil.show(MapStyleActivity.this,"无样式文件,请确认是否开启sd读写权限");
                else
                frameView.setVisibility(View.VISIBLE);

        }
        //左翻页
        if(id== R.id.pageup){
            viewPager.setCurrentItem(viewPagerIndex-1);
        }
        //右翻页
        if(id== R.id.pagedown){
            viewPager.setCurrentItem(viewPagerIndex+1);
        }
    }


    class ViewPagerAdapter extends PagerAdapter {
        int item1 = 0;
        int item2 = 0;
        int item3 = 0;
        int count = 0;
        RadioButton button1;
        RadioButton button2;
        RadioButton button3;
        private List<StyleInfo> infos;

        ViewPagerAdapter(List<StyleInfo> infos) {
            this.infos = infos;
            if (infos != null && infos.size() > 0)
                count = infos.size() / 3 + (infos.size() % 3 > 0 ? 1 : 0);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_viewpager, null);
             button1 =  (RadioButton) view.findViewById(R.id.button1);
             button2 = (RadioButton) view.findViewById(R.id.button2);
             button3 = (RadioButton) view.findViewById(R.id.button3);

            int index = position * 3;
            item1 = index + 0;
            if (item1 < infos.size()) {
                setItemViewData(button1,item1);
            }
            item2=index+1;
            if(item2<infos.size()) {
                setItemViewData(button2,item2);
            }
            item3=index+2;
            if(item3<infos.size()) {
                setItemViewData(button3,item3);
            }

            container.addView(view);
            return view;
        }
        private void setItemViewData(final RadioButton button, final int index){
            button.setVisibility(View.VISIBLE);
            Drawable image = Drawable.createFromPath(basePath + "/" + infos.get(index).getImageName());
            if(image==null)return;
            image.setBounds(0,3, 210, 150);
           if( index==selected)button.setBackgroundColor(Color.parseColor("#463eee"));
            else button.setBackgroundColor(Color.parseColor("#666666"));
            button.setCompoundDrawables(null, image, null, null);
            button.setText(infos.get(index).getName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected=index;
                    setStyle(index);
                    button.setBackgroundColor(Color.parseColor("#463eee"));
                }
            });
        }

        //设置样式文件到地图
        private void setStyle(int index){
            frameView.setVisibility(View.GONE);
            String name=basePath+"/"+infos.get(index).getName();
            lMap.setMapStyleFileByPath(name+"_style.data",name+"_style.data");//设置样式
            notifyDataSetChanged();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}
