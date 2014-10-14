package com.skyware.sdk.consts;

public class SocketConst {
	
	/** Sn最大上限 **/
    public static final int SN_MAX = 65536;
	
	/** TCP包最大大小 **/
    public static final int TCP_PACKET_MAX_SIZE = 1024;
    /** UDP包最大大小 **/
    public static final int UDP_PACKET_MAX_SIZE = 128;

    /** 发送Buffer最大大小 **/
    public static final int SEND_BUFFER_MAX_SIZE = 2048;
    /** 接收Buffer最大大小 **/
    public static final int RECV_BUFFER_MAX_SIZE = 2048;
    
    /** 设备TCP端口默认值 */
    public static final int REMOTE_PORT_TCP_DEFAULT = 8899;
    /** 设备UDP端口默认值 */
    public static final int REMOTE_PORT_UDP_DEFAULT = 48899;
    /** 设备UDP本地监听端口 */
    public static final int LOCAL_PORT_UDP_DEFAULT = 8888;
    /** 本地广播地址默认值 */
    public static final String BROADCAST_ADDR_DEFAULT = "192.168.0.1";
    /** 广播内容 */
    public static final String BROADCAST_CONTENT_DEFAULT = "HF-A11ASSISTHREAD";
    
    /** 广播内容的字符编码 */
    public static final String CHARSET_BROADCAST_CONTENT = "US-ASCII";
    /** TCP内容的字符编码 */
    public static final String CHARSET_TCP_CONTENT = "US-ASCII";
    
    /** Selector的超时时间 */
    public static final int NIO_SELECT_TIMEOUT = 500;
    
    /** Block IO TCP connect()的超时时间 */
    public static final int BIO_TIMEOUT_TCP_CONNECT = 500;
    /** Block IO TCP accept()和read()的超时时间 */
    public static final int BIO_TIMEOUT_TCP_READ = 500;
    /** Block IO UDP start()的超时时间 */
    public static final int BIO_TIMEOUT_UDP_START = 200;
    /** Block IO UDP receive()的超时时间 */
    public static final int BIO_TIMEOUT_UDP_RECV = 500;
    /** Block IO 广播 receive()的超时时间 */
    public static final int BIO_TIMEOUT_BROADCAST_RECV = 5000;
    
    /** Block IO TCP 短连接 接收线程的超时时间 */
    public static final int BIO_TIMEOUT_THREAD_TCP_RECV = 5000;
    /** Block IO UDP 接收线程的超时时间 */
    public static final int BIO_TIMEOUT_THREAD_UDP_RECV = 5000;
    /** Block IO 广播 接收线程的超时时间 */
    public static final int BIO_TIMEOUT_THREAD_BROADCAST_RECV = 5000;
    
    /** Block IO TCP 最大重连的次数 */
    public static final int BIO_TCP_MAX_RECONNECT_TIMES = 5;
    /** Block IO TCP 最大接收重试的次数（短连接，乘以IO间隔为接收等待时间） */
//    public static final int BIO_TCP_MAX_RECV_RETRY_TIMES = 10;
    /** Block IO UDP 最大重传的次数 */
    public static final int BIO_UDP_MAX_RESEND_TIMES = 5;
    /** Block IO TCP 心跳周期 */
    public static final int BIO_TCP_HEARTBEAT_PRIOD = 5000;
    /** Block IO TCP IO周期 */
//    public static final int BIO_TCP_IO_PRIOD = 500;
    
    public static final boolean FLAG_ASYNC_REPORT = true;
    
}

