package com.skyware.sdk.packet.entity;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.skyware.sdk.consts.SDKConst;
import com.skyware.sdk.util.ArrayUtil;

public class ProtocolHelper {

	
	/**
	 *	根据协议进行端口指定
	 */
	public static InetSocketAddress getSocketAddr(InetAddress targetIp, int targetProtocol) {

		return new InetSocketAddress(targetIp, SDKConst.PROTOCOL_PORT_COMM[targetProtocol]);
	}
	
	/**
	 *  TODO 不能仅仅通过端口确定，有可能端口重复，需要通过具体的协议内容确定
	 *	根据地址端口确定协议
	 */
	public static int getProtocolWithPort(int remotePort) {
		int protocol = ArrayUtil.findIndex(SDKConst.PROTOCOL_PORT_COMM, remotePort);
		if (protocol == -1) {
			protocol = ArrayUtil.findIndex(SDKConst.PROTOCOL_PORT_FIND, remotePort);
		}
		return protocol != -1 ? protocol : SDKConst.PROTOCOL_UNKNOWN;
	}
	

}
