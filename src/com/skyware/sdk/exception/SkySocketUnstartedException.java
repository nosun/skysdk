package com.skyware.sdk.exception;

import java.io.IOException;

public class SkySocketUnstartedException extends IOException{

	private static final long serialVersionUID = -3715725718954020241L;

    public SkySocketUnstartedException() {
    	super();
    }

	public SkySocketUnstartedException(String arg0) {
		super(arg0);
	}
	
}
