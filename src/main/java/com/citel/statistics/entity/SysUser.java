package com.citel.statistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户实体（可选）
 */
@Data
@TableName("sys_user")
public class SysUser {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录名 */
    private String username;

    /** 密码（BCrypt） */
    private String password;

    /** 姓名 */
    private String realName;

    /** 状态 1-启用 0-禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
