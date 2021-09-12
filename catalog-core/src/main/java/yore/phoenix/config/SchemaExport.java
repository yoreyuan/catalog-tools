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
package yore.phoenix.config;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import yore.common.RuntimeAnnotation;
import yore.common.RuntimeAspect;
import yore.phoenix.mapper.HBaseMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *   Phoenix SYSTEM."CATALOG" 表结构如下：
 *
 *   TENANT_ID|TABLE_SCHEM|TABLE_NAME              |COLUMN_NAME                |COLUMN_FAMILY|TABLE_SEQ_NUM|TABLE_TYPE|PK_NAME                    |COLUMN_COUNT|SALT_BUCKETS|DATA_TABLE_NAME|INDEX_STATE|IMMUTABLE_ROWS|VIEW_STATEMENT|DEFAULT_COLUMN_FAMILY|DISABLE_WAL|MULTI_TENANT|VIEW_TYPE|VIEW_INDEX_ID|DATA_TYPE|COLUMN_SIZE|DECIMAL_DIGITS|NULLABLE|ORDINAL_POSITION|SORT_ORDER|ARRAY_SIZE|VIEW_CONSTANT|IS_VIEW_REFERENCED|KEY_SEQ|LINK_TYPE|TYPE_NAME|REMARKS|SELF_REFERENCING_COL_NAME|REF_GENERATION|BUFFER_LENGTH|NUM_PREC_RADIX|COLUMN_DEF|SQL_DATA_TYPE|SQL_DATETIME_SUB|CHAR_OCTET_LENGTH|IS_NULLABLE|SCOPE_CATALOG|SCOPE_SCHEMA|SCOPE_TABLE|SOURCE_DATA_TYPE|IS_AUTOINCREMENT|INDEX_TYPE|INDEX_DISABLE_TIMESTAMP|STORE_NULLS|BASE_COLUMN_COUNT|IS_ROW_TIMESTAMP|TRANSACTIONAL|UPDATE_CACHE_FREQUENCY|IS_NAMESPACE_MAPPED|AUTO_PARTITION_SEQ|APPEND_ONLY_SCHEMA|GUIDE_POSTS_WIDTH|COLUMN_QUALIFIER           |IMMUTABLE_STORAGE_SCHEME|ENCODING_SCHEME|QUALIFIER_COUNTER|USE_STATS_FOR_PARALLELIZATION|TRANSACTION_PROVIDER|
 * ---------+-----------+------------------------+---------------------------+-------------+-------------+----------+---------------------------+------------+------------+---------------+-----------+--------------+--------------+---------------------+-----------+------------+---------+-------------+---------+-----------+--------------+--------+----------------+----------+----------+-------------+------------------+-------+---------+---------+-------+-------------------------+--------------+-------------+--------------+----------+-------------+----------------+-----------------+-----------+-------------+------------+-----------+----------------+----------------+----------+-----------------------+-----------+-----------------+----------------+-------------+----------------------+-------------------+------------------+------------------+-----------------+---------------------------+------------------------+---------------+-----------------+-----------------------------+--------------------+
 *          |           |CLM_PROPERTY_LOSS_DETAIL|                           |             |            0|v         |PK_CLM_PROPERTY_LOSS_DETAIL|           5|            |               |           |false         |              |                     |false      |false       |        1|             |         |           |              |        |                |          |          |             |                  |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |false      |                5|                |             |                     0|true               |                  |false             |                 |                           |                       1|              0|                 |                             |                    |
 *          |           |CLM_PROPERTY_LOSS_DETAIL|COL_ID                     |info         |             |          |PK_CLM_PROPERTY_LOSS_DETAIL|            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               3|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |COL_ID                     |                        |               |                 |                             |                    |
 *          |           |CLM_PROPERTY_LOSS_DETAIL|ID_CLM_PROPERTY_LOSS       |info         |             |          |PK_CLM_PROPERTY_LOSS_DETAIL|            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               4|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |ID_CLM_PROPERTY_LOSS       |                        |               |                 |                             |                    |
 *          |           |CLM_PROPERTY_LOSS_DETAIL|ID_CLM_PROPERTY_LOSS_DETAIL|info         |             |          |PK_CLM_PROPERTY_LOSS_DETAIL|            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               5|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |ID_CLM_PROPERTY_LOSS_DETAIL|                        |               |                 |                             |                    |
 *          |           |CLM_PROPERTY_LOSS_DETAIL|TAB_NAME                   |info         |             |          |PK_CLM_PROPERTY_LOSS_DETAIL|            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               2|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |TAB_NAME                   |                        |               |                 |                             |                    |
 *          |           |CLM_PROPERTY_LOSS_DETAIL|no                         |             |             |          |PK_CLM_PROPERTY_LOSS_DETAIL|            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       0|               1|         2|          |             |false             |      1|         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |                           |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |                           |             |            0|v         |PK_T_D_AGENT               |          18|            |               |           |false         |              |                     |false      |false       |        1|             |         |           |              |        |                |          |          |             |                  |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |false      |               18|                |             |                     0|true               |                  |false             |                 |                           |                       1|              0|                 |                             |                    |
 *          |           |T_D_AGENT               |C_CHA_BIRTH                |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               2|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |C_CHA_BIRTH                |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |C_CHA_CERT_CDE             |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               3|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |C_CHA_CERT_CDE             |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |C_CHA_CERT_TYPE            |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               4|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |C_CHA_CERT_TYPE            |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |C_CHA_ID                   |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               5|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |C_CHA_ID                   |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |C_CHA_ID_A                 |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               6|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |C_CHA_ID_A                 |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |C_CHA_ID_L                 |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               7|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |C_CHA_ID_L                 |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |C_CHA_ID_P                 |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               8|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |C_CHA_ID_P                 |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |C_CHA_NME                  |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|               9|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |C_CHA_NME                  |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |C_CHA_PHONE                |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|              10|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |C_CHA_PHONE                |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |C_CHA_SEX                  |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|              11|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |C_CHA_SEX                  |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |C_STATUS                   |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|              12|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |C_STATUS                   |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |IS_EMP                     |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|              13|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |IS_EMP                     |                        |               |                 |                             |                    |
 *          |           |T_D_AGENT               |T_QLFT_ADBTM               |info         |             |          |PK_T_D_AGENT               |            |            |               |           |              |              |                     |           |            |         |             |       12|           |              |       1|              14|         2|          |             |false             |       |         |         |       |                         |              |             |              |          |             |                |                 |           |             |            |           |                |                |          |                       |           |                 |false           |             |                      |                   |                  |                  |                 |T_QLFT_ADBTM               |                        |               |                 |                             |                    |
 *
 * </pre>
 *
 * @author Yore Yuan
 */
@Order(value = 1)
@Component
public class SchemaExport extends yore.common.FileWriter implements CommandLineRunner {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    HBaseMapper phoenixDao;

    //@Value("${catalog.ddl.output.format}")
    private String rowFormat;


    /**
     *
     * @param args 入参
     *        args[0] outpath，已通过脚本
     *        args[1] table name
     */
    @DS("phoenix")
    @Override
    public void run(String... args) throws Exception {
        initWriter(args[0]);
        RuntimeAspect.printSpend(this, args);
        closeWriter();
    }

    @RuntimeAnnotation(descr = "Phoenix")
    public void start(String[] args) throws Exception {
//        System.out.println(Arrays.toString(args));
        rowFormat = System.getProperty("catalog.ddl.output.format");
        if (StringUtils.isEmpty(rowFormat)) {
            rowFormat = "row";
        }
        LOG.info("输出格式为：{}", rowFormat);

        Set<String> tableSet = new HashSet<>();
        boolean isRow = "row".equalsIgnoreCase(rowFormat);

        if (args.length > 1) {
            // 指定了具体表名的
            for (int i = 1; i < args.length; i++) {
                tableSet.add(args[i]);
            }
        } else {
            // All
            String allTable = "SELECT DISTINCT TABLE_NAME FROM SYSTEM.CATALOG WHERE TABLE_SCHEM NOT IN (SELECT 'SYSTEM')";
            List<Map<String, Object>> allTableList = this.jdbcTemplate.queryForList(allTable);
            for (Map<String, Object> stringObjectMap : allTableList) {
                tableSet.add(String.valueOf(stringObjectMap.get("TABLE_NAME")));
            }
        }

//        tableSet.clear();
//        tableSet.add("T_M_BUS_ASSET_CUS");
//        tableSet.add("ods_aiu_ldaddress");

        // 生成表结构
        for (String TABLE_NAME : tableSet) {
            String tableTypeSql = String.format("SELECT DISTINCT TABLE_TYPE FROM SYSTEM.CATALOG WHERE TABLE_NAME='%s' AND COLUMN_NAME IS NULL", TABLE_NAME);
            String columnNamesql = String.format("SELECT COLUMN_NAME,COLUMN_FAMILY FROM SYSTEM.CATALOG WHERE TABLE_NAME='%s' AND COLUMN_FAMILY IS NOT NULL", TABLE_NAME);
            String createSqlTemp = "CREATE {0} IF NOT EXISTS {1}(\"no\" VARCHAR NOT NULL, {2} CONSTRAINT PK_{3} PRIMARY KEY (\"no\"))column_encoded_bytes=0;";
            if (!isRow) {
                createSqlTemp = "CREATE {0} IF NOT EXISTS {1}(\r\n" +
                        "  \"no\" VARCHAR NOT NULL,\r\n" +
                        "{2}" +
                        "  CONSTRAINT PK_{3} PRIMARY KEY (\"no\")\r\n" +
                        ")column_encoded_bytes=0;";;
            }

            // Phoenix 中针对大小写进行处理
            char firstChar = TABLE_NAME.trim().charAt(0);
            if (firstChar >= 'a' && firstChar <= 'z') {
                createSqlTemp = createSqlTemp.replace("{1}",  "\"" + TABLE_NAME + "\"")
                        .replace("{3}", TABLE_NAME);
            } else {
                createSqlTemp = createSqlTemp.replace("{1}",   TABLE_NAME )
                        .replace("{3}", TABLE_NAME);
            }

            /*
             * v 视图语句
             * s 表语句
             */
            List<Map<String, Object>> tableTypeList = this.jdbcTemplate.queryForList(tableTypeSql);
            createSqlTemp = "v".equalsIgnoreCase(String.valueOf(tableTypeList.get(0).get("TABLE_TYPE")))?
                    createSqlTemp.replace("{0}", "VIEW"):
                    createSqlTemp.replace("{0}", "TABLE");

            // 处理字段
            StringBuilder columnStr = new StringBuilder();
            List<Map<String, Object>> columnNameList = this.jdbcTemplate.queryForList(columnNamesql);
            for (Map<String, Object> columnNameMap : columnNameList) {
                Object COLUMN_NAME = columnNameMap.get("COLUMN_NAME");
                if (COLUMN_NAME == null || "no".equals(String.valueOf(COLUMN_NAME))) {
                    continue;
                }
                Object COLUMN_FAMILY = columnNameMap.get("COLUMN_FAMILY");
                if (isRow) {
                    columnStr.append(String.format("\"%s\".%s VARCHAR,", COLUMN_FAMILY, COLUMN_NAME));
                } else {
                    columnStr.append(String.format("  \"%s\".%s VARCHAR,\r\n", COLUMN_FAMILY, COLUMN_NAME));
                }
            }
            createSqlTemp = createSqlTemp.replace("{2}", columnStr.toString());
            writer.write(createSqlTemp + "\n");
            writer.flush();
        }
    }

}
