package com.skyware.sdk.consts;


public class SDKConst {
	
	public static final int PROTOCOL_COUNT 	= 2; 
	
	public static final int PROTOCOL_UNKNOWN 		= -0X01; 
	public static final int PROTOCOL_LIERDA 		= 0X00; 
	public static final int PROTOCOL_BROADLINK 	= 0X01; 
	
/*	public static final int CMD_DISCOVER_START 	= 0x001;
	public static final int CMD_DISCOVER_STOP 	= 0x002;
	public static final int CMD_DEVICE_CONTROL 	= 0x101;
	
	public static final int RET_DISCOVER_START_OK 	= 0x1001;
	public static final int RET_DISCOVER_START_ERR 	= 0x1002;
	public static final int RET_DISCOVER_STOP_OK 		= 0x1003;
	public static final int RET_DISCOVER_STOP_ERR		= 0x1004;
	public static final int RET_DISCOVER_RECEIVE 		= 0x1011;
	public static final int RET_DISCOVER_NEW 			= 0x1012;
	public static final int RET_DEVICE_CONTROL_OK 	= 0x1101;
	public static final int RET_DEVICE_CONTROL_ERR 	= 0x1102; 
	public static final int RET_DEVICE_STATUS_REPORT 	= 0x1111;*/
	
	public static final String EXTRA_CMD = "com.skysdk.cmd";
	public static final String EXTRA_RET = "com.skysdk.ret";
	
	public static final int MSG_BROADCAST_RECEIVE 	= 0x001;
	public static final int MSG_DISCOVER_FIND_NEW		= 0x002;
//	public static final int MSG_DISCOVER_DISAPPEAR	= 0x003;
	public static final int MSG_DEVICE_CMD_ACK 		= 0x101;
	public static final int MSG_DEVICE_STATUS 		= 0x111;
	public static final int MSG_DEVICE_CONNECT 		= 0x201;
	public static final int MSG_DEVICE_DISCONN 		= 0x202;
	
	public static final int MSG_DEVICE_STATUS_DEBUG 	= 0x8111;
	
	
	public static final Boolean DEBUG_FLAG = true;
}
