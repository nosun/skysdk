package com.skyware.sdk.packet.entity;

import com.skyware.sdk.consts.SDKConst;
import com.skyware.sdk.util.ConvertUtil;

public class BroadlinkPacketEntity {
	

	public static int getMyProtocolType() {
		return SDKConst.PROTOCOL_BROADLINK;
	}
	
	
	/**
	 *	设备发现包实体类
	 *	@author wangyf 2014-10-9
	 */
	public static class DevFind extends PacketEntity.DevFind{
		
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

			@Override
			public int getProtocolType() {
				return getMyProtocolType();
			}
			@Override
			public boolean byteDecoder(byte[] byteFillArray) {
				// TODO 更严谨一些
				int indexIp = 8*6 + 6;
				int indexMac = indexIp + 4;
				
				byte[] byteMac = new byte[6];
				System.arraycopy(byteFillArray, indexMac, byteMac, 0, 6);
				
				setMac(ConvertUtil.macByte2Long(byteMac));
				return true;
			}
		}
		
	}

	public static class DevCmd extends PacketEntity.DevCmd {

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


	public static class HeartBeat extends PacketEntity.HeartBeat {

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
}
