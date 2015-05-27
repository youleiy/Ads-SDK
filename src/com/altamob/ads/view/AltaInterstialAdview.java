package com.altamob.ads.view;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.altamob.ads.AdError;
import com.altamob.ads.AltaInterstitialActivity;
import com.altamob.ads.AltaInterstitialAdListener;
import com.altamob.ads.connect.Request;
import com.altamob.ads.connect.model.ResultAd;
import com.altamob.ads.connect.model.ResultObject;
import com.altamob.ads.connect.request.CallBack;
import com.altamob.ads.connect.util.BuildJsonUtil;
import com.facebook.ads.Ad;

@SuppressLint("HandlerLeak")
public class AltaInterstialAdview implements Ad {

	private static final String LOG_TAG = AltaInterstialAdview.class.getSimpleName();
	private AltaInterstitialAdListener altAdListener;
	private Context context;
	private boolean adLoaded;
	private String placementId;
	private final String uniqueId = UUID.randomUUID().toString();
	private final AltaIntristitialReciver broadcastReceiver;
	private Intent intent;
	private ResultAd resultAd;

	public boolean isAdLoaded() {
		return adLoaded;
	}

	public AltaInterstialAdview(Context context, String placementId) {
		this.context = context;
		this.placementId = placementId;

		broadcastReceiver = new AltaIntristitialReciver();
		broadcastReceiver.registerReceiver();
	}

	public void setAltAdListener(AltaInterstitialAdListener altAdListener) {
		this.altAdListener = altAdListener;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (altAdListener != null)
					altAdListener.onAltaAdloaded(AltaInterstialAdview.this);
				break;
			case 2:
				if (altAdListener != null)
					altAdListener.onAltaAdError(AltaInterstialAdview.this, (AdError) msg.obj);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 加载插屏广告素材
	 */
	@Override
	public void loadAd() {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		new Request(context, placementId).createRequestTask(1200, 627, new CallBack() {
			@Override
			public void OnFailure(AdError adError) {
				Log.e(LOG_TAG, String.format("sdk error code:%s errorInfo:%s", adError.getErrorCode(), adError.getErrorMessage()));
				sendErrorMsg(adError);
			}

			@Override
			public void OnCompleted(String result) {
//				Log.i(LOG_TAG, result);
				ResultObject resultObject = BuildJsonUtil.getResultObject(result);
				if (resultObject.getResult() != null && resultObject.getResult().size() > 0 && resultObject.getResult().get(0) != null) {
					resultAd = resultObject.getResult().get(0);
					intent = new Intent(context, AltaInterstitialActivity.class);
					intent.putExtra("adInterstitialUniqueId", AltaInterstialAdview.this.uniqueId);
					intent.putExtra("resultAd", resultAd);
					adLoaded = true;
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

	private void sendErrorMsg(AdError adError) {
		Message message = handler.obtainMessage();
		message.what = 2;
		message.obj = adError;
		handler.sendMessage(message);
	}

	@Override
	public void destroy() {
		if (broadcastReceiver != null) {
			broadcastReceiver.unReginsterReciver();
		}
	}

	/**
	 * 展示Altamob广告Activity
	 */
	public void show() {
		if (adLoaded) {
			this.context.startActivity(intent);
		}
	}

	private class AltaIntristitialReciver extends BroadcastReceiver {
		public void onReceive(Context paramContext, Intent paramIntent) {
			String str1 = paramIntent.getAction();
			String[] arrayOfString = str1.split(":");
			String str2 = arrayOfString[0];
			if ((AltaInterstialAdview.this.altAdListener == null) && (!(str2.equals("com.altamob.ads.interstitial.impression.logged"))))
				return;
			if ("com.altamob.ads.interstitial.clicked".equals(str2)) {
				AltaInterstialAdview.this.altAdListener.onAltaAdClick(AltaInterstialAdview.this);
			} else if ("com.altamob.ads.interstitial.dismissed".equals(str2)) {
				AltaInterstialAdview.this.altAdListener.onInterstitialDismissed(AltaInterstialAdview.this);
			} else if ("com.altamob.ads.interstitial.displayed".equals(str2)) {
				AltaInterstialAdview.this.altAdListener.onInterstitialDisplayed(AltaInterstialAdview.this);
				// 回调Server PostBack
				Request.postBack(resultAd.getImpression_url());
			}
		}

		public void registerReceiver() {
			IntentFilter localIntentFilter = new IntentFilter();
			localIntentFilter.addAction("com.altamob.ads.interstitial.displayed:" + AltaInterstialAdview.this.uniqueId);
			localIntentFilter.addAction("com.altamob.ads.interstitial.dismissed:" + AltaInterstialAdview.this.uniqueId);
			localIntentFilter.addAction("com.altamob.ads.interstitial.clicked:" + AltaInterstialAdview.this.uniqueId);
			localIntentFilter.addAction("com.altamob.ads.interstitial.impression.logged:" + AltaInterstialAdview.this.uniqueId);
			LocalBroadcastManager.getInstance(AltaInterstialAdview.this.context).registerReceiver(this, localIntentFilter);
		}

		public void unReginsterReciver() {
			try {
				LocalBroadcastManager.getInstance(AltaInterstialAdview.this.context).unregisterReceiver(this);
			} catch (Exception localException) {
			}
		}
	}

}
