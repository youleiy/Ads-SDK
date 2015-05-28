/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.altamob.ads.connect.request;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.json.JSONObject;

import android.util.Log;

import com.altamob.ads.AdError;
import com.altamob.ads.connect.model.Filter;
import com.altamob.ads.connect.model.GetAdRequest;
import com.altamob.ads.connect.util.HttpUtil;

public class RequestAsyncTask {

	private static final String TAG = RequestAsyncTask.class.getSimpleName();

	private String reuquestUrl;
	private CallBack callBack;
	private GetAdRequest request;
	private int requestTimeOut;

	public RequestAsyncTask() {

	}

	public RequestAsyncTask(String reuqestUrl, GetAdRequest requestParams, CallBack callback) {
		this.reuquestUrl = reuqestUrl;
		this.callBack = callback;
		this.request = requestParams;
	}

	public void execute() {
		Executors.newCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				// Log.i(TAG, String.valueOf(requestCounter));
				String result = "";
				try {
					// Log.i("requestJson",
					// BuildJsonUtil.buridAdRequestJson(request));
					result = HttpUtil.httpGet(buildGetUrl(), request.getToken(), requestTimeOut);

					JSONObject jsonObject = new JSONObject(result);
					if (jsonObject.has("error")) {
						Log.e(TAG, "Ad Service error:" + jsonObject.getString("error"));
						callBack.OnFailure(AdError.SERVER_ERROR);
					} else {
						callBack.OnCompleted(result);
					}
				} catch (IOException e) {
					Log.e(TAG, e.toString());
					e.printStackTrace();
					callBack.OnFailure(AdError.NETWORK_ERROR);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
					e.printStackTrace();
					callBack.OnFailure(AdError.SERVER_ERROR);
				}
			}

		});
	}

	private String buildGetUrl() {
		StringBuffer resultUrl = new StringBuffer();
		Filter filter = request.getFilter();
		resultUrl.append(reuquestUrl).append("?").append(addParams("country", filter.getCountry())).append(addParams("ip", filter.getIp()))
				.append(addParams("android_id", filter.getAndroid_id())).append(addParams("lan", filter.getLanguage()))
				.append(addParams("pkg", filter.getPkg())).append(addParams("placement", filter.getPlacement()))
				.append(addParams("platform", filter.getPlatform())).append(addParams("version", filter.getVersion()))
				.append(addParams("width", filter.getWidth())).append(addParams("height", filter.getHeight()))
				.append(addParams("template", request.getTemplate())).append(addParams("limit", request.getLimit()))
				.append(addParams("is_rooted", request.getFilter().isRooted()));
//		Log.i("GetURL", resultUrl.toString());
		return resultUrl.toString().substring(0, resultUrl.length() - 1);
	}

	private StringBuffer addParams(String key, Object value) {
		StringBuffer resultUrl = new StringBuffer();
		resultUrl.append(key).append("=").append(value).append("&");
		return resultUrl;
	}

	public RequestAsyncTask setRequestTimeOut(int requestTimeOut) {
		this.requestTimeOut = requestTimeOut;
		return this;
	}

}
