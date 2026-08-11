# 人员旅行数据查询统计系统 设计文档

| 项目名称 | 人员旅行数据查询统计系统（insis_training_009） |
| --- | --- |
| 技术栈 | Spring Boot 3.x + MySQL 8.x + MyBatis-Plus + Layui 2.8 + ECharts 5 |
| 开发工具 | IDEA / Java 17+ / Maven / Git |
| 数据来源 | 附件 insis_training_009_data.txt（存放于 data/data.txt，共 9685 条） |
| 文档版本 | v1.0 |

---

## 1. 项目概述

### 1.1 项目背景与目标

本项目基于给定的飞行人员测试数据（人员ID、性别、出生年份、总旅行里程、总旅行时间），实现一个数据查询与统计系统。系统提供按年龄、按飞行里程、按飞行时间三种查询模式，用户可自定义多个查询区间，查询结果以 Layui 数据列表和 ECharts 图表（柱状图、饼图、折线图）形式展示，并支持查询区间的保存与重新加载。

### 1.2 技术选型

| 层次 | 技术 | 说明 |
| --- | --- | --- |
| 后端 | Spring Boot 3.x + MyBatis-Plus | 分层架构，动态 SQL 支持多区间查询与统计 |
| 数据库 | MySQL 8.x | 存储人员数据与保存的查询区间 |
| 前端 | Layui 2.8 + ECharts 5 | Layui 数据表格分页展示；ECharts 图表渲染 |
| 构建/版本 | Maven + Git | 依赖管理；版本库管理代码 |

### 1.3 数据说明

| 字段序号 | 字段说明 | 类型 | 备注 |
| --- | --- | --- | --- |
| 1 | 人员ID | 数字 | 唯一标识 |
| 2 | 性别 | 数字 | 0-女，1-男 |
| 3 | 出生年份 | 数字 | 年龄 = 参考年 - 出生年份 |
| 4 | 总旅行里程 | 数字 | 飞行里程（公里） |
| 5 | 总旅行时间 | 数字 | 飞行时间（小时） |

---

## 2. 需求分析

### 2.1 功能需求

| 编号 | 需求 | 说明 |
| --- | --- | --- |
| F1 | 数据存储 | 测试数据建表并导入 MySQL |
| F2 | 按年龄查询 | 自定义多个年龄段（**允许重叠**），查询符合任一区间的人员，列表 + 柱状图/饼图 |
| F3 | 按飞行里程查询 | 自定义多个里程区间（**允许重叠**），列表 + 柱状图/饼图 |
| F4 | 按飞行时间查询 | 自定义多个时间区间（**不允许重叠**），列表 + 折线图 |
| F5 | 结果展示切换 | 列表（Layui，每页最多 20 条）与图表（ECharts）可切换展示 |
| F6 | 图表口径 | 横轴为用户自定义区间，纵轴为该区间命中的记录数，所有区间在一张图中展示 |
| F7 | 区间保存 | 用户自定义区间可保存、重新加载使用 |

### 2.2 非功能需求

- 代码编写与注释执行《C/C++、Java编程规范》标准。
- 建立 Git 版本库并使用版本库管理系统管理代码。
- 允许在现有功能上进行扩展。

### 2.3 关键业务规则

- **重叠区间语义**：列表展示"满足任一区间"的人员（OR 条件，天然去重，每人至多出现一次）；统计按区间分别计数，重叠区间中的同一人员会重复计入多个区间（符合"统计不同区间的数据记录数量"要求）。
- **时间区间约束**：时间区间两两不得重叠，后端参数校验拒绝非法区间。
- **年龄派生**：年龄不落库，查询时计算 `年龄 = 参考年 - 出生年份`，参考年默认取当年（`YEAR(CURDATE())`），可在 `application.yml` 中配置固定值，保证查询与统计口径一致。
- **分页上限**：`size ∈ [1, 20]`，强制每页最多 20 条。

---

## 3. 总体设计

### 3.1 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                     前端 Layui + ECharts                 │
│  查询页(三模式Tab + 区间编辑 + 列表/图表切换) · 条件管理页 │
└──────────────────────────┬──────────────────────────────┘
                           │ HTTP / JSON (REST)
┌──────────────────────────▼──────────────────────────────┐
│                    后端 Spring Boot                      │
│  Controller层：Person / Query / Statistics / Condition   │
│  Service 层：区间校验、查询组装、统计计数                 │
│  Mapper 层：MyBatis-Plus + 动态 SQL（foreach OR/CASE）   │
└──────────────────────────┬──────────────────────────────┘
                           │ JDBC
┌──────────────────────────▼──────────────────────────────┐
│              MySQL（person / query_condition）           │
└─────────────────────────────────────────────────────────┘
```

### 3.2 功能模块划分

| 模块 | 职责 | 关键功能 |
| --- | --- | --- |
| 数据管理 | 人员基础数据维护 | 新增/修改/删除/详情/全量分页（每页≤20条） |
| 年龄区间查询 | 按年龄段查询 | 自定义多个年龄段（允许重叠），列表 + 柱状图 + 饼图 |
| 里程区间查询 | 按飞行里程查询 | 自定义多个里程区间（允许重叠），列表 + 柱状图 + 饼图 |
| 时间区间查询 | 按飞行时间查询 | 自定义多个时间区间（不允许重叠，后端校验），列表 + 折线图 |
| 区间保存 | 查询区间持久化 | 保存/加载/更新/删除区间，重新加载后回填表单并重新查询 |
| 系统管理 | 基础支撑 | 用户登录（可选）、统一响应、全局异常、参数校验 |

### 3.3 通用设计

- **统一响应体**：`Result<T>{code, message, data}`，`code=0` 表示成功。
- **分页响应**：`PageResult{total, records, current, size}`。
- **全局异常**：`@RestControllerAdvice` 统一处理业务异常、参数校验异常、未知异常。
- **区间模型**：`QueryRange{min, max}`，三种模式统一复用，查询与统计共用同一组区间参数。

---

## 4. 数据库设计

### 4.1 表清单

| 表名 | 说明 |
| --- | --- |
| `person` | 人员信息表（查询与统计的数据源） |
| `query_condition` | 保存的查询区间表 |
| `sys_user` | 系统用户表（可选） |

### 4.2 建表 SQL（完整脚本见 sql/init.sql）

```sql
CREATE TABLE `person` (
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
```

### 4.3 索引设计

- `idx_birth_year`：年龄区间查询时转换为出生年份区间，命中索引。
- `idx_total_mileage` / `idx_total_travel_time`：里程、时间区间查询。
- `idx_gender`：性别筛选（扩展功能）。

### 4.4 多区间查询与统计 SQL 设计

列表查询（多区间 OR 组合，满足任一区间即命中）：

```sql
-- 年龄模式示例（REF_YEAR 为参考年，默认 YEAR(CURDATE())）
WHERE (REF_YEAR - birth_year BETWEEN 10 AND 20)
   OR (REF_YEAR - birth_year BETWEEN 18 AND 25)

-- 里程模式示例
WHERE (total_mileage BETWEEN 0 AND 5000)
   OR (total_mileage BETWEEN 5000 AND 10000)
```

统计（逐区间计数，所有区间一张图，Mapper XML 用 foreach 动态生成）：

```sql
SELECT
  SUM(CASE WHEN REF_YEAR - birth_year BETWEEN 10 AND 20 THEN 1 ELSE 0 END) AS cnt_1,
  SUM(CASE WHEN REF_YEAR - birth_year BETWEEN 18 AND 25 THEN 1 ELSE 0 END) AS cnt_2
FROM person;
```

### 4.5 保存区间的 JSON 结构（query_condition.query_param）

```json
{
  "ageRanges":     [{"min": 10, "max": 20}, {"min": 18, "max": 25}],
  "mileageRanges": [{"min": 0, "max": 5000}, {"min": 5000, "max": 10000}],
  "timeRanges":    [{"min": 0, "max": 5}, {"min": 5, "max": 10}]
}
```

---

## 5. REST 接口设计

统一前缀 `/api`，统一响应 `{code, message, data}`。

### 5.1 数据管理

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/persons` | 新增人员 |
| PUT | `/api/persons/{id}` | 修改人员 |
| DELETE | `/api/persons/{id}` | 删除人员 |
| GET | `/api/persons/{id}` | 人员详情 |
| GET | `/api/persons/page` | 全量分页（size ≤ 20） |

### 5.2 三种查询模式（核心）

| 模式 | 方法/路径 | 请求体 |
| --- | --- | --- |
| 按年龄 | `POST /api/persons/query/age` | `{"ageRanges":[{"min":10,"max":20},{"min":18,"max":25}], "current":1, "size":20}` |
| 按里程 | `POST /api/persons/query/mileage` | `{"mileageRanges":[{"min":0,"max":5000}], "current":1, "size":20}` |
| 按时间 | `POST /api/persons/query/time` | `{"timeRanges":[{"min":0,"max":5},{"min":5,"max":10}], "current":1, "size":20}` |

响应（三种模式结构一致）：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "total": 36,
    "records": [
      {"id": 63172, "gender": 1, "birthYear": 1950, "age": 76,
       "totalMileage": 18545.00, "totalTravelTime": 11.00}
    ],
    "current": 1,
    "size": 20
  }
}
```

校验规则：区间非空且 `min ≤ max`；时间区间两两不得重叠；`size ∈ [1, 20]`。

### 5.3 统计接口（返回每个用户自定义区间的记录数）

| 模式 | 方法/路径 | 请求体 | 前端图表 |
| --- | --- | --- | --- |
| 年龄 | `POST /api/statistics/age` | `{"ageRanges":[...]}` | 柱状图 / 饼图 |
| 里程 | `POST /api/statistics/mileage` | `{"mileageRanges":[...]}` | 柱状图 / 饼图 |
| 时间 | `POST /api/statistics/time` | `{"timeRanges":[...]}` | 折线图 |

响应：

```json
{
  "code": 0,
  "message": "success",
  "data": [
    {"label": "10-20岁", "value": 12},
    {"label": "18-25岁", "value": 24}
  ]
}
```

### 5.4 区间保存与重新加载

| 方法 | 路径 | 说明 | 请求体 |
| --- | --- | --- | --- |
| POST | `/api/conditions` | 保存区间条件 | `{conditionName, ageRanges, mileageRanges, timeRanges}` |
| GET | `/api/conditions` | 我的条件列表 | - |
| GET | `/api/conditions/{id}` | 条件详情 | - |
| PUT | `/api/conditions/{id}` | 更新条件 | 同上 |
| DELETE | `/api/conditions/{id}` | 删除条件 | - |

重新加载流程：前端 `GET /api/conditions/{id}` → 将返回的 `ageRanges / mileageRanges / timeRanges` 回填对应模式的区间编辑区 → 自动触发查询与统计。

### 5.5 用户认证（可选）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录，返回 Token |
| POST | `/api/auth/logout` | 退出 |
| GET | `/api/auth/me` | 当前用户信息 |

---

## 6. 前端设计

### 6.1 页面结构

系统为单页应用（SPA），所有功能集中在 `index.html`：

- 左侧导航栏：查询统计、区间管理，点击后切换内容面板。
- 查询统计面板：三个 Tab（按年龄 / 按里程 / 按时间），支持已保存区间下拉选择回填。
- 区间管理面板：已保存区间列表（加载 / 删除）。
- 旧页面 `pages/query.html`、`pages/conditions.html` 保留为跳转到 `index.html` 的入口。

查询页每个 Tab 包含三部分：

1. **区间编辑区**：Layui 表单 + 表格动态增删区间行（min/max 输入框），年龄、里程模式允许多个任意区间；时间模式添加区间时做两两不重叠校验提示。
2. **结果列表区**：Layui `table` 组件，每页最多 20 条，列：人员ID、性别、出生年份、年龄、总旅行里程、总旅行时间。
3. **图表区**：ECharts 容器，展示切换按钮（列表 / 柱状图 / 饼图 / 折线图），按模式限制可用图表。

### 6.2 展示切换

- 列表：Layui `table.render` 对接分页接口。
- 柱状图 / 饼图 / 折线图：ECharts `init` + `setOption`，数据源为统计接口返回的 `[{label, value}]`。
- 切换逻辑：同一份区间条件触发查询与统计，切换展示样式时重新渲染，保证口径一致。

### 6.3 关键交互

- 定义/修改区间 → 点击"查询"→ 同时刷新列表与图表。
- 点击"保存区间"→ 弹窗输入条件名称 → `POST /api/conditions`。
- 查询页下拉选择已保存区间 → 回填三种模式的区间输入框，可编辑后点击"查询"。
- 区间管理面板点击"加载"→ 切回查询面板并回填区间。

---

## 7. 工程结构

```
CITEL_T_003/
├── pom.xml
├── .gitignore
├── README.md
├── docs/
│   └── system-design.md            # 本文档
├── data/
│   └── data.txt                    # 测试数据(insis_training_009_data.txt)
├── sql/
│   ├── init.sql                    # 建库建表
│   └── data.sql                    # 测试数据导入(9685条)
├── src/main/java/com/citel/statistics/
│   ├── StatisticsApplication.java  # 启动类
│   ├── common/                     # Result / PageResult / BizException / GlobalExceptionHandler
│   ├── config/                     # MybatisPlusConfig / WebConfig
│   ├── controller/                 # PersonController / QueryController / StatisticsController /
│   │                               # QueryConditionController / AuthController
│   ├── service/                    # PersonService / QueryService / StatisticsService /
│   │                               # QueryConditionService / impl/
│   ├── mapper/                     # PersonMapper / QueryConditionMapper / xml/
│   ├── entity/                     # Person / QueryCondition / SysUser
│   ├── dto/                        # query/(AgeRangeQuery, MileageRangeQuery, TimeRangeQuery, QueryRange)
│   │                               # vo/(CountVO, PersonVO) / request/(SaveConditionRequest, LoginRequest)
│   └── util/                       # JwtUtil
├── src/main/resources/
│   ├── application.yml             # 数据源 / 参考年 / 端口配置
│   ├── mapper/                     # Mapper XML
│   └── static/                     # Layui + ECharts 前端（单页）
│       ├── index.html              # 单页应用入口（左侧导航 + 查询 / 区间管理）
│       ├── css/app.css             # 企业级页面样式
│       ├── js/                     # api.js app.js range.js query.js chart.js conditions.js
│       ├── pages/                  # 旧页面，重定向到 index.html
│       └── lib/                    # layui / echarts
```

---

## 8. 部署与测试

### 8.1 环境准备

- JDK 17+、Maven 3.8+、MySQL 8.x、IDEA。
- 创建数据库并执行脚本：

```bash
mysql -u root -p < sql/init.sql    # 建库建表
mysql -u root -p < sql/data.sql    # 导入测试数据(9685条)
```

### 8.2 数据导入校验

```sql
SELECT COUNT(*) FROM person;               -- 期望 9685
SELECT COUNT(DISTINCT id) FROM person;     -- 期望 9685
```

### 8.3 启动运行

- IDEA 中导入 Maven 工程，配置 `application.yml` 数据源（host/port/username/password），运行 `StatisticsApplication`。
- 浏览器访问 `http://localhost:8080/`。

### 8.4 测试要点

| 测试项 | 用例示例 | 预期 |
| --- | --- | --- |
| 年龄多区间（重叠） | 年龄段 [10,20]、[18,25] | 列表返回满足任一区间的人员；统计图中 18-20 岁人员在两个区间都计数 |
| 时间区间重叠校验 | [0,5]、[3,8] | 接口返回参数错误 |
| 分页上限 | size=100 | 实际每页最多 20 条 |
| 区间保存/加载 | 保存后重新加载 | 表单回填一致，查询结果一致 |

---

## 9. 需求符合性对照表

| # | 需求 | 设计实现 | 状态 |
| --- | --- | --- | --- |
| 1 | IDEA + Spring Boot | Spring Boot 3.x 工程，IDEA 开发 | 待编码 |
| 2 | 数据文件自建表导入 | `sql/init.sql` + `sql/data.sql` + 导入说明 | 已完成 |
| 3 | 字段存储 MySQL | `person` 表 5 列与数据一一对应 | 已完成 |
| 4 | 按年龄查询，多年龄段可重叠 | `POST /api/persons/query/age`，OR 组合 | 待编码 |
| 5 | 按飞行里程查询，多区间可重叠 | `POST /api/persons/query/mileage` | 待编码 |
| 6 | 按飞行时间查询，多区间不可重叠 | `POST /api/persons/query/time` + 重叠校验 | 待编码 |
| 7 | Layui 列表，每页最多 20 条 | `PageResult` 强制 size ≤ 20 | 待编码 |
| 8 | ECharts 柱/饼/折线，所有区间一张图 | 统计接口返回 `[{label,value}]` + chart.js | 待编码 |
| 9 | 展示样式可切换 | 前端列表/图表切换按钮 | 待编码 |
| 10 | 查询区间保存/重新加载 | `/api/conditions` + 回填流程 | 待编码 |
| 11 | 《C/C++、Java编程规范》 | 命名/Javadoc/异常处理规范落实到编码 | 编码时落实 |
| 12 | Git 版本库 | 仓库已初始化 + `.gitignore` | 已完成 |
| 13 | 允许扩展 | 性别筛选、Excel 导入导出等作为可选扩展 | 预留 |

---

## 10. 扩展建议

- **性别维度筛选**：查询条件增加 `gender` 参数，利用 `idx_gender` 索引。
- **数据导入接口**：提供文件上传批量导入，页面化导入流程。
- **数据导出**：查询结果导出 Excel（EasyExcel）。
- **图表增强**：增加性别分布饼图、里程/时间累计分布等统计。
- **多用户**：启用 `sys_user` 与 JWT 登录，条件按用户隔离。
