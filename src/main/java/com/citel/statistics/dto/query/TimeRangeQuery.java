package com.citel.statistics.dto.query;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 按飞行时间区间查询请求
 */
@Data
public class TimeRangeQuery {

    /** 时间区间列表（不允许重叠），单位小时 */
    @NotEmpty(message = "时间区间不能为空")
    private List<QueryRange> timeRanges;

    /** 页码，从1开始 */
    private Integer current = 1;

    /** 每页条数，最大20 */
    private Integer size = 20;
}
