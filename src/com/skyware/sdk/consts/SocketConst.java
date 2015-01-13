package com.skyware.sdk.consts;

public class SocketConst {
	
	/** Sn最大上限 **/
    public static final int SN_MAX = 65536;
	
    
	/** TCP包最大大小 **/
//    public static final int TCP_PACKET_MAX_SIZE = 1024;
    /** UDP包最大大小 **/
//    public static final int UDP_PACKET_MAX_SIZE = 128;
    
    /** 发送Buffer最大大小 **/
    public static final int SEND_BUFFER_MAX_SIZE = 2048;
    /** 接收Buffer最大大小 **/
    public static final int RECV_BUFFER_MAX_SIZE = 2048;
    
    
    /** 设备UDP广播本地监听端口 */
    public static final int PORT_UDP_LOCAL 	= 8888;
    
    /** 本地广播地址默认值 */
    public static final String BROADCAST_ADDR_DEFAULT = "192.168.0.1";
    
    /** 广播内容的字符编码 */
    public static final String CHARSET_BROADCAST_CONTENT = "US-ASCII";
    /** TCP内容的字符编码 */
    public static final String CHARSET_TCP_CONTENT = "US-ASCII";
    
    /** Selector的超时时间 */
    public static final int NIO_SELECT_TIMEOUT 	= 500;
    
    /** TCP connect()的超时时间 */
    public static final int TIMEOUT_TCP_CONNECT 	= 200;
    /** TCP accept()和read()的超时时间 */
    public static final int TIMEOUT_TCP_READ 		= 500;
    /** UDP start()的超时时间 */
    public static final int TIMEOUT_UDP_START 	= 2000;
    /** UDP receive()的超时时间 */
    public static final int TIMEOUT_UDP_RECV 		= 500;
    /** 广播 receive()的超时时间 */
    public static final int TIMEOUT_BROADCAST_RECV = 5000;
    
    /** TCP 短连接 接收线程的超时时间 */
    public static final int TIMEOUT_THREAD_TCP_RECV = 5000;
    /** UDP 接收线程的超时时间 */
    public static final int TIMEOUT_THREAD_UDP_RECV = 5000;
    /** 广播 接收线程的超时时间 */
    public static final int TIMEOUT_THREAD_BROADCAST_RECV = 5000;
    /** Future等待超时时间 */
    public static final int TIMEOUT_FUTURE_WAIT = 3000;
    
    /** UDP 最大重新start的次数 */
    public static final int RETRY_TIMES_UDP_START 	= 3;
    /** TCP 最大重连的次数 */
    public static final int RETRY_TIMES_TCP_CONNECT 	= 2;
    /** TCP 最大接收重试的次数（短连接，乘以IO间隔为接收等待时间） */
//    public static final int BIO_TCP_MAX_RECV_RETRY_TIMES = 10;
    /** UDP 最大重传的次数 */
    public static final int RETRY_TIMES_UDP_SEND = 5;
    
    /** TCP 心跳周期 */
    public static final int PERIOD_TCP_HEARTBEAT = 5000;
    /** TCP IO周期 */
//    public static final int BIO_TCP_IO_PRIOD = 500;
   
    
    /** 同步、异步上报的开关 */
    public static final boolean FLAG_ASYNC_REPORT = true;
    
}

