package com.altamob.ads.connect.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.util.Log;

import com.altamob.ads.connect.config.ConfigFactory;
import com.altamob.ads.connect.model.BaseRequest;
import com.altamob.ads.connect.request.ErrorCode;

public class HttpUtil {
	public static final String LOG_TAGS = HttpUtil.class.getSimpleName();

	/**
	 * 发送HttpPost请求,返回结果为Gzip格式，需要解压
	 * 
	 * @author haoyongfeng
	 * @param url
	 *            请求API URL
	 * @param request
	 *            Request Model
	 * @param isWrapParam
	 *            是否在生成Json的时候包装param参数，类似 param{ }
	 * @return
	 * @throws JSONException
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String gZipHttpPost(String url, BaseRequest request) throws JSONException, URISyntaxException, ClientProtocolException,
			IOException, Exception {
		String result = "";
		String jsonFormatStr = "";
		// 对发送的字符串进行加密
		org.json.JSONObject jsonObject;
		jsonObject = new org.json.JSONObject(jsonFormatStr);
		jsonObject.put("params", Encryption.encrypt(jsonObject.getString("params")));
		HttpClient httpClient = new DefaultHttpClient();
		if (jsonFormatStr != null && jsonFormatStr.length() > 0) {
			HttpPost post = new HttpPost(new URI(url));
			StringEntity entity = new StringEntity(jsonObject.toString());
			entity.setContentType("application/json");
			entity.setContentEncoding("UTF-8");
			post.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity msg = httpResponse.getEntity();
				if (entity != null) {
					result = unZip(msg.getContent());
				}
			} else {
				// Log.e("url error ", url + "   --  " +
				// httpResponse.toString());
				result = ErrorCode.NETWORK_ERROR.toString();
			}
		} else {
			// Log.e("LOG_TAGS",
			// String.format("the gzip request json %s is null",
			// jsonFormatStr));
			result = ErrorCode.SDK_ERROR.toString();
		}
		return result;
	}

	/**
	 * 发送HttpPost请求
	 * 
	 * @author haoyongfeng
	 * @param url
	 *            请求API URL
	 * @param request
	 *            Request Model
	 * @param requestTimeOut
	 *            请求超时时间
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String httpPost(String url, String jsonFormatStr, String token, int requestTimeOut) throws URISyntaxException,
			ClientProtocolException, IOException {
		String result = "";
		// Log.i(LOG_TAGS, jsonFormatStr);
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				ConfigFactory.getConfig().readInteger("DEFAULT_REQUEST_TIMEOUT", 20000));
		if (requestTimeOut > 0)
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, requestTimeOut);
		else
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					ConfigFactory.getConfig().readInteger("DEFAULT_REQUEST_TIMEOUT", 20000));
		if (jsonFormatStr != null && jsonFormatStr.length() > 0) {
			HttpPost post = new HttpPost(new URI(url));
			if (!StringUtils.isEmpty(token))
				post.setHeader("token", token);
			// String encryptionString = Encryption.encrypt(jsonFormatStr);
			// Log.i("Request Str", encryptionString);
			StringEntity entity = new StringEntity(jsonFormatStr);
			entity.setContentType("application/json");
			post.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(post);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity msg = httpResponse.getEntity();
				if (entity != null) {
					String resultMsg = EntityUtils.toString(msg);
					// Log.i("AES", resultMsg);
					// result = Encryption.desEncrypt(resultMsg);
					result = resultMsg;
				}
			} else {
				// Log.e("http error", url + " --" +
				// httpResponse.getStatusLine().getStatusCode());
			}
		} else {
			Log.e(LOG_TAGS, String.format("the request json %s is null", jsonFormatStr));
		}
		return result.trim();
	}

	public static String httpGet(String url, String token, int requestTimeout) throws URISyntaxException, ClientProtocolException, IOException {
		String result = "";
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
				ConfigFactory.getConfig().readInteger("DEFAULT_REQUEST_TIMEOUT", 30000));
		if (requestTimeout > 0)
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, requestTimeout);
		else
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					ConfigFactory.getConfig().readInteger("DEFAULT_REQUEST_TIMEOUT", 30000));
		HttpGet get = new HttpGet(new URI(url));
		if (!StringUtils.isEmpty(token))
			get.setHeader("token", token);
		HttpResponse httpResponse = httpClient.execute(get);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			HttpEntity msg = httpResponse.getEntity();
			String resultMsg = EntityUtils.toString(msg);
			// Log.i("AES", resultMsg);
			result = resultMsg;
		} else {
			// Log.e("http error", url + " -- " +
			// httpResponse.getStatusLine().getStatusCode());
		}
		return result.trim();
	}

	public static String getConfig(String configUrl) {
		String ip = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(configUrl);
		try {
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				ip = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			// Log.e(LOG_TAGS, "get config error:" + e.toString());
		}
		return ip;
	}

	public static String GetNetIp() {
		URL infoUrl = null;
		InputStream inStream = null;
		String ipLine = "";
		HttpURLConnection httpConnection = null;
		try {
			infoUrl = new URL("http://ip168.com/");
			URLConnection connection = infoUrl.openConnection();
			httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inStream = httpConnection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
				StringBuilder strber = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null)
					strber.append(line + "\n");

				Pattern pattern = Pattern
						.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
				Matcher matcher = pattern.matcher(strber.toString());
				if (matcher.find()) {
					ipLine = matcher.group();
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null)
					inStream.close();
				if (httpConnection != null)
					httpConnection.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ipLine;
	}

	int postbackCount = 0;

	public static void postBack(String postbackUrl, int errorRetryCount) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(postbackUrl);
		try {
			if (errorRetryCount <= 0)
				return;
			HttpResponse response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				return;
			} else {
				Log.e(LOG_TAGS, " post back error code:" + response.getStatusLine().getStatusCode() + " count:" + errorRetryCount);
				Thread.sleep(3000);
				postBack(postbackUrl, errorRetryCount - 1);
			}
		} catch (Exception e) {
			Log.e(LOG_TAGS, "postback error:" + e.toString());
		}
	}

	/**
	 * 解压Gzip
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public static String unZip(InputStream is) throws Exception {
		GZIPInputStream gis = new GZIPInputStream(is);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int length;
		byte data[] = new byte[128];
		while ((length = gis.read(data, 0, 128)) != -1) {
			out.write(data, 0, length);
		}
		gis.close();
		out.close();
		return out.toString();
	}

}
