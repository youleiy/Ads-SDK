package com.altamob.ads.connect.config;

import java.util.Iterator;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.altamob.ads.connect.request.SDKContext;
import com.altamob.ads.connect.util.HttpUtil;

public class AltaConfig implements Config {

	private static final String CONFIG_URL = "http://demo2873601.mockable.io/config.json";

	@Override
	public String readString(String key, String defaultValue) {
		if (SDKContext.getSharedPreferences() != null)
			return SDKContext.getSharedPreferences().getString(key, defaultValue);
		return defaultValue;
	}

	@Override
	public Integer readInteger(String key, Integer defaultValue) {
		return Integer.valueOf(readString(key, String.valueOf(defaultValue)));
	}

	@Override
	public Long readLong(String key, Long defaultValue) {
		return Long.valueOf(readString(key, String.valueOf(defaultValue)));
	}

	@Override
	public Double readDouble(String key, Double defaultValue) {
		return Double.valueOf(readString(key, String.valueOf(defaultValue)));
	}

	/**
	 * 后台更新远程配置
	 * 
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean updateInBackground() {
		Executors.newCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					if (SDKContext.getSharedPreferences() != null) {
						Editor editor = SDKContext.getSharedPreferences().edit();
						String configStr = HttpUtil.getConfig(CONFIG_URL);
						JSONObject jsonObject = new JSONObject(configStr);
						for (Iterator<String> iterator = jsonObject.keys(); iterator.hasNext();) {
							String key = iterator.next().toString();
							editor.putString(key, jsonObject.getString(key));
						}
						editor.commit();
					}
				} catch (JSONException e) {
					Log.e("update config error", e.toString());
				}
			}
		});
		return true;
	}

}
