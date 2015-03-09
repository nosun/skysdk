package com.skyware.sdk.entity;

public  abstract  class DevData implements IJsonEncoder, IMCUCoder<Object>{
	
	/** 有效data字段的数量 */
	protected int dataCount;
	
	public DevData() {}
	public DevData(DevData cpy) {
		this.dataCount = cpy.dataCount;
//		this.power = cpy.power;
		//TODO 拷贝构造函数
	}
	
	public int getDataCount() {
		return this.dataCount;
	}
	
	public abstract void setPower(String power);
	public abstract String getPower();
	
	@Override
	public Object mcuCoder() {
		return null;
	}
	
	@Override
	public boolean mcuDecoder(Object mcuData) {
		return false;
	}
}