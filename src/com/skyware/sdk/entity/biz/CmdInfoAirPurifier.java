package com.skyware.sdk.entity.biz;

import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.api.SkySDK;
import com.skyware.sdk.entity.CmdInfo;
import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.biz.DevDataAirpal.MODE;

public class CmdInfoAirPurifier extends CmdInfo{

//	private DevData data;
//	private DevDataHezhong data;
	
	public CmdInfoAirPurifier() {
		this(SkySDK.getConfig()!=null?SkySDK.getConfig().getProductType():SDKConst.PRODUCT_UNKNOWN);
	}
	
	public CmdInfoAirPurifier(int productName) {
		switch (productName) {
		case SDKConst.PRODUCT_AIRPAL_PL200:
		case SDKConst.PRODUCT_AIRPAL_PL500:
			setData(new DevDataAirpal());
			setClazz(DevDataAirpal.class);
			break;
		case SDKConst.PRODUCT_CAIR:
			setData(new DevDataCair());
			setClazz(DevDataCair.class);
			break;
		default:
			setData(new DevDataCair());
			setClazz(DevDataCair.class);
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
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setPower(DevDataAirpal.VALUE_ON_STATE);
		} else if (getData() instanceof DevDataCair) {
			((DevDataCair)getData()).setPower(DevDataCair.VALUE_ON_STATE);
		}
	}
	
	public void setPowerOff() {
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setPower(DevDataAirpal.VALUE_OFF_STATE);
		} else if (getData() instanceof DevDataCair) {
			((DevDataCair)getData()).setPower(DevDataCair.VALUE_OFF_STATE);
		}
	}
	
	public void setChildLockOn() {
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setChildLock(DevDataAirpal.VALUE_ON_STATE);
		}
	}
	
	public void setChildLockOff() {
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setChildLock(DevDataAirpal.VALUE_OFF_STATE);
		} 
	}
	
	public void setLightOn() {
		if (getData() instanceof DevDataCair) {
			((DevDataCair)getData()).setLight(DevDataCair.VALUE_ON_STATE);
		}
	}
	
	public void setLightOff() {
		if (getData() instanceof DevDataCair) {
			((DevDataCair)getData()).setLight(DevDataCair.VALUE_OFF_STATE);
		}
	}
	
	public void setUvOn() {
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setUv(DevDataAirpal.VALUE_ON_STATE);
		}
	}
	
	public void setUvOff() {
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setUv(DevDataAirpal.VALUE_OFF_STATE);
		}
	}
	
	public void setAnionOn() {
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setAnion(DevDataAirpal.VALUE_ON_STATE);
		}
	}
	
	public void setAnionOff() {
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setAnion(DevDataAirpal.VALUE_OFF_STATE);
		}
	}
	
	public void setMode(MODE mode) {
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setMode(DevDataAirpal.MODE_MAP.get(mode));
		}
	}
	
	public void setTimer(int time) {
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setTimer(time + "");
		}
	}
	
	public void setOnTimer(int time) {
		if(getData() instanceof DevDataCair) {
			((DevDataCair)getData()).setTimerOn(String.valueOf(time));
		}
	}
	
	public void setOffTimer(int time) {
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setTimer(time + "");
		} else if(getData() instanceof DevDataCair) {
			((DevDataCair)getData()).setTimerOff(String.valueOf(time));
		}
	}
	
	public void setFanSpeed(int fanSpeed) {
		if (getData() instanceof DevDataAirpal) {
			((DevDataAirpal)getData()).setFanSpeed(fanSpeed + "");
		} else if(getData() instanceof DevDataCair) {
			((DevDataCair)getData()).setFanSpeed(DevDataCair.VALUE_FANSPEED_PRE + String.valueOf(fanSpeed));
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
