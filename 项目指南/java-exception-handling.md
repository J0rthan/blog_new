# Java 异常处理速查文档（try-catch / try-catch-finally / throw / throws）

本文面向 Java/Spring 项目开发，系统梳理异常处理的核心语法与常见实践：  
- `try-catch`  
- `try-catch-finally`  
- `throw` 与 `throws`  
- checked exception 与 unchecked exception  
- 在 Web API（如 Spring Boot）里的推荐写法

---

## 1. 异常分类：Checked vs Unchecked

### 1.1 Checked Exception（受检异常）
- 典型：`IOException`、`SQLException`
- **编译期强制**：必须 **捕获（try-catch）** 或 **声明（throws）**

### 1.2 Unchecked Exception（非受检异常）
- 典型：`RuntimeException` 及其子类（`NullPointerException`、`IllegalArgumentException` 等）
- 编译器不强制捕获或声明
- 业务异常（例如你项目里的 `ApiException`）通常继承 `RuntimeException`，方便在业务链路中直接抛出，由统一异常处理器转换为 HTTP 响应

---

## 2. try-catch：捕获并处理异常

### 2.1 基本语法
```java
try {
    // 可能抛异常的代码
} catch (ExceptionType e) {
    // 处理异常
}
```

### 2.2 示例：捕获 IOException
```java
try {
    String text = java.nio.file.Files.readString(java.nio.file.Path.of("a.txt"));
    System.out.println(text);
} catch (java.io.IOException e) {
    System.out.println("读取失败：" + e.getMessage());
}
```

### 2.3 多个 catch（从“更具体”到“更泛化”）
```java
try {
    // ...
} catch (java.io.FileNotFoundException e) {
    // 更具体
} catch (java.io.IOException e) {
    // 更泛化
}
```

---

## 3. try-catch-finally：无论是否异常都执行 finally

### 3.1 finally 的意义
- **资源释放**：关闭文件、连接、锁等
- **收尾操作**：恢复状态、清理临时数据

### 3.2 语法与示例
```java
java.sql.Connection conn = null;
try {
    conn = dataSource.getConnection();
    // 使用 conn 做数据库操作
} catch (java.sql.SQLException e) {
    // 记录日志 / 转业务异常
} finally {
    if (conn != null) {
        try { conn.close(); } catch (java.sql.SQLException ignored) {}
    }
}
```

> 注意：`finally` 会执行，即使 `try` 中发生异常或 `return`；  
> 但如果 JVM 直接终止（例如 `System.exit`），finally 可能不会执行。

---

## 4. try-with-resources：更现代的“自动关闭资源”（推荐）

当资源实现了 `AutoCloseable`（如 `InputStream`、`Connection`），推荐：
```java
try (var in = new java.io.FileInputStream("a.txt")) {
    // 使用 in
} catch (java.io.IOException e) {
    // 处理异常
}
```

优点：
- 自动关闭资源
- 代码更短、错误更少

---

## 5. throw：在代码里“主动抛出”异常

### 5.1 基本语法
```java
throw new IllegalArgumentException("参数不合法");
```

### 5.2 常见场景：参数校验 / 业务校验
```java
if (token == null || token.isBlank()) {
    throw new RuntimeException("Invalid token");
}
```

### 5.3 在 Spring 项目里的典型写法（业务异常）
（示意：你的 `ApiExceptions.BadRequest(...)` 之类）
```java
if (username == null || username.isBlank()) {
    throw new ApiExceptions.BadRequest("username不能为空");
}
```

---

## 6. throws：在方法签名上“声明可能抛出异常”

### 6.1 基本语法
```java
public void readFile() throws java.io.IOException {
    // ...
}
```

### 6.2 声明多个异常
```java
public void f() throws java.io.IOException, java.sql.SQLException {
    // ...
}
```

### 6.3 checked exception 的规则
受检异常必须：
- 在当前方法 **try-catch 捕获**，或
- 在方法签名 **throws 声明**（交给调用者处理）

示例（声明方式）：
```java
public String load() throws java.io.IOException {
    return java.nio.file.Files.readString(java.nio.file.Path.of("a.txt"));
}
```

---

## 7. throw vs throws：对比总结

| 关键字 | 用在哪里 | 含义 | 是否创建异常对象 |
|---|---|---|---|
| `throw` | 方法体内 | 立刻抛出一个异常 | 是（`new XxxException(...)`） |
| `throws` | 方法声明处 | 声明可能抛出哪些异常 | 否 |

记忆：
- **throw：我现在就抛**
- **throws：我可能会抛（你调用我要处理）**

---

## 8. 在 Web API（Spring Boot）中的推荐实践

### 8.1 业务异常：使用 RuntimeException（不层层 throws）
典型做法：
- Service/Interceptor 内直接 `throw new ApiExceptions.Unauthorized(...)`
- `@RestControllerAdvice` 统一捕获，返回 JSON 错误体（ErrorResponse）

这样避免：
- 控制器/服务方法签名到处 `throws`
- 前端收到不统一的错误格式

### 8.2 checked exception：捕获并转换为业务异常
例如 IO/SQL 异常：
```java
try {
    // 可能抛 IOException
} catch (java.io.IOException e) {
    throw new ApiExceptions.InternalError("文件读取失败");
}
```

并在全局异常处理里统一映射为 5xx。

---

## 9. 常见注意点（易踩坑）

1. **catch 顺序**：子类异常必须在父类异常之前，否则编译报错。  
2. **不要吞异常**：空 catch 会隐藏问题；至少记录日志或转换异常。  
3. **finally 里也可能抛异常**：必要时 try-catch 包裹。  
4. **更新/写库异常**：唯一键冲突、字段超长等可能转为 `DataIntegrityViolationException`，可在全局异常处理里映射为 409/400。  
5. **接口返回统一错误结构**：建议让全局处理器负责封装，不在每个接口里重复 try-catch（除非需要精细转换）。

---
