# Spring Data JPA：save() 与常用内置操作速查（结合你的 Blog 项目）

> 场景对齐：你当前项目是 Spring Boot + Spring Data JPA（MySQL），有 `User`、`Post` 等实体；鉴权用 `AuthInterceptor + TokenStore`；列表接口使用 `Pageable/Page`；异常通过 `@RestControllerAdvice` 统一返回 `ErrorResponse`。

---

## 1. `save()` 到底做什么（新增 vs 修改）

在 Spring Data JPA 中，`Repository.save(entity)` 同时承担“新增”和“修改”的入口：

- **新增（INSERT）**：通常发生在 **主键 `id == null`**（常见：`@GeneratedValue` 自增主键）
- **修改（UPDATE）**：通常发生在 **主键 `id != null`**（`save()` 内部走 `merge` 或脏检查更新）

### 1.1 你项目中的典型用法
- 注册用户：创建 `User`，`id` 为 `null` → `save()` 插入新用户
- 发布文章：创建 `Post`，`id` 为 `null` → `save()` 插入新文章
- 修改文章：先 `findById(id)` 得到实体 → setTitle/setContent → `save()` 更新

### 1.2 为什么推荐“更新前先查再改再 save”
在你的项目语义里（例如“只能改自己的文章”）：
1. **确认文章存在**（否则抛 `NotFound`）
2. **确认 authorId == 当前 userId**（否则抛 `Forbidden`）
3. 再更新并保存  
这样能避免 `id != null` 但数据库并不存在该行时产生不清晰的错误路径（最终变成 500）。

---

## 2. CRUD 内置操作（JpaRepository / CrudRepository）

假设你的仓库接口类似：

```java
public interface PostRepository extends JpaRepository<Post, Long> {}
```

Spring Data 会自动提供大量常用方法，无需你手写 SQL。

### 2.1 查询类（Read）
- `findById(ID id)` → `Optional<T>`  
  你的常用模式：`orElseThrow(() -> new ApiExceptions.NotFound(...))`
- `findAll()` → `List<T>`
- `findAll(Pageable pageable)` → `Page<T>`  
  你已经用在文章列表分页：`findAll(pageable).map(this::toDto)`
- `existsById(ID id)` → `boolean`  
  适合只判断存在性、不需要加载全实体
- `count()` → `long`  
  统计表记录数（做后台仪表盘时常用）

### 2.2 写入类（Write）
- `save(T entity)` / `saveAll(Iterable<T>)`  
  新增与修改的统一入口
- `deleteById(ID id)` / `delete(T entity)` / `deleteAll(...)`  
  **物理删除**（数据彻底删除）
- `flush()` / `saveAndFlush(T entity)`  
  立即把持久化上下文的变更刷到数据库（调试/强一致场景用，平时不用频繁调用）

> 说明：如果你准备做“逻辑删除”（`deletedAt`），不建议直接用 `deleteById`，而是“更新字段”。

---

## 3. 你的项目里最常见的“派生查询”（无需 @Query）

Spring Data 能根据方法名自动生成查询（按实体字段名解析）。

### 3.1 用户
- `Optional<User> findByUsername(String username)`
- `boolean existsByUsername(String username)`（注册时防重复）
- **逻辑存在过滤**（如果你用 `exist` 字段）：  
  `Optional<User> findByIdAndExistTrue(Long id)`  
  或 `Optional<User> findByIdAndExist(Long id, Boolean exist)`

### 3.2 文章
- `Page<Post> findAllByDeletedAtIsNull(Pageable pageable)`（逻辑删除过滤）
- `Optional<Post> findByIdAndDeletedAtIsNull(Long id)`
- `Page<Post> findAllByAuthorIdAndDeletedAtIsNull(Long authorId, Pageable pageable)`

---

## 4. 自定义查询：`@Query`（JPQL vs 原生 SQL）

### 4.1 JPQL（推荐：面向实体/字段名，不是表/列名）
```java
@Query("select p from Post p where p.deletedAt is null order by p.createdAt desc")
Page<Post> listVisible(Pageable pageable);
```

### 4.2 原生 SQL（需要写真实表名/列名）
```java
@Query(value = "select * from posts where deleted_at is null order by created_at desc",
       nativeQuery = true)
Page<Post> listVisibleNative(Pageable pageable);
```

> 你项目目前用 JPQL 更合适，维护成本更低。

---

## 5. 修改类自定义操作：`@Modifying + @Transactional`

当你不想“先查再改再 save”，而是要 **直接执行 UPDATE/DELETE**，就用：

- `@Modifying`：标记这是 update/delete
- `@Transactional`：确保在事务中执行

### 5.1 逻辑删除（推荐做法之一）
```java
@Modifying
@Transactional
@Query("update Post p set p.deletedAt = CURRENT_TIMESTAMP where p.id = :id and p.deletedAt is null")
int softDelete(@Param("id") Long id);
```

通过返回行数判断是否成功：
- `1`：删除成功
- `0`：文章不存在或已删除（你可抛 `NotFound`）

### 5.2 “只能删自己的文章”（把 authorId 作为条件）
```java
@Modifying
@Transactional
@Query("update Post p set p.deletedAt = CURRENT_TIMESTAMP " +
       "where p.id = :id and p.authorId = :authorId and p.deletedAt is null")
int softDeleteByAuthor(@Param("id") Long id, @Param("authorId") Long authorId);
```

> 这种方式天然把“权限校验”融入 SQL 条件，避免先查后删的竞态窗口。
