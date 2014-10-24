package com.skyware.sdk.entity.biz;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.IMCUCoder;

public class DevDataLierda extends DevData implements IMCUCoder<JSONArray>{
	
	private String power;
	
	public static final String POWER_ON = "0";
	public static final String POWER_OFF = "1";
	public static final String POWER_NAME = "power";
	
	public DevDataLierda() {
		super();
	}
	public DevDataLierda(DevData data) {
		super(data);
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
	
	@Override
	public JSONObject jsonEncoder() throws JSONException {
		
		JSONObject json = new JSONObject();
		
		if (getPower() != null && getPower() != "") {
			json.put(POWER_NAME, getPower());
		}
		
		return json;
	}
}