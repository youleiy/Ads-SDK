package com.altamob.ads;

import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.altamob.ads.connect.config.ConfigFactory;
import com.altamob.ads.connect.request.SDKContext;
import com.altamob.ads.view.Ad_Type;
import com.altamob.ads.view.AltaAd;
import com.altamob.ads.view.AltaInterstialAdview;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

@SuppressLint("HandlerLeak")
public final class AltaInterstitialAd extends AltaAd implements Ad, InterstitialAdListener, AltaInterstitialAdListener {
	private static final String LOG_TAG = AltaInterstitialAd.class.getSimpleName();
	private InterstitialAd interstitialAd;
	private AltaInterstialAdview altaInterstialAdview;
	private AltaInterstitialAdListener altaInterstitialAdListener;
	private Ad_Type showViewType;
	/**
	 * 标记加载广告时是否报错，仅当两个平台都返回错误时才会发送错误通知
	 */
	private boolean isAltaMobLoadError;
	private boolean isFANLoadError;
	private boolean isOnlyAltamobAds;

	/**
	 * 根据比率随机算法确定的本次展示的创意来源
	 */
	private Ad_Type currentLoadAdType;

	public AltaInterstitialAd(Context context, String placementId) {
		SDKContext.init(context);
		initConfig();
		interstitialAd = new InterstitialAd(context, placementId);
		altaInterstialAdview = new AltaInterstialAdview(context, placementId);
	}

	public void setAltAdListener(AltaInterstitialAdListener altaInterstitialAdListener) {
		this.altaInterstitialAdListener = altaInterstitialAdListener;
	}

	public boolean isAdLoaded() {
		return isReturn;
	}

	@Override
	public void loadAd() {
		currentLoadAdType = SDKContext.getRandomAdTypeByRate();
		altstopWatch.start();
		altaInterstialAdview.setAltAdListener(this);
		altaInterstialAdview.loadAd();

		if (!isOnlyAltamobAds()) {
			fbstopWatch.start();
			interstitialAd.loadAd();
			interstitialAd.setAdListener(this);
		}

	}

	@Override
	public void destroy() {
		interstitialAd.destroy();
		altaInterstialAdview.destroy();
	}

	public void show() {
		switch (showViewType) {
		case FAN:
			interstitialAd.show();
			altaInterstialAdview.destroy();
			break;
		case ALTAMOB:
			altaInterstialAdview.show();
			interstitialAd.destroy();
			break;
		}
	}

	/**
	 * facebook interstitial Ad listener
	 * 
	 * @param context
	 */
	@Override
	public void onError(Ad ad, AdError adError) {
		Log.e(LOG_TAG, String.format("errorCode:%s,errorMessage:%s", adError.getErrorCode(), adError.getErrorMessage()));
		isFANLoadError = true;
		if (isAltaMobLoadError && altaInterstitialAdListener != null) {
			com.altamob.ads.AdError altaAderror = new com.altamob.ads.AdError(adError.getErrorCode(), adError.getErrorMessage());
			altaInterstitialAdListener.onAltaAdError(this, altaAderror);
		} else if (!isReturn) {
			synchronized (lock) {
				lock.notify();
			}
		}
	}

	@Override
	public void onAdLoaded(Ad ad) {
		fbstopWatch.split();
		Log.i(LOG_TAG, "facebook interstitial ad load over! time" + fbstopWatch.getSplitTime());
		fbstopWatch.reset();
		fbstopWatch.start();
		if (!isReturn) {
			showViewType = Ad_Type.FAN;
			altaInterstitialAdListener.onAltaAdloaded(this);
			isReturn = true;
		}
	}

	@Override
	public void onAdClicked(Ad ad) {
		if (altaInterstitialAdListener != null)
			altaInterstitialAdListener.onAltaAdClick(ad);
	}

	@Override
	public void onInterstitialDisplayed(Ad ad) {
		if (altaInterstitialAdListener != null)
			altaInterstitialAdListener.onInterstitialDisplayed(ad);
	}

	@Override
	public void onInterstitialDismissed(Ad ad) {
		if (altaInterstitialAdListener != null)
			altaInterstitialAdListener.onInterstitialDismissed(ad);
		interstitialAd.destroy();
		interstitialAd = null;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (altaInterstitialAdListener != null)
				altaInterstitialAdListener.onAltaAdloaded(AltaInterstitialAd.this);
		}
	};

	/**
	 * altamob intertitial listener
	 */
	private void notifyAdLoaded() {
		Message msg = handler.obtainMessage();
		handler.sendMessage(msg);
	}

	@Override
	public void onAltaAdloaded(final Ad ad) {
		altstopWatch.split();
		Log.i(LOG_TAG, "altamob interstitial ad load over! time" + altstopWatch.getSplitTime());
		final long loadSpend = altstopWatch.getSplitTime();
		Executors.newCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (lock) {
						if (!isOnlyAltamobAds() && currentLoadAdType == Ad_Type.FAN && loadSpend < REQUEST_FAN_TIMEOUT) {
							Log.i(LOG_TAG, "wait " + (REQUEST_FAN_TIMEOUT - loadSpend));
							lock.wait(REQUEST_FAN_TIMEOUT - loadSpend);
						} else {
							ad.destroy();
						}
						if (!isReturn) {
							showViewType = Ad_Type.ALTAMOB;
							notifyAdLoaded();
							isReturn = true;
						}
					}
				} catch (InterruptedException e) {
					Log.e(LOG_TAG, e.toString());
				}
			}
		});

	}

	@Override
	public void onAltaAdClick(Ad ad) {
		if (altaInterstitialAdListener != null)
			altaInterstitialAdListener.onAltaAdClick(ad);
	}

	@Override
	public void onAltaAdError(Ad ad, com.altamob.ads.AdError adError) {
		isAltaMobLoadError = true;
		if ((isOnlyAltamobAds() || isFANLoadError) && altaInterstitialAdListener != null)
			altaInterstitialAdListener.onAltaAdError(this, adError);
	}

	public boolean isOnlyAltamobAds() {
		return isOnlyAltamobAds;
	}

	public void setOnlyAltamobAds(boolean isOnlyAltamobAds) {
		this.isOnlyAltamobAds = isOnlyAltamobAds;
	}

	private void initConfig() {
		REQUEST_FAN_TIMEOUT = ConfigFactory.getConfig().readInteger("REQUEST_FAN_TIMEOUT", 5000);
	}
}
