package com.coolweather.app.model;

public class CityInfo {
	
	private int id;
	private String cityName;
	private String cityId;
	private String province;
	private String cnty;
	
	/** ����id */
	public void setId(int id) {
		this.id = id;
	}
	/** ��ȡid */
	public int getId() {
		return id;
	}
	/** ���ó������� */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	/** ��ȡ�������� */
	public String getCityName() {
		return cityName;
	}
	/** ���ó���ID */
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	/** ��ȡ����ID */
	public String getCityId() {
		return cityId;
	}
	/** ����ʡ������ */
	public void setProvince(String province) {
		this.province = province;
	}
	/** ��ȡʡ������ */
	public String getProvince() {
		return province;
	}
	/** ���ù������� */
	public void setCnty(String cnty) {
		this.cnty = cnty;
	}
	/** ��ȡ�������� */
	public String getCnty() {
		return cnty;
	}
}
