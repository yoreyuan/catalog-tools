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
package yore.clickhouse;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Yore Yuan
 */
@Service
public class ClickHouseDbServiceImpl implements ClickHouseDbService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${catalog.ddl.output.format}")
    private String rowFormat;


    @DS("clickhouse")
    @Override
    public List<Map<String, Object>> queryClickHouse(String sql) {
        return  this.jdbcTemplate.queryForList(sql);
    }

    @DS("clickhouse")
    @Override
    public List<String> queryAllDBName() {
        List<String> dbNameList = new ArrayList<>();
        String sql = "SHOW DATABASES";
        List<Map<String, Object>> listMap = this.queryClickHouse(sql);
        for (Map<String, Object> map : listMap) {
            dbNameList.add((String) map.get("name"));
        }
        return dbNameList;
    }


    @DS("clickhouse")
    @Override
    public List<String> queryAllTableByDBName(String database) {
        List<String> tableNameList = new ArrayList<>();
        String sql = "SELECT DISTINCT name FROM `system`.tables WHERE `database`='" + database + "'";
        List<Map<String, Object>> listMap = this.jdbcTemplate.queryForList(sql);
        for (Map<String, Object> map : listMap) {
            String dbName = (String) map.get("name");
            if (dbName.startsWith(".")) {
                continue;
            }
            tableNameList.add(dbName);
        }
        return tableNameList;
    }

    @DS("clickhouse")
    @Override
    public String showCreateTable(String db, String table) {
        String sql = "SHOW CREATE TABLE `?1`.`?2`"
                .replace("?1", db)
                .replace("?2", table);
        List<Map<String, Object>> listMap = this.queryClickHouse(sql);
        StringBuilder createSql = new StringBuilder();
        for (Map<String, Object> rsMap : listMap) {
            // 默认带有换行字符
            String sqlLine = (String)rsMap.get("statement");
            if ("row".equalsIgnoreCase(rowFormat)) {
                sqlLine = sqlLine.replaceAll("\r?\n", " ");
            }
            createSql.append(sqlLine);
        }
        createSql.append(";");
        return createSql.toString();
    }

}
