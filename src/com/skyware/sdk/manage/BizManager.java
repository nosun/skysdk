package com.skyware.sdk.manage;

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.skyware.sdk.callback.IBizCallback;
import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.consts.SDKConst;
import com.skyware.sdk.entity.CmdInfo;
import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.entity.DeviceInfo.DevType;
import com.skyware.sdk.entity.ErrorInfo;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;
import com.skyware.sdk.packet.entity.PacketEntity.PacketType;
import com.skyware.sdk.thread.ThreadPoolManager;
import com.skyware.sdk.util.ConvertUtil;
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
		return mContext.get();
	}
	
	private Map<Long, DeviceInfo> mDeviceMap;
	
	private NetworkManager mNetworkManager;
	
	private IBizCallback mBizCallback;
	
	/**
	 *	初始化资源
	 *
	 *	@param context	application
	 */
	public void init(Context context) {
		this.mContext = new WeakReference<Context>(context);
		
		mDeviceMap = new ConcurrentHashMap<Long, DeviceInfo>();
		
		mBizCallback = new MyBizCallback();
		
		mNetworkManager = new NetworkManager(mBizCallback);
	}
	
	/**
	 *	释放资源
	 *
	 *	@param context	application
	 */
	public void finallize() {
		mUIHandler = null;
		
		mDeviceMap = null;
		mNetworkManager.finallize();
		mNetworkManager = null;
		
		ThreadPoolManager.getInstance().finallize();
		mContext = null;
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
	public void startConnectToDevice(long mac) {
		Log.e(this.getClass().getSimpleName(), "startConnectToDevice()! mac: " + mac);
//		try {
		if (NetworkHelper.isWifiConnected(getContext())) {
			if(!mNetworkManager.startNewConnect(mac, true)){
				//TODO 连接失败上报
			}
		} else {
			mBizCallback.onConnectDeviceError(mac, ErrorConst.EWIFI_NOTCONNECT, "未连接wifi");
		}
//			//发送查询指令
//			OutPacket packet = PacketHelper.getDevCheckPacket(mNetworkManager.getSn());
////			Log.e(this.getClass().getSimpleName(), "packet: " + new String(packet.getContent(), Charset.forName("US-ASCII")));
//			if (packet != null) {
//				mNetworkManager.sendPacketToDevice(mac, packet, true);
//			}
	}
	
	/**
	 *	断开连接
	 *
	 */
	public boolean stopConnectToDevice(long mac) {
		Log.e(this.getClass().getSimpleName(), "stopConnectToDevice()! mac: " + mac);
		
		if (NetworkHelper.isWifiConnected(getContext())) {
			return mNetworkManager.stopConnect(mac);
		} else {
			return false;
		}
	}
	
	
	
	/**
	 *	向设备发送命令
	 */
	public void sendCmdToDevice(long mac, CmdInfo cmd, int sn) {
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
	public void checkDeviceStatus(long mac, int sn) {
		Log.e(this.getClass().getSimpleName(), "checkDeviceStatus()! mac: " + mac + ",sn: " + sn);
		
		if (NetworkHelper.isWifiConnected(getContext())) {
			if(!mNetworkManager.sendPacketToDevice(mac, PacketType.DEVCHECK, null, sn, true)){
				//TODO 发送失败上报（同步异常）
			}
		} else {
			//TODO 否则走大循环
			mBizCallback.onSendCmdError(mac, sn, ErrorConst.EWIFI_NOTCONNECT, "未连接wifi");
		}
	}
	
	/**
	 *	业务层回调
	 *
	 *	@author wangyf 2014-9-22
	 */
	private class MyBizCallback implements IBizCallback{
		@Override
		public void onDiscoverNewDevice(long devMac, String deviceIp, int protocol, DevType devType) {
			if (mUIHandler != null) {
				DeviceInfo deviceInfo = new DeviceInfo();
				deviceInfo.setIp(deviceIp);
				deviceInfo.setMac(ConvertUtil.macLong2String(devMac));
				deviceInfo.setProtocol(protocol);
				deviceInfo.setDevType(devType);
				//TODO 还有其他欲上报的信息
				
				//TODO 向服务器验证，注册该设备
				
				mDeviceMap.put(devMac, deviceInfo);
				
				mUIHandler.obtainMessage(SDKConst.MSG_DISCOVER_FIND_NEW, deviceInfo).sendToTarget();
			}	
		}
		
		@Override
		public void onConnectCloudError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onConnectDeviceSuccess(long devMac) {
			if (mUIHandler != null) {
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CONNECT_RESULT, 1, -1, devMac).sendToTarget();
			}
		}
		@Override
		public void onConnectDeviceError(long devMac, ErrorConst errType, String errMsg) {
			if (mUIHandler != null) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				ErrorInfo errInfo = new ErrorInfo();
				errInfo.setErrType(errType);
				errInfo.setErrStr(errMsg);
				
				map.put("KEY_MAC", devMac);
				map.put("KEY_ERR", errInfo);
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CONNECT_RESULT, 0, -1, map).sendToTarget();
			}
		}
		@Override
		public void onDeviceDisconnected(long devMac, ErrorConst errType, String errMsg) {
			if (mUIHandler != null) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				ErrorInfo errInfo = new ErrorInfo();
				errInfo.setErrType(errType);
				errInfo.setErrStr(errMsg);
				
				map.put("KEY_MAC", devMac);
				map.put("KEY_ERR", errInfo);
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_DISCONN, map).sendToTarget();
			}
		}
		@Override
		public void onSendCmdSuccess(long devMac, int sn) {
			if (mUIHandler != null) {
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CMD_ACK, sn, 1, devMac).sendToTarget();
			}
		}
		@Override
		public void onSendCmdError(long devMac, int sn, ErrorConst errType, String errMsg) {
			if (mUIHandler != null) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				ErrorInfo errInfo = new ErrorInfo();
				errInfo.setErrType(errType);
				errInfo.setErrStr(errMsg);
				
				map.put("KEY_MAC", devMac);
				map.put("KEY_ERR", errInfo);
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CMD_ACK, sn, 0, map).sendToTarget();
			}
		}
		@Override
		public void onRecvDevStatus(DevStatus devStatus) {
			if (mUIHandler != null) {
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
				toShow += " " + new String(packet.getContent(), Charset.forName("US-ASCII"));
				mUIHandler.obtainMessage(SDKConst.MSG_DEBUG_BROADCAST_RECEIVE, toShow).sendToTarget();
			}
		}
		@Override	@Deprecated
		public void onRecvTCPPacket(InPacket packet) {
			if (mUIHandler != null) {
				String toShow = "";
//				toShow += packet.getSourceAddr();
				toShow += "Recv: ";
				toShow += new String(packet.getContent(), Charset.forName("US-ASCII"));
				mUIHandler.obtainMessage(SDKConst.MSG_DEBUG_TCP_RECEIVE, toShow).sendToTarget();
			}
		}
		@Override	@Deprecated
		public void onSendTCPPacket(OutPacket packet) {
			if (mUIHandler != null) {
				String toShow = "";
//				toShow += packet.getSourceAddr();
				toShow += "Send: ";
				toShow += new String(packet.getContent(), Charset.forName("US-ASCII"));
				mUIHandler.obtainMessage(SDKConst.MSG_DEBUG_TCP_SEND, toShow).sendToTarget();
			}
		}
	}
	
}
