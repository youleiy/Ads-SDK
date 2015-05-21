package com.altamob.ads.connect.config;

public class ConfigFactory {
	private static Config config;
	private static byte[] lock = new byte[128];

	private ConfigFactory() {
	};

	public static Config getConfig() {
		if (null == config) {
			synchronized (lock) {
				if (null == config) {
					config = new AltaConfig();
				}
			}
		}
		return config;
	}
}
