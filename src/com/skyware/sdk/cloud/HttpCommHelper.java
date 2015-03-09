package com.skyware.sdk.cloud;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.util.HttpUtils;
import com.skyware.sdk.util.HttpUtils.HttpResult;

/**
 *	HTTP接口通信类
 *
 *	@author wangyf 2014-12-25
 */
public class HttpCommHelper {
	
	 
	/*	200 -- success
	 	400 -- 访问错误，bad request
	 	403 -- 没权限，session
		404 -- 不存在指定资源
	 	500 -- 服务器异常，数据库错误*/
	 
	public static final String KEY_RESPCODE = "respCode";
	public static final String KEY_RESULT = "result";
	
	public static final String KEY_USR_ID = "loginId";
	public static final String KEY_CMD_CONTENT 	= "commandv";
	public static final String KEY_CMD_DEV_ID 	= "id";
	public static final String KEY_DEVLIST_PAGE 	= "page";
	public static final String KEY_DEVLIST_MAX 	= "max";
	public static final String KEY_DEVLIST_NUM 	= "total";
	
	public static final String VALUE_RESPCODE_SUCCESS = "200";
//	public static final String VALUE_FAILED = "not exist";
//	public static final String VALUE_LOGIN_SUCCESS = "sucess login";
	public static final String VALUE_CMD_SUCCESS = "true";
	public static final String VALUE_CMD_FAILED = "false";
	
	// 接口ip
	public static String CLOUDSERVIER = "http://yun.skyware.com.cn:8080";
	// 获取所有的绑定设备数据
	public static String URL_DEVICE_BINDINGDEVICES = CLOUDSERVIER + "/mdot/device/bindDevices";
	// 控制设备接口
	public static String URL_CONTROL=CLOUDSERVIER+"/m/message/sendMg";
	
	/** 
	 *	获取所有绑定的设备数据
	 *
	 *	
	 *	@param mobileNo		手机号
	 *	@return
	 *	@throws HttpException
	 */
	public static DeviceInfo[] getAllBindDevices(String userId) 
			throws HttpException{
		String url = URL_DEVICE_BINDINGDEVICES;
		HttpResult result = null;
		int statusCode;
		try {
			//HTTP Post请求
			Map<String, String> params = new HashMap<String, String>();
			params.put(KEY_USR_ID, 		userId);
			params.put(KEY_DEVLIST_PAGE, 	"1");
			params.put(KEY_DEVLIST_MAX, 	"20");
			
			result = HttpUtils.doPost(url, params);
			
			statusCode = result.getStatusCode();
			JSONObject json = new JSONObject(result.getRespString());
			switch (statusCode) {
			case 200:
				if (json.getString(KEY_RESPCODE).contains(VALUE_RESPCODE_SUCCESS)) {
					if (json.getJSONArray(KEY_RESULT) != null) {
						return HttpEntityCoder.resolveBindDevList(json.getJSONArray(KEY_RESULT));
					}
				}
				break;
			default:
				throw new HttpException(statusCode);
			}

		} catch (IOException e) {
			e.printStackTrace();
			if (e instanceof SocketTimeoutException) {
				throw new HttpException(true);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *	发送指令
	 *	commandv={"sn":1,"cmd":"download","data":["uv::0"]} & loginId=843849188 & id=832250229
	 *
	 *	@param user
	 *	@return
	 *	@throws HttpException
	 */
	public static Boolean sendDevCmd(String userId, String devId, String cmdStr) 
			throws HttpException{
		
		HttpResult result = null;
		int statusCode;
		String respString;
		try {
			//HTTP Post请求
			Map<String, String> params = new HashMap<String, String>();
			if (userId != null && !userId.equals("")) {
				params.put(KEY_USR_ID, 			userId);
			}
			if (devId != null && !devId.equals("")) {
				params.put(KEY_CMD_DEV_ID, 		devId);
			}
			if (cmdStr != null && !cmdStr.equals("")) {
				params.put(KEY_CMD_CONTENT, 	cmdStr);
			}
			result = HttpUtils.doPost(URL_CONTROL, params);
			
			if (result != null) {
				statusCode = result.getStatusCode();
				respString = result.getRespString();
				switch (statusCode) {
				case 200:
					if (respString != null){
						if (respString.contains(VALUE_CMD_SUCCESS)) {
							return true;
						}
						if (respString.contains(VALUE_CMD_FAILED)) {
							return false;
						}
					}
					break;
				default:
					throw new HttpException(statusCode);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (e instanceof SocketTimeoutException) {
				throw new HttpException(true);
			}
		}
		return null;
	}
	

	
//	private static int getErrCodeId(int errCode) {
//		switch (errCode) {
//		case 400:
//			return R.string.http_err_400;
//		case 403:
//			return R.string.http_err_403;
//		case 404:
//			return R.string.http_err_404;
//		case 500:
//			return R.string.http_err_500;
//		default:
//			return -1;
//		}
//	}

	
	public static class HttpException extends Exception{
		private static final long serialVersionUID = -4951224153222466996L;
		private int statusCode;
		private boolean isTimeout;
		
		public HttpException(int statusCode) {
			this.statusCode = statusCode;
		}
		public HttpException(boolean isTimeout) {
			this.isTimeout = isTimeout;
		}
		public int getStatusCode() {
			return statusCode;
		}
		public boolean isTimeout() {
			return isTimeout;
		}
	}
}
