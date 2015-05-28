package com.altamob.ads.connect.model;

import java.util.List;

import com.altamob.ads.Comment;

public class ResultAd extends ResultObject {
	private static final long serialVersionUID = -8061071737265001019L;
	private String id;
	private String appid;
	private String title;
	private String logo;
	private String platform;
	private String description;
	private String impression_url;
	private String click_url;
	private String min_os_version;
	private Integer downloads;
	private String rating;
	private Creative creative;
	private String category;
	private String template;
	private String group_name;
	private String favor_count;
	private String banner_head;
	private List<Comment> comments;
	private String[] thumbnailList;
	private int est_loading;
	
	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public String[] getThumbnailList() {
		return thumbnailList;
	}

	public void setThumbnailList(String[] thumbnailList) {
		this.thumbnailList = thumbnailList;
	}

	/**
	 * 大小
	 */
	protected String file_size;
	/**
	 * 开发者
	 */
	protected String developer;

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
				+ description + ", filesize=" + file_size + ", impression_url=" + impression_url + ", click_url=" + click_url
				+ ", min_os_version=" + min_os_version + ", downloads=" + downloads + ", rating=" + rating + ", category=" + category + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBanner_head() {
		return banner_head;
	}

	public void setBanner_head(String banner_head) {
		this.banner_head = banner_head;
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

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public String getFavor_count() {
		return favor_count;
	}

	public void setFavor_count(String favor_count) {
		this.favor_count = favor_count;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public int getEst_loading() {
		return est_loading;
	}

	public void setEst_loading(int est_loading) {
		this.est_loading = est_loading;
	}

}
