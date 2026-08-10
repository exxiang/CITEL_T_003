package com.citel.statistics.controller;

import com.citel.statistics.common.Result;
import com.citel.statistics.dto.query.AgeRangeQuery;
import com.citel.statistics.dto.query.MileageRangeQuery;
import com.citel.statistics.dto.query.TimeRangeQuery;
import com.citel.statistics.dto.vo.CountVO;
import com.citel.statistics.service.StatisticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计图表数据接口
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /** 年龄区间统计（柱状图/饼图数据源） */
    @PostMapping("/age")
    public Result<List<CountVO>> countByAge(@RequestBody @Valid AgeRangeQuery query) {
        return Result.ok(statisticsService.countByAge(query));
    }

    /** 里程区间统计（柱状图/饼图数据源） */
    @PostMapping("/mileage")
    public Result<List<CountVO>> countByMileage(@RequestBody @Valid MileageRangeQuery query) {
        return Result.ok(statisticsService.countByMileage(query));
    }

    /** 时间区间统计（折线图数据源） */
    @PostMapping("/time")
    public Result<List<CountVO>> countByTime(@RequestBody @Valid TimeRangeQuery query) {
        return Result.ok(statisticsService.countByTime(query));
    }
}
