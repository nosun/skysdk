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
		DEVCHECK,		//状态查询
		HEARTBEAT,		//心跳
		DEVSTATUS,		//设备状态
		NETSTATUS,		//设备网络状态
		
		DEVFIND_ACK,	//广播回复
		DEVCOMMAND_ACK,	//指令回复
		DEVCHECK_ACK,	//状态查询回复
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
		
		public DevFind() {
		}
		
		@Override
		public PacketType getPacketType() {
			return PacketType.DEVFIND;
		}
		
		public abstract static class Ack implements PacketRecv{
			private String key;
//			protected String ip;
			
			public Ack() {
			}
//			public Ack(String key) {
//				this.key = key;
//				this.ip = ip;
//			}
			
			public void setKey(String key){
				this.key = key;
			}
			public String getKey(){
				return key;
			}
//			public void setIp(String ip){
//				this.ip = ip;
//			}
//			public String getIp(){
//				return ip;
//			}
			
			@Override
			public PacketType getPacketType() {
				return PacketType.DEVFIND_ACK;
			}
		}
	}

	public abstract static class DevCmd implements PacketSend{
		private int sn;
		private DevData data;	//设备参数实体类
		
		public DevCmd() {
			this.sn = -1;
			this.data = null;
		}
		public DevCmd(int sn, DevData data) {
			this.data = data;
			this.sn = sn;
		}
		
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
			private int sn;
			private int ret;
			
			public Ack() {
				this.sn = -1;
				this.ret = -1;
			}
//			public Ack(int sn, int ret) {
//				this.sn = sn;
//				this.ret = ret;
//			}
			
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
	
	public abstract static class DevCheck implements PacketSend{
		private int sn;
		
		public DevCheck() {
			this.sn = -1;
		}
		public DevCheck(int sn) {
			this.sn = sn;
		}
		
		@Override
		public PacketType getPacketType() {
			return PacketType.DEVCHECK;
		}
		public int getSn() {
			return sn;
		}
		public void setSn(int sn) {
			this.sn = sn;
		}
		
		public abstract static class Ack implements PacketRecv{
			private int sn;
			private int ret;
			
			public Ack() {
				this.sn = -1;
				this.ret = -1;
			}
//			public Ack(int sn, int ret) {
//				this.sn = sn;
//				this.ret = ret;
//			}
			
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
				return PacketType.DEVCHECK_ACK;
			}
		}
	}
	
	
	public abstract static class HeartBeat implements PacketSend{
		private int sn;
		
		public HeartBeat() {
			this.sn = -1;
		}
		public HeartBeat(int sn) {
			this.sn = sn;
		}
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
			
			public Ack() {
				this.sn = -1;
			}
//			public Ack(int sn) {
//				this.sn = sn;
//			}
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
		private int sn;
		private String key;
		private DevData devData;	//设备参数实体类
		
		public DevStatus() {
			this.sn = -1;
			this.devData = null;
		}
//		public DevStatus(int sn, String key, DevData devData) {
//			this.sn = sn;
//			this.key = key;
//			this.devData = devData;
//		}
		public int getSn() {
			return sn;
		}
		public void setSn(int sn) {
			this.sn = sn;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
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
			//TODO 回复包未定义
			public Ack() {
			}
			@Override
			public PacketType getPacketType() {
				return PacketType.DEVSTATUS_ACK;
			}
		}
		@Override
		public abstract JSONObject jsonEncoder() throws JSONException;
	}
}
