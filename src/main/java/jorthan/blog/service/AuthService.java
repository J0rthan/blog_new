package jorthan.blog.service;

import jorthan.blog.dtos.AuthDtos;
import jorthan.blog.entity.User;
import jorthan.blog.repository.AuthRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.Encoder;
import java.time.LocalDateTime;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public AuthDtos.RegisterResponse register(AuthDtos.RegisterRequest req) {
        User author = new User();
        author.setUserName(req.userName());
        author.setEmail(req.email());
        String passwordHash = encoder.encode(req.password());
        author.setPasswordHash(passwordHash);
        author = authRepository.save(author);

        return toDto(author);
    }

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest req) {
        User u = authRepository.findByEmail(req.email()).orElseThrow();
    }

    //Long userId,
    //String userName,
    //LocalDateTime createdAt
    public AuthDtos.RegisterResponse toDto(User user) {
        return new AuthDtos.RegisterResponse(
                user.getId(),
                user.getUserName(),
                user.getCreatedAt()
        );
    }
}
