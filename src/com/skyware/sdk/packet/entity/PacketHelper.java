package com.skyware.sdk.packet.entity;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.manage.BizManager;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.packet.Packet;
import com.skyware.sdk.packet.entity.PacketEntity.PacketType;
import com.skyware.sdk.util.ConvertUtil;
import com.skyware.sdk.util.NetworkHelper;

public class PacketHelper {
	
	private PacketHelper(){}	//禁止实例化

	
	/**
	 * 将已经flip()的ByteBuffer打包
	 * 
	 * @param buffer	ByteBuffer实例
	 * @return Packet	打成的包
	 */
	@Deprecated
	public static Packet buffer2Packet(ByteBuffer buffer) {
		byte[] content = new byte[buffer.limit()];
		buffer.get(content);
		Packet packet = new Packet();
		packet.setContent(content);
		return packet;
	}


	/**
	 *	获取默认的广播包
	 *
	 *	@param 	protocol 协议类型
	 *	@return	OutPacket广播包	null--转码失败
	 */
	public static OutPacket getBroadcastFindPacket(int protocol){
		OutPacket packet = new OutPacket();
		packet.setType(PacketType.DEVFIND);
		packet.setTargetAddr( new InetSocketAddress(
				NetworkHelper.getBroadcastIpAddress(BizManager.getInstace().getContext()),
				SDKConst.PROTOCOL_PORT_FIND[protocol]));
		packet.setProtocolType(protocol);
		
		PacketEntity.DevFind broadcastFind;
		switch (protocol) {
		case SDKConst.PROTOCOL_MOORE:
			broadcastFind = new MoorePacketEntity.DevFind();
			packet.setContent(broadcastFind.byteEncoder());
			break;
		case SDKConst.PROTOCOL_BROADLINK:
			broadcastFind = new BroadlinkPacketEntity.DevFind();
			packet.setContent(broadcastFind.byteEncoder());
			break;
		}
		return packet.getContent() != null? packet : null;
	}
	
	/**
	 * 获取心跳包
	 * 
	 * @param sn	请求-应答标识
	 * @return OutPacket 心跳包  null--转码失败
	 */
	public static OutPacket getHeartbeatPacket(int sn, int protocol){
		OutPacket packet = new OutPacket();
		
		packet.setType(PacketType.HEARTBEAT);
		packet.setSn(sn);
		packet.setProtocolType(protocol);
		
		PacketEntity.HeartBeat heartBeat;
		switch (protocol) {
		case SDKConst.PROTOCOL_MOORE:
			heartBeat = new MoorePacketEntity.HeartBeat(sn);
			packet.setContent(heartBeat.byteEncoder());
			break;
		case SDKConst.PROTOCOL_BROADLINK:
			heartBeat = new BroadlinkPacketEntity.HeartBeat(sn);
			packet.setContent(heartBeat.byteEncoder());
			break;
		}

		return packet.getContent() != null? packet : null;
	}
	
	/**
	 * 获取设备查询包
	 * 
	 * @param sn	请求-应答标识
	 * @param targetAddr 发送目标地址
	 * @return OutPacket 心跳包
	 */
	public static OutPacket getDevCheckPacket(int sn, String key, int protocol) {
		OutPacket packet = new OutPacket();
		
		packet.setType(PacketType.DEVCHECK);
		packet.setSn(sn);
		packet.setProtocolType(protocol);
		
		PacketEntity.DevCheck devCheck;
		switch (protocol) {
		case SDKConst.PROTOCOL_MOORE:
			devCheck = new MoorePacketEntity.DevCheck(sn);
			packet.setContent(devCheck.byteEncoder());
			break;
		case SDKConst.PROTOCOL_GREEN:
			devCheck = new GreenPacketEntity.DevCheck();
			packet.setContent(devCheck.byteEncoder());
			break;
		}		

		return packet.getContent() != null? packet : null;
	}
	
	/**
	 * 获取设备命令包
	 * 
	 * @param sn	请求-应答标识
	 * @param data 命令实体
	 * @param key	目标设备key
	 * @return OutPacket 心跳包	null--转码失败
	 */
	public static OutPacket getDevCmdPacket(int sn, DevData data, String key, int protocol) {
		OutPacket packet = new OutPacket();

		packet.setType(PacketType.DEVCOMMAND);
		packet.setSn(sn);
		packet.setProtocolType(protocol);
		
		PacketEntity.DevCmd devCmd;
		switch (protocol) {
		case SDKConst.PROTOCOL_MOORE:
			devCmd = new MoorePacketEntity.DevCmd(sn, data);
			packet.setContent(devCmd.byteEncoder());
			break;
		case SDKConst.PROTOCOL_BROADLINK:
			devCmd = new BroadlinkPacketEntity.DevCmd(sn, data, key);
			packet.setContent(devCmd.byteEncoder());
			break;
		}
		return packet.getContent() != null? packet : null;
	}
	
	
/* TODO 解析未知的包
 * public static PacketACK decodePacket(Packet packet) {
		
		return null;
	}*/

	/**
	 *	获取包的sn
	 *
	 *	@param packet
	 *	@return -1 -- 解析失败
	 */
	public static int resolvePacketSn(Packet packet, int protocol) {
		switch (protocol) {
		case SDKConst.PROTOCOL_MOORE:
			try {
				JSONObject mJson = new JSONObject(new String(packet.getContent()));
			
				return mJson.getInt(MoorePacketEntity.snName);
			} catch (JSONException e) {
//				e.printStackTrace();  //TODO 广播包
			}
			return -1;
		case SDKConst.PROTOCOL_BROADLINK:
			//TODO 博联插座
			switch (resolvePacketType(packet, protocol)) {
			case DEVCOMMAND_ACK:
				int magicHeaderLen = 32;
				int snOffset = magicHeaderLen + 8;
				byte[] snByte = new byte[2];
				System.arraycopy(packet.getContent(), snOffset, snByte, 0, 2);
				return ConvertUtil.snByte2UnsignedShort(snByte);
			default:
				return -1;
			}
		case SDKConst.PROTOCOL_GREEN:
			//TODO 金立格林 无sn
			return -1;
		default:
			return -1;
		}
	}
	
	/**
	 *	获取包的类型
	 *
	 *	@param packet
	 *	@return null-解析失败
	 */
	public static PacketType resolvePacketType(Packet packet, int protocol) {
		
		switch (protocol) {
		case SDKConst.PROTOCOL_MOORE:
			try {
				JSONObject mJson = new JSONObject(new String(packet.getContent()));
				
//				Log.e("PacketHelper", "InPakcet: " + mJson.toString(3));
				
				String cmdValue = mJson.optString(MoorePacketEntity.cmdName);
				
				// 如果不包含 cmd 项，则可能为设备网络状态上报
				// 形如：{"deviceOnline":"0","deviceId":"832250229"}
				if (cmdValue == null || cmdValue.equals("")) {
					if (mJson.opt(MoorePacketEntity.netStatusName) != null) {
						return PacketType.NETSTATUS;
					}
				}
				
				if (packet instanceof InPacket) {
					if (cmdValue.equals(MoorePacketEntity.HeartBeat.cmdValue)) {
						//心跳ACK
						return PacketType.HEARTBEAT_ACK; 
					} else if(cmdValue.equals(MoorePacketEntity.DevCmd.cmdValue)) {
						//指令ACK
						return PacketType.DEVCOMMAND_ACK; 
					} else if(cmdValue.equals(MoorePacketEntity.DevStatus.cmdValue)) {
						//状态上报
						return PacketType.DEVSTATUS; 
					} 
				} else if (packet instanceof OutPacket) {
					if (cmdValue.equals(MoorePacketEntity.HeartBeat.cmdValue)) {
						//心跳
						return PacketType.HEARTBEAT; 
					} else if(cmdValue.equals(MoorePacketEntity.DevCmd.cmdValue)) {
						//指令
						return PacketType.DEVCOMMAND; 
					} 
				}
				//TODO 尚未兼容广播包
				
			} catch (JSONException e) {
//				e.printStackTrace();	如果不是json，就是广播包
				return PacketType.DEVFIND_ACK;
			}
			break;
		case SDKConst.PROTOCOL_BROADLINK:
			//TODO 博联插座
			if (packet.getContent().length == 128) {
				return PacketType.DEVFIND_ACK;
			} else if (packet.getContent().length == 72) {
				return PacketType.DEVCOMMAND_ACK;
			}
			break;
		case SDKConst.PROTOCOL_GREEN:
			//TODO 中立格林
			return PacketType.DEVSTATUS;
			
		default:
			break;
		}
		return null;
	}
	
	
	/**
	 *	解析设备状态包
	 *
	 *	@param packet
	 *	@return
	 */
	public static PacketEntity.DevStatus resolveDevStatPacket(InPacket packet, int protocol) {
		PacketEntity.DevStatus status;
		switch (protocol) {
		case SDKConst.PROTOCOL_MOORE:
			status = new MoorePacketEntity.DevStatus();
			return status.byteDecoder(packet.getContent())? status: null;
		case SDKConst.PROTOCOL_BROADLINK:
			//TODO 博联插座
			status = new BroadlinkPacketEntity.DevStatus();
			return status.byteDecoder(packet.getContent())? status: null;
		case SDKConst.PROTOCOL_GREEN:
			//TODO 博联插座
			status = new GreenPacketEntity.DevStatus();
			return status.byteDecoder(packet.getContent())? status: null;
		default:
			return null;
		}
	}
	
	
	/**
	 *	解析广播回复包
	 *
	 *	@param packet
	 *	@return 广播回复包
	 */
	public static PacketEntity.DevFind.Ack resolveDevFindAck(Packet packet, int protocol) {
		PacketEntity.DevFind.Ack findAck;
		switch (protocol) {
		case SDKConst.PROTOCOL_MOORE:
			findAck = new MoorePacketEntity.DevFind.Ack();
			return findAck.byteDecoder(packet.getContent())? findAck: null;
		case SDKConst.PROTOCOL_BROADLINK:
			//TODO 博联插座
			findAck = new BroadlinkPacketEntity.DevFind.Ack();
			return findAck.byteDecoder(packet.getContent())? findAck: null;
		default:
			return null;
		}
	}
	
}