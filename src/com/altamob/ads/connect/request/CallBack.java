package com.altamob.ads.connect.request;

import com.altamob.ads.AdError;

public interface CallBack {
	void OnCompleted(String result);

	void OnFailure(AdError adError);
}
