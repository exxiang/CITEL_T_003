package com.citel.statistics.dto.query;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 按飞行里程区间查询请求
 */
@Data
public class MileageRangeQuery {

    /** 里程区间列表（允许重叠），单位公里 */
    @NotEmpty(message = "里程区间不能为空")
    private List<QueryRange> mileageRanges;

    /** 页码，从1开始 */
    private Integer current = 1;

    /** 每页条数，最大20 */
    private Integer size = 20;
}
