package com.sfmap.map.demo.util;

import android.content.Context;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class StyleHttpUtil {
    String sdPaht = Environment.getExternalStorageDirectory().getPath();
    String basePath = sdPaht + "/sfmap/stylefile";

    public  List<StyleInfo> getDataForHttp(String urlString, Context context){
        String json= HttpUtil.getJson(HttpUtil.getInputStream(urlString,context));
        return parseResult(json);
}

    //解析json
    public List<StyleInfo> parseResult(String json){
        if(json==null||"".equals(json))return null;
        JSONObject ct = null;
        List<StyleInfo> list=null;
        int status=-1;
        String message="";
        try {
            ct = new JSONObject(json);
            if(ct==null)return list;
            if(ct.has("status"))
            status=ct.getInt("status");
            if(ct.has("message"))
                message=ct.optString("message");
            if(!ct.has("results"))return list;
            JSONObject  result= ct.getJSONObject("results");
            if(result==null)return list;
            boolean has=result.has("result");
            if(!result.has("result"))return list;
            JSONArray styles=result.getJSONArray("result");
                if(styles==null)return list;
            list=new ArrayList<>();
            for(int i=0;i<styles.length();i++){
                JSONObject style=styles.optJSONObject(i);
                if(style==null)continue;
                 StyleInfo info=new StyleInfo();
                if(style.has("image"))
                    info.setImageName(style.optString("image"));
                if(style.has("name"))
                    info.setName(style.optString("name"));
                if(style.has("centerX"))
                    info.setCenterX(style.optDouble("centerX"));
                if(style.has("centerY"))
                    info.setCenterX(style.optDouble("centerY"));
                if(style.has("binStyleUrl"))
                    info.setStyleUrl(style.optString("binStyleUrl"));
                if(style.has("imageUrl"))
                    info.setImageUrl(style.optString("imageUrl"));
                if(style.has("iconUrl"))
                    info.setIconUrl(style.optString("iconUrl"));
                list.add(info);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    public boolean downFile(StyleInfo styleInfo, Context context){
        String imagepath = basePath + "/" + styleInfo.getName()+".png";
        String iconPaht = basePath + "/" + styleInfo.getName() + "_icon.data";
        String stylePaht = basePath + "/" + styleInfo.getName() + "_style.data";
        boolean imageDown=false,iconDown=false,styleDown=false;
        if (!new File(imagepath).exists()){
            imageDown= HttpUtil.httpDownload(styleInfo.getImageUrl(), imagepath, context,null);}
        else imageDown=true;
        if (!new File(iconPaht).exists()){
            iconDown= HttpUtil.httpDownload(styleInfo.getIconUrl(), iconPaht, context,null);}
        else iconDown=true;
        if (!new File(stylePaht).exists()){
            styleDown= HttpUtil.httpDownload(styleInfo.getStyleUrl(), stylePaht, context,null);}
        else styleDown=true;
        return imageDown&&iconDown&&styleDown;
    }
    /**
     * 获取本地样式数据信息
     * @param basePath
     * @return
     */
    public List<StyleInfo> readStyleFile(String basePath) {
        File dir = new File(basePath);
        File[] styles = dir.listFiles();
        String imageName = null;
        String name = null;
        List<StyleInfo> list=new ArrayList<>();
        StyleInfo info=null;
        if(styles==null)return list;
        for (File file : styles){
            if (file.getName().contains(".png")) {
                imageName = file.getName();
                name=imageName.substring(0,imageName.lastIndexOf("."));
                info = new StyleInfo();
                info.setName(name);
                info.setImageName(imageName);
                list.add(info);
            }
        }
        return list;
    }

    }





