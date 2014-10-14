package com.skyware.sdk.callback;

import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;

public interface IBizCallback {

    /**
     *	连接云平台出错
     * 
     *	@param errType	错误类型
     *	@param errMsg	错误消息
     */
    void onConnectCloudError(ErrorConst errType, String errMsg);
    
    /**
     *	发现新设备的回调
     *
     *	@param deviceMac	设备MAC
     *	@param deviceIp		设备IP
     */
    void onDiscoverNewDevice(String deviceMac, String deviceIp);
    
    /**
     *	接收到TCP包后的回调
     * 
     *	@param packet	接收到的数据包
     */
	void onRecvDevStatus(InPacket packet);
	
    /**
     *	接收到任何类型UDP包后的回调，用于调试和打印日志
     * 
     *	@param packet	接收到的数据包
     */
	void onRecvUDPPacket(InPacket packet);

    /**
     *	接收到任何类型TCP包后的回调，用于调试和打印日志
     * 
     *	@param packet	接收到的数据包
     */
	void onRecvTCPPacket(InPacket packet);
	
	/**
	 *	接收包错误
	 * 
	 *	@param errType	错误类型
	 *	@param errMsg	错误信息
	 */
	void onRecvError(ErrorConst errType, String errMsg);
   

    /**
     *	发送包完成
     * 
     *	@param packet	发送的包
     */
    void onSendTCPPacketSuccess(OutPacket packet);


    /**
     *	发送包错误
     *
     *	@param errType	错误类型
     *	@param errMsg	错误信息
     *	@param packet	发送的包
     */
    void onSendTCPPacketError(ErrorConst errType, String errMsg, OutPacket packet);
    

    /**
     *	提示sdk出现的错误
     *
     *	@param errType	错误类型
     *	@param errMsg	错误信息
     */
    void onSDKError(ErrorConst errType, String errMsg);
    
}
