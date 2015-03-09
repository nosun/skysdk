package com.skyware.sdk.entity;

import com.skyware.sdk.api.SDKConst;
import com.skyware.sdk.entity.biz.DevDataAirpal;
import com.skyware.sdk.entity.biz.DevDataBroadlink;
import com.skyware.sdk.entity.biz.DevDataCair;
import com.skyware.sdk.entity.biz.DevDataGreen;
import com.skyware.sdk.entity.biz.DevDataRoyalstar;

public class DevDataFactory {
	

	public static DevData getDevData(int productType) {
		switch (productType) {
		case SDKConst.PRODUCT_AIRPAL_PL200:
		case SDKConst.PRODUCT_AIRPAL_PL500:
			return new DevDataAirpal();
		case SDKConst.PRODUCT_CAIR:
			return new DevDataCair();
		case SDKConst.PRODUCT_BL_SP2:
			return new DevDataBroadlink();
		case SDKConst.PRODUCT_GREEN_BLACK1:
		case SDKConst.PRODUCT_GREEN_BLACK2:
		case SDKConst.PRODUCT_GREEN_WHITE:
			return new DevDataGreen();
		case SDKConst.PRODUCT_ROYALSTAR:
			return new DevDataRoyalstar();
		default:
			break;
		}
		return null;
	}
	
	
}
