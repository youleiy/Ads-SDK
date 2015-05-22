package com.altamob.ads.connect.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

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
					if (result.has("display_style"))
						resultAd.setDisplay_style(convertJson2String(result.getString("display_style")));
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
					if (result.has("transaction_id"))
						resultAd.setTransaction_id(convertJson2String(result.getString("transaction_id")));
					if (result.has("placement_id"))
						resultAd.setPlacement_id(convertJson2String(result.getString("placement_id")));
					if (result.has("source"))
						resultAd.setSource(convertJson2String(result.getString("source")));
					if (result.has("bid") && !result.getString("bid").equals("null"))
						resultAd.setBid(convertJson2String(result.getString("bid")));
					if (result.has("week"))
						resultAd.setWeek(convertJson2String(result.getString("week")));
					if (result.has("like_count"))
						resultAd.setLike_count(convertJson2String(result.getString("like_count")));
					if (result.has("banner_head"))
						resultAd.setBanner_head(convertJson2String(result.getString("banner_head")));
					if (result.has("filesize"))
						resultAd.setFile_size(convertJson2String(result.getString("filesize")));
					if (result.has("author"))
						resultAd.setAuthor(convertJson2String(result.getString("author")));
					if (result.has("category"))
						resultAd.setCategory(result.getJSONArray("category").get(0).toString());
					if (result.has("creative")) {
						JSONObject createJsonObj = result.getJSONObject("creative");
						Creative creative = new Creative();
						creative.setHeight(createJsonObj.getInt("height"));
						creative.setWidth(createJsonObj.getInt("width"));
						creative.setUrl(convertJson2String(createJsonObj.getString("url")));
						resultAd.setCreative(creative);
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
