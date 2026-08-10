package com.citel.statistics.util;

import com.citel.statistics.common.BizException;
import com.citel.statistics.dto.query.QueryRange;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 查询区间校验工具
 */
public final class RangeValidator {

    private RangeValidator() {
    }

    /**
     * 校验区间列表
     *
     * @param ranges           区间列表
     * @param name             区间名称（用于错误提示，如"年龄"）
     * @param overlapForbidden 是否禁止区间重叠（时间区间为 true）
     */
    public static void validate(List<QueryRange> ranges, String name, boolean overlapForbidden) {
        if (ranges == null || ranges.isEmpty()) {
            throw new BizException(name + "区间不能为空");
        }
        for (QueryRange range : ranges) {
            if (range.getMin() == null || range.getMax() == null) {
                throw new BizException(name + "区间上下限不能为空");
            }
            if (range.getMin().compareTo(range.getMax()) > 0) {
                throw new BizException(name + "区间下限不能大于上限");
            }
        }
        if (overlapForbidden) {
            List<QueryRange> sorted = new ArrayList<>(ranges);
            sorted.sort(Comparator.comparing(QueryRange::getMin));
            for (int i = 1; i < sorted.size(); i++) {
                if (sorted.get(i).getMin().compareTo(sorted.get(i - 1).getMax()) < 0) {
                    throw new BizException(name + "区间不允许重叠");
                }
            }
        }
    }
}
