package com.skyware.sdk.socket;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.util.Log;

import com.skyware.sdk.callback.ISocketCallback;
import com.skyware.sdk.exception.SkySocketUnstartedException;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;

public abstract class UDPHandler extends BIOHandler{
	
	/** UDP Socket链接 */
	protected DatagramSocket mUDPSocket;
	
	/** 本地绑定的Addr*/
	protected InetSocketAddress mLocalAddress;
	
	/** 发送UDP Packet */
	protected DatagramPacket mUDPSendPacket;

	/** 发送UDP Packet */
	protected DatagramPacket mUDPRecvPacket;

	/**
	 * 构造函数：指定本地监听端口和广播地址：端口，建立新的socket连接
	 */
	public UDPHandler(int bindPort, ISocketCallback callback) {
		super();
		this.mLocalAddress = new InetSocketAddress(bindPort);
		this.mSocketCallback = callback;
	}
	
	/**
	 * 获取当前read超时时间
	 * @return int 当前超时时间
	 * @throws SocketException
	 */
	public synchronized int getReadTimeout() throws SocketException {
		return mUDPSocket.getSoTimeout();
	}
	/**
	 * 设置当前read超时时间
	 * @param readTimeout	读超时时间
	 * @throws SocketException
	 */
	public synchronized void setReadTimeout(int readTimeout) throws SocketException {
		mUDPSocket.setSoTimeout(readTimeout);
	}
	
	
	/**
	 * 启动连接
	 */
	public boolean start() {
		try {
			if(!super.start()){
				return false;
			}
			// 建立UDP socket，绑定本地监听端口
			mUDPSocket = new DatagramSocket(mLocalAddress);
			// 设置read超时时间，超时则报出InterruptedIOException异常。0代表永远阻塞
			//mUDPSocket.setSoTimeout(readTimeout);	//NOTICE: 改到read之前设置	
			// 设置广播许可
			mUDPSocket.setBroadcast(true);
			// 初始化接收DatagramPacket，分配内存
			mUDPRecvPacket = new DatagramPacket(mRecvBuf,mRecvBuf.length);
			// 设置已开启状态
			mIsStarted = true;
			isExit.set(false);
			// 调用连接完成的回调
			mSocketCallback.onStartFinished(this);
			
			Log.e(this.getClass().getSimpleName(), "start!");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			mSocketCallback.onStartError(this, e);
			dispose();
			return false;
		}
	}

	/**
	 * 发送UDP广播
	 */
	@Override
	public boolean send(OutPacket packet) {
		try {
			if (!isStarted()) {
				throw new SkySocketUnstartedException("UDP broadcast socket didn't start yet!");
			}
			mSendMsgSize = packet.getContent().length;
			System.arraycopy(packet.getContent(), 0, mSendBuf, 0, mSendMsgSize);
			
			//包装UDP发送报文
			mUDPSendPacket = new DatagramPacket(mSendBuf, mSendMsgSize, packet.getTargetAddr());
			//发送
			mUDPSocket.send(mUDPSendPacket);
			
//			Log.e(this.getClass().getSimpleName(), "UDP send data lenth:" + mSendMsgSize);
			Log.e(this.getClass().getSimpleName(), "UDP send data content:" + new String(packet.getContent() ,charset));
			
			mSocketCallback.onSendFinished(packet);
			
//			Log.e(this.getClass().getSimpleName(),"UDP already send!");
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			mSocketCallback.onSendError(e, packet);
			return false;
		}

	}

	/**
	 * 接收UDP包（阻塞，可设置timeout）
	 */
	@Override
	public boolean receive(int readTimeout) throws IOException {
		try {
			if (!isStarted()) {
				throw new SkySocketUnstartedException("UDP broadcast socket didn't start yet!");
			}
			if (getReadTimeout() != readTimeout) {
				setReadTimeout(readTimeout);
			}
			
			// UDP接收包
			mUDPSocket.receive(mUDPRecvPacket);
			// 获取包长度
			mRecvMsgSize = mUDPRecvPacket.getLength();
			
			//封装InPacket
			InPacket packet = new InPacket();
			packet.setReceiveTime(System.currentTimeMillis());
			packet.setSourceAddr((InetSocketAddress) mUDPRecvPacket.getSocketAddress());
			
			byte[] content = new byte[mRecvMsgSize];
			System.arraycopy(mUDPRecvPacket.getData(), 0, content, 0, mRecvMsgSize);
			packet.setContent(content);
					
			Log.e(this.getClass().getSimpleName(), "UDP packet received, msg length :" + mRecvMsgSize + "msg content :" + new String(content, charset));
			
			//上报收到的packet
			mSocketCallback.onReceive(packet);
//			reset();		
			return true;
		} catch (SkySocketUnstartedException e){
			mSocketCallback.onReceiveError(this, e);
			throw e;
		} catch (InterruptedIOException e){
			mSocketCallback.onReceiveError(this, e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();		
			mSocketCallback.onReceiveError(this, e);
//			dispose();
			return false;
		}
	}
	
	/**
	 * 循环接收UDP数据（循环调用receive）
	 */
	public void receiveLoop(int readTimeout, int loopTimeout) {
		if(loopTimeout < 0) {
			throw new IllegalArgumentException("loopTimeout < 0");
		}
		
		long start = System.currentTimeMillis();
		
		while (!isExit.get()) {
			mIsRunning = true;
				
			try {
				receive(readTimeout);  //IO READ	
			} catch (SkySocketUnstartedException e) {
				e.printStackTrace();
				// TODO: 可以考虑重建连接
				
				isExit.set(true); //如果socket未开启，则退出循环
			} catch (InterruptedIOException e) {
				e.printStackTrace();
				// TODO: 阻塞读超时，在这里计时，如果次数过多结束线程
			} catch (IOException e) {
				e.printStackTrace();
			} finally {	
				// 检测时间差，如果循环超时，则退出循环
				if(loopTimeout != 0){
					long now = System.currentTimeMillis();
					if (now - start >= loopTimeout) {
						isExit.set(true);
					}
				}
				// 如果退出循环，则释放资源
				if (isExit.get()) {
					dispose();
				}
			}
		}
	}
	
	/**
	 * 回收资源
	 */
	@Override
	public boolean dispose() {
		try {
			if (!super.dispose()) {
				return false;
			}
			closeSocket();
			mSocketCallback.onCloseFinished(this);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			if(mSocketCallback != null)
				mSocketCallback.onCloseError(this, e);
			return false;
		}
	}

	/**
	 * 关闭连接
	 */
	private void closeSocket() {
		//reset();
		if (mUDPSocket != null) {
			mUDPSocket.close();
		}
		mUDPSocket = null;
		mUDPSendPacket = null;
		mUDPRecvPacket = null;
	}
	
	
	/**
	 * 重新设置消息接收时的状态
	 */
//	private void reset() {
//		if (mReceiveBuffer != null) {
//			mReceiveBuffer.clear();
//		}
//	}
}
