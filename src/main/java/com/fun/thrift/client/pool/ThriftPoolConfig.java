package com.fun.thrift.client.pool;

public class ThriftPoolConfig extends org.apache.commons.pool2.impl.GenericObjectPoolConfig {
	private String HOSTNAME;
	private int PORT;

	public String getHOSTNAME() {
		return HOSTNAME;
	}

	public void setHOSTNAME(String hOSTNAME) {
		HOSTNAME = hOSTNAME;
	}

	public int getPORT() {
		return PORT;
	}

	public void setPORT(int pORT) {
		PORT = pORT;
	}

}
