package com.skyware.sdk.consts;

public enum ErrorConst {
	SUCCESS,
	EPARA_ISNULL,
	EPARA_MISSINFO,
	EADD_EXIST,
	EADD_FIELD_ILLEGAL,
	EDEL_NOTEXIST,
	EUNKNOWN,
	EWIFI_NOTCONNECT, 		//未连接wifi
	
	ESOCK_NIO_CHANNELCLOSE_BY_OTHERTHREAD, 	//AsynchronousCloseException 
	ESOCK_NIO_INTERRUPT_BY_OTHERTHREAD,		//ClosedByInterruptException
	ESOCK_NIO_CHANNELCLOSE_OPER_ATTEMPT,	//ClosedChannelException
	ESOCK_NIO_CHANNEL_UNCONNECT,			//NotYetConnectedException 
	
	ESOCK_BIO_CLOSE_BY_REMOTE,
	ESOCK_BIO_CLOSE_BY_SELF,
	ESOCK_BIO_CLOSE_BY_HEARTBEAT,
	ESOCK_BIO_SOCK_OPTIONS,					//SocketException
	ESOCK_BIO_TCP_NOTFRAME_CLOSE,			//EOFException
	ESOCK_ARGUMENT_ERROR,					//IllegalArgumentException
	ESOCK_IO_UNKNOWN, 						//IOException
	
}
