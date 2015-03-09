package com.skyware.sdk.entity;

import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.entity.biz.CmdInfoAirPurifier;
import com.skyware.sdk.entity.biz.CmdInfoPlugin;
import com.skyware.sdk.entity.biz.CmdInfoWatarPot;

public class CmdInfoFactory{

//	private DevData data;
//	private DevDataHezhong data;
	

	public static CmdInfo getCmdInfo(int productType) {
		switch (productType) {
		case SDKConst.PRODUCT_AIRPAL_PL200:
		case SDKConst.PRODUCT_AIRPAL_PL500:
		case SDKConst.PRODUCT_CAIR:
			return new CmdInfoAirPurifier(productType);
		case SDKConst.PRODUCT_BL_SP2:
			return new CmdInfoPlugin();
		case SDKConst.PRODUCT_ROYALSTAR:
			return new CmdInfoWatarPot();
		default:
			break;
		}
		return null;
	}
	
	
}
