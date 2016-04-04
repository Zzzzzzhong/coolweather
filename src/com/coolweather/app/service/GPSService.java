package com.coolweather.app.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.activity.R;
import com.coolweather.app.activity.WeatherActivity;
import com.coolweather.app.util.LogUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.widget.Toast;

public class GPSService extends Service {
	
	private static final String TAG = "GPSService";
	
	private LocationManager locationManager;
	private String provider;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initLBS();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void initLBS() {
		locationManager = 
				(LocationManager) getSystemService(Context.LOCATION_SERVICE); //获取位置管理器

		List<String> providerList = locationManager.getProviders(true); //获取已经启用的位置提供器列表
		if(providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER; //获取GPS定位提供器
		} else if(providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER; //获取网络定位提供器
		} else {
			//当没有可用的位置提供器时，弹出toast提示
			Toast.makeText(this, R.string.no_location_provider, Toast.LENGTH_SHORT).show();
			return;
		}
		LogUtil.d(TAG, "provider:" + provider); //打印定位到的市区
		Location location = locationManager.getLastKnownLocation(provider); //获取设备当前的位置信息
		if(location != null) {
			//显示当前设备的位置信息
			getLocation(location);
		}
	}
	
	private void getLocation(final Location location) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String uri = "http://lbs.juhe.cn/api/getaddressbylngb?"
							+ "lngx=" + location.getLongitude() + "&lngy=" + location.getLatitude();
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(uri);
					httpGet.addHeader("Accept-Language", "zh-CN"); //指定返回的语言
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if(httpResponse.getStatusLine().getStatusCode() == 200) {
						LogUtil.d(TAG, "request: successful"); //打印定位到的市区
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						JSONObject jsonObject = new JSONObject(response);

						JSONObject rowObject = jsonObject.getJSONObject("row");
						JSONObject resultObject = rowObject.getJSONObject("result");
						JSONObject addressComponent = resultObject.getJSONObject("addressComponent"); //获取地址区域信息
						String district = addressComponent.getString("district"); //获取市区
						String city = addressComponent.getString("city"); //获取城市
						String province = addressComponent.getString("province"); //获取省份
						Map<String, String> map = new HashMap<String, String>();
						map.put("district", district);
						map.put("city", city);
						map.put("province", province);
						
						LogUtil.d(TAG, "district:" + district); //打印定位到的市区
						LogUtil.d(TAG, "city:" + city); //打印定位到的城市
						LogUtil.d(TAG, "province:" + province); //打印定位到的省份
						
//						Message message = new Message();
//						message.what = WeatherActivity.GET_LOCATION;
//						message.obj = map;
//						Handler mHandler = ((WeatherActivity)MyApplication.getContext()).getHandler();
//						mHandler.sendMessage(message);
						Intent intent = new Intent(GPSService.this, WeatherActivity.class);
						intent.putExtra("addressComponent", new String[] {district, city, province});
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}


}
