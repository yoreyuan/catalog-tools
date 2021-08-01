
# 1 catalog-core
可用于导出 Phoenix、HBase、Hive、Impala、ClickHouse、MySQL 的表结构，
也可用于解析指定路径（本地/hdfs）上的 HFile 文件。

导出的表结构可以指定为单行，也可以格式化为多行。

## 2 使用

```shell script
#打包项目
mvn clean package -DskipTests
ll catalog-core/target
tar -zxf catalog-core-1.0.3-bin.tar.gz
cd catalog-core-1.0.3

# 1 帮助信息
bin/catalog-tools help

# 2 查看当前项目版本
bin/catalog-tools version

# 3 查看示例
bin/catalog-tools demo

# 4 查看某个命令的运行状态
# 目前支持： phoenix、hfile、hbase、、、、
bin/catalog-tools phoenix status

# 5 手动关闭指定服务
bin/catalog-tools phoenix stop

```

解压后的目录说明如下：
项目部署包目录说明
```
├── bin                                     # 脚本所在目录
│   └── catalog-tools                       # catalog-tools脚本
├── conf
│   ├── application-clickhouse.properties   #应用访问 ClickHouse 的相关配置
│   ├── application-hive.properties         #应用访问 Hive 的相关配置
│   ├── application-impala.properties       #应用访问 Impala 的相关配置
│   ├── application-mysql.properties        #应用访问 MySQL 的相关配置
│   ├── application-phoenix.properties      #应用访问 Phoenix 的相关配置
│   ├── application.properties              #应用主配置
│   ├── core-site.xml                       #需替换为自己Hadoop的的配置
│   ├── hbase-site.xml                      #需替换为自己HBase的的配置
│   ├── hdfs-site.xml                       #需替换为自己Hadoop的的配置
│   ├── HFilePath.properties                #HFile相关配置
│   └── phoenix
├── lib                                     #项目jar包库目录
 ……
├── logs                                    # 项目日志目录
└── pid                                     # PID
6 directories, 293 files
```


6.1 备份 Phoenix 所有表或视图到本地的 `/home/yore/Phoenix-view-DDL-2021-07-03.sql`
```
bin/catalog-tools phoenix /home/yore/Phoenix-view-DDL-2021-07-03.sql
```

6.2 仅备份 Phoenix 指定的 T_M_CUS_HUS_TEST 和 "ods_liu_customunicont" 表或视图结构到本地的 `/home/yore/Phoenix-view-DDL-2021-07-03-part.sql`
```
bin/catalog-tools phoenix /home/yore/Phoenix-view-DDL-2021-07-03-part.sql table1 table2 view1 view2 ...
```

7 hfile 数据解析（目前仅支持解析为 JSON）
首先配置 `conf/HFilePath.properties`（前提 conf 下已经放置了最新的 HBase 的配置 `hbase-site.xml`，及 hdfs 相关的配置），
根据注释进行调整（例如 `file.model=hdfs`，`out.print.type=string`）。
hfile 路径目前支持本地路径和 HDFS 路径
```
bin/catalog-tools hfile /home/yore/hfile-kv-2021-07-03.data \
/apps/hbase/data/data/SYSTEM/CATALOG/d1dffe0be9a37d9734deead8367ce8a5/0/9fe264edcae946818e925d381638bae8 \
/home/yore/default/LIU_MASTER_TMP/154fa67830103ce4d14471d8e5d5e48e/info/dffc6865b268405a807e80350b528914
```

8 hbase表结构备份可通过类似如下
```
bin/catalog-tools hbase /home/yore/hbase-table-DDL-2021-07-03.txt
bin/catalog-tools hbase /home/yore/hbase-table-DDL-2021-07-03-part.txt table1 table2 ...
```


9 hive 表结构备份
```
bin/catalog-tools hive /home/yore/hive-table-DDL-2021-07-03.sql
bin/catalog-tools hive /home/yore/hive-table-DDL-2021-07-03-part01.sql database
bin/catalog-tools hive /home/yore/hive-table-DDL-2021-07-03-part02.sql database table1 table2 ...
```


10 impala 表结构备份
```
bin/catalog-tools impala /home/yore/impala-table-DDL-2021-07-03.sql
bin/catalog-tools impala /home/yore/impala-table-DDL-2021-07-03-part01.sql database
bin/catalog-tools impala /home/yore/impala-table-DDL-2021-07-03-part02.sql database table1 table2 ...
```

11 Kudu
采用 10 方式

12 clickhouse
```
bin/catalog-tools clickhouse /home/yore/clickhouse-table-DDL-2021-07-03.sql
bin/catalog-tools clickhouse /home/yore/clickhouse-table-DDL-2021-07-03-part01.sql database
bin/catalog-tools clickhouse /home/yore/clickhouse-table-DDL-2021-07-03-part02.sql database table1 table2 ...
```

13 mysql
```
bin/catalog-tools mysql /home/yore/mysql-table-DDL-2021-07-03.sql
bin/catalog-tools mysql /home/yore/mysql-table-DDL-2021-07-03-part01.sql database
bin/catalog-tools mysql /home/yore/mysql-table-DDL-2021-07-03-part02.sql database table1 table2 ...
```

