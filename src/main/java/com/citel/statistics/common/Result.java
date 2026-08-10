package com.citel.statistics.common;

import lombok.Data;

/**
 * 统一响应体
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    /** 状态码，0-成功，非0-失败 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 业务数据 */
    private T data;

    /** 成功（无数据） */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /** 成功（带数据） */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /** 失败 */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
