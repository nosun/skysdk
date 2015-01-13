package com.skyware.sdk.api;

public class SDKConfig {

	private boolean isApMode = false;
	private boolean isSaveMode = true;
	
	private String apSSID;
	private String apPasswd;
	private String apIp;
	private int apPort;
	
	private String userId;
	private boolean isLogin;
	
	public boolean isApMode() {
		return isApMode;
	}

	public SDKConfig setApMode(boolean isApMode) {
		this.isApMode = isApMode;
		return this;
	}
	
	public boolean isSaveMode() {
		return isSaveMode;
	}

	public void setSaveMode(boolean isSaveMode) {
		this.isSaveMode = isSaveMode;
	}

	public String getApSSID() {
		return apSSID;
	}

	public SDKConfig setApSSID(String apSSID) {
		this.apSSID = apSSID;
		return this;
	}
	
	public String getApPasswd() {
		return apPasswd;
	}

	public SDKConfig setApPasswd(String apPasswd) {
		this.apPasswd = apPasswd;
		return this;
	}

	public String getApIp() {
		return apIp;
	}

	public SDKConfig setApIp(String apIp) {
		this.apIp = apIp;
		return this;
	}

	public int getApPort() {
		return apPort;
	}

	public SDKConfig setApPort(int apPort) {
		this.apPort = apPort;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public SDKConfig setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public boolean getIsLogin() {
		return isLogin;
	}

	public SDKConfig setIsLogin(boolean isLogin) {
		this.isLogin = isLogin;
		return this;
	}

}
