# SpringBoot ExceptionHandler

> [!NOTE]
>
> 基于gpt生成，基本意思无误

本文梳理在 **Spring Boot（Spring MVC）** 中一次 HTTP 请求从进入到返回的完整执行链路，并重点解释 **拦截器 `preHandle` 抛异常** 时，为什么会被 `@RestControllerAdvice` 中的 `@ExceptionHandler` 捕获并统一返回 JSON 错误响应。

---

## 1. 一次请求的主链路（正常无异常）

以 `POST /api/auth/register` 为例：

1. **容器接收请求**（如 Tomcat），创建 `HttpServletRequest req` / `HttpServletResponse resp`
2. 进入 Spring MVC 总入口：**`DispatcherServlet`**
3. **选择 Handler（Controller 方法）**  
   由 `HandlerMapping` 决定哪个 Controller 方法来处理该请求
4. **执行拦截器链（Interceptor）`preHandle`**  
   按注册顺序逐个调用拦截器的 `preHandle`
5. **执行 Controller 方法**  
   包括：`@RequestBody` JSON 解析、`@Valid` 参数校验、业务 Service 调用、JPA 写库等
6. **写回响应**  
   返回对象序列化为 JSON 写入 `resp`
7. **拦截器收尾**  
   - `postHandle`（一般用于视图渲染前后处理，纯 REST 场景通常用得少）
   - `afterCompletion`（无论正常/异常，通常都用于资源清理、日志记录等）

---

## 2. 异常处理的总体原则

> 在同一次请求的处理线程内抛出的异常，会沿调用栈冒泡到 `DispatcherServlet`，由 Spring MVC 的异常解析机制定位合适的 `@ExceptionHandler`，最终生成统一的错误响应。

因此，异常可能来自：

- Interceptor：`preHandle`
- Controller 入参解析：JSON 解析失败（`HttpMessageNotReadableException`）
- Controller 校验：`@Valid` 校验失败（`MethodArgumentNotValidException`）
- Service / Repository：业务校验失败、数据库约束冲突等（例如你自定义 `ApiException`）

---

## 3. 场景 A：`Interceptor#preHandle` 抛异常 —— 为什么能进入 `@ExceptionHandler`

### 3.1 典型代码（推荐做法）

```java
@Override
public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
    String token = req.getHeader("Authorization");
    if (token == null || token.isBlank()) {
        throw new ApiExceptions.Unauthorized("AUTH_TOKEN_MISSING", "Missing Authorization token");
    }
    Long userId = tokenStore.getUserId(token);
    if (userId == null) {
        throw new ApiExceptions.Unauthorized("AUTH_TOKEN_INVALID", "Invalid token");
    }
    req.setAttribute("userId", userId);
    return true;
}
```

### 3.2 执行顺序（真实）

1. `DispatcherServlet` 选定 handler（目标 Controller 方法）
2. 调用拦截器链的 `preHandle`
3. `preHandle` 抛出 `ApiException`
4. 异常沿栈向上冒泡回到 `DispatcherServlet`
5. `DispatcherServlet` 启动异常解析流程：  
   在 `@RestControllerAdvice` 中寻找匹配的 `@ExceptionHandler`
6. 找到 `@ExceptionHandler(ApiException.class)` 后，调用异常处理方法  
   并通过参数解析机制自动注入：
   - `ApiException e`（刚抛出的异常对象）
   - `HttpServletRequest req`（当前这次请求对象）
7. 返回 `ResponseEntity<ErrorResponse>`，Spring 写回 JSON 响应，请求结束

### 3.3 结论

- `preHandle` 抛异常时：**Controller 不会执行**
- 统一错误响应由 `@RestControllerAdvice` 输出
- `req` 能注入到异常处理器：因为它属于同一次请求、同一线程且请求生命周期尚未结束

---

## 4. 场景 B：`preHandle` 返回 `false`（不抛异常）—— 与 `@ExceptionHandler` 的差异

有些写法会在 `preHandle` 内手动写响应并 `return false`：

```java
resp.setStatus(401);
resp.getWriter().write("Unauthorized");
return false;
```

此时：

- Spring MVC 不会进入 Controller
- **也不会进入 `@ExceptionHandler`**（因为没有异常被抛出）
- 响应由拦截器直接输出

### 建议

为保持错误响应结构一致，通常更推荐：

> 认证/鉴权失败 **直接 throw 业务异常**（如 `ApiException`），统一交给 `@RestControllerAdvice`。

---

## 5. 场景 C：JSON 解析失败与参数校验失败（Controller 入参阶段）

### 5.1 JSON 格式错误 / 字段类型不匹配

- 典型异常：`HttpMessageNotReadableException`
- 触发位置：`@RequestBody` 反序列化阶段

处理方式：

```java
@ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<ErrorResponse> handleBadJson(...) { ... }
```

### 5.2 `@Valid` 校验失败（如 `@NotBlank`）

- 典型异常：`MethodArgumentNotValidException`
- 触发位置：Controller 方法入参校验阶段

处理方式：

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidation(...) { ... }
```

---

## 6. 场景 D：Service / Repository 抛异常（业务与写库阶段）

常见来源：

- 业务校验失败：例如用户名已存在 → 抛 `Conflict`
- 数据库唯一约束冲突：可能抛 `DataIntegrityViolationException`
- 其它系统异常：最终落入兜底 `@ExceptionHandler(Exception.class)`

同样流程：

> 异常冒泡 → `DispatcherServlet` 捕获 → 匹配 `@ExceptionHandler` → 返回统一 JSON。

---

## 7. `@ExceptionHandler` 为什么能拿到 `HttpServletRequest req`

核心点：

- `req` 是容器在请求开始时创建的对象
- 在该请求结束之前（响应写回之前），`req` 都一直存在
- Spring 在调用 `@ExceptionHandler` 方法时，使用**方法参数解析机制**从当前请求上下文中取出 `HttpServletRequest` 并注入

因此：

- `req` 并不是“异常对象里带过来的”
- 而是“异常处理发生时，仍然处于同一次请求的处理过程中，Spring 能从上下文取到它”

---

## 8. 推荐实践：鉴权失败统一抛异常 + 全局统一返回

### 8.1 拦截器只做鉴权/注入用户上下文

- 成功：设置 `req.setAttribute("userId", userId)` 并返回 `true`
- 失败：抛 `ApiException`（401/403）

### 8.2 `@RestControllerAdvice` 统一格式化响应

- 所有模块（auth/post/comment 等）都遵循同一错误 JSON 结构
- 前端根据 `code` 做稳定分支处理（而不是解析 message 文本）

---

## 9. 小结

- `Interceptor#preHandle` 属于 Spring MVC 请求处理链的一部分  
- `preHandle` 抛出的异常会被 `DispatcherServlet` 捕获并交给 `@ExceptionHandler`  
- `@ExceptionHandler` 能注入 `HttpServletRequest`，因为它来自同一次请求上下文  
- 推荐用“抛异常 + 全局处理器”统一错误响应，避免拦截器自己拼 JSON 导致返回风格不一致