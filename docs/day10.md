> [!NOTE]
>
> ![image-20241223212855531](C:\Users\86131\AppData\Roaming\Typora\typora-user-images\image-20241223212855531.png)
>
> public static List parseArray(String text, Class clazz)方法是一个比较常用的方法，遇到问题的第一反应我没有怀疑jar包出现了问题，而是怀疑字符串里出现了某些特殊字符导致的出错，研究了好长一段时间，没有啥头绪最后看了数据之后发现是数据字段错误
>
> 
>
> ```
>     if (broadData != null || configMap.get(tableName) != null){
>             if (configMap.get(tableName).getSourceTable().equals(tableName)){
> //                System.err.println(jsonObject);
>                 if (!jsonObject.getString("op").equals("d")){
>                     JSONObject after = jsonObject.getJSONObject("after");
>                     String sinkTableName = configMap.get(tableName).getSinkTable();
>                     sinkTableName = "realtime_v1:"+sinkTableName;
>                     String hbaseRowKey = after.getString(configMap.get(tableName).getSinkRowKey());
>                     Table hbaseConnectionTable = hbaseConnection.getTable(TableName.valueOf(sinkTableName));
>                     Put put = new Put(Bytes.toBytes(hbaseRowKey));
>                     for (Map.Entry<String, Object> entry : after.entrySet()) {
>                         put.addColumn(Bytes.toBytes("info"),Bytes.toBytes(entry.getKey()),Bytes.toBytes(String.valueOf(entry.getValue())));
>                     }
>                     hbaseConnectionTable.put(put);
>                     System.err.println("put -> "+put.toJSON()+" "+ Arrays.toString(put.getRow()));
>                 }
>             }
>         }
> ```
>
> 往hbase添加数据,在这里采用了bytes,因为hbase底层是由bytes构成,采用bytes方式优化代码可以保持数据不会丢失

