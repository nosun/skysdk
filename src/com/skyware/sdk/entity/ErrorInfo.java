package com.skyware.sdk.entity;

import com.skyware.sdk.consts.ErrorConst;

public class ErrorInfo {

	private ErrorConst errType;
	
	private String errStr;

	
	public ErrorConst getErrType() {
		return errType;
	}
	public void setErrType(ErrorConst errType) {
		this.errType = errType;
	}

	public String getErrStr() {
		return errStr;
	}
	public void setErrStr(String errStr) {
		this.errStr = errStr;
	}
}
