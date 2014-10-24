package com.skyware.sdk.entity;

public class DeviceInfo {

	public static enum DevType{
		AIR_PURIFIER,
		PLUGIN,
		AIR_MONITOR
	};
	
//	protected String id;
	private String mac;
//	protected String sn;
	private String ip;
	
	private int protocol;
	private DevType devType;
	private DevData data;
	
	
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public DevData getDevData() {
		return data;
	}
	public void setDevData(DevData data) {
		this.data = data;
	}
	
	public int getProtocol() {
		return protocol;
	}
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	
	public DevType getDevType() {
		return devType;
	}
	public void setDevType(DevType devType) {
		this.devType = devType;
	}
	
	
}
