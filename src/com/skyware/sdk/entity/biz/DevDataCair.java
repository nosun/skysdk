package com.skyware.sdk.entity.biz;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.entity.DevData;

public class DevDataCair extends DevData{
	//控制量
	/** 开关 power::on|off */
	private String power;
	/** led屏开关 iaqled::on|off*/
	private String light;	
//	/** 模式 [0-2+] |0-手动 1-自动 2-睡眠 （3-节能）*/
//	private String mode;
	/** 定时开（小时） 0-12小时,0为取消设置*/
	private String timerOn;
	/** 定时关（小时） 0-12小时,0为取消设置*/
	private String timerOff;
	/** 风速 [lv1 - lv5] & 自动档lva */
	private String fanSpeed;
	
	//状态量
	/** PM等级 [1-5] | lv1-lv5,lv1为最优*/
	private String pm25Level;
	/** 滤网时间 [0~6000] 4 byte 单位天*/
	private String filterUseTime;
	
//	public static enum MODE{
//		MANUAL,
//		AUTO,
//		SLEEP,
//		SAVING
//	}
//	@SuppressWarnings("serial")
//	public static final HashMap<MODE, String> MODE_MAP = new HashMap<DevDataCair.MODE, String>(){{
//		put(MODE.MANUAL, VALUE_MODE_MANUAL);
//		put(MODE.AUTO, VALUE_MODE_AUTO);
//		put(MODE.SLEEP, VALUE_MODE_SLEEP);
//		put(MODE.SAVING, VALUE_MODE_SAVING);
//	}};
	
	// 控制类 对应ON OFF
	public static final String KEY_POWER = "power";
	public static final String KEY_LIGHT = "iaqled";
//	public static final String KEY_MODE = "mo";
	// 设置类 对应范围值
	public static final String KEY_TIMER_ON = "timeron";
	public static final String KEY_TIMER_OFF = "timeroff";
	public static final String KEY_FANSPEED = "windspeed";
	// 状态类 Wifi To MCU 0-1表示是否获取；MCU To Wifi 对应范围值
	public static final String KEY_PM25 = "pm25lv";
	public static final String KEY_FILTERTIME = "filter";
	
	
	public static final String KV_DELIMITER = "::";
	
	public static final String VALUE_ON_STATE 	= "on";
	public static final String VALUE_OFF_STATE 	= "off";
//	
//	public static final String VALUE_MODE_MANUAL 	= "0";
//	public static final String VALUE_MODE_AUTO 	= "1";	
//	public static final String VALUE_MODE_SLEEP 	= "2";	
//	public static final String VALUE_MODE_SAVING 	= "3";
	
	public static final String VALUE_TIMER_OFF 	= "0";
	public static final String VALUE_TIMER_MIN 	= "1";
	public static final String VALUE_TIMER_MAX 	= "12";
	
	public static final String VALUE_FANSPEED_AUTO = "lva";
	public static String VALUE_FANSPEED_PRE = "lv";
	
	public static final String VALUE_FILTER_TIME_NULL = "00000";
	public static final String VALUE_FILTER_TIME_MIN = "0";
	public static final String VALUE_FILTER_TIME_MAX = "6000";
	
	public static final String VALUE_PM25_NULL = "0000";
	public static String VALUE_PM25_LEVEL_PRE = "lv";
	
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		if (this.power == null || this.power.equals("")) {
			dataCount ++;
		}
		this.power = power;
	}
	public String getLight() {
		return light;
	}
	public void setLight(String light) {
		if (this.light == null || this.light.equals("")) {
			dataCount ++;
		}
		this.light = light;
	}
	public String getTimerOn() {
		return timerOn;
	}
	public void setTimerOn(String timerOn) {
		if (this.timerOn == null || this.timerOn.equals("")) {
			dataCount ++;
		}
		this.timerOn = timerOn;
	}
	public String getTimerOff() {
		return timerOff;
	}
	public void setTimerOff(String timerOff) {
		if (this.timerOff == null || this.timerOff.equals("")) {
			dataCount ++;
		}
		this.timerOff = timerOff;
	}
	public String getFanSpeed() {
		return fanSpeed;
	}
	public void setFanSpeed(String fanSpeed) {
		if (this.fanSpeed == null || this.fanSpeed.equals("")) {
			dataCount ++;
		}
		this.fanSpeed = fanSpeed;
	}
//	public String getMode() {
//		return mode;
//	}
//	public void setMode(String mode) {
//		if (this.mode == null || this.mode.equals("")) {
//			dataCount ++;
//		}
//		this.mode = mode;
//	}
	public String getFilterUseTime() {
		return filterUseTime;
	}
	public void setFilterUseTime(String filterUseTime) {
		if (this.filterUseTime == null || this.filterUseTime.equals("")) {
			dataCount ++;
		}
		this.filterUseTime = filterUseTime;
	}
	public String getPm25Level() {
		if (this.pm25Level == null || this.pm25Level.equals("")) {
			dataCount ++;
		}
		return pm25Level;
	}
	public void setPm25Level(String pm25Level) {
		this.pm25Level = pm25Level;
	}
	
	public DevDataCair() {
		super();
	}
	public DevDataCair(DevData data) {
		super(data);
	}
	
	@Override
	public JSONArray mcuCoder() {
		JSONArray dataArray = new JSONArray();
//		if(getPower().equals(POWER_ON)){
//			dataArray.put("power::0");
//		} else if (getPower().equals(POWER_OFF)) {
//			dataArray.put("power::1");
//		}
		if (getPower() != null && !getPower().equals("")) {
			dataArray.put(KEY_POWER + KV_DELIMITER + getPower());
		}
		if (getLight() != null && !getLight().equals("")) {
			dataArray.put(KEY_LIGHT + KV_DELIMITER + getLight());
		}
//		if (getMode() != null && !getMode().equals("")){
//			dataArray.put(KEY_MODE + KV_DELIMITER + getMode());
//		}
		if (getTimerOn() != null && !getTimerOn().equals("")){
			dataArray.put(KEY_TIMER_ON + KV_DELIMITER + getTimerOn());
		}
		if (getTimerOff() != null && !getTimerOff().equals("")){
			dataArray.put(KEY_TIMER_OFF + KV_DELIMITER + getTimerOff());
		}
		if (getFanSpeed() != null && !getFanSpeed().equals("")){
			dataArray.put(KEY_FANSPEED + KV_DELIMITER + getFanSpeed());
		}
//		if (getTempHum() != null && !getTempHum().equals("")){
//			dataArray.put(KEY_TEMPHUM + KV_DELIMITER + getTempHum());
//		}
//		if (getPm25() != null && !getPm25().equals("")){
//			dataArray.put(KEY_PM25 + KV_DELIMITER + getPm25());
//		}
		
//		return null;
		return dataArray;
	}
	
	@Override
	public boolean mcuDecoder(Object obj) {
		JSONArray mcuData;
		
		try {
			if (obj instanceof JSONArray) {
				mcuData = (JSONArray)obj;
				int dataNum = mcuData.length();
				String data[] = new String [mcuData.length()];
				for (int i = 0; i < dataNum; i++) {
					data[i] = mcuData.getString(i);
				}
				String kv[][] = new String [dataNum][2];
				String kv_temp[] = new String[2];
				for (int i = 0; i < dataNum; i++) {
					kv_temp = data[i].split(KV_DELIMITER);
					kv[i][0] = kv_temp[0];
					kv[i][1] = kv_temp[1];
				}
				
				for (int i = 0; i < dataNum; i++) {
					try {
						if (kv[i][0].equals(KEY_POWER)) {
							setPower(kv[i][1]);
						} else if (kv[i][0].equals(KEY_LIGHT)) {
							setLight(kv[i][1]);
						} else if (kv[i][0].equals(KEY_FANSPEED)) {
							setFanSpeed(kv[i][1]);
						} else if (kv[i][0].equals(KEY_TIMER_ON)) {
							setTimerOn(kv[i][1]);
						} else if (kv[i][0].equals(KEY_TIMER_OFF)) {
							setTimerOff(kv[i][1]);
						} else if (kv[i][0].equals(KEY_PM25)) {
							setPm25Level(kv[i][1]);
						} else if (kv[i][0].equals(KEY_FILTERTIME)) {
							setFilterUseTime(kv[i][1]);
						} 
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	@Override
	public JSONObject jsonEncoder() throws JSONException {

		JSONObject json = new JSONObject();

		if (getPower() != null && !getPower().equals("")) {
			json.put(KEY_POWER, getPower());
		}
		if (getLight() != null && !getLight().equals("")) {
			json.put(KEY_LIGHT, getLight());
		}
		if (getTimerOn() != null && !getTimerOn().equals("")){
			json.put(KEY_TIMER_ON, getTimerOn());
		}
		if (getTimerOff() != null && !getTimerOff().equals("")){
			json.put(KEY_TIMER_OFF, getTimerOff());
		}
		if (getFanSpeed() != null && !getFanSpeed().equals("")){
			json.put(KEY_FANSPEED, getFanSpeed());
		}
		if (getPm25Level() != null && !getPm25Level().equals("")){
			json.put(KEY_PM25, getPm25Level());
		}
		if (getFilterUseTime() != null && !getFilterUseTime().equals("")){
			json.put(KEY_FILTERTIME, getFilterUseTime());
		}
		return json;
	}

}
