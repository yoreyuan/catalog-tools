
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
 */package yore.impala;

import java.util.List;
import java.util.Map;

/**
 * @author Yore Yuan
 */
public interface ImpalaDbService {

    /**
     * 元数据库查询语句（如果配置的为 MySQL，则执行的为 MySQL 查询）
     *
     * @param sql sql
     * @return result list
     */
    List<Map<String, Object>> queryMetaDB(String sql);

    /**
     * Impala 查询语句
     *
     * @param sql Impala sql
     * @return result list
     */
    List<Map<String, Object>> queryImpala(String sql);

    /**
     * 获取 Impala 相关的 DB
     *
     * @return map, k=NAME, v=DB_ID
     */
    Map<String, Long> queryAllImpalaDBNameAndId();

    /**
     * 根据 DB_ID 查询所有指定的表
     *
     * @param DB_ID 库 id
     * @return List
     */
    List<String> queryAllImpalaTableByDbId(Long DB_ID);

    /**
     * 根据 DB_ID 和 TBL_NAME 查询所有指定的表是否存在
     *
     * @param DB_ID 库 id
     * @param TBL_NAME 表名
     * @return boolean 指定的表是否存在
     */
    boolean queryAllImpalaTableByDbIdAndTblName(Long DB_ID, String TBL_NAME);

    /**
     * 根据库和表名获取 Impala 建表语句
     *
     * @param db 库名
     * @param table 表名
     * @return 建表语句
     */
    String showImpalaCreateTable(String db, String table);

}
