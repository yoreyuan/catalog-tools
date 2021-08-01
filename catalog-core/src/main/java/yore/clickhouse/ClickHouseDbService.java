package yore.clickhouse;

import java.util.List;
import java.util.Map;

/**
 * @author Yore Yuan
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
