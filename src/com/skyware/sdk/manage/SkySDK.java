package com.skyware.sdk.manage;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.skyware.sdk.consts.SDKConst;
import com.skyware.sdk.entity.CmdInfo;
import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.entity.ErrorInfo;
import com.skyware.sdk.packet.entity.LierdaPacketEntity.DevStatus;
import com.skyware.sdk.util.ConvertUtil;

public class SkySDK {
	
	private static Callback mCallback;
	

	/**
	 *	开启SDK，初始化
	 *
	 *	@param context		Application
	 *	@param callback		回调API
	 */
	public static void startSDK(Context context, Callback callback) {
		mCallback = callback;
		
		BizManager.getInstace().setUIHandler(new SDKHandler());
        BizManager.getInstace().init(context);
	}
	
	/**
	 *	关闭SDK，释放资源
	 */
	public static void stopSDK() {
		mCallback = null;
		
		BizManager.getInstace().finallize();
	}
	
	/**
	 *	开始发现设备的任务
	 */
	public static void startDiscoverDevice() {
		BizManager.getInstace().startDiscoveryDevice();
	}
		
	/**
	 *	停止发现设备的任务
	 */
	public static void stopDiscoverDevice() {
		BizManager.getInstace().stopDiscoveryDevice();
	}
		
	/**
	 *	连接设备
	 *
	 *	@param mac
	 */
	public static void startConnectDevice(String mac) {
		long macLong = -1;
		if ((macLong = ConvertUtil.macString2Long(mac)) == -1) {
			throw new IllegalArgumentException("Mac string is illegal!");
		}
		
		BizManager.getInstace().startConnectToDevice(macLong);
	}
	
	/**
	 *	与设备断开连接
	 *
	 *	@param mac
	 */
	public static void stopConnectDevice(String mac) {
		long macLong = -1;
		if ((macLong = ConvertUtil.macString2Long(mac)) == -1) {
			throw new IllegalArgumentException("Mac string is illegal!");
		}
		
		BizManager.getInstace().stopConnectToDevice(macLong);
	}
	
	/**
	 *	向设备发送指令
	 *
	 *	@param mac		
	 *	@param cmd		指令列表，如 power::0 的字符串格式 
	 */
	public static void sendCmdToDevice(String mac, CmdInfo cmd, int sn) {	//TODO CMD_INFO or Json
		long macLong = -1;
		if ((macLong = ConvertUtil.macString2Long(mac)) == -1) {
			throw new IllegalArgumentException("Mac string is illegal!");
		}
		
		BizManager.getInstace().sendCmdToDevice(macLong, cmd, sn);
	}
	
	
	public static abstract class Callback {
		
		@Deprecated
		public void onBroadcastReceive(String data){}
		
		/**
		 *	发现新设备
		 *
		 *	@param info		设备信息类
		 */
		public abstract void onDiscoverNewDevice(DeviceInfo info);

		/**
		 *	设备离开网段
		 *
		 *	@param info		设备信息类
		 */
		public abstract void onDeviceDismiss(String mac);
		
		/**
		 *	与设备建立长连接成功
		 *
		 *	@param mac			目标设备mac
		 *	@param success		连接建立是否成功
		 *	@param errorInfo	如果失败的报错信息，成功则为null
		 */
		public abstract void onConnectDeviceResult(String mac, boolean success, ErrorInfo errorInfo);
		
		/**
		 *	与设备断开连接
		 *
		 *	@param mac			目标设备mac
		 *	@param errorInfo	连接断开的报错信息
		 */
		public abstract void onDeviceDisconnected(String mac, ErrorInfo errorInfo);
		
		/**
		 *	发送指令的结果
		 *
		 *	@param mac		目标设备mac
		 *	@param sn		指令包的sn
		 *	@param success	指令是否发送是否成功
		 *	@param errorInfo	如果失败的报错信息，成功则为null
		 */
		public abstract void onSendDevCmdResult(String mac, int sn, boolean success, ErrorInfo errorInfo);
		
		/**
		 *	收到设备状态上报
		 *
		 *	@param json		json格式的报文
		 */
		public abstract void onRecvDevStatus(String json);
		
		@Deprecated
		public void onRecvDevStatusDebug(DevStatus status){}
	}
	
	
	
	private static class SDKHandler extends Handler{
    	@SuppressWarnings("unchecked")
		@Override
    	public void handleMessage(Message msg) {
    		String data = null;
    		String mac = null;
    		DeviceInfo devInfo = null;
    		ErrorInfo errorInfo = null;
    		boolean success = false;
    		int sn = -1;
    		Map<String, Object> map = null;
    		
			switch (msg.what) {
			case SDKConst.MSG_BROADCAST_RECEIVE:	//广播回复
				data = (String)msg.obj;
				if (mCallback != null) {
					mCallback.onBroadcastReceive(data);
				}
				break;
			case SDKConst.MSG_DISCOVER_FIND_NEW:	//发现新设备
				//TODO 增加到ListView中
				devInfo = (DeviceInfo)msg.obj;
				if (mCallback != null) {
					mCallback.onDiscoverNewDevice(devInfo);
				}
				break;
				
			case SDKConst.MSG_DEVICE_CONNECT:		//设备连接结果
//				String mac = (String)msg.obj;
				
				if(msg.obj instanceof HashMap<?, ?>){
					map = (HashMap<String, Object>)msg.obj;
					mac = ConvertUtil.macLong2String((Long)map.get("KEY_MAC"));
					errorInfo = (ErrorInfo) map.get("KEY_ERR");
				} else if (msg.obj instanceof Long) {
					mac = ConvertUtil.macLong2String((Long)msg.obj);
				}
				
				success = msg.arg1 == 1? true : false;
				if (mCallback != null) {
					if (success) {
						mCallback.onConnectDeviceResult(mac, success, null);
					} else {
						mCallback.onConnectDeviceResult(mac, success, errorInfo);
					}
				}
				break;
				
			case SDKConst.MSG_DEVICE_DISCONN:		//设备断线
				map = (HashMap<String, Object>)msg.obj;
				mac = ConvertUtil.macLong2String((Long)map.get("KEY_MAC"));
				errorInfo = (ErrorInfo) map.get("KEY_ERR");
				
				if (mCallback != null) {
					mCallback.onDeviceDisconnected(mac, errorInfo);
				}
				break;
				
			case SDKConst.MSG_DEVICE_CMD_ACK:		//发送命令回复
				sn = msg.arg1;
				success = msg.arg2 == 1? true : false;
				
				if(msg.obj instanceof HashMap<?, ?>){
					map = (HashMap<String, Object>)msg.obj;
					mac = ConvertUtil.macLong2String((Long)map.get("KEY_MAC"));
					errorInfo = (ErrorInfo) map.get("KEY_ERR");
				} else if (msg.obj instanceof Long) {
					mac = ConvertUtil.macLong2String((Long)msg.obj);
				}

				if (mCallback != null) {
					mCallback.onSendDevCmdResult(mac, sn, success, errorInfo);
				}
				break;
				
			case SDKConst.MSG_DEVICE_STATUS:		//设备状态上报
				String statJson = (String)msg.obj;
				if (mCallback != null) {
					mCallback.onRecvDevStatus(statJson);
				}
				break;
				
			case SDKConst.MSG_DEVICE_STATUS_DEBUG:		//设备状态上报（测试用）
				DevStatus status = (DevStatus)msg.obj;
				if (mCallback != null) {
					mCallback.onRecvDevStatusDebug(status);
				}
				break;
				
			default:
				break;
			}
    	}
	
	}
	
}
