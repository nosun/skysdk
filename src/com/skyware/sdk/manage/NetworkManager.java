package com.skyware.sdk.manage;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
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
import com.skyware.sdk.packet.entity.PacketHelper;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;
import com.skyware.sdk.packet.entity.PacketEntity.PacketType;
import com.skyware.sdk.packet.entity.ProtocolHelper;
import com.skyware.sdk.util.ArrayUtil;

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

	/** android 网络状态变化监听 */
	BroadcastReceiver mNetworkChangeReceiver;
	
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
	
	/** 现有可访问设备的地址映射表 Mac-IpAddr */
	private ConcurrentHashMap<Long, InetAddress> mDeviceAddrMap;
	
	/** 现有可访问设备的协议（厂家）类型映射表 Mac-ProtocolType */
	private ConcurrentHashMap<Long, Integer> mDeviceProtocolMap;
	
	
	private void cleanDeviceMap() {
		mDeviceAddrMap.clear();
		mDeviceProtocolMap.clear();
	}
	
	/**
	 *	初始化资源
	 */
	private void init(){
		mSendPacketMap = new ConcurrentHashMap<Integer, OutPacket>();
		mDeviceAddrMap = new ConcurrentHashMap<Long, InetAddress>();
		mDeviceProtocolMap = new ConcurrentHashMap<Long, Integer>();
		
		mTcpComm = new TCPCommunication(mNetCallback);
		mUdpComm = new UDPCommunication(mNetCallback);
		
		registNetworkReceiver();
		
		if (SkySDK.getConfig().isApMode()) {
			initApInfo();
		}
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
		
		unregistNetworkReceiver();
	}
	
	private void initApInfo() {
		try {
			mDeviceAddrMap.put(SDKConst.AP_MAC_LONG, 
					InetAddress.getByName(SDKConst.PROTOCOL_GREEN_WIFI_AP_IP));
			mDeviceProtocolMap.put(SDKConst.AP_MAC_LONG, SDKConst.PROTOCOL_GREEN);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 *	发送和接收广播
	 */
	public boolean startBroadcaster() {
		
		return mUdpComm.startReceiverThread() &&
				mUdpComm.startBroadcasterThread(6000);
	}
	
	/**
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
		InetSocketAddress targetAddr = ProtocolHelper.getSocketAddr(targetIp, targetProtocol);
		
		if (ArrayUtil.contains(SDKConst.PROTOCOL_TCP_SET, targetProtocol)){
			return mTcpComm.startNewTCPTask(deviceMac, targetAddr, isPersist) != null;
		} else if (ArrayUtil.contains(SDKConst.PROTOCOL_UDP_SET, targetProtocol)) {
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
		
		if (ArrayUtil.contains(SDKConst.PROTOCOL_TCP_SET, targetProtocol)){
			return mTcpComm.stopTCPTask(deviceMac);
		} else if (ArrayUtil.contains(SDKConst.PROTOCOL_UDP_SET, targetProtocol)) {
			return mUdpComm.stopUdpKeepAlive(deviceMac);
		}
		return false;
	}

	/**
	 *	断开所有已有连接
	 *
	 */
	public boolean stopAllConnects() {
		return mTcpComm.stopAllTCPTask();
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
//DEBUG	 	packet = PacketHelper.getDevCmdPacket(sn, cmd.wrapDevData(targetProtocol), deviceMac, targetProtocol);
			packet = PacketHelper.getDevCmdPacket(sn, cmd.getData(), deviceMac, targetProtocol);
			break;
			//TODO 其他Packet
		case DEVCHECK:
			packet = PacketHelper.getDevCheckPacket(sn, deviceMac, targetProtocol);
			break;
		default:
			break;
		}
		
		if (packet != null) {
			packet.setTargetAddr(ProtocolHelper.getSocketAddr(targetIp, targetProtocol));
			packet.setSendTime(System.currentTimeMillis());
			packet.setTargetMac(deviceMac);
			if (isPersist) {
				packet.setFlag(OutPacket.TAG_TCP_PERSIST);
			}	
			
			// 加入到发送缓存
			addOutPacket(packet);
			
			if (ArrayUtil.contains(SDKConst.PROTOCOL_TCP_SET, targetProtocol)){
				// TCP连接 异步发送
				mTcpComm.sendPacketAsync(packet);
			} else if (ArrayUtil.contains(SDKConst.PROTOCOL_UDP_SET, targetProtocol)) {
				// UDP 异步发送
				mUdpComm.sendPacketAsync(packet);
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
	 *	网络层回调
	 *
	 *	@author wangyf 2014-9-22
	 */
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
	
				int protocol = ProtocolHelper.getProtocolWithPort(packet.getSourceAddr().getPort());
//				if (protocol != SDKConst.PROTOCOL_UNKNOWN) {
//					packet.setProtocolType(protocol);
//				}
				//解析UDP的sn和Type
				int sn = PacketHelper.resolvePacketSn(packet, protocol);
				if (sn >= 0) {
					packet.setSn(sn);
				}
				PacketType type = PacketHelper.resolvePacketType(packet, protocol);
				
				switch (type) {
				case DEVFIND_ACK:
					PacketEntity.DevFind.Ack ack = PacketHelper.resolveDevFindAck(packet, protocol);
					if (mDeviceAddrMap!=null && ack!=null) {
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
							
							//TODO 设备类型的判断应该由协议增加一个字段来判断
							mBizCallback.onDiscoverNewDevice(ack.getMac(), packet.getSourceAddr().getAddress().toString().substring(1), 
									protocol, SDKConst.PROTOCOL_DEV_TYPE[protocol]);
						}
					}
					break;
				case DEVCOMMAND_ACK:
					OutPacket oldPacket = findAndDelSourcePacket(packet.getSn());
					if (oldPacket != null) {
						mBizCallback.onSendCmdSuccess(oldPacket.getTargetMac(), oldPacket.getSn());
					
						//博联插座特殊逻辑：命令ack即为状态上报
						DevStatus devStatus1 = PacketHelper.resolveDevStatPacket(packet, protocol);
						mBizCallback.onRecvDevStatus(devStatus1);
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
				
				mBizCallback.onRecvUDPPacket(packet);
			}
		}

		@Override
		public void onReceiveTCP(InPacket packet) {
			if (mBizCallback != null) {
				int protocol = ProtocolHelper.getProtocolWithPort(packet.getSourceAddr().getPort());
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
							mBizCallback.onSendCmdSuccess(oldPacket.getTargetMac(), oldPacket.getSn());
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
		public void onSendTCPFinished(OutPacket packet) {
			mBizCallback.onSendTCPPacket(packet);	//for debug
		}

		@Override
		public void onSendUDPFinished(OutPacket packet) {
//			mBizCallback.onSendTCPPacket(packet);	//for debug
		}
		
		@Override
		public void onSendError(OutPacket packet, ErrorConst errType,
				String errMsg) {
			if (mBizCallback != null) {
				mBizCallback.onSendCmdError(packet.getTargetMac(), packet.getSn(), errType, errMsg);
			}
		}

		@Override
		public void onCloseTCP(long mac) {
			if (mBizCallback != null) {
				mBizCallback.onDeviceDisconnected(mac, ErrorConst.ESOCK_BIO_CLOSE_BY_SELF, "Socket close by remote!");
			} 
		}
		
	}
	
	
	private void registNetworkReceiver(){
		 IntentFilter filter = new IntentFilter();
		 filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//		 filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//		 filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		 Context context = BizManager.getInstace().getContext();
		 if (context != null) {
			 mNetworkChangeReceiver = new NetworkConnectChangedReceiver();
			 context.registerReceiver(mNetworkChangeReceiver, filter);
		 }
	}

	private void unregistNetworkReceiver(){
		 Context context = BizManager.getInstace().getContext();
		 if (context != null) {
			 context.unregisterReceiver(mNetworkChangeReceiver);
		 }
	}
	
	public class NetworkConnectChangedReceiver extends BroadcastReceiver {  
	    @Override  
	    public void onReceive(Context context, Intent intent) {  
/*	        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关  
	           
	        	int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);  
	            
	            switch (wifiState) {  
	            case WifiManager.WIFI_STATE_DISABLED:  
	            	Log.e("WYF", "wifiState: " + "WIFI_STATE_DISABLED");  
	                break;  
	            case WifiManager.WIFI_STATE_DISABLING:  
	            	Log.e("WYF", "wifiState: " + "WIFI_STATE_DISABLING");  
	            	break;
	            case WifiManager.WIFI_STATE_ENABLED:
	            	Log.e("WYF", "wifiState: " + "WIFI_STATE_ENABLED");  
	                break;  
	            case WifiManager.WIFI_STATE_ENABLING:
	            	Log.e("WYF", "wifiState: " + "WIFI_STATE_ENABLING");  
	                break;  
	            //  
	            }  
	        } */ 
	
	        // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。  
	        // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线  
	        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {  
	            Parcelable parcelableExtra = intent  
	                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);  
	            if (null != parcelableExtra) {  
	                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;  
	                State state = networkInfo.getState();  
//	                DetailedState state2 = networkInfo.getDetailedState();  
//	                Log.e("WYF", "connect state: " + state);   
	                switch (state){
	                case CONNECTED:
	                	if (SkySDK.getConfig().isApMode() == false) {
	                		startBroadcaster();
						}
	                	break;
	                case DISCONNECTED:
	                	if (SkySDK.getConfig().isApMode() == false) {
	                		stopBroadcaster();
						}
	                	stopAllConnects();
	                	cleanDeviceMap();
	                	break;
					default:
						break;
	                }

	            }  
	        }  
	        
	    }
	}

}
