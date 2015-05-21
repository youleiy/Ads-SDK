package com.altamob.ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.altamob.ads.connect.config.ConfigFactory;
import com.altamob.ads.connect.model.AdConfig;
import com.altamob.ads.connect.model.BtnInfo;
import com.altamob.ads.connect.model.ResultAd;
import com.altamob.ads.connect.util.BuildJsonUtil;
import com.altamob.ads.connect.util.StringUtils;
import com.altamob.ads.view.OnclickCallBack;
import com.facebook.ads.a.v;

public class AltaInterstitialActivity extends Activity implements OnclickCallBack {
	private static final String LOG_TAG = AltaInterstitialActivity.class.getSimpleName();
	private String interstitialID;
	private ResultAd resultAd;
	private WebView webview;
	private v closeButton;
	private String INTERSTITIAL_AD_URL;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		INTERSTITIAL_AD_URL = ConfigFactory.getConfig().readString("INTERSTITIAL_AD_URL",
				"http://cdn.admobclick.com/sdk/min/interstitial.html?date=")
				+ Math.random();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Intent intent = getIntent();
		resultAd = (ResultAd) intent.getSerializableExtra("resultAd");
		this.interstitialID = intent.getStringExtra("adInterstitialUniqueId");

		RelativeLayout localRelativeLayout = new RelativeLayout(this);
		webview = new WebView(this);
		webview.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
				android.widget.RelativeLayout.LayoutParams.MATCH_PARENT));
		webview.getSettings().setJavaScriptEnabled(true);
		webview.addJavascriptInterface(new x(this, this), "AdControl");
		webview.setWebViewClient(new AltaWebWebClient());
		webview.loadUrl(INTERSTITIAL_AD_URL);
		/**
		 * 右上角关闭按钮
		 */
		this.closeButton = new v(this);
		DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
		RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams((int) (60.0F * localDisplayMetrics.density),
				(int) (60.0F * localDisplayMetrics.density));
		localLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		localLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		this.closeButton.setLayoutParams(localLayoutParams);
		this.closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				AltaInterstitialActivity.this.finish();
			}
		});
		localRelativeLayout.addView(this.webview);
		localRelativeLayout.addView(this.closeButton);

		setContentView(localRelativeLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		sendBroadcastForEvent("com.altamob.ads.interstitial.displayed");
	}

	@Override
	public void finish() {
		sendBroadcastForEvent("com.altamob.ads.interstitial.dismissed");
		super.finish();
	}

	/**
	 * 发送广播通知InterstitalView回调lisener
	 * 
	 * @param paramString
	 */
	private void sendBroadcastForEvent(String paramString) {
		String postBackStr = "k1=%,k2=%s,k3=%s,k4=%s,k5=%s,k6=%s,k7=%s,k8=%s,k9=%s,k10=%s";
		Intent localIntent = new Intent(paramString + ":" + this.interstitialID);
		localIntent.putExtra("postbackParams", postBackStr);
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
	}

	private String getAdConfig() {
		AdConfig adConfig = new AdConfig();
		adConfig.setBgImg(resultAd.getCreative().getUrl());
		adConfig.setDescription(resultAd.getDescription());
		adConfig.setIcon(resultAd.getLogo());
		if (!StringUtils.isEmpty(resultAd.getRating()) && !resultAd.getRating().equals("null"))
			adConfig.setRating(Double.valueOf(resultAd.getRating()));
		adConfig.setTitle(resultAd.getTitle());
		BtnInfo btnInfo = new BtnInfo();
		btnInfo.setHref(String.format(String.format("javascript:AdControl.toDetail('%s');", resultAd.getClick_url())));
		adConfig.setBtnInfo(btnInfo);
		return BuildJsonUtil.buildAdUnitConfig(adConfig);
	}

	class AltaWebWebClient extends WebViewClient {
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Log.e(LOG_TAG, String.format("code:%s info:%s", errorCode, description));
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.i(LOG_TAG, "page is load over");
			if (resultAd != null)
				webview.loadUrl(String.format("javascript:adunitsConfig(%s);", getAdConfig()));
		}

		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
			return super.shouldOverrideKeyEvent(view, event);
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) {
			handler.proceed();
		}
	}

	@Override
	public void onClick() {
		sendBroadcastForEvent("com.altamob.ads.interstitial.clicked");
	}
}
