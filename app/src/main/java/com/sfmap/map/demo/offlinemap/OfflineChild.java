package com.sfmap.map.demo.offlinemap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.sfmap.api.maps.offlinemap.OfflineMapCity;
import com.sfmap.api.maps.offlinemap.OfflineMapManager;
import com.sfmap.api.maps.offlinemap.OfflineMapStatus;
import com.sfmap.map.demo.R;

public class OfflineChild implements OnClickListener, OnLongClickListener {
	private Context mContext;

	private TextView mOffLineCityName;// 离线包名称

	private TextView mOffLineCitySize;// 离线包大小

	private ImageView mDownloadImage;// 下载相关Image

	private TextView mDownloadProgress;

	private OfflineMapManager mapManager;

	private OfflineMapCity mMapCity;// 离线下载城市

	Dialog dialog;// 长按弹出的对话框

	private boolean mIsDownloading = false;

	private boolean isProvince = false;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int completeCode = (Integer) msg.obj;
			switch (msg.what) {
			case OfflineMapStatus.LOADING:
				
				
				displyaLoadingStatus(completeCode);
				
				
				break;
			case OfflineMapStatus.PAUSE:
				displayPauseStatus(completeCode);
				break;
			case OfflineMapStatus.STOP:
				break;
			case OfflineMapStatus.SUCCESS:
				displaySuccessStatus();
				break;
			case OfflineMapStatus.UNZIP:
				displayUnZIPStatus(completeCode);
				break;
			case OfflineMapStatus.ERROR:
				displayExceptionStatus("下载出现异常");
				break;
			case OfflineMapStatus.WAITING:
				displayWaitingStatus(completeCode);
				break;
			case OfflineMapStatus.CHECKUPDATES:
				displayDefault();
				break;
				case OfflineMapStatus.NEW_VERSION:
					displayNewVersionStatus();
                break;
			case OfflineMapStatus.EXCEPTION_NETWORK_LOADING:
			case OfflineMapStatus.EXCEPTION_SDCARD:
				displayExceptionStatus("下载出现异常");
				break;
			case OfflineMapStatus.EXCEPTION_MD5:
				displayExceptionStatus("MD5校验异常");
				break;
				
			}
		}

	};

	public boolean isProvince() {
		return isProvince;
	}

	public void setProvince(boolean isProvince) {
		this.isProvince = isProvince;
	}

	public OfflineChild(Context context, OfflineMapManager offlineMapManager) {
		mContext = context;
		initView();
		mapManager = offlineMapManager;
	}

	public String getCityName() {
		if (mMapCity != null) {
			return mMapCity.getCity();
		}
		return null;
	}

	public View getOffLineChildView() {
		return mOffLineChildView;
	}

	private View mOffLineChildView;

	private void initView() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mOffLineChildView = inflater.inflate(R.layout.offlinemap_child, null);
		mOffLineCityName = (TextView) mOffLineChildView.findViewById(R.id.name);
		mOffLineCitySize = (TextView) mOffLineChildView
				.findViewById(R.id.name_size);
		mDownloadImage = (ImageView) mOffLineChildView
				.findViewById(R.id.download_status_image);
		mDownloadProgress = (TextView) mOffLineChildView
				.findViewById(R.id.download_progress_status);

		mOffLineChildView.setOnClickListener(this);
		mOffLineChildView.setOnLongClickListener(this);

	}

	public void setOffLineCity(OfflineMapCity mapCity) {
		if (mapCity != null) {
			mMapCity = mapCity;
			mOffLineCityName.setText(mapCity.getCity());
			double size = ((int) (mapCity.getSize() / 1024.0 / 1024.0 * 100)) / 100.0;
			mOffLineCitySize.setText(String.valueOf(size) + " M");

			notifyViewDisplay(mMapCity.getState(), mMapCity.getcompleteCode(),
					mIsDownloading);
		}
	}

	/**
	 * 更新显示状态 在被点击和下载进度发生改变时会被调用
	 * 
	 * @param status
	 * @param completeCode
	 * @param isDownloading
	 */
	private void notifyViewDisplay(int status, int completeCode,
			boolean isDownloading) {
		if (mMapCity != null) {
			mMapCity.setState(status);
			mMapCity.setCompleteCode(completeCode);
		}
		Message msg = new Message();
		msg.what = status;
		msg.obj = completeCode;
		handler.sendMessage(msg);

	}

	/**
	 * 最原始的状态，未下载，显示下载按钮
	 */
	private void displayDefault() {
		mDownloadProgress.setVisibility(View.INVISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offline_arrow_download);
	}
	
	/**
	 * 显示有更新
	 */
	private void displayHasNewVersion() {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offline_arrow_download);
		mDownloadProgress.setText("已下载-有更新");
	}

	/**
	 * 等待中
	 * 
	 * @param completeCode
	 */
	private void displayWaitingStatus(int completeCode) {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offline_arrow_start);
		mDownloadProgress.setTextColor(Color.GREEN);
		mDownloadProgress.setText("等待中");
	}
	
	/**
	 * 下载出现异常
	 */
	private void displayExceptionStatus(String text) {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offline_arrow_start);
		mDownloadProgress.setTextColor(Color.RED);
		mDownloadProgress.setText(text);
	}

	/**
	 * 暂停
	 * 
	 * @param completeCode
	 */
	private void displayPauseStatus(int completeCode) {
		if (mMapCity != null) {
			completeCode = mMapCity.getcompleteCode();
		}

		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offline_arrow_start);
		mDownloadProgress.setTextColor(Color.RED);
		mDownloadProgress.setText("暂停中:" + completeCode + "%");

	}

	/**
	 * 下载成功
	 */
	private void displaySuccessStatus() {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.GONE);
		mDownloadProgress.setText("安装成功");

		mDownloadProgress.setTextColor(mContext.getResources().getColor(
				R.color.gary));
	}
	/**
	 * 新版本
	 */
	private void displayNewVersionStatus() {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadProgress.setText("新版本");

		mDownloadProgress.setTextColor(mContext.getResources().getColor(
				R.color.gary));
	}
	/**
	 * 正在解压
	 */
	private void displayUnZIPStatus(int completeCode) {
		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadImage.setVisibility(View.GONE);
		mDownloadProgress.setText("正在解压: " + completeCode + "%");
		mDownloadProgress.setTextColor(mContext.getResources().getColor(
				R.color.gary));
	}

	private void displyaLoadingStatus(int completeCode) {
		// todo
		if (mMapCity == null) {
			return;
		}

		mDownloadProgress.setVisibility(View.VISIBLE);
		mDownloadProgress.setText(mMapCity.getcompleteCode() + "%");
		mDownloadImage.setVisibility(View.VISIBLE);
		mDownloadImage.setImageResource(R.drawable.offline_arrow_stop);
		mDownloadProgress.setTextColor(Color.BLUE);
	}

	private synchronized void pauseDownload() {
		mapManager.pause();
		//暂停下载之后，开始下一个等待中的任务
		mapManager.restart();
	}

	/**
	 * 启动下载任务
	 */
	private synchronized boolean startDownload() {
		try {
			if (isProvince) {
				mapManager.downloadByProvinceName(mMapCity.getCity());
			} else {
				mapManager.downloadByCityName(mMapCity.getCity());
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}
	}

	public void onClick(View view) {
		

		int completeCode = -1, status = -1;
		if (mMapCity != null) {
			status = mMapCity.getState();
			completeCode = mMapCity.getcompleteCode();

			switch (status) {
			case OfflineMapStatus.UNZIP:
			case OfflineMapStatus.SUCCESS:
				try{
					mapManager.updateOfflineCityByName(mMapCity.getCity());
				}catch (Exception e){
					e.printStackTrace();
				}
				break;
			case OfflineMapStatus.LOADING:
				pauseDownload();
				displayPauseStatus(completeCode);
				break;
			case OfflineMapStatus.PAUSE:
			case OfflineMapStatus.CHECKUPDATES:
			case OfflineMapStatus.ERROR:
			case OfflineMapStatus.WAITING:
			default:
				if(startDownload())
					displayWaitingStatus(completeCode);
				else
					displayExceptionStatus("下载出现异常");
				break;
			}
			Log.e("zxy-child", mMapCity.getCity() + " " + mMapCity.getState());

		}

	}

	/**
	 * 长按弹出提示框 删除（取消）下载
	 */
	public synchronized void showDeleteDialog(final String name) {
		AlertDialog.Builder builder = new Builder(mContext);

		builder.setTitle(name);
		builder.setSingleChoiceItems(new String[] { "删除" }, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						dialog.dismiss();
						if (mapManager == null) {
							return;
						}
						switch (arg1) {
						case 0:
							mapManager.remove(name);
							break;

						default:
							break;
						}
					}
				});
		builder.setNegativeButton("取消", null);
		dialog = builder.create();
		dialog.show();
	}

	/**
	 * 长按弹出提示框 删除和更新
	 */
	public void showDeleteUpdateDialog(final String name) {
		AlertDialog.Builder builder = new Builder(mContext);

		builder.setTitle(name);
		builder.setSingleChoiceItems(new String[] { "删除", "检查更新" }, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						dialog.dismiss();
						if (mapManager == null) {
							return;
						}
						switch (arg1) {
						case 0:
							mapManager.remove(name);
							break;
						case 1:
							try {
								mapManager.updateOfflineCityByName(name);
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						default:
							break;
						}

					}
				});
		builder.setNegativeButton("取消", null);
		dialog = builder.create();
		dialog.show();
	}

	public boolean onLongClick(View arg0) {

		Log.d("map-longclick",
				mMapCity.getCity() + " : " + mMapCity.getState());
		if (mMapCity.getState() == OfflineMapStatus.SUCCESS) {
			showDeleteUpdateDialog(mMapCity.getCity());
		} else if (mMapCity.getState() != OfflineMapStatus.CHECKUPDATES) {
			showDeleteDialog(mMapCity.getCity());
		} 
		return false;
	}

}
