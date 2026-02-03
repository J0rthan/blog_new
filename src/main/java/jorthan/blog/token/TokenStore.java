package jorthan.blog.token;

import jorthan.blog.entity.User;
import jorthan.blog.expcetion.ApiExceptions;
import jorthan.blog.repository.AuthRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {
    private final Map<String, Long> tokenToUserId = new ConcurrentHashMap<>();

    // 通过userId获取token
    public String getToken(Long userId) {
        String token = UUID.randomUUID().toString();
        tokenToUserId.put(token, userId);

        return token;
    }

    // 通过token获取userId
    public Long getUserId(String token) {
        return tokenToUserId.get(token);
    }
}
