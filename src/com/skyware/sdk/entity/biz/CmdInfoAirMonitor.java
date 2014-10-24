package com.skyware.sdk.entity.biz;

import com.skyware.sdk.entity.CmdInfo;

public class CmdInfoAirMonitor extends CmdInfo{

	public CmdInfoAirMonitor() {
		setData(new DevDataGreen());
	}
	
	public void setDevStatusCheck() {
		if (getData() instanceof DevDataGreen) {
			((DevDataGreen)getData()).setPower(DevDataHezhong.VALUE_ON_STATE);
		}
	}
	
	
	
}
