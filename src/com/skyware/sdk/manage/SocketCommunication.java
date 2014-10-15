package com.skyware.sdk.manage;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

import com.skyware.sdk.callback.INetCallback;
import com.skyware.sdk.callback.ISocketCallback;
import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.socket.IOHandler;
import com.skyware.sdk.thread.PacketReportTask;
import com.skyware.sdk.thread.PacketSendTask;
import com.skyware.sdk.thread.SingleExecutor;

public abstract class SocketCommunication {
	
	/** 接收包队列 **/
	protected Queue<InPacket> mReceiveQueue;
	/** 发送包队列 **/
	protected Queue<OutPacket> mSendQueue;

	/** 单线程执行器对象 **/
	protected SingleExecutor mSingleExecutor;
	/** 发送包触发过程 **/
	protected Callable<Object> mPacketSendTask;
	/** 接收包触发过程 **/
	protected Callable<Object> mPacketReportTask;
	
	/** 包接收回调 **/
	protected ISocketCallback mSocketCallback;
	/** 业务回调 **/
	protected INetCallback mNetCallback;
	
		
	/** 注册业务回调(一般不会动态改变) */
	public void setNetCallback(INetCallback netCallback) {
		mNetCallback = netCallback;
	}
	
	public SocketCommunication(){
//		mReceiveQueue = new LinkedList<InPacket>();
//		mSendQueue = new LinkedList<OutPacket>();	
		// 改为线程安全的队列
		mReceiveQueue = new ConcurrentLinkedQueue<InPacket>();
		mSendQueue = new ConcurrentLinkedQueue<OutPacket>();
		
		mSingleExecutor = new SingleExecutor();
		mPacketSendTask = new PacketSendTask<Object>(this);
		mPacketReportTask = new PacketReportTask<Object>(this);
	}
    
	
	/**
	 * 直接同步发包，不经过队列缓存和异步线程
	 * 					(异步线程也会复用此方法发送数据)
	 * @param packet
	 */
	public abstract void sendPacketSync(OutPacket packet);
	

	
	/**
	 *	异步线程发送，添加packet到发送队列
	 *
	 *	@param packet	发送包
	 *	@param isPersist	长短连接
	 */
	public void sendPacketAsync(OutPacket packet) {
		if (mSingleExecutor == null) {
			Log.d(this.getClass().getSimpleName(),"sendpacket error,must call init() before!");
			return;
		}
		
		mSendQueue.offer(packet);
		mSingleExecutor.submit(mPacketSendTask);
	}
	
	/**
	 * 添加多个packet到发送队列，然后一起异步线程发送
	 * 
	 * @param	packet 发送包
	 * @return	void
	 * @Tips	synchronized方法
	 */
	public void sendPacketAsync(OutPacket[] packets) {
		sendPacketAsync(packets, mPacketSendTask);
	}
	
	/**
	 * 添加多个packet到发送队列，然后一起异步线程发送，可自定义发送callable
	 * 
	 * @param	packets		发送的包
	 * @param	trigger		自定义发包执行任务
	 * @return	void
	 * @Tips	synchronized方法
	 */
	public <V> void sendPacketAsync(OutPacket[] packets, Callable<V> trigger) {
		if (mSingleExecutor == null) {
			Log.d(this.getClass().getSimpleName(),"sendpacket error,must call init() before!");
			return;
		}
		
		for(OutPacket packet : packets){
			mSendQueue.offer(packet);
		}
		if (trigger != null) {
			mSingleExecutor.submit(trigger);
		}else {
			mSingleExecutor.submit(mPacketSendTask);
		}
	}
	
	
	/**
	 * 直接同步向业务层上报数据（推荐，除了Selector线程外）
	 * 
	 * @param	packet
	 * @return	void
	 */
	public void reportPacketSync(InPacket packet) {
		if (packet == null || mNetCallback == null)
			return;
		onReceive(packet);
	}
	
	/**
	 * 添加一个packet到接收队列，异步线程上报至业务层（不推荐，因为有不必要的线程开销）
	 * 
	 * @param	packet
	 * @return	void
	 * @Tips	synchronized方法
	 */
	public void reportPacketAsync(InPacket packet) {
		if (packet == null || mNetCallback == null)
			return;
		mReceiveQueue.offer(packet);
		mSingleExecutor.submit(mPacketReportTask);
	}

	
	/**
	 * 移除发送队列的一个数据包
	 * 
	 * @return packet 被移除的那个数据包
	 * 
	 */
	public OutPacket dequeueSendQueue() {
		return mSendQueue.poll();
	}

	/**
	 * 移除接收队列的一个数据包
	 * 
	 * @return MessagePacket 被移除的那个数据包
	 * 
	 */
	public InPacket dequeueReceiveQueue() {
		return mReceiveQueue.poll();
	}
	
	
	/**
	 * 检测Communication是否在工作
	 * 
	 * @return	是否在工作
	 */
	public abstract boolean isWorking();

	
	/**
	 *	释放资源
	 *
	 */
	public void dispose(){
		mReceiveQueue = null;
		mSendQueue = null;
		
		mSingleExecutor.dispose();
		mSingleExecutor = null;
		mPacketSendTask = null;
		mPacketReportTask = null;
	}
	
	/*
	 * -----------------------------------------------------------
	 *
	 * TODO:这里应该将底层上报的信息做简单处理分析，上报给业务层之间可显示的结论
	 */
	
	/** 
	 * 发送结束回调
	 * 
	 * @param packet
	 */
	public void onSendFinished(OutPacket packet) {
		if (mNetCallback == null)
			return;
		mNetCallback.onSendFinished(packet);
	}
	
	/**
	 *	发送错误回调
	 *
	 *	@param packet
	 *	@param errType
	 *	@param errMsg
	 */
	public void onSendError(OutPacket packet, ErrorConst errType, String errMsg) {
		if (mNetCallback == null)
			return;
		mNetCallback.onSendError(packet, errType, errMsg);
	}
	
	/** 
	 * 接收包成功的回调
	 * 
	 * @param packet
	 */
	abstract public void onReceive(InPacket packet);
	
	/**
	 *	接收错误回调
	 *
	 *	@param h		
	 *	@param errType
	 *	@param errMsg
	 */
	abstract public void onReceiveError(IOHandler h, ErrorConst errType, String errMsg);

	/**
	 *	关闭成功回调
	 *
	 *	@param h
	 */
	abstract public void onCloseFinished(IOHandler h);

	/**
	 *	关闭错误回调
	 *
	 *	@param h
	 *	@param errType	错误类型
	 *	@param errMsg	错误信息
	 */
	abstract public void onCloseError(IOHandler h, ErrorConst errType, String errMsg);

}
