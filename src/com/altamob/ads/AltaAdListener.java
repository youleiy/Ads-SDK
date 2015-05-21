package com.altamob.ads;

import com.facebook.ads.Ad;

public abstract interface AltaAdListener {
	public abstract void onAltaAdClick(Ad ad);

	public abstract void onAltaAdloaded(Ad ad);

	public abstract void onAltaAdError(Ad ad, AdError adError);

}
