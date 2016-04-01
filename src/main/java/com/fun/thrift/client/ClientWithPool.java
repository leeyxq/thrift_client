package com.fun.thrift.client;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fun.thrift.client.pool.ThriftPool;
import com.fun.thrift.client.pool.ThriftPoolConfig;
import com.google.common.base.Stopwatch;

public class ClientWithPool {
	static ThriftPool pool = null;
	private static Logger logger = LoggerFactory.getLogger(ClientWithPool.class);

	public static void main(String[] args) throws Exception {

		int nThreads = Runtime.getRuntime().availableProcessors();
		long times = 1000;
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

		final String host = _host;
		final int port = _port;
		final long avg_iterator = times;
		ExecutorService pools = Executors.newFixedThreadPool(nThreads, new ThreadFactory(){
			final AtomicInteger threadNumber = new AtomicInteger(1);
			final String namePrefix = "thrift-client-pool-";
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, namePrefix+threadNumber.getAndIncrement());
			}});
		final CyclicBarrier barrier = new CyclicBarrier(nThreads + 1);
		Stopwatch watch = new Stopwatch();
		//final AtomicLong iterator = new AtomicLong(times);
		initThriftPool(host, port, 512, nThreads);
		for (int i = 0; i < nThreads; i++) {
			pools.execute(new Runnable() {
				@Override
				public void run() {
					try {
						barrier.await();
						long i = avg_iterator;
						//do work 
						while (i-- > 0) {
							UserService.Client client = pool.getResource();
							client.add(String.format("%s %d", Thread.currentThread().getName(), i));
							pool.returnResource(client);
						}
						barrier.await();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		barrier.await();
		barrier.reset();
		//计时开始 
		watch.start();
		barrier.await();
		pool.destory();
		logger.info("并发数{}，共请求{}次，耗时{}毫秒", nThreads, times, watch.elapsedTime(TimeUnit.MILLISECONDS));
		pools.shutdown();

	}

	private static void initThriftPool(String host, int port, int maxTotal, int nThreads) {
		ThriftPoolConfig config = new ThriftPoolConfig();
		config.setPORT(port);
		config.setHOSTNAME(host);
		config.setMaxTotal(maxTotal);
		config.setTestOnBorrow(true);
		config.setMaxIdle(nThreads);//最大空闲数
		config.setMinIdle(nThreads);//最小空闲数
		config.setLifo(false);
		pool = new ThriftPool(config);
	}

}
