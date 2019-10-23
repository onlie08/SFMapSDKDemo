package com.sfmap.map.demo.view;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.sfmap.api.maps.CustomRenderer;
import com.sfmap.api.maps.ExtralDrawBitmap;
import com.sfmap.api.maps.ExtralDrawText;

import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.map.demo.util.Constants;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 点，text图层。
 */
public class MyCustomRender implements CustomRenderer{
    ExtralDrawBitmap drawBitmap = null;
    ExtralDrawText drawText = null;
    BitmapDescriptor bitmap = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.MARKER_COLOR_AZURE);
    int markerTextureID = 0;
    private FloatBuffer bitmapBuffer = null;
    Paint paint = new Paint();
    FloatBuffer textFloatBuffer = null;
    Bitmap textBitmap =null;
    int textTextureID = 0;
    private LatLng point = new LatLng(39.80403, 116.307525);
    MapView mapview = null;
    MapController map = null;

    public MyCustomRender(MapView mapView){
        try {
            this.mapview = mapView;
            map = mapView.getMap();
            drawBitmap = new ExtralDrawBitmap(mapView);
            drawText = new ExtralDrawText(mapView);
            bitmapBuffer = drawBitmap.makeBitmapFloatBuffer(this.bitmap);
            initTextEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 初始化画笔
     */
    private void initTextEvent(){
        this.paint.setTypeface(Typeface.DEFAULT);
        this.paint.setSubpixelText(true);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(5.0F);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setTextSize(40);
        this.paint.setTextAlign(Paint.Align.CENTER);
        this.paint.setColor(Color.RED);
        textFloatBuffer = drawText.getTextFloatBuffer();
        textBitmap = drawText.createTextBitmap("我在这里",paint,Color.BLUE);

    }


    @Override
    public void onMapReferencechanged() {
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        this.textTextureID = drawText.drawBitmap(gl,this.textBitmap,false,point,this.textTextureID,this.textFloatBuffer,180,0.5f,0.5f);
        this.markerTextureID = drawBitmap.drawBitmap(gl,bitmap,false, Constants.BEIJING,this.markerTextureID,this.bitmapBuffer,0,0.5f,1f);
    }
}
