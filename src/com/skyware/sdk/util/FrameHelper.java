package com.skyware.sdk.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class FrameHelper {
	
	private static final byte DELIMITER = '\n';
	
	private static final byte DELIMITER_2 = '}';
	
	/**
	 * 将待发送的数据包成帧
	 * 
	 * @param sendMsg			待发送的数据包byte数组
	 * @param msgSize			数据包有效长度
	 * @return msgNewSize 		成帧操作后的新长度
	 * @throws IllegalArgumentException	sendMsg的数组长度不足以成帧
	 * @throws IOException		如果msgSize中包含定界符
	 */
	public static int frameMsg(byte[] sendMsg, int msgSize) throws IllegalArgumentException, IOException {
		
//		Log.e("FrameHelper", "send byte size：" + msgSize + ", content: " + new String(sendMsg));
		for (int i=0; i< msgSize ;i++) {
//			Log.e("FrameHelper", "send byte：" + (char)sendMsg[i] + ", ascii: " + sendMsg[i]);
			if (sendMsg[i] == DELIMITER) {
				if (i == msgSize-1) {	//最后一个
					return msgSize;		//不做处理
				}
				//TODO 未来采用填充技巧避免抛异常
				throw new IOException("Message contains delimiter: " + DELIMITER);
			}
		}
		
		//TODO 如果采用填充方案则可能需要更多额外空间
		if (sendMsg.length <= msgSize) {
			throw new IllegalArgumentException("the byte[] size of msg is smaller than msgSize it contains");
		}
		
		sendMsg[msgSize++] = DELIMITER;
		
		return msgSize;
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
	public static int ReframeMsg(byte[] recvMsg, InputStream in) throws EOFException, IOException {
		int nextByte;
		int recvMsgSize = 0;
		

		do {
			nextByte = in.read();
//			Log.e("ReframeMsg", "TCP read Byte: " + (char)(nextByte) + ", ascii: "+ nextByte);
			if (nextByte == DELIMITER) {
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
		} while (nextByte != DELIMITER_2);

		return recvMsgSize;
	}
	
}
