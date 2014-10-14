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
	public void onConnectCloudError(ErrorConst errType, String errMsg);

	/**
	 * TCP连接成功
	 * 
	 * @param mac
	 */
	public void onConnectTCPSuccess(String mac);

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
	public void onConnectTCPError(String mac, ErrorConst errType, String errMsg);

	/**
	 * 成功接收到TCP包后的回调
	 * 
	 * @param packet
	 *            接收到的数据包
	 */
	public void onReceiveTCP(InPacket packet);

	/**
	 * 成功接收到UDP包后的回调
	 * 
	 * @param packet
	 *            接收到的数据包
	 */
	public void onReceiveUDP(InPacket packet);

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
	public void onReceiveTCPError(String mac, ErrorConst errType, String errMsg);

	/**
	 * 接收UDP包错误
	 * 
	 * @param errType
	 *            出错类型
	 * @param errMsg
	 *            出错信息
	 */
	public void onReceiveUDPError(String mac, ErrorConst errType, String errMsg);

	/**
	 * 发送包完成
	 * 
	 * @param packet
	 *            发送的包
	 */
	public void onSendFinished(OutPacket packet);

	/**
	 * 发送包错误
	 * 
	 * @param errType
	 *            出错类型
	 * @param packet
	 *            发送的包
	 */
	public void onSendError(OutPacket packet, ErrorConst errType, String errMsg);

}
