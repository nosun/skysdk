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
				mac |= (((long) (b[i] & 0xFF)) << 8 * (5 - i));
			} else {
				mac |= (((long) (b[i] & 0xFF)) << 8 * i);
			}
		}
		return mac;
	}
	public static byte[] macLong2Byte(long mac) {
		return macLong2Byte(mac, false);
	}

	public static byte[] macLong2Byte(long mac, boolean isBigEndian) {
		byte[] macByte = new byte[6];
		for (int i = 0; i < 6; i++) {
			if (isBigEndian) {
				macByte[i] = (byte) (mac >>> 8*(5 - i) & 0xFF);
			} else {
				macByte[i] = (byte) (mac >>> 8*i & 0xFF);
			}
		}
		return macByte;
	}

	public static byte[] snUnsignedShort2Byte(int s) {
		if (s < 0 || s > 65535) {
			return null;
		}
		byte[] shortByte = new byte[2];
		shortByte[0] = (byte) ((s >>> 8) & 0xff);
		shortByte[1] = (byte) ((s >>> 0) & 0xff);
		return shortByte;
	}
	public static int snByte2UnsignedShort(byte[] b) {
		return snByte2UnsignedShort(b, true);
	}

	public static int snByte2UnsignedShort(byte[] b, boolean isBigEndian) {
		int sn = 0;
		if (b.length != 2) {
			return -1;
		}
		for (int i = 0; i < 2; i++) {
			if (isBigEndian) {
				sn |= (((short) (b[i] & 0xFF)) << 8 * (1 - i));
			} else {
				sn |= (((short) (b[i] & 0xFF)) << 8 * i);
			}
		}
		return sn;
	}
	
	public static int fourBytes2Integer(byte[] floatBytes) {
		
		if (floatBytes.length != 4) {
			throw new IllegalArgumentException("FloutValue byteArray's length must be 4");
		}
		int ret = 0;
		
		ret |= (floatBytes[0] & 0xff)<<8*3;
		ret |= (floatBytes[1] & 0xff)<<8*2;
		ret |= (floatBytes[2] & 0xff)<<8*1;
		ret |= (floatBytes[3] & 0xff)<<8*0;
		
		return ret;
	}
}
