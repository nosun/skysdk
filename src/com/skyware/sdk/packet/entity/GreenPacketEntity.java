package com.skyware.sdk.packet.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.consts.SDKConst;
import com.skyware.sdk.entity.biz.DevDataGreen;

public class GreenPacketEntity {
	
	public static final int CMD_NUM = 1;
	public static final int CMD_CHECK_INDEX = 0;
	
	private static final byte[] CMD_CONTENTS_LIST = new byte[CMD_NUM];
	static{
		CMD_CONTENTS_LIST[CMD_CHECK_INDEX] = 0X04;
	}
	public static final byte[] MagicHeader = new byte[]{
		0x00, 0x00, 0x00, 0x00, 0x00
	};
	
	public static int getMyProtocolType() {
		return SDKConst.PROTOCOL_GREEN;
	}

	

	public static class DevCheck extends PacketEntity.DevCheck {

		public DevCheck() {
		}

		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}

		@Override
		public byte[] byteEncoder() {

			return new byte[]{
				0x01,0x04, 0x00,0x00, 0x00,0x0C 
			};
		}

	}
	
	
	public static class DevStatus extends PacketEntity.DevStatus {
		
		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}

		@Override
		public boolean byteDecoder(byte[] byteFillArray) {
			if (byteFillArray.length < 3) {
				return false;
			}
			int devId = byteFillArray[0];
			int cmdType = byteFillArray[1];
			int dataLen = byteFillArray[2];
			if (cmdType != CMD_CONTENTS_LIST[CMD_CHECK_INDEX]) {
				return false;
			}
			byte[] dataBytes = new byte[dataLen];
			System.arraycopy(byteFillArray, 3, dataBytes, 0, dataLen);
			DevDataGreen devData = new DevDataGreen();
			if(!devData.mcuDecoder(dataBytes)){
				return false;
			}
			setDevData(devData);
			return true;
		}

		@Override
		//向sdk user上报，为了统一格式
		public JSONObject jsonEncoder() throws JSONException {
			return null;
			
//			JSONObject json = new JSONObject();
//			
//			json.put(cmdName, cmdValue);
//
//			if (getSn() != -1) {
//				json.put(snName, getSn());
//			}
//			if (getMac() != -1) {
//				json.put(macName, ConvertUtil.macLong2String(getMac()));
//			}
//			
//			if (getDevData() != null) {
//				//转成JSON格式，向上汇报
//				json.put(dataName, getDevData().jsonEncoder());
//			}
					
//			return json;
		}

	}


}
