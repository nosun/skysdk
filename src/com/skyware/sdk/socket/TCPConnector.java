package com.skyware.sdk.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import android.util.Log;

import com.skyware.sdk.callback.ISocketCallback;
import com.skyware.sdk.consts.SDKConst;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.exception.SkySocketCloseByRemoteException;
import com.skyware.sdk.exception.SkySocketUnstartedException;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.packet.entity.FrameHelper;
import com.skyware.sdk.packet.entity.ProtocolHelper;

public abstract class TCPConnector extends BIOHandler{

	/** BIO TCP client socket */
	protected Socket mSocket;
	
	/** Socket连接的设备Mac，用于修改HashMap */
	protected long mac;
	
	/** BIO TCP 接收的输入流 */
	protected InputStream inputStream;
	
	/** BIO TCP 发送的输出流 */
	protected OutputStream outputStream;

	/**
	 * 构造函数I：指定address，建立新的socket连接
	 */
	public TCPConnector(InetSocketAddress targetAddress, ISocketCallback socketCallback, long mac) {
		super();
		this.mTargetAddress = targetAddress;
		setSocketCallback(socketCallback);
		this.setMac(mac);
	}
	
	/**
	 * 构造函数II：复用指定的socket
	 */
	public TCPConnector(Socket socket, ISocketCallback socketCallback, long mac) {
		super();
		this.mSocket = socket;
		setSocketCallback(socketCallback);
		this.setMac(mac);
	}
	
	public long getMac() {
		return mac;
	}

	public void setMac(long mac) {
		this.mac = mac;
	}
	
	/**
	 * 获取当前read超时时间
	 * @return int 当前超时时间
	 * @throws SocketException
	 */
	public synchronized int getReadTimeout() throws SocketException {
		return mSocket.getSoTimeout();
	}
	/**
	 * 设置当前read超时时间
	 * @param readTimeout	读超时时间
	 * @throws SocketException
	 */
	public synchronized void setReadTimeout(int readTimeout) throws SocketException {
		mSocket.setSoTimeout(readTimeout);
	}
	
	/**
	 * 创建连接
	 * @throws IOException  如果connect超时则抛出
	 */
	@Override
	public boolean start() throws IOException{
		//如果socketchannel未指定，则创建一个
		try {
			if(!super.start()){
				return false;
			}
			// 建立socket，本地随机端口
			mSocket = new Socket();
			// 设置立即发送，关闭Nagle算法
			mSocket.setTcpNoDelay(true);
			// 设置read超时时间，超时则报出InterruptedIOException异常。0代表永远阻塞
//			mSocket.setSoTimeout(readTimeout);	//NOTICE: 改到read之前设置
			// 三次握手，NOTICE:这里会阻塞，默认超时
			mSocket.connect(mTargetAddress, SocketConst.TIMEOUT_TCP_CONNECT);
			Log.e(this.getClass().getSimpleName(), "TCP connect! Remote ip: " + mSocket.getRemoteSocketAddress()
					+ " ,Local ip: " + mSocket.getLocalSocketAddress());
			// 获取Socket IO流
			outputStream = mSocket.getOutputStream();
			inputStream = mSocket.getInputStream();
			// 设置已开启状态
			mIsStarted = true;
			isExit.set(false);
			//调用连接完成的回调
			mSocketCallback.onStartFinished(this);
			return true;
		} catch (SocketTimeoutException e){ 
			e.printStackTrace();
//			mSocketCallback.onStartError(this, e);	交与上层
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			mSocketCallback.onStartError(this, e);
			dispose();
			return false;
		}
	}
	
	/**
	 * 发送TCP包
	 */
	@Override
	public boolean send(OutPacket packet) throws IOException{
		try {
			if (!isStarted()) {
				throw new SkySocketUnstartedException("TCP socket didn't start yet!");
			}
			mSendMsgSize = packet.getContent().length;
			System.arraycopy(packet.getContent(), 0, mSendBuf, 0, mSendMsgSize);
			int protocol = packet.getProtocolType();
			if (protocol == SDKConst.PROTOCOL_UNKNOWN) {
				return false;
			}
			//成帧
			mSendMsgSize = FrameHelper.frameMsg(mSendBuf, mSendMsgSize, protocol);
			//发送
			outputStream.write(mSendBuf, 0, mSendMsgSize);
			outputStream.flush();//强制输出到socket文件，不驻留内存缓冲区
			
//			Log.e(this.getClass().getSimpleName(), "TCP send data lenth:" + mSendMsgSize);
			Log.e(this.getClass().getSimpleName(), "TCP send! msg content:" + new String(packet.getContent(), charset));

			mSocketCallback.onSendFinished(packet);
			
			return true;
		} catch (SkySocketUnstartedException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			mSocketCallback.onSendError(e, packet);
//			dispose();
			return false;
		}

	}
	
	/**
	 * 接收TCP数据（阻塞，设置timeout）
	 */
	@Override
	public boolean receive(int readTimeout) throws IOException{
		try {
			if (!isStarted()) {
				throw new SkySocketUnstartedException("TCP socket didn't start yet!");
			}
			if (getReadTimeout() != readTimeout) {
				setReadTimeout(readTimeout);
			}
			
			Log.e(this.getClass().getSimpleName(), "start readStream!");
			
			int protocol = ProtocolHelper.getProtocolWithPort(mSocket.getPort());
			//IO读 + 解帧
			mRecvMsgSize = FrameHelper.ReframeMsg(mRecvBuf, inputStream, protocol);
			
//			Log.e(this.getClass().getSimpleName(), "stop readStream!");
			// 判断TCP连接是否已经断开
			if (mRecvMsgSize == 0) {
//				mSocketCallback.onCloseFinished(this);
//				dispose();
				throw new SkySocketCloseByRemoteException("TCP socket closed by remote");
			}
			
			//封装InPacket
			InPacket packet = new InPacket();
			packet.setReceiveTime(System.currentTimeMillis());
			packet.setSourceAddr((InetSocketAddress) mSocket.getRemoteSocketAddress());
			
			byte[] content = new byte[mRecvMsgSize];
			System.arraycopy(mRecvBuf, 0, content, 0, mRecvMsgSize);
			packet.setContent(content);
		
			Log.e(this.getClass().getSimpleName(), "TCP received! msg content :" + new String(content, charset));
			
			//上报收到的packet
			mSocketCallback.onReceive(packet);
			//reset();	
			return true;
		} catch (SkySocketUnstartedException e){
			e.printStackTrace();	
//			mSocketCallback.onReceiveError(this, e);
			throw e;
		} catch (SkySocketCloseByRemoteException e){
			e.printStackTrace();	
//			mSocketCallback.onReceiveError(this, e);
			throw e;
		} catch (InterruptedIOException e){
			e.printStackTrace();	
//			mSocketCallback.onReceiveError(this, e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();		
			mSocketCallback.onReceiveError(this, e);
//			dispose();
			return false;
		}
	}

	
	/**
	 * 回收资源，释放socket
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
	protected void closeSocket() throws IOException{
		//reset();
		if (mSocket != null) {
			Log.e(this.getClass().getSimpleName(), "TCP socket close! remote ip: "+ mSocket.getRemoteSocketAddress());
			mSocket.close();
		}
		mSocket = null;
		inputStream = null;
		outputStream = null;
	}

	/**
	 * 重新设置消息接收时的状态
	 */
//	private void reset() {
//		if (mReceiveBuf != null) {
//			mReceiveBuf.clear();
//		}
//	}

}
