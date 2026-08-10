package com.citel.statistics.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计结果视图对象（图表数据）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountVO {

    /** 区间标签，如 "10-20岁"、"0-5000公里" */
    private String label;

    /** 区间记录数 */
    private Long value;
}
