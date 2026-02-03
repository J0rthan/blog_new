# status message code 的使用场景

实例：一个标准的错误返回样例。

```json
{
  "code": "USER_USERNAME_TAKEN",
  "message": "Username already exists",
  "status": 409,
  "path": "/api/auth/register",
  "timestamp": "2026-02-02T08:20:10Z"
}
```

## status

HTTP状态码，用来大致区分消息类型。

具体解释如下：[HTTP status code](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status)

常见含义（登录/注册最常用）：

- **200/201**：成功
- **400 Bad Request**：请求参数不合法（缺字段、格式错误）
- **401 Unauthorized**：未登录 / token 无效 / 账号密码错误（认证失败）
- **403 Forbidden**：已登录但无权限
- **404 Not Found**：资源不存在
- **409 Conflict**：资源冲突（用户名已存在）
- **500**：服务端异常

它的作用：浏览器/网关/前端库能直接根据状态码做通用处理（比如 401 跳转登录页）。

## message

人类可读的说明，是用来给程序员看的错误信息，方便快速查找错误源。

例如：

- "username不能为空"
- "Invalid username or password"
- "Missing Authorization token"

注意：生产环境里 message 不建议把敏感细节（比如数据库异常、栈信息）直接返回给前端。

## code

**code** 是你自己定义的“业务错误标识”，比 status 更细。

code使用来方便前端识别并绑定的，是给“代码”读的。

为什么需要它？

- status 太粗：400 可能有十几种原因，401 也可能有多种原因
- 前端需要稳定的标识来写逻辑，而不是靠解析 message 文本

例子：

- REQ_VALIDATION_FAILED（参数校验失败）
- REQ_MALFORMED_JSON（JSON 格式错误）
- USER_USERNAME_TAKEN（用户名已被占用）
- AUTH_INVALID_CREDENTIALS（账号或密码错误）
- AUTH_TOKEN_MISSING（缺 token）
- AUTH_TOKEN_INVALID（token 无效）

前端典型用法：

- code == USER_USERNAME_TAKEN → 提示“用户名已存在”，并高亮用户名输入框
- code == AUTH_TOKEN_INVALID → 清理本地 token 并跳转登录