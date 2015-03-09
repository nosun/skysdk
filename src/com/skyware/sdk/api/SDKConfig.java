package com.skyware.sdk.api;

public class SDKConfig {

	private boolean isApMode = false;
	private boolean enablePush = true;
	private boolean isSaveMode = true;
	private boolean hasCrashCollect = true;
	
	private String apSSID;
	private String apPasswd;
	private String apIp;
	private int apPort;
	
	private String userId;
	private boolean isLogin;
	private int productType = SDKConst.PRODUCT_UNKNOWN;
	
	public boolean isApMode() {
		return isApMode;
	}

	public SDKConfig setApMode(boolean isApMode) {
		this.isApMode = isApMode;
		return this;
	}
	
	public boolean isEnablePush() {
		return enablePush;
	}

	public SDKConfig setEnablePush(boolean enablePush) {
		this.enablePush = enablePush;
		return this;
	}

	public boolean isSaveMode() {
		return isSaveMode;
	}

	public SDKConfig setSaveMode(boolean isSaveMode) {
		this.isSaveMode = isSaveMode;
		return this;
	}

	public boolean hasCrashCollect() {
		return hasCrashCollect;
	}

	public SDKConfig setHasCrashCollect(boolean hasCrashCollect) {
		this.hasCrashCollect = hasCrashCollect;
		return this;
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

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

}
