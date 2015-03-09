package com.skyware.sdk.entity.biz;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.entity.DevData;

public class DevDataRoyalstar extends DevData{
	//控制量
	/** 开关 */
	private String power;
	/** 模式 */
	private String mode;
	
	//状态量
	/** 温度*/
	private String temp;
	/** 当前状态*/
	private String stat;
	/** 错误状态*/
	private String error;
	/** 煮水加水剩余时间*/
	private String remainTime;
	
	public static final String KV_DELIMITER = "::";
	
	public static final String KEY_POWER = "p";
	public static final String KEY_MODE = "m";
	public static final String KEY_STAT = "s";
	public static final String KEY_RESET = "r";
	
	// 控制类 对应ON OFF
	public static final String VALUE_OFF_STATE 	= "0";	
	public static final String VALUE_ON_STATE 	= "1";
	
	// 设置类 对应范围值
	public static final String VALUE_MODE_AUTO 			= "10100";	
	public static final String VALUE_MODE_MANUAL_HEAT 	= "20100";
	public static final String VALUE_MODE_MANUAL_ADD 	= "30000";	
	public static final String VALUE_MODE_MANUAL_HEAT1 	= "21090";
	public static final String VALUE_MODE_MANUAL_HEAT2 	= "21080";
	public static final String VALUE_MODE_MANUAL_HEAT3 	= "21070";	
	public static final String VALUE_MODE_MANUAL_HEAT4 	= "21060";	
	public static final String VALUE_MODE_MANUAL_HEAT5 	= "21050";
	
	// 状态类 Wifi To MCU 0-1表示是否获取；MCU To Wifi 对应范围值
	public static final String[] VALUE_ERR 	= {"0", "1", "2", "3", "4", "5"};
	//正常状态	提壶操作或温度探头开路	温度探头短路操作	温度探头高温操作	煮水壶内无水状态	旋转龙头信号异常


	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		if (this.power == null || this.power.equals("")) {
			dataCount ++;
		}
		this.power = power;
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
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getStat() {
		return stat;
	}
	public void setStat(String stat) {
		this.stat = stat;
	}
	public String getRemainTime() {
		return remainTime;
	}
	public void setRemainTime(String remainTime) {
		this.remainTime = remainTime;
	}
	public DevDataRoyalstar() {
		super();
	}
	public DevDataRoyalstar(DevData data) {
		super(data);
	}
	
	
	@Override
	public JSONArray mcuCoder() {
		JSONArray dataArray = new JSONArray();
		
		if (getPower() != null && !getPower().equals("")) {
			dataArray.put(KEY_POWER + KV_DELIMITER + getPower());
		}
		if (getMode() != null && !getMode().equals("")){
			dataArray.put(KEY_MODE + KV_DELIMITER + getMode());
		}
		
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
						} else if (kv[i][0].equals(KEY_MODE)) {
							setMode(kv[i][1]);
						} else if (kv[i][0].equals(KEY_STAT)) {
							String statStr = kv[i][1];
							int len = statStr.length();
							if (len >= 1){
								setPower(statStr.substring(0, 1));
							}
							if (len >= 2) {
								setError(statStr.substring(1, 2));
							}
							if (len >= 3) {
								String m = statStr.substring(2, 3);
								if (m.equals("1")) {
									setMode(VALUE_MODE_AUTO);
								} else if (m.equals("2")) {
									setMode(VALUE_MODE_MANUAL_HEAT);
								} else if (m.equals("2")) {
									setMode(VALUE_MODE_MANUAL_ADD);
								}
							}
							if (len >= 6) {
								setTemp(statStr.substring(3, 6));
							}
							if (len >= 7) {
								setStat(statStr.substring(6, 7));
							}
							if (len >= 10) {
								setRemainTime(statStr.substring(7, 10));
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

//		if (getPower() != null && !getPower().equals("")) {
//			json.put(KEY_POWER, getPower());
//		}
//		if (getLight() != null && !getLight().equals("")) {
//			json.put(KEY_LIGHT, getLight());
//		}
//		if (getTimerOn() != null && !getTimerOn().equals("")){
//			json.put(KEY_TIMER_ON, getTimerOn());
//		}
//		if (getTimerOff() != null && !getTimerOff().equals("")){
//			json.put(KEY_TIMER_OFF, getTimerOff());
//		}
//		if (getFanSpeed() != null && !getFanSpeed().equals("")){
//			json.put(KEY_FANSPEED, getFanSpeed());
//		}
//		if (getPm25Level() != null && !getPm25Level().equals("")){
//			json.put(KEY_PM25, getPm25Level());
//		}
//		if (getFilterUseTime() != null && !getFilterUseTime().equals("")){
//			json.put(KEY_FILTERTIME, getFilterUseTime());
//		}
		return json;
	}

}
