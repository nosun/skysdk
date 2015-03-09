package com.skyware.sdk.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.skyware.sdk.entity.CmdInfo;
import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.entity.ErrorInfo;
import com.skyware.sdk.manage.BizManager;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;
import com.skyware.sdk.thread.ThreadPoolManager;

public class SkySDK {

	private static Callback mCallback;
	private static SDKConfig mConfig;

	public static enum Status {
		STARTED, RUNNING, PAUSING, STOPED
	}

	private static Status mSDKStatus;

	static {
		mConfig = new SDKConfig();
		mSDKStatus = Status.STOPED;
	}

	public static SDKConfig getConfig() {
		return mConfig;
	}

	public static Callback getCallback() {
		return mCallback;
	}

	// public static boolean isSDKStarted() {
	// return isStarted;
	// }
	// public static boolean isSDKPausing() {
	// return isPausing;
	// }

	/**
	 * 开启SDK，初始化
	 * 
	 * @param context
	 *            Application
	 * @param callback
	 *            回调API
	 * @param config
	 *            sdk配置
	 */
	public static void startSDK(Context context, Callback callback,
			SDKConfig config) {
		mCallback = callback;
		mConfig = config;

		if (mSDKStatus == null || mSDKStatus == Status.STOPED) {
			BizManager.getInstace().setUIHandler(new SDKHandler());
			BizManager.getInstace().init(context);

			mSDKStatus = Status.STARTED;
		} else if (mSDKStatus == Status.PAUSING) {

			resumeSDK(context);
		}
	}

	/**
	 * 开启SDK，初始化(向前兼容)
	 * 
	 * @param context
	 *            Application
	 * @param callback
	 *            回调API
	 */
	public static void startSDK(Context context, Callback callback) {
		if (mConfig == null) {
			mConfig = new SDKConfig();
		}
		startSDK(context, callback, mConfig);
	}

	/**
	 * 重启SDK，对应pauseSDK
	 * 
	 * @param context
	 */
	public static void resumeSDK(Context context) {
		if (mCallback == null || mConfig == null) {
			return;
		}

		if (mSDKStatus == null || mSDKStatus == Status.STOPED) {
			startSDK(context, mCallback, mConfig);
		} else if (mSDKStatus == Status.PAUSING || mSDKStatus == Status.STARTED) {
			// TODO 分配资源

			if (mConfig.isApMode()) {
//				String curSSID = NetworkHelper.getCurrentSSID(context);
//				if (curSSID != null && curSSID.contains(mConfig.getApSSID())) {
				startConnectAp();
//				}
			} else {
				startDiscoverDevice();
			}

			mSDKStatus = Status.RUNNING;
		}
	}

	/**
	 * 暂停SDK，只释放部分资源
	 */
	public static void pauseSDK() {
		mSDKStatus = Status.PAUSING;

		if (SkySDK.getConfig().isSaveMode()) {
			BizManager.getInstace().clear();
		}
	}

	/**
	 * 关闭SDK，释放资源
	 */
	public static void stopSDK() {
		mSDKStatus = Status.STOPED;

		BizManager.getInstace().finallize();
	}

	/**
	 * 开始发现设备的任务
	 */
	public static void startDiscoverDevice() {
		BizManager.getInstace().startDiscoveryDevice();
	}

	/**
	 * 停止发现设备的任务
	 */
	public static void stopDiscoverDevice() {
		BizManager.getInstace().stopDiscoveryDevice();
	}

	/**
	 * 连接设备
	 * 
	 * @param key
	 */
	@Deprecated
	public static void startConnectDevice(String key) {
//		if ((macString = ConvertUtil.macString2String(key)) == -1) {
//			throw new IllegalArgumentException("Mac string is illegal!");
//		}

		BizManager.getInstace().startConnectToDevice(key);
	}

	/**
	 * 与设备断开连接
	 * 
	 * @param key
	 */
	@Deprecated
	public static void stopConnectDevice(String key) {
//		long macString = -1;
//		if ((macString = ConvertUtil.macString2String(key)) == -1) {
//			throw new IllegalArgumentException("Mac string is illegal!");
//		}

		BizManager.getInstace().stopConnectToDevice(key);
	}

	/**
	 * 连接AP
	 */
	public static void startConnectAp() {
		if (mConfig.getApSSID() != null) {
			BizManager.getInstace()
					.startConnectToAp(mConfig.getApSSID(), mConfig.getApIp(), mConfig.getApPort(), false);
		}
	}

	/**
	 * 断开AP
	 */
	public static void stopConnectAp() {
		if (mConfig.getApSSID() != null) {
			BizManager.getInstace().stopConnectToAp(mConfig.getApSSID(), false);
		}
	}

	/**
	 * 重连AP
	 */
//	public static void reconnectAp() {
//		if (mConfig.getApSSID() != null) {
//			BizManager.getInstace().reConnectToAp(mConfig.getApSSID(), false);
//		}
//	}

	/**
	 *	向设备发送指令
	 *
	 *	@param key
	 *	@param cmd
	 *	@param sn
	 */
	public static void sendCmdToDevice(String key, CmdInfo cmd, int sn) {
		// TODO CMD_INFO or Json

		BizManager.getInstace().sendCmdToDevice(key, cmd, sn);
	}

	/**
	 *	登录设备
	 *
	 *	@param key
	 *	@param sn
	 */
	public static void doLoginDevice(String key, int sn) {
		// TODO CMD_INFO or Json
		BizManager.getInstace().loginDevice(key, sn);
	}
	
	/**
	 *	查询设备
	 *
	 *	@param key
	 *	@param sn
	 */
	public static void checkDeviceStatus(String key, int sn) {
		// TODO CMD_INFO or Json
		BizManager.getInstace().checkDeviceStatus(key, sn);
	}
	
	public static class MqttHandler{
		/**
		 *	订阅
		 *
		 *	@param topic
		 */
		public static void subcribe(String topic) {
			BizManager.getInstace().mqttSubcribe(topic);
		}
		
		/**
		 *	发布
		 *
		 *	@param topic
		 */
		public static void publish(String topic, String message) {
			BizManager.getInstace().mqttPublish(topic, message);
		}
	}
	
	
	/**
	 *	结果回调
	 *
	 *	@author wangyf 2015-1-23
	 */
	public static abstract class Callback {

		@Deprecated
		public void onBroadcastReceive(String data) {
		}

		@Deprecated
		public void onTCPReceive(String data) {
		}

		@Deprecated
		public void onTCPSend(String data) {
		}

		/**
		 * 发现新设备
		 * 
		 * @param info
		 *            设备信息
		 */
		public abstract void onDiscoverNewDevice(DeviceInfo info);

		/**
		 * 设备网络状况的变化
		 * 
		 * @param info
		 *            设备信息
		 */
		public void onDevNetStatChange(DeviceInfo info){};

		/**
		 * 发送指令的结果
		 * 
		 * @param key
		 *            目标设备key
		 * @param sn
		 *            指令包的sn
		 * @param success
		 *            指令是否发送是否成功
		 * @param errorInfo
		 *            如果失败的报错信息，成功则为null
		 */
		public abstract void onSendDevCmdResult(String key, int sn,
				boolean success, ErrorInfo errorInfo);

		/**
		 * 收到设备状态上报
		 * 
		 * @param json
		 *            json格式的报文
		 */
		public abstract void onRecvDevStatus(String json);

		/**
		 * 收到设备状态上报
		 * 
		 * @param status
		 *            设备状态上报
		 */
		public void onRecvDevStatus(DevStatus status) {
		}
		
		/**
		 * 与设备建立连接结果
		 * 
		 * @param key
		 *            目标设备key
		 * @param success
		 *            连接建立是否成功
		 * @param errorInfo
		 *            如果失败的报错信息，成功则为null
		 */
		@Deprecated
		public void onConnectDeviceResult(String key, boolean success,
				ErrorInfo errorInfo){}

		/**
		 * 与设备连接断开
		 * 
		 * @param key
		 *            目标设备key
		 * @param errorInfo
		 *            连接断开的报错信息
		 */
		@Deprecated
		public void onDeviceDisconnected(String key, ErrorInfo errorInfo){}

	}

	/**
	 *	为AP模式适配的Callback
	 *
	 *	@author wangyf 2015-1-21
	 */
	public static abstract class CallbackAP extends Callback {

		private Future<?> checkTaskFuture;

		@Override
		public void onDiscoverNewDevice(DeviceInfo info) {
		}

		@Override
		public void onDevNetStatChange(DeviceInfo info) {
		}
		
		@Override
		public void onConnectDeviceResult(String key, boolean success,
				ErrorInfo errorInfo) {
			if (key.equals(SDKConst.AP_KEY)) {
				if (success) {
					checkTaskFuture = ThreadPoolManager.getInstance()
						.getThreadPool()
						.scheduleWithFixedDelay(
							new Runnable() {
								@Override
								public void run() {
									BizManager.getInstace()
										.checkApStatus(SDKConst.AP_KEY, 0);
								}
							}, 0,
							SDKConst.PROTOCOL_GREEN_CHECK_INTERVAL,
							TimeUnit.MILLISECONDS);
				}
				onConnectApResult(success, errorInfo);
			}
		}

		@Override
		public void onDeviceDisconnected(String key, ErrorInfo errorInfo) {
			if (key.equals(SDKConst.AP_KEY)) {
				if (checkTaskFuture != null) {
					checkTaskFuture.cancel(true);
				}
				onApDisconnected(errorInfo);
			}
		}

		@Override
		public void onSendDevCmdResult(String key, int sn, boolean success,
				ErrorInfo errorInfo) {
		}

		/**
		 * 与AP建立连接的结果
		 * 
		 * @param success
		 *            连接建立是否成功
		 * @param errorInfo
		 *            如果失败的报错信息，成功则为null
		 */
		public abstract void onConnectApResult(boolean success,
				ErrorInfo errorInfo);

		/**
		 * 与Ap连接断开
		 * 
		 * @param errorInfo
		 *            连接断开的报错信息
		 */
		public abstract void onApDisconnected(ErrorInfo errorInfo);
	}

	
	
	private static class SDKHandler extends Handler {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			String data = null;
			String key = null;
			DeviceInfo devInfo = null;
			ErrorInfo errorInfo = null;
			boolean success = false;
			int sn = -1;
			Map<String, Object> map = null;

			switch (msg.what) {
			case SDKConst.MSG_DEBUG_BROADCAST_RECEIVE: // 广播回复
				if (msg.obj instanceof String) {
					data = (String) msg.obj;
					if (mCallback != null) {
						mCallback.onBroadcastReceive(data);
					}
				}
				break;
			case SDKConst.MSG_DEBUG_TCP_RECEIVE: // 调试：TCP接收
				if (msg.obj instanceof String) {
					data = (String) msg.obj;
					if (mCallback != null) {
						mCallback.onTCPReceive(data);
					}
				}
				break;
			case SDKConst.MSG_DEBUG_TCP_SEND: // 调试：TCP发送
				if (msg.obj instanceof String) {
					data = (String) msg.obj;
					if (mCallback != null) {
						mCallback.onTCPSend(data);
					}
				}
				break;
			case SDKConst.MSG_DISCOVER_FIND_NEW: // 发现新设备
				// TODO 增加到ListView中
				if (msg.obj instanceof DeviceInfo) {
					devInfo = (DeviceInfo) msg.obj;
					
					if (mCallback != null) {
						mCallback.onDiscoverNewDevice(devInfo);
					}
				}
				break;

			case SDKConst.MSG_DEVICE_CONNECT_RESULT: // 设备连接结果
				// String key = (String)msg.obj;
				if (devInfo instanceof DeviceInfo) {
					devInfo = (DeviceInfo) msg.obj;
				}
				if (msg.obj instanceof HashMap<?, ?>) {
					map = (HashMap<String, Object>) msg.obj;
					key = (String) map.get("KEY_MAC");
					errorInfo = (ErrorInfo) map.get("KEY_ERR");
				} else if (msg.obj instanceof String) {
					key = (String) msg.obj;
				}

				success = msg.arg1 == 1 ? true : false;
				if (mCallback != null) {
					mCallback.onConnectDeviceResult(key, success, null);
				}
				break;

			case SDKConst.MSG_DEVICE_DISCONN: // 设备断线
				map = (HashMap<String, Object>) msg.obj;
				key = (String) map.get("KEY_MAC");
				errorInfo = (ErrorInfo) map.get("KEY_ERR");
				
				if (mCallback != null) {
					mCallback.onDeviceDisconnected(key, errorInfo);
				}
				break;

			case SDKConst.MSG_DEVICE_NETSTAT_CHANGE: // 设备网络变化
				//TODO 
//				if (mCallback != null) {
//					mCallback.onDeviceDisconnected(key, errorInfo);
//				}
				break;
			case SDKConst.MSG_DEVICE_CMD_ACK: // 发送命令回复
				sn = msg.arg1;
				success = msg.arg2 == 1 ? true : false;

				if (msg.obj instanceof HashMap<?, ?>) {
					map = (HashMap<String, Object>) msg.obj;
					key = (String) map.get("KEY_MAC");
					errorInfo = (ErrorInfo) map.get("KEY_ERR");
				} else if (msg.obj instanceof String) {
					key = (String) msg.obj;
				}

				if (mCallback != null) {
					mCallback.onSendDevCmdResult(key, sn, success, errorInfo);
				}
				break;

			case SDKConst.MSG_DEVICE_STATUS_JSON: // 设备状态上报（Json）
				String statJson = (String) msg.obj;
				if (mCallback != null) {
					mCallback.onRecvDevStatus(statJson);
				}
				break;

			case SDKConst.MSG_DEVICE_STATUS: // 设备状态上报
				DevStatus status = (DevStatus) msg.obj;
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
