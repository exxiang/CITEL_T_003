package com.citel.statistics.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Year;

/**
 * 业务配置项
 */
@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /** 年龄计算参考年；为空时取系统当前年份 */
    private Integer referenceYear;

    /**
     * 获取年龄计算参考年
     *
     * @return 参考年份
     */
    public int getAgeReferenceYear() {
        return referenceYear != null ? referenceYear : Year.now().getValue();
    }
}
