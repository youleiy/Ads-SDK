package com.altamob.ads;

import com.facebook.ads.Ad;

public abstract interface AltaInterstitialAdListener extends AltaAdListener {
	public abstract void onInterstitialDisplayed(Ad paramAd);

	public abstract void onInterstitialDismissed(Ad paramAd);
}