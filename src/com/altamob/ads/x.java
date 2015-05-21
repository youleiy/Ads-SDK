package com.altamob.ads;

import java.net.HttpURLConnection;
import java.net.URL;

import com.altamob.ads.view.OnclickCallBack;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class x {
	Context mContext;
	OnclickCallBack callback;
	ProgressDialog progressDialog;

	/** Instantiate the interface and set the context */
	public x(Context c, OnclickCallBack callBack) {
		this.mContext = c;
		this.callback = callBack;
	}

	/** Show a toast from the web page */
	@JavascriptInterface
	public void toDetail(final String clickUrl) {
//		Log.i("WebAppInterface", "start");
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setMessage("Loading...");
		progressDialog.show();
		callback.onClick();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW);
				browserIntent.setData(Uri.parse(getDestUrl(clickUrl)));
				browserIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				mContext.startActivity(browserIntent);
				progressDialog.dismiss();
			}
		}).start();

	}

	public static String getDestUrl(String url) {
		try {
			URL serverUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setInstanceFollowRedirects(false);
			conn.connect();
			String location = conn.getHeaderField("Location");
//			Log.i("redirect", location);
			if (location.startsWith("market")) {
//				Log.i("dest", location);
				return location;
			} else {
				return getDestUrl(location);
			}
		} catch (Exception e) {
			Log.e("GetDestUrlError", e.toString());
			return url;
		}
	}
}