package com.skyware.sdk.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import com.skyware.sdk.manage.SocketCommunication;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.socket.IOHandler;

public class PacketReportTask implements Callable<Object>{
	private SocketCommunication mCommunication;
	private AtomicInteger period = new AtomicInteger(0);	//两次任务之间的间隔，默认为0
	private IOHandler handler;
	
	public PacketReportTask(SocketCommunication communication) {
		mCommunication = communication;
	}
	public PacketReportTask(SocketCommunication communication,int period) {
		mCommunication = communication;
		this.period.set(period);
	}
	
	public int getDelay() {
		return period.get();
	}
	public void setDelay(int period) {
		this.period.set(period);
	}
	
	public IOHandler getHandler() {
		return handler;
	}
	public void setHandler(IOHandler handler) {
		this.handler = handler;
	}
	
	public Object call() throws Exception
	{
	    InPacket packet = mCommunication.dequeueReceiveQueue();
	    while (packet != null)
	    {
	        mCommunication.reportPacketSync(getHandler(), packet);
	        // 取出下一个待上报的包
	        packet = mCommunication.dequeueReceiveQueue();
	        if(period.get() != 0)
				Thread.sleep(period.get());
	    }
	    return null;
	}
}
