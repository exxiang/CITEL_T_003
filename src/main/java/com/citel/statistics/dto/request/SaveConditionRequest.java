package com.citel.statistics.dto.request;

import com.citel.statistics.dto.query.QueryRange;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 保存查询区间条件请求
 */
@Data
public class SaveConditionRequest {

    /** 条件名称 */
    @NotBlank(message = "条件名称不能为空")
    private String conditionName;

    /** 年龄段列表 */
    private List<QueryRange> ageRanges;

    /** 里程区间列表 */
    private List<QueryRange> mileageRanges;

    /** 时间区间列表 */
    private List<QueryRange> timeRanges;
}
