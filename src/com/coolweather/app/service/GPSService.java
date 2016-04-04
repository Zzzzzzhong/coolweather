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
				(LocationManager) getSystemService(Context.LOCATION_SERVICE); //��ȡλ�ù�����

		List<String> providerList = locationManager.getProviders(true); //��ȡ�Ѿ����õ�λ���ṩ���б�
		if(providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER; //��ȡGPS��λ�ṩ��
		} else if(providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER; //��ȡ���綨λ�ṩ��
		} else {
			//��û�п��õ�λ���ṩ��ʱ������toast��ʾ
			Toast.makeText(this, R.string.no_location_provider, Toast.LENGTH_SHORT).show();
			return;
		}
		LogUtil.d(TAG, "provider:" + provider); //��ӡ��λ��������
		Location location = locationManager.getLastKnownLocation(provider); //��ȡ�豸��ǰ��λ����Ϣ
		if(location != null) {
			//��ʾ��ǰ�豸��λ����Ϣ
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
					httpGet.addHeader("Accept-Language", "zh-CN"); //ָ�����ص�����
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if(httpResponse.getStatusLine().getStatusCode() == 200) {
						LogUtil.d(TAG, "request: successful"); //��ӡ��λ��������
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						JSONObject jsonObject = new JSONObject(response);

						JSONObject rowObject = jsonObject.getJSONObject("row");
						JSONObject resultObject = rowObject.getJSONObject("result");
						JSONObject addressComponent = resultObject.getJSONObject("addressComponent"); //��ȡ��ַ������Ϣ
						String district = addressComponent.getString("district"); //��ȡ����
						String city = addressComponent.getString("city"); //��ȡ����
						String province = addressComponent.getString("province"); //��ȡʡ��
						Map<String, String> map = new HashMap<String, String>();
						map.put("district", district);
						map.put("city", city);
						map.put("province", province);
						
						LogUtil.d(TAG, "district:" + district); //��ӡ��λ��������
						LogUtil.d(TAG, "city:" + city); //��ӡ��λ���ĳ���
						LogUtil.d(TAG, "province:" + province); //��ӡ��λ����ʡ��
						
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
