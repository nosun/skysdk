package com.skyware.sdk.manage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.skyware.sdk.consts.SDKConst;
import com.skyware.sdk.entity.CmdInfo;
import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.entity.ErrorInfo;
import com.skyware.sdk.entity.SDKConfig;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;
import com.skyware.sdk.thread.ThreadPoolManager;
import com.skyware.sdk.util.ConvertUtil;

public class SkySDK {
	
	private static Callback mCallback;
	private static SDKConfig mConfig;

	public static SDKConfig getConfig() {
		return mConfig;
	}
	/**
	 *	开启SDK，初始化
	 *
	 *	@param context		Application
	 *	@param callback		回调API
	 *	@param config		sdk配置
	 */
	public static void startSDK(Context context, Callback callback, SDKConfig config) {
		mCallback = callback;
		mConfig = config;
		
		BizManager.getInstace().setUIHandler(new SDKHandler());
        BizManager.getInstace().init(context);
        
        if(mConfig != null && mConfig.isApMode()){
        	startConnectAp();
        } 
//      else {
//        	startDiscoverDevice();
//		}
	}
	
	/**
	 *	开启SDK，初始化(向前兼容)
	 *
	 *	@param context		Application
	 *	@param callback		回调API
	 */
	public static void startSDK(Context context, Callback callback) {
		mConfig = new SDKConfig();
		mConfig.setApMode(false);
		startSDK(context, callback, mConfig);
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
	 *	连接AP
	 *
	 *	@param mac
	 */
	public static void startConnectAp() {
		BizManager.getInstace().startConnectToDevice(SDKConst.AP_MAC_LONG);
	}
	
	/**
	 *	与Ap断开连接
	 *
	 *	@param mac
	 */
	public static void stopConnectAp(String mac) {
		BizManager.getInstace().stopConnectToDevice(SDKConst.AP_MAC_LONG);
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
		
		@Deprecated
		public void onTCPReceive(String data){}
		
		@Deprecated
		public void onTCPSend(String data){}
		
		/**
		 *	发现新设备
		 *
		 *	@param info		设备信息类
		 */
		public abstract void onDiscoverNewDevice(DeviceInfo info);

		/**
		 *	设备离开网段
		 *
		 *	@param mac		设备mac
		 */
		public abstract void onDeviceDismiss(String mac);
		
		/**
		 *	与设备建立连接结果
		 *
		 *	@param mac			目标设备mac
		 *	@param success		连接建立是否成功
		 *	@param errorInfo	如果失败的报错信息，成功则为null
		 */
		public abstract void onConnectDeviceResult(String mac, boolean success, ErrorInfo errorInfo);
		
		/**
		 *	与设备连接断开
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
		
		/**
		 *	收到设备状态上报
		 *
		 *	@param status	设备状态上报
		 */
		public void onRecvDevStatus(DevStatus status){}
	}
	
	public static abstract class CallbackAP extends Callback{

		private Future<?> checkTaskFuture;
		
		@Override
		public void onDiscoverNewDevice(DeviceInfo info) {}

		@Override
		public void onDeviceDismiss(String mac) {}

		@Override
		public void onConnectDeviceResult(String mac, boolean success,
				ErrorInfo errorInfo) {
			if (ConvertUtil.macString2Long(mac) == SDKConst.AP_MAC_LONG) {
				if (success) {
					checkTaskFuture = ThreadPoolManager.getInstance().getThreadPool().scheduleWithFixedDelay(new Runnable() {
						
						@Override
						public void run() {
							BizManager.getInstace().checkDeviceStatus(SDKConst.AP_MAC_LONG, 0);
						}
					}, 0, SDKConst.PROTOCOL_GREEN_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
				}
				onConnectApResult(success, errorInfo);
			}
		}

		@Override
		public void onDeviceDisconnected(String mac, ErrorInfo errorInfo) {
			if (ConvertUtil.macString2Long(mac) == SDKConst.AP_MAC_LONG) {
				if (checkTaskFuture != null) {
					checkTaskFuture.cancel(true);
				}
				onApDisconnected(errorInfo);
			}
		}

		@Override
		public void onSendDevCmdResult(String mac, int sn, boolean success,
				ErrorInfo errorInfo) {}
		
		/**
		 *	与AP建立连接的结果
		 *
		 *	@param success		连接建立是否成功
		 *	@param errorInfo	如果失败的报错信息，成功则为null
		 */
		public abstract void onConnectApResult(boolean success, ErrorInfo errorInfo);
		
		/**
		 *	与Ap连接断开
		 *
		 *	@param errorInfo	连接断开的报错信息
		 */
		public abstract void onApDisconnected(ErrorInfo errorInfo);
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
			case SDKConst.MSG_DEBUG_BROADCAST_RECEIVE:	//广播回复
				data = (String)msg.obj;
				if (mCallback != null) {
					mCallback.onBroadcastReceive(data);
				}
				break;
			case SDKConst.MSG_DEBUG_TCP_RECEIVE:	//调试：TCP接收
				data = (String)msg.obj;
				if (mCallback != null) {
					mCallback.onTCPReceive(data);
				}
				break;
			case SDKConst.MSG_DEBUG_TCP_SEND:	//调试：TCP发送
				data = (String)msg.obj;
				if (mCallback != null) {
					mCallback.onTCPSend(data);
				}
				break;
			case SDKConst.MSG_DISCOVER_FIND_NEW:	//发现新设备
				//TODO 增加到ListView中
				devInfo = (DeviceInfo)msg.obj;
				if (mCallback != null) {
					mCallback.onDiscoverNewDevice(devInfo);
				}
				break;
				
			case SDKConst.MSG_DEVICE_CONNECT_RESULT:		//设备连接结果
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
					mCallback.onConnectDeviceResult(mac, success, null);
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
			
			case SDKConst.MSG_DEVICE_STATUS_JSON:		//设备状态上报（Json）
				String statJson = (String)msg.obj;
				if (mCallback != null) {
					mCallback.onRecvDevStatus(statJson);
				}
				break;
				
			case SDKConst.MSG_DEVICE_STATUS:			//设备状态上报
				DevStatus status = (DevStatus)msg.obj;
				if (mCallback != null) {
					mCallback.onRecvDevStatus(status);
				}
				break;
				
			default:
				break;
			}
    	}
	
	}
	
}
