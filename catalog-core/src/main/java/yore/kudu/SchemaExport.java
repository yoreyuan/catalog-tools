package yore.kudu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.apache.kudu.ColumnSchema;
import org.apache.kudu.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yore.common.RuntimeAnnotation;
import yore.common.RuntimeAspect;
import yore.common.util.SysProperties;

import java.io.IOException;
import java.time.ZoneId;
import java.util.*;

/**
 * 在 Kudu 版本为 1.10.0 时
 * <p>不支持 kudu table create <master_addresses> <create_table_json> [-negotiation_timeout_ms=<ms>] [-timeout_ms=<ms>]
 * <p>所以尽管可以导出 Kudu 表结构为 json，但还是无法导入 Kudu
 * <p>此时建议通过 Impala 方式导出 Kudu 表
 *
 * @author Yore Yuan
 */
public class SchemaExport extends yore.common.FileWriter {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /**
     *
     * @param args 入参
     *        args[0] outpath，已通过脚本
     *        args[1] table name
     */
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.SHORT_IDS.get("CTT")));
        //args = new String[]{"/tmp/y/kudu-01.txt", "impala::impala_kudu_model.t_m_bus_tmp_ply"};
        initWriter(args[0]);
        RuntimeAspect.printSpend(SchemaExport.class, args);
        closeWriter();
    }


    @RuntimeAnnotation(descr = "Kudu")
    public void start(String[] args) {
        Properties properties = SysProperties.getComponeProperties("kudu-config");
        String kuduMaster = properties.getProperty("kudu.master");
        if (StringUtils.isBlank(kuduMaster)) {
            kuduMaster = "localhost:7051";
            LOG.info("Kudu Master 地址配置为空，已使用默认值 {}", kuduMaster);
        }
        KuduClient client = new KuduClient.KuduClientBuilder(kuduMaster).build();
        try {
            ListTablesResponse listTablesResponse = client.getTablesList();
            List<String> allTableNameList = listTablesResponse.getTablesList();
            boolean isIndicate = false;
            if (args.length > 1) {
                Set<String> indicateTableSet = new HashSet<>();
                for (int i = 1; i < args.length; i++) {
                    if (allTableNameList.contains(args[i])) {
                        indicateTableSet.add(args[i]);
                    } else {
                        LOG.error("指定的{}不存在，已自动跳过", args[i]);
                    }
                }
                if (indicateTableSet.isEmpty()) {
                    LOG.info("满足条件的表为空，已返回");
                    return;
                }
                allTableNameList = new ArrayList<>(indicateTableSet);
                isIndicate = true;
            }

            /* 指定了具体表名的优先级最高 */
            if (!isIndicate) {
                String filter1 = properties.getProperty("talbe.fileter.prefix");
                String filter2 = properties.getProperty("talbe.fileter.contains");
                boolean[] filterBlank = new boolean[]{
                        StringUtils.isBlank(filter1),
                        StringUtils.isBlank(filter2)
                };

                Set<String> filterTableSet = new HashSet<>();
                for (String tableName : allTableNameList) {
                    if (!filterBlank[0]) {
                        if (tableName.startsWith(filter1)) {
                            filterTableSet.add(tableName);
                        }
                    }
                    if (!filterBlank[1]) {
                        if (tableName.contains(filter2)) {
                            filterTableSet.add(tableName);
                        }
                    }
                }

                // 取差集
                allTableNameList.removeAll(filterTableSet);
            }

            String catalogLevel = properties.getProperty("catalog.level");
            if (StringUtils.isBlank(catalogLevel)) catalogLevel = "list";
            Properties sysProp = SysProperties.getSysProperties();
            String rowFormat = sysProp.getProperty("catalog.ddl.output.format");
            if (StringUtils.isEmpty(rowFormat)) {
                rowFormat = "row";
            }
            LOG.info("输出格式为：{}", rowFormat);
            boolean isRow = "row".equalsIgnoreCase(rowFormat);


            for (String tableName : allTableNameList) {
                if ("list".equals(catalogLevel)) {
                    writer.write(tableName + "\n");
                    continue;
                }

                JSONObject ddlJson = new JSONObject();
                KuduTable table = client.openTable(tableName);
                ddlJson.put("table_name", tableName);
                ddlJson.put("num_replicas", table.getNumReplicas());

                /* schema */
                JSONObject schema = new JSONObject();
                List<JSONObject> columnsList = new ArrayList<>();
                for (ColumnSchema column : table.getSchema().getColumns()) {
                    JSONObject json = new JSONObject();
                    json.put("desired_block_size", column.getDesiredBlockSize());
                    json.put("compression_algorithm", column.getCompressionAlgorithm());
                    json.put("column_name", column.getName());
                    json.put("comment", column.getComment());
                    json.put("column_type", column.getType());
                    json.put("encoding", column.getEncoding());
                    columnsList.add(json);
                }
                schema.put("columns", columnsList);
                List<String> keyColumnsList = new ArrayList<>();
                for (ColumnSchema primaryKeyColumn : table.getSchema().getPrimaryKeyColumns()) {
                    keyColumnsList.add(primaryKeyColumn.getName());
                }
                schema.put("key_column_names", keyColumnsList);
                ddlJson.put("schema", schema);

                /* partition */
                JSONObject partition = new JSONObject();
                List<PartitionSchema.HashBucketSchema> bucketSchemas = table.getPartitionSchema().getHashBucketSchemas();
                List<JSONObject> hashPartitionList = new ArrayList<>();
                for (PartitionSchema.HashBucketSchema hashBucketSchema : bucketSchemas) {
                    JSONObject json = new JSONObject();
                    json.put("seed", hashBucketSchema.getSeed());
                    json.put("columns", hashBucketSchema.getColumnIds());
                    json.put("num_buckets", hashBucketSchema.getNumBuckets());
                    hashPartitionList.add(json);
                }
                partition.put("hash_partitions", hashPartitionList);
                ddlJson.put("extra_configs", partition);

                /* extra_configs */
                JSONObject extraConfigs = new JSONObject();
                extraConfigs.put("configs", table.getExtraConfig());
                ddlJson.put("extra_configs", extraConfigs);

                if (isRow) {
                    writer.write(ddlJson.toJSONString() + "\n");
                } else {
                    writer.write(JSON.toJSONString(ddlJson, SerializerFeature.PrettyFormat) + "\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
