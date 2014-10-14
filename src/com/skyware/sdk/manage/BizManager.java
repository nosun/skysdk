package com.skyware.sdk.manage;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.skyware.sdk.callback.IBizCallback;
import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.consts.SDKConst;
import com.skyware.sdk.entity.DeviceInfo;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.util.PacketHelper;

public class BizManager {

	//饿汉式单例
	private static BizManager mInstance = new BizManager();
	public static BizManager getInstace() {
		return mInstance;
	}
	
	private BizManager() {}
	
	/** 业务回调，传递给网络层用来上报 */
	private MyBizCallback mBizCallback;
	
	public IBizCallback getBizCallback(){
		return this.mBizCallback;
	}
	
	/** UI线程的Handler，用来向UI层传递数据 */
	private Handler mainHandler;
	
	public void setMainHandler(Handler handler){
		this.mainHandler = handler;
	}
	
	/** Application */
	private Context mContext;
	
	public Context getContext() {
		return mContext;
	}
	
	private Map<String, DeviceInfo> mDeviceMap;
	
	
	/**
	 *	初始化资源
	 *
	 *	@param context	application
	 */
	public void init(Context context) {
		this.mContext = context;
		mBizCallback = new MyBizCallback();
		NetworkManager.getInstace().setBizCallback(mBizCallback);
		
		mDeviceMap = new ConcurrentHashMap<String, DeviceInfo>();
		
		NetworkManager.getInstace().init();
	}
	
	/**
	 *	开始发现设备的任务
	 */
	public void startDiscoveryDevice() {
		Log.e(this.getClass().getSimpleName(), "startDiscoveryDevice()!");
		NetworkManager.getInstace().startBroadcaster();
	}
	
	/**
	 *	停止发现设备的任务
	 */
	public void stopDiscoveryDevice() {
		Log.e(this.getClass().getSimpleName(), "stopDiscoveryDevice()!");
		NetworkManager.getInstace().stopBroadcaster();
	}

	
	/**
	 *	连接设备
	 *
	 */
	public void startConnectToDevice(String mac) {
		Log.e(this.getClass().getSimpleName(), "startConnectToDevice()! mac: " + mac);
		try {
			NetworkManager.getInstace().startNewConnect(mac, true);
			
			//发送查询指令
			OutPacket packet = PacketHelper.getDevCheckPacket(NetworkManager.getInstace().getSn());
//			Log.e(this.getClass().getSimpleName(), "packet: " + new String(packet.getContent(), Charset.forName("US-ASCII")));
			if (packet != null) {
				NetworkManager.getInstace().sendPacketToDevice(mac, packet, true);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 *	断开连接
	 *
	 */
	public void stopConnectToDevice(String mac) {
		Log.e(this.getClass().getSimpleName(), "stopConnectToDevice()! mac: " + mac);
		
		NetworkManager.getInstace().stopConnect(mac);
	}
	
	
	/**
	 *	向设备发送命令
	 */
	public void sendCmdToDevice(String mac, String[] cmd) {
		Log.e(this.getClass().getSimpleName(), "sendCmdToDevice()! mac: " + mac + ",cmd: " + cmd[0]);

		try {
			OutPacket packet = PacketHelper.getDevCmdPacket(NetworkManager.getInstace().getSn(), cmd);
			
//			Log.e(this.getClass().getSimpleName(), "packet: " + new String(packet.getContent(), Charset.forName("US-ASCII")));
			if (packet != null) {
				NetworkManager.getInstace().sendPacketToDevice(mac, packet, true);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private class MyBizCallback implements IBizCallback{


		@Override
		public void onConnectCloudError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRecvUDPPacket(InPacket packet) {
			if (mainHandler != null) {
				String toShow = "";
				toShow += packet.getSourceAddr();
				toShow += " " + new String(packet.getContent(), Charset.forName("US-ASCII"));
				mainHandler.obtainMessage(SDKConst.MSG_BROADCAST_RECEIVE, toShow).sendToTarget();
			}
		}

		@Override
		public void onDiscoverNewDevice(String deviceMac, String deviceIp) {
			if (mainHandler != null) {
				DeviceInfo deviceInfo = new DeviceInfo();
				deviceInfo.setIp(deviceIp);
				deviceInfo.setMac(deviceMac);
				
				//TODO 向服务器验证，注册该设备
				
				mDeviceMap.put(deviceMac, deviceInfo);
				
				mainHandler.obtainMessage(SDKConst.MSG_DISCOVER_FIND_NEW, deviceInfo).sendToTarget();
			}	
		}


		@Override
		public void onRecvDevStatus(InPacket packet) {
			if (mainHandler != null) {
				DeviceInfo deviceInfo = PacketHelper.resolveDevStatPacket(packet);
				
				mainHandler.obtainMessage(SDKConst.MSG_DEVICE_STATUS, deviceInfo).sendToTarget();
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
			if (mainHandler != null) {
				if(packet.getType() == OutPacket.Type.TYPE_DEVCOMMAND) {
					mainHandler.obtainMessage(SDKConst.MSG_DEVICE_CMD_ACK).sendToTarget();
				}
			}
		}

		@Override
		public void onSendTCPPacketError(ErrorConst errType, String errMsg,
				OutPacket packet) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSDKError(ErrorConst errType, String errMsg) {
			// TODO Auto-generated method stub
			
		}


	}


}
