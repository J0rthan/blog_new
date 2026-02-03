# 项目测试所用的curl指令

> [!NOTE]
>
> 项目指南有开发使用到的细节，中英夹杂



## 注册和登录功能
注册：

```bash
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{"userName": "your name", "email": "your email address", "password": "your password"}'
```

登陆：

```bash
curl -X POST http://localhost:8080/api/auth/login \   
-H "Content-Type: application/json" \
-d '{"email": "your email", "password": "your password"}'        
```

