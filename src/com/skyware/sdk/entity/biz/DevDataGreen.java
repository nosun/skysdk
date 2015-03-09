package com.skyware.sdk.entity.biz;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.util.ConvertUtil;

public class DevDataGreen extends DevData{

	/** 温度，2位小数， 测量范围 -20℃~50℃*/
	private String temperature;
	/** 湿度，2位小数， 测量范围 5~95RH% */
	private String humidity;	
	
	/** CO2，Co2实时测量值, 0位小数， 测量范围 0～2,000ppm */
	private String co2_m;
	/** CO2，1~60分钟移动平均值, 0位小数， 测量范围 0～2,000ppm */
	private String co2_h;
	/** CO2，1~24小时移动平均值, 0位小数， 测量范围 0～2,000ppm */
	private String co2_d;
	/** VOC，1分钟平均值测量值， 3位小数， 测量范围 0– 4mg∕m3 */
	private String voc_m;	
	/** VOC，1~60分钟移动平均值，3位小数， 测量范围 0– 4mg∕m3 */
	private String voc_h;	
	/** VOC，1~8小时移动平均值，3位小数， 测量范围 0– 4mg∕m3 */
	private String voc_d;	
	
	/** pm2.5，1分钟平均测量值，有1位小数，测量范围 0~600μg/m3  */
	private String pm2_5_m;
	/** pm2.5，1~60分钟平均测量值，有1位小数， 测量范围 0~600μg/m3  */
	private String pm2_5_h;
	/** pm2.5，1~24小时平均测量值，有1位小数， 测量范围 0~600μg/m3  */
	private String pm2_5_d;
	
	/** PM10，1分钟平均测量值，有1位小数， 测量范围 0~600μg/m3  */
	private String pm10_m;
	/** PM10，1~60分钟平均测量值，有1位小数， 测量范围 0~600μg/m3  */
	private String pm10_h;
	/** PM10，1~24小时平均测量值， 测量范围 0~600μg/m3  */
	private String pm10_d;
	
	public String getPm2_5AvgMin() {
		return pm2_5_m;
	}
	public void setPm2_5AvgMin(String pm2_5_m) {
		if (this.pm2_5_m == null || this.pm2_5_m.equals("")) {
			dataCount ++;
		}
		this.pm2_5_m = pm2_5_m;
	}
	public String getPm2_5AvgHour() {
		return pm2_5_h;
	}
	public void setPm2_5AvgHour(String pm2_5_h) {
		if (this.pm2_5_h == null || this.pm2_5_h.equals("")) {
			dataCount ++;
		}
		this.pm2_5_h = pm2_5_h;
	}
	public String getPm2_5AvgDay() {
		return pm2_5_d;
	}
	public void setPm2_5AvgDay(String pm2_5_d) {
		if (this.pm2_5_d == null || this.pm2_5_d.equals("")) {
			dataCount ++;
		}
		this.pm2_5_d = pm2_5_d;
	}
	
	public String getPm10AvgMin() {
		return pm10_m;
	}
	public void setPm10AvgMin(String pm10_m) {
		if (this.pm10_m == null || this.pm10_m.equals("")) {
			dataCount ++;
		}
		this.pm10_m = pm10_m;
	}
	public String getPm10AvgHour() {
		return pm10_h;
	}
	public void setPm10AvgHour(String pm10_h) {
		if (this.pm10_h == null || this.pm10_h.equals("")) {
			dataCount ++;
		}
		this.pm10_h = pm10_h;
	}
	public String getPm10AvgDay() {
		return pm10_d;
	}
	public void setPm10AvgDay(String pm10_d) {
		if (this.pm10_d == null || this.pm10_d.equals("")) {
			dataCount ++;
		}
		this.pm10_d = pm10_d;
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
	public String getCo2AvgMin() {
		return co2_m;
	}
	public void setCo2AvgMin(String co2_m) {
		if (this.co2_m == null || this.co2_m.equals("")) {
			dataCount ++;
		}
		this.co2_m = co2_m;
	}
	public String getCo2AvgHour() {
		return co2_h;
	}
	public void setCo2AvgHour(String co2_h) {
		if (this.co2_h == null || this.co2_h.equals("")) {
			dataCount ++;
		}
		this.co2_h = co2_h;
	}
	public String getCo2AvgDay() {
		return co2_d;
	}
	public void setCo2AvgDay(String co2_d) {
		if (this.co2_d == null || this.co2_d.equals("")) {
			dataCount ++;
		}
		this.co2_d = co2_d;
	}
	public String getVocAvgMin() {
		return voc_m;
	}
	public void setVocAvgMin(String voc_m) {
		if (this.voc_m == null || this.voc_m.equals("")) {
			dataCount ++;
		}
		this.voc_m = voc_m;
	}
	public String getVocAvgHour() {
		return voc_h;
	}
	public void setVocAvgHour(String voc_h) {
		if (this.voc_h == null || this.voc_h.equals("")) {
			dataCount ++;
		}
		this.voc_h = voc_h;
	}
	public String getVocAvgDay() {
		return voc_d;
	}
	public void setVocAvgDay(String voc_d) {
		if (this.voc_d== null || this.voc_d.equals("")) {
			dataCount ++;
		}
		this.voc_d = voc_d;
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
	public boolean mcuDecoder(Object obj) {
		if (obj != null && obj instanceof byte[]) {
			byte[] mcuData = (byte[])obj;
		
			if (mcuData.length %4 != 0) {
				return false;
			}
			int dataCount = mcuData.length/4;
			for (int i = 0; i < dataCount; i++) {
				byte[] floatBytes = new byte[4];
				float value;
				System.arraycopy(mcuData, i*4, floatBytes, 0, 4);
				
				value = Float.intBitsToFloat(ConvertUtil.fourBytes2Integer(floatBytes));
				
				DecimalFormat decimalFormat2 = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
				DecimalFormat decimalFormat3 = new DecimalFormat("0.000");
				
				switch (i) {
				case 0:
					setVocAvgHour(decimalFormat3.format(value) + "");
					break;
				case 1:
					setCo2AvgHour((int)value + "");
					break;
				case 2:
					setPm10AvgHour(value + "");
					break;
				case 3:
					setPm2_5AvgHour(value + "");
					break;
				case 4:
					setCo2AvgMin((int)value + "");
					break;
				case 5:
					setTemperature(decimalFormat2.format(value) + "");
					break;
				case 6:
					setHumidity(decimalFormat2.format(value) + "");
					break;
				case 7:
					setPm2_5AvgMin(value + "");
					break;
				case 8:
					setPm10AvgMin(value + "");
					break;
				case 9:
					setVocAvgMin(decimalFormat3.format(value) + "");
					break;
				case 10:
					setPm2_5AvgDay(value + "");
					break;
				case 11:
					setPm10AvgDay(value + "");
					break;
				case 12:
					setCo2AvgDay((int)value + "");
					break;
				case 13:
					setVocAvgDay(decimalFormat3.format(value) + "");
					break;
				default:
					break;
				}
			}
			return true;
		}
		return false;
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
