package jorthan.blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jorthan.blog.auth.AuthInterceptor;
import jorthan.blog.dtos.PostDtos;
import jorthan.blog.service.PostService;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping // 查看所有的文章
    public ResponseEntity<Page<PostDtos.PostSummaryResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(postService.list(pageable));
    }

    @PostMapping("/submit") // 提交一篇文章
    public ResponseEntity<PostDtos.PostDetailResponse> submit(@Valid @RequestBody PostDtos.PostSubmitRequest body, HttpServletRequest req, Pageable pageable) {
        // 先获取userId
        Long userId = (Long)req.getAttribute(AuthInterceptor.ATTR_USER_ID);

        return ResponseEntity.status(201).body(postService.submit(pageable, userId, body));
    }
}
