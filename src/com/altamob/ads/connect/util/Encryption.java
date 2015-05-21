package com.altamob.ads.connect.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.util.Base64;
import android.util.Log;

import com.altamob.ads.connect.config.ConfigFactory;

public class Encryption {

	@SuppressLint("TrulyRandom")
	public static String encrypt(String data) throws Exception {
		try {
			String key = ConfigFactory.getConfig().readString("AES_KEY", "EWERWRREW4567i8o");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			int blockSize = cipher.getBlockSize();

			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}

			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(key.getBytes());

			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);

			return Base64.encodeToString(encrypted, Base64.CRLF).trim();

		} catch (Exception e) {
			Log.e("encrypt error", e.toString());
			return null;
		}
	}

	public static String desEncrypt(String data) {
		try {
			String key = ConfigFactory.getConfig().readString("AES_KEY", "EWERWRREW4567i8o");
			byte[] encrypted1 = Base64.decode(data, Base64.CRLF);
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(key.getBytes());

			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			Log.e("desEncrypt error", e.toString());
			return null;
		}
	}
}