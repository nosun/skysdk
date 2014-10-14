package com.skyware.sdk.packet;

import java.net.SocketAddress;


public class OutPacket extends Packet{
	
	public static enum Type{
		TYPE_BROADCAST,		//广播
		TYPE_DEVCOMMAND,	//指令
		TYPE_HEARTBEAT,		//心跳
		TYPE_DEVCHECK,		//查询
		TYPE_DEVSTAT_ACK,	//状态回复
	}
	/** 包的流水号 */
	private int sn;

	/** 包的发送时间 */
	private long sendTime;

	/** 包对应的事件 */
//	private SDKEvent resultEvent;

	/** 包对应的类型 */
	private Type type;

	/** 包的发送目标设备MAC（广播包没有） */
	private String targetMac;

	/** 包发送目标的SocketAddress（广播包没有）*/
	private SocketAddress targetAddr;

	/** 用于一些特殊业务用途 */
	private int flag;
	
	public static int TAG_TCP_PERSIST = 0x11;
//	
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long receiveTime) {
		this.sendTime = receiveTime;
	}

	public String getTargetMac() {
		return targetMac;
	}

	public void setTargetMac(String targetMac) {
		this.targetMac = targetMac;
	}

	public SocketAddress getTargetAddr() {
		return targetAddr;
	}

	public void setTargetAddr(SocketAddress targetAddr) {
		this.targetAddr = targetAddr;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
}
