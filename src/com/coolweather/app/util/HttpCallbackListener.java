package com.coolweather.app.util;

public interface HttpCallbackListener {
	
	/** ���󷵻ؽ������� */
	void onFinish(String response);
	
	/** ����������ʱ���� */
	void onError(Exception e);
}
