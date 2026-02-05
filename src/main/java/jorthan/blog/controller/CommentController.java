package jorthan.blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jorthan.blog.dtos.CommentDtos;
import jorthan.blog.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/list")// 查看评论
    public ResponseEntity<Page<CommentDtos.CommentListResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(commentService.list(pageable));
    }

    @PostMapping("/submit/{postId}") // 发表评论
    public ResponseEntity<CommentDtos.CommentListResponse> submit(@Valid @RequestBody CommentDtos.CommentRequest body, HttpServletRequest req, @PathVariable Long postId) {
        return ResponseEntity.status(201).body(commentService.submit(body, req, postId));
    }
}
