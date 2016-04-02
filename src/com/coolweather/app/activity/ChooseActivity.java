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
	/** ������Ϣ �б� */
	private List<CityInfo> cityInfoList;
	/** ѡ�еĳ��� */
	private CityInfo selectedCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //�����ޱ�����
		setContentView(R.layout.choose_area);
		
		initView();
	}
	
	/** ��ʼ������ */
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

	/** �����ݿ���������ȡ������Ϣ */
	private void queryCityInfo() {
		cityInfoList = coolWeatherDB.loadCityInfo();
		if(cityInfoList.size() > 0) {
			dataList.clear(); //����б�����
			for(CityInfo cityInfo:cityInfoList) {
				dataList.add(cityInfo.getCityName());
			}
			mAdapter.notifyDataSetChanged(); //�����б�����
			listView.setSelection(0); //Ĭ��ѡ�е�һ��
			titleText.setText("�й������б� "); //���ñ���������
		} else {
			queryFromServer();
		}
	}

	/** �ӷ�������ȡ�й����е��б� */
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
				//ͨ��runOnUiThread()�ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//���ؽ��ȶԻ��򲢵���toast��ʾ����ʧ��
						closeProgressDialog();
						Toast.makeText(ChooseActivity.this, 
								R.string.load_error, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/** �ӷ�������ѯѡ�г��е����� */
	protected void queryWeatherOfCity() {
		//����ѡ�еĳ��л�ȡ��ѯ��������ַ
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
				//ͨ��runOnUiThread()�ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//���ؽ��ȶԻ��򲢵���toast��ʾ����ʧ��
						closeProgressDialog();
						Toast.makeText(ChooseActivity.this, 
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

	//���ذ�ť�ĵ���¼�
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	

}
