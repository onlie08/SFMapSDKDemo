package com.sfmap.map.demo.cluster;

import com.sfmap.api.maps.cluster.ClusterItem;
import com.sfmap.api.maps.model.LatLng;

/**
 * Created by 01377555 on 2018/10/22.
 */
public class RegionItem implements ClusterItem {
    private LatLng mLatLng;
    private String mTitle;
    public RegionItem(LatLng latLng, String title) {
        mLatLng=latLng;
        mTitle=title;
    }

    @Override
    public LatLng getPosition() {
        // TODO Auto-generated method stub
        return mLatLng;
    }
    public String getTitle(){
        return mTitle;
    }

}
