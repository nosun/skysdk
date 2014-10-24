package com.skyware.sdk.entity.biz;

import com.skyware.sdk.entity.CmdInfo;

public class CmdInfoPlugin extends CmdInfo{

//	private DevData data;
//	private DevDataBroadlink data;
	
	public CmdInfoPlugin() {
//		data = new DevData();
		setData(new DevDataBroadlink());
//		data.setFilterRemainTime(DevDataHezhong.VALUE_FILTERTIME_CHECK);
//		data.setPm25(DevDataHezhong.VALUE_PM25_CHECK);
//		data.setTempHum(DevDataHezhong.VALUE_TEMPHUM_CHECK);
	}
	
	public void setPowerOn() {
		getData().setPower(DevDataBroadlink.POWER_ON);
	}

	public void setPowerOff() {
		getData().setPower(DevDataBroadlink.POWER_OFF);
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
