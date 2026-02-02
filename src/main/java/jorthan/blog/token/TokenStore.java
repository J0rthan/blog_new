package jorthan.blog.token;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {
    private final Map<String, Long> tokenToUserId = new ConcurrentHashMap<>();

    public String getToken(Long userId) {
        String token = UUID.randomUUID().toString();
        tokenToUserId.put(token, userId);

        return token;
    }

    public Long getUserId(String token) {
        return tokenToUserId.get(token);
    }
}
