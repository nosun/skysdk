package com.skyware.sdk.manage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

import com.skyware.sdk.callback.INetCallback;
import com.skyware.sdk.callback.ISocketCallback;
import com.skyware.sdk.callback.UDPCallback;
import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.socket.IOHandler;
import com.skyware.sdk.socket.UDPBroadcaster;
import com.skyware.sdk.util.PacketHelper;

public class UDPCommunication extends SocketCommunication{

	/**	负责广播的IOHandler */
	private IOHandler mBroadcastHandler;
	
	/**	Start线程的Future */
	private Future<Object> mStartFuture;
	
	/**	Receiver线程的Future */
	private Future<Object> mReceiverFuture;
	
	/**	Broadcaster线程的Future */
	private ScheduledFuture<?> mBroadcasterFuture;
	
	/**	处理Receiver和周期broadcast的线程池 */
	private ScheduledThreadPoolExecutor mThreadPool;
	
	/** WiFi网段下的广播地址 */
	private InetSocketAddress mBroadcastAddr;
	
	public UDPCommunication(INetCallback netCallback, InetSocketAddress broadcastAddr) {
		
		mThreadPool = new ScheduledThreadPoolExecutor(3);	//Start线程、发送线程与接收线程
		
		mSocketCallback = new UDPCallback(this);
		setNetCallback(netCallback);
		
		mBroadcastAddr = broadcastAddr;
		mBroadcastHandler = new BroadcastReceiverTask(
						SocketConst.LOCAL_PORT_UDP_DEFAULT,
						mBroadcastAddr,
						mSocketCallback);
		try {
			mBroadcastHandler.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.e(this.getClass().getSimpleName(), "Construct!");
	}

	@Override
	public void sendPacketSync(OutPacket packet){
		if (mBroadcastHandler.isStarted()) {
			mBroadcastHandler.send(packet);
		}
	}
	
	
	
	
	/**
	 * 开启广播接收线程
	 */
	public boolean startReceiverThread(){
		if (mReceiverFuture != null) {		//第二次开启
			if (!mReceiverFuture.isDone()) {	//线程未关闭
				return false;
			} else {	//线程已关闭，需重新生成Handler
				if (mBroadcastHandler == null || mBroadcastHandler.isStarted() == false) {
					mBroadcastHandler = new BroadcastReceiverTask(
							SocketConst.LOCAL_PORT_UDP_DEFAULT,
							mBroadcastAddr,
							mSocketCallback);
					
					try {
						//开启start()线程
						mBroadcastHandler.start();
						//等待start完成
						mStartFuture.get(SocketConst.BIO_TIMEOUT_UDP_START, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
						mSocketCallback.onStartError(mBroadcastHandler, e);
					} catch (ExecutionException e) {
						e.printStackTrace();
						mSocketCallback.onStartError(mBroadcastHandler, new Exception(e.getCause()));
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						mSocketCallback.onStartError(mBroadcastHandler, e);
					} catch (IOException e) {
						e.printStackTrace();
						mSocketCallback.onStartError(mBroadcastHandler, e);
					}
				}
			}
		}
		if (mBroadcastHandler.isStarted()) {
			mReceiverFuture = mThreadPool.submit(mBroadcastHandler);
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
		if (mBroadcasterFuture != null){
			if (!mBroadcasterFuture.isDone()) {	//线程未关闭
				return false;
			} else {	//线程已关闭，需重新生成Handler
				// 
				if (mBroadcastHandler == null || mBroadcastHandler.isStarted() == false) {
					mBroadcastHandler = new BroadcastReceiverTask(
							SocketConst.LOCAL_PORT_UDP_DEFAULT,
							mBroadcastAddr,
							mSocketCallback);
					try {
						//开启start()线程
						mBroadcastHandler.start();
						//等待start完成
						mStartFuture.get(SocketConst.BIO_TIMEOUT_UDP_START, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
						mSocketCallback.onStartError(mBroadcastHandler, e);
					} catch (ExecutionException e) {
						e.printStackTrace();
						mSocketCallback.onStartError(mBroadcastHandler, new Exception(e.getCause()));
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						mSocketCallback.onStartError(mBroadcastHandler, e);
					} catch (IOException e) {
						e.printStackTrace();
						mSocketCallback.onStartError(mBroadcastHandler, e);
					}
				}
			}
		}
		if (mBroadcastHandler.isStarted()) {
			mBroadcasterFuture = mThreadPool.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					if(mBroadcastHandler == null 
							|| !mBroadcastHandler.isStarted()){
						throw new RuntimeException("IOHandler isn't stated!");
					}
					try {
						mBroadcastHandler.send(PacketHelper.getBroadcastPacket());
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}				
				}
			}, 0, period, TimeUnit.MILLISECONDS);
			return true;
		}
		
		return false;
	}
	
	/**
	 * 关闭广播接收线程
	 */
	public void stopReceiverThread(){
		if(mReceiverFuture != null && !mReceiverFuture.isDone()){
			mBroadcastHandler.stopRunning();
			mReceiverFuture.cancel(false); //will block
			mBroadcastHandler = null;
		}
	}
	
	/**
	 * 关闭广播发送线程
	 */
	public void stopBroadcasterThread(){
		if(mBroadcasterFuture != null && !mBroadcasterFuture.isDone()){
			mBroadcasterFuture.cancel(true); 
			mBroadcastHandler = null;
		}
	}


	@Override
	public boolean isWorking() {
		if (mBroadcastHandler == null || 
				!mBroadcastHandler.isStarted()) {
			return false;
		}
		return true;
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
	
	
	private class BroadcastReceiverTask extends UDPBroadcaster{
		
		private AtomicInteger initBroadcastTimes; 
		
		private AtomicInteger initBroadcastPeriod; 
		
		public BroadcastReceiverTask(int bindPort, InetSocketAddress broadcastAddr,
				ISocketCallback callback) {
			super(bindPort, broadcastAddr, callback);
			initBroadcastTimes = new AtomicInteger(5);
			initBroadcastPeriod = new AtomicInteger(100);
		}
		
		@Override
		public boolean start() {
			
			mStartFuture = mThreadPool.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					return BroadcastReceiverTask.super.start();
				}
			});
			return true;
		}
		

		@Override
		public Object call() throws Exception {
			
			while(initBroadcastTimes.getAndDecrement() > 0){
				send(PacketHelper.getBroadcastPacket());
				Thread.sleep(initBroadcastPeriod.get());
			}
//			receiveLoop(SocketConst.BIO_TIMEOUT_BROADCAST_RECV, 0);
			receiveLoop(0, 0);
			return null;
		}
	}



}
