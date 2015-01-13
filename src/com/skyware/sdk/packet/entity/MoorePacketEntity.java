package com.skyware.sdk.packet.entity;

import static com.skyware.sdk.packet.entity.MooreLegalHelper.checkRetValidate;
import static com.skyware.sdk.packet.entity.MooreLegalHelper.checkSnValidate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.IJsonDecoder;
import com.skyware.sdk.entity.IJsonEncoder;
import com.skyware.sdk.entity.biz.DevDataHezhong;
import com.skyware.sdk.util.ConvertUtil;

public class MoorePacketEntity{
	
	public final static String cmdName = "cmd";
	public final static String snName = "sn";
	
	public final static String netStatusName = "deviceOnline";
	
	public static int getMyProtocolType() {
		return SDKConst.PROTOCOL_MOORE;
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
			String ip;
			
			public Ack() {
			}
//			public Ack(long mac, String ip) {
//				super(mac);
//				this.ip = ip;
//			}
			public void setIp(String ip){
				this.ip = ip;
			}
			public String getIp(){
				return ip;
			}
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
					setKey(ConvertUtil.macFormat(strSplit[1].trim()));
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
		public DevCmd(int sn, DevData data) {
			super(sn, data);
		}
		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}
		@Override
		public void setSn(int sn) {
			if(checkSnValidate(sn))
				super.setSn(sn);
		}
			
		@Override
		public JSONObject jsonEncoder() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put(snName, getSn());
			obj.put(cmdName, cmdValue);
			
			if (getData() != null && getData().getDataCount() > 0) {
//				obj.put(dataName, ((DevDataLierda) getData()).mcuCoder());
				obj.put(dataName, ((DevDataHezhong) getData()).mcuCoder());
			} else if (getData() == null){
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
			public void setSn(int sn) {
				if(checkSnValidate(sn))
					super.setSn(sn);
			}
			@Override
			public void setRet(int ret) {
				if(checkRetValidate(ret))
					super.setRet(ret);
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
			@Override
			public boolean byteDecoder(byte[] byteFillArray) {
				String jsonStr = new String(byteFillArray);
				
				try {
					return jsonDecoder(new JSONObject(jsonStr));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return false;
			}
		}
	}
	
	
	
	/**
	 * 设备查询指令实体类
	 * @author wangyf
	 */
	public static class DevCheck extends PacketEntity.DevCheck{
		//查询指令本质上就是没有data的指令
		public DevCheck() {
		}
		public DevCheck(int sn) {
			super(sn);
		}
		@Override
		public int getProtocolType() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public byte[] byteEncoder() {
			// TODO Auto-generated method stub
			return null;
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
		public void setSn(int sn) {
			if(checkSnValidate(sn))
				super.setSn(sn);
		}
		@Override
		public JSONObject jsonEncoder() throws JSONException {
			JSONObject obj = new JSONObject();
			obj.put(snName, getSn());
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
			public void setSn(int sn) {
				if(checkSnValidate(sn))
					super.setSn(sn);
			}
			@Override
			public boolean jsonDecoder(JSONObject json) throws JSONException {
				if(!cmdValue.equals(json.getString(cmdName))){
					return false;
				}
				setSn(json.getInt(snName));
				return true;
			}
			@Override
			public boolean byteDecoder(byte[] byteFillArray) {
				String jsonStr = new String(byteFillArray);
				try {
					return jsonDecoder(new JSONObject(jsonStr));
				} catch (JSONException e) {
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
//		public DevStatus(int sn, long mac, DevData devData) {
//			super(sn, mac, devData);
//		}
		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}
		@Override
		public void setSn(int sn) {
			if(checkSnValidate(sn))
				super.setSn(sn);
		}
		
		@Override
		public boolean jsonDecoder(JSONObject json) throws JSONException {

			if(!cmdValue.equals(json.getString(cmdName))){
				return false;
			}
			setSn(json.getInt(snName));
			String mac = ConvertUtil.macFormat(json.getString(macName));
			if (mac == null || mac.equals("")) {
				return false;
			} else {
				setKey(mac);
			}
			
			JSONArray dataArr = json.getJSONArray(dataName);
//			DevDataLierda data = new DevDataLierda();
			DevDataHezhong data = new DevDataHezhong();
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
			if (getKey() != null && !getKey().equals("")) {
				json.put(macName, ConvertUtil.macFormat(getKey()));
			}
			
			if (getDevData() != null) {
				//转成JSON格式，向上汇报
				json.put(dataName, getDevData().jsonEncoder());
			}
					
			return json;
		}
		@Override
		public boolean byteDecoder(byte[] byteFillArray) {
			String jsonStr = new String(byteFillArray);
			try {
				return jsonDecoder(new JSONObject(jsonStr));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
		}
	}


}
