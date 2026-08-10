package com.citel.statistics.service.impl;

import com.citel.statistics.config.AppProperties;
import com.citel.statistics.dto.query.AgeRangeQuery;
import com.citel.statistics.dto.query.MileageRangeQuery;
import com.citel.statistics.dto.query.QueryRange;
import com.citel.statistics.dto.query.TimeRangeQuery;
import com.citel.statistics.dto.vo.CountVO;
import com.citel.statistics.mapper.PersonMapper;
import com.citel.statistics.service.StatisticsService;
import com.citel.statistics.util.RangeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 统计服务实现
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final PersonMapper personMapper;
    private final AppProperties appProperties;

    @Override
    public List<CountVO> countByAge(AgeRangeQuery query) {
        RangeValidator.validate(query.getAgeRanges(), "年龄", false);
        return doCount("age", query.getAgeRanges(), "岁");
    }

    @Override
    public List<CountVO> countByMileage(MileageRangeQuery query) {
        RangeValidator.validate(query.getMileageRanges(), "里程", false);
        return doCount("mileage", query.getMileageRanges(), "公里");
    }

    @Override
    public List<CountVO> countByTime(TimeRangeQuery query) {
        RangeValidator.validate(query.getTimeRanges(), "时间", true);
        return doCount("time", query.getTimeRanges(), "小时");
    }

    /** 统一执行逐区间计数 */
    private List<CountVO> doCount(String mode, List<QueryRange> ranges, String unit) {
        Map<String, Object> row = personMapper.countByRanges(mode, ranges, appProperties.getAgeReferenceYear());
        List<CountVO> result = new ArrayList<>();
        for (int i = 0; i < ranges.size(); i++) {
            QueryRange range = ranges.get(i);
            Object value = findValue(row, "c_" + i);
            String label = range.getMin() + "-" + range.getMax() + unit;
            result.add(new CountVO(label, value == null ? 0L : Long.parseLong(value.toString())));
        }
        return result;
    }

    /** 从统计结果 Map 中忽略大小写查找列值 */
    private Object findValue(Map<String, Object> row, String key) {
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
