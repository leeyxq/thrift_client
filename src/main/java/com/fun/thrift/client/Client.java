package com.fun.thrift.client;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

public class Client {
	private static Logger logger = LoggerFactory.getLogger(Client.class);

	public static void main(String[] args) throws Exception {

		int nThreads = Runtime.getRuntime().availableProcessors();
		long times = 10;
		String _host = "localhost";
		int _port = 9002;
		if (args.length == 2) {
			nThreads = Integer.parseInt(args[0]);
			times = Long.parseLong(args[1]);
		}
		if (args.length == 4) {
			nThreads = Integer.parseInt(args[0]);
			times = Long.parseLong(args[1]);
			_host = args[2];
			_port = Integer.parseInt(args[3]);
		}
		logger.info("thread:{}, times:{}, host:{}, port:{}", nThreads, times, _host, _port);
		final String host = _host;
		final int port = _port;
		final long avg_iterator = times;
		ExecutorService pools = Executors.newFixedThreadPool(nThreads);
		final CyclicBarrier barrier = new CyclicBarrier(nThreads + 1);
		Stopwatch watch = new Stopwatch();
		//final AtomicLong iterator = new AtomicLong(times);
		for (int i = 0; i < nThreads; i++) {
			pools.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String threadName = Thread.currentThread().getName();
						barrier.await();
						TTransport transport2 = new TSocket(host, port);
						transport2.open();
						logger.info("{}新建thrift连接...", threadName);
						TProtocol protocol2 = new TBinaryProtocol(transport2);
						UserService.Client client2 = new UserService.Client(protocol2);
						long i = avg_iterator;
						//do work 
						while (i-- > 0) {
							client2.add(String.format("%s %d", threadName, i));
						}
						barrier.await();
						client2.getInputProtocol().getTransport().close();
						client2.getOutputProtocol().getTransport().close();
						logger.info("{}关闭thrift连接...", threadName);
					} catch (Exception e) {
						logger.error("work线程运行异常，程序退出,err:" + e.getMessage(), e);
						Runtime.getRuntime().exit(0);
					}
				}
			});
		}
		barrier.await();
		barrier.reset();
		//计时开始 
		watch.start();
		barrier.await();
		logger.info("并发数{}，共请求{}次，耗时{}毫秒", nThreads, times * nThreads, watch.elapsedTime(TimeUnit.MILLISECONDS));
		pools.shutdown();

	}

}
