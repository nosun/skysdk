package com.skyware.sdk.cloud;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.entity.DeviceInfo.RemoteNetStat;

/**
 *	HTTP接口编解码类
 *
 *	@author wangyf 2015-1-27
 */
public class HttpEntityCoder {

	public static final String KEY_USR_ID = "loginId";
	public static final String KEY_CMD_CONTENT 	= "commandv";
	public static final String KEY_CMD_DEV_ID 	= "id";
	public static final String KEY_DEVLIST_PAGE 	= "page";
	public static final String KEY_DEVLIST_MAX 	= "max";
	
	public static final String VALUE_CMD_SUCCESS = "true";
	public static final String VALUE_CMD_FAILED = "false";
	
	//{"result":[{"id":"756181644","deviceMac":"eeeee","deviceSn":"202cb962ac59075b9d234b7","deviceName":"testName"}],"respCode":"200",total:”10”}
	public static DeviceInfo[] resolveBindDevList(JSONArray jsonArray) {
		if (jsonArray.length() <= 0) {
			return null;
		} 
		DeviceInfo[] devArray = new DeviceInfo[jsonArray.length()];
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				DeviceInfo dev = new DeviceInfo();
				JSONObject jo = jsonArray.getJSONObject(i);
				if (jo.has("id")) {
					dev.setId(jo.getString("id"));
	//				deviceidBuffer.append(jo.getString("id") + ",");
				}
				if (jo.has("deviceMac")) {
					dev.setMac(jo.getString("deviceMac"));
				}
				if (jo.has("deviceSn")) {
					dev.setSn(jo.getString("deviceSn"));
				}
				if (jo.has("deviceOnline")) {
					if (jo.getString("deviceOnline").equals("1")) {
						dev.setRemoteNetStat(RemoteNetStat.ONLINE);
					} else if (jo.getString("deviceOnline").equals("0")) {
						dev.setRemoteNetStat(RemoteNetStat.OFFLINE);
					}
				}
				
				devArray[i] = dev;
			}

			return devArray;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
