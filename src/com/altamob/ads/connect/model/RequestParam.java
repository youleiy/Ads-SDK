package com.altamob.ads.connect.model;

public class RequestParam {

	private BaseRequest params;

	public RequestParam(BaseRequest param) {
		this.params = param;
	}

	public BaseRequest getParams() {
		return params;
	}

	public void setParams(BaseRequest params) {
		this.params = params;
	}

}
