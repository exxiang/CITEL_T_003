-- ============================================================
-- 人员旅行信息查询统计系统 初始化脚本
-- 适用数据库：MySQL 8.x
-- 数据格式：人员ID;性别(0-女,1-男);出生年份;总旅行里程(飞行里程);总旅行时间(飞行时间)
-- 说明：仅创建库与表结构，不含示例数据
-- 执行方式：mysql -u root -p < sql/init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS `citel_statistics`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `citel_statistics`;

-- ------------------------------------------------------------
-- 1. 人员信息表（对应测试数据 5 列）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `person` (
  `id`                BIGINT        NOT NULL AUTO_INCREMENT COMMENT '人员ID(数据第1列)',
  `gender`            TINYINT       NOT NULL                COMMENT '性别 0-女 1-男(数据第2列)',
  `birth_year`        INT           NOT NULL                COMMENT '出生年份(数据第3列)',
  `total_mileage`     DECIMAL(12,2) NOT NULL                COMMENT '总旅行里程(飞行里程)-公里(数据第4列)',
  `total_travel_time` DECIMAL(10,2) NOT NULL                COMMENT '总旅行时间(飞行时间)-小时(数据第5列)',
  `created_at`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_gender` (`gender`),
  KEY `idx_birth_year` (`birth_year`),
  KEY `idx_total_mileage` (`total_mileage`),
  KEY `idx_total_travel_time` (`total_travel_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员信息表';

-- ------------------------------------------------------------
-- 2. 查询区间保存表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `query_condition` (
  `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`        BIGINT        NOT NULL                COMMENT '所属用户ID',
  `condition_name` VARCHAR(100)  NOT NULL                COMMENT '条件名称',
  `query_param`    JSON          NOT NULL                COMMENT '查询区间JSON(见设计文档4.5)',
  `created_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='查询区间保存表';

-- ------------------------------------------------------------
-- 3. 系统用户表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`   VARCHAR(64)  NOT NULL                COMMENT '登录名',
  `password`   VARCHAR(128) NOT NULL                COMMENT '密码(BCrypt)',
  `real_name`  VARCHAR(64)           DEFAULT NULL   COMMENT '姓名',
  `status`     TINYINT      NOT NULL DEFAULT 1      COMMENT '1-启用 0-禁用',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';
