package com.coolweather.app.model;

public class CityInfo {
	
	private int id;
	private String cityName;
	private String cityId;
	private String province;
	private String cnty;
	
	/** 设置id */
	public void setId(int id) {
		this.id = id;
	}
	/** 获取id */
	public int getId() {
		return id;
	}
	/** 设置城市名称 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	/** 获取城市名称 */
	public String getCityName() {
		return cityName;
	}
	/** 设置城市ID */
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	/** 获取城市ID */
	public String getCityId() {
		return cityId;
	}
	/** 设置省份名称 */
	public void setProvince(String province) {
		this.province = province;
	}
	/** 获取省份名称 */
	public String getProvince() {
		return province;
	}
	/** 设置国家名称 */
	public void setCnty(String cnty) {
		this.cnty = cnty;
	}
	/** 获取国家名称 */
	public String getCnty() {
		return cnty;
	}
}
