package com.altamob.ads.connect.model;

public class ResultAd extends ResultObject {
	private static final long serialVersionUID = -8061071737265001019L;
	private String id;
	private String appid;
	private String title;
	private String logo;
	private String platform;
	private String description;
	private String filesize;
	private String impression_url;
	private String click_url;
	private String min_os_version;
	private String bid;
	private Integer downloads;
	private String rating;
	private Creative creative;
	private String category;
	private String source;
	private String transaction_id;
	private String placement_id;

	private String week;
	private String display_style;
	private String like_count;
	private String banner_head;

	/**
	 * 大小
	 */
	protected String file_size;
	/**
	 * 开发者
	 */
	protected String author;

	public ResultAd() {
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilesize() {
		return filesize;
	}

	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	public String getImpression_url() {
		return impression_url;
	}

	public void setImpression_url(String impression_url) {
		this.impression_url = impression_url;
	}

	public String getClick_url() {
		return click_url;
	}

	public void setClick_url(String click_url) {
		this.click_url = click_url;
	}

	public String getMin_os_version() {
		return min_os_version;
	}

	public void setMin_os_version(String min_os_version) {
		this.min_os_version = min_os_version;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public Integer getDownloads() {
		return downloads;
	}

	public void setDownloads(Integer downloads) {
		this.downloads = downloads;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public Creative getCreative() {
		return creative;
	}

	public void setCreative(Creative creative) {
		this.creative = creative;
	}

	@Override
	public String toString() {
		return "Ad [id=" + id + ", appid=" + appid + ", title=" + title + ", logo=" + logo + ", platform=" + platform + ", description="
				+ description + ", filesize=" + filesize + ", impression_url=" + impression_url + ", click_url=" + click_url + ", min_os_version="
				+ min_os_version + ", bid=" + bid + ", downloads=" + downloads + ", rating=" + rating + ", category=" + category + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getPlacement_id() {
		return placement_id;
	}

	public void setPlacement_id(String placement_id) {
		this.placement_id = placement_id;
	}

	public String getDisplay_style() {
		return display_style;
	}

	public void setDisplay_style(String display_style) {
		this.display_style = display_style;
	}

	public String getLike_count() {
		return like_count;
	}

	public void setLike_count(String like_count) {
		this.like_count = like_count;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getBanner_head() {
		return banner_head;
	}

	public void setBanner_head(String banner_head) {
		this.banner_head = banner_head;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getFile_size() {
		return file_size;
	}

	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
