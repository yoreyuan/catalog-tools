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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.io.hfile.HFile;
import org.apache.hadoop.hbase.io.hfile.HFileScanner;
import org.apache.hadoop.hbase.util.Bytes;
import yore.common.RuntimeAnnotation;
import yore.common.RuntimeAspect;
import yore.common.util.SysProperties;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * HFile 解析工具
 *
 * @author Yore Yuan
 */
public class HfileReader extends yore.common.FileWriter {

    /**
     *
     * 参见：
     * <a href='https://gitee.com/yoreyuan/hbase-release/blob/HDP-3.1.0.0-78-tag/hbase-server/src/main/java/org/apache/hadoop/hbase/io/hfile/HFilePrettyPrinter.java'>
     *   org.apache.hadoop.hbase.io.hfile.HFilePrettyPrinter
     * </a>
     * @param args 入参
     *        args[0] outpath，输出的本地路径
     *        args[1] hfile path(local or hdfs)
     */
    public static void main(String[] args) {
        initWriter(args[0]);
        RuntimeAspect.printSpend(HfileReader.class, args);
    }


    @RuntimeAnnotation(descr = "HFile")
    public static void start(String[] args) throws Exception {
        Set<String> hfilePathSet = new HashSet<>();
        Properties properties = SysProperties.getComponeProperties("hfile-config");
        final String fileModel = properties.getProperty("file.model");
        final String outPrintType = properties.getProperty("out.print.type");

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                hfilePathSet.add(args[i]);
            }
        } else {
            String hfilesPath = properties.getProperty("hfiles.path").trim();
            for (String s : hfilesPath.split("\\s")) {
                if (StringUtils.isNotBlank(s)) {
                    hfilePathSet.add(s.trim());
                }
            }
        }

        try {
            Configuration configuration = HBaseConfiguration.create();
            FileSystem fileSystem = "hdfs".equalsIgnoreCase(fileModel)?
                    FileSystem.get(configuration) :
                    FileSystem.getLocal(configuration);
            //LOG.info("将解析{}个 hfile文件{}", hfilePathSet.size(), JSON.toJSON(hfilePathSet));
            System.out.printf("将解析%d个hfile文件:%s", hfilePathSet.size(), JSON.toJSON(hfilePathSet));
            for (String hfilePathStr : hfilePathSet) {
                HFile.Reader hReader = HFile.createReader(fileSystem, new Path(hfilePathStr), configuration);
                HFileScanner scanner = hReader.getScanner(true, true);
                boolean b = scanner.seekTo();
                while (scanner.next()) {
                    Cell cell = scanner.getCell();
                    byte[] rowBytes = cell.getRowArray(),
                            cfBytes = cell.getFamilyArray(),
                            qualifierBytes = cell.getQualifierArray(),
                            valueBytes = cell.getValueArray();
                    JSONObject json = new JSONObject();
                    if ("byte".equalsIgnoreCase(outPrintType)) {
                        json.put("rowkey", Bytes.toStringBinary(rowBytes, cell.getRowOffset(), cell.getRowLength()));
                        json.put("cf", Bytes.toStringBinary(cfBytes, cell.getFamilyOffset(), cell.getFamilyLength()));
                        json.put("K", Bytes.toStringBinary(qualifierBytes, cell.getQualifierOffset(), cell.getQualifierLength()));
                        json.put("V", Bytes.toStringBinary(valueBytes, cell.getValueOffset(), cell.getValueLength()));
                    } else if ("string".equalsIgnoreCase(outPrintType)) {
                        json.put("rowkey", new String(rowBytes, cell.getRowOffset(), cell.getRowLength()));
                        json.put("cf", new String(cfBytes, cell.getFamilyOffset(), cell.getFamilyLength()));
                        json.put("K", new String(qualifierBytes, cell.getQualifierOffset(), cell.getQualifierLength()));
                        json.put("V", new String(valueBytes, cell.getValueOffset(), cell.getValueLength()));
                    }
                    writer.write(json.toJSONString() + "\n");
                    writer.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeWriter();
        }
    }

}
