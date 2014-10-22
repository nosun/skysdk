package com.skyware.sdk.exception;

import java.io.IOException;

public class SkySocketCloseByRemoteException extends IOException{

	private static final long serialVersionUID = -3715725718954020241L;

    public SkySocketCloseByRemoteException() {
    	super();
    }

	public SkySocketCloseByRemoteException(String arg0) {
		super(arg0);
	}
	
}
