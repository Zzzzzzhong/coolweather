package com.coolweather.app.service;

import com.coolweather.app.activity.R;
import com.coolweather.app.activity.WeatherActivity;
import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
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
import android.widget.Toast;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateService();
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		long triggerAtTime = 4 * 60 * 60 * 1000; //设置自动更新时间为4小时
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		
		return super.onStartCommand(intent, flags, startId);
	}

	protected void updateService() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final String cityId = prefs.getString("cityId", null);
		final String cityName = prefs.getString("cityName", null);
		if(!TextUtils.isEmpty(cityId) || !TextUtils.isEmpty(cityName)) {
			//根据选中的城市获取查询天气的网址
			String address = "https://api.heweather.com/x3/weather?" 
					+ "cityid=" + cityId + "&key=16902dbe3ac24c6c8bfccc2056624f13";
			HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
				@Override
				public void onFinish(String response) {
					Utility.handleWeatherResponse(AutoUpdateService.this, response, cityId, cityName);
				}
				
				@Override
				public void onError(Exception e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	

}
