package com.skyware.sdk.entity;

import org.json.JSONObject;

import com.skyware.sdk.api.SDKConst;

public class DeviceInfo {

	public static enum DevType{
		AIR_PURIFIER,
		PLUGIN,
		AIR_MONITOR
	};
	public static enum RemoteNetStat{
		ONLINE,
		OFFLINE,
	};
	public static enum LocalNetStat{
		CONNECTED,
		RECONNING,
		BUSY,
		GONE
	};
	
	private String id;
	private String mac;
	private String sn;
	
	private int protocol = SDKConst.PROTOCOL_UNKNOWN;
	private int productType = SDKConst.PRODUCT_UNKNOWN;
	private DevType devType;
	
	private String ip;
	private RemoteNetStat remoteNetStat;
	private LocalNetStat localNetStat;
	
	private DevData data;
	
	private DevParas paras;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
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

	
	/**
	 *	是否在内网中
	 *
	 *	@return
	 */
	public boolean isLocalExist() {
		if (localNetStat != null) {
			switch (localNetStat) {
			case CONNECTED:
			case RECONNING:
			case BUSY:
				return true;
			case GONE:
				return false;
			default:
				break;
			}
		}
		return false;
	}

	/**
	 *	是否可以访问（包括内网）
	 *
	 *	@return
	 */
	public boolean isAccess() {
		if (remoteNetStat == RemoteNetStat.ONLINE || localNetStat == LocalNetStat.CONNECTED) {
			return true;
		}
		return false;
	}
	
	/**
	 *	是否在内网在线
	 *
	 *	@return
	 */
	public boolean isLocalOnline() {
		if (localNetStat != null) {
			switch (localNetStat) {
			case CONNECTED:
				return true;
			case RECONNING:
			case BUSY:
			case GONE:
				return false;
			default:
				break;
			}
		}
		return false;
	}
	/**
	 *	是否远程在线
	 *
	 *	@return
	 */
	public boolean isRemoteOnline() {
		if (remoteNetStat == RemoteNetStat.ONLINE) {
			return true;
		}
		return false;
	}
	
	public RemoteNetStat getRemoteNetStat() {
		return remoteNetStat;
	}
	public void setRemoteNetStat(RemoteNetStat remoteNetStat) {
		this.remoteNetStat = remoteNetStat;
	}
	public LocalNetStat getLocalNetStat() {
		return localNetStat;
	}
	public void setLocalNetStat(LocalNetStat localNetStat) {
		this.localNetStat = localNetStat;
	}
	public int getProductType() {
		return productType;
	}
	public void setProductType(int productType) {
		this.productType = productType;
	}

	
	
	public void jsonEncoder(JSONObject json) {
		if (json != null) {
			String device_mac = json.optString("device_mac");
			String device_id = json.optString("device_id");
			String device_sn = json.optString("device_sn");
			String device_online = json.optString("device_online");
			
			if(!device_mac.equals("") && !device_mac.equals("null")){
				setMac(device_mac);
			}
			if(!device_id.equals("") && !device_id.equals("null")){
				setId(device_id);
			}
			if(!device_sn.equals("") && !device_sn.equals("null")){
				setSn(device_sn);
			}
			if(!device_online.equals("") && !device_online.equals("null")){
				if (device_online.equals("1")) {
					setRemoteNetStat(RemoteNetStat.ONLINE);
				} else if (device_online.equals("0")) {
					setRemoteNetStat(RemoteNetStat.OFFLINE);
				}
			}
		}
	}
	
}
