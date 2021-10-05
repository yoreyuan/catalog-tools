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
package yore.hive;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yore Yuen
 */
@DS("metastore")
@Service
public class HiveDbServiceImpl implements HiveDbService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HiveJdbc hiveJdbc;

    @Value("${catalog.ddl.output.format}")
    private String rowFormat;



    @DS("metastore")
    @Override
    public List<Map<String, Object>> queryMetaDB(String sql) {
        return  this.jdbcTemplate.queryForList(sql);
    }

    @DS("hive")
    @Override
    public List<Map<String, Object>> queryHive(String sql) {
        return  this.jdbcTemplate.queryForList(sql);
    }

    @Override
    public Map<String, Long> queryAllHiveDBNameAndId() {
        String sql = "SELECT DB_ID,NAME FROM DBS WHERE OWNER_NAME IN('hive', 'public') AND `NAME` NOT IN('sys', 'information_schema')";
        Map<String, Long> dbMap = new HashMap<>();
        List<Map<String, Object>> listMap = this.queryMetaDB(sql);
        for (Map<String, Object> map : listMap) {
            Long v = (Long)map.get("DB_ID");
            String k = (String)map.get("NAME");
            dbMap.put(k, v);
        }
        return dbMap;
    }

    @Override
    public List<String> queryAllHiveTableByDbId(Long DB_ID) {
        List<String> tableNameList = new ArrayList<>();
        String sql = "SELECT TBL_NAME FROM TBLS WHERE DB_ID=" + DB_ID;
        List<Map<String, Object>> listMap = this.queryMetaDB(sql);
        for (Map<String, Object> map : listMap) {
            tableNameList.add((String)map.get("TBL_NAME"));
        }
        return tableNameList;
    }

    @Override
    public boolean queryAllHiveTableByDbIdAndTblName(Long DB_ID, String TBL_NAME) {
        String sql = "SELECT TBL_NAME FROM TBLS WHERE DB_ID=" + DB_ID + " AND TBL_NAME='?2'"
                .replace("?2", TBL_NAME);
        List<Map<String, Object>> listMap = this.queryMetaDB(sql);
        if (listMap==null || listMap.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @DS("hive")
    @Override
    public String showHiveCreateTable(String db, String table) {
        String sql = "SHOW CREATE TABLE ?1.?2"
                .replace("?1", db)
                .replace("?2", table);
        List<Map<String, Object>> listMap = this.queryHive(sql);
        String createSql = "";
        for (Map<String, Object> rsMap : listMap) {
            String sqlLine = (String)rsMap.get("createtab_stmt");
            if ("row".equalsIgnoreCase(rowFormat)) {
                createSql += sqlLine + " ";
            } else {
                createSql = String.join("\r\n", createSql, sqlLine);
            }
        }
        createSql += ";";
        /*if (!"row".equalsIgnoreCase(rowFormat)) {
            createSql = createSql.substring(2);
        }*/
        return createSql;
    }

}
