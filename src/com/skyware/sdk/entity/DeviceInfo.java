package com.skyware.sdk.entity;

public class DeviceInfo {

//	protected String id;
	private String mac;
//	protected String sn;
	private String ip;
	
	private String power;
	
	public static final String POWER_ON = "0";
	public static final String POWER_OFF = "1";
	
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
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}

	
	public boolean resolveData(String data[]) {
		
		for (String d: data) {
			if (d.equals("power::0")) {
				setPower(POWER_ON);
			} else if (d.equals("power::1")) {
				setPower(POWER_OFF);
			}
		}
		
		return false;
	}
}
