
# 1 catalog-core
可用于导出 Phoenix、HBase、Hive、Impala、Kudu、ClickHouse、MySQL 的表结构，
也可用于解析指定路径（本地/hdfs）上的 HFile 文件为 json。

导出的表结构可以指定为单行，也可以格式化为多行（`conf/application.properties`）。
```
#输出的 DDL 语句格式: row(单行)、multiline(多行)
#catalog.ddl.output.format=multiline
catalog.ddl.output.format=row
```

# 2 打包及配置
```
#打包项目
mvn clean package -DskipTests
ll catalog-core/target
tar -zxf catalog-core-1.2.0-bin.tar.gz
cd catalog-core-1.2.0
```

解压后的目录说明如下，项目部署包目录说明
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
│   ├── hfile-config.properties             #HFile相关配置
│   ├── kudu-config.properties              #Kudu相关配置
│   └── phoenix
├── lib                                     #项目jar包库目录
 ……
├── logs                                    # 项目日志目录
└── pid                                     # PID
6 directories, 293 files
```



# 3 使用
```
# 1 帮助信息
bin/catalog-tools help

# 2 查看当前项目版本
bin/catalog-tools version

# 3 查看示例
bin/catalog-tools demo

# 4 加密字符串
catalog-tools enc 123456

# 5 查看某个命令的运行状态
# 目前支持： phoenix、hfile、hbase、hive、impala、kudu、clickhouse、mysql
bin/catalog-tools phoenix status

# 6 手动关闭指定服务
bin/catalog-tools phoenix stop
```

进行下面操作之前需先配置 `conf/application.properties`中相关配置项

如果需访问 HBase ，还需将 `hbase-site.xml`，及 hdfs 相关的配置（`core-site.xml`、`hdfs-site.xml`）放到  `conf/` 下。


## 3.1 输出 HFile 数据
目前仅支持输出为 json。
需配置 `conf/hfile-config.properties`（例如 `file.model=hdfs`，`out.print.type=string`）
```
# 输出到 out/hfile-kv-2021-08-20.json 中
bin/catalog-tools hfile out/hfile-kv-2021-08-20.json \
/apps/hbase/data/data/SYSTEM/CATALOG/d1dffe0be9a37d9734deead8367ce8a5/0/9d600b80522d4f82b976c55f72adbf0f ...
```

## 3.2 输出 Phoenix 表或视图
需配置 `conf/application-phoenix.properties`
```
# 输出指定表或视图
bin/catalog-tools phoenix out/phoenix-DDL-2021-08-20-part.sql table_name1 table_name2 view_name1 ...

# 输出所有表或视图
bin/catalog-tools phoenix out/phoenix-DDL-2021-08-20.sql

# 查看状态
bin/catalog-tools phoenix status

# 查看日志
tail -f logs/tools-phoenix-localhost.out
```

## 3.3 输出 HBase 表
```
# 输出指定表
bin/catalog-tools hbase out/hbase-table-DDL-2021-08-20-part.txt table_name1 table_name2 ...

# 输出所有表
bin/catalog-tools hbase out/hbase-table-DDL-2021-08-20.txt

# 查看状态
bin/catalog-tools hbase status

# 查看日志
tail -f logs/tools-hbase-localhost.out
```

## 3.4 输出 Hive 表
```
# 输出指定表
bin/catalog-tools hive out/hive-table-DDL-2021-08-20-part02.sql db_name table_name1 table_name2 ...

# 输出指定库下的所有表
bin/catalog-tools hive out/hive-table-DDL-2021-08-20-part01.sql db_name

# 输出所有表
bin/catalog-tools hive out/hive-table-DDL-2021-08-20.sql

# 查看状态
bin/catalog-tools hive status

# 查看日志
tail -f logs/tools-hive-localhost.out
```

## 3.5 输出 Impala 表
```
# 输出指定表
bin/catalog-tools impala out/impala-table-DDL-2021-08-20-part02.sql db_name table_name1 table_name2 ...

# 输出指定库下的所有表
bin/catalog-tools impala out/impala-table-DDL-2021-08-20-part01.sql db_name

# 输出所有表
bin/catalog-tools impala out/impala-table-DDL-2021-08-20.sql

# 查看状态
bin/catalog-tools impala status

# 查看日志
tail -f logs/tools-impala-localhost.out
```

## 3.6 输出 Kudu 表
需配置 `conf/kudu-config.properties`（例如 `kudu.master`，`catalog.level`，和可选的过滤规则）

配置项 `catalog.level` 默认值为 list，对于环境中的 Kudu 不支持 `kudu table create <master_addresses> <create_table_json>` 
命令的建议使用 Impala 方式导出表结构。

**注意**：当指定了具体的表名时，过滤规则将自动忽略。

* 当 `catalog.level=list`，或者默认值时
```
# 输出指定表名
bin/catalog-tools kudu out/kudu-table-list-2021-08-20-part.txt table_name1 table_name2 ...

# 输出所有表名
bin/catalog-tools kudu out/kudu-table-list-2021-08-20.txt
```

* 当 `catalog.level=ddl`，或者默认值时
```
# 输出指定表
bin/catalog-tools kudu out/kudu-table-DDL-2021-08-20-part.json table_name1 table_name2 ...

# 输出所有表
bin/catalog-tools kudu out/kudu-table-DDL-2021-08-20.json
```

## 3.7 输出 ClickHouse 表
```
# 输出指定表
bin/catalog-tools clickhouse out/clickhouse-table-DDL-2021-08-20-part02.sql db_name table_name1 table_name2 ...

# 输出指定库下的所有表
bin/catalog-tools clickhouse out/clickhouse-table-DDL-2021-08-20-part01.sql db_name

# 输出所有表
bin/catalog-tools clickhouse out/clickhouse-table-DDL-2021-08-20.sql

# 查看状态
bin/catalog-tools clickhouse status

# 查看日志
tail -f logs/tools-clickhouse-localhost.out
```


## 3.8 输出 MySQL 表
```
# 输出指定表
bin/catalog-tools mysql out/mysql-table-DDL-2021-08-20-part02.sql db_name table_name1 table_name2 ...

# 输出指定库下的所有表
bin/catalog-tools mysql out/mysql-table-DDL-2021-08-20-part01.sql db_name

# 输出所有表
bin/catalog-tools mysql out/mysql-table-DDL-2021-08-20.sql

# 查看状态
bin/catalog-tools mysql status

# 查看日志
tail -f logs/tools-mysql-localhost.out
```


