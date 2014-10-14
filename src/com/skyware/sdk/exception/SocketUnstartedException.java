package com.skyware.sdk.exception;

import java.util.concurrent.TimeoutException;

public class SocketUnstartedException extends TimeoutException{

	private static final long serialVersionUID = -3715725718954020241L;

    public SocketUnstartedException() {
    	super();
    }

	public SocketUnstartedException(String arg0) {
		super(arg0);
	}
	
}
