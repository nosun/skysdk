package com.skyware.sdk.util;

public class ConvertUtil {
	
	
	public static String macLong2String(long mac) {
		if (mac > 0XFFFFFFFFFFFFL) {
			return null;
		}
		
		return Long.toHexString(mac).toUpperCase();
	}
	
	public static long macString2Long(String mac) {
		if (!ValidateHelper.isMac(mac)) {
			return -1;
		}
		
		return Long.parseLong(mac.replaceAll("[-:：]", ""), 16);
	}

	public static long macByte2Long(byte[] b) {
		return macByte2Long(b, false);
	}
	public static long macByte2Long(byte[] b, boolean isBigEndian) {
		long mac = 0;
		if (b.length < 6) {
			return -1;
		}
		for (int i = 0; i < 6; i++) {
			if (isBigEndian) {
				mac |= (((long)(b[i] & 0xFF)) << 8*(5-i)) ;
			} else {
				mac |= (((long)(b[i] & 0xFF)) << 8*i) ;
			}
		}
		return mac;
	}
}
