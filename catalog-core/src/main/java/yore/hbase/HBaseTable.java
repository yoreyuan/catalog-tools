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

import org.apache.hadoop.hbase.TableName;
import yore.common.RuntimeAnnotation;
import yore.common.RuntimeAspect;

import java.util.ArrayList;
import java.util.List;

/**
 * HBase 表结构备份
 *
 * @author Yore Yuen
 */
public class HBaseTable extends yore.common.FileWriter {

    /**
     *
     * @param args 入参
     *        args[0] outpath，备份的本地路径
     *        args[1] hfile path(local or hdfs)
     */
    public static void main(String[] args) throws Exception {
        initWriter(args[0]);
        RuntimeAspect.printSpend(HBaseTable.class, args);
        closeWriter();
    }


    @RuntimeAnnotation(descr = "HBase")
    public static void start(String[] args) throws Exception {
        HbaseUtil hbaseUtil = HbaseUtil.getInstance();
        List<String> createCmdList = new ArrayList<>();

        if (args.length > 1) {
            List<TableName> tableNames = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                tableNames.add(TableName.valueOf(args[i]));
            }
            createCmdList = hbaseUtil.getHBaseCreateCmd(tableNames);
        } else {
            createCmdList = hbaseUtil.getHBaseCreateCmd(null);
        }

        for (String createCmd : createCmdList) {
            writer.write(createCmd + "\n");
            writer.flush();
        }
    }

}
