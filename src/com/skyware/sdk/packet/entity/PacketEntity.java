package com.skyware.sdk.packet.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.IByteDecoder;
import com.skyware.sdk.entity.IByteEncoder;
import com.skyware.sdk.entity.IJsonEncoder;

public abstract class PacketEntity {

	public static enum PacketType{
		DEVFIND,		//广播
		DEVCOMMAND,		//指令
		HEARTBEAT,		//心跳
		DEVSTATUS,		//设备状态
		
		DEVFIND_ACK,	//广播回复
		DEVCOMMAND_ACK,	//指令回复
		HEARTBEAT_ACK,	//心跳回复
		DEVSTATUS_ACK,	//状态回复
		
		DEVERROR		//设备异常
	}
	
	public static interface PacketSend extends IByteEncoder{
		int getProtocolType();
		PacketType getPacketType();
	}
	
	public static interface PacketRecv extends IByteDecoder{
		int getProtocolType();
		PacketType getPacketType();
	}
	
	public abstract static class DevFind implements PacketSend{
		
		@Override
		public PacketType getPacketType() {
			return PacketType.DEVFIND;
		}
		
		public abstract static class Ack implements PacketRecv{
			protected long mac;
			protected String ip;
			
			public void setMac(long mac){
				this.mac = mac;
			}
			public long getMac(){
				return mac;
			}
			public void setIp(String ip){
				this.ip = ip;
			}
			public String getIp(){
				return ip;
			}
			
			@Override
			public PacketType getPacketType() {
				return PacketType.DEVFIND_ACK;
			}
		}
	}

	public abstract static class DevCmd implements PacketSend{
		protected int sn;
		protected DevData data;	//设备参数实体类
		
		@Override
		public PacketType getPacketType() {
			return PacketType.DEVCOMMAND;
		}
		public int getSn() {
			return sn;
		}
		public void setSn(int sn) {
			this.sn = sn;
		}
		public DevData getData() {
			return data;
		}
		public void setData(DevData data) {
			this.data = data;
		}
		
		public abstract static class Ack implements PacketRecv{
			protected int sn;
			protected int ret;
			
			public int getSn() {
				return sn;
			}
			public void setSn(int sn) {
				this.sn = sn;
			}
			public int getRet() {
				return ret;
			}
			public void setRet(int ret) {
				this.ret = ret;
			}
			@Override
			public PacketType getPacketType() {
				return PacketType.DEVCOMMAND_ACK;
			}
		}
	}
	
	public abstract static class HeartBeat implements PacketSend{
		protected int sn;
		public int getSn() {
			return sn;
		}
		public void setSn(int sn) {
			this.sn = sn;
		}
		
		@Override
		public PacketType getPacketType() {
			return PacketType.HEARTBEAT;
		}
		public abstract static class Ack implements PacketRecv{
			protected int sn;
			public int getSn() {
				return sn;
			}
			public void setSn(int sn) {
				this.sn = sn;
			}
			@Override
			public PacketType getPacketType() {
				return PacketType.HEARTBEAT_ACK;
			}
		}
	}
	
	public abstract static class DevStatus implements PacketRecv, IJsonEncoder{
		protected int sn;
		protected String mac;
		protected DevData devData;	//设备参数实体类
		
		public int getSn() {
			return sn;
		}
		public void setSn(int sn) {
			this.sn = sn;
		}
		public String getMac() {
			return mac;
		}
		public void setMac(String mac) {
			this.mac = mac;
		}	
		public DevData getDevData() {
			return devData;
		}
		public void setDevData(DevData devData) {
			this.devData = devData;
		}
		
		@Override
		public PacketType getPacketType() {
			return PacketType.DEVSTATUS;
		}
		
		public abstract static class Ack implements PacketSend{
			@Override
			public PacketType getPacketType() {
				return PacketType.DEVSTATUS_ACK;
			}
		}
		@Override
		public abstract JSONObject jsonEncoder() throws JSONException;
	}
}
