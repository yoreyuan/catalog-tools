package yore.hive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 导出 Hive 表结构，
 *
 * @author Yore Yuan
 */
@SpringBootApplication
public class HiveTableSchema {

    public static void main(String[] args) {
        SpringApplication.run(HiveTableSchema.class, args);
    }

}
