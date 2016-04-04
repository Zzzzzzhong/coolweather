package com.coolweather.app.model;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	/** ���ݿ����� */
	private static final String DB_NAME = "cool_weather";
	
	/** ���ݿ�汾 */
	private static final int DB_VERSION = 1;
	
	/** ���� */
	private static final String TABLE_NAME = "CityInfo";
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;

	/** ˽�л����췽�� */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = 
				new CoolWeatherOpenHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/** ��ȡCoolWeatherDB��ʵ�� */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if(coolWeatherDB == null) {
			coolWeatherDB =  new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	
	/** ��CityInfoʵ�����浽���ݿ� */
	public void saveCityInfo(CityInfo cityInfo) {
		if(cityInfo != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", cityInfo.getCityName());
			values.put("city_id", cityInfo.getCityId());
			values.put("province", cityInfo.getProvince());
			values.put("cnty", cityInfo.getCnty());
			db.insert(TABLE_NAME, null, values);
		}
	}
	
	/** �����ݿ��ȡ���еĳ�����Ϣ */
	public List<CityInfo> loadCityInfo() {
		List<CityInfo> list = new ArrayList<CityInfo>();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
		if(cursor.moveToFirst()) {
			do {
				CityInfo cityInfo = new CityInfo(); //����CityInfo��ʵ���������ݿ��ȡ���ݶ����ʼ��
				cityInfo.setId(cursor.getInt(cursor.getColumnIndex("id")));
				cityInfo.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				cityInfo.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
				cityInfo.setProvince(cursor.getString(cursor.getColumnIndex("province")));
				cityInfo.setCnty(cursor.getString(cursor.getColumnIndex("cnty")));
				list.add(cityInfo);
			} while(cursor.moveToNext());
		}
		return list;
	}
	
	public CityInfo getCityInfoByCityName(String cityName) {
		CityInfo cityInfo = null; //�������Ľ��
		Cursor cursor = db.query(TABLE_NAME, null, "city_name=?", new String[] {cityName}, null, null, null);
		if(cursor.moveToFirst()) {
			cityInfo = new CityInfo(); //����CityInfo��ʵ���������ݿ��ȡ���ݶ����ʼ��
			cityInfo.setId(cursor.getInt(cursor.getColumnIndex("id")));
			cityInfo.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
			cityInfo.setCityId(cursor.getString(cursor.getColumnIndex("city_id")));
			cityInfo.setProvince(cursor.getString(cursor.getColumnIndex("province")));
			cityInfo.setCnty(cursor.getString(cursor.getColumnIndex("cnty")));
		}
		return cityInfo;
	}

}
