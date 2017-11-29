package site.lankui.impaler.util;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Generator {

	public static int clientId(String name) {
		String md5 = generateMD5(name);
		int sum = 0;
		for (char c : md5.toCharArray()) {
			sum += c;
		}
		return sum;
	}

	private static String generateMD5(String name) {
		String md5;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] buff = digest.digest(name.getBytes());
			md5 = bytesToHex(buff);
		} catch (NoSuchAlgorithmException e) {
			return name;
		}
		return md5;
	}

	private static String bytesToHex(byte[] md5Array) {
		BigInteger bigInt = new BigInteger(1, md5Array);
		return bigInt.toString(16);
	}
}
