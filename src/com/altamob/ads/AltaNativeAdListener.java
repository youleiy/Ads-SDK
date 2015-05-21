package com.altamob.ads;

import com.facebook.ads.Ad;

public abstract interface AltaNativeAdListener {
	public abstract void onAltaAdloaded(Ad ad);

	public abstract void onAltaAdError(Ad ad, AdError adError);

}
