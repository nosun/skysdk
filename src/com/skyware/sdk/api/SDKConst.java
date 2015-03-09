package com.skyware.sdk.api;



public class SDKConst {
	
//	public static enum ProductType{
//		AirPal_PL300,
//		AirPal_PL500,
//		IAQMonitor,
//		SP2,
//		Cair
//	}
	
	/** ---IMPORTANT--- BEBUG标记，上线时不要忘记设为false*/
	public static final Boolean FLAG_DEBUG = true;
	
	public static final String SP_NAME_MQTT = "skysdk_mqtt";
	
	/** 状态推送超时时间 */
	public static final int TIMEOUT_PUSH_STATUS = 1000 * 10;	//10s
	
	/** WIFI 模块AP模式，点对点通信*/
//	public static final Boolean FLAG_AP_MODE = true;
//	public static final long AP_MAC_LONG = 0xFFFFFFFFFFFEL;
	public static final String AP_KEY = "airpal.key.ap";
//	public static final int PROTOCOL_COUNT 	= 3; 
	
	//产品常量
	public static final int PRODUCT_UNKNOWN 		= 0X00;		
	
	public static final int PRODUCT_AIRPAL_PL200 	= 0X0101;	//AirPal 爱宝乐PL200
	public static final int PRODUCT_AIRPAL_PL500 	= 0X0102;	//AirPal 爱宝乐PL500
    public static final int PRODUCT_CAIR 			= 0X0111;	//Cair 	净元凯尔
    
    public static final int PRODUCT_GREEN_WHITE 	= 0X0201;	//中立格林白色模块
    public static final int PRODUCT_GREEN_BLACK1 	= 0X0202;	//中立格林黑色模块1
    public static final int PRODUCT_GREEN_BLACK2 	= 0X0203;	//中立格林黑色模块2
    
    public static final int PRODUCT_BL_SP2 			= 0X0301;	//博联sp2插座
    
    public static final int PRODUCT_ROYALSTAR 		= 0X0401;	//荣事达智能水壶
    //协议常量
	public static final int PROTOCOL_UNKNOWN 	= -0X01; 
	public static final int PROTOCOL_LIERDA 		= 0X00; 	//利尔达-黄工 	Json字符协议
	public static final int PROTOCOL_BROADLINK 	= 0X01;		//博联插座		二进制协议
	public static final int PROTOCOL_GREEN	 	= 0X02;		//中立格林		二进制协议
//	public static final int PROTOCOL_TCL360	 	= 0X03;
	
	public static final int[] PROTOCOL_FIND_SET 	= {0X00}; 
	public static final int[] PROTOCOL_TCP_SET 	= {0X00, 0X02}; 
	public static final int[] PROTOCOL_UDP_SET 	= {0X01}; 

    public static final int[] PROTOCOL_PORT_COMM = {8899, 80, 502};
    public static final int[] PROTOCOL_PORT_FIND = {48899, 80, 502};
//    public static final DevType[] PROTOCOL_DEV_TYPE = {DevType.AIR_PURIFIER, DevType.PLUGIN, DevType.AIR_MONITOR};

    /** Wifi AP+STA Mode ip / port --- 中立格林**/
    public static final String PROTOCOL_GREEN_WIFI_SSID = "MXCHIP";
    public static final String PROTOCOL_GREEN_WIFI_AP_IP = "192.168.1.1";
    public static final int PROTOCOL_GREEN_WIFI_AP_PORT = 502;
    public static final long PROTOCOL_GREEN_CHECK_INTERVAL = 2500;
    public static final int PROTOCOL_GREEN_PACKET_BYTES = 61;
    
    public static final int THREAD_POOL_CORE_INITIAL_NUM = 1;
    public static final int THREAD_TCP_MAX_NUM = 3;
    public static final int THREAD_UDP_MAX_NUM = 2;
    
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
	
	
	public static final int MSG_DISCOVER_FIND_NEW		= 0x0001;
//	public static final int MSG_DISCOVER_DISAPPEAR	= 0x003;
	public static final int MSG_DEVICE_CMD_ACK 		= 0x0101;
	public static final int MSG_DEVICE_STATUS 		= 0x0111;
	public static final int MSG_DEVICE_STATUS_JSON 	= 0x0112;
	
	public static final int MSG_DEVICE_CONNECT_RESULT		= 0x0201;
	public static final int MSG_DEVICE_DISCONN			= 0x0202;
	public static final int MSG_DEVICE_NETSTAT_CHANGE		= 0x0203;
	
	public static final int MSG_DEBUG_BROADCAST_RECEIVE 	= 0x8101;
	public static final int MSG_DEBUG_TCP_RECEIVE 	= 0x8111;
	public static final int MSG_DEBUG_TCP_SEND 		= 0x8112;

	
}
