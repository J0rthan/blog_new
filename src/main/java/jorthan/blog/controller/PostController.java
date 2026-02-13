package jorthan.blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jorthan.blog.auth.AuthInterceptor;
import jorthan.blog.dtos.PostDtos;
import jorthan.blog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/list") // 查看所有的文章
    public ResponseEntity<Page<PostDtos.PostSummaryResponse>> list(@PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(postService.list(pageable));
    }

    @PostMapping("/submit") // 提交一篇文章
    public ResponseEntity<PostDtos.PostDetailResponse> submit(@Valid @RequestBody PostDtos.PostRequest body, HttpServletRequest req) {
        // 先获取userId
        Long userId = (Long)req.getAttribute(AuthInterceptor.ATTR_USER_ID);

        return ResponseEntity.status(201).body(postService.submit(userId, body));
    }

    @GetMapping("/read/{postId}") // 查看某一篇文章
    public ResponseEntity<PostDtos.PostDetailResponse> read(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.read(postId));
    }

    @PostMapping("/update/{postId}") // 修改一篇文章
    public ResponseEntity<PostDtos.PostDetailResponse> update(@Valid @RequestBody PostDtos.PostRequest body, HttpServletRequest req, @PathVariable Long postId) {
        return ResponseEntity.ok(postService.update(body, req, postId));
    }

    @PostMapping("/delete/{postId}") // 删除一篇文章
    public ResponseEntity<PostDtos.PostDeleteResponse> delete(HttpServletRequest req, @PathVariable Long postId) {
        return ResponseEntity.ok(postService.delete(req, postId));
    }

    @PostMapping("/restore/{postId}") // 恢复一篇文章
    public ResponseEntity<PostDtos.PostDetailResponse> restore(HttpServletRequest req, @PathVariable Long postId) {
        return ResponseEntity.ok(postService.restore(req, postId));
    }
}
