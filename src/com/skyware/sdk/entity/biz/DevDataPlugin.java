package com.skyware.sdk.entity.biz;

import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.IMCUCoder;


public class DevDataPlugin extends DevData implements IMCUCoder<Byte[]>{

	private String power;
	
	public static final String POWER_ON = "0";
	public static final String POWER_OFF = "1";
	public static final String POWER_NAME = "power";
	
	public DevDataPlugin() {}
	public DevDataPlugin(DevData data) {
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
	public Byte[] mcuCoder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean mcuDecoder(Byte[] mcuData) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	public JSONObject jsonEncoder() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

}
