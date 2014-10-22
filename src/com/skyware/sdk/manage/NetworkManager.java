package com.skyware.sdk.manage;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

import com.skyware.sdk.callback.IBizCallback;
import com.skyware.sdk.callback.INetCallback;
import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.consts.SDKConst;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.entity.CmdInfo;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.packet.entity.PacketEntity;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;
import com.skyware.sdk.packet.entity.PacketEntity.PacketType;
import com.skyware.sdk.util.PacketHelper;

public class NetworkManager {

//	//饿汉式单例
//	private static NetworkManager mInstance = new NetworkManager();
//	public static NetworkManager getInstace() {
//		return mInstance;
//	}
	
	public NetworkManager(IBizCallback mBizCallback) {
		Log.e(this.getClass().getSimpleName(), "Construct!");
		mSn = new AtomicInteger(0);
		//mBizCallback = BizManager.getInstace().getBizCallback();
		
		this.mBizCallback = mBizCallback;
		mNetCallback = new MyNetCallback();
		
		init();
	}

	
	/**	业务层回调 */
	IBizCallback mBizCallback;
	
	/** 网络层接口（本层）*/
	INetCallback mNetCallback;
	
	/** TCP和UDP通信对象 */
	TCPCommunication mTcpComm;
	UDPCommunication mUdpComm;
	

	/**	管理自增的SN，作为packet唯一标识 */
    private AtomicInteger mSn;
    
    public int getSn()
    {
        if (mSn.get() >= SocketConst.SN_MAX)
        {
        	synchronized (this) {
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
	
	/** 设备地址映射表 Mac-IpAddr */
	private ConcurrentHashMap<Long, InetAddress> mDeviceAddrMap;
	
	/** 设备协议（厂家）类型映射表 Mac-ProtocolType */
	private ConcurrentHashMap<Long, Integer> mDeviceProtocolMap;
	
	/**
	 *	初始化资源
	 */
	private void init(){
		mSendPacketMap = new ConcurrentHashMap<Integer, OutPacket>();
		mDeviceAddrMap = new ConcurrentHashMap<Long, InetAddress>();
		mDeviceProtocolMap = new ConcurrentHashMap<Long, Integer>();
		
		mTcpComm = new TCPCommunication(mNetCallback);
		mUdpComm = new UDPCommunication(mNetCallback);
	}
	
	/**
	 *	释放资源
	 */
	public void finallize(){
		
		mSendPacketMap = null;
		mDeviceAddrMap = null;
		
		mUdpComm.finallize();
		mTcpComm.finallize();
		mTcpComm = null;
		mUdpComm = null;
	}
	
	/**
	 *	发送和接收广播
	 *
	 */
	public boolean startBroadcaster() {
		return mUdpComm.startReceiverThread() &&
				mUdpComm.startBroadcasterThread(6000);
	}
	
	/**
	 * 
	 *	停止发送和接受广播
	 */
	public void stopBroadcaster() {
		mUdpComm.stopReceiverThread();
		mUdpComm.stopBroadcasterThread();
	}
	
	
	/**
	 *	开启新连接
	 *
	 *	@param deviceMac
	 *	@param isPersist
	 *	@return false -- 尚未发现该设备，或者建立TCP连接线程失败
	 */
	public boolean startNewConnect(long deviceMac, boolean isPersist) {
		InetAddress targetIp = mDeviceAddrMap.get(deviceMac);
		int targetProtocol = mDeviceProtocolMap.get(deviceMac);
		if (targetIp == null || targetProtocol == SDKConst.PROTOCOL_UNKNOWN) {
			return false;
		}
		InetSocketAddress targetAddr = getSocketAddr(targetIp, targetProtocol);
		
		switch (targetProtocol) {
		case SDKConst.PROTOCOL_LIERDA:
			return mTcpComm.startNewTCPTask(deviceMac, targetAddr, isPersist) != null;
		case SDKConst.PROTOCOL_BROADLINK:
			return mUdpComm.startNewUdpKeepAlive(deviceMac, targetAddr);
		}
		return false;
	}
	
	/**
	 *	断开已有连接
	 *
	 *	@param deviceMac
	 *	@return
	 */
	public boolean stopConnect(long deviceMac) {
		int targetProtocol = mDeviceProtocolMap.get(deviceMac);
		switch (targetProtocol) {
		case SDKConst.PROTOCOL_LIERDA:
			return mTcpComm.stopTCPTask(deviceMac);
		case SDKConst.PROTOCOL_BROADLINK:
			return mUdpComm.stopUdpKeepAlive(deviceMac);
		}
		return false;
	}

	/**
	 *	向设备发送报文（TCP）
	 *
	 *	@param deviceMac	目标设备Mac
	 *	@param packet		发送包
	 *	@param isPersist	是否保持长连接
	 *	@return false -- 不存在该设备（设备已经不在局域网内）
	 */
	public boolean sendPacketToDevice(long deviceMac, PacketType type, CmdInfo cmd, int sn, boolean isPersist) {
//		packet.setSn(getSn());	在Biz中生成sn
		InetAddress targetIp = mDeviceAddrMap.get(deviceMac);
		int targetProtocol = mDeviceProtocolMap.get(deviceMac);
		if (targetIp == null || targetProtocol == SDKConst.PROTOCOL_UNKNOWN) {
			return false;
		}
		OutPacket packet = null;
		switch (type) {
		case DEVCOMMAND:
			packet = PacketHelper.getDevCmdPacket(sn, cmd.wrapDevData(targetProtocol), targetProtocol);
			break;
			//TODO 其他Packet
		default:
			break;
		}
		
		if (packet != null) {
			packet.setTargetAddr(getSocketAddr(targetIp, targetProtocol));
			packet.setSendTime(System.currentTimeMillis());
			packet.setTargetMac(deviceMac);
			if (isPersist) {
				packet.setFlag(OutPacket.TAG_TCP_PERSIST);
			}	
			
			// 加入到发送缓存
			addOutPacket(packet);
			
			switch (targetProtocol) {
			case SDKConst.PROTOCOL_LIERDA:
				// TCP连接 异步发送
				mTcpComm.sendPacketAsync(packet);
				break;
			case SDKConst.PROTOCOL_BROADLINK:
				// UDP 异步发送
				mUdpComm.sendPacketAsync(packet);
				break;
			}
		}
		
		return true;
	}
	
	
	/**
	 *	短连接切换长连接
	 */
	public boolean switchTCPPesist(long deviceMac) {
		return mTcpComm.setTCPPersist(deviceMac, true);
	}
	
	/**
	 *	长连接切换短连接
	 */
	public boolean switchTCPShort(long deviceMac) {
		return mTcpComm.setTCPPersist(deviceMac, false);
	}
	
	
	/**
	 *	根据协议进行端口指定
	 */
	private InetSocketAddress getSocketAddr(InetAddress targetIp, int targetProtocol) {
		InetSocketAddress targetAddr = null;
		switch (targetProtocol) {
		case SDKConst.PROTOCOL_LIERDA:
			targetAddr = new InetSocketAddress(targetIp, 
					SocketConst.PORT_TCP_REMOTE[SDKConst.PROTOCOL_LIERDA]);
		break;
		case SDKConst.PROTOCOL_BROADLINK:
			targetAddr = new InetSocketAddress(targetIp, 
					SocketConst.PORT_UDP_REMOTE[SDKConst.PROTOCOL_BROADLINK]);
		break;
		}
		return targetAddr;
	}
	
	/**
	 *	根据地址端口确定协议
	 */
	private int getProtocolWithRemoteUDPAddr(InetSocketAddress remoteAddr) {
		int remotePort = remoteAddr.getPort();
		
		for (int protocol = 0; protocol < SDKConst.PROTOCOL_COUNT; protocol++) 
			if (remotePort == SocketConst.PORT_UDP_REMOTE[protocol]) 
				return protocol;
			
		return SDKConst.PROTOCOL_UNKNOWN;
	}
	
	/**
	 *	根据地址端口确定协议
	 */
	private int getProtocolWithRemoteTCPAddr(InetSocketAddress remoteAddr) {
		int remotePort = remoteAddr.getPort();
		
		for (int protocol = 0; protocol < SDKConst.PROTOCOL_COUNT; protocol++) 
			if (remotePort == SocketConst.PORT_TCP_REMOTE[protocol]) 
				return protocol;
			
		return SDKConst.PROTOCOL_UNKNOWN;
	}
	
	
	private class MyNetCallback implements INetCallback{

		@Override
		public void onConnectCloudError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onConnectTCPSuccess(long mac) {
			if (mBizCallback != null) {
				mBizCallback.onConnectDeviceSuccess(mac);
			}
		}
		
		@Override
		public void onConnectTCPError(long mac, ErrorConst errType, String errMsg) {
			if (mBizCallback != null) {
				mBizCallback.onConnectDeviceError(mac, errType, errMsg);
			}
		}

		
		@Override
		public void onReceiveUDP(InPacket packet) {
			if (mBizCallback != null) {
	
				int protocol = getProtocolWithRemoteUDPAddr(packet.getSourceAddr());
//				if (protocol != SDKConst.PROTOCOL_UNKNOWN) {
//					packet.setProtocolType(protocol);
//				}
				PacketType type = PacketHelper.resolvePacketType(packet, protocol);
				
				switch (type) {
				case DEVFIND_ACK:
					PacketEntity.DevFind.Ack ack = PacketHelper.resolveDevFindAck(packet, protocol);
					if (mDeviceAddrMap.containsKey(ack.getMac()) && mDeviceAddrMap.get(ack.getMac()) != null) {
//						String oldIp = mDeviceAddrMap.get(ack.getMac()).toString().substring(1);
						InetAddress oldIp = mDeviceAddrMap.get(ack.getMac());
						//如果IP变更，则更新Map
//						if ( !oldIp.equals(ack.getIp()) ) {
						if ( !oldIp.equals(packet.getSourceAddr()) ) {
							mDeviceAddrMap.remove(ack.getMac());
							mDeviceAddrMap.put(ack.getMac(), packet.getSourceAddr().getAddress());
						}
						
					} else {	//发现新设备
						mDeviceAddrMap.put(ack.getMac(), packet.getSourceAddr().getAddress());
						mDeviceProtocolMap.put(ack.getMac(), protocol);
						
						mBizCallback.onDiscoverNewDevice(ack.getMac(), packet.getSourceAddr().getAddress().toString().substring(1));
					}
					break;
				case DEVCOMMAND_ACK:
					
					break;
				case DEVSTATUS:
					
					break;
				default:
					
					break;
				}
				
				mBizCallback.onRecvUDPPacket(packet);
			}
		}

		@Override
		public void onReceiveTCP(InPacket packet) {
			if (mBizCallback != null) {
				int protocol = getProtocolWithRemoteTCPAddr(packet.getSourceAddr());
				//解析TCP的sn和Type
				int sn = PacketHelper.resolvePacketSn(packet, protocol);
				if (sn >= 0) {
					packet.setSn(sn);
				}
				
				PacketType type= PacketHelper.resolvePacketType(packet, protocol);
				if (type != null) {
					packet.setType(type);
				
					switch(type){
					case HEARTBEAT_ACK:
						//TODO 心跳回复处理
						
						break;
					case DEVCOMMAND_ACK:
						
						OutPacket oldPacket = findAndDelSourcePacket(packet.getSn());
						if (oldPacket != null) {
							mBizCallback.onSendTCPPacketSuccess(oldPacket);
						}
						break;
					case DEVSTATUS:
						
						DevStatus devStatus = PacketHelper.resolveDevStatPacket(packet, protocol);
						mBizCallback.onRecvDevStatus(devStatus);
						//TODO 发送ACK
						
						break;
					default:
						break;
					}
				}
				
				mBizCallback.onRecvTCPPacket(packet);	//for debug
			}
		}
		
		@Override
		public void onReceiveTCPError(long mac, ErrorConst errType,
				String errMsg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReceiveUDPError(long mac, ErrorConst errType,
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
			if (mBizCallback != null) {
				mBizCallback.onSendTCPPacketError(errType, errMsg, packet);
			}
		}

		@Override
		public void onCloseTCP(long mac) {
			if (mBizCallback != null) {
				mBizCallback.onDeviceDisconnected(mac, ErrorConst.ESOCK_BIO_CLOSE_BY_SELF, "Socket close by remote!");
			} 
		}
		
	}
	
}
