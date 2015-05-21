package com.altamob.ads.view;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.altamob.ads.AdError;
import com.altamob.ads.AltaAdListener;
import com.altamob.ads.AltaAdSize;
import com.altamob.ads.x;
import com.altamob.ads.connect.Request;
import com.altamob.ads.connect.config.Config;
import com.altamob.ads.connect.config.ConfigFactory;
import com.altamob.ads.connect.model.AdConfig;
import com.altamob.ads.connect.model.BtnInfo;
import com.altamob.ads.connect.model.ResultAd;
import com.altamob.ads.connect.model.ResultObject;
import com.altamob.ads.connect.request.CallBack;
import com.altamob.ads.connect.util.BuildJsonUtil;
import com.altamob.ads.connect.util.StringUtils;
import com.facebook.ads.Ad;

@SuppressLint("HandlerLeak")
public class AltaBannerAdView extends RelativeLayout implements Ad, OnClickListener, OnclickCallBack {
	private static final String LOG_TAG = AltaBannerAdView.class.getSimpleName();
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
	private AltaAdListener adListerer;
	private ResultAd resultAd;
	private String placementId;
	private WebView webview;
	private RelativeLayout relativeLayout;
	private Context context;

	private String BANNER_AD_URL;
	private String RECTANGLE_AD_URL;
	private int REFRESH_AD_PERIOD;
	private int requestPicWidth;
	private int requestPicHeight;

	/**
	 * 广告刷新时间
	 */

	public void setAdListerer(AltaAdListener adListerer) {
		this.adListerer = adListerer;
	}

	private void initConfig() {
		Config config = ConfigFactory.getConfig();
		BANNER_AD_URL = config.readString("BANNER_AD_URL", "http://cdn.admobclick.com/sdk/min/banner.html?date=") + Math.random();
		RECTANGLE_AD_URL = config.readString("RECTANGLE_AD_URL", "http://cdn.admobclick.com/sdk/min/rectangle.html?date=") + Math.random();
		REFRESH_AD_PERIOD = config.readInteger("REFRESH_AD_PERIOD", 30);
//		Log.i("BANNER_AD_URL", BANNER_AD_URL);
//		Log.i("RECTANGLE_AD_URL", RECTANGLE_AD_URL);
//		Log.i("REFRESH_AD_PERIOD", String.valueOf(REFRESH_AD_PERIOD));
	}

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	public AltaBannerAdView(Context context, String placementId, AltaAdSize adsize) {
		super(context);
		this.context = context;
		initConfig();
		DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
		relativeLayout = new RelativeLayout(context);
		this.placementId = placementId;
		webview = new WebView(context);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.addJavascriptInterface(new x(context, this), "AdControl");
		switch (adsize) {
		case BANNER_HEIGHT_50:
			requestPicWidth = 96;
			requestPicHeight = 96;
			webview.loadUrl(BANNER_AD_URL);
			break;
		case BANNER_HEIGHT_90:
			requestPicWidth = 128;
			requestPicHeight = 128;
			webview.loadUrl(BANNER_AD_URL);
			break;
		case RECTANGLE_HEIGHT_250:
			requestPicWidth = 1024;
			requestPicHeight = 768;
			webview.loadUrl(RECTANGLE_AD_URL);
			break;
		default:
			break;
		}
		webview.setWebViewClient(new AltaWebWebClient());
		relativeLayout.addView(webview);
		AltaBannerAdView.this.addView(relativeLayout);
	}

	@Override
	public void onClick(View v) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resultAd.getClick_url()));
		browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getContext().startActivity(browserIntent);
		adListerer.onAltaAdClick(AltaBannerAdView.this);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				webview.loadUrl(String.format("javascript:adunitsConfig(%s);", getAdConfig()));
				adListerer.onAltaAdloaded(AltaBannerAdView.this);
				// 通知server端，该广告已经展示
				Request.postBack(resultAd.getImpression_url());
				break;
			case 2:
				adListerer.onAltaAdError(AltaBannerAdView.this, (AdError) msg.obj);
				scheduledThreadPoolExecutor.shutdown();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void loadAd() {
		scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
//				Log.i(LOG_TAG, "------------------------load altamob Banner ad------------------------");
				Request request = new Request(context, placementId);
				initConfig();
				DisplayMetrics localDisplayMetrics = getResources().getDisplayMetrics();
				// (int) (localDisplayMetrics.density * 200.0F)
				request.createRequestTask(requestPicWidth, requestPicHeight, new CallBack() {
					@Override
					public void OnFailure(AdError adError) {
						sendErrorMsg(adError);
					}

					@Override
					public void OnCompleted(String result) {
						Log.d(LOG_TAG, "requst complete");
						ResultObject resultAds = BuildJsonUtil.getResultObject(result);
						if (resultAds != null && resultAds.getResult() != null && resultAds.getResult().size() > 0
								&& resultAds.getResult().get(0) != null) {
							resultAd = resultAds.getResult().get(0);
							Message message = handler.obtainMessage();
							message.what = 1;
							handler.sendMessage(message);
						} else {
							Log.w(LOG_TAG, "the  ResultObject is null");
							AdError adError = new AdError(AdError.NO_FILL_ERROR_CODE, AdError.NO_FILL.getErrorMessage());
							sendErrorMsg(adError);
						}
					}
				}).execute();
			}
		}, 0, REFRESH_AD_PERIOD, TimeUnit.SECONDS);

	}

	private void sendErrorMsg(AdError adError) {
		Message message = handler.obtainMessage();
		message.what = 2;
		message.obj = adError;
		handler.sendMessage(message);
	}

	private String getAdConfig() {
		AdConfig adConfig = new AdConfig();
		adConfig.setBgImg(resultAd.getCreative().getUrl());
		adConfig.setDescription(resultAd.getDescription());
		adConfig.setIcon(resultAd.getCreative().getUrl());
		if (!StringUtils.isEmpty(resultAd.getRating()) && !resultAd.getRating().equals("null"))
			adConfig.setRating(Double.valueOf(resultAd.getRating()));
		adConfig.setTitle(resultAd.getTitle());
		BtnInfo btnInfo = new BtnInfo();
		btnInfo.setHref(String.format("javascript:AdControl.toDetail('%s');", resultAd.getClick_url()));
		adConfig.setBtnInfo(btnInfo);
		return BuildJsonUtil.buildAdUnitConfig(adConfig);
	}

	@Override
	public void destroy() {
		scheduledThreadPoolExecutor.shutdown();
	}

	class AltaWebWebClient extends WebViewClient {
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Log.e(LOG_TAG, String.format("code:%s info:%s", errorCode, description));
			AdError adError = new AdError(AdError.NETWORK_ERROR_CODE, AdError.NETWORK_ERROR.getErrorMessage());
			sendErrorMsg(adError);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
//			Log.i(LOG_TAG, "page is load over");
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
		if (adListerer != null)
			adListerer.onAltaAdClick(AltaBannerAdView.this);
	}
}
