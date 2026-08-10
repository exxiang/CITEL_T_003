package com.citel.statistics.controller;

import com.citel.statistics.common.Result;
import com.citel.statistics.dto.request.SaveConditionRequest;
import com.citel.statistics.entity.QueryCondition;
import com.citel.statistics.service.QueryConditionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 查询区间条件保存接口
 */
@RestController
@RequestMapping("/api/conditions")
@RequiredArgsConstructor
public class QueryConditionController {

    private final QueryConditionService queryConditionService;

    /** 保存查询区间条件 */
    @PostMapping
    public Result<Long> save(@RequestBody @Valid SaveConditionRequest request) {
        return Result.ok(queryConditionService.save(request).getId());
    }

    /** 条件列表 */
    @GetMapping
    public Result<List<QueryCondition>> list() {
        return Result.ok(queryConditionService.list());
    }

    /** 条件详情（重新加载用） */
    @GetMapping("/{id}")
    public Result<QueryCondition> detail(@PathVariable Long id) {
        return Result.ok(queryConditionService.getById(id));
    }

    /** 更新条件 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid SaveConditionRequest request) {
        queryConditionService.update(id, request);
        return Result.ok();
    }

    /** 删除条件 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        queryConditionService.delete(id);
        return Result.ok();
    }
}
