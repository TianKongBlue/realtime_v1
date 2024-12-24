192.168.10.130 cdh01

192.168.10.131 cdh02

192.168.10.129 cdh03

scp /etc/profile cdh02:/etc/

scp /etc/profile cdh03:/etc/

.ssh无法配置免密:报错如下

![image-20241219085434111](C:\Users\86131\AppData\Roaming\Typora\typora-user-images\image-20241219085434111.png)

原因是因为没有设置/etc/hosts 没有配置映射

在配置CDH中 在/opt/cloudera/parcel-cacheCDH的文件重命名错误

hdfs纠删码

设置dfs.namenode.ec.system.default.policy为no
