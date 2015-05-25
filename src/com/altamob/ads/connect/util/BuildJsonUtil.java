package com.altamob.ads.connect.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.altamob.ads.Comment;
import com.altamob.ads.connect.model.AdConfig;
import com.altamob.ads.connect.model.BtnInfo;
import com.altamob.ads.connect.model.Creative;
import com.altamob.ads.connect.model.Filter;
import com.altamob.ads.connect.model.GetAdRequest;
import com.altamob.ads.connect.model.ResultAd;
import com.altamob.ads.connect.model.ResultObject;
import com.altamob.ads.connect.model.UploadAppListRequest;

public class BuildJsonUtil {
	private static final String LOG_TAG = "Json format error";

	public static ResultObject getResultObject(String resultJson) {
		ResultObject resultObject = new ResultObject();
		try {
			JSONObject jsonResultObject = new JSONObject(resultJson);
			if (jsonResultObject.isNull("ads")) {
				Log.e(LOG_TAG, "the json format invalid <ads> is null");
				return null;
			}
			JSONArray ads = jsonResultObject.getJSONArray("ads");
			List<ResultAd> resultAds = new ArrayList<ResultAd>();
			for (int i = 0; i < ads.length(); i++) {
				try {
					JSONObject result = ads.getJSONObject(i);
					ResultAd resultAd = new ResultAd();
					if (result.has("template"))
						resultAd.setTemplate(convertJson2String(result.getString("template")));
					if (result.has("click_url"))
						resultAd.setClick_url(convertJson2String(result.getString("click_url")));
					if (result.has("logo"))
						resultAd.setLogo(convertJson2String(result.getString("logo")));
					if (result.has("impression_url"))
						resultAd.setImpression_url(convertJson2String(result.getString("impression_url")));
					if (result.has("description"))
						resultAd.setDescription(convertJson2String(result.getString("description")));
					if (result.has("rating") && !result.getString("rating").equals("null"))
						resultAd.setRating(convertJson2String(result.getString("rating")));
					if (result.has("title"))
						resultAd.setTitle(convertJson2String(result.getString("title")));
					if (result.has("group_name"))
						resultAd.setGroup_name(convertJson2String(result.getString("group_name")));
					if (result.has("favor_count"))
						resultAd.setFavor_count(convertJson2String(result.getString("favor_count")));
					if (result.has("banner_head"))
						resultAd.setBanner_head(convertJson2String(result.getString("banner_head")));
					if (result.has("file_size"))
						resultAd.setFile_size(convertJson2String(result.getString("file_size")));
					if (result.has("developer"))
						resultAd.setDeveloper(convertJson2String(result.getString("developer")));
					if (result.has("category"))
						resultAd.setCategory(result.getJSONObject("category").getString("name"));
					if (result.has("creatives")) {
						Creative creative = new Creative();
						List<String> thumbnailArray = new ArrayList<String>();
						JSONArray creativesJsonArray = result.getJSONArray("creatives");
						for (int z = 0; z < creativesJsonArray.length(); z++) {
							JSONObject creativeJson = creativesJsonArray.getJSONObject(z);
							if (creativeJson.getString("type").equals("banner")) {
								if (creativeJson.has("height"))
									creative.setHeight(creativeJson.getInt("height"));
								if (creativeJson.has("width"))
									creative.setWidth(creativeJson.getInt("width"));
								creative.setUrl(convertJson2String(creativeJson.getString("url")));
							} else if (creativeJson.getString("type").equals("thumbnail")) {
								thumbnailArray.add(creativeJson.getString("url"));
							}
						}
						resultAd.setCreative(creative);
						resultAd.setThumbnailList(thumbnailArray.toArray(new String[] {}));
					}
					if (result.has("comments")) {
						JSONArray comments = result.getJSONArray("comments");
						List<Comment> commentsList = new ArrayList<Comment>();
						for (int j = 0; j < comments.length(); j++) {
							Comment comment = new Comment();
							JSONObject commentJson = comments.getJSONObject(j);
							comment.setContent(commentJson.getString("content"));
							comment.setHeadIcon(commentJson.getString("user_icon"));
							if (commentJson.has("rating") && !commentJson.getString("rating").equals("null"))
								comment.setRating(Float.valueOf(commentJson.getString("rating")));
							comment.setUsername(commentJson.getString("user_name"));
							commentsList.add(comment);
						}
						resultAd.setComments(commentsList);
					}
					resultAds.add(resultAd);
				} catch (Exception e) {
					Log.e(LOG_TAG, e.toString());
					continue;
				}
			}
			resultObject.setResult(resultAds);
		} catch (JSONException e) {
			Log.e(LOG_TAG, e.toString());
		}
		return resultObject;
	}

	public static String buildAdUnitConfig(AdConfig adConfig) {
		StringBuffer result = new StringBuffer();
		result.append("{");
		result.append(getWrapKv("icon", adConfig.getIcon()));
		result.append(getWrapKv("bgImg", adConfig.getBgImg()));
		result.append(getWrapKv("title", adConfig.getTitle()));
		result.append(getWrapKv("description", adConfig.getDescription()));
		result.append(getWrapKv("rating", adConfig.getRating()));
		BtnInfo btnInfo = adConfig.getBtnInfo();
		result.append(wrapStr("btnInfo")).append(":{");
		result.append(getWrapKv("title", btnInfo.getTitle()));
		result.append(getWrapKv("href", btnInfo.getHref()));
		// result.append(getWrapKv("width", btnInfo.getWidth()));
		result.append(getWrapKv("bgc", btnInfo.getBgc()));
		result.append(getWrapKv("ftColor", btnInfo.getFtColor()).replace(",", ""));
		result.append("}");
		result.append("}");
		return result.toString();
	}

	public static String buridAdRequestJson(GetAdRequest request) {
		StringBuffer result = new StringBuffer();
		result.append("{");
		result.append(wrapStr("params")).append(":{");
		result.append(wrapStr("filter")).append(":{");
		Filter filter = request.getFilter();
		result.append(getWrapKv("ad_id", filter.getAd_id()));
		result.append(getWrapKv("country", filter.getCountry()));
		result.append(getWrapKv("device", filter.getDevice()));
		result.append(getWrapKv("android_id", filter.getAndroid_id()));
		result.append(getWrapKv("version", filter.getVersion()));
		result.append(getWrapKv("language", filter.getLanguage()));
		result.append(getWrapKv("placement", filter.getPlacement()));
		result.append(getWrapKv("platform", filter.getPlatform()));
		result.append(getWrapKv("pkg", filter.getPkg()));
		result.append(getWrapKv("ip", filter.getIp()));
		result.append(wrapStr("tags")).append(":").append(filter.getTags()).append(",");
		result.append(getWrapKv("height", filter.getHeight()));
		result.append(getWrapKv("width", filter.getWidth()).replace(",", ""));
		result.append("},");
		result.append(getWrapKv("limit", request.getLimit()).replace(",", ""));
		result.append("}");
		result.append("}");
		return result.toString();
	}

	public static String buildUploadRequest(UploadAppListRequest uploadAppListRequest) {
		StringBuffer result = new StringBuffer();
		result.append("{");
		result.append(wrapStr("params")).append(":{");
		result.append(wrapStr("device")).append(":[{");
		result.append(getWrapKv("android_id", uploadAppListRequest.getDevice().getAndroid_id()));
		result.append(getWrapKv("installed_packages", uploadAppListRequest.getDevice().getInstalled_packages()).replace(",", ""));
		result.append("}]");
		result.append("}");
		result.append("}");
		return result.toString();
	}

	/**
	 * 
	 * @param wrapStr
	 * @param obj
	 * @return "wrapStr":value
	 */
	private static String getWrapKv(String wrapStr, Object obj) {
		String str = "null";
		if (obj == null)
			return wrapStr(wrapStr) + ":" + str + ",";
		if (obj instanceof String[]) {
			String[] array = (String[]) obj;
			if (array != null && array.length > 0)
				str = Arrays.toString(array);
		} else
			str = obj.toString();
		return wrapStr(wrapStr).append(":").append(null == obj ? "null" : wrapStr(str)).append(",").toString();
	}

	/**
	 * 
	 * @param ori
	 * @return "ori"
	 * 
	 */
	private static StringBuffer wrapStr(String ori) {
		return new StringBuffer("\"" + convertJson2String(ori) + "\"");
	}

	private static String convertJson2String(String json) {
		if (!StringUtils.isEmpty(json) && !json.equals("null"))
			return json;
		return null;
	}

}
