# 项目测试所用的curl指令
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

