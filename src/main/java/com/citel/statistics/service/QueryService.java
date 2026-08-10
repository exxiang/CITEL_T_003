package com.citel.statistics.service;

import com.citel.statistics.common.PageResult;
import com.citel.statistics.dto.query.AgeRangeQuery;
import com.citel.statistics.dto.query.MileageRangeQuery;
import com.citel.statistics.dto.query.TimeRangeQuery;
import com.citel.statistics.dto.vo.PersonVO;

/**
 * 三种查询模式服务
 */
public interface QueryService {

    /** 按年龄区间查询 */
    PageResult<PersonVO> queryByAge(AgeRangeQuery query);

    /** 按飞行里程区间查询 */
    PageResult<PersonVO> queryByMileage(MileageRangeQuery query);

    /** 按飞行时间区间查询（时间区间不允许重叠） */
    PageResult<PersonVO> queryByTime(TimeRangeQuery query);
}
