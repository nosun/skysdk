package com.skyware.sdk.callback;

import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.entity.DeviceInfo.DevType;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;

public interface IBizCallback {

	/**
	 * 连接云平台出错
	 * 
	 * @param errType
	 *            错误类型
	 * @param errMsg
	 *            错误消息
	 */
	void onConnectCloudError(ErrorConst errType, String errMsg);
	
	/**
	 * 连接设备成功
	 * 
	 * @param deviceMac
	 */
	void onConnectDeviceSuccess(long deviceMac);

	
	/**
	 * 连接设备出错
	 * 
	 * @param deviceMac
	 *            设备mac
	 * @param errType
	 *            出错类型
	 * @param errMsg
	 *            出错消息
	 */
	void onConnectDeviceError(long deviceMac, ErrorConst errType, String errMsg);

	/**
	 * 设备连接断开
	 * 
	 * @param deviceMac
	 * @param errType
	 * @param errMsg
	 */
	void onDeviceDisconnected(long deviceMac, ErrorConst errType, String errMsg);

	/**
	 * 发现新设备的回调
	 * 
	 * @param deviceMac
	 *            设备MAC
	 * @param deviceIp
	 *            设备IP
	 * @param deviceType
	 *            设备类型
	 * @param protocol
	 *            协议版本
	 */
	void onDiscoverNewDevice(long deviceMac, String deviceIp, int protocol, DevType deviceType);

	/**
	 * 接收到设备的状态上报
	 * 
	 * @param status
	 *            接收到的数据包
	 */
	void onRecvDevStatus(DevStatus status);

	/**
	 * 接收到任何类型UDP包后的回调，用于调试和打印日志
	 * 
	 * @param packet
	 *            接收到的数据包
	 */
	void onRecvUDPPacket(InPacket packet);

	/**
	 * 接收到任何类型TCP包后的回调，用于调试和打印日志
	 * 
	 * @param packet
	 *            接收到的数据包
	 */
	void onRecvTCPPacket(InPacket packet);

	/**
	 * 接收包错误
	 * 
	 * @param errType
	 *            错误类型
	 * @param errMsg
	 *            错误信息
	 */
	void onRecvError(ErrorConst errType, String errMsg);


	/**
	 *	发送包完成
	 *
	 *	@param devMac	设备Mac
	 *	@param sn		包Sn
	 */
	void onSendCmdSuccess(long devMac, int sn);


	/**
	 *	发送包错误
	 *
	 *	@param devMac	设备Mac
	 *	@param sn		包Sn
	 *	@param errType	错误类型
	 *	@param errMsg	错误信息
	 */
	void onSendCmdError(long devMac, int sn, ErrorConst errType,
			String errMsg);

	/**
	 * 提示sdk出现的错误
	 * 
	 * @param errType
	 *            错误类型
	 * @param errMsg
	 *            错误信息
	 */
	void onSDKError(ErrorConst errType, String errMsg);

	void onSendTCPPacket(OutPacket packet);
}
