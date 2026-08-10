package com.citel.statistics.service;

import com.citel.statistics.dto.request.SaveConditionRequest;
import com.citel.statistics.entity.QueryCondition;

import java.util.List;

/**
 * 查询区间条件服务
 */
public interface QueryConditionService {

    /** 保存条件 */
    QueryCondition save(SaveConditionRequest request);

    /** 更新条件 */
    void update(Long id, SaveConditionRequest request);

    /** 删除条件 */
    void delete(Long id);

    /** 条件详情 */
    QueryCondition getById(Long id);

    /** 条件列表 */
    List<QueryCondition> list();
}
