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

import java.util.List;
import java.util.Map;

/**
 * @author Yore Yuen
 */
public interface ClickHouseDbService {

    /**
     * ClickHouse 查询语句
     *
     * @param sql MySQL sql
     * @return result list
     */
    List<Map<String, Object>> queryClickHouse(String sql);

    /**
     * 获取 ClickHouse 相关的 DB Name
     *
     * @return List
     */
    List<String> queryAllDBName();

    /**
     * 查询指定库下的所有的表
     *
     * @param database Database
     * @return List
     */
    List<String> queryAllTableByDBName(String database);

    /**
     * 根据库和表名获取 MySQL 建表语句
     *
     * @param db 库名
     * @param table 表名
     * @return 建表语句
     */
    String showCreateTable(String db, String table);

}
