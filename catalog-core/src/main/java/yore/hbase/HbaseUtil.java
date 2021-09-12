/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yore.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yore Yuan
 */
public class HbaseUtil {

    private Configuration conf = null;
    private Connection connection = null;
    // 命名空间与管理表DDL
    private Admin admin = null;

    private HbaseUtil() {
        conf = InitHbaseEnv();
        try {
            UserGroupInformation userGroupInformation = UserGroupInformation.createRemoteUser("hbase");
            connection = ConnectionFactory.createConnection(conf, User.create(userGroupInformation));
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单例模式，获取HBase实例
     */
    public static HbaseUtil getInstance() {
        return new HbaseUtil();
    }

    private Configuration InitHbaseEnv() {
        conf = HBaseConfiguration.create();
//        conf.set("hbase.zookeeper.quorum", "bdd2,bdd3,bdd7,bdd8,bdm0");//zookeeper地址
//        conf.set("hbase.zookeeper.quorum", "bdm0,bdm1,etl1,es1,es2");
//        conf.set("hbase.rpc.timeout", "1800000");
//        conf.set("hbase.client.scanner.timeout.period", "1800000");
//        conf.set("hadoop.user.name", "hbase");
        return conf;
    }


    /**
     * 连关闭接
     */
    public  void close() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (admin != null) {
                admin.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取 HBase 中所有表名
     * @return List
     */
    public List<String> list() {
        List<String> result = new ArrayList<>();
        try {
            TableName[] tableNames = admin.listTableNames();
            for (TableName tableName : tableNames) {
                result.add(tableName.getNameAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public List<TableName> listTableName() {
        List<TableName> result = new ArrayList<>();
        try {
            TableName[] tableNames = admin.listTableNames();
            result.addAll(Arrays.asList(tableNames));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取 HBase 建表命令
     * @param tableNames TableName集合，当为 null 时默认查询所有
     * @return HBase create cmd
     */
    public List<String> getHBaseCreateCmd(List<TableName> tableNames) {
        List<String> result = new ArrayList<>();
        //tableNames.add(TableName.valueOf("T_D_ORG"));
        try {
            List<TableDescriptor> tableDescriptors = tableNames == null?
                    admin.listTableDescriptors():
                    admin.listTableDescriptors(tableNames);
            for (TableDescriptor tableDescriptor : tableDescriptors) {
                String tableName = tableDescriptor.getTableName().getNameAsString();
                StringBuilder cf_sb = new StringBuilder();
                ColumnFamilyDescriptor[] cfs = tableDescriptor.getColumnFamilies();
                for (ColumnFamilyDescriptor cfDesc : cfs) {
                    //cf_sb.append(",").append(cfDesc.toStringCustomizedValues());  //
                    //cf_sb.append(",").append(cfDesc.getNameAsString());     // info
                    cf_sb.append(",").append(cfDesc.toString());     // info
                }
                //System.out.println("create " + tableDescriptor.toString());
                //System.out.println(Arrays.toString(tableDescriptor.getColumnFamilies()));
                String createTemp = "create '{1}'{2}"
                        .replace("{1}", tableName)
                        .replace("{2}", cf_sb.toString()
                                .replaceAll("\\s", ""));
                result.add(createTemp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
