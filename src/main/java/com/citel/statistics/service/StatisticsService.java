package com.citel.statistics.service;

import com.citel.statistics.dto.query.AgeRangeQuery;
import com.citel.statistics.dto.query.MileageRangeQuery;
import com.citel.statistics.dto.query.TimeRangeQuery;
import com.citel.statistics.dto.vo.CountVO;

import java.util.List;

/**
 * 统计服务
 */
public interface StatisticsService {

    /** 年龄区间统计 */
    List<CountVO> countByAge(AgeRangeQuery query);

    /** 里程区间统计 */
    List<CountVO> countByMileage(MileageRangeQuery query);

    /** 时间区间统计 */
    List<CountVO> countByTime(TimeRangeQuery query);
}
