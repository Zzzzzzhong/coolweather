package com.coolweather.app.util;

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
	
	/**
	 * 解析服务器返回的数据，获取天气预报情况
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static String handleWeatherResponse(Context context, String response, String cityId, String cityName) {
		String result = null;
		if(!TextUtils.isEmpty(response)) {
			try {
				result = (String)parseJSON(response);
				saveWeatherInfo(context, result, cityId, cityName);
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
	 * 将服务器返回的天气信息保存到SharedPreference
	 * @param context
	 * @param weatherInfo
	 */
	public static void saveWeatherInfo(Context context, String weatherInfo, String cityId, String cityName) {
		SharedPreferences.Editor editor = 
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString("cityId", cityId); //保存城市ID
		editor.putString("cityName", cityName); //保存城市名称
		editor.putString("weatherInfo", weatherInfo); //保存天气情况
		editor.commit(); //提交保存
	}
	
	private static String parseJSON(String response) throws Exception {
		buffer = new StringBuilder();
		
		JSONObject dataObject = new JSONObject(response);
		JSONArray dataArray = dataObject.getJSONArray("HeWeather data service 3.0");
		JSONObject jsonObject = dataArray.getJSONObject(0);
		
		getStatus(jsonObject);
		getBasic(jsonObject);
		getAqi(jsonObject);
		getNow(jsonObject);
		getHourly(jsonObject);
		getDaily(jsonObject);				
		getSuggestion(jsonObject);
				
		return buffer.toString();		
	}
	
	/**
	 * 获取访问情况
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getStatus(JSONObject jsonObject) {
		String status;
		try {
			status = jsonObject.getString("status");
			if("ok".equals(status)) {
				buffer.append("访问情况：接口正常！\n");
			} else if("invalid key".equals(status)) {
				buffer.append("\n访问情况：错误的用户key！\n");
			} else if("no more requests".equals(status)) {
				buffer.append("\n访问情况：超过访问次数！\n");
			} else if("anr".equals(status)) {
				buffer.append("\n访问情况：服务无响应或超时！\n");
			} else if("permission denied".equals(status)) {
				buffer.append("\n访问情况：没有访问权限！\n");
			}
		} catch (JSONException e) {
			return;
		}
	}
	
	/**
	 * 获取城市基本信息
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getBasic(JSONObject jsonObject) {
		JSONObject basic;
		try {
			basic = jsonObject.getJSONObject("basic");
			buffer.append("\n城市基本信息\n");
			String city = basic.getString("city");
			buffer.append("城市名称："+city+"\n");
			String cnty = basic.getString("cnty");
			buffer.append("国家名称："+cnty+"\n");
			String lat = basic.getString("lat");
			buffer.append("纬度："+lat+"\n");
			String lon = basic.getString("lon");
			buffer.append("经度："+lon+"\n");
			JSONObject update = basic.getJSONObject("update");
			String loc = update.getString("loc");
			buffer.append("当地时间："+loc+"\n");	
		} catch (JSONException e) {
			return;
		}	
	}
	
	/**
	 * 获取空气质量指数*
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getAqi(JSONObject jsonObject) {
		JSONObject aqi1;
		try {
			aqi1 = jsonObject.getJSONObject("aqi");
			buffer.append("\n空气质量指数*\n");
			JSONObject city = aqi1.getJSONObject("city");
			String aqi = city.getString("aqi");
			buffer.append("空气质量指数："+aqi+"\n");
			String co = city.getString("co");
			buffer.append("一氧化碳1小时平均值(ug/m^3)："+co+"\n");
			String no2 = city.getString("no2");
			buffer.append("二氧化氮1小时平均值(ug/m^3)："+no2+"\n");
			String o3 = city.getString("o3");
			buffer.append("臭氧1小时平均值(ug/m^3)："+o3+"\n");
			String pm10 = city.getString("pm10");
			buffer.append("PM10 1小时平均值(ug/m^3)："+pm10+"\n");
			String pm25 = city.getString("pm25");
			buffer.append("PM2.5 1小时平均值(ug/m^3)："+pm25+"\n");
			String qlty = city.getString("qlty");
			buffer.append("空气质量类别："+qlty+"\n");
			String so2 = city.getString("so2");
			buffer.append("二氧化硫1小时平均值(ug/m^3)："+so2+"\n");
		} catch (JSONException e) {
			return;
		}
		
	}
	
	/**
	 * 获取每3小时天气预报
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getHourly(JSONObject jsonObject) {
		JSONArray hourly;
		try {
			hourly = jsonObject.getJSONArray("hourly_forecast");
			buffer.append("\nHourly_forecast天气预报\n");
			for(int i=0; i<hourly.length(); i++) {
				JSONObject object = hourly.getJSONObject(i);
				String date = object.getString("date");
				buffer.append("当地日期："+date+"\n");
				String hum = object.getString("hum");
				buffer.append("湿度(%)："+hum+"\n");
				String pop = object.getString("pop");
				buffer.append("降水概率(%)："+pop+"\n");
				String pres = object.getString("pres");
				buffer.append("气压："+pres+"\n");
				String tmp = object.getString("tmp");
				buffer.append("温度："+tmp+"\n");
				JSONObject wind = object.getJSONObject("wind");
				buffer.append("风力状况：\n");
				String deg = wind.getString("deg");
				buffer.append("风向(角度)："+deg+"\n");
				String dir = wind.getString("dir");
				buffer.append("风向(方向)："+dir+"\n");
				String sc = wind.getString("sc");
				buffer.append("风力等级："+sc+"\n");
				int spd = wind.getInt("spd");
				buffer.append("风速(Kmph)："+spd+"\n");
			}
		} catch (JSONException e) {
			return;
		}		
	}
	
	/**
	 * 获取未来6天天气预报
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getDaily(JSONObject jsonObject) {
		JSONArray daily;
		try {
			daily = jsonObject.getJSONArray("daily_forecast");
			buffer.append("\n未来6天天气预报\n");
			for(int i=0; i<daily.length(); i++) {					
				JSONObject object = daily.getJSONObject(i);
				JSONObject astro = object.getJSONObject("astro");
				buffer.append("天文数值：\n");
				String sr = astro.getString("sr");
				buffer.append("日出时间："+sr+"\n");
				String ss = astro.getString("ss");
				buffer.append("日落时间："+ss+"\n");
				JSONObject cond = object.getJSONObject("cond");
				buffer.append("天气状况："+cond+"\n");
				int code_d = cond.getInt("code_d");
				buffer.append("白天天气代码："+code_d+"\n");
				String txt_d = cond.getString("txt_d");
				buffer.append("白天天气描述："+txt_d+"\n");
				int code_n = cond.getInt("code_n");
				buffer.append("夜间天气代码："+code_n+"\n");
				String txt_n = cond.getString("txt_n");
				buffer.append("夜间天气描述："+txt_n+"\n");
				
				String date = object.getString("date");
				buffer.append("当地日期："+date+"\n");
				String hum = object.getString("hum");
				buffer.append("湿度(%)："+hum+"\n");
				String pcpn = object.getString("pcpn");
				buffer.append("降雨量(mm)："+pcpn+"\n");
				String pop = object.getString("pop");
				buffer.append("降水概率(%)："+pop+"\n");
				String pres = object.getString("pres");
				buffer.append("气压："+pres+"\n");
				
				JSONObject tmp = object.getJSONObject("tmp");
				buffer.append("温度：\n");
				String max = tmp.getString("max");
				buffer.append("最高温度(摄氏度)："+max+"\n");
				String min = tmp.getString("min");
				buffer.append("最低温度(摄氏度)："+min+"\n");
				
				String vis = object.getString("vis");
				buffer.append("能见度(km)："+vis+"\n");
				
				JSONObject wind = object.getJSONObject("wind");
				buffer.append("风力状况：\n");
				String deg = wind.getString("deg");
				buffer.append("风向(角度)："+deg+"\n");
				String dir = wind.getString("dir");
				buffer.append("风向(方向)："+dir+"\n");
				String sc = wind.getString("sc");
				buffer.append("风力等级："+sc+"\n");
				String spd = wind.getString("spd");
				buffer.append("风速(Kmph)："+spd+"\n");
			}
		} catch (JSONException e) {
			return;
		}		
		
	}
	
	/**
	 * 获取实况天气
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getNow(JSONObject jsonObject) {
		JSONObject now;
		try {
			now = jsonObject.getJSONObject("now");
			buffer.append("\n实况天气\n");
			JSONObject cond = now.getJSONObject("cond");
			buffer.append("天气状况：\n");
			int code = cond.getInt("code");
			buffer.append("天气代码："+code+"\n");
			String txt = cond.getString("txt");
			buffer.append("天气描述："+txt+"\n");
			String fl = now.getString("fl");
			buffer.append("体感温度："+fl+"\n");
			String hum = now.getString("hum");
			buffer.append("湿度(%)："+hum+"\n");
			String pcpn = now.getString("pcpn");
			buffer.append("降雨量(mm)："+pcpn+"\n");
			String pres = now.getString("pres");
			buffer.append("气压："+pres+"\n");
			String tmp = now.getString("tmp");
			buffer.append("温度："+tmp+"\n");
			String vis = now.getString("vis");
			buffer.append("能见度(km)："+vis+"\n");
			JSONObject wind = now.getJSONObject("wind");
			buffer.append("风力状况：\n");
			String deg = wind.getString("deg");
			buffer.append("风向(角度)："+deg+"\n");
			String dir = wind.getString("dir");
			buffer.append("风向(方向)："+dir+"\n");
			String sc = wind.getString("sc");
			buffer.append("风力等级："+sc+"\n");
			String spd = wind.getString("spd");
			buffer.append("风速(Kmph)："+spd+"\n");
		} catch (JSONException e) {
			return;
		}
	}
	
	/**
	 * 获取生活指数
	 * @param jsonObject
	 * @throws JSONException
	 */
	private static void getSuggestion(JSONObject jsonObject) {
		JSONObject suggestion;
		try {
			suggestion = jsonObject.getJSONObject("suggestion");
			buffer.append("\n生活指数*\n");
			JSONObject comf = suggestion.getJSONObject("comf");
			buffer.append("舒适指数：\n");
			String brf = comf.getString("brf");
			buffer.append("简介："+brf+"\n");
			String txt = comf.getString("txt");
			buffer.append("详情："+txt+"\n");
			
			JSONObject cw = suggestion.getJSONObject("cw");
			buffer.append("洗车指数：\n");
			String brf1 = cw.getString("brf");
			buffer.append("简介："+brf1+"\n");
			String txt1 = cw.getString("txt");
			buffer.append("详情："+txt1+"\n");
			
			JSONObject drsg = suggestion.getJSONObject("drsg");
			buffer.append("穿衣指数：\n");
			String brf2 = drsg.getString("brf");
			buffer.append("简介："+brf2+"\n");
			String txt2 = drsg.getString("txt");
			buffer.append("详情："+txt2+"\n");
			
			JSONObject flu = suggestion.getJSONObject("flu");
			buffer.append("感冒指数：\n");
			String brf3 = flu.getString("brf");
			buffer.append("简介："+brf3+"\n");
			String txt3 = flu.getString("txt");
			buffer.append("详情："+txt3+"\n");
			
			JSONObject sport = suggestion.getJSONObject("sport");
			buffer.append("运动指数：\n");
			String brf4 = sport.getString("brf");
			buffer.append("简介："+brf4+"\n");
			String txt4 = sport.getString("txt");
			buffer.append("详情："+txt4+"\n");
			
			JSONObject trav = suggestion.getJSONObject("trav");
			buffer.append("旅游指数：\n");
			String brf5 = trav.getString("brf");
			buffer.append("简介："+brf5+"\n");
			String txt5 = trav.getString("txt");
			buffer.append("详情："+txt5+"\n");
			
			JSONObject uv = suggestion.getJSONObject("uv");
			buffer.append("紫外线指数：\n");
			String brf6 = uv.getString("brf");
			buffer.append("简介："+brf6+"\n");
			String txt6 = uv.getString("txt");
			buffer.append("详情："+txt6+"\n");
		} catch (JSONException e) {
			return;
		}
	}
	
	
//	城市信息解析结果：
//	{"city_info":[{"city":"南子岛","cnty":"中国","id":"CN101310230","lat":"11.26","lon":"114.20","prov":"海南"},
//	              {"city":"北京","cnty":"中国","id":"CN101010100","lat":"39.904000","lon":"116.391000","prov":"直辖市"},
//	}
	
//	天气解析结果解析结果：
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
//	                    "qlty": "良",
//	                    "so2": "2"
//	                }
//	            },
//	            "basic": {
//	                "city": "北京",
//	                "cnty": "中国",
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
//	                        "txt_d": "多云",
//	                        "txt_n": "阴"
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
//	                        "dir": "北风",
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
//	                        "txt_d": "晴",
//	                        "txt_n": "晴"
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
//	                        "dir": "无持续风向",
//	                        "sc": "微风",
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
//	                        "txt_d": "晴",
//	                        "txt_n": "晴"
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
//	                        "dir": "无持续风向",
//	                        "sc": "微风",
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
//	                        "txt_d": "阴",
//	                        "txt_n": "阴"
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
//	                        "dir": "无持续风向",
//	                        "sc": "微风",
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
//	                        "txt_d": "多云",
//	                        "txt_n": "晴"
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
//	                        "dir": "无持续风向",
//	                        "sc": "微风",
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
//	                        "txt_d": "晴",
//	                        "txt_n": "晴"
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
//	                        "dir": "无持续风向",
//	                        "sc": "微风",
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
//	                        "txt_d": "晴",
//	                        "txt_n": "晴"
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
//	                        "dir": "无持续风向",
//	                        "sc": "微风",
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
//	                        "dir": "东南风",
//	                        "sc": "微风",
//	                        "spd": "12"
//	                    }
//	                }
//	            ],
//	            "now": {
//	                "cond": {
//	                    "code": "104",
//	                    "txt": "阴"
//	                },
//	                "fl": "14",
//	                "hum": "9",
//	                "pcpn": "0",
//	                "pres": "1022",
//	                "tmp": "15",
//	                "vis": "10",
//	                "wind": {
//	                    "deg": "340",
//	                    "dir": "北风",
//	                    "sc": "4-5",
//	                    "spd": "20"
//	                }
//	            },
//	            "status": "ok",
//	            "suggestion": {
//	                "comf": {
//	                    "brf": "舒适",
//	                    "txt": "白天不太热也不太冷，风力不大，相信您在这样的天气条件下，应会感到比较清爽和舒适。"
//	                },
//	                "cw": {
//	                    "brf": "较适宜",
//	                    "txt": "较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"
//	                },
//	                "drsg": {
//	                    "brf": "较舒适",
//	                    "txt": "建议着薄外套、开衫牛仔衫裤等服装。年老体弱者应适当添加衣物，宜着夹克衫、薄毛衣等。"
//	                },
//	                "flu": {
//	                    "brf": "较易发",
//	                    "txt": "天凉，昼夜温差较大，较易发生感冒，请适当增减衣服，体质较弱的朋友请注意适当防护。"
//	                },
//	                "sport": {
//	                    "brf": "较适宜",
//	                    "txt": "天气较好，户外运动请注意防晒。推荐您进行室内运动。"
//	                },
//	                "trav": {
//	                    "brf": "适宜",
//	                    "txt": "天气较好，温度适宜，是个好天气哦。这样的天气适宜旅游，您可以尽情地享受大自然的风光。"
//	                },
//	                "uv": {
//	                    "brf": "中等",
//	                    "txt": "属中等强度紫外线辐射天气，外出时建议涂擦SPF高于15、PA+的防晒护肤品，戴帽子、太阳镜。"
//	                }
//	            }
//	        }
//	    ]
//	}
	
}
