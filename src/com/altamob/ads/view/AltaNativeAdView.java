package com.altamob.ads.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.altamob.ads.AdError;
import com.altamob.ads.AltaImage;
import com.altamob.ads.AltaNativeAdListener;
import com.altamob.ads.NativeAd;
import com.altamob.ads.connect.Request;
import com.altamob.ads.connect.config.Config;
import com.altamob.ads.connect.config.ConfigFactory;
import com.altamob.ads.connect.model.ResultAd;
import com.altamob.ads.connect.model.ResultObject;
import com.altamob.ads.connect.request.CallBack;
import com.altamob.ads.connect.util.BuildJsonUtil;
import com.altamob.ads.connect.util.HttpUtil;
import com.facebook.ads.Ad;

public class AltaNativeAdView implements Ad {
	private static final String LOG_TAG = AltaNativeAdView.class.getSimpleName();
	private AltaNativeAdListener altAdListener;
	private String placementId;
	private Context context;
	private int NATIVE_AD_WIDTH;
	private int NATIVE_AD_HEIGHT;
	private List<NativeAd> nativeAds;
	private int limit;
	private ExecutorService executeService = Executors.newFixedThreadPool(3);;
	private String requestTemplate;

	public AltaNativeAdView(Context context, String placementId) {
		this.placementId = placementId;
		this.context = context;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (altAdListener != null)
					altAdListener.onAltaAdloaded(AltaNativeAdView.this);
				break;
			case 2:
				if (altAdListener != null)
					altAdListener.onAltaAdError(AltaNativeAdView.this, (AdError) msg.obj);
				break;
			default:
				break;
			}
		}
	};

	public void loadAd() {
		Request request = new Request(context, placementId);
		initConfig();
		request.createRequestTask(NATIVE_AD_WIDTH, NATIVE_AD_HEIGHT, limit, requestTemplate, new CallBack() {
			@Override
			public void OnFailure(AdError adError) {
				sendErrorMsg(adError);
			}

			@Override
			public void OnCompleted(String result) {
				ResultObject resultObject = BuildJsonUtil.getResultObject(result);
				if (null != resultObject && resultObject.getResult() != null && resultObject.getResult().size() > 0) {
					List<ResultAd> resultAds = resultObject.getResult();
					nativeAds = convert2NativeAds(resultAds);
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

	public List<NativeAd> getNativeAds() {
		return nativeAds;
	}

	private List<NativeAd> convert2NativeAds(List<ResultAd> resultAds) {
		if (resultAds == null || resultAds.size() == 0)
			return new ArrayList<NativeAd>();
		List<NativeAd> nativeAds = new ArrayList<NativeAd>();
		for (ResultAd resultAd : resultAds) {
			final NativeAd nativeAd = new NativeAd();
			nativeAd.setAdBody(resultAd.getDescription());
			nativeAd.setAdTitle(resultAd.getTitle());
			nativeAd.setAdBody(resultAd.getDescription());
			nativeAd.setAdCoverImage(AltaImage.convertByCreative(resultAd.getCreative()));
			nativeAd.setRating(resultAd.getRating());
			nativeAd.setClickUrl(resultAd.getClick_url());
			nativeAd.setAdIcon(resultAd.getLogo());
			nativeAd.setImpressionUrl(resultAd.getImpression_url());
			nativeAd.setLikeCount(resultAd.getLike_count());
			nativeAd.setDisplayStyle(resultAd.getDisplay_style());
			nativeAd.setWeek(resultAd.getWeek());
			nativeAd.setAuthor(resultAd.getAuthor());
			nativeAd.setBanner_head(resultAd.getBanner_head());
			nativeAd.setFileSize(resultAd.getFile_size());
			nativeAd.setCategory(resultAd.getCategory());
			nativeAds.add(nativeAd);
			executeService.execute(new Runnable() {
				@Override
				public void run() {
					HttpUtil.postBack(nativeAd.getImpressionUrl(), 3);
				}
			});
		}
		return nativeAds;
	}

	private void initConfig() {
		Config config = ConfigFactory.getConfig();
		NATIVE_AD_WIDTH = config.readInteger("NATIVE_AD_WIDTH", 1024);
		NATIVE_AD_HEIGHT = config.readInteger("NATIVE_AD_HEIGHT", 768);
		// Log.i("NATIVE_AD_WIDTH", String.valueOf(NATIVE_AD_WIDTH));
		// Log.i("NATIVE_AD_HEIGHT", String.valueOf(NATIVE_AD_HEIGHT));
	}

	private void sendErrorMsg(AdError adError) {
		Message message = handler.obtainMessage();
		message.what = 2;
		message.obj = adError;
		handler.sendMessage(message);
	}

	@Override
	public void destroy() {

	}

	public void setAltAdListener(AltaNativeAdListener altAdListener) {
		this.altAdListener = altAdListener;
	}

	public AltaNativeAdListener getAltAdListener() {
		return altAdListener;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setRequestTemplate(String requestTemplate) {
		this.requestTemplate = requestTemplate;
	}
}