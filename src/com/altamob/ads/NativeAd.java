package com.altamob.ads;

public class NativeAd {
	/**
	 * NativeAd Bean
	 */
	private String adTitle;
	private AltaImage adCoverImage;
	private String adIcon;
	private String adBody;
	private String rating;
	private String clickUrl;
	private String impressionUrl;

	/**
	 * 大小
	 */
	private String fileSize;
	/**
	 * 开发者
	 */
	private String author;
	/**
	 * 用户点赞数量
	 */
	private String likeCount;
	/**
	 * App of the week
	 */
	private String week;

	/**
	 * 展示样式：game_worth_try | gennel | banner
	 */
	private String displayStyle;

	/**
	 * banner 头部图片
	 */
	private String banner_head;

	/**
	 * App类别
	 */
	private String category;

	public String getAdBody() {
		return adBody;
	}

	public String getAdTitle() {
		return adTitle;
	}

	public void setAdTitle(String adTitle) {
		this.adTitle = adTitle;
	}

	public void setAdBody(String adBody) {
		this.adBody = adBody;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getAdIcon() {
		return adIcon;
	}

	public void setAdIcon(String adIcon) {
		this.adIcon = adIcon;
	}

	public AltaImage getAdCoverImage() {
		return adCoverImage;
	}

	public void setAdCoverImage(AltaImage adCoverImage) {
		this.adCoverImage = adCoverImage;
	}

	public void setClickUrl(String clickUrl) {
		this.clickUrl = clickUrl;
	}

	public String getClickUrl() {
		return clickUrl;
	}

	public String getImpressionUrl() {
		return impressionUrl;
	}

	public void setImpressionUrl(String impressionUrl) {
		this.impressionUrl = impressionUrl;
	}

	public String getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(String likeCount) {
		this.likeCount = likeCount;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getDisplayStyle() {
		return displayStyle;
	}

	public void setDisplayStyle(String displayStyle) {
		this.displayStyle = displayStyle;
	}

	public String getBanner_head() {
		return banner_head;
	}

	public void setBanner_head(String banner_head) {
		this.banner_head = banner_head;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
