package com.skyware.sdk.entity;


/**
 *	字节（二进制）解码接口
 *
 *	@author wangyf 2014-10-20
 */
public interface IByteDecoder {
	/**  
	 * 从字节数组中解码并赋值给自身
	 * 
	 * @param byteFillArray		填充满的byte数组
	 * @return boolean	是否解码成功
	 */
	public boolean byteDecoder(byte[] byteFillArray);
}
