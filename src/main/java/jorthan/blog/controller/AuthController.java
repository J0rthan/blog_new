package jorthan.blog.controller;

import jakarta.validation.Valid;
import jorthan.blog.dtos.AuthDtos;
import jorthan.blog.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.RegisterResponse> register(@RequestBody @Valid AuthDtos.RegisterRequest req) {
        return ResponseEntity.status(201).body(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.LoginResponse> login(@RequestBody @Valid AuthDtos.LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/delete")
    public ResponseEntity<AuthDtos.DeleteResponse> delete(@RequestBody @Valid AuthDtos.DeleteRequest req) {
        return ResponseEntity.ok(authService.delete(req));
    }
}
