package com.sfmap.map.demo.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.map.demo.R;

public class InfoWinAdapter implements MapController.InfoWindowAdapter {
    private TextView tv1,tv2;
    private Context mContext;
    private LatLng latLng;
    private String agentName;
    private String snippet;
    private boolean flag;

    private InfoWinAdapter.PoiInfoListener poiInfoListener;

    public InfoWinAdapter(Context context) {
        mContext = context;
    }

    public interface PoiInfoListener {
        void addSelectMidPoi();

        void setEndPoi();

        void delMidPoi();
    }

    public void setPoiInfoListener(PoiInfoListener listener) {
        poiInfoListener = listener;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        initData(marker);
        View view = initView();
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void initData(Marker marker) {
        latLng = marker.getPosition();
        snippet = marker.getSnippet();
        agentName = marker.getTitle();
        flag = marker.isFlat();
    }

    private View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_personal_win, null);
        tv1 = view.findViewById(R.id.textView1);
        tv2 = view.findViewById(R.id.textView2);
//        mMenuView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        tv1.setText(agentName);
        tv2.setText(latLng.toString());
        return view;
    }
}