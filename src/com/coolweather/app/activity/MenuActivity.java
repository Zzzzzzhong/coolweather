package com.coolweather.app.activity;

import com.coolweather.app.service.AutoUpdateService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MenuActivity extends Activity implements OnCheckedChangeListener, OnClickListener {
	
	/** 自动获取位置的开关 */
	private ToggleButton btnLocationSwitch;
	/** 自动更新天气的开关 */
	private ToggleButton btnUpdataSwitch;
	/** 显示自动更新时间的TextView */
	private TextView tvUpdatePeriod;
	/** 设置自动更新时间的Button */
	private Button btnSetTime;
	/** 选择自动更新时间的Spinner */
	private Spinner spinChooseTime;
	/** 保存修改的Button */
	private Button btnSaveTime;
	/** 显示当前自动更新时间的布局 */
	private LinearLayout llShowTime;
	/** 设置自动更新时间的布局 */
	private LinearLayout llSetTime;
	
	/** 保存选中的更新时间的临时变量 */
	private String tempTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu_layout);
		
		initView();
	}

	/** 初始化布局页面 */
	private void initView() {
		btnLocationSwitch = (ToggleButton) findViewById(R.id.tb_location_switch);
		btnUpdataSwitch = (ToggleButton) findViewById(R.id.tb_update_switch);
		llShowTime = (LinearLayout) findViewById(R.id.layout_show_time);
		llSetTime = (LinearLayout) findViewById(R.id.layout_set_time);
		tvUpdatePeriod = (TextView) findViewById(R.id.tv_update_period);
		btnSetTime = (Button) findViewById(R.id.btn_set_time);
		spinChooseTime = (Spinner) findViewById(R.id.spin_choose_time);
		btnSaveTime = (Button) findViewById(R.id.btn_save_time);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean isAutoGetLocation = prefs.getBoolean("isAutoGetLocation", false);
		Boolean isAutoUpdate = prefs.getBoolean("isAutoUpdate", false);
		int autoUpdatePeriod = prefs.getInt("autoUpdatePeriod", 0);
		if(isAutoUpdate) {
			btnUpdataSwitch.setChecked(true);
		} else {
			llShowTime.setVisibility(View.GONE);
			llSetTime.setVisibility(View.GONE);
			btnSaveTime.setVisibility(View.GONE);
			btnUpdataSwitch.setChecked(false);
		}
		
		if(isAutoGetLocation) {
			btnLocationSwitch.setChecked(true);
		} else {
			btnLocationSwitch.setChecked(false);
		}
		
		if(autoUpdatePeriod > 0) {
			tvUpdatePeriod.setText(autoUpdatePeriod + getString(R.string.hour));
		}
		
//		http://apis.baidu.com/heweather/weather/free?city=beijing&apikey=16902dbe3ac24c6c8bfccc2056624f13
//		int i = 3;
//		getResources().getIdentifier("ic_launcher", "drawable", getPackageName());
		
		spinChooseTime.setSelection(autoUpdatePeriod - 1); //默认为已选的更新时间
		spinChooseTime.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String[] timeArray = getResources().getStringArray(R.array.time_period_array);
				tempTime = timeArray[position]; //获取选中的时间
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				//不做任何处理
			}
		});
		
		btnLocationSwitch.setOnCheckedChangeListener(this);
		btnUpdataSwitch.setOnCheckedChangeListener(this);
		btnSetTime.setOnClickListener(this);
		btnSaveTime.setOnClickListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		SharedPreferences.Editor editor = 
				PreferenceManager.getDefaultSharedPreferences(this).edit();
		switch (buttonView.getId()) {
		case R.id.tb_update_switch:
			if(! isChecked) {
				editor.putBoolean("isAutoUpdate", false);				
				llShowTime.setVisibility(View.GONE);
				llSetTime.setVisibility(View.GONE);
				btnSaveTime.setVisibility(View.GONE);
			} else {
				editor.putBoolean("isAutoUpdate", true);
				llShowTime.setVisibility(View.VISIBLE);
			}
			editor.commit();
			break;
		case R.id.tb_location_switch:
			if(! isChecked) {
				editor.putBoolean("isAutoGetLocation", false);
			} else {
				editor.putBoolean("isAutoGetLocation", true);
			}
			editor.commit();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_set_time:
			llSetTime.setVisibility(View.VISIBLE);
			btnSaveTime.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_save_time:
			String selectedTime = tempTime.substring(0, 1);
//			Toast.makeText(this, selectedTime, Toast.LENGTH_SHORT).show();
			SharedPreferences.Editor editor = 
					PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putInt("autoUpdatePeriod", Integer.valueOf(selectedTime));
			editor.commit();
			
			tvUpdatePeriod.setText(tempTime);

			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent sIntent = new Intent(this, AutoUpdateService.class);
		startService(sIntent);
	}

}
