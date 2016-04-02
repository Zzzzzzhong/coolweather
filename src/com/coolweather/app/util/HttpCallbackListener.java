package com.coolweather.app.util;

public interface HttpCallbackListener {
	
	/** 请求返回结果后调用 */
	void onFinish(String response);
	
	/** 请求发生错误时调用 */
	void onError(Exception e);
}
