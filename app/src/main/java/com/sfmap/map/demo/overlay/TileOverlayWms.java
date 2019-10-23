package com.sfmap.map.demo.overlay;

import android.os.Environment;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.model.TileOverlay;
import com.sfmap.api.maps.model.TileOverlayOptions;
import com.sfmap.api.maps.model.TileProvider;
import com.sfmap.api.maps.model.UrlTileProvider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

public class TileOverlayWms {
    private static final int TILE_IMAGE_SIZE = 1024;
    private final MapController mMapController;
    private final String mHostUrl;
    private final double ORIGIN_SHIFT = 20037508.342789244;//2*Math.PI*6378137/2.0;//

    private static final String PATH_BASE_COMMON =
            "?token=941b2105e90064a2183d3064a49e9a4b&" +
            "transparent=TRUE&" +
            "LAYERS=basic&" +
            "SERVICE=WMS&" +
            "VERSION=1.1.1&" +
            "REQUEST=GetMap&" +
            "STYLES=&" +
            "FORMAT=image%2Fpng&" +
            "SRS=EPSG%3A4326&";
    private static final String AOI_PATH_BASE = "api/geoserver/wms" + PATH_BASE_COMMON;
    private static final String BUILDING_PATH_BASE = "build/wms" + PATH_BASE_COMMON;
    private TileOverlay mAoiTitle;

    public TileOverlayWms(MapController map, String hostUrl) {
        mMapController = map;
        mHostUrl = hostUrl;
        initAoiMapLayer();
    }

    /**
     * 计算分辨率
     * @param zoom 缩放级别
     */
    private double Resolution(int zoom) {
        //2*Math.PI*6378137/titleSize;//
        double initialResolution = 156543.03392804062;
        return initialResolution / (Math.pow(2, zoom));
    }

    /**
     * X米转经纬度
     */
    private double Meters2Lon(double mx) {
        return (mx / ORIGIN_SHIFT) * 180.0;
    }

    /**
     * Y米转经纬度
     */
    private double Meters2Lat(double my) {
        double lat = (my / ORIGIN_SHIFT) * 180.0;
        lat = 180.0 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
        return lat;
    }

    /**
     * 根据瓦片的x/y等级返回瓦片范围
     * @param tx tx
     * @param ty tx
     * @param zoom zoom
     * @return string
     */
    private String calculateBoxBounds(int tx, int ty, int zoom) {
        int titleSize = TILE_IMAGE_SIZE;
        double minX = Pixels2Meters(tx * titleSize, zoom);
        double maxY = -Pixels2Meters(ty * titleSize, zoom);
        double maxX = Pixels2Meters((tx + 1) * titleSize, zoom);
        double minY = -Pixels2Meters((ty + 1) * titleSize, zoom);

        //转换成经纬度
        minX = Meters2Lon(minX);
        minY = Meters2Lat(minY);
        maxX = Meters2Lon(maxX);
        maxY = Meters2Lat(maxY);
        return minX + ","
                + minY + ","
                + maxX + ","
                + maxY +
                "&WIDTH=" + TILE_IMAGE_SIZE +
                "&HEIGHT=" + TILE_IMAGE_SIZE;
    }

    /**
     * 根据像素、等级算出坐标
     *
     * @param p pixels
     * @param zoom zoom level
     * @return coordinate
     */
    private double Pixels2Meters(int p, int zoom) {
        return p * Resolution(zoom) - ORIGIN_SHIFT;
    }

    private String getAoiBaseUrl() {
        return mHostUrl + AOI_PATH_BASE + "BBOX=";
    }

    private void initAoiMapLayer() {
        TileProvider tileProvider = new UrlTileProvider(TILE_IMAGE_SIZE, TILE_IMAGE_SIZE) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                try {
                    String titleBounds = calculateBoxBounds(x, y, zoom);
                    String fullUrl = getAoiBaseUrl() + titleBounds;

                    Timber.v("Tile Url --> %s", fullUrl);
                    return new URL(fullUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };


        mAoiTitle = mMapController.addTileOverlay(
                new TileOverlayOptions()
                        .tileProvider(tileProvider)
                        .zIndex(5)
                        .diskCacheDir(new File(Environment.getExternalStorageDirectory(), "sfmap/tileCache").getPath())
                        .diskCacheEnabled(true)
                        .diskCacheSize(1000) //缓存大小KB
        );
    }

    public void destroy() {
        if(mAoiTitle != null) {
            mAoiTitle.remove();
        }
    }

}
