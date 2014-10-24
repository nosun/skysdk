package com.skyware.sdk.manage;

import static com.skyware.sdk.consts.SocketConst.*;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

import com.skyware.sdk.callback.INetCallback;
import com.skyware.sdk.callback.ISocketCallback;
import com.skyware.sdk.callback.UDPCallback;
import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.consts.SDKConst;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.packet.entity.PacketHelper;
import com.skyware.sdk.socket.IOHandler;
import com.skyware.sdk.socket.UDPHandler;
import com.skyware.sdk.thread.ThreadPoolManager;

public class UDPCommunication extends SocketCommunication{

	/**	负责广播的IOHandler */
	private IOHandler mUDPHandler;
	
	/**	Start线程的Future */
	private Future<Object> mStartFuture;
	
	/**	Receiver线程的Future */
	private Future<Object> mReceiverFuture;
	
	/**	Broadcaster线程的Future */
	private ScheduledFuture<?> mBroadcasterFuture;
	
//	private ScheduledThreadPoolExecutor mThreadPool;
	
	/** WiFi网段下的广播地址 */
//	private InetSocketAddress mBroadcastAddr;
	
	public UDPCommunication(INetCallback netCallback) {

		/**	处理Receiver和周期broadcast的线程 */
//		mThreadPool = new ScheduledThreadPoolExecutor(3);	//Start线程、发送线程与接收线程
		ThreadPoolManager.getInstance().addCoreThread(SDKConst.THREAD_UDP_MAX_NUM);
		
		mSocketCallback = new UDPCallback(this);
		setNetCallback(netCallback);
		
		mUDPHandler = new UDPTask(mSocketCallback);
		
		try {
			mUDPHandler.start();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.e(this.getClass().getSimpleName(), "Construct!");
	}

	
	
	@Override
	public void sendPacketSync(OutPacket packet){
		if (mUDPHandler.isStarted()) {
			try {
				mUDPHandler.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	public boolean startNewUdpKeepAlive(long deviceMac,
			InetSocketAddress targetAddr) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean stopUdpKeepAlive(long deviceMac) {
		// TODO Auto-generated method stub
		return false;
	}


	
	/**
	 * 开启广播接收线程
	 */
	public boolean startReceiverThread(){
		if (mReceiverFuture != null && !mReceiverFuture.isDone()) {	//接收线程未关闭
			return false;
		} 
		//接收线程未创建或已关闭
		
		//如果UDP handler未创建/已销毁，或者创建了但未start
		if (mUDPHandler == null || !mUDPHandler.isStarted()) {
			//创建新的handler，并等待start完成
			createHandlerAndWaitForStart();
		}
		
		//如果Handler start成功，则创建receiver线程
		if (mUDPHandler != null && mUDPHandler.isStarted()) {
			mReceiverFuture = ThreadPoolManager.getInstance().getThreadPool().submit(mUDPHandler);
			return true;
		}
		return false;
	}
	
	/**
	 * 开启广播发送线程
	 * 
	 * @param period	广播发送周期(毫秒)
	 */
	public boolean startBroadcasterThread(int period){
		if (mBroadcasterFuture != null && !mBroadcasterFuture.isDone()) {	//广播线程未关闭
			return false;
		}
		//重建广播线程，先要检查是否start，正在start的时候需要等待
		
		//如果UDP socket未创建或已断开（dispose后为null）
		if (mUDPHandler == null || !mUDPHandler.isStarted()) {
			//创建新的handler，并等待start完成
			createHandlerAndWaitForStart();
		}

		if (mUDPHandler != null && mUDPHandler.isStarted()) {
			mBroadcasterFuture = ThreadPoolManager.getInstance().getThreadPool().scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					if(mUDPHandler != null && mUDPHandler.isStarted()){
//						throw new RuntimeException("IOHandler isn't stated!");
						try {
							for(int protocol : SDKConst.PROTOCOL_FIND_SET){
								mUDPHandler.send(PacketHelper.getBroadcastFindPacket(protocol));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}, 0, period, TimeUnit.MILLISECONDS);
			return true;
		}
		
		return false;
	}
	
	private boolean createHandlerAndWaitForStart() {
		if (mUDPHandler == null) {
			//创建新的handler
			mUDPHandler = new UDPTask(mSocketCallback);
		}
		try {
			//开启start()线程
			mUDPHandler.start();
			//等待start完成
			return (Boolean) mStartFuture.get(TIMEOUT_UDP_START, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
			mSocketCallback.onStartError(mUDPHandler, new Exception(e.getCause()));
		} catch (IOException e) {
			e.printStackTrace();
			mSocketCallback.onStartError(mUDPHandler, e);
		} catch (TimeoutException e) {	//start超时
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	
	/**
	 * 关闭广播接收线程
	 */
	public void stopReceiverThread(){
		if(mReceiverFuture != null && !mReceiverFuture.isDone()){
//			mBroadcastHandler.stopRunning();
//			mReceiverFuture.cancel(false); //will block
			if (mUDPHandler != null) {
				mUDPHandler.dispose();
			}
			mReceiverFuture.cancel(true);
//			mUDPHandler = null;
		}
	}
	
	/**
	 * 关闭广播发送线程
	 */
	public void stopBroadcasterThread(){
		if(mBroadcasterFuture != null && !mBroadcasterFuture.isDone()){
//			if (mUDPHandler != null) {
//				mUDPHandler.dispose();
//			}
			mBroadcasterFuture.cancel(true); 
//			mUDPHandler = null;
		}
	}


	@Override
	public boolean isWorking() {
		
		if (mUDPHandler != null && mUDPHandler.isStarted()) {
			return true;
		}
		return false;
	}
	
	@Override
	public void finallize() {
		super.finallize();
		
		stopReceiverThread();
		stopBroadcasterThread();
		
//		mThreadPool.shutdownNow();
//		mThreadPool = null;
		
		mStartFuture = null;
		mReceiverFuture = null;
		mBroadcasterFuture = null;
		
		mSocketCallback = null;
		setNetCallback(null);

		Log.e(this.getClass().getSimpleName(), "Destroy!");
	}
	
	

	/**
	 * 开启成功回调
	 * 
	 * @param h
	 */
	public void onStartSuccess(IOHandler h){
		if (mNetCallback == null)
			return;
//		if (h instanceof TCPConnector) {
//			mNetCallback.onConnectDeviceSuccess(((TCPConnector) h).getMac());
//		}
	}

	/**
	 *	开启错误回调（TCP三次握手错误）
	 *
	 *	@param h
	 *	@param errType
	 *	@param errMsg
	 */
	public void onStartError(IOHandler h, ErrorConst errType, String errMsg) {
		if (mNetCallback == null)
			return;
//		if (h instanceof TCPConnector) {
//			mNetCallback.onConnectDeviceError(((TCPConnector) h).getMac(), errType, errMsg);
//		}
	}
	
	@Override
	public void onReceive(InPacket packet) {
		if (mNetCallback == null)
			return;
		mNetCallback.onReceiveUDP(packet);
	}

	@Override
	public void onReceiveError(IOHandler h, ErrorConst errType, String errMsg) {
		if (mNetCallback == null)
			return;
//		if (h instanceof UDPBroadcaster) {
//			mNetCallback.onReceiveUDPError(((TCPConnector) h).getMac(), errType, errMsg);
//		} 
//		
//		if (h instanceof ) {
//			mNetCallback.onReceiveUDPError(((TCPConnector) h).getMac(), errType, errMsg);
//		}
		
	}
	
	@Override
	public void onSendFinished(OutPacket packet) {
		if (mNetCallback != null){
			mNetCallback.onSendUDPFinished(packet);
		}
	}
	
	@Override
	public void onCloseFinished(IOHandler h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCloseError(IOHandler h, ErrorConst errType, String errMsg) {
		// TODO Auto-generated method stub
		
	}

	
	
	private class UDPTask extends UDPHandler{
		
		private AtomicInteger initBroadcastTimes; 
		
		private AtomicInteger initBroadcastPeriod; 

		public UDPTask(ISocketCallback callback) {
			this(PORT_UDP_LOCAL, callback);
		}
		public UDPTask(int bindPort, ISocketCallback callback) {
			super(bindPort, callback);
			initBroadcastTimes = new AtomicInteger(5);
			initBroadcastPeriod = new AtomicInteger(100);
		}
		@Override
		public boolean dispose() {
			
			stopBroadcasterThread();
			mUDPHandler = null;
			return super.dispose();
		}
		
		@Override
		public boolean start() {
			
			mStartFuture = ThreadPoolManager.getInstance().getThreadPool().submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					synchronized (UDPTask.this) {	//防止两个线程同时start
						int reStartTimes = 0;
						while (++reStartTimes < SocketConst.RETRY_TIMES_UDP_START) {
							try{
								if (!isStarted()) {	//防止重复start
									return UDPTask.super.start();
								} 
							} catch (BindException e){	//防止绑定端口冲突
//								Log.e("WYF", "Bind port retry!");
								if (!isStarted()) {
									setLocalBindPort(getLocalBindPort() + 1);
									return UDPTask.super.start();
								}
							}
						}
						return false;
					}
				}
			});
			return true;
		}
		

		@Override
		public Object call() throws Exception {
			
			while(initBroadcastTimes.getAndDecrement() > 0){
				for(int protocol : SDKConst.PROTOCOL_FIND_SET){
					send(PacketHelper.getBroadcastFindPacket(protocol));
				}
				Thread.sleep(initBroadcastPeriod.get());
			}
//			receiveLoop(SocketConst.BIO_TIMEOUT_BROADCAST_RECV, 0);
			receiveLoop(0, 0);
			return null;
		}
	}


}
