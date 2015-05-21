package com.altamob.ads;

import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.altamob.ads.connect.config.ConfigFactory;
import com.altamob.ads.connect.request.SDKContext;
import com.altamob.ads.connect.util.StopWatch;
import com.altamob.ads.view.Ad_Type;
import com.altamob.ads.view.AltaBannerAdView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

@SuppressLint("HandlerLeak")
public class AltaAdView extends RelativeLayout implements Ad {
	private static final String LOG_TAG = AltaAdView.class.getSimpleName();
	private int REQUEST_FAN_TIMEOUT;
	private AltaAdListener adLoadListener;
	private boolean isReturn;
	private byte[] lock = new byte[128];
	AltaBannerAdView altaAdview;
	AdView facebookAdview;
	AltaAdListener adAltListener;
	StopWatch fbstopWatch = new StopWatch();
	StopWatch altstopWatch = new StopWatch();
	Context context;
	private boolean isAddViewyet;
	/**
	 * 标记加载广告时是否报错，仅当两个平台都返回错误时才会发送错误通知
	 */
	private boolean isAltaMobLoadError;
	private boolean isFANLoadError;
	/**
	 * 是否只显示ALtamob的广告
	 */
	private boolean isOnlyAltamobAds;
	/**
	 * 根据比率随机算法确定的本次展示的创意来源
	 */
	private Ad_Type currentLoadAdType;

	public AltaAdView(Context context, String placementId, AltaAdSize size) {
		super(context);
		this.context = context;
		SDKContext.init(context);
		initConfig();
		altaAdview = new AltaBannerAdView(context, placementId, size);
		facebookAdview = new AdView(context, placementId, convertAdsize(size));
	}

	private void initConfig() {
		REQUEST_FAN_TIMEOUT = ConfigFactory.getConfig().readInteger("REQUEST_FAN_TIMEOUT", 5000);
	}

	public void setAltAdListener(AltaAdListener adLoadListener) {
		this.adLoadListener = adLoadListener;
	}

	Handler addViewHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Ad_Type adType = (Ad_Type) msg.obj;
//				Log.i("currentLoadAdtype", "Show:" + String.valueOf(adType));
				switch (adType) {
				case FAN:
					addSubView(facebookAdview);
					if (adLoadListener != null)
						adLoadListener.onAltaAdloaded(AltaAdView.this);
					altaAdview.destroy();
					break;
				case ALTAMOB:
					addSubView(altaAdview);
					if (adLoadListener != null)
						adLoadListener.onAltaAdloaded(AltaAdView.this);
					facebookAdview.destroy();
					break;
				}
				break;
			case 2:
				if (adLoadListener != null)
					adLoadListener.onAltaAdError(AltaAdView.this, (com.altamob.ads.AdError) msg.obj);
				break;
			case 3:
				if (adLoadListener != null)
					adLoadListener.onAltaAdClick(AltaAdView.this);
				break;
			default:
				break;
			}
		}
	};

	private void addSubView(View view) {
		if (!isAddViewyet) {
			isAddViewyet = true;
			this.addView(view);
		}
		isReturn = true;
	}

	private synchronized void setResultAdView(Ad_Type adType) {
		Message message = addViewHandler.obtainMessage();
		message.what = 1;
		message.obj = adType;
		addViewHandler.sendMessage(message);
	}

	@Override
	public void loadAd() {
		try {
			currentLoadAdType = SDKContext.getRandomAdTypeByRate();
			Log.i("currentLoadAdtype", "Random:" + String.valueOf(currentLoadAdType));
			loadAltaAds();
			if (!isOnlyAltamobAds)
				loadFBAd();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_TAG, e.toString());
			if (facebookAdview != null)
				facebookAdview.destroy();
			if (altaAdview != null)
				altaAdview.destroy();
			com.altamob.ads.AdError adError = new com.altamob.ads.AdError(com.altamob.ads.AdError.SERVER_ERROR_CODE,
					com.altamob.ads.AdError.SERVER_ERROR.getErrorMessage());
			adLoadListener.onAltaAdError(this, adError);
		}
	}

	private void loadFBAd() {
		/**
		 * 另起一个线程load facebook AdView
		 */
		new Thread() {
			@Override
			public void run() {
				try {
					fbstopWatch.start();
					facebookAdview.loadAd();
					facebookAdview.setAdListener(new AdListener() {
						@Override
						public void onAdClicked(Ad paramAd) {
							Message message = addViewHandler.obtainMessage();
							message.what = 3;
							addViewHandler.sendMessage(message);
						}

						@Override
						public void onAdLoaded(Ad paramAd) {
							fbstopWatch.split();
							Log.i(LOG_TAG, "facebook adView load over! " + " time" + fbstopWatch.getSplitTime());
							if (!isReturn) {
								setResultAdView(Ad_Type.FAN);
							}
							fbstopWatch.stop();
							fbstopWatch.reset();
							fbstopWatch.start();
						}

						/**
						 * 加载facebook有任何异常，直接唤醒加载altamob广告的线程
						 * 
						 */
						@Override
						public void onError(Ad paramAd, AdError adError) {
							Log.e(LOG_TAG, String.format("fberrorCode:%s,fberrorMessage:%s", adError.getErrorCode(), adError.getErrorMessage()));
							isFANLoadError = true;
							if (isAltaMobLoadError) {
								com.altamob.ads.AdError altaAdError = new com.altamob.ads.AdError(adError.getErrorCode(), adError.getErrorMessage());
								Message message = addViewHandler.obtainMessage();
								message.what = 2;
								message.obj = altaAdError;
								addViewHandler.sendMessage(message);
							} else if (!isReturn) {
								synchronized (lock) {
									lock.notify();
								}
							}
						}
					});
				} catch (Exception e) {
					Log.e(LOG_TAG, e.toString());
				}
			}
		}.start();
	}

	private void loadAltaAds() {
		Log.i("REQUEST_FAN_TIMEOUT", String.valueOf(REQUEST_FAN_TIMEOUT));
		altstopWatch.start();
		altaAdview.loadAd();
		altaAdview.setAdListerer(new AltaAdListener() {
			@Override
			public void onAltaAdClick(Ad ad) {
				Message message = addViewHandler.obtainMessage();
				message.what = 3;
				addViewHandler.sendMessage(message);
			}

			/**
			 * AltaMob Adview加载完毕后要确认facebook广告是否在5s内加载完毕,
			 * 如果指定时间(默认5s)后facebook尚未加载完毕则加载altamob广告
			 */
			@Override
			public void onAltaAdloaded(Ad ad) {
				altstopWatch.split();
				final long loadSpend = altstopWatch.getSplitTime();
				Log.i(LOG_TAG, "altamob adView load over! time:" + loadSpend);

				Executors.newCachedThreadPool().execute(new Runnable() {
					@Override 
					public void run() {
						try {
							synchronized (lock) {
								if (!isOnlyAltamobAds && currentLoadAdType.equals(Ad_Type.FAN) && loadSpend < REQUEST_FAN_TIMEOUT) {
									Log.i(LOG_TAG, "wait " + (REQUEST_FAN_TIMEOUT - loadSpend));
									lock.wait(REQUEST_FAN_TIMEOUT - loadSpend);
								}
							}
							if (!isReturn) {
								setResultAdView(Ad_Type.ALTAMOB);
							}
						} catch (InterruptedException e) {
							Log.e(LOG_TAG, e.toString());
						}
					};
				});
			}

			@Override
			public void onAltaAdError(Ad ad, com.altamob.ads.AdError adError) {
				Log.e(LOG_TAG, String.format("Altamob ad load Error code:%s msg:%s", adError.getErrorCode(), adError.getErrorMessage()));
				isAltaMobLoadError = true;
				if (isOnlyAltamobAds || isFANLoadError) {
					Message message = addViewHandler.obtainMessage();
					message.what = 2;
					message.obj = adError;
					addViewHandler.sendMessage(message);
				}
			}
		});
	}

	@Override
	public void destroy() {
		if (facebookAdview != null)
			facebookAdview.destroy();
		if (altaAdview != null)
			altaAdview.destroy();
	}

	private AdSize convertAdsize(AltaAdSize size) {
		switch (size) {
		case BANNER_HEIGHT_50:
			return AdSize.BANNER_HEIGHT_50;
		case BANNER_HEIGHT_90:
			return AdSize.BANNER_HEIGHT_90;
		case RECTANGLE_HEIGHT_250:
			return AdSize.RECTANGLE_HEIGHT_250;
		default:
			return null;
		}
	}

	public void setOnlyAltamobAds(boolean isOnlyAltamobAds) {
		this.isOnlyAltamobAds = isOnlyAltamobAds;
	}

}
