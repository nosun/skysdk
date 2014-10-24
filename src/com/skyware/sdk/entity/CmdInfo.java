package com.skyware.sdk.entity;

public abstract class CmdInfo {

	private DevData data;
	private Class<?> clazz;

	
	public DevData getData() {
		return data;
	}
	public void setData(DevData data) {
		this.data = data;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
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
