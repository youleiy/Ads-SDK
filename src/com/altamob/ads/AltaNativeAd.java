package com.altamob.ads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.altamob.ads.connect.request.SDKContext;
import com.altamob.ads.view.AltaAd;
import com.altamob.ads.view.AltaNativeAdView;
import com.facebook.ads.Ad;

public class AltaNativeAd extends AltaAd implements Ad, AltaNativeAdListener {
	private static final String LOG_TAG = AltaNativeAd.class.getSimpleName();
	AltaNativeAdView altaNativeAdView;
	protected List<View> clickListeners = new ArrayList<View>();
	protected AltaNativeAdListener altAdListener;
	private List<NativeAd> nativeAds;
	private int limit = 1;
	private String requestTemplate;

	public void setAltNativeAdListener(AltaNativeAdListener altAdListener) {
		this.altAdListener = altAdListener;
	}

	public AltaNativeAd(Context context, String placementId) {
		this.context = context;
		this.placementId = placementId;
		SDKContext.init(context);
		altaNativeAdView = new AltaNativeAdView(context, placementId);
	}

	public void loadAd() {
		altstopWatch.start();
		altaNativeAdView.setAltAdListener(this);
		altaNativeAdView.loadAd();
	}

	Handler nativeAdHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				altAdListener.onAltaAdloaded(AltaNativeAd.this);
				break;
			case 2:
				altAdListener.onAltaAdError(AltaNativeAd.this, (com.altamob.ads.AdError) msg.obj);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void sendMessage(int code, Object obj) {
		Message msg = nativeAdHandler.obtainMessage();
		msg.what = code;
		msg.obj = obj;
		nativeAdHandler.sendMessage(msg);
	}

	@Override
	public void onAltaAdloaded(Ad ad) {
		altstopWatch.split();
		Log.i(LOG_TAG, "altamob native Ad load over,time:" + altstopWatch.getSplitTime());
		altstopWatch.reset();
		altstopWatch.start();
		Executors.newCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				nativeAds = altaNativeAdView.getNativeAds();
				sendMessage(1, null);
			}
		});
	}

	@Override
	public void destroy() {
		if (altaNativeAdView != null)
			altaNativeAdView.destroy();
	}

	@Override
	public void onAltaAdError(Ad ad, com.altamob.ads.AdError adError) {
		Log.e(LOG_TAG, String.format("errorCode:%s errorInfo:%s", adError.getErrorCode(), adError.getErrorMessage()));
		sendMessage(2, adError);
	}

	public NativeAd[] getNativeAds() {
		return nativeAds.toArray(new NativeAd[] {});
	}

	public void setLimit(int limit) {
		altaNativeAdView.setLimit(limit);
	}

	public void setRequestTemplate(String requestTemplate) {
		altaNativeAdView.setRequestTemplate(requestTemplate);
	}

}
