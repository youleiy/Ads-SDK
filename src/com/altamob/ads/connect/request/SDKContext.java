package com.altamob.ads.connect.request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.altamob.ads.connect.config.Config;
import com.altamob.ads.connect.config.ConfigFactory;
import com.altamob.ads.connect.model.Device;
import com.altamob.ads.connect.model.UploadAppListRequest;
import com.altamob.ads.connect.util.BuildJsonUtil;
import com.altamob.ads.connect.util.HttpUtil;
import com.altamob.ads.connect.util.StringUtils;
import com.altamob.ads.view.Ad_Type;

/**
 * 初始化 Context 提供sharedPrefresce和一些公用方法 每次初始化控件时进行初始化,后台更新配置以及上传tags
 * 
 * @author yf.hao
 *
 */
public class SDKContext {
	private static final String LOG_TAG = SDKContext.class.getSimpleName();
	private static final String SHAREDPREFERENCENAME = "Altamob_SDK";
	public static SharedPreferences sharedPreferences;
	public static Context context;

	/**
	 * 间隔发送本机应用列表时间 7days
	 */
	private static Integer ONE_DAY_TIME;
	private static Integer SEND_TAGS_INTERVAL_TIME;
	private static Integer UPDATE_CONFIG_INTERVAL_TIME;
	private static String UPLOAD_DEVICEINFO;
	private static double REQUEST_FAN_RATE;
	private static final String LAST_SEND_TIME_STR = "LAST_SEND_TAGS_TIME";
	private static final String LAST_UPDATE_CONFIG_TIME_STR = "LAST_UPDATE_CONFIG_TIME";
	private static final String LAST_UPLOAD_ERROR_TIME_STR = "LAST_UPLOAD_ERROR_TIME";
	private static final String REQUEST_FAN_RATE_STR = "REQUEST_FAN_RATE";
	private static final String LOCAL_TAGS = "LOCAL_TAGS";
	private static final String GET_IP_URL = "http://ifconfig.sh";
	private static String SERVICE_DOMAIN;
	private static Config config;
	private static String ip;
	// 预装应用列表
	private static List<String> post_installs = new ArrayList<String>();
	// 用户安装应用列表
	private static List<String> pre_installs = new ArrayList<String>();

	public static void init(Context context) {
		SDKContext.context = context;
		sharedPreferences = context.getSharedPreferences(SHAREDPREFERENCENAME, Context.MODE_PRIVATE);
		initConfig();
		uploadAppList();
		updateConfigInbackGround();
	}

	private static void initConfig() {
		config = ConfigFactory.getConfig();
		SERVICE_DOMAIN = config.readString("SERVICE_DOMAIN", "http://10.200.10.220:8989/");
		UPLOAD_DEVICEINFO = config.readString("UPLOAD_APIPATH", "alta-adserver/api/recommend/offer/tag.json");
		/**
		 * 间隔发送本机应用列表时间 7days
		 */
		ONE_DAY_TIME = config.readInteger("SEND_ERROR_INTERVAL_TIME", 24 * 60 * 60 * 1000);
		SEND_TAGS_INTERVAL_TIME = config.readInteger("SEND_INTERVAL_TIME", 7 * 24 * 60 * 60 * 1000);
		/**
		 * 更新配置时间 1hours
		 */
		UPDATE_CONFIG_INTERVAL_TIME = config.readInteger("UPDATE_CONFIG_INTERVAL_TIME", 1 * 60 * 60 * 1000);
		REQUEST_FAN_RATE = config.readDouble(REQUEST_FAN_RATE_STR, 0.8);
	}

	public static SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}

	/**
	 * 上传设备应用列表至服务器
	 */
	private static void uploadAppList() {
		Long last_send_tag_time = sharedPreferences.getLong(LAST_SEND_TIME_STR, 0L);
		// 每七天上传一次applist
		if (System.currentTimeMillis() - last_send_tag_time > SEND_TAGS_INTERVAL_TIME) {
			initInstallApp();
			final UploadAppListRequest uploadAppListRequest = new UploadAppListRequest();
			Device device = new Device();
			device.setAndroid_id(android.os.Build.ID);
			post_installs.addAll(pre_installs);
			device.setInstalled_packages(post_installs.toArray(new String[] {}));
			uploadAppListRequest.setDevice(device);
			Executors.newCachedThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					try {
						// 保存Ip到本地
						sharedPreferences.edit().putString("IP", HttpUtil.getConfig(GET_IP_URL));
						// 如果上传失败则等到24小时候再上传
						if (System.currentTimeMillis() - sharedPreferences.getLong(LAST_UPLOAD_ERROR_TIME_STR, 0) > ONE_DAY_TIME) {
							String result = HttpUtil.httpPost(SERVICE_DOMAIN + UPLOAD_DEVICEINFO,
									BuildJsonUtil.buildUploadRequest(uploadAppListRequest), null, 30000);
							JSONObject jsonObject = new JSONObject(result);
							if (!jsonObject.isNull("error") && StringUtils.isEmpty(jsonObject.getString("error"))) {
								sharedPreferences.edit().putLong(LAST_SEND_TIME_STR, System.currentTimeMillis()).commit();
								JSONObject resultObj = new JSONObject(jsonObject.getString("tag"));
								String tags = resultObj.getString("definedTags");
								sharedPreferences.edit().putString(LOCAL_TAGS, tags).commit();
							} else {
								Log.e(LOG_TAG, jsonObject.toString());
								sharedPreferences.edit().putLong(LAST_UPLOAD_ERROR_TIME_STR, System.currentTimeMillis()).commit();
							}
						}
					} catch (Exception e) {
						Log.e(LOG_TAG, e.toString());
					}
				}
			});
		}
	}

	public static String getIp() {
		ip = sharedPreferences.getString("IP", "");
		if (StringUtils.isEmpty(ip)) {
			Executors.newCachedThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					ip = HttpUtil.getConfig(GET_IP_URL);
					sharedPreferences.edit().putString("IP", ip.replace("\n", "")).commit();
				}
			});
		}
		return ip.replace("\n", "");
	}

	/**
	 * 获取存储在本地的tags
	 * 
	 * @return
	 */
	public static String getTags() {
		if (!StringUtils.isEmpty(sharedPreferences.getString(LOCAL_TAGS, null)))
			return sharedPreferences.getString(LOCAL_TAGS, null);
		return null;
	}

	/**
	 * 根据比率随机获取要展示的广告(FAN/ALTAMOB)
	 */
	public static Ad_Type getRandomAdTypeByRate() {
		double randomNumber;
		randomNumber = Math.random();
		if (randomNumber >= 0 && randomNumber <= REQUEST_FAN_RATE) {
			return Ad_Type.FAN;
		} else {
			return Ad_Type.ALTAMOB;
		}
	}

	/**
	 * 后台更新配置
	 * 
	 * @param last_update_config_time
	 */
	private static void updateConfigInbackGround() {
		Long last_update_config_time = sharedPreferences.getLong(LAST_UPDATE_CONFIG_TIME_STR, 0L);
		if (System.currentTimeMillis() - last_update_config_time > UPDATE_CONFIG_INTERVAL_TIME) {
			if (config.updateInBackground())
				sharedPreferences.edit().putLong(LAST_UPDATE_CONFIG_TIME_STR, System.currentTimeMillis()).commit();
		}
	}

	// 获取设备应用列表
	private static void initInstallApp() {
		PackageManager pm = SDKContext.context.getPackageManager();
		Intent query = new Intent(Intent.ACTION_MAIN);
		query.addCategory("android.intent.category.LAUNCHER");
		List<ResolveInfo> resolves = pm.queryIntentActivities(query, PackageManager.GET_ACTIVITIES);
		for (int i = 0; i < resolves.size(); i++) {
			String packagename = "";
			ResolveInfo info = resolves.get(i);
			// 判断是否为系统级应用
			if ((info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
				// 安装的应用
				packagename = info.activityInfo.packageName.toString();
				post_installs.add(packagename);
			} else {
				// 系统应用
				packagename = info.activityInfo.packageName.toString();
				pre_installs.add(packagename);
			}
		}
	}

}
