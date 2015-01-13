package com.skyware.sdk.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import com.skyware.sdk.manage.SocketCommunication;
import com.skyware.sdk.packet.OutPacket;

public class PacketSendTask implements Callable<Object>{
	private SocketCommunication mCommunication;
	private AtomicInteger period = new AtomicInteger(0);	//两次任务之间的间隔，默认为0
	
	public PacketSendTask(SocketCommunication communication) {
		mCommunication = communication;
	}
	public PacketSendTask(SocketCommunication communication,int period) {
		mCommunication = communication;
		this.period.set(period);
	}
	public synchronized int getDelay() {
		return period.get();
	}
	public synchronized void setDelay(int period) {
		this.period.set(period);
	}

	public Object call() throws Exception {
		
		OutPacket packet = mCommunication.dequeueSendQueue();
		while (packet != null) {
			mCommunication.sendPacketSync(packet);
			// 取出下一个待发送的包
			packet = mCommunication.dequeueSendQueue();
			if(period.get() != 0)
				Thread.sleep(period.get());
		}
		return null;
	}
	
}
