package com.skyware.sdk.util;

import java.net.InetSocketAddress;

import com.skyware.sdk.consts.SocketConst;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

public class NetworkHelper {

/*	public static InetSocketAddress getBroadcastAddress() {

		try {
			InetAddress localHost;
			InetAddress broadcastAddr = null;

			localHost = Inet4Address.getLocalHost();

			NetworkInterface networkInterface;
			networkInterface = NetworkInterface.getByInetAddress(localHost);

			for (InterfaceAddress address : networkInterface
					.getInterfaceAddresses()) {
				// System.out.println(address.getNetworkPrefixLength());
				if (address.getBroadcast() != null) {
					broadcastAddr = address.getBroadcast();
					break;
				}
			}

			return new InetSocketAddress(broadcastAddr,
					SocketConst.UDP_REMOTE_PORT_DEFAULT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}*/

	@SuppressWarnings("deprecation")
	public static InetSocketAddress getBroadcastAddress(Context ctx) {
		WifiManager cm = (WifiManager) ctx.getSystemService("wifi");
		String broadcastAddrStr;
		if (cm != null) {
			DhcpInfo dhcpInfo = cm.getDhcpInfo();
			broadcastAddrStr = Formatter.formatIpAddress(dhcpInfo.gateway | -16777216);
			
			return new InetSocketAddress(broadcastAddrStr,
					SocketConst.REMOTE_PORT_UDP_DEFAULT);
		} else {
			return null;
		}
	}

}
