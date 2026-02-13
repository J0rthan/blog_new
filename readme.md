# 项目测试所用的curl指令

> [!NOTE]
>
> 项目指南有开发使用到的细节，中英夹杂
>
> 指南里面部分md用gpt5.2生成的(gpt5.2貌似写的比我更好😁)

## 项目角色介绍

项目目前只有两个角色：

- 作者（兼管理员，该网站只有一个作者）
- 用户（浏览网站，只能发表评论和浏览网站内容）

## 注册和登录功能
注册：

```bash
curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{"userName": "{your name}", "email": "{your email address}", "password": "{your password}"}'
```

登陆：

```bash
curl -X POST http://localhost:8080/api/auth/login \   
-H "Content-Type: application/json" \
-d '{"email": "{your email}", "password": "{your password}"}'        
```

## 稿件模块

### 提交一篇文章

```bash
curl -X POST http://localhost:8080/api/post/submit \      
-H "Content-Type: application/json" \
-H "Auth-Token: {your-token}" \
-d '{"title": "***", "content": "***", "summary": "***"}' # 自行填充
```

### 查看所有文章

```bash
curl -X GET http://localhost:8080/api/post/list
```

### 修改一篇文章

```bash
curl -X POST http://localhost:8080/api/post/update/{postId} \
-H "Content-Type: application/json" \
-H "Auth-Token: {your token}" \
-d '{"title": ***, "content": "***", "summary": "***"}'
```

### 阅读某一篇文章

```bash
curl -X GET http://localhost:8080/api/post/read/{postId}
```

### 删除一篇文章

```bash
curl (-i) -X POST http://localhost:8080/api/post/delete/{postId} \                       
-H "Auth-Token: {your token}"
```

### 恢复一篇文章

```bash
curl (-i) -X POST http://localhost:8080/api/post/restore/{postId} \                     
-H "Auth-Token: {your token}"
```

## 评论模块

### 查找一篇文章的所有评论

```bash
curl (-i) -X GET http://localhost:8080/api/comment/list/{postId}
```

### 对一篇文章进行评论

```bash
curl (-i) -X POST http://localhost:8080/api/comment/submit/{postId} \
-H "Content-Type: application/json" \
-H "Auth-Token: {your token}" \
-d '{"content": "***"}'
```

### 删除一篇文章的某一评论

```bash
curl (-i) -X POST http://localhost:8080/api/comment/delete/{postId}/{commentId} \
-H "Auth-Token: {your token}" \
```

