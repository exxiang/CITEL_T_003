package com.citel.statistics.controller;

import com.citel.statistics.common.PageResult;
import com.citel.statistics.common.Result;
import com.citel.statistics.dto.vo.PersonVO;
import com.citel.statistics.entity.Person;
import com.citel.statistics.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 人员数据管理接口
 */
@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    /** 新增人员 */
    @PostMapping
    public Result<Long> create(@RequestBody Person person) {
        personService.create(person);
        return Result.ok(person.getId());
    }

    /** 修改人员 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Person person) {
        personService.update(id, person);
        return Result.ok();
    }

    /** 删除人员 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        personService.delete(id);
        return Result.ok();
    }

    /** 人员详情 */
    @GetMapping("/{id}")
    public Result<PersonVO> detail(@PathVariable Long id) {
        return Result.ok(personService.getById(id));
    }

    /** 全量分页查询（每页最多20条） */
    @GetMapping("/page")
    public Result<PageResult<PersonVO>> page(@RequestParam(defaultValue = "1") int current,
                                             @RequestParam(defaultValue = "20") int size) {
        return Result.ok(personService.page(current, size));
    }
}
