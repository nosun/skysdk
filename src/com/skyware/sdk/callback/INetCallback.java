package com.skyware.sdk.callback;

import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.socket.IOHandler;

public interface INetCallback {

	/**
	 * 连接云平台出错
	 * 
	 * @param errType
	 *            出错类型
	 * @param errMsg
	 *            出错消息
	 */
	void onConnectCloudError(ErrorConst errType, String errMsg);


	void onConnectMqttError(ErrorConst errType, String errMsg);
	
	/**
	 * TCP连接成功
	 * 
	 * @param h
	 *            handler
	 * @param key
	 */
	void onConnectTCPSuccess(IOHandler h, String key);

	/**
	 * TCP连接出错
	 * 
	 * @param h
	 *            handler
	 * @param key
	 *            设备key
	 * @param errType
	 *            出错类型
	 * @param errMsg
	 *            出错消息
	 */
	void onConnectTCPError(IOHandler h, String key, ErrorConst errType, String errMsg);

	/**
	 * 成功接收到TCP包后的回调
	 * 
	 * @param h
	 *            handler
	 * @param packet
	 *            接收到的数据包
	 */
	void onReceiveTCP(IOHandler h, InPacket packet);

	/**
	 * 成功接收到UDP包后的回调
	 * 
	 * @param h
	 *            handler
	 * @param packet
	 *            接收到的数据包
	 */
	void onReceiveUDP(IOHandler h, InPacket packet);


	void onReceivePushMqtt(String topicName, byte[] payload, int qos, boolean retained);


	/**
	 * 接收TCP包错误
	 * 
	 * @param h
	 *            handler
	 * @param key
	 *            设备key
	 * @param errType
	 *            出错类型
	 * @param errMsg
	 *            出错信息
	 */
	void onReceiveTCPError(IOHandler h, String key, ErrorConst errType, String errMsg);

	/**
	 * 接收UDP包错误
	 * 
	 * @param h
	 *            handler
	 * @param errType
	 *            出错类型
	 * @param errMsg
	 *            出错信息
	 */
	void onReceiveUDPError(IOHandler h, String key, ErrorConst errType, String errMsg);

	/**
	 * 发送TCP包完成
	 * 
	 * @param h
	 *            handler
	 * @param packet
	 *            发送的包
	 */
	void onSendTCPFinished(IOHandler h, OutPacket packet);

	/**
	 * 发送UDP包完成
	 * 
	 * @param h
	 *            handler
	 * @param packet
	 *            发送的包
	 */
	void onSendUDPFinished(IOHandler h, OutPacket packet);

	/**
	 * 发送包错误
	 * 
	 * @param h
	 *            handler
	 * @param errType
	 *            出错类型
	 * @param packet
	 *            发送的包
	 */
	void onSendError(IOHandler h, OutPacket packet, ErrorConst errType, String errMsg);

	/**
	 * TCP连接关闭
	 * 
	 * @param h
	 *            handler
	 * @param key
	 */
	void onCloseTCP(IOHandler h, String key);



}
