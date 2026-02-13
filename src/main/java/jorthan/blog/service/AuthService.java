package jorthan.blog.service;

import jorthan.blog.dtos.AuthDtos;
import jorthan.blog.entity.User;
import jorthan.blog.expcetion.ApiExceptions;
import jorthan.blog.repository.AuthRepository;

import jorthan.blog.token.TokenStore;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.Encoder;
import java.time.LocalDateTime;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final TokenStore tokenStore;

    public AuthService(AuthRepository authRepository, TokenStore tokenStore) {
        this.authRepository = authRepository;
        this.tokenStore = tokenStore;
    }

    public AuthDtos.RegisterResponse register(AuthDtos.RegisterRequest req) {
        User author = new User();
        author.setUserName(req.userName());
        author.setEmail(req.email());
        String passwordHash = encoder.encode(req.password());
        author.setPasswordHash(passwordHash);
        author.setExist(Boolean.TRUE);
        author.setIdentity("reader");
        author = authRepository.save(author);

        return toRegisterDto(author);
    }

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest req) {
        User u = authRepository.findByEmail(req.email()).orElseThrow(() -> new ApiExceptions.NotFound("Cannot get the valid user"));
        String token = tokenStore.getToken(u.getId());

        return toLogInDto(u, token);
    }

    public AuthDtos.DeleteResponse delete(AuthDtos.DeleteRequest req) {
        User u = authRepository.findByEmail(req.email()).orElseThrow(() -> new ApiExceptions.NotFound("Cannot get the valid user"));
        u.setExist(false);
        u.setDeletedAt(LocalDateTime.now());
        u = authRepository.save(u);

        return toDeleteDto(u);
    }

    //Long userId,
    //String userName,
    //LocalDateTime createdAt
    public AuthDtos.RegisterResponse toRegisterDto(User user) {
        return new AuthDtos.RegisterResponse(
                user.getId(),
                user.getUserName(),
                user.getCreatedAt(),
                user.getIdentity()
        );
    }

    // String userName,
    // String token
    public AuthDtos.LoginResponse toLogInDto(User user, String token) {
        return new AuthDtos.LoginResponse(
                user.getUserName(),
                token,
                user.getIdentity()
        );
    }

    //  String userName,
    //  String identity,
    //  boolean exist,
    //  LocalDateTime deletedAt
    public AuthDtos.DeleteResponse toDeleteDto(User user) {
        return new AuthDtos.DeleteResponse(
                user.getUserName(),
                user.getIdentity(),
                user.getExist(),
                user.getDeletedAt()
        );
    }
}
