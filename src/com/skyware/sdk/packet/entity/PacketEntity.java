package com.skyware.sdk.packet.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.IByteDecoder;
import com.skyware.sdk.entity.IByteEncoder;
import com.skyware.sdk.entity.IJsonEncoder;

public abstract class PacketEntity {

	public static enum PacketType{
		DEVFIND,		//广播
		DEVLOGIN,		//登录
		DEVCHECK,		//状态查询
		DEVCOMMAND,		//指令
		HEARTBEAT,		//心跳
		DEVSTATUS,		//设备状态
		NETSTATUS,		//设备网络状态
		
		DEVFIND_ACK,	//广播回复
		DEVLOGIN_ACK,	//登录回复
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
	public static interface PacketProduct{
		int getProductType();
	}
	
	public abstract static class DevFind implements PacketSend{
		
		@Override
		public PacketType getPacketType() {
			return PacketType.DEVFIND;
		}
		
		public abstract static class Ack implements PacketRecv{
			public String key;
			
			@Override
			public PacketType getPacketType() {
				return PacketType.DEVFIND_ACK;
			}
		}
	}

	
	public abstract static class DevCmd implements PacketSend{
		public int sn;
		public DevData devData;	//设备参数实体类
		public int productType;
		
		public DevCmd() {
			this.sn = -1;
			this.devData = null;
			this.productType = SDKConst.PRODUCT_UNKNOWN;
		}
		public DevCmd(int sn, DevData data, int productType) {
			this.devData = data;
			this.sn = sn;
			this.productType = productType;
		}
		
		@Override
		public PacketType getPacketType() {
			return PacketType.DEVCOMMAND;
		}
		
		public abstract static class Ack implements PacketRecv{
			protected int sn;
			protected int ret;
			protected String key;
			
			public Ack() {
				this.sn = -1;
				this.ret = -1;
			}
//			public Ack(int sn, int ret) {
//				this.sn = sn;
//				this.ret = ret;
//			}
			
			@Override
			public PacketType getPacketType() {
				return PacketType.DEVCOMMAND_ACK;
			}
		}
	}
	
	
	public abstract static class DevLogin implements PacketSend{
		public int sn;
		
		public DevLogin() {
			this.sn = -1;
		}
		public DevLogin(int sn) {
			this.sn = sn;
		}
		
		@Override
		public PacketType getPacketType() {
			return PacketType.DEVLOGIN;
		}
		
		public abstract static class Ack implements PacketRecv, PacketProduct{
			public int sn;
			public int ret;
			public String key;
			
			public Ack() {
				this.sn = -1;
				this.ret = -1;
			}
//			public Ack(int sn, int ret) {
//				this.sn = sn;
//				this.ret = ret;
//			}
			
			@Override
			public PacketType getPacketType() {
				return PacketType.DEVCHECK_ACK;
			}
		}
	}
	
	public abstract static class DevCheck implements PacketSend{
		public int sn;
		
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
		
		public abstract static class Ack implements PacketRecv{
			public int sn;
			public int ret;
			public String key;
			public int productType;
			
			public Ack() {
				this.sn = -1;
				this.ret = -1;
				this.productType = SDKConst.PRODUCT_UNKNOWN;
			}
//			public Ack(int sn, int ret) {
//				this.sn = sn;
//				this.ret = ret;
//			}
			
			@Override
			public PacketType getPacketType() {
				return PacketType.DEVCHECK_ACK;
			}
		}
	}
	
	
	public abstract static class HeartBeat implements PacketSend{
		public int sn;
		
		public HeartBeat() {
			this.sn = -1;
		}
		public HeartBeat(int sn) {
			this.sn = sn;
		}
		
		@Override
		public PacketType getPacketType() {
			return PacketType.HEARTBEAT;
		}
		
		public abstract static class Ack implements PacketRecv{
			public int sn;
			public String key;
			
			public Ack() {
				this.sn = -1;
			}
//			public Ack(int sn) {
//				this.sn = sn;
//			}
			@Override
			public PacketType getPacketType() {
				return PacketType.HEARTBEAT_ACK;
			}
		}
	}
	
	public abstract static class DevStatus implements PacketRecv, IJsonEncoder{
		public int sn;
		public String key;
		public DevData devData;	//设备参数实体类
		public int productType;
		
		public DevStatus() {
			this.sn = -1;
			this.devData = null;
			this.productType = SDKConst.PRODUCT_UNKNOWN;
		}
//		public DevStatus(int sn, String key, DevData devData) {
//			this.sn = sn;
//			this.key = key;
//			this.devData = devData;
//		}
		
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
