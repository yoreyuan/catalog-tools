package yore.impala;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 导出 Impala 表结构，
 *
 * @author Yore Yuan
 */
@SpringBootApplication
public class ImpalaTableSchema {

    public static void main(String[] args) {
        SpringApplication.run(ImpalaTableSchema.class, args);
    }

}
