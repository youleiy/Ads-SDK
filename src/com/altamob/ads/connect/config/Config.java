package com.altamob.ads.connect.config;

public interface Config {

	String readString(String key, String defaultValue);

	Integer readInteger(String key, Integer defaultValue);

	Long readLong(String key, Long defaultValue);

	Double readDouble(String key, Double defaultValue);

	boolean updateInBackground();
}
