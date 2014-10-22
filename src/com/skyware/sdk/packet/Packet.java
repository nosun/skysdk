package com.skyware.sdk.packet;

public class Packet {
	/** 包的内容 */
	protected byte[] content;

//	/** 协议类型 */
//	protected int protocolType = SDKConst.PROTOCOL_UNKNOWN;

	public synchronized byte[] getContent() {
		return content;
	}

	public synchronized void setContent(byte[] content) {
		this.content = content;
	}

//	public int getProtocolType() {
//		return protocolType;
//	}
//
//	public void setProtocolType(int protocolType) {
//		this.protocolType = protocolType;
//	}

}
