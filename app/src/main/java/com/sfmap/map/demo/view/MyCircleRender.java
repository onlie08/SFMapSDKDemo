package com.sfmap.map.demo.view;

import com.sfmap.api.maps.CustomRenderer;
import com.sfmap.api.maps.ExtralDrawCircle;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.map.demo.util.Constants;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 自绘制圆型
 */
public class MyCircleRender implements CustomRenderer{
    ExtralDrawCircle drawable = null;
    int fillColor = 0X5F0000FF;//填充颜色
    int strokeColor = 0xFF00FF00;//轮廓颜色
    int strokeWidth = 20;//轮廓宽度
    LatLng center = Constants.BEIJING;
    double radius = 10000;
    FloatBuffer pointBuffer = null;
    public MyCircleRender(MapView mapView){

        drawable = new ExtralDrawCircle(mapView);
        try{
            pointBuffer = drawable.calMapFPoint(center, radius);
        }catch (Exception ex){

        }
    }



    @Override
    public void onMapReferencechanged() {
        try{
            pointBuffer = drawable.calMapFPoint(center,radius);
        }catch (Exception ex){

        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        try{
            drawable.drawCircle(gl,pointBuffer,center,radius,fillColor,strokeColor,strokeWidth);
        }catch (Exception ex){

        }
    }
}
