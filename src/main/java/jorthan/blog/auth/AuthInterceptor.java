package jorthan.blog.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jorthan.blog.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final String ATTR_USER_ID = "AUTH_USER_ID";
    private final TokenStore tokenStore;

    public AuthInterceptor(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        String path = req.getRequestURI();
        String method = req.getMethod();

        // 放行所有的GET方法和登陆注册请求
        if (method.equals("GET") || path.startsWith("/api/auth")) {
            return true;
        }

        // 通过header获取token
        String token = req.getHeader("Auth_Token");
        if (token == null || token.isBlank()) {
            throw new Exception();
        }

        // 通过token查userId
        Long userId = tokenStore.getUserId(token);
        if (userId == null) {
            throw new Exception();
        }

        // 将userId存入req中，便于后续业务使用
        req.setAttribute(ATTR_USER_ID, userId);
        return true;
    }
}
