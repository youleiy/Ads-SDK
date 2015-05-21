package com.altamob.ads.view;

import android.content.Context;

import com.altamob.ads.connect.util.StopWatch;

public class AltaAd {
	protected static final String TAG = AltaAd.class.getSimpleName();
	protected String placementId;
	protected StopWatch fbstopWatch = new StopWatch();
	protected StopWatch altstopWatch = new StopWatch();
	protected boolean isReturn;
	protected int REQUEST_FAN_TIMEOUT;
	protected Boolean lock = false;
	protected Context context;

}
