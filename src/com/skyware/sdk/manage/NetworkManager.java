package com.skyware.sdk.manage;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.api.SkySDK;
import com.skyware.sdk.callback.IBizCallback;
import com.skyware.sdk.callback.INetCallback;
import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.entity.CmdInfo;
import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.entity.DeviceInfo.LocalNetStat;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.packet.entity.PacketEntity;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;
import com.skyware.sdk.packet.entity.PacketEntity.PacketType;
import com.skyware.sdk.packet.entity.PacketHelper;
import com.skyware.sdk.packet.entity.ProtocolHelper;
import com.skyware.sdk.socket.IOHandler;
import com.skyware.sdk.util.ArrayUtil;

public class NetworkManager {

	//饿汉式单例
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
	
	/**	业务层回调（上层） */
	IBizCallback mBizCallback;
	
	/** 网络层接口（本层）*/
	INetCallback mNetCallback;

	public INetCallback getNetCallback() {
		return mNetCallback;
	}
	/** TCP和UDP通信对象 */
	TCPCommunication mTcpComm;
	UDPCommunication mUdpComm;
	

	/**	管理自增的SN，作为packet唯一标识 */
    private AtomicInteger mSn;
    
    public int getSn() {
        if (mSn.get() >= SocketConst.SN_MAX) {
        	synchronized (this) {
        		mSn.set(mSn.get() % SocketConst.SN_MAX);
			}
        }
        return mSn.incrementAndGet();
    }

    /* ---------------- 缓存 --------------- */
	/**	发送包缓存 Sn - Packet */
	private ConcurrentHashMap<Integer, OutPacket> mSendPacketMap;
	
	/**	接收设备状态推送缓存 Sn - DevStatus */
	private ConcurrentHashMap<Integer, InPacket> mRecvStatusMap;
	
	/** 现有内网中有效的设备表 Key - DeviceInfo */
	private ConcurrentHashMap<String, DeviceInfo> mLocalDevMap;
	
	/** 现有MQTT订阅的设备表 Key - DeviceInfo */
	private ConcurrentHashMap<String, DeviceInfo> mMqttSubDevMap;
	
	public void addOutPacket(OutPacket packet) {
		if (packet != null && mSendPacketMap != null) {
			mSendPacketMap.put(packet.getSn(), packet);
		}
	}
	public OutPacket findAndDelSourcePacket(int sn) {
		OutPacket packet = null;
		if (mSendPacketMap != null) {
			packet = mSendPacketMap.remove(sn);
		}
		return packet;
	}
	public OutPacket findSourcePacket(int sn) {
		OutPacket packet = null;
		if (mSendPacketMap != null) {
			packet = mSendPacketMap.get(sn);
		}
		return packet;
	}

	/**
	 *	增加状态包缓存
	 *
	 *	@param sn
	 *	@return false -- sn已经存在  true -- sn不存在
	 */
	public boolean addStatusPacket(int sn, InPacket packet) {
		boolean isFound = false;
		if (mRecvStatusMap != null) {
			InPacket oldpacket = mRecvStatusMap.get(sn);
			if (oldpacket != null) {	//got
				long recvTime = oldpacket.getReceiveTime();
				//判断是否超时 防止sn溢出冲突
				if(recvTime != 0 && System.currentTimeMillis() - recvTime < SDKConst.TIMEOUT_PUSH_STATUS){
					isFound = true;
				}
			}
			if (!isFound) {
				mRecvStatusMap.put(sn, packet);
				return true;
			} 
		}
		return false;
	}
	
	
	/**
	 *	初始化
	 */
	private void init(){
		mSendPacketMap = new ConcurrentHashMap<Integer, OutPacket>();
		mLocalDevMap = new ConcurrentHashMap<String, DeviceInfo>();
		mMqttSubDevMap = new ConcurrentHashMap<String, DeviceInfo>();
		mRecvStatusMap = new ConcurrentHashMap<Integer, InPacket>();
		
		mTcpComm = new TCPCommunication(mNetCallback);
		mUdpComm = new UDPCommunication(mNetCallback);
		
		registNetworkReceiver();
	}
	
	/**
	 *	清空资源
	 */
	public void clear(){
		
		//TODO 判断是否需要清空
		mLocalDevMap.clear();
		mSendPacketMap.clear();
		mRecvStatusMap.clear();
		mMqttSubDevMap.clear();
		
		mUdpComm.finallize();
		mTcpComm.finallize();
	}
	
	/**
	 *	释放对象
	 */
	public void finallize(){
		
		clear();
		
		mSendPacketMap = null;
		mLocalDevMap = null;
		mRecvStatusMap = null;
		mMqttSubDevMap = null;
		
		mTcpComm = null;
		mUdpComm = null;
		
		unregistNetworkReceiver();
	}
	
//	private void initApInfo() {
//		try {
//			mDeviceAddrMap.put(SDKConst.AP_MAC_LONG, 
//					InetAddress.getByName(SkySDK.getConfig().getApIp()));
//			mDeviceProtocolMap.put(SDKConst.AP_MAC_LONG, SDKConst.PROTOCOL_GREEN);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}		
//	}
	
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
	 *	开启新连接	通过devKey连接
	 *
	 *	@param deviceKey
	 *	@param isPersist
	 *	@return false -- 尚未发现该设备，或者建立TCP连接线程失败
	 */
	public boolean startConnect(String deviceKey, boolean isPersist) {
		DeviceInfo devInfo = mLocalDevMap.get(deviceKey);
		if (devInfo == null || devInfo.getIp() == null ||
				devInfo.getIp().equals("") || devInfo.getProtocol() == SDKConst.PROTOCOL_UNKNOWN) {
			return false;
		}
		InetSocketAddress targetAddr = ProtocolHelper.getSocketAddr(devInfo.getIp(), devInfo.getProtocol());
		
		if (ProtocolHelper.isUseTcp(devInfo.getProtocol())) {
			return startConnect(targetAddr, deviceKey, true, isPersist);
		} else if (ProtocolHelper.isUseUdp(devInfo.getProtocol())) {
			return startConnect(targetAddr, deviceKey, false, isPersist);
		}
		return false;
	}

	// Ap 连接， 直接绕过Local检测
	public boolean startConnectAp(String key, String ip, int port, boolean isPersist) {
		InetSocketAddress targetAddr = new InetSocketAddress(ip, port);
		return startConnect(targetAddr, key, true, isPersist);
	}

	public boolean stopConnectAp(String key) {
		return mTcpComm.stopTCPTask(key);
	}     
	
	@Deprecated
	public boolean sendPacketToAp(String key, PacketType type, CmdInfo cmd, int sn, boolean isPersist) {
	
		OutPacket packet = null;
		switch (type) {
		case DEVCOMMAND:
			packet = PacketHelper.getDevCmdPacket(sn, cmd.getData(), key, SDKConst.PROTOCOL_GREEN, SDKConst.PRODUCT_GREEN_BLACK2);
			break;
			//TODO 其他Packet
		case DEVCHECK:
			packet = PacketHelper.getDevCheckPacket(sn, SDKConst.PROTOCOL_GREEN);
			break;
		default:
			break;
		}
		
		InetSocketAddress targetAddr = new InetSocketAddress("192.168.1.1", 502);
		
		if (packet != null) {
			packet.setTargetAddr(targetAddr);
			packet.setSendTime(System.currentTimeMillis());
			packet.setTargetKey(key);
			if (isPersist) {
				packet.setFlag(OutPacket.TAG_TCP_PERSIST);
			}	
			
			// 加入到发送缓存
//			addOutPacket(packet);
			
			mTcpComm.sendPacketAsync(packet);
			
			return true;
		}
		return false;
	}
	
	/**
	 *	开启新连接 主动通过ip建立连接
	 *
	 *	@param targetAddr	目标设备addr
	 *	@param devKey		目标设备key
	 *	@param tcpOrUdp		true -- TCP  false -- UDP
	 *	@param isPersist
	 *	@return false -- 建立TCP连接线程失败
	 */ 
	public boolean startConnect(InetSocketAddress targetAddr, String devKey, 
										boolean tcpOrUdp, boolean isPersist) {
		if (tcpOrUdp) {
			return mTcpComm.startTCPTask(devKey, targetAddr, isPersist) != null;
		} else {
			return mUdpComm.startNewUdpTask(devKey, targetAddr, isPersist);
		}
	}
	
	/**
	 *	断开已有连接
	 *
	 *	@param deviceKey
	 *	@return
	 */
	public boolean stopConnect(String deviceKey) {
		if (mLocalDevMap == null) {
			return false;
		}
		DeviceInfo devInfo = mLocalDevMap.get(deviceKey);
		if (devInfo == null || devInfo.getIp() == null ||
				devInfo.getIp().equals("") || devInfo.getProtocol() == SDKConst.PROTOCOL_UNKNOWN) {
			return false;
		}
		
		if (ArrayUtil.contains(SDKConst.PROTOCOL_TCP_SET, devInfo.getProtocol())){
			return mTcpComm.stopTCPTask(deviceKey);
		} else if (ArrayUtil.contains(SDKConst.PROTOCOL_UDP_SET, devInfo.getProtocol())) {
			return mUdpComm.stopUdpTask(deviceKey);
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
	 *	@param deviceKey	目标设备Key
	 *	@param packet		发送包
	 *	@param isPersist	是否保持长连接
	 *	@return false -- 不存在该设备（设备已经不在局域网内）
	 */
	public boolean sendPacketToDevice(String deviceKey, PacketType type, CmdInfo cmd, int sn, boolean isPersist) {
//		packet.setSn(getSn());	在Biz中生成sn
		if (mLocalDevMap == null) {
			return false;
		}
		DeviceInfo devInfo = mLocalDevMap.get(deviceKey); 
		if (devInfo == null || devInfo.getIp() == null ||
				devInfo.getIp().equals("") || devInfo.getProtocol() == SDKConst.PROTOCOL_UNKNOWN) {
			return false;
		}
		
		OutPacket packet = null;
		switch (type) {
		case DEVLOGIN:
			packet = PacketHelper.getDevLoginPacket(sn, devInfo.getProtocol());
			break;
		case DEVCHECK:
			packet = PacketHelper.getDevCheckPacket(sn, devInfo.getProtocol());
			break;
		case DEVCOMMAND:
//DEBUG	 	packet = PacketHelper.getDevCmdPacket(sn, cmd.wrapDevData(targetProtocol), deviceKey, targetProtocol);
			packet = PacketHelper.getDevCmdPacket(sn, cmd.getData(), deviceKey, devInfo.getProtocol(), devInfo.getProductType());
			break;
			//TODO 其他Packet
		default:
			break;
		}
		
		if (packet != null) {
			packet.setTargetAddr(ProtocolHelper.getSocketAddr(devInfo.getIp(), devInfo.getProtocol()));
			packet.setSendTime(System.currentTimeMillis());
			packet.setTargetKey(deviceKey);
			if (isPersist) {
				packet.setFlag(OutPacket.TAG_TCP_PERSIST);
			}	
			
			// 加入到发送缓存
			addOutPacket(packet);
			
			if (ArrayUtil.contains(SDKConst.PROTOCOL_TCP_SET, devInfo.getProtocol())){
				// TCP连接 异步发送
				mTcpComm.sendPacketAsync(packet);
			} else if (ArrayUtil.contains(SDKConst.PROTOCOL_UDP_SET, devInfo.getProtocol())) {
				// UDP 异步发送
				mUdpComm.sendPacketAsync(packet);
			}
			
			return true;
		}
		return false;
	}
	
	
	/**
	 *	短连接切换长连接
	 */
	public boolean switchTCPPesist(String deviceKey) {
		return mTcpComm.setTCPPersist(deviceKey, true);
	}
	
	/**
	 *	长连接切换短连接
	 */
	public boolean switchTCPShort(String deviceKey) {
		return mTcpComm.setTCPPersist(deviceKey, false);
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
		public void onConnectMqttError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onConnectTCPSuccess(IOHandler h, String key) {
			if (mBizCallback != null && mLocalDevMap != null) {
				// 改变设备的网络状态为内网连接
				DeviceInfo devInfo = mLocalDevMap.get(key);
				if (devInfo != null) {
					devInfo.setLocalNetStat(LocalNetStat.CONNECTED);
//					mBizCallback.on
				}
				mBizCallback.onConnectDeviceSuccess(key);
			}
		}
		
		@Override
		public void onConnectTCPError(IOHandler h, String key, ErrorConst errType, String errMsg) {
			if (mBizCallback != null) {
				// TODO 重连判断, RECONN or BUSY
				mBizCallback.onConnectDeviceError(key, errType, errMsg);
			}
		}
		
		@Override
		public void onReceiveUDP(IOHandler h, InPacket packet) {
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
					if (mLocalDevMap!=null && ack!=null) {
						String devKey = ack.key;
						if (devKey != null) {
							if(mLocalDevMap.containsKey(devKey) && mLocalDevMap.get(devKey) != null 
									&& !mLocalDevMap.get(devKey).equals("")) {
								// 现有设备ip变化
//								String oldIp = mDeviceAddrMap.get(ack.getKey()).toString().substring(1);
								DeviceInfo devInfo = mLocalDevMap.get(ack.key);
								String oldIp = devInfo.getIp();
								String newIp = packet.getSourceAddr().getAddress().getHostAddress();
								// 更新ip
								if (oldIp != null && newIp != null && !oldIp.equals(newIp) ) {
									devInfo.setIp(newIp);
								}
							} else {	//发现新设备
//								mDeviceAddrMap.put(ack.getKey(), packet.getSourceAddr().getAddress());
//								mDeviceProtocolMap.put(ack.getKey(), protocol);
								DeviceInfo newDevInfo = new DeviceInfo();
								newDevInfo.setMac(ack.key);
								newDevInfo.setIp(packet.getSourceAddr().getAddress().getHostAddress());
								newDevInfo.setProtocol(protocol);
								//TODO 获取设备类型，比如净化器、插座之类的
//								newDevInfo.setDevType(ProtocolHelper.getDevType(protocol));
								//TODO 获取产品类型，此处用默认设置
								if (SkySDK.getConfig()!=null) {
									newDevInfo.setProductType(SkySDK.getConfig().getProductType());
								}
								
								mLocalDevMap.put(devKey, newDevInfo);
								//TODO 设备类型的判断应该由协议增加一个字段来判断
								mBizCallback.onDiscoverNewDevice(newDevInfo);
							}
						} 
					}
					break;
				case DEVCOMMAND_ACK:
					OutPacket oldPacket = findAndDelSourcePacket(packet.getSn());
					if (oldPacket != null) {
						mBizCallback.onSendCmdSuccess(oldPacket.getTargetKey(), oldPacket.getSn());
					
						//博联插座特殊逻辑：命令ack即为状态上报
						if (protocol == SDKConst.PROTOCOL_BROADLINK) {
							String key = PacketHelper.resolveDeviceKey(packet, protocol);
							int productType = SDKConst.PRODUCT_UNKNOWN;
							if(mLocalDevMap !=null && mLocalDevMap.get(key)!=null){
								productType = mLocalDevMap.get(key).getProductType();
								// TODO 如果本地Map中无此设备，还是否需要上报？
							}
							DevStatus devStatus = PacketHelper.resolveDevStatPacket(packet, protocol, productType);
							mBizCallback.onRecvDevStatus(devStatus);
						}
					}
					break;
				case DEVSTATUS:
					String key = PacketHelper.resolveDeviceKey(packet, protocol);
					int productType = SDKConst.PRODUCT_UNKNOWN;
					if(mLocalDevMap != null && mLocalDevMap.get(key)!=null){
						productType = mLocalDevMap.get(key).getProductType();
						// TODO 如果本地Map中无此设备，还是否需要上报？
					}
					DevStatus devStatus = PacketHelper.resolveDevStatPacket(packet, protocol, productType);
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
		public void onReceiveTCP(IOHandler h, InPacket packet) {
			if (mBizCallback != null) {
				int protocol = ProtocolHelper.getProtocolWithPort(packet.getSourceAddr().getPort());
				//解析TCP的sn和Type
				int sn = PacketHelper.resolvePacketSn(packet, protocol);
				if (sn > 0) {
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
							mBizCallback.onSendCmdSuccess(oldPacket.getTargetKey(), oldPacket.getSn());
						}
						break;
					case DEVSTATUS:
						// 如果已经推送过，则不上报  如果包中无sn字段，直接上报
						if(packet.getSn() <= 0 || addStatusPacket(packet.getSn(), packet)){
							String key = PacketHelper.resolveDeviceKey(packet, protocol);
							int productType = SDKConst.PRODUCT_UNKNOWN;
							if(mLocalDevMap != null && mLocalDevMap.get(key)!=null){
								productType = mLocalDevMap.get(key).getProductType();
								// TODO 如果本地Map中无此设备，还是否需要上报？
							}
							DevStatus devStatus = PacketHelper.resolveDevStatPacket(packet, protocol, productType);
							mBizCallback.onRecvDevStatus(devStatus);
							try {
								mBizCallback.onRecvDevStatus(devStatus.jsonEncoder().toString());
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
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
		public void onReceivePushMqtt(String topicName, byte[] payload, int qos, boolean retained) {
			InPacket packet = new InPacket();
			packet.setContent(payload);
			packet.setReceiveTime(System.currentTimeMillis());
			
			//TODO 根据什么来解析具体的协议？
			int protocol = SDKConst.PROTOCOL_LIERDA;
			
			//解析TCP的sn和Type
			int sn = PacketHelper.resolvePacketSn(packet, protocol);
			if (sn >= 0) {
				packet.setSn(sn);
			}

			PacketType type= PacketHelper.resolvePacketType(packet, protocol);
			if (type != null && mBizCallback != null) {
				packet.setType(type);
			
				switch(type){
				case HEARTBEAT_ACK:
					//TODO 心跳回复处理
					break;
				case DEVCOMMAND_ACK:
					OutPacket oldPacket = findAndDelSourcePacket(packet.getSn());
					if (oldPacket != null) {
						mBizCallback.onSendCmdSuccess(oldPacket.getTargetKey(), oldPacket.getSn());
					}
					break;
				case NETSTATUS:
					//TODO 上报到指定接口
					mBizCallback.onRecvDevStatus(new String(packet.getContent()));
					break;
				case DEVSTATUS:
					// 如果已经推送过，则不上报
					if(addStatusPacket(packet.getSn(), packet)){
						String key = PacketHelper.resolveDeviceKey(packet, protocol);
						int productType = SDKConst.PRODUCT_UNKNOWN;
						if(mMqttSubDevMap != null && mMqttSubDevMap.get(key)!=null){
							productType = mMqttSubDevMap.get(key).getProductType();
							// TODO 如果订阅Map中无此设备，还是否需要上报？
						} else {
							if (SkySDK.getConfig()!=null) {
								productType = SkySDK.getConfig().getProductType();
							}
						}
						DevStatus devStatus = PacketHelper.resolveDevStatPacket(packet, protocol, productType);
						if (devStatus != null) {
							mBizCallback.onRecvDevStatus(devStatus);
							try {
								mBizCallback.onRecvDevStatus(devStatus.jsonEncoder().toString());
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
					//TODO 发送ACK
					
					break;
				default:
					break;
				}
			}
			
		}
		
		@Override
		public void onReceiveTCPError(IOHandler h, String key, ErrorConst errType,
				String errMsg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReceiveUDPError(IOHandler h, String key, ErrorConst errType,
				String errMsg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onSendTCPFinished(IOHandler h, OutPacket packet) {
			mBizCallback.onSendTCPPacket(packet);	//for debug
		}

		@Override
		public void onSendUDPFinished(IOHandler h, OutPacket packet) {
//			mBizCallback.onSendTCPPacket(packet);	//for debug
		}
		
		@Override
		public void onSendError(IOHandler h, OutPacket packet, ErrorConst errType,
				String errMsg) {
			if (mBizCallback != null) {
				mBizCallback.onSendCmdError(packet.getTargetKey(), packet.getSn(), errType, errMsg);
			}
		}

		@Override
		public void onCloseTCP(IOHandler h, String key) {
			if (mBizCallback != null) {
				mBizCallback.onDeviceDisconnected(key, ErrorConst.ESOCK_BIO_CLOSE_BY_SELF, "Socket close by remote!");
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
	                	SkySDK.resumeSDK(context);
	                	break;
	                case DISCONNECTED:
	                	SkySDK.pauseSDK();	//不能stop否则监听被注销
//	                	if (SkySDK.getConfig().isApMode() == false) {
//	                		stopBroadcaster();
//						}
//	                	stopAllConnects();
//	                	cleanDeviceMap();
	                	break;
					default:
						break;
	                }

	            }
	        }  
	        
	    }
	}

}
