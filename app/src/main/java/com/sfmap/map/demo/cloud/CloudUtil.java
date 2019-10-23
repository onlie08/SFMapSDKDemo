package com.sfmap.map.demo.cloud;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;

/**
 * 云存储和云检索的工具类
 */
public class CloudUtil {
    /**
     * 在地图上增加单点marker
     *
     * @param iconID
     * @param latLng
     */
    public static Marker addMarker(MapController lMap, Context mContext, int iconID, LatLng latLng) {
        if(lMap == null){
            return null;
        }
        Marker marker = null;
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng).setAddByAnimation(false);

        markerOption.draggable(true);
        Bitmap bitmap = BitmapFactory
                .decodeResource(mContext.getResources(), iconID);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        // 将Marker设置为贴地显示，可以双指下拉看效果
        markerOption.setFlat(false);
        marker = lMap.addMarker(markerOption);
        if (bitmap != null) {
            bitmap.recycle();
        }
        return marker;
    }

    /**
     * 清除地图上的mark点
     */
    public static void removeMapAllMark(MapController lMap){
        if(lMap == null){
            return;
        }
        lMap.clear();
    }
}
