package com.skyware.sdk.entity.biz;

import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.api.SkySDK;
import com.skyware.sdk.entity.CmdInfo;
import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.biz.DevDataAirpal.MODE;

public class CmdInfoWatarPot extends CmdInfo{

//	private DevData data;
//	private DevDataHezhong data;
	
	public CmdInfoWatarPot() {
		this(SkySDK.getConfig()!=null?SkySDK.getConfig().getProductType():SDKConst.PRODUCT_UNKNOWN);
	}
	
	public CmdInfoWatarPot(int productName) {
		switch (productName) {
		case SDKConst.PRODUCT_ROYALSTAR:
			setData(new DevDataRoyalstar());
			setClazz(DevDataRoyalstar.class);
			break;
		}
	}
	
	public CmdInfoWatarPot(Class<?> clazz) {
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
		if (getData() instanceof DevDataRoyalstar) {
			((DevDataRoyalstar)getData()).setPower(DevDataRoyalstar.VALUE_ON_STATE);
		}
	}
	
	public void setPowerOff() {
		if (getData() instanceof DevDataRoyalstar) {
			((DevDataRoyalstar)getData()).setPower(DevDataRoyalstar.VALUE_OFF_STATE);
		}
	}

	
	public void setMode(String mode) {
		if (getData() instanceof DevDataRoyalstar) {
			((DevDataRoyalstar)getData()).setMode(mode);
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
