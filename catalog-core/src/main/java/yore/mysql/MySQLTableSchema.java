package yore.mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 导出 MySQL 表结构，
 *
 * @author Yore Yuan
 */
@SpringBootApplication
public class MySQLTableSchema {

    public static void main(String[] args) {
        SpringApplication.run(MySQLTableSchema.class, args);
    }

}
