package jorthan.blog.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDateTime;

public class AuthDtos {
    // 注册请求
    public record RegisterRequest(
            @NotBlank @Size(min = 1, max = 20) String userName,
            @NotBlank @Email @Size(max = 64) String email,
            @NotBlank @Size(min = 6, max = 15) String password
    ) {}

    // 注册成功返回体
    public record RegisterResponse(
            Long userId,
            String userName,
            LocalDateTime createdAt,
            String identity
    ) {}

    // 登陆请求
    public record LoginRequest(
            @NotBlank @Email @Size(max = 64) String email,
            @NotBlank @Size(min = 6, max = 15) String password
    ) {}

    // 登陆成功返回体
    public record LoginResponse(
            String userName,
            String token,
            String identity
    ) {}

    // 删除请求
    public record  DeleteRequest(
            @NotBlank @Email @Size(max = 64) String email
    ) {}

    // 删除成功返回体
    public record DeleteResponse(
            String userName,
            String identity,
            boolean exist,
            LocalDateTime deletedAt
    ) {}

    // 错误(exception)返回体
    public record ErrorResponse(
            String code,
            String message,
            int status,
            String path,
            Instant timestamp
    ) {}
}
