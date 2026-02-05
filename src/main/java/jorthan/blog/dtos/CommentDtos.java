package jorthan.blog.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CommentDtos {
    // 发表评论请求
    public record CommentRequest(
            @NotBlank @Size(min = 1, max = 200) String content
    ) {}

    // 所有评论呈现响应体
    public record CommentListResponse(
            Long commentId,
            Long postId,
            String userName,
            String content,
            LocalDateTime createdAt
    ) {}

    // 评论删除响应体
    public record CommentDeleteResponse(
            Long commentId,
            Long postId,
            String userName,
            String content,
            LocalDateTime deletedAt
    ) {}
}
