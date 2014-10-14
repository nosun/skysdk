package com.skyware.sdk.packet;

import java.net.SocketAddress;

public class InPacket extends Packet{
	
	public static enum Type{
		TYPE_BROADCAST_ACK,	//广播回复
		TYPE_HEARTBEAT_ACK,	//心跳回复
		TYPE_COMMAND_ACK,	//指令回复
		TYPE_DEV_STATUS,	//设备状态
		TYPE_DEV_ERROR		//设备异常
	}
	
	/** 包的流水号 */
	private int sn;

	/** 包的接收时间 */
	private long receiveTime;

	/** 包对应的事件 */
//	private SDKEvent resultEvent;
	
	/** 包对应的类型 */
	private Type type;

	/** 包的发送目标设备MAC */
	private String sourceMac;

	/** 包发送目标的SocketAddress */
	private SocketAddress sourceAddr;


//	public SDKEvent getResultEvent() {
//		return resultEvent;
//	}
//
//	public void setResultEvent(SDKEvent resultEvent) {
//		this.resultEvent = resultEvent;
//	}

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public long getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(long receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getSourceMac() {
		return sourceMac;
	}

	public void setSourceMac(String sourceMac) {
		this.sourceMac = sourceMac;
	}

	public SocketAddress getSourceAddr() {
		return sourceAddr;
	}

	public void setSourceAddr(SocketAddress sourceAddr) {
		this.sourceAddr = sourceAddr;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
