package com.sfmap.map.demo.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.sfmap.api.maps.MapUtils;

import java.io.File;


public class OffLineMapUtils {
	/**
	 * 获取map 缓存和读取目录
	 */
	public static  String getSdCacheDir(Context context) {
//		if (Environment.getExternalStorageState().equals(
//				Environment.MEDIA_MOUNTED)) {
//			//			java.io.File fExternalStorageDirectory = Environment
////					.getExternalStorageDirectory();
////			File fExternalStorageDirectory = new File(MapUtils.getExternalStroragePath(context));
//			java.io.File dir = new java.io.File(
//					fExternalStorageDirectory, "sfmapmap");
//			boolean result = false;
//			if (!dir.exists()) {
//				result = dir.mkdir();
//			}
//			java.io.File sfmapmap = new java.io.File(dir,
//					"offlineMap");
//			if (!sfmapmap.exists()) {
//				result = sfmapmap.mkdir();
//			}
//			Log.e("offline",sfmapmap.toString() + "/");
//			return sfmapmap.toString() + "/";
//		} else {
			return "";
//		}
	}
}
