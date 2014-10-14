package com.skyware.sdk.manage;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;

import android.util.Log;

import com.skyware.sdk.callback.IBizCallback;
import com.skyware.sdk.callback.INetCallback;
import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.util.NetworkHelper;
import com.skyware.sdk.util.PacketHelper;
import com.skyware.sdk.util.PacketHelper.Broadcast;

public class NetworkManager {

	//饿汉式单例
	private static NetworkManager mInstance = new NetworkManager();
	public static NetworkManager getInstace() {
		return mInstance;
	}
	
	private NetworkManager() {
		Log.e(this.getClass().getSimpleName(), "Construct!");
		mSn = new AtomicInteger(0);
		//mBizCallback = BizManager.getInstace().getBizCallback();
		mNetCallback = new MyNetCallback();
	}
	
	public void setBizCallback(IBizCallback mBizCallback) {
		this.mBizCallback = mBizCallback;
	}
	
	
	/**	业务层回调 */
	IBizCallback mBizCallback;
	
	/** 网络层接口（本层）*/
	INetCallback mNetCallback;
	
	/** TCP和UDP通信对象 */
	TCPCommunication mTcpComm;
	UDPCommunication mUdpComm;
	
	/** 广播地址 */
	InetSocketAddress mBroadcastAddr;
	
	public InetSocketAddress getBroadcastAddr() {
		return mBroadcastAddr;
	}
	public void setBroadcastAddr(InetSocketAddress mBroadcastAddr) {
		this.mBroadcastAddr = mBroadcastAddr;
	}

	/**	管理自增的SN，作为packet唯一标识 */
    private AtomicInteger mSn;
    
    public int getSn()
    {
        if (mSn.get() >= SocketConst.SN_MAX)
        {
        	synchronized (mInstance) {
        		mSn.set(mSn.get() % SocketConst.SN_MAX);
			}
        }
        return mSn.incrementAndGet();
    }

	/**	发送包缓存 SN-Packet */
	private ConcurrentHashMap<Integer, OutPacket> mSendPacketMap;

	public void addOutPacket(OutPacket packet) {
		if (packet == null) {
			return;
		}
		mSendPacketMap.put(packet.getSn(), packet);
	}
	public OutPacket findAndDelSourcePacket(int sn) {
		OutPacket packet = mSendPacketMap.remove(sn);
		return packet;
	}
	public OutPacket findSourcePacket(int sn) {
		OutPacket packet = mSendPacketMap.get(sn);
		return packet;
	}
	
	/** 设备地址映射表 Mac-SocketAddr */
	private ConcurrentHashMap<String, InetSocketAddress> mDeviceAddrMap;
	
	
	/**
	 *	初始化资源
	 */
	public void init(){
		mSendPacketMap = new ConcurrentHashMap<Integer, OutPacket>();
		mDeviceAddrMap = new ConcurrentHashMap<String, InetSocketAddress>();
		
		mBroadcastAddr = NetworkHelper.getBroadcastAddress(BizManager.getInstace().getContext());
		mTcpComm = new TCPCommunication(mNetCallback);
		mUdpComm = new UDPCommunication(mNetCallback, mBroadcastAddr);
	}
	
	/**
	 *	发送和接收广播
	 *
	 */
	public boolean startBroadcaster() {
		return mUdpComm.startReceiverThread() &&
				mUdpComm.startBroadcasterThread(5000);
	}
	
	/**
	 *	停止发送和接受广播
	 */
	public void stopBroadcaster() {
		mUdpComm.stopReceiverThread();
		mUdpComm.stopBroadcasterThread();
	}
	
	
	public boolean startNewConnect(String deviceMac, boolean isPersist) {
		InetSocketAddress targetAddr = mDeviceAddrMap.get(deviceMac);
		if (targetAddr != null) {
			return mTcpComm.startNewTCPTask(deviceMac, targetAddr, isPersist) != null;
		}
		return false;
	}
	
	public boolean stopConnect(String deviceMac) {
		return mTcpComm.stopTCPTask(deviceMac);
	}

	/**
	 *	向设备发送报文（TCP）
	 *
	 *	@param deviceMac	目标设备Mac
	 *	@param packet		发送包
	 *	@param isPersist	是否保持长连接
	 */
	public void sendPacketToDevice(String deviceMac, OutPacket packet, boolean isPersist) {
//		packet.setSn(getSn());	在Biz中生成sn
		packet.setSendTime(System.currentTimeMillis());
		InetSocketAddress targetAddr = mDeviceAddrMap.get(deviceMac);
		
//		Log.e(this.getClass().getSimpleName(), targetAddr.toString());
		
		packet.setTargetMac(deviceMac);
		packet.setTargetAddr(targetAddr);
		if (isPersist) {
			packet.setFlag(OutPacket.TAG_TCP_PERSIST);
		}	
		
		// 加入到发送缓存
		addOutPacket(packet);
		
		mTcpComm.sendPacketAsync(packet);
		
		
//		mTcpComm.sendPacketSync(packet);
//		if (isPersist) {
//			mTcpComm.setTCPPersist(deviceMac, isPersist);
//		}
//		if(isPersist){	//如果是长连接，则直接异步发送
//			mTcpComm.sendPacketAsync(packet);
//		} else {	//如果是短连接，则开启TCP短连接指令任务
//
//			
//		}
	}
	
	
	/**
	 *	短连接切换长连接
	 */
	public boolean switchTCPPesist(String deviceMac) {
		return mTcpComm.setTCPPersist(deviceMac, true);
	}
	
	/**
	 *	长连接切换短连接
	 */
	public boolean switchTCPShort(String deviceMac) {
		return mTcpComm.setTCPPersist(deviceMac, false);
	}
	
	private class MyNetCallback implements INetCallback{

		@Override
		public void onConnectCloudError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onConnectTCPSuccess(String mac) {
			 
		}
		
		@Override
		public void onConnectTCPError(String mac, ErrorConst errType, String errMsg) {
			
		}


		@Override
		public void onReceiveUDP(InPacket packet) {
			
			try {
				Broadcast.ACK ack = PacketHelper.getBroadcastAck(packet);
				
				if (mDeviceAddrMap.containsKey(ack.getMac()) && mDeviceAddrMap.get(ack.getMac()) != null) {
					String oldIp = mDeviceAddrMap.get(ack.getMac()).getAddress().toString().substring(1);
					//如果IP变更，则更新Map
					if ( !oldIp.equals(ack.getIp()) ) {
						mDeviceAddrMap.remove(ack.getMac());
						mDeviceAddrMap.put(ack.getMac(), new InetSocketAddress(ack.getIp(), SocketConst.REMOTE_PORT_TCP_DEFAULT));
					}
					
				} else {	//发现新设备
					mDeviceAddrMap.put(ack.getMac(), new InetSocketAddress(ack.getIp(), SocketConst.REMOTE_PORT_TCP_DEFAULT));
					
					mBizCallback.onDiscoverNewDevice(ack.getMac(), ack.getIp());
				}
				

				mBizCallback.onRecvUDPPacket(packet);
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onReceiveTCP(InPacket packet) {
//TODO		解析包，根据类型来决策
//			findAndDelSourcePacket(packet.getSn());
			
			switch(packet.getType()){
			case TYPE_HEARTBEAT_ACK:
				//TODO 心跳回复处理
				
				break;
			case TYPE_COMMAND_ACK:
				
				OutPacket oldPacket = findAndDelSourcePacket(packet.getSn());
				if (oldPacket != null) {
					mBizCallback.onSendTCPPacketSuccess(oldPacket);
				}
				break;
			case TYPE_DEV_STATUS:
				mBizCallback.onRecvDevStatus(packet);
				//TODO 发送ACK
				
				break;
			default:
				break;
			}
			
			mBizCallback.onRecvTCPPacket(packet);
		}
		
		@Override
		public void onReceiveTCPError(String mac, ErrorConst errType,
				String errMsg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReceiveUDPError(String mac, ErrorConst errType,
				String errMsg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onSendFinished(OutPacket packet) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSendError(OutPacket packet, ErrorConst errType,
				String errMsg) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	
	
}
