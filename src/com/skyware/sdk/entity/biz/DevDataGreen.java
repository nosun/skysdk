package com.skyware.sdk.entity.biz;

import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.IMCUCoder;
import com.skyware.sdk.util.ConvertUtil;

public class DevDataGreen extends DevData implements IMCUCoder<byte[]>{

	/** pm2.5，有1位小数， 测量范围 0~600μg/m3  */
	private String pm2_5;
	/** PM10，有1位小数， 测量范围 0~600μg/m3  */
	private String pm10;
	/** 温度，2位小数， 测量范围 -20℃~50℃*/
	private String temperature;
	/** 湿度，2位小数， 测量范围 5~95RH% */
	private String humidity;	
	/** CO2，0位小数， 测量范围 0～2,000ppm */
	private String co2;
	/** VOC，3位小数， 测量范围 0– 1.55μg/m3  */
	private String voc;	
	
	public String getPm2_5() {
		return pm2_5;
	}
	public void setPm2_5(String pm2_5) {
		if (this.pm2_5 == null || this.pm2_5.equals("")) {
			dataCount ++;
		}
		this.pm2_5 = pm2_5;
	}
	public String getPm10() {
		return pm10;
	}
	public void setPm10(String pm10) {
		if (this.pm10 == null || this.pm10.equals("")) {
			dataCount ++;
		}
		this.pm10 = pm10;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		if (this.temperature == null || this.temperature.equals("")) {
			dataCount ++;
		}
		this.temperature = temperature;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		if (this.humidity == null || this.humidity.equals("")) {
			dataCount ++;
		}
		this.humidity = humidity;
	}
	public String getCo2() {
		return co2;
	}
	public void setCo2(String co2) {
		if (this.co2 == null || this.co2.equals("")) {
			dataCount ++;
		}
		this.co2 = co2;
	}
	public String getVoc() {
		return voc;
	}
	public void setVoc(String voc) {
		if (this.voc == null || this.voc.equals("")) {
			dataCount ++;
		}
		this.voc = voc;
	}
	
	
	public DevDataGreen() {
		super();
	}
	public DevDataGreen(DevData data) {
		super(data);
	}
	
	
	@Override
	public byte[] mcuCoder() {

		//暂时不需要控制，故此处不处理
		return null;
	}
	
	@Override
	public boolean mcuDecoder(byte[] mcuData) {
		
		if (mcuData.length %4 != 0) {
			return false;
		}
		int dataCount = mcuData.length/4;
		for (int i = 0; i < dataCount; i++) {
			byte[] floatBytes = new byte[4];
			float value;
			System.arraycopy(mcuData, i*4, floatBytes, 0, 4);
			
			value = Float.intBitsToFloat(ConvertUtil.fourBytes2Integer(floatBytes));
			
			switch (i) {
			case 0:
				setPm2_5(value + "");
				break;
			case 1:
				setPm10(value + "");
				break;
			case 2:
				setTemperature(value + "");
				break;
			case 3:
				setHumidity(value + "");
				break;
			case 4:
				setCo2(value + "");
				break;
			case 5:
				setVoc(value + "");
				break;
			default:
				break;
			}
		}
		
		return true;
	}
	
	
	@Override
	public JSONObject jsonEncoder() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setPower(String power) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getPower() {
		// TODO Auto-generated method stub
		return null;
	}

}
