package com.skyware.sdk.exception;

import java.util.concurrent.TimeoutException;

public class SocketDisconnectedException extends TimeoutException{

	private static final long serialVersionUID = -3715725718954020241L;

    public SocketDisconnectedException() {
    	super();
    }

	public SocketDisconnectedException(String arg0) {
		super(arg0);
	}
	
}
