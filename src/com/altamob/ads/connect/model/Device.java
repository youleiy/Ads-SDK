package com.altamob.ads.connect.model;

public class Device {
	private String android_id;
	private String[] installed_packages;

	public String getAndroid_id() {
		return android_id;
	}

	public void setAndroid_id(String android_id) {
		this.android_id = android_id;
	}

	public String[] getInstalled_packages() {
		return installed_packages;
	}

	public void setInstalled_packages(String[] installed_packages) {
		this.installed_packages = installed_packages;
	}

}
