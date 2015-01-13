package com.skyware.sdk.entity;

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

}
