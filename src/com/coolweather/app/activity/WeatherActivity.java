package com.coolweather.app.activity;

import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener {
	
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
		
		String cityId = getIntent().getStringExtra("cityId");
		String cityName = getIntent().getStringExtra("cityName");
		
		if(!TextUtils.isEmpty(cityId) && !TextUtils.isEmpty(cityName)) {
			//接收到城市ID时查询天气
			queryWeatherOfCity(cityId, cityName);
		} else {
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
		tvCity.setText(prefs.getString("cityName", null));
		tvWeather.setText(prefs.getString("weatherInfo", null));
		
		Intent sIntent = new Intent(this, AutoUpdateService.class);
		startService(sIntent);
	}
	
	/** 显示进度对话框 */
	private void showProgressDialog() {
		if(progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在 加载..."); //设置对话框内容
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

}
