package com.coolweather.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class DetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_layout);
		
		TextView tv_weather = (TextView) findViewById(R.id.tv_weather);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		tv_weather.setText(prefs.getString("weatherInfo", null));
	}

}
