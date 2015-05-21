package com.altamob.ads.connect.model;

import java.io.Serializable;

/**
 * 创意
 * 
 * @author 杜鹏程 An array of creative images for a campaign. Populated only when
 *         request parameter "expand=creatives" is used (this field will be null
 *         if the request parameter is not present)
 *
 */
public class Creative  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5528414394594891422L;
	/**
	 * 创意宽 width - the width of the image
	 */
	private int width;
	/**
	 * 创意高 height - the height of the image
	 */
	private int height;
	/**
	 * 图片素材地址 url - the image URL
	 */
	private String url;

	/**
	 * languageCode - the two-character language code
	 */
	private String languageCode;
	/**
	 * 缩略图 thumbnail - boolean value: true if this is a thumbnail image
	 */
	private String thumbnail;


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	/*
	 * 获取宽高尺寸
	 */
	public String getWH() {
		return width + "*" + height;
	}

	@Override
	public String toString() {
		return "Creative [width=" + width + ", height=" + height + ", url=" + url + ", languageCode=" + languageCode + ", thumbnail=" + thumbnail
				+ "]";
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
