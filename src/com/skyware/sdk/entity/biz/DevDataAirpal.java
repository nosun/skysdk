package com.skyware.sdk.entity.biz;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.entity.DevData;

public class DevDataAirpal extends DevData{
	//控制量
	private String power;
	private String childLock;
	private String uv;
	private String anion;
//	private String sleepMode;
	/** 模式 [0-2+] |0-手动 1-自动 2-睡眠 （3-节能）*/
	private String mode;
	/** 定时（小时） [0-12] 2 byte| "00" 为关 ; 定时（分钟） [0-720] 3 byte| "000" 为关*/
	private String timer;
	/** 风速 [0-5] */
	private String fanSpeed;
	
	//状态量
	/** 温度 [1~65] 2 byte| "00" == null 单位℃*/
	private String temperature;
	/** 湿度 [5-65] 2 byte| "00" == null 单位% （与温度值相连）*/
	private String humidity;
	/** PM值 [0-999]  3 byte| "000" == null 单位μg/m3 */
	private String pm25;
	/** PM等级 [1-6] | "0" == null (与PM值相连)*/
	private String pm25Level;
	/** 滤网剩余时间 [0~6000] 4 byte 单位天*/
	private String filterRemainTime;
	/** 滤网剩余等级 [1~6] 1 byte（与滤网值相连）*/
	private String filterRemainLevel;
	
	public static enum MODE{
		MANUAL,
		AUTO,
		SLEEP,
		SAVING
	}
	@SuppressWarnings("serial")
	public static final HashMap<MODE, String> MODE_MAP = new HashMap<DevDataAirpal.MODE, String>(){{
		put(MODE.MANUAL, VALUE_MODE_MANUAL);
		put(MODE.AUTO, VALUE_MODE_AUTO);
		put(MODE.SLEEP, VALUE_MODE_SLEEP);
		put(MODE.SAVING, VALUE_MODE_SAVING);
	}};
	
	// 控制类 对应ON OFF
	public static final String KEY_POWER = "pw";
	public static final String KEY_CHILDLOCK = "lc";
	public static final String KEY_UV = "uv";
	public static final String KEY_ANION = "io";
//	public static final String KEY_SLEEPMODE = "sl";
	public static final String KEY_MODE = "mo";
	// 设置类 对应范围值
	public static final String KEY_TIMER = "tm";
	public static final String KEY_FANSPEED = "fa";
	// 状态类 Wifi To MCU 0-1表示是否获取；MCU To Wifi 对应范围值
	public static final String KEY_FILTERTIME = "fi";
	public static final String KEY_TEMPHUM = "th";
	public static final String KEY_PM25 = "pm";
	
	public static final String KV_DELIMITER = "::";
	
	public static final String VALUE_ON_STATE 	= "1";
	public static final String VALUE_OFF_STATE 	= "0";
	
	public static final String VALUE_MODE_MANUAL 	= "0";
	public static final String VALUE_MODE_AUTO 	= "1";	
	public static final String VALUE_MODE_SLEEP 	= "2";	
	public static final String VALUE_MODE_SAVING 	= "3";
	
	public static final String VALUE_TIMER_OFF 	= "0";
	public static final String VALUE_TIMER_MIN 	= "1";
	public static final String VALUE_TIMER_MAX 	= "12";
	
	public static final String VALUE_FANSPEED_OFF = "0";
	public static final String VALUE_FANSPEED_MIN = "1";
	public static final String VALUE_FANSPEED_MAX = "5";	
	
	public static final String VALUE_FILTER_TIME_GET = "1";
	public static final String VALUE_FILTER_TIME_NULL = "00000";
	public static final String VALUE_FILTER_TIME_MIN = "0";
	public static final String VALUE_FILTER_TIME_MAX = "6000";
	public static final String VALUE_FILTER_LEVEL_MIN = "1";
	public static final String VALUE_FILTER_LEVEL_MAX = "6";
	
	public static final String VALUE_TEMPHUM_GET = "1";
	public static final String VALUE_TEMPHUM_NULL = "0000";
	public static final String VALUE_TEMP_MIN = "1";
	public static final String VALUE_TEMP_MAX = "99";
	public static final String VALUE_HUM_MIN = "1";
	public static final String VALUE_HUM_MAX = "99";
	
	public static final String VALUE_PM25_GET = "1";
	public static final String VALUE_PM25_NULL = "0000";
	public static final String VALUE_PM25_MIN = "0";
	public static final String VALUE_PM25_MAX = "999";
	public static final String VALUE_PM25_LEVEL_MIN = "1";
	public static final String VALUE_PM25_LEVEL_MAX = "6";
	
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		if (this.power == null || this.power.equals("")) {
			dataCount ++;
		}
		this.power = power;
	}
	public String getChildLock() {
		return childLock;
	}
	public void setChildLock(String childLock) {
		if (this.childLock == null || this.childLock.equals("")) {
			dataCount ++;
		}
		this.childLock = childLock;
	}
	public String getUv() {
		return uv;
	}
	public void setUv(String uv) {
		if (this.uv == null || this.uv.equals("")) {
			dataCount ++;
		}
		this.uv = uv;
	}
	public String getAnion() {
		return anion;
	}
	public void setAnion(String anion) {
		if (this.anion == null || this.anion.equals("")) {
			dataCount ++;
		}
		this.anion = anion;
	}
	public String getTimer() {
		return timer;
	}
	public void setTimer(String timer) {
		if (this.timer == null || this.timer.equals("")) {
			dataCount ++;
		}
		this.timer = timer;
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
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		if (this.mode == null || this.mode.equals("")) {
			dataCount ++;
		}
		this.mode = mode;
	}
	public String getFilterRemainTime() {
		return filterRemainTime;
	}
	public void setFilterRemainTime(String filterRemainTime) {
		if (this.filterRemainTime == null || this.filterRemainTime.equals("")) {
			dataCount ++;
		}
		this.filterRemainTime = filterRemainTime;
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
	public String getPm25() {
		return pm25;
	}
	public void setPm25(String pm25) {
		if (this.pm25 == null || this.pm25.equals("")) {
			dataCount ++;
		}
		this.pm25 = pm25;
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
	public String getFilterRemainLevel() {
		if (this.filterRemainLevel == null || this.filterRemainLevel.equals("")) {
			dataCount ++;
		}
		return filterRemainLevel;
	}
	public void setFilterRemainLevel(String filterRemainLevel) {
		this.filterRemainLevel = filterRemainLevel;
	}
	
	public DevDataAirpal() {
		super();
	}
	public DevDataAirpal(DevData data) {
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
		if (getChildLock() != null && !getChildLock().equals("")) {
			dataArray.put(KEY_CHILDLOCK + KV_DELIMITER + getChildLock());
		}
		if (getUv() != null && !getUv().equals("")){
			dataArray.put(KEY_UV + KV_DELIMITER + getUv());
		}
		if (getAnion() != null && !getAnion().equals("")){
			dataArray.put(KEY_ANION + KV_DELIMITER + getAnion());
		}
		if (getMode() != null && !getMode().equals("")){
			dataArray.put(KEY_MODE + KV_DELIMITER + getMode());
		}
		if (getTimer() != null && !getTimer().equals("")){
			dataArray.put(KEY_TIMER + KV_DELIMITER + getTimer());
		}
		if (getFanSpeed() != null && !getFanSpeed().equals("")){
			dataArray.put(KEY_FANSPEED + KV_DELIMITER + getFanSpeed());
		}
//		if (getFilterRemainTime() != null && !getFilterRemainTime().equals("")){
//			dataArray.put(KEY_FILTERTIME + KV_DELIMITER + getFilterRemainTime());
//		}
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
		try {
			if (obj instanceof JSONArray) {
				JSONArray mcuData = (JSONArray)obj;
			
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
						} else if (kv[i][0].equals(KEY_CHILDLOCK)) {
							//TODO 验证
							setChildLock(kv[i][1]);
						} else if (kv[i][0].equals(KEY_UV)) {
							//TODO 验证
							setUv(kv[i][1]);
						} else if (kv[i][0].equals(KEY_ANION)) {
							//TODO 验证
							setAnion(kv[i][1]);
						} else if (kv[i][0].equals(KEY_MODE)) {
							//TODO 验证
							setMode(kv[i][1]);
						} else if (kv[i][0].equals(KEY_TIMER)) {
							//TODO 验证
							setTimer(kv[i][1]);
						} else if (kv[i][0].equals(KEY_FANSPEED)) {
							//TODO 验证
							setFanSpeed(kv[i][1]);
						} else if (kv[i][0].equals(KEY_TEMPHUM)) {
							if (!kv[i][1].equals(VALUE_TEMPHUM_NULL)) {
								setTemperature(kv[i][1].substring(0, 2));
								setHumidity(kv[i][1].substring(2, 4));
							}
						} else if (kv[i][0].equals(KEY_PM25)) {
							if (!kv[i][1].equals(VALUE_PM25_NULL)) {
								setPm25(kv[i][1].substring(0, 3));
								setPm25Level(kv[i][1].substring(3, 4));
							}
						} else if (kv[i][0].equals(KEY_FILTERTIME)) {
							if (!kv[i][1].equals(VALUE_FILTER_TIME_NULL)) {
								setFilterRemainTime(kv[i][1].substring(0, 4));
								setFilterRemainLevel(kv[i][1].substring(4, 5));
							}
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
		if (getChildLock() != null && !getChildLock().equals("")) {
			json.put(KEY_CHILDLOCK, getChildLock());
		}
		if (getUv() != null && !getUv().equals("")){
			json.put(KEY_UV, getUv());
		}
		if (getAnion() != null && !getAnion().equals("")){
			json.put(KEY_ANION, getAnion());
		}
		if (getMode() != null && !getMode().equals("")){
			json.put(KEY_MODE, getMode());
		}
		if (getTimer() != null && !getTimer().equals("")){
			json.put(KEY_TIMER, getTimer());
		}
		if (getFanSpeed() != null && !getFanSpeed().equals("")){
			json.put(KEY_FANSPEED, getFanSpeed());
		}
		if (getFilterRemainTime() != null && !getFilterRemainTime().equals("")
			&& getFilterRemainLevel() != null && !getFilterRemainLevel().equals("")){
			json.put(KEY_FILTERTIME, getFilterRemainTime() + getFilterRemainLevel());
		}
		if (getTemperature() != null && !getTemperature().equals("")
			&& getHumidity() != null && !getHumidity().equals("")){
			json.put(KEY_TEMPHUM, getTemperature() + getHumidity());
		}
		if (getPm25() != null && !getPm25().equals("")){
			json.put(KEY_PM25, getPm25() + getPm25Level());
		}
		
		return json;
	}

}
