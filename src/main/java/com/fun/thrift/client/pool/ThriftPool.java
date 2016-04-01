package com.fun.thrift.client.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.fun.thrift.client.UserService;

public class ThriftPool {
	private final GenericObjectPool<UserService.Client> pool;
	private final ThriftPooledObjectFacotry factory;

	public ThriftPool(ThriftPoolConfig config) {
		factory = new ThriftPooledObjectFacotry(config);
		this.pool = new GenericObjectPool<UserService.Client>(factory, config);
	}

	public UserService.Client getResource() throws Exception {
		return this.pool.borrowObject();
	}

	public void returnResource(UserService.Client client) {
		this.pool.returnObject(client);
	}

	public void destory() {
		this.pool.close();
		this.factory.destory();
	}
}
