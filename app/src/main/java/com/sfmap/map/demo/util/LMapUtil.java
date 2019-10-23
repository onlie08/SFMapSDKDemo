/**
 * 
 */
package com.sfmap.map.demo.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.Html;
import android.text.Spanned;
import android.widget.EditText;

import com.sfmap.api.maps.MapUtils;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.services.core.LatLonPoint;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LMapUtil {
	public static final String Kilometer = "\u516c\u91cc";// "公里";
	public static final String Meter = "\u7c73";// "米";
	/**
	 * 判断edittext是否null
	 */
	public static String checkEditText(EditText editText) {
		if (editText != null && editText.getText() != null
				&& !(editText.getText().toString().trim().equals(""))) {
			return editText.getText().toString().trim();
		} else {
			return "";
		}
	}

	public static Spanned stringToSpan(String src) {
		return src == null ? null : Html.fromHtml(src.replace("\n", "<br />"));
	}

	public static String colorFont(String src, String color) {
		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<font color=").append(color).append(">").append(src)
				.append("</font>");
		return strBuf.toString();
	}

	public static String makeHtmlNewLine() {
		return "<br />";
	}

	public static String makeHtmlSpace(int number) {
		final String space = "&nbsp;";
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < number; i++) {
			result.append(space);
		}
		return result.toString();
	}

	public static String getFriendlyLength(int lenMeter) {
		if (lenMeter > 10000) // 10 km
		{
			int dis = lenMeter / 1000;
			return dis + Kilometer;
		}

		if (lenMeter > 1000) {
			float dis = (float) lenMeter / 1000;
			DecimalFormat fnum = new DecimalFormat("##0.0");
			String dstr = fnum.format(dis);
			return dstr + Kilometer;
		}

		if (lenMeter > 100) {
			int dis = lenMeter / 50 * 50;
			return dis + Meter;
		}

		int dis = lenMeter / 10 * 10;
		if (dis == 0) {
			dis = 10;
		}

		return dis + Meter;
	}

	public static boolean IsEmptyOrNullString(String s) {
		return (s == null) || (s.trim().length() == 0);
	}

	/**
	 * 把LatLng对象转化为LatLonPoint对象
	 */
	public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
		return new LatLonPoint(latlon.latitude, latlon.longitude);
	}

	/**
	 * 把LatLonPoint对象转化为LatLon对象
	 */
	public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
		return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
	}

	/**
	 * 把集合体的LatLonPoint转化为集合体的LatLng
	 */
	public static ArrayList<LatLng> convertArrList(List<LatLonPoint> shapes) {
		ArrayList<LatLng> lineShapes = new ArrayList<LatLng>();
		for (LatLonPoint point : shapes) {
			LatLng latLngTemp = LMapUtil.convertToLatLng(point);
			lineShapes.add(latLngTemp);
		}
		return lineShapes;
	}

	/**
	 * long类型时间格式化
	 */
	public static String convertToTime(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		return df.format(date);
	}

	public static final String HtmlBlack = "#000000";
	public static final String HtmlGray = "#808080";

//	@Deprecated
//	public static String getDataRootPath(){
//		java.io.File fExternalStorageDirectory = Environment
//				.getExternalStorageDirectory();
//		return fExternalStorageDirectory.getAbsolutePath()+"/sfmap";
//	}

	public static String getDataRootPath(Context context){
//		return MapUtils.getExternalStroragePath(context) + File.separator+"sfmap";
		return null;
	}

	public static String getApiKey(Context context) throws PackageManager.NameNotFoundException {
		String key = "";
		if ((key == null) || (key.equals("")))
		{
			ApplicationInfo localApplicationInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (localApplicationInfo == null||localApplicationInfo.metaData==null) {
				return key;
			}
			key = localApplicationInfo.metaData.getString("com.sfmap.apikey");
		}
		return key;
	}

}
