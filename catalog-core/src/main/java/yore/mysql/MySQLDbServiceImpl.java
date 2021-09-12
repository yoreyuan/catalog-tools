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
package yore.mysql;

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
public class MySQLDbServiceImpl implements MySQLDbService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${catalog.ddl.output.format}")
    private String rowFormat;


    @DS("mysql")
    @Override
    public List<Map<String, Object>> queryMySQL(String sql) {
        return  this.jdbcTemplate.queryForList(sql);
    }

    @DS("mysql")
    @Override
    public List<String> queryAllDBName() {
        List<String> dbNameList = new ArrayList<>();
        String sql = "SHOW DATABASES";
        List<Map<String, Object>> listMap = this.queryMySQL(sql);
        for (Map<String, Object> map : listMap) {
            dbNameList.add((String) map.get("Database"));
        }
        return dbNameList;
    }


    @DS("mysql")
    @Override
    public List<String> queryAllTableByDBName(String database) {
        List<String> tableNameList = new ArrayList<>();
        this.jdbcTemplate.execute("USE " + database);
        List<Map<String, Object>> listMap = this.jdbcTemplate.queryForList("SHOW TABLES;");
        for (Map<String, Object> map : listMap) {
            tableNameList.add((String) map.get("Tables_in_" + database));
        }
        return tableNameList;
    }

    @DS("mysql")
    @Override
    public String showCreateTable(String db, String table) {
        String sql = "SHOW CREATE TABLE ?1.?2"
                .replace("?1", db)
                .replace("?2", table);
        List<Map<String, Object>> listMap = this.queryMySQL(sql);
        StringBuilder createSql = new StringBuilder();
        for (Map<String, Object> rsMap : listMap) {
            // 默认带有换行字符
            String sqlLine = (String)rsMap.get("Create Table");
            sqlLine = sqlLine.replace(
                    "CREATE TABLE `" + table + "`",
                    "CREATE TABLE `" + db + "`.`" + table + "`"
            );
            if ("row".equalsIgnoreCase(rowFormat)) {
                sqlLine = sqlLine.replaceAll("\r?\n", " ");
            }
            createSql.append(sqlLine);
        }
        createSql.append(";");
        return createSql.toString();
    }

}
