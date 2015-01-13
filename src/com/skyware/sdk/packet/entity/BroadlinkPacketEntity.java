package com.skyware.sdk.packet.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.entity.DevData;
import com.skyware.sdk.entity.biz.DevDataBroadlink;
import com.skyware.sdk.util.ConvertUtil;

public class BroadlinkPacketEntity {
	

	public static final int CMD_TYPE_CHECK = 0;
	public static final int CMD_TYPE_OFF = 1;
	public static final int CMD_TYPE_ON 	= 2;
	private static final List<byte[]> CmdType = new ArrayList<byte[]>(3);
	static{
		CmdType.add(new byte[]{0x01,0x00,0x00,0x00,(byte) 0xb0,(byte) 0xbe,0x00,0x00});
		CmdType.add(new byte[]{0x01,0x00,0x00,0x00,(byte) 0xb1,(byte) 0xbe,0x00,0x00});
		CmdType.add(new byte[]{0x01,0x00,0x00,0x00,(byte) 0xb2,(byte) 0xbe,0x00,0x00});
	}
	private static final byte[] MagicHeader = new byte[]{
		0x5a,(byte) 0xa5,(byte) 0xaa,0x55,0x5a,(byte) 0xa5,(byte) 0xaa,0x55,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	};
	
	public static int getMyProtocolType() {
		return SDKConst.PROTOCOL_BROADLINK;
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
			return new byte[]{	0,0,0,0,0,0,0,0,
					8,0,0,0,(byte)0xDE,7,0X0A,21,
					3,4,10,0X0A,0,0,0,0,
					(byte)0XC0,(byte)0XA8,01,97,0X4A,(byte)0XA8,00,00,
					(byte)0XE0,(byte)0XC2,0,0,0,0,6,0,
					0,0,0,0,0,0,0,0};
		}
		
		/**
		 * 广播包回复实体类
		 */
		public static class Ack extends PacketEntity.DevFind.Ack{
			public Ack() {
			}
			@Override
			public int getProtocolType() {
				return getMyProtocolType();
			}
			@Override
			public boolean byteDecoder(byte[] byteFillArray) {
				// TODO 更严谨一些
				int indexIp = 8*6 + 6;
				int indexMac = indexIp + 4;
				
				if (byteFillArray.length <= indexMac + 6) {
					return false;
				}
				
				byte[] byteMac = new byte[6];
				System.arraycopy(byteFillArray, indexMac, byteMac, 0, 6);
				
				setKey(ConvertUtil.macByte2String(byteMac));
				return true;
			}
		}
		
	}

	public static class DevCmd extends PacketEntity.DevCmd {

		private String key;
		public DevCmd() {
		}
		public DevCmd(int sn, DevData data, String key) {
			super(sn, data);
			this.key = key;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}


		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}

		@Override
		public byte[] byteEncoder() {
			if (getData() instanceof DevDataBroadlink == false) {
				return null;
			}
			DevDataBroadlink bdata;
			if (getData() instanceof DevDataBroadlink) {
				bdata = (DevDataBroadlink) getData();
			} else {
				return null;
			}
			
			
			if (bdata.getDataCount() > 0 && bdata.getPower()!= null) {
				if(bdata.getPower().equals(DevDataBroadlink.POWER_ON)) {
					byte[] firstLine = new byte[]{
							0x4d,(byte) 0xcf,0x00,0x00,0x11,0x27,0x6a,0x00};
					byte[] snByte = ConvertUtil.snUnsignedShort2Byte(getSn());
					byte[] macByte = ConvertUtil.macString2Byte(getKey());
					byte[] cmdType = CmdType.get(CMD_TYPE_ON);
					
					byte[] onTailor = new byte[]{
							0x3a,0x01,0x69,(byte) 0xfa,0x45,(byte) 0xac,(byte) 0xea,0x55,
							(byte) 0xb9,(byte) 0xb5,(byte) 0x99,0x40,(byte) 0x96,0x60,0x73,0x73};
					
					byte[] ret = new byte[MagicHeader.length + firstLine.length + snByte.length
					                      + macByte.length + cmdType.length + onTailor.length];
					int index = 0;
					System.arraycopy(MagicHeader,	0, ret, index, MagicHeader.length);	index += MagicHeader.length;
					System.arraycopy(firstLine,		0, ret, index, firstLine.length);	index += firstLine.length;
					System.arraycopy(snByte, 		0, ret, index, snByte.length);		index += snByte.length;
					System.arraycopy(macByte,		0, ret, index, macByte.length);		index += macByte.length;
					System.arraycopy(cmdType,		0, ret, index, cmdType.length);		index += cmdType.length;
					System.arraycopy(onTailor, 		0, ret, index, onTailor.length);	
					
					return ret;
				} else if (bdata.getPower().equals(DevDataBroadlink.POWER_OFF)) {
					byte[] firstLine = new byte[]{
							(byte) 0x8b,(byte) 0xcf,0x00,0x00,0x11,0x27,0x6a,0x00};
					byte[] snByte = ConvertUtil.snUnsignedShort2Byte(getSn());
					byte[] macByte = ConvertUtil.macString2Byte(getKey());
					byte[] cmdType = CmdType.get(CMD_TYPE_OFF);
					
					byte[] offTailor = new byte[]{
							0x55,(byte) 0xaa,(byte) 0xba,0x64,0x12,(byte) 0x9e,0x14,(byte) 0xb8,
							0x75,0x16,(byte) 0xda,0x24,(byte) 0xc9,(byte) 0xb1,(byte) 0xb0,0x0d};
					
					byte[] ret = new byte[MagicHeader.length + firstLine.length + snByte.length
					                      + macByte.length + cmdType.length + offTailor.length];
					int index = 0;
					System.arraycopy(MagicHeader,	0, ret, index, MagicHeader.length);	index += MagicHeader.length;
					System.arraycopy(firstLine,		0, ret, index, firstLine.length);	index += firstLine.length;
					System.arraycopy(snByte, 		0, ret, index, snByte.length);		index += snByte.length;
					System.arraycopy(macByte,		0, ret, index, macByte.length);		index += macByte.length;
					System.arraycopy(cmdType,		0, ret, index, cmdType.length);		index += cmdType.length;
					System.arraycopy(offTailor, 	0, ret, index, offTailor.length);
					
					return ret;
				}
			}
			return null;
		}

	}


	public static class HeartBeat extends PacketEntity.HeartBeat {

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
		public byte[] byteEncoder() {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	
	public static class DevStatus extends PacketEntity.DevStatus {
		public final static String cmdName = "cmd";
		public final static String snName = "sn";
		public final static String cmdValue = "upload";
		public final static String macName = "mac";
		public final static String dataName = "data";
		
		@Override
		public int getProtocolType() {
			return getMyProtocolType();
		}

		@Override
		public boolean byteDecoder(byte[] byteFillArray) {
			if (byteFillArray.length != 72) {
				return false;
			}
			int magicLen = MagicHeader.length;
			int snOffset = magicLen + 8;
			byte[] snByte = new byte[2]; 
			System.arraycopy(byteFillArray, snOffset, snByte, 0, 2);
			setSn(ConvertUtil.snByte2UnsignedShort(snByte));
			
			int macOffset = snOffset + 2;
			byte[] macByte = new byte[6]; 
			System.arraycopy(byteFillArray, macOffset, macByte, 0, 6);
			setKey(ConvertUtil.macByte2String(macByte));
			
			int dataOffset = macOffset + 6;
			byte[] dataByte = new byte[8]; 
			System.arraycopy(byteFillArray, dataOffset, dataByte, 0, 8);
			if(Arrays.equals(dataByte, CmdType.get(CMD_TYPE_ON))){
				DevData data = new DevDataBroadlink();
				data.setPower(DevDataBroadlink.POWER_ON);
				setDevData(data);
				return true;
			} else if (Arrays.equals(dataByte, CmdType.get(CMD_TYPE_OFF))) {
				DevData data = new DevDataBroadlink();
				data.setPower(DevDataBroadlink.POWER_OFF);
				setDevData(data);
				return true;
			}
			return false;
		}

		@Override
		//向sdk user上报，为了统一格式
		public JSONObject jsonEncoder() throws JSONException {
			
			JSONObject json = new JSONObject();
			
			json.put(cmdName, cmdValue);

			if (getSn() != -1) {
				json.put(snName, getSn());
			}
			if (getKey() != null && !getKey().equals("")) {
				json.put(macName, getKey());
			}
			
			if (getDevData() != null) {
				//转成JSON格式，向上汇报
				json.put(dataName, getDevData().jsonEncoder());
			}
					
			return json;
		}

	}


}
