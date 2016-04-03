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
		requestWindowFeature(Window.FEATURE_NO_TITLE); //�����ޱ�����
		setContentView(R.layout.weather_layout);
		
		initView();
		
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		
		String cityId = getIntent().getStringExtra("cityId");
		String cityName = getIntent().getStringExtra("cityName");
		
		if(!TextUtils.isEmpty(cityId) && !TextUtils.isEmpty(cityName)) {
			//���յ�����IDʱ��ѯ����
			queryWeatherOfCity(cityId, cityName);
		} else {
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
		tvCity.setText(prefs.getString("cityName", null));
		tvWeather.setText(prefs.getString("weatherInfo", null));
		
		Intent sIntent = new Intent(this, AutoUpdateService.class);
		startService(sIntent);
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
