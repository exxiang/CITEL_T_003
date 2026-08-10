package com.citel.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citel.statistics.common.BizException;
import com.citel.statistics.common.PageResult;
import com.citel.statistics.config.AppProperties;
import com.citel.statistics.dto.vo.PersonVO;
import com.citel.statistics.entity.Person;
import com.citel.statistics.mapper.PersonMapper;
import com.citel.statistics.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 人员数据服务实现
 */
@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private static final int MAX_PAGE_SIZE = 20;

    private final PersonMapper personMapper;
    private final AppProperties appProperties;

    @Override
    public PageResult<PersonVO> page(int current, int size) {
        int pageNum = Math.max(current, 1);
        int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        Page<Person> page = personMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Person>().orderByAsc(Person::getId));
        List<PersonVO> records = page.getRecords().stream().map(this::toVO).toList();
        return PageResult.of(page.getTotal(), records, pageNum, pageSize);
    }

    @Override
    public PersonVO getById(Long id) {
        Person person = personMapper.selectById(id);
        if (person == null) {
            throw new BizException("人员不存在");
        }
        return toVO(person);
    }

    @Override
    public void create(Person person) {
        if (person.getGender() == null || person.getBirthYear() == null
                || person.getTotalMileage() == null || person.getTotalTravelTime() == null) {
            throw new BizException("性别、出生年份、总旅行里程、总旅行时间不能为空");
        }
        personMapper.insert(person);
    }

    @Override
    public void update(Long id, Person person) {
        person.setId(id);
        int rows = personMapper.updateById(person);
        if (rows == 0) {
            throw new BizException("人员不存在");
        }
    }

    @Override
    public void delete(Long id) {
        personMapper.deleteById(id);
    }

    /** 实体转视图对象 */
    private PersonVO toVO(Person person) {
        PersonVO vo = new PersonVO();
        vo.setId(person.getId());
        vo.setGender(person.getGender());
        vo.setBirthYear(person.getBirthYear());
        vo.setAge(appProperties.getAgeReferenceYear() - person.getBirthYear());
        vo.setTotalMileage(person.getTotalMileage());
        vo.setTotalTravelTime(person.getTotalTravelTime());
        return vo;
    }
}
