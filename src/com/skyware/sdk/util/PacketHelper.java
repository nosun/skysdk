package com.skyware.sdk.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.entity.IJsonDecoder;
import com.skyware.sdk.entity.IJsonEncoder;
import com.skyware.sdk.manage.NetworkManager;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.packet.Packet;

public class PacketHelper {
	
	private PacketHelper(){}	//禁止实例化
	
	//ret值 Code-Describe Map
	private static final Map<Integer, String> retCodes;
	
	static {
		retCodes = new HashMap<Integer, String>();
		retCodes.put(200, "成功收到协议包");
		retCodes.put(403, "设备未授权");
		retCodes.put(404, "未接收到主控的串口数据");
		retCodes.put(501, "不支持此协议");
		retCodes.put(503, "WiFi模块负载重，请设备稍后再发");
	}
	
	/**
	 * 将已经flip()的ByteBuffer打包
	 * 
	 * @param buffer	ByteBuffer实例
	 * @return Packet	打成的包
	 */
	public static Packet buffer2Packet(ByteBuffer buffer) {
		byte[] content = new byte[buffer.limit()];
		buffer.get(content);
		Packet packet = new Packet();
		packet.setContent(content);
		return packet;
	}

	
	/**
	 * 获取默认的广播包
	 * 
	 * @return OutPacket 广播包
	 * @throws UnsupportedEncodingException 默认的字符集不支持
	 */
	public static OutPacket getBroadcastPacket() throws UnsupportedEncodingException {
		OutPacket packet = new OutPacket();
		Broadcast broadcast = new Broadcast();
		packet.setType(OutPacket.Type.TYPE_BROADCAST);
		packet.setTargetAddr(NetworkManager.getInstace().getBroadcastAddr());
		packet.setContent(broadcast.toString()
					.getBytes(SocketConst.CHARSET_BROADCAST_CONTENT));
		return packet;
	}
	
	/**
	 * 获取心跳包
	 * 
	 * @param sn	请求-应答标识
	 * @param targetAddr 发送目标地址
	 * @return OutPacket 心跳包
	 * @throws UnsupportedEncodingException 默认的字符集不支持
	 * @throws JSONException json参数异常
	 */
	public static OutPacket getHeartbeatPacket(int sn) throws UnsupportedEncodingException, JSONException {
		OutPacket packet = new OutPacket();
		HeartBeat heartBeat = new HeartBeat();
		heartBeat.setSn(sn);
		
		packet.setType(OutPacket.Type.TYPE_HEARTBEAT);
//		packet.setTargetAddr(targetAddr);
		packet.setSn(sn);
		packet.setContent(heartBeat.jsonEncoder().toString()
							.getBytes(SocketConst.CHARSET_TCP_CONTENT));
		return packet;
	}
	
	/**
	 * 获取设备查询包
	 * 
	 * @param sn	请求-应答标识
	 * @param targetAddr 发送目标地址
	 * @return OutPacket 心跳包
	 * @throws UnsupportedEncodingException 默认的字符集不支持
	 * @throws JSONException json参数异常
	 */
	public static OutPacket getDevCheckPacket(int sn) throws UnsupportedEncodingException, JSONException {
		OutPacket packet = new OutPacket();
		DevCheck devCheck = new DevCheck();
		devCheck.setSn(sn);
		
		packet.setType(OutPacket.Type.TYPE_DEVCHECK);
//		packet.setTargetAddr(targetAddr);
		packet.setSn(sn);
		packet.setContent(devCheck.jsonEncoder().toString()
							.getBytes(SocketConst.CHARSET_TCP_CONTENT));
		return packet;
	}
	
	/**
	 * 获取设备查询包
	 * 
	 * @param sn	请求-应答标识
	 * @param targetAddr 发送目标地址
	 * @return OutPacket 心跳包
	 * @throws UnsupportedEncodingException 默认的字符集不支持
	 * @throws JSONException json参数异常
	 */
	public static OutPacket getDevCmdPacket(int sn, String[] data) throws UnsupportedEncodingException, JSONException {
		OutPacket packet = new OutPacket();
		DevCommand devCmd = new DevCommand();
		devCmd.setSn(sn);
		devCmd.setData(data);
		
		packet.setType(OutPacket.Type.TYPE_DEVCOMMAND);
		packet.setSn(sn);
		packet.setContent(devCmd.jsonEncoder().toString()
							.getBytes(SocketConst.CHARSET_TCP_CONTENT));
		return packet;
	}
	
	
/* TODO 解析未知的包
 * public static PacketACK decodePacket(Packet packet) {
		
		return null;
	}*/
	
	
	/**
	 *	解析设备状态包
	 *
	 *	@param packet
	 *	@return
	 */
	public static DevStatus resolveDevStatPacket(InPacket packet) {
		try {
			JSONObject json = new JSONObject(new String(packet.getContent(), SocketConst.CHARSET_TCP_CONTENT));
			DevStatus status = new DevStatus();
			status.jsonDecoder(json);
			
			return status;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *	获取包的sn
	 *
	 *	@param packet
	 *	@return -1 -- 解析失败
	 */
	public static int getPacketSn(Packet packet) {
		JSONObject mJson;
		try {
			mJson = new JSONObject(new String(packet.getContent(), Charset.forName(SocketConst.CHARSET_TCP_CONTENT)));
		
			return mJson.getInt("sn");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 *	获取发送包的类型
	 *
	 *	@param packet
	 *	@return null-解析失败
	 */
	public static OutPacket.Type getPacketType(OutPacket packet) {
		
		try {
			JSONObject mJson = new JSONObject(new String(packet.getContent(), Charset.forName(SocketConst.CHARSET_TCP_CONTENT)));
			
			String cmdValue = mJson.getString("cmd");
			if (cmdValue.equals(HeartBeat.cmdValue)) {
				return OutPacket.Type.TYPE_HEARTBEAT; 
			} else if(cmdValue.equals(DevCommand.cmdValue)) {
				return OutPacket.Type.TYPE_DEVCOMMAND; 
			} 
			//TODO 尚未兼容广播包
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 *	获取接收包的类型
	 *
	 *	@param packet
	 *	@return null-解析失败
	 */
	public static InPacket.Type getPacketType(InPacket packet) {
		
		try {
			JSONObject mJson = new JSONObject(new String(packet.getContent(), Charset.forName(SocketConst.CHARSET_TCP_CONTENT)));
			
//			Log.e("PacketHelper", "InPakcet: " + mJson.toString(3));
			
			String cmdValue = mJson.getString("cmd");
//			Log.e("PacketHelper", "cmd: " + cmdValue);
			if (cmdValue.equals(HeartBeat.cmdValue)) {
				return InPacket.Type.TYPE_HEARTBEAT_ACK; 
			} else if(cmdValue.equals(DevCommand.cmdValue)) {
				return InPacket.Type.TYPE_COMMAND_ACK; 
			} else if(cmdValue.equals(DevStatus.cmdValue)) {
				return InPacket.Type.TYPE_DEV_STATUS; 
			} 
			//TODO 尚未兼容广播包
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 *	解析广播回复包
	 *
	 *	@param packet
	 *	@return
	 *	@throws UnsupportedEncodingException
	 *	@throws JSONException
	 */
	public static Broadcast.ACK getBroadcastAck(Packet packet) throws UnsupportedEncodingException, JSONException {
		Broadcast.ACK broadcastAck = new Broadcast.ACK();
		boolean ret = broadcastAck.toBean(new String(packet.getContent(), SocketConst.CHARSET_BROADCAST_CONTENT));
		
		return ret? broadcastAck: null;
	}
	
	
	
	public abstract static class PacketACK{
		
	}
	
	/**
	 *	广播包实体类
	 *	@author wangyf 2014-10-9
	 */
	public static class Broadcast{

		@Override
		public String toString() {
			return SocketConst.BROADCAST_CONTENT_DEFAULT;
		}
	
		/**
		 * 广播包回复实体类
		 */
		public static class ACK extends PacketACK{
			private String ip;
			private String mac;
			
			public String getMac() {
				return mac;
			}
			public String getIp() {
				return ip;
			}

			public boolean toBean(String str) {
				// TODO 更严谨一些
				String[] strSplit = str.split(",");
				ip = strSplit[0];
				mac = strSplit[1];
				return true;
			}
		}
	}
	
	/**
	 * 心跳包实体类
	 * @author wangyf
	 */
	public static class HeartBeat implements IJsonEncoder{
		
		private int sn;

		private final static String snName = "sn";
		private final static String cmdName = "cmd";
		private final static String cmdValue = "heartbeat";
		
		public int getSn() {
			return this.sn;
		}
		public void setSn(int sn) {
			if(checkSnValidate(sn))
				this.sn = sn;
		}
		
		@Override
		public JSONObject jsonEncoder() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put(snName, getSn());
			obj.put(cmdName, cmdValue);
			return obj;
		}
		
		/**
		 * 心跳包回复实体类
		 */
		public static class ACK extends PacketACK implements IJsonDecoder{
			private int sn;
			
			public int getSn() {
				return sn;
			}
			public void setSn(int sn) {
				if(checkSnValidate(sn))
					this.sn = sn;
			}

			@Override
			public boolean jsonDecoder(JSONObject json) throws JSONException {
				if(!cmdValue.equals(json.getString(cmdName))){
					return false;
				}
				setSn(json.getInt(snName));
				return true;
			}
		}
	}
	
	/**
	 * 设备控制指令实体类
	 * @author wangyf
	 */
	public static class DevCommand implements IJsonEncoder{
		
		private int sn;
		//private DevData data;	TODO：设备参数实体类
		private String data[];
		
		
		private final static String snName = "sn";
		private final static String cmdName = "cmd";
		private final static String cmdValue = "download";
		private final static String dataName = "data";
		
		public int getSn() {
			return sn;
		}
		public void setSn(int sn) {
			if(checkSnValidate(sn))
				this.sn = sn;
		}
		public String[] getData() {
			return data;
		}
		public void setData(String data[]) {
			this.data = data;
		}
			
		@Override
		public JSONObject jsonEncoder() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put(snName, getSn());
			obj.put(cmdName, cmdValue);
			
			
			if (getData() != null && getData().length != 0) {
				JSONArray dataArray = new JSONArray();
				for(String d : data){
					dataArray.put(d);
				}
				obj.put(dataName, dataArray);
			} else if (getData() == null){
				obj.put(dataName, "");
			}
			
			return obj;
		}
		
		
		/**
		 * 控制包回复实体类
		 */
		public static class ACK extends PacketACK implements IJsonDecoder{
			private int sn;
			private int ret;

			private final static String retName = "ret";
			
			public void setSn(int sn) {
				if(checkSnValidate(sn))
					this.sn = sn;
			}
			public int getSn() {
				return sn;
			}
			public void setRet(int ret) {
				if(checkRetValidate(ret))
					this.ret = ret;
			}
			public int getRet() {
				return ret;
			}

			@Override
			public boolean jsonDecoder(JSONObject json) throws JSONException {
				if(!cmdValue.equals(json.getString(cmdName))){
					return false;
				}
				setSn(json.getInt(snName));
				setRet(json.getInt(retName));
				return true;
			}
		}
	}
	
	/**
	 * 设备查询指令实体类
	 * @author wangyf
	 */
	public static class DevCheck extends DevCommand{
		//查询指令本质上就是没有data的指令
		public DevCheck(){
			setData(null);
		}
	}
	
	/**
	 * 设备状态上报实体类
	 * @author wangyf
	 */
	public static class DevStatus implements IJsonDecoder, IJsonEncoder{
		private int sn = -1;
		private String mac;
		private DevData devData;	//设备参数实体类

//		private String data[];
		
		private final static String snName = "sn";
		private final static String macName = "mac";
		private final static String cmdName = "cmd";
		private final static String cmdValue = "upload";
		private final static String dataName = "data";
		
		public int getSn() {
			return sn;
		}
		public void setSn(int sn) {
			if(checkSnValidate(sn))
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
		public boolean jsonDecoder(JSONObject json) throws JSONException {

			if(!cmdValue.equals(json.getString(cmdName))){
				return false;
			}
			setSn(json.getInt(snName));
			setMac(json.getString(macName));
			
			JSONArray dataArr = json.getJSONArray(dataName);
			DevData data = new DevData();
			data.mcuDecoder(dataArr);
			
			setDevData(data);
//			info.setMac(status.getMac());
//			info.resolveData(status.getData());
			
			return true;
		}
		@Override
		public JSONObject jsonEncoder() throws JSONException {
			
			JSONObject json = new JSONObject();
			
			json.put(cmdName, cmdValue);

			if (getSn() != -1) {
				json.put(snName, getSn());
			}
			if (getMac() != null) {
				json.put(macName, getMac());
			}
			
			if (getDevData() != null) {
				//转成JSON格式，向上汇报
				json.put(dataName, getDevData().jsonEncoder());
			}
					
			return null;
		}
	
		
	}
	
	
	/**
	 * 公共方法:检测sn是否合法，若非法抛出IllegalArgumentException
	 * @param sn	待检测的SN
	 * @return	如果合法返回true
	 */
	private static boolean checkSnValidate(int sn) {
		if (sn < 0 || sn > 65535) {
			throw new IllegalArgumentException("sn is <0 or >65535");
		}
		return true;
	}
	/**
	 * 公共方法:检测ret是否合法，若非法抛出IllegalArgumentException
	 * @param ret	待检测的ret
	 * @return	如果合法返回true
	 */
	private static boolean checkRetValidate(int ret) {
		if (!retCodes.keySet().contains(ret)) {
			throw new IllegalArgumentException("ret code is illegal");
		}
		return true;
	}
	
	
	
}