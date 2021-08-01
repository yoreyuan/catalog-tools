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
                sqlLine = sqlLine.replaceAll("\r?\n", "");
            }
            createSql.append(sqlLine);
        }
        createSql.append(";");
        return createSql.toString();
    }

}
