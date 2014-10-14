package com.skyware.sdk.entity;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DevData implements IMCUCoder<JSONArray>, IJsonEncoder{
	
	private String power;
	
	public static final String POWER_ON = "0";
	public static final String POWER_OFF = "1";
	public static final String POWER_NAME = "power";
	
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	
	@Override
	public JSONArray mcuCoder() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean mcuDecoder(JSONArray mcuData) {
		try {
			String data[] = new String [mcuData.length()];
			for (int i = 0; i < mcuData.length(); i++) {
					data[i] = mcuData.getString(i);
			}
			for (String d: data) {
				if (d.equals("power::0")) {
					setPower(POWER_ON);
				} else if (d.equals("power::1")) {
					setPower(POWER_OFF);
				}
			}
			
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public JSONObject jsonEncoder() throws JSONException {
		
		JSONObject json = new JSONObject();
		
		if (getPower() != null && getPower() != "") {
			json.put(POWER_NAME, getPower());
		}
		
		return null;
	}


}