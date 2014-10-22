package com.skyware.sdk.entity;

import com.skyware.sdk.consts.SDKConst;

public class CmdInfo {

	private DevData data;
	
	public CmdInfo() {
		data = new DevData();
	}
	
	public void setPowerOn() {
		data.setPower(DevData.POWER_ON);
	}
	
	public void setPowerOff() {
		data.setPower(DevData.POWER_OFF);
	}
	
	public DevData wrapDevData(int protocol) {
		switch (protocol) {
		case SDKConst.PROTOCOL_LIERDA:
			return new LierdaDevData(data);
		case SDKConst.PROTOCOL_BROADLINK:
			return new BroadlinkDevData(data);
		default:
			return null;
		}
	}
	
	
	
}
