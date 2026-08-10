package com.citel.statistics.service.impl;

import com.citel.statistics.common.PageResult;
import com.citel.statistics.config.AppProperties;
import com.citel.statistics.dto.query.AgeRangeQuery;
import com.citel.statistics.dto.query.MileageRangeQuery;
import com.citel.statistics.dto.query.QueryRange;
import com.citel.statistics.dto.query.TimeRangeQuery;
import com.citel.statistics.dto.vo.PersonVO;
import com.citel.statistics.mapper.PersonMapper;
import com.citel.statistics.service.QueryService;
import com.citel.statistics.util.RangeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 三种查询模式服务实现
 */
@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    private static final int MAX_PAGE_SIZE = 20;

    private final PersonMapper personMapper;
    private final AppProperties appProperties;

    @Override
    public PageResult<PersonVO> queryByAge(AgeRangeQuery query) {
        RangeValidator.validate(query.getAgeRanges(), "年龄", false);
        return doQuery("age", query.getAgeRanges(), query.getCurrent(), query.getSize());
    }

    @Override
    public PageResult<PersonVO> queryByMileage(MileageRangeQuery query) {
        RangeValidator.validate(query.getMileageRanges(), "里程", false);
        return doQuery("mileage", query.getMileageRanges(), query.getCurrent(), query.getSize());
    }

    @Override
    public PageResult<PersonVO> queryByTime(TimeRangeQuery query) {
        RangeValidator.validate(query.getTimeRanges(), "时间", true);
        return doQuery("time", query.getTimeRanges(), query.getCurrent(), query.getSize());
    }

    /** 统一执行多区间分页查询 */
    private PageResult<PersonVO> doQuery(String mode, List<QueryRange> ranges,
                                         Integer current, Integer size) {
        int pageNum = Math.max(current == null ? 1 : current, 1);
        int pageSize = Math.min(Math.max(size == null ? 20 : size, 1), MAX_PAGE_SIZE);
        int offset = (pageNum - 1) * pageSize;
        int refYear = appProperties.getAgeReferenceYear();
        List<PersonVO> records = personMapper.selectPageByRanges(mode, ranges, refYear, offset, pageSize);
        long total = personMapper.selectCountByRanges(mode, ranges, refYear);
        return PageResult.of(total, records, pageNum, pageSize);
    }
}
