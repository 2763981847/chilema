package com.chilema;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@MapperScan(basePackages = "com.chilema.mapper")
@EnableTransactionManagement
@EnableCaching
public class ChilemaTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChilemaTakeOutApplication.class, args);
        log.info("项目启动成功");
    }

}
