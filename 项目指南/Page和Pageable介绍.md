# Page 与 Pageable 总结（Spring Data JPA / Spring Boot）

本文总结 `Pageable` 与 `Page` 的概念、常用用法，以及它们在项目（文章列表分页）中的实际作用与落地方式。

---

## 1. Pageable 是什么（输入：你“想要哪一页、怎么排”）

`Pageable` 表示一次分页请求的“分页参数集合”，是**查询的输入条件**，主要包含：

- `page`：页号（默认 **0-based**，即 `page=0` 表示第 1 页）
- `size`：每页条数
- `sort`：排序规则（字段 + 方向，可多个）

### 1.1 URL 参数如何映射到 Pageable

Spring MVC 会自动把 query 参数绑定到 `Pageable`：

- `GET /api/posts?page=0&size=10`
- `GET /api/posts?page=1&size=10&sort=createdAt,desc`
- 多排序：`GET /api/posts?sort=createdAt,desc&sort=id,desc`

### 1.2 默认值与限制

你可以在 Controller 里给默认分页/排序：

```java
@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
Pageable pageable
```

也可以在配置里设置默认 size 与 max size（保护服务）：

```properties
spring.data.web.pageable.default-page-size=10
spring.data.web.pageable.max-page-size=50
spring.data.web.pageable.one-indexed-parameters=false
```

> 注意：配置文件能控制默认 size/max size/是否 1-based，但“默认 sort”通常通过 `@PageableDefault` 或代码兜底完成。

---

## 2. Page 是什么（输出：这一页的数据 + 分页元信息）

`Page<T>` 表示一次分页查询的结果，是**查询输出**，包含两部分：

1. **当前页数据内容**：`List<T> content`
2. **分页元信息**（metadata）：
   - `totalElements`：符合条件的总条数
   - `totalPages`：总页数
   - `number`：当前页号（0-based）
   - `size`：每页大小
   - `hasNext/hasPrevious`：是否还有下一页/上一页
   - `sort`、`first/last` 等

### 2.1 常用方法

- `page.getContent()`
- `page.getTotalElements()`
- `page.getTotalPages()`
- `page.getNumber()`、`page.getSize()`
- `page.hasNext()`、`page.hasPrevious()`

### 2.2 `Page.map(...)` 的价值（最常用）

`Page` 支持对“内容列表”做映射并保留分页信息：

```java
Page<Post> p = postRepository.findAll(pageable);
Page<PostSummaryResponse> dtoPage = p.map(this::toDto);
```

含义：
- `content` 从 `Post` 转成 DTO
- `totalElements/totalPages/number/size/sort` 等信息保持不变

---

## 3. 它们在项目里的“实现作用”（以文章列表为例）

### 3.1 解决的问题

在“浏览文章列表”场景，分页需要同时满足：

- 前端能指定：第几页、每页多少条、按什么字段排序
- 后端返回不仅有数据，还要有：
  - 总条数（用于分页控件）
  - 总页数（用于页码展示）
  - 是否有下一页（用于下一页按钮状态）

因此：

- `Pageable` 用于承载“前端想怎么翻页”
- `Page<T>` 用于把“当前页数据 + 分页统计”一次性返回

### 3.2 典型调用链（你项目中的真实顺序）

1. Controller 接收 `Pageable`（自动从 URL 绑定）
2. Service 调用 Repository 的分页查询方法
3. Repository 返回 `Page<Entity>`
4. Service 用 `Page.map(...)` 转成 `Page<DTO>`
5. Controller 返回 `Page<DTO>` / `ResponseEntity<Page<DTO>>`
6. Spring MVC 使用 Jackson 将 `Page` 序列化为 JSON

### 3.3 为什么你代码里看不到 Page 的实现类？

你写的是接口：

```java
Page<Post> page = postRepository.findAll(pageable);
```

真正的实例由 Spring Data JPA 在内部创建（通常是 `PageImpl`），但对你暴露为 `Page` 接口，这是典型的“面向接口编程”。

---

## 4. 设计建议（结合你的项目风格）

### 4.1 列表接口尽量返回“摘要 DTO”，避免传全文

- 列表：`Page<PostSummaryResponse>`（不含 content）
- 详情：`PostDetailResponse`（包含 content）

好处：分页接口响应更轻，速度更快。

### 4.2 默认排序放在接口层（更清晰）

文章列表通常默认按 `createdAt desc`：

```java
@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
```

并允许前端通过 `sort=` 覆盖默认。

### 4.3 限制 max-page-size（保护服务）

必须配置 `spring.data.web.pageable.max-page-size` 或自行在代码里 clamp size，避免 `size=100000` 这种请求拖垮系统。

---

## 5. 一句话记忆

- **Pageable：请求我想看哪一页（输入）**
- **Page：返回这一页是什么 + 总共有多少（输出）**
- 在项目中，它们让“分页 + 排序 + 元信息”以非常少的代码实现，并且对前端友好。
