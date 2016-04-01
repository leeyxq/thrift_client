# thrift_client
thrift client for java

一、测试硬件环境：

	client： 192.168.113.24	8核 192.168.113.118 24核
	server： 192.168.113.26	8核
		
二、软件环境

	nodejs：5.6.0
	coffee-script
	thrift：0.9.3
	eclipse
	java：1.6+
	R语言
	
三、测试目的

        nodejs在高并发长链接请求下，“rr”策略和“none”策略两者work负载均衡对比（cluster集群调度策略详细介绍：https://strongloop.com/strongblog/whats-new-in-node-js-v0-12-cluster-round-robin-load-balancing/）

四、测试步骤：

	1.开启server端（node实现）
		npm install -d
		//参数一：日志文件，参数二：是否控制台打印（false不打印，true打印）， 参数三：集群策略（rr，none）
		coffee servers.coffee servers.log false none
		
	2.运行client端（java实现）
	
		//参数一：10为并发请求线程数，参数二：10000为每个请求线程请求数，参数三：thrift server主机，参数四：thrift server主机端口
		java cp ./client-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.fun.thrift.client.Client 10 10000 192.168.113.26 9002
	3.使用R语言统计
	
	主要实现思路：java客户端发请求－>node服务端中work各自打印自己所处理的记录－>使用R语言中summary函数对node中work日志进行统计
	安装R并进入R命令行模式
	df = read.table(file="servers.log", skip=8, header=FALSE)
	summary(df$V1)
	
五、测试结论

	1.client并发请求数跟server集群中work数一致或相差不大时，node集群策略“rr”模式和“none” work负载情况差别不大
	2.client并发请求数远大于server集群中work数时，node集群策略“rr”模式好与“none”模式
	
六、测试统计数据

	一组：
	服务器：集群模式“rr”＋8个work
	客户端：并发请求线程数：80，每个并发请求线程请求10000次，2个客户端同时执行请求
	work-30921 work-30926 work-30933 work-30939 work-30944 work-30949 work-30954 work-30959
	    250000     150000     240000     160000     120000     240000     220000     220000

	work-31007 work-31012 work-31017 work-31018 work-31023 work-31028 work-31033 work-31038 
	    250000     200000     190000     220000     150000     210000     160000     220000 
	    
	work-31079 work-31084 work-31089 work-31090 work-31095 work-31100 work-31105 work-31110 
	    240000     180000     190000     160000     150000     250000     190000     240000

	二组：
	服务器：集群模式“none”＋8个work
	客户端：并发请求线程数：80，每个并发请求线程请求10000次，2个客户端同时执行请求
	work-30792 work-30797 work-30802 work-30803 work-30808 work-30813 work-30818 work-30823
	    270000     250000     290000      20000     190000     200000     310000 	  70000

	work-31243 work-31248 work-31253 work-31254 work-31259 work-31264 work-31269 work-31274 
	    170000     170000     160000     150000     150000     220000     410000     170000

	work-31157 work-31162 work-31167 work-31168 work-31173 work-31178 work-31183 work-31188
	    250000     220000      10000     520000      10000     250000      20000 320000
