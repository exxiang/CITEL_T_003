package com.citel.statistics.controller;

import com.citel.statistics.common.PageResult;
import com.citel.statistics.common.Result;
import com.citel.statistics.dto.query.AgeRangeQuery;
import com.citel.statistics.dto.query.MileageRangeQuery;
import com.citel.statistics.dto.query.TimeRangeQuery;
import com.citel.statistics.dto.vo.PersonVO;
import com.citel.statistics.service.QueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 三种查询模式接口
 */
@RestController
@RequestMapping("/api/persons/query")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    /** 按年龄区间查询（多个年龄段，允许重叠） */
    @PostMapping("/age")
    public Result<PageResult<PersonVO>> queryByAge(@RequestBody @Valid AgeRangeQuery query) {
        return Result.ok(queryService.queryByAge(query));
    }

    /** 按飞行里程区间查询（多个区间，允许重叠） */
    @PostMapping("/mileage")
    public Result<PageResult<PersonVO>> queryByMileage(@RequestBody @Valid MileageRangeQuery query) {
        return Result.ok(queryService.queryByMileage(query));
    }

    /** 按飞行时间区间查询（多个区间，不允许重叠） */
    @PostMapping("/time")
    public Result<PageResult<PersonVO>> queryByTime(@RequestBody @Valid TimeRangeQuery query) {
        return Result.ok(queryService.queryByTime(query));
    }
}
