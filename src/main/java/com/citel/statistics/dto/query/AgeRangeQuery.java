package com.citel.statistics.dto.query;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 按年龄区间查询请求
 */
@Data
public class AgeRangeQuery {

    /** 年龄段列表（允许重叠） */
    @NotEmpty(message = "年龄区间不能为空")
    private List<QueryRange> ageRanges;

    /** 页码，从1开始 */
    private Integer current = 1;

    /** 每页条数，最大20 */
    private Integer size = 20;
}
