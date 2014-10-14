package com.skyware.sdk.callback;

import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.socket.IOHandler;


public interface ISocketCallback {

	/**
     * 	当socket开启出现错误的回调
     * 
     * 	@param h	handler自身
     * 	@param e	捕捉到的异常
     */
    public void onStartError(IOHandler h, Exception e);
    
    /**
     * 	当socket开启成功的回调
     * 
     * 	@param h	handler自身
     */
    public void onStartFinished(IOHandler h);
	
	/**
     * 	接收到数据包的回调
     * 
     * 	@param packet	接收的数据包
     */
    public void onReceive(InPacket packet);

    /**
     *	接收数据包出现异常的回调
     *
     *	@param h	handler自身
     *	@param e	捕捉到的异常
     */
    public void onReceiveError(IOHandler h, Exception e);
    
    /**
     * 	当发送数据完成回调
     * 
     * 	@param packet	发送的数据包
     */
    public void onSendFinished(OutPacket packet);

    /**
     * 	当发送数据出现异常的回调
     * 
     * 	@param e		捕捉到的异常
     * 	@param packet 	发送的数据包
     */
    public void onSendError(Exception e, OutPacket packet);
    
    /**
     * 	当释放资源成功的回调
     * 
     *	@param h	handler自身
     */
    public void onCloseFinished(IOHandler h);
    

    /**
     *	当释放资源失败的回调
     *
     *	@param h	handler自身
     *	@param e	异常
     */
    public void onCloseError(IOHandler h, Exception e);

}
