package com.skyware.sdk.packet.entity;

import java.util.HashMap;
import java.util.Map;

public class MooreLegalHelper {
	
	//ret值 Code-Describe Map
	private static final Map<Integer, String> retCodes;
	
	static {
		retCodes = new HashMap<Integer, String>();
		retCodes.put(200, "成功收到协议包");
		retCodes.put(403, "设备未授权");
		retCodes.put(404, "未接收到主控的串口数据");
		retCodes.put(501, "不支持此协议");
		retCodes.put(503, "WiFi模块负载重，请设备稍后再发");
	}
	
	/**
	 * 公共方法:检测sn是否合法，若非法抛出IllegalArgumentException
	 * @param sn	待检测的SN
	 * @return	如果合法返回true
	 */
	public static boolean checkSnValidate(int sn) {
		if (sn < 0 || sn > 65535) {
			throw new IllegalArgumentException("sn is <0 or >65535");
		}
		return true;
	}
	/**
	 * 公共方法:检测ret是否合法，若非法抛出IllegalArgumentException
	 * @param ret	待检测的ret
	 * @return	如果合法返回true
	 */
	public static boolean checkRetValidate(int ret) {
		if (!retCodes.keySet().contains(ret)) {
			throw new IllegalArgumentException("ret code is illegal");
		}
		return true;
	}
	
}
