package com.skyware.sdk.util;

import java.io.File;

import com.skyware.sdk.api.SDKConfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 持久化帮助类
 * 
 * @author wangyf 2014-12-25
 */
public class PersistHelper {

	private static final String SP_NAME_CONFIG = "skysdk_config";
	private static final String SP_NAME_MQTT = "skysdk_mqtt";
	
	public static SDKConfig readConfig(Context context) {
		if (context != null) {
			SDKConfig config = new SDKConfig();
			SharedPreferences sp = context.getSharedPreferences(SP_NAME_CONFIG, Context.MODE_PRIVATE); //私有数据
			config.setUserId(sp.getString("user_id", ""));
			config.setIsLogin(sp.getBoolean("user_islogin", false));
			return config;
		}
		return null;
	}
	
	public static void saveConfig(Context context, SDKConfig config) {
		if (context != null && config != null) {
			SharedPreferences sp = context.getSharedPreferences(SP_NAME_CONFIG, Context.MODE_PRIVATE); //私有数据
			
			if (sp.getString("user_id", "").equals(config.getUserId())
					|| sp.getBoolean("user_islogin", false) == config.getIsLogin()) {
				updateConfig(context, config);
			} else {
				clearConfig(context);
				updateConfig(context, config);
			}
		}
	}
	
	
	public static void updateConfig(Context context, SDKConfig config) {
		if (context != null && config != null) {
			SharedPreferences sp = context.getSharedPreferences(SP_NAME_CONFIG, Context.MODE_PRIVATE); //私有数据
			Editor editor = sp.edit();//获取编辑器
			if (config.getUserId()!=null && !config.getUserId().equals("")) {
				editor.putString("user_id", 		config.getUserId());
			}
			editor.putBoolean("user_islogin", 		config.getIsLogin());
			
			editor.commit();//提交修改
		}
	}
	
	public static void clearConfig(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME_CONFIG, Context.MODE_PRIVATE); //私有数据
		Editor editor = sp.edit();//获取编辑器
		editor.remove("user_id").remove("user_islogin");
		
		editor.commit();//提交修改
	}
	
	public static boolean delConfig(Context context) {
		if (context != null) {
			File file= new File("/data/data/" + context.getPackageName().toString()
					+ "/shared_prefs", SP_NAME_CONFIG + ".xml");

			if(file.exists()){
				file.delete();
				return true;
			}
		}
		return false;
	}
	
	
	
}
