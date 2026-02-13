package jorthan.blog.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class PostDtos {
    // 提交/修改一篇文章的请求体
    public record PostRequest(
            @NotBlank @Size(min = 1, max = 20) String title,
            @NotBlank @Size(min = 1, max =  200_000) String content,
            @NotBlank @Size(min = 1, max = 200) String summary,
            @NotBlank @Size(min = 1, max = 100) String category
    ) {}

//    // 删除一篇文章的请求体
//    public record PostDeleteRequest(
//            @NotBlank Long postId
//    ) {}

    // 浏览所有文章响应体(只返回标题等粗略信息)
    public record PostSummaryResponse(
        Long id,
        String authorName,
        String title,
        String summary,
        boolean exist,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
    ) {}

    // 返回一个文章的详细信息
    public record PostDetailResponse(
            Long id,
            String authorName,
            String title,
            String content,
            boolean exist,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {}

    // 删除文章响应体
    public record PostDeleteResponse(
            Long id,
            String title,
            String summary,
            boolean exist,
            String message
    ) {}
}
