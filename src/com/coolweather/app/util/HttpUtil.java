package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
	/**
	 * ����Http����
	 * @param address �������ַ
	 * @param listener �ص���������Ӧ����Ľӿ�
	 */
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection httpConn = null;
				try {
					URL url = new URL(address);
					httpConn = (HttpURLConnection) url.openConnection();
					httpConn.setRequestMethod("GET"); //����ʽΪGET
					httpConn.setConnectTimeout(8 * 1000); //�������ӳ�ʱΪ8s
					httpConn.setReadTimeout(8 * 1000); //���ö�ȡ��ʱΪ8s
					InputStream inStream = httpConn.getInputStream(); //��ȡ���ӵ�������
					BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
					StringBuilder response = new StringBuilder(); //����������Ӧ���
					String line = null; //����ÿһ�е�����
					while((line = reader.readLine()) != null) {
						response.append(line);
					}
					if(listener != null) {
						//�ص�onFinish()�����������ȡ������Ӧ���
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if(listener != null) {
						//�ص�onError()����
						listener.onError(e);
					}
				} finally {
					if(httpConn != null) {
						httpConn.disconnect(); //�Ͽ�����
					}
				}
			}
			
		}).start();
	}
}
