package com.fun.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class Server {

	public static void main(String[] args) throws Exception {
		UserService.Iface iface = new UserService.Iface() {
			@Override
			public String add(String u) throws TException {
				System.out.printf("server:%s\r\n", u);
				return "hello, " + u;
			}
		};
		UserService.Processor<UserService.Iface> processor = new UserService.Processor<UserService.Iface>(iface);
		TServerTransport serverTransport = new TServerSocket(9002);
		TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
		server.serve();
	}
}
