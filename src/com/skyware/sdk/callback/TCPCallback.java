package com.skyware.sdk.callback;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

import android.util.Log;

import com.skyware.sdk.consts.ErrorConst;
import com.skyware.sdk.consts.SocketConst;
import com.skyware.sdk.exception.SocketDisconnectedException;
import com.skyware.sdk.manage.TCPCommunication;
import com.skyware.sdk.packet.InPacket;
import com.skyware.sdk.packet.OutPacket;
import com.skyware.sdk.socket.IOHandler;

public class TCPCallback implements ISocketCallback {
	
	private TCPCommunication mCommunication;

	public TCPCallback(TCPCommunication msgCommunication) {
		mCommunication = msgCommunication;
	}
	
	@Override
	public void onStartFinished(IOHandler h) {
		
		mCommunication.onConnectSuccess(h);
	}

	@Override
	public void onStartError(IOHandler h, Exception e) {
		Log.e(e.getClass().getSimpleName(), e.getLocalizedMessage());
		
		ErrorConst errType = null;
		
		//SocketChannel.write()抛出的Exception
		//NOTICE: Exception的范围要从小到大

		if (e instanceof SocketException) {
			//during socket creation or setting options, and is the superclass of all other socket related exceptions.
			errType = ErrorConst.ESOCK_BIO_SOCK_OPTIONS; 
		} else if (e instanceof IOException) {
			//other IO exceptions
			errType = ErrorConst.ESOCK_IO_UNKNOWN;
		} else if (e instanceof IllegalArgumentException) {
			//arguments error
			errType = ErrorConst.ESOCK_ARGUMENT_ERROR;
		} else {
			errType = ErrorConst.EUNKNOWN;
		}
		mCommunication.onConnectError(h, errType, e.getLocalizedMessage());
	}
	
	@Override
	public void onReceive(InPacket packet) {
		// 如果是selector监听线程，则使用异步上报，保证监听实时性，否则采用同步上报
		if (SocketConst.FLAG_ASYNC_REPORT)
			mCommunication.reportPacketAsync(packet);
		else
			mCommunication.reportPacketSync(packet);
	}

	@Override
	public void onReceiveError(IOHandler h, Exception e) {
		Log.e(e.getClass().getSimpleName(), e.getLocalizedMessage());
		ErrorConst errType = null;
		//SocketChannel.write()抛出的Execption

		if (e instanceof SocketDisconnectedException ) {
			//if this TCP client socket is not yet connected.
			errType = ErrorConst.ESOCK_BIO_CLOSE_BY_REMOTE;
		} else if (e instanceof EOFException ) {
			//if this stream close but has not framed data remained.
			errType = ErrorConst.ESOCK_BIO_TCP_NOTFRAME_CLOSE;
		} else if (e instanceof IOException) {
			//if another I/O error occurs.
			errType = ErrorConst.ESOCK_IO_UNKNOWN;
		} else {
			errType = ErrorConst.EUNKNOWN;
		}
		
		mCommunication.onReceiveError(h, errType, e.getLocalizedMessage());
	}

	@Override
	public void onSendFinished(OutPacket packet) {
		mCommunication.onSendFinished(packet);
	}

	@Override
	public void onSendError(Exception e, OutPacket packet) {
		Log.e(e.getClass().getSimpleName(), e.getLocalizedMessage());
		ErrorConst errType = null;
		//SocketChannel.write()抛出的Execption
		
		if (e instanceof SocketDisconnectedException ) {
			//if this TCP client socket is not yet connected.
			errType = ErrorConst.ESOCK_BIO_CLOSE_BY_REMOTE;
		} else if (e instanceof IOException) {
			//if another I/O error occurs.
			errType = ErrorConst.ESOCK_IO_UNKNOWN;
		} else {
			errType = ErrorConst.EUNKNOWN;
		}

		mCommunication.onSendError(packet, errType, e.getLocalizedMessage());
	}

	@Override
	public void onCloseFinished(IOHandler h) {
		mCommunication.onCloseFinished(h);
	}

	@Override
	public void onCloseError(IOHandler h, Exception e) {
		Log.e(e.getClass().getSimpleName(), e.getLocalizedMessage());
//		mCommunication.onCloseError(h, errType, errMsg);
	}

}
