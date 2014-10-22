package com.skyware.sdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

public class NetworkHelper {

	/**
	 * Integer constant to check if this build is currently running under
	 * Jellybean and above
	 */
	private static final int BUILD_VERSION_JELLYBEAN = 17;


	/**
	 * returns BASE ssid
	 * 
	 * @return BASE ssid
	 */
	public static String getBaseSSID(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getBSSID();
	}

	/**
	 * returns current ssid connected to
	 * 
	 * @return current ssid
	 */
	public static String getCurrentSSID(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return removeSSIDQuotes(wifiInfo.getSSID());
	}

	/**
	 * method to check wifi
	 * 
	 * @return true if wifi is connected in our device else false
	 */
	public static boolean isWifiConnected(Context context) {
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
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
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
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		int gatwayVal = wifiManager.getDhcpInfo().gateway;
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
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		int gatwayVal = wifiManager.getDhcpInfo().gateway;
		return (String.format("%d.%d.%d.%d", (gatwayVal & 0xff),
				(gatwayVal >> 8 & 0xff), (gatwayVal >> 16 & 0xff),
				(gatwayVal >> 24 | 0xff))).toString();
	}
//	@SuppressWarnings("deprecation")
//	public static InetAddress getBroadcastAddress(Context ctx) {
//		WifiManager cm = (WifiManager) ctx.getSystemService("wifi");
//		String broadcastAddrStr;
//		if (cm != null) {
//			DhcpInfo dhcpInfo = cm.getDhcpInfo();
//			broadcastAddrStr = Formatter.formatIpAddress(dhcpInfo.gateway | -16777216);
//			
//			return new InetSocketAddress(broadcastAddrStr,);
//		} else {
//			return null;
//		}
//	}
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
