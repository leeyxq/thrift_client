package com.fun.thrift.client.pool;

import java.net.ConnectException;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fun.thrift.client.UserService;

public class ThriftPooledObjectFacotry extends BasePooledObjectFactory<UserService.Client> {

	private String HOSTNAME;
	private int PORT;
	private Logger logger = LoggerFactory.getLogger(ThriftPooledObjectFacotry.class);
	private boolean isDetory = false;

	public ThriftPooledObjectFacotry(ThriftPoolConfig config) {
		this.HOSTNAME = config.getHOSTNAME();
		this.PORT = config.getPORT();
	}

	@Override
	public UserService.Client create() throws ConnectException {
		try {
			TTransport transport2 = new TSocket(HOSTNAME, PORT);
			transport2.open();
			TProtocol protocol2 = new TBinaryProtocol(transport2);
			UserService.Client client2 = new UserService.Client(protocol2);
			logger.debug("新建thrift连接");
			return client2;
		} catch (Exception e) {
			throw new ConnectException("连接[" + HOSTNAME + ":" + PORT + "]失败,请检查网络配置");
		}
	}

	@Override
	public PooledObject<UserService.Client> wrap(UserService.Client client) {
		return new DefaultPooledObject<UserService.Client>(client);
	}

	@Override
	public boolean validateObject(PooledObject<UserService.Client> p) {
		return p.getObject().getOutputProtocol().getTransport().isOpen();
	}

	@Override
	public void destroyObject(PooledObject<UserService.Client> p) {
		try {
			p.getObject().getInputProtocol().getTransport().close();
			p.getObject().getOutputProtocol().getTransport().close();
		} catch (Exception e) {
			logger.error("对象销毁失败：" + e.getMessage());
		}
		logger.debug("关闭thrift连接");
	}

	public synchronized void destory() {
		if (!isDetory) {
			try {
			} catch (Exception e2) {
			}
		}
	}
}