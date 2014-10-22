package com.skyware.sdk.entity;


public class BroadlinkDevData extends DevData implements IMCUCoder<Byte[]>{

	public BroadlinkDevData() {}
	public BroadlinkDevData(DevData data) {
		super(data);
	}
	
	@Override
	public Byte[] mcuCoder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean mcuDecoder(Byte[] mcuData) {
		// TODO Auto-generated method stub
		return false;
	}

}
