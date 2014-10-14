package com.skyware.sdk.entity;

public class DeviceInfo {

//	protected String id;
	private String mac;
//	protected String sn;
	private String ip;
	
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
	
	

	
}
