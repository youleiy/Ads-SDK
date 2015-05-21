package com.altamob.ads.connect.model;

import java.io.Serializable;
import java.util.List;

public class ResultObject implements Serializable {

	/**
	 * 请求结果类
	 */
	private static final long serialVersionUID = 1L;

	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	public static final String FILED = "false";
	private String pagging;
	private String status;
	private String error;
	private List<ResultAd> result;
	private String postBackColoums;

	public String getPagging() {
		return pagging;
	}

	public void setPagging(String pagging) {
		this.pagging = pagging;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ResultAd> getResult() {
		return result;
	}

	public void setResult(List<ResultAd> result) {
		this.result = result;
	}

	public String getPostBackColoums() {
		return postBackColoums;
	}

	public void setPostBackColoums(String postBackColoums) {
		this.postBackColoums = postBackColoums;
	}

}