package yore.phoenix;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 导出 Phoenix 表结构，
 * 包含表和视图
 *
 * @author Yore Yuan
 */
@SpringBootApplication
@MapperScan("yore.phoenix.mapper")
public class PhoenixTableSchema {

    public static void main(String[] args) {
        SpringApplication.run(PhoenixTableSchema.class, args);
    }

}
