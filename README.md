# thrift_client
thrift client for java
一、测试硬件环境：
	client：
		192.168.113.24	8核
		192.168.113.118	24核
	server：
		192.168.113.26	8核
二、软件环境
		nodejs：	5.6.0
		coffee-script
		thrift：	0.9.3
    	eclipse+j2ee
	java	1.6+
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
PastedGraphic-1.png

二组：

	服务器：集群模式“none”＋8个work
	客户端：并发请求线程数：80，每个并发请求线程请求10000次，2个客户端同时执行请求
PastedGraphic-2.png
