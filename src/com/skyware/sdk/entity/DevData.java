package com.skyware.sdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class DevData implements IJsonEncoder{
	
	/** 有效data字段的数量 */
	private int dataCount;
	
	private String power;
	
	public static final String POWER_ON = "0";
	public static final String POWER_OFF = "1";
	public static final String POWER_NAME = "power";
	
	public DevData() {}
	public DevData(DevData cpy) {
		this.dataCount = cpy.dataCount;
		this.power = cpy.power;
		//TODO 拷贝构造函数
	}
	
	public int getDataCount() {
		return this.dataCount;
	}
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		if (power != null && (power.equals(POWER_ON) || power.equals(POWER_OFF) )) {
			if (this.power == null || this.power.equals("")) {
				dataCount ++;
			}
			this.power = power;
		}
	}

	@Override
	public JSONObject jsonEncoder() throws JSONException {
		
		JSONObject json = new JSONObject();
		
		if (getPower() != null && getPower() != "") {
			json.put(POWER_NAME, getPower());
		}
		
		return json;
	}
}