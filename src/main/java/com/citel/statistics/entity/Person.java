package com.citel.statistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 人员信息实体
 */
@Data
@TableName("person")
public class Person {

    /** 人员ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 性别 0-女 1-男 */
    private Integer gender;

    /** 出生年份 */
    private Integer birthYear;

    /** 总旅行里程（飞行里程，公里） */
    private BigDecimal totalMileage;

    /** 总旅行时间（飞行时间，小时） */
    private BigDecimal totalTravelTime;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
