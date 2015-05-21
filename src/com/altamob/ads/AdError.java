package com.altamob.ads;

public class AdError {
	public static final int NETWORK_ERROR_CODE = 1000;
	public static final int NO_FILL_ERROR_CODE = 1001;
	public static final int LOAD_TOO_FREQUENTLY_ERROR_CODE = 1002;
	public static final int SERVER_ERROR_CODE = 2000;
	public static final int INTERNAL_ERROR_CODE = 2001;
	public static final AdError NETWORK_ERROR = new AdError(1000, "Network Error");
	public static final AdError NO_FILL = new AdError(1001, "No Fill");
	public static final AdError LOAD_TOO_FREQUENTLY = new AdError(1002, "Ad was re-loaded too frequently");
	public static final AdError SERVER_ERROR = new AdError(2000, "Server Error");
	public static final AdError INTERNAL_ERROR = new AdError(2001, "Internal Error");

	private final int errorCode;
	private final String errorMessage;

	public AdError(int paramInt, String paramString) {
		this.errorCode = paramInt;
		this.errorMessage = paramString;
	}

	public int getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}
