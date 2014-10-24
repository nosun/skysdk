package com.skyware.sdk.callback;

import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;

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

	/**
	 * TCP连接成功
	 * 
	 * @param mac
	 */
	void onConnectTCPSuccess(long mac);

	/**
	 * TCP连接出错
	 * 
	 * @param mac
	 *            设备mac
	 * @param errType
	 *            出错类型
	 * @param errMsg
	 *            出错消息
	 */
	void onConnectTCPError(long mac, ErrorConst errType, String errMsg);

	/**
	 * 成功接收到TCP包后的回调
	 * 
	 * @param packet
	 *            接收到的数据包
	 */
	void onReceiveTCP(InPacket packet);

	/**
	 * 成功接收到UDP包后的回调
	 * 
	 * @param packet
	 *            接收到的数据包
	 */
	void onReceiveUDP(InPacket packet);

	/**
	 * 接收TCP包错误
	 * 
	 * @param mac
	 *            设备mac
	 * @param errType
	 *            出错类型
	 * @param errMsg
	 *            出错信息
	 */
	void onReceiveTCPError(long mac, ErrorConst errType, String errMsg);

	/**
	 * 接收UDP包错误
	 * 
	 * @param errType
	 *            出错类型
	 * @param errMsg
	 *            出错信息
	 */
	void onReceiveUDPError(long mac, ErrorConst errType, String errMsg);

	/**
	 * 发送TCP包完成
	 * 
	 * @param packet
	 *            发送的包
	 */
	void onSendTCPFinished(OutPacket packet);

	/**
	 * 发送UDP包完成
	 * 
	 * @param packet
	 *            发送的包
	 */
	void onSendUDPFinished(OutPacket packet);

	/**
	 * 发送包错误
	 * 
	 * @param errType
	 *            出错类型
	 * @param packet
	 *            发送的包
	 */
	void onSendError(OutPacket packet, ErrorConst errType, String errMsg);

	/**
	 * TCP连接关闭
	 * 
	 * @param mac
	 */
	void onCloseTCP(long mac);


}
