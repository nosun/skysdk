package com.skyware.sdk.exception;

public class SkyPacketParseException extends Exception {

	private static final long serialVersionUID = 3257284738459775545L;
	
	public SkyPacketParseException() {
		super();
	}
	
	public SkyPacketParseException(String arg0) {
		super(arg0);
	}
	
	public SkyPacketParseException(Throwable arg0) {
		super(arg0);
	}
	
	public SkyPacketParseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
