package com.altamob.ads;

import com.altamob.ads.connect.model.Creative;
import com.facebook.ads.NativeAd.Image;

public class AltaImage {
	private final String url;
	private final int width;
	private final int height;

	private AltaImage(String paramString, int paramInt1, int paramInt2) {
		this.url = paramString;
		this.width = paramInt1;
		this.height = paramInt2;
	}

	public String getUrl() {
		return this.url;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public static AltaImage convertByCreative(Creative creative) {
		if (creative == null)
			return null;
		AltaImage altaImage = new AltaImage(creative.getUrl(), creative.getWidth(), creative.getHeight());
		return altaImage;
	}

	public static AltaImage covertByFbImage(Image image) {
		if (image == null)
			return null;
		AltaImage altaImage = new AltaImage(image.getUrl(), image.getWidth(), image.getHeight());
		return altaImage;
	}

	@Override
	public String toString() {
		return "AltaImage [url=" + url + ", width=" + width + ", height=" + height + "]";
	}

}