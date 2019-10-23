package com.sfmap.map.demo.overlay;

import android.os.Environment;
import android.util.Log;


import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.model.TileOverlay;
import com.sfmap.api.maps.model.TileOverlayOptions;
import com.sfmap.api.maps.model.UrlTileProvider;

import java.io.File;
import java.net.URL;
import java.util.Locale;


public class TileOverlayXyz {
    private static final String TAG = "TileOverlayXyz";
    private static final String TITLE_SERVICE_PATH = "bldname/tile0";
    private static final String TITLE_SERVICE_PARAMS = "?x=%d&y=%d&z=%d&token=941b2105e90064a2183d3064a49e9a4b";
    private final MapController mapController;
    private final String tileServiceBaseUrl;
    private TileOverlay tileOverlay;

    public TileOverlayXyz(MapController mapController, String tileServiceBaseUrl) {
        this.mapController = mapController;
        this.tileServiceBaseUrl = tileServiceBaseUrl;
        loadOMCMap();
    }

    private void loadOMCMap() {

        if(tileOverlay != null) {
            tileOverlay.remove();
        }

        final String url = tileServiceBaseUrl +  TITLE_SERVICE_PATH + TITLE_SERVICE_PARAMS;
        TileOverlayOptions tileOverlayOptions =
                new TileOverlayOptions().tileProvider(new UrlTileProvider(256, 256) {
                    @Override
                    public URL getTileUrl(int x, int y, int zoom) {
                        try {
                            URL urlObject = new URL(String.format(Locale.US, url, x, y,zoom));
                            Log.v(TAG,"tile load url is -> \n" + urlObject.toString());
                            return urlObject;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.v(TAG, e.getMessage(), e);
                        }
                        return null;
                    }
                });
        tileOverlayOptions.diskCacheEnabled(true)
                .diskCacheDir(new File(Environment.getExternalStorageDirectory(), "sfmap/tileCache").getPath())
                .diskCacheSize(10 * 1000 * 1000)
                .memoryCacheEnabled(true)
                .memCacheSize(2 * 1000 * 1000)
                .zIndex(-100);
        tileOverlay = mapController.addTileOverlay(tileOverlayOptions);
    }

    public void onMapCameraChangeFinish() {
    }

    public void destroy() {
        if(tileOverlay != null) {
            tileOverlay.remove();
        }
    }
}
