package com.skyware.sdk.entity;

import org.json.JSONArray;
import org.json.JSONException;

public class LierdaDevData extends DevData implements IMCUCoder<JSONArray>{
	
	public LierdaDevData() {
		super();
	}
	public LierdaDevData(DevData data) {
		super(data);
	}
	@Override
	public JSONArray mcuCoder() {
		JSONArray dataArray = new JSONArray();
		if(getPower().equals(POWER_ON)){
			dataArray.put("power::0");
		} else if (getPower().equals(POWER_OFF)) {
			dataArray.put("power::1");
		}
		return dataArray;
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
}