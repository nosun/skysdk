 package com.skyware.sdk.packet.entity;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.DevDataFactory;
import com.skyware.sdk.entity.IJsonDecoder;
import com.skyware.sdk.entity.IJsonEncoder;
import com.skyware.sdk.util.ConvertUtil;

public class LierdaPacketEntity{
	
	public final static String cmdName = "cmd";
	public final static String snName = "sn";
	public final static String macName = "mac";
	public final static String netStatusName = "deviceOnline";
	
	public static int getMyProtocolType() {
		return SDKConst.PROTOCOL_LIERDA;
	}

	
	/**
	 *	设备发现包实体类
	 *	@author wangyf 2014-10-9
	 */
	public static class DevFind extends PacketEntity.DevFind{
		
		public DevFind() {
		}
		
		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}
		@Override
		public byte[] byteEncoder() {
			try {
				return "HF-A11ASSISTHREAD".getBytes(SocketConst.CHARSET_BROADCAST_CONTENT);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		/**
		 * 广播包回复实体类
		 */
		public static class Ack extends PacketEntity.DevFind.Ack{
			protected String ip;
			
			public Ack() {}
//			public Ack(long mac, String ip) {
//				super(mac);
//				this.ip = ip;
//			}
			
			@Override
			public int getProtocolType() {
				return getMyProtocolType();
			}
			@Override
			public boolean byteDecoder(byte[] byteFillArray) {
				// TODO 更严谨一些
				String[] strSplit = new String(byteFillArray).split(",");
				ip = strSplit[0].trim();
				if(ConvertUtil.macFormat(strSplit[1].trim()) != null){
					key = ConvertUtil.macFormat(strSplit[1].trim());
					return true;
				}
				return false;
			}
		}
	}
	
	
	/**
	 * 设备控制指令实体类
	 * @author wangyf
	 */
	public static class DevCmd extends PacketEntity.DevCmd 
							implements IJsonEncoder{
		public final static String cmdValue = "download";
		public final static String dataName = "data";
		
		public DevCmd() {
		}
		public DevCmd(int sn, DevData data, int productType) {
			super(sn, data, productType);
		}
		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}
			
		@Override
		public JSONObject jsonEncoder() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put(snName, sn);
			obj.put(cmdName, cmdValue);
			
			if (devData != null && devData.getDataCount() > 0) {
				obj.put(dataName, devData.mcuCoder());
			} else if (devData == null){
//				obj.put(dataName, "");	//DevCheck
			}
			
			return obj;
		}
		@Override
		public byte[] byteEncoder() {
			try {
				return jsonEncoder().toString().getBytes(SocketConst.CHARSET_TCP_CONTENT);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * 控制包回复实体类
		 */
		public static class Ack extends PacketEntity.DevCmd.Ack 
							implements IJsonDecoder{

			private final static String retName = "ret";
			
			public Ack() {
			}
//			public Ack(int sn, int ret) {
//				super(sn, ret);
//			}
			@Override
			public int getProtocolType() {
				return getMyProtocolType();
			}
			
			@Override
			public boolean jsonDecoder(JSONObject json) throws JSONException {
				if(!cmdValue.equals(json.getString(cmdName))){
					return false;
				}
				sn = json.getInt(snName);
				ret = json.getInt(retName);
				return true;
			}
			@Override
			public boolean byteDecoder(byte[] byteFillArray) {
				try {
					String jsonStr = new String(byteFillArray, SocketConst.CHARSET_TCP_CONTENT);
					return jsonDecoder(new JSONObject(jsonStr));
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return false;
			}
		}
	}
	
	
	/**
	 * 设备登录指令实体类
	 * @author wangyf
	 */
	public static class DevLogin extends PacketEntity.DevLogin implements IJsonEncoder{
		//登录指令本质上就是没有data的指令
		public final static String cmdValue = "login";
		
		public DevLogin() {
		}
		public DevLogin(int sn) {
			super(sn);
		}
		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}
		@Override
		public byte[] byteEncoder() {
			try {
				return jsonEncoder().toString().getBytes(SocketConst.CHARSET_TCP_CONTENT);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public JSONObject jsonEncoder() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(snName, sn);
			json.put(cmdName, cmdValue);
			return json;
		}
		
		/**
		 * 设备登录指令回复实体类
		 */
		public static class Ack extends PacketEntity.DevLogin.Ack implements IJsonDecoder{

			private String protocolVer;
			private String hfHardwareVer;
			private String hfSoftwareVer;
			
			@Override
			public int getProtocolType() {
				return getMyProtocolType();
			}
			@Override
			public int getProductType() {
				return 0;
			}
			
			@Override
			public boolean byteDecoder(byte[] byteFillArray) {
				try {
					String jsonStr = new String(byteFillArray, SocketConst.CHARSET_TCP_CONTENT);
					return jsonDecoder(new JSONObject(jsonStr));
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return false;
			}
			@Override
			public boolean jsonDecoder(JSONObject json) throws JSONException {
				//TODO 解析
				return false;
			}
		}
	}
	
	
	/**
	 * 设备查询指令实体类
	 * @author wangyf
	 */
	public static class DevCheck extends PacketEntity.DevCheck implements IJsonEncoder{
		//查询指令本质与设备上报内容雷同，方向相反\
		public final static String cmdValue = "info";
		
		public DevCheck() {
		}
		public DevCheck(int sn) {
			super(sn);
		}
		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}
		@Override
		public byte[] byteEncoder() {
			try {
				return jsonEncoder().toString().getBytes(SocketConst.CHARSET_TCP_CONTENT);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public JSONObject jsonEncoder() throws JSONException {
			JSONObject json = new JSONObject();
			json.put(snName, sn);
			json.put(cmdName, cmdValue);
			return json;
		}
		
		/**
		 * 设备查询指令回复实体类
		 */
		public static class Ack extends PacketEntity.DevCheck.Ack implements IJsonDecoder{

			@Override
			public int getProtocolType() {
				return getMyProtocolType();
			}
			@Override
			public boolean byteDecoder(byte[] byteFillArray) {
				try {
					String jsonStr = new String(byteFillArray, SocketConst.CHARSET_TCP_CONTENT);
					return jsonDecoder(new JSONObject(jsonStr));
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return false;
			}
			@Override
			public boolean jsonDecoder(JSONObject json) throws JSONException {
				//TODO 解析
				return false;
			}
		}

	}
	
	
	/**
	 * 心跳包实体类
	 * @author wangyf
	 */
	public static class HeartBeat extends PacketEntity.HeartBeat 
								implements IJsonEncoder{
		public final static String cmdValue = "heartbeat";
		
		public HeartBeat() {
		}
		public HeartBeat(int sn) {
			super(sn);
		}		
		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}
		
		@Override
		public JSONObject jsonEncoder() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put(snName, sn);
			obj.put(cmdName, cmdValue);
			return obj;
		}
		@Override
		public byte[] byteEncoder() {
			try {
				return jsonEncoder().toString().getBytes(SocketConst.CHARSET_TCP_CONTENT);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * 心跳包回复实体类
		 */
		public static class Ack extends PacketEntity.HeartBeat.Ack 
								implements IJsonDecoder{
			public Ack() {
			}
//			public Ack(int sn) {
//				super(sn);
//			}
//			
			@Override
			public int getProtocolType() {
				return getMyProtocolType();
			}
			
			@Override
			public boolean jsonDecoder(JSONObject json) throws JSONException {
				if(!cmdValue.equals(json.getString(cmdName))){
					return false;
				}
				sn = json.getInt(snName);
				return true;
			}
			@Override
			public boolean byteDecoder(byte[] byteFillArray) {
				try {
					String jsonStr = new String(byteFillArray, SocketConst.CHARSET_TCP_CONTENT);
					return jsonDecoder(new JSONObject(jsonStr));
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return false;
			}
		}
	}
		
	
	
	/**
	 * 设备状态上报实体类
	 * @author wangyf
	 */
	public static class DevStatus extends PacketEntity.DevStatus 
							implements IJsonDecoder, IJsonEncoder{
		//		private String data[];
		public final static String cmdValue = "upload";
		public final static String macName = "mac";
		public final static String dataName = "data";
		
		public DevStatus() {
		}
		public DevStatus(int productType) {
			this.productType = productType;
		}
//		public DevStatus(int sn, long mac, DevData devData) {
//			super(sn, mac, devData);
//		}
		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}
		
		@Override
		public boolean jsonDecoder(JSONObject json) throws JSONException {

			if(!cmdValue.equals(json.getString(cmdName))){
				return false;
			}
			sn = json.getInt(snName);
			String mac = ConvertUtil.macFormat(json.getString(macName));
			if (mac == null || mac.equals("")) {
				return false;
			} else {
				key = mac;
			}
			
			JSONArray dataArr = json.getJSONArray(dataName);
			
			devData = DevDataFactory.getDevData(productType);
			if (devData != null) {
				return devData.mcuDecoder(dataArr);
			}
			
			return false;
		}
		
		@Override
		public JSONObject jsonEncoder() throws JSONException {
			
			JSONObject json = new JSONObject();
			json.put(cmdName, cmdValue);

			if (sn != -1) {
				json.put(snName, sn);
			}
			if (key != null && !key.equals("")) {
				json.put(macName, ConvertUtil.macFormat(key));
			}
			
			if (devData != null) {
				//转成JSON格式，向上汇报
				json.put(dataName, devData.jsonEncoder());
			}
			return json;
		}
		
		@Override
		public boolean byteDecoder(byte[] byteFillArray) {
			try {
				String jsonStr = new String(byteFillArray, SocketConst.CHARSET_TCP_CONTENT);
				return jsonDecoder(new JSONObject(jsonStr));
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return false;
		}
	}


}
