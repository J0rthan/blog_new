package jorthan.blog.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class PostDtos {
    // 提交一篇文章的请求体
    public record PostSubmitRequest(
            @NotBlank @Size(min = 1, max = 20) String title,
            @NotBlank @Size(min = 1, max =  200_000) String content,
            @NotBlank @Size(min = 1, max = 200) String summary
    ) {}

    // 浏览所有文章响应体(只返回标题等粗略信息)
    public record PostSummaryResponse(
        Long id,
        String authorName,
        String title,
        String summary,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
    ) {}

    // 返回一个文章的详细信息
    public record PostDetailResponse(
            Long id,
            String authorName,
            String title,
            String content,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {}
}
