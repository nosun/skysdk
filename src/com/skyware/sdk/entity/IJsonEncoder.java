package com.skyware.sdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * Json编码接口
 * @author wyf (zgtjwyftc@gmail.com)
 * @date 2014-8-25 上午10:24:09 
 */
public interface IJsonEncoder {
	
	/** 
	 * 将自身所有属性（非空）编码封装成JsonObject
	 * 
	 * @return JSONObject json对象
	 * @throws JSONException
	 */
	public JSONObject jsonEncoder() throws JSONException;
	
}
