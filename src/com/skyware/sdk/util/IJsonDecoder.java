package com.skyware.sdk.util;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * @Description Json编解码接口
 * @author wyf (zgtjwyftc@gmail.com)
 * @date 2014-8-25 上午10:24:09 
 *  
 * @Tips 
 */
public interface IJsonDecoder {
	
	/**  
	 * 从JSONObject中解码并赋值给自身
	 * 
	 * @param json		json对象
	 * @return boolean	是否解码成功
	 * @throws JSONException
	 */
	public boolean jsonDecoder(JSONObject json) throws JSONException;
	
}
