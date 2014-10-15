package com.skyware.sdk.entity;



/** 
 * @Description MCU指令的编码解码接口
 * @author wyf (zgtjwyftc@gmail.com)
 * @date 2014-8-25 下午12:04:10 
 *  
 * @Tips 利用泛型适配各种类型的MCU数据类型，可能是JsonArray，也有可能是二进制串 Byte[]
 */
public interface IMCUCoder <MCUDataType>{

	/** 
	 * @Description 将Map的K,V 编码成 MCUDataType
	 * @param  Map<String, Object>
	 * @return MCUDataType
	 */
	public MCUDataType mcuCoder();

	/** 
	 * @Description 将MCU传来的data进行解析，赋给Map返回
	 * @param mcuData
	 * @return Map<String, Object>
	 */
	public boolean mcuDecoder(MCUDataType mcuData);
}
