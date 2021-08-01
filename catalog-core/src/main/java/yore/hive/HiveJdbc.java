package yore.hive;

import com.baomidou.dynamic.datasource.toolkit.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yore Yuan
 */
@Component
public class HiveJdbc {

    @Autowired
    private Environment environment;

    @Value("${spring.datasource.dynamic.datasource.hive.url}")
    private String url;

    @Value("${spring.datasource.dynamic.datasource.hive.username}")
    private String username;

    @Value("${spring.datasource.dynamic.datasource.hive.password}")
    private String password;

    @Value("${spring.datasource.dynamic.datasource.hive.driver-class-name}")
    private String driver;

    private Pattern pattern = Pattern.compile("ENC\\((.*)\\)");


    public Connection getHiveJdbcConn() {
        Connection connection = null;
//        String p = environment.getProperty("spring.profiles.active");
//        String url = environment.getProperty("spring.datasource.dynamic.datasource.hive.url");
//        String username = environment.getProperty("spring.datasource.dynamic.datasource.hive.username");
//        String password = environment.getProperty("spring.datasource.dynamic.datasource.hive.password");
//        String driver = environment.getProperty("spring.datasource.dynamic.datasource.hive.driver-class-name");
        Matcher matcher = pattern.matcher(password);
        if (matcher.find()) {
            try {
                username = CryptoUtils.encrypt(username);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

}
