package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.CityInfo;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseActivity extends Activity {
	
	private static final String GET_CHINESE_CITYS_ADDRESS = 
			"https://api.heweather.com/x3/citylist?search=allchina&key=16902dbe3ac24c6c8bfccc2056624f13";
	
	private static final String GET_CITY_WEATHER_ADDRESS = 
			"https://api.heweather.com/x3/weather?";
	
	private TextView titleText;
	private ListView listView;
	private ProgressDialog progressDialog;
	
	private CoolWeatherDB coolWeatherDB;
	
	private ArrayAdapter<String> mAdapter;
	
	private List<String> dataList = new ArrayList<String>();
	/** 城市信息 列表 */
	private List<CityInfo> cityInfoList;
	/** 选中的城市 */
	private CityInfo selectedCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题栏
		setContentView(R.layout.choose_area);
		
		initView();
	}
	
	/** 初始化布局 */
	private void initView() {
		titleText = (TextView) findViewById(R.id.tv_title);
		listView = (ListView) findViewById(R.id.lv_list);
		mAdapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_expandable_list_item_1, dataList);
		listView.setAdapter(mAdapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedCity = cityInfoList.get(position);
//				queryWeatherOfCity();
			}
		});
		queryCityInfo();
	}

	/** 从数据库或服务器读取城市信息 */
	private void queryCityInfo() {
		cityInfoList = coolWeatherDB.loadCityInfo();
		if(cityInfoList.size() > 0) {
			dataList.clear(); //清除列表数据
			for(CityInfo cityInfo:cityInfoList) {
				dataList.add(cityInfo.getCityName());
			}
			mAdapter.notifyDataSetChanged(); //更新列表数据
			listView.setSelection(0); //默认选中第一项
			titleText.setText("中国城市列表 "); //设置标题栏文字
		} else {
			queryFromServer();
		}
	}

	/** 从服务器读取中国城市的列表 */
	private void queryFromServer() {
		showProgressDialog();
		HttpUtil.sendHttpRequest(GET_CHINESE_CITYS_ADDRESS, new HttpCallbackListener() {
			boolean result = false;
			@Override
			public void onFinish(String response) {
				result = Utility.handleCitysResponse(coolWeatherDB, response);
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						if(result) {
							queryCityInfo();
						} else {
							Toast.makeText(ChooseActivity.this, 
									R.string.save_error, Toast.LENGTH_SHORT).show();
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
						Toast.makeText(ChooseActivity.this, 
								R.string.load_error, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/** 从服务器查询选中城市的天气 */
	protected void queryWeatherOfCity() {
		//根据选中的城市获取查询天气的网址
		String address = GET_CITY_WEATHER_ADDRESS 
				+ "cityid=" + selectedCity.getCityId() + "&key=16902dbe3ac24c6c8bfccc2056624f13";
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			boolean result = false;
			@Override
			public void onFinish(String response) {
				result = Utility.handleCitysResponse(coolWeatherDB, response);
				if(result) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							queryCityInfo();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				//通过runOnUiThread()回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//隐藏进度对话框并弹出toast提示加载失败
						closeProgressDialog();
						Toast.makeText(ChooseActivity.this, 
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

	//返回按钮的点击事件
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	

}
