package com.skyware.sdk.util;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

public class NetworkHelper {

	/**
	 * Integer constant to check if this build is currently running under
	 * Jellybean and above
	 */
	private static final int BUILD_VERSION_JELLYBEAN = 17;

	public static final int WIFI_CIPHER_NOPASS = 0X01;
	public static final int WIFI_CIPHER_WEP = 0X02;
	public static final int WIFI_CIPHER_WPA = 0X03;

	private static Map<String, Integer> mWifiIdMap;
	private static WifiManager mWifiManager;
	
	static {
		mWifiIdMap = new HashMap<String, Integer>();
	}
	
	private static WifiManager getWifiManager(Context context){
		if (context == null) {
			return null;
		}
		if (mWifiManager == null) {
			mWifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
		}
		return mWifiManager;
	}
	
	/**
	 * returns BASE ssid
	 * 
	 * @return BASE ssid
	 */
	public static String getBaseSSID(Context context) {
		if (context == null) {
			return null;
		}
		WifiInfo wifiInfo = getWifiManager(context).getConnectionInfo();
		return wifiInfo.getBSSID();
	}

	/**
	 * returns current ssid connected to
	 * 
	 * @return current ssid
	 */
	public static String getCurrentSSID(Context context) {
		if (context == null) {
			return null;
		}
		WifiInfo wifiInfo = getWifiManager(context).getConnectionInfo();
		return removeSSIDQuotes(wifiInfo.getSSID());
	}
	
	public static WifiInfo getCurrentWifiInfo(Context context) {
		if (context == null) {
			return null;
		}
		return getWifiManager(context).getConnectionInfo();
	}

	public static WifiConfiguration getCurrentWifiConfig(Context context) {
		if (context == null) {
			return null;
		}
		List<WifiConfiguration> wifiConfigList = getWifiManager(context).getConfiguredNetworks();
		WifiInfo curWifiInfo = getWifiManager(context).getConnectionInfo();
		for(WifiConfiguration config: wifiConfigList){
			if(config.SSID.contains(curWifiInfo.getSSID())){
				return config;
			}
		}
		return null;
	}

	/**
	 * method to check wifi
	 * 
	 * @return true if wifi is connected in our device else false
	 */
	public static boolean isWifiConnected(Context context) {
		if (context == null) {
			return false;
		}
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			return true;
		} else
			return false;
	}

	/**
	 * Returns the current IP address connected to
	 * 
	 * @return
	 */
	public static String getCurrentIpAddressConnected(Context context) {
		if (context == null) {
			return null;
		}
		WifiInfo wifiInfo = getWifiManager(context).getConnectionInfo();
		int ipval = wifiInfo.getIpAddress();
		String ipString = String
				.format("%d.%d.%d.%d", (ipval & 0xff), (ipval >> 8 & 0xff),
						(ipval >> 16 & 0xff), (ipval >> 24 & 0xff));

		return ipString.toString();
	}

	/**
	 * Returns the current GatewayIP address connected to
	 * 
	 * @return
	 */
	public static String getGatewayIpAddress(Context context) {
		if (context == null) {
			return null;
		}
		int gatwayVal = getWifiManager(context).getDhcpInfo().gateway;
		return (String.format("%d.%d.%d.%d", (gatwayVal & 0xff),
				(gatwayVal >> 8 & 0xff), (gatwayVal >> 16 & 0xff),
				(gatwayVal >> 24 & 0xff))).toString();
	}

	/**
	 * Returns the current BroadcastIp address
	 * 
	 * @return
	 */
	public static String getBroadcastIpAddress(Context context) {
		if (context == null) {
			return null;
		}
//		int gatwayVal = getWifiManager(context).getDhcpInfo().gateway;
//		return (String.format("%d.%d.%d.%d", (gatwayVal & 0xff),
//				(gatwayVal >> 8 & 0xff), (gatwayVal >> 16 & 0xff),
//				(gatwayVal >> 24 | 0xff))).toString();
        DhcpInfo myDhcpInfo = getWifiManager(context).getDhcpInfo();
        if(myDhcpInfo == null)
            return "255.255.255.255";
        int broadcast = myDhcpInfo.ipAddress & myDhcpInfo.netmask | ~myDhcpInfo.netmask;
        byte quads[] = new byte[4];
        for(int k = 0; k < 4; k++)
            quads[k] = (byte)(broadcast >> k * 8 & 255);

        try {
            return InetAddress.getByAddress(quads).getHostAddress();
        }
        catch(Exception e) {
            return "255.255.255.255";
        }
	}

	// @SuppressWarnings("deprecation")
	// public static InetAddress getBroadcastAddress(Context ctx) {
	// WifiManager cm = (WifiManager) ctx.getSystemService("wifi");
	// String broadcastAddrStr;
	// if (cm != null) {
	// DhcpInfo dhcpInfo = cm.getDhcpInfo();
	// broadcastAddrStr = Formatter.formatIpAddress(dhcpInfo.gateway |
	// -16777216);
	//
	// return new InetSocketAddress(broadcastAddrStr,);
	// } else {
	// return null;
	// }
	// }
	/**
	 * Filters the double Quotations occuring in Jellybean and above devices.
	 * This is only occuring in SDK 17 and above this is documented in SDK as
	 * http
	 * ://developer.android.com/reference/android/net/wifi/WifiConfiguration.
	 * html#SSID
	 * 
	 * @param connectedSSID
	 * @return
	 */
	public static String removeSSIDQuotes(String connectedSSID) {
		int currentVersion = Build.VERSION.SDK_INT;

		if (currentVersion >= BUILD_VERSION_JELLYBEAN) {
			if (connectedSSID.startsWith("\"") && connectedSSID.endsWith("\"")) {
				connectedSSID = connectedSSID.substring(1,
						connectedSSID.length() - 1);
			}
		}
		return connectedSSID;
	}

	// 判断Wifi是否开启 
    public static boolean isWifiEnable(Context context) {   
    	if (context == null) {
			return false;
		}
        return getWifiManager(context).isWifiEnabled();
    }  
    
	// 打开WIFI    
    public static boolean openWifi(Context context) {   
    	if (context == null) {
			return false;
		}
    	WifiManager wifiManager = getWifiManager(context);
        if (!wifiManager.isWifiEnabled()) {   
        	return wifiManager.setWifiEnabled(true);   
        }   
        return false;
    }   
   
    // 关闭WIFI    
    public static boolean closeWifi(Context context) {   
    	if (context == null) {
			return false;
		}
    	WifiManager wifiManager = getWifiManager(context);
        if (wifiManager.isWifiEnabled()) {   
        	return wifiManager.setWifiEnabled(false);   
        }  
        return false;
    }   
	
	/**
	 * 寻找当前wifi扫描列表中是否包含指定ssid
	 * 
	 * @param context
	 * @param ssid
	 * @return
	 */
	public static boolean isApScanExist(Context context, String ssid) {
		if (context == null) {
			return false;
		}
		List<ScanResult> wifiList = getWifiManager(context).getScanResults();
		if (wifiList!=null ) {
			for (ScanResult result : wifiList) {
				if (result.SSID.contains(ssid)) {
					ssid = removeSSIDQuotes(result.SSID);	//赋给精确的SSID,用来config
					return true;
				}
			}
		}
		return false;
	}

	public static WifiConfiguration isApConfigExist(Context context, String ssid) {
		if (context == null) {
			return null;
		}
		List<WifiConfiguration> existingConfigs = getWifiManager(context)
				.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.contains(ssid)) {
				ssid = removeSSIDQuotes(existingConfig.SSID);
				return existingConfig;
			}
		}
		return null;
	}

	/**
	 * 连接指定wifi
	 * 
	 * @param context
	 * @param ssid
	 * @return
	 */
	public static boolean connectWifi(Context context, String ssid, String pwd, int type) {
		if (context == null) {
			return false;
		}
		if (getCurrentSSID(context).contains(ssid)) {
			return true;
		}
		if (!isApScanExist(context, ssid)) {
			return false;
		}
		WifiManager wifiManager = getWifiManager(context);
		WifiConfiguration wifiConfig = setWifiParams(context, ssid, pwd, type);
		//增加至wifi配置列表
		int netid = wifiManager.addNetwork(wifiConfig);
		//保持netId，断开时会用到
		mWifiIdMap.put(ssid, netid);
		//连接指定id的Wifi
		return wifiManager.enableNetwork(netid, true);
	}

	/**
	 * 连接指定wifi，已存在Config
	 * 
	 * @param context
	 * @param ssid
	 * @return
	 */
	public static boolean connectWifi(Context context, WifiConfiguration config) {
		if (config == null) {
			return false;
		}
		
		String ssid = config.SSID;
		
		if (getCurrentSSID(context).contains(ssid)) {
			return true;
		}
		if (!isApScanExist(context, ssid)) {
			return false;
		}
		WifiManager wifiManager = getWifiManager(context);

		//连接指定id的Wifi
		return wifiManager.enableNetwork(config.networkId, true);
	}
	
	/**
	 * 断开指定wifi
	 * 
	 * @param context
	 * @param ssid
	 * @return
	 */
	public static boolean disconnectWifi(Context context, String ssid) {
		if (context == null) {
			return false;
		}
		Integer netId = mWifiIdMap.get(ssid);
		if(netId != null){
			if(getWifiManager(context).disableNetwork(netId)){
				mWifiIdMap.remove(ssid);
				return true;
			}
		}
		return false;
	}
	
	/* 设置要连接的热点的参数 */
	public static WifiConfiguration setWifiParams(Context context,
			String ssid, String passwd, int type) {
		if (context == null) {
			return null;
		}
		WifiConfiguration apConfig = new WifiConfiguration();
		apConfig.allowedAuthAlgorithms.clear();
		apConfig.allowedGroupCiphers.clear();
		apConfig.allowedKeyManagement.clear();
		apConfig.allowedPairwiseCiphers.clear();
		apConfig.allowedProtocols.clear();

		WifiConfiguration tempConfig = isApConfigExist(context, ssid);
		if (tempConfig != null) {
			getWifiManager(context).removeNetwork(tempConfig.networkId);
		}

		apConfig.SSID = "\"" + ssid + "\"";

		switch (type) {
		case WIFI_CIPHER_NOPASS:
//			apConfig.wepKeys[0] = "";
//			apConfig.wepTxKeyIndex = 0;
			apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//			apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//			apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//			apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//			apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			break;
		case WIFI_CIPHER_WEP:
			apConfig.hiddenSSID = true;
			apConfig.wepKeys[0] = "\"" + passwd + "\"";
			apConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			apConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			apConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);
			apConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP40);
			apConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			apConfig.wepTxKeyIndex = 0;
			break;
		case WIFI_CIPHER_WPA:
			apConfig.preSharedKey = "\"" + passwd + "\"";
			apConfig.hiddenSSID = true;
			apConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			apConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			apConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			apConfig.status = WifiConfiguration.Status.ENABLED;
			break;
		default:
			break;
		}

		// apConfig.preSharedKey = "\"\"";
		// apConfig.hiddenSSID = true;
		// apConfig.status = WifiConfiguration.Status.ENABLED;
		// apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		// apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		// apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		// apConfig.allowedPairwiseCiphers
		// .set(WifiConfiguration.PairwiseCipher.TKIP);
		// apConfig.allowedPairwiseCiphers
		// .set(WifiConfiguration.PairwiseCipher.CCMP);
		// apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		return apConfig;
	}
	
	
	/*
	 * public static InetSocketAddress getBroadcastAddress() {
	 * 
	 * try { InetAddress localHost; InetAddress broadcastAddr = null;
	 * 
	 * localHost = Inet4Address.getLocalHost();
	 * 
	 * NetworkInterface networkInterface; networkInterface =
	 * NetworkInterface.getByInetAddress(localHost);
	 * 
	 * for (InterfaceAddress address : networkInterface
	 * .getInterfaceAddresses()) { //
	 * System.out.println(address.getNetworkPrefixLength()); if
	 * (address.getBroadcast() != null) { broadcastAddr =
	 * address.getBroadcast(); break; } }
	 * 
	 * return new InetSocketAddress(broadcastAddr,
	 * SocketConst.UDP_REMOTE_PORT_DEFAULT); } catch (UnknownHostException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); } catch
	 * (SocketException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } return null;
	 * 
	 * }
	 */
}
