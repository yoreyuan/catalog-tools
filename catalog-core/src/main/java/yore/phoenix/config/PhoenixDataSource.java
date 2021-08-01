package yore.phoenix.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ExceptionSorter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Phoenix DataSource
 *
 * &#64;MapperScan 配置mybatis的接口类放的地方
 * @author Yore Yuan
 */
@Configuration
@MapperScan(basePackages = PhoenixDataSource.PACKAGE, sqlSessionFactoryRef = "phoenixSqlSessionFactory")
public class PhoenixDataSource {

    // 精确到 cluster 目录，以便跟其他数据源隔离
    static final String PACKAGE = "yore.phoenix.mapper";
    static final String MAPPER_LOCATION = "classpath:phoenix/*.xml";

    /**
     * 需要通过@Value获取配置文件中的配置项
     * @return DataSource DruidDataSource
     */
    @Bean(name = "phoenixdataSource")
    public DataSource clusterDataSource() {
        DruidDataSource dds = new DruidDataSource();
        dds.setDriverClassName(driverClass);
        dds.setUrl(url);
        dds.setUsername(user);
        dds.setPassword(password);

        dds.setInitialSize(initialSize);
        dds.setMaxActive(maxActive);
        dds.setMaxWait(maxWait);
        dds.setMinIdle(minIdle);
//        dds.setMaxActive(maxIdle);
        dds.setValidationQuery(validationQuery);
        dds.setTestOnBorrow(testOnBorrow);
        dds.setTestOnReturn(testOnReturn);
        dds.setTestWhileIdle(testWhileIdle);

        dds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dds.setRemoveAbandoned(removeAbandoned);
        dds.setRemoveAbandonedTimeout(removeAbandonedTimeout);
//        dds.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        dds.setLogAbandoned(logAbandoned);
        dds.setPoolPreparedStatements(poolPreparedStatements);
        dds.setQueryTimeout(300);

        try {
            dds.setFilters("stat");
        }catch (Exception e){
            e.printStackTrace();
        }
        // 设置剔除异常连接机制
        dds.setExceptionSorter(new PhoenixExceptionSorter());

        return dds;
    }

    @Bean(name = "phoenixTransactionManager")
    public DataSourceTransactionManager clusterTransactionManager() {
        return new DataSourceTransactionManager(clusterDataSource());
    }

    @Bean(name = "phoenixSqlSessionFactory")
    public SqlSessionFactory clusterSqlSessionFactory(@Qualifier("phoenixdataSource") DataSource clusterDataSource)
            throws Exception {
        System.setProperty("HADOOP_USER_NAME", "phoenix");
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(clusterDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(PhoenixDataSource.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }

    static class PhoenixExceptionSorter implements ExceptionSorter {
        @Override
        public boolean isExceptionFatal(SQLException e) {
            if (e.getMessage().contains("Phoenix Connection is null or closed")) {
                return true;
            }
            return false;
        }

        @Override
        public void configFromProperties(Properties properties) {
        }
    }

    @Value("${spring.datasource.dynamic.datasource.phoenix.url}")
    private String url;

    @Value("${spring.datasource.dynamic.datasource.phoenix.username}")
    private String user;

    @Value("${spring.datasource.dynamic.datasource.phoenix.password}")
    private String password;

    @Value("${spring.datasource.dynamic.datasource.phoenix.driver-class-name}")
    private String driverClass;

    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.initial-size}")
    private Integer initialSize;

    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.max-active}")
    private Integer maxActive;

    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.max-wait}")
    private Long maxWait;

    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.min-idle}")
    private Integer minIdle;

//    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.maxIdle}")
//    private Integer maxIdle;

    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.validation-query}")
    private String validationQuery;

    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.test-on-borrow}")
    private Boolean testOnBorrow;

    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.test-on-return}")
    private Boolean testOnReturn;

    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.test-while-idle}")
    private Boolean testWhileIdle;

    /** 每5分钟运行一次空闲连接回收器（默认-1） */
    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.time-between-eviction-runs-millis}")
    private Long timeBetweenEvictionRunsMillis;

    /** 池中的连接空闲15分钟后被回收（默认30分钟） */
    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.min-evictable-idle-time-millis}")
    private Integer minEvictableIdleTimeMillis;

    /** 打开removeAbandoned功能 */
    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.remove-abandoned}")
    private Boolean removeAbandoned;

    /** 活动连接的最大空闲时间,单位为秒 超过此时间的连接会被释放到连接池中 */
    @Value("#{${spring.datasource.dynamic.datasource.phoenix.druid.remove-abandoned-timeout-millis} / 1000}")
    private Integer removeAbandonedTimeout;

//    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.numTestsPerEvictionRun}")
//    private Integer numTestsPerEvictionRun;

    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.log-abandoned}")
    private Boolean logAbandoned;

    @Value("${spring.datasource.dynamic.datasource.phoenix.druid.pool-prepared-statements}")
    private Boolean poolPreparedStatements;

}
