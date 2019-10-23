package com.sfmap.map.demo;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sfmap.api.maps.MapsInitializer;
import com.sfmap.map.demo.basic.BaseMapFragmentActivity;
import com.sfmap.map.demo.basic.BasicMapActivity;
import com.sfmap.map.demo.basic.CameraActivity;
import com.sfmap.map.demo.basic.CirclePointsActivity;
import com.sfmap.map.demo.basic.EventsActivity;
import com.sfmap.map.demo.basic.MapOptionActivity;
import com.sfmap.map.demo.basic.OpenglActivity;
import com.sfmap.map.demo.basic.PoiClickActivity;
import com.sfmap.map.demo.basic.ScreenShotActivity;
import com.sfmap.map.demo.basic.UiSettingsActivity;
import com.sfmap.map.demo.geocoder.GeocoderActivity;
import com.sfmap.map.demo.geojson.GeojsonSearchActivity;
import com.sfmap.map.demo.location.SfLocationModeSourceActivity;
import com.sfmap.map.demo.navi.NaviActivity;
import com.sfmap.map.demo.offlinemap.BuiltinOfflineMapActivity;
import com.sfmap.map.demo.offlinemap.OfflineChild;
import com.sfmap.map.demo.offlinemap.OfflineMapActivity;
import com.sfmap.map.demo.overlay.ArcActivity;
import com.sfmap.map.demo.overlay.CircleActivity;
import com.sfmap.map.demo.overlay.ClusterActivity;
import com.sfmap.map.demo.overlay.DrawLineActivity;
import com.sfmap.map.demo.overlay.GroundOverlayActivity;
import com.sfmap.map.demo.overlay.MarkerActivity;
import com.sfmap.map.demo.overlay.NavigateArrowOverlayActivity;
import com.sfmap.map.demo.overlay.PolygonActivity;
import com.sfmap.map.demo.overlay.PolylineActivity;
import com.sfmap.map.demo.overlay.TileProviderActivity;
import com.sfmap.map.demo.poisearch.PoiAroundSearchActivity;
import com.sfmap.map.demo.poisearch.PoiKeywordSearchActivity;
import com.sfmap.map.demo.view.FeatureView;


/**
 * sfmapMap地图demo总汇
 */
public final class MainActivity extends ListActivity {
	private static String[] PERMISSIONS_REQUEST = {
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION
	};
	private static class DemoDetails {
		private final int titleId;
		private final int descriptionId;
		private final Class<? extends android.app.Activity> activityClass;

		public DemoDetails(int titleId, int descriptionId,
				Class<? extends android.app.Activity> activityClass) {
			super();
			this.titleId = titleId;
			this.descriptionId = descriptionId;
			this.activityClass = activityClass;
		}
	}

	private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {
		public CustomArrayAdapter(Context context, DemoDetails[] demos) {
			super(context, R.layout.feature, R.id.title, demos);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FeatureView featureView;
			if (convertView instanceof FeatureView) {
				featureView = (FeatureView) convertView;
			} else {
				featureView = new FeatureView(getContext());
			}
			DemoDetails demo = getItem(position);
			featureView.setTitleId(demo.titleId);
			featureView.setDescriptionId(demo.descriptionId);
			return featureView;
		}
	}

	private static final DemoDetails[] demos = {
			new DemoDetails(R.string.list_item_title_basic_map, R.string.list_item_description_basic_map,
					BasicMapActivity.class),
			new DemoDetails(R.string.list_item_title_base_fragment_map,
					R.string.list_item_description_base_fragment_map,
					BaseMapFragmentActivity.class),
			new DemoDetails(R.string.list_item_title_camera_demo, R.string.list_item_description_camera,
					CameraActivity.class),
			new DemoDetails(R.string.list_item_title_events, R.string.list_item_description_events,
					EventsActivity.class),
			new DemoDetails(R.string.list_item_title_poiclick,
					R.string.list_item_description_poiclick, PoiClickActivity.class),
			new DemoDetails(R.string.list_item_title_mapOption,
					R.string.list_item_description_mapOption, MapOptionActivity.class),
			new DemoDetails(R.string.list_item_title_screenshot,
					R.string.list_item_description_screenshot, ScreenShotActivity.class),
			new DemoDetails(R.string.list_item_title_opengl, R.string.list_item_description_opengl,
					OpenglActivity.class),
			new DemoDetails(R.string.list_item_title_uisettings,
					R.string.list_item_description_uisettings, UiSettingsActivity.class),
			new DemoDetails(R.string.list_item_title_geojson_demo,
					R.string.list_item_description_geojson, GeojsonSearchActivity.class),
            new DemoDetails(R.string.list_item_title_cluster_demo,
                    R.string.list_item_description_cluster, ClusterActivity.class),
			new DemoDetails(R.string.list_item_title_polyline,
					R.string.list_item_description_polyline, PolylineActivity.class),
			new DemoDetails(R.string.list_item_title_polygon,
					R.string.list_item_description_polygon, PolygonActivity.class),
			new DemoDetails(R.string.list_item_title_circle, R.string.list_item_description_circle,
					CircleActivity.class),
			new DemoDetails(R.string.list_item_title_marker, R.string.list_item_description_marker,
					MarkerActivity.class),
			new DemoDetails(R.string.list_item_title_arc, R.string.list_item_description_arc,
					ArcActivity.class),
			new DemoDetails(R.string.list_item_title_groundoverlay,
					R.string.list_item_description_groundoverlay,
					GroundOverlayActivity.class),
			new DemoDetails(R.string.list_item_title_navigatearrow,
					R.string.list_item_description_navigatearrow,
					NavigateArrowOverlayActivity.class),
			new DemoDetails(R.string.list_item_title_geocoder,
					R.string.list_item_description_geocoder, GeocoderActivity.class),
//			new DemoDetails(R.string.list_item_title_location_mode_source,
//					R.string.list_item_description_location_mode_source,
//					AmapLocationModeSourceActivity.class),
			new DemoDetails(R.string.list_item_title_sf_location_mode_source,
					R.string.list_item_description_location_mode_source,
					SfLocationModeSourceActivity.class),
			new DemoDetails(R.string.list_item_title_poikeywordsearch,
					R.string.list_item_description_poikeywordsearch,
					PoiKeywordSearchActivity.class),
			new DemoDetails(R.string.list_item_title_poiaroundsearch,
					R.string.list_item_description_poiaroundsearch,
					PoiAroundSearchActivity.class),
			new DemoDetails(R.string.overlay_draw_line_demo_title,
					R.string.overlay_draw_line_demo_description,
					DrawLineActivity.class),
			new DemoDetails(R.string.basic_navi_demo_title,
					R.string.basic_navi_demo_description,
					NaviActivity.class),
			new DemoDetails(R.string.draw_circle_to_group_points_title,
					R.string.draw_circle_to_group_points_description,
					CirclePointsActivity.class),
			new DemoDetails(R.string.tile_provider_demo_title,
					R.string.tile_provider_demo_description,
					TileProviderActivity.class),

			new DemoDetails(R.string.builtin_map_data,
					R.string.builtin_map_data_description,
					BuiltinOfflineMapActivity.class),

//			new DemoDetails(R.string.list_item_title_localpoisearch_name,
//			         R.string.list_item_description_localpoisearch_name,
//					LocalPoiSearchActivity.class),
//			new DemoDetails(R.string.list_item_title_localpoisearch_letter,
//					R.string.list_item_description_localpoisearch_letter,
//					LocalPoiLetterSearchActivity.class),
//			new DemoDetails(R.string.list_item_title_busline,
//					R.string.list_item_description_busline, BuslineActivity.class),
//			new DemoDetails(R.string.list_item_title_route, R.string.list_item_description_route,
//					RouteActivity.class),
//			new DemoDetails(R.string.list_item_title_offlinemap,
//					R.string.list_item_description_offlinemap, OfflineMapActivity.class),
//			new DemoDetails(R.string.list_item_title_district_boundary,
//					R.string.list_item_description_district_boundary,
//					DistrictWithBoundaryActivity.class),
//			new DemoDetails(R.string.list_item_title_map_style,
//					R.string.list_item_description_map_style,
//					MapStyleActivity.class),
//			new DemoDetails(R.string.list_item_title_map_cloudsave,
//					R.string.list_item_description_map_cloudsave,
//					CloudSaveActivity.class),
//			new DemoDetails(R.string.list_item_title_map_cloudsearch,
//					R.string.list_item_description_map_cloudsearch,
//					CloudSearchListResultActivity.class),
//			new DemoDetails(R.string.list_item_title_layers, R.string.list_item_description_layers,
//					LayersActivity.class),
// 			new DemoDetails(R.string.list_item_title_heatmap_demo,
//					R.string.list_item_description_heatmap, HeatMapActivity.class),
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		requestPermission();
		setTitle("地图Demo" + MapsInitializer.getVersion());
		ListAdapter adapter = new CustomArrayAdapter(
				this.getApplicationContext(), demos);
		setListAdapter(adapter);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		System.exit(0);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		DemoDetails demo = (DemoDetails) getListAdapter().getItem(position);
		startActivity(new Intent(this.getApplicationContext(),
				demo.activityClass));
	}

	private void requestPermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			if (this.checkPermission(Manifest.permission.READ_PHONE_STATE, Process.myPid(), Process.myUid())
					!= PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Process.myPid(), Process.myUid())
					!= PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Process.myPid(), Process.myUid())
					!= PackageManager.PERMISSION_GRANTED) {
				this.requestPermissions(PERMISSIONS_REQUEST, 1);
			}
		}
	}
}
