package com.paypal.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.paypal.core.LoggingManager;

public class EncryptionUtil {

	private static final char[] pssChar = "changeit!".toCharArray();

	private static final byte[] salt = {

	(byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,

	(byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99

	};

	// Iteration count
	private static final int count = 20;

	private static final PBEKeySpec pbeKeySpec = new PBEKeySpec(pssChar);

	private static final PBEParameterSpec pbeParamSpec = new PBEParameterSpec(
			salt, count);

	private static SecretKeyFactory keyFac;

	private static SecretKey pbeKey;

	private static Cipher pbeEncryptCipher;

	private static Cipher pbeDecryptCipher;

	public EncryptionUtil() {

	}

	static {
		try {
			keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			pbeKey = keyFac.generateSecret(pbeKeySpec);
			pbeEncryptCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeDecryptCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeEncryptCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
			pbeDecryptCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
		} catch (Exception ex) {
			LoggingManager.severe(EncryptionUtil.class, ex.getMessage(), ex);
		}

	}

	public static String encrypt(String originaltext) {
		String returnStr = null;
		try {
			byte[] cleartext = originaltext.getBytes();
			byte[] ciphertext = pbeEncryptCipher.doFinal(cleartext);
			returnStr = Arrays.toString(ciphertext);
		} catch (Exception ex) {
			LoggingManager.severe(EncryptionUtil.class, ex.getMessage(), ex);
		}
		return returnStr;
	}

	public static String decrypt(String ciphertext) {
		String returnStr = null;
		try {
			byte[] cipherBytes = getBytes(ciphertext);
			byte[] originalText = pbeDecryptCipher.doFinal(cipherBytes);
			returnStr = new String(originalText);
		} catch (Exception ex) {
			LoggingManager.severe(EncryptionUtil.class, ex.getMessage(), ex);
		}
		return returnStr;
	}

	private static byte[] getBytes(String s) {
		s = s.substring(1, s.length() - 1);
		String parts[] = s.split(",");
		byte[] t = new byte[parts.length];
		int i = -1;
		for (String part : parts) {
			t[++i] = (byte) Byte.parseByte(part.trim());
		}
		return t;
	}
	
	public static void main(String[] args) {
		String name = "ttt";
		String t = EncryptionUtil.encrypt(name);
		System.out.println(t);
		System.out.println(EncryptionUtil.decrypt(t));
	}

}
