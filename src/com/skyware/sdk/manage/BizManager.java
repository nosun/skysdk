package com.skyware.sdk.manage;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;

import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.api.SkySDK;
import com.skyware.sdk.callback.IBizCallback;
import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.entity.CmdInfo;
import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.entity.ErrorInfo;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;
import com.skyware.sdk.packet.entity.PacketEntity.PacketType;
import com.skyware.sdk.push.MqttService;
import com.skyware.sdk.thread.ThreadPoolManager;
import com.skyware.sdk.util.NetworkHelper;

public class BizManager {

	//饿汉式单例
	private static BizManager mInstance = new BizManager();
	public static BizManager getInstace() {
		return mInstance;
	}
	
	private BizManager() {}

	/** UI线程的Handler，用来向UI层传递数据 */
	private Handler mUIHandler;
	
	public void setUIHandler(Handler handler){
		this.mUIHandler = handler;
	}
	
	/** Application */
	private WeakReference<Context> mContext;
	
	public Context getContext() {
		if (mContext != null) {
			return mContext.get();
		}
		return null;
	}
	
	private IBizCallback mBizCallback;
	
	/** Key --> DeviceInfo 映射表 */
	private Map<String, DeviceInfo> mDeviceMap;
	
	/** 网络管理类，维护TCP/UDP通信 */
	private NetworkManager mNetworkManager;
	
	public NetworkManager getNetworkManager() {
		return mNetworkManager;
	}
	
	/** 自动切网前的wifi信息保存 */
	private PrevNetworkInfo mPrevNetworkInfo;
	
	/**
	 *	初始化
	 *
	 *	@param context	application
	 */
	public void init(Context context) {

		mBizCallback = new MyBizCallback();
		
		mContext = new WeakReference<Context>(context);
		mDeviceMap = new ConcurrentHashMap<String, DeviceInfo>();
		
		mNetworkManager = new NetworkManager(mBizCallback);
		mPrevNetworkInfo = new PrevNetworkInfo();
		if (SkySDK.getConfig() != null && !SkySDK.getConfig().isApMode()) {
			initMqtt(context);
		}
	}

	private void initMqtt(Context context){
		//获取设备唯一编码 deviceId
		String deviceID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);   
		Editor editor = context.getSharedPreferences(SDKConst.SP_NAME_MQTT, Context.MODE_PRIVATE).edit();
    	editor.putString(MqttService.PREF_DEVICE_ID, deviceID);
    	editor.commit();
    	//开启Mqtt Service
		MqttService.actionStart(mContext.get());
	}
	/**
	 *	释放所有对象
	 *
	 *	@param context	application
	 */
	public void finallize() {
		//清空线程池
		ThreadPoolManager.getInstance().finallize();
		if (SkySDK.getConfig() != null && !SkySDK.getConfig().isApMode()) {
			//关闭Mqtt Service
			MqttService.actionStop(mContext.get());
		}
		mDeviceMap = null;
		mPrevNetworkInfo = null;
		
		if (mNetworkManager != null) {
			mNetworkManager.finallize();
			mNetworkManager = null;
		}
//		mContext = null;
		mUIHandler = null;
	}

	/**
	 *	清空资源
	 */
	public void clear() {
		if (mNetworkManager != null) {
			mNetworkManager.clear();
		}
		
		ThreadPoolManager.getInstance().shutdownAll();
	}
	
	/**
	 *	开始发现设备的任务
	 */
	public void startDiscoveryDevice() {
		Log.e(this.getClass().getSimpleName(), "startDiscoveryDevice()!");
		if (NetworkHelper.isWifiConnected(getContext())) {
			mNetworkManager.startBroadcaster();
		}
	}
	
	/**
	 *	停止发现设备的任务
	 */
	public void stopDiscoveryDevice() {
		Log.e(this.getClass().getSimpleName(), "stopDiscoveryDevice()!");
		if (NetworkHelper.isWifiConnected(getContext())) {
			mNetworkManager.stopBroadcaster();
		}
	}

	
	/**
	 *	连接设备
	 */
	public void startConnectToDevice(String key) {
		Log.e(this.getClass().getSimpleName(), "startConnectToDevice()! key: " + key);
		if (!NetworkHelper.isWifiConnected(getContext())) {
			// 没有连接Wifi
			mBizCallback.onConnectDeviceError(key, ErrorConst.EWIFI_NOTCONNECT, "扫描不到指定Wifi");
		} else {
			if(!mNetworkManager.startConnect(key, true)){
				//TODO 连接失败上报
			}
		}
//			//发送查询指令
//			OutPacket packet = PacketHelper.getDevCheckPacket(mNetworkManager.getSn());
////			Log.e(this.getClass().getSimpleName(), "packet: " + new String(packet.getContent(), Charset.forName("US-ASCII")));
//			if (packet != null) {
//				mNetworkManager.sendPacketToDevice(key, packet, true);
//			}
	}
	
	/**
	 *	断开连接
	 *
	 */
	public boolean stopConnectToDevice(String key) {
		Log.e(this.getClass().getSimpleName(), "stopConnectToDevice()! key: " + key);
		
		if (NetworkHelper.isWifiConnected(getContext())) {
			return mNetworkManager.stopConnect(key);
		} else {
			return false;
		}
	}
	
	/**
	 *	连接Ap  TODO:设置ip，在NetManager中增加ap mode
	 */
	public void startConnectToAp(String ssid, String ip, int port, boolean auto) {
		Log.e(this.getClass().getSimpleName(), "startConnectToAp()! ssid:"+ssid);
		String apKey = SDKConst.AP_KEY;
		if (NetworkHelper.isWifiConnected(getContext())) {
//				&& NetworkHelper.getCurrentSSID(getContext()).contains(ssid))) {	
			//已经连接Wifi且是目标AP
			if(mPrevNetworkInfo != null){
				mPrevNetworkInfo.netType = PrevNetworkInfo.NETWORK_TYPE_SAME_AP;
			}
			if(!mNetworkManager.startConnectAp(apKey, ip, port, true)){	//尝试连接
				//TODO 连接失败上报
			}
		} else {	//Wifi无连接，或者没有开启Wifi
			if (!auto) {
				mBizCallback.onConnectDeviceError(apKey, ErrorConst.EWIFI_NOTCONNECT, "未连接指定Wifi");
				return;
			}
			if(!NetworkHelper.isWifiEnable(getContext())){	//如果wifi未开启 则开启wifi
				NetworkHelper.openWifi(getContext());
				if(mPrevNetworkInfo != null){
					mPrevNetworkInfo.netType = PrevNetworkInfo.NETWORK_TYPE_2G3G;
				}
			} else if (NetworkHelper.isWifiConnected(getContext())) { //连接其他Wifi
				if(mPrevNetworkInfo != null){	//将信息保存下来，以便退出时恢复
					mPrevNetworkInfo.netType = PrevNetworkInfo.NETWORK_TYPE_OTHER_WIFI;
					mPrevNetworkInfo.wifiInfo = NetworkHelper.getCurrentWifiInfo(getContext());
					mPrevNetworkInfo.wifiConfig = NetworkHelper.getCurrentWifiConfig(getContext());
				}
			} else {	//正在连接Wifi中
				if(mPrevNetworkInfo != null){	//将信息保存下来，以便退出时恢复
					mPrevNetworkInfo.netType = PrevNetworkInfo.NETWORK_TYPE_CONNECTING;
				}
				return;
			}
			
			// 连接指定Wifi
			if(!NetworkHelper.isApScanExist(getContext(), ssid)){	//如果扫描不到
				mBizCallback.onConnectDeviceError(apKey, ErrorConst.EWIFI_NOTCONNECT, "扫描不到指定Wifi");
			} else {	// 切换Wifi
				if(!NetworkHelper.connectWifi(getContext(), ssid, null, NetworkHelper.WIFI_CIPHER_NOPASS)){
					mBizCallback.onConnectDeviceError(apKey, ErrorConst.EWIFI_NOTCONNECT, "连接指定Wifi失败");
				} else if(!mNetworkManager.startConnectAp(apKey, ip, port, true)){	//尝试TCP连接Ap
					//TODO 连接失败上报
					
				}
			}
		}
	}
	
	/**
	 *	断开Ap
	 *
	 */
	public boolean stopConnectToAp(String ssid, boolean auto) {
		Log.e(this.getClass().getSimpleName(), "stopConnectToAp()! ssid:" + ssid);
		String apKey = SDKConst.AP_KEY;
		if (!auto) {
			return mNetworkManager.stopConnectAp(apKey);
		}
		if (NetworkHelper.isWifiConnected(getContext())) {
			if(mPrevNetworkInfo != null){
				switch (mPrevNetworkInfo.netType) {
				case PrevNetworkInfo.NETWORK_TYPE_2G3G:
					//断开连接
					boolean b1 = mNetworkManager.stopConnect(apKey);
					//断开wifi
					boolean b2 = NetworkHelper.disconnectWifi(getContext(), ssid);
					//关闭wifi
					boolean b3 = NetworkHelper.closeWifi(getContext());
					return b1 & b2 & b3;
				case PrevNetworkInfo.NETWORK_TYPE_OTHER_WIFI:
					//断开连接
					boolean bb1 = mNetworkManager.stopConnect(apKey);
					//断开wifi
					boolean bb2 = NetworkHelper.disconnectWifi(getContext(), ssid);
					//连接原来的Wifi	TODO 考虑异步问题
					boolean bb3 = NetworkHelper.connectWifi(getContext(), mPrevNetworkInfo.wifiConfig);
					return bb1 && bb2 && bb3;
				case PrevNetworkInfo.NETWORK_TYPE_SAME_AP:
					//断开连接
					return mNetworkManager.stopConnectAp(apKey);
				}
			}
		} else {
			return true;
		}
		return false;
	}
	
	/**
	 *	重连Ap 
	 */
//	public void reConnectToAp(String ssid, boolean auto) {
//		Log.e(this.getClass().getSimpleName(), "startConnectToAp()! ssid:"+ssid);
//		String apKey = SDKConst.AP_KEY;
//		
//		if ((NetworkHelper.isWifiConnected(getContext()) && 
//				NetworkHelper.getCurrentSSID(getContext()).contains(ssid)) || !auto) {
//			//已经连接AP
////			mNetworkManager.stopConnect(apKey);
//
//			mNetworkManager.startConnect(apKey, true);
//
//		} else {	//Wifi无连接，或者没有开启Wifi
//			if(!NetworkHelper.isWifiEnable(getContext())){	//如果wifi未开启 则开启wifi
//				NetworkHelper.openWifi(getContext());
//			}
//			
//			// 连接指定Wifi
//			if(!NetworkHelper.isApScanExist(getContext(), ssid)){	//如果扫描不到
//				mBizCallback.onConnectDeviceError(apKey, ErrorConst.EWIFI_NOTCONNECT, "扫描不到指定Wifi");
//			} else {	// 切换Wifi
//				if(!NetworkHelper.connectWifi(getContext(), ssid, null, NetworkHelper.WIFI_CIPHER_NOPASS)){
//					mBizCallback.onConnectDeviceError(apKey, ErrorConst.EWIFI_NOTCONNECT, "连接指定Wifi失败");
//				} else if(!mNetworkManager.startConnect(apKey, true)){	//尝试TCP连接Ap
//					//TODO 连接失败上报
//					
//				}
//			}
//		}
//	}
	
	
	/**
	 *	向设备发送命令
	 */
	public void sendCmdToDevice(String mac, CmdInfo cmd, int sn) {
		Log.e(this.getClass().getSimpleName(), "sendCmdToDevice()! mac: " + mac + ",cmd: " + cmd + ",sn: " + sn);
		
		if (NetworkHelper.isWifiConnected(getContext())) {
			if(!mNetworkManager.sendPacketToDevice(mac, PacketType.DEVCOMMAND, cmd, sn, true)){
				//TODO 发送失败上报（同步异常）
			}
		} else {
			//TODO 否则走大循环
			mBizCallback.onSendCmdError(mac, sn, ErrorConst.EWIFI_NOTCONNECT, "未连接wifi");
		}
	}
	
	/**
	 *	查询设备状态
	 */
	public void checkDeviceStatus(String key, int sn) {
		Log.e(this.getClass().getSimpleName(), "checkDeviceStatus()! key: " + key + ",sn: " + sn);
		
		if (NetworkHelper.isWifiConnected(getContext())) {
			if(!mNetworkManager.sendPacketToAp(key, PacketType.DEVCHECK, null, sn, true)){
				//TODO 发送失败上报（同步异常）
			}
		} else {
			//TODO 否则走大循环
			mBizCallback.onSendCmdError(key, sn, ErrorConst.EWIFI_NOTCONNECT, "未连接wifi");
		}
	}
	
	/**
	 *	业务层回调
	 *
	 *	@author wangyf 2014-9-22
	 */
	private class MyBizCallback implements IBizCallback{
		@Override
		public void onDiscoverNewDevice(DeviceInfo deviceInfo) {
			if (mUIHandler != null && deviceInfo != null && deviceInfo.getMac() != null) {
//				deviceInfo.setIp(deviceIp);
//				deviceInfo.setKey(devKey);
//				deviceInfo.setProtocol(protocol);
//				deviceInfo.setDevType(devType);
				//TODO 还有其他欲上报的信息
				
				//默认发现立即建立连接
				startConnectToDevice(deviceInfo.getMac());
				
				//TODO 向服务器验证，注册该设备
				
				mDeviceMap.put(deviceInfo.getMac(), deviceInfo);
				
				mUIHandler.obtainMessage(SDKConst.MSG_DISCOVER_FIND_NEW, deviceInfo).sendToTarget();
			}	
		}
		
		@Override
		public void onConnectCloudError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onConnectDeviceSuccess(String devKey) {
			if (mUIHandler != null) {
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CONNECT_RESULT, 1, -1, devKey).sendToTarget();
			}
		}
		@Override
		public void onConnectDeviceError(String devKey, ErrorConst errType, String errMsg) {
			if (mUIHandler != null) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				ErrorInfo errInfo = new ErrorInfo();
				errInfo.setErrType(errType);
				errInfo.setErrStr(errMsg);
				
				map.put("KEY_MAC", devKey);
				map.put("KEY_ERR", errInfo);
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CONNECT_RESULT, 0, -1, map).sendToTarget();
			}
		}
		@Override
		public void onDeviceDisconnected(String devKey, ErrorConst errType, String errMsg) {
			if (mUIHandler != null) {
				//TODO 判断是否要重连
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				ErrorInfo errInfo = new ErrorInfo();
				errInfo.setErrType(errType);
				errInfo.setErrStr(errMsg);
				
				map.put("KEY_MAC", devKey);
				map.put("KEY_ERR", errInfo);
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_DISCONN, map).sendToTarget();
			}
		}
		@Override
		public void onSendCmdSuccess(String devKey, int sn) {
			if (mUIHandler != null) {
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CMD_ACK, sn, 1, devKey).sendToTarget();
			}
		}
		@Override
		public void onSendCmdError(String devKey, int sn, ErrorConst errType, String errMsg) {
			if (mUIHandler != null) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				ErrorInfo errInfo = new ErrorInfo();
				errInfo.setErrType(errType);
				errInfo.setErrStr(errMsg);
				
				map.put("KEY_MAC", devKey);
				map.put("KEY_ERR", errInfo);
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CMD_ACK, sn, 0, map).sendToTarget();
			}
		}
		@Override
		public void onRecvDevStatus(DevStatus devStatus) {
			if (mUIHandler != null && devStatus != null) {
				try {
					mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_STATUS, devStatus).sendToTarget();
					mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_STATUS_JSON, devStatus.jsonEncoder().toString()).sendToTarget();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		@Override
		public void onRecvError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSDKError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}
		@Override	@Deprecated
		public void onRecvUDPPacket(InPacket packet) {
			if (mUIHandler != null) {
				String toShow = "";
				toShow += packet.getSourceAddr();
				toShow += " " + new String(packet.getContent());
				mUIHandler.obtainMessage(SDKConst.MSG_DEBUG_BROADCAST_RECEIVE, toShow).sendToTarget();
			}
		}
		@Override	@Deprecated
		public void onRecvTCPPacket(InPacket packet) {
			if (mUIHandler != null) {
				String toShow = "";
//				toShow += packet.getSourceAddr();
				toShow += "Recv: ";
				toShow += new String(packet.getContent());
				mUIHandler.obtainMessage(SDKConst.MSG_DEBUG_TCP_RECEIVE, toShow).sendToTarget();
			}
		}
		@Override	@Deprecated
		public void onSendTCPPacket(OutPacket packet) {
			if (mUIHandler != null) {
				String toShow = "";
//				toShow += packet.getSourceAddr();
				toShow += "Send: ";
				toShow += new String(packet.getContent());
				mUIHandler.obtainMessage(SDKConst.MSG_DEBUG_TCP_SEND, toShow).sendToTarget();
			}
		}
	}
	

	private class PrevNetworkInfo{
		
		public static final int NETWORK_TYPE_2G3G 		= 0X00;
		public static final int NETWORK_TYPE_OTHER_WIFI 	= 0X01;
		public static final int NETWORK_TYPE_CONNECTING 	= 0X02;
		public static final int NETWORK_TYPE_SAME_AP		= 0X03;
		
		public WifiInfo wifiInfo;
		
		public WifiConfiguration wifiConfig;

		public int netType;
	}
}
