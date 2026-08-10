package com.citel.statistics;

import com.citel.statistics.config.AppProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 人员旅行数据查询统计系统 启动类
 */
@SpringBootApplication
@MapperScan("com.citel.statistics.mapper")
@EnableConfigurationProperties(AppProperties.class)
public class StatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatisticsApplication.class, args);
    }
}
