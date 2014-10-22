package com.skyware.sdk.util;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.consts.SDKConst;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.manage.BizManager;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.packet.Packet;
import com.skyware.sdk.packet.entity.BroadlinkPacketEntity;
import com.skyware.sdk.packet.entity.LierdaPacketEntity;
import com.skyware.sdk.packet.entity.LierdaPacketEntity.DevCmd;
import com.skyware.sdk.packet.entity.LierdaPacketEntity.DevStatus;
import com.skyware.sdk.packet.entity.LierdaPacketEntity.HeartBeat;
import com.skyware.sdk.packet.entity.PacketEntity;
import com.skyware.sdk.packet.entity.PacketEntity.PacketType;

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
				SocketConst.PORT_UDP_REMOTE[protocol]));

		switch (protocol) {
		case SDKConst.PROTOCOL_LIERDA:
			PacketEntity.DevFind broadcastFind1 = new LierdaPacketEntity.DevFind();
			
			packet.setContent(broadcastFind1.byteEncoder());
			break;
		case SDKConst.PROTOCOL_BROADLINK:
			PacketEntity.DevFind broadcastFind2 = new BroadlinkPacketEntity.DevFind();
			
			packet.setContent(broadcastFind2.byteEncoder());
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
		
		switch (protocol) {
		case SDKConst.PROTOCOL_LIERDA:
			PacketEntity.HeartBeat heartBeat1 = new LierdaPacketEntity.HeartBeat();
			heartBeat1.setSn(sn);
			packet.setContent(heartBeat1.byteEncoder());
			break;
		case SDKConst.PROTOCOL_BROADLINK:
			PacketEntity.HeartBeat heartBeat2 = new BroadlinkPacketEntity.HeartBeat();
			heartBeat2.setSn(sn);
			packet.setContent(heartBeat2.byteEncoder());
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
/*	public static OutPacket getDevCheckPacket(int sn) {
		OutPacket packet = new OutPacket();
		DevCheck devCheck = new DevCheck();
		devCheck.setSn(sn);
		
		packet.setType(OutPacket.Type.TYPE_DEVCHECK);
//		packet.setTargetAddr(targetAddr);
		packet.setSn(sn);

		packet.setContent(devCheck.jsonEncoder().toString()
								.getBytes(SocketConst.CHARSET_TCP_CONTENT));

		return packet;
	}*/
	
	/**
	 * 获取设备查询包
	 * 
	 * @param sn	请求-应答标识
	 * @param targetAddr 发送目标地址
	 * @return OutPacket 心跳包	null--转码失败
	 */
	public static OutPacket getDevCmdPacket(int sn, DevData data, int protocol) {
		OutPacket packet = new OutPacket();

		packet.setType(PacketType.DEVCOMMAND);
		packet.setSn(sn);
		
		switch (protocol) {
		case SDKConst.PROTOCOL_LIERDA:
			PacketEntity.DevCmd devCmd1 = new LierdaPacketEntity.DevCmd();
			devCmd1.setSn(sn);
			devCmd1.setData(data);
			packet.setContent(devCmd1.byteEncoder());
			break;
		case SDKConst.PROTOCOL_BROADLINK:
			PacketEntity.DevCmd devCmd2 = new BroadlinkPacketEntity.DevCmd();
			devCmd2.setSn(sn);
			devCmd2.setData(data);
			packet.setContent(devCmd2.byteEncoder());
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
		case SDKConst.PROTOCOL_LIERDA:
			try {
				JSONObject mJson = new JSONObject(new String(packet.getContent(), 
								Charset.forName(SocketConst.CHARSET_TCP_CONTENT)));
			
				return mJson.getInt("sn");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return -1;
		case SDKConst.PROTOCOL_BROADLINK:
			//TODO 博联插座
			
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
		case SDKConst.PROTOCOL_LIERDA:
			try {
				JSONObject mJson = new JSONObject(new String(packet.getContent(), Charset.forName(SocketConst.CHARSET_TCP_CONTENT)));
				
//				Log.e("PacketHelper", "InPakcet: " + mJson.toString(3));
				
				String cmdValue = mJson.getString("cmd");
//				Log.e("PacketHelper", "cmd: " + cmdValue);
				if (packet instanceof InPacket) {
					if (cmdValue.equals(HeartBeat.cmdValue)) {
						return PacketType.HEARTBEAT_ACK; 
					} else if(cmdValue.equals(DevCmd.cmdValue)) {
						return PacketType.DEVCOMMAND_ACK; 
					} else if(cmdValue.equals(DevStatus.cmdValue)) {
						return PacketType.DEVSTATUS; 
					} 
				} else if (packet instanceof OutPacket) {
					if (cmdValue.equals(HeartBeat.cmdValue)) {
						return PacketType.HEARTBEAT; 
					} else if(cmdValue.equals(DevCmd.cmdValue)) {
						return PacketType.DEVCOMMAND; 
					} 
				}
				//TODO 尚未兼容广播包
				
			} catch (JSONException e) {
//				e.printStackTrace();	TODO:如果不是json，就是广播包
				return PacketType.DEVFIND_ACK;
			}
			break;
		case SDKConst.PROTOCOL_BROADLINK:
			//TODO 博联插座
			return PacketType.DEVFIND_ACK;
			
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
		case SDKConst.PROTOCOL_LIERDA:
			status = new LierdaPacketEntity.DevStatus();
			return status.byteDecoder(packet.getContent())? status: null;
		case SDKConst.PROTOCOL_BROADLINK:
			//TODO 博联插座
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
		case SDKConst.PROTOCOL_LIERDA:
			findAck = new LierdaPacketEntity.DevFind.Ack();
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