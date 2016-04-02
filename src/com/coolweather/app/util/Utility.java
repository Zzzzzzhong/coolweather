package com.coolweather.app.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.model.CityInfo;
import com.coolweather.app.model.CoolWeatherDB;

import android.text.TextUtils;

public class Utility {
	
//	解析结果：
//	{"city_info":[{"city":"南子岛","cnty":"中国","id":"CN101310230","lat":"11.26","lon":"114.20","prov":"海南"},
//	              {"city":"北京","cnty":"中国","id":"CN101010100","lat":"39.904000","lon":"116.391000","prov":"直辖市"},
//	}

	/**
	 * 查询中国的城市信息列表,对服务器响应的结果进行处理并保存到数据库
	 * @param coolWeatherDB
	 * @param response 服务器响应的结果
	 * @return
	 */
	public synchronized static boolean handleCitysResponse(CoolWeatherDB coolWeatherDB, String response) {
		if(!TextUtils.isEmpty(response)) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				JSONArray array = jsonObject.getJSONArray("city_info");
				for(int i=0; i<array.length(); i++) {
					JSONObject subObject = array.getJSONObject(i);
					CityInfo cityInfo = new CityInfo();
					cityInfo.setCityName(subObject.getString("city"));
					cityInfo.setCityId(subObject.getString("id"));
					cityInfo.setProvince(subObject.getString("prov"));
					cityInfo.setCnty(subObject.getString("cnty"));
					coolWeatherDB.saveCityInfo(cityInfo);
				}
				return true;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
		}
		return false;
	}
	
}
