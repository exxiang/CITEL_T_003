package com.citel.statistics.common;

import lombok.Data;

import java.util.List;

/**
 * 分页响应体
 *
 * @param <T> 记录类型
 */
@Data
public class PageResult<T> {

    /** 总记录数 */
    private long total;

    /** 当前页记录 */
    private List<T> records;

    /** 当前页码，从1开始 */
    private long current;

    /** 每页条数 */
    private long size;

    public static <T> PageResult<T> of(long total, List<T> records, long current, long size) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setRecords(records);
        result.setCurrent(current);
        result.setSize(size);
        return result;
    }
}
