package com.altamob.ads.connect.model;

public class AdConfig {
	private String icon;
	private String bgImg;
	private String title;
	private String description;
	private double rating;
	private BtnInfo btnInfo;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getBgImg() {
		return bgImg;
	}

	public void setBgImg(String bgImg) {
		this.bgImg = bgImg;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public BtnInfo getBtnInfo() {
		return btnInfo;
	}

	public void setBtnInfo(BtnInfo btnInfo) {
		this.btnInfo = btnInfo;
	}

}
