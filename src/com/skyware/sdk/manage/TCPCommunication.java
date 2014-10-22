package com.skyware.sdk.manage;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

import com.skyware.sdk.callback.INetCallback;
import com.skyware.sdk.callback.ISocketCallback;
import com.skyware.sdk.callback.TCPCallback;
import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.exception.SkySocketCloseByRemoteException;
import com.skyware.sdk.exception.SkySocketUnstartedException;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.socket.IOHandler;
import com.skyware.sdk.socket.TCPConnector;

public class TCPCommunication extends SocketCommunication{

	/**
	 * 保持长连接的设备MAC-socket Map
	 */
	private ConcurrentMap<Long, IOHandler> mHandlerPersist;
	
	/**
	 * 短连接的设备MAC-socket Map，一旦接受到回馈数据就断开
	 * 		可能与一台设备（MAC）保持多个短连接（不同的socket，因为本地端口不同）
	 */
	private ConcurrentMap<Long, IOHandler> mHandlerTemp;

	/**
	 * 处理Socket连接线程的线程池
	 * 目前的网络通信采用TPC模型（一个连接一线程处理），包括接收线程，心跳
	 */
	private ScheduledThreadPoolExecutor mThreadPool;

    
	
	
	public TCPCommunication(INetCallback netCallback){
		super();
		mSocketCallback = new TCPCallback(this);
		
		mHandlerPersist = new ConcurrentHashMap<Long, IOHandler>();
		mHandlerTemp = new ConcurrentHashMap<Long, IOHandler>();
		
		mThreadPool = new ScheduledThreadPoolExecutor(5);
		
		setNetCallback(netCallback);
		
		Log.e(this.getClass().getSimpleName(), "Construct!");
	}
	
	
	public IOHandler startNewTCPTask(long mac, InetSocketAddress targetAddr, boolean isPersist) {
		TCPConnectTask newHandler = new TCPConnectTask(targetAddr, mSocketCallback, mac);
		
		if (isPersist) {
			newHandler.setTCPPersist(true);
			mHandlerPersist.put(mac, newHandler);
		} else {
			mHandlerTemp.put(mac, newHandler);
		}
		
		//提交任务
		newHandler.setFuture(mThreadPool.submit(newHandler));
		
		return newHandler;
	}
	
	
	public boolean stopTCPTask(long mac) {
		IOHandler handlerStop =  mHandlerTemp.get(mac);
		if (handlerStop == null) {
			handlerStop =  mHandlerPersist.get(mac);
		}
		if (handlerStop == null) {
			return false;
		}
		if (handlerStop instanceof TCPConnectTask) {
			return stopTCPConnectTask((TCPConnectTask)handlerStop);
		}
		return false;
	}
	
	public boolean stopAllTCPTask() {
		boolean b1 = false, b2 = false;
		for (IOHandler handlerStop : mHandlerPersist.values()) {
			if (handlerStop != null && handlerStop instanceof TCPConnectTask) {
				b1 = stopTCPConnectTask((TCPConnectTask)handlerStop);
			}
		}
		for (IOHandler handlerStop : mHandlerTemp.values()) {
			if (handlerStop != null && handlerStop instanceof TCPConnectTask) {
				b2 = stopTCPConnectTask((TCPConnectTask)handlerStop);
			}
		}
		return b1 && b2;
	}

	private boolean stopTCPConnectTask(TCPConnectTask task) {
		Future<Object> future = task.getFuture();
		
		if (future == null || future.isDone() || future.isCancelled()) {
			task.dispose();
			return false;
		} else {
			boolean b1 = future.cancel(true);
			boolean b2 = task.dispose();
			return b1 && b2;
		}
	}
	
	@Override
	public void sendPacketSync(OutPacket packet){
		if(packet == null) {
			throw new IllegalArgumentException("packet is null or TargetMac is wrong");
		}
		long targetMac = packet.getTargetMac();
		IOHandler handler = mHandlerPersist.get(targetMac);
		try {
			if (handler != null) {	//复用已有长连接发送数据
				handler.send(packet);
			} else {	//尝试复用已有短连接发送数据，如果没有则新建
				
				//寻找短连接
				IOHandler oldHandler = mHandlerTemp.get(targetMac);
				
				//如果没有找到，或者找到了但发送失败，则新建Socket任务来发送
				if (oldHandler == null || !oldHandler.send(packet)) {
					
					startNewTCPTask(targetMac, packet.getTargetAddr(), 
//						packet.getFlag() == OutPacket.TAG_TCP_PERSIST)	
							false)		//默认短连接
							.send(packet);	//发送数据
				}
			} 
		} catch (SkySocketUnstartedException e) {
			//TODO 重连机制
			Log.e(this.getClass().getSimpleName(), "Tcp send 失败，需要重连！！！");
		} catch (IOException e) {
			
		}
	}
	
	
	/**
	 * 设置监听器 TCP Receiver(NIO Selector)
	 * 
	 * @param socketChannelMap 所有要监听的channel集合
	 */
//	public void prepareReceiver(Map<String, SocketChannel> socketChannelMap) {
//
//	    //开启一个TCP NIO socket连接
//	    mSocketReceiveConnect = new TCPConnectSelector(socketChannelMap);
//        mSocketReceiveConnect.setSocketCallback(mSocketCallback);
//        //注册channels到selector
//        mSocketReceiveConnect.start();
//	}
	
	/**
	 * 上层通知新发现设备的接口
	 * 
	 * @param devMacs	发现的设备Mac列表（可靠的）
	 */
/*	protected void notifyDeviceDiscover(List<String> devMacs) {
		for (String mac : devMacs) {
			//查找已有Handler，如果有则直接发送查询请求
			IOHandler persist = mHandlerPersist.get(mac);
			IOHandler temp = mHandlerTemp.get(mac);
			if (persist!= null || temp!= null) {
				//PacketHelper.getDevCheckPacket(sn, targetAddr);

			}
		}
	}*/
	
	/**
	 *	设置mac对应socket为长连接
	 *
	 *	@param 	mac
	 *	@return	true--成功设置 false--mac不存在TCP连接
	 */
	public boolean setTCPPersist(long mac, boolean isPersist){
		IOHandler persist = mHandlerPersist.get(mac);
		IOHandler temp = mHandlerTemp.get(mac);
		if (persist != null) {
			if (persist.isRunning()) {
				if (persist instanceof TCPConnectTask) {
					((TCPConnectTask)persist).setTCPPersist(isPersist);
					if (!isPersist) {	//长连接转短连接
						//从长连接Map删除，添加到短连接Map
						mHandlerTemp.put(mac, mHandlerPersist.remove(mac));
					}
					return true;
				}
			} else {
				mHandlerPersist.remove(mac);
			}
			
		} else if(temp != null){
			
			if (temp.isRunning()) {
				if (temp instanceof TCPConnectTask) {
					((TCPConnectTask)temp).setTCPPersist(isPersist);
					if (isPersist) {	//短连接转长连接
						//从短连接Map删除，添加到长连接Map
						mHandlerPersist.put(mac, mHandlerTemp.remove(mac));
					}
					return true;
				}
			} else {
				mHandlerTemp.remove(mac);
			}
			
		}
		return false;
	}
	
	

	@Override
	public boolean isWorking() {
		
		return mThreadPool.getActiveCount() > 0;
	}
	
	@Override
	public void finallize(){
		super.finallize();
	
		stopAllTCPTask();
		
		mHandlerPersist = null;
		mHandlerTemp = null;

		mThreadPool.shutdownNow();
		mThreadPool = null;
		
		mSocketCallback = null;
		setNetCallback(null);
		
		Log.e(this.getClass().getSimpleName(), "Destroy!");
	}

	
	/**
	 *	============================================
	 * 
	 * 
	 * 连接成功回调
	 * 
	 * @param h
	 */
	public void onConnectSuccess(IOHandler h){
		if (mNetCallback != null){
			if (h instanceof TCPConnector) {
				mNetCallback.onConnectTCPSuccess(((TCPConnector) h).getMac());
			}
		}
	}

	/**
	 *	连接错误回调（TCP三次握手错误）
	 *
	 *	@param h
	 *	@param errType
	 *	@param errMsg
	 */
	public void onConnectError(IOHandler h, ErrorConst errType, String errMsg) {
		if (mNetCallback != null){
			if (h instanceof TCPConnector) {
				if (!h.isRunning()) {
					mNetCallback.onConnectTCPError(((TCPConnector) h).getMac(), errType, errMsg);
				}
			}
		}
	}
	
	@Override
	public void onReceive(InPacket packet) {
		if (mNetCallback != null){
			mNetCallback.onReceiveTCP(packet);
		}
	}

	@Override
	public void onReceiveError(IOHandler h, ErrorConst errType, String errMsg) {
		if (mNetCallback != null){
			if (h instanceof TCPConnector) {
//				if (errType == ErrorConst.ESOCK_BIO_CLOSE_BY_REMOTE) {
//					mNetCallback.onReceiveTCPError(((TCPConnector) h).getMac(), errType, errMsg);
//				} else {
				mNetCallback.onReceiveTCPError(((TCPConnector) h).getMac(), errType, errMsg);
			}
		}
	}

	@Override
	public void onCloseFinished(IOHandler h) {
		if (mNetCallback != null){
			if (h instanceof TCPConnector) {
				mNetCallback.onCloseTCP(((TCPConnector) h).getMac());
			}
		}
	}

	@Override
	public void onCloseError(IOHandler h, ErrorConst errType, String errMsg) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * TCP连接管理任务
	 * 
	 * @author wangyf
	 *
	 */
	private class TCPConnectTask extends TCPConnector{

		/** 标识该连接是否为TCP长连接*/
		private AtomicBoolean isPersist;

		/** socket发送包队列 **/
		private Queue<OutPacket> sendQueue;
	
		/** socket 重connect最大重试次数 **/
		private AtomicInteger maxReconnectTimes;

		
		/** Task对应的Future */
		private Future<Object> mFuture;
		
		public boolean isTCPPersist() {
			return isPersist.get();
		}
		public void setTCPPersist(boolean persist) {
			isPersist.set(persist);
		}
		public int getMaxReconnectTimes() {
			return maxReconnectTimes.get();
		}
//		public void setMaxReconnectTimes(int maxReconnectTimes) {
//			this.maxReconnectTimes.set(maxReconnectTimes);
//		}
		public Future<Object> getFuture() {
			return mFuture;
		}
		public void setFuture(Future<Object> mFuture) {
			this.mFuture = mFuture;
		}
		
		public TCPConnectTask(InetSocketAddress targetAddress, ISocketCallback socketCallback, long mac) {
			super(targetAddress, socketCallback, mac);
			isPersist = new AtomicBoolean(false);
			sendQueue = new ConcurrentLinkedQueue<OutPacket>();
			maxReconnectTimes = new AtomicInteger(SocketConst.BIO_TCP_MAX_RECONNECT_TIMES); 
		}
		
		@Override
		public boolean send(OutPacket packet) throws IOException {
			if (!isStarted()) {
				sendQueue.offer(packet);
				return true;
			} 

			return super.send(packet);
		}
		
		@Override
		public boolean dispose() {
			//将Task从Map中删除
			mHandlerTemp.remove(getMac());
			mHandlerPersist.remove(getMac());
			return super.dispose();
		}
		
		@Override
		public Object call() throws Exception {
			Log.e(this.getClass().getSimpleName(), "TCP Task start! task_info:" + this);
			
			int reConnectTimes = 0;
			
			int recvSucessTimes = 0;
			
			do {  
				mIsRunning = true;
				try {
					if(isStarted()){
						
						//先发送 	TODO wait-notify机制替换
						if (sendQueue.peek() != null) {
							super.send(sendQueue.poll());
						}
						
						//再接收
						receive(0);  //IO READ	
						reConnectTimes = 0;	//如果执行到这里无异常，则证明重连成功
						
						//特殊业务逻辑，如果发送Command，则等待设备上报后再断开（第二次接收）
						//	，而且此时非长连接，就断开连接，关闭线程
						if(recvSucessTimes++ == 1 && !isTCPPersist()){	//TODO 判断是否是上报包，防止收到别人的非上报包
							isExit.set(true);
							break;
						}
						
					} else {
						if(!start()){ // 尝试连接，如果失败，则计次
							if(++reConnectTimes >= getMaxReconnectTimes()){
								// 如果超过重连次数，则关闭线程
								isExit.set(true); 
								onConnectError(this, ErrorConst.ESOCK_IO_UNKNOWN, "未知异常，socket开启失败");
							} 
							Log.e(this.getClass().getSimpleName(), "reConnectTimes = "+reConnectTimes);
						}
					}
				} catch (SkySocketUnstartedException e) {
					//连接未开启，继续尝试重连
				} catch (SkySocketCloseByRemoteException e) {	//对方关闭连接
					// 如果是TCP长连接，或者短连接有尚未发送的数据，则重连
//					if (isTCPPersist() || sendQueue.peek() != null) {              
					if(++reConnectTimes >= getMaxReconnectTimes()){
						isExit.set(true); 
						mSocketCallback.onStartError(this, e);
					}
					Log.e(this.getClass().getSimpleName(), "reConnectTimes = "+reConnectTimes);
//					} else {	
//						isExit.set(true); //如果连接已经断开，则退出循环
//					}
				} catch (SocketTimeoutException e) {	//Connect超时，start失败
					if(++reConnectTimes >= getMaxReconnectTimes()){
						isExit.set(true); 
						mSocketCallback.onStartError(this, e);
					}
					Log.e(this.getClass().getSimpleName(), "reConnectTimes = "+reConnectTimes);
				} catch (InterruptedIOException e) {	//Read超时
					// 继续读
				} finally{
					// 如果退出任务线程，则释放资源
					if (isExit.get()) {
						dispose();
						break;
					}		
				}
			} while (!isExit.get());
			
			Log.e(this.getClass().getSimpleName(), "TCP Task stop! task_info:" + this);
			return null;
		}

	}


}
