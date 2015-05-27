package com.altamob.ads;

import java.io.Serializable;

public class NativeAd implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	private String developer;
	/**
	 * 用户点赞数量
	 */
	private String favorCount;
	/**
	 * App of the week
	 */
	private String groupName;

	/**
	 * 展示样式：game_worth_try | gennel | banner
	 */
	private String template;

	/**
	 * banner 头部图片
	 */
	private String banner_head;

	/**
	 * App类别
	 */
	private String category;

	/**
	 * 评论列表
	 */
	private Comment[] comments;

	/**
	 * 缩略图列表
	 */
	private String[] thumbnailList;

	public Comment[] getComments() {
		return comments;
	}

	public void setComments(Comment[] comments) {
		this.comments = comments;
	}

	public String[] getThumbnailList() {
		return thumbnailList;
	}

	public void setThumbnailList(String[] thumbnailList) {
		this.thumbnailList = thumbnailList;
	}

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFavorCount() {
		return favorCount;
	}

	public void setFavorCount(String favorCount) {
		this.favorCount = favorCount;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}
}
