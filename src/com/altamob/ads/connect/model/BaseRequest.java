package com.altamob.ads.connect.model;

/**
 * Request请求的超类
 * 
 * @author haoyongfeng
 *
 */
public class BaseRequest {
	private transient String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
