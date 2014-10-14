package com.skyware.sdk.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;

import com.skyware.sdk.callback.ISocketCallback;
import com.skyware.sdk.consts.SocketConst;

public abstract class BIOHandler implements IOHandler{
	
	/** 是否退出(外界可强制要求停止read循环) */
	protected AtomicBoolean isExit;
	
	/** 访问地址 */
	protected InetSocketAddress mTargetAddress;

	/** 接收到数据的回调 */
	protected ISocketCallback mSocketCallback;

	/** 是否已经初始化 */
	protected volatile boolean mIsStarted = false;
	
	/** 是否正在运行Runnable方法 */
	protected volatile boolean mIsRunning = false;
	
	/** 字符集转换 */
	protected Charset charset;
	
	/**-------- BIO特有属性 ---------
	
	/** 发送缓冲区 */
	protected byte[] mSendBuf;
	/** 发送包大小 */
	protected int mSendMsgSize;

	/** 接收缓冲区 */
	protected byte[] mRecvBuf;
	/** 接收包大小 */
	protected int mRecvMsgSize;

	
	public BIOHandler() {
		isExit = new AtomicBoolean(false);
		charset = Charset.forName("US-ASCII");
	}
	

	@Override
	public boolean start() throws IOException {
		mSendBuf = new byte[SocketConst.SEND_BUFFER_MAX_SIZE];
		mRecvBuf = new byte[SocketConst.RECV_BUFFER_MAX_SIZE];
		return true;
	}


	/**
	 * @Description 注册包接收回调
	 * @param packageSendReceiveEvent
	 * @return void
	 * 
	 */
	public synchronized void setSocketCallback(ISocketCallback socketCallback) {
		mSocketCallback = socketCallback;
	}

	
	/**
	 * 停止读循环（外界调用）
	 */
	@Override
	public void stopRunning(){
		isExit.set(true);
	}
	
	/**
	 * 释放资源
	 */
	@Override
	public boolean dispose() {
//		mTargetAddress = null;
//		if (mSocketCallback != null) {
//			mSocketCallback.onDisposeFinished();
//		}
		mSendBuf = null;
		mRecvBuf = null;
		mIsRunning = false;
		mIsStarted = false;
		isExit.set(true);
		return true;
	}

	/**
	 * 判断是否已经初始化
	 * 
	 * @return boolean 是否运行状态
	 */
	@Override
	public boolean isStarted() {
		return mIsStarted;
	}
	
	/**
	 * 判断是否在运行状态
	 * 
	 * @return boolean 是否运行状态
	 */
	@Override
	public boolean isRunning() {
		return mIsRunning;
	}

}
