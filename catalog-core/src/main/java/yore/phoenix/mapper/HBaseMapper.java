package yore.phoenix.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Yore Yuan
 */
@Repository
public interface HBaseMapper {

    /**
     * 执行 Phoenix SQL，并返回 SQL 执行结果集
     *
     * @param sql Phoenix SQL
     * @return List 结果集
     */
    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Object>> queryForList(String sql);
    
}
