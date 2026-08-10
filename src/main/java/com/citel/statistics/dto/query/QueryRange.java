package com.citel.statistics.dto.query;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 查询区间模型（年龄/里程/时间通用，统一使用 BigDecimal 便于 MyBatis 绑定）
 */
@Data
public class QueryRange {

    /** 区间下限（含） */
    private BigDecimal min;

    /** 区间上限（含） */
    private BigDecimal max;
}
