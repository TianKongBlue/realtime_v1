![image-20241220110057549](C:\Users\86131\AppData\Roaming\Typora\typora-user-images\image-20241220110057549.png)

解决方法删除本地仓库里面的依赖

在E:\apache-maven-3.8.3\repository

```
Caused by: com.github.shyiko.mysql.binlog.network.ServerException: Binary log is not open
	at com.github.shyiko.mysql.binlog.BinaryLogClient.listenForEventPackets(BinaryLogClient.java:1043)
	... 3 more
```

big-log日志检测不到

server-id=1
log-bin=mysql-bin
binlog_format=row
binlog-do-db=gmall

JAVA中new一个对象存在堆内存中(heap)引用栈内存(stack)

寄存器是将数据暂存到内存当中->向量化(基于内存)运行效率快例如doris,clickhouse

