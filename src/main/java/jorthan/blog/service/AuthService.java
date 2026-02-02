package jorthan.blog.service;

import jorthan.blog.dtos.AuthDtos;
import jorthan.blog.entity.User;
import jorthan.blog.repository.AuthRepository;

import org.springframework.stereotype.Service;

import java.beans.Encoder;

@Service
public class AuthService {
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest req) {
        User author = new User();
        author.setUserName(req.userName());
        String passwordHash =
    }
}
