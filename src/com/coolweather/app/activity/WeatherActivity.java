package com.coolweather.app.activity;

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

import com.coolweather.app.model.CityInfo;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.LogUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener {
	
	public static final int GET_LOCATION = 0;
	
	private static final String TAG = "WeatherActivity";
	
	private static final String GET_CITY_WEATHER_ADDRESS = 
			"https://api.heweather.com/x3/weather?";
	
	private Button btnSwitchCity;
	private Button btnRefresh;
	private TextView tvCity;
	private TextView tvWeather;
	private ProgressDialog progressDialog;
	
	CoolWeatherDB coolWeatherDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //�����ޱ�����
		setContentView(R.layout.weather_layout);
		
		initView();
		
		coolWeatherDB = CoolWeatherDB.getInstance(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		String cityId = getIntent().getStringExtra("cityId");
		String cityName = getIntent().getStringExtra("cityName");
		
		if(!TextUtils.isEmpty(cityId) && !TextUtils.isEmpty(cityName)) {
			//���յ�����IDʱ��ѯ����
			queryWeatherOfCity(cityId, cityName);
		} else {
			
			//�ж��Ƿ��Զ���λ
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			if(prefs.getBoolean("isAutoGetLocation", false)) {
				initLBS();
			}
			
			//û�н��յ�����IDʱֱ����ʾ��������
			showWeather();
		}
	
	}

	/** ��ʼ������ҳ�� */
	private void initView() {
		btnSwitchCity = (Button) findViewById(R.id.btn_switch_city);
		btnRefresh = (Button) findViewById(R.id.btn_refresh);
		tvCity = (TextView) findViewById(R.id.tv_city);
		tvWeather = (TextView) findViewById(R.id.tv_weather);
		
		btnSwitchCity.setOnClickListener(this);
		btnRefresh.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_switch_city:
			Intent intent = new Intent(WeatherActivity.this, ChooseActivity.class);
			intent.putExtra("fromWeatherAcitivity", true);
			startActivity(intent);
			break;
		case R.id.btn_refresh:
			tvCity.setText(R.string.refreshing);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String cityId = prefs.getString("cityId", null);
			String cityName = prefs.getString("cityName", null);
			if(!TextUtils.isEmpty(cityId) || !TextUtils.isEmpty(cityName)) {
				//���յ�����IDʱ��ѯ����
				queryWeatherOfCity(cityId, cityName);
			}
			break;
		}
	}
	
	
	//https://api.heweather.com/x3/weather?cityid=CN101340101&key=16902dbe3ac24c6c8bfccc2056624f13

	/** �ӷ�������ѯѡ�г��е����� */
	protected void queryWeatherOfCity(final String cityId, final String cityName) {
		//����ѡ�еĳ��л�ȡ��ѯ��������ַ
		String address = GET_CITY_WEATHER_ADDRESS 
				+ "cityid=" + cityId + "&key=16902dbe3ac24c6c8bfccc2056624f13";
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			String result = null;
			@Override
			public void onFinish(String response) {
				result = Utility.handleWeatherResponse(WeatherActivity.this, response, cityId, cityName);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						if(!TextUtils.isEmpty(result)) {
//							tvWeather.setText(result);
							showWeather();
						}else {
							Toast.makeText(WeatherActivity.this, 
									R.string.load_error, Toast.LENGTH_SHORT).show();
						}
						
					}
				});
			}
			
			@Override
			public void onError(Exception e) {
				//ͨ��runOnUiThread()�ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//���ؽ��ȶԻ��򲢵���toast��ʾ����ʧ��
						closeProgressDialog();
						Toast.makeText(WeatherActivity.this, 
								R.string.load_error, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/** ��ʾ�������� */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//����ǵ�һ�δ򿪴�Ӧ�ã�������Ĭ���Զ������������Զ�����ʱ����Ϊ4Сʱ
		if(prefs.getBoolean("firstTime", true)) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstTime", false);
			editor.putBoolean("isAutoGetLocation", false); //Ĭ�ϲ��Զ���λ
			editor.putBoolean("isAutoUpdate", true); //Ĭ���Զ���������
			editor.putInt("autoUpdatePeriod", 4); //Ĭ��ÿ4Сʱ����һ��
			//����������Ĭ�ϵص�
			
			editor.commit(); //�ύ
		}
		
		tvCity.setText(prefs.getString("cityName", null));
		tvWeather.setText(prefs.getString("weatherInfo", null));
		
		Intent sIntent = new Intent(this, AutoUpdateService.class);
		startService(sIntent);
	}
	
	/** ��ʾ���ȶԻ��� */
	private void showProgressDialog() {
		if(progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���..."); //���öԻ�������
			progressDialog.setCanceledOnTouchOutside(false); //���ò���ͨ�������Ի�����ĵط�ʹ�Ի�����ʧ
		}
		progressDialog.show(); //��ʾ���ȶԻ���
	}
	
	/** ���ؽ��ȶԻ��� */
	private void closeProgressDialog() {
		if(progressDialog != null) {
			progressDialog.dismiss(); //���ؽ��ȶԻ���
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu); //�����õ��˵�
		return true; //��ʾ�����˵��ɹ�
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, MenuActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case GET_LOCATION:
				Map<String, String> map = (HashMap<String, String>) msg.obj;
				String district = map.get("district");
				String city = map.get("city");
				String province = map.get("province");
				
				CityInfo cityInfo = null; //�������ĳ�����Ϣ

				if(!TextUtils.isEmpty(district) && coolWeatherDB.getCityInfoByCityName(district.substring(0, district.length()-1)) != null) {
					cityInfo = coolWeatherDB.getCityInfoByCityName(district.substring(0, district.length()-1));
				} else if(!TextUtils.isEmpty(city) && coolWeatherDB.getCityInfoByCityName(city.substring(0, district.length()-1)) != null) {
					cityInfo = coolWeatherDB.getCityInfoByCityName(city.substring(0, district.length()-1));
				} else if(!TextUtils.isEmpty(province) && coolWeatherDB.getCityInfoByCityName(province.substring(0, district.length()-1)) != null) {
					cityInfo = coolWeatherDB.getCityInfoByCityName(province.substring(0, district.length()-1));
				} else {
					//�����ȡ����������Ϣ����Toast��ʾ
					Toast.makeText(WeatherActivity.this, R.string.gps_failure, Toast.LENGTH_SHORT).show();
				}
				
				if(cityInfo != null) {
					//��ѯ��λ���ĳ��е�����
					String cityName = cityInfo.getCityName();
					String cityId = cityInfo.getCityId();
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
					if(!cityName.equals(prefs.getString("cityName", null)) && !cityId.equals(prefs.getString("cityId", null))) {
						queryWeatherOfCity(cityId, cityName);
					}	
				}

			}
		}
	};
	
	/** ����WeatherActivity��Handler���� */
	public Handler getHandler() {
		return mHandler;
	}
	
	private void initLBS() {
		LocationManager locationManager = 
				(LocationManager) getSystemService(Context.LOCATION_SERVICE); //��ȡλ�ù�����

		String provider = null;
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
						
						Message message = new Message();
						message.what = WeatherActivity.GET_LOCATION;
						message.obj = map;
						mHandler.sendMessage(message);
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
