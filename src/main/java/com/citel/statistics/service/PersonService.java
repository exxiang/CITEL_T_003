package com.citel.statistics.service;

import com.citel.statistics.common.PageResult;
import com.citel.statistics.dto.vo.PersonVO;
import com.citel.statistics.entity.Person;

/**
 * 人员数据服务
 */
public interface PersonService {

    /** 全量分页查询（每页最多20条） */
    PageResult<PersonVO> page(int current, int size);

    /** 查询详情 */
    PersonVO getById(Long id);

    /** 新增 */
    void create(Person person);

    /** 修改 */
    void update(Long id, Person person);

    /** 删除 */
    void delete(Long id);
}
