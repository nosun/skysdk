package com.skyware.sdk.entity;

/**
 *	字节（二进制）编码接口
 *
 *	@author wangyf 2014-10-20
 */
public interface IByteEncoder {
	
	/** 
	 * 将自身所有属性（非空）编码封装成byte数组
	 * 
	 * @return 填充满的byte数组
	 */
	public byte[] byteEncoder();
}
