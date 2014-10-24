package com.skyware.sdk.entity.biz;

import com.skyware.sdk.consts.SDKConst.Product;
import com.skyware.sdk.entity.CmdInfo;
import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.biz.DevDataHezhong.MODE;

public class CmdInfoAirPurifier extends CmdInfo{

//	private DevData data;
//	private DevDataHezhong data;
	
	
	public CmdInfoAirPurifier() {
//		data = new DevData();
		setData(new DevDataHezhong());
		setClazz(DevDataHezhong.class);
//		data.setFilterRemainTime(DevDataHezhong.VALUE_FILTERTIME_CHECK);
//		data.setPm25(DevDataHezhong.VALUE_PM25_CHECK);
//		data.setTempHum(DevDataHezhong.VALUE_TEMPHUM_CHECK);
	}
	
	public CmdInfoAirPurifier(Product productName) {
		switch (productName) {
		case AirPal:
			setData(new DevDataHezhong());
			setClazz(DevDataHezhong.class);
			break;

		default:
			break;
		}
	}
	
	public CmdInfoAirPurifier(Class<?> clazz) {
		try {
			setData((DevData) clazz.newInstance());
			setClazz(clazz);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void setPowerOn() {
		
		//TODO 基于反射的通用方案
		if (getData() instanceof DevDataHezhong) {
			((DevDataHezhong)getData()).setPower(DevDataHezhong.VALUE_ON_STATE);
		}
	}
	
	public void setPowerOff() {
		if (getData() instanceof DevDataHezhong) {
			((DevDataHezhong)getData()).setPower(DevDataHezhong.VALUE_OFF_STATE);
		}
	}
	
	public void setChildLockOn() {
		if (getData() instanceof DevDataHezhong) {
			((DevDataHezhong)getData()).setChildLock(DevDataHezhong.VALUE_ON_STATE);
		}
	}
	
	public void setChildLockOff() {
		if (getData() instanceof DevDataHezhong) {
			((DevDataHezhong)getData()).setChildLock(DevDataHezhong.VALUE_OFF_STATE);
		}
	}
	
	public void setUvOn() {
		if (getData() instanceof DevDataHezhong) {
			((DevDataHezhong)getData()).setUv(DevDataHezhong.VALUE_ON_STATE);
		}
	}
	
	public void setUvOff() {
		if (getData() instanceof DevDataHezhong) {
			((DevDataHezhong)getData()).setUv(DevDataHezhong.VALUE_OFF_STATE);
		}
	}
	
	public void setAnionOn() {
		if (getData() instanceof DevDataHezhong) {
			((DevDataHezhong)getData()).setAnion(DevDataHezhong.VALUE_ON_STATE);
		}
	}
	
	public void setAnionOff() {
		if (getData() instanceof DevDataHezhong) {
			((DevDataHezhong)getData()).setAnion(DevDataHezhong.VALUE_OFF_STATE);
		}
	}
	
	public void setMode(MODE mode) {
		if (getData() instanceof DevDataHezhong) {
			((DevDataHezhong)getData()).setMode(DevDataHezhong.MODE_MAP.get(mode));
		}
	}
	
	public void setTimer(int time) {
		if (getData() instanceof DevDataHezhong) {
			((DevDataHezhong)getData()).setTimer(time + "");
		}
	}
	
	public void setFanSpeed(int fanSpeed) {
		if (getData() instanceof DevDataHezhong) {
			((DevDataHezhong)getData()).setFanSpeed(fanSpeed + "");
		}
	}

	
//	public DevData wrapDevData(int protocol) {
//		switch (protocol) {
//		case SDKConst.PROTOCOL_LIERDA:
////			return new DevDataLierda(data);
//			return new DevDataHezhong(data);
//		case SDKConst.PROTOCOL_BROADLINK:
//			return new DevDataBroadlink(data);
//		default:
//			return null;
//		}
//	}
	
	
	
}
