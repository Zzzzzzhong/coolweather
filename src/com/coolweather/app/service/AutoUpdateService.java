package com.coolweather.app.service;

import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.LogUtil;
import com.coolweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class AutoUpdateService extends Service {
	
	private static final String TAG = "MyLog";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean isAutoUpdate = prefs.getBoolean("isAutoUpdate", false);
		int autoUpdatePeriod = prefs.getInt("autoUpdatePeriod", 0);
		final String cityId = prefs.getString("cityId", null);
		final String cityName = prefs.getString("cityName", null);
		
		LogUtil.d(TAG, "isAutoUpdate:" + isAutoUpdate); //打印是否自动更新
		LogUtil.d(TAG, "autoUpdatePeriod:" + autoUpdatePeriod); //打印自动更新时间间隔
		LogUtil.d(TAG, "cityId:" + cityId); //打印城市ID
		LogUtil.d(TAG, "cityName:" + cityName); //打印城市名称
		
		if(isAutoUpdate) {
			if(autoUpdatePeriod > 0) {
				//如果已经打开了自动更新并且读取到更新时间，才进行自动更新天气的操作
				new Thread(new Runnable() {
					@Override
					public void run() {
						updateService(cityId, cityName);
					}
				}).start();
				AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				long triggerAtTime = autoUpdatePeriod * 60 * 60 * 1000; //设置自动更新时间
				Intent i = new Intent(this, AutoUpdateReceiver.class);
				PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
				manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);	
			}
		}
		return super.onStartCommand(intent, flags, startId);	
	}

	protected void updateService(final String cityId, final String cityName) {
		if(!TextUtils.isEmpty(cityId) || !TextUtils.isEmpty(cityName)) {
			//根据选中的城市获取查询天气的网址
			String address = "https://api.heweather.com/x3/weather?" 
					+ "cityid=" + cityId + "&key=16902dbe3ac24c6c8bfccc2056624f13";
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
				@Override
				public void onFinish(String response) {
					Utility.handleWeatherResponse(AutoUpdateService.this, response);
				}
	
				@Override
				public void onError(Exception e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	

}
