package yore.impala;

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
 * @author Yore Yuan
 */
@DS("metastore")
@Service
public class ImpalaDbServiceImpl implements ImpalaDbService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${catalog.ddl.output.format}")
    private String rowFormat;


    @DS("metastore")
    @Override
    public List<Map<String, Object>> queryMetaDB(String sql) {
        return  this.jdbcTemplate.queryForList(sql);
    }

    @DS("impala")
    @Override
    public List<Map<String, Object>> queryImpala(String sql) {
        return  this.jdbcTemplate.queryForList(sql);
    }

    @Override
    public Map<String, Long> queryAllImpalaDBNameAndId() {
        String sql = "SELECT DB_ID,NAME FROM DBS WHERE `NAME` NOT IN('sys', 'information_schema')";
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
    public List<String> queryAllImpalaTableByDbId(Long DB_ID) {
        List<String> tableNameList = new ArrayList<>();
        String sql = "SELECT TBL_NAME FROM TBLS WHERE DB_ID=" + DB_ID;
        List<Map<String, Object>> listMap = this.queryMetaDB(sql);
        for (Map<String, Object> map : listMap) {
            tableNameList.add((String)map.get("TBL_NAME"));
        }
        return tableNameList;
    }

    @Override
    public boolean queryAllImpalaTableByDbIdAndTblName(Long DB_ID, String TBL_NAME) {
        String sql = "SELECT TBL_NAME FROM TBLS WHERE DB_ID=" + DB_ID + " AND TBL_NAME='?2'"
                .replace("?2", TBL_NAME);
        List<Map<String, Object>> listMap = this.queryMetaDB(sql);
        if (listMap==null || listMap.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @DS("impala")
    @Override
    public String showImpalaCreateTable(String db, String table) {
        String sql = "SHOW CREATE TABLE ?1.?2"
                .replace("?1", db)
                .replace("?2", table);
        List<Map<String, Object>> listMap = this.queryImpala(sql);
        StringBuilder createSql = new StringBuilder();
        for (Map<String, Object> rsMap : listMap) {
            // 默认带有换行字符
            String sqlLine = (String)rsMap.get("result");
            if ("row".equalsIgnoreCase(rowFormat)) {
                sqlLine = sqlLine.replaceAll("\r?\n", " ");
//                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
//                Pattern p = Pattern.compile("\r?\n");
//                Matcher m = p.matcher(sqlLine);
//                sqlLine = m.replaceAll("");
            }
            createSql.append(sqlLine);
        }
        createSql.append(";");
        return createSql.toString();
    }


}
