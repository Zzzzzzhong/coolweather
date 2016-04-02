package com.coolweather.app.activity;

import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {
	
	private static final String GET_CITY_WEATHER_ADDRESS = 
			"https://api.heweather.com/x3/weather?";
	
	private TextView tvCity;
	private TextView tvWeather;
	private ProgressDialog progressDialog;
	
	CoolWeatherDB coolWeatherDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
		setContentView(R.layout.weather_layout);
		
		tvCity = (TextView) findViewById(R.id.tv_city);
		tvWeather = (TextView) findViewById(R.id.tv_weather);
		
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		
		String cityId = getIntent().getStringExtra("cityId");
		String cityName = getIntent().getStringExtra("cityName");

		queryWeatherOfCity(cityId, cityName);
	}
	
	/** 从服务器查询选中城市的天气 */
	protected void queryWeatherOfCity(String cityId, final String cityName) {
		//根据选中的城市获取查询天气的网址
		String address = GET_CITY_WEATHER_ADDRESS 
				+ "cityid=" + cityId + "&key=16902dbe3ac24c6c8bfccc2056624f13";
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			String result = null;
			@Override
			public void onFinish(String response) {
				result = Utility.handleWeatherResponse(coolWeatherDB, response);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						tvCity.setText(cityName);
						if(null != result) {
							tvWeather.setText(result);
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
