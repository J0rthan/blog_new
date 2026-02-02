package jorthan.blog.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
            LocalDateTime createdAt
    ) {}

    // 登陆请求
    public record LoginRequest(
            @NotBlank @Email @Size(max = 64) String email,
            @NotBlank @Size(min = 6, max = 15) String password
    ) {}

    // 登陆成功返回体
    public record LoginResponse(
            String userName,
            String token
    ) {}

    // 登陆请求
}
