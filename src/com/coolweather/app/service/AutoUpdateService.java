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
		
		LogUtil.d(TAG, "isAutoUpdate:" + isAutoUpdate); //��ӡ�Ƿ��Զ�����
		LogUtil.d(TAG, "autoUpdatePeriod:" + autoUpdatePeriod); //��ӡ�Զ�����ʱ����
		LogUtil.d(TAG, "cityId:" + cityId); //��ӡ����ID
		LogUtil.d(TAG, "cityName:" + cityName); //��ӡ��������
		
		if(isAutoUpdate) {
			if(autoUpdatePeriod > 0) {
				//����Ѿ������Զ����²��Ҷ�ȡ������ʱ�䣬�Ž����Զ����������Ĳ���
				new Thread(new Runnable() {
					@Override
					public void run() {
						updateService(cityId, cityName);
					}
				}).start();
				AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				long triggerAtTime = autoUpdatePeriod * 60 * 60 * 1000; //�����Զ�����ʱ��
				Intent i = new Intent(this, AutoUpdateReceiver.class);
				PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
				manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);	
			}
		}
		return super.onStartCommand(intent, flags, startId);	
	}

	protected void updateService(final String cityId, final String cityName) {
		if(!TextUtils.isEmpty(cityId) || !TextUtils.isEmpty(cityName)) {
			//����ѡ�еĳ��л�ȡ��ѯ��������ַ
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
