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
		requestWindowFeature(Window.FEATURE_NO_TITLE); //�����ޱ�����
		setContentView(R.layout.weather_layout);
		
		tvCity = (TextView) findViewById(R.id.tv_city);
		tvWeather = (TextView) findViewById(R.id.tv_weather);
		
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		
		String cityId = getIntent().getStringExtra("cityId");
		String cityName = getIntent().getStringExtra("cityName");

		queryWeatherOfCity(cityId, cityName);
	}
	
	/** �ӷ�������ѯѡ�г��е����� */
	protected void queryWeatherOfCity(String cityId, final String cityName) {
		//����ѡ�еĳ��л�ȡ��ѯ��������ַ
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
	
	/** ��ʾ���ȶԻ��� */
	private void showProgressDialog() {
		if(progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���� ����..."); //���öԻ�������
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

}
