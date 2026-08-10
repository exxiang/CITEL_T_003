package com.citel.statistics.dto.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 人员信息视图对象
 */
@Data
public class PersonVO {

    /** 人员ID */
    private Long id;

    /** 性别 0-女 1-男 */
    private Integer gender;

    /** 出生年份 */
    private Integer birthYear;

    /** 年龄（参考年-出生年份） */
    private Integer age;

    /** 总旅行里程（公里） */
    private BigDecimal totalMileage;

    /** 总旅行时间（小时） */
    private BigDecimal totalTravelTime;
}
