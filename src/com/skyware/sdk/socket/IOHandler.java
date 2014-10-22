package com.skyware.sdk.socket;

import java.io.IOException;
import java.util.concurrent.Callable;
import com.skyware.sdk.packet.OutPacket;

public interface IOHandler extends Callable<Object>{

	/**
	 * 启动网络连接
	 * 
	 * @return boolean 是否创建成功
	 */
	boolean start() throws IOException;

    /**
     * 发送数据
     * 
     * @param packet	发送包
     * @return boolean 是否发送成功
     */
	boolean send(OutPacket packet) throws IOException;

    /**
     * 接收数据（阻塞）
     * 
     * @param	recvTimeout	接收超时时间，0表示永远阻塞
     * @return boolean 是否接收成功
     * @throws	IOException	超时异常 未开启异常
     */
	boolean receive(int recvTimeout) throws IOException;

    /**
     * 循环接收数据（长时间接收）
     * 
     * @param	recvTimeout	接收超时时间，0表示永远阻塞
     * @param	loopTimeout 	循环超时时间，0表示永远循环
     * @throws	TimeoutException
     */
//	void receiveLoop(int recvTimeout, int loopTimeout) throws TimeoutException;
    
    /**
     * 回收所有资源
     * 
     * @return boolean 是否释放成功
     */
	boolean dispose();
	
	
	/**
	 * 判断是否已启动连接
	 * 
	 * @return boolean 是否运行状态
	 */
	boolean isStarted();
	
	/**
	 * 判断是否在运行状态
	 * 
	 * @return boolean 是否运行状态
	 */
	boolean isRunning();
	
	/**
	 * 强制终止运行状态
	 */
	void stopRunning();
	
	/**
	 * 获取协议类型
	 */
//	int getProtocolType();
}
