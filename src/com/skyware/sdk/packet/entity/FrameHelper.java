package com.skyware.sdk.packet.entity;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import com.skyware.sdk.api.SDKConst;

public class FrameHelper {
	
	private static final byte DELIMITER_MOORE = '\n';
	
	private static final byte DELIMITER_MOORE_2 = '}';
	
	/**
	 * 将待发送的数据包成帧
	 * 
	 * @param sendMsg			待发送的数据包byte数组
	 * @param msgSize			数据包有效长度
	 * @return msgNewSize 		成帧操作后的新长度
	 * @throws IllegalArgumentException	sendMsg的数组长度不足以成帧
	 * @throws IOException		如果msgSize中包含定界符
	 */
	public static int frameMsg(byte[] sendMsg, int msgSize, int protocol) throws IllegalArgumentException, IOException {
		
		switch (protocol) {
		case SDKConst.PROTOCOL_MOORE:
			for (int i=0; i< msgSize ;i++) {
				if (sendMsg[i] == DELIMITER_MOORE) {
					if (i == msgSize-1) {	//最后一个
						return msgSize;		//不做处理
					}
					//TODO 未来采用填充技巧避免抛异常
					throw new IOException("Message contains delimiter: " + DELIMITER_MOORE);
				}
			}
			
			//TODO 如果采用填充方案则可能需要更多额外空间
			if (sendMsg.length <= msgSize) {
				throw new IllegalArgumentException("the byte[] size of msg is smaller than msgSize it contains");
			}
			
			sendMsg[msgSize++] = DELIMITER_MOORE;
			
			return msgSize;
		case SDKConst.PROTOCOL_GREEN:
			int length = 0;
			//先存起来
//			byte[] toSend = new byte[msgSize];
//			System.arraycopy(sendMsg, 0, toSend, 0, msgSize);
//			//加入魔术开头
//			System.arraycopy(GreenPacketEntity.MagicHeader, 0, sendMsg, 0, GreenPacketEntity.MagicHeader.length);
//			length += GreenPacketEntity.MagicHeader.length;
//			//加入长度
//			sendMsg[length] = (byte) msgSize;
//			length += 1;
			//再拷入内容
//			System.arraycopy(toSend, 0, sendMsg, length, msgSize);
//			length += msgSize;
			
//			return length;
			//15-1-13 由于协议改动，此处直接返回，不加魔术头和长度
			return msgSize;
		default:
			return -1;
		}
	}
	
	
	/**
	 * 从Input流中取出一帧数据包
	 * 
	 * @param recvMsg 	将取出的数据帧放入此byte数组
	 * @param in 		Socket的输入流
	 * @return recvMsgSize 取出数据帧的长度
	 * @throws EOFException	流关闭时有尚未成帧的数据
	 * @throws IOException		stream.read()出错
	 */
	public static int ReframeMsg(byte[] recvMsg, InputStream in, int protocol) throws EOFException, IOException {
		int nextByte;
		int recvMsgSize = 0;
		
		switch (protocol) {
		case SDKConst.PROTOCOL_MOORE:
			do {
				nextByte = in.read();
//				Log.e("ReframeMsg", "TCP read Byte: " + (char)(nextByte) + ", ascii: "+ nextByte);
				if (nextByte == DELIMITER_MOORE) {
					continue;
				}
				
				if (nextByte == -1) {	//流关闭，需检测是否正常关闭
					if (recvMsgSize == 0) {
						return recvMsgSize;		//没字节读入，正常关闭
					} else {
						throw new EOFException("Non-empty message without delimiter!");
					}
				}
				recvMsg[recvMsgSize++] = (byte) nextByte;
			} while (nextByte != DELIMITER_MOORE_2);
			break;
			
		case SDKConst.PROTOCOL_GREEN:
//			byte[] magicHeader = new byte[GreenPacketEntity.MagicHeader.length];
//			Arrays.fill(magicHeader, (byte) 0xff);
			//处理魔术开头
//			in.read(magicHeader, 0, GreenPacketEntity.MagicHeader.length);
//			if (Arrays.equals(magicHeader, GreenPacketEntity.MagicHeader)) {
//				recvMsgSize += 5;	抛弃魔术开头和长度byte
//				System.arraycopy(magicHeader, 0, recvMsg, 0, 5);

//				int remainLen = in.read(); 
				int remainLen = SDKConst.PROTOCOL_GREEN_PACKET_BYTES;  //protocol v0.2定义固定长度
//				recvMsgSize += 1;
				
				in.read(recvMsg, 0, remainLen);
				recvMsgSize += remainLen;
//			}
			break;
		default:
			break;
		}
		
		return recvMsgSize;
	}
	
}
