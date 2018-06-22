package com.xiyu.schedulix.api.util;

import org.json.JSONException;

import org.json.JSONObject;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component 
public class GetActiveMqSize {


	//private static  String url="http://172.31.1.67:8161/api/jolokia/read/org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=wenjian_id/QueueSize";
	private static  String url="http://192.168.1.22:8161/api/jolokia/read/org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=wenjian_id/QueueSize";
	private static  String user="admin";
	private static  String pwd="admin";

	public static Integer getMqSize() throws JSONException {

		RestTemplate restT = new RestTemplate();
		restT.getInterceptors().add(new BasicAuthorizationInterceptor(user, pwd));
		String quoteString = restT.getForObject(url, String.class);
		JSONObject js = new JSONObject(quoteString);
		// System.out.println(js);
		// System.out.println(js.get("value"));
		return (Integer) js.get("value");
	}
}
