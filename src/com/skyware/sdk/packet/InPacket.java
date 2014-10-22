package com.skyware.sdk.packet;

import java.net.InetSocketAddress;
import com.skyware.sdk.packet.entity.PacketEntity.PacketType;

public class InPacket extends Packet{
	
	
	/** 包的流水号 */
	private int sn;

	/** 包的接收时间 */
	private long receiveTime;

	/** 包对应的事件 */
//	private SDKEvent resultEvent;
	
	/** 包对应的类型 */
	private PacketType type;

	/** 包的发送目标设备MAC */
	private int sourceMac;

	/** 包发送目标的SocketAddress */
	private InetSocketAddress sourceAddr;


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

	public int getSourceMac() {
		return sourceMac;
	}

	public void setSourceMac(int sourceMac) {
		this.sourceMac = sourceMac;
	}

	public InetSocketAddress getSourceAddr() {
		return sourceAddr;
	}

	public void setSourceAddr(InetSocketAddress sourceAddr) {
		this.sourceAddr = sourceAddr;
	}

	public PacketType getType() {
		return type;
	}

	public void setType(PacketType type) {
		this.type = type;
	}
}
