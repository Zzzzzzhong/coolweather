package com.coolweather.app.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.model.CityInfo;
import com.coolweather.app.model.CoolWeatherDB;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Utility {
	
	static StringBuilder buffer;
	
	static Map<String, String> weatherInfo;
	
	/**
	 * ��ѯ�й��ĳ�����Ϣ�б�,�Է�������Ӧ�Ľ�����д������浽���ݿ�
	 * @param coolWeatherDB
	 * @param response ��������Ӧ�Ľ��
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
	
	String textData = "{\"HeWeather data service 3.0\":[{\"basic\":{\"city\":\"��خ\",\"cnty\":\"�й�\",\"id\":\"CN101280102\",\"lat\":\"22.570000\",\"lon\":\"113.220000\",\"update\":{\"loc\":\"2016-04-04 16:49\",\"utc\":\"2016-04-04 08:49\"}},\"daily_forecast\":[{\"astro\":{\"sr\":\"06:16\",\"ss\":\"18:43\"},\"cond\":{\"code_d\":\"307\",\"code_n\":\"300\",\"txt_d\":\"����\",\"txt_n\":\"����\"},\"date\":\"2016-04-04\",\"hum\":\"76\",\"pcpn\":\"0.5\",\"pop\":\"94\",\"pres\":\"1011\",\"tmp\":{\"max\":\"25\",\"min\":\"21\"},\"vis\":\"10\",\"wind\":{\"deg\":\"176\",\"dir\":\"�޳�������\",\"sc\":\"΢��\",\"spd\":\"5\"}},{\"astro\":{\"sr\":\"06:15\",\"ss\":\"18:43\"},\"cond\":{\"code_d\":\"300\",\"code_n\":\"305\",\"txt_d\":\"����\",\"txt_n\":\"С��\"},\"date\":\"2016-04-05\",\"hum\":\"57\",\"pcpn\":\"0.3\",\"pop\":\"91\",\"pres\":\"1013\",\"tmp\":{\"max\":\"26\",\"min\":\"22\"},\"vis\":\"10\",\"wind\":{\"deg\":\"168\",\"dir\":\"�Ϸ�\",\"sc\":\"3-4\",\"spd\":\"14\"}},{\"astro\":{\"sr\":\"06:14\",\"ss\":\"18:44\"},\"cond\":{\"code_d\":\"305\",\"code_n\":\"306\",\"txt_d\":\"С��\",\"txt_n\":\"����\"},\"date\":\"2016-04-06\",\"hum\":\"49\",\"pcpn\":\"0.0\",\"pop\":\"0\",\"pres\":\"1013\",\"tmp\":{\"max\":\"28\",\"min\":\"23\"},\"vis\":\"10\",\"wind\":{\"deg\":\"165\",\"dir\":\"�Ϸ�\",\"sc\":\"3-4\",\"spd\":\"15\"}},{\"astro\":{\"sr\":\"06:13\",\"ss\":\"18:44\"},\"cond\":{\"code_d\":\"306\",\"code_n\":\"305\",\"txt_d\":\"����\",\"txt_n\":\"С��\"},\"date\":\"2016-04-07\",\"hum\":\"51\",\"pcpn\":\"0.1\",\"pop\":\"0\",\"pres\":\"1013\",\"tmp\":{\"max\":\"28\",\"min\":\"20\"},\"vis\":\"10\",\"wind\":{\"deg\":\"185\",\"dir\":\"�޳�������\",\"sc\":\"΢��\",\"spd\":\"6\"}},{\"astro\":{\"sr\":\"06:12\",\"ss\":\"18:45\"},\"cond\":{\"code_d\":\"104\",\"code_n\":\"104\",\"txt_d\":\"��\",\"txt_n\":\"��\"},\"date\":\"2016-04-08\",\"hum\":\"60\",\"pcpn\":\"0.2\",\"pop\":\"17\",\"pres\":\"1013\",\"tmp\":{\"max\":\"28\",\"min\":\"21\"},\"vis\":\"10\",\"wind\":{\"deg\":\"183\",\"dir\":\"�޳�������\",\"sc\":\"΢��\",\"spd\":\"9\"}},{\"astro\":{\"sr\":\"06:11\",\"ss\":\"18:45\"},\"cond\":{\"code_d\":\"104\",\"code_n\":\"104\",\"txt_d\":\"��\",\"txt_n\":\"��\"},\"date\":\"2016-04-09\",\"hum\":\"73\",\"pcpn\":\"0.8\",\"pop\":\"57\",\"pres\":\"1013\",\"tmp\":{\"max\":\"27\",\"min\":\"22\"},\"vis\":\"10\",\"wind\":{\"deg\":\"158\",\"dir\":\"�Ϸ�\",\"sc\":\"3-4\",\"spd\":\"14\"}},{\"astro\":{\"sr\":\"06:10\",\"ss\":\"18:45\"},\"cond\":{\"code_d\":\"104\",\"code_n\":\"305\",\"txt_d\":\"��\",\"txt_n\":\"С��\"},\"date\":\"2016-04-10\",\"hum\":\"70\",\"pcpn\":\"0.6\",\"pop\":\"49\",\"pres\":\"1009\",\"tmp\":{\"max\":\"25\",\"min\":\"21\"},\"vis\":\"10\",\"wind\":{\"deg\":\"173\",\"dir\":\"�޳�������\",\"sc\":\"΢��\",\"spd\":\"10\"}}],\"hourly_forecast\":[{\"date\":\"2016-04-04 16:00\",\"hum\":\"65\",\"pop\":\"80\",\"pres\":\"1011\",\"tmp\":\"30\",\"wind\":{\"deg\":\"183\",\"dir\":\"�Ϸ�\",\"sc\":\"3-4\",\"spd\":\"19\"}},{\"date\":\"2016-04-04 19:00\",\"hum\":\"78\",\"pop\":\"25\",\"pres\":\"1011\",\"tmp\":\"28\",\"wind\":{\"deg\":\"170\",\"dir\":\"�Ϸ�\",\"sc\":\"3-4\",\"spd\":\"19\"}},{\"date\":\"2016-04-04 22:00\",\"hum\":\"87\",\"pop\":\"0\",\"pres\":\"1013\",\"tmp\":\"25\",\"wind\":{\"deg\":\"157\",\"dir\":\"���Ϸ�\",\"sc\":\"΢��\",\"spd\":\"15\"}}],\"now\":{\"cond\":{\"code\":\"501\",\"txt\":\"��\"},\"fl\":\"25\",\"hum\":\"97\",\"pcpn\":\"1.6\",\"pres\":\"1009\",\"tmp\":\"22\",\"vis\":\"3\",\"wind\":{\"deg\":\"130\",\"dir\":\"���Ϸ�\",\"sc\":\"3-4\",\"spd\":\"14\"}},\"status\":\"ok\",\"suggestion\":{\"comf\":{\"brf\":\"����\",\"txt\":\"���첻̫��Ҳ��̫�䣬�������������������������������£�Ӧ��е��Ƚ���ˬ�����ʡ�\"},\"cw\":{\"brf\":\"����\",\"txt\":\"����ϴ����δ��24Сʱ�����꣬����ڴ��ڼ�ϴ������ˮ��·�ϵ���ˮ���ܻ��ٴ�Ū�����İ�����\"},\"drsg\":{\"brf\":\"����\",\"txt\":\"�����ų���T���������ӵ���ȷ�װ������������������֯�����������׺ͳ��㡣\"},\"flu\":{\"brf\":\"���׷�\",\"txt\":\"����ת��������ʪ�Ƚϴ󣬽��׷�����ð�����ʽ�����������ע���ʵ�������\"},\"sport\":{\"brf\":\"�ϲ���\",\"txt\":\"�н�ǿ��ˮ��������ѡ�������ڽ��н��������˶���\"},\"trav\":{\"brf\":\"�ϲ���\",\"txt\":\"�¶����˵�һ�죬�������󣬵��н�ǿ��ˮ��������ĳ��в����ܶ��鷳����������û��Ƕ�ѡ�������ڻ�ɣ�\"},\"uv\":{\"brf\":\"����\",\"txt\":\"���������߷��������������ر�������������ڻ��⣬����Ϳ��SPF��8-12֮��ķ�ɹ����Ʒ��\"}}}]}";
	
	/**
	 * �������������ص����ݣ���ȡ����Ԥ�����
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static Map<String, String> handleWeatherResponse(Context context, String response) {
		Map<String, String> result = null;
		if(!TextUtils.isEmpty(response)) {
			try {
				result = parseJSON(response);
				saveWeatherInfo(context, result);
				return result;
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * �����������ص�������Ϣ���浽SharedPreference
	 * @param context
	 * @param weatherInfo
	 */
	public static void saveWeatherInfo(Context context, Map<String, String> weatherInfo) {
		SharedPreferences.Editor editor = 
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString("cityId",  weatherInfo.get("id")); //�������ID
		editor.putString("cityName",  weatherInfo.get("city")); //�����������
		editor.putString("status", weatherInfo.get("status")); //�����ѯ״̬
		editor.putString("loc",  weatherInfo.get("loc")); //�������ʱ��
		editor.putString("code", weatherInfo.get("code")); //������������
		editor.putString("txt", weatherInfo.get("txt")); //������������
		editor.putString("tmp", weatherInfo.get("tmp")); //���浱ǰ�¶�
		editor.putString("maxTemp", weatherInfo.get("maxTemp")); //���浱������¶�
		editor.putString("minTemp", weatherInfo.get("minTemp")); //���浱������¶�
		editor.putString("weatherInfo", weatherInfo.get("weatherInfo")); //���浱������¶�
		
		editor.commit(); //�ύ����
	}
	
	private static Map<String, String> parseJSON(String response) throws Exception {
		buffer = new StringBuilder();
		weatherInfo = new HashMap<String, String>();
		
		JSONObject dataObject = new JSONObject(response);
		JSONArray dataArray = dataObject.getJSONArray("HeWeather data service 3.0");
		JSONObject jsonObject = dataArray.getJSONObject(0);
		
		getStatus(jsonObject);
		getBasic(jsonObject);
		getNow(jsonObject);
		
		getAqi(jsonObject);
		getHourly(jsonObject);
		getDaily(jsonObject);				
		getSuggestion(jsonObject);
		weatherInfo.put("weatherInfo", buffer.toString());
		
		return weatherInfo;		
	}
	
	/**
	 * ��ȡ�������
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getStatus(JSONObject jsonObject) {
		String status = null;
		try {
			status = jsonObject.getString("status");
			if("ok".equals(status)) {
				weatherInfo.put("status", "����������ӿ�������");
			} else if("invalid key".equals(status)) {
				weatherInfo.put("status", "���������������û�key��");
			} else if("no more requests".equals(status)) {
				weatherInfo.put("status", "����������������ʴ�����");
			} else if("anr".equals(status)) {
				weatherInfo.put("status", "�����������������Ӧ��ʱ��");
			} else if("permission denied".equals(status)) {
				weatherInfo.put("status", "���������û�з���Ȩ�ޣ�");
			}
		} catch (JSONException e) {
			return;
		}
	}
	
	/**
	 * ��ȡ���л�����Ϣ
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getBasic(JSONObject jsonObject) {
		JSONObject basic;
		try {
			basic = jsonObject.getJSONObject("basic");
			buffer.append("\n���л�����Ϣ\n");
			String id = basic.getString("id");
			weatherInfo.put("id", id);// ��ȡ��������
			String city = basic.getString("city");
			buffer.append("�������ƣ�"+city+"\n");
			weatherInfo.put("city", city);// ��ȡ��������
			String cnty = basic.getString("cnty");
			buffer.append("�������ƣ�"+cnty+"\n");
			weatherInfo.put("cnty", cnty);// ��ȡ��������
			String lat = basic.getString("lat");
			buffer.append("γ�ȣ�"+lat+"\n");
			weatherInfo.put("lat", lat);// ��ȡγ��
			String lon = basic.getString("lon");
			buffer.append("���ȣ�"+lon+"\n");
			weatherInfo.put("lon", lon);// ��ȡ����
			JSONObject update = basic.getJSONObject("update");
			String loc = update.getString("loc");
			buffer.append("����ʱ�䣺"+loc+"\n");	
			weatherInfo.put("loc", loc);// ��ȡ����ʱ��
		} catch (JSONException e) {
			return;
		}	
	}
	
	/**
	 * ��ȡʵ������
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getNow(JSONObject jsonObject) {
		JSONObject now = null;
		try {
			now = jsonObject.getJSONObject("now");
			buffer.append("\nʵ������\n");
			JSONObject cond = now.getJSONObject("cond");
			buffer.append("����״����\n");
			String code = cond.getString("code");
			buffer.append("�������룺"+code+"\n");
			weatherInfo.put("code", code);// ��ȡ��������
			String txt = cond.getString("txt");
			buffer.append("����������"+txt+"\n");
			weatherInfo.put("txt", txt);// ��ȡ��������
			String fl = now.getString("fl");
			buffer.append("����¶ȣ�"+fl+"\n");
			weatherInfo.put("fl", fl);// ��ȡ����¶�
			String hum = now.getString("hum");
			buffer.append("ʪ��(%)��"+hum+"\n");
			weatherInfo.put("hum", hum);// ��ȡʪ��
			String pcpn = now.getString("pcpn");
			buffer.append("������(mm)��"+pcpn+"\n");
			weatherInfo.put("pcpn", pcpn);// ��ȡ������(mm)
			String pres = now.getString("pres");
			buffer.append("��ѹ��"+pres+"\n");
			weatherInfo.put("pres", pres);// ��ȡ��ѹ
			String tmp = now.getString("tmp");
			buffer.append("�¶ȣ�"+tmp+"\n");
			weatherInfo.put("tmp", tmp);// ��ȡ�¶�
			String vis = now.getString("vis");
			buffer.append("�ܼ���(km)��"+vis+"\n");
			weatherInfo.put("vis", vis);// ��ȡ�ܼ���(km)
			JSONObject wind = now.getJSONObject("wind");
			buffer.append("����״����\n");
			String deg = wind.getString("deg");
			buffer.append("����(�Ƕ�)��"+deg+"\n");
			weatherInfo.put("deg", deg);// ��ȡ����(�Ƕ�)
			String dir = wind.getString("dir");
			buffer.append("����(����)��"+dir+"\n");
			weatherInfo.put("dir", dir);// ��ȡ����(����)
			String sc = wind.getString("sc");
			buffer.append("�����ȼ���"+sc+"\n");
			weatherInfo.put("sc", sc);// ��ȡ�����ȼ�
			String spd = wind.getString("spd");
			buffer.append("����(Kmph)��"+spd+"\n");
			weatherInfo.put("spd", spd);// ��ȡ����(Kmph)
		} catch (JSONException e) {
			return;
		}
	}
	
	/**
	 * ��ȡ��������ָ��*
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getAqi(JSONObject jsonObject) {
		JSONObject aqi1;
		try {
			aqi1 = jsonObject.getJSONObject("aqi");
			buffer.append("\n��������ָ��*\n");
			JSONObject city = aqi1.getJSONObject("city");
			String aqi = city.getString("aqi");
			buffer.append("��������ָ����"+aqi+"\n");
			weatherInfo.put("aqi", aqi);// ��ȡ��������ָ��
			String co = city.getString("co");
			buffer.append("һ����̼1Сʱƽ��ֵ(ug/m^3)��"+co+"\n");
			weatherInfo.put("co", co);// ��ȡһ����̼1Сʱƽ��ֵ(ug/m^3)
			String no2 = city.getString("no2");
			buffer.append("��������1Сʱƽ��ֵ(ug/m^3)��"+no2+"\n");
			weatherInfo.put("no2", no2);// ��ȡ��������1Сʱƽ��ֵ(ug/m^3)
			String o3 = city.getString("o3");
			buffer.append("����1Сʱƽ��ֵ(ug/m^3)��"+o3+"\n");
			weatherInfo.put("o3", o3);// ��ȡ����1Сʱƽ��ֵ(ug/m^3)
			String pm10 = city.getString("pm10");
			buffer.append("PM10 1Сʱƽ��ֵ(ug/m^3)��"+pm10+"\n");
			weatherInfo.put("pm10", pm10);// ��ȡPM10 1Сʱƽ��ֵ(ug/m^3)
			String pm25 = city.getString("pm25");
			buffer.append("PM2.5 1Сʱƽ��ֵ(ug/m^3)��"+pm25+"\n");
			weatherInfo.put("pm25", pm25);// ��ȡPM2.5 1Сʱƽ��ֵ(ug/m^3)
			String qlty = city.getString("qlty");
			buffer.append("�����������"+qlty+"\n");
			weatherInfo.put("qlty", qlty);// ��ȡ�����������
			String so2 = city.getString("so2");
			buffer.append("��������1Сʱƽ��ֵ(ug/m^3)��"+so2+"\n");
			weatherInfo.put("so2", so2);// ��ȡ��������1Сʱƽ��ֵ(ug/m^3)
		} catch (JSONException e) {
			return;
		}
		
	}
	
	/**
	 * ��ȡÿ3Сʱ����Ԥ��
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getHourly(JSONObject jsonObject) {
		JSONArray hourly;
		try {
			hourly = jsonObject.getJSONArray("hourly_forecast");
			buffer.append("\nHourly_forecast����Ԥ��\n");
			for(int i=0; i<hourly.length(); i++) {
				JSONObject object = hourly.getJSONObject(i);
				String date = object.getString("date");
				buffer.append("�������ڣ�"+date+"\n");
				String hum = object.getString("hum");
				buffer.append("ʪ��(%)��"+hum+"\n");
				String pop = object.getString("pop");
				buffer.append("��ˮ����(%)��"+pop+"\n");
				String pres = object.getString("pres");
				buffer.append("��ѹ��"+pres+"\n");
				String tmp = object.getString("tmp");
				buffer.append("�¶ȣ�"+tmp+"\n");
				JSONObject wind = object.getJSONObject("wind");
				buffer.append("����״����\n");
				String deg = wind.getString("deg");
				buffer.append("����(�Ƕ�)��"+deg+"\n");
				String dir = wind.getString("dir");
				buffer.append("����(����)��"+dir+"\n");
				String sc = wind.getString("sc");
				buffer.append("�����ȼ���"+sc+"\n");
				int spd = wind.getInt("spd");
				buffer.append("����(Kmph)��"+spd+"\n");
			}
		} catch (JSONException e) {
			return;
		}		
	}
	
	/**
	 * ��ȡδ��6������Ԥ��
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getDaily(JSONObject jsonObject) {
		JSONArray daily;
		try {
			daily = jsonObject.getJSONArray("daily_forecast");
			buffer.append("\nδ��6������Ԥ��\n");
			for(int i=0; i<daily.length(); i++) {					
				JSONObject object = daily.getJSONObject(i);
				JSONObject astro = object.getJSONObject("astro");
				buffer.append("������ֵ��\n");
				String sr = astro.getString("sr");
				buffer.append("�ճ�ʱ�䣺"+sr+"\n");
				String ss = astro.getString("ss");
				buffer.append("����ʱ�䣺"+ss+"\n");
				JSONObject cond = object.getJSONObject("cond");
				buffer.append("����״����"+cond+"\n");
				int code_d = cond.getInt("code_d");
				buffer.append("�����������룺"+code_d+"\n");
				String txt_d = cond.getString("txt_d");
				buffer.append("��������������"+txt_d+"\n");
				int code_n = cond.getInt("code_n");
				buffer.append("ҹ���������룺"+code_n+"\n");
				String txt_n = cond.getString("txt_n");
				buffer.append("ҹ������������"+txt_n+"\n");
				
				String date = object.getString("date");
				buffer.append("�������ڣ�"+date+"\n");
				String hum = object.getString("hum");
				buffer.append("ʪ��(%)��"+hum+"\n");
				String pcpn = object.getString("pcpn");
				buffer.append("������(mm)��"+pcpn+"\n");
				String pop = object.getString("pop");
				buffer.append("��ˮ����(%)��"+pop+"\n");
				String pres = object.getString("pres");
				buffer.append("��ѹ��"+pres+"\n");
				
				JSONObject tmp = object.getJSONObject("tmp");
				buffer.append("�¶ȣ�\n");
				String max = tmp.getString("max");
				buffer.append("����¶�(���϶�)��"+max+"\n");
				String min = tmp.getString("min");
				buffer.append("����¶�(���϶�)��"+min+"\n");
				if(i == 0) {
					//���浱�յ���ߺ��������
					weatherInfo.put("maxTemp", max);
					weatherInfo.put("minTemp", min);
				}
				String vis = object.getString("vis");
				buffer.append("�ܼ���(km)��"+vis+"\n");
				
				JSONObject wind = object.getJSONObject("wind");
				buffer.append("����״����\n");
				String deg = wind.getString("deg");
				buffer.append("����(�Ƕ�)��"+deg+"\n");
				String dir = wind.getString("dir");
				buffer.append("����(����)��"+dir+"\n");
				String sc = wind.getString("sc");
				buffer.append("�����ȼ���"+sc+"\n");
				String spd = wind.getString("spd");
				buffer.append("����(Kmph)��"+spd+"\n");
			}
		} catch (JSONException e) {
			return;
		}		
		
	}
	
	/**
	 * ��ȡ����ָ��
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getSuggestion(JSONObject jsonObject) {
		JSONObject suggestion;
		try {
			suggestion = jsonObject.getJSONObject("suggestion");
			buffer.append("\n����ָ��*\n");
			JSONObject comf = suggestion.getJSONObject("comf");
			buffer.append("����ָ����\n");
			String brf = comf.getString("brf");
			buffer.append("��飺"+brf+"\n");
			String txt = comf.getString("txt");
			buffer.append("���飺"+txt+"\n");
			
			JSONObject cw = suggestion.getJSONObject("cw");
			buffer.append("ϴ��ָ����\n");
			String brf1 = cw.getString("brf");
			buffer.append("��飺"+brf1+"\n");
			String txt1 = cw.getString("txt");
			buffer.append("���飺"+txt1+"\n");
			
			JSONObject drsg = suggestion.getJSONObject("drsg");
			buffer.append("����ָ����\n");
			String brf2 = drsg.getString("brf");
			buffer.append("��飺"+brf2+"\n");
			String txt2 = drsg.getString("txt");
			buffer.append("���飺"+txt2+"\n");
			
			JSONObject flu = suggestion.getJSONObject("flu");
			buffer.append("��ðָ����\n");
			String brf3 = flu.getString("brf");
			buffer.append("��飺"+brf3+"\n");
			String txt3 = flu.getString("txt");
			buffer.append("���飺"+txt3+"\n");
			
			JSONObject sport = suggestion.getJSONObject("sport");
			buffer.append("�˶�ָ����\n");
			String brf4 = sport.getString("brf");
			buffer.append("��飺"+brf4+"\n");
			String txt4 = sport.getString("txt");
			buffer.append("���飺"+txt4+"\n");
			
			JSONObject trav = suggestion.getJSONObject("trav");
			buffer.append("����ָ����\n");
			String brf5 = trav.getString("brf");
			buffer.append("��飺"+brf5+"\n");
			String txt5 = trav.getString("txt");
			buffer.append("���飺"+txt5+"\n");
			
			JSONObject uv = suggestion.getJSONObject("uv");
			buffer.append("������ָ����\n");
			String brf6 = uv.getString("brf");
			buffer.append("��飺"+brf6+"\n");
			String txt6 = uv.getString("txt");
			buffer.append("���飺"+txt6+"\n");
		} catch (JSONException e) {
			return;
		}
	}
	
	
//	������Ϣ���������
//	{"city_info":[{"city":"���ӵ�","cnty":"�й�","id":"CN101310230","lat":"11.26","lon":"114.20","prov":"����"},
//	              {"city":"����","cnty":"�й�","id":"CN101010100","lat":"39.904000","lon":"116.391000","prov":"ֱϽ��"},
//	}
	
//	��������������������
//	{
//	    "HeWeather data service 3.0": [
//	        {
//	            "aqi": {
//	                "city": {
//	                    "aqi": "53",
//	                    "co": "0",
//	                    "no2": "16",
//	                    "o3": "73",
//	                    "pm10": "53",
//	                    "pm25": "9",
//	                    "qlty": "��",
//	                    "so2": "2"
//	                }
//	            },
//	            "basic": {
//	                "city": "����",
//	                "cnty": "�й�",
//	                "id": "CN101010100",
//	                "lat": "39.904000",
//	                "lon": "116.391000",
//	                "update": {
//	                    "loc": "2016-04-02 22:04",
//	                    "utc": "2016-04-02 14:04"
//	                }
//	            },
//	            "daily_forecast": [
//	                {
//	                    "astro": {
//	                        "sr": "05:56",
//	                        "ss": "18:39"
//	                    },
//	                    "cond": {
//	                        "code_d": "101",
//	                        "code_n": "104",
//	                        "txt_d": "����",
//	                        "txt_n": "��"
//	                    },
//	                    "date": "2016-04-02",
//	                    "hum": "7",
//	                    "pcpn": "0.0",
//	                    "pop": "0",
//	                    "pres": "1021",
//	                    "tmp": {
//	                        "max": "20",
//	                        "min": "8"
//	                    },
//	                    "vis": "10",
//	                    "wind": {
//	                        "deg": "324",
//	                        "dir": "����",
//	                        "sc": "3-4",
//	                        "spd": "15"
//	                    }
//	                },
//	                {
//	                    "astro": {
//	                        "sr": "05:54",
//	                        "ss": "18:40"
//	                    },
//	                    "cond": {
//	                        "code_d": "100",
//	                        "code_n": "100",
//	                        "txt_d": "��",
//	                        "txt_n": "��"
//	                    },
//	                    "date": "2016-04-03",
//	                    "hum": "9",
//	                    "pcpn": "0.0",
//	                    "pop": "1",
//	                    "pres": "1020",
//	                    "tmp": {
//	                        "max": "18",
//	                        "min": "6"
//	                    },
//	                    "vis": "10",
//	                    "wind": {
//	                        "deg": "203",
//	                        "dir": "�޳�������",
//	                        "sc": "΢��",
//	                        "spd": "3"
//	                    }
//	                },
//	                {
//	                    "astro": {
//	                        "sr": "05:53",
//	                        "ss": "18:41"
//	                    },
//	                    "cond": {
//	                        "code_d": "100",
//	                        "code_n": "100",
//	                        "txt_d": "��",
//	                        "txt_n": "��"
//	                    },
//	                    "date": "2016-04-04",
//	                    "hum": "18",
//	                    "pcpn": "0.0",
//	                    "pop": "13",
//	                    "pres": "1007",
//	                    "tmp": {
//	                        "max": "22",
//	                        "min": "8"
//	                    },
//	                    "vis": "10",
//	                    "wind": {
//	                        "deg": "231",
//	                        "dir": "�޳�������",
//	                        "sc": "΢��",
//	                        "spd": "0"
//	                    }
//	                },
//	                {
//	                    "astro": {
//	                        "sr": "05:51",
//	                        "ss": "18:42"
//	                    },
//	                    "cond": {
//	                        "code_d": "104",
//	                        "code_n": "104",
//	                        "txt_d": "��",
//	                        "txt_n": "��"
//	                    },
//	                    "date": "2016-04-05",
//	                    "hum": "15",
//	                    "pcpn": "0.0",
//	                    "pop": "0",
//	                    "pres": "1015",
//	                    "tmp": {
//	                        "max": "23",
//	                        "min": "10"
//	                    },
//	                    "vis": "10",
//	                    "wind": {
//	                        "deg": "126",
//	                        "dir": "�޳�������",
//	                        "sc": "΢��",
//	                        "spd": "9"
//	                    }
//	                },
//	                {
//	                    "astro": {
//	                        "sr": "05:49",
//	                        "ss": "18:43"
//	                    },
//	                    "cond": {
//	                        "code_d": "101",
//	                        "code_n": "100",
//	                        "txt_d": "����",
//	                        "txt_n": "��"
//	                    },
//	                    "date": "2016-04-06",
//	                    "hum": "16",
//	                    "pcpn": "0.0",
//	                    "pop": "1",
//	                    "pres": "1011",
//	                    "tmp": {
//	                        "max": "23",
//	                        "min": "10"
//	                    },
//	                    "vis": "10",
//	                    "wind": {
//	                        "deg": "180",
//	                        "dir": "�޳�������",
//	                        "sc": "΢��",
//	                        "spd": "1"
//	                    }
//	                },
//	                {
//	                    "astro": {
//	                        "sr": "05:48",
//	                        "ss": "18:44"
//	                    },
//	                    "cond": {
//	                        "code_d": "100",
//	                        "code_n": "100",
//	                        "txt_d": "��",
//	                        "txt_n": "��"
//	                    },
//	                    "date": "2016-04-07",
//	                    "hum": "8",
//	                    "pcpn": "0.0",
//	                    "pop": "0",
//	                    "pres": "1019",
//	                    "tmp": {
//	                        "max": "22",
//	                        "min": "7"
//	                    },
//	                    "vis": "10",
//	                    "wind": {
//	                        "deg": "330",
//	                        "dir": "�޳�������",
//	                        "sc": "΢��",
//	                        "spd": "2"
//	                    }
//	                },
//	                {
//	                    "astro": {
//	                        "sr": "05:46",
//	                        "ss": "18:45"
//	                    },
//	                    "cond": {
//	                        "code_d": "100",
//	                        "code_n": "100",
//	                        "txt_d": "��",
//	                        "txt_n": "��"
//	                    },
//	                    "date": "2016-04-08",
//	                    "hum": "9",
//	                    "pcpn": "0.0",
//	                    "pop": "0",
//	                    "pres": "1014",
//	                    "tmp": {
//	                        "max": "25",
//	                        "min": "10"
//	                    },
//	                    "vis": "10",
//	                    "wind": {
//	                        "deg": "206",
//	                        "dir": "�޳�������",
//	                        "sc": "΢��",
//	                        "spd": "9"
//	                    }
//	                }
//	            ],
//	            "hourly_forecast": [
//	                {
//	                    "date": "2016-04-02 22:00",
//	                    "hum": "10",
//	                    "pop": "0",
//	                    "pres": "1024",
//	                    "tmp": "14",
//	                    "wind": {
//	                        "deg": "118",
//	                        "dir": "���Ϸ�",
//	                        "sc": "΢��",
//	                        "spd": "12"
//	                    }
//	                }
//	            ],
//	            "now": {
//	                "cond": {
//	                    "code": "104",
//	                    "txt": "��"
//	                },
//	                "fl": "14",
//	                "hum": "9",
//	                "pcpn": "0",
//	                "pres": "1022",
//	                "tmp": "15",
//	                "vis": "10",
//	                "wind": {
//	                    "deg": "340",
//	                    "dir": "����",
//	                    "sc": "4-5",
//	                    "spd": "20"
//	                }
//	            },
//	            "status": "ok",
//	            "suggestion": {
//	                "comf": {
//	                    "brf": "����",
//	                    "txt": "���첻̫��Ҳ��̫�䣬�������������������������������£�Ӧ��е��Ƚ���ˬ�����ʡ�"
//	                },
//	                "cw": {
//	                    "brf": "������",
//	                    "txt": "������ϴ����δ��һ�����꣬������С����ϴһ�µ����������ܱ���һ�졣"
//	                },
//	                "drsg": {
//	                    "brf": "������",
//	                    "txt": "�����ű����ס�����ţ������ȷ�װ������������Ӧ�ʵ����������żп�������ë�µȡ�"
//	                },
//	                "flu": {
//	                    "brf": "���׷�",
//	                    "txt": "��������ҹ�²�ϴ󣬽��׷�����ð�����ʵ������·������ʽ�����������ע���ʵ�������"
//	                },
//	                "sport": {
//	                    "brf": "������",
//	                    "txt": "�����Ϻã������˶���ע���ɹ���Ƽ������������˶���"
//	                },
//	                "trav": {
//	                    "brf": "����",
//	                    "txt": "�����Ϻã��¶����ˣ��Ǹ�������Ŷ�������������������Σ������Ծ�������ܴ���Ȼ�ķ�⡣"
//	                },
//	                "uv": {
//	                    "brf": "�е�",
//	                    "txt": "���е�ǿ�������߷������������ʱ����Ϳ��SPF����15��PA+�ķ�ɹ����Ʒ����ñ�ӡ�̫������"
//	                }
//	            }
//	        }
//	    ]
//	}
	
}
