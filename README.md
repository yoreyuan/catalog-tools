
# 1 catalog-core
It can be used to export the table structure of Phoenix, HBase, Hive, Impala, Kudu, ClickHouse, MySQL,
It can also be used to parse HFile files on the specified path (local/hdfs) to json.

The exported table structure can be specified as a single row or formatted as multiple rows.
```
##Output statement format: row (single line), multiline (multiple lines)
#catalog.ddl.output.format=multiline
catalog.ddl.output.format=row
```

# 2 Packaging and configuration
```
mvn clean package -DskipTests
ll catalog-core/target
tar -zxf catalog-core-1.2.0-bin.tar.gz
cd catalog-core-1.2.0
```

The decompressed directory description is as follows, 
the project deployment package directory description
```
├── bin                                     # Script directory
│   └── catalog-tools                       # catalog-tools Script
├── conf
│   ├── application-clickhouse.properties   # Configure access to ClickHouse
│   ├── application-hive.properties         # Configure access to Hive
│   ├── application-impala.properties       # Configure access to Impala
│   ├── application-mysql.properties        # Configure access to MySQL
│   ├── application-phoenix.properties      # Configure access to Phoenix
│   ├── application.properties              # App configuration
│   ├── core-site.xml                       #Hadoop configuration
│   ├── hbase-site.xml                      #HBase configuration
│   ├── hdfs-site.xml                       #HDFS configuration
│   ├── hfile-config.properties             # HFile configuration
│   ├── kudu-config.properties              # Kudu configuration
│   └── phoenix
├── lib                                     #library directory
 ……
├── logs                                    #log directory
└── pid                                     #PID
6 directories, 293 files
```

# 3 Usage
```
# 1 Help prompt information
bin/catalog-tools help

# 2 View current project version
bin/catalog-tools version

# 3 View example
bin/catalog-tools demo

# 4 Encrypted string
bin/catalog-tools enc 123456

# 5 View the running status of a command
# Currently supported: phoenix, hfile, hbase, hive, impala, kudu, clickhouse, mysql
bin/catalog-tools phoenix status

# 6 Manually close the specified service
bin/catalog-tools phoenix stop
```

Before performing the following operations, you need to configure the relevant 
configuration items in `conf/application.properties`.

If you need to access HBase, you need to put `hbase-site.xml` and hdfs-related 
configurations (`core-site.xml`, `hdfs-site.xml`) under `conf/`.


## 3.1 Export HFile data
Currently only supports output as json.
Need to configure `conf/hfile-config.properties` (for example, `file.model=hdfs`, `out.print.type=string`)
```
# Output to out/hfile-kv-2021-08-20.json
bin/catalog-tools hfile out/hfile-kv-2021-08-20.json \
/apps/hbase/data/data/SYSTEM/CATALOG/d1dffe0be9a37d9734deead8367ce8a5/0/9d600b80522d4f82b976c55f72adbf0f ...
```

## 3.2 Export Phoenix table or view
Need to configure `conf/application-phoenix.properties`
```
# Output the specified table or view
bin/catalog-tools phoenix out/phoenix-DDL-2021-08-20-part.sql table_name1 table_name2 view_name1 ...

# Output all tables or views
bin/catalog-tools phoenix out/phoenix-DDL-2021-08-20.sql

# View status
bin/catalog-tools phoenix status

# View log
tail -f logs/tools-phoenix-localhost.out
```

## 3.3 Export HBase table
```
# Output the specified table
bin/catalog-tools hbase out/hbase-table-DDL-2021-08-20-part.txt table_name1 table_name2 ...

# Output all tables 
bin/catalog-tools hbase out/hbase-table-DDL-2021-08-20.txt

# View status
bin/catalog-tools hbase status

# View log
tail -f logs/tools-hbase-localhost.out
```

## 3.4 Export Hive Table
```
# Output the specified table
bin/catalog-tools hive out/hive-table-DDL-2021-08-20-part02.sql db_name table_name1 table_name2 ...

# Output all tables in the specified database
bin/catalog-tools hive out/hive-table-DDL-2021-08-20-part01.sql db_name

# Output all tables
bin/catalog-tools hive out/hive-table-DDL-2021-08-20.sql

# iew status
bin/catalog-tools hive status

# View log
tail -f logs/tools-hive-localhost.out
```

## 3.5 Export Impala table
```
# Output the specified table
bin/catalog-tools impala out/impala-table-DDL-2021-08-20-part02.sql db_name table_name1 table_name2 ...

# Output all tables in the specified database
bin/catalog-tools impala out/impala-table-DDL-2021-08-20-part01.sql db_name

# Output all tables
bin/catalog-tools impala out/impala-table-DDL-2021-08-20.sql

# iew status
bin/catalog-tools impala status

# View log
tail -f logs/tools-impala-localhost.out
```

## 3.6 Export Kudu table
Need to configure `conf/kudu-config.properties` (such as `kudu.master`, `catalog.level`, and optional filter rules)

The default value of the configuration item `catalog.level` is list. 
For Kudu in the environment, `kudu table create <master_addresses> <create_table_json>` is not supported. 
It is recommended to use Impala to export the table structure.

**Note**: When a specific table name is specified, the filter rules will be automatically ignored.

* When `catalog.level=list`, or the default value
```
# Output the specified table name
bin/catalog-tools kudu out/kudu-table-list-2021-08-20-part.txt table_name1 table_name2 ...

# Output all tables name
bin/catalog-tools kudu out/kudu-table-list-2021-08-20.txt
```

* When `catalog.level=ddl`, or the default value
```
# Output the specified table
bin/catalog-tools kudu out/kudu-table-DDL-2021-08-20-part.json table_name1 table_name2 ...

# Output all tables
bin/catalog-tools kudu out/kudu-table-DDL-2021-08-20.json
```

## 3.7 Export ClickHouse table
```
# Output the specified table
bin/catalog-tools clickhouse out/clickhouse-table-DDL-2021-08-20-part02.sql db_name table_name1 table_name2 ...

# Output all tables in the specified database
bin/catalog-tools clickhouse out/clickhouse-table-DDL-2021-08-20-part01.sql db_name

# Output all tables
bin/catalog-tools clickhouse out/clickhouse-table-DDL-2021-08-20.sql

# iew status
bin/catalog-tools clickhouse status

# View log
tail -f logs/tools-clickhouse-localhost.out
```


## 3.8 Export MySQL table
```
# Output the specified table
bin/catalog-tools mysql out/mysql-table-DDL-2021-08-20-part02.sql db_name table_name1 table_name2 ...

# Output all tables in the specified database
bin/catalog-tools mysql out/mysql-table-DDL-2021-08-20-part01.sql db_name

# Output all tables
bin/catalog-tools mysql out/mysql-table-DDL-2021-08-20.sql

# iew status
bin/catalog-tools mysql status

# View log
tail -f logs/tools-mysql-localhost.out
```
