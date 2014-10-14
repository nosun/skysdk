package com.skyware.sdk.packet;

public class Packet {
	 /** 包的内容 */
    protected byte[] content;


    public synchronized byte[] getContent()
    {
        return content;
    }

    public synchronized void setContent(byte[] content)
    {
        this.content = content;
    }
}
