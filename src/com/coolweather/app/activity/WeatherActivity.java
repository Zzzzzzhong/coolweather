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
		requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
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
			//接收到城市ID时查询天气
			queryWeatherOfCity(cityId, cityName);
		} else {
			
			//判断是否自动定位
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			if(prefs.getBoolean("isAutoGetLocation", false)) {
				initLBS();
			}
			
			//没有接收到城市ID时直接显示本地天气
			showWeather();
		}
	
	}

	/** 初始化布局页面 */
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
				//接收到城市ID时查询天气
				queryWeatherOfCity(cityId, cityName);
			}
			break;
		}
	}
	
	
	//https://api.heweather.com/x3/weather?cityid=CN101340101&key=16902dbe3ac24c6c8bfccc2056624f13

	/** 从服务器查询选中城市的天气 */
	protected void queryWeatherOfCity(final String cityId, final String cityName) {
		//根据选中的城市获取查询天气的网址
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
				//通过runOnUiThread()回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//隐藏进度对话框并弹出toast提示加载失败
						closeProgressDialog();
						Toast.makeText(WeatherActivity.this, 
								R.string.load_error, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/** 显示本地天气 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		//如果是第一次打开此应用，则设置默认自动更新天气，自动更新时间间隔为4小时
		if(prefs.getBoolean("firstTime", true)) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstTime", false);
			editor.putBoolean("isAutoGetLocation", false); //默认不自动定位
			editor.putBoolean("isAutoUpdate", true); //默认自动更新天气
			editor.putInt("autoUpdatePeriod", 4); //默认每4小时更新一次
			//还可以设置默认地点
			
			editor.commit(); //提交
		}
		
		tvCity.setText(prefs.getString("cityName", null));
		tvWeather.setText(prefs.getString("weatherInfo", null));
		
		Intent sIntent = new Intent(this, AutoUpdateService.class);
		startService(sIntent);
	}
	
	/** 显示进度对话框 */
	private void showProgressDialog() {
		if(progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载..."); //设置对话框内容
			progressDialog.setCanceledOnTouchOutside(false); //设置不能通过触摸对话框外的地方使对话框消失
		}
		progressDialog.show(); //显示进度对话框
	}
	
	/** 隐藏进度对话框 */
	private void closeProgressDialog() {
		if(progressDialog != null) {
			progressDialog.dismiss(); //隐藏进度对话框
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu); //解析得到菜单
		return true; //表示创建菜单成功
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
				
				CityInfo cityInfo = null; //搜索到的城市信息

				if(!TextUtils.isEmpty(district) && coolWeatherDB.getCityInfoByCityName(district.substring(0, district.length()-1)) != null) {
					cityInfo = coolWeatherDB.getCityInfoByCityName(district.substring(0, district.length()-1));
				} else if(!TextUtils.isEmpty(city) && coolWeatherDB.getCityInfoByCityName(city.substring(0, district.length()-1)) != null) {
					cityInfo = coolWeatherDB.getCityInfoByCityName(city.substring(0, district.length()-1));
				} else if(!TextUtils.isEmpty(province) && coolWeatherDB.getCityInfoByCityName(province.substring(0, district.length()-1)) != null) {
					cityInfo = coolWeatherDB.getCityInfoByCityName(province.substring(0, district.length()-1));
				} else {
					//如果获取不到城市信息，则Toast提示
					Toast.makeText(WeatherActivity.this, R.string.gps_failure, Toast.LENGTH_SHORT).show();
				}
				
				if(cityInfo != null) {
					//查询定位到的城市的天气
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
	
	/** 返回WeatherActivity的Handler对象 */
	public Handler getHandler() {
		return mHandler;
	}
	
	private void initLBS() {
		LocationManager locationManager = 
				(LocationManager) getSystemService(Context.LOCATION_SERVICE); //获取位置管理器

		String provider = null;
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
