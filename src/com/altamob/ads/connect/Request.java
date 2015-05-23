package com.altamob.ads.connect;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;
import android.util.Log;

import com.altamob.ads.connect.config.Config;
import com.altamob.ads.connect.config.ConfigFactory;
import com.altamob.ads.connect.model.Filter;
import com.altamob.ads.connect.model.GetAdRequest;
import com.altamob.ads.connect.request.CallBack;
import com.altamob.ads.connect.request.RequestAsyncTask;
import com.altamob.ads.connect.request.SDKContext;
import com.altamob.ads.connect.util.HttpUtil;

/**
 * 发起SDK请求类
 * 
 * @author yongfeng.hao
 *
 */
public class Request {
	private static final String LOG_TAG = Request.class.getSimpleName();
	private static Config config;
	private static String SERVICE_DOMAIN;
	private static String GET_AD_APIPATH;
	private static Integer POST_BACK_RETRY_COUNT;

	/**
	 * 标记使用SDK的App
	 */
	private static volatile String token = "ea32be33-e3b7-411a-a493-0e8a3fc79317";

	private String placementId;
	/**
	 * 用来计算SDK调用频率的秒表
	 */
	Executor executor = Executors.newCachedThreadPool();

	public Request(Context context, String placementId) {
		this.placementId = placementId;
		initConfig();
	}

	private void initConfig() {
		config = ConfigFactory.getConfig();
		SERVICE_DOMAIN = config.readString("SERVICE_DOMAIN", "http://sandbox.sdk.admobclick.com/");
		GET_AD_APIPATH = config.readString("GET_AD_APIPATH", "v1/promote/ads");
		POST_BACK_RETRY_COUNT = config.readInteger("POST_BACK_RETRY_COUNT", 3);
		// Log.i("SERVICE_DOMAIN", SERVICE_DOMAIN);
		// Log.i("GET_AD_APIPATH", GET_AD_APIPATH);
		// Log.i("POST_BACK_RETRY_COUNT",
		// String.valueOf(POST_BACK_RETRY_COUNT));
	}

	public RequestAsyncTask createRequestTask(int width, int height, int requestCount, String template, CallBack callBack) {
		GetAdRequest request = getBaseAdReuqet();
		request.setLimit(requestCount);
		request.setTemplate(template);
		setRequestFileter(width, height, request);
		return new RequestAsyncTask(SERVICE_DOMAIN + GET_AD_APIPATH, request, callBack);
	}

	/**
	 * 异步请求banner广告
	 * 
	 * @param callBack
	 *            回调函数
	 * @return
	 */
	public RequestAsyncTask createRequestTask(int width, int height, CallBack callBack) {
		GetAdRequest request = getBaseAdReuqet();
		setRequestFileter(width, height, request);
		return new RequestAsyncTask(SERVICE_DOMAIN + GET_AD_APIPATH, request, callBack);
	}

	private void setRequestFileter(int width, int height, GetAdRequest request) {
		Filter filter = request.getFilter();
		filter.setAd_id("");
		filter.setAndroid_id(android.os.Build.ID);
		filter.setCountry(Locale.getDefault().getCountry());
		filter.setDevice(android.os.Build.MODEL);
		filter.setLanguage(Locale.getDefault().getLanguage());
		filter.setPkg(SDKContext.context.getApplicationInfo().packageName);
		filter.setPlacement(placementId);
		filter.setPlatform("Android");
		filter.setIp(SDKContext.getIp());
		filter.setTags(SDKContext.getTags());
		filter.setVersion(android.os.Build.VERSION.RELEASE);
		filter.setHeight(height);
		filter.setWidth(width);
		filter.setRooted(isRooted());
		Log.i("is rooted", String.valueOf(isRooted()));
	}

	/**
	 * 显示回调
	 */
	public static void postBack(final String postBackUrl) {
		// Log.i("PostBack", postBackUrl);
		Executors.newFixedThreadPool(3).execute(new Runnable() {
			@Override
			public void run() {
				HttpUtil.postBack(postBackUrl, POST_BACK_RETRY_COUNT);
			}
		});

	}

	private GetAdRequest getBaseAdReuqet() {
		GetAdRequest request = new GetAdRequest();
		request.setToken(token);
		request.setFilter(new Filter());
		return request;
	}

	private boolean isRooted() {
		return findBinary("su");
	}

	private boolean findBinary(String binaryName) {
		boolean found = false;
		if (!found) {
			String[] places = { "/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/",
					"/system/bin/failsafe/", "/data/local/" };
			for (String where : places) {
				if (new File(where + binaryName).exists()) {
					found = true;
					break;
				}
			}
		}
		return found;
	}
}
