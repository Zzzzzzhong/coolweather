package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
	/**
	 * 发送Http请求
	 * @param address 请求的网址
	 * @param listener 回调服务器响应结果的接口
	 */
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection httpConn = null;
				try {
					URL url = new URL(address);
					httpConn = (HttpURLConnection) url.openConnection();
					httpConn.setRequestMethod("GET"); //请求方式为GET
					httpConn.setConnectTimeout(8 * 1000); //设置连接超时为8s
					httpConn.setReadTimeout(8 * 1000); //设置读取超时为8s
					InputStream inStream = httpConn.getInputStream(); //获取连接的输入流
					BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
					StringBuilder response = new StringBuilder(); //用来保存响应结果
					String line = null; //保存每一行的数据
					while((line = reader.readLine()) != null) {
						response.append(line);
					}
					if(listener != null) {
						//回调onFinish()方法。保存读取到的响应结果
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if(listener != null) {
						//回调onError()方法
						listener.onError(e);
					}
				} finally {
					if(httpConn != null) {
						httpConn.disconnect(); //断开连接
					}
				}
			}
			
		}).start();
	}
}
