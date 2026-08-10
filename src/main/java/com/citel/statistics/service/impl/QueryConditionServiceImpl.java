package com.citel.statistics.service.impl;

import com.citel.statistics.common.BizException;
import com.citel.statistics.dto.request.SaveConditionRequest;
import com.citel.statistics.entity.QueryCondition;
import com.citel.statistics.mapper.QueryConditionMapper;
import com.citel.statistics.service.QueryConditionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询区间条件服务实现
 */
@Service
@RequiredArgsConstructor
public class QueryConditionServiceImpl implements QueryConditionService {

    /** 未启用登录时默认用户ID */
    private static final long DEFAULT_USER_ID = 0L;

    private final QueryConditionMapper queryConditionMapper;
    private final ObjectMapper objectMapper;

    @Override
    public QueryCondition save(SaveConditionRequest request) {
        QueryCondition condition = new QueryCondition();
        condition.setUserId(DEFAULT_USER_ID);
        condition.setConditionName(request.getConditionName());
        condition.setQueryParam(toJson(request));
        queryConditionMapper.insert(condition);
        return condition;
    }

    @Override
    public void update(Long id, SaveConditionRequest request) {
        QueryCondition condition = queryConditionMapper.selectById(id);
        if (condition == null) {
            throw new BizException("条件不存在");
        }
        condition.setConditionName(request.getConditionName());
        condition.setQueryParam(toJson(request));
        queryConditionMapper.updateById(condition);
    }

    @Override
    public void delete(Long id) {
        queryConditionMapper.deleteById(id);
    }

    @Override
    public QueryCondition getById(Long id) {
        QueryCondition condition = queryConditionMapper.selectById(id);
        if (condition == null) {
            throw new BizException("条件不存在");
        }
        return condition;
    }

    @Override
    public List<QueryCondition> list() {
        return queryConditionMapper.selectList(null);
    }

    /** 将保存请求序列化为 query_param JSON */
    private String toJson(SaveConditionRequest request) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("ageRanges", request.getAgeRanges());
        param.put("mileageRanges", request.getMileageRanges());
        param.put("timeRanges", request.getTimeRanges());
        try {
            return objectMapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            throw new BizException("条件参数序列化失败");
        }
    }
}
