package com.skyware.sdk.packet;

import java.net.InetSocketAddress;

import com.skyware.sdk.packet.entity.PacketEntity.PacketType;


public class OutPacket extends Packet{

	/** 包的流水号 */
	private int sn;

	/** 包的发送时间 */
	private long sendTime;

	/** 包对应的事件 */
//	private SDKEvent resultEvent;

	/** 包对应的类型 */
	private PacketType type;

	/** 包的发送目标设备MAC（广播包没有） */
	private long targetMac;

	/** 包发送目标的SocketAddress（广播包没有）*/
	private InetSocketAddress targetAddr;

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

	public PacketType getType() {
		return type;
	}

	public void setType(PacketType type) {
		this.type = type;
	}
	
	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long receiveTime) {
		this.sendTime = receiveTime;
	}

	public long getTargetMac() {
		return targetMac;
	}

	public void setTargetMac(long targetMac) {
		this.targetMac = targetMac;
	}

	public InetSocketAddress getTargetAddr() {
		return targetAddr;
	}

	public void setTargetAddr(InetSocketAddress targetAddr) {
		this.targetAddr = targetAddr;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
}
