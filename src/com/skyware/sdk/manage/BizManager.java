package com.skyware.sdk.manage;

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
import com.skyware.sdk.entity.ErrorInfo;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.packet.entity.PacketEntity.DevStatus;
import com.skyware.sdk.packet.entity.PacketEntity.PacketType;
import com.skyware.sdk.util.ConvertUtil;

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
	private Context mContext;
	
	public Context getContext() {
		return mContext;
	}
	
	private Map<Long, DeviceInfo> mDeviceMap;
	
	private NetworkManager mNetworkManager;
	
	/**
	 *	初始化资源
	 *
	 *	@param context	application
	 */
	public void init(Context context) {
		this.mContext = context;
		
		mDeviceMap = new ConcurrentHashMap<Long, DeviceInfo>();
		
		mNetworkManager = new NetworkManager(new MyBizCallback());
	}
	
	/**
	 *	释放资源
	 *
	 *	@param context	application
	 */
	public void finallize() {
		mContext = null;
		mUIHandler = null;
		
		mDeviceMap = null;
		mNetworkManager.finallize();
		mNetworkManager = null;
	}
	
	/**
	 *	开始发现设备的任务
	 */
	public void startDiscoveryDevice() {
		Log.e(this.getClass().getSimpleName(), "startDiscoveryDevice()!");
		mNetworkManager.startBroadcaster();
	}
	
	/**
	 *	停止发现设备的任务
	 */
	public void stopDiscoveryDevice() {
		Log.e(this.getClass().getSimpleName(), "stopDiscoveryDevice()!");
		mNetworkManager.stopBroadcaster();
	}

	
	/**
	 *	连接设备
	 *
	 */
	public void startConnectToDevice(long mac) {
		Log.e(this.getClass().getSimpleName(), "startConnectToDevice()! mac: " + mac);
//		try {
		mNetworkManager.startNewConnect(mac, true);
			
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
	public void stopConnectToDevice(long mac) {
		Log.e(this.getClass().getSimpleName(), "stopConnectToDevice()! mac: " + mac);
		
		mNetworkManager.stopConnect(mac);
	}
	
	
	/**
	 *	向设备发送命令
	 */
	public void sendCmdToDevice(long mac, CmdInfo cmd, int sn) {
		Log.e(this.getClass().getSimpleName(), "sendCmdToDevice()! mac: " + mac + ",cmd: " + cmd + ",sn: " + sn);
		
		mNetworkManager.sendPacketToDevice(mac, PacketType.DEVCOMMAND, cmd, sn, true);
	}
	
	
	
	private class MyBizCallback implements IBizCallback{


		@Override
		public void onConnectCloudError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onConnectDeviceSuccess(long deviceMac) {
			if (mUIHandler != null) {
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CONNECT, 1, -1, deviceMac).sendToTarget();
			}
		}

		@Override
		public void onConnectDeviceError(long deviceMac, ErrorConst errType, String errMsg) {
			if (mUIHandler != null) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				ErrorInfo errInfo = new ErrorInfo();
				errInfo.setErrType(errType);
				errInfo.setErrStr(errMsg);
				
				map.put("KEY_MAC", deviceMac);
				map.put("KEY_ERR", errInfo);
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CONNECT, 0, -1, map).sendToTarget();
			}
		}
		
		@Override
		public void onDeviceDisconnected(long deviceMac, ErrorConst errType, String errMsg) {
			if (mUIHandler != null) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				ErrorInfo errInfo = new ErrorInfo();
				errInfo.setErrType(errType);
				errInfo.setErrStr(errMsg);
				
				map.put("KEY_MAC", deviceMac);
				map.put("KEY_ERR", errInfo);
				mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_DISCONN, map).sendToTarget();
			}
		}
		
		@Override
		public void onRecvUDPPacket(InPacket packet) {
			if (mUIHandler != null) {
				String toShow = "";
				toShow += packet.getSourceAddr();
				toShow += " " + new String(packet.getContent(), Charset.forName("US-ASCII"));
				mUIHandler.obtainMessage(SDKConst.MSG_BROADCAST_RECEIVE, toShow).sendToTarget();
			}
		}

		@Override
		public void onDiscoverNewDevice(long deviceMac, String deviceIp) {
			if (mUIHandler != null) {
				DeviceInfo deviceInfo = new DeviceInfo();
				deviceInfo.setIp(deviceIp);
				deviceInfo.setMac(ConvertUtil.macLong2String(deviceMac));
				//TODO 还有其他欲上报的信息
				
				//TODO 向服务器验证，注册该设备
				
				mDeviceMap.put(deviceMac, deviceInfo);
				
				mUIHandler.obtainMessage(SDKConst.MSG_DISCOVER_FIND_NEW, deviceInfo).sendToTarget();
			}	
		}


		@Override
		public void onRecvDevStatus(DevStatus devStatus) {
			if (mUIHandler != null) {
				try {
					if (SDKConst.DEBUG_FLAG) {
						mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_STATUS_DEBUG, devStatus).sendToTarget();
					} 
					
					mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_STATUS, devStatus.jsonEncoder().toString()).sendToTarget();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		
		@Override
		public void onRecvTCPPacket(InPacket packet) {
			
		}

		@Override
		public void onRecvError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSendTCPPacketSuccess(OutPacket packet) {
			if (mUIHandler != null) {
				if(packet.getType() == PacketType.DEVCOMMAND) {
					mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CMD_ACK, packet.getSn(), 1, packet.getTargetMac()).sendToTarget();
				}
			}
		}

		@Override
		public void onSendTCPPacketError(ErrorConst errType, String errMsg,
				OutPacket packet) {
			if (mUIHandler != null) {
				if(packet.getType() == PacketType.DEVCOMMAND) {
					
					HashMap<String, Object> map = new HashMap<String, Object>();
					ErrorInfo errInfo = new ErrorInfo();
					errInfo.setErrType(errType);
					errInfo.setErrStr(errMsg);
					
					map.put("KEY_MAC", packet.getTargetMac());
					map.put("KEY_ERR", errInfo);
					mUIHandler.obtainMessage(SDKConst.MSG_DEVICE_CMD_ACK, packet.getSn(), 0, map).sendToTarget();
				}
			}
		}

		@Override
		public void onSDKError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}
	}
}
