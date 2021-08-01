package yore.hive;

import java.util.List;
import java.util.Map;

/**
 * @author Yore Yuan
 */
public interface HiveDbService {

    /**
     * 元数据库查询语句（如果配置的为 MySQL，则执行的为 MySQL 查询）
     *
     * @param sql sql
     * @return result list
     */
    List<Map<String, Object>> queryMetaDB(String sql);

    /**
     * Hive 查询语句
     *
     * @param sql Hive sql
     * @return result list
     */
    List<Map<String, Object>> queryHive(String sql);

    /**
     * 获取 hive 相关的 DB
     *
     * @return map, k=NAME, v=DB_ID
     */
    Map<String, Long> queryAllHiveDBNameAndId();

    /**
     * 根据 DB_ID 查询所有指定的表
     *
     * @param DB_ID 库 id
     * @return List
     */
    List<String> queryAllHiveTableByDbId(Long DB_ID);

    /**
     * 根据 DB_ID 和 TBL_NAME 查询所有指定的表是否存在
     *
     * @param DB_ID 库 id
     * @param TBL_NAME 表名
     * @return boolean 指定的表是否存在
     */
    boolean queryAllHiveTableByDbIdAndTblName(Long DB_ID, String TBL_NAME);

    /**
     * 根据库和表名获取 hive 建表语句
     *
     * @param db 库名
     * @param table 表名
     * @return 建表语句
     */
    String showHiveCreateTable(String db, String table);

}
